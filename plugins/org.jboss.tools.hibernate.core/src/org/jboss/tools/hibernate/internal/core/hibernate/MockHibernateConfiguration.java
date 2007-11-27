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
package org.jboss.tools.hibernate.internal.core.hibernate;

import java.io.IOException;
import java.util.Properties;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.TreeItem;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IMappingStorage;
import org.jboss.tools.hibernate.core.INamedQueryMapping;
import org.jboss.tools.hibernate.core.IOrmModelVisitor;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.internal.core.data.Column;
import org.jboss.tools.hibernate.internal.core.data.Table;
import org.jboss.tools.hibernate.internal.core.util.StringUtils;

/**
 * @author alex
 *
 * Test hibernate mapping
 */
public class MockHibernateConfiguration extends HibernateConfiguration {

	/**
	 * @param project
	 * @param model
	 */
	public MockHibernateConfiguration( HibernateMapping hbm) {
		super(hbm,null);
		Table table=hbm.getOrCreateTable("SCHEMA.TABLE1");
		
		Column col=new Column();
		col.setName("COL1");
		col.setSqlTypeName("LONG");
		col.setOwnerTable(table);
		table.addColumn(col);
		
		RootClassMapping cm=new RootClassMapping();
		cm.setClassName("com.test.PersistentClass1");
		cm.setDatabaseTable(table);
		table.addPersistentClassMapping(cm);
		
		PropertyMapping pm=new PropertyMapping();
		pm.setName("field1");
		
		SimpleValueMapping svm=new SimpleValueMapping();
		svm.addColumn(col);
		svm.setTable(table);
		pm.setValue(svm);
		
		cm.addProperty(pm);
		pm.setPersistentClassMapping(cm);
		
		final IResource res=hbm.getProject().getProject();
		final IPersistentClassMapping[] maps=new IPersistentClassMapping[]{cm};
		this.addMappingStorage(new IMappingStorage(){
				public Object accept(IOrmModelVisitor visitor, Object argument) {
					return visitor.visitMappingStorage(this,argument);
				}
			
				/* (non-Javadoc)
				 * @see org.jboss.tools.hibernate.core.IMappingStorage#getProjectMapping()
				 */
				public IMapping getProjectMapping() {
					return null;
				}

				public String getName(){
					return "Test storage";
				}
				public IResource getResource(){
					return res;
				}
				public IPersistentClassMapping[] getPersistentClassMappings(){
					return  maps;
				}
				public Object getAdapter(Class adapter) {
					return null;
				}
				
				/* (non-Javadoc)
				 * @see org.jboss.tools.hibernate.core.IMappingStorage#reload()
				 */
				public void reload() throws IOException, CoreException {
					// TODO Auto-generated method stub
					
				}

				/* (non-Javadoc)
				 * @see org.jboss.tools.hibernate.core.IMappingStorage#save()
				 */
				public void save() throws IOException, CoreException {
					// TODO Auto-generated method stub
				}

				public void addPersistentClassMapping(IPersistentClassMapping m){}
				public void removePersistentClassMapping(IPersistentClassMapping m){}

                public boolean resourceChanged() {
                    // TODO Auto-generated method stub
                    return true;
                }

					public INamedQueryMapping[] getNamedQueryMappings() {
						// TODO Auto-generated method stub
						return null;
					}

					public void addNamedQueryMapping(INamedQueryMapping mapping) {
						// TODO Auto-generated method stub
						
					}

					public void removeNamedQueryMapping(INamedQueryMapping mapping) {
						// TODO Auto-generated method stub
						
					}

					public int getPersistentClassMappingCount() {
						// TODO Auto-generated method stub
						return 0;
					}

					public void setResource(IResource res) {
						// TODO Auto-generated method stub
						
					}

					// added by yk 19.10.2005
					public void addFilterDef(String fdname, Properties prop) {
						// TODO Auto-generated method stub
						
					}

					public FilterDef getFilterDef(String key) {
						// TODO Auto-generated method stub
						return null;
					}

					public int getFilterDefSize() {
						// TODO Auto-generated method stub
						return 0;
					}
					// added by yk 19.10.2005.
					
					public String getQualifiedName(TreeItem item) {
						//return (String) accept(QualifiedNameVisitor.visitor,item);
						// add 02.12.2005
						return StringUtils.parentItemName(item, this.getName());						
					}
					

					public int getNamedQueryMappingCount() {
						// TODO Auto-generated method stub
						return 0;
					}

					public boolean isResourceChanged() {
						// TODO Auto-generated method stub
						return false;
					}

					public void save(boolean force) throws IOException, CoreException {
						// TODO Auto-generated method stub
						
					}

					public boolean isDirty() {
						// TODO Auto-generated method stub
						return false;
					}

					public void setDirty(boolean dirty) {
						// TODO Auto-generated method stub
						
					}

		});
	}

}
