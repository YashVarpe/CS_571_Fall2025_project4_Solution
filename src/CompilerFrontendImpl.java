public class CompilerFrontendImpl extends CompilerFrontend {
    public CompilerFrontendImpl() {
        super();
    }

    public CompilerFrontendImpl(boolean debug_) {
        super(debug_);
    }

    /*
     * Initializes the local field "lex" to be equal to the desired lexer.
     * The desired lexer has the following specification:
     * * NUM: [0-9]*\.[0-9]+
     * PLUS: \+
     * MINUS: -
     * TIMES: \*
     * DIV: /
     * WHITE_SPACE (' '|\n|\r|\t)*
     *
     * (Note: The README includes LPAREN and RPAREN, which are added below)
     */
    @Override
    protected void init_lexer() {
        this.lex = new LexerImpl();
        Automaton a;
        char[] digits = "0123456789".toCharArray();

        // NUM: [0-9]*\.[0-9]+
        a = new AutomatonImpl();
        a.addState(0, true, false);  // Start state
        a.addState(1, false, false); // Seen dot
        a.addState(2, false, true);  // Seen at least one digit after dot (accept)
        
        // [0-9]* loop on state 0
        for (char d : digits) {
            a.addTransition(0, d, 0);
        }
        // \. transition from 0 to 1
        a.addTransition(0, '.', 1);
        
        // [0-9]+ from state 1
        for (char d : digits) {
            a.addTransition(1, d, 2); // 1 -> 2 (first digit after dot)
            a.addTransition(2, d, 2); // 2 -> 2 (subsequent digits)
        }
        lex.add_automaton(TokenType.NUM, a);

        // PLUS: \+
        a = new AutomatonImpl();
        a.addState(0, true, false);
        a.addState(1, false, true);
        a.addTransition(0, '+', 1);
        lex.add_automaton(TokenType.PLUS, a);

        // MINUS: -
        a = new AutomatonImpl();
        a.addState(0, true, false);
        a.addState(1, false, true);
        a.addTransition(0, '-', 1);
        lex.add_automaton(TokenType.MINUS, a);

        // TIMES: \*
        a = new AutomatonImpl();
        a.addState(0, true, false);
        a.addState(1, false, true);
        a.addTransition(0, '*', 1);
        lex.add_automaton(TokenType.TIMES, a);

        // DIV: /
        a = new AutomatonImpl();
        a.addState(0, true, false);
        a.addState(1, false, true);
        a.addTransition(0, '/', 1);
        lex.add_automaton(TokenType.DIV, a);
        
        // LPAREN: \(
        a = new AutomatonImpl();
        a.addState(0, true, false);
        a.addState(1, false, true);
        a.addTransition(0, '(', 1);
        lex.add_automaton(TokenType.LPAREN, a);

        // RPAREN: \)
        a = new AutomatonImpl();
        a.addState(0, true, false);
        a.addState(1, false, true);
        a.addTransition(0, ')', 1);
        lex.add_automaton(TokenType.RPAREN, a);

        // WHITE_SPACE (' '|\n|\r|\t)*
        a = new AutomatonImpl();
        a.addState(0, true, true); // Start state is also accept state (for *)
        char[] whitespaceChars = " \n\r\t".toCharArray();
        for (char w : whitespaceChars) {
            a.addTransition(0, w, 0); // Loop on state 0
        }
        lex.add_automaton(TokenType.WHITE_SPACE, a);
    }
}