package jlox;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static jlox.TokenType.*;

/*
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
public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // expression → equality ;
    private Expr expression() {
        return equality();
    }

    // equality → comparison ( ( "!=" | "==" ) comparison )* ;
    private Expr equality() {
        return binaryExpression(this::comparison, BANG_EQUAL, EQUAL_EQUAL);
    }

    // comparison → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
    private Expr comparison() {
        return binaryExpression(this::term, GREATER, GREATER_EQUAL, LESS, LESS_EQUAL);
    }

    // term → factor ( ( "-" | "+" ) factor )* ;
    private Expr term() {
        return binaryExpression(this::factor, MINUS, PLUS);
    }

    // factor → unary ( ( "/" | "*" ) unary )* ;
    private Expr factor() {
        return binaryExpression(this::unary, SLASH, STAR);
    }

    // unary → ( "!" | "-" ) unary | primary ;
    private Expr unary() {
        if (matchAndAdvance(BANG, MINUS)) {
            var operator = previous();
            var right = unary();
            return new Expr.Unary(operator, right);
        }

        return primary();
    }

    // primary → NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" ;
    private Expr primary() {
        if (matchAndAdvance(FALSE)) return new Expr.Literal(false);
        if (matchAndAdvance(TRUE)) return new Expr.Literal(true);
        if (matchAndAdvance(NIL)) return new Expr.Literal(null);

        if (matchAndAdvance(NUMBER, STRING)) {
            return new Expr.Literal(previous().literal);
        }

        if (matchAndAdvance(LEFT_PAREN)) {
            var expr = expression();

        }
        return null;
    }

    private Expr binaryExpression(Supplier<Expr> higherPrecedence, TokenType... types) {
        Expr expr = higherPrecedence.get();

        while (matchAndAdvance(types)) {
            var operator = previous();
            var right = higherPrecedence.get();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private boolean matchAndAdvance(TokenType... types) {
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
}
