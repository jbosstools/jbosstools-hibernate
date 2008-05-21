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

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

/**
 * @author Konstantin Mishin
 * 
 */
public class QLWordRule extends WordRule {
	
	private StringBuffer fBuffer= new StringBuffer();
	private char x;
//	akuzmin 14.09.2005	
	private String qlseparators;
	public QLWordRule(IWordDetector detector,String separators) {
		super(detector);
		qlseparators=separators;
		x=qlseparators.charAt(0);
	}

	public QLWordRule(IWordDetector detector, IToken defaultToken) {
		super(detector, defaultToken);
	}
	
	public IToken evaluate(ICharacterScanner scanner) {
		int z=scanner.getColumn();
		char c = Character.toUpperCase((char) scanner.read());
		if (fDetector.isWordStart(c) && (qlseparators.indexOf(x)!=-1 || z < 1)) {
			if (fColumn == UNDEFINED || (fColumn == z - 1)) {
				
				fBuffer.setLength(0);
				do {
					fBuffer.append(c);
					x=c;
					c = Character.toUpperCase((char) scanner.read());
				} while (c != ICharacterScanner.EOF && fDetector.isWordPart(c));
				scanner.unread();
				IToken token= (IToken) fWords.get(fBuffer.toString());
				if (token != null)
					return token;

				if (fDefaultToken.isUndefined())
					unreadBuffer(scanner);

				return fDefaultToken;
			}
		}
		x=c;
		scanner.unread();
		return Token.UNDEFINED;
	}
	
	protected void unreadBuffer(ICharacterScanner scanner) {
		for (int i= fBuffer.length() - 1; i >= 0; i--)
			scanner.unread();
	}

}
