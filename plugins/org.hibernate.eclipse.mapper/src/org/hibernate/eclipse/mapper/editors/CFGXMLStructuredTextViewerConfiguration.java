package org.hibernate.eclipse.mapper.editors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.wst.sse.core.text.rules.StructuredTextPartitioner;
import org.eclipse.wst.xml.core.text.rules.StructuredTextPartitionerForXML;
import org.eclipse.wst.xml.ui.StructuredTextViewerConfigurationXML;
import org.eclipse.wst.xml.ui.contentassist.NoRegionContentAssistProcessor;

public class CFGXMLStructuredTextViewerConfiguration extends StructuredTextViewerConfigurationXML {

	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		IContentAssistant ca = super.getContentAssistant(sourceViewer);
		if (ca != null && ca instanceof ContentAssistant) {
			ContentAssistant contentAssistant = (ContentAssistant) ca;
			IContentAssistProcessor xmlContentAssistProcessor = new CFGXMLContentAssistProcessor(findJavaProject());
			IContentAssistProcessor noRegionProcessor = new NoRegionContentAssistProcessor();
			setContentAssistProcessor(contentAssistant, xmlContentAssistProcessor, StructuredTextPartitioner.ST_DEFAULT_PARTITION);
			setContentAssistProcessor(contentAssistant, xmlContentAssistProcessor, StructuredTextPartitionerForXML.ST_DEFAULT_XML);
			setContentAssistProcessor(contentAssistant, noRegionProcessor, StructuredTextPartitioner.ST_UNKNOWN_PARTITION);			
		}
		return ca;
	}	
	
	
	/**
	 * @return
	 */
	private IJavaProject findJavaProject() {
		if (getEditorPart() != null) {
	         IFile file = null;
	         IProject project = null;
	         IJavaProject jProject = null;

	         IEditorInput input = this.getEditorPart().getEditorInput();
	         
	         if (input instanceof IFileEditorInput)
	         {
	            IFileEditorInput fileInput = (IFileEditorInput) input;
	            file = fileInput.getFile();
	            project = file.getProject();
	            jProject = JavaCore.create(project);
	         }

	         return jProject;
	      }

		return null;
	}
	
}
