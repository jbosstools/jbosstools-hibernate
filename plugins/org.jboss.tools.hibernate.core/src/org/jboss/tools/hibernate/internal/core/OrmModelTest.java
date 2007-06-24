/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.internal.core;

import org.eclipse.core.resources.IProject;
	
import java.util.Iterator;

import org.apache.commons.collections.SequencedHashMap;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.jboss.tools.hibernate.core.IDatabaseSchema;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IJavaLoggingProperties;
import org.jboss.tools.hibernate.core.ILog4JProperties;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IOrmConfiguration;
import org.jboss.tools.hibernate.core.IOrmModelVisitor;
import org.jboss.tools.hibernate.core.IOrmProject;
import org.jboss.tools.hibernate.core.IOrmProjectChangedListener;
import org.jboss.tools.hibernate.core.IPackage;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.OrmProjectEvent;
import org.jboss.tools.hibernate.internal.core.data.Schema;
import org.jboss.tools.hibernate.internal.core.data.Table;

	/**
	 * @author Tau
	 *  
	 */
	public abstract class OrmModelTest extends AbstractOrmElement implements IOrmProject{

		private IProject project;
		private SequencedHashMap classes=new SequencedHashMap();
		private SequencedHashMap tables=new SequencedHashMap();
		private SequencedHashMap schemas=new SequencedHashMap();
		private SequencedHashMap packages=new SequencedHashMap();
		private IMapping mapping;
		private IOrmConfiguration config;
		private Log4JProperties log4jProperties;
		private IJavaLoggingProperties javaLoggingProperties;
		private SequencedHashMap listeners=new SequencedHashMap();
		
		private static final IDatabaseTable[] TABLES={};
		private static final IPersistentClass[] CLASSES={};
		private static final IDatabaseSchema[] SCHEMAS={};
		private static final IPackage[] PACKAGES={};
		
		
		/**
		 *  
		 */
		public OrmModelTest(IProject project) {
			super();
			this.project=project;
			reload();
		}
		
		public OrmModelTest() {
			super();
		}
		
		/**
		 * @return Returns the project.
		 */
		public IProject getProject() {
			return project;
		}
///added gavrs 22.3.2005		
		public void addDataBaseTables(IDatabaseTable[] tables,boolean generateMapping){
			//IDatabaseTable[] dataBaseTables=new Table[fullyQualifiedNames.length];
			//for(int i = 0 ; i < fullyQualifiedNames.length ; i++) {
				//dataBaseTables[i]=getOrCreateTable(fullyQualifiedNames[i]);
				//tables.put(fullyQualifiedNames,dataBaseTables[i]);
				
	        //}
		}
//-------------------------------
		protected void reload(){
			//	create/refresh mappings, configurations etc
			/* del tau
			HibernateMapping hm=new HibernateMapping(project,(OrmProject) this);
			this.mapping=hm;
			config=new OrmConfiguration();
			log4jProperties=new Log4JProperties(project);
			
			IPersistentClassMapping[] maps=mapping.getPersistentClassMappings();
			//TODO (tau(5)-> tau) Remove the class
			for(int i=0;i<maps.length;++i){
				PersistentClass pc=this.getOrCreatePersistentClass(maps[i].getName());
				pc.setPersistentClassMapping(maps[i]);
				Iterator it=maps[i].getFieldMappingIterator();
				while(it.hasNext()){
					AbstractFieldMapping fm=(AbstractFieldMapping)it.next();
					PersistentField pf=pc.getOrCreateField(fm.getName());
					fm.setPersistentField(pf);
					pf.setMapping(fm);
				}
			}
			*/
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.jboss.tools.hibernate.core.IOrmProject#findClass(java.lang.String)
		 */
		public IPersistentClass findClass(String fullyQualifiedName) {
			return (IPersistentClass)classes.get(fullyQualifiedName);
		}
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.jboss.tools.hibernate.core.IOrmProject#findTable(java.lang.String)
		 */
		public IDatabaseTable findTable(String fullyQualifiedName) {
			return (IDatabaseTable)tables.get(fullyQualifiedName);
		}
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.jboss.tools.hibernate.core.IOrmProject#getConfiguration()
		 */
		public IOrmConfiguration getOrmConfiguration() {
			return config;
		}
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.jboss.tools.hibernate.core.IOrmProject#getDatabaseTables()
		 */
		public IDatabaseTable[] getDatabaseTables() {
			return (IDatabaseTable[])tables.values().toArray(TABLES);
		}
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.jboss.tools.hibernate.core.IOrmProject#getMapping()
		 */
		public IMapping getMapping() {
			
			return mapping;
		}
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.jboss.tools.hibernate.core.IOrmProject#getPertsistentClasses()
		 */
		public IPersistentClass[] getPertsistentClasses() {
			return (IPersistentClass[])classes.values().toArray(CLASSES);
		}
		
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.jboss.tools.hibernate.core.IOrmProject#getDatabaseSchemas()
		 */
		public IDatabaseSchema[] getDatabaseSchemas() {
			return (IDatabaseSchema[])schemas.values().toArray(SCHEMAS);
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.jboss.tools.hibernate.core.IOrmProject#getPackages()
		 */
		public IPackage[] getPackages() {
			return (IPackage[])packages.values().toArray(PACKAGES);
		}
		
		public PersistentClass getOrCreatePersistentClass(String fullyQualifiedName){
			PersistentClass res=(PersistentClass)classes.get(fullyQualifiedName);
			if(res==null){
				res=new PersistentClass();
				res.setName(fullyQualifiedName);
				classes.put(fullyQualifiedName,res);
				Package pack=getOrCreatePackage(res.getPackageName());
				pack.addPersistentClass(res);
				res.setPackage(pack);
			}
			return res;
		}
		
		public Package getOrCreatePackage(String fullyQualifiedName){
			Package res=(Package)packages.get(fullyQualifiedName);
			if(res==null){
				res=new Package(mapping);
				res.setName(fullyQualifiedName);
				packages.put(fullyQualifiedName,res);
			}
			return res;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.jboss.tools.hibernate.core.IOrmProject#getLog4jProperties()
		 */
		public ILog4JProperties getLog4jProperties() {
			return log4jProperties;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.jboss.tools.hibernate.core.IOrmProject#getJavaLoggingProperties()
		 */
		public IJavaLoggingProperties getJavaLoggingProperties() {
			return javaLoggingProperties;
		}
		
		public Table getOrCreateTable(String fullyQualifiedName){
			Table res=(Table)tables.get(fullyQualifiedName);
			if(res==null){
				res=new Table();
				res.setName(fullyQualifiedName);
				tables.put(fullyQualifiedName,res);
				Schema schema=getOrCreateSchema(res.getSchemaName());
				schema.addDatabaseTable(res);
				res.setSchema(schema);
			}
			return res;
			
		}
		public Schema getOrCreateSchema(String fullyQualifiedName){
			Schema res=(Schema)schemas.get(fullyQualifiedName);
			if(res==null){
				res=new Schema(mapping);
				res.setName(fullyQualifiedName);
				schemas.put(fullyQualifiedName,res);
			}
			return res;
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.jboss.tools.hibernate.core.IOrmProject#addOrmModelEventListener(org.jboss.tools.hibernate.core.IOrmProjectListener)
		 */
		public void addChangedListener(IOrmProjectChangedListener listener) {
			listeners.put(listener,listener);
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.jboss.tools.hibernate.core.IOrmProject#removeOrmModelEventListener(org.jboss.tools.hibernate.core.IOrmProjectListener)
		 */
		public void removeChangedListener(IOrmProjectChangedListener listener) {
			listeners.remove(listener);
		}
		
		protected void fireOrmModelEvent(final OrmProjectEvent event){
			Iterator it=listeners.values().iterator();
			while(it.hasNext()){
				final IOrmProjectChangedListener listener = (IOrmProjectChangedListener)it.next();
				Platform.run(new ISafeRunnable() {
					public void handleException(Throwable exception) {
						OrmCore.log(exception, "Exception occurred in listener of OrmProject events");
					}
					public void run() throws Exception {
						listener.projectChanged(event, false);
					}
				});
			}
		}
		
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.jboss.tools.hibernate.core.IOrmElement#accept(org.jboss.tools.hibernate.core.IOrmModelVisitor,
		 *      java.lang.Object)
		 */
		public Object accept(IOrmModelVisitor visitor, Object argument) {
			return visitor.visitOrmProject(this,argument);
		}
		
		
		/* (non-Javadoc)
		 * @see org.jboss.tools.hibernate.core.IOrmProject#addPersistentClasses(java.lang.String[], boolean)
		 */
		public void addPersistentClasses(String[] fullyQualifiedNames,
				boolean generateMapping) {

		}
	}
