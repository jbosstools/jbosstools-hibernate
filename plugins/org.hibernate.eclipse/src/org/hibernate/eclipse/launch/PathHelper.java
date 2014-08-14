package org.hibernate.eclipse.launch;

import java.io.File;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.osgi.util.NLS;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
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

		IResource findMember = root.findMember(pathOrNull);
		if(findMember==null) {
			IContainer[] findContainersForLocation = root.findContainersForLocation(pathOrNull);
			if(findContainersForLocation.length>0) {
				findMember = findContainersForLocation[0];
			}
		}
		return findMember;
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
				String out = NLS.bind(HibernateConsoleMessages.PathHelper_has_invalid_variable_references, name, e.getMessage());
				return out;
			}
		}

		IPath path = pathOrNull(resolve(strpath));

		if (checkFilesystem && path != null) {
			File file = new File(path.toOSString());
			if (file.exists()) {
				if (file.isDirectory()) {
					return null;
				}
				String out = NLS.bind(HibernateConsoleMessages.PathHelper_not_directory, path);
				return out;
			}
		}

	    IResource res= ResourcesPlugin.getWorkspace().getRoot().findMember(path);
	    if (res != null) {
	        int resType= res.getType();
	        if (resType == IResource.PROJECT || resType == IResource.FOLDER) {
	            IProject proj= res.getProject();
	            if (!proj.isOpen() ) {
	            	String out = NLS.bind(HibernateConsoleMessages.PathHelper_project_for_is_closed, name, path);
	                return out;
	            }
	        } else {
	        	String out = NLS.bind(HibernateConsoleMessages.PathHelper_has_to_be_folder_or_project, name, path);
	            return out;
	        }
	    } else {
        	String out = NLS.bind(HibernateConsoleMessages.PathHelper_does_not_exist, name, path);
	        return out;
	    }
	    return null;
	}


	/**
	 * Checks if file exists.
	 * Handles variables replacement too.
	 *
	 * @param strpath
	 * @param name
	 * @param checkFilesystem
	 * @return
	 */
	static public String checkFile(String strpath, String name, boolean checkFilesystem) {
		if(strpath.indexOf("${") >= 0) { //$NON-NLS-1$
			IStringVariableManager manager = VariablesPlugin.getDefault().getStringVariableManager();
			try {
				manager.validateStringVariables(strpath);
			}
			catch (CoreException e) {
				String out = NLS.bind(HibernateConsoleMessages.PathHelper_has_invalid_variable_references, name, e.getMessage());
				return out;
			}
		}

		IPath path = pathOrNull(resolve(strpath));

		if (checkFilesystem && path != null) {
			File file = new File(path.toOSString());
			if (file.exists()) {
				if (file.isFile()) {
					return null;
				}
				String out = NLS.bind(HibernateConsoleMessages.PathHelper_not_file, path);
				return out;
			}
		}

	    IResource res= ResourcesPlugin.getWorkspace().getRoot().findMember(path);
	    if (res != null) {
	        int resType= res.getType();
	        if (resType == IResource.FILE) {
	            IProject proj= res.getProject();
	            if (!proj.isOpen() ) {
	            	String out = NLS.bind(HibernateConsoleMessages.PathHelper_project_for_is_closed, name, path);
	                return out;
	            }
	        } else {
	        	String out = NLS.bind(HibernateConsoleMessages.PathHelper_has_to_be_file, name, path);
	            return out;
	        }
	    } else {
        	String out = NLS.bind(HibernateConsoleMessages.PathHelper_does_not_exist, name, path);
	        return out;
	    }
	    return null;
	}
}
