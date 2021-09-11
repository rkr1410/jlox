package jlox;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ParserTestBase {

    protected Stmt parseSingleStatement(String source) {
        var result = parse(source);
        assertThat(result).as("Expected a single statement").hasSize(1);
        return result.iterator().next();
    }

    protected List<Stmt> parse(String source) {
        var scanner = new Scanner(source);
        var tokens = scanner.scanTokens();
        var parser = new Parser(tokens);

        return parser.parse();
    }

    protected ByteArrayOutputStream setNewErr() {
        var err = new ByteArrayOutputStream();
        var errPrintStream = new PrintStream(err);
        Lox.setErr(errPrintStream);
        return err;
    }

    protected static void assertStmtType(Stmt statement, Class<? extends Stmt> expectedType) {
        var visitor = new TestVisitors.StmtTypeVisitor();
        var actualType = statement.accept(visitor);
        assertThat(actualType).isEqualTo(expectedType);
    }

    protected static Expr getExpression(Stmt stmt) {
        assertStmtType(stmt, Stmt.Expression.class);
        var getExprVisitor = new TestVisitors.BasicStmtVisitor<Expr>() {
            @Override
            public Expr visitExpressionStmt(Stmt.Expression stmt) {
                return stmt.expression;
            }
        };
        // Will succeed, as the stmt is the correct type assured by first assert
        return stmt.accept(getExprVisitor);
    }

    protected static void assertExprType(Expr expr, Class<? extends Expr> expectedType) {
        var visitor = new TestVisitors.ExprTypeVisitor();
        var actualType = expr.accept(visitor);
        assertThat(actualType).isEqualTo(expectedType);
    }

    protected static Object getLiteralValue(Expr expr) {
        assertExprType(expr, Expr.Literal.class);
        var exprVisitor = new TestVisitors.BasicExprVisitor<>() {
            @Override public Object visitLiteralExpr(Expr.Literal expr) {
                return expr.value;
            }
        };
        // Will succeed, as the stmt is the correct type assured by assert
        return expr.accept(exprVisitor);
    }
}
