package org.hibernate.eclipse.hqleditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

/**
 * This class implements a RuleBaseScanner for HQL source code text.
 */
public class HQLCodeScanner extends RuleBasedScanner {

    /** Defines the HQL keywords. */
    private static String[] fgKeywords = {
    	"between", //$NON-NLS-1$
    	"class", //$NON-NLS-1$
    	"delete", //$NON-NLS-1$
    	"desc", //$NON-NLS-1$
    	"distinct", //$NON-NLS-1$
    	"elements", //$NON-NLS-1$
    	"escape", //$NON-NLS-1$
    	"exists", //$NON-NLS-1$
    	"false", //$NON-NLS-1$
    	"fetch", //$NON-NLS-1$
    	"from", //$NON-NLS-1$
    	"full", //$NON-NLS-1$
    	"group", //$NON-NLS-1$
    	"having", //$NON-NLS-1$
    	"in", //$NON-NLS-1$
    	"indices", //$NON-NLS-1$
    	"inner", //$NON-NLS-1$
    	"insert", //$NON-NLS-1$
    	"into", //$NON-NLS-1$
    	"is", //$NON-NLS-1$
    	"join", //$NON-NLS-1$
    	"left", //$NON-NLS-1$
    	"like", //$NON-NLS-1$
    	"new", //$NON-NLS-1$
    	"not", //$NON-NLS-1$
    	"null", //$NON-NLS-1$
    	"or", //$NON-NLS-1$
    	"order", //$NON-NLS-1$
    	"outer", //$NON-NLS-1$
    	"properties", //$NON-NLS-1$
    	"right", //$NON-NLS-1$
    	"select", //$NON-NLS-1$
    	"set", //$NON-NLS-1$
    	"some", //$NON-NLS-1$
    	"true", //$NON-NLS-1$
    	"union", //$NON-NLS-1$
    	"update", //$NON-NLS-1$
    	"versioned", //$NON-NLS-1$
    	"where", //$NON-NLS-1$

   		// -- SQL tokens --
   		// These aren't part of HQL, but usefull when you have SQL in the editor
    	"case", //$NON-NLS-1$
    	"end", //$NON-NLS-1$
    	"else", //$NON-NLS-1$
    	"then", //$NON-NLS-1$
    	"when", //$NON-NLS-1$
    	"on", //$NON-NLS-1$
    	"with", //$NON-NLS-1$

    	// -- EJBQL tokens --
    	"both", //$NON-NLS-1$
    	"empty", //$NON-NLS-1$
    	"leading", //$NON-NLS-1$
    	"member", //$NON-NLS-1$
    	"object", //$NON-NLS-1$
    	"of", //$NON-NLS-1$
    	"trailing", //$NON-NLS-1$
        };
    
   
    /** Defines the SQL built-in function names. */
    private static String[] fgFunctions = {
    	"log", //$NON-NLS-1$
    	"roundmagic", //$NON-NLS-1$
    	"rand", //$NON-NLS-1$
    	"sign", //$NON-NLS-1$
    	"char", //$NON-NLS-1$
    	"curdate", //$NON-NLS-1$
    	"radians", //$NON-NLS-1$
    	"mod", //$NON-NLS-1$
    	"pi", //$NON-NLS-1$
    	"coalesce", //$NON-NLS-1$
    	"ltrim", //$NON-NLS-1$
    	"month", //$NON-NLS-1$
    	"acos", //$NON-NLS-1$
    	"day", //$NON-NLS-1$
    	"current_time", //$NON-NLS-1$
    	"curtime", //$NON-NLS-1$
    	"hextoraw", //$NON-NLS-1$
    	"cast", //$NON-NLS-1$
    	"space", //$NON-NLS-1$
    	"second", //$NON-NLS-1$
    	"hour", //$NON-NLS-1$
    	"lcase", //$NON-NLS-1$
    	"avg", //$NON-NLS-1$
    	"monthname", //$NON-NLS-1$
    	"soundex", //$NON-NLS-1$
    	"extract", //$NON-NLS-1$
    	"rawtohex", //$NON-NLS-1$
    	"quater", //$NON-NLS-1$
    	"current_date", //$NON-NLS-1$
    	"abs", //$NON-NLS-1$
    	"trim", //$NON-NLS-1$
    	"bit_length", //$NON-NLS-1$
    	"nullif", //$NON-NLS-1$
    	"ucase", //$NON-NLS-1$
    	"cot", //$NON-NLS-1$
    	"sqrt", //$NON-NLS-1$
    	"max", //$NON-NLS-1$
    	"week", //$NON-NLS-1$
    	"now", //$NON-NLS-1$
    	"atan", //$NON-NLS-1$
    	"upper", //$NON-NLS-1$
    	"lower", //$NON-NLS-1$
    	"min", //$NON-NLS-1$
    	"concat", //$NON-NLS-1$
    	"ceiling", //$NON-NLS-1$
    	"asin", //$NON-NLS-1$
    	"degrees", //$NON-NLS-1$
    	"sum", //$NON-NLS-1$
    	"locate", //$NON-NLS-1$
    	"user", //$NON-NLS-1$
    	"sin", //$NON-NLS-1$
    	"tan", //$NON-NLS-1$
    	"substring", //$NON-NLS-1$
    	"length", //$NON-NLS-1$
    	"dayname", //$NON-NLS-1$
    	"floor", //$NON-NLS-1$
    	"dayofweek", //$NON-NLS-1$
    	"dayofyear", //$NON-NLS-1$
    	"str", //$NON-NLS-1$
    	"dayofmonth", //$NON-NLS-1$
    	"ascii", //$NON-NLS-1$
    	"reverse", //$NON-NLS-1$
    	"exp", //$NON-NLS-1$
    	"cos", //$NON-NLS-1$
    	"year", //$NON-NLS-1$
    	"current_timestamp", //$NON-NLS-1$
    	"log10", //$NON-NLS-1$
    	"minute", //$NON-NLS-1$
    	"rtrim", //$NON-NLS-1$
    	"count", //$NON-NLS-1$
    	"database", //$NON-NLS-1$
        };
    
