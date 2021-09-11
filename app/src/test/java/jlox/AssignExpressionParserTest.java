package jlox;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static jlox.TestVisitors.BasicExprVisitor;
import static jlox.TestVisitors.ExprTypeVisitor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AssignExpressionParserTest extends ParserTestBase {

    @Test
    @DisplayName("Assignment of addition expression is allowed")
    void testAdditionAssignment() {
        var result = parseSingleStatement("testVariable = 1 + 2;");
        assertAssignment(result, "testVariable", Expr.Binary.class);
    }

    @Test
    @DisplayName("Assignment of multiplication expression is allowed")
    void testMultiplicationAssignment() {
        var result = parseSingleStatement("testVariable2 = 1 * 2;");
        assertAssignment(result, "testVariable2", Expr.Binary.class);
    }

    @Test
    @DisplayName("Assignment of negation expression is allowed")
    void testNegationAssignment() {
        var result = parseSingleStatement("itsABoolean = !true;");
        assertAssignment(result, "itsABoolean", Expr.Unary.class);
    }

    @Test
    @DisplayName("Assignment of a string is allowed")
    void testStringAssignment() {
        var result = parseSingleStatement("justAVariable = \"a string\";");
        assertAssignment(result, "justAVariable", Expr.Literal.class);
    }

    @Test
    @DisplayName("Assignment to an underscored variable name is allowed")
    void testStringAssignmentUnderscoredVar() {
        var result = parseSingleStatement("just_a_variable = \"a string\";");
        assertAssignment(result, "just_a_variable", Expr.Literal.class);
    }

    @Test
    @DisplayName("Assignment to a number is invalid")
    void testAssignmentToNumberIsInvalid() {
        var err = setNewErr();
        parseSingleStatement(" 1 = 1;");

        assertThat(err.toString()).contains("Invalid assignment target");
    }

    @Test
    @DisplayName("Assignment to an addition is invalid")
    void testAssignmentToAdditionIsInvalid() {
        var err = setNewErr();
        parseSingleStatement("1234 + 16 = false;");

        assertThat(err.toString()).contains("Invalid assignment target");
    }

    @Test
    @DisplayName("Assignment to a grouping is invalid")
    void testAssignmentToGroupingIsInvalid() {
        var err = setNewErr();
        parseSingleStatement("(variableName) = false;");

        assertThat(err.toString()).contains("Invalid assignment target");
    }

    @Test
    @DisplayName("Assignment of variable declaration is invalid")
    void testAssignmentOfAGroupingIsInvalid() {
        var err = setNewErr();
        parseSingleStatement("variableName = var c;");

        assertThat(err.toString()).contains("Error at 'var': Expected an expression");
    }

    @Test
    @DisplayName("Assignment of a statement block is invalid")
    void testAssignmentOfStatementBlockIsInvalid() {
        var err = setNewErr();
        parseSingleStatement("variableName = { 5 };");

        assertThat(err.toString()).contains("Error at '{': Expected an expression");
    }

    private static void assertAssignment(Stmt stmt, String varName, Class<? extends Expr> rightHandType) {
        var assignment = getAssignmentExpr(stmt);
        var rightHand = assignment.value;

        assertThat(assignment.name.lexeme).isEqualTo(varName);
        assertThat(assignment.value.accept(new ExprTypeVisitor())).isEqualTo(rightHandType);
    }


    private static Expr.Assign getAssignmentExpr(Stmt stmt) {
        var expr = getExpression(stmt);

        assertExprType(expr, Expr.Assign.class);

        var exprVisitor = new BasicExprVisitor<Expr.Assign>() {
            @Override public Expr.Assign visitAssignExpr(Expr.Assign expr) {
                return expr;
            }
        };
        // Will succeed, as the stmt is the correct type assured by assert
        return expr.accept(exprVisitor);
    }
}
