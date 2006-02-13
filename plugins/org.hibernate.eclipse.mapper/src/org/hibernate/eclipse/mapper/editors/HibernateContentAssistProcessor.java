package org.hibernate.eclipse.mapper.editors;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.hibernate.eclipse.mapper.editors.xpl.BaseXMLContentAssistProcessor;

public abstract class HibernateContentAssistProcessor extends BaseXMLContentAssistProcessor {
	
	public HibernateContentAssistProcessor() {
		
	}

	protected IJavaProject getJavaProject(ContentAssistRequest contentAssistRequest) {
		return CFGXMLStructuredTextViewerConfiguration.findJavaProject(contentAssistRequest);
	}
	
}
