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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.collections.SequencedHashMap;
import org.dom4j.Element;
import org.dom4j.Node;
import org.jboss.tools.hibernate.core.*;
import org.jboss.tools.hibernate.core.hibernate.*;
import org.jboss.tools.hibernate.internal.core.BaseResourceReaderWriter;
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;
import org.jboss.tools.hibernate.internal.core.data.Column;


/**
 * @author KVictor
 */

public class XMLFileWriterVisitor extends BaseResourceReaderWriter implements IHibernateMappingVisitor {

	class MappingContext {
		public Element node;

		public IPropertyMapping property;

		public ICollectionMapping collection;

		public boolean isIndex; // true - if collection index, false - collection element

		public boolean isInComposite;
		
	}
	private HibernateConfiguration config;
	// added by yk 30.09.2005
	private XMLReadWriteHelper rwhelper;
	// added by yk 30.09.2005.

	private XMLFileStorage storage;

	private static String[] CLASS_ELEMENTS = new String[] { "meta",
			"subselect", "cache", "synchronize", "id", "composite-id",
			"discriminator", "natural-id", "version", "timestamp", "property", "many-to-one",
			"one-to-one", "component", "dynamic-component", "properties",
			"any", "map", "set", "list", "bag", "idbag", "array",
			"primitive-array", "query-list", "join", "subclass",
			"joined-subclass", "union-subclass", "loader", "sql-insert",
			"sql-update", "sql-delete", "filter" };

	private static String[] SUBCLASS_ELEMENTS = new String[] { "meta",
			"synchronize", "property", "many-to-one", "one-to-one",
			"component", "dynamic-component", "any", "map",
			"set", "list", "bag", "idbag", "array", "primitive-array",
			"query-list", "join", "subclass", "loader", "sql-insert",
			"sql-update", "sql-delete" };

	private static String[] JOINEDSUBCLASS_ELEMENTS = new String[] { "meta",
			"subselect", "synchronize", "key", "property", "many-to-one",
			"one-to-one", "component", "dynamic-component", 
			"any", "map", "set", "list", "bag", "idbag", "array",
			"primitive-array", "query-list", "joined-subclass", "loader",
			"sql-insert", "sql-update", "sql-delete" };

	private static String[] UNIONSUBCLASS_ELEMENTS = new String[] { "meta",
			"subselect", "synchronize", "property", "many-to-one",
			"one-to-one", "component", "dynamic-component",
			"any", "map", "set", "list", "bag", "idbag", "array",
			"primitive-array", "query-list", "union-subclass", "loader",
			"sql-insert", "sql-update", "sql-delete" };

	private static Set<String> CLASS_PROPERTIES = new HashSet<String>();
	static {
		CLASS_PROPERTIES.add("property");
		CLASS_PROPERTIES.add("many-to-one");
		CLASS_PROPERTIES.add("one-to-one");
		CLASS_PROPERTIES.add("component");
		CLASS_PROPERTIES.add("dynamic-component");
		CLASS_PROPERTIES.add("any");
		CLASS_PROPERTIES.add("map");
		CLASS_PROPERTIES.add("set");
		CLASS_PROPERTIES.add("list");
		CLASS_PROPERTIES.add("bag");
		CLASS_PROPERTIES.add("idbag");
		CLASS_PROPERTIES.add("array");
		CLASS_PROPERTIES.add("primitive-array");
		CLASS_PROPERTIES.add("properties");
	}

	private static Set<String> COMPONENT_PROPERTIES = new HashSet<String>();
	static {
		COMPONENT_PROPERTIES.add("property");
		COMPONENT_PROPERTIES.add("many-to-one");
		COMPONENT_PROPERTIES.add("one-to-one");
		COMPONENT_PROPERTIES.add("component");
		COMPONENT_PROPERTIES.add("dynamic-component");
		COMPONENT_PROPERTIES.add("any");
		COMPONENT_PROPERTIES.add("map");
		COMPONENT_PROPERTIES.add("set");
		COMPONENT_PROPERTIES.add("list");
		COMPONENT_PROPERTIES.add("bag");
		COMPONENT_PROPERTIES.add("array");
		COMPONENT_PROPERTIES.add("primitive-array");
	}

	private static Set<String> COMPOSITE_PROPERTIES = new HashSet<String>();
	static {
		COMPOSITE_PROPERTIES.add("property");
		COMPOSITE_PROPERTIES.add("many-to-one");
		COMPOSITE_PROPERTIES.add("any");
		COMPOSITE_PROPERTIES.add("nested-composite-element");
	}

	private static Set<String> JOIN_PROPERTIES = new HashSet<String>();
	static {
		JOIN_PROPERTIES.add("property");
		JOIN_PROPERTIES.add("many-to-one");
		JOIN_PROPERTIES.add("any");
		JOIN_PROPERTIES.add("component");
		JOIN_PROPERTIES.add("dynamic-component");
	}
	
	
/*	private static String[] NATURAL_ID_ELEMENTS = new String[] 			// hibernate 3.0.5
	{"property","many-to-one", "any", "component", "dynamic-component"};
*/
	// from dtd: <!ELEMENT property (meta*,(column|formula)*,type?)>
	private static String[] PROPERTY_ELEMENTS = new String[] { "meta",
			"column", "formula", "type" };

	// from dtd: <!ELEMENT element (column|formula)*>
	private static String[] ELEMENT_ELEMENTS = new String[] { "column",
			"formula" };

	// from dtd: <!ELEMENT component
	// (meta*,parent?,(property|many-to-one|one-to-one|component|dynamic-component|any|map|set|list|bag|array|primitive-array)*)>
	private static String[] COMPONENT_ELEMENTS = new String[] { "meta",
			"parent", "property", "many-to-one", "one-to-one", "component",
			"dynamic-component", "any", "map", "set", "list", "bag", "array",
			"primitive-array" };

	// from dtd: <!ELEMENT composite-element
	// ((meta*),parent?,(property|many-to-one|any|nested-composite-element)*)>
	private static String[] COMPOSITE_ELEMENTS = new String[] { "meta",
			"parent", "property", "many-to-one", "any",
			"nested-composite-element" };

	// from dtd: <!ELEMENT any (meta*,meta-value*,column,column+)>
	private static String[] ANY_ELEMENTS = new String[] { "meta", "meta-value",
			"column" };

	// from dtd: <!ELEMENT one-to-one (meta*|formula*)>
	private static String[] ONETOONE_ELEMENTS = new String[] { "meta",
			"formula" };

	// from dtd: <!ELEMENT many-to-one (meta*,(column|formula)*)>
	private static String[] MANYTOONE_ELEMENTS = new String[] { "meta",
			"column", "formula" };

	// from dtd: <!ELEMENT many-to-many (meta*,(column|formula)*,filter*)>
	private static String[] MANYTOMANY_ELEMENTS = new String[] { "meta",
			"column", "formula", "filter" };
	
	//added 16.05.05 by KVictor
	//<!ELEMENT many-to-any (meta-value*,column, column+)>
	private static String[] MANYTOANY_ELEMENTS = new String[] { "meta-value", "column" };
	
	// <!ELEMENT set
	// (meta*,subselect?,cache?,synchronize*,key,(element|one-to-many|many-to-many|composite-element|many-to-any),loader?,sql-insert?,sql-update?,sql-delete?,sql-delete-all?,
	// filter*)>
	private static String[] SET_ELEMENTS = new String[] { "meta", "subselect",
			"cache", "synchronize", "key", "element", "one-to-many",
			"many-to-many", "composite-element", "many-to-any", "loader",
			"sql-insert", "sql-update", "sql-delete", "sql-delete-all",
			"filter" };
	
	//added 13.05.05 by KVictor
	//<!ELEMENT map (meta*,subselect?,cache?,	synchronize*,key,
	//				(map-key|composite-map-key|map-key-many-to-many|index|composite-index|index-many-to-many|index-many-to-any), 
	//			(element|one-to-many|many-to-many|composite-element|many-to-any),
	//			loader?,sql-insert?,sql-update?,sql-delete?,sql-delete-all?,
	//			filter*
	private static String[] MAP_ELEMENTS = new String[] { "meta", "subselect",
			"cache", "synchronize", "key",
			"map-key", "composite-map-key", "map-key-many-to-many", "index", "composite-index", "index-many-to-many", "index-many-to-any",
			"element", "one-to-many", "many-to-many", "composite-element", "many-to-any", "loader",
			"sql-insert", "sql-update", "sql-delete", "sql-delete-all",
			"filter" };
	
	//<!ELEMENT primitive-array (meta*, subselect?, cache?, synchronize*, key,
	//			(index|list-index), 
	//			element,
	//			loader?,sql-insert?,sql-update?,sql-delete?,sql-delete-all?
	private static String[] PRIM_ARRAY_ELEMENTS = new String[] { "meta", "subselect",
			"cache", "synchronize", "key",
			"index","list-index", 
			"element", "loader",
			"sql-insert", "sql-update", "sql-delete", "sql-delete-all" };
	
	//added 16.05.05
	//<!ELEMENT list (meta*,subselect?,cache?,synchronize*,key, 
	//			(index|list-index), 
	//			(element|one-to-many|many-to-many|composite-element|many-to-any),
	//			loader?,sql-insert?,sql-update?,sql-delete?,sql-delete-all?,
	//			filter*
	private static String[] LIST_ELEMENTS = new String[] { "meta", "subselect",
			"cache", "synchronize", "key","index", "list-index",
			"element", "one-to-many",
			"many-to-many", "composite-element", "many-to-any", "loader",
			"sql-insert", "sql-update", "sql-delete", "sql-delete-all",
			"filter" };
	
	//<!ELEMENT array (meta*,	subselect?, cache?,	synchronize*,key, 
	//			(index|list-index), 
	//			(element|one-to-many|many-to-many|composite-element|many-to-any),
	//			loader?,sql-insert?,sql-update?,sql-delete?,sql-delete-all?
	private static String[] ARRAY_ELEMENTS = new String[] { "meta", "subselect",
			"cache", "synchronize", "key",
			"index","list-index",
			"element", "one-to-many", "many-to-many", "composite-element", "many-to-any",
			"loader", "sql-insert", "sql-update", "sql-delete", "sql-delete-all" };

	//<!ELEMENT bag (meta*,subselect?,cache?,synchronize*,key, 
	//				(element|one-to-many|many-to-many|composite-element|many-to-any),
	//				loader?,sql-insert?,sql-update?,sql-delete?,sql-delete-all?,
	//				filter*
	private static String[] BAG_ELEMENTS = new String[] { "meta", "subselect",
			"cache", "synchronize", "key",
			"element", "one-to-many", "many-to-many", "composite-element", "many-to-any",
			"loader", "sql-insert", "sql-update", "sql-delete", "sql-delete-all", "filter" };
	
	//<!ELEMENT idbag (meta*,	subselect?,	cache?,	synchronize*, collection-id, key, 
	//			(element|many-to-many|composite-element|many-to-any),
	//			loader?,sql-insert?,sql-update?,sql-delete?,sql-delete-all?,
	//			filter*
	private static String[] IDBAG_ELEMENTS = new String[] { "meta", "subselect",
			"cache", "synchronize", "collection-id", "key", 
			"element", "many-to-many", "composite-element", "many-to-any",
			"loader", "sql-insert", "sql-update", "sql-delete", "sql-delete-all", "filter" };	
	
	
	//<!ELEMENT collection-id (meta*, column*, generator)>
	private static String[] COLLECTION_ID_ELEMENTS = new String[] { "meta", "column", "generator" };
	
	//<!ELEMENT join (subselect?,key,(property|many-to-one|component|dynamic-component|any)*,sql-insert?,sql-update?,sql-delete?)>
	private static String[] JOIN_ELEMENTS = new String[] { "subselect", "key", "property", "many-to-one", "component","dynamic-component", "any", "sql-insert", "sql-update", "sql-delete" };
	
	public XMLFileWriterVisitor(XMLFileStorage storage,
			HibernateConfiguration config) {
		this.storage = storage;
		this.config = config;
		rwhelper = new XMLReadWriteHelper(this.config);
	}

	private void processElement(Element node, ISimpleValueMapping simple) {
		if (!"element".equals(node.getName()))
			clearElement(node);
		// added by yk 05.10.2005
		node.setName("element");
		// added by yk 05.10.2005.

		Type type = simple.getType();
		mergeAttribute(node, "type", type == null ? null : type.getName());
		// FORMULA
		
		/* rem by yk 23.09.2005   mergeElement(node, "formula", simple.getFormula()); */
		// added by yk 23.09.2005
		mergeElementMultilineCDATA(node, "formula", simple.getFormula());
		// added by yk 23.09.2005.

		// COLUMN
		int colCount = simple.getColumnSpan();
		processColumns(node, simple.getColumnIterator(), colCount, false);
		reorderElements(node, ELEMENT_ELEMENTS);
	/* rem by yk 05.10.2005	node.setName("element"); */
	}
	
