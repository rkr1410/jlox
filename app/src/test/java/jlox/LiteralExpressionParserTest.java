package jlox;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static jlox.TestVisitors.*;
import static org.assertj.core.api.Assertions.assertThat;

public class LiteralExpressionParserTest extends ParserTestBase {

    @Test
    @DisplayName("false is a Boolean.FALSE literal")
    void testFalseLiteral() {
        var result = parseSingleStatement("false;");
        var literal = getLiteralValue(result);
        assertThat(literal).isEqualTo(Boolean.FALSE);
    }

    @Test
    @DisplayName("true is a Boolean.TRUE literal")
    void testTrueLiteral() {
        var result = parseSingleStatement("true;");
        var literal = getLiteralValue(result);
        assertThat(literal).isEqualTo(true);
    }

    @Test
    @DisplayName("nil is a null literal")
    void testNilLiteral() {
        var result = parseSingleStatement("nil;");
        var literal = getLiteralValue(result);
        assertThat(literal).isNull();
    }

    @Test
    @DisplayName("String is a literal")
    void testStringLiteral() {
        var result = parseSingleStatement("\"this is a test string\";");
        var literal = getLiteralValue(result);
        assertThat(literal).isEqualTo("this is a test string");
    }

    @Test
    @DisplayName("Empty string is a literal")
    void testEmptyStringLiteral() {
        var result = parseSingleStatement("\"\";");
        var literal = getLiteralValue(result);
        assertThat(literal).isEqualTo("");
    }

    @Test
    @DisplayName("Zero number is a literal")
    void testNonDecimalLiteral() {
        var result = parseSingleStatement("0;");
        var literal = getLiteralValue(result);
        assertThat(literal).isEqualTo(0d);
    }

    @Test
    @DisplayName("Max int number is a literal")
    void testMaxIntNumberLiteral() {
        var result = parseSingleStatement("2147483647;");
        var literal = getLiteralValue(result);
        assertThat(literal).isEqualTo(2147483647d);
    }

    @Test
    @DisplayName("Negative number is NOT a literal")
    void testMinimumNumberLiteral() {
        var result = parseSingleStatement("-1;");
        var expr = getExpression(result);
        var exprType = expr.accept(new ExprTypeVisitor());
        assertThat(exprType).isNotEqualTo(Expr.Literal.class);
    }

    private Object getLiteralValue(Stmt stmt) {
        var expr = getExpression(stmt);
        return getLiteralValue(expr);
    }
}
