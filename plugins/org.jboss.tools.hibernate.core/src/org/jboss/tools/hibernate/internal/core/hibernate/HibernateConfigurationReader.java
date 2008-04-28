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
import java.util.Iterator;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.hibernate.core.IMappingStorage;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.exception.NestableRuntimeException;
import org.jboss.tools.hibernate.core.hibernate.ICollectionMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IPropertyMapping;
import org.jboss.tools.hibernate.core.hibernate.IRootClassMapping;
import org.jboss.tools.hibernate.internal.core.BaseResourceReaderWriter;
import org.jboss.tools.hibernate.internal.core.util.ScanProject;



/**
 * @author alex
 *
 * Class parses hibernate configuration file
 */
public class HibernateConfigurationReader extends BaseResourceReaderWriter {
	
	private HibernateConfiguration hc;
	private IProject project;
	
	public HibernateConfigurationReader(HibernateConfiguration hc, IProject project){
		this.hc=hc;
		this.project=project;
	}
	public void read(InputStream input) throws IOException, CoreException{
		try{
			org.dom4j.Document doc = readDocument(input);
			// added by yk 01.10.2005
			hc.clearErrorMarkers();
			// added by yk 01.10.2005.

			parseHibernateConfiguration(doc);
		}
		catch(DocumentException dex){
			throw new NestableRuntimeException(dex);
		}
	}

	
	/**
	 *	by troyas
	 */
	private void parseHibernateConfiguration(org.dom4j.Document doc) throws CoreException, IOException{
		/**
		 * XXX: toAlex can we use dom4j ? Yes you can.
		 */
		Element root = doc.getRootElement();
		Element sfNode = root.element("session-factory");
		if(sfNode != null){

            // changed by Nick 12.08.2005
            String jndiName=sfNode.attributeValue("name");
            if(jndiName!=null && jndiName.length()>0)
                hc.setProperty("hibernate.session_factory_name", jndiName);
            // by Nick
            addProperties(sfNode);
			parseSessionFactory(sfNode);
			
		}
//		Element secNode = root.element("security");
//		if(secNode != null){
//			//XXX: toAlex (Slava) How can I parse and add security?
//			//We have no support for security
//		}
	}
	
	/**
	 *	by troyas
	 */
	private void parseSessionFactory(Element sfNode) throws CoreException, IOException{
		
		// removed by Nick 12.08.2005
//        String jndiName=sfNode.attributeValue("name");
//        if(jndiName!=null && jndiName.length()>0)
//            hc.setProperty("hibernate.session_factory_name", jndiName);
        // by Nick
        Iterator elements = sfNode.elementIterator();
		while(elements.hasNext()){
			Element subElement = ( Element ) elements.next();
			String subElementName = subElement.getName();
			if(!subElementName.equals("property")){
				if(subElementName.equals("mapping")){
					parseMappingElement(subElement);
				}else if(subElementName.equals("collection-cache")){
					parseCollectionCacheElement(subElement);
				}else if(subElementName.equals("listener")){
					parseListenerElement(subElement);
				}else if(subElementName.equals("filter-def")){
					//TODO: toAlex (Slava) What way can I process filter-def from hibernate configuration?
				}else if(subElementName.equals("class-cache")){
					parseClassCacheElement(subElement);
				}
			}
		//	hc.save();
		}
	
	}
	
	private void addProperties(Element sfNode) {
		Iterator iter = sfNode.elementIterator("property");
		while ( iter.hasNext() ) {
			Element node = ( Element ) iter.next();
			String name = node.attributeValue("name");
			String value = node.getText().trim();
			if (!name.startsWith("hibernate.")){
				name = "hibernate."+name;
			}
			hc.setProperty(name, value);
		}	
			//XXX Slava(5) check property name. it should start with hibernate.
			//if it doesn't then add "hibernate." to the name.See Hibernate reference documentation.
	}