	//changed 17.05.05 by KVictor
	private void processIndex(Element node, ISimpleValueMapping simple, IIndexedCollectionMapping collection) {
		if(collection.isMap()){
			//<!ELEMENT map-key ((column|formula)*)>
			//<!ELEMENT index (column*)>
			if ( !("map-key".equals(node.getName())) && !("index".equals(node.getName())) )
				clearElement(node);
			
			// added by yk 05.10.2005
			node.setName("map-key");
			// added by yk 05.10.2005.

			//<!ATTLIST map-key column CDATA #IMPLIED>
			int colCount = simple.getColumnSpan();
			processColumns(node, simple.getColumnIterator(), colCount, true);
			
			//<!ATTLIST map-key formula CDATA #IMPLIED>
			/* rem by yk 23.09.2005 mergeElement(node, "formula", simple.getFormula()); */
			// added by yk 23.09.2005
			mergeElementMultilineCDATA(node, "formula", simple.getFormula());
			// added by yk 23.09.2005.
			
			//<!ATTLIST map-key type CDATA #REQUIRED>
			Type type = simple.getType();
			mergeAttribute(node, "type", type.getName());

			/* rem by yk 05.10.2005 node.setName("map-key"); */
		} else{
			//<!ELEMENT list-index (column?)>
			//<!ELEMENT index (column*)>
			if ( !("list-index".equals(node.getName())) && !("index".equals(node.getName())) )
				clearElement(node);

			// added by yk 05.10.2005
			node.setName("list-index");
			// added by yk 05.10.2005.

			mergeAttribute(node, "type", null); //clear type from index
			//<!ATTLIST list-index column CDATA #IMPLIED>
			int colCount = simple.getColumnSpan();
			processColumns(node, simple.getColumnIterator(), colCount, true);
			
			//<!ATTLIST list-index base CDATA "0">
			if(collection instanceof IListMapping){
				int base=((IListMapping)collection).getBaseIndex();
				mergeAttribute(node, "base", base==0? null: String.valueOf(base));
			}
			/* rem by yk 05.10.2005 node.setName("list-index");*/
		}
	}

	// changed 3.05.05 by KVictor (in process...)
	private void processProperty(ISimpleValueMapping simple, MappingContext ctx) {
		// <!ELEMENT property (meta*,(column|formula)*,type?)>
		Element node = ctx.node;
		IPropertyMapping property = ctx.property;
		if (!"property".equals(node.getName()))
			clearElement(node);

		// name
		mergeAttribute(node, "name", property.getName());
		mergeAttribute(node, "access", "property".equals(property
				.getPropertyAccessorName()) ? null : property
				.getPropertyAccessorName());

		// TYPE
		Type type = simple.getType();
		if (type != null) {
			Properties typeParams = simple.getTypeParameters();
			if (typeParams != null && !typeParams.isEmpty()) {
				Element elem = node.element("type");
				if (elem == null)
					elem.addElement("type");
				elem.addAttribute("name", type.getName());
				Iterator it = typeParams.keySet().iterator();
				while (it.hasNext()) {
					String name = (String) it.next();
					String value = typeParams.getProperty(name);
					Element param = elem.addElement("param");
					param.addAttribute("name", name);
					param.setText(value);
				}
			} else {
				removeElements(node, "type");
				mergeAttribute(node, "type", type.getName());
			}
		} else {
			removeElements(node, "type");
		}
		
		// <!ATTLIST property update (true|false) "true">
		mergeAttribute(node, "update", property.isUpdateable() ? null : "false");
		// <!ATTLIST property insert (true|false) "true">
		mergeAttribute(node, "insert", property.isInsertable() ? null : "false");
		// <!ATTLIST property optimistic-lock (true|false) "true"> <!-- only
		// supported for properties of a class (not component) -->
		mergeAttribute(node, "optimistic-lock",
				property.isOptimisticLocked() ? null : "false");
		// <!ATTLIST property lazy (true|false) "false">
		mergeAttribute(node, "lazy", property.isLazy() ? "true" : null);
		// FORMULA
		/* rem by yk 23.09.2005 mergeElement(node, "formula", simple.getFormula()); */
		// added by yk 23.09.2005
		mergeElementMultilineCDATA(node, "formula", simple.getFormula());
		// added by yk 23.09.2005.
		// COLUMN
		int colCount = simple.getColumnSpan();
		processColumns(node, simple.getColumnIterator(), colCount, false);
		
		reorderElements(node, PROPERTY_ELEMENTS);
		node.setName("property");
	}
	
	public Object visitSimpleValueMapping(ISimpleValueMapping simple,
			Object argument) {
		MappingContext ctx = (MappingContext) argument;
		if (ctx.collection != null) {
			if(ctx.isIndex) processIndex(ctx.node, simple, (IIndexedCollectionMapping)ctx.collection);
			else processElement(ctx.node, simple);
		} else processProperty(simple, ctx);
		return null;
	}
	
	
	//---------------- Components ----------------------------
	private void processCompositeProperties(Element node,
			IComponentMapping component) {
		// from dtd: (property|many-to-one|any|nested-composite-element)*
		Iterator it = node.elementIterator();
		Map<String,Node> elements = new HashMap<String,Node>();
		// collect property elements
		while (it.hasNext()) {
			Element elem = (Element) it.next();
			String elemName = elem.getName();
			if (COMPOSITE_PROPERTIES.contains(elemName)) {
				String name = elem.attributeValue("name", null);
				if (name != null)
					elements.put(name, elem.detach());
				else
					elem.detach();
			}
		}
		MappingContext ctx = new MappingContext();
		// merge elements with existing properties
		it = component.getPropertyIterator();
		while (it.hasNext()) {
			IPropertyMapping mapping = (IPropertyMapping) it.next();
			if (mapping.getValue() != null) {
				Element elem = (Element) elements.get(mapping.getName());
				if (elem == null)
					elem = node.addElement("property");
				else
					node.add(elem);
				ctx.node = elem;
				ctx.property = mapping;
				ctx.isInComposite = true;
				mapping.getValue().accept(this, ctx);
			}
		}
	}

	private void processCompositeElement(Element node, IPropertyMapping property, IComponentMapping mapping, boolean isNested){
		// <!ELEMENT composite-element
		// ((meta*),parent?,(property|many-to-one|any|nested-composite-element)*)>
		// <!ELEMENT nested-composite-element
		// (parent?,(property|many-to-one|any|nested-composite-element)*)>
		if (isNested) { // nested
			if (!"nested-composite-element".equals(node.getName()))
				clearElement(node);
			// <!ATTLIST component name CDATA #REQUIRED>
			mergeAttribute(node, "name", property.getName());
			// <!ATTLIST component access CDATA #IMPLIED>
			mergeAttribute(node, "access", "property".equals(property
					.getPropertyAccessorName()) ? null : property
					.getPropertyAccessorName());
			node.setName("nested-composite-element");
		} else {
			if (!"composite-element".equals(node.getName()))
				clearElement(node);
			node.setName("composite-element");
		}
		// <!ATTLIST composite-element class CDATA #REQUIRED>
		mergeAttribute(node, "class", mapping.getComponentClassName());
			
		// (property|many-to-one|any|nested-composite-element)*)
		processCompositeProperties(node, mapping);

		// PARENT
		processParent(node, mapping);
		reorderElements(node, COMPOSITE_ELEMENTS);
	}
	
	
	private void processCompositeIndexProperties(Element node,IComponentMapping component) {
		// from dtd:
		// (key-property|key-many-to-one)+
		Iterator it = node.elementIterator();
		Map<String,Node> elements = new HashMap<String,Node>();
		// collect property elements
		while (it.hasNext()) {
			Element elem = (Element) it.next();
			String elemName = elem.getName();
			if ("key-property".equals(elemName) || "key-many-to-one".equals(elemName)) {
				String name = elem.attributeValue("name", null);
				if (name != null)
					elements.put(name, elem.detach());
				else
					elem.detach();
			}
		}
		// merge elements with existing properties
		it = component.getPropertyIterator();
		while (it.hasNext()) {
			IPropertyMapping mapping = (IPropertyMapping) it.next();
			IHibernateValueMapping value=mapping.getValue();
			if (value != null) {
				Element elem = (Element) elements.get(mapping.getName());
				if (elem == null)
					elem = node.addElement("key-property");
				else
					node.add(elem);
				//key-property
				if(value instanceof ISimpleValueMapping)processKeyProp(elem, mapping);
				//key-many-to-one
				else if(value instanceof IManyToOneMapping)processKeyManyToOneProp(elem, mapping);
			}
		}
	}

	
	//changed 17.05.05 by KVictor
	private void processCompositeIndex(Element node, IComponentMapping mapping, IMapMapping collection) {
		//<!ELEMENT composite-map-key ( (key-property|key-many-to-one)+ )>
		//<!ELEMENT composite-index ( (key-property|key-many-to-one)+ )>
		if ( !("composite-map-key".equals(node.getName())) && !("composite-index".equals(node.getName())) )
			clearElement(node);
		
		//	<!ATTLIST composite-map-key class CDATA #REQUIRED>
		mergeAttribute(node, "class", mapping.getComponentClassName());

		processCompositeIndexProperties(node, mapping);
		node.setName("composite-map-key");
	}
	

	private void processParent(Element node, IComponentMapping mapping) {
		Element parentElem = node.element("parent");
		String parent = mapping.getParentProperty();
		if (parent != null && parent.length() > 0) {
			if (parentElem == null)
				parentElem = node.addElement("parent");
			parentElem.addAttribute("name", parent);
		} else {
			if (parentElem != null)
				node.remove(parentElem);
		}
	}

	private void processComponentProperties(Element node,IComponentMapping component) {
		// from dtd:
		// (property|many-to-one|one-to-one|component|dynamic-component|any|map|set|list|bag|array|primitive-array)*)
		Iterator it = node.elementIterator();
		Map<String,Node> elements = new HashMap<String,Node>();
		// collect property elements
		while (it.hasNext()) {
			Element elem = (Element) it.next();
			String elemName = elem.getName();
			if (COMPONENT_PROPERTIES.contains(elemName)) {
				String name = elem.attributeValue("name", null);
				if (name != null)
					elements.put(name, elem.detach());
				else
					elem.detach();
			}
		}
		MappingContext ctx = new MappingContext();
		// merge elements with existing properties
		it = component.getPropertyIterator();
		while (it.hasNext()) {
			IPropertyMapping mapping = (IPropertyMapping) it.next();
			if (mapping.getValue() != null) {
				Element elem = (Element) elements.get(mapping.getName());
				if (elem == null)
					elem = node.addElement("property");
				else
					node.add(elem);
				ctx.node = elem;
				ctx.property = mapping;
				mapping.getValue().accept(this, ctx);
			}
		}
	}

	private void  processComponent(Element node, IPropertyMapping property, IComponentMapping mapping){
		// <!ELEMENT component
		// (meta*,parent?,(property|many-to-one|one-to-one|component|dynamic-component|any|map|set|list|bag|array|primitive-array)*)>

		if ( !"component".equals(node.getName())
				&& !"dynamic-component".equals(node.getName()) )
			clearElement(node);

		// <!ATTLIST component name CDATA #REQUIRED>
		mergeAttribute(node, "name", property.getName());
		// <!ATTLIST component update (true|false) "true">
		mergeAttribute(node, "update", property.isUpdateable() ? "true" : "false");
		// <!ATTLIST component insert (true|false) "true">
		mergeAttribute(node, "insert", property.isInsertable() ? "true" : "false");
		// <!ATTLIST component optimistic-lock (true|false) "true">
		mergeAttribute(node, "optimistic-lock",
				property.isOptimisticLocked() ? null : "false");
		mergeAttribute(node, "unique",property.getUnique() ? "true" : null);


		// (property|many-to-one|one-to-one|component|dynamic-component|any|map|set|list|bag|array|primitive-array)*
 		processComponentProperties(node, mapping);

		if(!mapping.isProperties_component())
		{
			// <!ATTLIST component access CDATA #IMPLIED>
			mergeAttribute(node, "access", "property".equals(property
					.getPropertyAccessorName()) ? null : property
					.getPropertyAccessorName());
	
			// <!ATTLIST component class CDATA #IMPLIED>
			mergeAttribute(node, "class", mapping.isDynamic()? null: mapping.getComponentClassName());
	
			// <!ATTLIST component lazy (true|false) "false">
			mergeAttribute(node, "lazy", property.isLazy() ? "true" : null);
			// <!ATTLIST component unique (true|false) "false">
			// will be written in columns
	
			// PARENT
			processParent(node, mapping);
	
			reorderElements(node, COMPONENT_ELEMENTS);
			if (mapping.isDynamic())
				node.setName("dynamic-component");
			else
				node.setName("component");
		}
		else
		{// set node name for "properties" node;
			node.setName("properties");
		}
	}
	
	public Object visitComponentMapping(IComponentMapping mapping,
			Object argument) {
		MappingContext ctx = (MappingContext) argument;
		if (ctx.collection != null || ctx.isInComposite) {
			if(ctx.isIndex)processCompositeIndex(ctx.node, mapping, (IMapMapping)ctx.collection);
			else processCompositeElement(ctx.node, ctx.property, mapping, ctx.isInComposite);
		}
		else processComponent(ctx.node, ctx.property, mapping);
		return null;

	}
	//------------------- end components
	
