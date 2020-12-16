/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.xmllight.prettyprint;

import java.util.Iterator;

import de.monticore.ast.ASTNode;
import de.monticore.lang.xmlbasis._visitor.XMLBasisVisitor2;
import de.monticore.lang.xmllight._ast.*;
import de.monticore.lang.xmllight._visitor.XMLLightHandler;
import de.monticore.lang.xmllight._visitor.XMLLightTraverser;
import de.monticore.lang.xmllight._visitor.XMLLightTraverserImplementation;
import de.monticore.lang.xmllight._visitor.XMLLightVisitor2;
import de.monticore.literals.mccommonliterals._ast.ASTSignedBasicDoubleLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTSignedBasicFloatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTSignedBasicLongLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTSignedNatLiteral;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor2;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;

public class XMLLightPrettyPrinter extends IndentPrinter implements XMLLightVisitor2, XMLLightHandler, XMLBasisVisitor2, MCCommonLiteralsVisitor2 {
	private XMLLightTraverser traverser;

	public void setTraverser(XMLLightTraverser realThis) {
		this.traverser = realThis;
	}

	public XMLLightTraverser getTraverser() {
		return this.traverser;
	}

	/**
	 * Default Constructor.
	 */
	public XMLLightPrettyPrinter() {
		this.traverser = new XMLLightTraverserImplementation();
		this.traverser.add4XMLLight(this);
		this.traverser.setXMLLightHandler(this);
		this.traverser.add4XMLBasis(this);
		this.traverser.add4MCCommonLiterals(this);
	}

	/**
	 * Serializes and pretty-prints the XML-AST.
	 *
	 * @param xmlDocument The root node of the input AST
	 * @return The pretty-printed XML-AST as String
	 */
	public String printXMLDocument(ASTXMLDocument xmlDocument) {
		clearBuffer();
		xmlDocument.accept(getTraverser());
		return getContent();
	}

	@Override
	public void handle(ASTXMLProlog node) {
		if (!node.isEmptyXMLAttributes()) {
			print("<?xml");
			for (ASTXMLAttribute x :
				node.getXMLAttributeList()) {
				x.accept(getTraverser());
			}
			print("?>");
		}
		if (!node.isEmptyXMLPIs()) {
			for (ASTXMLPI x :
				node.getXMLPIList()) {
				x.accept(getTraverser());
			}
		}
		if (node.isPresentDocTypeDecl()) {
			node.getDocTypeDecl().accept(getTraverser());
		}
	}

	@Override
	public void handle(ASTDocTypeDecl node) {
		println();
		print("<!DOCTYPE " + node.getXMLName() + " ");
		if (node.isPresentExternalID()) {
			node.getExternalID().accept(getTraverser());
		}
		if (node.isPresentIntSubset()) {
			print("[");
			node.getIntSubset().accept(getTraverser());
			print("]");
		}
		print(">");
	}

	@Override
	public void visit(ASTExternalID node) {
		if (node.isSYSTEM()) {
			print("SYSTEM ");
		} else if (node.isPUBLIC()) {
			print("PUBLIC ");
		}
		if (node.isPresentPubidLiteral()) {
			print(node.getPubidLiteral() + " ");
		}
		print(node.getSystemLiteral() + " ");
	}

	@Override
	public void handle(ASTXMLNode node) {
		println();
		print("<");
		print(node.getName());

		// traverse attributes
		Iterator<ASTXMLAttribute> iter_attributes = node.getXMLAttributeList().iterator();
		while (iter_attributes.hasNext()) {
			iter_attributes.next().accept(getTraverser());
		}

		if (node.isPresentEndFlag()) {
			print(">");

			// traverse node content
			Iterator<ASTXMLContent> iter_content = node.getXMLContentList().iterator();
			boolean isFirst =true;
			while (iter_content.hasNext()) {
				iter_content.next().accept(getTraverser());
				if(isFirst){
					isFirst=false;
					indent();
				}
			}
			if (node.getXMLContentList().size()>1)
				println();
			unindent();
			print("</");
			print(node.getName());
			print(">");
		} else {
			print("/>");
		}
	}

	@Override
	public void visit(ASTXMLAttribute node) {
		print(" ");
		print(node.getName());
		print("=");
	}

	@Override
	public void visit(ASTXMLPI node) {
		println();
		print("<? ");
	}

	@Override
	public void endVisit(ASTXMLPI node) {
		print(" ?>");
	}

	@Override
	public void visit(ASTXMLBoolean node) {
		print(node.getValue().getValue());
	}

	@Override
	public void visit(ASTXMLNull node) {
		print("null");
	}

	@Override
	public void visit(ASTSignedBasicDoubleLiteral node) {
		print(node.getPre()+"."+node.getPost());
	}

	@Override
	public void visit(ASTSignedBasicFloatLiteral node) {
		print(node.getSource());
	}

	@Override
	public void visit(ASTSignedBasicLongLiteral node) {
		print(node.getSource());
	}

	@Override
	public void visit(ASTSignedNatLiteral node) {
		print(node.getSource());
	}

	@Override
	public void visit(ASTXMLString node) {
		print("\"" + node.getStringLiteral().getSource() + "\"");
	}

	@Override
	public void visit(ASTXMLName node) {
		print(node.getName() + " ");
	}

	@Override
	public void visit(ISymbol node) {
		//Nothing
	}

	@Override
	public void endVisit(ISymbol node) {
		//Nothing
	}

	@Override
	public void visit(IScope node) {
		//Nothing
	}

	@Override
	public void endVisit(IScope node) {
		//Nothing
	}

	@Override
	public void visit(ASTNode node) {
		//Nothing
	}

	@Override
	public void endVisit(ASTNode node) {
		//Nothing
	}
}
