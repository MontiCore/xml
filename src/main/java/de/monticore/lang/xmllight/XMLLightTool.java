/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.xmllight;

import de.monticore.MontiCoreNodeIdentifierHelper;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import de.monticore.io.paths.MCPath;
import de.monticore.lang.xmllight._ast.ASTXMLDocument;
import de.monticore.lang.xmllight._od.XMLLight2OD;
import de.monticore.lang.xmllight._parser.XMLLightParser;
import de.monticore.lang.xmllight._symboltable.*;
import de.monticore.lang.xmllight._visitor.XMLLightTraverser;
import de.monticore.lang.xmllight.prettyprint.XMLLightPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.utils.Names;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class XMLLightTool extends XMLLightToolTOP{

  /*=================================================================*/
  /* Part 1: Handling the arguments and options
  /*=================================================================*/

  /**
   * Processes user input from command line and delegates to the corresponding
   * tools.
   *
   * @param args The input parameters for configuring the XML(light) tool.
   */
  @Override
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
      ASTXMLDocument ast = parse(cmd.getOptionValue("i"));

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
        XMLLightSymbols2Json s2j = new XMLLightSymbols2Json();

        if (!s.isEmpty()) {
          Path symbolFile = output.resolve(s.trim()).toAbsolutePath();
          storeSymbols(symbolTable, symbolFile, s2j);
        }

        //print (formatted!) symboltable to console
        de.monticore.symboltable.serialization.JsonPrinter.enableIndentation();
        System.out.println(s2j.serialize(symbolTable));
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


  /*=================================================================*/
  /* Part 2: Executing arguments
  /*=================================================================*/

  /**
   * Parses the contents of a given file as XML(light).
   *
   * @param path The path to the XML(light)-file as String
   */
  @Override
  public ASTXMLDocument parse(String path) {
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
  @Override
  public void prettyPrint(ASTXMLDocument xmldoc, String file) {
    XMLLightPrettyPrinter pp = new XMLLightPrettyPrinter();
    String s = pp.printXMLDocument(xmldoc);
    print(s, file);
  }


  /**
   * stores the symbol table of a passed ast in a file created in the passed output directory.
   * The file path for the stored symbol table of an XML(light) "abc.BasicPhone.XML(light)" and the output
   * path "target" will be: "target/abc/BasicPhone.XML(light)sym"
   *
   * @return serialized String
   */

  public String storeSymbols(IXMLLightArtifactScope scope, Path out,
                             XMLLightSymbols2Json s2j) {
    Path f = out
        .resolve(Paths.get(Names.getPathFromPackage(scope.getPackageName())))
        .resolve(scope.getName() + ".xmlsym");
    String serialized = s2j.serialize(scope);
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
    XMLLightTraverser traverser = XMLLightMill.traverser();
    XMLLight2OD xml2od = new XMLLight2OD(printer, repository);
    traverser.add4XMLLight(xml2od);
    traverser.setXMLLightHandler(xml2od);

    // print object diagram
    String od = xml2od.printObjectDiagram((new File(modelName)).getName(), xmlDoc);
    print(od, file);
  }


  /*=================================================================*/
  /* Part 3: Defining the options incl. help-texts
  /*=================================================================*/

  /**
   * Initializes the Standard CLI options for the JSON tool.
   *
   * @return The CLI options with arguments.
   */

  @Override
  public Options addStandardOptions(Options options) {

    //help
    options.addOption(org.apache.commons.cli.Option.builder("h")
        .longOpt("help")
        .desc("Prints this help dialog")
        .build());

    //parse input file
    options.addOption(org.apache.commons.cli.Option.builder("i")
        .longOpt("input")
        .argName("file")
        .hasArg()
        .desc("Reads the source file (mandatory) and parses the contents as XML (light)")
        .build());

    //pretty print JSON
    options.addOption(org.apache.commons.cli.Option.builder("pp")
        .longOpt("prettyprint")
        .argName("file")
        .optionalArg(true)
        .numberOfArgs(1)
        .desc("Prints the XML(light)-AST to stdout or the specified file (optional)")
        .build());

    // pretty print SC
    options.addOption(Option.builder("s")
        .longOpt("symboltable")
        .desc("Serializes and prints the symbol table to stdout, if present, the specified output file")
        .optionalArg(true)
        .numberOfArgs(1)
        .argName("file")
        .build());

    // set path for imported symbols
    options.addOption(Option.builder("path")
        .desc("Sets the artifact path for imported symbols")
        .argName("dirlist")
        .hasArg()
        .hasArgs()
        .valueSeparator(' ')
        .build());

    return options;

  }

  /**
   * Initializes the Additional CLI options for the JSON tool.
   *
   * @return The CLI options with arguments.
   */
  @Override
  public Options addAdditionalOptions(Options options) {

    // print object diagram
    options.addOption(Option.builder("so")
        .longOpt("syntaxobjects")
        .argName("file")
        .optionalArg(true)
        .numberOfArgs(1)
        .desc("Prints an object diagram of the XML(light)-AST to stdout or the specified file (optional)")
        .build());
    return options;

  }

}
