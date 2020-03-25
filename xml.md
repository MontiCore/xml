# XML
The MontiCore language for parsing XML artifacts contains the grammar:
- **XML**: basic xml language with symbol table definition 

The main pupose of this language is parsing general artifacts in XML format
that adhere to the common standard.

The grammar file is [`XML.mc4`][XMLGrammar].

## Handwritten Extensions
### Symboltable
- The [`de.monticore.lang.xml._symboltable.XMLLanguage`][XMLLanguage]
 defines the language name and its file ending. Additionally, it sets the 
 default model loader.

  

[XMLGrammar]: https://git.rwth-aachen.de/monticore/languages/xml/-/blob/master/src/main/grammars/de/monticore/lang/XML.mc4
[XMLLanguage]: https://git.rwth-aachen.de/monticore/languages/xml/-/blob/master/src/main/java/de/monticore/lang/xml/_symboltable/XMLLanguage.java