    /** Defines an array of arrays that combines all the HQL language elements defined previously. */ 
    private static Object[] fgAllWords = { fgKeywords, fgFunctions };
    
    static {
    	Arrays.sort(fgFunctions);
    	Arrays.sort(fgKeywords);
    }
    
    /**
     * This class determines if a character is a whitespace character.
     */
    public class HQLWhiteSpaceDetector implements IWhitespaceDetector {

        public boolean isWhitespace( char c ) {
            return Character.isWhitespace( c );
        }

    } // end inner class
    
    /**
     * Constructs an instance of this class using the given color provider.
     */
    public HQLCodeScanner( HQLColorProvider colorProvider ) {
        // Define tokens for the SQL language elements.
        IToken commentToken    = null;
        IToken stringToken     = null;
        IToken keywordToken    = null;
        IToken functionToken   = null;
//        IToken identifierToken = null;
        IToken delimitedIdentifierToken = null;
        IToken otherToken      = null;
        
        /* On "high contrast" displays the default text color (black) is a problem,
         * since the normal high contrast background is black.  ("High contrast" is
         * a Windows feature that helps vision-impaired people.)  When high contrast mode is enabled,
         * use different colors that show up better against a black background.
         */
        if (Display.getDefault().getHighContrast() == true) {
            commentToken    = new Token( new TextAttribute( colorProvider.getColor( HQLColorProvider.HQL_HC_COMMENT_COLOR )));
            stringToken     = new Token( new TextAttribute( colorProvider.getColor( HQLColorProvider.HQL_HC_QUOTED_LITERAL_COLOR )));
            keywordToken    = new Token( new TextAttribute( colorProvider.getColor( HQLColorProvider.HQL_HC_KEYWORD_COLOR )));
            functionToken   = new Token( new TextAttribute( colorProvider.getColor( HQLColorProvider.HQL_HC_KEYWORD_COLOR )));
            delimitedIdentifierToken = new Token( new TextAttribute( colorProvider.getColor( HQLColorProvider.HQL_HC_DELIMITED_IDENTIFIER_COLOR )));
            otherToken      = new Token( new TextAttribute( colorProvider.getColor( HQLColorProvider.HQL_HC_DEFAULT_COLOR )));
        }
        else {
            commentToken    = new Token( new TextAttribute( colorProvider.getColor( HQLColorProvider.HQL_COMMENT_COLOR )));
            stringToken     = new Token( new TextAttribute( colorProvider.getColor( HQLColorProvider.HQL_QUOTED_LITERAL_COLOR )));
            keywordToken    = new Token( new TextAttribute( colorProvider.getColor( HQLColorProvider.HQL_KEYWORD_COLOR ), null, SWT.BOLD));
            functionToken   = new Token( new TextAttribute( colorProvider.getColor( HQLColorProvider.HQL_KEYWORD_COLOR )));
            delimitedIdentifierToken = new Token( new TextAttribute( colorProvider.getColor( HQLColorProvider.HQL_DELIMITED_IDENTIFIER_COLOR )));
            otherToken      = new Token( new TextAttribute( colorProvider.getColor( HQLColorProvider.HQL_DEFAULT_COLOR )));
        }

        setDefaultReturnToken( otherToken );

        List rules = new ArrayList();

        // Add rule for single-line comments.
        rules.add( new EndOfLineRule( "--", commentToken )); //$NON-NLS-1$

        // Add rules for delimited identifiers and string literals.
        rules.add( new SingleLineRule( "'", "'", stringToken, '\\' )); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        rules.add( new SingleLineRule( "\"", "\"", delimitedIdentifierToken, '\\' )); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        // Add generic whitespace rule.
        rules.add( new WhitespaceRule( new HQLWhiteSpaceDetector() ));

        // Define a word rule and add SQL keywords to it.
        WordRule wordRule = new WordRule( new HQLWordDetector(), otherToken );
        String[] reservedWords = getHQLKeywords();
        for (int i = 0; i < reservedWords.length; i++) {
            wordRule.addWord( reservedWords[i], keywordToken );
            wordRule.addWord( reservedWords[i].toLowerCase(), keywordToken );
        }
        
        // Add the SQL function names to the word rule.
        String[] functions = getHQLFunctionNames();
        for (int i = 0; i< functions.length; i++) {
            wordRule.addWord( functions[i], functionToken );
            wordRule.addWord( functions[i].toLowerCase(), functionToken );
        }
        
        // Add the word rule to the list of rules.
        rules.add( wordRule );

        // Convert the list of rules to an array and return it.
        IRule[] result = new IRule[ rules.size() ];
        rules.toArray( result );
        setRules( result );
    }

    /**
     * Gets an array of HQL keywords
     * 
     * @return the HQL keywords
     */
    public static String[] getHQLKeywords() {
        return fgKeywords;
    }
    
    /**
     * Gets an array of HQL function names 
     * 
     * @return the HQL function names
     */
    public static String[] getHQLFunctionNames() {
        return fgFunctions;
    }
    
    /**
     * Gets an array of arrays containing all HQL words, including keywords
     * and function names
     * 
     * @return the array of SQL words
     */
    public static Object[] getSQLAllWords() {
        return fgAllWords;
    }   
    
} // end class
