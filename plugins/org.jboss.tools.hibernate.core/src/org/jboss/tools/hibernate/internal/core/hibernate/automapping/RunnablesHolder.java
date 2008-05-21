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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.Signature;
import org.jboss.tools.hibernate.core.ICodeRendererService;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.OrmProgressMonitor;
import org.jboss.tools.hibernate.internal.core.CodeRendererServiceWrapper;
import org.jboss.tools.hibernate.internal.core.hibernate.HibernateMapping;
import org.jboss.tools.hibernate.internal.core.util.ClassUtils;



/**
 * @author Nick - mailto:n.belaevski@exadel.com
 * created: 03.06.2005
 * 
 * contains implementations of IWorkspaceRunnable useful when auto-mapping
 */

public class RunnablesHolder {
    
    static abstract class GenericRunnable implements IWorkspaceRunnable
    {
        protected int jobPart = 1000;
        //akuzmin 27.07.2005
        protected OrmProgressMonitor o_monitor;       
        public void setJobPart(int newJobPart)
        {
            if (newJobPart != 0)
                jobPart = newJobPart;
        }
        //akuzmin 27.07.2005
        public void setOrmMonitor(OrmProgressMonitor monitor)
        {
        	o_monitor=monitor;
        }
        
        public abstract void run(IProgressMonitor monitor) throws CoreException;
    
        protected ICodeRendererService renderer = null;
        
        public GenericRunnable(ICodeRendererService renderer)
        {
            this.renderer = renderer;
        }
    }

    static class SourcePropertyCreationRunnable extends GenericRunnable
    {
        /**
         * @param renderer
         */
        public SourcePropertyCreationRunnable(ICodeRendererService renderer) {
            super(renderer);
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IWorkspaceRunnable#run(org.eclipse.core.runtime.IProgressMonitor)
         */
        private List<IPersistentField> queries = new ArrayList<IPersistentField>();
        
        void addQuery(IPersistentField field) {
            queries.add(field);   
        }
        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IWorkspaceRunnable#run(org.eclipse.core.runtime.IProgressMonitor)
         */
        public void run(IProgressMonitor monitor) throws CoreException {
            
        	if (o_monitor==null)
        		o_monitor = new OrmProgressMonitor(monitor);
            
            Map typesQueries = new HashMap();
            CodeRendererServiceWrapper wrapper = null;
            
            Iterator itr = queries.iterator();
            if (itr != null)
            {
                while (itr.hasNext())
                {
                    IPersistentField field = (IPersistentField)itr.next();
                    if (field.getOwnerClass() == null)
                        continue;
                    
                    IType inType = field.getOwnerClass().getType();
                    
                    if (inType == null)
                        continue ;
                    
                    List[] vars;
                    if (typesQueries.containsKey(inType))
                    {
                        vars = (List[]) typesQueries.get(inType);
                    }
                    else
                    {
                        vars = new ArrayList[2];
                        vars[0] = new ArrayList();
                        vars[1] = new ArrayList();
                        typesQueries.put(inType,vars);
                    }
                    
                    if (!vars[0].contains(field.getName()))
                    {
                        vars[0].add(field.getName());
                        vars[1].add(field.getType());
                    }
                }

                Iterator types = typesQueries.keySet().iterator();
                
                o_monitor.setTaskParameters(jobPart,typesQueries.size());

                while (types.hasNext())
                {
                    IType inType = (IType) types.next();
                    List[] vars = (List[]) typesQueries.get(inType);
                    String[] names = (String[]) vars[0].toArray(new String[0]);
                    String[] javaTypes = (String[]) vars[1].toArray(new String[0]);
                    
                    if (renderer instanceof CodeRendererServiceWrapper) {
                        wrapper = (CodeRendererServiceWrapper) renderer;
                    }
                    else
                    {
                        wrapper = new CodeRendererServiceWrapper(renderer);
                    }

                    wrapper.batchGenerateProperties(names,javaTypes,inType);

                    o_monitor.worked();
                }
            }
            queries.clear();
        }
    }

    static class ReversingCreateCompilationUnitsOperation extends GenericRunnable {
        private Map<String,ICompilationUnit> units = new HashMap<String,ICompilationUnit>();
        private IType baseclass;
//        private HibernateMapping mapping;        
        
        public ReversingCreateCompilationUnitsOperation(ICodeRendererService renderer, IType baseclass,HibernateMapping mapping) {
            super(renderer);
        	this.baseclass = baseclass;
//            this.mapping = mapping;  
        }
        
