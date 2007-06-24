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
package org.jboss.tools.hibernate.internal.core;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.DocumentType;
import org.dom4j.Element;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.internal.core.hibernate.HibernateConfiguration;
import org.jboss.tools.hibernate.internal.core.util.ScanProject;


/**
 * @author Konstantin Mishin
 *
 */
public class ContextFileWriter extends BaseResourceReaderWriter {

	private HibernateConfiguration hibernateConfiguration;
	private IFile res;
	IPersistentClass[] classes;
	String packageName;
	boolean generateInterfaces;
	Document doc;

	public ContextFileWriter(HibernateConfiguration hc,IFile res, IPersistentClass[] c, String pn, boolean gi){
		hibernateConfiguration=hc;
		this.res=res;
		classes = c;
		packageName = pn;
		generateInterfaces = gi;
	}

	public void write() {
		if(res.exists())
			try {
				doc = readDocument(res.getContents(true));
			} catch (DocumentException e) {
				ExceptionHandler.logThrowableError(e,null);
			} catch (CoreException e) {
				ExceptionHandler.logThrowableError(e,null);
			}
			if(doc == null)
				doc = createDocument();
			merge();
			try {
				if(res.exists())
					res.setContents(writeDocument(doc), IResource.NONE, null);
				else
					res.create(writeDocument(doc), IResource.NONE, null);				
			} catch (UnsupportedEncodingException e) {
				ExceptionHandler.logThrowableError(e,null);
			} catch (CoreException e) {
				ExceptionHandler.logThrowableError(e,null);
			} catch (IOException e) {
				ExceptionHandler.logThrowableError(e,null);
			}
	}

	private Document createDocument(){
		Document doc= DocumentHelper.createDocument();
		Element root=DocumentHelper.createElement("beans");
		doc.setRootElement(root);
		return doc;
	}

	private void merge(){
		DocumentType dt=DocumentFactory.getInstance().createDocType(
				"beans", 
				"-//SPRING//DTD BEAN//EN", 
		"http://www.springframework.org/dtd/spring-beans.dtd");
		doc.setDocType(dt);
		String id;
		Element root = doc.getRootElement();
		Element element;
		id = (String)hibernateConfiguration.getPropertyValue("spring.dataSource");
		if(!"".equals(id)) {
			element = elementByAttribute(root, "bean", "id", id);
			mergeDataSource(element);
		}
		id = (String)hibernateConfiguration.getPropertyValue("spring.sessionFactory");
		if(!"".equals(id)) {
			element = elementByAttribute(root, "bean", "id", id);
			mergeSessionFactory(element);
		}
		id = (String)hibernateConfiguration.getPropertyValue("spring.hibernateInterceptor");
		if(!"".equals(id)) {
			element = elementByAttribute(root, "bean", "id", id);
			mergeHibernateInterceptor(element);
		}
		id = (String)hibernateConfiguration.getPropertyValue("spring.daoTransactionInterceptor");
		if(!"".equals(id)) {
			element = elementByAttribute(root, "bean", "id", id);
			mergeDaoTransactionInterceptor(element);
		}
		id = (String)hibernateConfiguration.getPropertyValue("spring.daoTxAttributes");
		if(!"".equals(id)) {
			element = elementByAttribute(root, "bean", "id", id);
			mergeDaoTxAttributes(element);
		}
		String name;
		for (int i = 0; i < classes.length; i++) {
			name = classes[i].getShortName();
			element = elementByAttribute(root, "bean", "id", name+"DAOTarget");
			mergeDaoTarget(element, name);
			element = elementByAttribute(root, "bean", "id", name+"DAO");
			mergeDao(element, name);
		}
	}

	private void mergeDataSource(Element element){
		element.addAttribute("class", "org.springframework.jdbc.datasource.DriverManagerDataSource");
		addPropertyWithValue(element, "driverClassName", (String)hibernateConfiguration.getPropertyValue("hibernate.connection.driver_class"));
		addPropertyWithValue(element, "url", (String)hibernateConfiguration.getPropertyValue("hibernate.connection.url"));
		addPropertyWithValue(element, "username", (String)hibernateConfiguration.getPropertyValue("hibernate.connection.username"));
		addPropertyWithValue(element, "password", (String)hibernateConfiguration.getPropertyValue("hibernate.connection.password"));
	}

	private void mergeSessionFactory(Element element){
		element.addAttribute("class", "org.springframework.orm.hibernate3.LocalSessionFactoryBean");
		ArrayList<String> attributes = new ArrayList<String>();
		for (int i = 0; i < classes.length; i++) {
			try {
				attributes.add(ScanProject.getRelativePath(classes[i].getPersistentClassMappingStorage().getResource()));
			} catch (CoreException e) {
				ExceptionHandler.logThrowableWarning(e, null);
			}					
		}
		addPropertyWithList(element, "mappingResources", attributes);
		HashMap<String,String> hashMap = new HashMap<String,String>();
		hashMap.put("hibernate.dialect", OrmConfiguration.dialects[((Integer)hibernateConfiguration.getPropertyValue("hibernate.dialect")).intValue()]);
		addPropertyWithProps(element, "hibernateProperties", hashMap);
		String ref = (String)hibernateConfiguration.getPropertyValue("spring.dataSource");
		if(!"".equals(ref))
			addPropertyWithRef(element, "dataSource", ref);
	}

