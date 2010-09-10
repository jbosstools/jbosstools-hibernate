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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
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
	public static final String ANT_TASKS_DESCRIPTION_PATH = "ant-tasks-description.xml".replaceAll("//", File.separator); //$NON-NLS-1$ //$NON-NLS-2$

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
		File resourceFile = null;
		try {
			resourceFile = getResourceItem(ANT_TASKS_DESCRIPTION_PATH);
		} catch (IOException e) {
			HibernateConsolePlugin.getDefault().logErrorMessage("getResource: ", e); //$NON-NLS-1$
		}
		if (resourceFile == null || !resourceFile.exists()) {
			return null;
		}
		StringBuffer cbuf = new StringBuffer((int) resourceFile.length());
		try {
			String ls = System.getProperties().getProperty("line.separator", "\n");  //$NON-NLS-1$//$NON-NLS-2$
			BufferedReader in = new BufferedReader(new FileReader(resourceFile));
			String str;
			while ((str = in.readLine()) != null) {
				cbuf.append(str + ls);
			}
			in.close();
		} catch (IOException e) {
			HibernateConsolePlugin.getDefault().logErrorMessage("IOException: ", e); //$NON-NLS-1$
		}
		Document res = null;
		try {
			res = DocumentHelper.parseText(cbuf.toString());
		} catch (DocumentException e) {
			HibernateConsolePlugin.getDefault().logErrorMessage("DocumentException: ", e); //$NON-NLS-1$
		}
		return res;
	}
	
	private static File getResourceItem(String strResPath) throws IOException {
		IPath resourcePath = new Path(strResPath);
		File resourceFile = resourcePath.toFile();
		URL entry = HibernateConsolePlugin.getDefault().getBundle().getEntry(
				strResPath);
		if (entry != null) {
			URL resProject = FileLocator.resolve(entry);
			strResPath = FileLocator.resolve(resProject).getFile();
		}
		resourceFile = new File(strResPath);
		return resourceFile;
	}
}
