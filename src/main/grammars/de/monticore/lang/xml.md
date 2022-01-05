<!-- (c) https://github.com/MontiCore/monticore -->

<!-- This is a MontiCore stable explanation. -->

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
* The XML grammars enable parsing arbitrary XML artifacts 
  into their abstract syntax for further processing.
  * ```XMLBasis``` [`de.monticore.lang.XMLBasis.mc4`](src/main/grammars/de/monticore/lang/XMLBasis.mc4)
    provides general tokens required for parsing the XML language.
  * ```XMLLight``` [`de.monticore.lang.XMLLight.mc4`](src/main/grammars/de/monticore/lang/XMLLight.mc4)
    represents the overall AST structure of the XML language while
    simultaneously being compliant to the literals provided by 
    MontiCore's literal grammars.
    `XMLLight` does not cover all of XML, but a useful subset.
  * ```FullXML``` [`de.monticore.lang.FullXML.mc4`](src/main/grammars/de/monticore/lang/FullXML.mc4)
    allows processing arbitrary XML-compliant artifacts, including more
    sophistitated header information and parsing plain text passages included
    into the document. 
* Please note: (1) Further wellformedness checks (beyond syntactic
    correctness) are not included,
    because we assume to parse correctly produced XML documents only.
    This approach is based on the assumption that XML documents mainly
    serve as data transportation format and thus have been produced by 
    automatic tools that know what they are doing.
* Please note that XML (like JSON, ASCII, and Unicode) 
  is just a carrier language.
  The conrete XML dialect and the question, how to re-create the
  real objects / data structures, etc. behind the XML structure is beyond
  this grammar. 
  This needs to be applied manualy, but can be applied to the 
  AST already defined in ```XMLLight``` language subset if desired.

## Symboltable
* The XML artifacts do not provide any symbols, because the notion of 
  names/symbols/identifiers is highly specific to the respectife XML dialect.
  The XML standard itself does not embody the notion of symbol at all.

### Symbol kinds used by XML (importable):
* None, because XML does not have standardized 
  mechanisms to refer to external symbols.

### Symbols exported by XML:
* XML documents generally do NOT export any symbols to external artifacts. 
  This has two reasons:
  * Usually XML dialects encode their information in various specific forms.
    A default symbol table would therefore not be useful.
  * XML is mainly a transport technique for data, e.g., during runtime of
    products, services, but also tools and simulators. XML artefacts are
    meant for reading and processing, not usually for referring to their
    internal information by other artifacts.
* Thus there is generally no symboltable to be stored.  

## Functionality: CoCos
* none provided; it is assumed that the XML model is correct, because it is
  typically produced by another program.

## Handwritten Extensions
* [XMLTool](./src/main/java/de/monticore/XMLLightTool.java)
  A command line tool for the XML language.
* [XMLPrettyPrinter](./src/main/java/de/monticore/lang/xmllight/prettyprint/XMLLightPrettyPrinter.java)
  A pretty-printer for serialzing XML-ASTs into XML-compliant artifacts.

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/dev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

