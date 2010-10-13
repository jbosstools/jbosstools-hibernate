/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.console.test.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.hibernate.console.ConfigurationXMLFactory;
import org.hibernate.eclipse.console.test.HibernateConsoleTestPlugin;

/**
 * @author Vitali Yemialyanchyk
 */
public class ResourceReadUtils {

	public static final String LN_1 = "\n"; //$NON-NLS-1$
	public static final String LN_2 = "\r\n"; //$NON-NLS-1$
	
	/**
	 * Parse, i.e. adjust xml text so attributes for same xmls 
	 * will be in one order.
	 * 
	 * @param sample
	 * @return adjusted xml
	 */
	public static String adjustXmlText(String sample) {
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(sample);
		} catch (DocumentException e) {
			// ignore
		}
		if (doc == null) {
			return sample;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ConfigurationXMLFactory.dump(baos, doc.getRootElement());
		return baos.toString().trim();
	}
	
	public static String getSample(String fileName) {
		File resourceFile = null;
		try {
			resourceFile = getResourceItem(fileName);
		} catch (IOException e1) {
		}
		if (resourceFile == null || !resourceFile.exists()) {
			return null;
		}
		StringBuffer cbuf = new StringBuffer((int) resourceFile.length());
		try {
			String ls = System.getProperties().getProperty("line.separator", LN_1);  //$NON-NLS-1$
			BufferedReader in = new BufferedReader(new FileReader(resourceFile));
			String str;
			while ((str = in.readLine()) != null) {
				cbuf.append(str + ls);
			}
			in.close();
		} catch (IOException e) {
		}
		return adjustXmlText(cbuf.toString());
	}

	public static File getResourceItem(String strResPath) throws IOException {
		IPath resourcePath = new Path(strResPath);
		File resourceFolder = resourcePath.toFile();
		URL entry = HibernateConsoleTestPlugin.getDefault().getBundle().getEntry(
				strResPath);
		String tplPrjLcStr = strResPath;
		if (entry != null) {
			URL resProject = FileLocator.resolve(entry);
			tplPrjLcStr = FileLocator.resolve(resProject).getFile();
		}
		resourceFolder = new File(tplPrjLcStr);
		return resourceFolder;
	}

    public static String readStream(InputStream is) {
        StringBuffer sb = new StringBuffer(""); //$NON-NLS-1$
        try {
            byte[] b = new byte[4096];
            while (true) {
                int l = is.read(b, 0, b.length);
                if (l < 0) {
                	break;
                }
                sb.append(new String(b, 0, l));
            }
            is.close();
        } catch (IOException e) {
        	e.printStackTrace();
        }
        return sb.toString();
    }
}

