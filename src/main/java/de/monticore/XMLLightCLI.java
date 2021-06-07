package de.monticore;

import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.io.paths.MCPath;
import de.monticore.lang.xmllight.XMLLightMill;
import de.monticore.lang.xmllight._ast.ASTXMLDocument;
import de.monticore.lang.xmllight._od.XMLLight2OD;
import de.monticore.lang.xmllight._parser.XMLLightParser;
import de.monticore.lang.xmllight._symboltable.*;
import de.monticore.lang.xmllight.prettyprint.XMLLightPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.utils.Names;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Command line interface for the XML(light) language and corresponding tooling.
 * Defines, handles, and executes the corresponding command line options and
 * arguments, such as --help
 */
public class XMLLightCLI {


  /* Part 1: Handling the arguments and options
  /*=================================================================*/

	/**
	 * Main method that is called from command line and runs the XML(light) tool.
	 *
	 * @param args The input parameters for configuring the XML(light) tool.
	 */
	public static void main(String[] args) {
		XMLLightCLI cli = new XMLLightCLI();
		cli.run(args);
	}

	/**
	 * Processes user input from command line and delegates to the corresponding
	 * tools.
	 *
	 * @param args The input parameters for configuring the XML(light) tool.
	 */
	public void run(String[] args) {

		Options options = initOptions();

		try {
			// create CLI parser and parse input options from command line
			CommandLineParser clipparser = new DefaultParser();
			CommandLine cmd = clipparser.parse(options, args);

			// help: when --help
			if (cmd.hasOption("h")) {
				printHelp(options);
				// do not continue, when help is printed
				return;
			}

			// if -i input is missing: also print help and stop
			if (!cmd.hasOption("i")) {
				printHelp(options);
				// do not continue, when help is printed
				return;
			}

			// parse input file, which is now available
			// (only returns if successful)
			ASTXMLDocument ast = parseFile(cmd.getOptionValue("i"));

			MCPath mp = new MCPath();

			// -option path
			if (cmd.hasOption("path")) {
				for (String p : cmd.getOptionValue("path").split(" ")) {
					mp.addEntry(Paths.get(p));
				}
			}

			IXMLLightArtifactScope symbolTable = createSymbolTable(ast, mp);
			symbolTable.setName("");
			// print (and optionally store) symbol table
			if (cmd.hasOption("s")) {
				//storing to default directory
				Path output = Paths.get("target");
				de.monticore.symboltable.serialization.JsonPrinter.disableIndentation();
				String s = cmd.getOptionValue("symboltable", StringUtils.EMPTY);
				XMLLightDeSer deser = new XMLLightDeSer();

				if (!s.isEmpty()) {
					String symbolFile = output.resolve(s.trim()).toAbsolutePath().toString();
					storeSymbols(symbolTable, symbolFile, deser);
				} else {
					storeSymbols(symbolTable, output, deser);
				}

				//print (formatted!) symboltable to console
				de.monticore.symboltable.serialization.JsonPrinter.enableIndentation();
				System.out.println(deser.serialize(symbolTable,new XMLLightSymbols2Json()));
			}

			// -option pretty print
			if (cmd.hasOption("pp")) {
				String path = cmd.getOptionValue("pp", StringUtils.EMPTY);
				prettyPrint(ast,path);
			}

			// -option syntax objects
			if (cmd.hasOption("so")) {
				String path = cmd.getOptionValue("so", StringUtils.EMPTY);
				xml2od(ast, getModelNameFromFile(cmd.getOptionValue("i")), path);
			}
		} catch (ParseException e) {
			// an unexpected error from the apache CLI parser:
			Log.error("0xA7112 Could not process CLI parameters: " + e.getMessage());
		}

	}

