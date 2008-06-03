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
package org.jboss.tools.hibernate.internal.core.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.jboss.tools.hibernate.core.IOrmProject;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;

/**
 * Searching file with given fileName in given project
 * @author troyas
 *
 */
public class ScanProject {
	
    public interface IFilterInterface {
        boolean isCompatible(Object o);
    }
    
    public static final int SCOPE_SRC = 1;
    public static final int SCOPE_BIN = 2;
	public static final int SCOPE_ROOT = 4;
    public static final int SCOPE_ALL = SCOPE_BIN | SCOPE_SRC | SCOPE_ROOT;
    
    public static ICompilationUnit findCUByResource(IResource resource) throws CoreException
    {
	    IJavaProject jProject = JavaCore.create(resource.getProject());
	    IPath resourcePath = resource.getFullPath();
	    
	    IPackageFragment fragment = jProject.findPackageFragment(resourcePath.uptoSegment(resourcePath.segmentCount() - 1));
	    return (ICompilationUnit)fragment.getCompilationUnit(resourcePath.lastSegment());
    }
    
    public static IPath relativePathToAbsolute(IPath path, IProject project)
    {
        IPath thePath = null;
        if (path != null)
        {
            IResource rsrc = project.getWorkspace().getRoot().findMember(path);
            thePath = path;
            if (rsrc != null)
            {
                thePath = rsrc.getLocation();
            }
        }
        return thePath;
    }
    
    public static IResource[] getScopedPaths(IProject project, int scope) throws CoreException
	{
		if (scope == SCOPE_ALL)
		{
		    IResource[] rootMembers = project.members();
			return rootMembers;
		}
		IJavaProject javaProject = JavaCore.create(project);
		
		//TODO (tau->tau) !!! java.lang.ClassCastException 31.03.2006
//		Internal Error
//		java.lang.ClassCastException
//			at org.eclipse.jem.internal.plugin.JavaEMFNature.primaryContributeToContext(JavaEMFNature.java:139)
//...
//			at org.jboss.tools.hibernate.internal.core.util.ScanProject.getScopedPaths(ScanProject.java:95)		
		
		IClasspathEntry[] entries = javaProject.getResolvedClasspath(true);
		Vector<IResource> filteredEntries = new Vector<IResource>();
		
		for (int i = 0; i < entries.length; i++) {
            IClasspathEntry entry = entries[i];
            
            boolean addEntry = ((scope & SCOPE_SRC) != 0 && entry.getContentKind() == IPackageFragmentRoot.K_SOURCE);
            
            if (!addEntry)
                addEntry = ((scope & SCOPE_BIN) != 0 && entry.getContentKind() == IPackageFragmentRoot.K_BINARY);
            
            if (addEntry)
            {
                IResource rsrc = project.getWorkspace().getRoot().findMember(entry.getPath());
                if (rsrc != null)
                    filteredEntries.add(rsrc);
            }
        }
		IResource[] resources = (IResource[])filteredEntries.toArray(new IResource[]{});
		return resources;
	}
	
	//by Nick 16.03.2005 scope qualifier introduced to skip binary/source folders searching
	public static IFile scanner(String fileName, IProject project, int scope) throws CoreException{
		IResource[] resources = getScopedPaths(project,scope);
		IFile file = scanForResource(resources, fileName);
		return file;
	}

    //by Nick - methods scanner, getResources changed to static for convenience
	/**
	 * Get the file with the spectfied file name in the specified project
	 * @param fileName
	 * @param project
	 * @return
	 * @throws CoreException
	 */
	public static IFile scanner(String fileName, IProject project) throws CoreException{
		return scanner(fileName, project, SCOPE_ALL);
	}