	// ------------------ Collections
	//changed 16.05.05 by KVictor
	public Object visitListMapping(IListMapping column, Object argument) {
		//<!ELEMENT list (meta*,subselect?,cache?,synchronize*,key,
		//			(index|list-index), 
		//			(element|one-to-many|many-to-many|composite-element|many-to-any),
		//			loader?,sql-insert?,sql-update?,sql-delete?,sql-delete-all?,
		//			filter*
		MappingContext ctx = (MappingContext) argument;
		Element node = ctx.node;
		IPropertyMapping property = ctx.property;
		if (!"list".equals(node.getName()))
			clearElement(node);

		processSharedCollAtrs(node, column, property);
		
		//(index|list-index)
		Element element = node.element("index");
		if (element == null) {
			element = node.element("list-index");
			if (element == null) {
				element = node.addElement("index");
			}
		}
		
		processIndex(element, (ISimpleValueMapping)column.getIndex(), column);
		
		// FILTER
		// TODO FILTER	
		
		// (element|one-to-many|many-to-many|composite-element|many-to-any)
		element = node.element("element");
		if (element == null) {
			element = node.element("one-to-many");
			if (element == null) {
				element = node.element("many-to-many");
				if (element == null) {
					element = node.element("many-to-any");
					if (element == null) {
						element = node.element("composite-element");
						if (element == null)
							element = node.addElement("element");
					}
				}
			}
		}
		
		ctx = new MappingContext();
		ctx.property = property;
		ctx.node = element;
		ctx.collection = column;
		column.getElement().accept(this, ctx);

		reorderElements(node, LIST_ELEMENTS);
		node.setName("list");
		return null;
	}
	
	//changed 16.05.05 by KVictor 
	public Object visitArrayMapping(IArrayMapping mapping, Object argument) {
		//<!ELEMENT array (meta*,	subselect?,	cache?, synchronize*,key, 
		//			(index|list-index), 
		//			(element|one-to-many|many-to-many|composite-element|many-to-any),
		//			loader?,sql-insert?,sql-update?,sql-delete?,sql-delete-all?
		MappingContext ctx = (MappingContext) argument;
		Element node = ctx.node;
		IPropertyMapping property = ctx.property;
		if (!"array".equals(node.getName()))
			clearElement(node);
		
		processSharedCollAtrs(node, mapping, property);
		
		//(index|list-index)
		Element element = node.element("index");
		if (element == null) {
			element = node.element("list-index");
			if (element == null) {
				element = node.addElement("index");
			}
		}
		
		processIndex(element, (ISimpleValueMapping)mapping.getIndex(), mapping);
		
		// (element|one-to-many|many-to-many|composite-element|many-to-any)
		element = node.element("element");
		if (element == null) {
			element = node.element("one-to-many");
			if (element == null) {
				element = node.element("many-to-many");
				if (element == null) {
					element = node.element("many-to-any");
					if (element == null) {
						element = node.element("composite-element");
						if (element == null)
							element = node.addElement("element");
					}
				}
			}
		}
		
		ctx = new MappingContext();
		ctx.property = property;
		ctx.node = element;
		ctx.collection = mapping;
		mapping.getElement().accept(this, ctx);
		
		reorderElements(node, ARRAY_ELEMENTS);
		node.setName("array");
		return null;
	}
	
	//changed 16.05.05 by KVictor
	public Object visitBagMapping(IBagMapping bagMapping, Object argument) {
		//<!ELEMENT bag (meta*,subselect?,cache?,synchronize*,key, 
		//				(element|one-to-many|many-to-many|composite-element|many-to-any),
		//				loader?,sql-insert?,sql-update?,sql-delete?,sql-delete-all?,
		//				filter*
		MappingContext ctx = (MappingContext) argument;
		Element node = ctx.node;
		IPropertyMapping property = ctx.property;
		if (!"bag".equals(node.getName()))
			clearElement(node);
		
		processSharedCollAtrs(node, bagMapping, property);
		
		// FILTER
		// TODO FILTER	
		
		// (element|one-to-many|many-to-many|composite-element|many-to-any)
		Element element = node.element("element");
		if (element == null) {
			element = node.element("one-to-many");
			if (element == null) {
				element = node.element("many-to-many");
				if (element == null) {
					element = node.element("many-to-any");
					if (element == null) {
						element = node.element("composite-element");
						if (element == null)
							element = node.addElement("element");
					}
				}
			}
		}
		
		ctx = new MappingContext();
		ctx.property = property;
		ctx.node = element;
		ctx.collection = bagMapping;
		bagMapping.getElement().accept(this, ctx);
		
		reorderElements(node, BAG_ELEMENTS);
		node.setName("bag");
		return null;
	}
	
	private void processCollectionId(Element node, ISimpleValueMapping simple) {
		//<!ELEMENT collection-id (meta*, column*, generator)>
		if (!"collection-id".equals(node.getName()))clearElement(node);
		
		// added by yk 05.10.2005
		node.setName("collection-id");
		// added by yk 05.10.2005.

		Type type = simple.getType();
		if(type != null)
			mergeAttribute(node, "type", type.getName());
		
		Iterator it=simple.getColumnIterator();
		// COLUMN
		if(it.hasNext()){
			IDatabaseColumn column=(IDatabaseColumn) it.next();
			mergeAttribute(node, "column", column.getName());
			mergeAttribute(node, "length",
					column.getLength() 
                    // changed by Nick 02.09.2005
                    /* == Column.DEFAULT_LENGTH */ <= Column.DEFAULT_LENGTH ? null : String
                            // by Nick
                            .valueOf(column.getLength()));
			
		}else processColumns(node,it ,0, true);

		// GENERATOR
		// --
		removeElements(node, "generator");
		processGenerator(node, simple);
		// --
		reorderElements(node, COLLECTION_ID_ELEMENTS);
		/* rem by yk 05.10.2005 node.setName("collection-id"); */
	}
	
	//changed 16.05.05 by KVictor
	public Object visitIdBagMapping(IIdBagMapping mapping, Object argument) {
		//<!ELEMENT idbag (meta*,	subselect?,	cache?,	synchronize*, collection-id, key, 
		//			(element|many-to-many|composite-element|many-to-any),
		//			loader?,sql-insert?,sql-update?,sql-delete?,sql-delete-all?,
		//			filter*
		MappingContext ctx = (MappingContext) argument;
		Element node = ctx.node;
		IPropertyMapping property = ctx.property;
		if (!"idbag".equals(node.getName()))
			clearElement(node);
		
		processSharedCollAtrs(node, mapping, property);
		
		//collection-id
		Element idElem=node.element("collection-id");
		if(idElem==null) idElem=node.addElement("collection-id");
		processCollectionId(idElem, (ISimpleValueMapping) mapping.getIdentifier() );
		
		// FILTER
		// TODO FILTER	
		
		//(element|many-to-many|composite-element|many-to-any)
		Element element = node.element("element");
		if (element == null) {
			element = node.element("many-to-many");
			if (element == null) {
				element = node.element("many-to-any");
				if (element == null) {
					element = node.element("composite-element");
					if (element == null)
						element = node.addElement("element");
				}
			}
		}
		
		ctx = new MappingContext();
		ctx.property = property;
		ctx.node = element;
		ctx.collection = mapping;
		mapping.getElement().accept(this, ctx);
		
		reorderElements(node, IDBAG_ELEMENTS);
		node.setName("idbag");
		return null;
	}
	
	//changed 13.05.05 by KVictor
	public Object visitPrimitiveArrayMapping(IPrimitiveArrayMapping mapping,
			Object argument) {
		//<!ELEMENT primitive-array (
		//			meta*, 
		//			subselect?,
		//			cache?, 
		//			synchronize*,
		//			key, (index|list-index),element,loader?,sql-insert?,sql-update?,sql-delete?,sql-delete-all?
		
		MappingContext ctx = (MappingContext) argument;
		Element node = ctx.node;
		IPropertyMapping property = ctx.property;
		if (!"primitive-array".equals(node.getName()))
			clearElement(node);
		
		processSharedCollAtrs(node, mapping, property);
		
		//(index|list-index)
		Element element = node.element("index");
		if (element == null) {
			element = node.element("list-index");
			if (element == null) {
				element = node.addElement("index");
			}
		}
		
		processIndex(element, (ISimpleValueMapping)mapping.getIndex(), mapping);
		
		//element
		element = node.element("element");
		if (element == null) {
			element = node.addElement("element");
		}
		processElement(element, (ISimpleValueMapping)mapping.getElement());
		
		reorderElements(node, PRIM_ARRAY_ELEMENTS);
		node.setName("primitive-array");
		return null;
	}
	
	//changed 13.05.05 by KVictor
	public Object visitMapMapping(IMapMapping mapping, Object argument) {
		//<!ELEMENT map (meta*,subselect?,cache?,	synchronize*,key,
		//				(map-key|composite-map-key|map-key-many-to-many|index|composite-index|index-many-to-many|index-many-to-any), 
		//			(element|one-to-many|many-to-many|composite-element|many-to-any),
		//			loader?,sql-insert?,sql-update?,sql-delete?,sql-delete-all?,
		//			filter*
		MappingContext ctx = (MappingContext) argument;
		Element node = ctx.node;
		IPropertyMapping property = ctx.property;
		if (!"map".equals(node.getName()))
			clearElement(node);
		
		processSharedCollAtrs(node, mapping, property);
		
		// FILTER
		// TODO FILTER
		
		// (element|one-to-many|many-to-many|composite-element|many-to-any)
		Element element = node.element("element");
		if (element == null) {
			element = node.element("one-to-many");
			if (element == null) {
				element = node.element("many-to-many");
				if (element == null) {
					element = node.element("many-to-any");
					if (element == null) {
						element = node.element("composite-element");
						if (element == null)
							element = node.addElement("element");
					}
				}
			}
		}
		ctx = new MappingContext();
		ctx.property = property;
		ctx.node = element;
		ctx.collection = mapping;
		mapping.getElement().accept(this, ctx);
		
		//(map-key|composite-map-key|map-key-many-to-many|index|composite-index|index-many-to-many|index-many-to-any)
		element = node.element("map-key");
		if (element == null) {
			element = node.element("composite-map-key");
			if (element == null) {
				element = node.element("map-key-many-to-many");
				if (element == null) {
					element = node.element("index");
					if (element == null) {
						element = node.element("composite-index");
						if (element == null){
							element = node.element("index-many-to-many");
							if (element == null){
								element = node.element("index-many-to-any");
								if (element == null)
								element = node.addElement("map-key");	
							}		
						}
					}
				}
			}
		}

		ctx.node = element;
		ctx.isIndex=true;
		mapping.getIndex().accept(this, ctx);		
		
		reorderElements(node, MAP_ELEMENTS);
		node.setName("map");
		return null;
	}
	
