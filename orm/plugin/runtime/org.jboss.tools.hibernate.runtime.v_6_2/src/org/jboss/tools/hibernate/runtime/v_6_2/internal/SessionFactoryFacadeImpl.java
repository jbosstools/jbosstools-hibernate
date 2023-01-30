package org.jboss.tools.hibernate.runtime.v_6_2.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.model.domain.internal.MappingMetamodelImpl;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.jboss.tools.hibernate.runtime.common.AbstractSessionFactoryFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.ICollectionMetadata;

public class SessionFactoryFacadeImpl extends AbstractSessionFactoryFacade {

	public SessionFactoryFacadeImpl(IFacadeFactory facadeFactory, Object target) {
		super(facadeFactory, target);
	}

	@Override
	protected void initializeAllClassMetadata() {
		SessionFactoryImplementor sessionFactory = (SessionFactoryImplementor)getTarget();
		MappingMetamodelImpl metamodel = (MappingMetamodelImpl)sessionFactory.getMetamodel();   
		Map<String, EntityPersister> entityPersisters = metamodel.entityPersisters();
		allClassMetadata = new HashMap<String, IClassMetadata>(
				entityPersisters.size());
		for (Entry<String, EntityPersister> entry : entityPersisters.entrySet()) {
			allClassMetadata.put(
					(String)entry.getKey(), 
					getFacadeFactory().createClassMetadata(entry.getValue()));
		}
	}
	
	protected void initializeAllCollectionMetadata() {
		SessionFactoryImplementor sessionFactory = (SessionFactoryImplementor)getTarget();
		MappingMetamodelImpl metamodel = (MappingMetamodelImpl)sessionFactory.getMetamodel();   
		Map<String, CollectionPersister> collectionPersisters = metamodel.collectionPersisters();
		allCollectionMetadata = new HashMap<String, ICollectionMetadata>(
				collectionPersisters.size());
		for (Entry<String, CollectionPersister> entry : collectionPersisters.entrySet()) {
			allCollectionMetadata.put(
					(String)entry.getKey(), 
					getFacadeFactory().createCollectionMetadata(entry.getValue()));
		}
	}

}
