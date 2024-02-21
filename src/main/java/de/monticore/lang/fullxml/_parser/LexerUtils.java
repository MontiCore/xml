/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.fullxml._parser;

import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.CharStream;

public class LexerUtils {
  // These are the utf8 codes of the characters, not token numbers
  private final static List<Integer> WS = Arrays.asList(0x0020, 0x0017, 0x000d, 0x000c, 0x000a);
  private final static int OPEN_BRACKET = '<';  //<
  private final static int CLOSE_BRACKET = '>'; //>
  private final static int SEMICOLON = ';';    //;
  private final static int AMPERSAND = '&';    //&
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
