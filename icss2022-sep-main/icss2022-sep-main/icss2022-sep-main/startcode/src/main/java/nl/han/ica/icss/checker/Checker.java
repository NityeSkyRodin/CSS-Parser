package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.types.ExpressionType;
import java.util.HashMap;


public class Checker {

    private IHANLinkedList<HashMap<String, ExpressionType>> variableTypes;

    public void check(AST ast) {
        //ALL CODE Checker
        variableTypes = new HANLinkedList<>();
        checkStyleSheet(ast.root);
    }

    private void checkStyleSheet(Stylesheet stylesheet){
        variableTypes.addFirst(new HashMap<>());

    for(ASTNode child: stylesheet.getChildren()){
        String childLabel = child.getNodeLabel();
        if(childLabel.equals("Stylerule")){
            checkStylerule((Stylerule) child);
        }
        if(childLabel.contains("VariableAssignment")){
            checkVariableAssignment((VariableAssignment) child);
        }
    }
    variableTypes.removeFirst();
    }

    private void checkStylerule(Stylerule stylerule){
        variableTypes.addFirst(new HashMap<>());
        checkBody(stylerule);
        variableTypes.removeFirst();
    }

    private void checkBody(ASTNode astNode){

        for(ASTNode child: astNode.getChildren()){
            String childLabel = child.getNodeLabel();
            if(childLabel.equals("Declaration")){
                checkDeclaration((Declaration) child);
            }
            if(childLabel.contains("VariableAssignment")){
                checkVariableAssignment((VariableAssignment) child);
            }
            if(childLabel.equals("If_Clause")){
                checkIfClause((IfClause) child);
            }
        }
    }


    private void checkDeclaration(Declaration declaration) {
        variableTypes.addFirst(new HashMap<>());
        Expression expression = declaration.expression;

        ExpressionType expressionType = checkExpressionType(expression);

        //CH04 kijkt of literal past bij de naam
        if (expressionType == ExpressionType.UNDEFINED) {
            declaration.setError("ERROR_CODE 1: INVALID");

        }
         if (declaration.property.name.equals("color") || declaration.property.name.equals("background-color")){
            if (expressionType != ExpressionType.COLOR){
                declaration.setError("ERROR_CODE 4:" + declaration.property.name+" "+declaration.expression.getNodeLabel() + " IS NOT VALID FOR PROPERTY");
            }
        }
         if(declaration.property.name.equals("width") || declaration.property.name.equals("height")){
            if(expressionType != ExpressionType.PIXEL && expressionType != ExpressionType.PERCENTAGE){
                declaration.setError("ERROR_CODE 4:" + declaration.property.name+" "+declaration.expression.getNodeLabel() + " IS NOT VALID FOR PROPERTY");
            }
        }

    variableTypes.removeFirst();

    }

    private void checkVariableAssignment(VariableAssignment variableAssignment){

        Expression expression = variableAssignment.expression;

        VariableReference variableReference = variableAssignment.name;
        ExpressionType expressionType = checkExpressionType(expression);


        if (expressionType == ExpressionType.UNDEFINED) {
            //check of variable een waarde heeft, DOET DE PARSER OOK AL
            variableAssignment.setError("ERROR_CODE 1: VARIABLE HAS NO VALUE");
        }

        variableTypes.getFirst().put(variableReference.name,expressionType);
    }

    private void checkIfClause(IfClause ifClause) {
        variableTypes.addFirst(new HashMap<>());
        ExpressionType expressionType = checkExpressionType(ifClause.conditionalExpression);
        if (expressionType != ExpressionType.BOOL) {
            //CH05 if clause moet een boolean zijn
            ifClause.setError("ERROR_CODE 3: CONDITIONAL EXPRESSION IS NOT A BOOLEAN");
        }
        checkBody(ifClause);
        for (ASTNode node: ifClause.getChildren()) {
            if (node instanceof ElseClause) {
                checkElseClause((ElseClause) node);
            }
        }
        variableTypes.removeFirst();
    }

    private void checkElseClause(ElseClause elseClause){
        variableTypes.addFirst(new HashMap<>());
        checkBody(elseClause);
        variableTypes.removeFirst();
    }

    private ExpressionType checkExpressionType(Expression expression) {

        ExpressionType expressionType;
        if (expression instanceof Operation) {
           expressionType = this.checkOperation((Operation) expression);
        }
        else if (expression instanceof VariableReference) {
            expressionType = this.checkVariableReference((VariableReference) expression);
        }
        else if (expression instanceof Literal) {
            expressionType = this.checkLiteral((Literal) expression);
        }
        else {
            expressionType = ExpressionType.UNDEFINED;
        }
        return expressionType;
    }

    private ExpressionType checkLiteral(Literal expressionType){

        if(expressionType instanceof PixelLiteral){
            return ExpressionType.PIXEL;
        }
        if(expressionType instanceof PercentageLiteral){
            return ExpressionType.PERCENTAGE;
        }
        if(expressionType instanceof ScalarLiteral){
            return ExpressionType.SCALAR;
        }
        if(expressionType instanceof ColorLiteral){
            return ExpressionType.COLOR;
        }
        if(expressionType instanceof BoolLiteral){
            return ExpressionType.BOOL;
        }
        else{
            return  ExpressionType.UNDEFINED;
    }
    }
    private ExpressionType checkOperation(Operation operation) {
        ExpressionType lhs = checkExpressionType(operation.lhs);
        ExpressionType rhs = checkExpressionType(operation.rhs);

        //CH03 kleuren mogen niet gebruikt worden in operatoren
        if(lhs == ExpressionType.BOOL || rhs == ExpressionType.BOOL || lhs == ExpressionType.COLOR || rhs == ExpressionType.COLOR){
            operation.setError("ERROR_CODE 7: BOOL/COLOR CAN NOT BE USED IN OPERATION");
            return ExpressionType.UNDEFINED;
        }
        return lhs;
    }

    private ExpressionType checkVariableReference(VariableReference variableReference) {
        ExpressionType expressionType = ExpressionType.UNDEFINED;

            for (int i = 0; i < variableTypes.getSize(); i++) {
                if (variableTypes.get(i) != null && variableTypes.get(i).containsKey(variableReference.name)) {
                    expressionType = variableTypes.get(i).get(variableReference.name);
                }
            }
            if (expressionType == ExpressionType.UNDEFINED) {
                //CH01 && CH06 Variabelen moeten een waarde hebben en mogen niet gebruikt worden als ze niet bestaan/buiten de scope vallen
                variableReference.setError("ERROR_CODE 2: VARIABLE NOT FOUND OR NOT USED IN SCOPE");
                return null;
            }

        return expressionType;
    }
}