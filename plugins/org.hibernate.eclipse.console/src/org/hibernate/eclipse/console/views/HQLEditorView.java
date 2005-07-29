package org.hibernate.eclipse.console.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.commands.AbstractHandler;
import org.eclipse.ui.commands.ExecutionException;
import org.eclipse.ui.commands.HandlerSubmission;
import org.eclipse.ui.commands.IHandler;
import org.eclipse.ui.commands.IWorkbenchCommandSupport;
import org.eclipse.ui.commands.Priority;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.IUpdate;
import org.hibernate.eclipse.console.Messages;
import org.hibernate.eclipse.console.actions.ExecuteHQLAction;

public class HQLEditorView extends ViewPart implements ITextInputListener {	
	
	private ExecuteHQLAction executeAction;
	
	
	public HQLEditorView() {
		super();
	}


	/**
	 * Returns the query to be executed. The query is either 1) the 
	 * text currently highlighted/selected in the editor or 2) all of 
     * the text in the editor. 
	 * @return query string to be executed
	 */
	public String getQuery() {
	    String query; 
	    
	    ITextSelection selection = (ITextSelection) fSourceViewer.getSelection();
	    if ( !selection.isEmpty() && selection.getLength()>0 ) {
	        query = selection.getText();
	    } else {    
	        query = getContents();
	    }
	    
		return query;
	}
	
	public void setQuery(String text) {
		fSourceViewer.getDocument().set(text);
	}
	
	protected IDocumentListener fDocumentListener= null;
	
	protected SourceViewer fSourceViewer;
	protected IAction fClearDisplayAction;
	protected TextOperationAction fContentAssistAction;

	protected Map fGlobalActions= new HashMap(4);
	protected List fSelectionActions= new ArrayList(3);

	protected String fRestoredContents= null;
	/**
	 * This memento allows the Display view to save and restore state
	 * when it is closed and opened within a session. A different
	 * memento is supplied by the platform for persistance at
	 * workbench shutdown.
	 */
	private static IMemento fgMemento;
	private HandlerSubmission fSubmission;
	
