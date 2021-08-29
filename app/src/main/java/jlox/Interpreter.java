package jlox;

import java.text.DecimalFormat;
import java.util.List;

import static jlox.TokenType.*;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
  private final static DecimalFormat noTrailingZeroesFormat =  new DecimalFormat("0.#");

  private Environment environment = new Environment();

  void interpret(List<Stmt> statements) {
    try {
      for (Stmt statement : statements) {
        execute(statement);
      }
    } catch (RuntimeError e) {
      Lox.runtimeError(e);
    }
  }

  private void execute(Stmt stmt) {
    stmt.accept(this);
  }

  private String stringify(Object value) {
    if (value == null) return "nil";

    if (value instanceof Double) {
      return noTrailingZeroesFormat.format((double) value);
    }

    return value.toString();
  }

  @Override
  public Void visitExpressionStmt(Stmt.Expression stmt) {
    evaluate(stmt.expression);
    return null;
  }

  @Override
  public Void visitPrintStmt(Stmt.Print stmt) {
    var value = evaluate(stmt.expression);
    System.out.println(stringify(value));
    return null;
  }

  @Override
  public Void visitVarStmt(Stmt.Var stmt) {
    var value = (Object) null;
    if (stmt.initializer != null) {
      value = evaluate(stmt.initializer);
    }

    environment.define(stmt.name.lexeme, value);
    return null;
  }

  @Override
  public Object visitAssignExpr(Expr.Assign expr) {
    var value = evaluate(expr.value);
    environment.assign(expr.name, value);
    return value;
  }

  @Override
  public Object visitBinaryExpr(Expr.Binary expr) {
    var operator = expr.operator.type;
    var left = evaluate(expr.left);
    var right = evaluate(expr.right);
//TODO what if types don't agree? just let java throw?

    if (left instanceof String && right instanceof String && operator == PLUS) {
      return left + (String)right;
    }

    if (operator == EQUAL) {
      return isEqual(left, right);
    } else if (operator == BANG_EQUAL) {
      return !isEqual(left, right);
    }

    var leftNum = requireDouble(expr.operator, left);
    var rightNum = requireDouble(expr.operator, right);

    switch (operator) {
      case MINUS:
        return leftNum - rightNum;
      case PLUS:
        return leftNum + rightNum;
      case SLASH:
        return leftNum / rightNum;
      case STAR:
        return leftNum * rightNum;
      case GREATER:
        return leftNum > rightNum;
      case GREATER_EQUAL:
        return leftNum >= rightNum;
      case LESS:
        return leftNum < rightNum;
      case LESS_EQUAL:
        return leftNum <= rightNum;
    }
    return null;
  }

  private double requireDouble(Token operator, Object operand) {
    if (operand instanceof Double) {
      return (double) operand;
    }
    var errMsg = "Operand must be a number" + (operator.type == PLUS ? " or a string" : "");
    throw new RuntimeError(operator, errMsg);
  }

  private boolean isEqual(Object a, Object b) {
    if (a == null && b == null) return true;
    if (a == null) return false;
    return a.equals(b);
  }

  @Override
  public Object visitGroupingExpr(Expr.Grouping expr) {
    return evaluate(expr.expression);
  }

  private Object evaluate(Expr expr) {
    return expr.accept(this);
  }

  @Override
  public Object visitLiteralExpr(Expr.Literal expr) {
    return expr.value;
  }

  @Override
  public Object visitUnaryExpr(Expr.Unary expr) {
    var right = evaluate(expr.right);
    switch (expr.operator.type) {
      case BANG:
        return !isTruthy(right);
      case MINUS:
        // TODO can the cast fail?
        return -(double)right;
    }
    // TODO Unreachable?
    return null;
  }

  @Override
  public Object visitVariableExpr(Expr.Variable expr) {
    return environment.get(expr.name);
  }

  // Ruby-style: false and nil are falsy, everything else is truthy
  private boolean isTruthy(Object o) {
    if (o == null) return false;
    if (o instanceof Boolean) return (boolean) o;
    return true;
  }
}
