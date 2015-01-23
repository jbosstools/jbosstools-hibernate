package org.jboss.tools.hibernate.runtime.spi;


public interface IHQLCodeAssist {

	void codeComplete(String query, int currentOffset,
			IHQLCompletionHandler handler);

}
