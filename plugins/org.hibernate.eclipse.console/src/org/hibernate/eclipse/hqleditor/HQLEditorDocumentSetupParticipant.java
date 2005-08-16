package org.hibernate.eclipse.hqleditor;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.DefaultPartitioner;

/**
 * This class implements the <code>IDocumentSetupParticipant</code> interface in
 * order to setup document partitioning for HQL documents.
 */
public class HQLEditorDocumentSetupParticipant implements IDocumentSetupParticipant {
	
	/**
     * Sets up the document to be ready for use by a text file buffer.
     * 
	 * @see org.eclipse.core.filebuffers.IDocumentSetupParticipant#setup(org.eclipse.jface.text.IDocument)
	 */
	public void setup( IDocument document ) {
		if (document instanceof IDocumentExtension3) {
			IDocumentExtension3 extension3 = (IDocumentExtension3) document;
			IDocumentPartitioner partitioner = new DefaultPartitioner( new HQLPartitionScanner(), HQLPartitionScanner.HQL_PARTITION_TYPES );
            partitioner.connect( document );
			extension3.setDocumentPartitioner( HQLSourceViewerConfiguration.HQL_PARTITIONING, partitioner );
		}
	}
}
