/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.internal.core.hibernate;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.DocumentType;
import org.dom4j.Element;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.hibernate.core.IMappingStorage;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.exception.NestableRuntimeException;
import org.jboss.tools.hibernate.internal.core.BaseResourceReaderWriter;
import org.jboss.tools.hibernate.internal.core.util.ScanProject;


/**
 * @author alex
 *
 * Class writes content of Hibernate configuration into a stream
 */
public class HibernateConfigurationWriter extends BaseResourceReaderWriter {
	
	private HibernateConfiguration hc;
	private IFile res;
	
	public HibernateConfigurationWriter(HibernateConfiguration hc,IFile res){
		this.hc = hc;
		this.res = res;
	}
	
	public InputStream write(boolean flagSaveMappingStorages) throws IOException, CoreException {
		InputStream input = null;
		org.dom4j.Document doc =null;
		try {
			try{
				if(res.exists() && res.isLocal(IResource.DEPTH_ZERO)){
					input = res.getContents(true);
					doc = readDocument(input);
				}
			} catch (Exception ex){
				OrmCore.getPluginLog().logError(ex);
			}
			if(doc == null) doc = createDocument();
			mergeHibernateConfiguration(doc, flagSaveMappingStorages);
			return writeDocument(doc);
		} catch (Exception e){
			throw new NestableRuntimeException(e);
		}
		finally{
			if(input != null) input.close();
		}

	}
// added by yk 21.09.2005
	public InputStream writeDefaultConfiguration(boolean flagSaveMappingStorages) throws IOException {
		InputStream input = null;
		try {
            input = res.getContents(true);
            org.dom4j.Document doc = createDocument();
			mergeHibernateConfiguration(doc, flagSaveMappingStorages);
			return writeDocument(doc);
		} catch (Exception e) {
			throw new NestableRuntimeException(e);
		} finally {
			if(input != null) input.close();
		}
	}
// added by yk 21.09.2005.

	private org.dom4j.Document createDocument(){
		org.dom4j.Document doc = DocumentHelper.createDocument();
		Element root = DocumentHelper.createElement("hibernate-configuration");
		doc.setRootElement(root);
		return doc;
	}
	
	
//	private ArrayList elements=new ArrayList();
	
	private void mergeHibernateConfiguration(org.dom4j.Document doc, boolean flagSaveMappingStorages) throws CoreException, IOException{
		Element root = doc.getRootElement();
		Element sfNode = root.element("session-factory");
		if(sfNode == null) sfNode = root.addElement("session-factory");
		
		DocumentType dt = DocumentFactory.getInstance().createDocType(
				"hibernate-configuration", 
				"-//Hibernate/Hibernate Configuration DTD 3.0//EN", 
				"http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd");
		
		doc.setDocType(dt);
		processProperties(sfNode);
		processSessionFactory(sfNode, flagSaveMappingStorages);
		reorderElements(root, "session-factory");
		reorderElements(root, "security");
	}

	private void processProperties(Element sfNode) {
//		 added by yk 19.09.2005
		String[] words = {"property"};
		collectComments(sfNode, words, "name");
// added by yk 19.09.2005.		
		HashSet<String> written = new HashSet<String>();
		Properties props = hc.getProperties();
		Iterator iter = sfNode.elementIterator("property");
		while (iter.hasNext()) {
			Element node = (Element) iter.next();
			String name = node.attributeValue("name");
			if (!name.startsWith("hibernate.")){
				name = "hibernate."+name;
			}
			String value = props.getProperty(name);
			if(value == null ){
				sfNode.remove(node);
			} else {
				//if(!value.equals("hibernate.session_factory_name"))
				node.setText(value);
			}
			written.add(name);
		}	
		iter = props.keySet().iterator();
		while(iter.hasNext()){
			String name = (String)iter.next();
			if(!written.contains(name) && !name.equals("hibernate.session_factory_name")){// added gavr 8/11/2005
				Element node = sfNode.addElement("property");
				//TODO write short names without hibernate.
				node.addAttribute("name", name);
				node.setText(props.getProperty(name));
			}
		}
		
// added by yk 11.07.2005
		iter = sfNode.elementIterator("property");
		Hashtable<String,String> ht = new Hashtable<String,String>();
		while(iter.hasNext())
		{// remove all properties of the node.
			Element elem = (Element)iter.next();
			ht.put(elem.attributeValue("name"), elem.getText());
			sfNode.remove(elem);
		}
		addAttributesToNode(sfNode, ht.keySet().toArray(), true, ht);
// added by yk 11.07.2005 stop
		reorderElements(sfNode, "property");		
	}
	
	private void processSessionFactory(Element sfNode, boolean flagSaveMappingStorages) throws CoreException,IOException {
		String jndiName = hc.getProperty("hibernate.session_factory_name");
		mergeAttribute(sfNode, "name", jndiName);
		processMappings(sfNode, flagSaveMappingStorages);
		processClassCaches(sfNode);
		processCollectionCaches(sfNode);
		processListeners(sfNode);
/* rem by yk 19.09.2005
		reorderElements(sfNode, "property");
		reorderElements(sfNode, "mapping");
		reorderElements(sfNode, "class-cache");
		reorderElements(sfNode, "collection-cache");
		reorderElements(sfNode, "listener");
*/		
	}
	
