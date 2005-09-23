package org.hibernate.eclipse.mapper.editors;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.SourceEditingRuntimeException;
import org.eclipse.wst.sse.ui.internal.StructuredTextEditor;
import org.eclipse.wst.xml.ui.internal.Logger;
import org.eclipse.wst.xml.ui.internal.XMLUIPlugin;
import org.eclipse.wst.xml.ui.internal.provisional.StructuredTextEditorXML;
import org.eclipse.wst.xml.ui.internal.tabletree.IDesignViewer;
import org.eclipse.wst.xml.ui.internal.tabletree.XMLEditorMessages;
import org.eclipse.wst.xml.ui.internal.tabletree.XMLMultiPageEditorPart;
import org.eclipse.wst.xml.ui.internal.tabletree.XMLTableTreeHelpContextIds;
import org.eclipse.wst.xml.ui.internal.tabletree.XMLTableTreeViewer;
import org.hibernate.eclipse.mapper.MapperPlugin;

public class XMLFormEditorPart extends FormEditor {

	/** The text editor. */
	private StructuredTextEditor textEditor;
	private IDesignViewer designViewer;
	private PropertyListener propertyListener;
	private int designPageIndex;
	private int sourcePageIndex;
	
	protected void addPages() {
		
		try {
			createSourcePage();
			createAndAddDesignPage();
			addSourcePage();
			
			connectDesignPage();
		}
		catch (PartInitException e) {
			MapperPlugin.getDefault().logException(e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Connects the design viewer with the viewer selection manager. Should be
	 * done after createSourcePage() is done because we need to get the
	 * ViewerSelectionManager from the TextEditor. setModel is also done here
	 * because getModel() needs to reference the TextEditor.
	 */
	private void connectDesignPage() {
		if (designViewer != null) {
			designViewer.setModel(getModel());
			designViewer.setViewerSelectionManager(textEditor.getViewerSelectionManager());
		}
	}
	/**
	 * Create and Add the Design Page using a registered factory
	 * 
	 */
	private void createAndAddDesignPage() {
		IDesignViewer tableTreeViewer = createDesignPage();

		designViewer = tableTreeViewer;
		// note: By adding the design page as a Control instead of an
		// IEditorPart, page switches will indicate
		// a "null" active editor when the design page is made active
		designPageIndex = addPage(tableTreeViewer.getControl());
		setPageText(designPageIndex, tableTreeViewer.getTitle());
	}

	protected IDesignViewer createDesignPage() {
		XMLTableTreeViewer tableTreeViewer = new XMLTableTreeViewer(getContainer());
		// Set the default infopop for XML design viewer.
		XMLUIPlugin.getInstance().getWorkbench().getHelpSystem().setHelp(tableTreeViewer.getControl(), XMLTableTreeHelpContextIds.XML_DESIGN_VIEW_HELPID);
		return tableTreeViewer;
	}

	
	protected void createSourcePage() throws PartInitException {
		textEditor = createTextEditor();
		textEditor.setEditorPart(this);

		if (propertyListener == null) {
			propertyListener = new PropertyListener();
		}
		textEditor.addPropertyListener(propertyListener);
	}
	
	private StructuredTextEditor createTextEditor() {
		return new StructuredTextEditorXML();
	}
	
	StructuredTextEditor getTextEditor() {
		return textEditor;
	}
	
	/**
	 * Internal IPropertyListener
	 */
	class PropertyListener implements IPropertyListener {
		public void propertyChanged(Object source, int propId) {
			switch (propId) {
				// had to implement input changed "listener" so that
				// StructuredTextEditor could tell it containing editor that
				// the input has change, when a 'resource moved' event is
				// found.
				case IEditorPart.PROP_INPUT :
				case IEditorPart.PROP_DIRTY : {
					if (source == getTextEditor()) {
						if (getTextEditor().getEditorInput() != getEditorInput()) {
							setInput(getTextEditor().getEditorInput());
							/*
							 * title should always change when input changes.
							 * create runnable for following post call
							 */
							Runnable runnable = new Runnable() {
								public void run() {
									_firePropertyChange(IWorkbenchPart.PROP_TITLE);
								}
							};
							/*
							 * Update is just to post things on the display
							 * queue (thread). We have to do this to get the
							 * dirty property to get updated after other
							 * things on the queue are executed.
							 */
							postOnDisplayQue(runnable);
						}
					}
					break;
				}
				case IWorkbenchPart.PROP_TITLE : {
					// update the input if the title is changed
					if (source == getTextEditor()) {
						if (getTextEditor().getEditorInput() != getEditorInput()) {
							setInput(getTextEditor().getEditorInput());
						}
					}
					break;
				}
				default : {
					// propagate changes. Is this needed? Answer: Yes.
					if (source == getTextEditor()) {
						_firePropertyChange(propId);
					}
					break;
				}
			}

		}
	}
	class TextInputListener implements ITextInputListener {
		public void inputDocumentAboutToBeChanged(IDocument oldInput, IDocument newInput) {
		}

		public void inputDocumentChanged(IDocument oldInput, IDocument newInput) {
			if (designViewer != null && newInput != null)
				designViewer.setModel(getModel());
		}
	}
	
	private void addSourcePage() throws PartInitException {
		try {
			sourcePageIndex = addPage(textEditor, getEditorInput());
			setPageText(sourcePageIndex, XMLEditorMessages.XMLMultiPageEditorPart_0);
			// the update's critical, to get viewer selection manager and
			// highlighting to work
			textEditor.update();

			firePropertyChange(PROP_TITLE);

			// Changes to the Text Viewer's document instance should also
			// force an
			// input refresh
			textEditor.getTextViewer().addTextInputListener(new TextInputListener());
		}
		catch (PartInitException exception) {
			// dispose editor
			dispose();
			MapperPlugin.getDefault().logException(exception);
			throw new SourceEditingRuntimeException(exception, XMLEditorMessages.An_error_has_occurred_when1_ERROR_);
		}
	}

	private IStructuredModel getModel() {
		IStructuredModel model = null;
		if (textEditor != null)
			model = textEditor.getModel(); // should get it by other means!
		return model;
	}

	public void doSave(IProgressMonitor monitor) {
		textEditor.doSave(monitor);
	}

	public void doSaveAs() {
		textEditor.doSaveAs();
	}

	public boolean isSaveAsAllowed() {
		return textEditor.isSaveAsAllowed();
	}

	void _firePropertyChange(int property) {
		super.firePropertyChange(property);
	}
	
	/**
	 * Posts the update code "behind" the running operation.
	 */
	void postOnDisplayQue(Runnable runnable) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
		if (windows != null && windows.length > 0) {
			Display display = windows[0].getShell().getDisplay();
			display.asyncExec(runnable);
		}
		else
			runnable.run();
	}

	void gotoMarker(IMarker marker) {
		setActivePage(sourcePageIndex);
		IDE.gotoMarker(textEditor, marker);
	}
	
	public Object getAdapter(Class key) {
		Object result = null;
		if (key == IDesignViewer.class) {
			result = designViewer;

		}
		else if (key.equals(IGotoMarker.class)) {
			result = new IGotoMarker() {
				public void gotoMarker(IMarker marker) {
					XMLFormEditorPart.this.gotoMarker(marker);
				}
			};
		}
		else {
			// DMW: I'm bullet-proofing this because
			// its been reported (on IBM WSAD 4.03 version) a null pointer
			// sometimes
			// happens here on startup, when an editor has been left
			// open when workbench shutdown.
			if (textEditor != null) {
				result = textEditor.getAdapter(key);
			}
		}
		if(result==null) {
			return super.getAdapter(key);
		} else {
			return result;
		}
	}
}
