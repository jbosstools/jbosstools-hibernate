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
package org.jboss.tools.hibernate.internal.core.codegenerator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.Signature;
import org.jboss.tools.hibernate.core.ICodeGenerationService;
import org.jboss.tools.hibernate.core.OrmProgressMonitor;
import org.jboss.tools.hibernate.internal.core.hibernate.HibernateMapping;
import org.jboss.tools.hibernate.internal.core.util.ClassUtils;


/**
 * @author kaa
 * akuzmin@exadel.com
 * Oct 12, 2005
 */
public class DAORunnablesHolder {
    static abstract class GenericRunnable implements IWorkspaceRunnable
    {
        protected int jobPart = 1000;

        protected OrmProgressMonitor o_monitor;       
        public void setJobPart(int newJobPart)
        {
            if (newJobPart != 0)
                jobPart = newJobPart;
        }

        public void setOrmMonitor(OrmProgressMonitor monitor)
        {
        	o_monitor=monitor;
        }
        
        public abstract void run(IProgressMonitor monitor) throws CoreException;
    
        protected ICodeGenerationService extrenderer = null;
        protected String interfaceName;
        protected String implementationName;
        public GenericRunnable(ICodeGenerationService extrenderer,String interfaceName, String implementationName )
        {
            this.extrenderer=extrenderer;
            this.interfaceName=interfaceName;
            this.implementationName=implementationName;
        }
    }

    static class DAOCreateCompilationUnitsOperation extends GenericRunnable {
        private Map<String,ICompilationUnit> units = new HashMap<String,ICompilationUnit>();
//        private HibernateMapping mapping;
        private boolean isInterface;
        private boolean isUseInterface;
        private String baseTypeName;
        public DAOCreateCompilationUnitsOperation(ICodeGenerationService extrenderer, HibernateMapping mapping, String baseTypeName, String interfaceName, String implementationName,
        		boolean isInterface,boolean isUseInterface)
        {
            super(extrenderer,interfaceName,implementationName);
//            this.mapping=mapping;
            this.isInterface=isInterface;
            this.isUseInterface=isUseInterface;
            this.baseTypeName=baseTypeName;
        }
        
        ICompilationUnit addNameToQueue(IPackageFragment fragment, String name)
        {
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
        	if (o_monitor==null)
        		o_monitor = new OrmProgressMonitor(monitor);
            o_monitor.setTaskParameters(jobPart,units.size());

            Iterator itr = units.keySet().iterator();
            if (itr != null)
            while (itr.hasNext())
            {
                String className = (String)itr.next();
                //create .java file for reversed class
                ICompilationUnit unit = (ICompilationUnit)units.get(className);
                if (unit == null || !unit.exists())
                {
                    String simpleName = Signature.getSimpleName(className);
                    String[] names = simpleName.split("\\$");

                    extrenderer.createCompilationUnit(names[0],ClassUtils.getPackageFragment(unit));
                    unit.makeConsistent(null);
                }
                

                if (isInterface)
                {
                	extrenderer.createImportType(unit,Signature.getSimpleName(className));                	
                }
                else
                {
	                String[] ifaceNames = null;
	                
	                if (isUseInterface)
	                {
	                	ifaceNames=new String[1];
	                	if (unit.getElementName().lastIndexOf(implementationName)==-1)
		                	ifaceNames[0]=ClassUtils.getPackageFragment(unit).getElementName()+'.'+Signature.getSimpleName(className).substring(0,Signature.getSimpleName(className).lastIndexOf(implementationName))+interfaceName;                      
	                	else
	                		ifaceNames[0]=ClassUtils.getPackageFragment(unit).getElementName()+'.'+unit.getElementName().substring(0,unit.getElementName().lastIndexOf(implementationName))+interfaceName;                      
	                }
	                extrenderer.createTypeWithOutMethodStubs(unit,Signature.getSimpleName(className),baseTypeName,ifaceNames,className.indexOf('$') != -1);
                }
                o_monitor.worked();
            }            
        }
    }

}
