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

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.hibernate.core.CodeGenerationService;
import org.jboss.tools.hibernate.core.ICodeGenerationService;
import org.jboss.tools.hibernate.core.IDAOGenerator;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.OrmProgressMonitor;
import org.jboss.tools.hibernate.core.PropertyInfoStructure;
import org.jboss.tools.hibernate.core.IAutoMappingService.Settings;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IManyToOneMapping;
import org.jboss.tools.hibernate.internal.core.CodeRendererServiceWrapper;
import org.jboss.tools.hibernate.internal.core.codegenerator.DAORunnablesHolder.DAOCreateCompilationUnitsOperation;
import org.jboss.tools.hibernate.internal.core.hibernate.ComponentMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.HibernateConfiguration;
import org.jboss.tools.hibernate.internal.core.hibernate.HibernateMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.PropertyMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.SimpleValueMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.ConfigurationReader;
import org.jboss.tools.hibernate.internal.core.util.ClassUtils;


/**
 * @author kaa
 * akuzmin@exadel.com
 * Oct 12, 2005
 */
public class DAOGenerator implements IDAOGenerator {
	private DAOCreateCompilationUnitsOperation createCUOperation,createInterfaceCUOperation,createTestCaseCUOperation;
    private HibernateMapping mapping;
	private ConfigurationReader hamConfig;    
	public DAOGenerator(HibernateMapping mapping) {
		this.mapping=mapping;
	}

