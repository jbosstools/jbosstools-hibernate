/*******************************************************************************
  * Copyright (c) 2010 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jpt.common.utility.internal.CollectionTools;
import org.eclipse.jpt.jpa.core.JpaProject;
import org.eclipse.jpt.jpa.core.context.JpaRootContextNode;
import org.eclipse.jpt.jpa.core.context.orm.OrmJoinTable;
import org.eclipse.jpt.jpa.core.context.orm.OrmJoinTableRelationshipStrategy;
import org.eclipse.jpt.jpa.core.context.orm.OrmPersistentType;
import org.eclipse.jpt.jpa.core.context.orm.OrmReadOnlyPersistentAttribute;
import org.eclipse.jpt.jpa.core.context.orm.OrmTypeMapping;
import org.eclipse.jpt.jpa.core.context.persistence.MappingFileRef;
import org.eclipse.jpt.jpa.core.context.persistence.Persistence;
import org.eclipse.jpt.jpa.core.internal.jpa1.context.orm.GenericOrmXml;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmBasicMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmColumn;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmEntityImpl;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmIdMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmJoinTable;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmManyToManyMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmManyToOneMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.orm.HibernateOrmTable;
import org.jboss.tools.test.util.ResourcesUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJpaOrmModelTests {

	private static final String PROJECT_NAME = "testHibernateJpaOrmProject";
	private static final String PROJECT_PATH = "res/" + PROJECT_NAME;

	static IProject project = null;
	static JpaProject jpaProject = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		project = ResourcesUtils.importProject(Platform.getBundle("org.jboss.tools.hibernate.jpt.core.test"),
				PROJECT_PATH, new NullProgressMonitor());
		project.refreshLocal(IResource.DEPTH_INFINITE, null);
		jpaProject = (JpaProject) project.getAdapter(JpaProject.class);
	}

	@Test
	public void testDefaultMapping(){
		assertNotNull(jpaProject);
		JpaRootContextNode rootContextNode = jpaProject.getRootContextNode();
		Persistence p = rootContextNode.getPersistenceXml().getPersistence();
		assertTrue(p.persistenceUnits().hasNext());
		assertTrue(p.persistenceUnits().next() instanceof HibernatePersistenceUnit);
		HibernatePersistenceUnit hpu = (HibernatePersistenceUnit) p.persistenceUnits().next();
		List<MappingFileRef> mfrs = CollectionTools.list(hpu.mappingFileRefs());
		assertTrue(mfrs.size() == 1);
		assertTrue(mfrs.get(0).getMappingFile() instanceof GenericOrmXml);
		GenericOrmXml orm = (GenericOrmXml)mfrs.get(0).getMappingFile();
		List<OrmPersistentType> pTypes = CollectionTools.list(orm.getRoot().getPersistentTypes());
		assertTrue( pTypes.size() == 3 );
		checkManyToMany1(orm.getRoot().getPersistentType("entity.ManyToMany1"));
		checkManyToMany2(orm.getRoot().getPersistentType("entity.ManyToMany2"));
	}

	@Test
	public void testNamigStrategyMapping(){
		ConsoleConfiguration cc = KnownConfigurations.getInstance().find(PROJECT_NAME);
		assertNotNull("Console configuration not found for project " + PROJECT_NAME, cc);
		cc.build();
		assertNotNull("Console configuration build problem", cc.getConfiguration());
		assertNotNull("Naming Strategy not found", cc.getConfiguration().getNamingStrategy());
		assertEquals("ns.NamingStrategy", cc.getConfiguration().getNamingStrategy().getClass().getName());

		jpaProject = (JpaProject) project.getAdapter(JpaProject.class);
		assertNotNull(jpaProject);
		JpaRootContextNode rootContextNode = jpaProject.getRootContextNode();
		Persistence p = rootContextNode.getPersistenceXml().getPersistence();
		assertTrue(p.persistenceUnits().hasNext());
		assertTrue(p.persistenceUnits().next() instanceof HibernatePersistenceUnit);
		HibernatePersistenceUnit hpu = (HibernatePersistenceUnit) p.persistenceUnits().next();
		List<MappingFileRef> mfrs = CollectionTools.list(hpu.mappingFileRefs());
		assertTrue(mfrs.size() == 1);
		assertTrue(mfrs.get(0).getMappingFile() instanceof GenericOrmXml);
		GenericOrmXml orm = (GenericOrmXml)mfrs.get(0).getMappingFile();
		checkManyToMany1NS(orm.getRoot().getPersistentType("entity.ManyToMany1"));
		checkManyToMany2NS(orm.getRoot().getPersistentType("entity.ManyToMany2"));
		cc.reset();
	}

	public void checkManyToMany1(OrmPersistentType ormPersistentType){
		OrmTypeMapping mapping = ormPersistentType.getMapping();
		assertTrue(mapping instanceof HibernateOrmEntityImpl);
		HibernateOrmEntityImpl entity = (HibernateOrmEntityImpl) mapping;
		HibernateOrmTable table = entity.getTable();
		assertEquals("ManyToMany1", table.getDBTableName());

		ArrayList<OrmReadOnlyPersistentAttribute> attrs = CollectionTools.list(ormPersistentType.attributes());
		assertTrue(attrs.size() == 3);

		//id
		assertTrue(attrs.get(0).getMapping() instanceof HibernateOrmIdMapping);
		HibernateOrmIdMapping hjidm = (HibernateOrmIdMapping)attrs.get(0).getMapping();
		HibernateOrmColumn hjc = (HibernateOrmColumn)hjidm.getColumn();
		assertEquals("id1", hjc.getDBColumnName());

		//justData
		assertTrue(attrs.get(1).getMapping() instanceof HibernateOrmManyToOneMapping);
		@SuppressWarnings("unchecked")
		HibernateOrmManyToOneMapping hjbm = (HibernateOrmManyToOneMapping)attrs.get(1).getMapping();
		assertEquals("justData1", hjbm.getName());
		assertEquals("entity.JustData", hjbm.getDefaultTargetEntity());

		//mtm
		assertTrue(attrs.get(2).getMapping() instanceof HibernateOrmManyToManyMapping);
		HibernateOrmManyToManyMapping hjmtmm = (HibernateOrmManyToManyMapping)attrs.get(2).getMapping();
		assertEquals("entity.ManyToMany2", hjmtmm.getTargetEntity());
	}

	public void checkManyToMany2(OrmPersistentType ormPersistentType){
		OrmTypeMapping mapping = ormPersistentType.getMapping();
		assertTrue(mapping instanceof HibernateOrmEntityImpl);
		HibernateOrmEntityImpl entity = (HibernateOrmEntityImpl) mapping;
		HibernateOrmTable table = entity.getTable();
		assertEquals("ManyToMany22", table.getDBTableName());

		ArrayList<OrmReadOnlyPersistentAttribute> attrs = CollectionTools.list(ormPersistentType.attributes());
		assertTrue(attrs.size() == 3);

		//id
		assertTrue(attrs.get(0).getMapping() instanceof HibernateOrmIdMapping);
		HibernateOrmIdMapping hjidm = (HibernateOrmIdMapping)attrs.get(0).getMapping();
		HibernateOrmColumn hjc = (HibernateOrmColumn)hjidm.getColumn();
		assertEquals("id", hjc.getDBColumnName());

		//justData
		assertTrue(attrs.get(1).getMapping() instanceof HibernateOrmBasicMapping);
		HibernateOrmBasicMapping hjbm = (HibernateOrmBasicMapping)attrs.get(1).getMapping();
		hjc = (HibernateOrmColumn)hjbm.getColumn();
		assertEquals("simpleData", hjc.getDBColumnName());

		//mtm
		assertTrue(attrs.get(2).getMapping() instanceof HibernateOrmManyToManyMapping);
		HibernateOrmManyToManyMapping hjmtmm = (HibernateOrmManyToManyMapping)attrs.get(2).getMapping();
		assertEquals("entity.ManyToMany1", hjmtmm.getTargetEntity());
		OrmJoinTableRelationshipStrategy jtJoiningStrategy = hjmtmm.getRelationship().getJoinTableStrategy();
		OrmJoinTable joinTable = jtJoiningStrategy.getJoinTable();
		assertTrue(joinTable instanceof HibernateOrmJoinTable);
		HibernateOrmJoinTable hjjt = (HibernateOrmJoinTable)joinTable;
		assertEquals("ManyToMany22_ManyToMany1", hjjt.getDBTableName());
	}

	public void checkManyToMany1NS(OrmPersistentType ormPersistentType){
		OrmTypeMapping mapping = ormPersistentType.getMapping();
		assertTrue(mapping instanceof HibernateOrmEntityImpl);
		HibernateOrmEntityImpl entity = (HibernateOrmEntityImpl) mapping;
		HibernateOrmTable table = entity.getTable();
		assertEquals("ctn_ManyToMany1", table.getDBTableName());

		ArrayList<OrmReadOnlyPersistentAttribute> attrs = CollectionTools.list(ormPersistentType.attributes());
		assertTrue(attrs.size() == 3);

		//id
		assertTrue(attrs.get(0).getMapping() instanceof HibernateOrmIdMapping);
		HibernateOrmIdMapping hjidm = (HibernateOrmIdMapping)attrs.get(0).getMapping();
		HibernateOrmColumn hjc = (HibernateOrmColumn)hjidm.getColumn();
		assertEquals("pc_id1", hjc.getDBColumnName());

		//justData
		assertTrue(attrs.get(1).getMapping() instanceof HibernateOrmManyToOneMapping);
		@SuppressWarnings("unchecked")
		HibernateOrmManyToOneMapping hjbm = (HibernateOrmManyToOneMapping)attrs.get(1).getMapping();
		assertEquals("justData1", hjbm.getName());
		assertEquals("entity.JustData", hjbm.getDefaultTargetEntity());

		//mtm
		assertTrue(attrs.get(2).getMapping() instanceof HibernateOrmManyToManyMapping);
		HibernateOrmManyToManyMapping hjmtmm = (HibernateOrmManyToManyMapping)attrs.get(2).getMapping();
		assertEquals("entity.ManyToMany2", hjmtmm.getTargetEntity());
	}

	public void checkManyToMany2NS(OrmPersistentType ormPersistentType){
		OrmTypeMapping mapping = ormPersistentType.getMapping();
		assertTrue(mapping instanceof HibernateOrmEntityImpl);
		HibernateOrmEntityImpl entity = (HibernateOrmEntityImpl) mapping;
		HibernateOrmTable table = entity.getTable();
		assertEquals("tn_ManyToMany22", table.getDBTableName());

		ArrayList<OrmReadOnlyPersistentAttribute> attrs = CollectionTools.list(ormPersistentType.attributes());
		assertTrue(attrs.size() == 3);

		//id
		assertTrue(attrs.get(0).getMapping() instanceof HibernateOrmIdMapping);
		HibernateOrmIdMapping hjidm = (HibernateOrmIdMapping)attrs.get(0).getMapping();
		HibernateOrmColumn hjc = (HibernateOrmColumn)hjidm.getColumn();
		assertEquals("cn_id", hjc.getDBColumnName());

		//justData
		assertTrue(attrs.get(1).getMapping() instanceof HibernateOrmBasicMapping);
		HibernateOrmBasicMapping hjbm = (HibernateOrmBasicMapping)attrs.get(1).getMapping();
		hjc = (HibernateOrmColumn)hjbm.getColumn();
		assertEquals("cn_simpleData", hjc.getDBColumnName());

		//mtm
		assertTrue(attrs.get(2).getMapping() instanceof HibernateOrmManyToManyMapping);
		HibernateOrmManyToManyMapping hjmtmm = (HibernateOrmManyToManyMapping)attrs.get(2).getMapping();
		assertEquals("entity.ManyToMany1", hjmtmm.getTargetEntity());
		OrmJoinTableRelationshipStrategy jtJoiningStrategy = hjmtmm.getRelationship().getJoinTableStrategy();
		OrmJoinTable joinTable = jtJoiningStrategy.getJoinTable();
		assertTrue(joinTable instanceof HibernateOrmJoinTable);
		HibernateOrmJoinTable hjjt = (HibernateOrmJoinTable)joinTable;
		assertEquals("col_entity.ManyToMany2_entity.ManyToMany1_ManyToMany1_entity.ManyToMany1_mtm1", hjjt.getDBTableName());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if(project != null) {
			boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
			try {
				project.delete(true,true, null);
			} finally {
				ResourcesUtils.setBuildAutomatically(saveAutoBuild);
			}
		}
	}

}
