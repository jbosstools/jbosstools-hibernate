package org.hibernate.eclipse.hqleditor;

import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IShowEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.ui.texteditor.DefaultRangeIndicator;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.Messages;


/**
 * HQL Editor
 */
public class HQLEditor extends TextEditor implements IPropertyChangeListener, IShowEditorInput {
	public static final String PLUGIN_NAME = HibernateConsolePlugin.ID;
	public static final String HELP_CONTEXT_ID = PLUGIN_NAME + ".hqleditorhelp"; //$NON-NLS-1$
	
    /** The HQL code scanner, which is used for colorizing the edit text. */
    private HQLCodeScanner fHQLCodeScanner;
    /** The document setup participant object, which is used partition the edit text. */
    private HQLEditorDocumentSetupParticipant fDocSetupParticipant;
    /** The projection (code folding) support object. */
    private ProjectionSupport fProjectionSupport;
    
    /**
     * Constructs an instance of this class. This is the default constructor.
     */
    public HQLEditor() {
        super();
    }

    /**
     * Creates and installs the editor actions.
     * 
     * @see org.eclipse.ui.texteditor.AbstractTextEditor#createActions()
     */
    protected void createActions() {
        super.createActions();
        ResourceBundle bundle = getResourceBundle();

        IAction a = new TextOperationAction( bundle,
                "ContentAssistProposal.", this, ISourceViewer.CONTENTASSIST_PROPOSALS ); //$NON-NLS-1$
        a.setActionDefinitionId( ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS );
        setAction( "ContentAssistProposal", a ); //$NON-NLS-1$

        a = new TextOperationAction( bundle, "ContentAssistTip.", this, ISourceViewer.CONTENTASSIST_CONTEXT_INFORMATION ); //$NON-NLS-1$
        a.setActionDefinitionId( ITextEditorActionDefinitionIds.CONTENT_ASSIST_CONTEXT_INFORMATION );
        setAction( "ContentAssistTip", a ); //$NON-NLS-1$

        a = new TextOperationAction( bundle, "ContentFormat.", this, ISourceViewer.FORMAT ); //$NON-NLS-1$
        setAction( "ContentFormat", a ); //$NON-NLS-1$

        /*a = new HQLConnectAction( bundle, "HQLEditor.connectAction." ); //$NON-NLS-1$
        setAction( "HQLEditor.connectAction", a ); //$NON-NLS-1$
        */

        /*a = new HQLDisconnectAction( bundle, "HQLEditor.disconnectAction." ); //$NON-NLS-1$
        setAction( "HQLEditor.disconnectAction", a ); //$NON-NLS-1$
        */
        
//        a = new ExecuteHQLAction( this ); //$NON-NLS-1$
//        setAction( "HQLEditor.runAction", a ); //$NON-NLS-1$
        
        /*a = new HQLSetStatementTerminatorAction( bundle, "HQLEditor.setStatementTerminatorAction." ); //$NON-NLS-1$
        setAction( "HQLEditor.setStatementTerminatorAction", a ); //$NON-NLS-1$
        */
    }

	private ResourceBundle getResourceBundle() {
		return ResourceBundle.getBundle( Messages.BUNDLE_NAME );
	}

	/**
     * Creates the SWT controls for the editor.
     * 
     * @see org.eclipse.ui.texteditor.AbstractTextEditor#createPartControl(org.eclipse.swt.widgets.Composite)
     */
    public void createPartControl( Composite parent ) {
        super.createPartControl( parent );
        setProjectionSupport( createProjectionSupport() );
        
        /* Now that we have enabled source folding, make sure everything is
         * expanded.
         */
        ProjectionViewer viewer = (ProjectionViewer) getSourceViewer();
        viewer.doOperation( ProjectionViewer.TOGGLE );
        
        /* Set a help context ID to enable F1 help. */
        WorkbenchHelp.setHelp( parent, HELP_CONTEXT_ID );
               
    }

    /**
     * Creates, configures, and returns a <code>ProjectionSupport</code>
     * object for this editor.
     * 
     * @return the <code>ProjectSupport</code> object to use with this editor
     */
    protected ProjectionSupport createProjectionSupport() {
        ProjectionViewer viewer = (ProjectionViewer) getSourceViewer();
        ProjectionSupport projSupport = new ProjectionSupport( viewer, getAnnotationAccess(), getSharedColors() );
        projSupport.addSummarizableAnnotationType( "org.eclipse.ui.workbench.texteditor.error" ); //$NON-NLS-1$
        projSupport.addSummarizableAnnotationType( "org.eclipse.ui.workbench.texteditor.warning" ); //$NON-NLS-1$
        projSupport.install();

        return projSupport;
    }