	public void generateDAO(IPersistentClass[] classes, Settings settings,String packageName,final boolean generateInterfaces,final boolean isNeedLog,boolean generateTests) {
        OrmProgressMonitor monitor = new OrmProgressMonitor(OrmProgressMonitor.getMonitor());
        hamConfig = ConfigurationReader.getAutomappingConfigurationInstance(mapping,settings);
        final ICodeGenerationService extrender=new CodeGenerationServiceWraper(new CodeGenerationService());
        
        IPackageFragment[] thePackage = null;
        
         if (packageName == null)
            return;

        try {
            thePackage = hamConfig.getPojoRenderer().getOrCreatePackage(((HibernateConfiguration) mapping.getConfiguration()).getProject(),packageName);
        } catch (CoreException e) {
        	OrmCore.getPluginLog().logError("Exception creating package", e);
            return;
        }        
        
        if (thePackage == null || thePackage.length == 0)
            return;
        
        final IPackageFragment defPackage=thePackage[0];
        if (generateInterfaces)
		createInterfaceCUOperation = new DAOCreateCompilationUnitsOperation(/*hamConfig.getPojoRenderer(),*/extrender,mapping,null,getDAOInterfaceName(""),getDAOImplementationName(""),true,false);
		createCUOperation = new DAOCreateCompilationUnitsOperation(/*hamConfig.getPojoRenderer(),*/extrender,mapping,null,getDAOInterfaceName(""),getDAOImplementationName(""),false,generateInterfaces);
		createTestCaseCUOperation = new DAOCreateCompilationUnitsOperation(/*hamConfig.getPojoRenderer(),*/extrender,mapping,"junit.framework.TestCase",getDAOInterfaceName(""),getDAOTestCaseName(""),false,false);
		
		IWorkspaceRunnable methodsInterfaceRunnable =null;
		IWorkspaceRunnable methodsImplementationRunnable =null;
		IWorkspaceRunnable methodsTestCaseRunnable =null;
        for(int i=0;i<classes.length;i++)
        {
        	if (generateInterfaces)
        		 createInterfaceCUOperation.addNameToQueue(defPackage,getDAOInterfaceName(classes[i].getShortName()));
        	createCUOperation.addNameToQueue(defPackage,getDAOImplementationName(classes[i].getShortName()));
        	if (generateTests)
				try {
					if(classes[i].getType() != null && !Flags.isAbstract(classes[i].getType().getFlags()))
						createTestCaseCUOperation.addNameToQueue(defPackage,getDAOTestCaseName(classes[i].getShortName()));
				} catch (JavaModelException e) {
					OrmCore.getPluginLog().logError(e.getMessage(), e);
				}
        		
        }
        final IPersistentClass[] pcArray =classes;        
        if (generateInterfaces)
        {
        createInterfaceCUOperation.setJobPart(5);
        createInterfaceCUOperation.setOrmMonitor(monitor);
        methodsInterfaceRunnable = new IWorkspaceRunnable()
        {
            public void run(IProgressMonitor monitor) throws CoreException {

                OrmProgressMonitor o_monitor = new OrmProgressMonitor(monitor);
                o_monitor.setTaskParameters(100,pcArray.length);
                
                for (int i = 0; i < pcArray.length; i++) {
                    ICompilationUnit wc = null;
                    if (!defPackage.getCompilationUnit(getDAOInterfaceName(pcArray[i].getShortName())+".java").isWorkingCopy())
                        wc = defPackage.getCompilationUnit(getDAOInterfaceName(pcArray[i].getShortName())+".java").getWorkingCopy(null);

                    createDAOInterfaceMethods(extrender, pcArray[i],defPackage.getCompilationUnit(getDAOInterfaceName(pcArray[i].getShortName())+".java"));

                    if (wc != null)
                    {
                        wc.discardWorkingCopy();
                    }
                    
                    o_monitor.worked();
                } 
            }
        };
        
        }
        createCUOperation.setJobPart(5);
        createCUOperation.setOrmMonitor(monitor);
        methodsImplementationRunnable = new IWorkspaceRunnable()
        {
            public void run(IProgressMonitor monitor) throws CoreException {

                OrmProgressMonitor o_monitor = new OrmProgressMonitor(monitor);
                o_monitor.setTaskParameters(100,pcArray.length);
                
                for (int i = 0; i < pcArray.length; i++) {
                    ICompilationUnit wc = null;
                    if (!defPackage.getCompilationUnit(getDAOImplementationName(pcArray[i].getShortName())+".java").isWorkingCopy())
                        wc = defPackage.getCompilationUnit(getDAOImplementationName(pcArray[i].getShortName())+".java").getWorkingCopy(null);

                    createDAOimplementationCode(extrender, pcArray[i],defPackage.getCompilationUnit(getDAOImplementationName(pcArray[i].getShortName())+".java"),generateInterfaces,isNeedLog);

                    if (wc != null)
                    {
                        wc.discardWorkingCopy();
                    }
                    
                    o_monitor.worked();
                } 
            }
        };
/////////////////////
        if (generateTests)
        {
        	createTestCaseCUOperation.setJobPart(5);
        	createTestCaseCUOperation.setOrmMonitor(monitor);
        	methodsTestCaseRunnable = new IWorkspaceRunnable()
        	{
	            public void run(IProgressMonitor monitor) throws CoreException {
	
	                OrmProgressMonitor o_monitor = new OrmProgressMonitor(monitor);
	                o_monitor.setTaskParameters(100,pcArray.length);

	                for (int i = 0; i < pcArray.length; i++) 
	                	if(pcArray[i].getType() != null && !Flags.isAbstract(pcArray[i].getType().getFlags())){
	                		ICompilationUnit wc = null;
	                		if (!defPackage.getCompilationUnit(getDAOTestCaseName(pcArray[i].getShortName())+".java").isWorkingCopy())
	                			wc = defPackage.getCompilationUnit(getDAOTestCaseName(pcArray[i].getShortName())+".java").getWorkingCopy(null);


	                		createDAOTestCaseMethods(extrender, pcArray[i],defPackage.getCompilationUnit(getDAOTestCaseName(pcArray[i].getShortName())+".java"));

	                		if (wc != null)
	                		{
	                			wc.discardWorkingCopy();
	                		}

	                		o_monitor.worked();
	                	} 
	            }

	        };
	        
	        }
       
/////////////////////        
        IProgressMonitor theMonitor = OrmProgressMonitor.getMonitor();
        theMonitor.setTaskName("Creating source files");
        //TODO(tau->tau) RULE        
        try {
        	if (generateInterfaces)
        	{
        		((HibernateConfiguration) mapping.getConfiguration()).getProject().getWorkspace().run(createInterfaceCUOperation,new SubProgressMonitor(theMonitor,2));
        		((HibernateConfiguration) mapping.getConfiguration()).getProject().getWorkspace().run(methodsInterfaceRunnable,new SubProgressMonitor(theMonitor,8));
        	}
			((HibernateConfiguration) mapping.getConfiguration()).getProject().getWorkspace().run(createCUOperation,new SubProgressMonitor(theMonitor,2));
    		((HibernateConfiguration) mapping.getConfiguration()).getProject().getWorkspace().run(methodsImplementationRunnable,new SubProgressMonitor(theMonitor,8));
        	if (generateTests)
        	{
        		((HibernateConfiguration) mapping.getConfiguration()).getProject().getWorkspace().run(createTestCaseCUOperation,new SubProgressMonitor(theMonitor,2));
        		((HibernateConfiguration) mapping.getConfiguration()).getProject().getWorkspace().run(methodsTestCaseRunnable,new SubProgressMonitor(theMonitor,8));
        	}

		} catch (CoreException e) {
			OrmCore.getPluginLog().logError(e.getMessage(),e);
		}

		
	}