        ICompilationUnit addNameToQueue(IPackageFragment fragment, String name) {
            if (fragment == null || !fragment.exists() || name == null)
                return null;
                
            String unitName = name;
            
            int index = -1;
            if ( (index = unitName.indexOf('$')) != -1)
            {
                unitName = unitName.substring(0,index);
            }
            
            ICompilationUnit unit = fragment.getCompilationUnit(unitName+".java");
            String fqName = Signature.toQualifiedName(new String[]{fragment.getElementName(),name});
            units.put(fqName,unit);
            return unit;
        }

        /* (non-Javadoc)
         * @see org.eclipse.core.resources.IWorkspaceRunnable#run(org.eclipse.core.runtime.IProgressMonitor)
         */
        public void run(IProgressMonitor monitor) throws CoreException {
        	
        	// delete tau 09.06.2006 
            //Random rdmz= new Random(RandomUtils.nextLong());
        	
        	if (o_monitor==null)
        		o_monitor = new OrmProgressMonitor(monitor);
            o_monitor.setTaskParameters(jobPart,units.size());

            Iterator itr = units.keySet().iterator();
            if (itr != null)
            while (itr.hasNext())
            {
//            	boolean iscreatemethods = false;
                String className = (String)itr.next();
                //create .java file for reversed class
                ICompilationUnit unit = (ICompilationUnit)units.get(className);
                if (unit == null || !unit.exists())
                {
                    String simpleName = Signature.getSimpleName(className);
                    String[] names = simpleName.split("\\$");

                    renderer.createCompilationUnit(names[0],ClassUtils.getPackageFragment(unit));
                    unit.makeConsistent(null);
                }
                
//                if (className.indexOf('$') != -1)
//                {
//              akuzmin 02.08.2005
//                if (unit.findPrimaryType() == null) {
//                	iscreatemethods = true;
//                }

                String baseClassName = null;
                String[] ifaceNames = null;
                if (baseclass != null)
                {
                    if (baseclass.isInterface())
                    {
                        ifaceNames = new String[]{baseclass.getFullyQualifiedName()};
                    }
                    else
                    {
                        baseClassName = baseclass.getFullyQualifiedName();
                    }
                }
                
//                IType type = 
                	renderer.createType(unit,Signature.getSimpleName(className),baseClassName,ifaceNames,className.indexOf('$') != -1);

//              akuzmin 02.08.2005                    
/*                if (iscreatemethods)
                {
    				IPersistentClass clazz = mapping.findClass(className);
    				if ((clazz!=null)&&(clazz.getPersistentClassMapping()!=null))
    				{
    					IHibernateClassMapping pcmapping = (IHibernateClassMapping) clazz.getPersistentClassMapping();
    					String[] compproperties={};
    					Iterator iterator;
    					if (pcmapping.getIdentifier() instanceof ComponentMapping)//compositeID
    					{
    						String idclassName =pcmapping.getIdentifier().getFieldMapping().getName();
    						ComponentMapping cm = (ComponentMapping)pcmapping.getIdentifier();
//    						IPersistentClass idclass = mapping.findClass(idclassName);    						
    						IPersistentClass idclass =cm.getComponentClass();
    						if (idclass!=null)
    						{
	    		                ICompilationUnit idunit = (ICompilationUnit)idclass.getSourceCode();
	
	    		                if (idunit == null || !idunit.exists())
	    		                {
	    		                    String simpleName = Signature.getSimpleName(idclassName);
	    		                    String[] names = simpleName.split("\\$");
	
	    		                    renderer.createCompilationUnit(names[0],ClassUtils.getPackageFragment(idunit));
	    		                    idunit.makeConsistent(null);
	    		                }
	    		                IType idType = idunit.getType(idclassName);
	    		                if (idType!=null)
	    		                {
	    		                    String[] singequals={"Object"};
	    		                    String[] singhash={};
//	    		                    if (idType.getMethod("equals",singequals).exists())
//	    		                    {
//	    		                    	idType.getMethod("equals",singequals).delete(true,monitor);
//	    		                    }
//	    		                    if (idType.getMethod("hashCode",singhash).exists())
//	    		                    {
//	    		                    	idType.getMethod("hashCode",singhash).delete(true,monitor);
//	    		                    }

		    						compproperties=new String[cm.getPropertySpan()];
		    						iterator = cm.getPropertyIterator();
		    						int i=0;
		    						while(iterator.hasNext())
		    						{
		    							compproperties[i++]=
		    								//pcmapping.getIdentifier().getFieldMapping().getName()+"."+
		    								((PropertyMapping)iterator.next()).getName();	
		    						}

                                    renderer.createEquals(idType,compproperties);		    				        
		    						renderer.createHashCode(idType,compproperties);                                   
		    						renderer.createToString(idType,compproperties);                                   
	    		                    
	    		                }
    						}
    						compproperties=new String[1];
    						compproperties[0]=pcmapping.getIdentifier().getFieldMapping().getName();
    						
    					}
    					else
    					{
    						if ((pcmapping.getIdentifier()!=null)&&(((SimpleValueMapping)pcmapping.getIdentifier()).getIdentifierGeneratorStrategy().equals("assigned")))
    						{
    							compproperties=new String[1];
    							compproperties[0]=((SimpleValueMapping)pcmapping.getIdentifier()).getFieldMapping().getPersistentField().getName();
    						}
    						else
    						{
    							PropertyMapping idpm=(PropertyMapping) pcmapping.getIdentifierProperty();
    							ArrayList prop=new ArrayList();
    							iterator = pcmapping.getPropertyIterator();
    							PropertyMapping pm;
        						while(iterator.hasNext())
        						{
        							pm=(PropertyMapping)iterator.next();
        							
        							if ((pm.getValue()!=null)&&(idpm!=pm)&&(pm.getValue().isSimpleValue()))
        							{
//        								Column col=(Column)pm.getValue().getColumnIterator().next();
//        								if (col.getOwnerTable().getIndexName(col.getName())!=null)
//        								{
//        									prop.add(pm.getName());
//        								}
//        								else
//        								{
        									if (pm.getUnique())
        										prop.add(pm.getName());
//        								}
        							}
        						}
        						if (prop.size()>0)
        						{
        							compproperties=new String[prop.size()];
        							for(int i=0;i<prop.size();i++)
        								compproperties[i]=(String) prop.get(i);
        						}
    						}
    					}
                        renderer.createEquals(type,compproperties);                                   
                        renderer.createHashCode(type,compproperties);                                   
                        renderer.createToString(type,compproperties);                                   
	    			}
                }
                else 
                {
                    String simpleName = Signature.getSimpleName(className);
                    String[] names = simpleName.split("\\$");
                    if ((names.length>1)&&(mapping!=null)&&(unit!=null)&&(unit.findPrimaryType()!=null))
                    {
                    	IPersistentClass clazz = mapping.findClass(unit.findPrimaryType().getFullyQualifiedName());
                    	if ((clazz!=null)&&(clazz.getPersistentClassMapping()!=null)&&
                    			(clazz.getPersistentClassMapping().getIdentifier() instanceof ComponentMapping)&&
                    			(clazz.getPersistentClassMapping().getIdentifier().getFieldMapping().getPersistentField().getType().equals(className)))
                    	{
		                    String[] singequals={"QObject;"};
		                    String[] singhash={};
//		                    if (type.getMethod("equals",singequals).exists())
//		                    {
//		                    	type.getMethod("equals",singequals).delete(true,monitor);
//		                    }
//		                    if (type.getMethod("hashCode",singhash).exists())
//		                    {
//		                    	type.getMethod("hashCode",singhash).delete(true,monitor);
//		                    }
                    		
                    		ComponentMapping cm=(ComponentMapping) clazz.getPersistentClassMapping().getIdentifier();
    						String[] compproperties=new String[cm.getPropertySpan()];
    						Iterator iterator = cm.getPropertyIterator();
    						int i=0;
    						while(iterator.hasNext())
    						{
    							compproperties[i++]=
    								//pcmapping.getIdentifier().getFieldMapping().getName()+"."+
    								((PropertyMapping)iterator.next()).getName();	
    						}
                            renderer.createEquals(type,compproperties);                                   
                            renderer.createHashCode(type,compproperties);                                   
                            renderer.createToString(type,compproperties);                                   
    						ClassUtils.createSerialVersion(type,rdmz);
    						if ((clazz.getSourceCode()!=null)&&(clazz.getSourceCode().findPrimaryType()!=null))
    						{
//    		                    if (clazz.getSourceCode().findPrimaryType().getMethod("equals",singequals).exists())
//    		                    {
//    		                    	clazz.getSourceCode().findPrimaryType().getMethod("equals",singequals).delete(true,monitor);
//    		                    }
//    		                    if (clazz.getSourceCode().findPrimaryType().getMethod("hashCode",singhash).exists())
//    		                    {
//    		                    	clazz.getSourceCode().findPrimaryType().getMethod("hashCode",singhash).delete(true,monitor);
//    		                    }
    							
    							compproperties=new String[1];
    							compproperties[0]=clazz.getPersistentClassMapping().getIdentifier().getFieldMapping().getPersistentField().getName();
                                renderer.createEquals(clazz.getSourceCode().findPrimaryType(),compproperties);                                   
                                renderer.createHashCode(clazz.getSourceCode().findPrimaryType(),compproperties);                                   
                                renderer.createToString(clazz.getSourceCode().findPrimaryType(),compproperties);                                   
    						}
    						
                    	}

                    }
                }
                   
//                }
*/
                o_monitor.worked();
            }
        }
    }

}
