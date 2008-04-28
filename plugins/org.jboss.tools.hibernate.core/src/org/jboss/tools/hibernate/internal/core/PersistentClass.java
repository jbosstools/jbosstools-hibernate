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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.SequencedHashMap;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IMappingStorage;
import org.jboss.tools.hibernate.core.IOrmModelVisitor;
import org.jboss.tools.hibernate.core.IPackage;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.IPersistentFieldMapping;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.internal.core.hibernate.PersistableProperty;
import org.jboss.tools.hibernate.internal.core.util.ScanProject;
import org.jboss.tools.hibernate.internal.core.util.TypeAnalyzer;


/**
 * @author alex
 *
 * Represents a Java class that has an O-R mapping
 */
public class PersistentClass extends AbstractOrmElement implements IPersistentClass {
	private static final long serialVersionUID = 1L;

	private IMapping projectMapping;
	private IPackage classPackage;
	//link to an object in JDT tree
	private ICompilationUnit sourceCode;
	private PersistentClass superClass;
	private SequencedHashMap fields=new SequencedHashMap();
	
    private long timeStamp = 0;
	
	//added by Nick 4.04.2005
	// stores inherited fields - COPIES of parent classes persistent fields 
	//private Map superFields=new SequencedHashMap();
	//by Nick
	
	private IPersistentClassMapping mapping;
	private static IPersistentField[] FIELDS={};
	
	//add tau 13.03.2006
	private IType persistentClassType = null;
	private boolean dirtyFields = true;
	
	//add tau 14.03.2006
	private boolean needRefresh = true;
	
	//add tau 15.03.2006
	private static Comparator<Object> comparator = getComparatorField();	
	
	public boolean isDirtyFields() {
		return dirtyFields;
	}

	public void setDirtyFields(boolean dirty) {
		this.dirtyFields = dirty;
	}

