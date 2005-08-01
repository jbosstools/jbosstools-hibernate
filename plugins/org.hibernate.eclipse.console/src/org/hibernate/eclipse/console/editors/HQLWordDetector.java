package org.hibernate.eclipse.console.editors;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * This class determines whether a given character is valid as part of an SQL keyword 
 * in the current context.
 */
public class HQLWordDetector implements IWordDetector {

    /**
     * Returns whether the specified character is valid as the first 
     * character in a word.
     * @see org.eclipse.jface.text.rules.IWordDetector#isWordStart(char)
     */
    public boolean isWordStart( char c ) {
        String[] reservedWords = HQLCodeScanner.getHQLKeywords();
        for (int i = 0; i < reservedWords.length; i++) {
            if ( (reservedWords[i].charAt(0) == c)
              || (reservedWords[i].toLowerCase().charAt(0) == c) ) {
                return true;
            }
        }   

        String[] datatypes = HQLCodeScanner.getHQLDatatypes();
        for (int i = 0; i < datatypes.length; i++) {
            if ( (datatypes[i].charAt(0) == c)
              || (datatypes[i].toLowerCase().charAt(0) == c) ) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns whether the specified character is valid as a subsequent character 
     * in a word.
     * @see org.eclipse.jface.text.rules.IWordDetector#isWordPart(char)
     */
    public boolean isWordPart( char c ) {
        String[] keywords = HQLCodeScanner.getHQLKeywords();
        for (int i = 0; i < keywords.length; i++) {
            if ( (keywords[i].indexOf(c) != -1)
              || (keywords[i].toLowerCase().indexOf(c) != -1) ) {
                return true;
            }
        }

        String[] datatypes = HQLCodeScanner.getHQLDatatypes();
        for (int i = 0; i < datatypes.length; i++) {
            if ( (datatypes[i].indexOf(c) != -1)
              || (datatypes[i].toLowerCase().indexOf(c) != -1) ) {
                return true;
            }
        }

        return false;
    }

} // end class
