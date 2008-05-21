/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.internal.core.properties;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.swt.graphics.RGB;

/**
 * @author Konstantin Mishin
 * 
 */
public class QLScanner extends RuleBasedScanner {
//	akuzmin 14.09.2005
	private String separators;
	public QLScanner(ColorManager manager,String[] words,String separators) {
		this.separators=separators;
		IToken procInstr1 =
			new Token(
				new TextAttribute(
					manager.getColor(new RGB(0, 0, 255))));
		IToken procInstr2 =
			new Token(
				new TextAttribute(
					manager.getColor(new RGB(128, 0, 0))));
		IToken procInstr3 =
			new Token(
				new TextAttribute(
					manager.getColor(new RGB(0, 128, 0))));
		IRule[] rules = new IRule[4]; 
		//Add a rule for single quotes.
		rules[0] = new SingleLineRule("\"", "\"", procInstr3);
		//Add a rule for double quotes.
		rules[1] = new SingleLineRule("'", "'", procInstr1);
		//Add a word rule
		QLWordRule wordRule = new QLWordRule(new QLWordDetector(),separators);
		for (int i=0;i<words.length;i++)
			wordRule.addWord(words[i], procInstr2);
		rules[2] = wordRule;
		//Add generic whitespace rule.
		rules[3] = new WhitespaceRule(new SQLWhitespaceDetector());
		setRules(rules);
	}
	
	public class SQLWhitespaceDetector implements IWhitespaceDetector {
		
		public boolean isWhitespace(char c) {
			return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
		}
	}

//akuzmin 14.09.2005	
	public class QLWordDetector implements IWordDetector {
		
		
	    public boolean isWordStart(char c) {
	    	return (separators.indexOf(c)==-1 && 'a'>=c && c<='z');
	   }
	    
	   public boolean isWordPart(char c) {
	        return (separators.indexOf(c)==-1 && 'a'>=c && c<='z');
	    }
	}	
}
