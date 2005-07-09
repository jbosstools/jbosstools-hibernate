package org.hibernate.eclipse.mapper.editors;


import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.core.internal.provisional.exceptions.SourceEditingRuntimeException;
import org.eclipse.wst.sse.ui.internal.StructuredTextEditor;
import org.eclipse.wst.xml.core.internal.provisional.IXMLPreferenceNames;
import org.eclipse.wst.xml.ui.internal.provisional.StructuredTextEditorXML;
import org.hibernate.eclipse.mapper.MapperPlugin;
import org.hibernate.eclipse.mapper.editors.reveng.OverrideFormPage;
import org.hibernate.eclipse.mapper.editors.reveng.OverviewFormPage;
import org.hibernate.eclipse.mapper.editors.reveng.PreviewMappingFormPage;
import org.hibernate.eclipse.mapper.editors.reveng.SQLTypeMappingFormPage;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * An example showing how to create a multi-page editor.
 * This example has 3 pages:
 * <ul>
 * <li>page 0 contains a nested text editor.
 * <li>page 1 allows you to change the font used in page 2
 * <li>page 2 shows the words in page 0 in sorted order
 * </ul>
 */
public class ReverseEngineeringMultiPageEditor extends FormEditor {

	/**
	 * Internal part activation listener
	 */
	class PartListener extends ShellAdapter implements IPartListener {
		private IWorkbenchPart fActivePart;
		private boolean fIsHandlingActivation = false;

		private void handleActivation() {

			if (fIsHandlingActivation)
				return;

			if (fActivePart == ReverseEngineeringMultiPageEditor.this) {
				fIsHandlingActivation = true;
				try {
					safelySanityCheckState();
				} finally {
					fIsHandlingActivation = false;
				}
			}
		}

		/**
		 * @see IPartListener#partActivated(IWorkbenchPart)
		 */
		public void partActivated(IWorkbenchPart part) {
			fActivePart = part;
			handleActivation();
		}

		/**
		 * @see IPartListener#partBroughtToTop(IWorkbenchPart)
		 */
		public void partBroughtToTop(IWorkbenchPart part) {
		}

		/**
		 * @see IPartListener#partClosed(IWorkbenchPart)
		 */
		public void partClosed(IWorkbenchPart part) {
		}

		/**
		 * @see IPartListener#partDeactivated(IWorkbenchPart)
		 */
		public void partDeactivated(IWorkbenchPart part) {
			fActivePart = null;
		}

		/**
		 * @see IPartListener#partOpened(IWorkbenchPart)
		 */
		public void partOpened(IWorkbenchPart part) {
		}

		/*
		 * @see ShellListener#shellActivated(ShellEvent)
		 */
		public void shellActivated(ShellEvent e) {
			handleActivation();
		}
	}
	
	/** The source page index. */
	private int fSourcePageIndex;
	/** The text editor. */
	private StructuredTextEditor fTextEditor;

	private IFormPage overviewPage;
	private IFormPage typeMappingPage;
	private IFormPage previewMappingPage;
	private IFormPage overridePage;


	/**
	 * StructuredTextMultiPageEditorPart constructor comment.
	 */
	public ReverseEngineeringMultiPageEditor() {
		super();
	}

	protected int getDefaultPageIndex() {
		return overviewPage.getIndex();
	}
	
	/*
	 * This method is just to make firePropertyChanged accessbible from some
	 * (anonomous) inner classes.
	 */
	protected void _firePropertyChange(int property) {
		//super.firePropertyChange(property);
	}

	/**
	 * Adds the source page of the multi-page editor.
	 */
	protected void addSourcePage() {
		try {
			fSourcePageIndex = addPage(fTextEditor, getEditorInput() );
			setPageText(fSourcePageIndex, "source");
		} catch (PartInitException exception) {
			// dispose editor
			dispose();
			MapperPlugin.getDefault().logException(exception);
			throw new SourceEditingRuntimeException(exception, "An error has occurred when {1}");
		}
	}

	
	protected void addPages() {
		try {
			// source page MUST be created before design page, now
			createSourcePage();
			createAndAddFormPages();
			addSourcePage();

			setActivePage();

			// future_TODO: add a catch block here for any exception the
			// design
			// page throws and convert it into a more informative message.
		} catch (PartInitException e) {
			MapperPlugin.getDefault().logException(e);
			throw new RuntimeException(e);
		}		
	}

