<!-- (c) https://github.com/MontiCore/monticore -->
# XML


The MontiCore language XML defines the parsing and processing infrastructure 
for XML artifacts.
The language component (and full language) is part of the MontiCore language 
library.

Please note that XML (like JSON or ASCII) is just a carrier language.
The concrete XML dialect and the question, how to recreate the
real objects / data structures, etc. behind the XML structure
is beyond this grammar but can be applied to the AST defined in ```XMLLight```.

* XMLBasis [`de.monticore.lang.XMLBasis.mc4`](src/main/grammars/de/monticore/lang/XMLBasis.mc4).
* XMLLight [`de.monticore.lang.XMLLight.mc4`](src/main/grammars/de/monticore/lang/XMLLight.mc4).


## Functionality

### Parsing XML artifacts and pretty printing.
* available ([see language explanation](src/main/grammars/de/monticore/lang/xml.md))
  

## Further Links

* [XML grammars](src/main/grammars/de/monticore/lang/)

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/dev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