	private String getDAOInterfaceName(String className)
	{
		return className+"DAO";
	}
	
	private String getDAOImplementationName(String className)
	{
		return className+"DAOImpl";
	}

	private String getDAOTestCaseName(String className)
	{
		return className+"DAOTestCase";
	}
	
	private void createDAOInterfaceMethods(ICodeGenerationService renderer, IPersistentClass class1, ICompilationUnit unit) throws CoreException {
        IHibernateClassMapping pcmapping = (IHibernateClassMapping) class1.getPersistentClassMapping();
        PropertyInfoStructure compproperties=null;
        
        // add tau 23.05.2006 -> ESORM-582: NullPointerException - orm2.core.CodeRendererService.importTypeName
        pcmapping.getPersistentClass().getFields();            
        
        Iterator iterator = pcmapping.getPropertyIterator();
        PropertyMapping pm;
        IType searchClass=renderer.createType(unit,getDAOInterfaceName(class1.getShortName())+'$'+"SearchCriteria",null,null,true);
        while(iterator.hasNext())
        {
        	 compproperties=null;
             pm=(PropertyMapping)iterator.next();
             // #changed# by Konstantin Mishin on 10.03.2006 fixed for ESORM-542
//             if ((pm.getValue()!=null)&&(pm.getValue().isSimpleValue()))
//             {
//            	 compproperties=new PropertyInfoStructure(pm.getPersistentField().getName(),pm.getPersistentField().getType());
//             }
//             else
//                if ((pm.getValue()!=null)&&(pm.getValue() instanceof IManyToOneMapping))
//                {
//                	compproperties=new PropertyInfoStructure(pm.getPersistentField().getName(),pm.getPersistentField().getType());
//                }
             if (((pm.getValue()!=null)&&(pm.getValue().isSimpleValue())) || pm.getValue() instanceof IManyToOneMapping)
             {
            	 String type = pm.getPersistentField().getType();
            	 if(type == null && pm.getValue().getType() != null)
            		 type = pm.getValue().getType().getJavaType().getName();
            	 String name = pm.getPersistentField().getName();
         		if(JavaConventions.validateFieldName(name).matches(IStatus.ERROR))
        			name = "_"+name;
         		if(type != null)
            		 compproperties=new PropertyInfoStructure(name,
            				 ClassUtils.isPrimitiveType(type)?
            						 ClassUtils.getComplementarySimpleType(type):           					 
            							 type);
             }
             // #changed#
             if (compproperties!=null)
             {
                 // #changed# by Konstantin Mishin on 10.03.2006 fixed for ESORM-526
            	//renderer.createField(searchClass,compproperties,"");
            	renderer.createField(searchClass,compproperties,"public");
               	//renderer.createGetter(searchClass,compproperties);
               	//renderer.createSetter(searchClass,compproperties);
                // #changed# 
              }
            
        }
        // #changed# by Konstantin Mishin on 10.03.2006 fixed for ESORM-526 and ESORM-542
        compproperties=new PropertyInfoStructure("startFrom","int");
    	//renderer.createField(searchClass,compproperties,"");
    	renderer.createField(searchClass,compproperties,"public");
        compproperties=new PropertyInfoStructure("maxResults","int");
    	//renderer.createField(searchClass,compproperties,"");
    	renderer.createField(searchClass,compproperties,"public");
        //compproperties=new PropertyInfoStructure("descending","java.util.Set");
        compproperties=new PropertyInfoStructure("ascending","java.util.List");
    	//renderer.createField(searchClass,compproperties,"");
    	renderer.createField(searchClass,compproperties,"public");
        //compproperties=new PropertyInfoStructure("descending","java.util.Set");
        compproperties=new PropertyInfoStructure("descending","java.util.List");
    	//renderer.createField(searchClass,compproperties,"");
    	renderer.createField(searchClass,compproperties,"public");
        // #changed#
        renderer.createSearchCriteriaMethods(searchClass);
        if (pcmapping.getIdentifier()!=null)
        {
          	String idTypeName = null;
           	if (pcmapping.getIdentifier() instanceof ComponentMapping)
           		idTypeName=((ComponentMapping)pcmapping.getIdentifier()).getComponentClassName();
           	else {
            // #changed# by Konstantin Mishin on 27.01.2006 fixed for ESORM-29
           		if(pcmapping.getIdentifierProperty()!=null && pcmapping.getIdentifierProperty().getPersistentField()!=null)
           			idTypeName=pcmapping.getIdentifierProperty().getPersistentField().getType();
           		if(idTypeName == null && pcmapping.getIdentifier().getType() != null)
           			idTypeName=pcmapping.getIdentifier().getType().getJavaType().getName();
          	}
   			//idTypeName=pcmapping.getIdentifier().getType().getJavaType().getName();
        	// #changed#
           	renderer.createFinders(unit.findPrimaryType(),class1.getName(),idTypeName,true);
           	renderer.createWorkMethods(unit.findPrimaryType(),class1.getName(),idTypeName,true,false);
        }
        //screw away legacy auto-generated ctors
        IType workType = CodeRendererServiceWrapper.getWorkingCopy(unit.findPrimaryType());
        if (workType != null)
        {
            IMethod[] methods = workType.getMethods();
            
            if (methods != null)
            {
                for (int i = 0; i < methods.length; i++) {
                    IMethod method = methods[i];
                    
                    if (method.getElementName().equals(workType.getElementName()) && ClassUtils.isESGenerated(method))
                    {
                        method.delete(true, null);
                    }
                }
            }
            CodeRendererServiceWrapper.commitChanges(workType.getCompilationUnit());
            CodeRendererServiceWrapper.saveChanges(workType.getCompilationUnit());
        }
        else
        {
        	OrmCore.getPluginLog().logInfo("Working copy type does not exist!");
        }
        //screwed
		
	}
	
