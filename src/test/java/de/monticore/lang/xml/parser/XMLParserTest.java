/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.xml.parser;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import de.monticore.lang.xml._ast.ASTXMLDocument;
import de.monticore.lang.xml._parser.XMLParser;

public class XMLParserTest {
  
  @Test
  public void testBookstore() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/xml/parser/bookstore.xml");
    XMLParser parser = new XMLParser();
    
    Optional<ASTXMLDocument> jsonDoc = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(jsonDoc.isPresent());
  }
  
}
