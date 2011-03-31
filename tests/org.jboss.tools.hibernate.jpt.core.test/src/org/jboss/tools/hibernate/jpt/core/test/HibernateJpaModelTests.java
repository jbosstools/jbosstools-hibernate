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
import org.eclipse.jpt.jpa.core.context.java.JavaJoinTable;
import org.eclipse.jpt.jpa.core.context.java.JavaJoinTableRelationshipStrategy;
import org.eclipse.jpt.jpa.core.context.java.JavaPersistentAttribute;
import org.eclipse.jpt.jpa.core.context.java.JavaPersistentType;
import org.eclipse.jpt.jpa.core.context.java.JavaTypeMapping;
import org.eclipse.jpt.jpa.core.context.persistence.ClassRef;
import org.eclipse.jpt.jpa.core.context.persistence.Persistence;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaBasicMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaColumn;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaEntity;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaIdMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaJoinTable;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaManyToManyMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaTable;
import org.jboss.tools.test.util.ResourcesUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJpaModelTests {

	private static final String PROJECT_NAME = "testHibernateJpaProject";
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
		List<ClassRef> crs = CollectionTools.list(hpu.classRefs());
		assertTrue(crs.size() == 2);
		if (crs.get(0).isFor("entity.ManyToMany1")){
			checkManyToMany1(crs.get(0));
			checkManyToMany2(crs.get(1));
		} else {
			checkManyToMany1(crs.get(1));
			checkManyToMany2(crs.get(0));
		}
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
		List<ClassRef> crs = CollectionTools.list(hpu.classRefs());
		assertTrue(crs.size() == 2);
		if (crs.get(0).isFor("entity.ManyToMany1")){
			checkManyToManyNS1(crs.get(0));
			checkManyToManyNS2(crs.get(1));
		} else {
			checkManyToManyNS1(crs.get(1));
			checkManyToManyNS2(crs.get(0));
		}
		cc.reset();
	}

	public void checkManyToMany1(ClassRef crs){
		JavaPersistentType javaPersistentType = crs.getJavaPersistentType();
		JavaTypeMapping mapping = javaPersistentType.getMapping();
		assertTrue(mapping instanceof HibernateJavaEntity);
		HibernateJavaEntity entity = (HibernateJavaEntity) mapping;
		HibernateJavaTable table = entity.getTable();
		assertEquals("ManyToMany1", table.getDBTableName());

		ArrayList<JavaPersistentAttribute> attrs = CollectionTools.list(javaPersistentType.attributes());
		assertTrue(attrs.size() == 3);

		//id
		assertTrue(attrs.get(0).getMapping() instanceof HibernateJavaIdMapping);
		HibernateJavaIdMapping hjidm = (HibernateJavaIdMapping)attrs.get(0).getMapping();
		HibernateJavaColumn hjc = (HibernateJavaColumn)hjidm.getColumn();
		assertEquals("id1", hjc.getDBColumnName());

		//justData
		assertTrue(attrs.get(1).getMapping() instanceof HibernateJavaBasicMapping);
		HibernateJavaBasicMapping hjbm = (HibernateJavaBasicMapping)attrs.get(1).getMapping();
		hjc = (HibernateJavaColumn)hjbm.getColumn();
		assertEquals("justData1", hjc.getDBColumnName());

		//mtm
		assertTrue(attrs.get(2).getMapping() instanceof HibernateJavaManyToManyMapping);
		HibernateJavaManyToManyMapping hjmtmm = (HibernateJavaManyToManyMapping)attrs.get(2).getMapping();
		assertEquals("entity.ManyToMany2", hjmtmm.getTargetEntity());
	}

	public void checkManyToMany2(ClassRef crs){
		JavaPersistentType javaPersistentType = crs.getJavaPersistentType();
		JavaTypeMapping mapping = javaPersistentType.getMapping();
		assertTrue(mapping instanceof HibernateJavaEntity);
		HibernateJavaEntity entity = (HibernateJavaEntity) mapping;
		HibernateJavaTable table = entity.getTable();
		assertEquals("ManyToMany22", table.getDBTableName());

		ArrayList<JavaPersistentAttribute> attrs = CollectionTools.list(javaPersistentType.attributes());
		assertTrue(attrs.size() == 3);
		//id
		assertTrue(attrs.get(0).getMapping() instanceof HibernateJavaIdMapping);
		HibernateJavaIdMapping hjidm = (HibernateJavaIdMapping)attrs.get(0).getMapping();
		HibernateJavaColumn hjc = (HibernateJavaColumn)hjidm.getColumn();
		assertEquals("id", hjc.getDBColumnName());

		//justData
		assertTrue(attrs.get(1).getMapping() instanceof HibernateJavaBasicMapping);
		HibernateJavaBasicMapping hjbm = (HibernateJavaBasicMapping)attrs.get(1).getMapping();
		hjc = (HibernateJavaColumn)hjbm.getColumn();
		assertEquals("justData", hjc.getDBColumnName());

		//mtm
		assertTrue(attrs.get(2).getMapping() instanceof HibernateJavaManyToManyMapping);
		HibernateJavaManyToManyMapping hjmtmm = (HibernateJavaManyToManyMapping)attrs.get(2).getMapping();
		assertEquals("entity.ManyToMany1", hjmtmm.getTargetEntity());
		JavaJoinTableRelationshipStrategy jtJoiningStrategy = hjmtmm.getRelationship().getJoinTableStrategy();
		JavaJoinTable joinTable = jtJoiningStrategy.getJoinTable();
		assertTrue(joinTable instanceof HibernateJavaJoinTable);
		HibernateJavaJoinTable hjjt = (HibernateJavaJoinTable)joinTable;
		assertEquals("ManyToMany22_ManyToMany1", hjjt.getDBTableName());
	}

	public void checkManyToManyNS1(ClassRef crs){
		JavaPersistentType javaPersistentType = crs.getJavaPersistentType();
		JavaTypeMapping mapping = javaPersistentType.getMapping();
		assertTrue(mapping instanceof HibernateJavaEntity);
		HibernateJavaEntity entity = (HibernateJavaEntity) mapping;
		HibernateJavaTable table = entity.getTable();
		assertEquals("ctn_ManyToMany1", table.getDBTableName());

		ArrayList<JavaPersistentAttribute> attrs = CollectionTools.list(javaPersistentType.attributes());
		assertTrue(attrs.size() == 3);

		//id
		assertTrue(attrs.get(0).getMapping() instanceof HibernateJavaIdMapping);
		HibernateJavaIdMapping hjidm = (HibernateJavaIdMapping)attrs.get(0).getMapping();
		HibernateJavaColumn hjc = (HibernateJavaColumn)hjidm.getColumn();
		assertEquals("pc_id1", hjc.getDBColumnName());

		//justData
		assertTrue(attrs.get(1).getMapping() instanceof HibernateJavaBasicMapping);
		HibernateJavaBasicMapping hjbm = (HibernateJavaBasicMapping)attrs.get(1).getMapping();
		hjc = (HibernateJavaColumn)hjbm.getColumn();
		assertEquals("pc_justData1", hjc.getDBColumnName());

		//mtm
		assertTrue(attrs.get(2).getMapping() instanceof HibernateJavaManyToManyMapping);
		HibernateJavaManyToManyMapping hjmtmm = (HibernateJavaManyToManyMapping)attrs.get(2).getMapping();
		assertEquals("entity.ManyToMany2", hjmtmm.getTargetEntity());
	}

	public void checkManyToManyNS2(ClassRef crs){
		JavaPersistentType javaPersistentType = crs.getJavaPersistentType();
		JavaTypeMapping mapping = javaPersistentType.getMapping();
		assertTrue(mapping instanceof HibernateJavaEntity);
		HibernateJavaEntity entity = (HibernateJavaEntity) mapping;
		HibernateJavaTable table = entity.getTable();
		assertEquals("tn_ManyToMany22", table.getDBTableName());

		ArrayList<JavaPersistentAttribute> attrs = CollectionTools.list(javaPersistentType.attributes());
		assertTrue(attrs.size() == 3);
		//id
		assertTrue(attrs.get(0).getMapping() instanceof HibernateJavaIdMapping);
		HibernateJavaIdMapping hjidm = (HibernateJavaIdMapping)attrs.get(0).getMapping();
		HibernateJavaColumn hjc = (HibernateJavaColumn)hjidm.getColumn();
		assertEquals("cn_id", hjc.getDBColumnName());

		//justData
		assertTrue(attrs.get(1).getMapping() instanceof HibernateJavaBasicMapping);
		HibernateJavaBasicMapping hjbm = (HibernateJavaBasicMapping)attrs.get(1).getMapping();
		hjc = (HibernateJavaColumn)hjbm.getColumn();
		assertEquals("cn_justData", hjc.getDBColumnName());

		//mtm
		assertTrue(attrs.get(2).getMapping() instanceof HibernateJavaManyToManyMapping);
		HibernateJavaManyToManyMapping hjmtmm = (HibernateJavaManyToManyMapping)attrs.get(2).getMapping();
		assertEquals("entity.ManyToMany1", hjmtmm.getTargetEntity());
		JavaJoinTableRelationshipStrategy jtJoiningStrategy = hjmtmm.getRelationship().getJoinTableStrategy();
		JavaJoinTable joinTable = jtJoiningStrategy.getJoinTable();
		assertTrue(joinTable instanceof HibernateJavaJoinTable);
		HibernateJavaJoinTable hjjt = (HibernateJavaJoinTable)joinTable;
		hjjt.getDbTable();
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
