package org.hibernate.eclipse.console.editors;

import org.eclipse.ui.editors.text.TextEditor;

public class HQLEditor extends TextEditor {

	private ColorManager colorManager;
	public static final String ID = "org.hibernate.eclipse.console.editors.hqleditor";

	public HQLEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new HQLConfiguration(colorManager) );
		setDocumentProvider(new HQLDocumentProvider() );
	}
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

}
