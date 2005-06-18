package org.hibernate.eclipse.console.editors;

import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public class HQLScanner extends RuleBasedScanner {

	public HQLScanner(ColorManager colorManager) {
        WordRule rule = new WordRule(new IWordDetector() {
           public boolean isWordStart(char c) { 
        	   return Character.isJavaIdentifierStart(c); 
           }
           public boolean isWordPart(char c) {   
           	return Character.isJavaIdentifierPart(c); 
           }
        });
        Token keyword = new Token(new TextAttribute(
        		colorManager.getColor(new RGB(238,238,255) ), null, SWT.BOLD) );
        Token comment = new Token(
        		colorManager.getColor(new RGB(200,200,200) ) );
        Token string = new Token(
        		colorManager.getColor(new RGB(255,255,255) ) );
        //add tokens for each reserved word
        String[] KEYWORDS = new String[] {
        		"from",
				"where",
				"as",
        };
        
        for (int n = 0; n < KEYWORDS.length; n++)
           rule.addWord(KEYWORDS[n], keyword);   
        setRules(new IRule[] {
           rule,
           //new SingleLineRule("#", null, comment),
           new SingleLineRule("\"", "\"", string, '\\'),
           new SingleLineRule("'", "'", string, '\\'),
           new WhitespaceRule(new IWhitespaceDetector() {
              public boolean isWhitespace(char c) {
                 return Character.isWhitespace(c);
              }
           }),
        });
     }

}
