package org.hibernate.eclipse.jdt.ui.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.IProblem;
import org.hibernate.eclipse.jdt.ui.Activator;

public class HQLProblem extends CategorizedProblem {	
	private int startingOffset;
	private int endingOffset;
	private int line;
	private IFile resource;
	private final String msg;
	private final boolean isError;
	
	HQLProblem(final String msg, 
			   boolean isError, 
			   final IFile resource, 
			   final int startingOffset,
			   final int endingOffset,
			   final int line) {
		this.msg = msg;
		this.isError = isError;
		this.startingOffset = startingOffset;
		this.endingOffset = endingOffset;
		this.line = line;
		this.resource = resource;
	}

	public int getID() {
		return IProblem.ExternalProblemNotFixable;
	}
	
	public String[] getArguments() {	
		return new String[0];
	}
	
	public String getMessage() {	
		return msg;
	}
	
	public char[] getOriginatingFileName() {		
		return resource.getName().toCharArray();
	}
	
	public int getSourceStart() {
		return startingOffset;
	}
	
	public int getSourceEnd() {	
		return endingOffset;
	}
	
	public int getSourceLineNumber() {		
		return line;
	}
	
	public void setSourceStart(int sourceStart) {
		startingOffset = sourceStart;
	}	
	
	public void setSourceEnd(int sourceEnd) {
		endingOffset = sourceEnd;
	}
	
	public void setSourceLineNumber(int lineNumber) {
		line = lineNumber;		
	}
	
	public boolean isError() {
		return isError;
	}
	
	public boolean isWarning() {
		return !isError();
	}
	
	
	public int getCategoryID() {		
		return CAT_SYNTAX;
	}
	
	public String getMarkerType() {
		return Activator.HQL_SYNTAX_PROBLEM;
	}
}
