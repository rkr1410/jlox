package jlox;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static jlox.TestVisitors.BasicExprVisitor;

public class BinaryExpressionParserTest extends ParserTestBase {

    @Test
    @DisplayName("Compare strings (equality)")
    void testStringEquals() {
        var result = parseSingleStatement("\"abc\" == \"def\";");
        assertBinaryWithLiterals(result, "abc", TokenType.EQUAL_EQUAL, "def");
    }

    @Test
    @DisplayName("Compare strings (inequality)")
    void testStringNotEquals() {
        var result = parseSingleStatement("\"abc\" != nil;");
        assertBinaryWithLiterals(result, "abc", TokenType.BANG_EQUAL, null);
    }

    @Test
    @DisplayName("Add two non-decimal number literals")
    void testNumberAddition() {
        var result = parseSingleStatement("1 + 2;");
        assertBinaryWithLiterals(result, 1d, TokenType.PLUS, 2d);
    }

    @Test
    @DisplayName("Add two decimal number literals")
    void testDecimalAddition() {
        var result = parseSingleStatement("1.5 + 2.1;");
        assertBinaryWithLiterals(result, 1.5d, TokenType.PLUS, 2.1d);
    }

    @Test
    @DisplayName("Multiply two number literals")
    void testLiteralMultiplication() {
        var result = parseSingleStatement("1 + 2;");
        assertBinaryWithLiterals(result, 1d, TokenType.PLUS, 2d);
    }

    // This is a parser test, so no type checking is done
    @Test
    @DisplayName("Compare string and boolean (greater than)")
    void testLiteralGreaterThan() {
        var result = parseSingleStatement("\"abc\">false;");
        assertBinaryWithLiterals(result, "abc", TokenType.GREATER, false);
    }

    @Test
    @DisplayName("Compare boolean and pl-letters string (greater than or equals)")
    void testLiteralGreaterThanOrEquals() {
        var result = parseSingleStatement("true>=\"zażółć gęślą jaźń\";");
        assertBinaryWithLiterals(result, true, TokenType.GREATER_EQUAL, "zażółć " +
                "gęślą jaźń");
    }

    @Test
    @DisplayName("Compare boolean and number (less)")
    void testLiteralLess() {
        var result = parseSingleStatement("true < 654;");
        assertBinaryWithLiterals(result, true, TokenType.LESS, 654d);
    }

    @Test
    @DisplayName("Compare number and boolean (less or equals)")
    void testLiteralLessOrEquals() {
        var result = parseSingleStatement("997 <= false;");
        assertBinaryWithLiterals(result, 997d, TokenType.LESS_EQUAL, false);
    }

    private static void assertBinaryWithLiterals(
            Stmt stmt,
            Object leftLiteral,
            TokenType operationTokenType,
            Object rightLiteral) {
        var binary = getBinaryExpr(stmt);
        var softly = new SoftAssertions();

        softly.assertThat(getLiteralValue(binary.left)).isEqualTo(leftLiteral);
        softly.assertThat(binary.operator.type).isEqualTo(operationTokenType);
        softly.assertThat(getLiteralValue(binary.right)).isEqualTo(rightLiteral);
        softly.assertAll();
    }


    private static Expr.Binary getBinaryExpr(Stmt stmt) {
        var expr = getExpression(stmt);

        assertExprType(expr, Expr.Binary.class);

        var exprVisitor = new BasicExprVisitor<Expr.Binary>() {
            @Override public Expr.Binary visitBinaryExpr(Expr.Binary expr) {
                return expr;
            }
        };
        // Will succeed, as the stmt is the correct type assured by assert
        return expr.accept(exprVisitor);
    }
}