	/**
	 * Processes user input from command line and delegates to the corresponding
	 * tools.
	 *
	 * @param options The input parameters and options.
	 */
	public void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.setWidth(80);
		formatter.printHelp("XMLLightCLI", options);
	}

	/*=================================================================*/
  /* Part 2: Executing arguments
  /*=================================================================*/

	/**
	 * Parses the contents of a given file as XML(light).
	 *
	 * @param path The path to the XML(light)-file as String
	 */
	public ASTXMLDocument parseFile(String path) {
		Optional<ASTXMLDocument> xmlDoc = Optional.empty();

		// disable fail-quick to find all parsing errors
		Log.enableFailQuick(false);
		XMLLightParser parser = new XMLLightParser();
		try {
			Path model = Paths.get(path.trim());
			xmlDoc = parser.parse(model.toAbsolutePath().toString());
		} catch (IOException | NullPointerException e) {
			Log.error("0xA7113 Input file '" + path + "' not found.");
		}

		// re-enable fail-quick to print potential errors
		Log.enableFailQuick(true);
		return xmlDoc.get();
	}

	/**
	 * Prints the contents of the XML(light)-AST to stdout or a specified file.
	 *
	 * @param xmldoc The XML(light)-AST to be pretty printed
	 * @param file   The target file name for printing the XML(light) artifact. If empty,
	 *               the content is printed to stdout instead
	 */
	public void prettyPrint(ASTXMLDocument xmldoc, String file) {
		XMLLightPrettyPrinter pp = new XMLLightPrettyPrinter();
		String s = pp.printXMLDocument(xmldoc);
		print(s, file);
	}

	/*=================================================================*/

	/**
	 * stores the symbol table of a passed ast in a file created in the passed output directory.
	 * The file path for the stored symbol table of an XML(light) "abc.BasicPhone.XML(light)" and the output
	 * path "target" will be: "target/abc/BasicPhone.XML(light)sym"
	 *
	 * @return serialized String
	 */
	public String storeSymbols(IXMLLightArtifactScope scope, Path out,
														 XMLLightDeSer deser) {
		Path f = out
			.resolve(Paths.get(Names.getPathFromPackage(scope.getPackageName())))
			.resolve(scope.getName() + ".xmlsym");
		String serialized = deser.serialize(scope,new XMLLightSymbols2Json());
		print(serialized, f.toString());
		return serialized;
	}

	/**
	 * Creates the symbol table from the parsed AST.
	 *
	 * @param ast The top XML(light) model element.
	 * @return The artifact scope derived from the parsed AST
	 */
	public IXMLLightArtifactScope createSymbolTable(ASTXMLDocument ast, MCPath symbolPath) {
		IXMLLightGlobalScope globalScope = XMLLightMill.globalScope();
		globalScope.clear();
		globalScope.setSymbolPath(symbolPath);

		XMLLightScopesGenitorDelegator symbolTable = XMLLightMill.scopesGenitorDelegator();
		return symbolTable.createFromAST(ast);
	}

	/**
	 * Extracts the model name from a given file name. The model name corresponds
	 * to the unqualified file name without file extension.
	 *
	 * @param file The path to the input file
	 * @return The extracted model name
	 */
	public String getModelNameFromFile(String file) {
		String modelName = new File(file).getName();
		// cut file extension if present
		if (modelName.length() > 0) {
			int lastIndex = modelName.lastIndexOf(".");
			if (lastIndex != -1) {
				modelName = modelName.substring(0, lastIndex);
			}
		}
		return modelName;
	}

	/**
	 * Creates an object diagram for the XML(light)-AST to stdout or a specified file.
	 *
	 * @param xmlDoc    The XML(light)-AST for which the object diagram is created
	 * @param modelName The derived model name for the XML(light)-AST
	 * @param file      The target file name for printing the object diagram. If empty,
	 *                  the content is printed to stdout instead
	 */
	public void xml2od(ASTXMLDocument xmlDoc, String modelName, String file) {
		// initialize XML(light)n2od printer
		IndentPrinter printer = new IndentPrinter();
		MontiCoreNodeIdentifierHelper identifierHelper = new MontiCoreNodeIdentifierHelper();
		ReportingRepository repository = new ReportingRepository(identifierHelper);
		XMLLight2OD xml2od = new XMLLight2OD(printer, repository);

		// print object diagram
		String od = xml2od.printObjectDiagram((new File(modelName)).getName(), xmlDoc);
		print(od, file);
	}

	/**
	 * Prints the given content to a target file (if specified) or to stdout (if
	 * the file is Optional.empty()).
	 *
	 * @param content The String to be printed
	 * @param path    The target path to the file for printing the content. If empty,
	 *                the content is printed to stdout instead
	 */
	public void print(String content, String path) {
		// print to stdout or file
		if (path.isEmpty()) {
			System.out.println(content);
		} else {
			File f = new File(path.trim());
			// create directories (logs error otherwise)
			f.getAbsoluteFile().getParentFile().mkdirs();

			FileWriter writer;
			try {
				writer = new FileWriter(f);
				writer.write(content);
				writer.close();
			} catch (IOException e) {
				Log.error("0xA7114 Could not write to file " + f.getAbsolutePath());
			}
		}
	}

	/**
	 * stores the symbol table of a passed ast in a file at the passed symbolFileName
	 *
	 * @param deser          Deserializer for XML(light)Artifact
	 * @param scope          XML(light)Artifact to be stored
	 * @param symbolFileName Location in which the symboltable should be stored
	 * @return serialized String
	 */
	public String storeSymbols(IXMLLightArtifactScope scope,
														 String symbolFileName, XMLLightDeSer deser) {
		String serialized = deser.serialize(scope,new XMLLightSymbols2Json());
		print(serialized,symbolFileName);
		return serialized;
	}


	/*=================================================================*/
  /* Part 3: Defining the options incl. help-texts
  /*=================================================================*/

	/**
	 * Initializes the available CLI options for the XML(light) tool.
	 *
	 * @return The CLI options with arguments.
	 */
	protected Options initOptions() {
		Options options = new Options();

		// help dialog
		options.addOption(Option.builder("h")
			.longOpt("help")
			.desc("Prints this help dialog")
			.build());

		// parse input file
		options.addOption(Option.builder("i")
			.longOpt("input")
			.argName("file")
			.hasArg()
			.desc("Reads the source file (mandatory) and parses the contents as XML (light)")
			.build());

		// pretty print XML(light)
		options.addOption(Option.builder("pp")
			.longOpt("prettyprint")
			.argName("file")
			.optionalArg(true)
			.numberOfArgs(1)
			.desc("Prints the XML(light)-AST to stdout or the specified file (optional)")
			.build());

		// print object diagram
		options.addOption(Option.builder("so")
			.longOpt("syntaxobjects")
			.argName("file")
			.optionalArg(true)
			.numberOfArgs(1)
			.desc("Prints an object diagram of the XML(light)-AST to stdout or the specified file (optional)")
			.build());

		// set path for imported symbols
		options.addOption(Option.builder("path")
			.desc("Sets the artifact path for imported symbols")
			.argName("dirlist")
			.hasArg()
			.hasArgs()
			.valueSeparator(' ')
			.build());

		options.addOption(Option.builder("s")
			.longOpt("symboltable")
			.desc("Serializes and prints the symbol table to stdout, if present, the specified output file")
			.optionalArg(true)
			.numberOfArgs(1)
			.argName("file")
			.build());
		return options;
	}
}
