package org.hibernate.eclipse.criteriaeditor;

import java.util.ResourceBundle;

import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.hibernate.eclipse.console.Messages;

public class CriteriaEditor extends AbstractDecoratedTextEditor {

	public CriteriaEditor() {
		super();
		setSourceViewerConfiguration(new CriteriaViewerConfiguration(JavaPlugin.getDefault().getJavaTextTools(), this));
		IPreferenceStore store = new ChainedPreferenceStore(new IPreferenceStore[] {
				PreferenceConstants.getPreferenceStore(),
				EditorsUI.getPreferenceStore(),
				JavaPlugin.getDefault().getPreferenceStore()});
		setPreferenceStore(store);
		
	}
	
	protected void createActions() {
		super.createActions();
		Action action = new TextOperationAction(getResourceBundle(), "ContentAssistProposal.", this, ISourceViewer.CONTENTASSIST_PROPOSALS); //$NON-NLS-1$
		action.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
		setAction("ContentAssistProposal", action);//$NON-NLS-1$
		
	}
	
	private ResourceBundle getResourceBundle() {
		return ResourceBundle.getBundle( Messages.BUNDLE_NAME );
	}

	
}