	/**
	 * Get files with the specified end of file name in the specified project
	 * @param strEnd
	 * @param project
	 * @return
	 * @throws CoreException
	 */
	public static List getResources(String strEnd, IProject project, int scope) throws CoreException{
		List<IResource> resources_found = new ArrayList<IResource>();
		if ((scope & SCOPE_ROOT) != 0)
		{	
			// Try to find our file in project root
			IResource[] rootMembers = project.members();
			for (int i = 0; i < rootMembers.length; i++) {
				IResource resource = rootMembers[i];
				if (resource.getType() == IResource.FILE && resource.getName().endsWith(strEnd))
					resources_found.add(resource);
			}
		}		
		IResource[] resources = getScopedPaths(project,scope);
		resources_found.addAll(scanForResources(resources, strEnd));
		return resources_found;
	}
	
	/**
	 * Get files with the specified end of file name in the specified project
	 * @param strEnd
	 * @param project
	 * @return
	 * @throws CoreException
	 */
	public static List getResources(String strEnd, IProject project) throws CoreException{
		return getResources(strEnd, project, SCOPE_ALL);
	}
	
	//TODO EXP9
	//add tau 03.03.2006	
	private static IPackageFragment[] findAllOrFirstPackageFragment(String packageName, IProject project, boolean findAll)
    {
		
		//TODO For TEST 03.03.2006
		//System.out.println("packageName="+packageName+",project="+project.getName());
		
		if (project == null) {
			//System.out.println("projectNULL="+packageName);			
			return null;
		}
		
        if (packageName == null) {
    		//System.out.println("packageNameNULL="+packageName);
            return null;    		
        }
        
        IJavaElement[] projectAllElements = new IPackageFragment[0];
        IJavaElement[] projectPackageElements = null;        
        
        try {
        	IOrmProject ormProject = OrmCore.getDefault().getOrmModel().getOrmProject(project);
        	if (ormProject != null){
        		projectPackageElements = ormProject.getPackageFragments(packageName, findAll);
        	} else {
        		projectAllElements = JavaCore.create(project).getAllPackageFragmentRoots();        		
        	}
        	
        } catch (JavaModelException e) {
            ExceptionHandler.logThrowableWarning(e,"Exception retrieving Java project children, processing package: <" + packageName + ">");
        }

        if (projectPackageElements != null) {
        	return (IPackageFragment[]) projectPackageElements;
        } else {
        	return findPackageFragments(packageName, projectAllElements, findAll);
        }
    }
	
	//metod add tau 03.03.2006	
	public static IPackageFragment[] findPackageFragments(String packageName, IJavaElement[] projectAllElements, boolean findAll) {
        ArrayList<IPackageFragment> fragments = new ArrayList<IPackageFragment>();		
        for (int k = 0 ; k < projectAllElements.length && (findAll || fragments.size() == 0); k++ )
        {
            IJavaElement element = projectAllElements[k];
            if (element.getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT)
            {
                IPackageFragmentRoot fragmentRoot = (IPackageFragmentRoot)element;
                IPackageFragment packageFragment = fragmentRoot.getPackageFragment(packageName);
                if (packageFragment.exists() && (findAll || fragments.size() == 0)){
                   	fragments.add(packageFragment);
                }
            }
        }
        return fragments.toArray(new IPackageFragment[0]);
	}

