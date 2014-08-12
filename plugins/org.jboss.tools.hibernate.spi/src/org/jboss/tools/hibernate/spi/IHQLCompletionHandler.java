package org.jboss.tools.hibernate.spi;

import java.util.List;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

public interface IHQLCompletionHandler {

	List<ICompletionProposal> getCompletionProposals();
	boolean accept(IHQLCompletionProposal proposal);
	void completionFailure(String errorMessage);
	String getLastErrorMessage();
	void clear();

}