	private void processFetchAttribute(Element node, String fetchMode,String defaultMode){
		// <!ATTLIST XXX outer-join (true|false|auto) #IMPLIED>
		mergeAttribute(node, "outer-join", null);
		// <!ATTLIST XXX fetch (join|select|subselect) #IMPLIED>
		mergeAttribute(node, "fetch", "default".equals(fetchMode) || defaultMode.equals(fetchMode)? null : fetchMode);
		
	}
	private void clearTablesAttributes(Element node)
	{
		mergeAttribute(node, "table", null);
		mergeAttribute(node, "schema", null);
		mergeAttribute(node, "catalog", null);
	}
	//added 13.05.05 by KVictor
	private void processSharedCollAtrs(Element node, ICollectionMapping mapping, IPropertyMapping property){
		// <!ATTLIST set name CDATA #REQUIRED>
		mergeAttribute(node, "name", property.getName());
		// <!ATTLIST set access CDATA #IMPLIED>
		mergeAttribute(node, "access", "property".equals(property
				.getPropertyAccessorName()) ? null : property
				.getPropertyAccessorName());
		
		// <!ATTLIST set table CDATA #IMPLIED> <!-- default: name -->
		// <!ATTLIST set schema CDATA #IMPLIED> <!-- default: none -->
		// <!ATTLIST set catalog CDATA #IMPLIED> <!-- default: none -->
		
		if(!mapping.isOneToMany() && 
				(mapping.getSubselect() == null || "".equals(mapping.getSubselect())))processDatabaseTable(node, mapping.getCollectionTable());
		else
		{		clearTablesAttributes(node);		}
		
		//<!ATTLIST map subselect CDATA #IMPLIED>
		/* rem by yk 27.09.2005 mergeAttribute(node, "subselect", mapping.getSubselect()); */
		mergeElementMultilineCDATA(node, "subselect", mapping.getSubselect());
		if(!mapping.isArray()){
			// <!ATTLIST set lazy (true|false) #IMPLIED>
			/* rem by yk 20.09.2005  mergeAttribute(node, "lazy", mapping.isLazy()?  "true":"false"); */
			// added by yk 20.09.2005
			mergeAttribute(node, "lazy", (mapping.isLazy() == storage.isDefaultLazy()) ? null : String.valueOf(mapping.isLazy())); 
			// added by yk 20.09.2005.

		}
		// #changed# by Konstantin Mishin on 19.12.2005 fixed for ESORM-419
		//if(!mapping.isIndexed()){
		if(!mapping.isIndexed() || mapping.isMap()){
		// #changed#
			// <!ATTLIST set sort CDATA "unsorted"> <!--
			// unsorted|natural|"comparator class" -->
			mergeAttribute(node, "sort", "unsorted".equals(mapping.getSort())? null: mapping.getSort());
			
			// <!ATTLIST set order-by CDATA #IMPLIED> <!-- default: none -->
			mergeAttribute(node, "order-by", mapping.getOrderBy());
		}
		// <!ATTLIST set inverse (true|false) "false">
		mergeAttribute(node, "inverse", mapping.isInverse() ? "true" : null);
		// <!ATTLIST set cascade CDATA #IMPLIED>
		mergeAttribute(node, "cascade", property.getCascade());
		
		// <!ATTLIST set where CDATA #IMPLIED> <!-- default: none -->
		mergeAttribute(node, "where", mapping.getWhere());
		// <!ATTLIST set batch-size CDATA #IMPLIED>
		mergeAttribute(node, "batch-size", mapping.getBatchSize()==1? null: String.valueOf(mapping.getBatchSize()));
		// <!ATTLIST set outer-join (true|false|auto) #IMPLIED>
		// <!ATTLIST set fetch (join|select|subselect) #IMPLIED>
		processFetchAttribute(node, mapping.getFetchMode(), "select");
		// <!ATTLIST set persister CDATA #IMPLIED>
		//
		// <!ATTLIST set collection-type CDATA #IMPLIED>
		//
		// <!ATTLIST set check CDATA #IMPLIED> <!-- default: none -->
		mergeAttribute(node, "check", mapping.getCheck());
		// <!ATTLIST set optimistic-lock (true|false) "true"> <!-- only
		mergeAttribute(node, "optimistic-lock", mapping.isOptimisticLocked()?null:"false");
		// supported for properties of a class (not component) -->
		
// added by yk 28.06.2005
		mergeAttribute(node, "persister",mapping.getPersister());
// added by yk 28.06.2005 stop
		// SUBSELECT
		/* rem by yk 27.09.2005 mergeElement(node, "subselect", mapping.getSubselect()); */
		mergeElementMultilineCDATA(node, "subselect", mapping.getSubselect());
		// CACHE
		processCache(node, mapping);
		// SYNCHRONIZE
		processSynchronize(node, mapping);
		// KEY
		processKey(node, mapping);
		// CUSTOM SQL:
		// loader?,sql-insert?,sql-update?,sql-delete?,sql-delete-all
		processCustomSQL(node, mapping);
	}
	
	//changed 13.05.05
	public Object visitSetMapping(ISetMapping mapping, Object argument) {
		// <!ELEMENT set
		// (meta*,subselect?,cache?,synchronize*,key,(element|one-to-many|many-to-many|composite-element|many-to-any),loader?,sql-insert?,sql-update?,sql-delete?,sql-delete-all?,
		// filter*)>
		MappingContext ctx = (MappingContext) argument;
		Element node = ctx.node;
		IPropertyMapping property = ctx.property;
		if (!"set".equals(node.getName()))
			clearElement(node);

		processSharedCollAtrs(node, mapping, property);
		
		// FILTER
		// TODO FILTER	
		
		// (element|one-to-many|many-to-many|composite-element|many-to-any)
		Element element = node.element("element");
		if (element == null) {
			element = node.element("one-to-many");
			if (element == null) {
				element = node.element("many-to-many");
				if (element == null) {
					element = node.element("many-to-any");
					if (element == null) {
						element = node.element("composite-element");
						if (element == null)
							element = node.addElement("element");
					}
				}
			}
		}
		ctx = new MappingContext();
		ctx.property = property;
		ctx.node = element;
		ctx.collection = mapping;
		mapping.getElement().accept(this, ctx);

		reorderElements(node, SET_ELEMENTS);
		node.setName("set");
		return null;
	}

	// ----------------------- End collections

	
	private void processEntityName(Element node, String entityName){
		mergeAttribute(node, "class", null);
		mergeAttribute(node, "entity-name", entityName);
		
	}
	
	//---------------- one-to-many
	public Object visitOneToManyMapping(IOneToManyMapping mapping,
			Object argument) {
		//<!ELEMENT one-to-many EMPTY>
		
		MappingContext ctx = (MappingContext) argument;
		Element node = ctx.node;

		if (!"one-to-many".equals(node.getName()))
			clearElement(node);
		
		
		//<!ATTLIST one-to-many not-found (exception|ignore) "exception">
		//mergeAttribute(node, "not-found", mapping.)
// added by yk 28.06.2005
		mergeAttribute(node, "not-found", mapping.isIgnoreNotFound() ? "ignore": null);
// added by yk 28.06.2005 stop

		//<!ATTLIST one-to-many node CDATA #IMPLIED>
		
		//<!ATTLIST one-to-many embed-xml (true|false) "true">
		
		//<!ATTLIST one-to-many class CDATA #IMPLIED>
		//<!ATTLIST one-to-many entity-name CDATA #IMPLIED>
		processEntityName(node,mapping.getReferencedEntityName());
		
		node.setName("one-to-many");
		return null;
	}
	
	//changed 17.05.05 by KVictor
	//-------------------- many-to-many -------------------
	private void processIndexManyToMany(Element node, IManyToManyMapping mapping, IMapMapping collection){
		//<!ELEMENT map-key-many-to-many ((column|formula)*)>
		//<!ELEMENT index-many-to-many (column*)>
		
		if ( !("map-key-many-to-many".equals(node.getName())) && !("index-many-to-many ".equals(node.getName())) )
			clearElement(node);

		// added by yk 05.10.2005
		node.setName("map-key-many-to-many");
		// added by yk 05.10.2005.

		//<!ATTLIST index-many-to-many entity-name CDATA #IMPLIED>
		processEntityName(node,mapping.getReferencedEntityName());
		
		//<!ATTLIST index-many-to-many foreign-key CDATA #IMPLIED>
		mergeAttribute(node, "foreign-key", mapping.getForeignKeyName());

		//COLUMN|FORMULA
		/* rem by yk 23.09.2005 mergeElement(node, "formula", mapping.getFormula()); */
		// added by yk 23.09.2005
		mergeElementMultilineCDATA(node, "formula", mapping.getFormula());
		// added by yk 23.09.2005.

		processColumns(node, mapping.getColumnIterator(), /* rem by yk 26.09.2005 2 */ mapping.getColumnSpan(), false);
		
		/* rem by yk 05.10.2005 node.setName("map-key-many-to-many"); */
	}
	
	// changed 12.05.05 by KVictor
	private void processManyToMany(Element node, IManyToManyMapping mapping, ICollectionMapping collection) {
		// <!ELEMENT many-to-many (meta*,(column|formula)*,filter*)>

		if (!"many-to-many".equals(node.getName()))
			clearElement(node);

		// added by yk 05.10.2005
		node.setName("many-to-many");
		// added by yk 05.10.2005.

		// <!ATTLIST many-to-many class CDATA #IMPLIED>
		// <!ATTLIST many-to-many entity-name CDATA #IMPLIED>
		processEntityName(node,mapping.getReferencedEntityName());

		// <!ATTLIST many-to-many outer-join (true|false|auto) #IMPLIED>
		// <!ATTLIST many-to-many fetch (join|select) "join">
		processFetchAttribute(node, mapping.getFetchMode(), "join");

		// <!ATTLIST many-to-many foreign-key CDATA #IMPLIED>
		mergeAttribute(node, "foreign-key", mapping.getForeignKeyName());

		// <!ATTLIST many-to-many not-found (exception|ignore) "exception">
		mergeAttribute(node, "not-found", mapping.isIgnoreNotFound() ? "ignore"
				: null);

		// COLUMN|FORMULA
		/* rem by yk 23.09.2005 mergeElement(node, "formula", mapping.getFormula()); */
		// added by yk 23.09.2005
		mergeElementMultilineCDATA(node, "formula", mapping.getFormula());
		// added by yk 23.09.2005.

		processColumns(node, mapping.getColumnIterator(), /* rem by yk 26.09.2005 2 */mapping.getColumnSpan(), false);

		reorderElements(node, MANYTOMANY_ELEMENTS);
		/* rem by yk 05.10.2005 node.setName("many-to-many"); */
	}

	public Object visitManyToManyMapping(IManyToManyMapping mapping,Object argument) {
		MappingContext ctx = (MappingContext) argument;
		if(ctx.isIndex) processIndexManyToMany(ctx.node, mapping, (IMapMapping)ctx.collection);
		else processManyToMany(ctx.node, mapping, ctx.collection);
		return null;
	}

	
	//-------------------- Any , many-to-any
	private void processMetaValues(Element node, IAnyMapping mapping){
		removeElements(node, "meta-value");
		Map metaValues = mapping.getMetaValues();
		if(metaValues==null) return;
		Iterator it = metaValues.keySet().iterator();
		while (it.hasNext()) {
			String value = (String) it.next();
			String className = (String) metaValues.get(value);
			Element metaValue = node.addElement("meta-value");
			metaValue.addAttribute("value", value);
			metaValue.addAttribute("class", className);
		}
	}
	
	private int processAny(Element node, IPropertyMapping property , IAnyMapping mapping) {
		// <!ELEMENT any (meta*,meta-value*,column,column+)>

		if (!"any".equals(node.getName()))
			clearElement(node);

		// name
		mergeAttribute(node, "name", property.getName());
		// id-type
		mergeAttribute(node, "id-type", mapping.getIdentifierType());
		// meta-type
		mergeAttribute(node, "meta-type", mapping.getMetaType());
		// access
		mergeAttribute(node, "access", "property".equals(property
				.getPropertyAccessorName()) ? null : property
				.getPropertyAccessorName());

		// <!ATTLIST any update (true|false) "true">
		mergeAttribute(node, "update", property.isUpdateable() ? null : "false");
		// <!ATTLIST any insert (true|false) "true">
		mergeAttribute(node, "insert", property.isInsertable() ? null : "false");
		// <!ATTLIST any cascade CDATA #IMPLIED>
		mergeAttribute(node, "cascade", property.getCascade());

		// <!ATTLIST any index CDATA #IMPLIED>
		//

		// <!ATTLIST property optimistic-lock (true|false) "true"> <!-- only
		// supported for properties of a class (not component) -->
		mergeAttribute(node, "optimistic-lock",
				property.isOptimisticLocked() ? null : "false");

		// <!ATTLIST property lazy (true|false) "false">
		mergeAttribute(node, "lazy", property.isLazy() ? "true" : null);

		// META-VALUE
		processMetaValues(node, mapping);

		// COLUMN
		processColumns(node, mapping.getColumnIterator(), /* rem by yk 26.09.2005 2 */ mapping.getColumnSpan(), false);

		reorderElements(node, ANY_ELEMENTS);
		node.setName("any");
		return XMLFileReader.MAPPING_OK;
	}
	
	//changed 16.05.05 by KVictor
	private void processIndexManyToAny(Element node, IAnyMapping mapping, IMapMapping collection) {
		//<!ELEMENT index-many-to-any (column, column+)>
		//see processAny
		if (!"index-many-to-any".equals(node.getName()))
			clearElement(node);

		mergeAttribute(node, "id-type", mapping.getIdentifierType());
		// meta-type
		mergeAttribute(node, "meta-type", mapping.getMetaType());

		// COLUMN
		processColumns(node, mapping.getColumnIterator(), /* rem by yk 26.09.2005 2 */ mapping.getColumnSpan(), false);

		node.setName("index-many-to-any");
	}
	
	//changed 16.05.05 by KVictor
	private void processManyToAny(Element node, IAnyMapping mapping, ICollectionMapping collection) {
		//<!ELEMENT many-to-any (meta-value*,column, column+)>
		if (!"many-to-any".equals(node.getName()))
			clearElement(node);

		mergeAttribute(node, "id-type", mapping.getIdentifierType());
		// meta-type
		mergeAttribute(node, "meta-type", mapping.getMetaType());

		// META-VALUE
		processMetaValues(node, mapping);

		// COLUMN
		processColumns(node, mapping.getColumnIterator(), /* rem by yk 26.09.2005 2 */ mapping.getColumnSpan(), false);

		reorderElements(node, MANYTOANY_ELEMENTS);
		node.setName("many-to-any");
	}
	
	public Object visitAnyMapping(IAnyMapping mapping, Object argument) {
		MappingContext ctx = (MappingContext) argument;
		if(ctx.collection != null ){
			if(ctx.isIndex) processIndexManyToAny(ctx.node, mapping, (IMapMapping)ctx.collection);
			else processManyToAny(ctx.node, mapping, ctx.collection);
		}
		/* rem by yk 08.10.2005 processAny(ctx.node, ctx.property, mapping); */
		// added by yk 08.10.2005
		if(XMLFileReader.MAPPING_ERROR == processAny(ctx.node, ctx.property, mapping))
		{// temporary variant
			if("property".equals(ctx.node.getParent().getName()))
			{
				ctx.node.getParent().detach();
			}
		}
		// added by yk 08.10.2005.

		return null;
	}
	
