package nl.han.ica.icss.parser;


import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {

	//Accumulator attributes:
	private AST ast;

	//Use this to keep track of the parent nodes when recursively traversing the ast
	private final IHANStack<ASTNode> currentContainer;

	public ASTListener() {
		ast = new AST();
		currentContainer = new HANStack<>();
	}

	public AST getAST() {
		return ast;
	}

	@Override
	public void enterStylerule(ICSSParser.StyleruleContext ctx) {
		//PA00-PA04
		Stylerule stylerule = new Stylerule();
		enter(stylerule);
	}

	@Override
	public void exitStylerule(ICSSParser.StyleruleContext ctx) {
		//PA00-PA04
		currentContainerPop();
	}

	@Override
	public void enterSelector(ICSSParser.SelectorContext ctx) {
		//PA00-PA04
		String selector = ctx.getText();
		Selector selectorNode;
		if (selector.startsWith("#")) {
			selectorNode = new IdSelector(selector);
		} else if (selector.startsWith(".")) {
			selectorNode = new ClassSelector(selector);
		} else {
			selectorNode = new TagSelector(selector);
		}
		enter(selectorNode);

	}

	@Override
	public void exitSelector(ICSSParser.SelectorContext ctx) {
		//PA00-PA04
		currentContainerPop();
	}


	@Override
	public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
		//PA00-PA04
		String dec = ctx.LOWER_IDENT().getSymbol().getText();
		Declaration declaration = new Declaration(dec);
		enter(declaration);
	}

	@Override
	public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
		//PA00-PA04
		currentContainerPop();
	}

	@Override
	public void enterVariableassigment(ICSSParser.VariableassigmentContext ctx) {
		//PA02
		VariableAssignment variableAssignment = new VariableAssignment();
		enter(variableAssignment);
	}

	@Override
	public void exitVariableassigment(ICSSParser.VariableassigmentContext ctx) {
		//PA02
		currentContainerPop();
	}

	@Override
	public void enterVariableReference(ICSSParser.VariableReferenceContext ctx) {
		//PA02
		String variable = ctx.getText();
		VariableReference variableReference = new VariableReference(variable);
		enter(variableReference);
	}

	@Override
	public void exitVariableReference(ICSSParser.VariableReferenceContext ctx) {
		//PA02
		currentContainerPop();
	}

	@Override
	public void enterLiteral(ICSSParser.LiteralContext ctx) {
		//PA00-PA04
		String literal = ctx.getText();
		Literal literalNode;
		if (literal.startsWith("#")) {
			literalNode = new ColorLiteral(literal);
		} else if (literal.endsWith("%")) {
			literalNode = new PercentageLiteral(literal);
		} else if (literal.endsWith("px")) {
			literalNode = new PixelLiteral(literal);
		} else if (literal.equals("TRUE") || literal.equals("FALSE")) {
			literalNode = new BoolLiteral(literal);
		}
		else {
			literalNode = new ScalarLiteral(literal);
		}
		enter(literalNode);
	}

	@Override
	public void exitLiteral(ICSSParser.LiteralContext ctx) {
		//PA00-PA04
		currentContainerPop();
	}

	@Override
	public void enterOperation(ICSSParser.OperationContext ctx) {
		//PA03
		Operation operation = null;
		String text = ctx.operatorLiteral().getText();

		switch (text) {
			case "+":
				operation = new AddOperation();
				break;
			case "-":
				operation = new SubtractOperation();
				break;
			case "*":
				operation = new MultiplyOperation();
				break;
		}

		enter(operation);
	}

	@Override
	public void exitOperation(ICSSParser.OperationContext ctx) {
		//PA03
		currentContainerPop();
	}

	@Override
	public void enterIfclause(ICSSParser.IfclauseContext ctx) {
		//PA04
		IfClause ifClause = new IfClause();
		enter(ifClause);
	}

	@Override
	public void exitIfclause(ICSSParser.IfclauseContext ctx) {
		//PA04
		currentContainerPop();
	}

	@Override
	public void enterElseclause(ICSSParser.ElseclauseContext ctx) {
		//PA04
		ElseClause elseClause = new ElseClause();
		enter(elseClause);
	}

	@Override
	public void exitElseclause(ICSSParser.ElseclauseContext ctx) {
		//PA04
		currentContainerPop();
	}

	private void currentContainerPop() {
		//PA00-PA04
		currentContainer.pop();
	}

	private void enter(ASTNode astNode) {
		//PA00-PA04
		if (currentContainer.isEmpty()) {
			ast.root.addChild(astNode);
		} else {
			currentContainer.peek().addChild(astNode);
		}
		currentContainer.push(astNode);
	}


}