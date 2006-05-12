package org.hibernate.eclipse.criteriaeditor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.eval.IEvaluationContext;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jdt.ui.text.JavaTextTools;
import org.eclipse.jdt.ui.text.java.CompletionProposalCollector;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.IEditorInput;
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

public class CriteriaEditor extends AbstractDecoratedTextEditor implements QueryEditor {

	final private QueryInputModel queryInputModel;
	
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
	
		IDocumentProvider docProvider = getDocumentProvider();
        IDocument doc = docProvider.getDocument( input );
        if(doc!=null) {
        	JavaTextTools tools= HibernateConsolePlugin.getDefault().getJavaTextTools();
        	tools.setupJavaDocumentPartitioner(doc, IJavaPartitions.JAVA_PARTITIONING);
        }
        
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
			e.codeComplete(code, prefix.length()-1+position, collector);
		}
	}
	

	public void doSave(IProgressMonitor progressMonitor) { 
		//super.doSave(progressMonitor);
		CriteriaEditorInput hei = (CriteriaEditorInput)getEditorInput();
		hei.setQuery(getQueryString());
		performSave(false, progressMonitor);
	}
	   
}
