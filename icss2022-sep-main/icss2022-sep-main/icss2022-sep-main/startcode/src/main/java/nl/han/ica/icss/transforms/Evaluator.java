package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.HANLinkedList;
import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;


import java.util.ArrayList;
import java.util.HashMap;



public class Evaluator implements Transform {

    private IHANLinkedList<HashMap<String, Literal>> variableValues;

    public Evaluator() {
        variableValues = new HANLinkedList<>();
    }

    @Override
    public void apply(AST ast) {
        variableValues = new HANLinkedList<>();

        evaluateStyleSheet(ast.root);
    }

    private void evaluateStyleSheet(Stylesheet styleSheet){
        variableValues.addFirst(new HashMap<>());
        ArrayList<ASTNode> nodesRemoved = new ArrayList<>();
        for (ASTNode node : styleSheet.getChildren()) {
            if (node instanceof Stylerule) {
                evaluateStylerule((Stylerule) node);
            } else if (node instanceof VariableAssignment) {
                evaluateVariableAssignment((VariableAssignment) node);
                nodesRemoved.add(node);
            }
        }

        for (ASTNode node : nodesRemoved) {
            styleSheet.removeChild(node);
        }
        variableValues.removeFirst();
    }

    private void evaluateStylerule(Stylerule stylerule){
        variableValues.addFirst(new HashMap<>());

        evaluateBody(stylerule.body);

        variableValues.removeFirst();
    }
    private void evaluateBody(ArrayList<ASTNode> astNode){
        variableValues.addFirst(new HashMap<>());
        ArrayList<ASTNode> nodesToRemove = new ArrayList<>();
        ArrayList<ASTNode> nodesToAdd = new ArrayList<>();
        for (ASTNode node : astNode) {
            String nodeLabel = node.getNodeLabel();
            if (nodeLabel.equals("Declaration")) {
                evaluateDeclaration((Declaration) node);
            }
            else if (nodeLabel.equals("If_Clause")) {
                nodesToAdd.addAll(evaluateIfClause((IfClause) node));
                nodesToRemove.add(node);
            }
            else if (nodeLabel.contains("VariableAssignment")) {
                evaluateVariableAssignment((VariableAssignment) node);
                nodesToRemove.add(node);
            }

        }

        for (ASTNode node : nodesToRemove) {
            astNode.remove(node);
        }
        for (ASTNode node : nodesToAdd) {
            astNode.add(node);
        }
        variableValues.removeFirst();
    }

    private void evaluateDeclaration(Declaration declaration){
        declaration.expression = evaluateExpression(declaration.expression);
    }

    private void evaluateVariableAssignment(VariableAssignment variableAssignment){
        //TR01 vervang alle waarde door de berekende waarde
        variableAssignment.expression = evaluateExpression(variableAssignment.expression);

        Literal literal;
        literal = evaluateExpression(variableAssignment.expression);

        for (int i = 0; i < variableValues.getSize(); i++){
            if(variableValues.get(i).containsKey(variableAssignment.name.name)){
                variableValues.get(i).replace(variableAssignment.name.name,literal);
            }
        }
        variableValues.getFirst().put(variableAssignment.name.name,literal);

    }

    private Literal evaluateVariableReference(VariableReference variableReference) {
        //TR01 vervang alle waarde door de berekende waarde
        for(int i = 0; i < variableValues.getSize(); i++){
            if(variableValues.get(i).containsKey(variableReference.name)){
                return variableValues.get(i).get(variableReference.name);
            }
        }
        return variableValues.getFirst().get(variableReference.name);
    }
    private ArrayList<ASTNode> evaluateIfClause(IfClause ifClause){
        //TR02	evaluate if/else clauses
        boolean ifClauseBool = ((BoolLiteral) evaluateExpression(ifClause.conditionalExpression)).value;

        if(ifClauseBool){
            evaluateBody(ifClause.body);
            return ifClause.body;
        }
        else{
            if(ifClause.elseClause == null){
                return new ArrayList<>();
            }
            evaluateBody(ifClause.elseClause.body);
            return ifClause.elseClause.body;
        }
    }

    private Literal evaluateExpression(Expression expression){

        if (expression instanceof Operation) {
            return evaluateOperation((Operation) expression);
        }
        else if(expression instanceof VariableReference){
            return evaluateVariableReference((VariableReference) expression);
        }
        else if(expression instanceof Literal){
            return (Literal) expression;
        }
        return null;
    }

    private Literal evaluateOperation(Operation operation){
        Literal left = evaluateExpression(operation.lhs);
        Literal right = evaluateExpression(operation.rhs);

        if (operation instanceof AddOperation) {
            return evaluateAddOperation(left, right);
        }else if (operation instanceof SubtractOperation) {
            return evaluateSubtractOperation(left, right);
        }else if (operation instanceof MultiplyOperation) {
            return evaluateMultiplyOperation(left, right);
        }
        return null;
    }

    private Literal evaluateMultiplyOperation(Literal left, Literal right){
        //TR01 vervang alle waarde door de berekende waarde
    if (left instanceof ScalarLiteral && right instanceof PixelLiteral) {

        return  new PixelLiteral(((ScalarLiteral) left).value * ((PixelLiteral) right).value);
    }
    if (left instanceof PixelLiteral && right instanceof ScalarLiteral) {
        return  new PixelLiteral(((PixelLiteral) left).value * ((ScalarLiteral) right).value);
    }
    if (left instanceof ScalarLiteral && right instanceof PercentageLiteral) {
        return new PercentageLiteral(((ScalarLiteral) left).value * ((PercentageLiteral) right).value);
    }
    if (left instanceof PercentageLiteral && right instanceof ScalarLiteral) {
            return new PercentageLiteral(((PercentageLiteral) left).value * ((ScalarLiteral) right).value);
    }
    if (left instanceof ScalarLiteral && right instanceof ScalarLiteral){
        return new ScalarLiteral(((ScalarLiteral) left).value * ((ScalarLiteral) right).value);
    }
    return null;
    }

    private Literal evaluateAddOperation(Literal left, Literal right) {
        //TR01 vervang alle waarde door de berekende waarde
        if (left instanceof PixelLiteral && right instanceof PixelLiteral) {
            return new PixelLiteral(((PixelLiteral) left).value + ((PixelLiteral) right).value);
        }

        if (left instanceof PercentageLiteral && right instanceof PercentageLiteral) {
            return new PercentageLiteral(((PercentageLiteral) left).value + ((PercentageLiteral) right).value);
        }
        return null;
    }


    private Literal evaluateSubtractOperation(Literal left, Literal right){
        //TR01 vervang alle waarde door de berekende waarde
        if (left instanceof PixelLiteral && right instanceof PixelLiteral) {
            return new PixelLiteral(((PixelLiteral) left).value - ((PixelLiteral) right).value);
        }
        if (left instanceof PercentageLiteral && right instanceof PercentageLiteral) {
            return new PercentageLiteral(((PercentageLiteral) left).value - ((PercentageLiteral) right).value);
        }
        return null;
    }
}
