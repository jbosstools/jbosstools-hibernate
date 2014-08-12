package org.jboss.tools.hibernate.spi;

public interface IHQLCompletionProposal {

	String getCompletion();
	int getReplaceStart();
	int getReplaceEnd();
	String getSimpleName();
	int getCompletionKind();
	String getEntityName();
	String getShortEntityName();
	IProperty getProperty();

}
