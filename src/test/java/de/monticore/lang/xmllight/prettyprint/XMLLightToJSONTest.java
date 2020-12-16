package de.monticore.lang.xmllight.prettyprint;

import de.monticore.XMLLightCLI;
import de.monticore.lang.xmllight._ast.ASTXMLDocument;
import de.monticore.lang.xmllight._parser.XMLLightParser;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class XMLLightToJSONTest {
	@Test
	public void testBookstore() throws RecognitionException, IOException {
		Path model = Paths.get("src/test/resources/xml/prettyprint/bookstore.xml");
		XMLLightParser parser = new XMLLightParser();

		// parse model
		Optional<ASTXMLDocument> xmlDoc = parser.parse(model.toString());
		assertFalse(parser.hasErrors());
		assertTrue(xmlDoc.isPresent());

		// pretty print AST
		XMLLightToJSON pp = new XMLLightToJSON();
		String printedModel = pp.printXMLDocument(xmlDoc.get());

		XMLLightCLI cli = new XMLLightCLI();
		cli.print(printedModel,"src/test/resources/xml/prettyprint/bookstore.json");
	}
}
