/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.xmllight._parser;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.antlr.v4.runtime.RecognitionException;

import de.monticore.lang.xmllight._ast.ASTXMLDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XMLParserTest {

  @BeforeEach
  public void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testBookstore() throws RecognitionException, IOException {
    Path model = Paths.get("src/test/resources/xml/parser/bookstore.xml");
    XMLLightParser parser = new XMLLightParser();
    
    Optional<ASTXMLDocument> xmlDoc = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(xmlDoc.isPresent());
  }
  
}