	private void createDAOimplementationCode(ICodeGenerationService renderer,IPersistentClass class1,ICompilationUnit unit,boolean isInterfaceExists,boolean isNeedLog) throws CoreException
	{
        IHibernateClassMapping pcmapping = (IHibernateClassMapping) class1.getPersistentClassMapping();
        // #changed# by Konstantin Mishin on 10.03.2006 fixed for ESORM-526
		//PropertyInfoStructure compproperty=new PropertyInfoStructure("sessionFactory","org.hibernate.SessionFactory");
		PropertyInfoStructure compproperty=new PropertyInfoStructure("session","org.hibernate.Session");
		renderer.createField(unit.findPrimaryType(),compproperty);
		renderer.createSetter(unit.findPrimaryType(),compproperty);
		renderer.createGetter(unit.findPrimaryType(),compproperty);
		if(isNeedLog)
			renderer.createLogField(unit.findPrimaryType());
//		renderer.createSessionMethods(unit.findPrimaryType(),isNeedLog);
	    // #changed#
//    	renderer.createConstructor(unit.findPrimaryType());
        if ((pcmapping.getIdentifier()!=null)&&(isInterfaceExists))
        {
            PropertyInfoStructure[] compproperties={};
            Iterator iterator;
            ArrayList<PropertyInfoStructure> prop = new ArrayList<PropertyInfoStructure>();
            
            // add tau 23.05.2006 -> ESORM-582: NullPointerException - orm2.core.CodeRendererService.importTypeName
            pcmapping.getPersistentClass().getFields();            
            
            iterator = pcmapping.getPropertyIterator();
            PropertyMapping pm;
            while(iterator.hasNext())
                 {
                        pm=(PropertyMapping)iterator.next();
                        String name = pm.getPersistentField().getName();
                        if(JavaConventions.validateFieldName(name).matches(IStatus.ERROR))
                			name = "_"+name;
                        if ((pm.getValue()!=null)&&(pm.getValue().isSimpleValue()))
                        {
                                    prop.add(new PropertyInfoStructure(name,pm.getPersistentField().getType()));
                        }
                        else
                            if ((pm.getValue()!=null)&&(pm.getValue() instanceof IManyToOneMapping))
                            {
                                        prop.add(new PropertyInfoStructure(name,pm.getPersistentField().getType()));
                            }

                        
                }
            compproperties=(PropertyInfoStructure[]) prop.toArray(new PropertyInfoStructure[0]);
        	String idTypeName = null;
        	if (pcmapping.getIdentifier() instanceof ComponentMapping)
        		idTypeName=((ComponentMapping)pcmapping.getIdentifier()).getComponentClassName();
           	else {
                // #changed# by Konstantin Mishin on 27.01.2006 fixed for ESORM-29
               		if(pcmapping.getIdentifierProperty()!=null && pcmapping.getIdentifierProperty().getPersistentField()!=null)
               			idTypeName=pcmapping.getIdentifierProperty().getPersistentField().getType();
               		if(idTypeName == null && pcmapping.getIdentifier().getType() != null)
               			idTypeName=pcmapping.getIdentifier().getType().getJavaType().getName();
              	}
        	// #changed# 
        	renderer.createFinders(unit.findPrimaryType(),class1.getName(),idTypeName,false);
        	renderer.createHBCriteria(unit.findPrimaryType(),class1.getName(),compproperties);
        	renderer.createWorkMethods(unit.findPrimaryType(),class1.getName(),idTypeName,false,isNeedLog);
        }
 
        //screw away legacy auto-generated ctors		
        IType workType = CodeRendererServiceWrapper.getWorkingCopy(unit.findPrimaryType());
        if (workType != null)
        {
            IMethod[] methods = workType.getMethods();
            
            if (methods != null)
            {
                for (int i = 0; i < methods.length; i++) {
                    IMethod method = methods[i];
                    
                    if (method.getElementName().equals(workType.getElementName()) && ClassUtils.isESGenerated(method))
                    {
                        method.delete(true, null);
                    }
                }
            }
            CodeRendererServiceWrapper.commitChanges(workType.getCompilationUnit());
            CodeRendererServiceWrapper.saveChanges(workType.getCompilationUnit());
        }
        else
        {
        	OrmCore.getPluginLog().logInfo("Working copy type does not exist!");
        }
        //screwed
		
	}
	private void createDAOTestCaseMethods(ICodeGenerationService extrender, IPersistentClass class1, ICompilationUnit unit) throws CoreException{
        IHibernateClassMapping pcmapping = (IHibernateClassMapping) class1.getPersistentClassMapping();
        if (pcmapping.getIdentifier()!=null)
        {
            PropertyInfoStructure[] compproperties={};
            Iterator iterator;
            ArrayList<PropertyInfoStructure> prop = new ArrayList<PropertyInfoStructure>();
            
            // add tau 23.05.2006 -> ESORM-582: NullPointerException - orm2.core.CodeRendererService.importTypeName
            pcmapping.getPersistentClass().getFields();            
            
            iterator = pcmapping.getPropertyIterator();
            PropertyMapping pm;
            while(iterator.hasNext())
                 {
                        pm=(PropertyMapping)iterator.next();
                        String type = pm.getPersistentField().getType();
                        if(type == null && pm.getValue().getType() != null)
                        	type = pm.getValue().getType().getJavaType().getName();
                        if ((pm.getValue()!=null)&&(pm.getValue().isSimpleValue()) &&  type != null)
                        {
                        			if (pcmapping.getIdentifier()==pm.getValue())
                        			{
                        				if ((pcmapping.getIdentifier()!=null)&&(((SimpleValueMapping)pcmapping.getIdentifier()).getIdentifierGeneratorStrategy().equals("assigned")))
                                            prop.add(new PropertyInfoStructure(pm.getPersistentField().getName(),type));
                        			}
                        			else
                                    prop.add(new PropertyInfoStructure(pm.getPersistentField().getName(),type));
                        }
//                        else
//                            if ((pm.getValue()!=null)&&(pm.getValue() instanceof IManyToOneMapping))
//                            {
//                                        prop.add(new PropertyInfoStructure(pm.getPersistentField().getName(),pm.getPersistentField().getType()));
//                            }

                        
                }
            compproperties=(PropertyInfoStructure[]) prop.toArray(new PropertyInfoStructure[0]);
            PropertyInfoStructure idInfo = null;
            if ((pcmapping.getIdentifier()!=null)&&(pcmapping.getIdentifier().getFieldMapping()!=null)&&(pcmapping.getIdentifier().getFieldMapping().getPersistentField()!=null))
            {
	        	if (pcmapping.getIdentifier() instanceof ComponentMapping)
	        	{
	        		idInfo=new PropertyInfoStructure(pcmapping.getIdentifier().getFieldMapping().getPersistentField().getName(),((ComponentMapping)pcmapping.getIdentifier()).getComponentClassName());
	        	}
	        	else
	        	{
	            // #changed# by Konstantin Mishin on 27.01.2006 fixed for ESORM-29
	           		if(pcmapping.getIdentifierProperty()!=null && pcmapping.getIdentifierProperty().getPersistentField()!=null) {
	           			String type = pcmapping.getIdentifierProperty().getPersistentField().getType();
	           			if(type == null && pcmapping.getIdentifier().getType() != null)
	           				type = pcmapping.getIdentifier().getType().getJavaType().getName();
	           			if(type != null)
	           				idInfo=new PropertyInfoStructure(pcmapping.getIdentifier().getFieldMapping().getPersistentField().getName(),type);
       				//idInfo=new PropertyInfoStructure(pcmapping.getIdentifier().getFieldMapping().getPersistentField().getName(),pcmapping.getIdentifier().getType().getJavaType().getName());
	           		}
	        	}
	        	if(idInfo != null)
	            // #changed#
	        		extrender.createDAOTestCaseMethods(unit.findPrimaryType(),class1.getName(),idInfo,compproperties);
            }
        }
		PropertyInfoStructure compproperty=new PropertyInfoStructure("dao",getDAOImplementationName(class1.getShortName()));
		String scopeModifier="private";
		extrender.createField(unit.findPrimaryType(),compproperty,scopeModifier);
        // #changed# by Konstantin Mishin on 10.03.2006 fixed for ESORM-526
		//compproperty=new PropertyInfoStructure("factory","org.hibernate.SessionFactory");
		compproperty=new PropertyInfoStructure("session","org.hibernate.Session");
        // #changed#
		extrender.createField(unit.findPrimaryType(),compproperty,scopeModifier);

        //screw away legacy auto-generated ctors		
        IType workType = CodeRendererServiceWrapper.getWorkingCopy(unit.findPrimaryType());
        if (workType != null) {
            IMethod[] methods = workType.getMethods();
            
            if (methods != null)  {
                for (int i = 0; i < methods.length; i++) {
                    IMethod method = methods[i];
                    
                    if (method.getElementName().equals(workType.getElementName()) && ClassUtils.isESGenerated(method))
                    {
                        method.delete(true, null);
                    }
                }
            }
            CodeRendererServiceWrapper.commitChanges(workType.getCompilationUnit());
            CodeRendererServiceWrapper.saveChanges(workType.getCompilationUnit());
        } else {
        	OrmCore.getPluginLog().logInfo("Working copy type does not exist!");
        }
        //screwed
		
	}
	
}
