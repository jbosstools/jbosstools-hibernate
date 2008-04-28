/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.criteriaeditor;

import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.text.IColorManager;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jdt.ui.text.JavaSourceViewerConfiguration;
import org.eclipse.jdt.ui.text.JavaTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public class JavaViewerConfiguration extends JavaSourceViewerConfiguration {

	private IPreferenceStore preferenceStore;

	public JavaViewerConfiguration(JavaTextTools tools,	IPreferenceStore preferenceStore, CriteriaEditor editor) {
		super( tools.getColorManager(), preferenceStore, editor, IJavaPartitions.JAVA_PARTITIONING );
		this.preferenceStore = preferenceStore;
	}

	public IContentAssistProcessor getContentAssistantProcessor() {
		return new JavaCompletionProcessor( (CriteriaEditor) getEditor() );
	}

	/**
	 * @see SourceViewerConfiguration#getContentAssistant(ISourceViewer)
	 */
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {

		ContentAssistant assistant = new ContentAssistant();
		assistant.setContentAssistProcessor( getContentAssistantProcessor(),
				IDocument.DEFAULT_CONTENT_TYPE );

		configure( assistant, preferenceStore, getColorManager() );

		assistant.setContextInformationPopupOrientation( IContentAssistant.CONTEXT_INFO_ABOVE );
		assistant.setInformationControlCreator( getInformationControlCreator( sourceViewer ) );

		return assistant;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getReconciler(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		return null;
	}
	
	public static void configure(ContentAssistant assistant, IPreferenceStore store, IColorManager manager) {
				
		boolean enabled= store.getBoolean(PreferenceConstants.CODEASSIST_AUTOACTIVATION);
		assistant.enableAutoActivation(enabled);
		
		int delay= store.getInt(PreferenceConstants.CODEASSIST_AUTOACTIVATION_DELAY);
		assistant.setAutoActivationDelay(delay);
		
		Color c= getColor(store, PreferenceConstants.CODEASSIST_PROPOSALS_FOREGROUND, manager);
		assistant.setProposalSelectorForeground(c);
		
		c= getColor(store, PreferenceConstants.CODEASSIST_PROPOSALS_BACKGROUND, manager);
		assistant.setProposalSelectorBackground(c);
		
		c= getColor(store, PreferenceConstants.CODEASSIST_PARAMETERS_FOREGROUND, manager);
		assistant.setContextInformationPopupForeground(c);
		assistant.setContextSelectorForeground(c);
		
		c= getColor(store, PreferenceConstants.CODEASSIST_PARAMETERS_BACKGROUND, manager);
		assistant.setContextInformationPopupBackground(c);
		assistant.setContextSelectorBackground(c);
		
		enabled= store.getBoolean(PreferenceConstants.CODEASSIST_AUTOINSERT);
		assistant.enableAutoInsert(enabled);

		JavaCompletionProcessor cp= getJavaProcessor(assistant);
		if (cp != null) {
			String triggers= store.getString(PreferenceConstants.CODEASSIST_AUTOACTIVATION_TRIGGERS_JAVA);
			if (triggers != null) {
				cp.setCompletionProposalAutoActivationCharacters(triggers.toCharArray());
			}

			// alternative is to use a registry which is internal or look up a sorter id...
			enabled= store.getBoolean(PreferenceConstants.CODEASSIST_ORDER_PROPOSALS);
			cp.orderProposalsAlphabetically(enabled);
		}
	}

	private static JavaCompletionProcessor getJavaProcessor(ContentAssistant assistant) {
		IContentAssistProcessor p= assistant.getContentAssistProcessor(IDocument.DEFAULT_CONTENT_TYPE);
		if (p instanceof JavaCompletionProcessor)
			return  (JavaCompletionProcessor) p;
		return null;
	}
		
	private static Color getColor(IPreferenceStore store, String key, IColorManager manager) {
		RGB rgb= PreferenceConverter.getColor(store, key);
		return manager.getColor(rgb);
	}
	
}
