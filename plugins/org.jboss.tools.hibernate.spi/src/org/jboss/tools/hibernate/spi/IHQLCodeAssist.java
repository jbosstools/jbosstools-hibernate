package org.jboss.tools.hibernate.spi;


public interface IHQLCodeAssist {

	void codeComplete(String query, int currentOffset,
			IHQLCompletionHandler handler);

}