	private void createAndAddFormPages() throws PartInitException {
		overviewPage = new OverviewFormPage(this);
		addPage(overviewPage);
		
		typeMappingPage = new SQLTypeMappingFormPage(this);
		addPage(typeMappingPage);
		
		overridePage = new OverrideFormPage(this);
		addPage(overridePage);
		
		previewMappingPage = new PreviewMappingFormPage(this);
		addPage(previewMappingPage);
		
	}

	/**
	 * Creates the source page of the multi-page editor.
	 */
	protected void createSourcePage() {
		fTextEditor = createTextEditor();
		fTextEditor.setEditorPart(this);
		//fTextEditor.addPropertyListener(this);
	}

	/**
	 * Method createTextEditor.
	 * 
	 * @return StructuredTextEditor
	 */
	protected StructuredTextEditor createTextEditor() {
		return new StructuredTextEditorXML();
	}

	/*
	 * (non-Javadoc) Saves the contents of this editor. <p> Subclasses must
	 * override this method to implement the open-save-close lifecycle for an
	 * editor. For greater details, see <code> IEditorPart </code></p>
	 * 
	 * @see IEditorPart
	 */
	public void doSave(IProgressMonitor monitor) {
		fTextEditor.doSave(monitor);
	}

	/*
	 * (non-Javadoc) Saves the contents of this editor to another object. <p>
	 * Subclasses must override this method to implement the open-save-close
	 * lifecycle for an editor. For greater details, see <code> IEditorPart
	 * </code></p>
	 * 
	 * @see IEditorPart
	 */
	public void doSaveAs() {
		fTextEditor.doSaveAs();
		// 253619
		// following used to be executed here, but is
		// now called "back" from text editor (since
		// mulitiple paths to the performSaveAs in StructuredTextEditor.
		//doSaveAsForStructuredTextMulitPagePart();
	}

	private void editorInputIsAcceptable(IEditorInput input) throws PartInitException {
		if (input instanceof IFileEditorInput) {
			// verify that it can be opened
			CoreException[] coreExceptionArray = new CoreException[1];
			if (fileDoesNotExist( (IFileEditorInput) input, coreExceptionArray) ) {
				CoreException coreException = coreExceptionArray[0];
				if (coreException.getStatus().getCode() == IResourceStatus.FAILED_READ_LOCAL) {
					// I'm assuming this is always 'does not exist'
					// we'll refresh local go mimic behavior of default
					// editor, where the
					// troublesome file is refreshed (and will cause it to
					// 'disappear' from Navigator.
					try {
						( (IFileEditorInput) input).getFile().refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor() );
					} catch (CoreException ce) {
						// very unlikely
						MapperPlugin.getDefault().logException(ce);
					}
					throw new PartInitException("Resource does not exist" + input.getName() );
				} else {
					throw new PartInitException("Editor could not be open" + input.getName() );
				}
			}
		} else if (input instanceof IStorageEditorInput) {
			InputStream contents = null;
			try {
				contents = ( (IStorageEditorInput) input).getStorage().getContents();
			} catch (CoreException noStorageExc) {
			}
			if (contents == null) {
				throw new PartInitException("Editor could not be open on " + input.getName() );
			} else {
				try {
					contents.close();
				} catch (IOException e) {
				}
			}
		}
	}

	
	/*
	 * (non-Javadoc) Initializes the editor part with a site and input. <p>
	 * Subclasses of <code> EditorPart </code> must implement this method.
	 * Within the implementation subclasses should verify that the input type
	 * is acceptable and then save the site and input. Here is sample code:
	 * </p><pre> if (!(input instanceof IFileEditorInput) ) throw new
	 * PartInitException("Invalid Input: Must be IFileEditorInput");
	 * setSite(site); setInput(editorInput); </pre>
	 */
	protected boolean fileDoesNotExist(IFileEditorInput input, Throwable[] coreException) {
		boolean result = false;
		InputStream inStream = null;
		if ( (!(input.exists() ) ) || (!(input.getFile().exists() ) ) ) {
			result = true;
		} else {
			try {
				inStream = input.getFile().getContents(true);
			} catch (CoreException e) {
				// very likely to be file not found
				result = true;
				coreException[0] = e;
			} finally {
				if (input != null) {
					try {
						if (inStream != null) {
							inStream.close();
						}
					} catch (IOException e) {
						MapperPlugin.getDefault().logException(e);
					}
				}
			}
		}
		return result;
	}

	public Object getAdapter(Class key) {
		Object result = null;
		
			// DMW: I'm bullet-proofing this because
			// its been reported (on 4.03 version) a null pointer sometimes
			// happens here on startup, when an editor has been left
			// open when workbench shutdown.
			if (fTextEditor != null) {
				result = fTextEditor.getAdapter(key);
			}
		
		return result;
	}

	/**
	 * IExtendedSimpleEditor method
	 */
	public IEditorPart getEditorPart() {
		return this;
	}

	protected IStructuredModel getModel() {
		IStructuredModel model = null;
		if (fTextEditor != null)
			model = fTextEditor.getModel();
		return model;
	}

	protected IPreferenceStore getPreferenceStore() {
		return MapperPlugin.getDefault().getPreferenceStore();
	}

	public StructuredTextEditor getTextEditor() {
		return fTextEditor;
	}