	/*
	private static IPackageFragment[] findAllOrFirstPackageFragment(String packageName, IProject project, boolean findAll)
    {
		System.out.println("packageName="+packageName+",project="+project.getName());		
		
        if (packageName == null) {
    		System.out.println("packageNameNULL="+packageName);
            return null;    		
        }
        ArrayList fragments = new ArrayList();
        IJavaProject javaProject = JavaCore.create(project);
        IJavaElement[] projectElements = null;
        try {
            projectElements = javaProject.getAllPackageFragmentRoots();
        } catch (JavaModelException e) {
            ExceptionHandler.logThrowableWarning(e,"Exception retrieving Java project children, processing package: <" + packageName + ">");
        }
        boolean classFound = false;
        if (projectElements != null)
        for (int k = 0 ; k < projectElements.length && (findAll || fragments.size() == 0); k++ )
        {
            IJavaElement element = projectElements[k];
            if (element.getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT)
            {
                IPackageFragmentRoot fragmentRoot = (IPackageFragmentRoot)element;
                
                IJavaElement[] pkgFragments = null;
                //try {
                	
                	// edit tau 28.02.2006
                    //pkgFragments = fragmentRoot.getChildren();
                    IPackageFragment packageFragment = fragmentRoot.getPackageFragment(packageName);
                    if (packageFragment.exists() && (findAll || fragments.size() == 0)){
                    	fragments.add(packageFragment);
                    }
                    
                //} catch (JavaModelException e) {
                //    ExceptionHandler.logThrowableWarning(e,"Exception retrieving package fragment root children, processing package: <" + packageName + ">");
                //}
                
                
                //if (pkgFragments != null)
                	
                //for (int m = 0 ; m < pkgFragments.length && (findAll || fragments.size() == 0); m++ )
                //for (int m = 0 ; m < pkgFragments.length && (findAll || fragments.size() == 0); m++ )                	
                //{
                //    if (pkgFragments[m].getElementType() == IJavaElement.PACKAGE_FRAGMENT)
                //        if (pkgFragments[m].getElementName().equals(packageName))
                //            fragments.add((IPackageFragment)pkgFragments[m]);
                //}
                    
            }
        }

        return (IPackageFragment[]) fragments.toArray(new IPackageFragment[0]);
    }
	*/
    
    public static IPackageFragment[] findAllPackageFragments(String packageName, IProject project) throws CoreException
    {
        return findAllOrFirstPackageFragment(packageName,project,true);
    }
    
    /**
	 * finds first package fragment by name
	 * @param packageName
	 * @param project
	 * @return
	 * @throws CoreException
	 */
	public static IPackageFragment findFirstPackageFragment(String packageName, IProject project) throws CoreException
	{
        IPackageFragment[] fragments = findAllOrFirstPackageFragment(packageName,project,false);
        if (fragments == null || fragments.length == 0)
            return null;
        
        return fragments[0];
	}
	
	/**
	 * Finds compilation unit by class FQN
	 * !!!WARNING: doesn't handle nested classes - returns null for them
	 * @param className - the name of the class to search for
	 * @param packageName - the name of the package to search in
	 * @param project - project to search
	 * @return compilation unit for the class
	 */
	public static ICompilationUnit findCompilationUnit(String className, String packageName, IProject project) throws CoreException
	{
		return findCompilationUnit(Signature.toQualifiedName(new String[]{packageName,className}),project);
/*
	    //old code queued for deletion
	      
 	    IJavaProject javaProject = JavaCore.create(project);
	    IPackageFragment fragment = findPackage(packageName,project);
	    if (fragment != null)
	        unit = fragment.getCompilationUnit(className + ".java");
*/      
	}

	public static ICompilationUnit findCompilationUnit(String fQName, IProject project) throws CoreException
	{
	    ICompilationUnit unit = null;
	    IType classType = findClass(fQName,project);  
	    
	    //skip inner classes
	    if (classType != null && !classType.isMember())
	        unit = classType.getCompilationUnit();
	    return unit;
	}
	
    public static IType findClassInCU(ICompilationUnit unit, String className) throws JavaModelException
    {
        if (unit == null || !unit.exists() || className == null)
            return null;

        // added by Nick 07.10.2005
        ICompilationUnit wc = null;
        if (!unit.isWorkingCopy())
            wc = unit.getWorkingCopy(null); 
        // by Nick
        
        IType result = null;
        IType[] types = unit.getAllTypes();
        if (types != null)
        {
            for (int i = 0; i < types.length && result == null; i++) {
                IType type = types[i];
                if (className.equals(type.getTypeQualifiedName()))
                    result = type;
            }
        }

        if (wc != null)
        {
            wc.discardWorkingCopy();
        }
        
        return result;
    }
    
