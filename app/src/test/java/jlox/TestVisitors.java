package jlox;

public class TestVisitors {
    static class StmtTypeVisitor implements Stmt.Visitor<Class<? extends Stmt>> {
        @Override public Class<? extends Stmt> visitBlockStmt(Stmt.Block stmt) {
            return Stmt.Block.class;
        }

        @Override
        public Class<? extends Stmt> visitExpressionStmt(Stmt.Expression stmt) {
            return Stmt.Expression.class;
        }

        @Override public Class<? extends Stmt> visitPrintStmt(Stmt.Print stmt) {
            return Stmt.Print.class;
        }

        @Override public Class<? extends Stmt> visitVarStmt(Stmt.Var stmt) {
            return Stmt.Var.class;
        }

        @Override public Class<? extends Stmt> visitIfStmtStmt(Stmt.IfStmt stmt) {
            return Stmt.IfStmt.class;
        }
    }

    static class BasicStmtVisitor<T> implements Stmt.Visitor<T> {
        @Override public T visitBlockStmt(Stmt.Block stmt) {
            return null;
        }

        @Override public T visitExpressionStmt(Stmt.Expression stmt) {
            return null;
        }

        @Override public T visitPrintStmt(Stmt.Print stmt) {
            return null;
        }

        @Override public T visitVarStmt(Stmt.Var stmt) {
            return null;
        }

        @Override public T visitIfStmtStmt(Stmt.IfStmt stmt) {
            return null;
        }
    }

    static class ExprTypeVisitor implements Expr.Visitor<Class<? extends Expr>> {

        @Override public Class<? extends Expr> visitAssignExpr(Expr.Assign expr) {
            return Expr.Assign.class;
        }

        @Override public Class<? extends Expr> visitBinaryExpr(Expr.Binary expr) {
            return Expr.Binary.class;
        }

        @Override
        public Class<? extends Expr> visitGroupingExpr(Expr.Grouping expr) {
            return Expr.Grouping.class;
        }

        @Override public Class<? extends Expr> visitLiteralExpr(Expr.Literal expr) {
            return Expr.Literal.class;
        }

        @Override public Class<? extends Expr> visitUnaryExpr(Expr.Unary expr) {
            return Expr.Unary.class;
        }

        @Override
        public Class<? extends Expr> visitVariableExpr(Expr.Variable expr) {
            return Expr.Variable.class;
        }
    }

    static class BasicExprVisitor<T> implements Expr.Visitor<T> {
        @Override public T visitAssignExpr(Expr.Assign expr) {
            return null;
        }

        @Override public T visitBinaryExpr(Expr.Binary expr) {
            return null;
        }

        @Override public T visitGroupingExpr(Expr.Grouping expr) {
            return null;
        }

        @Override public T visitLiteralExpr(Expr.Literal expr) {
            return null;
        }

        @Override public T visitUnaryExpr(Expr.Unary expr) {
            return null;
        }

        @Override public T visitVariableExpr(Expr.Variable expr) {
            return null;
        }
    }
}