	private void mergeHibernateInterceptor(Element element){
		element.addAttribute("class", "org.springframework.orm.hibernate3.HibernateInterceptor");
		String ref = (String)hibernateConfiguration.getPropertyValue("spring.sessionFactory");
		if(!"".equals(ref))
			addPropertyWithRef(element, "sessionFactory", ref);
	}

	private void mergeDaoTransactionInterceptor(Element element){
		element.addAttribute("class", "org.springframework.transaction.interceptor.TransactionInterceptor");
		String ref = (String)hibernateConfiguration.getPropertyValue("spring.transactionManager");
		if(!"".equals(ref))
			addPropertyWithRef(element, "transactionManager", ref);
		ref = (String)hibernateConfiguration.getPropertyValue("spring.daoTxAttributes");
		if(!"".equals(ref))
			addPropertyWithRef(element, "transactionAttributeSource", ref);
	}

	private void mergeDaoTxAttributes(Element element){
		element.addAttribute("class", "org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource");
		HashMap<String,String> hashMap = new HashMap<String,String>();
		hashMap.put("find*", "PROPAGATION_SUPPORTS");
		hashMap.put("get*", "PROPAGATION_SUPPORTS");
		hashMap.put("iterate*", "PROPAGATION_SUPPORTS");
		hashMap.put("insert", "PROPAGATION_REQUIRED");
		hashMap.put("delete", "PROPAGATION_REQUIRED");
		hashMap.put("update", "PROPAGATION_REQUIRED");
		hashMap.put("merge", "PROPAGATION_REQUIRED");
		addPropertyWithProps(element, "properties", hashMap);
	}

	private void mergeDaoTarget(Element element, String className){
		element.addAttribute("class", packageName+"."+className+"DAOImpl");
		String ref = (String)hibernateConfiguration.getPropertyValue("spring.sessionFactory");
		if(!"".equals(ref))
			addPropertyWithRef(element, "sessionFactory", ref);
	}

	private void mergeDao(Element element, String className){
		element.addAttribute("class", "org.springframework.aop.framework.ProxyFactoryBean");
		if (generateInterfaces)
			addPropertyWithValue(element, "proxyInterfaces", packageName+"."+className+"DAO");			
		ArrayList<String> attributes = new ArrayList<String>();
		String value = (String)hibernateConfiguration.getPropertyValue("spring.hibernateInterceptor");
		if(!"".equals(value))
			attributes.add(value);			
		value = (String)hibernateConfiguration.getPropertyValue("spring.daoTransactionInterceptor");
		if(!"".equals(value))
			attributes.add(value);
		attributes.add(className+"DAOTarget");
		addPropertyWithList(element, "interceptorNames", attributes);
	}

	private Element elementByName(Element element, String name) {
		Element propsElement = element.element(name);	
		if(propsElement == null)
			propsElement = element.addElement(name);
		return propsElement;
	}

	private Element elementByAttribute(Element parent, String elementName, String attributeName, String attributeValue){
		Element element;
		Iterator iter = parent.elementIterator(elementName);
		while (iter.hasNext()) {
			element = (Element) iter.next();
			if(attributeValue.equals(element.attributeValue(attributeName)))
				return element;
		}
		element = parent.addElement(elementName);
		return element.addAttribute(attributeName, attributeValue);
	}

	private Element elementByText(Element parent, String elementName, String text){
		Element element;
		Iterator iter = parent.elementIterator(elementName);
		while (iter.hasNext()) {
			element = (Element) iter.next();
			if(text.equals(element.getText()))
				return element;
		}
		element = parent.addElement(elementName);
		return element.addText(text);
	}

	private void addPropertyWithValue(Element parent, String propertyName, String text){
		Element element = elementByAttribute(parent, "property", "name", propertyName);
		Element value = elementByName(element, "value");	
		value.setText(text);
	}

	private void addPropertyWithRef(Element parent, String propertyName, String refValue){
		Element element = elementByAttribute(parent, "property", "name", propertyName);
		Element ref = elementByName(element, "ref");
		ref.addAttribute("bean", refValue);
	}

	private void addPropertyWithList(Element parent, String propertyName, List values){
		Element element = elementByAttribute(parent, "property", "name", propertyName);
		Element listElement = elementByName(element, "list");	
		for (int i = 0; i < values.size(); i++)
			elementByText(listElement, "value", (String)values.get(i));
	}

	private void addPropertyWithProps(Element parent, String propertyName, HashMap hashMap){
		Element value;
		Element element = elementByAttribute(parent, "property", "name", propertyName);
		Element propsElement = elementByName(element, "props");		
		Iterator iter = hashMap.entrySet().iterator();
		while (iter.hasNext()) {
			Entry entry = (Entry) iter.next();			
			value = elementByAttribute(propsElement, "prop", "key", (String)entry.getKey());
			value.setText((String)entry.getValue());
		}
	}
}
