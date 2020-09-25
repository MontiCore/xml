/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.xml.prettyprint;

import de.monticore.lang.xml._ast.ASTXMLDocument;
import de.monticore.lang.xml._parser.XMLParser;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class XMLPrettyPrinterTest {

  @Test
  public void testBookstore() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/xml/prettyprint/bookstore.xml");
    XMLParser parser = new XMLParser();
    
    // parse model
    Optional<ASTXMLDocument> xmlDoc = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(xmlDoc.isPresent());

    // pretty print AST
    XMLPrettyPrinter pp = new XMLPrettyPrinter();
    String printedModel = pp.printXMLDocument(xmlDoc.get());

    // parse printed model
    Optional<ASTXMLDocument> printedXMLDoc = parser.parse_StringXMLDocument(printedModel);
    assertFalse(parser.hasErrors());
    assertTrue(printedXMLDoc.isPresent());
    
    // original model and printed model should be the same
    assertTrue(xmlDoc.get().deepEquals(printedXMLDoc.get(), true));
  }

}
