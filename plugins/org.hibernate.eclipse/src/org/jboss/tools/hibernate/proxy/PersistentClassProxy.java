package org.jboss.tools.hibernate.proxy;

import java.util.Iterator;

import org.hibernate.mapping.Join;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Subclass;
import org.jboss.tools.hibernate.spi.IPersistentClass;
import org.jboss.tools.hibernate.spi.IProperty;
import org.jboss.tools.hibernate.spi.ITable;
import org.jboss.tools.hibernate.spi.IValue;

public class PersistentClassProxy implements IPersistentClass {
	
	private PersistentClass target = null;
	private IPersistentClass rootClass = null;
	private IPersistentClass superClass = null;
	private ITable table = null;
	private IValue discriminator = null;
	private IValue identifier = null;
	private IProperty version = null;

	public PersistentClassProxy(PersistentClass persistentClass) {
		target = persistentClass;
	}

	public PersistentClass getTarget() {
		return target;
	}

	@Override
	public String getClassName() {
		return target.getClassName();
	}

	@Override
	public String getEntityName() {
		return target.getEntityName();
	}

	@Override
	public boolean isAssignableToRootClass() {
		return RootClass.class.isAssignableFrom(target.getClass());
	}

	@Override
	public boolean isRootClass() {
		return target.getClass() == RootClass.class;
	}

	@Override
	public Property getIdentifierProperty() {
		return target.getIdentifierProperty();
	}

	@Override
	public boolean hasIdentifierProperty() {
		return target.hasIdentifierProperty();
	}

	@Override
	public boolean isInstanceOfRootClass() {
		return target instanceof RootClass;
	}

	@Override
	public boolean isInstanceOfSubclass() {
		return target instanceof Subclass;
	}

	@Override
	public String getNodeName() {
		return target.getNodeName();
	}

	@Override
	public IPersistentClass getRootClass() {
		if (rootClass == null && target.getRootClass() != null) {
			rootClass = new PersistentClassProxy(target.getRootClass());
		}
		return rootClass;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<Property> getPropertyClosureIterator() {
		return target.getPropertyClosureIterator();
	}

	@Override
	public IPersistentClass getSuperclass() {
		if (superClass != null) {
			superClass = new PersistentClassProxy(target.getSuperclass());
		}
		return superClass;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<Property> getPropertyIterator() {
		return target.getPropertyIterator();
	}

	@Override
	public Property getProperty(String string) {
		return target.getProperty(string);
	}

	@Override
	public ITable getTable() {
		if (table == null && target.getTable() != null) {
			table = new TableProxy(target.getTable());
		}
		return table;
	}

	@Override
	public boolean isAbstract() {
		return target.isAbstract();
	}

	@Override
	public IValue getDiscriminator() {
		if (discriminator == null && target.getDiscriminator() != null) {
			discriminator = new ValueProxy(target.getDiscriminator());
		}
		return discriminator;
	}

	@Override
	public IValue getIdentifier() {
		if (identifier == null && target.getIdentifier() != null) {
			identifier = new ValueProxy(target.getIdentifier());
		}
		return identifier;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<Join> getJoinIterator() {
		return target.getJoinIterator();
	}

	@Override
	public IProperty getVersion() {
		if (version == null && target.getVersion() != null) {
			version = new PropertyProxy(target.getVersion());
		}
		return version;
	}

	@Override
	public void setClassName(String className) {
		target.setClassName(className);
	}

	@Override
	public void setEntityName(String entityName) {
		target.setEntityName(entityName);
	}

	@Override
	public void setDiscriminatorValue(String value) {
		target.setDiscriminatorValue(value);
	}

	@Override
	public void setAbstract(boolean b) {
		target.setAbstract(b);
	}

	@Override
	public void addProperty(Property property) {
		target.addProperty(property);
	}
	
	

}
