/**
 * 
 */
package org.hibernate.eclipse.mapper.extractor;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.hibernate.eclipse.hqleditor.CompletionHelper;
import org.hibernate.eclipse.hqleditor.HibernateResultCollector.Settings;
import org.w3c.dom.Node;

class PackageHandler extends JavaTypeHandler {
	
	public PackageHandler(HBMInfoExtractor extractor) {
		super(extractor);					
	}

	public ICompletionProposal[] attributeCompletionProposals(IJavaProject project, Node node, String attributeName, String start, int offset) {
		Settings settings = new Settings();
        settings.setAcceptPackages(true);
		return CompletionHelper.completeOnJavaTypes(project, settings,extractor.getPackageName(node), start, offset);            
	}
}