	public Object visitManyToAnyMapping(IManyToAnyMapping mapping,
			Object argument) {
		MappingContext ctx = (MappingContext) argument;
		if(ctx.isIndex) processIndexManyToAny(ctx.node, mapping, (IMapMapping)ctx.collection);
		else processManyToAny(ctx.node, mapping, ctx.collection);
		return null;
	}

	// -------------- Many-to-one

	public Object visitManyToOneMapping(IManyToOneMapping mapping,
			Object argument) {
		// <!ELEMENT many-to-one (meta*,(column|formula)*)>
		MappingContext ctx = (MappingContext) argument;
		Element node = ctx.node;
		IPropertyMapping property = ctx.property;

		if (!"many-to-one".equals(node.getName()))
			clearElement(node);
		
		// added by yk 05.10.2005
		node.setName("many-to-one");
		// added by yk 05.10.2005.


		// name
		mergeAttribute(node, "name", property.getName());
		mergeAttribute(node, "access", "property".equals(property
				.getPropertyAccessorName()) ? null : property
				.getPropertyAccessorName());
		
		// TODO add "class" attribute.
		
		// <!ATTLIST many-to-one class CDATA #IMPLIED>
		// <!ATTLIST many-to-one entity-name CDATA #IMPLIED>
		processEntityName(node,mapping.getReferencedEntityName());

		// <!ATTLIST many-to-one cascade CDATA #IMPLIED>
		mergeAttribute(node, "cascade", property.getCascade());

		// <!ATTLIST many-to-one outer-join (true|false|auto) #IMPLIED>
		// <!ATTLIST many-to-one fetch (join|select) #IMPLIED>
		processFetchAttribute(node, mapping.getFetchMode(), "select");

		// <!ATTLIST many-to-one update (true|false) "true">
		mergeAttribute(node, "update", property.isUpdateable() ? null : "false");

		// <!ATTLIST many-to-one insert (true|false) "true">
		mergeAttribute(node, "insert", property.isInsertable() ? null : "false");

		// <!ATTLIST many-to-one optimistic-lock (true|false) "true"> <!-- only
		// supported for properties of a class (not component) -->
		mergeAttribute(node, "optimistic-lock",
				property.isOptimisticLocked() ? null : "false");

		// <!ATTLIST many-to-one foreign-key CDATA #IMPLIED>
		mergeAttribute(node, "foreign-key", mapping.getForeignKeyName());

		// <!ATTLIST many-to-one property-ref CDATA #IMPLIED>
		mergeAttribute(node, "property-ref", mapping.getReferencedPropertyName());

		// <!ATTLIST many-to-one lazy (true|false|proxy) #IMPLIED>
		String fetch = mapping.getFetchMode();
// added by yk 05.07.2005
//		mergeAttribute(node, "lazy", "join".equals(fetch) ? null : getToOneLazyString((PropertyMapping)property) );
// added by yk 05.07.2005 stop
//akuzmin 06.07.2005
		if ("join".equals(fetch))
			mergeAttribute(node, "lazy","false");		
		else	
			mergeAttribute(node, "lazy", "proxy".equals(getToOneLazyString((PropertyMapping)property)) ? null : getToOneLazyString((PropertyMapping)property) );		
/* rem by yk 05.07.2005
		mergeAttribute(node, "lazy", "join".equals(fetch) ? null : String
				.valueOf(property.isLazy()));
*/
		// <!ATTLIST many-to-one not-found (exception|ignore) "exception">
		mergeAttribute(node, "not-found", mapping.isIgnoreNotFound() ? "ignore"	: null);

		//<!ATTLIST many-to-one not-null (true|false) #IMPLIED>
		mergeAttribute(node, "not-null", !mapping.isNullable() ? null : "false");
		//<!ATTLIST many-to-one unique (true|false) "false">
		
		// FORMULA
		/* rem by yk 23.09.2005 mergeElement(node, "formula", mapping.getFormula()); */
		// added by yk 23.09.2005
		mergeElementMultilineCDATA(node, "formula", mapping.getFormula());
		// added by yk 23.09.2005.

		// COLUMN
		processColumns(node, mapping.getColumnIterator(), /* rem by yk 26.09.2005 2 */ mapping.getColumnSpan(), false);

		reorderElements(node, MANYTOONE_ELEMENTS);
/* rem by yk 05.10.2005		node.setName("many-to-one"); */
		return null;
	}

// added by yk 05.07.2005
	private String getToOneLazyString(PropertyMapping pm)
	{
		String lazy = pm.getToOneLazy();
		return ( "proxy".equals(lazy) ) ? null : lazy; 
	}
// added by yk 05.07.2005 stop
	
	// changed 12.05.05 by KVictor
	public Object visitOneToOneMapping(IOneToOneMapping mapping, Object argument) {
		// <!ELEMENT one-to-one (meta*|formula*)>
		MappingContext ctx = (MappingContext) argument;
		Element node = ctx.node;
		IPropertyMapping property = ctx.property;

		// <!ATTLIST one-to-one name CDATA #REQUIRED>
		mergeAttribute(node, "name", property.getName());

		// <!ATTLIST one-to-one access CDATA #IMPLIED>
		mergeAttribute(node, "access", "property".equals(property
				.getPropertyAccessorName()) ? null : property
				.getPropertyAccessorName());

		// <!ATTLIST one-to-one class CDATA #IMPLIED>
		// <!ATTLIST one-to-one entity-name CDATA #IMPLIED>
		processEntityName(node,mapping.getReferencedEntityName());

		// <!ATTLIST one-to-one cascade CDATA #IMPLIED>
		mergeAttribute(node, "cascade", property.getCascade());

		// <!ATTLIST one-to-one outer-join (true|false|auto) #IMPLIED>
		// <!ATTLIST one-to-one fetch (join|select) #IMPLIED>
		processFetchAttribute(node, mapping.getFetchMode(), "select");

		// <!ATTLIST one-to-one constrained (true|false) "false">
		mergeAttribute(node, "constrained", mapping.isConstrained() ? "true"
				: null);

		// <!ATTLIST one-to-one foreign-key CDATA #IMPLIED>
		mergeAttribute(node, "foreign-key", mapping.getForeignKeyName());

		// <!ATTLIST one-to-one property-ref CDATA #IMPLIED>
		mergeAttribute(node, "property-ref", mapping
				.getReferencedPropertyName());

		// <!ATTLIST one-to-one lazy (true|false|proxy) #IMPLIED>
		String fetch = mapping.getFetchMode();
//akuzmin 06.07.2005		
		if ("join".equals(fetch))
			mergeAttribute(node, "lazy","false");		
		else	
			mergeAttribute(node, "lazy", "proxy".equals(getToOneLazyString((PropertyMapping)property)) ? null : getToOneLazyString((PropertyMapping)property) );		
		// FORMULA
		/* rem by yk 23.09.2005 mergeElement(node, "formula", mapping.getFormula()); */
		// added by yk 23.09.2005
		mergeElementMultilineCDATA(node, "formula", mapping.getFormula());
		// added by yk 23.09.2005.

		reorderElements(node, ONETOONE_ELEMENTS);
		node.setName("one-to-one");
		return null;
	}

	public Object visitJoinMapping(IJoinMapping mapping, Object argument) {

		return null;
	}

	/*
	 * ------------- helper methods ---------------------
	 */

	private void clearElement(Element node){
		node.clearContent();
		node.attributes().clear();
	}

	// ------------- end helpers --------------

	// CUSTOM SQL for a collection mapping
	private void processCustomSQL(Element node, ICollectionMapping collection) {

		removeElements(node, "loader");
		removeElements(node, "sql-insert");
		removeElements(node, "sql-update");
		removeElements(node, "sql-delete");
		removeElements(node, "sql-delete-all");
		
		if (collection.getLoaderName() != null && collection.getLoaderName().length() > 0) {
			node.addElement("loader").addAttribute("query-ref",
					collection.getLoaderName());
		} 

		String sql = collection.getCustomSQLInsert();
		boolean callable = collection.isCustomInsertCallable();

		if (sql != null && sql.length() > 0) {
			Element sqlNode = node.addElement("sql-insert");
			/* rem by yk 04.10.2005 sqlNode.setText(sql); */
			// added by yk 04.10.2005
			mergeElementMultilineCDATA(node, "sql-insert", sql);
			// added by yk 04.10.2005.

			if (callable)
				sqlNode.addAttribute("callable", "true");
		} 

		sql = collection.getCustomSQLUpdate();
		callable = collection.isCustomUpdateCallable();

		if (sql != null && sql.length() > 0) {
			Element sqlNode = node.addElement("sql-update");
			/* rem by yk 04.10.2005 sqlNode.setText(sql); */
			// added by yk 04.10.2005
			mergeElementMultilineCDATA(node, "sql-update", sql);
			// added by yk 04.10.2005.

			if (callable)
				sqlNode.addAttribute("callable", "true");
		} 

		sql = collection.getCustomSQLDelete();
		callable = collection.isCustomDeleteCallable();

		if (sql != null && sql.length() > 0) {
			Element sqlNode = node.addElement("sql-delete");
			/* rem by yk 04.10.2005  sqlNode.setText(sql); */ 
			// added by yk 04.10.2005
			mergeElementMultilineCDATA(node, "sql-delete", sql);
			// added by yk 04.10.2005.

			if (callable)
				sqlNode.addAttribute("callable", "true");
		} 

		sql = collection.getCustomSQLDeleteAll();
		callable = collection.isCustomDeleteAllCallable();

		if (sql != null && sql.length() > 0) {
			Element sqlNode = node.addElement("sql-delete-all");
			/* rem by yk 04.10.2005  sqlNode.setText(sql); */
			// added by yk 04.10.2005
			mergeElementMultilineCDATA(node, "sql-delete-all", sql);
			// added by yk 04.10.2005.

			if (callable)
				sqlNode.addAttribute("callable", "true");
		} 
	}

	// CUSTOM SQL for class mapping
	private void processCustomSQL(Element node,
			IHibernateClassMapping persistentClass) {

		removeElements(node, "loader");
		removeElements(node, "sql-insert");
		removeElements(node, "sql-update");
		removeElements(node, "sql-delete");
		
		if (persistentClass.getLoaderName() != null && persistentClass.getLoaderName().length() > 0) {
			node.addElement("loader").addAttribute("query-ref",
					persistentClass.getLoaderName());
		} 

		String sql = persistentClass.getCustomSQLInsert();
		boolean callable = persistentClass.isCustomInsertCallable();

		if (sql != null && sql.length() > 0) {
			Element sqlNode = node.addElement("sql-insert");
			/* rem by yk 04.10.2005 sqlNode.setText(sql); */
			// added by yk 04.10.2005
			mergeElementMultilineCDATA(node, "sql-insert", sql);
			// added by yk 04.10.2005.

			if (callable)
				sqlNode.addAttribute("callable", "true");
		} 

		sql = persistentClass.getCustomSQLUpdate();
		callable = persistentClass.isCustomUpdateCallable();

		if (sql != null && sql.length() > 0) {
			Element sqlNode = node.addElement("sql-update");
			/* rem by yk 04.10.2005 sqlNode.setText(sql); */
			// added by yk 04.10.2005
			mergeElementMultilineCDATA(node, "sql-update", sql);
			// added by yk 04.10.2005.
			
			if (callable)
				sqlNode.addAttribute("callable", "true");
		} 

		sql = persistentClass.getCustomSQLDelete();
		callable = persistentClass.isCustomDeleteCallable();

		if (sql != null && sql.length() > 0) {
			Element sqlNode = node.addElement("sql-delete");
			/* rem by yk 04.10.2005 sqlNode.setText(sql); */
			// added by yk 04.10.2005
			mergeElementMultilineCDATA(node, "sql-delete", sql);
			// added by yk 04.10.2005.

			if (callable)
				sqlNode.addAttribute("callable", "true");
		} 

	}

	// CUSTOM SQL for join mapping
	private void processCustomSQL(Element node, IJoinMapping join) {

		removeElements(node, "sql-insert");
		removeElements(node, "sql-update");
		removeElements(node, "sql-delete");
		
		String sql = join.getCustomSQLInsert();
		boolean callable = join.isCustomInsertCallable();

		if (sql != null && sql.length() > 0) {
			Element sqlNode = node.addElement("sql-insert");
			/* rem by yk 04.10.2005 sqlNode.setText(sql); */
			// added by yk 04.10.2005
			mergeElementMultilineCDATA(node, "sql-insert", sql);
			// added by yk 04.10.2005.

			if (callable)
				sqlNode.addAttribute("callable", "true");
		} 

		sql = join.getCustomSQLUpdate();
		callable = join.isCustomUpdateCallable();

		if (sql != null && sql.length() > 0) {
			Element sqlNode = node.addElement("sql-update");
			/* rem by yk 04.10.2005 sqlNode.setText(sql); */
			// added by yk 04.10.2005
			mergeElementMultilineCDATA(node, "sql-update", sql);
			// added by yk 04.10.2005.

			if (callable)
				sqlNode.addAttribute("callable", "true");
		} 

		sql = join.getCustomSQLDelete();
		callable = join.isCustomDeleteCallable();

		if (sql != null && sql.length() > 0) {
			Element sqlNode = node.addElement("sql-delete");
			/* rem by yk 04.10.2005 sqlNode.setText(sql); */
			// added by yk 04.10.2005
			mergeElementMultilineCDATA(node, "sql-delete", sql);
			// added by yk 04.10.2005.

			if (callable)
				sqlNode.addAttribute("callable", "true");
		} 

	}

	
	// SYNCHRONIZE
	private void processSynchronize(Element node,
			IHibernateClassMapping persistentClass) {
		processSynchronize(node, persistentClass.getSynchronizedTables());
	}

