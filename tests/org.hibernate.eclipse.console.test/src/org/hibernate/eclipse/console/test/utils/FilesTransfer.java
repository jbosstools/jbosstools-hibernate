package org.hibernate.eclipse.console.test.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

/**
 * Test utility class to operate with files and folders
 * 
 * @author vitali
 */
public class FilesTransfer {
	
	private FilesTransfer() {}

	public static final String SRC_FOLDER = "src"; //$NON-NLS-1$
	public static final String LIB_FOLDER = "lib"; //$NON-NLS-1$

	public static final FileFilter filterFiles = new FileFilter() {
		public boolean accept(File pathname) {
			return !pathname.isDirectory();
		}
	};

	public static final FileFilter filterFolders = new FileFilter() {
		public boolean accept(File pathname) {
			// exclude ".svn" and other unnecessary folders
			if (pathname.getName().charAt(0) == '.') {
				return false;
			}
			if (LIB_FOLDER.equals(pathname.getName())) {
				return false;
			}
			return pathname.isDirectory();
		}
	};

	public static final FileFilter filterJars = new FileFilter() {
		public boolean accept(File pathname) {
			return !pathname.isDirectory()
					|| pathname.getName().endsWith(".jar"); //$NON-NLS-1$
		}
	};

	/**
	 * Copy whole folder content from source folder to destination folder. 
	 * @param src - source folder
	 * @param dst - destination folder.
	 */
	public static void copyFolder(File src, IFolder dst) {
		copyFolder(src, dst, filterFiles, filterFolders, null);
	}

	/**
	 * Copy whole folder content from source folder to destination folder. 
	 * @param src - source folder
	 * @param dst - destination folder.
	 * @param filterFiles - to filter particular files
	 * @param filterFolders - to filter particular folders
	 * @param filesList - to collect all paths to files which was copied
	 */
	public static void copyFolder(File src, IFolder dst, 
			FileFilter filterFiles, FileFilter filterFolders, List<IPath> filesList) {
		File[] files = src.listFiles(filterFiles);
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (!file.exists()) {
				continue;
			}
			IFile iFile = dst.getFile(file.getName());
			FileInputStream fis = null;
			BufferedInputStream bis = null;
			try {
				if (iFile.exists()) {
					iFile.delete(true, null);
				}
				fis = new FileInputStream(file);
				bis = new BufferedInputStream(fis);
				iFile.create(bis, IResource.FORCE, null);
				if (filesList != null) {
					filesList.add(iFile.getFullPath());
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (CoreException e) {
				e.printStackTrace();
			} finally {
				if (bis != null) {
					try {
						bis.close();
					} catch (IOException e) {}
				}
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {}
				}
			}
		}
		File[] dirs = src.listFiles(filterFolders);
		for (int i = 0; i < dirs.length; i++) {
			File dir = dirs[i];
			if (!dir.exists()) {
				continue;
			}
			IFolder iFolder = dst.getFolder(dir.getName());
			try {
				if (!iFolder.exists()) {
					iFolder.create(true, true, null);
				}
				copyFolder(dir, iFolder, filterFiles, filterFolders, filesList);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}
}
