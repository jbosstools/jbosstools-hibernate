package org.jboss.tools.hibernate.runtime.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;

public abstract class AbstractPersistentClassFacade 
extends AbstractFacade 
implements IPersistentClass {

	protected IProperty identifierProperty = null;
	protected IPersistentClass rootClass = null;
	protected HashSet<IProperty> propertyClosures = null;
	protected IPersistentClass superClass = null;
	protected HashMap<String, IProperty> properties = null;

	public AbstractPersistentClassFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public String getClassName() {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getClassName", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public String getEntityName() {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getEntityName", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public boolean isAssignableToRootClass() {
		return getRootClassClass().isAssignableFrom(getTarget().getClass());
	}
	
	@Override
	public boolean isRootClass() {
		return getTarget().getClass() == getRootClassClass();
	}

	@Override
	public IProperty getIdentifierProperty() {
		if (identifierProperty == null) {
			Object targetIdentifierProperty = Util.invokeMethod(
					getTarget(), 
					"getIdentifierProperty", 
					new Class[] {}, 
					new Object[] {});
			if (targetIdentifierProperty != null) {
				identifierProperty = getFacadeFactory().createProperty(targetIdentifierProperty);
			}
		}
		return identifierProperty;
	}

	@Override
	public boolean hasIdentifierProperty() {
		return (boolean)Util.invokeMethod(
				getTarget(), 
				"hasIdentifierProperty", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public boolean isInstanceOfRootClass() {
		return getRootClassClass().isAssignableFrom(getTarget().getClass());
	}

	@Override
	public boolean isInstanceOfSubclass() {
		return getSubclassClass().isAssignableFrom(getTarget().getClass());
	}

	@Override
	public String getNodeName() {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getNodeName", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public IPersistentClass getRootClass() {
		if (rootClass == null) {
			Object targetRootClass = Util.invokeMethod(
					getTarget(), 
					"getRootClass", 
					new Class[] {}, 
					new Object[] {});
			if (targetRootClass != null) {
				rootClass = getFacadeFactory().createPersistentClass(targetRootClass);
			}
		}
		return rootClass;
	}

	@Override
	public Iterator<IProperty> getPropertyClosureIterator() {
		if (propertyClosures == null) {
			initializePropertyClosures();
		}
		return propertyClosures.iterator();
	}
	
	@Override
	public IPersistentClass getSuperclass() {
		if (superClass != null) {
			Object targetSuperclass = Util.invokeMethod(
					getTarget(), 
					"getSuperclass", 
					new Class[] {}, 
					new Object[] {});
			if (targetSuperclass != null) {
				superClass = getFacadeFactory().createPersistentClass(targetSuperclass);				
			}
		}
		return superClass;
	}

	protected Class<?> getRootClassClass() {
		return Util.getClass(getRootClassClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getSubclassClass() {
		return Util.getClass(getSubclassClassName(), getFacadeFactoryClassLoader());
	}
	
	protected String getRootClassClassName() {
		return "org.hibernate.mapping.RootClass";
	}

	protected String getSubclassClassName() {
		return "org.hibernate.mapping.Subclass";
	}

	protected void initializePropertyClosures() {
		propertyClosures = new HashSet<IProperty>();
		Iterator<?> targetPropertyClosures = (Iterator<?>)Util.invokeMethod(
				getTarget(), 
				"getPropertyClosureIterator", 
				new Class[] {}, 
				new Object[] {});
		while (targetPropertyClosures.hasNext()) {
			propertyClosures.add(
					getFacadeFactory().createProperty(
							targetPropertyClosures.next()));
		}
	}

}
