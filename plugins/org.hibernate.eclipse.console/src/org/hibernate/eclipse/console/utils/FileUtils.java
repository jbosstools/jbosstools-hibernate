/*******************************************************************************
 * Copyright (c) 2007-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.console.utils;

import java.io.File;

/**
 * 
 * @author vitali
 */
public class FileUtils {

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
