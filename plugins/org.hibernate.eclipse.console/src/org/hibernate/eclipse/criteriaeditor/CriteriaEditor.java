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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.eval.IEvaluationContext;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.text.java.CompletionProposalCollector;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IShowEditorInput;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.hibernate.cfg.Configuration;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.QueryInputModel;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.Messages;
import org.hibernate.eclipse.console.QueryEditor;
import org.hibernate.mapping.PersistentClass;

public class CriteriaEditor extends AbstractDecoratedTextEditor implements QueryEditor, IShowEditorInput {

	final private QueryInputModel queryInputModel;
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
		
		queryInputModel = new QueryInputModel();
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

	public boolean askUserForConfiguration(String name) {
		return MessageDialog.openQuestion(HibernateConsolePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), "Open Session factory", "Do you want to open the session factory for " + name + " ?");        	
	}

	public ConsoleConfiguration getConsoleConfiguration() {
		CriteriaEditorInput hei = (CriteriaEditorInput)getEditorInput();
		return KnownConfigurations.getInstance().find(hei.getConsoleConfigurationName());
	}

	public QueryInputModel getQueryInputModel() {
		   return queryInputModel;
	}

	public String getQueryString() {		
        IEditorInput editorInput = getEditorInput();
        IDocumentProvider docProvider = getDocumentProvider();
        IDocument doc = docProvider.getDocument( editorInput );
        return doc.get();
	}

	protected void initializeKeyBindingScopes() {
		setKeyBindingScopes(new String[] { "org.hibernate.eclipse.console.hql" });  //$NON-NLS-1$
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
		cfg.executeBSHQuery(getQueryString(), getQueryInputModel().getQueryParametersForQuery() );
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
	
	private String[] getImports() {
		
		ConsoleConfiguration consoleConfiguration = getConsoleConfiguration();
		
		if(consoleConfiguration.getConfiguration()==null) {
			consoleConfiguration.build();
		} 
		
		Set imports = new HashSet();
		Configuration configuration = consoleConfiguration.getConfiguration();
		if(configuration!=null) {
			Iterator classMappings = configuration.getClassMappings();
			while ( classMappings.hasNext() ) {
				PersistentClass clazz = (PersistentClass) classMappings.next();
				String className = clazz.getClassName();
				if(className!=null) {
					imports.add( className );
				}				
			}
		}
		
		imports.add("org.hibernate.*");
		imports.add("org.hibernate.criterion.*");
		
		return (String[]) imports.toArray( new String[imports.size()] );
	}

	public void codeComplete(String prefix, CompletionProposalCollector collector, int position, IJavaProject project) throws JavaModelException {		
		ITextSelection selection= (ITextSelection)getSelectionProvider().getSelection();
		String code= getSourceViewer().getDocument().get();			
		code= prefix + code;
		IEvaluationContext e= getEvaluationContext(project);
		if (e != null) {
			e.codeComplete(code, prefix.length()+position, collector);
		}
	}
	

	public void doSave(IProgressMonitor progressMonitor) { 
		//super.doSave(progressMonitor);
		CriteriaEditorInput hei = (CriteriaEditorInput)getEditorInput();
		hei.setQuery(getQueryString());
		performSave(false, progressMonitor);
	}

	public void showEditorInput(IEditorInput editorInput) {
		
		CriteriaEditorInput hei = (CriteriaEditorInput)getEditorInput();
		
		IStorage storage = ((CriteriaEditorInput)editorInput).getStorage();
		 if (storage instanceof CriteriaEditorStorage) {
             CriteriaEditorStorage sqlEditorStorage = (CriteriaEditorStorage) storage;
             getDocumentProvider().getDocument( hei ).set( sqlEditorStorage.getContentsString() );             
         }		
	}

	

}
