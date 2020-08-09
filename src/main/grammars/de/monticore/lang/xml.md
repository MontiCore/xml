<!-- (c) https://github.com/MontiCore/monticore -->
# XML
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
  that adhere to the common standard.
* The XML grammar enables parsing arbitrary XML artifacts for further 
  processing.
* Actually the grammar represents a slight superset to the official XML 
  standard. It is intended for parsing XML-compliant artifacts. Further 
  well-formedness checks are not included, because we assume to parse correctly 
  produced XML documents only.
* Please note that XML (like JSON or ASCII) is just a carrier language.
  The conrete XML dialect and the question, how to recreate the
  real objects / data structures, etc. behind the XML structure is beyond this 
  grammar, but can be applied to the AST defined here.

* Main grammar [`de.monticore.lang.XML.mc4`](src/main/grammars/de/monticore/lang/XML.mc4).

## Symboltable
* The XML artifacts provide symbols of different, yet to be explored kinds. 
* Symbol management:
  * XML artifacts provide a hierarchy of scopes along the objects they define.
  * Each *"attribute name"* (i.e., each property key) acts as a symbol.
  * Symbols are by definition *externally visible* and *exported*. 
    All of them, even deeply nested ones!

### Symbol kinds used by XML (importable):
* None, because XML does not have mechanisms to refer to external symbols.

### Symbol kinds defined by XML:
* Symbol kinds are currently explored.

### Symbols exported by XML:
* XML documents generally do NOT export any symbols to external artifacts.
    Thus there is no symbol-table to be stored 
* XML Symbols are available only when the model is loaded.

## Functionality: CoCos
* none provided; it is assumed that the XML model was produced correctly.

## Further Information

* [XML grammar](src/main/grammars/de/monticore/lang/XML.mc4)  

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)

* [**List of languages**](https://git.rwth-aachen.de/monticore/monticore/-/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://git.rwth-aachen.de/monticore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)

* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

