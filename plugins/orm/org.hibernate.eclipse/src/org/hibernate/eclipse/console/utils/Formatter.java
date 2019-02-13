package org.hibernate.eclipse.console.utils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.StringTokenizer;

import org.hibernate.util.xpl.StringHelper;

/**
 *  Old formatter from previous Hibernate versions which allowed a bit more control, i.e. we want *less* spaces not more.
 *  In here to simplify migration from 3.2 to 3.3 where this class changed.
 *   
 */
public class Formatter {
	
	private static final Set<String> BEGIN_CLAUSES = new HashSet<String>();
	private static final Set<String> END_CLAUSES = new HashSet<String>();
	private static final Set<String> LOGICAL = new HashSet<String>();
	private static final Set<String> QUANTIFIERS = new HashSet<String>();
	private static final Set<String> DML = new HashSet<String>();
	private static final Set<String> MISC = new HashSet<String>();
	static {
		
		BEGIN_CLAUSES.add("left"); //$NON-NLS-1$
		BEGIN_CLAUSES.add("right"); //$NON-NLS-1$
		BEGIN_CLAUSES.add("inner"); //$NON-NLS-1$
		BEGIN_CLAUSES.add("outer"); //$NON-NLS-1$
		BEGIN_CLAUSES.add("group"); //$NON-NLS-1$
		BEGIN_CLAUSES.add("order"); //$NON-NLS-1$

		END_CLAUSES.add("where"); //$NON-NLS-1$
		END_CLAUSES.add("set"); //$NON-NLS-1$
		END_CLAUSES.add("having"); //$NON-NLS-1$
		END_CLAUSES.add("join"); //$NON-NLS-1$
		END_CLAUSES.add("from"); //$NON-NLS-1$
		END_CLAUSES.add("by"); //$NON-NLS-1$
		END_CLAUSES.add("join"); //$NON-NLS-1$
		END_CLAUSES.add("into"); //$NON-NLS-1$
		END_CLAUSES.add("union"); //$NON-NLS-1$
		
		LOGICAL.add("and"); //$NON-NLS-1$
		LOGICAL.add("or"); //$NON-NLS-1$
		LOGICAL.add("when"); //$NON-NLS-1$
		LOGICAL.add("else"); //$NON-NLS-1$
		LOGICAL.add("end"); //$NON-NLS-1$
		
		QUANTIFIERS.add("in"); //$NON-NLS-1$
		QUANTIFIERS.add("all"); //$NON-NLS-1$
		QUANTIFIERS.add("exists"); //$NON-NLS-1$
		QUANTIFIERS.add("some"); //$NON-NLS-1$
		QUANTIFIERS.add("any"); //$NON-NLS-1$
		
		DML.add("insert"); //$NON-NLS-1$
		DML.add("update"); //$NON-NLS-1$
		DML.add("delete"); //$NON-NLS-1$
		
		MISC.add("select"); //$NON-NLS-1$
		MISC.add("on"); //$NON-NLS-1$
		//MISC.add("values");

	}
	
	String indentString = "    "; //$NON-NLS-1$
	String initial = "\n    "; //$NON-NLS-1$

	boolean beginLine = true;
	boolean afterBeginBeforeEnd = false;
	boolean afterByOrSetOrFromOrSelect = false;
	boolean afterValues = false;
	boolean afterOn = false;
	boolean afterBetween = false;
	boolean afterInsert = false;
	int inFunction = 0;
	int parensSinceSelect = 0;
	private LinkedList<Integer> parenCounts = new LinkedList<Integer>();
	private LinkedList<Boolean> afterByOrFromOrSelects = new LinkedList<Boolean>();

	int indent = 1;

	StringBuffer result = new StringBuffer();
	StringTokenizer tokens;
	String lastToken;
	String token;
	String lcToken;
	
	public Formatter(String sql) {
		tokens = new StringTokenizer(
				sql, 
				"()+*/-=<>'`\"[]," + StringHelper.WHITESPACE,  //$NON-NLS-1$
				true
			);
	}

	public Formatter setInitialString(String initial) {
		this.initial = initial;
		return this;
	}
	
	public Formatter setIndentString(String indent) {
		this.indentString = indent;
		return this;
	}
	
	public String format() {
		
		result.append(initial);
		
		while ( tokens.hasMoreTokens() ) {
			token = tokens.nextToken();
			lcToken = token.toLowerCase();
			
			if ( "'".equals(token) ) { //$NON-NLS-1$
				String t;
				do {
					t = tokens.nextToken();
					token += t;
				} 
				while ( !"'".equals(t) && tokens.hasMoreTokens() ); // cannot handle single quotes //$NON-NLS-1$
			}		
			else if ( "\"".equals(token) ) { //$NON-NLS-1$
				String t;
				do {
					t = tokens.nextToken();
					token += t;
				} 
				while ( !"\"".equals(t) ); //$NON-NLS-1$
			}
			
			if ( afterByOrSetOrFromOrSelect && ",".equals(token) ) { //$NON-NLS-1$
				commaAfterByOrFromOrSelect();
			}
			else if ( afterOn && ",".equals(token) ) { //$NON-NLS-1$
				commaAfterOn();
			}
			
			else if ( "(".equals(token) ) { //$NON-NLS-1$
				openParen();
			}
			else if ( ")".equals(token) ) { //$NON-NLS-1$
				closeParen();
			}

			else if ( BEGIN_CLAUSES.contains(lcToken) ) {
				beginNewClause();
			}

			else if ( END_CLAUSES.contains(lcToken) ) {
				endNewClause(); 
			}
			
			else if ( "select".equals(lcToken) ) { //$NON-NLS-1$
				select();
			}
			
			else if ( DML.contains(lcToken) ) {
				updateOrInsertOrDelete();
			}
			
			else if ( "values".equals(lcToken) ) { //$NON-NLS-1$
				values();
			}
			
			else if ( "on".equals(lcToken) ) { //$NON-NLS-1$
				on();
			}
			
			else if ( afterBetween && lcToken.equals("and") ) { //$NON-NLS-1$
				misc();
				afterBetween = false;
			}
			
			else if ( LOGICAL.contains(lcToken) ) {
				logical();
			}
			
			else if ( isWhitespace(token) ) {
				white();
			}
			
			else {
				misc();
			}
			
			if ( !isWhitespace( token ) ) lastToken = lcToken;
			
		}
		return result.toString();
	}

