package org.jboss.tools.hibernate.runtime.v_6_0.internal;

import static org.junit.Assert.assertSame;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.runtime.common.AbstractCollectionMetadataFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.ICollectionMetadata;
import org.junit.Before;
import org.junit.Test;

public class CollectionMetadataFacadeTest {
	
	private static final IFacadeFactory FACADE_FACTORY = new FacadeFactoryImpl();
	
	private CollectionMetadata collectionMetadataTarget = null;
	private ICollectionMetadata collectionMetadataFacade = null;
	
	@Before
	public void before() {
		collectionMetadataTarget = createCollectionMetadata();
		collectionMetadataFacade = new AbstractCollectionMetadataFacade(
				FACADE_FACTORY, collectionMetadataTarget) {};
	}
	
	@Test
	public void testGetElementType() {
		assertSame(elementType, ((IFacade)collectionMetadataFacade.getElementType()).getTarget());
	}
	
	private final Type elementType = (Type)Proxy.newProxyInstance(
			getClass().getClassLoader(), 
			new Class[] { Type.class },
			new InvocationHandler() {		
				@Override
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					return null;
				}
			});
	
	private CollectionMetadata createCollectionMetadata() {
		return (CollectionMetadata)Proxy.newProxyInstance(
				getClass().getClassLoader(), 
				new Class[] { CollectionMetadata.class }, 
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						if ("getElementType".equals(method.getName())) {
							
						}
						return elementType;
					}
				});
	}
	
}
