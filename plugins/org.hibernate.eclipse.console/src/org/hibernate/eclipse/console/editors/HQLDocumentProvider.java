package org.hibernate.eclipse.console.editors;

import java.io.ByteArrayInputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.DefaultPartitioner;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.FileDocumentProvider;

public class HQLDocumentProvider extends FileDocumentProvider {

	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);
		if (document != null) {
			IDocumentPartitioner partitioner =
				new DefaultPartitioner(
					new HQLPartitionScanner(),
					new String[] {
						HQLPartitionScanner.HQL_TAG,
						HQLPartitionScanner.HQL_COMMENT });
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return document;
	}
	
	public boolean setDocumentContent(IDocument document, IEditorInput editorInput, String encoding) throws CoreException {
		if(!super.setDocumentContent(document, editorInput, encoding)) {
			if(editorInput instanceof HQLEditorInput) {
				setDocumentContent(document, new ByteArrayInputStream(((HQLEditorInput)editorInput).getQueryString().getBytes()), null);
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}
	
	public boolean isModifiable(Object element) {
		return true;
	}
	
	
}