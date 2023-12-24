package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;

public class Generator {

	public String generate(AST ast) {
		//GE01	Generate CSS from AST elke functie maakt hier gebruik van
		//GE02	Zorg dat de CSS met twee spaties inspringing per scopeniveau gegenereerd wordt.
     return generateStyleSheet(ast.root);

	}

	private String generateStyleSheet(Stylesheet rule) {

		String ouputValue = "";
		for (ASTNode child : rule.getChildren()) {
			if (child instanceof Stylerule) {
				ouputValue = ouputValue + generateStyleRule(child);
			}
		}
		return ouputValue;
	}

	private String generateStyleRule(ASTNode node){
		Stylerule rule = (Stylerule) node;
		String ouputValue = "";


		ouputValue = ouputValue + rule.selectors.get(0).toString() + " {" + "\n";

		for (ASTNode child : rule.getChildren()) {
			if (child instanceof Declaration) {
				ouputValue = ouputValue + generateDeclaration(child);
			}
		}
		ouputValue = ouputValue + "}" + "\n" + "\n";
		return ouputValue;
	}

	private String generateDeclaration(ASTNode node){
		Declaration declaration = (Declaration) node;

		String ouputValue = "";

		ouputValue = ouputValue + "  " + declaration.property.name + ": ";

		if(declaration.expression instanceof ColorLiteral){
			ouputValue = ouputValue + ((ColorLiteral) declaration.expression).value +";" + "\n";
		}
		if(declaration.expression instanceof  PixelLiteral){
			ouputValue = ouputValue + ((PixelLiteral) declaration.expression).value +"px;" + "\n";
		}
		if (declaration.expression instanceof PercentageLiteral){
			ouputValue = ouputValue + ((PercentageLiteral) declaration.expression).value +"%;" + "\n";
		}

		return ouputValue;
	}

}
