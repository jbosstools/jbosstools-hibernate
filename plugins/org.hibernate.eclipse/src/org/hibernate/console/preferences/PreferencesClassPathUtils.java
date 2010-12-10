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
package org.hibernate.console.preferences;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.hibernate.console.ConnectionProfileUtil;

/**
 * Utils class to extract classpath urls from ConsoleConfigurationPreferences
 */
public class PreferencesClassPathUtils {
	/*
	 * get custom classpath URLs 
	 */
	public static URL[] getCustomClassPathURLs(ConsoleConfigurationPreferences prefs) {
		URL[] customClassPathURLsTmp = prefs == null ? new URL[0] :
			prefs.getCustomClassPathURLS();
		URL[] customClassPathURLs = null;
		String[] driverURLParts = prefs == null ? null :
			ConnectionProfileUtil.getConnectionProfileDriverURL(prefs.getConnectionProfileName());
		URL[] urls = null;
		if (driverURLParts != null) {
			urls = new URL[driverURLParts.length];
			for (int i = 0; i < driverURLParts.length; i++) {
				File file = new File(driverURLParts[i].trim());
				try {
					urls[i] = file.toURI().toURL();
				} catch (MalformedURLException e) {
					urls[i] = null; 
				}
			}
		}
		// should DTP connection profile driver jar file be inserted
		int insertItems = ( urls != null ) ? urls.length : 0;
		if (insertItems > 0) {
			insertItems = 0;
			for (int i = 0; i < urls.length; i++) {
				if (urls[i] == null) {
					continue;
				}
				int j = 0;
				for (; j < customClassPathURLsTmp.length; j++) {
					if (customClassPathURLsTmp[j].equals(urls[i])) {
						break;
					}
				}
				if (j == customClassPathURLsTmp.length) {
					urls[insertItems++] = urls[i];
				}
			}
		}
		if (insertItems > 0) {
			customClassPathURLs = new URL[customClassPathURLsTmp.length + insertItems];
	        System.arraycopy(customClassPathURLsTmp, 0, 
	        		customClassPathURLs, 0, customClassPathURLsTmp.length);
	        // insert DTP connection profile driver jar file URL after the default classpath entries
			for (int i = 0; i < insertItems; i++) {
				customClassPathURLs[customClassPathURLsTmp.length + i] = urls[i];
			}
		} else {
			customClassPathURLs = customClassPathURLsTmp;
		}
		return customClassPathURLs;
	}
}