    public static IType findClass(String className, IPackageFragment[] thePackage)
    {
        if (className == null)
            return null;
        
        if (thePackage == null || thePackage.length == 0)
            return null;
        
        IType theType = null;

        String cuName = null;
        int index = className.indexOf('$');
        if (index != -1)
        {
            cuName = className.substring(0,index);
        }
        else
        {
            cuName = className;
        }
        cuName += ".java";
        
        String cfName = className+".class";
        
        for (int j = 0; j < thePackage.length && theType == null; j++) 
        {
            IPackageFragment fragment = thePackage[j];
            
            ICompilationUnit unit = fragment.getCompilationUnit(cuName);
            
            try {
                if (unit != null && unit.exists())
                {
                    theType = findClassInCU(unit,className);
                }
                else
                {
                    IClassFile classFile = fragment.getClassFile(cfName);
                    if (classFile != null && classFile.exists())
                    {
                        theType = classFile.getType();
                    }
                }
            } catch (JavaModelException e) {
                ExceptionHandler.logThrowableError(e,"Exception searching class");
            }
        }
        return theType;
    }
    
	/**
	 * This method searches for classes in project and return matched or null
	 * inner classes also can be searched for
	 * @param className
	 * @param packageName
	 * @param project
	 * @return
	 * @throws CoreException
	 */
	public static IType findClass(String className, String packageName, IProject project) throws CoreException
	{
        if (packageName == null)
            return null;

		IPackageFragment[] pkg = findAllPackageFragments(packageName,project);
		
		if (pkg == null)
			return null;
		
		return findClass(className,pkg);
    }

	public static IType findClass(String fQName, IProject project) throws CoreException
	{
        if (fQName == null)
			return null;

		return findClass(Signature.getSimpleName(fQName),Signature.getQualifier(fQName), project);
	}

	/**
	 * Searches for file with fileName in project root or in classPath
	 * @param fileName
	 * @param project
	 * @return
	 * @throws CoreException
	 */
	public static IFile scannerCP(String fileName, IProject project) throws CoreException{
		IFile resource = null;

		// Try to find our file in project root
		IPath path = (IPath) project.getProjectRelativePath();
		if (project.exists(path.append(fileName)))
			return project.getFile(path.append(fileName));
		
		// Try to find our file in project's classpath
		IJavaProject javaProject = JavaCore.create(project);
		IClasspathEntry[] entries = javaProject.getResolvedClasspath(true);
		for (int i = 0 ; i < entries.length && resource == null ; i++ )
		{
			path = entries[i].getPath();
			if (project.exists(path.append(fileName)))
				resource = project.getFile(path.append(fileName));
		}
		return resource;
	}
	
	
	// Helper methods
	
	private static IFile scanForResource(IResource[] res, String fileName) throws CoreException{
//		IFile nesFile = null;
		IFile resultFile = null;
		for(int i=0; i<res.length; i++){
			if(res[i].getType()==IResource.FILE){
				IFile file = (IFile)res[i];
				if(file.getName().equals(fileName))
					resultFile = file;
			}
			else if(res[i].getType()==IResource.FOLDER) {
				IFile deeperFile = scanForResource(((IFolder)res[i]).members(), fileName);
				if (deeperFile != null)
				    resultFile = deeperFile;

			if (resultFile != null)
			    break;
			}
		}
		return resultFile;
	}
	
	private static List<IFile> scanForResources(IResource[] res, String endOfName) throws CoreException{
		List<IFile> list = new ArrayList<IFile>();
//	    IFile nesFiles = null;
		for(int i=0; i<res.length; i++){
			if(res[i].getType()==IResource.FILE){
				IFile file = (IFile)res[i];
				if(file.getName().endsWith(endOfName)){
					list.add(file);
				}				
			}
			else if(res[i].getType()==IResource.FOLDER){
				list.addAll(scanForResources(((IFolder)res[i]).members(), endOfName));
			}
		}
		return list;
	}
	
