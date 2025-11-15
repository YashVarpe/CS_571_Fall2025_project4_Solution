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

        //NUM: [0-9]*\.[0-9]+
        //This handles the "zero or more digits" before the dot
        a = new AutomatonImpl();
        a.addState(0, true, false);  //Start state -> state 0 -> [0-9]*
        a.addState(1, false, false); //Seen dot -> state 1 -> dot = '\.'
        a.addState(2, false, true);  //Seen at least one digit after dot (accept) -> state 2 -> [0-9]+
        
        //[0-9]* loop on state 0
        for (char d : digits) {
            a.addTransition(0, d, 0);
        }
        //'\.' when we see the dot '.', we move from state 0 to state 1
        a.addTransition(0, '.', 1);
        
        //[0-9]+ from state 1
        //This handles the "one or more digits" after the dot
        for (char d : digits) {
            a.addTransition(1, d, 2); // 1 -> 2 (first digit after dot)
            a.addTransition(2, d, 2); // 2 -> 2 (subsequent digits)
        }
        lex.add_automaton(TokenType.NUM, a);

        //PLUS: \+
        //This is a simple automaton with two states for the '+' sign
        a = new AutomatonImpl();
        a.addState(0, true, false);
        a.addState(1, false, true);
        a.addTransition(0, '+', 1);
        lex.add_automaton(TokenType.PLUS, a);

        //MINUS: -
        //This is a simple automaton with two states for the '-' sign
        a = new AutomatonImpl();
        a.addState(0, true, false);
        a.addState(1, false, true);
        a.addTransition(0, '-', 1);
        lex.add_automaton(TokenType.MINUS, a);

        //TIMES: \*
        //This is a simple automaton with two states for the '*' sign
        a = new AutomatonImpl();
        a.addState(0, true, false);
        a.addState(1, false, true);
        a.addTransition(0, '*', 1);
        lex.add_automaton(TokenType.TIMES, a);

        //DIV: /
        //This is a simple automaton with two states for the '/' sign
        a = new AutomatonImpl();
        a.addState(0, true, false);
        a.addState(1, false, true);
        a.addTransition(0, '/', 1);
        lex.add_automaton(TokenType.DIV, a);
        
        //LPAREN: \(
        //This is a simple automaton with two states for the '(' sign
        a = new AutomatonImpl();
        a.addState(0, true, false);
        a.addState(1, false, true);
        a.addTransition(0, '(', 1);
        lex.add_automaton(TokenType.LPAREN, a);

        //RPAREN: \)
        //This is a simple automaton with two states for the ')' sign
        a = new AutomatonImpl();
        a.addState(0, true, false);
        a.addState(1, false, true);
        a.addTransition(0, ')', 1);
        lex.add_automaton(TokenType.RPAREN, a);

        //WHITE_SPACE (' '|\n|\r|\t)*
        //This is a simple automaton with one states for any whitespace
        //State 0 is both the start and the accept state, for "zero or more"
        a = new AutomatonImpl();
        a.addState(0, true, true); // Start state is also accept state (for *)
        char[] whitespaceChars = " \n\r\t".toCharArray();
        for (char w : whitespaceChars) {
            a.addTransition(0, w, 0); // Loop on state 0
        }
        lex.add_automaton(TokenType.WHITE_SPACE, a);
    }
}