package org.jboss.tools.hibernate.runtime.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.jboss.tools.hibernate.runtime.spi.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IJoin;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.IValue;

public abstract class AbstractPersistentClassFacade 
extends AbstractFacade 
implements IPersistentClass {

	protected IProperty identifierProperty = null;
	protected IPersistentClass rootClass = null;
	protected HashSet<IProperty> propertyClosures = null;
	protected IPersistentClass superClass = null;
	protected HashMap<String, IProperty> properties = null;
	protected ITable table = null;
	protected IValue discriminator = null;
	protected IValue identifier = null;
	protected HashSet<IJoin> joins = null;
	protected IProperty version = null;
	protected HashSet<IPersistentClass> subclasses = null;
	protected ITable rootTable = null;

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

	@Override
	public Iterator<IProperty> getPropertyIterator() {
		if (properties == null) {
			initializeProperties();
		}
		return properties.values().iterator();
	}
	
	@Override
	public IProperty getProperty(String string) {
		if (properties == null) {
			initializeProperties();
		}
		return properties.get(string);
	}

	@Override
	public ITable getTable() {
		if (table == null) {
			Object targetTable = Util.invokeMethod(
					getTarget(), 
					"getTable", 
					new Class[] {}, 
					new Object[] {});
			if (targetTable != null) {
				table = getFacadeFactory().createTable(targetTable);
			}
		}
		return table;
	}

	@Override
	public Boolean isAbstract() {
		return (Boolean)Util.invokeMethod(
				getTarget(), 
				"isAbstract", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public IValue getDiscriminator() {
		if (discriminator == null) {
			Object targetDiscriminator = Util.invokeMethod(
					getTarget(), 
					"getDiscriminator", 
					new Class[] {}, 
					new Object[] {});
			if (discriminator != null) {
				discriminator = getFacadeFactory().createValue(targetDiscriminator);
			}
		}
		return discriminator;
	}

	@Override
	public IValue getIdentifier() {
		if (identifier == null) {
			Object targetIdentifier = Util.invokeMethod(
					getTarget(), 
					"getIdentifier", 
					new Class[] {}, 
					new Object[] {});
			if (targetIdentifier != null) {
				identifier = getFacadeFactory().createValue(targetIdentifier);
			}
		}
		return identifier;
	}

	@Override
	public Iterator<IJoin> getJoinIterator() {
		if (joins == null) {
			initializeJoins();
		}
		return joins.iterator();
	}
	
	@Override
	public IProperty getVersion() {
		if (version == null) {
			Object targetVersion = Util.invokeMethod(
					getTarget(), 
					"getVersion", 
					new Class[] {}, 
					new Object[] {});
			if (version != null) {
				version = getFacadeFactory().createProperty(targetVersion);
			}
		}
		return version;
	}

	@Override
	public void setClassName(String className) {
		Util.invokeMethod(
				getTarget(), 
				"setClassName", 
				new Class[] { String.class }, 
				new Object[] { className });
	}

	@Override
	public void setEntityName(String entityName) {
		Util.invokeMethod(
				getTarget(), 
				"setEntityName", 
				new Class[] { String.class }, 
				new Object[] { entityName });
	}

	@Override
	public void setDiscriminatorValue(String value) {
		Util.invokeMethod(
				getTarget(), 
				"setDiscriminatorValue", 
				new Class[] { String.class }, 
				new Object[] { value });
	}

	@Override
	public void setAbstract(boolean b) {
		Util.invokeMethod(
				getTarget(), 
				"setAbstract", 
				new Class[] { boolean.class }, 
				new Object[] { b });
	}

	@Override
	public void addProperty(IProperty property) {
		Object propertyTarget = Util.invokeMethod(
				property, 
				"getTarget", 
				new Class[] {}, 
				new Object[] {});
		Util.invokeMethod(getTarget(), "addProperty", new Class[] { getPropertyClass() }, new Object[] { propertyTarget });
		properties = null;
		propertyClosures = null;
	}

	@Override
	public boolean isInstanceOfJoinedSubclass() {
		return getJoinedSubclassClass().isAssignableFrom(getTarget().getClass());
	}

	@Override
	public void setTable(ITable table) {
		if (isInstanceOfRootClass() || isInstanceOfJoinedSubclass()) {
			Object tableTarget = Util.invokeMethod(
					table, 
					"getTarget", 
					new Class[] {}, 
					new Object[] {});
			Util.invokeMethod(
					getTarget(), 
					"setTable", 
					new Class[] { getTableClass() }, 
					new Object[] { tableTarget });
		}
	}

	@Override
	public void setKey(IValue value) {
		Object valueTarget = Util.invokeMethod(
				value, 
				"getTarget", 
				new Class[] {}, 
				new Object[] {});
		Util.invokeMethod(
				getTarget(), 
				"setKey", 
				new Class[] { getKeyValueClass() }, 
				new Object[] { valueTarget });
	}

	public boolean isInstanceOfSpecialRootClass() {
		return false;
	}

	@Override
	public IProperty getProperty() {
		throw new RuntimeException("getProperty() is only allowed on SpecialRootClass"); //$NON-NLS-1$
	}

	@Override
	public IProperty getParentProperty() {
		throw new RuntimeException("getProperty() is only allowed on SpecialRootClass"); //$NON-NLS-1$
	}

	@Override
	public void setIdentifierProperty(IProperty property) {
		Object propertyTarget = Util.invokeMethod(
				property, 
				"getTarget", 
				new Class[] {}, 
				new Object[] {});
		Util.invokeMethod(
				getTarget(), 
				"setIdentifierProperty", 
				new Class[] { getPropertyClass() }, 
				new Object[] { propertyTarget });
		identifierProperty = property;
	}

	@Override
	public void setIdentifier(IValue value) {
		Object valueTarget = Util.invokeMethod(
				value, 
				"getTarget", 
				new Class[] {}, 
				new Object[] {});
		Util.invokeMethod(
				getTarget(), 
				"setIdentifier", 
				new Class[] { getKeyValueClass() }, 
				new Object[] { valueTarget });
		identifier = value;
	}

	@Override
	public void setDiscriminator(IValue discr) {
		Object discrTarget = Util.invokeMethod(
				discr, 
				"getTarget", 
				new Class[] {}, 
				new Object[] {});
		Util.invokeMethod(
				getTarget(), 
				"setDiscriminator", 
				new Class[] { getValueClass() }, 
				new Object[] { discrTarget });
		discriminator = discr;
	}

	@Override
	public void setProxyInterfaceName(String name) {
		Util.invokeMethod(
				getTarget(), 
				"setProxyInterfaceName", 
				new Class[] { String.class }, 
				new Object[] { name });
	}

	@Override
	public void setLazy(boolean b) {
		Util.invokeMethod(
				getTarget(), 
				"setLazy", 
				new Class[] { boolean.class }, 
				new Object[] { b });
	}

	@Override
	public Iterator<IPersistentClass> getSubclassIterator() {
		if (subclasses == null) {
			initializeSubclasses();
		}
		return subclasses.iterator();
	}
	
	@Override
	public ITable getRootTable() {
		if (rootTable == null) {
			Object targetRootTable = Util.invokeMethod(
					getTarget(), 
					"getRootTable", 
					new Class[] {}, 
					new Object[] {});
			if (targetRootTable != null) {
				rootTable = getFacadeFactory().createTable(targetRootTable);
			}
		}
		return rootTable;
	}

	@Override
	public boolean isCustomDeleteCallable() {
		return (boolean)Util.invokeMethod(
				getTarget(), 
				"isCustomDeleteCallable", 
				new Class[] {}, 
				new Object[] {});
	}

	protected Class<?> getRootClassClass() {
		return Util.getClass(getRootClassClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getSubclassClass() {
		return Util.getClass(getSubclassClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getPropertyClass() {
		return Util.getClass(getPropertyClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getJoinedSubclassClass() {
		return Util.getClass(getJoinedSubclassClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getTableClass() {
		return Util.getClass(getTableClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getKeyValueClass() {
		return Util.getClass(getKeyValueClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getValueClass() {
		return Util.getClass(getValueClassName(), getFacadeFactoryClassLoader());
	}
	
	protected String getRootClassClassName() {
		return "org.hibernate.mapping.RootClass";
	}

	protected String getSubclassClassName() {
		return "org.hibernate.mapping.Subclass";
	}

	protected String getPropertyClassName() {
		return "org.hibernate.mapping.Property";
	}

	protected String getJoinedSubclassClassName() {
		return "org.hibernate.mapping.JoinedSubclass";
	}

	protected String getTableClassName() {
		return "org.hibernate.mapping.Table";
	}

	protected String getKeyValueClassName() {
		return "org.hibernate.mapping.KeyValue";
	}

	protected String getValueClassName() {
		return "org.hibernate.mapping.Value";
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

	protected void initializeProperties() {
		properties = new HashMap<String, IProperty>();
		Iterator<?> targetPropertyIterator = (Iterator<?>)Util.invokeMethod(
				getTarget(), 
				"getPropertyIterator", 
				new Class[] {}, 
				new Object[] {});
		while (targetPropertyIterator.hasNext()) {
			Object property = targetPropertyIterator.next();
			String propertyName = (String)Util.invokeMethod(
					property, 
					"getName", 
					new Class[] {}, 
					new Object[] {}); 
			properties.put(
					propertyName, 
					getFacadeFactory().createProperty(property));
		}
	}

	protected void initializeJoins() {
		joins = new HashSet<IJoin>();
		Iterator<?> targetJoinIterator = (Iterator<?>)Util.invokeMethod(
				getTarget(), 
				"getJoinIterator", 
				new Class[] {}, 
				new Object[] {});
		while (targetJoinIterator.hasNext()) {
			joins.add(getFacadeFactory().createJoin(targetJoinIterator.next()));
		}
	}

	protected void initializeSubclasses() {
		subclasses = new HashSet<IPersistentClass>();
		Iterator<?> targetSubclassIterator = (Iterator<?>)Util.invokeMethod(
				getTarget(), 
				"getSubclassIterator", 
				new Class[] {}, 
				new Object[] {});
		while (targetSubclassIterator.hasNext()) {
			subclasses.add(getFacadeFactory().createPersistentClass(
					targetSubclassIterator.next()));
		}
	}

}