	// #changed# by Konstantin Mishin on 2005/08/26 fixed for ORMIISTUD-681
	//private void processMappings(Element sfNode) throws CoreException, IOException {
	private void processMappings(Element sfNode, boolean flagSaveMappingStorages) {
	// #changed#
// added by yk 19.09.2005
		String[] words = {"mapping"};
		collectComments(sfNode, words, null);
// added by yk 19.09.2005.
		
		Iterator iter = sfNode.elementIterator("mapping");
		while ( iter.hasNext() ) {
			Element node = (Element) iter.next();
			String resourceName = node.attributeValue("resource");
			if(resourceName == null) resourceName = node.attributeValue("file");
			if(resourceName != null){
					sfNode.remove(node);
			}
		}
		IMappingStorage[] stgs = hc.getMappingStorages();
		ArrayList<String> attributes = new ArrayList<String>();
		for(int i = 0; i < stgs.length; ++i) {
			// #added# by Konstantin Mishin on 2005/08/26 fixed for ORMIISTUD-681
			try {
			// #added#
				
				// edit tau 14.02.2006 - add flagSaveMappingStorage
				// ESORM-513 Overwrites and changes our Hibernate mapping files, even if they are marked read-only?
				if (flagSaveMappingStorages) {
					stgs[i].save();					
				}

				
				if(stgs[i].getPersistentClassMappings().length > 0 || stgs[i].getFilterDefSize() > 0)
				{// pick up attributes
					attributes.add(ScanProject.getRelativePath(stgs[i].getResource()));		
				}
			// #added# by Konstantin Mishin on 2005/08/26 fixed for ORMIISTUD-681
			} catch (Exception e){
				OrmCore.getPluginLog().logError(e);
			}
			// #added#
		}
		
		// add picked up attributes to parent node.
		addAttributesToNode(sfNode, attributes.toArray(), false, null);
// added by yk 19.09.2005
		reorderElements(sfNode, "mapping");
// added by yk 19.09.2005.
	}
	
	private void addAttributesToNode(Element parent_node, Object[] attrib_array,
									 boolean isproperty, Hashtable properties) {
		Arrays.sort(attrib_array,getResourcesNameComparator());
		for(int i = 0; i < attrib_array.length; i++) {
			Element node = parent_node.addElement(isproperty ? "property" : "mapping");
			String res_path = (String)attrib_array[i];
			node.addAttribute(isproperty ? "name" : "resource", res_path);
			if(isproperty) {
				if(properties != null && properties.size() > 0)
				node.setText((String)properties.get(res_path));
			}
		}
	}
	
	private Comparator<Object> getResourcesNameComparator() {
		Comparator<Object> comparator = new Comparator<Object>() {
			public int compare(Object arg0, Object arg1) {
				String res1_name = (String)arg0;
				String res2_name = (String)arg1;
				if(res1_name == null)	return 0;
				return res1_name.compareTo(res2_name);
			}
		};
		return comparator;
	}
	
	private void processClassCaches(Element sfNode) {
//		 added by yk 19.09.2005
		String[] words = {"class-cache"};
		collectComments(sfNode, words, null);
// added by yk 19.09.2005.				
		Iterator iter = sfNode.elementIterator("class-cache");
		while ( iter.hasNext() ) {
			Element node = ( Element ) iter.next();
			sfNode.remove(node);
			//Class caches will be written in hbm files
		}
		reorderElements(sfNode, "class-cache");
	}
	
	private void processCollectionCaches(Element sfNode) {
//		 added by yk 19.09.2005
		String[] words = {"collection-cache"};
		collectComments(sfNode, words, null);
// added by yk 19.09.2005.				
		Iterator iter = sfNode.elementIterator("collection-cache");
		while ( iter.hasNext() ) {
			Element node = ( Element ) iter.next();
			sfNode.remove(node);
			//Collection caches will be written in hbm files
		}
		reorderElements(sfNode, "collection-cache");
	}
	
	private void processListeners(Element sfNode) {
//		 added by yk 19.09.2005
		String[] words = {"listener"};
		collectComments(sfNode, words, "class");
// added by yk 19.09.2005.		
		HashSet<String> written = new HashSet<String>();
		Iterator iter = sfNode.elementIterator("listener");
		Map listeners = hc.getListeners();
		while ( iter.hasNext() ) {
			Element node = ( Element ) iter.next();
			String type = node.attributeValue("type");
			if(listeners.get(type) == null) sfNode.remove(node);
			else node.addAttribute("class", (String)listeners.get(type));
			written.add(type);
		}
		iter = listeners.keySet().iterator();
		while(iter.hasNext()){
			String type = (String)iter.next();
			if(!written.contains(type))
				sfNode.addAttribute(type,(String)listeners.get(type));
		}
// added by yk 19.09.2005
		reorderElements(sfNode, "listener");
// added by yk 19.09.2005.
	}
}
