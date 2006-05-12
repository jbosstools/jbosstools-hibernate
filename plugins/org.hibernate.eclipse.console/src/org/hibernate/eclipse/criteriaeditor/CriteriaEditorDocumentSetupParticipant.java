package org.hibernate.eclipse.criteriaeditor;

import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jdt.ui.text.JavaTextTools;
import org.eclipse.jface.text.IDocument;
import org.hibernate.eclipse.console.HibernateConsolePlugin;

public class CriteriaEditorDocumentSetupParticipant {

	/**
     * Sets up the document to be ready for use by a text file buffer.
     * 
	 * @see org.eclipse.core.filebuffers.IDocumentSetupParticipant#setup(org.eclipse.jface.text.IDocument)
	 */
	public void setup( IDocument document ) {
		JavaTextTools tools= HibernateConsolePlugin.getDefault().getJavaTextTools();
        tools.setupJavaDocumentPartitioner(document, IJavaPartitions.JAVA_PARTITIONING);        
	}
}
