/**
 * 
 */
package org.hibernate.eclipse.mapper.extractor;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.eval.IEvaluationContext;
import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.hibernate.eclipse.mapper.extractor.HBMXMLResultCollector.Settings;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

class JavaTypeHandler implements HBMInfoHandler {
	
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
	    return handleTypes(project, settings,this.extractor.getPackageName(node), start, offset);            
	}
	
	protected ICompletionProposal[] handleTypes(IJavaProject javaProject, Settings settings, String packageName, String start, int offset) {
		
			if (javaProject != null) {
				IEvaluationContext context = javaProject.newEvaluationContext();                
                if(packageName!=null) {
                    context.setPackageName(packageName);
                }
				
				
				HBMXMLResultCollector rc = new HBMXMLResultCollector(javaProject);
				//rc.reset(offset, javaProject, null);
				rc.setAccepts(settings);
				try {
					// cannot send in my own document as it won't compile as
					// java - so we just send in
					// the smallest snippet possible
					context.codeComplete(start, start.length(), rc);
				} catch (JavaModelException jme) {
					// TODO: handle/report!
					jme.printStackTrace();
				}
				IJavaCompletionProposal[] results = rc.getJavaCompletionProposals();
				this.extractor.transpose(start, offset, results);
				return results;
			}
		return new JavaCompletionProposal[0];
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