	private void processSynchronize(Element node, ICollectionMapping collection) {
		processSynchronize(node, collection.getSynchronizedTables());
	}

	private void processSynchronize(Element node, String syncTables) {
		Iterator tables = node.elementIterator("synchronize");
		while (tables.hasNext()) {
			Element elem = (Element) tables.next();
			node.remove(elem);
		}
		if (syncTables != null) {
			StringTokenizer st = new StringTokenizer(syncTables, ",");
			while (st.hasMoreTokens()) {
				Element elem = node.addElement("synchronize");
				elem.addAttribute("table", st.nextToken());
			}
		}
	}

	// CACHE
	private void processCache(Element node, IRootClassMapping persistentClass) {
		processCache(node, persistentClass.getCacheConcurrencyStrategy(),
				persistentClass.getCacheRegionName());
	}

	private void processCache(Element node, ICollectionMapping collection) {
		processCache(node, collection.getCacheConcurrencyStrategy(), collection
				.getCacheRegionName());
	}

	private void processCache(Element node, String strategy, String region) {
		Element elem = node.element("cache");
		if (strategy == null) {
			if (elem != null)
				node.remove(elem);
		} else {
			if (elem == null)
				elem = node.addElement("cache");
			mergeAttribute(elem, "usage", strategy);
			mergeAttribute(elem, "region", region);
		}
	}

	private void processColumn(Element node, IDatabaseColumn column, boolean isKey) {
		String nodeName=node.getName();
		if ("column".equals(nodeName)) {
	
			mergeAttribute(node, "name", column.getName());
			mergeAttribute(node, "not-null", !column.isNullable() ? "true": null);
			mergeAttribute(node, "unique", column.isUnique() ? "true": null);
			
			IDatabaseTable owner=column.getOwnerTable();
			if(owner!=null){
				mergeAttribute(node, "unique-key", owner.getUniqueKeyName(column.getName()));
				mergeAttribute(node, "index", owner.getIndexName(column.getName()));
			}

			setTypeAndParameters(node,column);
			mergeAttribute(node, "check", column.getCheckConstraint());
			
		} else if ("key-property".equals(nodeName)) {
			mergeAttribute(node, "column", column.getName());
			mergeAttribute(node, "length",
					column.getLength()<= Column.DEFAULT_LENGTH ? null : String
							.valueOf(column.getLength()));
		} else if ("property".equals(nodeName)){
			
			mergeAttribute(node, "column", column.getName());
			mergeAttribute(node, "length",
					column.getLength()<= Column.DEFAULT_LENGTH ? null : String
							.valueOf(column.getLength()));
			mergeAttribute(node, "precision",
					column.getPrecision()<= Column.DEFAULT_PRECISION ? null
							: String.valueOf(column.getPrecision()));
			mergeAttribute(node, "scale",
					column.getScale()<= Column.DEFAULT_SCALE ? null : String
							.valueOf(column.getScale()));
			// <!ATTLIST property not-null (true|false) "false">
			mergeAttribute(node, "not-null", column.isNullable() ? null: "true");
			// <!ATTLIST property unique (true|false) "false">
			mergeAttribute(node, "unique", column.isUnique() ? "true" : null);
			IDatabaseTable owner=column.getOwnerTable();
			if(owner!=null){
				mergeAttribute(node, "index", owner.getIndexName(column.getName()));
			}
		}
		// added by yk 30.09.2005
		else if("id".equals(nodeName))
		{
			 mergeAttribute(node, "column", column.getName());
				mergeAttribute(node, "length",
						column.getLength()<= Column.DEFAULT_LENGTH ? null : String
								.valueOf(column.getLength()));
		}
		// added by yk 30.09.2005.
		else{
			 mergeAttribute(node, "column", column.getName());
		}
	}

// added by yk 29.08.2005
	private void setTypeAndParameters(Element node, IDatabaseColumn column)
	{
		String columntypename = column.getSqlTypeName();
		String params = "";
/*		if("null".equalsIgnoreCase(columntypename))
		{
            mergeAttribute(node, "sql-type", null);
			setLengthPrecisionScale(node,column, true);
			return;
		}
*/		if(column.isNativeType() && columntypename.length() != 0 /*getSqlTypeCode() == 0 || !TypeUtils.isColumnSQLTypeReproducible(column)) */)
		{
			int length 		= column.getLength();
			int precision 	= column.getPrecision();;
			int scale 		= column.getScale();
			String defstring = "0";
            if(column.getLength() > /* changed by Nick 02.09.2005*/ Column.DEFAULT_LENGTH) params += /*(length >= 0) ? */String.valueOf(length)/* : defstring*//*String.valueOf(Column.DEFAULT_LENGTH)*/;
            if(params.length() == 0 && 
              ( (column.getPrecision() > /* changed by Nick 02.09.2005*/ Column.DEFAULT_PRECISION) || column.getScale() > /* changed by Nick 02.09.2005*/ Column.DEFAULT_SCALE) )
			{
//				if(column.getScale() != Column.DEFAULT_SCALE)
//				{
					params += (precision > Column.DEFAULT_PRECISION) ? String.valueOf(column.getPrecision()) : String.valueOf(column.getScale() > 0 ? column.getScale() : 1)/*defstring*//*String.valueOf(Column.DEFAULT_PRECISION)*/;
					params += ",";
					params += (scale > Column.DEFAULT_SCALE) ? String.valueOf(column.getScale()) : defstring/*String.valueOf(Column.DEFAULT_SCALE)*/;
//				}
//				else
//					params += (precision >= 0) ? String.valueOf(column.getPrecision()) : defstring /*String.valueOf(Column.DEFAULT_PRECISION)*/;
			}
            
            if(params.length() > 0)
            {
                if(columntypename.indexOf("(") != -1)
                    columntypename = columntypename.substring(0, columntypename.indexOf("("));
                columntypename += "(" + params + ")";
            }
            mergeAttribute(node, "sql-type", columntypename);
            setLengthPrecisionScale(node,column, true);
        }
		else
		{
            mergeAttribute(node, "sql-type", null);/* changed by Nick 1.09.2005 TypeUtils.isColumnSQLTypeReproducible(column) ? null : column.getSqlTypeName()); */
            setLengthPrecisionScale(node,column, false);
		}
	}
	private void setLengthPrecisionScale(Element node, IDatabaseColumn column, boolean erase)
	{
        mergeAttribute(node, "length",
                column.getLength() 
                    // changed by Nick 02.09.2005
                    /* == Column.DEFAULT_LENGTH*/ <= Column.DEFAULT_LENGTH ? null : (erase) ? null : String.valueOf(column.getLength()));
        mergeAttribute(node, "precision",
                column.getPrecision() 
                // changed by Nick 02.09.2005
                /* == Column.DEFAULT_PRECISION*/ <= Column.DEFAULT_PRECISION ? null : (erase) ? null : String.valueOf(column.getPrecision()));
        mergeAttribute(node, "scale",
                column.getScale()
                // changed by Nick 02.09.2005
                /*== Column.DEFAULT_SCALE*/ <= Column.DEFAULT_SCALE ? null : (erase) ? null: String.valueOf(column.getScale()));
	}
	private void processColumns(Element node, Iterator columnsIterator,
			int colCount, boolean isKey) {
		// Note:
		// 1. if column is a single then put it into attributes
		// but note that key and id nodes may contain only column and length
		// attributes
		// property node may contain all column attributes
		// 2. if columnsIterator contains more than one column then clear column
		// attributes and
		// merge column elements
		IDatabaseColumn column = null;
		if (colCount == 1) { // SINGLE
			column = (IDatabaseColumn) columnsIterator.next();
			// #added# by Konstantin Mishin on 13.01.2006 fixed for ESORM-447
			if(column.getPersistentValueMapping() instanceof ManyToManyMapping &&
					column.getName().equals(OrmConfiguration.DEFAULT_ELEMENT_COLUMN_NAME) && !column.isNullable())
			{
				column.setNullable(true);
				if(column.isDefaultValues()){
					removeElements(node, "column");
					mergeAttribute(node, "column", null);
					mergeAttribute(node, "length", null);
					mergeAttribute(node, "precision", null);
					mergeAttribute(node, "scale", null);
					mergeAttribute(node, "not-null", null);
					mergeAttribute(node, "unique", null);
					mergeAttribute(node, "index", null);
					column.setNullable(false);
					return;
				}
				column.setNullable(false);
			}
			// #added#
			/* rem by yk 26.09.2005  if(column.isDefaultValues()) */
			if(!column.isNativeType() && rwhelper.isColumnCompatibleForEasyWriting(node, column))
			{
				removeElements(node, "column");
				processColumn(node, column, isKey);
				return;
			}
			
		} 
		Iterator it = node.elementIterator("column");
		HashMap<String,Node> columnElems = new HashMap<String,Node>();
		while (it.hasNext()) {
			Element elem = (Element) it.next();
			columnElems.put(elem.attributeValue("name"), elem.detach());
		}
		mergeAttribute(node, "column", null);
		mergeAttribute(node, "length", null);
		mergeAttribute(node, "precision", null);
		mergeAttribute(node, "scale", null);
		mergeAttribute(node, "not-null", null);
		mergeAttribute(node, "unique", null);
		mergeAttribute(node, "index", null);

		while (columnsIterator.hasNext() || column!=null){
			if(columnsIterator.hasNext())column = (IDatabaseColumn) columnsIterator.next();
			Element elem = (Element) columnElems.get(column.getName());
			if (elem == null)	elem = node.addElement("column");
			else node.add(elem);
			processColumn(elem, column, isKey);
			column=null;
		}
	}

	// added 4.05.05 by KVictor
	private void processGenerator(Element node, ISimpleValueMapping simple) {
		String strategy = simple.getIdentifierGeneratorStrategy();
		if (strategy != null) {
			Element element = node.addElement("generator");
			element.addAttribute("class", strategy);
			Properties props = simple.getIdentifierGeneratorProperties();
			if (props == null || props.size() == 0)
				return;
			Iterator it = props.keySet().iterator();
			while (it.hasNext()) {
				String name = (String) it.next();
				String value = props.getProperty(name);
				Element param = element.addElement("param");
				param.addAttribute("name", name);
				param.setText(value);
			}
		}
	}

	// changed 4.05.05 by KVictor
	private void processSimpleId(Element node, IPropertyMapping property,
			ISimpleValueMapping simple) {
		clearElement(node);
		if (property != null) {
			mergeAttribute(node, "name", property.getName());
			mergeAttribute(node, "access", "property".equals(property
					.getPropertyAccessorName()) ? null : property
					.getPropertyAccessorName());
		}
		Type type = simple.getType();
		if (type != null) {
			mergeAttribute(node, "type", type.getName());
		}
		mergeAttribute(node, "unsaved-value", simple.getNullValue());

		// COLUMN
		int colCount = simple.getColumnSpan();
		processColumns(node, simple.getColumnIterator(), colCount, true);

		// GENERATOR
		// --
		processGenerator(node, simple);
		// --
		node.setName("id");
	}

	// added 6.05.05 by KVictor
	private void processKeyProp(Element node, IPropertyMapping prop) {
		mergeAttribute(node, "name", prop.getName());
		mergeAttribute(node, "access", "property".equals(prop
				.getPropertyAccessorName()) ? null : prop
				.getPropertyAccessorName());
		IHibernateValueMapping vm = prop.getValue();
		Type type = vm.getType();
		if (type != null) {
			mergeAttribute(node, "type", type.getName());
		}
		// COLUMN
		int colCount = vm.getColumnSpan();
		processColumns(node, vm.getColumnIterator(), colCount, true);
	}

	// added 6.05.05 by KVictor
	private void processKeyManyToOneProp(Element node, IPropertyMapping prop) {
		mergeAttribute(node, "name", prop.getName());
		mergeAttribute(node, "access", "property".equals(prop
				.getPropertyAccessorName()) ? null : prop
				.getPropertyAccessorName());
		IManyToOneMapping vm = (IManyToOneMapping) prop.getValue();
		processEntityName(node,vm.getReferencedEntityName());
		mergeAttribute(node, "foreign-key", vm.getForeignKeyName());
		// COLUMN
		int colCount = vm.getColumnSpan();
		processColumns(node, vm.getColumnIterator(), colCount, true);
	}

