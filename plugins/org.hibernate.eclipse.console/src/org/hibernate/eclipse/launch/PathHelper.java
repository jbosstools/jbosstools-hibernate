package org.hibernate.eclipse.launch;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
// TODO: move to internal.
public class PathHelper {

	public static String getLocationAsStringPath(String path) {
		if(path==null) return null;
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource member = PathHelper.findMember(root, path);
		if(member==null) {
			if (new File(path).exists()){
	        	return path;
	        } else {
	        	return null;
	        }
		}  else {
	        	return PathHelper.getLocation( member ).toOSString();
	    }		
	}

	public static IResource findMember(IWorkspaceRoot root, String path) {
		Path pathOrNull = PathHelper.pathOrNull(path);
		if(pathOrNull==null) return null;
		return root.findMember(pathOrNull);
	}

	public static IPath getLocation(final IResource resource) {		
		if (resource.getRawLocation() == null) { 
			return resource.getLocation(); 
		} 
		else return resource.getRawLocation();  
	}

	
	static private String resolve(String expression) {
		if(expression==null) return null;
		IStringVariableManager variableManager = VariablesPlugin.getDefault().getStringVariableManager();
		
		try {
			return variableManager.performStringSubstitution(expression, false);
		} catch (CoreException e) {
			return expression;
		}
	}
	
	public static Path pathOrNull(String p) {
		return pathOrNull(p, false);
	}
	
	public static Path pathOrNull(String p, boolean resolveVariables) {
		if(resolveVariables && p!=null) {
			p  = resolve(p);
		}
		if(p==null || p.trim().length()==0) {
			return null;
		} else {
			return new Path(p);
		}
	}
	
	/**
	 * Checks if directory exists.
	 * Handles variables replacement too.
	 * 
	 * @param strpath
	 * @param name
	 * @param checkFilesystem
	 * @return
	 */
	static public String checkDirectory(String strpath, String name, boolean checkFilesystem) {
		if(strpath.indexOf("${") >= 0) { //$NON-NLS-1$
			IStringVariableManager manager = VariablesPlugin.getDefault().getStringVariableManager();
			try {
				manager.validateStringVariables(strpath);
			}
			catch (CoreException e) {
				return name + " has invalid variable references [" + e.getMessage() + "]";				
			}
		} 
		
		IPath path = pathOrNull(resolve(strpath));
		
		if (checkFilesystem) {
			if (path != null && new File(path.toOSString()).exists()) {
				return null;
			}
		}
				
	    IResource res= ResourcesPlugin.getWorkspace().getRoot().findMember(path);
	    if (res != null) {
	        int resType= res.getType();
	        if (resType == IResource.PROJECT || resType == IResource.FOLDER) {
	            IProject proj= res.getProject();
	            if (!proj.isOpen() ) {
	                return "Project for " + name + " is closed [" + path + "]";                    
	            }                               
	        } else {
	            return name + " has to be a folder or project [" + path + "]";
	        }
	    } else {	    		    	
	        return name + " does not exist [" + path + "]";
	    }
	    return null;
	}


}
