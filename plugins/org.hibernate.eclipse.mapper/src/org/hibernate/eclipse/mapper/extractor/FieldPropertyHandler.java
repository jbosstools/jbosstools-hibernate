/**
 * 
 */
package org.hibernate.eclipse.mapper.extractor;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

class FieldPropertyHandler implements HBMInfoHandler {

	/**
	 * 
	 */
	private final HBMInfoExtractor extractor;

	/**
	 * @param extractor
	 */
	FieldPropertyHandler(HBMInfoExtractor extractor) {
		this.extractor = extractor;
	}

	public ICompletionProposal[] attributeCompletionProposals(IJavaProject project, Node node, String attributeName, String start, int offset) {
		//	TODO: should also try to find properties getXXX()
	    if(project!=null) {
			String typename = this.extractor.getNearestType(node);			
						
			HBMXMLResultCollector rc = null;
			try {
				IType type = project.findType(typename);
				if(type==null) return new ICompletionProposal[0]; //nothing to look for then
				rc = new HBMXMLResultCollector(project);
				rc.setAccepts(false,false,false,false,true,false); // TODO: only handle properties ?
				//rc.reset(offset, javaProject, null);
				
				
				type.codeComplete(start.toCharArray(), -1, start.length(), new char[0][0], new char[0][0], new int[0], false, rc);
			} catch(JavaModelException jme) {
				// TODO: report
			}
			
			IJavaCompletionProposal[] results = rc.getJavaCompletionProposals();
			this.extractor.transpose(start, offset, results);
			return results; 
		}
		
		return new ICompletionProposal[0];            
	}

	public IJavaElement getJavaElement(IJavaProject project, Node currentNode, Attr currentAttrNode) {
		IType type = extractor.getNearestTypeJavaElement(project, currentNode.getParentNode());
		if(type!=null) {				
			IField field = type.getField(currentAttrNode.getValue());
			return field;
		}
		
		return null;
	}
}