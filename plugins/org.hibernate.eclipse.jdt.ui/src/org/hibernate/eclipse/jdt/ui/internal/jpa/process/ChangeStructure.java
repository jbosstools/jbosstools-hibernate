package org.hibernate.eclipse.jdt.ui.internal.jpa.process;

import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.text.edits.TextEdit;

/**
 * group all information about changes of document in one structure
 */
public class ChangeStructure {
	public String fullyQualifiedName;
	public IPath path;
	public ICompilationUnit icu;
	public TextEdit textEdit;
	public ITextFileBuffer textFileBuffer;
	public Change change;
}
