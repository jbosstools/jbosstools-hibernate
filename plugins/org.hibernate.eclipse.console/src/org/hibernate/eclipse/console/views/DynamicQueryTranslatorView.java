package org.hibernate.eclipse.console.views;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.hibernate.SessionFactory;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.execution.ExecutionContext;
import org.hibernate.eclipse.console.utils.QLFormatHelper;
import org.hibernate.eclipse.hqleditor.HQLEditor;
import org.hibernate.eclipse.hqleditor.HQLSourceViewer;
import org.hibernate.eclipse.hqleditor.HQLSourceViewerConfiguration;
import org.hibernate.hql.QueryTranslator;
import org.hibernate.impl.SessionFactoryImpl;
import org.hibernate.type.Type;
import org.hibernate.util.StringHelper;

public class DynamicQueryTranslatorView extends ViewPart {

	private IPartListener2 partListener = new IPartListener2() {
	
		public void partInputChanged(IWorkbenchPartReference partRef) {
						
		}
	
		public void partVisible(IWorkbenchPartReference partRef) {
			
		}
	
		public void partHidden(IWorkbenchPartReference partRef) { 
			
		}
	
		public void partOpened(IWorkbenchPartReference partRef) {
			
		}
	
		public void partDeactivated(IWorkbenchPartReference partRef) {
			
		}
	
		public void partClosed(IWorkbenchPartReference partRef) {
			if(partRef.getPart(false)==currentEditor) {
				setCurrentEditor(null);
			}
		}
	
		public void partBroughtToTop(IWorkbenchPartReference partRef) {
			hookIntoEditor(partRef);
		}
	
		public void partActivated(IWorkbenchPartReference partRef) {
			hookIntoEditor( partRef );			
		}

		

	};
	
	private SourceViewer textViewer;
	private HQLEditor currentEditor;
    private MonoReconciler reconciler;
	
    private void hookIntoEditor(IWorkbenchPartReference partRef) {
    	if(partRef==null) setCurrentEditor(null);
		IWorkbenchPart part = partRef.getPart(false);
		if(part!=null && (part instanceof HQLEditor)) {
			setCurrentEditor((HQLEditor) part);
		} 
	}
	private void setCurrentEditor(HQLEditor editor) {
		if(editor==currentEditor) return;
		if(currentEditor!=null) {			
			reconciler.uninstall();
		}
		
		currentEditor = editor;
		
		if(currentEditor!=null) {
			ITextViewer editorViewer = currentEditor.getTextViewer();
			reconciler.install(editorViewer);	
		}
		
		updateText(currentEditor);
		
	}

	private void updateText(HQLEditor editor) {
		if(textViewer!=null && textViewer.getDocument()!=null) {
			if(editor!=null) {
				ConsoleConfiguration consoleConfiguration = editor.getConsoleConfiguration();
				if(consoleConfiguration!=null) {
					if(consoleConfiguration.isSessionFactoryCreated()) {
						textViewer.getDocument().set(generateSQL(consoleConfiguration.getExecutionContext(), consoleConfiguration.getSessionFactory(), editor.getQuery()));
					} else {
						textViewer.getDocument().set("Session factory not created for configuration: " + consoleConfiguration.getName());
					}
				} else {
					textViewer.getDocument().set("No Console configuration associated with HQL Editor");
				}
			} else {
				textViewer.getDocument().set("No HQL Query editor");
			}		
		}
	}

	public String generateSQL(ExecutionContext context, final SessionFactory sf, final String query) {

		if(StringHelper.isEmpty(query)) return "";
		
		String result;
		
		result = (String) context.execute(new ExecutionContext.Command() {
			public Object execute() {
				try {
					SessionFactoryImpl sfimpl = (SessionFactoryImpl) sf; // hack - to get to the actual queries..
					StringBuffer str = new StringBuffer(256);
					QueryTranslator[] translators = sfimpl.getQuery(query, false, Collections.EMPTY_MAP);
					for (int i = 0; i < translators.length; i++) {
						QueryTranslator translator = translators[i];
						Type[] returnTypes = translator.getReturnTypes();						
						str.append("SQL #" + i + " types: ");
						for (int j = 0; j < returnTypes.length; j++) {
							Type returnType = returnTypes[j];
							str.append(returnType.getName());
							if(j<returnTypes.length-1) { str.append(", "); }							
						}
						str.append("\n-----------------\n");
						
						str.append(QLFormatHelper.formatForScreen(translator.getSQLString()));
						str.append("\n\n");
					}
					return str.toString();
				} catch(Throwable t) {
					StringWriter sw = new StringWriter();
					t.printStackTrace(new PrintWriter(sw));					
					return sw.getBuffer().toString();
				}
				
			}
		});
		
		return result;
	}

	public void createPartControl(Composite parent) {
		textViewer = new HQLSourceViewer( parent, new VerticalRuler(1), null, false, SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL );
		//textViewer.setEditable(false);
		textViewer.setDocument( new Document() );
		textViewer.getDocument().set("No HQL Query editor selected");
		textViewer.configure(new HQLSourceViewerConfiguration(null));
		
		IWorkbenchWindow window = PlatformUI.getWorkbench()
		.getActiveWorkbenchWindow();
		IPartService service = window.getPartService();

		hookIntoEditor(service.getActivePartReference());
	}

	public void init(IViewSite site) throws PartInitException {
		super.init(site);
	
		IReconcilingStrategy strategy = new AbstractReconcilingStrategy() {
			
			protected void doReconcile(final IDocument doc) {
				Display display = PlatformUI.getWorkbench().getDisplay();
				display.asyncExec(new Runnable() {
				
					public void run() {
						//textViewer.getDocument().set(doc.get());
						updateText(currentEditor);
					}				
				});				
			}
		
		};

		reconciler = new MonoReconciler(strategy,false);
		reconciler.setDelay(500);		
	
		IWorkbenchWindow window = PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow();
		IPartService service = window.getPartService();
		service.addPartListener(partListener);
		
		hookIntoEditor(service.getActivePartReference());
			
	}
	
	public void dispose() {
		IWorkbenchWindow window = PlatformUI.getWorkbench()
			.getActiveWorkbenchWindow();
		IPartService service = window.getPartService();		
		service.removePartListener(partListener);
		super.dispose();		
	}
	
	public void setFocus() {

	}
}
