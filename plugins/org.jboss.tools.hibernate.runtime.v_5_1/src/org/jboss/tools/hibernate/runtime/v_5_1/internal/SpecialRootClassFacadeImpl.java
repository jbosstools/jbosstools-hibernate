package org.jboss.tools.hibernate.runtime.v_5_1.internal;

import java.lang.reflect.Field;

import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.jboss.tools.hibernate.runtime.common.AbstractSpecialRootClassFacade;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IProperty;

public class SpecialRootClassFacadeImpl extends AbstractSpecialRootClassFacade {

	public SpecialRootClassFacadeImpl(
			IFacadeFactory facadeFactory, 
			IProperty property) {
		super(facadeFactory, new RootClass(getMetadataBuildingContext(property)));
		this.property = property;
		generate();
	}
	
	private static MetadataBuildingContext getMetadataBuildingContext(IProperty property) {
		Property target = (Property)((IFacade)property).getTarget();
		PersistentClass pc = target.getPersistentClass();
		MetadataBuildingContext result = null;
		try {
			Field field = PersistentClass.class.getDeclaredField("metadataBuildingContext");
			field.setAccessible(true);
			result = (MetadataBuildingContext)field.get(pc);
		} catch (NoSuchFieldException | 
				SecurityException | 
				IllegalArgumentException | 
				IllegalAccessException e) {
			throw new RuntimeException("Problem while trying to retrieve MetadataBuildingContext from field", e);
		}
		return result;
	}

}
