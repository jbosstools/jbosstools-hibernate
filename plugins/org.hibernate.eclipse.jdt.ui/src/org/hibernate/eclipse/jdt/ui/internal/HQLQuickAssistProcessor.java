package org.hibernate.eclipse.jdt.ui.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jface.text.IDocument;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.EclipseImages;

public class HQLQuickAssistProcessor extends BasicQuickAssistProcessor {

	public IJavaCompletionProposal[] getAssists(IInvocationContext context,
			IProblemLocation[] locations) throws CoreException {
		
		IJavaCompletionProposal[] result = new IJavaCompletionProposal[0];
		if(!hasAssists( context )) return result;
		
		ASTNode coveringNode = context.getCoveringNode();
		if(!(coveringNode instanceof StringLiteral)) {
			return result;
		}
		
		StringLiteral stringLiteral= (StringLiteral) coveringNode;
		String contents= stringLiteral.getLiteralValue();
		result = new IJavaCompletionProposal[1];			
		result[0] = new ExternalActionQuickAssistProposal(contents, EclipseImages.getImage(ImageConstants.HQL_EDITOR), "Copy to HQL Editor", context) {
			public void apply(IDocument document) {
				HibernateConsolePlugin.getDefault().openScratchHQLEditor( getName(), getContents() );				
			}
		};
		
		return result;
	}
		
}
