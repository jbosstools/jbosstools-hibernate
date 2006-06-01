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
				int lastRead = scanner.EOF;
				while(pos<off && (lastRead=scanner.read())!=scanner.EOF) {
					pos++;
				}
				
				while(pos-off<len && (lastRead=scanner.read())!=scanner.EOF) {
					cbuf[pos-off] = (char) lastRead;
					pos++;
				}
				
				if(lastRead==scanner.EOF) {
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
