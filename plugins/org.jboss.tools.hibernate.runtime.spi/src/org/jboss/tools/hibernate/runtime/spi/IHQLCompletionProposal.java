package org.jboss.tools.hibernate.runtime.spi;

public interface IHQLCompletionProposal {

	String getCompletion();
	int getReplaceStart();
	int getReplaceEnd();
	String getSimpleName();
	int getCompletionKind();
	String getEntityName();
	String getShortEntityName();
	IProperty getProperty();
	
	int aliasRefKind();
	int entityNameKind();
	int propertyKind();
	int keywordKind();
	int functionKind();

}
