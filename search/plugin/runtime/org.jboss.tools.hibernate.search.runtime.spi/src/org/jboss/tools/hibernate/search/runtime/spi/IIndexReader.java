package org.jboss.tools.hibernate.search.runtime.spi;

public interface IIndexReader {
	
	int numDocs();
	
	IDocument document(int n);

}
