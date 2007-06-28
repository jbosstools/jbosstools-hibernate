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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.dom4j.Attribute;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.DocumentType;
import org.dom4j.Element;
import org.dom4j.Node;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.exception.NestableRuntimeException;
import org.jboss.tools.hibernate.internal.core.BaseResourceReaderWriter;
import org.jboss.tools.hibernate.internal.core.hibernate.query.NamedQueryDefinition;




/**
 * @author troyas
 *
 */
public class XMLFileWriter extends BaseResourceReaderWriter {
	
//	private HibernateConfiguration config;
	private XMLFileStorage storage;
	private IFile resource;
	private XMLFileWriterVisitor visitor;
	
	public XMLFileWriter(XMLFileStorage storage, IFile resource, HibernateConfiguration config) {
		this.resource = resource;
		this.storage = storage;
//		this.config = config;
		visitor= new XMLFileWriterVisitor(storage, config);
	}
	
	public InputStream write() throws IOException, CoreException {
		InputStream input = null;
		org.dom4j.Document doc =null;
		try {
			try{
				// added by yk 04.11.2005
				resource.refreshLocal(IResource.DEPTH_ZERO, null);
				// added by yk 04.11.2005.
				if(resource.exists() && resource.isLocal(IResource.DEPTH_ZERO)){
					input = resource.getContents(true);
					doc = readDocument(input);
				}
			} catch (Exception ex){
				OrmCore.getPluginLog().logError(ex);
			}
			if(doc==null) doc = createDocument();
			mergeMapping(doc);
			return writeDocument(doc);
		} catch (Exception e){
			throw new NestableRuntimeException("Unable to write into "+storage.getName() + "    " + e.getMessage(), e);
		}
		finally{
			if(input!=null) input.close();
		}
	}
	
	private org.dom4j.Document createDocument(){
		org.dom4j.Document doc= DocumentHelper.createDocument();
		Element root=DocumentHelper.createElement("hibernate-mapping");
		doc.setRootElement(root);
		return doc;
	}

	private void mergeMapping(org.dom4j.Document doc) throws CoreException, IOException{
		Element root = doc.getRootElement();
		if(root==null) root = doc.addElement("hibernate-mapping");
		DocumentType dt=DocumentFactory.getInstance().createDocType("hibernate-mapping", "-//Hibernate/Hibernate Mapping DTD 3.0//EN", "http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd");
		doc.setDocType(dt);

		//processMetas(root);
		reorderElements(root,"meta");
		processTypeDefs(root);
		reorderElements(root,"typedef");
		//processImports(root);
		reorderElements(root,"import");
		processClasses(root);
		reorderElements(root,"class");
		reorderElements(root,"subclass");
		reorderElements(root,"joined-subclass");
		reorderElements(root,"union-subclass");
		processQueries(root);
		reorderElements(root,"query");
		//handleSqlQueres(root, storage);	
		reorderElements(root,"sql-query");
		//handleFilterDefs(root, storage);
		reorderElements(root,"filter-def");
		
		//processMapping should be last one
		processMapping(root);
		
	}

//	private ArrayList elements=new ArrayList();
	
	private void processMapping(Element root) {
		
		//String defSchema=config.getProperty("hibernate.default_schema");
		mergeAttribute(root, "schema", storage.getSchemaName());
		//String defCat=config.getProperty("hibernate.default_catalog");
		mergeAttribute(root, "catalog", storage.getCatalogName());
		mergeAttribute(root, "package", storage.getDefaultPackage());
		mergeAttribute(root, "default-cascade", "none".equals(storage.getDefaultCascade()) ? null : storage.getDefaultCascade());
		mergeAttribute(root, "default-access", "property".equals(storage.getDefaultAccess()) ? null : storage.getDefaultAccess());
		mergeAttribute(root, "default-lazy", storage.isDefaultLazy() ? null : "false");
		mergeAttribute(root, "auto-import", storage.isAutoImport() ? null : "false");
		
	}
	
	private void collectClasses(Element root, Element parentElement, Map<String,Node> classElements){
		Iterator nodes = parentElement.elementIterator( );
		while ( nodes.hasNext() ) {
			Element classNode = (Element)nodes.next();
			String className=null;
			IPersistentClassMapping classMapping=null;
			String nodeName = classNode.getName();
			if("class".equals(nodeName) || 
					"subclass".equals(nodeName) || 
					"joined-subclass".equals(nodeName) || 
					"union-subclass".equals(nodeName)){
				collectClasses(root, classNode, classElements);
				className = getFullyQualifiedClassName(classNode.attributeValue("name", null), root);
				if(className!=null) classMapping = storage.getPersistentClassMapping(className);
				if(classMapping!=null){
					classElements.put(className, classNode.detach());
				} else parentElement.remove(classNode);
			}
		}
		
	}
	
	private void processClasses(Element root) {
		
		HashMap<String,Node> classElements = new HashMap<String,Node>();
		collectClasses(root, root, classElements);
		IPersistentClassMapping cms[] = storage.getPersistentClassMappings();
		HashSet<String> written = new HashSet<String>();
		for(int i = 0; i < cms.length; ++i) {
			processClassMapping(root, cms[i],written, classElements);
		}
	}
	
