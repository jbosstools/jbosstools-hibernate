package org.jboss.tools.hibernate.runtime.spi;


public interface IHQLCompletionHandler {

	boolean accept(IHQLCompletionProposal proposal);
	void completionFailure(String errorMessage);

}
