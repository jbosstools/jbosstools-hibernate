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
package org.jboss.tools.hibernate.internal.core.hibernate.automapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.hibernate.Type;
import org.jboss.tools.hibernate.internal.core.PersistentField;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.RunnablesHolder.SourcePropertyCreationRunnable;
import org.jboss.tools.hibernate.internal.core.util.SearchUtils;
import org.jboss.tools.hibernate.internal.core.util.StringUtils;

/**
 * @author Nick - mailto:n.belaevski@exadel.com
 * created: 21.06.2005
 * 
 */
public class PersistentFieldProvider {

    private MapList mapList = new MapList();
    private SourcePropertyCreationRunnable runnable;
    private ConfigurationReader hamConfig;
    private NamingProvider np = new NamingProvider();
    
    
/*    public class PersistentFieldProxy extends PersistentField {

        private IPersistentField field;
        
        private IPersistentClass clazz;
        private String fieldName;
        private Searcher searcher;
        private String fallbackTypeString;
        
        *//**
         * 
         *//*
        public PersistentFieldProxy(IPersistentClass clazz, String fieldName, Searcher searcher, String fallbackTypeString) {
            this.clazz = clazz;
            this.fieldName = fieldName;
            this.searcher = searcher;
            this.fallbackTypeString = fallbackTypeString;
            mapList.add(this.clazz,this);
        }

        private IPersistentField buildField(boolean isFuzzy) throws CoreException {
            if (field == null)
            {
                if (searcher.fields == null || searcher.fields.length == 0)
                    searcher.setFields(clazz.getFields());
                
                field = internalGetOrCreateField(clazz,fieldName,searcher,fallbackTypeString,isFuzzy);
                if (field != null)
                {
                    field.setMapping(getMapping());
                    if (field.getMapping() != null)
                    {
                        field.getMapping().setPersistentField(field);
                        if (field.getMapping() instanceof AbstractFieldMapping) {
                            AbstractFieldMapping afm = (AbstractFieldMapping) field.getMapping();
                            afm.setName(field.getName());
                        }
                        HibernateAutoMappingHelper.setPropertyMappingAccessor(field.getMapping());
                    }
                }
            }
            return field;
        }

        public IPersistentClass getOwnerClass() {
            return clazz;
        }

        public IPersistentField getField() {
            return field;
        }
    }

    private void internalFieldsProcess(boolean isFuzzy) throws CoreException
    {
        Iterator keys = mapList.getKeys();
        while (keys.hasNext())
        {
            Object key = keys.next();
            Iterator proxies = mapList.get(key);
            while (proxies.hasNext())
            {
                PersistentFieldProxy proxy = (PersistentFieldProxy) proxies.next();
                proxy.buildField(isFuzzy);
            }
        }
    }
    
    public void fieldsProcess(boolean isFuzzy) throws CoreException
    {
        if (isFuzzy)
        {
            setSearchOnly(true);
            internalFieldsProcess(false);
        }
        setSearchOnly(false);
        internalFieldsProcess(isFuzzy);
    }
*/    
    private boolean searchOnly = false;
    
    private abstract class Searcher
    {
        abstract IPersistentField search(boolean isFuzzy);
        protected String query;
        protected IPersistentField[] fields;
        protected IPersistentClass clazz;
        
        Searcher(IPersistentClass clazz, String query)
        {
            super();
            setFields(clazz.getFields());
            this.query = query;
        }
        
        void setFields(IPersistentField[] fields)
        {
            List fieldsList = new ArrayList();
            for (int i = 0; i < fields.length; i++) {
                IPersistentField field = fields[i];
                if (field == null)
                    continue ;
                    
                if ((field.getAccessorMask() & hamConfig.getPropertyAccessorMask()) != 0)
                    fieldsList.add(field);
            }
            this.fields = (IPersistentField[]) fieldsList.toArray(new IPersistentField[0]);
        }
    }
    class HibTypeSearcher extends Searcher
    {
        HibTypeSearcher(IPersistentClass clazz, String query) {
            super(clazz, query);
        }

        private Type hibType;
        void setType(Type hibType)
        {
            this.hibType = hibType;
        }

        IPersistentField search(boolean isFuzzy)
        {
            return SearchUtils.findUnMappedFieldByType(fields,query,hibType,isFuzzy);
        }
    }

    class JavaTypeSearcher extends Searcher
    {
        JavaTypeSearcher(IPersistentClass clazz, String query) {
            super(clazz, query);
        }

        private String javaType;
        void setJavaType(String javaType)
        {
            this.javaType = javaType;
        }

        IPersistentField search(boolean isFuzzy)
        {
            return SearchUtils.findUnMappedFieldByType(fields,query,javaType,isFuzzy);
        }
    }
    
    class JavaTypesSetSearcher extends Searcher
    {
        JavaTypesSetSearcher(IPersistentClass clazz, String query) {
            super(clazz, query);
        }

        private Set javaTypesSet;
        void setJavaTypesSet(Set javaTypesSet)
        {
            this.javaTypesSet = javaTypesSet;
        }

        IPersistentField search(boolean isFuzzy)
        {
            return SearchUtils.findUnMappedFieldByType(fields,query,javaTypesSet,isFuzzy);
        }
    }
    
