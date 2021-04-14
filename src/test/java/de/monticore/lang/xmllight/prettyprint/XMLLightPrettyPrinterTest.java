/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.xmllight.prettyprint;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Ignore;
import org.junit.Test;

import de.monticore.lang.fullxml._parser.FullXMLParser;
import de.monticore.lang.xmllight._ast.ASTXMLDocument;

public class XMLLightPrettyPrinterTest {
  
  @Ignore
  @Test
  public void testBookstore() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/xml/prettyprint/bookstore.xml");
    FullXMLParser parser = new FullXMLParser();
    
    // parse model
    Optional<ASTXMLDocument> xmlDoc = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(xmlDoc.isPresent());
    
    // pretty print AST
    XMLLightPrettyPrinter pp = new XMLLightPrettyPrinter();
    String printedModel = pp.printXMLDocument(xmlDoc.get());
    
    // parse printed model
    Optional<ASTXMLDocument> printedXMLDoc = parser.parse_StringXMLDocument(printedModel);
    assertFalse(parser.hasErrors());
    assertTrue(printedXMLDoc.isPresent());
    
    // original model and printed model should be the same
    assertTrue(xmlDoc.get().deepEquals(printedXMLDoc.get()));
  }
  
}
