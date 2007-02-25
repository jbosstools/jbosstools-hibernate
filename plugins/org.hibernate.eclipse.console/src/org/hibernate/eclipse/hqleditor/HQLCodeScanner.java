/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
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
import org.hibernate.eclipse.hqleditor.preferences.HQLPreferenceConstants;

public class HQLCodeScanner extends RuleBasedScanner {

    /** Defines the HQL keywords. Based on hql.g antlr grammer in 2005 ;) */
    private static String[] hqlKeywords = {
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
    	"and",
    	"or",

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
    
   
    /** built-in function names. Various normal builtin functions in SQL/HQL. Maybe sShould try and do this dynamically based on dialect */
    private static String[] builtInFunctions = {
		    // standard sql92 functions
    		"substring",
    		"locate",
    		"trim",
    		"length",
    		"bit_length",
    		"coalesce",
    		"nullif",
    		"abs",
    		"mod",
    		"sqrt",
    		"upper",
    		"lower",
    		"cast",
    		"extract",
    		
    		// time functions mapped to ansi extract
    		"second",
    		"minute",
    		"hour",
    		"day",
    		"month",
    		"year",
    		
    		"str",
    		
    		//    		 misc functions - based on oracle dialect
    		"sign",
    		"acos",
    		"asin",
    		"atan",
    		"cos",
    		"cosh",
    		"exp",
    		"ln",
    		"sin",
    		"sinh",
    		"stddev",
    		"sqrt",
    		"tan",
    		"tanh",
    		"variance",
    		
    		"round",
    		"trunc",
    		"ceil",
    		"floor",
    		
    		"chr",
    		"initcap",
    		"lower",
    		"ltrim",
    		"rtrim",
    		"soundex",
    		"upper",
    		"ascii",
    		"length",
    		"to_char",
    		"to_date",
    		
    		"current_date",
    		"current_time",
    		"current_timestamp",
    		"lastday",
    		"sysday",
    		"systimestamp",
    		"uid",
    		"user",
    		
    		"rowid",
    		"rownum",
    		
    		"concat",
    		"instr",
    		"instrb",
    		"lpad",
    		"replace",
    		"rpad",
    		"substr",
    		"substrb",
    		"translate",
    		
    		"substring",
    		"locate",
    		"bit_length",
    		"coalesce",
    		
    		"atan2",
    		"log",
    		"mod",
    		"nvl",
    		"nvl2",
    		"power",
    		
    		"add_months",
    		"months_between",
    		"next_day",
    		
           };
    
    static {
    	// for performance in search.
    	Arrays.sort(builtInFunctions);
    	Arrays.sort(hqlKeywords);
    }
    
    static public class HQLWhiteSpaceDetector implements IWhitespaceDetector {

        public boolean isWhitespace( char c ) {
            return Character.isWhitespace( c );
        }

    } 
    
    public HQLCodeScanner( HQLColors colorProvider ) {
        final IToken commentToken    = new Token( new TextAttribute( colorProvider.getColor( HQLPreferenceConstants.HQL_COMMENT_COLOR )));
        final IToken stringToken     = new Token( new TextAttribute( colorProvider.getColor( HQLPreferenceConstants.HQL_QUOTED_LITERAL_COLOR )));
        final IToken keywordToken    = new Token( new TextAttribute( colorProvider.getColor( HQLPreferenceConstants.HQL_KEYWORD_COLOR ), null, SWT.BOLD)) {
        	public Object getData() {
        		// TODO Auto-generated method stub
        		return super.getData();
        	}
        };
        final IToken functionToken   = new Token( new TextAttribute( colorProvider.getColor( HQLPreferenceConstants.HQL_KEYWORD_COLOR )));
        final IToken otherToken      = new Token( new TextAttribute( colorProvider.getColor( HQLPreferenceConstants.HQL_DEFAULT_COLOR )));        
        
        setDefaultReturnToken( otherToken );

        List rules = new ArrayList();

        rules.add( new EndOfLineRule( "--", commentToken )); //$NON-NLS-1$
        rules.add( new SingleLineRule( "'", "'", stringToken, '\\' )); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        rules.add( new WhitespaceRule( new HQLWhiteSpaceDetector() ));

        //rules.add( new HQLLexerRule( hqlToken )); // TODO: need to categorize tokens into types.
        WordRule wordRule = new WordRule( new HQLWordDetector(), otherToken );
        addWordRules( keywordToken, wordRule, getHQLKeywords() );
        addWordRules( functionToken, wordRule, getHQLFunctionNames() );
        
        rules.add( wordRule );

        setRules( (IRule[]) rules.toArray( new IRule[ rules.size() ] ) );
    }

	private void addWordRules(final IToken token, WordRule wordRule, String[] reservedWords) {
		for (int i = 0; i < reservedWords.length; i++) {
            wordRule.addWord( reservedWords[i], token );
            wordRule.addWord( reservedWords[i].toUpperCase(), token );
        }
	}

    public static String[] getHQLKeywords() {
        return hqlKeywords;
    }
    
    public static String[] getHQLFunctionNames() {
        return builtInFunctions;
    }
    
} // end class
