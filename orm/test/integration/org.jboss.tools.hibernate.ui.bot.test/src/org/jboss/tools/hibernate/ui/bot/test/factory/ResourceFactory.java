/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.bot.test.factory;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;

/**
 * Provides usefull resource related API for hibernate tests
 * 
 * @author Jiri Peterka
 *
 */
public class ResourceFactory {

	/**
	 * Gets absolute plugin path
	 * 
	 * @param pluginId given plugin id
	 * @return absolute plugin path
	 */
	public static String getAbsolutePluginPath(String pluginId) {
		String path = "";
		URL entry = Platform.getBundle(pluginId).getEntry("/");
		try {
			path = FileLocator.toFileURL(entry).getPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return path;
	}

	/**
	 * Return resources location of given relPath
	 * 
	 * @param pluginId plugin id
	 * @param relPath relative path
	 * @return absolute path to given resouce under resources
	 */
	public static String getResourcesLocation(String pluginId, String relPath) {
		String ret = getAbsolutePluginPath(pluginId) + File.separator
				+ "resources" + File.separator + relPath;
		return ret;
	}

	/**
	 * Return bundle location of given relPath
	 * 
	 * @param pluginId plugin id
	 * @param relPath relative path
	 * @return absolute path to given resource under bundle
	 */
	public static String getBundleLocation(String pluginId, String relPath) {
		String ret = getAbsolutePluginPath(pluginId) + File.separator + relPath;
		return ret;
	}

	/**
	 * Checks if resource exist and if not it downloads it
	 * 
	 * @param pluginId plugin id under resource is checked
	 * @param source source file
	 * @param target target file
	 */
	public static void assureResource(String pluginId, String source,
			String target) {
		URL url = null;
		try {

			url = new URL(source);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			fail("Cannot asure " + source + "resource." + e.toString());
		}

		String absTarget = getBundleLocation(pluginId, target);
		File f = new File(absTarget);
		if (!f.exists()) {
			try {
				downloadFile(url, absTarget);
			} catch (IOException e) {
				e.printStackTrace();
				fail("Cannot download " + source + "resource." + e.toString());
			}
		}

	}

	/**
	 * Download given file
	 * 
	 * @param sourceURL source url
	 * @param target target absolute path
	 * @throws IOException thrown by java IO operations
	 */
	private static void downloadFile(URL sourceURL, String target)
			throws IOException {
		ReadableByteChannel rbc = Channels.newChannel(sourceURL.openStream());
		FileOutputStream fos = new FileOutputStream(target);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		fos.close();
	}
}
