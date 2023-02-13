package de.monticore.lang.xmllight._prettyprint;

import de.monticore.lang.xmllight._ast.ASTXMLAttribute;
import de.monticore.lang.xmllight._ast.ASTXMLPI;
import de.monticore.prettyprint.IndentPrinter;

public class XMLLightPrettyPrinter extends XMLLightPrettyPrinterTOP {
  public XMLLightPrettyPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(de.monticore.lang.xmllight._ast.ASTXMLProlog node) {
    if(!node.isEmptyXMLAttributes()) {
      getPrinter().print("<?xml ");
      for(ASTXMLAttribute x:
              node.getXMLAttributeList()) {
        x.accept(getTraverser());
      }
      getPrinter().print("?>");
    }
    if(!node.isEmptyXMLPIs()) {
      for(ASTXMLPI x:
              node.getXMLPIList()) {
        x.accept(getTraverser());
      }
    }
    if(node.isPresentDocTypeDecl()) {
      node.getDocTypeDecl().accept(getTraverser());
    }
  }


}