    /**
     * Creates the source viewer to be used by this editor.
     * 
     * @see org.eclipse.ui.texteditor.AbstractTextEditor#createSourceViewer(org.eclipse.swt.widgets.Composite,
     *      org.eclipse.jface.text.source.IVerticalRuler, int)
     */
    protected ISourceViewer createSourceViewer( Composite parent, IVerticalRuler ruler, int styles ) {
        HQLSourceViewer viewer = new HQLSourceViewer( parent, ruler, getOverviewRuler(), isOverviewRulerVisible(),
                styles );

        return viewer;
    }

    /**
     * Creates the source viewer configuation to be used by this editor.
     * 
     * @return the new source viewer configuration object
     */
    protected HQLSourceViewerConfiguration createSourceViewerConfiguration() {
        HQLSourceViewerConfiguration config = new HQLSourceViewerConfiguration( this );
        
        return config;
    }

    /**
     * Dispose of resources held by this editor.
     * 
     * @see IWorkbenchPart#dispose()
     */
    public void dispose() {
        super.dispose();
    }

    /**
     * Abandons all modifications applied to this text editor's input element's
     * textual presentation since the last save operation.
     * 
     * @see ITextEditor#doRevertToSaved()
     */
    public void doRevertToSaved() {
        super.doRevertToSaved();
        //updateOutlinePage();
    }

 
    /**
     * Sets the input of the outline page after this class has set input.
     * 
     * @param input the new input for the editor
     * @see org.eclipse.ui.editors.text.TextEditor#doSetInput(org.eclipse.ui.IEditorInput)
     */
    public void doSetInput( IEditorInput input ) throws CoreException {
        super.doSetInput( input );

        /* Make sure the document partitioner is set up. The document setup
         * participant sets up document partitioning, which is used for text
         * colorizing and other text features.
         */
        IDocumentProvider docProvider = this.getDocumentProvider();
        if (docProvider != null) {
            IDocument doc = docProvider.getDocument( input );
            if (doc != null) {
                HQLEditorDocumentSetupParticipant docSetupParticipant = getDocumentSetupParticipant();
                docSetupParticipant.setup( doc );
            }
        }

        /* Determine if our input object is an instance of IHQLEditorInput.  If so,
         * get all the information that it contains.
         */
        /*ConnectionInfo connInfo = null;
        Database db = null;
        String defaultSchemaName = null;
        if (input instanceof IHQLEditorInput) {
            IHQLEditorInput hqlInput = (IHQLEditorInput) input;
            connInfo = hqlInput.getConnectionInfo();
            db = sqlInput.getDatabase();
            defaultSchemaName = sqlInput.getDefaultSchemaName();
        }*/
        
        /* If we didn't get a database but we did get a connection, try to get
         * the database from the connection.
         */
        /*if (connInfo != null && db == null) {
            db = connInfo.getSharedDatabase();
        }*/
        
        /* Save away the connection and the database information. */
        //setConnectionInfo( connInfo );
        //setDatabase( db );
        //setDefaultSchemaName( defaultSchemaName );

        /* Show the connection status in the status area at the bottom of the
         * workbench window.
         */
        //refreshConnectionStatus();
        
        /* Pass the input along to the outline page. */
        //HQLEditorContentOutlinePage outlinePage = getOutlinePage();
        //if (outlinePage != null) {
        //    outlinePage.setInput( input );
        //}
    }

    /**
     * Sets up this editor's context menu before it is made visible.
     * 
     * @see org.eclipse.ui.texteditor.AbstractTextEditor#editorContextMenuAboutToShow(org.eclipse.jface.action.IMenuManager)
     */
    protected void editorContextMenuAboutToShow( IMenuManager menu ) {
        super.editorContextMenuAboutToShow( menu );

        menu.add( new Separator() );
        addAction( menu, "ContentAssistProposal" ); //$NON-NLS-1$
        addAction( menu, "ContentAssistTip" ); //$NON-NLS-1$
        addAction( menu, "ContentFormat" ); //$NON-NLS-1$

        menu.add( new Separator() );
        /*if (getConnectionInfo() == null) {
            addAction( menu, "HQLEditor.connectAction" ); //$NON-NLS-1$
        }
        else {
            addAction( menu, "HQLEditor.disconnectAction" ); //$NON-NLS-1$
        }*/
        addAction( menu, "HQLEditor.runAction" ); //$NON-NLS-1$
        //addAction( menu, "HQLEditor.setStatementTerminatorAction" ); //$NON-NLS-1$
    }