	private void processClassMapping(Element root, IPersistentClassMapping cm, HashSet<String> written, HashMap classElements ){
		if(cm.getSuperclassMapping()!=null) processClassMapping(root, cm.getSuperclassMapping(), written, classElements);
		if(cm.getStorage()!=storage) return;//skip mappings from other storages
		if(written.contains(cm.getName())) return;
		Element classNode= (Element)classElements.get(cm.getName());
		if(classNode==null) classNode= root.addElement("class");
		else root.add(classNode);
		written.add(cm.getName());
		cm.accept(visitor, classNode);
	}
	
	
	private String getFullyQualifiedClassName(String unqualifiedName, Element root) {
		if ( unqualifiedName == null ) return null;
		String defaultPackage = null;
		Attribute packNode = root.attribute("package");
		if(packNode!=null) defaultPackage = packNode.getValue();
		if(unqualifiedName.indexOf('.') < 0 && defaultPackage!=null && defaultPackage.length() >0 ){
			return defaultPackage+"."+unqualifiedName;
		}
		return unqualifiedName;
	}
	private void processQueries(Element node) {
		Iterator queryNodes = node.elementIterator("query");
		HashSet<String> written = new HashSet<String>();
		while(queryNodes.hasNext()){
			Element subnode = (Element) queryNodes.next();
			
// added by yk 13.07.2005
			subnode.clearContent();
// added by yk 13.07.2005 stop
			
			String qname = subnode.attributeValue( "name" );
			
			NamedQueryDefinition namedQuery = storage.getQuery(qname);
			if(namedQuery!=null){
				
				mergeAttribute(subnode, "cacheable", namedQuery.isCacheable() ? "true" : null);
				mergeAttribute(subnode, "cache-region", namedQuery.getCacheRegion());
				mergeAttribute(subnode, "timeout", namedQuery.getTimeout()==null?null:String.valueOf(namedQuery.getTimeout()));
				mergeAttribute(subnode, "fetch-size", namedQuery.getFetchSize()==null?null:String.valueOf(namedQuery.getFetchSize()));
				mergeAttribute(subnode, "flush-mode", namedQuery.getFlushMode());
				
			/* rem by yk 13.07.2005	subnode.setText(namedQuery.getQueryString()); */
				if(namedQuery.getQueryString().length() > 0)
				{
					subnode.addCDATA(namedQuery.getQueryString());
				}
				
				written.add(qname);
			}else{
				node.remove(subnode);
			}
		}
		Iterator it=storage.getQueries().keySet().iterator();
		while(it.hasNext()){
			String name = (String)it.next();
			if(!written.contains(name)) {
				NamedQueryDefinition namedQuery = storage.getQuery(name);
				Element subnode= node.addElement("query");
// added by yk 13.07.2005
				subnode.clearContent();
//	 added by yk 13.07.2005 stop				
				mergeAttribute(subnode, "name", name);
				mergeAttribute(subnode, "cacheable", namedQuery.isCacheable() ? "true" : null);
				mergeAttribute(subnode, "cache-region", namedQuery.getCacheRegion());
				mergeAttribute(subnode, "timeout", namedQuery.getTimeout()==null?null:String.valueOf(namedQuery.getTimeout()));
				mergeAttribute(subnode, "fetch-size", namedQuery.getFetchSize()==null?null:String.valueOf(namedQuery.getFetchSize()));
				mergeAttribute(subnode, "flush-mode", namedQuery.getFlushMode());
				/* rem by yk 13.07.2005subnode.setText(namedQuery.getQueryString()); */
				if(namedQuery.getQueryString().length() > 0)
				{
					subnode.addCDATA(namedQuery.getQueryString());
				}
			}
		}
	}

	private void processTypeDefs(Element root) {
		Iterator typeDefs = root.elementIterator("typedef");
		HashSet<String> written = new HashSet<String>();
		while(typeDefs.hasNext()){
			Element typeDefElem = (Element)typeDefs.next();
			String typeName = typeDefElem.attributeValue("name");
			TypeDef typeDef = storage.getTypeDef(typeName);
			if(typeDef != null) {
				processTypeDef(typeDefElem, typeName, typeDef);
			} else {
				root.remove(typeDefElem);
			}
			written.add(typeName);
		}	
		Iterator it = storage.getTypeDefs().keySet().iterator();
		while(it.hasNext()){
			String typeName = (String) it.next();
			if(!written.contains(typeName)) {
				Element typeDefElem = root.addElement("typedef");
				TypeDef typeDef = storage.getTypeDef(typeName);
				processTypeDef(typeDefElem, typeName, typeDef);
			}
		}
	}

	private void processTypeDef(Element typeDefElem, String typeName, TypeDef typeDef) {
		mergeAttribute(typeDefElem, "name", typeName);
		mergeAttribute(typeDefElem, "class", typeDef.getTypeClass());
		
		Iterator paramIter = typeDefElem.elementIterator("param");
		HashSet<String> written = new HashSet<String>();
		Properties parameters = typeDef.getParameters();
		while(paramIter.hasNext()){
			Element param = (Element)paramIter.next();
			String name = param.attributeValue("name");
			String value=parameters.getProperty(name);
			if(value != null){
				param.setText(value);
			}else{
				typeDefElem.remove(param);
			}
			written.add(name);
		}
		Iterator it = parameters.keySet().iterator();
		while(it.hasNext()){
			String name= (String) it.next();
			if(!written.contains(name)){
				Element param = typeDefElem.addElement("param");
				String value=parameters.getProperty(name);
				param.addAttribute("name", name);
				param.setText(value);
			}
		}
	}
}
