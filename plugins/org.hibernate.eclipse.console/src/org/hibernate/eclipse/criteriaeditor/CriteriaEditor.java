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

import java.util.HashSet;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.eval.IEvaluationContext;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.text.java.CompletionProposalCollector;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.source.ISourceViewer;
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
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.hibernate.cfg.Configuration;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.AbstractQueryEditor;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.views.QueryPageTabView;
import org.hibernate.mapping.PersistentClass;

public class CriteriaEditor extends AbstractQueryEditor {

	private CriteriaEditorDocumentSetupParticipant docSetupParticipant;

	public CriteriaEditor() {
		super();
		//setDocumentProvider(JDIDebugUIPlugin.getDefault().getSnippetDocumentProvider());
		// TODO: setup document
		// JavaTextTools tools= JDIDebugUIPlugin.getDefault().getJavaTextTools();
		//tools.setupJavaDocumentPartitioner(document, IJavaPartitions.JAVA_PARTITIONING);

		IPreferenceStore store = new ChainedPreferenceStore(new IPreferenceStore[] {
				PreferenceConstants.getPreferenceStore(),
				EditorsUI.getPreferenceStore()});

		setSourceViewerConfiguration(new JavaViewerConfiguration(HibernateConsolePlugin.getDefault().getJavaTextTools(), store, this));
		setPreferenceStore(store);
		setEditorContextMenuId("#CriteraEditorContext"); //$NON-NLS-1$
		setRulerContextMenuId("#CriteraRulerContext"); //$NON-NLS-1$

	}

	protected void createActions() {
		super.createActions();
		Action action = new TextOperationAction(getResourceBundle(), "HQLEditor_ContentAssistProposal_", this, ISourceViewer.CONTENTASSIST_PROPOSALS); //$NON-NLS-1$
		action.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
		setAction("ContentAssistProposal", action);//$NON-NLS-1$

	}

	private ResourceBundle getResourceBundle() {
		return ResourceBundle.getBundle( HibernateConsoleMessages.BUNDLE_NAME );
	}


	protected void doSetInput(IEditorInput input) throws CoreException {

		super.doSetInput( input );

		/* Make sure the document partitioner is set up. The document setup
         * participant sets up document partitioning, which is used for text
         * colorizing and other text features.
         */
        IDocumentProvider docProvider = this.getDocumentProvider();
        if (docProvider != null) {
            IDocument doc = docProvider.getDocument( input );
            if (doc != null) {
                CriteriaEditorDocumentSetupParticipant docSetupParticipant = getDocumentSetupParticipant();
                docSetupParticipant.setup( doc );
            }
        }

	}

	private CriteriaEditorDocumentSetupParticipant getDocumentSetupParticipant() {
        if (docSetupParticipant == null) {
            docSetupParticipant = new CriteriaEditorDocumentSetupParticipant();
        }
        return docSetupParticipant;
    }

	public void executeQuery(ConsoleConfiguration cfg) {
		final IWorkbenchPage activePage = getEditorSite().getPage();
		try {
			activePage.showView(QueryPageTabView.ID);
		} catch (PartInitException e) {
			// ignore
		}
		
		cfg.executeBSHQuery(getQueryString(), getQueryInputModel().getCopyForQuery() );
	}

	protected IEvaluationContext getEvaluationContext(IJavaProject project) {
		IEvaluationContext evalCtx = null ;
		if (project != null) {
			evalCtx = project.newEvaluationContext();
		}
		if (evalCtx  != null) {
			evalCtx.setImports(getImports());
		}
		return evalCtx ;
	}

	@SuppressWarnings("unchecked")
	private String[] getImports() {

		final ConsoleConfiguration consoleConfiguration = getConsoleConfiguration();

		Set<String> imports = new HashSet<String>();
		Configuration configuration = consoleConfiguration.getConfiguration();
		if(configuration!=null) {
			Iterator<PersistentClass> classMappings = configuration.getClassMappings();
			while ( classMappings.hasNext() ) {
				PersistentClass clazz = classMappings.next();
				String className = clazz.getClassName();
				if(className!=null) {
					imports.add( className );
				}
			}
		}

		imports.add("org.hibernate.*"); //$NON-NLS-1$
		imports.add("org.hibernate.criterion.*"); //$NON-NLS-1$

		return imports.toArray( new String[imports.size()] );
	}

	public void codeComplete(String prefix, CompletionProposalCollector collector, int position, IJavaProject project) throws JavaModelException {
		String code= getSourceViewer().getDocument().get();
		code= prefix + code;
		IEvaluationContext e= getEvaluationContext(project);
		if (e != null) {
			e.codeComplete(code, prefix.length()+position, collector);
		}
	}


	public void showEditorInput(IEditorInput editorInput) {

		if (!(getEditorInput() instanceof CriteriaEditorInput)) {
			super.showEditorInput( editorInput );
			return;
		}
		CriteriaEditorInput hei = (CriteriaEditorInput)getEditorInput();
		super.showEditorInput( editorInput );
		IStorage storage = ((CriteriaEditorInput)editorInput).getStorage();
		if (storage instanceof CriteriaEditorStorage) {
			CriteriaEditorStorage sqlEditorStorage = (CriteriaEditorStorage) storage;
			IDocument document = getDocumentProvider().getDocument( hei );
			if (document.get().compareTo(sqlEditorStorage.getContentsString()) != 0) {
				document.set( sqlEditorStorage.getContentsString() );
			}
		}
	}


	public void createPartControl(Composite parent) {
		parent.setLayout( new GridLayout(1,false) );

    	createToolbar( parent );

		super.createPartControl( parent );
		if (getSourceViewer() != null ){
			getSourceViewer().addTextListener(new ITextListener(){

				public void textChanged(TextEvent event) {
					updateExecButton();
				}});
		}

		Control control = parent.getChildren()[1];
    	control.setLayoutData( new GridData( GridData.FILL_BOTH ) );

	    // the following is needed to make sure the editor area gets focus when editing after query execution
	    // TODO: find a better way since this is triggered on every mouse click and key stroke in the editor area
    	// one more remark: without this code -> JBIDE-4446
	    StyledText textWidget = getSourceViewer().getTextWidget();
		textWidget.addKeyListener(new KeyAdapter() {

			public void keyPressed(KeyEvent e) {
				getSite().getPage().activate(CriteriaEditor.this);
			}

		});
		textWidget.addMouseListener(new MouseAdapter() {

			public void mouseDown(MouseEvent e) {
				getSite().getPage().activate(CriteriaEditor.this);
			}

		});
		initTextAndToolTip(HibernateConsoleMessages.ExecuteQueryAction_run_criteria);
	}

	@Override
	protected String getConnectedImageFilePath() {
		return 	"icons/images/criteria_editor_connect.gif";		//$NON-NLS-1$
	}

	@Override
	protected String getSaveAsFileExtension() {
		return "*.crit";	//$NON-NLS-1$
	}
}