	public String getPackageName(){
		String name=getName();
		int i=name.lastIndexOf('.');
		if(i==-1)return "";
		return name.substring(0,i);
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentClass#getShortName()
	 */
	public String getShortName() {
		String name=getName();
		int i=name.lastIndexOf('.');
		if(i==-1)return name;
		return name.substring(i+1);
	}
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentClass#getSourceCode()
	 */
	public ICompilationUnit getSourceCode() {
		// add tau 13.03.2006
		if (sourceCode == null)
		{
			String className=getName();
			if(className == null) return null;
			
			int i = className.indexOf('$');
			String cuName = className;
			if(i!=-1) cuName = className.substring(0,i);
			
			ICompilationUnit cu = null;
			try {
				cu = ScanProject.findCompilationUnit(cuName, projectMapping.getProject().getProject());
			} catch (CoreException e) {
				OrmCore.getPluginLog().logError("Update failed for "+getName(),e);
			}
			if (cu == null) return null; // no cu
			setSourceCode(cu);
		}
		
		return sourceCode;
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentClass#getPackage()
	 */
	public IPackage getPackage() {
		return classPackage;
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentClass#getSuperClass()
	 */
	public IPersistentClass getSuperClass() {
		return superClass;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentClass#getRootClass()
	 */
	public IPersistentClass getRootClass() {
		IPersistentClass root=this;
		while(root.getSuperClass()!=null) root=root.getSuperClass();
		return root;
	}
	
	//added by Nick 1.04.2005
    protected static PersistentField clonePersistentField(IPersistentClass newOwner, PersistentField field)
    {
        if (field == null || newOwner == null)
            return null;
        
        //changed by Nick 15.04.2005
		PersistentField clonedField = new PersistentField();
        //changed by Nick
		clonedField.setName(field.getName());
        clonedField.setOwnerClass(newOwner);
        clonedField.setType(field.getType());
		clonedField.addAccessorMask(field.getAccessorMask());
        
        return clonedField;
    }
    protected static PersistentField updatePersistentField(PersistentField field, PersistentField updateFrom)
    {
        if (field == null || updateFrom == null) return field;
        
		field.setType(updateFrom.getType());
		field.addAccessorMask(updateFrom.getAccessorMask());
        
        return field;
    }
	
	/**
	 * 
	 * */
	private void mergeSuperFields(IPersistentClass owner, Map allFields)
	{
		if(owner!=this){
	        Iterator it = fields.values().iterator();
	        while(it.hasNext())
	        {
				PersistentField pf=(PersistentField)it.next();
				if(allFields.get(pf.getName())==null){
					allFields.put(pf.getName(), clonePersistentField(owner, pf));
				}else{
					updatePersistentField((PersistentField)allFields.get(pf.getName()), pf);
				}
	        }
		}
	    if (superClass != null)
	    {
			superClass.mergeSuperFields(owner, allFields);
	    }
	}
	private SequencedHashMap getFieldsInternal(){
		if(mapping==null || !mapping.isIncludeSuperFields()) return fields;
		mergeSuperFields(this, fields);
		return fields;
	}
	//by Nick
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentClass#getFields()
	 */
	public synchronized IPersistentField[] getFields() {
		// edit tau 13.03.2006
		//reorderFields();
		//return (IPersistentField[])getFieldsInternal().values().toArray(FIELDS);
		
		// add tau 14.03.2006
		if (isNeedRefresh()) {
			refreshTrue();
			
			// add tau 23.03.2006 from AbstractMapping.refresh(...)
			// TODO (tau->tau) 23.03.2006
	    	UpdateMappingVisitor updatevisitor = new UpdateMappingVisitor(projectMapping);
        	//updatevisitor.doMappingsUpdate(projectMapping.getPersistentClassMappings());
	    	updatevisitor.doMappingsUpdate(new IPersistentClassMapping[] {mapping});	    	
			
		}

		if (isDirtyFields()) {
			reorderFields();
			setDirtyFields(false);
			return (IPersistentField[])getFieldsInternal().values().toArray(FIELDS);			
		} else {
			return (IPersistentField[]) fields.values().toArray(FIELDS);
		}
	}
	
	// add tau 14.03.2006	
	private IPersistentField[] getFieldsBeforeRefresh() {
		if (isDirtyFields()) {
			reorderFields();
			setDirtyFields(false);
			return (IPersistentField[])getFieldsInternal().values().toArray(FIELDS);			
		} else {
			return (IPersistentField[]) fields.values().toArray(FIELDS);
		}
	}	
	
	public IPersistentField getField(String name){
		return (IPersistentField)getFieldsInternal().get(name);
	}
	
	
	/* rem by yk 13.07.2005 private void removeField(String name){ */
	public void removeField(String name){
		fields.remove(name);
		// add tau 13.03.2006		
		setDirtyFields(true);		
	}
	
	//added by yan 20051005
	public void removeFields() {
		fields.clear();
		// add tau 13.03.2006
		setDirtyFields(true);		
	}
	
	public PersistentField getOrCreateField(String name){
		PersistentField field=(PersistentField)getField(name);
		if(field==null){
			field=new PersistentField();
			field.setName(name);
			field.setOwnerClass(this);
			fields.put(name,field);
			
			// add tau 13.03.2006			
			if (!isDirtyFields())	setDirtyFields(true);
		}
		return field;
	}
	
	// add tau 22.03.2006
	private PersistentField createField(PersistableProperty property, int accessorMask){
		PersistentField persistentField =getOrCreateField(property.getName());
		persistentField.addAccessorMask(accessorMask);
		if (persistentField.getType() == null)
			persistentField.setType(property.getType());
	
		persistentField.setGenerifiedTypes(property.getGenerifiedTypes());		

		return persistentField;
	}	
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentClass#getPersistentClassMapping()
	 */
	public IPersistentClassMapping getPersistentClassMapping() {
		return mapping;
	}
	

	/**
	 * @param mapping The mapping to set.
	 */
	public void setPersistentClassMapping(IPersistentClassMapping mapping) {
		this.mapping = mapping;
	}
	
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentClass#clearMapping()
	 */
	public void clearMapping() {
		if(mapping!=null) mapping.clear();
	}

	/**
	 * @param sourceCode The sourceCode to set.
	 */
	public void setSourceCode(ICompilationUnit sourceCode) {
		persistentClassType = null; // add tau 13.03.2006
		this.sourceCode = sourceCode;
	}
	/**
	 * @param superClass The superClass to set.
	 */
	public void setSuperClass(PersistentClass superClass) {
		this.superClass = superClass;
	}
	
	/**
	 * @param classPackage The classPackage to set.
	 */
	public void setPackage(IPackage classPackage) {
		this.classPackage = classPackage;
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IOrmElement#accept(org.jboss.tools.hibernate.core.IOrmModelVisitor, java.lang.Object)
	 */
	public Object accept(IOrmModelVisitor visitor, Object argument) {
		return visitor.visitPersistentClass(this,argument);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentClass#getProjectMapping()
	 */
	public IMapping getProjectMapping() {
		return projectMapping;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentClass#setProjectMapping(org.jboss.tools.hibernate.core.IMapping)
	 */
	public void setProjectMapping(IMapping mapping) {
		projectMapping=mapping;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentClass#deleteField(java.lang.String)
	 */
	public void deleteField(String fieldName) {
		if(mapping!=null){
			mapping.deleteFieldMapping(fieldName);
		}
		fields.remove(fieldName);
		// add tau 13.03.2006
		setDirtyFields(true);		
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.core.IPersistentClass#renameField(org.jboss.tools.hibernate.core.IPersistentField, java.lang.String)
	 */
	public void renameField(IPersistentField field, String newName) {
		String oldName=field.getName();
		fields.remove(oldName);
		PersistentField res=(PersistentField)field;
		res.setName(newName);
		fields.put(newName, res);
		res.setOwnerClass(this);
		setSourceCode(null);
		if(field.getMapping()!=null && mapping!=null){
			IPersistentFieldMapping pfm=field.getMapping();
			mapping.renameFieldMapping(pfm, newName);
		}
		// add tau 13.03.2006
		setDirtyFields(true);		
	}

    //added by Nick 01.06.2005
	// edit tau 13.03.2006
	public IType getType() {
	    ICompilationUnit unit = this.getSourceCode();
        IType returnType = null;
        
        if (unit != null && unit.exists())
        {
        	if (persistentClassType == null) {
	            try 
	            {
	            	persistentClassType = ScanProject.findClassInCU(unit,this.getShortName());
	            } catch (JavaModelException e) {
	            	OrmCore.getPluginLog().logError("Error finding persistent class type",e);
	            }        		
        	}
    		returnType =  persistentClassType;        	
        }
        return returnType;
    }
    //by Nick
	
	// add tau 14.03.2006 for lazy initializes PersistentClass
	public void refresh(){
		setNeedRefresh(true);		
	}
	
	
	/**
	 * Synchronize with the source code 
	 * */
	// edit tau 14.03.2006 rename refresh() -> refreshTrue()
	private void refreshTrue(){
		try {
			
			// tau move to getSourceCode() 13.03.2006
//			if (getSourceCode() == null)
//			{
//				String className=getName();
//				// add by yk 08.06.2005.
//				if(className == null){	return;	}
//				// add by yk 08.06.2005 stop.
//
//				int i=className.indexOf('$');
//				String cuName=className;
//				if(i!=-1) cuName=className.substring(0,i);
//				
//				// TODO EXP8 
//				ICompilationUnit cu = ScanProject.findCompilationUnit(cuName, projectMapping.getProject().getProject());
//				if (cu == null)
//					return; // no cu
//				setSourceCode(cu);
//			}

            //changed by Nick 30.03.2005
            //XXX Nick check pc.getShortName() for inner classes
            IType classRoot = getType();
            //IType classRoot = pc.getSourceCode().getType(pc.getShortName());
			//by Nick
            if (classRoot == null)
				return;

			//remove fields without a mapping before sync
			//IPersistentField allFields[]= getFields();
			IPersistentField allFields[]= getFieldsBeforeRefresh();
			
			for(int i=0;i<allFields.length;++i){
				if(allFields[i].getMapping()==null) removeField(allFields[i].getName());
			}
			
			
			TypeAnalyzer ta = new TypeAnalyzer(classRoot);

			if (getSuperClass() != null)
					// sniff inherited fields up to next PC in hierarchy
					ta.setUpperBoundType(getSuperClass().getType());

            Set<String> existingFields = new HashSet<String>();
			PersistableProperty[] properties = ta.getPropertiesByMask(PersistentField.ACCESSOR_PROPERTY);            
			//changed by Nick 30.05.2005 - now naming provider lazy initializes when quering
            if (properties != null)
				for (int i = 0; i < properties.length; i++) {

					//edit tau 22.03.2006
					PersistentField persistentField = createField(properties[i], PersistentField.ACCESSOR_PROPERTY);
					existingFields.add(persistentField.getName());
					
//					PersistableProperty property = properties[i];					
//					PersistentField pf =getOrCreateField(property.getName());
//					existingFields.add(pf.getName());
//                  pf.addAccessorMask(PersistentField.ACCESSOR_PROPERTY);
//					if (pf.getType() == null)
//						pf.setType(property.getType());
//				
//					pf.setGenerifiedTypes(property.getGenerifiedTypes());
                }

			PersistableProperty[] fieldsProperty = ta.getPropertiesByMask(PersistentField.ACCESSOR_FIELD);
			if (fieldsProperty != null)
				for (int f = 0; f < fieldsProperty.length; f++)
				{
					// Create persistent field for each field from source code

					//edit tau 22.03.2006
					PersistableProperty fieldProperty = fieldsProperty[f];
					String name = fieldProperty.getName();
					if (getField(name) == null)
					{
						if (name.indexOf('_') == 0
								&& getField(name.substring(1)) != null)
							continue;
					}
					
					PersistentField persistentField = createField(fieldProperty, PersistentField.ACCESSOR_FIELD);
					existingFields.add(persistentField.getName());
					
//					PersistableProperty field = fields[f];
//					PersistentField pf = null;
//					String name = field.getName();
//					if (getField(name) == null)
//					{
//						if (name.indexOf('_') == 0
//								&& getField(name.substring(1)) != null)
//							continue;
//					}
//					pf = getOrCreateField(name);
//                    existingFields.add(pf.getName());
//					pf.addAccessorMask(PersistentField.ACCESSOR_FIELD);
//					if (pf.getType() == null)
//						pf.setType(field.getType());
//
//                    pf.setGenerifiedTypes(field.getGenerifiedTypes());
                }
			
			if (getSuperClass() != null){
				//check fields from super class
			    // changed by Nick 10.06.2005
//                allFields= getFields();
//				ta.setUpperBoundType(null);
                ta.setUpperBoundType(null);
                
                //allFields = getFields();
                allFields = getFieldsBeforeRefresh();
                
				// by Nick
                for(int i=0;i<allFields.length;++i){
					if(allFields[i].getType()==null){
						PersistentField pf = (PersistentField)allFields[i];
						PersistableProperty pp = ta.getPropertyOrField(pf.getName());
						if(pp!=null) {
						    existingFields.add(pf.getName());
                            pf.setType(pp.getType());
                            pf.setGenerifiedTypes(pp.getGenerifiedTypes());
                            pf.addAccessorMask(pp.getAccessorMask());
                        }
					}
				}
			}
            
            // added by Nick 29.06.2005
			
			//IPersistentField[] theFields = (IPersistentField[])this.getFields();
			IPersistentField[] theFields = (IPersistentField[])this.getFieldsBeforeRefresh();
			
            for (int i = 0; i < theFields.length; i++) {
                PersistentField field = (PersistentField) theFields[i];
                if (!existingFields.contains(field.getName()))
                    field.setType(null);
            }
            // by Nick
		}
		catch (Exception ex)
		{
			OrmCore.getPluginLog().logError("Update failed for "+getName(),ex);
		}
		finally{
			timeStamp=refreshTimeStamp();
			// add tau 13.03.2006
			setDirtyFields(true);
			// add tau 14.03.2006			
			setNeedRefresh(false);			
		}
	}
	
	
// added by yk 19.07.2005
	/**
	 *  Reorder of fields in according to sequence order of fields in the source file. 
	 */
	private void reorderFields()
	{
 		IType classType = null;
 		if(mapping == null) return;
		try
		{
			if(getSourceCode() == null) return;
				
	        /* del tau 13.03.2006 look in  PersistentClass.getSourceCode();
			if(getSourceCode() == null)  				
			{
				ICompilationUnit cu = ScanProject.findCompilationUnit(getName(), projectMapping.getProject().getProject());
				if (cu == null) return;
				setSourceCode(cu);
			}
			*/
			
			classType = getType();
			if(classType == null) {	return; /*throw new Exception("Error of getting classtype for: ");*/}

			ArrayList<Object> temp = new ArrayList<Object>();
            TypeAnalyzer    ta      = new TypeAnalyzer(classType);
            IField[]        flds    = ta.getFields();
            if(flds == null/* || flds.length < 1*/) 
                    {       return; /*throw new Exception("There is not any fields in typeanalyzer for: "); */}
            
			for(int i = fields.size(); i > 0; i--)
			{		temp.add(fields.remove(i-1));				}
			
			if(temp.size() < 1) return;
			Object[] oldfields = temp.toArray();
			
			// del tau 15.03.2006
			// edit tau 13.03.2006
			//Comparator comparator =  getComparatorField();
			//Arrays.sort(oldfields,getComparator2());
			
			Arrays.sort(oldfields, comparator);			
			
			for(int t = 0; t < flds.length; t++)
			{
				//int index = Arrays.binarySearch(oldfields,flds[t],getComparator());
				int index = Arrays.binarySearch(oldfields,flds[t], comparator);
				
				if(index >= 0)
				{
					fields.put( ((IPersistentField)oldfields[index]).getName(), oldfields[index]);
				}
			}
			
			if(fields.size() < temp.size())
			{// there are fields which not contain the source file
				Iterator it = temp.iterator();
				while(it.hasNext())
				{
					IPersistentField ipf = (IPersistentField)it.next();
					if(!fields.containsKey(ipf.getName()))
					{
						fields.put(ipf.getName(), ipf);
					}
				}
			}
		}
		catch(Exception exc)
		{
			String body = ". Error getting properties for: ";
			if(exc.getMessage() != null  && !exc.getMessage().equals(""))
				{			body = exc.getMessage();							}
			
			// add tau 30.01.2006
			String fullyQualifiedName = "";
			if (classType != null) {
				fullyQualifiedName = classType.getFullyQualifiedName();
			}
			OrmCore.getPluginLog().logInfo(this.getClass().getName() + body + fullyQualifiedName);
		}
	}
	
	// no use tau 13.03.2006
/*
	private Comparator getComparator()
	{
		Comparator cmp = new Comparator()
		{
			public int compare(Object arg0, Object arg1) 
			{
				IPersistentField ipf	= (IPersistentField)arg0;
				IField fld			= (IField)arg1;
				if(ipf == null || ipf.getName() == null) return 0;
				return  ipf.getName().compareTo(fld.getElementName());
			}
		};
		return cmp;
	}
*/
	
	// no use tau 13.03.2006
/*
	private Comparator<Object> getComparator2() {
		Comparator<Object> cmp = new Comparator<Object>() {
			public int compare(Object arg0, Object arg1) {
				IPersistentField ipf	= (IPersistentField)arg0;
				IPersistentField ipf2	= (IPersistentField)arg1;
				if(ipf == null || ipf.getName() == null) return 0;
				return  ipf.getName().compareTo(ipf2.getName());
			}
		};
		return cmp;
	}
*/
// added by yk 19.07.2005 stop
	
	// add tau 13.03.2006
	private static Comparator<Object> getComparatorField() {
		Comparator<Object> cmp = new Comparator<Object>() {
			public int compare(Object arg0, Object arg1) {
				IPersistentField ipf	= (IPersistentField)arg0;
				if(ipf == null || ipf.getName() == null) return 0;
				if (arg1 instanceof IPersistentField) {
					return  ipf.getName().compareTo(((IPersistentField)arg1).getName());
				} else {
					return  ipf.getName().compareTo(((IField)arg1).getElementName());
				}
			}
		};
		return cmp;
	}	
	
    private static final long SOURCE_NOT_EXISTS = 1L;
    
    private long refreshTimeStamp()
    {
        long result = SOURCE_NOT_EXISTS;
		if( sourceCode==null || !sourceCode.exists() )return result;
        try {
	        IResource resource = sourceCode.getUnderlyingResource();
	        
	        if (resource != null)
	        {
	                resource.refreshLocal(IResource.DEPTH_ZERO,null);
	                result = resource.getLocalTimeStamp();
	        }
        }catch (CoreException e) {
        	OrmCore.getPluginLog().logError("Exception refreshing resource timestamp..., " + e.toString());            
        }
        return result;
    }
    //by Nick
    public synchronized boolean isResourceChanged() {
        long ts = refreshTimeStamp();
        boolean isChanged = (timeStamp != ts || ts == SOURCE_NOT_EXISTS);
        if (isChanged){
	        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) {
	        	OrmCore.getPluginLog().logInfo("PersistentClass.isResourceChanged()->class.refresh():"+
	        			this.getName()+ 
	        			", oldTS= "+timeStamp+        	
						", newTs= "+ts);	        
	        }
            this.refresh();
        } else {
	        if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) {
	        	OrmCore.getPluginLog().logInfo("PersistentClass.isResourceChanged()->class.NOrefresh():"+
	        			this.getName()+ 
	        			", oldTS= "+timeStamp+        	
						", newTs= "+ts);        	
	        }
        }
        return isChanged;
    }

    // added by Nick 16.06.2005
    /* (non-Javadoc)
     * @see org.jboss.tools.hibernate.core.IPersistentClass#hasMappedFields()
     */
    public boolean hasMappedFields() {
        boolean result = false;
        
        if (this.getPersistentClassMapping() != null)
        {
            IPersistentClassMapping mapping = this.getPersistentClassMapping();
            Iterator itr = mapping.getFieldMappingIterator();
            if (itr != null && itr.hasNext())
                result = true;
        }
        
        return result;
    }
    // by Nick

    // add tau 14.03.2006
	public synchronized boolean isNeedRefresh() {
		return needRefresh;
	}

    // add tau 14.03.2006	
	public synchronized void setNeedRefresh(boolean needRefresh) {
		this.needRefresh = needRefresh;
	}

	// add tau 24.04.2006
	public IDatabaseTable getDatabaseTable() {
		if (mapping != null) {
			return mapping.getDatabaseTable();
		} else return null;
	}

	// add tau 24.04.2006	
	public IMappingStorage getPersistentClassMappingStorage() {
		if (mapping != null) {
			return mapping.getStorage();
		} else return null;
	}
}
