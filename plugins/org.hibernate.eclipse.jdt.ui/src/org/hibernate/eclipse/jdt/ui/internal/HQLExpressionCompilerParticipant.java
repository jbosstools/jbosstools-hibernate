package org.hibernate.eclipse.jdt.ui.internal;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.compiler.BuildContext;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.CompilationParticipant;
import org.eclipse.jdt.core.compiler.ReconcileContext;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.hibernate.eclipse.nature.HibernateNature;

public class HQLExpressionCompilerParticipant extends CompilationParticipant {

	public HQLExpressionCompilerParticipant() {
 
	}

	protected CompilationUnit parse(ICompilationUnit unit) {
		ASTParser parser = ASTParser.newParser(AST.JLS3); 
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		parser.setSource(unit); 
		parser.setResolveBindings(false); 
		return (CompilationUnit) parser.createAST(null /* IProgressMonitor */); // parse
	}
	
	public void buildStarting(BuildContext[] files, boolean isBatch) {		
		for (int i = 0; i < files.length; i++) {
			BuildContext context = files[i];
			ConsoleConfiguration consoleConfiguration = getConsoleConfiguration( ProjectUtils.findJavaProject( context.getFile().getProject().getName() ) );
			if(consoleConfiguration!=null && consoleConfiguration.isSessionFactoryCreated()) {
				ASTParser parser = ASTParser.newParser( AST.JLS3 );
				parser.setKind( ASTParser.K_COMPILATION_UNIT );
				parser.setSource( context.getContents() );
				parser.setResolveBindings( false );
				ASTNode node = parser.createAST( null );
				CompilationUnit cu = null;
				if(node instanceof CompilationUnit) {
					cu = (CompilationUnit) node;
				}				
				HQLDetector hqlDetector = new HQLDetector(cu, consoleConfiguration, context.getFile());
				node.accept(hqlDetector);
				if(!hqlDetector.getProblems().isEmpty()) {
					CategorizedProblem[] toArray = hqlDetector.getProblems().toArray( new CategorizedProblem[0] );
					context.recordNewProblems( toArray );
				}
			}
		}
	}
	
	public boolean isActive(IJavaProject project) {
		return HibernateNature.getHibernateNature( project ) != null;
	}
	
	public void reconcile(ReconcileContext context) {
		// TODO: disabled reconilation for now to avoid too many parses/overhead.
		/*
		ICompilationUnit workingCopy = context.getWorkingCopy();
		ConsoleConfiguration consoleConfiguration = getConsoleConfiguration( workingCopy.getJavaProject() );
		if(consoleConfiguration==null || !consoleConfiguration.isSessionFactoryCreated()) {
			// TODO: complain it aint there.
		} else {
			CompilationUnit parse = parse( workingCopy );
			HQLDetector hqlDetector = new HQLDetector(parse, consoleConfiguration, workingCopy.getResource());			
			parse.accept( hqlDetector );
			if(!hqlDetector.getProblems().isEmpty()) {
				CategorizedProblem[] toArray = (CategorizedProblem[]) hqlDetector.getProblems().toArray( new CategorizedProblem[0] );
				context.putProblems( Activator.HQL_SYNTAX_PROBLEM, toArray );
			}
		}*/
		
		
	}
	
	
	private ConsoleConfiguration getConsoleConfiguration(IJavaProject project) {
		return getConsoleConfiguration(HibernateNature.getHibernateNature( project ));
	}
	
	static ConsoleConfiguration getConsoleConfiguration(HibernateNature hibernateNature) {

		if(hibernateNature!=null) {
			return hibernateNature.getDefaultConsoleConfiguration();
		} else {
			return null;
		}
	}
	
	public void cleanStarting(IJavaProject javaProject){
	/*	IProject p = javaProject.getProject();

		//HibernateNature.getHibernateNature( javaProject);
		try{
			// clear out all markers during a clean.
			IMarker[] markers = p.findMarkers(Activator.HQL_SYNTAX_PROBLEM, true, IResource.DEPTH_INFINITE);
			if( markers != null ){
				for (int i = 0; i < markers.length; i++) {
					markers[i].delete();
				}				
			}
		}
		catch(CoreException e){
			HibernateConsolePlugin.getDefault().logErrorMessage( "Unable to delete batch hql markers", e); 
		}*/
		super.cleanStarting( javaProject );
	}

	
}