/*	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		editorInputIsAcceptable(input);
		try {
			super.init(site, input);
			if (partListener == null) {
				partListener = new PartListener();
			}
			//getSite().getPage().addPartListener(partListner);
			// we want to listen for our own activation
			IWorkbenchWindow window = getSite().getWorkbenchWindow();
			window.getPartService().addPartListener(partListener);
			window.getShell().addShellListener(partListener);
		} catch (Exception e) {
			if (e instanceof SourceEditingRuntimeException) {
				Throwable t = ( (SourceEditingRuntimeException) e).getOriginalException();
				if (t instanceof IOException) {
					System.out.println(t);
					// file not found
				}
			}
		}
		setPartName(input.getName() );
	}*/

	/*
	 * (non-Javadoc) Returns whether the "save as" operation is supported by
	 * this editor. <p> Subclasses must override this method to implement the
	 * open-save-close lifecycle for an editor. For greater details, see
	 * <code> IEditorPart </code></p>
	 * 
	 * @see IEditorPart
	 */
	public boolean isSaveAsAllowed() {
		return fTextEditor != null && fTextEditor.isSaveAsAllowed();
	}

	/*
	 * (non-Javadoc) Returns whether the contents of this editor should be
	 * saved when the editor is closed. <p> This method returns <code> true
	 * </code> if and only if the editor is dirty ( <code> isDirty </code> ).
	 * </p>
	 */
	public boolean isSaveOnCloseNeeded() {
		// overriding super class since it does a lowly isDirty!
		if (fTextEditor != null)
			return fTextEditor.isSaveOnCloseNeeded();
		return isDirty();
	}

	/**
	 * Notifies this multi-page editor that the page with the given id has
	 * been activated. This method is called when the user selects a different
	 * tab.
	 * 
	 * @param newPageIndex
	 *            the index of the activated page
	 */
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		saveLastActivePageIndex(newPageIndex);
	}

	/**
	 * Posts the update code "behind" the running operation.
	 */
	protected void postOnDisplayQue(Runnable runnable) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
		if (windows != null && windows.length > 0) {
			Display display = windows[0].getShell().getDisplay();
			display.asyncExec(runnable);
		} else
			runnable.run();
	}


	protected void safelySanityCheckState() {
		// If we're called before editor is created, simply ignore since we
		// delegate this function to our embedded TextEditor
		if (getTextEditor() == null)
			return;

		getTextEditor().safelySanityCheckState(getEditorInput() );

	}

	protected void saveLastActivePageIndex(int newPageIndex) {
		// save the last active page index to preference manager
		getPreferenceStore().setValue(IXMLPreferenceNames.LAST_ACTIVE_PAGE, newPageIndex);
	}

	/**
	 * Sets the currently active page.
	 */
	protected void setActivePage() {
		// retrieve the last active page index from preference manager
		int activePageIndex = getPreferenceStore().getInt(IXMLPreferenceNames.LAST_ACTIVE_PAGE);

		// We check this range since someone could hand edit the XML
		// preference file to an invalid value ... which I know from
		// experience :( ... if they do, we'll reset to default and continue
		// rather than throw an assertion error in the setActivePage(int)
		// method.
		if (activePageIndex < 0 || activePageIndex >= getPageCount() ) {
			activePageIndex = getDefaultPageIndex();
		}
		setActivePage(activePageIndex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
	 */
	protected void setInput(IEditorInput input) {
		// If driven from the Source page, it's "model" may not be up to date
		// with the input just yet. We'll rely on later notification from the
		// TextViewer to set us straight
		super.setInput(input);
		/*if (fDesignViewer != null)
			fDesignViewer.setModel(getModel() );*/
		setPartName(input.getName() );
	}

	/**
	 * IExtendedMarkupEditor method
	 */
	public IStatus validateEdit(Shell context) {
		if (getTextEditor() == null)
			return new Status(IStatus.ERROR, MapperPlugin.ID, IStatus.INFO, "", null); //$NON-NLS-1$

		return getTextEditor().validateEdit(context);
	}
}
