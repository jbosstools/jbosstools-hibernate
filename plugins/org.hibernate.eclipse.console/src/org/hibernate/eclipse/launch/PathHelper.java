package org.hibernate.eclipse.launch;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
// TODO: move to internal.
public class PathHelper {

	public static String getLocationAsStringPath(String path) {
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

	public static Path pathOrNull(String p) {
		if(p==null || p.trim().length()==0) {
			return null;
		} else {
			return new Path(p);
		}
	}

	static public String checkDirectory(IPath path, String name, boolean checkFilesystem) {
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
