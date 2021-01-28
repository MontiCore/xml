<!-- (c) https://github.com/MontiCore/monticore -->

# XML Language Description

* The MontiCore language XML contains the grammar 
  and symbol management infrastructure for parsing and processing 
  XML artifacts

```xml
<Calendar>
  <Appointment name="meeting">
    <Date>24.04.2020</Date>
    <Time>14:00</Time>
    <Location>zoom</Location>
  </Appointment>
  <Appointment name="lunch">
    <Date>24.04.2020</Date>
    <Time>11:30</Time>
    <Location>cafeteria</Location>
  </Appointment>
</Calendar>
```
* The main purpose of this language is parsing general artifacts in XML format
  that adhere to the common [standard](https://www.w3.org/TR/2008/REC-xml-20081126/).
* The XML grammars enables parsing arbitrary XML artifacts for further 
  processing.
* ```XMLBasis``` provides general Tokens 
* ```XMLLight``` represents the overall AST structure of the XML language 
  while simultaneously being compliant to MontiCore's provided literals.
* ```FullXML``` allows processing arbitrary XML-compliant artifacts,
  including more sophistitated header information and parsing plain text
  passages included into the document. Further well-formedness checks are
  not included, because we assume to parse correctly produced XML
  documents only.
* Please note that XML (like JSON or ASCII) is just a carrier language.
  The conrete XML dialect and the question, how to recreate the
  real objects / data structures, etc. behind the XML structure is beyond
  this grammar, but can be applied to the AST defined in ```XMLLight```.

* XMLBasis grammar [`de.monticore.lang.XMLBasis.mc4`](src/main/grammars/de/monticore/lang/XMLBasis.mc4).
* XMLLight grammar [`de.monticore.lang.XMLLight.mc4`](src/main/grammars/de/monticore/lang/XMLLight.mc4).
* FullXML grammar [`de.monticore.lang.FullXML.mc4`](src/main/grammars/de/monticore/lang/FullXML.mc4).

## Symboltable
* The XML artifacts do not provide any symbols.

### Symbol kinds used by XML (importable):
* None, because XML does not have mechanisms to refer to external symbols.

### Symbol kinds defined by XML:
* Symbol kinds are currently explored.

### Symbols exported by XML:
* XML documents generally do NOT export any symbols to external artifacts. 
  This has two reasons:
  * Usually XML dialect encode their information in various specific forms.
    A default symbol table would therefore not be useful.
  * XML is mainly a transport technique for data, e.g., during runtime of
    products, services, but also tools and simulators. XML artefacts are
    meant for reading and processing, not usually for referring to their
    internal information by other artifacts.
* Thus there is no symbol-table to be stored.  

## Functionality: CoCos
* none provided; it is assumed that the XML model was produced correctly.

## Handwritten Extensions
* [XMLCLI](./src/main/java/de/monticore/XMLLightCLI.java)
  A command line interface for the XML language.
* [XMLPrettyPrinter](./src/main/java/de/monticore/lang/xml/prettyprint/XMLLightPrettyPrinter.java)
  A pretty-printer for serialzing XML-ASTs into XML-compliant artifacts.

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/dev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

