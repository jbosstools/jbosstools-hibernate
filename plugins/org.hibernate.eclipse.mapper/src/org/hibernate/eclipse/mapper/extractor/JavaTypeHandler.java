/**
 * 
 */
package org.hibernate.eclipse.mapper.extractor;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.eval.IEvaluationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.hibernate.eclipse.hqleditor.CompletionHelper;
import org.hibernate.eclipse.hqleditor.HibernateResultCollector.Settings;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

public class JavaTypeHandler implements HBMInfoHandler {
	
	/**
	 * 
	 */
	protected final HBMInfoExtractor extractor;

	/**
	 * @param extractor
	 */
	JavaTypeHandler(HBMInfoExtractor extractor) {
		this.extractor = extractor;
	}

	public ICompletionProposal[] attributeCompletionProposals(IJavaProject project, Node node, String attributeName, String start, int offset) {
		Settings settings = new Settings();
        settings.setAcceptClasses(true);
        settings.setAcceptInterfaces(true);
        settings.setAcceptPackages(true);
        settings.setAcceptTypes(true);
	    return CompletionHelper.completeOnJavaTypes(project, settings,this.extractor.getPackageName(node), start, offset);            
	}
	
	
	
	public IJavaElement getJavaElement(IJavaProject project, Node currentNode, Attr currentAttrNode) {
		return getNearestTypeJavaElement(project, currentNode);
	}

	public IJavaElement getNearestTypeJavaElement(IJavaProject project, Node currentNode) {
		String nearestType = extractor.getNearestType(currentNode);
		if(nearestType!=null) {
			try {
				IType type = project.findType(nearestType);
				return type;
			} catch (JavaModelException e) {
				//ignore
			}
		}
		return null;
	}

}