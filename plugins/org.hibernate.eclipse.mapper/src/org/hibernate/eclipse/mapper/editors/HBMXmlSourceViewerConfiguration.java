/*
 * Created on 16-Nov-2004
 *
 */
package org.hibernate.eclipse.mapper.editors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.jboss.ide.eclipse.jdt.xml.ui.assist.XMLContentAssistProcessor;
import org.jboss.ide.eclipse.jdt.xml.ui.editors.XMLConfiguration;
import org.jboss.ide.eclipse.jdt.xml.ui.editors.XMLTextTools;

/**
 * @author max
 *
 */
public class HBMXmlSourceViewerConfiguration extends XMLConfiguration {

	/**
	 * @param tools
	 */
	public HBMXmlSourceViewerConfiguration(XMLTextTools tools) {
		super(tools);		
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.ide.eclipse.jdt.xml.ui.editors.XMLConfiguration#getTagsContentAssistProcessor()
	 */
	protected XMLContentAssistProcessor getTagsContentAssistProcessor() {
		XMLContentAssistProcessor processor = super.getTagsContentAssistProcessor();
		
		processor.addAttributeValueContributor(new HBMXmlTypeContributor(findJavaProject()));
		
		return processor;
	}

	/**
	 * @return
	 */
	private IJavaProject findJavaProject() {
		if (this.getEditor() != null)
	      {
	         IFile file = null;
	         IProject project = null;
	         IJavaProject jProject = null;

	         IEditorInput input = this.getEditor().getEditorInput();
	         
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
