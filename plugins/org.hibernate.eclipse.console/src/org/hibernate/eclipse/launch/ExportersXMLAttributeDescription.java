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
package org.hibernate.eclipse.launch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.eclipse.console.HibernateConsolePlugin;

/**
 * Ant Hibernate Exporters tasks attribute description.
 * 
 * @author Vitali Yemialyanchyk
 */
public class ExportersXMLAttributeDescription {

	/**
	 * path to file to store description
	 */
	public static final String ANT_TASKS_DESCRIPTION_PATH = "org/hibernate/eclipse/launch/ant-tasks-description.xml"; //$NON-NLS-1$

	public static class AttributeDescription {
		public String name;
		public String guiName;
		public String defaultValue;
	}
	
	private static Map<String, Map<String, AttributeDescription>> mapExporter2AttributeDescr = null;
	private static Map<String, Set<String>> mapExporter2SetSubTags = null;
	
	private static void initExportersDescriptionmap() {
		if (mapExporter2AttributeDescr != null) {
			return;
		}
		mapExporter2AttributeDescr = new TreeMap<String, Map<String, AttributeDescription>>();
		mapExporter2SetSubTags = new TreeMap<String, Set<String>>();
		Document doc = getDocument();
		if (doc == null) {
			return;
		}
		Element root = doc.getRootElement();
		@SuppressWarnings("rawtypes")
		Iterator itTask = root.elementIterator("task"); //$NON-NLS-1$
		while (itTask.hasNext()) {
			Element elTask = (Element)itTask.next();
			Map<String, AttributeDescription> attributes = new TreeMap<String, AttributeDescription>();
			@SuppressWarnings("rawtypes")
			Iterator itAttribute = elTask.elementIterator("attribute"); //$NON-NLS-1$
			while (itAttribute.hasNext()) {
				Element elAttribute = (Element)itAttribute.next();
				AttributeDescription ad = new AttributeDescription();
				ad.name = elAttribute.attributeValue("name"); //$NON-NLS-1$
				ad.guiName = elAttribute.attributeValue("gui-name"); //$NON-NLS-1$
				ad.defaultValue = elAttribute.attributeValue("default"); //$NON-NLS-1$
				attributes.put(ad.guiName, ad);
			}
			Set<String> subtags = new TreeSet<String>();
			itAttribute = elTask.elementIterator("subtag"); //$NON-NLS-1$
			while (itAttribute.hasNext()) {
				Element elAttribute = (Element)itAttribute.next();
				AttributeDescription ad = new AttributeDescription();
				ad.name = elAttribute.attributeValue("name"); //$NON-NLS-1$
				ad.guiName = elAttribute.attributeValue("gui-name"); //$NON-NLS-1$
				ad.defaultValue = elAttribute.attributeValue("default"); //$NON-NLS-1$
				attributes.put(ad.guiName, ad);
				subtags.add(ad.guiName);
			}
			//String taskId = elTask.attributeValue("id"); //$NON-NLS-1$
			String taskName = elTask.attributeValue("name"); //$NON-NLS-1$
			mapExporter2AttributeDescr.put(taskName, attributes);
			mapExporter2SetSubTags.put(taskName, subtags);
		}
	}
	
	/**
	 * get map with core description
	 * 
	 * @return
	 */
	public static Map<String, Map<String, AttributeDescription>> getExportersDescription() {
		initExportersDescriptionmap();
		Map<String, Map<String, AttributeDescription>> res = 
			new TreeMap<String, Map<String, AttributeDescription>>();
		res.putAll(mapExporter2AttributeDescr);
		return res;
	}

	public static Map<String, Set<String>> getExportersSetSubTags() {
		initExportersDescriptionmap();
		Map<String, Set<String>> res = new TreeMap<String, Set<String>>();
		res.putAll(mapExporter2SetSubTags);
		return res;
	}
	
	private static Document getDocument() {
		InputStream input = getResInputStream(ANT_TASKS_DESCRIPTION_PATH, ExportersXMLAttributeDescription.class);
		if (input == null) {
			HibernateConsolePlugin.getDefault().logErrorMessage("Can't read resource: " + ANT_TASKS_DESCRIPTION_PATH, (Throwable)null); //$NON-NLS-1$
			return null;
		}
		StringBuffer cbuf = new StringBuffer();
		InputStreamReader isReader = null;
		BufferedReader in = null;
		try {
			String ls = System.getProperties().getProperty("line.separator", "\n");  //$NON-NLS-1$//$NON-NLS-2$
			isReader = new InputStreamReader(input);
			in = new BufferedReader(isReader);
			String str;
			while ((str = in.readLine()) != null) {
				cbuf.append(str + ls);
			}
		} catch (IOException e) {
			HibernateConsolePlugin.getDefault().logErrorMessage("IOException: ", e); //$NON-NLS-1$
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// ignore
				}
			}
			if (isReader != null) {
				try {
					isReader.close();
				} catch (IOException e) {
					// ignore
				}
			}
			try {
				input.close();
			} catch (IOException e) {
				// ignore
			}
		}
		Document res = null;
		try {
			res = DocumentHelper.parseText(cbuf.toString());
		} catch (DocumentException e) {
			HibernateConsolePlugin.getDefault().logErrorMessage("DocumentException: ", e); //$NON-NLS-1$
		}
		return res;
	}

	/**
	 * @param resName fully qualified path of the resource
	 * @param clazz the class where the resource will exist
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static InputStream getResInputStream(final String resName, final Class clazz) {
		InputStream input = null;
		if (System.getSecurityManager() == null) {
			input = getResInputStreamInternal(resName, clazz);
		} else {
			input = AccessController.doPrivileged(new PrivilegedAction() {
				public Object run() {
					return getResInputStreamInternal(resName, clazz);
				}
			});
		}
		return input;
	}
	
	@SuppressWarnings("rawtypes")
	static InputStream getResInputStreamInternal(final String resName, Class clazz) {
		ClassLoader loader = clazz.getClassLoader();
		final InputStream input = loader == null ? ClassLoader.getSystemResourceAsStream(resName) : loader.getResourceAsStream(resName);
		return input;
	}
}
