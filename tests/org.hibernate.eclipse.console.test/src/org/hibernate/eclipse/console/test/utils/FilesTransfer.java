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

	public static final FileFilter filterFiles = new FileFilter() {
		public boolean accept(File f) {
			return f.exists() && f.isFile() && !f.isHidden();
		}
	};

	public static final FileFilter filterFolders = new FileFilter() {
		public boolean accept(File f) {
			return f.exists() && f.isDirectory() && !f.isHidden();
		}
	};

	public static final FileFilter filterFilesJar = new FileFilter() {
		public boolean accept(File f) {
			return f.exists() && f.isFile() && !f.isHidden() && f.getName().toLowerCase().endsWith(".jar"); //$NON-NLS-1$
		}
	};

	public static final FileFilter filterFilesJava = new FileFilter() {
		public boolean accept(File f) {
			return f.exists() && f.isFile() && !f.isHidden() && f.getName().toLowerCase().endsWith(".java"); //$NON-NLS-1$
		}
	};

	public static final FileFilter filterFilesXml = new FileFilter() {
		public boolean accept(File f) {
			return f.exists() && f.isFile() && !f.isHidden() && f.getName().toLowerCase().endsWith(".xml"); //$NON-NLS-1$
		}
	};

	public static final FileFilter filterFilesJavaXml = new FileFilter() {
		public boolean accept(File f) {
			return f.exists() && f.isFile() && !f.isHidden() && 
				(f.getName().toLowerCase().endsWith(".java") || f.getName().toLowerCase().endsWith(".xml")); //$NON-NLS-1$ //$NON-NLS-2$
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
			File srcDir = dirs[i];
			IFolder dstDir = dst.getFolder(srcDir.getName());
			try {
				if (!dstDir.exists()) {
					dstDir.create(true, true, null);
				}
				copyFolder(srcDir, dstDir, filterFiles, filterFolders, filesList);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Collect all folders starting from src folder, which contains at least one file
	 * accepted by filterFiles.
	 * @param src
	 * @param filterFiles
	 * @param filterFolders
	 * @param foldersList
	 */
	public static void collectFoldersWithFiles(File src, FileFilter filterFiles, 
			FileFilter filterFolders, List<String> foldersList) {
		File[] files = src.listFiles(filterFiles);
		if (files.length > 0) {
			foldersList.add(src.getPath());
		}
		File[] dirs = src.listFiles(filterFolders);
		for (int i = 0; i < dirs.length; i++) {
			collectFoldersWithFiles(dirs[i], filterFiles, filterFolders, foldersList);
		}
	}

	/**
	 * Delete the whole directory
	 * @param path
	 */
	public static boolean delete(File path) {
		boolean res = true, tmp = true;
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					tmp = delete(files[i]);;
				} else {
					tmp = deleteFile(files[i]);
				}
				res = res && tmp;
			}
		}
		tmp = deleteFile(path);
		res = res && tmp;
		return res;
	}

	/**
	 * Delete single file
	 * @param file
	 */
	public static boolean deleteFile(File file) {
		boolean res = false;
		if (file.exists()) {
			if (file.delete()) {
				res = true;
			}
		}
		return res;
	}
}
