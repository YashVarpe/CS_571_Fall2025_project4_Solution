public class ParserImpl extends Parser {

    /*
     * Implements a recursive-descent parser for the following CFG:
     * * T -> F AddOp T              { if ($2.type == TokenType.PLUS) { $$ = new PlusExpr($1,$3); } else { $$ = new MinusExpr($1, $3); } }
     * T -> F                      { $$ = $1; }
     * F -> Lit MulOp F            { if ($2.type == TokenType.Times) { $$ = new TimesExpr($1,$3); } else { $$ = new DivExpr($1, $3); } }
     * F -> Lit                    { $$ = $1; }
     * Lit -> NUM                  { $$ = new FloatExpr(Float.parseFloat($1.lexeme)); }
     * Lit -> LPAREN T RPAREN      { $$ = $2; }
     * AddOp -> PLUS               { $$ = $1; }
     * AddOp -> MINUS              { $$ = $1; }
     * MulOp -> TIMES              { $$ = $1; }
     * MulOp -> DIV                { $$ = $1; }
     */
    @Override
    public Expr do_parse() throws Exception {
        //This is the main entry point for the parser
        //We start parsing from the start symbol which is T
        Expr result = parseT();
        
        //After parsing, we should be at the end of the token list
        //If there are still tokens left, it ,means the input was invalid
        if (tokens != null) {
            throw new Exception("Expected end of input, but found token: " + tokens.elem.lexeme);
        }
        
        return result;
    }

    //This handles addition and subtraction
    private Expr parseT() throws Exception {
        Expr e1 = parseF();

        //Now we look at the next token to decide which rule to use
        if (tokens != null && (peek(TokenType.PLUS, 0) || peek(TokenType.MINUS, 0))) {
            Token op = parseAddOp();
            Expr e2 = parseT(); // Recursive call for T

            if (op.ty == TokenType.PLUS) {
                return new PlusExpr(e1, e2);
            } else {
                return new MinusExpr(e1, e2);
            }
        }
        return e1;
    }

    //This handles multiplication and division
    private Expr parseF() throws Exception {
        //All rules start with Lit
        Expr e1 = parseLit();
        //Now we look at the next token to decide which rule to use
        //We check the next token for a '*' or '/'
        if (tokens != null && (peek(TokenType.TIMES, 0) || peek(TokenType.DIV, 0))) {
            Token op = parseMulOp();
            Expr e2 = parseF(); //Recursive call for F

            //Apply SDT rule to build the expression tree
            if (op.ty == TokenType.TIMES) {
                return new TimesExpr(e1, e2);
            } else {
                return new DivExpr(e1, e2);
            }
        }
        return e1;
    }
    private Expr parseLit() throws Exception {
        if (tokens == null) {
            throw new Exception("Unexpected end of input, expected NUM or LPAREN");
        }

        if (peek(TokenType.NUM, 0)) {
            // Rule: Lit -> NUM
            Token num = consume(TokenType.NUM);
            return new FloatExpr(Float.parseFloat(num.lexeme));
        } else if (peek(TokenType.LPAREN, 0)) {
            // Rule: Lit -> LPAREN T RPAREN
            consume(TokenType.LPAREN);
            Expr e = parseT();
            consume(TokenType.RPAREN); // This will throw if RPAREN is missing
            return e;
        } else {
            throw new Exception("Parsing error: expected NUM or LPAREN, found " + tokens.elem.ty);
        }
    }
    private Token parseAddOp() throws Exception {
        if (tokens == null) {
            throw new Exception("Unexpected end of input, expected PLUS or MINUS");
        }

        if (peek(TokenType.PLUS, 0)) {
            return consume(TokenType.PLUS);
        } else if (peek(TokenType.MINUS, 0)) {
            return consume(TokenType.MINUS);
        } else {
            throw new Exception("Parsing error: expected PLUS or MINUS, found " + tokens.elem.ty);
        }
    }
    private Token parseMulOp() throws Exception {
        if (tokens == null) {
            throw new Exception("Unexpected end of input, expected TIMES or DIV");
        }
        
        if (peek(TokenType.TIMES, 0)) {
            return consume(TokenType.TIMES);
        } else if (peek(TokenType.DIV, 0)) {
            return consume(TokenType.DIV);
        } else {
            throw new Exception("Parsing error: expected TIMES or DIV, found " + tokens.elem.ty);
        }
    }
}