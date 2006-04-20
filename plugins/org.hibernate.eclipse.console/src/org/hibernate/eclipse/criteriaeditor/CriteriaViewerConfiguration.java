package org.hibernate.eclipse.criteriaeditor;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.jdt.ui.text.JavaTextTools;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.source.ISourceViewer;

public class CriteriaViewerConfiguration extends JavaSourceViewerConfiguration {

	private CriteriaCompletionProcessor completionProcessor;

	public CriteriaViewerConfiguration(JavaTextTools tools, CriteriaEditor editor) {
		super(tools.getColorManager(), JavaPlugin.getDefault().getPreferenceStore(), editor, null);		
	}
	
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		return new ContentAssistant() {
		
			public IContentAssistProcessor getContentAssistProcessor(String contentType) {
				return new CriteriaCompletionProcessor((CriteriaEditor) getEditor());
			}
		
		} ;
	}
}