	/**
	 * @see ViewPart#createChild(IWorkbenchPartContainer)
	 */
	public void createPartControl(Composite parent) {
		
		int styles= SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.FULL_SELECTION;
		fSourceViewer= new SourceViewer(parent, null, styles);
		//fSourceViewer.configure(new DisplayViewerConfiguration());
		fSourceViewer.getSelectionProvider().addSelectionChangedListener(getSelectionChangedListener());
		IDocument doc= getRestoredDocument();
		fSourceViewer.setDocument(doc);
		fSourceViewer.addTextInputListener(this);
		fRestoredContents= null;
		createActions();
		initializeToolBar();

		// create context menu
		MenuManager menuMgr = new MenuManager("#PopUp"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager mgr) {
				fillContextMenu(mgr);
			}
		});
		
		Menu menu = menuMgr.createContextMenu(fSourceViewer.getTextWidget());
		fSourceViewer.getTextWidget().setMenu(menu);
		getSite().registerContextMenu(menuMgr, fSourceViewer.getSelectionProvider());
		
		getSite().setSelectionProvider(fSourceViewer.getSelectionProvider());
		//PlatformUI.getWorkbench().getHelpSystem().setHelp(fSourceViewer.getTextWidget(), IJavaDebugHelpContextIds.DISPLAY_VIEW);
		//getSite().getWorkbenchWindow().addPerspectiveListener(this);
	}

	protected IDocument getRestoredDocument() {
		IDocument doc= null;
		if (fRestoredContents != null) {
			doc= new Document(fRestoredContents);
		} else {
			doc= new Document();
		}
		/*IDocumentPartitioner partitioner= tools.createDocumentPartitioner();
		partitioner.connect(doc);
		doc.setDocumentPartitioner(partitioner);*/
		/*fDocumentListener= new IDocumentListener() {
			public void documentAboutToBeChanged(DocumentEvent event) {
			}
			public void documentChanged(DocumentEvent event) {
				updateAction(ActionFactory.FIND.getId());
			}
		};
		doc.addDocumentListener(fDocumentListener);*/
		
		return doc;
	}
	
	public void setFocus() {
		if (fSourceViewer != null) {
			fSourceViewer.getControl().setFocus();
		}
	}
	
	/**
	 * Initialize the actions of this view
	 */
	protected void createActions() {
				
		executeAction = new ExecuteHQLAction(this);
		
		fClearDisplayAction= new ClearAction(fSourceViewer);

		IActionBars actionBars = getViewSite().getActionBars();		
		
		IAction action= new TextOperationAction(this, ITextOperationTarget.CUT);
		action.setText("Cut"); //$NON-NLS-1$
		//action.setToolTipText
		//action.setDescription
		setGlobalAction(actionBars, ActionFactory.CUT.getId(), action);
		
		action= new TextOperationAction(this, ITextOperationTarget.COPY);
		action.setText(Messages.popup_copy_text);  //$NON-NLS-1$
		setGlobalAction(actionBars, ActionFactory.COPY.getId(), action);
		
		action= new TextOperationAction(this, ITextOperationTarget.PASTE);
		action.setText(Messages.popup_paste_text); //$NON-NLS-1$
		setGlobalAction(actionBars, ActionFactory.PASTE.getId(), action);
		
		action= new TextOperationAction(this, ITextOperationTarget.SELECT_ALL);
		action.setText(Messages.popup_select_all); //$NON-NLS-1$
		setGlobalAction(actionBars, ActionFactory.SELECT_ALL.getId(), action);
		
		fSelectionActions.add(ActionFactory.CUT.getId());
		fSelectionActions.add(ActionFactory.COPY.getId());
		fSelectionActions.add(ActionFactory.PASTE.getId());
		
		fContentAssistAction= new TextOperationAction(this, ISourceViewer.CONTENTASSIST_PROPOSALS);
		fContentAssistAction.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
		fContentAssistAction.setText("Content assist"); //$NON-NLS-1$
		actionBars.updateActionBars();

		IHandler handler = new AbstractHandler() {
			public Object execute(Map parameterValuesByName) throws ExecutionException {
				fContentAssistAction.run();
				return null;
			}
			
		};
		IWorkbench workbench = PlatformUI.getWorkbench();
		
		IWorkbenchCommandSupport commandSupport = workbench.getCommandSupport();
		fSubmission = new HandlerSubmission(null, null, getSite(), ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS, handler, Priority.MEDIUM); //$NON-NLS-1$
		commandSupport.addHandlerSubmission(fSubmission);	

	}

	protected void setGlobalAction(IActionBars actionBars, String actionID, IAction action) {
		fGlobalActions.put(actionID, action);
		actionBars.setGlobalActionHandler(actionID, action);
	}

	/**
	 * Configures the toolBar.
	 */
	private void initializeToolBar() {
		IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
		//tbm.add(new Separator(IJavaDebugUIConstants.EVALUATION_GROUP));
		tbm.add(this.executeAction);
		tbm.add(fClearDisplayAction);

		getViewSite().getActionBars().updateActionBars();
	}

	/**
	 * Adds the context menu actions for the display view.
	 */
	protected void fillContextMenu(IMenuManager menu) {
		
		if (fSourceViewer.getDocument() == null) {
			return;
		} 
		//menu.add(new Separator(IJavaDebugUIConstants.EVALUATION_GROUP));
		//if (EvaluationContextManager.getEvaluationContext(this) != null) {
		//	menu.add(fContentAssistAction);
		//}
		menu.add(new Separator());		
		menu.add((IAction) fGlobalActions.get(ActionFactory.CUT.getId()));
		menu.add((IAction) fGlobalActions.get(ActionFactory.COPY.getId()));
		menu.add((IAction) fGlobalActions.get(ActionFactory.PASTE.getId()));
		menu.add((IAction) fGlobalActions.get(ActionFactory.SELECT_ALL.getId()));
		menu.add(new Separator());
		//menu.add((IAction) fGlobalActions.get(ActionFactory.FIND.getId()));
		menu.add(fClearDisplayAction);
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#getAdapter(Class)
	 */
	public Object getAdapter(Class required) {
			
		if (ITextOperationTarget.class.equals(required)) {
			return fSourceViewer.getTextOperationTarget();
		}
		
		/*if (IFindReplaceTarget.class.equals(required)) {
			return fSourceViewer.getFindReplaceTarget();
		}*/
			
		/*if (IDataDisplay.class.equals(required)) {
			return fDataDisplay;
		}*/
		if (ITextViewer.class.equals(required)) {
			return fSourceViewer;
		}
		
		return super.getAdapter(required);
	}
	
	protected void updateActions() {
		Iterator iterator = fSelectionActions.iterator();
		while (iterator.hasNext()) {
			IAction action = (IAction) fGlobalActions.get(iterator.next());
			if (action instanceof IUpdate) {
				 ((IUpdate) action).update();
			}
		}
	}		
	
	/**
	 * Saves the contents of the display view and the formatting.
	 * 
	 * @see org.eclipse.ui.IViewPart#saveState(IMemento)
	 */
	public void saveState(IMemento memento) {
		if (fSourceViewer != null) {
		    String contents= getContents();
		    if (contents != null) {
		        memento.putTextData(contents);
		    }
		} else if (fRestoredContents != null) {
			memento.putTextData(fRestoredContents);
		}
	}
	
	/**
	 * Restores the contents of the display view and the formatting.
	 * 
	 * @see org.eclipse.ui.IViewPart#init(IViewSite, IMemento)
	 */
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		init(site);
		if (fgMemento != null) {
			memento= fgMemento;
		}
		if (memento != null) {
			fRestoredContents= memento.getTextData();
		}
	}
	
	/**
	 * Returns the entire trimmed contents of the current document.
	 * If the contents are "empty" <code>null</code> is returned.
	 */
	private String getContents() {
	    if (fSourceViewer != null) {
			IDocument doc= fSourceViewer.getDocument();
			if (doc != null) {
				String contents= doc.get().trim();
				if (contents.length() > 0) {
				    return contents;
				}
			}
	    }
	    return null;
	}
	
	protected final ISelectionChangedListener getSelectionChangedListener() {
		return new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					updateSelectionDependentActions();
				}
			};
	}
	
	protected void updateSelectionDependentActions() {
		Iterator iterator= fSelectionActions.iterator();
		while (iterator.hasNext())
			updateAction((String)iterator.next());		
	}


	protected void updateAction(String actionId) {
		IAction action= (IAction)fGlobalActions.get(actionId);
		if (action instanceof IUpdate) {
			((IUpdate) action).update();
		}
	}
	/**
	 * @see ITextInputListener#inputDocumentAboutToBeChanged(IDocument, IDocument)
	 */
	public void inputDocumentAboutToBeChanged(IDocument oldInput, IDocument newInput) {
	}

	/**
	 * @see ITextInputListener#inputDocumentChanged(IDocument, IDocument)
	 */
	public void inputDocumentChanged(IDocument oldInput, IDocument newInput) {
		oldInput.removeDocumentListener(fDocumentListener);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose() {
		//getSite().getWorkbenchWindow().removePerspectiveListener(this);
		/*if (fSourceViewer != null) {
			fSourceViewer.dispose();
		}*/
		
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchCommandSupport commandSupport = workbench.getCommandSupport();
		commandSupport.removeHandlerSubmission(fSubmission);
		
		super.dispose();
	}


}