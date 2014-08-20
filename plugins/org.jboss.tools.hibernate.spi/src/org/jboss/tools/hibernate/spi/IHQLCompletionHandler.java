package org.jboss.tools.hibernate.spi;


public interface IHQLCompletionHandler {

	boolean accept(IHQLCompletionProposal proposal);
	void completionFailure(String errorMessage);
	String getLastErrorMessage();

}
