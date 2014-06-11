package org.jboss.tools.hibernate.proxy;

import org.hibernate.EntityMode;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.spi.IClassMetadata;
import org.jboss.tools.hibernate.spi.ISessionImplementor;

public class ClassMetadataProxy implements IClassMetadata {
	
	private ClassMetadata target = null;

	public ClassMetadataProxy(ClassMetadata classMetadata) {
		target = classMetadata;
	}

	@Override
	public String getEntityName() {
		return target.getEntityName();
	}

	@Override
	public String getIdentifierPropertyName() {
		return target.getIdentifierPropertyName();
	}

	@Override
	public String[] getPropertyNames() {
		return target.getPropertyNames();
	}

	@Override
	public Type[] getPropertyTypes() {
		return target.getPropertyTypes();
	}

	@Override
	public Class<?> getMappedClass() {
		return target.getMappedClass();
	}

	@Override
	public Type getIdentifierType() {
		return target.getIdentifierType();
	}

	@Override
	public Object getPropertyValue(Object object, String name) {
		return target.getPropertyValue(object, name);
	}

	@Override
	public boolean hasIdentifierProperty() {
		return target.hasIdentifierProperty();
	}

	@Override
	public Object getIdentifier(Object object) {
		return target.getIdentifier(object);
	}

	@Override
	public Object getIdentifier(Object object, ISessionImplementor implementor) {
		Object result = null;
		if (implementor instanceof SessionProxy) {
			SessionImplementor impl = (SessionImplementor)((SessionProxy)implementor).getTarget();
			result = target.getIdentifier(object, impl);
		}
		return result;
	}

}
