/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.fullxml._parser;

import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.CharStream;

public class LexerUtils {
  
  private final static List<Integer> WS = Arrays.asList(32, 23, 13, 12, 10);
  private final static int OPEN_BRACKET = 60;  //<
  private final static int CLOSE_BRACKET = 62; //>
  private final static int SEMICOLON = 59;    //;
  private final static int AMPERSAND = 38;    //&
  private final static int EOF = -1;

  
  public static boolean isCharacterData(CharStream cs) {
    // look ahead for < or end of file
    int j = 1;
    //skip all leading Whitespace
    while (WS.contains(cs.LA(j))) {
      j++;
    }

    //if only Whitespace is between the non character data, then there is no need for a character data token
    if (cs.LA(j) == EOF || cs.LA(j) == OPEN_BRACKET || cs.LA(j) == AMPERSAND) {
      return false;
    }

    // trace back for > or ;
    for (int i = -1; i + cs.index() >= 0; i--) {
      // continue on any whitespace
      if (WS.contains(cs.LA(i))) {
        continue;
      }

      // true if > or ; is detected
      return CLOSE_BRACKET == cs.LA(i) || SEMICOLON == cs.LA(i);

      // false if any other character is detected
    }
    return false;

  }
  
}
