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
package org.hibernate.eclipse.hqleditor;


import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.texteditor.DefaultRangeIndicator;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.hibernate.Session;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.QueryPage;
import org.hibernate.console.execution.ExecutionContext.Command;
import org.hibernate.eclipse.console.AbstractQueryEditor;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.views.IQueryParametersPage;
import org.hibernate.eclipse.console.views.QueryPageTabView;
import org.hibernate.eclipse.console.views.QueryParametersPage;


/**
 * HQL Editor
 */
public class HQLEditor extends AbstractQueryEditor {


	public static final String PLUGIN_NAME = HibernateConsolePlugin.ID;
	public static final String HELP_CONTEXT_ID = PLUGIN_NAME + ".hqleditorhelp"; //$NON-NLS-1$

	/** The HQL code scanner, which is used for colorizing the edit text. */
	private HQLCodeScanner fHQLCodeScanner;
	/** The document setup participant object, which is used partition the edit text. */
	private HQLEditorDocumentSetupParticipant docSetupParticipant;
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
	            "HQLEditor_ContentAssistProposal_", this, ISourceViewer.CONTENTASSIST_PROPOSALS ); //$NON-NLS-1$
	    a.setActionDefinitionId( ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS );
	    setAction( "ContentAssistProposal", a ); //$NON-NLS-1$

	    a = new TextOperationAction( bundle, "HQLEditor_ContentAssistTip_", this, ISourceViewer.CONTENTASSIST_CONTEXT_INFORMATION ); //$NON-NLS-1$
	    a.setActionDefinitionId( ITextEditorActionDefinitionIds.CONTENT_ASSIST_CONTEXT_INFORMATION );
	    setAction( "ContentAssistTip", a ); //$NON-NLS-1$

	    a = new TextOperationAction( bundle, "HQLEditor_ContentFormat_", this, ISourceViewer.FORMAT ); //$NON-NLS-1$
	    setAction( "ContentFormat", a ); //$NON-NLS-1$

	    /*a = new HQLConnectAction( bundle, "HQLEditor.connectAction." ); //$NON-NLS-1$
	    setAction( "HQLEditor.connectAction", a ); //$NON-NLS-1$
	    */

	    /*a = new HQLDisconnectAction( bundle, "HQLEditor.disconnectAction." ); //$NON-NLS-1$
	    setAction( "HQLEditor.disconnectAction", a ); //$NON-NLS-1$
	    */

	//        a = new ExecuteQueryAction( this ); //$NON-NLS-1$
	//        setAction( "HQLEditor.runAction", a ); //$NON-NLS-1$

	    /*a = new HQLSetStatementTerminatorAction( bundle, "HQLEditor.setStatementTerminatorAction." ); //$NON-NLS-1$
	    setAction( "HQLEditor.setStatementTerminatorAction", a ); //$NON-NLS-1$
	    */
	}

	private ResourceBundle getResourceBundle() {
		return ResourceBundle.getBundle( HibernateConsoleMessages.BUNDLE_NAME );
	}

	/**
	 * Creates the SWT controls for the editor.
	 *
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createPartControl( Composite parent ) {
		parent.setLayout( new GridLayout(1,false) );

		createToolbar(parent);

		super.createPartControl( parent );

		if (getSourceViewer() != null ){
			getSourceViewer().addTextListener(new ITextListener(){

				public void textChanged(TextEvent event) {
					updateExecButton();
				}});
		}

		// move to base class?
		Control control = parent.getChildren()[1];
		control.setLayoutData( new GridData( GridData.FILL_BOTH ) );

	    setProjectionSupport( createProjectionSupport() );

	    /* Now that we have enabled source folding, make sure everything is
	     * expanded.
	     */
	    ProjectionViewer viewer = (ProjectionViewer) getSourceViewer();
	    viewer.doOperation( ProjectionViewer.TOGGLE );

	    /* Set a help context ID to enable F1 help. */
	    getSite().getWorkbenchWindow().getWorkbench().getHelpSystem().setHelp( parent, HELP_CONTEXT_ID );

	    // the following is needed to make sure the editor area gets focus when editing after query execution
	    // TODO: find a better way since this is triggered on every mouse click and key stroke in the editor area
    	// one more remark: without this code -> JBIDE-4446
	    StyledText textWidget = getSourceViewer().getTextWidget();
		textWidget.addKeyListener(new KeyAdapter() {

			public void keyPressed(KeyEvent e) {
				getSite().getPage().activate(HQLEditor.this);
			}

		});
		textWidget.addMouseListener(new MouseAdapter() {

			public void mouseDown(MouseEvent e) {
				getSite().getPage().activate(HQLEditor.this);
			}

		});
		initTextAndToolTip(HibernateConsoleMessages.ExecuteQueryAction_run_hql);
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
	@SuppressWarnings("unchecked")
	public Object getAdapter( Class classForWhichAdapterNeeded ) {
	    Object adapter = null;

	    if(IQueryParametersPage.class.equals( classForWhichAdapterNeeded )) {
	    	return new QueryParametersPage(this);
	    }
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
	    if (docSetupParticipant == null) {
	        docSetupParticipant = new HQLEditorDocumentSetupParticipant();
	    }
	    return docSetupParticipant;
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
	public HQLColors getHQLColorProvider() {
	    return new HQLColors();
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
	 * Sets the document setup participant object associated with this editor to
	 * the given object. The setup participant sets the partitioning type for
	 * the document.
	 *
	 * @return the current document setup participant
	 */
	public void setDocumentSetupParticipant( HQLEditorDocumentSetupParticipant docSetupParticipant ) {
	    this.docSetupParticipant = docSetupParticipant;
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


	   public ITextViewer getTextViewer() {
		   return getSourceViewer();
	   }

	protected QueryPage queryPage = null;

	public void executeQuery(ConsoleConfiguration cfg) {
		final IWorkbenchPage activePage = getEditorSite().getPage();
		try {
			activePage.showView(QueryPageTabView.ID);
		} catch (PartInitException e) {	
			// ignore
		}
		if (queryPage == null || !getPinToOneResTab()) {
			queryPage = cfg.executeHQLQuery(getQueryString(), getQueryInputModel().getCopyForQuery());
		} else {
			final ConsoleConfiguration cfg0 = cfg;
			cfg.execute(new Command() {
				public Object execute() {
					KnownConfigurations.getInstance().getQueryPageModel().remove(queryPage);
					Session session = cfg0.getSessionFactory().openSession();
					queryPage.setModel(getQueryInputModel().getCopyForQuery());
					queryPage.setQueryString(getQueryString());
					queryPage.setSession(session);
					KnownConfigurations.getInstance().getQueryPageModel().add(queryPage);
					return null;
				}
			});
		}
	}

	@Override
	protected String getConnectedImageFilePath() {
		return 	"icons/images/hql_editor_connect.gif";		//$NON-NLS-1$
	}


	@Override
	protected String getSaveAsFileExtension() {
		return "*.hql";	//$NON-NLS-1$
	}
}
