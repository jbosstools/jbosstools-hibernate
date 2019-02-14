package org.hibernate.eclipse.jdt.ui.internal.jpa.process;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.text.edits.TextEdit;

/**
 * group all information about changes of document in one structure
 */
public class ChangeStructure {
	public IPath path;
	public ICompilationUnit icu;
	public TextEdit textEdit;
	public Change change;
}