    public PersistentFieldProvider(SourcePropertyCreationRunnable runnable, ConfigurationReader hamConfig)
    {
        if (runnable != null)
        {
            this.runnable = runnable;
        }
        else
        {
            this.runnable = new RunnablesHolder.SourcePropertyCreationRunnable(hamConfig.getPojoRenderer());
        }
        this.hamConfig = hamConfig;
    }
    
    private PersistentField createQueuedReversedField(IPersistentClass clazz, String name, String javaType) throws CoreException
    {
        PersistentField fld = HibernateAutoMapping.createPersistentFieldByType(clazz,name,javaType,null);
        fld.addAccessorMask(PersistentField.ACCESSOR_FIELD|PersistentField.ACCESSOR_PROPERTY);
        
        runnable.addQuery(fld);
        return fld;
    }
    
//    private PersistentFieldProxy internalCreateFieldProxy(IPersistentClass clazz, String fieldName, Searcher searcher, String fallbackTypeString) throws CoreException
//    {
//        return new PersistentFieldProxy(clazz,fieldName,searcher,fallbackTypeString);
//    }

    private IPersistentField internalGetOrCreateField(IPersistentClass clazz, String fieldName, Searcher searcher, String fallbackTypeString,boolean isFuzzy) throws CoreException
    {
        IPersistentField field = null;
        
        boolean found = false;
        
        String nameBase = fieldName.replaceAll("[^\\w\\d_]","");
        if (!StringUtils.isValidJavaName(nameBase+"_notReservedWord"))
        {
            nameBase = "safeFieldName_" + nameBase;
        }
            
        String name = nameBase;
        
        np.reset();
        
        do {

            field = searcher.search(isFuzzy);
            if (field == null)
            {
                if (clazz.getField(name) == null && (JavaConventions.validateFieldName(name).getSeverity() & (IStatus.CANCEL | IStatus.ERROR)) == 0)
                {
                    found = true;
                    if (!searchOnly)
                        field = createQueuedReversedField(clazz,name,fallbackTypeString);
                }
                else
                {
                    name = np.computeName(nameBase);
                    IPersistentField[] fields = new IPersistentField[]{clazz.getField(name)};
                    searcher.setFields(fields);
                }
            }

        } while (field == null && !found);

        return field;
    }
    
    public IPersistentField getOrCreatePersistentField(IPersistentClass clazz, String fieldName, Type hibType, boolean isFuzzy) throws CoreException
    {
        String fallbackTypeString = hamConfig.getJavaTypeName(hibType);
        HibTypeSearcher searcher = new HibTypeSearcher(clazz,fieldName);
        searcher.setType(hibType);
        
        return internalGetOrCreateField(clazz,fieldName,searcher,fallbackTypeString,isFuzzy);
    }
    
    public IPersistentField getOrCreatePersistentField(IPersistentClass clazz, String fieldName, String javaType, boolean isFuzzy) throws CoreException
    {
        JavaTypeSearcher searcher = new JavaTypeSearcher(clazz,fieldName);
        searcher.setJavaType(javaType);
        
        return internalGetOrCreateField(clazz,fieldName,searcher,javaType,isFuzzy);
    }
    
    public IPersistentField getOrCreatePersistentField(IPersistentClass clazz, String fieldName, Set javaTypesSet, String defaultJavaType, boolean isFuzzy) throws CoreException
    {
        JavaTypesSetSearcher searcher = new JavaTypesSetSearcher(clazz,fieldName);
        searcher.setJavaTypesSet(javaTypesSet);
        
        return internalGetOrCreateField(clazz,fieldName,searcher,defaultJavaType,isFuzzy);
    }
    
/*    public PersistentFieldProxy createPersistentFieldProxy(IPersistentClass clazz, String fieldName, Type hibType) throws CoreException
    {
        String fallbackTypeString = hamConfig.getJavaTypeName(hibType);
        HibTypeSearcher searcher = new HibTypeSearcher(clazz,fieldName);
        searcher.setType(hibType);
        
        return internalCreateFieldProxy(clazz,fieldName,searcher,fallbackTypeString);
    }
    
    public PersistentFieldProxy createPersistentFieldProxy(IPersistentClass clazz, String fieldName, String javaType) throws CoreException
    {
        JavaTypeSearcher searcher = new JavaTypeSearcher(clazz,fieldName);
        searcher.setJavaType(javaType);
        
        return internalCreateFieldProxy(clazz,fieldName,searcher,javaType);
    }
    
    public PersistentFieldProxy createPersistentFieldProxy(IPersistentClass clazz, String fieldName, Set javaTypesSet, String defaultJavaType) throws CoreException
    {
        JavaTypesSetSearcher searcher = new JavaTypesSetSearcher(clazz,fieldName);
        searcher.setJavaTypesSet(javaTypesSet);
        
        return internalCreateFieldProxy(clazz,fieldName,searcher,defaultJavaType);
    }

*/
    public void runFieldCreation(IProject project) throws CoreException
    {
        //TODO(tau->tau) RULE    	
        project.getWorkspace().run(runnable,null);
    }

    public void setSearchOnly(boolean searchOnly) {
        this.searchOnly = searchOnly;
    }
    
}
