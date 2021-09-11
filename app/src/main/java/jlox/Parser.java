package jlox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import static jlox.TokenType.*;

/*

    program     →   declaration* EOF ;
    declaration →   varDecl | statement ;
    varDecl     →   "var" IDENTIFIER ("=" expression)? ";" ;
    statement   →   exprStmt | printStmt | block | ifStmt  ;
    ifStmt      →   "if" "(" expression ")" statement ("else" statement)?  ;
    block       →   "{" declaration* "}" ;
    exprStmt    →   expression ";" ;
    printStmt   →   "print" expression ";" ;
    expression  →   assignment ;
    assignment  →   IDENTIFIER "=" assignment | equality ;
    equality    →   comparison ( ( "!=" | "==" ) comparison )* ;
    comparison  →   term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
    term        →   factor ( ( "-" | "+" ) factor )* ;
    factor      →   unary ( ( "/" | "*" ) unary )* ;
    unary       →   ( "!" | "-" ) unary | primary ;
    primary     →   NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" | IDENTIFIER;

    Grammar notation    |   Code representation
    ---------------------------------------------------------
         Terminal       |   Code to match and consume a token
        Nonterminal     |   Call to that rule’s function
            |           |   if or switch statement
          * or +        |   while or for loop
            ?           |   if statement
 */
public class Parser {
    private static class ParseError extends RuntimeException {}
    /*
        On parse error, look forward for these token types to synchronize the parser
        and parse the rest of the source, so that the error is not last thing reported.
     */
    private final static Set<TokenType> expressionStarters = Set.of(CLASS, FUN, VAR, FOR, IF, WHILE, PRINT, RETURN);

    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    List<Stmt> parse() {
        try {
            var stmts = new ArrayList<Stmt>();
            while (!isAtEnd()) {
                stmts.add(declaration());
            }
            return stmts;
        } catch (ParseError e) {
            return null;
        }
    }

    // declaration → varDecl | statement ;
    private Stmt declaration() {
        try {
            if (advanceIf(VAR)) return varDeclaration();
            return statement();
        } catch (Exception e) {
            synchronize();
            return null;
        }
    }

    private Stmt varDeclaration() {
        Token name = expect(IDENTIFIER, "Identifier expected");

        Expr initializer = null;
        if (advanceIf(EQUAL)) {
            initializer = expression();
        }
        expect(SEMICOLON, "Expected ; after variable declaration");
        return new Stmt.Var(name, initializer);
    }

    // statement → exprStmt | printStmt ;
    private Stmt statement() {
        if (advanceIf(IF)) return ifStatement();
        if (advanceIf(PRINT)) return printStatement();
        if (advanceIf(LEFT_BRACE)) return new Stmt.Block(block());
        return expressionStatement();
    }

    private Stmt ifStatement() {
        expect(LEFT_PAREN, "Expected ( after if");
        Expr ifCondition = expression();
        expect(RIGHT_PAREN, "Expected ) after condition");
        Stmt thenBranch = statement();
        Stmt elseBranch = null;

        if (advanceIf(ELSE)) {
            elseBranch = statement();
        }

        return new Stmt.IfStmt(ifCondition, thenBranch, elseBranch);
    }

    private Stmt printStatement() {
        var value = expression();
        expect(SEMICOLON, "Expected ; after print value");
        return new Stmt.Print(value);
    }

    private Stmt expressionStatement() {
        var expr = expression();
        expect(SEMICOLON, "Expected ; after expression");
        return new Stmt.Expression(expr);
    }

    private List<Stmt> block() {
        var statements = new ArrayList<Stmt>();
        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration());
        }

        expect(RIGHT_BRACE, "Expected }");
        return statements;
    }

    // expression → equality ;
    private Expr expression() {
        return assignment();
    }

    // assignment → IDENTIFIER "=" assignment | equality ;
    private Expr assignment() {
        Expr expr = equality();

        if (advanceIf(EQUAL)) {
            Token equals = previous();
            Expr value = assignment();

            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable)expr).name;
                return new Expr.Assign(name, value);
            }
            // Don't throw, since there's no need to synchronize,
            // as we 'know where we are' and not in panic mode
            //noinspection ThrowableNotThrown
            error(equals, "Invalid assignment target");
        }

        return expr;
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
        if (advanceIf(BANG, MINUS)) {
            var operator = previous();
            var right = unary();
            return new Expr.Unary(operator, right);
        }

        return primary();
    }

    // primary → NUMBER | STRING | "true" | "false" | "nil" | "(" expression ")" ;
    private Expr primary() {
        if (advanceIf(FALSE)) return new Expr.Literal(false);
        if (advanceIf(TRUE)) return new Expr.Literal(true);
        if (advanceIf(NIL)) return new Expr.Literal(null);

        if (advanceIf(NUMBER, STRING)) {
            return new Expr.Literal(previous().literal);
        }

        if (advanceIf(IDENTIFIER)) {
            return new Expr.Variable(previous());
        }

        if (advanceIf(LEFT_PAREN)) {
            var expr = expression();
            expect(RIGHT_PAREN, "Expected ')' to close grouping expression");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "Expected an expression");
    }

    private Expr binaryExpression(Supplier<Expr> higherPrecedence, TokenType... types) {
        Expr expr = higherPrecedence.get();

        while (advanceIf(types)) {
            var operator = previous();
            var right = higherPrecedence.get();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private boolean advanceIf(TokenType... types) {
        boolean found = Arrays.stream(types).anyMatch(this::check);
        if (found) {
            advance();
        }
        return found;
    }

    private Token expect(TokenType tokenType, String message) {
        if (check(tokenType)) return advance();

        throw error(peek(), message);
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

    private void synchronize() {
        advance();
        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) return;
            if (expressionStarters.contains(peek().type)) return;
            advance();
        }
    }
}
