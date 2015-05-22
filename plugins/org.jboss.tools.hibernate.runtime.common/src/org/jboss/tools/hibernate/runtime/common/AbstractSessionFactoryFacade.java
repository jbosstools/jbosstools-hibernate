package org.jboss.tools.hibernate.runtime.common;

import java.util.HashMap;
import java.util.Map;

import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.ICollectionMetadata;
import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ISessionFactory;

public abstract class AbstractSessionFactoryFacade 
extends AbstractFacade 
implements ISessionFactory {

	protected Map<String, IClassMetadata> allClassMetadata = null;
	protected Map<String, ICollectionMetadata> allCollectionMetadata = null;

	public AbstractSessionFactoryFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public void close() {
		Util.invokeMethod(getTarget(), "close", new Class[] {}, new Object[] {});
	}

	@Override
	public Map<String, IClassMetadata> getAllClassMetadata() {
		if (allClassMetadata == null) {
			initializeAllClassMetadata();
		}
		return allClassMetadata;
	}
	
	@Override
	public Map<String, ICollectionMetadata> getAllCollectionMetadata() {
		if (allCollectionMetadata == null) {
			initializeAllCollectionMetadata();
		}
		return allCollectionMetadata;
	}
	
	protected void initializeAllClassMetadata() {
		Map<?, ?> targetAllClassMetadata = (Map<?, ?>)Util.invokeMethod(
				getTarget(), 
				"getAllClassMetadata", 
				new Class[] {}, 
				new Object[] {});
		allClassMetadata = new HashMap<String, IClassMetadata>(
				targetAllClassMetadata.size());
		for (Map.Entry<?, ?> entry : targetAllClassMetadata.entrySet()) {
			allClassMetadata.put(
					(String)entry.getKey(), 
					getFacadeFactory().createClassMetadata(entry.getValue()));
		}
	}

	protected void initializeAllCollectionMetadata() {
		Map<?, ?> targetAllCollectionMetadata = (Map<?, ?>)Util.invokeMethod(
				getTarget(), 
				"getAllCollectionMetadata", 
				new Class[] {}, 
				new Object[] {});
		allCollectionMetadata = new HashMap<String, ICollectionMetadata>(
				targetAllCollectionMetadata.size());
		for (Map.Entry<?, ?> entry : targetAllCollectionMetadata.entrySet()) {
			allCollectionMetadata.put(
					(String)entry.getKey(), 
					getFacadeFactory().createCollectionMetadata(entry.getValue()));
		}
	}

}
