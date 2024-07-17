package es.ignaciofp.lox;

import es.ignaciofp.lox.Expr.Binary;
import es.ignaciofp.lox.Expr.Grouping;
import es.ignaciofp.lox.Expr.Literal;
import es.ignaciofp.lox.Expr.Ternary;
import es.ignaciofp.lox.Expr.Unary;

public class Interpreter implements Expr.Visitor<Object> {

    public void interpret(Expr expression) {
        try {
            Object value = evaluate(expression);
            System.out.print(stringify(value));
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    @Override
    public Object visitTernaryExpr(Ternary expr) {
        Object condition = evaluate(expr.condition);

        if (isTruthy(condition)) return evaluate(expr.left);
        else return evaluate(expr.right);
    }    

    @Override
    public Object visitBinaryExpr(Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        double leftVal;
        double rightVal;
        switch (expr.operator.type) {
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left - (double)right;
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                if ((double)right == 0) throw new RuntimeError(expr.operator, "Cannot divide by zero.");
                return (double)left / (double)right;
            case STAR:
                if (left instanceof Double && right instanceof Double) {
                    return (double)left * (double)right;
                }

                if ((left instanceof String && right instanceof Double) || (left instanceof Double && right instanceof String)) {
                    String str = (left instanceof String) ? (String)left : (String)right;
                    StringBuilder sb = new StringBuilder(str);
                    for (int i = 1; i < ((left instanceof Double) ? (double)left : (double)right); i++) {
                        sb.append(str);
                    }
                    return sb.toString();
                }

                throw new RuntimeError(expr.operator, "Operands must be two numbers or one number and one string.");
            case PLUS: {
                if (left instanceof Double && right instanceof Double) {
                    return (double)left + (double)right;
                }

                if (left instanceof String && right instanceof String) {
                    return (String)left + (String)right;
                }

                if ((left instanceof String && !(right instanceof String)) || (!(left instanceof String) && right instanceof String)) {
                    return (left instanceof String) ? (String)left + stringify(right) : stringify(left) + (String)right;
                } 

                throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings.");
            }
            case GREATER:
                checkStringOrDoubleOperands(expr.operator, left, right);
                leftVal = (left instanceof Double) ? (double)left : ((String)left).length();
                rightVal = (right instanceof Double) ? (double)right : ((String)right).length();
                return leftVal > rightVal;
            case GREATER_EQUAL: 
                checkStringOrDoubleOperands(expr.operator, left, right);
                leftVal = (left instanceof Double) ? (double)left : ((String)left).length();
                rightVal = (right instanceof Double) ? (double)right : ((String)right).length();
                return leftVal >= rightVal;
            case LESS: 
                checkStringOrDoubleOperands(expr.operator, left, right);
                leftVal = (left instanceof Double) ? (double)left : ((String)left).length();
                rightVal = (right instanceof Double) ? (double)right : ((String)right).length();
                return leftVal < rightVal;
            case LESS_EQUAL:
                checkStringOrDoubleOperands(expr.operator, left, right);
                leftVal = (left instanceof Double) ? (double)left : ((String)left).length();
                rightVal = (right instanceof Double) ? (double)right : ((String)right).length();
                return leftVal <= rightVal;
            case BANG_EQUAL: return !isEqual(left, right);
            case EQUAL_EQUAL: return isEqual(left, right);
            default:
                break;
        }

        return null;
    }

    @Override
    public Object visitGroupingExpr(Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitUnaryExpr(Unary expr) {
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double)right;
            default:
                break;
        }

        return null;
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    private void checkStringOrDoubleOperands(Token operator, Object left, Object right) {
        if ((left instanceof String || left instanceof Double) && (right instanceof String || right instanceof Double)) return;
        throw new RuntimeError(operator, "Operands must be numbers, strings, or both of them.");
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean)object;
        return true;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;
        return a.equals(b);
    }

    private String stringify(Object object) {
        if (object == null) return "nil";

        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString();
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }
}
