/**
 * 
 */
package org.hibernate.eclipse.mapper.extractor;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

public interface HBMInfoHandler {
	/** 
	 * Provide completion proposals
	 * 
	 **/
     public ICompletionProposal[] attributeCompletionProposals(IJavaProject javaProject, Node node, String attributeName, String start, int offset);

     /** Provide JavaElement info for hyperlinking */
	public IJavaElement getJavaElement(IJavaProject project, Node currentNode, Attr currentAttrNode); 
}