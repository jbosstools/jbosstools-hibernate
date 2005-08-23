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
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;

public class GeneratorTypeHandler implements HBMInfoHandler {
	
		private final HBMInfoExtractor extractor;

		/**
		 * @param extractor
		 */
		public GeneratorTypeHandler(HBMInfoExtractor extractor) {
			this.extractor = extractor;
		}

		public ICompletionProposal[] attributeCompletionProposals(IJavaProject project, Node node, String attributeName, String start, int offset) {
		    List types = this.extractor.findMatchingGenerators(start);
			
			List proposals = new ArrayList(types.size() );		
			for (Iterator iter = types.iterator(); iter.hasNext();) {
				HibernateTypeDescriptor element = (HibernateTypeDescriptor) iter.next();
				String extendedinfo = "<b>Generator type</b>: " + element.getName();
				if(element.getReturnClass()!=null) {
					extendedinfo += "<br><b>Return class</b>: " + element.getReturnClass();				
				}
				proposals.add(new CompletionProposal(element.getName(), offset, start.length(), element.getName().length(), null, null, null, extendedinfo) );
			}
			
			try {
				IType typeInterface = project.findType("org.hibernate.id.IdentifierGenerator");
				Set alreadyFound = new HashSet();			
				if (typeInterface != null) {
					ITypeHierarchy hier = typeInterface.newTypeHierarchy(project, new NullProgressMonitor() );
					IType[] classes = hier.getAllSubtypes(typeInterface); // TODO: cache these results ?
					this.extractor.generateTypeProposals(start, offset, proposals, alreadyFound, classes, "org.hibernate.id");				
				}
			} catch (CoreException e) {
				throw new RuntimeException(e); // TODO: log as error!
			}
			
			ICompletionProposal[] result = (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[proposals.size()]);
			return result;            
		}

		public IJavaElement getJavaElement(IJavaProject project, Node currentNode, Attr currentAttrNode) {
			return extractor.getNearestTypeJavaElement(project, currentNode);
		}
}
