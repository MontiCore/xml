/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.xmllight._parser;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import de.monticore.lang.xmllight._ast.ASTXMLDocument;

public class XMLParserTest {
  
  @Test
  public void testBookstore() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/xml/parser/bookstore.xml");
    XMLLightParser parser = new XMLLightParser();
    
    Optional<ASTXMLDocument> xmlDoc = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(xmlDoc.isPresent());
  }
  
}