    /**
     * Gets an adapter for the given class. Returns the HQL content outline page
     * if the get request is for an outline page. Otherwise returns a projection
     * adapter if one hasn't already been created.
     * 
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     * @see org.eclipse.jface.text.source.projection.ProjectionSupport#getAdapter(org.eclipse.jface.text.source.ISourceViewer,
     *      java.lang.Class)
     */
    public Object getAdapter( Class classForWhichAdapterNeeded ) {
        Object adapter = null;

        /* Get and return the content outline page, if that's what's requested. */
      if (IContentOutlinePage.class.equals( classForWhichAdapterNeeded )) {
//            HQLEditorContentOutlinePage outlinePage = getOutlinePage();
//            if (outlinePage == null) {
//                outlinePage = createContentOutlinePage();
//                setOutlinePage( outlinePage );
//                if (getEditorInput() != null) {
//                    outlinePage.setInput( getEditorInput() );
//                }
//            }
  //          adapter = outlinePage;
    	  adapter = null;
        }
        /* Delegate getting the adapter to the projection support object,
         * if there is one. Projection refers to the ability to visibly collapse
         * and expand sections of the document.
         */
        else if (adapter == null) {
            ProjectionSupport projSupport = getProjectionSupport();
            if (projSupport != null) {
                adapter = projSupport.getAdapter( getSourceViewer(), classForWhichAdapterNeeded );
            }
        }

        /* If we still don't have an adapter let the superclass handle it. */
        if (adapter == null) {
            adapter = super.getAdapter( classForWhichAdapterNeeded );
        }

        return adapter;
    }
    
    /**
     * Gets the document setup participant object associated with this editor.
     * The setup participant sets the partitioning type for the document.
     * 
     * @return the current document setup participant
     */
    public HQLEditorDocumentSetupParticipant getDocumentSetupParticipant() {
        if (fDocSetupParticipant == null) {
            fDocSetupParticipant = new HQLEditorDocumentSetupParticipant();
        }
        return fDocSetupParticipant;
    }


    /**
     * Gets the <code>ProjectionSupport</code> object associated with this
     * editor.
     * 
     * @return the current <code>ProjectionSupport</code> object
     */
    protected ProjectionSupport getProjectionSupport() {
        return fProjectionSupport;
    }


    /**
     * Gets the HQL source code text scanner. Creates a default one if it
     * doesn't exist yet.
     * 
     * @return the HQL source code text scanner
     */
    public HQLCodeScanner getHQLCodeScanner() {
        if (fHQLCodeScanner == null) {
            fHQLCodeScanner = new HQLCodeScanner( getHQLColorProvider() );
        }
        return fHQLCodeScanner;
    }


    /**
     * Gets the color provider for colorizing HQL source code.
     * 
     * @return the HQL color provider
     */
    public HQLColorProvider getHQLColorProvider() {
        return new HQLColorProvider();
    }

    /**
     * Initializes the editor.
     * 
     * @see org.eclipse.ui.editors.text.TextEditor#initializeEditor()
     */
    protected void initializeEditor() {
        super.initializeEditor();
        setSourceViewerConfiguration( createSourceViewerConfiguration() );
        setRangeIndicator( new DefaultRangeIndicator() );
    }
    
    /**
     * Handles notifications to the object that a property has changed.
     * 
     * @param event the property change event object describing which property
     *            changed and how
     */
    public void propertyChange( PropertyChangeEvent event ) {
        /*if (event.getProperty().equals( HQLConnectAction.CONNECTION )) {
            ConnectionInfo connInfo = (ConnectionInfo) event.getNewValue();
            setConnectionInfo( connInfo );
            refreshConnectionStatus();
        }*/
    }

  
    /**
     * Sets the document setup participant object associated with this editor to
     * the given object. The setup participant sets the partitioning type for
     * the document.
     * 
     * @return the current document setup participant
     */
    public void setDocumentSetupParticipant( HQLEditorDocumentSetupParticipant docSetupParticipant ) {
        fDocSetupParticipant = docSetupParticipant;
    }

    /**
     * Sets the <code>ProjectionSupport</code> object associated with this
     * editor.
     * 
     * @param projSupport the <code>ProjectionSupport</code> object to use
     */
    protected void setProjectionSupport( ProjectionSupport projSupport ) {
        fProjectionSupport = projSupport;
    }

	public void showEditorInput(IEditorInput editorInput) {
		
			try {
				doSetInput(editorInput);
			}
			catch (CoreException e) {
				e.printStackTrace();
			}
			
		
	}

	public String getQuery() {		
        IEditorInput editorInput = getEditorInput();
        IDocumentProvider docProvider = getDocumentProvider();
        IDocument doc = docProvider.getDocument( editorInput );
        return doc.get();
	}

   public void doSave(IProgressMonitor progressMonitor) {
	   HQLEditorInput hei = (HQLEditorInput)getEditorInput();
	   hei.setQuery(getQuery());
   }

   public ConsoleConfiguration getConsoleConfiguration() {
	   HQLEditorInput hei = (HQLEditorInput)getEditorInput();
	   return KnownConfigurations.getInstance().find(hei.getConsoleConfigurationName());
   }

   public boolean askUserForConfiguration(String name) {
	   return MessageDialog.openQuestion(HibernateConsolePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), "Open Session factory", "Do you want to open the session factory for " + name + " ?");        
   }

   public ITextViewer getTextViewer() {
	   return getSourceViewer();
   }
   
   protected void initializeKeyBindingScopes() {
       setKeyBindingScopes(new String[] { "org.hibernate.eclipse.console.hql" });  //$NON-NLS-1$
   }
} 