	private void commaAfterOn() {
		out();
		indent--;
		newline();
		afterOn = false;
		afterByOrSetOrFromOrSelect = true;
	}

	private void commaAfterByOrFromOrSelect() {
		out();
		newline();
	}

	private void logical() {
		if ( "end".equals(lcToken) ) indent--; //$NON-NLS-1$
		newline();
		out();
		beginLine = false;
	}

	private void on() {
		indent++;
		afterOn = true;
		newline();
		out();
		beginLine = false;
	}

	private void misc() {
		out();
		if ( "between".equals(lcToken) ) { //$NON-NLS-1$
			afterBetween = true;
		}
		if (afterInsert) {
			newline();
			afterInsert = false;
		}
		else {
			beginLine = false;
			if ( "case".equals(lcToken) ) { //$NON-NLS-1$
				indent++;
			}
		}
	}

	private void white() {
		if ( !beginLine ) {
			result.append(" "); //$NON-NLS-1$
		}
	}
	
	private void updateOrInsertOrDelete() {
		out();
		indent++;
		beginLine = false;
		if ( "update".equals(lcToken) ) newline(); //$NON-NLS-1$
		if ( "insert".equals(lcToken) ) afterInsert = true; //$NON-NLS-1$
	}

	private void select() {
		out();
		indent++;
		newline();
		parenCounts.addLast( new Integer(parensSinceSelect) );
		afterByOrFromOrSelects.addLast( new Boolean(afterByOrSetOrFromOrSelect) );
		parensSinceSelect = 0;
		afterByOrSetOrFromOrSelect = true;
	}

	private void out() {
		result.append(token);
	}

	private void endNewClause() {
		if (!afterBeginBeforeEnd) {
			indent--;
			if (afterOn) {
				indent--;
				afterOn=false;
			}
			newline();
		}
		out();
		if ( !"union".equals(lcToken) ) indent++; //$NON-NLS-1$
		newline();
		afterBeginBeforeEnd = false;
		afterByOrSetOrFromOrSelect = "by".equals(lcToken)  //$NON-NLS-1$
				|| "set".equals(lcToken) //$NON-NLS-1$
				|| "from".equals(lcToken); //$NON-NLS-1$
	}

	private void beginNewClause() {
		if (!afterBeginBeforeEnd) {
			if (afterOn) {
				indent--;
				afterOn=false;
			}
			indent--;
			newline();
		}
		out();
		beginLine = false;
		afterBeginBeforeEnd = true;
	}

	private void values() {
		indent--;
		newline();
		out();
		indent++;
		newline();
		afterValues = true;
	}

	private void closeParen() {
		parensSinceSelect--;
		if (parensSinceSelect<0) {
			indent--;
			parensSinceSelect = ( (Integer) parenCounts.removeLast() ).intValue();
			afterByOrSetOrFromOrSelect = ( (Boolean) afterByOrFromOrSelects.removeLast() ).booleanValue();
		}
		if ( inFunction>0 ) {
			inFunction--;
			out();
		}
		else {
			if (!afterByOrSetOrFromOrSelect) {
				indent--;
				newline();
			}
			out();
		}
		beginLine = false;
	}

	private void openParen() {
		if ( isFunctionName( lastToken ) || inFunction>0 ) {
			inFunction++;
		}
		beginLine = false;
		if ( inFunction>0 ) {
			out();
		}
		else {
			out();
			if (!afterByOrSetOrFromOrSelect) {
				indent++;
				newline();
				beginLine = true;
			}
		}
		parensSinceSelect++;
	}

	private static boolean isFunctionName(String tok) {
		final char begin = tok.charAt(0);
		final boolean isIdentifier = Character.isJavaIdentifierStart( begin ) || '"'==begin;
		return isIdentifier && 
				!LOGICAL.contains(tok) && 
				!END_CLAUSES.contains(tok) &&
				!QUANTIFIERS.contains(tok) &&
				!DML.contains(tok) &&
				!MISC.contains(tok);
	}

	private static boolean isWhitespace(String token) {
		return StringHelper.WHITESPACE.indexOf(token)>=0;
	}
	
	private void newline() {
		result.append("\n"); //$NON-NLS-1$
		for ( int i=0; i<indent; i++ ) {
			result.append(indentString);
		}
		beginLine = true;
	}

	public static void main(String[] args) {
		if ( args.length>0 ) System.out.println( 
			new Formatter( StringHelper.join(" ", args) ).format() //$NON-NLS-1$
		);
	}

}
