package org.hibernate.eclipse.jdt.ui.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.engine.query.HQLQueryPlan;
import org.hibernate.impl.SessionFactoryImpl;

public class HQLDetector extends ASTVisitor {

	private final IFile resource;
	List problems = new ArrayList();
	private final ConsoleConfiguration consoleConfiguration;
	private final CompilationUnit cu;
	
	public HQLDetector(CompilationUnit cu, ConsoleConfiguration consoleConfiguration, IResource resource) {
		this.cu = cu;
		this.consoleConfiguration = consoleConfiguration;
		this.resource = (IFile) resource;
	}

	public boolean visit(NormalAnnotation node) {
		if(node.getTypeName().getFullyQualifiedName().endsWith( "NamedQuery" )) {
			Iterator iterator = node.values().iterator();
			while ( iterator.hasNext() ) {
				MemberValuePair element = (MemberValuePair) iterator.next();
				if(element.getName().getIdentifier().equals("query")) {
					Expression value = element.getValue();
					if(value instanceof StringLiteral) {
						StringLiteral sl = (StringLiteral)value;
						try {
							checkQuery( consoleConfiguration, sl.getLiteralValue() );
						} catch(RuntimeException re) {
							problems.add(new HQLProblem(re.getLocalizedMessage(), true, resource, sl.getStartPosition(), sl.getStartPosition()+sl.getLength()-1, getLineNumber(sl.getStartPosition())));
						}
					}
				}
				
			}
		} 
		return super.visit( node );
	}
	
	private int getLineNumber(int startPosition) {
		if(cu!=null) {
			return cu.getLineNumber( startPosition );
		} else {
			return 0;
		}
	}

	public boolean visit(MarkerAnnotation node) {
		// TODO Auto-generated method stub
		return super.visit( node );
	}
	
	public boolean visit(SingleMemberAnnotation node) {
		// TODO Auto-generated method stub
		return super.visit( node );
	}	
	
	public boolean visit(MethodInvocation node) {	
		if(node.getName().getIdentifier().equals( "createQuery" )) {
			if(node.arguments().size()==1) {
				Object object = node.arguments().get(0);
				if(object instanceof StringLiteral) {
					StringLiteral sl = (StringLiteral) object;
					String literalValue = sl.getLiteralValue();
					try {
						checkQuery( consoleConfiguration, literalValue );
					} catch(RuntimeException re) {
						problems.add(new HQLProblem(re.getLocalizedMessage(), true, resource, sl.getStartPosition(), sl.getStartPosition()+sl.getLength()-1, getLineNumber( sl.getStartPosition() )));
					}
				}
			}			
			return false;
		} else {
			return false;
		}		
	}

	private void checkQuery(ConsoleConfiguration cc, String query) {
		if(cc!=null && cc.isSessionFactoryCreated()) {
			new HQLQueryPlan(query, false, Collections.EMPTY_MAP, (SessionFactoryImpl)cc.getSessionFactory());
		} else {											
			//messager.printWarning( annoValue.getPosition(), "Could not verify syntax. SessionFactory not created." );
		}		
	}

	public List getProblems() {
		return problems;
	}
}
