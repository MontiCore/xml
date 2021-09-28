/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.xmllight.prettyprint;

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
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLLightToJSON extends IndentPrinter implements XMLLightVisitor2, XMLLightHandler, XMLBasisVisitor2, MCCommonLiteralsVisitor2 {
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
  public XMLLightToJSON() {
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
    println("{");
    indent();
    xmlDocument.accept(getTraverser());
    println();
    unindent();
    print("}");
    return getContent();
  }

  @Override
  public void handle(ASTXMLProlog node) {
    if (node.getXMLAttributeList().size()>0){
    print("\"xml\": ");
      print("{");
      println();
      indent();
      boolean first = true;
      for (ASTXMLAttribute a:
           node.getXMLAttributeList()) {
        if (first){
          first=false;
        }else {
          println(",");
        }
        a.accept(getTraverser());
      }
      println();
      unindent();
      println("},");
    }
    if (node.isPresentDocTypeDecl()){
      node.getDocTypeDecl().accept(getTraverser());
      println(",");
    }
    if (node.getXMLPIList().size()>0){
      for (ASTXMLPI p:
           node.getXMLPIList()) {
        p.accept(getTraverser());
        println(",");
      }
    }
  }

  @Override
  public void handle(ASTDocTypeDecl node) {
    println("\"!DOCTYPE\": {");
    indent();
    print("\"type\": \"");
    node.getXMLName().accept(getTraverser());
    print("\"");
    if((node.isPresentIntSubset())||(node.isPresentExternalID())){
      println(",");
    }
    if (node.isPresentExternalID()) {
      node.getExternalID().accept(getTraverser());
      if (node.isPresentIntSubset()) {
        println(",");
      }
    }
    if (node.isPresentIntSubset()) {
      print("\"InternalSubsets\": \"Not implemented\"");
    }
    println();
    unindent();
    print("}");
  }

  @Override
  public void visit(ASTExternalID node) {
    println("\"ExternalID\": {");
    indent();
    print("\"SystemOrPublic\": \"");
    if (node.isSYSTEM()){
      print("SYSTEM");
    } else if (node.isPUBLIC()){
      print("PUBLIC");
    }
    println("\",");
    if (node.isPresentPubidLiteral()){
      println("\"PubID\": \""+node.getPubidLiteral()+"\",");
    }
    println("\"SystemLiteral\": \""+node.getSystemLiteral()+"\"");
    unindent();
    print("}");
  }

  @Override
  public void handle(ASTXMLNode node) {
    print("\"" + node.getName() + "\": ");
    printXMLNodeWithoutName(node);
  }

  /**
   * Goes through XMLContents and groups similar named XML-Nodes inside the traversed node
   *
   * @param node XMLNode to be traversed
   * @return List of indexes that have been grouped into an Array
   */
  private List<Integer> groupAttributesToArray(ASTXMLNode node) {
    List<Integer> substituted = new ArrayList<>();
    //First traversal: index content and identify duplicates
    Map<String, List<Integer>> content = new HashMap<>();
    for (int i = 0; i < node.getXMLContentList().size(); i++) {
      if (node.getXMLContent(i) instanceof ASTXMLNode) {
        ASTXMLNode n = (ASTXMLNode) node.getXMLContent(i);
        if (content.containsKey(n.getName())) {
          content.get(n.getName()).add(i);
        } else {
          List<Integer> list = new ArrayList<>();
          list.add(i);
          content.put(n.getName(), list);
        }
      }
    }
    //Second: Traverse Map and group into Array
    boolean linebreak = !node.isEmptyXMLAttributes();
    for (String v :
      content.keySet()) {
      if (content.get(v).size() > 1) {
        if (!linebreak) {
          linebreak = true;
        } else {
          println(",");
        }
        printToArray(node, content.get(v), v);
        substituted.addAll(content.get(v));
      }
    }
    return substituted;
  }

  /**
   * Prints array by using the given parameters
   *
   * @param node    given Supernode
   * @param indexes List of indices to be grouped, mustn't contain less than 2 arguments
   *                or indices that dont refer to XMLContent that is an instance of XMLNode
   * @param name    Name of the Array
   */
  private void printToArray(ASTXMLNode node, List<Integer> indexes, String name) {
    //Check for valid parameters
    if (indexes.size() < 2) {
      Log.error("Error 0xA7110: parameter indexes has to have at least two elements");
    }
    for (Integer i :
      indexes) {
      if (!(node.getXMLContent(i) instanceof ASTXMLNode)) {
        Log.error("Error 0xA7111: Element " + i + "of node " + node.getName() + "is not an ASTXMLNode");
      }
    }

    //Now we are sure we have valid parameters
    println("\"" + name + "\":[");
    indent();
    boolean isFirst = true;
    for (Integer i :
      indexes) {
      if (isFirst) {
        isFirst = false;
      } else {
        println(",");
      }
      printXMLNodeWithoutName((ASTXMLNode) node.getXMLContent(i));
    }
    println();
    unindent();
    print("]");
  }

  /**
   * Like handle(ASTXMLNode), but doesn't print the name of the node
   *
   * @param node Node to be printed
   */
  private void printXMLNodeWithoutName(ASTXMLNode node) {
    //See if linebreak is necessary
    boolean linebreak = false;
    boolean first = true;
    if ((node.getXMLContentList().size() + node.getXMLAttributeList().size()) > 1) {
      linebreak = true;
      println("{");
      indent();
    }
    // traverse attributes
    for (ASTXMLAttribute a :
      node.getXMLAttributeList()) {
      if (first) {
        first = false;
      } else {
        println(",");
      }
      a.accept(getTraverser());
    }
    if (node.isPresentEndFlag()) {
      //Duplicate Content keys generate an Array
      List<Integer> ignored = groupAttributesToArray(node);
      if (ignored.size() > 0) {
        first = false;
      }
      // traverse node content
      for (int i = 0; i < node.getXMLContentList().size(); i++) {
        if (!ignored.contains(i)) {
          if (first) {
            first = false;
          } else {
            println(",");
            //Special case: No key for content, and attributes
            if (node.getXMLContentList().size() == 1) {
              print("\"" + node.getName() + "\": ");
            }
          }
          node.getXMLContent(i).accept(getTraverser());
        }
      }
    } else if (node.isEmptyXMLAttributes()) {
      print("null");
    }
    if (linebreak) {
      println();
      unindent();
      print("}");
    }
  }

  @Override
  public void visit(ASTXMLAttribute node) {
    print("\"-" + node.getName() + "\": ");
  }

  @Override
  public void handle(ASTXMLPI node) {
    print("\"?" + node.getXMLName().getName().replaceAll("\"", "") + "\": ");
    for (ASTXMLValue v :
      node.getXMLValueList()) {
      v.accept(getTraverser());
    }
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
    print(node.getPre() + "." + node.getPost());
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
    if (node.isPresentString()) {
      print("\"" + node.getString() + "\"");
    } else {
      print("'" + node.getCharSequence() + "'");
    }
  }

  @Override
  public void visit(ASTXMLName node) {
    print(node.getName() + " ");
  }

  @Override
  public void visit(ASTXMLCharData node) {
    if ((node.getXMLValueList().size() != 1) || (node.getXMLValue(0) instanceof ASTXMLName)) {
      print("\"");
    }
  }

  @Override
  public void endVisit(ASTXMLCharData node) {
    if ((node.getXMLValueList().size() != 1) || (node.getXMLValue(0) instanceof ASTXMLName)) {
      print("\"");
    }
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