	/**
	 * returns a file from source folder
	 * */
	public static IFile findResource(String resourceName, IProject project) throws CoreException {
		// Try to find our file in project root
		IPath path = project.getProjectRelativePath();
		path=path.append(resourceName);
		if (project.exists(path))return project.getFile(path);
		IContainer parent= project.getParent();
		
		IJavaProject javaProject=JavaCore.create(project);
		IPackageFragmentRoot[] roots= javaProject.getAllPackageFragmentRoots();
		IPackageFragmentRoot r=null;
		for(int i=0;i<roots.length;++i) {
			r=roots[i];
			if (r.getKind() == IPackageFragmentRoot.K_BINARY)
            {
                path=r.getPath().append(r.getSourceAttachmentRootPath()).append(resourceName);
                if (parent.exists(path))return parent.getFile(path);
            }
            else if (r.getKind() == IPackageFragmentRoot.K_SOURCE)
            {
                path = r.getPath().append(resourceName);
                IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
                if (root.exists(path))
                    return root.getFile(path);
            }
        }
		return null;
	}
	/**
	 * returns relative path in a source folder
	 * */
	public static String getRelativePath(IResource file) throws CoreException {
		IPath path=file.getFullPath();
		if(path.segmentCount()>2){ 
			IJavaProject javaProject=JavaCore.create(file.getProject());
			IPackageFragmentRoot[] roots= javaProject.getAllPackageFragmentRoots();
			IPackageFragmentRoot r=null;
			for(int i=0;i<roots.length;++i) {
				r=roots[i];
				IPath rp=r.getPath().append(r.getSourceAttachmentRootPath());
				int segs=path.matchingFirstSegments(rp);
				if(segs>1){ //including project
					return path.removeFirstSegments(segs).toString(); 
				}
			}
		}
		return file.getProjectRelativePath().toString();
	}
	
    
    // added by Nick 08.06.2005
    //
    // this code was primarily getSourcePath(IProject) method
    // added package name filter
    // if filter == null finds any(first) source path
    public static IPath getPackageSourcePath(String packageName, IProject project) throws CoreException
    {
        
    	IPath path=null;
        IJavaProject javaProject=JavaCore.create(project);
        IPackageFragmentRoot[] roots= javaProject.getAllPackageFragmentRoots();
        IPackageFragmentRoot r=null;
        for(int i=0;i<roots.length;++i) {
            r=roots[i];
            
            boolean isNeededRoot = false;

            if (packageName == null)
            {
                isNeededRoot = true;
            }
            else
            {
                IPackageFragment fragment = r.getPackageFragment(packageName);
                if (fragment != null && fragment.exists())
                {
                    isNeededRoot = true;
                }
            }
            
            // edit tau 09.12.2005
            //if(isNeededRoot && !r.isArchive() && !r.isExternal() && !r.isReadOnly() && r.isOpen()){
            
            //if(isNeededRoot && !r.isArchive() && !r.isExternal() && !r.isReadOnly() && r.isOpen() &&         
            //	r.getJavaProject().getProject().equals(project)){
            if(isNeededRoot && !r.isArchive() && !r.isExternal() && !r.isReadOnly() && r.isOpen() &&         
            	r.getJavaProject().getProject().equals(project)){
            
            		path=r.getPath().append(r.getSourceAttachmentRootPath());

                // changed by Nick 29.06.2005
//                int seg=path.matchingFirstSegments(project.getFullPath());
//              if(seg>0)path=path.removeFirstSegments(seg);
                // by Nick
                
            		break;
            }
        }
        return path;
    }
    //by Nick
    
//  added by tau 09.03.2006
    //
    // this code was primarily getSourcePath(IProject) method
    // added package name filter
    // if filter == null finds any(first) source path
    public static IPath getPackageSourcePathQic(String packageName, IProject project) throws CoreException
    {
        
    	IPath path = null;
    	IPackageFragment packageFragment = findFirstPackageFragment(packageName, project);    	
        	
        if (packageFragment != null && packageFragment.getJavaProject().getProject().equals(project) ) {
        	path =  packageFragment.getPath();
        }
    
        return path;
    }
    
    
    /**
	 * Returns first source folder found in the given project. Returns null if no such folder was found. 
	 * */
	public static IPath getSourcePath(IProject project) throws CoreException {
	    IPath path = getPackageSourcePath(null,project);
	    //IPath path2 = getPackageSourcePathQic(null,project); test	    
	    if (path != null)
	    {
	        path = project.getWorkspace().getRoot().findMember(path).getProjectRelativePath();   
	    }
	    return path;
    }