	/**
	 * @param subElement
	 */
	private void parseListenerElement(Element subElement) {
		String type = subElement.attributeValue("type",null);
		String classImpl = subElement.attributeValue("class",null);
		hc.addListener(type, classImpl);
	}
	/**
	 * @param subElement
	 */
	private void parseCollectionCacheElement(Element subElement) {
		String regionNode = subElement.attributeValue("region",null);
		String collectionRole = subElement.attributeValue("collection",null);
		String concurrencyStrategy = subElement.attributeValue("usage",null);
		String region = ( regionNode == null ) ? collectionRole : regionNode;
		setCollectionCacheConcurrencyStrategy(collectionRole, concurrencyStrategy, region);
	}
	
	private void setCollectionCacheConcurrencyStrategy(String collectionRole, String concurrencyStrategy, String region){
		int i=collectionRole.lastIndexOf('.');
		if(i==-1)return;
		IHibernateClassMapping cm=(IHibernateClassMapping)hc.getClassMapping(collectionRole.substring(0,i));
		if(cm!=null){
			IPropertyMapping pm=cm.getProperty(collectionRole.substring(i+1));
			if(pm!=null && pm.getValue() instanceof ICollectionMapping){
				ICollectionMapping collectionMapping = (ICollectionMapping)pm.getValue();
				collectionMapping.setCacheRegionName(region);
				collectionMapping.setCacheConcurrencyStrategy(concurrencyStrategy);
			}
		}
		//XXX toAlex: where can I get collectionMapping? And what king of CollectionMapping can I use here?
	}
	
	private void parseClassCacheElement(Element subElement) {
		String className = subElement.attributeValue("class",null);
		String regionNode = subElement.attributeValue("region",null);
		String concurrencyStrategy = subElement.attributeValue("usage",null);
		String region = ( regionNode == null ) ? className : regionNode;
		setCacheConcurrencyStrategy(className, concurrencyStrategy, region);
	}
	
	private void setCacheConcurrencyStrategy(String className, String concurrencyStrategy, String region) {
		IPersistentClassMapping cm=hc.getClassMapping(className);
		if(cm instanceof IRootClassMapping) {
			IRootClassMapping rootClassMapping = (IRootClassMapping) cm;
			rootClassMapping.setCacheConcurrencyStrategy(concurrencyStrategy);
			rootClassMapping.setCacheRegionName(region);
		}
	}
	
	/**
	 * @param subelement
	 * @throws CoreException 
	 * @throws IOException 
	 */
	protected void parseMappingElement(Element subelement) throws CoreException, IOException{
		//XXX (5) Create XMLFileStorage, put it into storages .
		//run XMLFileStorage.reload
		//hc.addMappingStorage();
		String mappingResourceName = subelement.attributeValue("resource");
		if(mappingResourceName==null) mappingResourceName = subelement.attributeValue("file");
		//if(logger.isLoggable(Level.CONFIG))	logger.info("read mapping " + mappingResourceName);
		if(mappingResourceName!=null && !mappingResourceName.trim().equals("")){
			try{
				IFile file = ScanProject.findResource(mappingResourceName, project);
				if(file!=null && file.isAccessible()){
					// added by Nick 16.06.2005
                    XMLFileStorage storage = null;
                    IMappingStorage[] storages = hc.getMappingStorages();

                    for (int i = 0; i < storages.length && storage == null; i++) {
                        IMappingStorage storage1 = storages[i];
                        if (storage1 != null && storage1 instanceof XMLFileStorage)
                        {
                            if (storage1.getResource() == file)
                                storage = (XMLFileStorage) storage1;
                        }
                    }
                    // by Nick
                    if (storage == null)
                        storage = new XMLFileStorage(hc, file);
					// added by Nick 16.06.2005
                    if (storage.resourceChanged())
                        storage.reload();
                    // by Nick
                } else 
                	{
                		//throw new RuntimeException("Resource not found:" +mappingResourceName);
                	throw new Exception();
                	}  
			} catch(Exception ex){
				OrmCore.getPluginLog().logError("Read resource failure: " + mappingResourceName);
			}
		}
	}
}