	// changed 6.05.05 by KVictor
	private void processCompositeId(Element node, IPropertyMapping property,
			IComponentMapping component) {
		clearElement(node);
		// --
		if (property != null) {
			mergeAttribute(node, "name", property.getName());
			mergeAttribute(node, "access", "property".equals(property
					.getPropertyAccessorName()) ? null : property
					.getPropertyAccessorName());
		}
		

		mergeAttribute(node, "unsaved-value", component.getNullValue());

		// added 6.05.05 by KVictor --
		Iterator iterator = component.getPropertyIterator();
		while (iterator.hasNext()) {
			IPropertyMapping prop = (IPropertyMapping) iterator.next();
			IHibernateValueMapping vm = prop.getValue();
			if (vm instanceof IManyToOneMapping) {
				Element elem = node.addElement("key-many-to-one");
				processKeyManyToOneProp(elem, prop);
			} else if (vm instanceof ISimpleValueMapping) {
				Element elem = node.addElement("key-property");
				processKeyProp(elem, prop);
			}
		}
		node.setName("composite-id");
	}

	private void processId(Element node, IRootClassMapping persistentClass) {
		Element elem = node.element("id");
		if (elem == null)
			elem = node.element("composite-id");
		IHibernateKeyMapping mapping = persistentClass.getIdentifier();
		if (mapping != null) {
			if (elem == null)
				elem = node.addElement("id");
			if (mapping.isSimpleValue())
				processSimpleId(elem, persistentClass.getIdentifierProperty(),
						(ISimpleValueMapping) mapping);
			else
				processCompositeId(elem, persistentClass
						.getIdentifierProperty(), (IComponentMapping) mapping);
		} else {
			if (elem != null)
				node.remove(elem);
		}
	}

	private void processKey(Element node, ICollectionMapping collection) {
		processKey(node, collection.getKey(), collection.getReferencedPropertyName());
	}

	private void processKey(Element node, IJoinMapping join) {
		processKey(node, join.getKey(), null);
	}
	
	private void processKey(Element node, IJoinedSubclassMapping persistentClass) {
		processKey(node, persistentClass.getKey(), null);
	}

	private void processKey(Element node, IHibernateKeyMapping mapping, String refpropertyname) {
		Element elem = node.element("key");
		if (elem == null)
			elem = node.addElement("key");
		if (mapping != null) {
			// added by yk 04.10.2005
			//	<!ATTLIST key property-ref CDATA #IMPLIED>
			mergeAttribute(elem, "property-ref", refpropertyname);
			// added by yk 04.10.2005.

			
			// <!ATTLIST key foreign-key CDATA #IMPLIED>
			mergeAttribute(elem, "foreign-key", mapping.getForeignKeyName());
			// <!ATTLIST key on-delete (cascade|noaction) "noaction">
			mergeAttribute(elem, "on-delete",
					mapping.isCascadeDeleteEnabled() ? "cascade" : null);
			
			// <!ATTLIST key update (true|false) #IMPLIED>
			mergeAttribute(elem, "update", mapping.isUpdateable()? null: "false");
			//<!ATTLIST key not-null (true|false) #IMPLIED>
			mergeAttribute(elem, "not-null", mapping.isNullable()? null: "true");
			
			// <!ATTLIST key column CDATA #IMPLIED>
			int colCount = mapping.getColumnSpan();
			processColumns(elem, mapping.getColumnIterator(), colCount, true);
		}
	}

	private void processJoinProperties(Element node, IJoinMapping join) {
		// from dtd: (property|many-to-one|component|dynamic-component|any)*
		Iterator it = node.elementIterator();
		Map<String,Node> elements = new HashMap<String,Node>();
		// collect property elements
		while (it.hasNext()) {
			Element elem = (Element) it.next();
			String elemName = elem.getName();
			if (JOIN_PROPERTIES.contains(elemName)) {
				String name = elem.attributeValue("name", null);
				if (name != null)
					elements.put(name, elem.detach());
				else
					elem.detach();
			}
		}
		MappingContext ctx = new MappingContext();
		// merge elements with existing properties
		it = join.getPropertyIterator();
		while (it.hasNext()) {
			IPropertyMapping mapping = (IPropertyMapping) it.next();
			if (mapping.getValue() != null) {
				Element elem = (Element) elements.get(mapping.getName());
				if (elem == null)
					elem = node.addElement("property");
				else
					node.add(elem);
				ctx.node = elem;
				ctx.property = mapping;
				mapping.getValue().accept(this, ctx);
			}
		}
	}

	
	
	// changed 11.05.05 by KVictor
	private void processProperties(Element node,
			IHibernateClassMapping persistentClass) {
		// from dtd:
		// (property|many-to-one|one-to-one|component|dynamic-component|properties|any|map|set|list|bag|idbag|array|primitive-array|query-list)
		// TODO In next version: add support for elements: properties |
		// query-list
		
		Iterator it = node.elementIterator();
		Map elements = new SequencedHashMap();
		// collect property elements
		while (it.hasNext()) {
			Element elem = (Element) it.next();
			String elemName = elem.getName();
			if (CLASS_PROPERTIES.contains(elemName)) {
				String name = elem.attributeValue("name", null);
				if (name != null)
					elements.put(name, elem.detach());
				else
					elem.detach();
			}
		}
		MappingContext ctx = new MappingContext();
		// merge elements with existing properties
		it = persistentClass.getUnjoinedPropertyIterator();
		while (it.hasNext()) {
			IPropertyMapping mapping = (IPropertyMapping) it.next();
			if (persistentClass.getIdentifierProperty() == mapping ||
					persistentClass.getVersion() == mapping )
				continue; // do not process id & version properties
			if (mapping.getValue() != null) {
				Element elem = (Element) elements.remove(mapping.getName());

// added by yk 26.08.2005
				if(mapping.isNaturalID()) continue; // do not process natural-id;
// added by yk 26.08.2005.

				if (elem == null)
					elem = node.addElement("property");
				else
					node.add(elem);
				ctx.node = elem;
				ctx.property = mapping;
				mapping.getValue().accept(this, ctx);
			}
		}
	}

	// changed 11.05.05 by KVictor
	private void processDiscriminator(Element node,
			IRootClassMapping persistentClass) {
		Element elem = node.element("discriminator");

		ISimpleValueMapping mapping = (ISimpleValueMapping) persistentClass
				.getDiscriminator();
		if (mapping != null) {
			if (elem == null)
				elem = node.addElement("discriminator");
			/* rem by yk 23.09.2005 mergeElement(elem, "formula", mapping.getFormula()); */
			// added by yk 23.09.2005
			mergeElementMultilineCDATA(node, "formula", mapping.getFormula());
			// added by yk 23.09.2005.
			// column
			int colCount = mapping.getColumnSpan();
			processColumns(elem, mapping.getColumnIterator(), colCount, true);

			Type type = mapping.getType();
			if (type != null) {
				mergeAttribute(elem, "type", type.getName());
			}

			mergeAttribute(elem, "not-null", !mapping.isNullable() ? "true"
					: null);
			mergeAttribute(elem, "force", persistentClass
					.isForceDiscriminator() ? "true" : null);
			
			mergeAttribute(elem, "insert", !persistentClass
					.isDiscriminatorInsertable() ? "false" : null);
		} else if (elem != null)
			node.remove(elem);
	}

	// changed 5.05.05 by KVictor
	private void processVersion(Element node, IRootClassMapping persistentClass) {
		// from dtd:
		// (version|timestamp)?
		Element elem = node.element("version");
		if (elem == null)
			elem = node.element("timestamp");
		// Note that <timestamp> is equivalent to <version type="timestamp">
		IPropertyMapping property = persistentClass.getVersion();

		if (property != null && property.getValue()!=null) {
			ISimpleValueMapping value = (ISimpleValueMapping) property
					.getValue();
			if (elem == null)
				elem = node.addElement("version");
			else
				elem.setName("version");
			mergeAttribute(elem, "name", property.getName());
			mergeAttribute(elem, "access", "property".equals(property
					.getPropertyAccessorName()) ? null : property
					.getPropertyAccessorName());
			Type type = value.getType();
			String tempval;
			if (type != null) {
// added by yk 05.07.2005
				tempval = type.getName();
				mergeAttribute(elem, "type", ("integer".equals(tempval)) ? null : tempval );
// added by yk 05.07.2005 stop
			}
// added by yk 05.07.2005
			tempval = value.getNullValue();
// added by yk 05.07.2005 stop
			mergeAttribute(elem, "unsaved-value", ("undefined".equals(tempval)) ? null : tempval);
			
			if (value.getColumnSpan() > 0)
				mergeAttribute(elem, "column", ((IDatabaseColumn) value
						.getColumnIterator().next()).getName());

		} else {
			if (elem != null)
				node.remove(elem);
		}
	}

	private void processDatabaseTable(Element node, IDatabaseTable table){
		// TABLE
		
		if(table == null){
			mergeAttribute(node, "table", null);
			mergeAttribute(node, "schema", null);
			mergeAttribute(node, "catalog", null);
			return;
		}
		mergeAttribute(node, "table", table.getShortName());
		
		// SCHEMA
		String defSchema = storage.getSchemaName();
		String schema = table.getSchema().getShortName();
		if (defSchema == null || defSchema.length() == 0){
            storage.setSchemaName(schema);
            defSchema=schema;
		}
		else if (defSchema.equals(schema))
			schema = null;
		
		mergeAttribute(node, "schema", schema);

		// CATALOG
		String defCat = storage.getCatalogName();
		String catalog = table.getSchema()
				.getCatalog();
		if (defCat == null || defCat.length() == 0){
			storage.setCatalogName(catalog);
			defCat=catalog;
		}
		else if (defCat.equals(catalog))
			catalog = null;
		
		mergeAttribute(node, "catalog", catalog);
	}
	
	private void processNaturalID(Element node, IHibernateClassMapping mapping)
	{
		final String 	subnodename 	= "natural-id";
		Element 		naturalid_node 	= node.element(subnodename);

        if(naturalid_node == null) 
        {// if "natural-id" not exist in the node.          TODO to change the behaviour;
            return;
        }

        if(naturalid_node.elements() == null || naturalid_node.elements().size() < 1)
			{		node.remove(naturalid_node); return;	}

        Map<String,Node> elements = new HashMap<String,Node>();
		getElementsAndDetach(naturalid_node, elements);
		mappingNodeElements(naturalid_node, elements, mapping);
		
		if(naturalid_node.elements().size() < 1)
		{			node.remove(naturalid_node);			}
	}

	/**
	 * collect elements of the node 
	 * @param node 		node which will processed
	 * @param container keep collection of elements
	 */
	private void getElementsAndDetach(Element node, Map<String,Node> container)
	{
		Iterator elements 	= node.elementIterator();
		while(elements.hasNext())
		{
			Element element = (Element)elements.next();
			String name = element.attributeValue("name");
			if(name != null)
				container.put(name, element.detach());
			else
				element.detach();
		}
	}
	
	/**
	 * 
	 * @param node		node which will mapped
	 * @param container elements of the node 
	 * @param mapping   owner class mapping
	 */
	private void mappingNodeElements(Element node, Map container, IHibernateClassMapping mapping)
	{
		MappingContext 	ctx 			= new MappingContext();
		Iterator 		properties 	= mapping.getPropertyIterator();
		while(properties.hasNext())
		{
			IPropertyMapping ipm = (IPropertyMapping) properties.next();
			if (mapping.getIdentifierProperty() == ipm ||
					mapping.getVersion() == ipm )
				continue;
			if (ipm.getValue() != null) 
			{
				Element elem = (Element) container.remove(ipm.getName());
				if (elem != null)
				{
					IHibernateValueMapping value = ipm.getValue(); 
					if(value instanceof IAnyMapping 		||
					   value instanceof IComponentMapping 	||
					   value instanceof ManyToOneMapping  	||
					   value instanceof SimpleValueMapping	)
					{
					node.add(elem);
					acceptPropertyMapping(elem, ipm,ctx);
					ipm.setNaturalID(true);
					//properties.remove();
					}
				}
			}
		}
	}

	private void acceptPropertyMapping(Element element, IPropertyMapping ipm, MappingContext ctx)
	{
		ctx.node = element;
		ctx.property = ipm;
		ipm.getValue().accept(this, ctx);
	}
	 private void processJoin(Element node, IHibernateClassMapping persistentClass){
		//<!ELEMENT join (subselect?,key,(property|many-to-one|component|dynamic-component|any)*,sql-insert?,sql-update?,sql-delete?)>
		Iterator it = node.elementIterator("join");
		Map<String,Node> elements = new HashMap<String,Node>();
		// collect join elements
		while (it.hasNext()) {
			Element elem = (Element) it.next();
			String table = elem.attributeValue("table", null);
			if (table != null)
				elements.put(table, elem.detach());
			else
				elem.detach();
		}
		//merge joins
		it=persistentClass.getJoinIterator();
		while(it.hasNext()){
			IJoinMapping join=(IJoinMapping)it.next();
			String table=join.getTable().getShortName();
			Element elem = (Element) elements.get(table);
			if(elem==null) elem=node.addElement("join");
			else node.add(elem);
	
			//<!ATTLIST join table CDATA #REQUIRED>
			//<!ATTLIST join schema CDATA #IMPLIED>						<!-- default: none -->
			//<!ATTLIST join catalog CDATA #IMPLIED>						<!-- default: none -->
			if(join.getSubselect() == null || "".equals(persistentClass.getSubselect()))
				processDatabaseTable(elem, join.getTable());
			else
			{		clearTablesAttributes(node);		}
			//<!ATTLIST join fetch (join|select) "join">
			mergeAttribute(elem, "fetch", join.isSequentialSelect() ? "select" : null);
			//<!ATTLIST join inverse (true|false) "false">
			mergeAttribute(elem, "inverse", join.isInverse() ? "true" : null);
			//<!ATTLIST join optional (true|false) "false">
			mergeAttribute(elem, "optional", join.isOptional() ? "true" : null);
			// <!ATTLIST join subselect CDATA #IMPLIED>
			/* rem by yk 27.09.2005 mergeElement(elem, "subselect", join.getSubselect()); */
			mergeElementMultilineCDATA(node, "subselect", join.getSubselect());
			// KEY
			processKey(elem, join);
			//properties
			processJoinProperties(elem, join);
			// CUSTOM SQL:
			// sql-insert?,sql-update?,sql-delete?
			processCustomSQL(elem, join);
			// it is required for ordering elements as defined in dtd
			reorderElements(node, JOIN_ELEMENTS);
		}
	 }

