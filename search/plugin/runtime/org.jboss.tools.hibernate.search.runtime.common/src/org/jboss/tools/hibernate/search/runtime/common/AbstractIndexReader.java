package org.jboss.tools.hibernate.search.runtime.common;

import org.jboss.tools.hibernate.runtime.common.Util;
import org.jboss.tools.hibernate.search.runtime.spi.IDocument;
import org.jboss.tools.hibernate.search.runtime.spi.IIndexReader;

public abstract class AbstractIndexReader extends AbstractFacade implements IIndexReader {

	public AbstractIndexReader(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}
	
	public int numDocs() {
		Object targetNumDocs = 
				Util.invokeMethod(getTarget(), "numDocs", new Class[] {}, new Object[] {});
		return (Integer)targetNumDocs;
	}
	
	public IDocument document(int n) {
		Object targetDocument = 
				Util.invokeMethod(getTarget(), "document", new Class[] { Integer.TYPE }, new Object[] { n });
		return new AbstractDocument(getFacadeFactory(), targetDocument) {};
	}
}
