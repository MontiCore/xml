/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import de.monticore.lang.xmllight.XMLLightTool;
import de.monticore.lang.xmllight._ast.ASTXMLDocument;
import de.monticore.lang.xmllight._parser.XMLLightParser;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class XMLLightToolTest {

  private static final String INPUT = "src/test/resources/xml/parser/bookstore.xml";
  private static final String PRINT = "target/generated-test-sources/bookstore.txt";


  @Before
  public void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testParseAndPrint() throws IOException {
    String[] args = { "-i", INPUT, "-pp", PRINT };
    XMLLightTool.main(args);

    // check if printed XML is valid
    XMLLightParser parser = new XMLLightParser();
    Path expected = Paths.get(INPUT);
    Optional<ASTXMLDocument> xmlDocexp = parser.parse(expected.toString());
    Path model = Paths.get(PRINT);
    Optional<ASTXMLDocument> xmlDoc = parser.parse(model.toString());
    assertFalse(parser.hasErrors());
    assertTrue(xmlDoc.isPresent());
    assertTrue(xmlDocexp.get().deepEquals(xmlDoc.get()));
  }

  @Test
  public void testSyntaxObjects() throws IOException {
    String[] args = { "-i", INPUT, "-so" };
    XMLLightTool.main(args);
  }

  @Test
  public void testSymbolTable() throws IOException {
    String[] args = { "-i", INPUT, "-s" };
    XMLLightTool.main(args);
  }
}
