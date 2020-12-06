/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.xmllight.prettyprint;

import java.util.Iterator;

import de.monticore.lang.xmllight._ast.ASTXMLAttribute;
import de.monticore.lang.xmllight._ast.ASTXMLBoolean;
import de.monticore.lang.xmllight._ast.ASTXMLContent;
import de.monticore.lang.xmllight._ast.ASTXMLDocument;
import de.monticore.lang.xmllight._ast.ASTXMLName;
import de.monticore.lang.xmllight._ast.ASTXMLNode;
import de.monticore.lang.xmllight._ast.ASTXMLNull;
import de.monticore.lang.xmllight._ast.ASTXMLString;
import de.monticore.lang.xmllight._visitor.XMLLightVisitor;
import de.monticore.literals.mccommonliterals._ast.ASTSignedBasicDoubleLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTSignedBasicFloatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTSignedBasicLongLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTSignedNatLiteral;
import de.monticore.prettyprint.IndentPrinter;

public class XMLLightPrettyPrinter extends IndentPrinter implements XMLLightVisitor {

  /**
   * Default Constructor.
   */
  public XMLLightPrettyPrinter() {

  }

  /**
   * Serializes and pretty-prints the XML-AST.
   *
   * @param xmlDocument The root node of the input AST
   * @return The pretty-printed XML-AST as String
   */
  public String printXMLDocument(ASTXMLDocument xmlDocument) {
    clearBuffer();
    getRealThis().handle(xmlDocument);
    return getContent();
  }

  @Override
  public void handle(ASTXMLNode node) {
    print("<");
    print(node.getName());

    // traverse attributes
    Iterator<ASTXMLAttribute> iter_attributes = node.getXMLAttributeList().iterator();
    while (iter_attributes.hasNext()) {
      iter_attributes.next().accept(getRealThis());
    }

    if (node.isPresentEndFlag()) {
      print(">");
      println();
      indent();

      // traverse node content
      Iterator<ASTXMLContent> iter_content = node.getXMLContentList().iterator();
      while (iter_content.hasNext()) {
        iter_content.next().accept(getRealThis());
      }

      println();
      unindent();
      print("</");
      print(node.getName());
      print(">");
      println();
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
  public void visit(ASTXMLBoolean node) {
    print(node.getValue().getValue());
  }
  
  @Override
  public void visit(ASTXMLNull node) {
    print("null");
  }
  
  @Override
  public void visit(ASTSignedBasicDoubleLiteral node) {
    print(node.getSource());
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

}