	/**
	 * merges common attributes of all class mappings
	 */
	private void processClass(Element node, IHibernateClassMapping persistentClass) {
		String className = persistentClass.getClassName();
		String entityName = persistentClass.getEntityName();
		String packName = persistentClass.getPersistentClass().getPackage().getName();
		// ENTITY-NAME
		if (!className.equals(entityName)) {
			node.addAttribute("entity-name", entityName);
		}
		if (storage.getDefaultPackage() == null
				|| storage.getDefaultPackage().length() == 0)
			storage.setDefaultPackage(packName);
		if (packName.equals(storage.getDefaultPackage()))
			className = persistentClass.getPersistentClass().getShortName();
		// NAME
		node.addAttribute("name", className);

		// PROXY INTERFACE
		String proxyIface = persistentClass.getProxyInterfaceName();
		String defProxyClassName = persistentClass.isDynamic() ? java.util.Map.class
				.getName()
				: persistentClass.getClassName();
		mergeAttribute(node, "proxy", proxyIface != null
				&& !proxyIface.equals(defProxyClassName) ? proxyIface : null);

		// DYNAMIC UPDATE
		mergeAttribute(node, "dynamic-update", persistentClass
				.getDynamicUpdate() ? "true" : null);

		// DYNAMIC INSERT
		mergeAttribute(node, "dynamic-insert", persistentClass
				.getDynamicInsert() ? "true" : null);

		// SELECT BEFORE UPDATE
		mergeAttribute(node, "select-before-update", persistentClass
				.getSelectBeforeUpdate() ? "true" : null);

		// LAZY
		boolean lazyClass = persistentClass.isLazy();
		mergeAttribute(node, "lazy",
				lazyClass == storage.isDefaultLazy() ? null : String
						.valueOf(lazyClass));

		// ABSTRACT
		mergeAttribute(node, "abstract",
				persistentClass.getIsAbstract() ? "true" : null);

		// PERSISTER
		mergeAttribute(node, "persister", persistentClass
				.getPersisterClassName());

		// MUTABLE
		// <!ATTLIST class mutable (true|false) "true"> hibernate3.0.dtd;
		mergeAttribute(node, "mutable", persistentClass.isMutable() ? null : "false");
		
		// BATCH SIZE
		mergeAttribute(node, "batch-size",
				persistentClass.getBatchSize() != 1 ? String
						.valueOf(persistentClass.getBatchSize()) : null);

	}

	// changed 2.05.05 by KVictor
	public Object visitRootClassMapping(IRootClassMapping persistentClass, Object argument) {
		Element node = (Element) argument;
		if (!"class".equals(node.getName()))
			clearElement(node); // can't merge with a node of different type

		collectComments(node, CLASS_ELEMENTS, "name");
		processClass(node, persistentClass); // common attributes

		// META
		
		//TABLE, CATALOG, SCHEMA
		if(persistentClass.getSubselect() == null || "".equals(persistentClass.getSubselect()))
			processDatabaseTable(node, persistentClass.getDatabaseTable());
		else
		{		clearTablesAttributes(node);		}

		processNaturalID(node, persistentClass);

		// SUBSELECT
		/* rem by yk 22.09.2005  mergeElement(node, "subselect", persistentClass.getSubselect()); */
		mergeElementMultilineCDATA(node, "subselect", persistentClass.getSubselect());

		// DISCRIMINATOR
		String defDisriminator = persistentClass.getEntityName();
		String discriminator = persistentClass.getDiscriminatorValue();
		mergeAttribute(node, "discriminator-value", defDisriminator != null
				&& defDisriminator.equals(discriminator) ? null : discriminator);

		// MUTABLE
		mergeAttribute(node, "mutable", persistentClass.isMutable() ? null
				: "false");

		// POLYMORPHISM
		mergeAttribute(node, "polymorphism", persistentClass
				.isExplicitPolymorphism() ? "explicit" : null);

		// WHERE
		mergeAttribute(node, "where", persistentClass.getWhere());

		// OPTIMISTIC LOCK MODE
		String olMode = persistentClass.getOptimisticLockMode();
		mergeAttribute(node, "optimistic-lock", OrmConfiguration.CHECK_VERSION
				.equals(olMode) ? null : olMode);

		// CHECK
		mergeAttribute(node, "check", persistentClass.getCheck());

		// ROW ID
		mergeAttribute(node, "rowid", persistentClass.getRowId());

		// CACHE
		processCache(node, persistentClass);

		// SYNCHRONIZE
		processSynchronize(node, persistentClass);

		// ID
		processId(node, persistentClass);

		// DISCRIMINATOR
		processDiscriminator(node, persistentClass);

		// VERSION
		processVersion(node, persistentClass);

		// PROPERTIES
		processProperties(node, persistentClass);

		// JOIN
		processJoin(node, persistentClass);

		// SUBCLASS
		removeElements(node, "subclass"); // all subclasses will be written in
											// hibernate-mapping element
		removeElements(node, "joined-subclass"); // all subclasses will be
													// written in
													// hibernate-mapping element
		removeElements(node, "union-subclass"); // all subclasses will be
												// written in hibernate-mapping
												// element

		// CUSTOM SQL
		processCustomSQL(node, persistentClass);

		// FILTER
		// TODO FILTER

		// it is required for ordering elements as defined in dtd
		reorderElements(node, CLASS_ELEMENTS);
		node.setName("class");
		return null;
	}

	// added 28.04.05 by KVictor
	private void processSubclass(Element node, ISubclassMapping persistentClass) {

		processClass(node, persistentClass); // common attributes

		// DISCRIMINATOR
		String defDisriminator = persistentClass.getEntityName();
		String discriminator = persistentClass.getDiscriminatorValue();
		mergeAttribute(node, "discriminator-value", defDisriminator != null
				&& defDisriminator.equals(discriminator) ? null : discriminator);

		// EXTENDSCLASS
		mergeAttribute(node, "extends", persistentClass.getSuperclass()
				.getName());

		// PROPERTIES ETC
		processProperties(node, persistentClass);

		// JOIN
		processJoin(node, persistentClass);

		// SUBCLASS
		removeElements(node, "subclass"); // all subclasses will be written in
											// hibernate-mapping element

		// CUSTOM SQL
		processCustomSQL(node, persistentClass);

		// SYNCHRONIZE
		processSynchronize(node, persistentClass);

	}

	// changed 28.04.05 by KVictor
	public Object visitSubclassMapping(ISubclassMapping mapping, Object argument) {

		Element node = (Element) argument;
		collectComments(node, SUBCLASS_ELEMENTS, "name");
		if (!"subclass".equals(node.getName()))
			clearElement(node); // can't merge with a node of different type
		processSubclass(node, mapping);
		reorderElements(node, SUBCLASS_ELEMENTS);
		node.setName("subclass");
		return null;
	}

	// added 28.04.05 by KVictor
	private void processJoinedSubclass(Element node, IJoinedSubclassMapping persistentClass) {

		processClass(node, persistentClass); // common attributes

		//TABLE, CATALOG, SCHEMA
		if(persistentClass == null || "".equals(persistentClass.getSubselect()))
			processDatabaseTable(node, persistentClass.getDatabaseTable());
		else
		{		clearTablesAttributes(node);		}
		
		// TABLE
		mergeAttribute(node, "table", persistentClass.getDatabaseTable().getName());
		
		// EXTENDSCLASS
		mergeAttribute(node, "extends", persistentClass.getSuperclass()
				.getName());

		// CHECK
		mergeAttribute(node, "check", persistentClass.getCheck());

		// SUBSELECT
		/* rem by yk 27.09.2005  mergeElement(node, "subselect", persistentClass.getSubselect()); */
		mergeElementMultilineCDATA(node, "subselect", persistentClass.getSubselect());

		// SYNCHRONIZE
		processSynchronize(node, persistentClass);

		// KEY
		processKey(node, persistentClass);

		// PROPERTIES ETC
		processProperties(node, persistentClass);

		// JOINED-SUBCLASS
		removeElements(node, "joined-subclass"); // all subclasses will be
													// written in
													// hibernate-mapping element

		// CUSTOM SQL
		processCustomSQL(node, persistentClass);
	}

	// changed 28.04.05 by KVictor
	public Object visitJoinedSubclassMapping(IJoinedSubclassMapping mapping, Object argument) {
		Element node = (Element) argument;
		collectComments(node, JOINEDSUBCLASS_ELEMENTS, "name");
		if (!"joined-subclass".equals(node.getName()))
			clearElement(node); // can't merge with a node of different type
		processJoinedSubclass(node, mapping);
		reorderElements(node, JOINEDSUBCLASS_ELEMENTS);
		node.setName("joined-subclass");
		return null;
	}

	// added 28.04.05 by KVictor
	private void processUniondSubclass(Element node, IUnionSubclassMapping persistentClass) {
		processClass(node, persistentClass); // common attributes

		// TABLE
		// SCHEMA
		// CATALOG
		if(persistentClass.getSubselect() == null || "".equals(persistentClass.getSubselect()))
			processDatabaseTable(node, persistentClass.getDatabaseTable());
		else
		{		clearTablesAttributes(node);		}

		// EXTENDSCLASS
		mergeAttribute(node, "extends", persistentClass.getSuperclass()
				.getName());

		// CHECK
		mergeAttribute(node, "check", persistentClass.getCheck());

		// SUBSELECT
		/* rem by yk 27.09.2005mergeElement(node, "subselect", persistentClass.getSubselect()); */
		mergeElementMultilineCDATA(node, "subselect", persistentClass.getSubselect());

		// SYNCHRONIZE
		processSynchronize(node, persistentClass);

		// PROPERTIES ETC
		processProperties(node, persistentClass);

		// UNION-SUBCLASS
		removeElements(node, "union-subclass"); // all subclasses will be
												// written in hibernate-mapping
												// element

		// CUSTOM SQL
		processCustomSQL(node, persistentClass);
	}

	// changed 28.04.05 by KVictor
	public Object visitUnionSubclassMapping(IUnionSubclassMapping mapping,
			Object argument) {
		Element node = (Element) argument;
		collectComments(node, UNIONSUBCLASS_ELEMENTS, "name");
		if (!"union-subclass".equals(node.getName()))
			clearElement(node); // can't merge with a node of different type
		processUniondSubclass(node, mapping);
		reorderElements(node, UNIONSUBCLASS_ELEMENTS);
		node.setName("union-subclass");
		return null;
	}

	public Object visitPropertyMapping(IPropertyMapping mapping, Object argument) {
		return null;
	}

	public Object visitOrmProject(IOrmProject project, Object argument) {
		return null;
	}

	public Object visitDatabaseSchema(IDatabaseSchema schema, Object argument) {
		return null;
	}

	public Object visitDatabaseTable(IDatabaseTable table, Object argument) {
		return null;
	}

	public Object visitDatabaseColumn(IDatabaseColumn column, Object argument) {
		return null;
	}

	public Object visitDatabaseConstraint(IDatabaseConstraint constraint, Object argument) {
		return null;
	}

	public Object visitPackage(IPackage pakage, Object argument) {
		return null;
	}

	public Object visitMapping(IMapping mapping, Object argument) {
		return null;
	}

	public Object visitMappingStorage(IMappingStorage storage, Object argument) {
		return null;
	}

	public Object visitPersistentClass(IPersistentClass clazz, Object argument) {
		return null;
	}

	public Object visitPersistentField(IPersistentField field, Object argument) {
		return null;
	}

	public Object visitPersistentClassMapping(IPersistentClassMapping mapping, Object argument) {
		return null;
	}

	public Object visitPersistentFieldMapping(IPersistentFieldMapping mapping, Object argument) {
		return null;
	}

	public Object visitPersistentValueMapping(IPersistentValueMapping mapping, Object argument) {
		return null;
	}

	// add tau 27.07.2005
	public Object visitNamedQueryMapping(INamedQueryMapping mapping, Object argument) {
		return null;
	}

	public Object visitDatabaseColumn(org.hibernate.mapping.Column column, Object argument) {
		// TODO Auto-generated method stub
		return null;
	}

}
