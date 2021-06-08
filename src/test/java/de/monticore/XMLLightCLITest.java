/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import de.monticore.lang.xmllight._ast.ASTXMLDocument;
import de.monticore.lang.xmllight._parser.XMLLightParser;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class XMLLightCLITest {

	private static final String INPUT = "src/test/resources/xml/parser/bookstore.xml";
	private static final String PRINT = "target/generated-test-sources/bookstore.txt";

	@Test
	public void testParseAndPrint() throws IOException {
		String[] args = { "-i", INPUT, "-pp", PRINT };
		XMLLightCLI.main(args);

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
		XMLLightCLI.main(args);
	}

	@Test
	public void testSymbolTable() throws IOException {
		String[] args = { "-i", INPUT, "-s" };
		XMLLightCLI.main(args);
	}
}
