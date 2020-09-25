/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.xml.prettyprint;

import de.monticore.lang.xml._ast.*;
import de.monticore.lang.xml._visitor.XMLInheritanceVisitor;
import de.monticore.lang.xml._visitor.XMLVisitor;
import de.monticore.literals.mccommonliterals._ast.*;
import de.monticore.prettyprint.IndentPrinter;

import java.util.Iterator;
import java.util.List;

public class XMLPrettyPrinter extends IndentPrinter implements XMLVisitor {

  /**
   * Default Constructor.
   */
  public XMLPrettyPrinter() {

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
  public void handle(ASTXNode node) {
    print("<");
    print(node.getName());

    // traverse attributes
    Iterator<ASTAttribute> iter_attributes = node.getAttributeList().iterator();
    while (iter_attributes.hasNext()) {
      iter_attributes.next().accept(getRealThis());
    }

    if (node.isPresentEndKey()) {
      print(">");
      println();
      indent();

      // traverse node content
      Iterator<ASTXNodeContent> iter_content = node.getContentList().iterator();
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
  public void visit(ASTAttribute node) {
    print(" ");
    print(node.getKey());
    print("=");
  }

  @Override
  public void visit(ASTXMLArray node) {
    println("[");
  }
  
  @Override
  public void endVisit(ASTXMLArray node) {
    print("]");
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
  public void visit(ASTStringLiteral node) {
    print("\"" + node.getSource() + "\"");
  }

  @Override
  public void visit(ASTXMLNames node) {
    String sep = "";
    for (String name : node.getValueList()) {
      print(sep + name);
      sep = " ";
    }
  }

}
