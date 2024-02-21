package de.monticore;

import de.monticore.lang.xmllight._ast.ASTXMLNode;
import langembeddingfullxml.LangEmbeddingFullXMLMill;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LangEmbeddingFullXMLTest {

  @Test
  public void testParsing() throws IOException {
    var ast = LangEmbeddingFullXMLMill.parser().parse_String(
        "XML :\n" +
        "<tagA>\n" +
        "  <tagB/>\n" +
        "</tagA>"
    ).get();

    assertNotNull(ast);

    var xml = ast.getXMLDocument();
    ASTXMLNode tagA = xml.getXMLNodeList().get(0);
    assertEquals("tagA", tagA.getName());

    ASTXMLNode tagB = (ASTXMLNode) tagA.getXMLContent(0);
    assertEquals("tagB", tagB.getName());
  }
}
