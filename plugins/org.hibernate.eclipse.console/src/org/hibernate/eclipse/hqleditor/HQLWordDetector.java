package org.hibernate.eclipse.hqleditor;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * This class determines whether a given character is valid as part of an HQL keyword 
 * in the current context.
 */
public class HQLWordDetector implements IWordDetector {

    public boolean isWordStart( char c ) {
        String[] reservedWords = HQLCodeScanner.getHQLKeywords();
        for (int i = 0; i < reservedWords.length; i++) {
            if ( (reservedWords[i].charAt(0) == c)
              || (reservedWords[i].toUpperCase().charAt(0) == c) ) {
                return true;
            }
        }   
        
        return false;
    }

    public boolean isWordPart( char c ) {
        String[] keywords = HQLCodeScanner.getHQLKeywords();
        for (int i = 0; i < keywords.length; i++) {
            if ( (keywords[i].indexOf(c) != -1)
              || (keywords[i].toUpperCase().indexOf(c) != -1) ) {
                return true;
            }
        }

        return false;
    }

} 
