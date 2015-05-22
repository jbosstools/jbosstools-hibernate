package org.jboss.tools.hibernate.runtime.v_3_5.internal;

import org.hibernate.Session;
import org.hibernate.cfg.Settings;
import org.hibernate.mapping.Column;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.type.Type;
import org.hibernate.type.TypeFactory;
import org.jboss.tools.hibernate.proxy.HQLCodeAssistProxy;
import org.jboss.tools.hibernate.proxy.HibernateMappingExporterExtension;
import org.jboss.tools.hibernate.proxy.HibernateMappingExporterProxy;
import org.jboss.tools.hibernate.proxy.SessionProxy;
import org.jboss.tools.hibernate.proxy.SettingsProxy;
import org.jboss.tools.hibernate.runtime.common.AbstractFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IEntityMetamodel;
import org.jboss.tools.hibernate.runtime.spi.IHQLCodeAssist;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.ISession;
import org.jboss.tools.hibernate.runtime.spi.ISettings;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.spi.ITypeFactory;

public class FacadeFactoryImpl extends AbstractFacadeFactory {
	
	public ClassLoader getClassLoader() {
		return FacadeFactoryImpl.class.getClassLoader();
	}

	@Override
	public IClassMetadata createClassMetadata(Object target) {
		return new ClassMetadataFacadeImpl(this, (ClassMetadata)target);
	}

	@Override
	public IColumn createColumn(Object target) {
		return new ColumnFacadeImpl(this, (Column)target);
	}

	@Override
	public IEntityMetamodel createEntityMetamodel(Object target) {
		return new EntityMetamodelFacadeImpl(this, (EntityMetamodel)target);
	}

	@Override
	public IHibernateMappingExporter createHibernateMappingExporter(Object target) {
		return new HibernateMappingExporterProxy(this, (HibernateMappingExporterExtension)target);
	}

	@Override
	public IHQLCodeAssist createHQLCodeAssist(Object target) {
		return new HQLCodeAssistProxy(this, (HQLCodeAssist)target);
	}

	@Override
	public ISession createSession(Object target) {
		return new SessionProxy(this, (Session)target);
	}

	@Override
	public ISettings createSettings(Object target) {
		return new SettingsProxy(this, (Settings)target);
	}

	@Override
	public IPersistentClass createSpecialRootClass(IProperty property) {
		return new SpecialRootClassFacadeImpl(this, property);
	}

	@Override
	public ITypeFactory createTypeFactory() {
		return new TypeFactoryFacadeImpl(this, (TypeFactory)null);
	}

	@Override
	public IType createType(Object target) {
		return new TypeFacadeImpl(this, (Type)target);
	}

}
