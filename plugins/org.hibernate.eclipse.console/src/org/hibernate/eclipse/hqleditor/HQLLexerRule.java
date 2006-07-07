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

import java.io.IOException;
import java.io.Reader;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.hibernate.hql.antlr.HqlBaseLexer;
import org.hibernate.hql.antlr.HqlSqlTokenTypes;

import antlr.Token;
import antlr.TokenStreamException;

public class HQLLexerRule implements IRule {

	private final IToken hqlToken;

	public HQLLexerRule(IToken hqlToken) {
		this.hqlToken = hqlToken;
	}

	public IToken evaluate(final ICharacterScanner scanner) {
		HqlBaseLexer lexer = new HqlBaseLexer(new Reader() {

			public void close() throws IOException {
				// noop				
			}

			public int read(char[] cbuf, int off, int len) throws IOException {
				int pos = 0;
				int lastRead = ICharacterScanner.EOF;
				while(pos<off && (lastRead=scanner.read())!=ICharacterScanner.EOF) {
					pos++;
				}
				
				while(pos-off<len && (lastRead=scanner.read())!=ICharacterScanner.EOF) {
					cbuf[pos-off] = (char) lastRead;
					pos++;
				}
				
				if(lastRead==ICharacterScanner.EOF) {
					return -1;
				} else {
					return len;
				}				
			}
			
		});
		try {
			Token token = lexer.nextToken();
			if(token.getType()==HqlSqlTokenTypes.IDENT) {
				return hqlToken;
			} 
		}
		catch (TokenStreamException e) {
			// undefined
		}
		
		return org.eclipse.jface.text.rules.Token.UNDEFINED;		
	}

}
