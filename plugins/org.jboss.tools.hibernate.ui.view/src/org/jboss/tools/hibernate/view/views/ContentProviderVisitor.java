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
package org.jboss.tools.hibernate.view.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.hibernate.mapping.Column;
import org.jboss.tools.hibernate.view.ViewPlugin;


/**
 * @author Tau from Minsk
 * 
 */
public class ContentProviderVisitor {
	
	private static final Object[] nullChildren = new Object[0];

    private ResourceBundle BUNDLE = ResourceBundle.getBundle(ContentProviderVisitor.class.getPackage().getName() + ".views");
	
//	public Object visitOrmProject(IOrmProject project, Object argument) {
//		return project.getMappings();
//	}
//
//	public Object visitDatabaseSchema(IDatabaseSchema schema, Object argument) {
//		return schema.getDatabaseTables();
//	}
//
//	public Object visitDatabaseTable(IDatabaseTable table, Object argument) {
//		return table.getColumns();
//	}
//
//	public Object visitDatabaseColumn(IDatabaseColumn column, Object argument) {
//		return nullChildren;
//	}
//
//	public Object visitDatabaseConstraint(IDatabaseConstraint constraint, Object argument) {
//		return nullChildren;
//	}
//
//	public Object visitPackage(IPackage pakage, Object argument) {
//		return pakage.getPersistentClasses();
//	}
//
//	public Object visitMapping(final IMapping mapping, final Object argument) {
//		// start job
//		if (mapping.isFlagDirty()) {		
//	   		//if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW )	ExceptionHandler.logInfo("0-ContentProviderVisitor.visitMapping(...), parentElement= " + mapping);				
//			Job job = new Job(BUNDLE.getString("Job.LoadMapping")) {
//			     protected IStatus run(IProgressMonitor monitor) {
//						((OrmContentProvider)argument).lockMenu = true; // add tau 09.03.2006			    	 
//			    	 	mapping.getMappingStorages();
//			            return Status.OK_STATUS;
//			     }
//			};
//			
//			job.addJobChangeListener(new JobChangeAdapter() {
//			       public void done(IJobChangeEvent event) {
//			    	   if (event.getJob().getState() != Job.NONE) return; // Job not finished
//			    	   //if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW )	ExceptionHandler.logInfo("1-ContentProviderVisitor.visitMapping(...), parentElement= " + mapping);
//					   ((OrmContentProvider)argument).lockMenu = false; // add tau 09.03.2006			    	   
//			    	   if (event.getResult().isOK()){
//			    	   		if (ViewPlugin.TRACE || ViewPlugin.TRACE_VIEW )	ExceptionHandler.logInfo("2-ContentProviderVisitor.visitMapping(...), parentElement= " + mapping);				    		   
//			    	   		((OrmContentProvider)argument).viewerRefreshUpdate(mapping, false);
//			    	   }
//			       }
//			});
//			job.setPriority(Job.INTERACTIVE);
//			job.schedule(); // start as soon as possible
//			return new String []{BUNDLE.getString("Job.LoadMapping")};
//		} else {
//			if (((OrmContentProvider)argument).getTip() == OrmContentProvider.CLASS_FIELD_CONTENT_PROVIDER) return mapping.getPertsistentClasses();
//			else if (((OrmContentProvider)argument).getTip() == OrmContentProvider.PACKAGE_CLASS_FIELD_CONTENT_PROVIDER) return mapping.getPackages();
//			else if (((OrmContentProvider)argument).getTip() == OrmContentProvider.STORAGE_CLASS_FIELD_CONTENT_PROVIDER) return mapping.getMappingStorages();
//			else if (((OrmContentProvider)argument).getTip() == OrmContentProvider.SCHEMA_TABLE_COLUMN_CONTENT_PROVIDER) return mapping.getDatabaseSchemas(); 
//			else if (((OrmContentProvider)argument).getTip() == OrmContentProvider.TABLE_COLUMN_CONTENT_PROVIDER) return mapping.getDatabaseTables();		
//			else return nullChildren;
//		}
//		
//	}
//
//	public Object visitMappingStorage(IMappingStorage storage, Object argument) {
//		//storage.getNamedQueryMappings(); 26.07.2005
//		//return 	storage.getPersistentClassMappings();
//		// add tau 27.07.2005
//    	Object[] namedQueryMappings = storage.getNamedQueryMappings();
//    	Object[] persistentClassMappings = storage.getPersistentClassMappings();
//    	Object[] mappings = new Object[namedQueryMappings.length + persistentClassMappings.length];
//    	System.arraycopy(namedQueryMappings, 0, mappings, 0, namedQueryMappings.length);		
//    	System.arraycopy(persistentClassMappings, 0, mappings, namedQueryMappings.length, persistentClassMappings.length);
//    	
//    	((OrmContentProvider)argument).setSorting(false); // add 28.07.2005 tau    	
//    	return mappings;
//	}
//
//	public Object visitPersistentClass(IPersistentClass clazz, Object argument) {
//		((OrmContentProvider)argument).setSorting(false);		
//		return clazz.getFields();
//	}
//
//	public Object visitPersistentField(IPersistentField field, Object argument) {
//		IPersistentFieldMapping mapping = field.getMapping();
//		if (mapping != null){
//			return mapping.accept(this, argument);
//		}
//		
//		return nullChildren;		
//	}
//
//	public Object visitPersistentClassMapping(IPersistentClassMapping mapping, Object argument) {
//		IPersistentClass persistentClass = mapping.getPersistentClass();
//		if (persistentClass != null){
//			((OrmContentProvider)argument).setSorting(false);
//			return persistentClass.getFields();
//		}
//		return nullChildren;
//	}
//
//	public Object visitPersistentFieldMapping(IPersistentFieldMapping mapping, Object argument) {
//		IPersistentValueMapping valueMapping = mapping.getPersistentValueMapping();
//		if (valueMapping != null){
//			return valueMapping.accept(this, argument);
//		}
//		return nullChildren;
//	}
//
//	public Object visitPersistentValueMapping(IPersistentValueMapping mapping, Object argument) {
//        // added by Nick 06.09.2005
//        ((OrmContentProvider)argument).setSorting(false);
//        // by Nick
//        
//        Iterator iter = mapping.getColumnIterator();
//		if (iter != null){
//			ArrayList list = new ArrayList();
//			while (iter.hasNext()) {
//				list.add(iter.next());
//			}
//			return list.toArray();
//		}
//		return nullChildren;
//	}
//
//	private Object[] visitCollectionMapping(ICollectionMapping mapping, Object argument){
//		IHibernateValueMapping elem=mapping.getElement();
//		// added by Nick 06.09.2005
//        ((OrmContentProvider)argument).setSorting(false);
//        // by Nick
//        IHibernateValueMapping key=mapping.getKey();
//	    return new Object[]{key,elem};	
//	}
//	private Object[] visitIndexedCollectionMapping(IIndexedCollectionMapping mapping, Object argument){
//		IHibernateValueMapping elem=mapping.getElement();
//		// added by Nick 06.09.2005
//        ((OrmContentProvider)argument).setSorting(false);
//        // by Nick
//        IHibernateValueMapping key=mapping.getKey();
//	    return new Object[]{key, elem, mapping.getIndex()};	
//	}
//	
//	/* (non-Javadoc)
//	 * @see org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor#visitAnyMapping(org.jboss.tools.hibernate.core.hibernate.IAnyMapping, java.lang.Object)
//	 */
//	public Object visitAnyMapping(IAnyMapping mapping, Object argument) {
//		return visitPersistentValueMapping(mapping, argument);
//	}
//
//	/* (non-Javadoc)
//	 * @see org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor#visitArrayMapping(org.jboss.tools.hibernate.core.hibernate.IArrayMapping, java.lang.Object)
//	 */
//	public Object visitArrayMapping(IArrayMapping mapping, Object argument) {
//		return visitIndexedCollectionMapping(mapping, argument);
//	}
//
//	/* (non-Javadoc)
//	 * @see org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor#visitBagMapping(org.jboss.tools.hibernate.core.hibernate.IBagMapping, java.lang.Object)
//	 */
//	public Object visitBagMapping(IBagMapping mapping, Object argument) {
//		return visitCollectionMapping(mapping, argument);
//	}
//
//	/* (non-Javadoc)
//	 * @see org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor#visitComponentMapping(org.jboss.tools.hibernate.core.hibernate.IComponentMapping, java.lang.Object)
//	 */
//	public Object visitComponentMapping(IComponentMapping mapping, Object argument) {
//		//changed on 06/07/05 by alex
//		((OrmContentProvider)argument).setSorting(false);
//		return mapping.getComponentClass().getFields();
///*		
// * 		Iterator iter = mapping.getPropertyIterator();
//		if (iter != null){
//			ArrayList list = new ArrayList();
//			while (iter.hasNext()) {
//                //added 13.05.2005 by Nick - show type for component elements
//                Object obj = iter.next();
//                boolean added = false;
//                if (obj instanceof IPersistentFieldMapping) {
//                    IPersistentFieldMapping fm = (IPersistentFieldMapping) obj;
//                    if (fm.getPersistentField() != null)
//                    {
//                        added = true;
//                        list.add(fm.getPersistentField());
//                    }
//                }
//                if (!added)
//                    list.add(obj);
//                //by Nick
//			}
//			return list.toArray(nullChildren);
//		}*/
//		//return nullChildren;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor#visitIdBagMapping(org.jboss.tools.hibernate.core.hibernate.IIdBagMapping, java.lang.Object)
//	 */
//	public Object visitIdBagMapping(IIdBagMapping mapping, Object argument) {
//		IHibernateValueMapping elem=mapping.getElement();
//		IHibernateValueMapping key=mapping.getKey();
//		IHibernateValueMapping id=mapping.getIdentifier();
//	    return new Object[]{key,elem, id};	
//	}
//
//	/* (non-Javadoc)
//	 * @see org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor#visitJoinedSubclassMapping(org.jboss.tools.hibernate.core.hibernate.IJoinedSubclassMapping, java.lang.Object)
//	 */
//	public Object visitJoinedSubclassMapping(IJoinedSubclassMapping mapping, Object argument) {
//		return visitPersistentClassMapping(mapping, argument);
//	}
//
//	/* (non-Javadoc)
//	 * @see org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor#visitJoinMapping(org.jboss.tools.hibernate.core.hibernate.IJoinMapping, java.lang.Object)
//	 */
//	public Object visitJoinMapping(IJoinMapping mapping, Object argument) {
//		return null;
//	}
//
//	/* (non-Javadoc)
//	 * @see org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor#visitListMapping(org.jboss.tools.hibernate.core.hibernate.IListMapping, java.lang.Object)
//	 */
//	public Object visitListMapping(IListMapping mapping, Object argument) {
//		return visitIndexedCollectionMapping(mapping, argument);
//	}
//
//	/* (non-Javadoc)
//	 * @see org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor#visitManyToAnyMapping(org.jboss.tools.hibernate.core.hibernate.IManyToAnyMapping, java.lang.Object)
//	 */
//	public Object visitManyToAnyMapping(IManyToAnyMapping mapping, Object argument) {
//		return visitPersistentValueMapping(mapping, argument);
//	}
//
//	/* (non-Javadoc)
//	 * @see org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor#visitManyToManyMapping(org.jboss.tools.hibernate.core.hibernate.IManyToManyMapping, java.lang.Object)
//	 */
//	public Object visitManyToManyMapping(IManyToManyMapping mapping, Object argument) {
//		return visitPersistentValueMapping(mapping, argument);
//	}
//
//	/* (non-Javadoc)
//	 * @see org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor#visitManyToOneMapping(org.jboss.tools.hibernate.core.hibernate.IManyToOneMapping, java.lang.Object)
//	 */
//	public Object visitManyToOneMapping(IManyToOneMapping mapping, Object argument) {
//        return visitPersistentValueMapping(mapping, argument);
//	}
//
//	/* (non-Javadoc)
//	 * @see org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor#visitMapMapping(org.jboss.tools.hibernate.core.hibernate.IMapMapping, java.lang.Object)
//	 */
//	public Object visitMapMapping(IMapMapping mapping, Object argument) {
//		return visitIndexedCollectionMapping(mapping, argument);
//	}
//
//	/* (non-Javadoc)
//	 * @see org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor#visitOneToManyMapping(org.jboss.tools.hibernate.core.hibernate.IOneToManyMapping, java.lang.Object)
//	 */
//	public Object visitOneToManyMapping(IOneToManyMapping mapping, Object argument) {
//		return visitPersistentValueMapping(mapping, argument);
//	}
//
//	/* (non-Javadoc)
//	 * @see org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor#visitOneToOneMapping(org.jboss.tools.hibernate.core.hibernate.IOneToOneMapping, java.lang.Object)
//	 */
//	public Object visitOneToOneMapping(IOneToOneMapping mapping, Object argument) {
//		return visitPersistentValueMapping(mapping, argument);
//	}
//
//	/* (non-Javadoc)
//	 * @see org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor#visitPrimitiveArrayMapping(org.jboss.tools.hibernate.core.hibernate.IPrimitiveArrayMapping, java.lang.Object)
//	 */
//	public Object visitPrimitiveArrayMapping(IPrimitiveArrayMapping mapping, Object argument) {
//		return visitIndexedCollectionMapping(mapping, argument);
//	}
//
//	/* (non-Javadoc)
//	 * @see org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor#visitPropertyMapping(org.jboss.tools.hibernate.core.hibernate.IPropertyMapping, java.lang.Object)
//	 */
//	public Object visitPropertyMapping(IPropertyMapping mapping, Object argument) {
//		return visitPersistentFieldMapping(mapping, argument);
//	}
//
//	/* (non-Javadoc)
//	 * @see org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor#visitRootClassMapping(org.jboss.tools.hibernate.core.hibernate.IRootClassMapping, java.lang.Object)
//	 */
//	public Object visitRootClassMapping(IRootClassMapping mapping, Object argument) {
//		return visitPersistentClassMapping(mapping, argument);
//	}
//
//	/* (non-Javadoc)
//	 * @see org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor#visitSetMapping(org.jboss.tools.hibernate.core.hibernate.ISetMapping, java.lang.Object)
//	 */
//	public Object visitSetMapping(ISetMapping mapping, Object argument) {
//		return visitCollectionMapping(mapping, argument);
//	}
//
//	/* (non-Javadoc)
//	 * @see org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor#visitSimpleValueMapping(org.jboss.tools.hibernate.core.hibernate.ISimpleValueMapping, java.lang.Object)
//	 */
//	public Object visitSimpleValueMapping(ISimpleValueMapping simple, Object argument) {
//		return visitPersistentValueMapping(simple, argument);
//	}
//
//	/* (non-Javadoc)
//	 * @see org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor#visitSubclassMapping(org.jboss.tools.hibernate.core.hibernate.ISubclassMapping, java.lang.Object)
//	 */
//	public Object visitSubclassMapping(ISubclassMapping mapping, Object argument) {
//		return visitPersistentClassMapping(mapping, argument);
//	}
//
//	/* (non-Javadoc)
//	 * @see org.jboss.tools.hibernate.core.hibernate.IHibernateMappingVisitor#visitUnionSubclassMapping(org.jboss.tools.hibernate.core.hibernate.IUnionSubclassMapping, java.lang.Object)
//	 */
//	public Object visitUnionSubclassMapping(IUnionSubclassMapping mapping, Object argument) {
//		return visitPersistentClassMapping(mapping, argument);
//	}
//
//	// add tau 27.07.2005
//	public Object visitNamedQueryMapping(INamedQueryMapping mapping, Object argument) {
//		return null;
//	}

	public Object visitDatabaseColumn(Column column, Object argument) {
		// TODO Auto-generated method stub
		return null;
	}

}
