package jlox;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static jlox.TokenType.*;

/*

TODO remove after done goofing around

    expression  →   equality ;
    equality    →   comparison ( ( "!=" | "==" ) comparison )* ;
    comparison  →   term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
    term        →   factor ( ( "-" | "+" ) factor )* ;
    factor      →   unary ( ( "/" | "*" ) unary )* ;
    unary       →   ( "!" | "-" ) unary | primary ;
    primary     →   NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" ;

    Grammar notation      Code representation
         Terminal           Code to match and consume a token
        Nonterminal         Call to that rule’s function
            |               if or switch statement
          * or +            while or for loop
            ?               if statement
 */
public class P2 {
    private static class ParseError extends RuntimeException {}
    /*
        On parse error, look forward for these token types to synchronize the parser
        and parse the rest of the source, so that the error is not the last thing reported.
     */
    private final static Set<TokenType> expressionStarters = Set.of(CLASS, FUN, VAR, FOR, IF, WHILE, PRINT, RETURN);

    private final List<Token> tokens;
    private int current = 0;

    public P2(List<Token> tokens) {
        this.tokens = tokens;
        System.err.println("TOKENS:");
        System.err.println(tokens);
        System.err.println("------------------------------------");
    }

    Expr parse() {
        try {
            return expression();
        } catch (ParseError e) {
            e.printStackTrace();
            return null;
        }
    }

    // expression → equality ;
    private Expr expression() {
        return term();
    }

    private Expr term() {
        Expr expr = factor();

        while (advanceIf(PLUS)) {
            var operator = previous();
            var right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr factor() {
        Expr expr = primary();

        while (advanceIf(STAR)) {
            var operator = previous();
            var right = primary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr primary() {
        if (advanceIf(NUMBER)) return new Expr.Literal(previous().literal);

        throw error(peek(), "Expected an expression");
    }

    private boolean advanceIf(TokenType... types) {
        boolean found = Arrays.stream(types).anyMatch(this::check);
        if (found) {
            advance();
        }
        return found;
    }

    private boolean check(TokenType type) {
        return !isAtEnd() && peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private ParseError error(Token token, String message) {
        Lox.error(peek(), message);
        return new ParseError();
    }

    //TODO when to call this?
    private void synchronize() {
        advance();
        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) return;
            if (expressionStarters.contains(peek().type)) return;
            advance();
        }
    }
}
