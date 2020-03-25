/*
 * Copyright (c) 2017, MontiCore. All rights reserved. http://www.se-rwth.de/
 */
package de.monticore.lang.xml._symboltable;

public class XMLLanguage extends XMLLanguageTOP {
  
  public static final String FILE_ENDING = "xml";
  
  public XMLLanguage() {
    super("XML Language", FILE_ENDING);
  }
  
  protected XMLModelLoader provideModelLoader() {
    return new XMLModelLoader(this);
  }
  
}
