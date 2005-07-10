package org.hibernate.eclipse.console;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Viewer filter for file selection dialogs.
 * Client provides a list of file extensions and a list of excluded files (possibly empty). 
 * The filter is not case sensitive.
 * 
 * (Basically a generic version of ArchiveFileFilter)
 */
public class FileFilter extends ViewerFilter {

	private final String[] fileExtensions;

	private List excludedFiles;
	private boolean recursive ;

	private final boolean allowDirectories;
	
	/**
	 * @param excludedFiles Excluded files will not pass the filter.
	 * <code>null</code> is allowed if no files should be excluded. 
	 * @param recusive Folders are only shown if, searched recursively, contain
	 * a matching file
	 */
	public FileFilter(String[] fileExtensions, IFile[] excludedFiles, boolean recusive) {
		this.fileExtensions = fileExtensions;
		if (excludedFiles != null) {
			this.excludedFiles= Arrays.asList(excludedFiles);
		} else {
			this.excludedFiles= null;
		}
		recursive = recusive;
		allowDirectories = false;
	}
	
	public FileFilter(String[] fileExtensions, List excludedFiles, boolean recusive, boolean allowDirectories) {
		
		this.fileExtensions = fileExtensions;
		this.excludedFiles= excludedFiles;
		recursive = recusive;
		this.allowDirectories = allowDirectories;		
	}
	
	/*
	 * @see ViewerFilter#select
	 */
	public boolean select(Viewer viewer, Object parent, Object element) {
		if ( (element instanceof IFile) ) {
			if (this.excludedFiles != null && this.excludedFiles.contains(element) ) {
				return false;
			}
			return isFileExtension( ( (IFile)element).getFullPath() );
		} 
		else if (allowDirectories && element instanceof IFolder) {
			return true;
		} else if (element instanceof IContainer) { // IProject, IFolder
			if (!recursive ) {
				return true;
			}
			try {
				IResource[] resources= ( (IContainer)element).members();
				for (int i= 0; i < resources.length; i++) {
					// recursive! Only show containers that contain a matching file
					if (select(viewer, parent, resources[i]) ) {
						return true;
					}
				}
			} catch (CoreException e) {
				HibernateConsolePlugin.getDefault().log(e.getStatus() );
			}				
		}
		return false;
	}
	
	public boolean isFileExtension(IPath path) {
		for (int i= 0; i < fileExtensions.length; i++) {
			if (path.lastSegment().endsWith(fileExtensions[i]) ) {
				return true;
			}
		}
		return false;
	}
			
	
			
}