    public static String[] getProjectSourceTypesNames(IPackageFragment fragment)
    {
        Vector<String> typesVector = new Vector<String>();
        try {
            ICompilationUnit[] units = fragment.getCompilationUnits();
            for (int i = 0; i < units.length; i++) {
                ICompilationUnit unit = units[i];
                
                ICompilationUnit wc = null;
                // added by Nick 07.10.2005
                if (!unit.isWorkingCopy())
                    wc = unit.getWorkingCopy(null);
                // by Nick
                
                IType[] types = unit.getAllTypes();
                
                if (types != null)
                    for (int j = 0; j < types.length; j++) {
                        IType type = types[j];
                        typesVector.add(type.getTypeQualifiedName());
                    }
            
                if (wc != null)
                {
                    wc.discardWorkingCopy();
                }
            }
        } catch (JavaModelException e) {
            ExceptionHandler.logThrowableError(e,e.getMessage());
        }
        return typesVector.toArray(new String[0]);
    }

    public static List<ICompilationUnit> getAllCompilationUnits(List packageFragments, IFilterInterface filter)
    {
        List<ICompilationUnit> units = new ArrayList<ICompilationUnit>();
        
        Iterator packIterator = packageFragments.iterator();
        
        while (packIterator.hasNext())
        {
            Object o = packIterator.next();
            if (o instanceof IPackageFragment) {
                IPackageFragment fragment = (IPackageFragment) o;
                
                ICompilationUnit[] packageUnits;
                try {
                    packageUnits = fragment.getCompilationUnits();
                    
                    for (int i = 0; i < packageUnits.length; i++) {
                        ICompilationUnit unit = packageUnits[i];
                        if (filter.isCompatible(unit))
                            units.add(unit);
                    }
                } catch (JavaModelException e) {
                    ExceptionHandler.logThrowableError(e,e.getMessage());
                }
            }
        }

        return units;
    }

    /*
    // del tau 03.03.2006
	// Add tau 02.03.2006 for performance 
	public static ICompilationUnit findJavaCompilationUnit(String persistentClassName, IPackageFragmentRoot[] packageFragmentRoots) throws CoreException {
		
        if (persistentClassName == null || packageFragmentRoots.length == 0 ) {
            return null;    		
        }
        
        ICompilationUnit unit = null;
        
        String packageName = Signature.getQualifier(persistentClassName);
        if (packageName == null) return null;

        
	    //IType classType = findClass(Signature.getSimpleName(persistentClassName),Signature.getQualifier(persistentClassName));
	    
		//public static IType findClass(String className, String packageName, IProject project) throws CoreException

			//IPackageFragment[] pkg = findAllPackageFragments(packageName,project);
			
			//if (pkg == null)
			//	return null;
			
		//return findClass(className,pkg);
	    

        ArrayList fragments = new ArrayList();
        
        for (int k = 0 ; k < packageFragmentRoots.length && fragments.size() == 0; k++ )
        {
            IJavaElement element = packageFragmentRoots[k];
            if (element.getElementType() == IJavaElement.PACKAGE_FRAGMENT_ROOT)
            {
                IPackageFragmentRoot fragmentRoot = (IPackageFragmentRoot)element;
                
                IJavaElement[] pkgFragments = null;
                //IPackageFragment packageFragment = fragmentRoot.getPackageFragment(packageName);
                IPackageFragment packageFragment = fragmentRoot.getPackageFragment(packageName);
                
                if (packageFragment.exists() && fragments.size() == 0){
                  	fragments.add(packageFragment);
                 }
            }
        }
        
        //return (IPackageFragment[]) fragments.toArray(new IPackageFragment[0]);

		IType classType = findClass(Signature.getSimpleName(persistentClassName),
									(IPackageFragment[]) fragments.toArray(new IPackageFragment[0]));        
        
        
	    //skip inner classes
	    if (classType != null && !classType.isMember()) {
	        unit = classType.getCompilationUnit();
	    }
        
	    return unit;
        
    }
    */
    
}
