/**
 * 
 */
package org.hibernate.eclipse.mapper.extractor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

public class HibernateTypeHandler implements HBMInfoHandler {

	private final HBMInfoExtractor extractor;

	/**
	 * @param extractor
	 */
	public HibernateTypeHandler(HBMInfoExtractor extractor) {
		this.extractor = extractor;
	}

	public ICompletionProposal[] attributeCompletionProposals(IJavaProject project, Node node, String attributeName, String start, int offset) {
	    List types = this.extractor.findMatchingHibernateTypes(start);
		
		List proposals = new ArrayList(types.size());		
		for (Iterator iter = types.iterator(); iter.hasNext();) {
			HibernateTypeDescriptor element = (HibernateTypeDescriptor) iter.next();
			String extendedinfo = "<b>Hibernate type</b>: " + element.getName();
			if(element.getReturnClass()!=null) {
				extendedinfo += "<br><b>Return class</b>: " + element.getReturnClass();				
			}
			if(element.getPrimitiveClass()!=null) {
				extendedinfo += "<br><b>Return primitive</b>: " + element.getPrimitiveClass();
			}
			proposals.add(new CompletionProposal(element.getName(), offset, start.length(), element.getName().length(), null, null, null, extendedinfo));
		}
		
		try {
			IType typeInterface = project.findType("org.hibernate.usertype.CompositeUserType");
			Set alreadyFound = new HashSet();			
			if (typeInterface != null) {
				ITypeHierarchy hier = typeInterface.newTypeHierarchy(project, new NullProgressMonitor());
				IType[] classes = hier.getAllSubtypes(typeInterface); // TODO: cache these results ?
				this.extractor.generateTypeProposals(start, offset, proposals, alreadyFound, classes);				
			}
			
			typeInterface = project.findType("org.hibernate.usertype.UserType");
			if (typeInterface != null) {
				ITypeHierarchy hier = typeInterface.newTypeHierarchy(project, new NullProgressMonitor());
				IType[] classes = hier.getAllSubtypes(typeInterface); // TODO: cache these results ?
				this.extractor.generateTypeProposals(start, offset, proposals, alreadyFound, classes);				
			}
		} catch (CoreException e) {
			throw new RuntimeException(e); // TODO: log as error!
		}
		
		ICompletionProposal[] result = (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[proposals.size()]);
		return result;            
	}

	public IJavaElement getJavaElement(IJavaProject project, Node currentNode, Attr currentAttrNode) {
		return extractor.getNearestTypeJavaElement(project, currentNode);
	}}