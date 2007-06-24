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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Attribute;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IDatabaseConstraint;
import org.jboss.tools.hibernate.core.IDatabaseTable;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.IPersistentField;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateValueMapping;
import org.jboss.tools.hibernate.core.hibernate.Type;
import org.jboss.tools.hibernate.internal.core.BaseResourceReaderWriter;
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;
import org.jboss.tools.hibernate.internal.core.PersistentField;
import org.jboss.tools.hibernate.internal.core.data.Column;
import org.jboss.tools.hibernate.internal.core.data.ForeignKey;
import org.jboss.tools.hibernate.internal.core.data.PrimaryKey;
import org.jboss.tools.hibernate.internal.core.data.Table;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.HibernateAutoMapping;
import org.jboss.tools.hibernate.internal.core.hibernate.query.NamedQueryDefinition;
import org.jboss.tools.hibernate.internal.core.util.ClassUtils;
import org.jboss.tools.hibernate.internal.core.util.StringUtils;
import org.jboss.tools.hibernate.internal.core.util.TypeUtils;


/**
 * @author troyas
 *
 */
public class XMLFileReader extends BaseResourceReaderWriter{
	
	private IResource resource;
	private XMLFileStorage storage;
	private HibernateConfiguration config;
	private HibernateMapping hbMapping;
	
	public static final int MAPPING_OK = 0;
	public static final int MAPPING_ERROR = -1;
	
	public XMLFileReader(XMLFileStorage storage, IResource resource, HibernateConfiguration config){
		this.resource = resource;
		this.storage = storage;
		this.config = config;
		hbMapping=config.getHibernateMapping();
	}

	private boolean isFileAccessible()
	{
		boolean accessible = false;
		if(resource != null && resource.getType()==IResource.FILE)
		{
			if( ((IFile)resource).isAccessible() )
				accessible = true;
		}
		return accessible;
	}
	
	public boolean canReadFile()
	{
		boolean canread = false;
		if(isFileAccessible())
		{
			try
			{
				readDocument(((IFile)resource).getContents(true));
				canread = true;
			}
			catch(Exception exc)
			{	canread = false;}
		}
		return canread;
	}
	public void reload() throws CoreException, IOException, DocumentException{
		if(isFileAccessible())
		{
			InputStream content = ((IFile)resource).getContents(true);
			org.dom4j.Document doc = readDocument(content);
			rootBinder(doc, storage, Collections.EMPTY_MAP);
			if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) ExceptionHandler.logInfo(">> mapping loaded " + resource.getFullPath());
		}
	}

	private void rootBinder(org.dom4j.Document doc, XMLFileStorage storage, Map inheritedMetas){
		Element root = doc.getRootElement();
		inheritedMetas = getMetas(root, inheritedMetas, true);
		Attribute schemaNode = root.attribute("schema");
		storage.setSchemaName((schemaNode == null ) ? null : schemaNode.getValue());
		Attribute catalogNode = root.attribute("catalog");
		storage.setCatalogName((catalogNode == null ) ? null : catalogNode.getValue());
		Attribute dcNode = root.attribute("default-cascade" );
		storage.setDefaultCascade((dcNode == null) ? "none" : dcNode.getValue());
		Attribute daNode = root.attribute("default-access");
		storage.setDefaultAccess((daNode == null) ? "property" : daNode.getValue());
		Attribute dlNode = root.attribute("default-lazy");
		storage.setDefaultLazy(dlNode == null || dlNode.getValue().equals("true"));
		Attribute aiNode = root.attribute("auto-import");
		storage.setAutoImport((aiNode == null ) ? true : "true".equals( aiNode.getValue()));
		Attribute packNode = root.attribute("package");
		storage.setDefaultPackage((packNode==null) ? "" : packNode.getValue());

		handleTypeDefs(root, storage);
		handleImports(root, storage);
		handleClasses(root, storage, inheritedMetas);
		handleDynamicClasses(root, storage, inheritedMetas);
		handleSubclasses(root, storage, inheritedMetas, null);
		handleJoinedSubclasses(root, storage, inheritedMetas, null);
		handleUnionSubclasses(root, storage, inheritedMetas, null);
		handleQueres(root, storage);
		//handleSqlQueres(root, storage);	//skipped yet (by troyas)
		handleFilterDefs(root, storage);
	}
	
	private void handleQueres(Element node, XMLFileStorage storage) {
		Iterator queryNodes = node.elementIterator("query");
		while(queryNodes.hasNext()){
			Element subnode = (Element) queryNodes.next();
			handleQuery(subnode, storage);
		}
	}

	private void handleQuery(Element subnode, XMLFileStorage storage) {
		String qname = subnode.attributeValue( "name" );
		String query = subnode.getText();

		boolean cacheable = "true".equals( subnode.attributeValue( "cacheable" ) );
		String region = subnode.attributeValue( "cache-region" );
		Attribute tAtt = subnode.attribute( "timeout" );
		Integer timeout = tAtt == null ? null : new Integer( tAtt.getValue() );
		Attribute fsAtt = subnode.attribute( "fetch-size" );
		Integer fetchSize = fsAtt == null ? null : new Integer( fsAtt.getValue() );
		String flushMode = subnode.attributeValue( "flush-mode" );

		// edit tau - add storage in parametr for new NamedQueryDefinition(storage,...
		// 20.10.2005
		NamedQueryDefinition namedQuery = new NamedQueryDefinition(storage, qname,query,
				cacheable, region, timeout, fetchSize, flushMode);

		storage.addQuery( qname, namedQuery );
	}

	private void handleImports(Element node, XMLFileStorage storage) {
		Iterator importNodes = node.elementIterator("import");
		while(importNodes.hasNext()){
			Element subnode = (Element) importNodes.next();
			handleImport(subnode, storage);
		}
	}

	private void handleImport(Element subnode, XMLFileStorage storage) {
		String className = getClassName(subnode.attribute("class"));
		Attribute renameNode = subnode.attribute("rename");
		String rename = (renameNode==null) ? ClassUtils.getUnqualifyName(className) : renameNode.getValue();
		storage.addImport(className, rename);
	}

	private void handleUnionSubclasses(Element node, XMLFileStorage storage, Map inheritedMetas, ClassMapping superClass) {
		Iterator unionsubclassnodes = node.elementIterator( "union-subclass" );
		while ( unionsubclassnodes.hasNext() ) {
			Element subnode = ( Element ) unionsubclassnodes.next();
			handleUnionSubclass(superClass, storage, subnode, inheritedMetas);
		}
	}

	private void handleUnionSubclass(ClassMapping superClass, XMLFileStorage storage, Element subnode, Map inheritedMetas) {
		UnionSubclassMapping subclass = new UnionSubclassMapping( superClass );
		if(superClass==null) superClass = getSuperclass(subclass, subnode);
		if(superClass!=null){
			superClass.addSubclass( subclass );
			subclass.setSuperclass(superClass);
		}
		bindUnionSubclass( subnode, subclass, storage, inheritedMetas );
		storage.addPersistentClassMapping(subclass);//putClassMapping( subclass );
	}

	private void bindUnionSubclass(Element node, UnionSubclassMapping unionSubclass, XMLFileStorage storage, Map inheritedMetas) {
		bindClass(node, unionSubclass, storage, inheritedMetas);
		inheritedMetas = getMetas(node, inheritedMetas, true);

		Attribute schemaNode = node.attribute( "schema" );
		String schema = schemaNode == null ? storage.getSchemaName() : schemaNode.getValue();

		Attribute catalogNode = node.attribute( "catalog" );
		String catalog = catalogNode == null ? storage.getCatalogName() : catalogNode.getValue();
		
		IDatabaseTable table = storage.getTable(schema, catalog, getClassTableName(unionSubclass, node, storage));
		unionSubclass.setDatabaseTable(table);

		createClassProperties( node, unionSubclass, storage, inheritedMetas, null, true );
		
	}

	private void handleJoinedSubclasses(Element node, XMLFileStorage storage, Map inheritedMetas, ClassMapping superClass) {
		Iterator joinedsubclassnodes = node.elementIterator( "joined-subclass" );
		while ( joinedsubclassnodes.hasNext() ) {
			Element subnode = ( Element ) joinedsubclassnodes.next();
			handleJoinedSubclass( superClass, storage, subnode, inheritedMetas );
		}
	}

	private void handleJoinedSubclass(ClassMapping superClass, XMLFileStorage storage, Element subnode, Map inheritedMetas) {
		JoinedSubclassMapping subclass = new JoinedSubclassMapping( superClass );
		if(superClass==null) superClass = getSuperclass(subclass, subnode);
		if(superClass!=null){
			superClass.addSubclass( subclass );
			subclass.setSuperclass(superClass);
		}
		bindJoinedSubclass( subnode, subclass, storage, inheritedMetas );
		storage.addPersistentClassMapping(subclass);
	}

	private void bindJoinedSubclass(Element node, JoinedSubclassMapping subclass, XMLFileStorage storage, Map inheritedMetas) {

		bindClass(node, subclass, storage, inheritedMetas);
		inheritedMetas = getMetas(node, inheritedMetas, true);
		//SCHEMA
		Attribute schemaNode = node.attribute("schema");
		String schema = schemaNode == null ? storage.getSchemaName()
												: schemaNode.getValue();

		//CATALOG
		Attribute catalogNode = node.attribute("catalog");
		String catalog = catalogNode == null ? storage.getCatalogName()
												: catalogNode.getValue();

		//SUBSELECT
		subclass.setSubselect(getMultileneCDATA(node, "subselect"));

		//TABLE
		IDatabaseTable table = storage.getTable(schema, catalog, getClassTableName(subclass, node, storage));
		subclass.setDatabaseTable(table);

		//KEY
		Element keyNode = node.element( "key" );
		
		//IHibernateKeyMapping pk = subclass.getIdentifier();
		SimpleValueMapping key = new SimpleValueMapping(table);
		subclass.setKey( key );
		if(keyNode!=null){
			key.setCascadeDeleteEnabled( "cascade".equals( keyNode.attributeValue( "on-delete" ) ) );
			bindSimpleValue( keyNode, key, false, subclass.getEntityName(), storage);
		}

		//CHECK
		Attribute chNode = node.attribute( "check" );
		
		createPrimaryKey((Table)table, subclass);
		createForeignKey((Table)table, subclass);
		
		createClassProperties(node, subclass, storage, inheritedMetas, null, true);
		
	}

	private void handleSubclasses(Element node, XMLFileStorage storage, 
								  Map inheritedMetas, ClassMapping superClass) {
		Iterator subclassnodes = node.elementIterator( "subclass" );
			while ( subclassnodes.hasNext() ) {
				Element subnode = (Element)subclassnodes.next();
				handleSubclass(superClass, storage, subnode, inheritedMetas);
			}
	}

	private void handleSubclass(ClassMapping superClass, XMLFileStorage storage,
								Element subnode, java.util.Map inheritedMetas) {
		SubclassMapping subclass = new SubclassMapping(superClass);
		if(superClass==null) superClass = getSuperclass(subclass, subnode);
		if(superClass!=null){
			superClass.addSubclass(subclass);
			subclass.setSuperclass(superClass);
		}
		bindSubclass(subnode, subclass, storage, inheritedMetas);
		storage.addPersistentClassMapping(subclass);
	}

	public void bindSubclass(Element node, SubclassMapping subclass,
			XMLFileStorage storage, java.util.Map inheritedMetas) {

		bindClass(node, subclass, storage, inheritedMetas);
		inheritedMetas = getMetas(node, inheritedMetas, true); // get meta's from <subclass>
		createClassProperties(node, subclass, storage, inheritedMetas, null, true);
	}

	protected void createClassProperties(Element node,
												ClassMapping classMapping,
												XMLFileStorage storage,
												java.util.Map inheritedMetas, 
												IDatabaseConstraint uniquekey, 	// uniquekey which consist of property; 
												boolean mutable					// updateable sign for a property;
												)	
	{
		String 			path 		= classMapping.getEntityName();
		IDatabaseTable 	table 		= classMapping.getDatabaseTable();
		Iterator iter = node.elementIterator();
		while(iter.hasNext()){
			Element subnode = (Element)iter.next();
			String name = subnode.getName();
			String propertyName = subnode.attributeValue("name");
			
			if(!validateTypeOfNaturalIDSubnode(node, subnode)) continue;
			
			IHibernateValueMapping value = null;
			if ( "many-to-one".equals( name ) ) {
				if(propertyName==null) continue; // skip tag without name
				value = new ManyToOneMapping( table );
				bindManyToOne( subnode, ( ManyToOneMapping ) value, propertyName, true, storage );
			}
			else if ( "any".equals( name ) ) {
				if(propertyName==null) continue; // skip tag without name
				value = new AnyMapping( table );
				if(XMLFileReader.MAPPING_ERROR == bindAny( subnode, ( AnyMapping ) value, true, storage ))
				{
					value = null;
				}
			}
			else if ( "one-to-one".equals( name ) ) {
				if(propertyName==null) continue; // skip tag without name
				OneToOneMapping oneToOne = new OneToOneMapping( table, classMapping.getKey() );
				bindOneToOne( subnode, oneToOne, true, storage );
				value = oneToOne;
			}
			else if ( "property".equals( name ) ) {
				if(propertyName==null) continue; // skip tag without name
				value = new SimpleValueMapping( table );
				bindSimpleValue( subnode, (SimpleValueMapping)value, true, propertyName, storage);
			}
			else if ( "component".equals( name ) || "dynamic-component".equals( name ) || "properties".equals( name ) ) {
				if(propertyName==null) continue; // skip tag without name
				String subpath = ClassUtils.qualify( path, propertyName );
				value = new ComponentMapping(classMapping);
				bindComponent(subnode,
							(ComponentMapping)value,
							classMapping.getClassName(),
							propertyName,
							subpath,
							true,
							"properties".equals( name ),
							storage,
							inheritedMetas,
							"properties".equals( name )				// is component a "properties"
							);
				if("properties".equals( name ))
				{	((ComponentMapping)value).setProperties_component(true);		}
			}
			else if ( "query-list".equals( name ) ) {
//				value = new QueryList( table );
//				( ( QueryList ) value ).setQueryName( subnode.attributeValue( "query-ref" ) );
			}
			else if ( "join".equals( name ) ) {
				JoinMapping join = new JoinMapping();
				join.setPersistentClass( classMapping );
				bindJoin( subnode, join, storage, inheritedMetas );
				classMapping.addJoin( join );
			}
			else if ( "subclass".equals( name ) ) {
				handleSubclass( classMapping, storage, subnode, inheritedMetas );
			}
			else if ( "joined-subclass".equals( name ) ) {
				handleJoinedSubclass( classMapping, storage, subnode, inheritedMetas );
			}
			else if ( "union-subclass".equals( name ) ) {
				handleUnionSubclass( classMapping, storage, subnode, inheritedMetas );
			}
			else if ( "filter".equals( name ) ) {
				parseFilter( subnode, classMapping );
			}
			else if ("natural-id".equals(name))
			{
				String mtb = subnode.attributeValue("mutable");
				IDatabaseConstraint u_key = table.createUniqueKey(null);// create unique name w/o columns;
				createClassProperties(subnode,classMapping, storage,inheritedMetas, u_key, (mtb != null && "true".equals(mtb)) );
			}
			else
			{
				if(propertyName==null) continue; // skip tag without name
				value = handleCollections(classMapping, subnode, propertyName);				
			}

			if ( value != null ) 
			{
				PropertyMapping pm = createProperty(classMapping, value, propertyName,
													classMapping.getClassName(), classMapping.isDynamic(),
													subnode, storage, inheritedMetas);
				// set updateable
				if(!mutable)pm.setUpdateable(false);
				classMapping.addProperty(pm);
				
				// set columns for unique key;
				if(uniquekey != null)
				{
					Iterator it_pm_columns = pm.getColumnIterator();
					setColumnsForUniqueKey(uniquekey, it_pm_columns);
				}
			}
		}
		if(table != null){
			table.addPersistentClassMapping(classMapping);
		}
	}

	/**
	 * validate type of subnode of the "natural-id" node.
	 * @param node	"natural-id" node .
	 * @param subnode subnode of the "natural-id" node. 
	 * @return true if type of subnode is allowed for the "natural-id" node. 
	 */
	private boolean validateTypeOfNaturalIDSubnode(Element node, Element subnode)
	{
		boolean right_type = false;
		if("natural-id".equals(node.getName()))
		{// for "natural-id" node;
			if("property".equals(subnode.getName())  || "many-to-one".equals(subnode.getName()) ||
			   "component".equals(subnode.getName()) || "dynamic-component".equals(subnode.getName()) ||
			   "any".equals(subnode.getName()))
			{	right_type = true;}
		}
		else
		{// for other nodes;
			right_type = true;
		}
		return right_type;
	}
	
	private void setColumnsForUniqueKey(IDatabaseConstraint uniquekey, Iterator it_columns)
	{
		if(it_columns == null) 		return;
		
		while(it_columns.hasNext())
		{		uniquekey.addColumn((IDatabaseColumn)it_columns.next());				}
	}
	private CollectionMapping handleCollections(IHibernateClassMapping owner, Element node, String path){
		CollectionMapping cm=null;
		String name = node.getName();
		if("map".equals(name)) {
			cm=new MapMapping(owner);
			if(bindMap(node, (MapMapping)cm, owner.getClassName(), path) == null)
				cm = null;
		}
		if("set".equals(name)){
			cm=new SetMapping(owner);
			if(bindCollection(node, cm, owner.getClassName(), path) == null)
				cm = null;
		}
		if("list".equals(name)) {
			cm= new ListMapping(owner);
			if(bindList(node,(ListMapping) cm, owner.getClassName(), path) == null)
				cm = null;
			
		}
		if("bag".equals(name)) {
			cm=new BagMapping(owner);
			if(bindCollection(node, cm, owner.getClassName(), path) == null)
				cm = null;
		}
		if("idbag".equals(name)) {
			cm= new IdBagMapping(owner);
			if(bindIdBag(node, (IdBagMapping) cm, owner.getClassName(), path) == null)
				cm = null;
		}
		if("array".equals(name)) {
			cm=new ArrayMapping(owner);
			if(bindArray(node, (ArrayMapping)cm, owner.getClassName(), path) == null)
				cm = null;
		}
		if("primitive-array".equals(name)){
			cm=new PrimitiveArrayMapping(owner);
			if(bindArray(node, (ArrayMapping)cm, owner.getClassName(), path) == null)
				cm = null;
		}
		return cm;
		
	}
	private Object bindArray(Element node, ArrayMapping array, String className, String path)  
	{
		if(bindList( node, array, className, path ) == null)
			return null;

		Attribute att = node.attribute( "element-class" );
		if ( att != null ) array.setElementClassName( getClassName( att ) );
		return array;
	}
	/**
	 * For idbags
	 * @return TODO
	 * */
	private  Object bindIdBag(Element node,IdBagMapping collection, String className, String path) {

		if(bindCollection( node, collection, className, path ) == null)
			return null;

		Element subnode = node.element( "collection-id" );
		SimpleValueMapping id = new SimpleValueMapping( collection.getCollectionTable() );
		bindSimpleValue(
			subnode,
			id,
			false,
			OrmConfiguration.DEFAULT_IDENTIFIER_COLUMN_NAME,
			storage );
		collection.setIdentifier( id );
		makeIdentifier( subnode, id, storage );
		return collection;
	}
	
	/**
	 * Called for Lists, arrays, primitive arrays
	 * @return TODO
	 */
	private Object bindList(Element node, ListMapping list, String className, String path) {

		if(bindCollection( node, list, className, path ) == null)
			return null;

		Element subnode = node.element( "list-index" );
		if ( subnode == null ) subnode = node.element( "index" );
		SimpleValueMapping iv = new SimpleValueMapping( list.getCollectionTable() );
		bindSimpleValue(
			subnode,
			iv,
			list.isOneToMany(),
			OrmConfiguration.DEFAULT_INDEX_COLUMN_NAME,
			storage );
		iv.setTypeName( "integer" );
		list.setIndex( iv );
		String baseIndex = subnode.attributeValue( "base" );
		if ( baseIndex != null ) list.setBaseIndex( Integer.parseInt( baseIndex ) );
		//list.setIndexNodeName( subnode.attributeValue("node") );
		return list;
	}

	/**
	 * Called for Maps
	 * @return TODO
	 */
	private Object bindMap(Element node, MapMapping map, String className, String path) {

		if(bindCollection( node, map, className, path ) == null)
			return null;

		Iterator iter = node.elementIterator();
		while ( iter.hasNext() ) {
			Element subnode = (Element) iter.next();
			String name = subnode.getName();

			if ( "index".equals( name ) || "map-key".equals( name ) ) {
				SimpleValueMapping value = new SimpleValueMapping( map.getCollectionTable() );
				bindSimpleValue(
					subnode,
					value,
					map.isOneToMany(),
					OrmConfiguration.DEFAULT_INDEX_COLUMN_NAME,
					storage );
				map.setIndex( value );
				//map.setIndexNodeName( subnode.attributeValue("node") );
			}
			else if ( "index-many-to-many".equals( name ) || "map-key-many-to-many".equals( name ) ) {
				ManyToManyMapping mto = new ManyToManyMapping( map.getCollectionTable() );
				bindManyToOne( subnode, mto, OrmConfiguration.DEFAULT_INDEX_COLUMN_NAME, map.isOneToMany(), storage );
				map.setIndex( mto );

			}
			else if ( "composite-index".equals( name ) || "composite-map-key".equals( name ) ) {
				ComponentMapping component = new ComponentMapping( map );
				bindComposite(
					subnode,
					component,
					map.getRole() + ".index",
					map.isOneToMany(),
					storage,
					null );
				map.setIndex( component );
			}
			else if ( "index-many-to-any".equals( name ) ) {
				ManyToAnyMapping any = new ManyToAnyMapping( map.getCollectionTable() );
				map.setIndex( any );
				if(XMLFileReader.MAPPING_ERROR == bindAny( subnode, any, map.isOneToMany(), storage ))
				{
					return null;
				}
			}
		}
		return map;
	}
	
	
	private Object bindCollection(Element node, CollectionMapping collection, String className,String path) {

		String subpath = ClassUtils.qualify( className, path );
		// ROLENAME
		collection.setRole( subpath );

		Attribute inverseNode = node.attribute( "inverse" );
		if ( inverseNode != null ) {
			collection.setInverse( "true".equals( inverseNode.getValue() ) );
		}

		Attribute olNode = node.attribute( "optimistic-lock" );
		collection.setOptimisticLocked( olNode == null || "true".equals( olNode.getValue() ) );

		Attribute orderNode = node.attribute( "order-by" );
		if ( orderNode != null ) {
			collection.setOrderBy( orderNode.getValue() );
		}
		Attribute whereNode = node.attribute( "where" );
		if ( whereNode != null ) {
			collection.setWhere( whereNode.getValue() );
		}
		Attribute batchNode = node.attribute( "batch-size" );
		if ( batchNode != null ) {
			collection.setBatchSize( Integer.parseInt( batchNode.getValue() ) );
		}

		/* TODO support node and embed-xml
		String nodeName = node.attributeValue( "node" );
		if ( nodeName == null ) nodeName = node.attributeValue( "name" );
		collection.setNodeName( nodeName );
		String embed = node.attributeValue( "embed-xml" );
		collection.setEmbedded( embed==null || "true".equals(embed) );
		*/

		// PERSISTER
		Attribute persisterNode = node.attribute( "persister" );
		if ( persisterNode != null ) {
			/* rem by yk 28.06.2005collection.setCollectionPersisterClass( persisterNode.getValue()); */
// added by yk 28.06.2005
			collection.setPersister(persisterNode.getValue());
		}
//		 added by yk 28.06.2005 stop


		Attribute typeNode = node.attribute( "collection-type" );
		if ( typeNode != null ) collection.setTypeName( typeNode.getValue() );

		Attribute jfNode = node.attribute( "outer-join" );
		if ( jfNode != null ) {
			if("auto".equals(jfNode.getValue())) collection.setFetchMode("default");
			else if("true".equals(jfNode.getValue()))collection.setFetchMode("join");
			else collection.setFetchMode("select");
		}
		Attribute ftNode = node.attribute( "fetch" );
		if(ftNode!=null){
			collection.setFetchMode(ftNode.getValue());
		}
		//SUBSELECT
		collection.setSubselect(getMultileneCDATA(node, "subselect"));
		
		Element oneToManyNode = node.element( "one-to-many" );
		if ( oneToManyNode != null ) {
			OneToManyMapping oneToMany = new OneToManyMapping( collection.getOwner() );
			collection.setElement( oneToMany );
			bindOneToMany( oneToManyNode, oneToMany );
			String refEntity=oneToMany.getReferencedEntityName();
			IPersistentClassMapping cm=storage.getPersistentClassMapping(refEntity);
			if(cm!=null) collection.setCollectionTable(cm.getDatabaseTable());
			else {
				collection.setCollectionTable(storage.getTempTable(oneToMany.getReferencedEntityName()));
				// XX we have to set up real table later in  OneToMany.update
			}
		}
		else {
			// TABLE
			Attribute tableNode = node.attribute( "table" );
			String tableName;
			if ( tableNode != null ) {
				//changed by Nick 19.05.2005
                tableName = tableNode.getValue();
                //tableName = HibernateAutoMapping.tableName(tableNode.getValue()); 
                //by Nick
            }
			else {
				tableName = HibernateAutoMapping.propertyToTableName( className, path );
			}
			Attribute schemaNode = node.attribute( "schema" );
			String schema = schemaNode == null ? storage.getSchemaName() : schemaNode.getValue();

			Attribute catalogNode = node.attribute( "catalog" );
			String catalog = catalogNode == null ? storage.getCatalogName() : catalogNode.getValue();

			collection.setCollectionTable( storage.getTable(schema,	catalog, tableName));

		}

		// LAZINESS
		Attribute lazyNode = node.attribute( "lazy" );
		boolean isLazyTrue = lazyNode == null ? storage.isDefaultLazy() : "true".equals( lazyNode.getValue() );
		collection.setLazy( isLazyTrue );

		// SORT
		Attribute sortedAtt = node.attribute( "sort" );
		// unsorted, natural, comparator.class.name
		if ( sortedAtt == null || sortedAtt.getValue().equals( "unsorted" ) ) {
			collection.setSort( "unsorted" );
		}
		else {
			collection.setSort( sortedAtt.getValue() );
		}

		// CASCADE 
		Attribute cascadeAtt = node.attribute( "cascade" );
		if ( cascadeAtt != null ) {
			collection.setCascade( cascadeAtt.getValue()  );
		}
		
		// CUSTOM SQL
		handleCustomSQL( node, collection );
		
/* TODO collection filter
		Iterator iter = node.elementIterator( "filter" );
		while ( iter.hasNext() ) {
			final Element filter = (Element) iter.next();
			parseFilter( filter, collection, storage );
		}

*/
		collection.setSynchronizedTables(getSynchronizedTables(node));

		Element element = node.element( "loader" );
		if ( element != null ) {
			collection.setLoaderName( element.attributeValue( "query-ref" ) );
		}

		// added by yk 04.11.2005
		Element keyelem = node.element( "key" );
		if(keyelem != null){
		// added by yk 04.11.2005.
		collection.setReferencedPropertyName(keyelem.attributeValue( "property-ref" ) );
		}

		//--------------------- from second pass ---------------------------------------

		// CHECK
		Attribute chNode = node.attribute( "check" );
		if ( chNode != null ) {
			collection.setCheck ( chNode.getValue() );
		}

		// contained elements:
		Iterator iter = node.elementIterator();
		while ( iter.hasNext() ) {
			Element subnode = (Element) iter.next();
			String name = subnode.getName();

			if ( "key".equals( name ) ) {
				SimpleValueMapping key = new SimpleValueMapping( collection.getCollectionTable() );
				key.setCascadeDeleteEnabled( "cascade".equals( subnode.attributeValue( "on-delete" ) ) );
				bindSimpleValue(
					subnode,
					key,
					collection.isOneToMany(),
					OrmConfiguration.DEFAULT_KEY_COLUMN_NAME,
					storage );
				collection.setKey( key );

				Attribute notNull = subnode.attribute( "not-null" );
				Element clm = subnode.element("column");
				notNull = (clm != null) ? clm.attribute("not-null") : null;
				key.setNullable(notNull == null || notNull.getValue().equals( "false" ));
				Attribute updateable = subnode.attribute( "update" );
				key.setUpdateable( updateable == null || updateable.getValue().equals( "true" ) );
			}
			else if ( "element".equals( name ) ) {
				SimpleValueMapping elt = new SimpleValueMapping( collection.getCollectionTable() );
				collection.setElement( elt );
				bindSimpleValue(
					subnode,
					elt,
					true,
					OrmConfiguration.DEFAULT_ELEMENT_COLUMN_NAME,
					storage );
			}
			else if ( "many-to-many".equals( name ) ) {
				ManyToManyMapping elt = new ManyToManyMapping( collection.getCollectionTable() );
				collection.setElement( elt );
				bindManyToOne(
					subnode,
					elt,
					OrmConfiguration.DEFAULT_ELEMENT_COLUMN_NAME,
					false,
					storage );
			}
			else if ( "composite-element".equals( name ) ) {
				ComponentMapping elt = new ComponentMapping( collection );
				collection.setElement( elt );
				bindComposite(
					subnode,
					elt,
					collection.getRole() + ".element",
					true,
					storage,
					null );
			}
			else if ( "many-to-any".equals( name )) {
				ManyToAnyMapping elt = new ManyToAnyMapping( collection.getCollectionTable() );
				collection.setElement( elt );
				if(XMLFileReader.MAPPING_ERROR == bindAny( subnode, elt, true, storage ))
					return null;
			}
			else if ( "cache".equals( name ) ) {
				collection.setCacheConcurrencyStrategy( subnode.attributeValue( "usage" ) );
				collection.setCacheRegionName( subnode.attributeValue( "region" ) );
			}
			/* TODO support node?
			String nodeName = subnode.attributeValue( "node" );
			if ( nodeName != null ) collection.setElementNodeName( nodeName );
			*/

		}
		return collection;

	}
	
	private void bindJoin(Element node, JoinMapping join, XMLFileStorage mappings, java.util.Map inheritedMetas)	{

		ClassMapping persistentClass = (ClassMapping) join.getPersistentClass();
		String path = persistentClass.getEntityName();

		//TABLENAME
		Attribute schemaNode = node.attribute("schema");
		String schema = schemaNode == null ? mappings.getSchemaName()
				: schemaNode.getValue();

		Attribute catalogNode = node.attribute("catalog");
		String catalog = catalogNode == null ? mappings.getCatalogName()
				: catalogNode.getValue();

		Table table = (Table) mappings.getTable(schema, catalog, 
								getClassTableName(persistentClass, node, mappings));
		join.setTable(table);

		Attribute fetchNode = node.attribute("fetch");
		if (fetchNode != null)
			join.setSequentialSelect("select".equals(fetchNode.getValue()));

		Attribute invNode = node.attribute("inverse");
		if (invNode != null)
			join.setInverse("true".equals(invNode.getValue()));

		Attribute nullNode = node.attribute("optional");
		if (nullNode != null)
			join.setOptional("true".equals(nullNode.getValue()));

		//SUBSELECT
		join.setSubselect(getMultileneCDATA(node, "subselect"));

		//KEY
		Element keyNode = node.element("key");

		//XX: Slava (Slava) Check key
		SimpleValueMapping key = new SimpleValueMapping(table);
		// added by yk 20.09.2005
		if(keyNode != null)
		{
			Attribute keyupdate = keyNode.attribute("update");
			key.setUpdateable( (keyupdate == null) ? true : "true".equals(keyupdate.getValue()) );
			
			Attribute keynotnull = keyNode.attribute("not-null");
			key.setNullable( (keynotnull == null) ? true : !"true".equals(keynotnull.getValue()) );
		// added by yk 20.09.2005.
		join.setKey(key);
		key.setCascadeDeleteEnabled("cascade".equals(keyNode.attributeValue("on-delete")));
		bindSimpleValue(keyNode, key, false, persistentClass.getEntityName(), mappings);
		}

		//PROPERTIES
		Iterator iter = node.elementIterator();
		while (iter.hasNext()) {
			Element subnode = (Element) iter.next();
			String name = subnode.getName();
			String propertyName = subnode.attributeValue("name");

			SimpleValueMapping value = null;
			if ("many-to-one".equals(name)) {
				value = new ManyToOneMapping(table);
				bindManyToOne(subnode, (ManyToOneMapping) value, propertyName, true, mappings);
			} else if ("any".equals(name)) {
				value = new AnyMapping(table);
				if(XMLFileReader.MAPPING_ERROR == bindAny(subnode, (AnyMapping) value, true, mappings))
				{
					value = null;
				}
			} else if ("property".equals(name)) {
				value = new SimpleValueMapping(table);
				bindSimpleValue(subnode, value, true, propertyName, mappings);
				//System.out.println("on join path:: " + path);
			} else if ("component".equals(name)	|| "dynamic-component".equals(name)) {
				String subpath = ClassUtils.qualify(path, propertyName);
				value = new ComponentMapping(join);
				bindComponent(subnode, (ComponentMapping) value, persistentClass.getClassName(), 
							propertyName, subpath, true, false,	mappings, inheritedMetas, false);
			}

			if (value != null) {
				PropertyMapping prop = createProperty(persistentClass, value, propertyName,
						persistentClass.getName(), persistentClass.isDynamic(), subnode, mappings, inheritedMetas);
				prop.setOptional(join.isOptional());
				join.addProperty(prop);
			}

		}

		// CUSTOM SQL
		handleCustomSQL(node, join);

	}
	
	private void parseFilter(Element filterElement, ClassMapping classMapping){
		String name = filterElement.attributeValue( "name" );
		String condition = null;
		if ( filterElement.attributeValue( "condition" ) == null ) {
			condition = filterElement.getTextTrim();
		}
		else {
			condition = filterElement.attributeValue( "condition" );
		}
		classMapping.addFilter(name, condition);
	}
	
	private ClassMapping getSuperclass(ClassMapping subClass, Element subnode)	{
		
		ClassMapping superClass = null;
		String superClassName = getClassName(subnode.attribute("extends"));
		
		if(superClassName!=null){
			superClass = (ClassMapping) storage.getPersistentClassMapping(superClassName);
			if(superClass==null)superClass = (ClassMapping) config.getClassMapping(superClassName);
		}
		subClass.setExtends(superClassName);
		
		return superClass;
	}
	
	private void handleDynamicClasses(Element root, XMLFileStorage storage, Map inheritedMetas) {
		Iterator dynanodes = root.elementIterator( "dynamic-class" );
		while(dynanodes.hasNext()){
			Element classNode = (Element)dynanodes.next();
			RootClassMapping rootClassMapping = new RootClassMapping();
			bindRootClass( classNode, rootClassMapping, storage, inheritedMetas);
			storage.addPersistentClassMapping(rootClassMapping);//putClassMapping(rootClassMapping);
		}
	}

	private void handleClasses(Element root, XMLFileStorage storage, Map inheritedMetas) {
		Iterator nodes = root.elementIterator( "class" );
		while ( nodes.hasNext() ) {
			Element classNode = (Element)nodes.next();
			RootClassMapping rootClassMapping = new RootClassMapping();
			bindRootClass( classNode, rootClassMapping, storage, inheritedMetas );
			storage.addPersistentClassMapping(rootClassMapping);
		}
	}

	private void bindRootClass(Element node, RootClassMapping persistentClass,
			XMLFileStorage storage, Map inheritedMetas) {
		
		bindClass(node, persistentClass, storage, inheritedMetas);
		inheritedMetas = getMetas(node, inheritedMetas, true); // get meta's
															   // from <class>
		//SCHEMA
		Attribute schemaNode = node.attribute("schema");
		String schema = schemaNode == null ? storage.getSchemaName()
											: schemaNode.getValue();

		//CATALOG
		Attribute catalogNode = node.attribute("catalog");
		String catalog = catalogNode == null ? storage.getCatalogName()
											  : catalogNode.getValue();

		//SUBSELECT
		persistentClass.setSubselect(getMultileneCDATA(node, "subselect"));

		//TABLE
		IDatabaseTable table = null;
		table = storage.getTable(schema, catalog, getClassTableName(
				persistentClass, node, storage));
		persistentClass.setDatabaseTable(table);

		//MUTABLE
		Attribute mutable = node.attribute("mutable");
		persistentClass.setMutable( !(mutable != null && ("false".equals(mutable.getValue()))) );
		
		//WHERE
		Attribute whereNode = node.attribute("where");
		if (whereNode != null)
			persistentClass.setWhere(whereNode.getValue());

		//CHECK
		Attribute chNode = node.attribute("check");
		if (chNode != null)
			persistentClass.setCheck(chNode.getValue());

		//POLYMORPHISM
		if (persistentClass.isDynamic()) {
			persistentClass.setExplicitPolymorphism(true);
		} else {
			Attribute polyNode = node.attribute("polymorphism");
			persistentClass.setExplicitPolymorphism((polyNode != null)
					&& polyNode.getValue().equals("explicit"));
		}
		//ROW ID
		Attribute rowidNode = node.attribute("rowid");
		if (rowidNode != null)
			persistentClass.setRowId(rowidNode.getValue());

		//Subnodes:
		Iterator subnodes = node.elementIterator();
		while (subnodes.hasNext()) {
			Element subnode = (Element) subnodes.next();
			String name = subnode.getName();
			String propertyName = subnode.attributeValue("name",null);
			if (name.equals("id")) {
				SimpleValueMapping id = new SimpleValueMapping(table);
				persistentClass.setIdentifier(id);

				if (propertyName == null) {
					bindSimpleValue(subnode, id, false,	OrmConfiguration.DEFAULT_IDENTIFIER_COLUMN_NAME, storage);
					if (!id.isTypeSpecified()) {
						ExceptionHandler.logInfo("must specify an identifier type: " + persistentClass.getEntityName());
					}
				} else {
					String className = persistentClass.getClassName();

					bindSimpleValue(subnode, id, false, propertyName, storage);

                    //by Nick 23.05.2005
                    PropertyMapping prop = createProperty(persistentClass,id,null,className,persistentClass.isDynamic(),
                            subnode,storage,inheritedMetas);
                    //by Nick
					persistentClass.setIdentifierProperty(prop);
					persistentClass.addProperty(prop);
				}
				makeIdentifier(subnode, id, storage);

			} else if (name.equals("composite-id")) {
				ComponentMapping component = new ComponentMapping(persistentClass);
				persistentClass.setIdentifier(component);
				bindCompositeId(subnode, component, persistentClass, propertyName, storage, inheritedMetas);
				if (propertyName == null) {
					//It is not necessary to create a fake property
					/*
					persistentClass.setEmbeddedIdentifier(component.isEmbedded());
					if ( component.isEmbedded() ) {
						component.setDynamic( persistentClass.isDynamic() );
						
						PropertyMapping prop = new PropertyMapping();
						prop.setName("id");
						prop.setPropertyAccessorName("embedded");
						prop.setValue(component);
						persistentClass.setIdentifierProperty(prop);
						persistentClass.addProperty(prop);
					}*/

				} else {
                    PropertyMapping prop = createProperty(persistentClass,component,null,persistentClass.getClassName(),persistentClass.isDynamic(),
                            subnode,storage,inheritedMetas);
					persistentClass.setIdentifierProperty(prop);
					persistentClass.addProperty(prop);
				}

			} else if (name.equals("version") || name.equals("timestamp")) {
				//VERSION
				SimpleValueMapping val = new SimpleValueMapping(table);
				bindSimpleValue(subnode, val, false, propertyName, storage);
				if (!val.isTypeSpecified()) {
					val.setType("version".equals(name) ? Type.getType("integer") : Type.getType("timestamp"));
					val.setTypeName(val.getType().getName());
				}
                PropertyMapping prop = createProperty(persistentClass,val,null,persistentClass.getClassName(),persistentClass.isDynamic(),
                        subnode,storage,inheritedMetas);
				makeVersion(subnode, val);
				persistentClass.setVersion(prop);
				persistentClass.addProperty(prop);

			} else if (name.equals("discriminator")) {
				//DISCRIMINATOR
				SimpleValueMapping discrim = new SimpleValueMapping(table);
				persistentClass.setDiscriminator(discrim);
				bindSimpleValue(subnode, discrim, false,
						OrmConfiguration.DEFAULT_DISCRIMINATOR_COLUMN_NAME,
						storage);
				if (!discrim.isTypeSpecified()) {
					discrim.setTypeName("string");
				}
				persistentClass.setPolymorphic(true);
				if ("true".equals(subnode.attributeValue("force")))
					persistentClass.setForceDiscriminator(true);
				if ("false".equals(subnode.attributeValue("insert")))
					persistentClass.setDiscriminatorInsertable(false);

			} else if (name.equals("cache")) {
				persistentClass.setCacheConcurrencyStrategy(subnode
						.attributeValue("usage"));
				persistentClass.setCacheRegionName(subnode
						.attributeValue("region"));
			}
		}
		createPrimaryKey((Table)table,persistentClass);
		createClassProperties(node, persistentClass, storage, inheritedMetas, null, true/*updateable*/);
	}


	private void createPrimaryKey(Table table, ClassMapping persistentClass) {
		PrimaryKey pk = new PrimaryKey();
		pk.setTable((Table) table);
		pk.setName("pk_"+table.getName());
		if(persistentClass.getKey()!=null){
			Iterator columnsIt = persistentClass.getKey().getColumnIterator();
			while(columnsIt.hasNext()){
				Column column = (Column)(columnsIt.next()); 
				pk.addColumn(column);
			}
			table.setPrimaryKey(pk);
		}
	}
	
	private void createForeignKey(Table table, ClassMapping persistentClass){
		ForeignKey fk = new ForeignKey();
		fk.setTable(table);
		fk.setName("fk_"+table.getName());
		
		SimpleValueMapping keyMapping = (SimpleValueMapping)persistentClass.getKey();
		Iterator columnsIt = keyMapping.getColumnIterator();
		while(columnsIt.hasNext()){
			Column column = (Column)(columnsIt.next());
			fk.addColumn(column);
			table.addForeignKey(fk.getName(), fk);
		}
	}
	
	private void bindClass(Element node, ClassMapping persistentClass,
							XMLFileStorage storage, Map inheritedMetas) {

		String entityName;
		//CLASS
		if ("dynamic-class".equals(node.getName())) {
			entityName = node.attributeValue("entity-name");
			String className = node.attributeValue("name", null);
			if (className == null) {
				className = entityName;
			}
			persistentClass.setClassName(className);
			persistentClass.setDynamic(true);
		} else {
			String className = getClassName(node.attribute("name"));
			persistentClass.setClassName(className);
			Attribute entityNameNode = node.attribute("entity-name");
			if (entityNameNode != null) {
				entityName = entityNameNode.getValue();
			} else {
				entityName = className;
			}
		}
		persistentClass.setEntityName(entityName);
		
		//LAZY
		Attribute lazyNode = node.attribute( "lazy" );
		boolean lazy = lazyNode == null ? storage.isDefaultLazy() : "true".equals( lazyNode.getValue() );
		persistentClass.setLazy( lazy );
		
		//PROXY INTERFACE
		Attribute proxyNode = node.attribute("proxy");
		String proxyName = getClassName( node.attribute( "proxy" ) );
		if ( proxyName != null ) {
			persistentClass.setProxyInterfaceName( proxyName );
			persistentClass.setLazy( true );
		}
		
		//DISCRIMINATOR
		Attribute discriminatorNode = node.attribute("discriminator-value");
		persistentClass
				.setDiscriminatorValue((discriminatorNode == null) ? persistentClass
						.getEntityName()
						: discriminatorNode.getValue());
		//DYNAMIC UPDATE
		Attribute dynamicNode = node.attribute("dynamic-update");
		persistentClass.setDynamicUpdate((dynamicNode == null) ? false : "true"
				.equals(dynamicNode.getValue()));
		//DYNAMIC INSERT
		Attribute insertNode = node.attribute("dynamic-insert");
		persistentClass.setDynamicInsert((insertNode == null) ? false : "true"
				.equals(insertNode.getValue()));

		//IMPORT
		storage.addImport(entityName, entityName);
		if (storage.isAutoImport() && entityName.indexOf('.') > 0) {
			storage.addImport(entityName, ClassUtils
					.getUnqualifyName(entityName));
		}

		//BATCH SIZE
		Attribute batchNode = node.attribute("batch-size");
		try { //XXX toAlex: (Slava) NumberFormatException don't stop parsing
			  // process, sure ? sure
			if (batchNode != null)
				persistentClass.setBatchSize(Integer.parseInt(batchNode.getValue()));
		} catch (NumberFormatException e) {
			ExceptionHandler.logInfo("batch-size is not integer: " + batchNode.getValue());
		}
		//XXX Slava(5) load all attributes and child elements such as (id,
		// composite-id, discriminator etc.)

		//SELECT BEFORE UPDATE
		Attribute sbuNode = node.attribute("select-before-update");
		if (sbuNode != null)
			persistentClass.setSelectBeforeUpdate("true".equals(sbuNode
					.getValue()));

		//OPTIMISTIC LOCK MODE
		Attribute olNode = node.attribute("optimistic-lock");
		persistentClass.setOptimisticLockMode(getOptimisticLockMode(olNode));

		//META
		persistentClass.setMetaAttributes(getMetas(node, inheritedMetas));

		//PERSISTER
		Attribute persisterNode = node.attribute("persister");
		if (persisterNode != null)
			persistentClass.setPersisterClassName(persisterNode.getValue());

		//CUSTOM SQL
		handleCustomSQL(node, persistentClass);

		//ABSTRACT
		String abstractNode = node.attributeValue("abstract", "false");
		persistentClass.setIsAbstract("true".equals(abstractNode));

		//SYNCHRONIZE
		persistentClass.setSynchronizedTables(getSynchronizedTables(node));
	}

	private String getSynchronizedTables(Element node){
		Iterator tables = node.elementIterator("synchronize");
		String syncTables = null;
		while (tables.hasNext()) {
			if (syncTables == null)
				syncTables = "";
			else
				syncTables += ",";
				syncTables += ((Element) tables.next()).attributeValue("table");
		}
		return syncTables;
	}
	private void bindCompositeId(Element subnode, ComponentMapping component, RootClassMapping persistentClass, String propertyName, XMLFileStorage storage, Map inheritedMetas) {
		String path = ClassUtils.qualify( persistentClass.getEntityName(), propertyName == null ? "id" : propertyName );
		bindComponent( subnode,
				component,
				persistentClass.getClassName(),
				propertyName,
				path,
				false,
				subnode.attribute( "class" ) == null && propertyName == null,
				storage,
				inheritedMetas, false );
		
	}
	
	private void bindComposite(Element node, ComponentMapping component, String path,
			boolean isNullable, XMLFileStorage storage, java.util.Map inheritedMetas) {
		bindComponent(
			node,
			component,
			null,
			null,
			path,
			isNullable,
			false,
			storage,
			inheritedMetas, false );
	}
	

	private void bindComponent(Element node, ComponentMapping component, 
								String parentClass, String parentProperty, 
								String path,
								boolean isNullable, boolean isEmbedded, 
								XMLFileStorage storage,
								Map inheritedMetas,
								boolean isProperties			// is the component a "properties" ;
								)
	{
		component.setEmbedded(isEmbedded);
		component.setMetaAttributes(getMetas(node, inheritedMetas));
		Attribute classNode = node.attribute("class");
		if ("dynamic-component".equals(node.getName())) {
			component.setDynamic(true);
		} else if (isEmbedded) {
			// an "embedded" component (composite ids and unique)
			// note that this does not handle nested components
			component.setComponentClassName(component.getOwner().getClassName());
		} else {
			if (classNode != null)
				component.setComponentClassName(getClassName(classNode));
            //by Nick
			
			// del tau 23.05.2006 -> findFieldType(parentClass,parentProperty) == null
//            else
//            {
//                component.setComponentClassName(findFieldType(parentClass,parentProperty));
//            }
			
        }
		
		Iterator iter = node.elementIterator();
		while (iter.hasNext()) {
			Element subnode = (Element) iter.next();
			String name = subnode.getName();
			String propertyName = subnode.attributeValue( "name" );
			String subpath = (propertyName == null) ? null : ClassUtils.qualify( path, propertyName );
			
			
			IHibernateValueMapping value = handleCollections(component.getOwner(), subnode, subpath);
			if(value!=null)
			{
				if(isProperties)
				{ value = null;}
			} else if ("many-to-one".equals(name) || ("key-many-to-one".equals(name) && !isProperties) ) {
				value = new ManyToOneMapping((Table) component.getTable());
				bindManyToOne(subnode, (ManyToOneMapping) value, propertyName,
						isNullable, storage);
			} else if ("one-to-one".equals(name)  && !isProperties ) {
				value = new OneToOneMapping((Table) component.getTable(),
						component.getOwner().getKey());
				bindOneToOne(subnode, (OneToOneMapping) value, isNullable,
						storage);
			} else if ("any".equals(name) && !isProperties ) {
				value = new AnyMapping((Table) component.getTable());
				if(XMLFileReader.MAPPING_ERROR == bindAny(subnode, (AnyMapping) value, isNullable, storage))
				{
					value = null;
				}
			} else if ("property".equals(name) || ("key-property".equals(name) && !isProperties ) ) {
				value = new SimpleValueMapping((Table) component.getTable());
				bindSimpleValue(subnode, (SimpleValueMapping) value, isNullable, propertyName, storage);
			} else if ("component".equals(name)
					|| "dynamic-component".equals(name)
					|| (("nested-composite-element".equals(name)) && !isProperties ) ) {
				value = component.getOwner() != null ? new ComponentMapping(
						component.getOwner()) : // a class component
						new ComponentMapping((Table) component.getTable()); 
				bindComponent(subnode, (ComponentMapping) value, /*parentClass*/component.getComponentClassName(), //changed by Nick 23.05.2005 
						propertyName, subpath,
						isNullable, isEmbedded, storage, inheritedMetas, isProperties);
			} else if ("parent".equals(name) && !isProperties ) {
				component.setParentProperty(propertyName);
			}

			if (value != null) {
				component.addProperty(createProperty(component.getOwner(), value, propertyName,
                        /*parentClass*/component.getComponentClassName(), false, subnode,
						storage, inheritedMetas));
			}
		}
		if ( "true".equals( node.attributeValue( "unique" ) ) ) {
			iter = component.getColumnIterator();
			ArrayList cols = new ArrayList();
			while ( iter.hasNext() )
				cols.add( iter.next() );
			component.getOwner().getDatabaseTable().createUniqueKey( cols );
		}
	}
	
	public  void bindOneToMany(Element node, OneToManyMapping oneToMany) {
		oneToMany.setReferencedEntityName( getEntityName( node, storage ) );
		
// added by yk 28.06.2005
		boolean ignore = false;
		Attribute notfound = node.attribute("not-found");
		if(notfound != null)
		{	
			ignore = "exception".equals(notfound.getValue()) ? false : true;
		}
		oneToMany.setIgnoreNotFound(ignore);
// added by yk 28.06.2005 stop
		
		/* TODO support embed-xml
		final String embed = node.attributeValue( "embed-xml" );
		oneToMany.setEmbedded( embed == null || "true".equals( embed ) );
		*/
	}

	

	public void bindManyToOne(Element node, ManyToOneMapping manyToOne, String path, boolean isNullable, XMLFileStorage storage)	{
		
		bindColumnsOrFormula( node, manyToOne, path, isNullable, storage );
		initOuterJoinFetchSetting( node, manyToOne );
		
		Attribute ukName = node.attribute( "property-ref" );
		if ( ukName != null ) manyToOne.setReferencedPropertyName( ukName.getValue() );
		
		manyToOne.setReferencedEntityName( getEntityName( node, storage ) );
		
		Attribute fkNode = node.attribute( "foreign-key" );
		if ( fkNode != null ) manyToOne.setForeignKeyName( fkNode.getValue() );
		
		String notFound = node.attributeValue( "not-found" );
		manyToOne.setIgnoreNotFound( "ignore".equals( notFound ) );
}

	public void bindOneToOne(Element node, OneToOneMapping oneToOne,
			boolean isNullable, XMLFileStorage storage) {

		initOuterJoinFetchSetting(node, oneToOne);

		Attribute constrNode = node.attribute("constrained");
		boolean constrained = constrNode != null
				&& constrNode.getValue().equals("true");
		oneToOne.setConstrained(constrained);

		Attribute fkNode = node.attribute("foreign-key");
		if (fkNode != null)
			oneToOne.setForeignKeyName(fkNode.getValue());

		Attribute ukName = node.attribute("property-ref");
		if (ukName != null)
			oneToOne.setReferencedPropertyName(ukName.getValue());

		oneToOne.setReferencedEntityName(getEntityName(node, storage));
		// added by yk 23.09.2005
		Element formulaNode = node.element("formula");
		if (formulaNode != null)
			oneToOne.setFormula(getMultileneCDATA(node, "formula"));
		// added by yk 23.09.2005.
	}

	private String getEntityName(Element elem, XMLFileStorage storage) {
		String entityName = elem.attributeValue( "entity-name" );
		return entityName == null ?
				getClassName( elem.attribute( "class" ) ) :
				entityName;
	}
	
	private void initOuterJoinFetchSetting(Element node, ToOneMapping value) {
		String selectMode = "select";
		String joinMode = "join";
		Attribute fetchNode = node.attribute( "fetch" );
		String fetchMode = null;
		if ( fetchNode == null ){
			Attribute jfNode = node.attribute( "outer-join" );
			if ( jfNode == null ) {
				if ("one-to-one".equals( node.getName() ) ){
					fetchMode = ( (OneToOneMapping) value ).isConstrained() ? selectMode : joinMode;	
				}else if ( "many-to-many".equals( node.getName() ) ){
					//do nothing
				}else{
					fetchMode = selectMode;
				}
			}else{
				String eoj = jfNode.getValue();
				if ( "auto".equals( eoj ) ) {
					fetchMode = selectMode;
				}
				else {
					fetchMode = "true".equals( eoj ) ?	joinMode : selectMode;
				}
			}
			
		}else{
			fetchMode = "join".equals( fetchNode.getValue() ) ?	joinMode : selectMode;
		}
		if ( !"many-to-many".equals( node.getName() ) )	value.setFetchMode(fetchMode);
	}
	
	private int bindAny(Element node, AnyMapping any, boolean isNullable,
			XMLFileStorage storage) {
		
		// added by yk 08.10.2005 temporary variant
		int columnscount = 0;
		Iterator columns = node.elementIterator("column");
		while(columns.hasNext())
		{	columns.next(); columnscount++;}
		if(columnscount < 2)
		{		return XMLFileReader.MAPPING_ERROR;		}
		// added by yk 08.10.2005.
		
		Attribute typeNode = node.attribute("type");
		if (typeNode == null)
			typeNode = node.attribute("id-type"); //for an any
		any.setIdentifierType(typeNode.getValue());

		Attribute metaAttribute = node.attribute("meta-type");
		if (metaAttribute != null) {
			any.setMetaType(metaAttribute.getValue());
		} else any.setMetaType("string");
		Iterator iter = node.elementIterator("meta-value");
		if (iter.hasNext()) {
			HashMap values = new HashMap();
			while (iter.hasNext()) {
				Element metaValue = (Element) iter.next();
				String entityName = getClassName(metaValue.attribute("class"));
				String value=metaValue.attributeValue( "value" ); 
				values.put(value, entityName);
			}
			any.setMetaValues(values);
		}
		if (any.getTable() != null) {
			bindColumns(node, any, isNullable, false, null, storage);
		}
		return XMLFileReader.MAPPING_OK;
	}

	private PropertyMapping createProperty(IPersistentClassMapping classMapping, IHibernateValueMapping value,
			String propertyName, String className, boolean isDynamic,
			Element subnode, XMLFileStorage mappings,
			java.util.Map inheritedMetas) {

		//setTypeForValue(className, propertyName, value);

		// TODO: Slava (Slava) add code here
		//	if ( value instanceof ToOneMapping ) {
		//		ToOneMapping toOne = ( ToOneMapping ) value;
		//		String propertyRef = toOne.getReferencedPropertyName();
		//		if ( propertyRef != null ) {
		//			storage.addUniquePropertyReference( toOne.getReferencedEntityName(),
		//			propertyRef );
		//		}
		//	}
		//	else if ( value instanceof CollectionMapping ) {
		//		CollectionMapping coll = ( CollectionMapping ) value;
		//		String propertyRef = coll.getReferencedPropertyName();
		//		//not necessarily a *unique* property reference
		//		if ( propertyRef != null ) {
		//			storage.addPropertyReference( coll.getOwnerEntityName(),
		//			propertyRef );
		//		}
		//	}
		//	
		//	value.createForeignKey();
        PropertyMapping prop = new PropertyMapping();
        bindProperty(subnode, prop, isDynamic, storage, inheritedMetas);
        prop.setValue(value);
        if (value != null)
        {
            value.setFieldMapping(prop);
// added by yk 06.07.2005
            setLazy(subnode, prop);
// added by yk 06.07.2005 stop
        }
		//by Nick 23.05.2005
        IPersistentClass pc = classMapping.getPersistentClass();
        String typeName = null;
        IPersistentField pf = null;

        if (pc == null)
        {
            PersistentField pField = new PersistentField();
            
            //del tau 23.05.2006 Why? -> pc == null 
            //pField.setOwnerClass(pc);
            
            if (propertyName != null)
                pField.setName(propertyName);
            else
                pField.setName(prop.getName());
            
            if (value instanceof ComponentMapping)
            {
                pField.setType(((ComponentMapping)value).getComponentClassName());
            }
            
            // del tau 23.05.2006 -> findFieldType(className,pField.getName() = null
//            else
//            {
//                if (pField.getType() == null)
//                {
//                    typeName = findFieldType(className,pField.getName());
//                    pField.setType(typeName);
//                }
//            }
            
            pf = pField;
        }
        else
        {
            pf = pc.getField(propertyName);
        }
        
        if (pf != null)
        {
            pf.setMapping(prop);
        }
        prop.setPersistentField(pf);        
        //by Nick
		return prop;
	}

//	 added by yk 06.07.2005
	private void setLazy(Element node, PropertyMapping property)
	{
		String nodename = node.getName();
		Attribute lazyNode = node.attribute("lazy");
		if ("property".equals(nodename) || "any".equals(nodename) || "component".equals(nodename))
		{//	<!ATTLIST any lazy (true|false) "false">
			property.setLazy( (lazyNode == null) ? false : "true".equals(lazyNode.getValue()));
		}
		if("many-to-one".equals(nodename) || "one-to-one".equals(nodename))
		{
			if(lazyNode == null) property.setToOneLazy("proxy");
			else
			{	property.setToOneLazy(lazyNode.getValue());		}
		}
//		if ("key-many-to-one".equals(nodename) || "many-to-many".equals(nodename))
//		{// 	<!ATTLIST key-many-to-one lazy (false|proxy) #IMPLIED>
//		}
	}
//	added by yk 06.07.2005 stop	
	
	private void makeVersion(Element node, SimpleValueMapping model) {

		// VERSION UNSAVED-VALUE
		Attribute nullValueNode = node.attribute( "unsaved-value" );
		if ( nullValueNode != null ) {
			model.setNullValue( nullValueNode.getValue() );
		}
		else {
			model.setNullValue( "undefined" );
		}

	}
	
	private void makeIdentifier(Element node, SimpleValueMapping model, XMLFileStorage storage) {
		//GENERATOR
		Element subnode = node.element( "generator" );
		if ( subnode != null ) {
			//XXX: toAlex (Slava)	What way can we use generator ?
			model.setIdentifierGeneratorStrategy( subnode.attributeValue( "class" ) );
			//Add necessary fields/getters/setters into SimpleValueMapping. (Slava)done

			Properties params = new Properties();
			Iterator iter = subnode.elementIterator( "param" );
			while ( iter.hasNext() ) {
				Element childNode = ( Element ) iter.next();
				params.setProperty( childNode.attributeValue( "name" ),
						childNode.getText() );
			}

			model.setIdentifierGeneratorProperties( params );
		}

		//model.getTable().setIdentifierValue( model );

		// ID UNSAVED-VALUE
		Attribute nullValueNode = node.attribute( "unsaved-value" );
		if ( nullValueNode != null ) {
			model.setNullValue( nullValueNode.getValue() );
		}
		else {
			if ( "assigned".equals( model.getIdentifierGeneratorStrategy() ) ) {
				model.setNullValue( "undefined" );
			}
			else {
				model.setNullValue( null );
			}
		}
	}
	
	public void bindProperty(Element node, PropertyMapping property,
			boolean isDynamic, XMLFileStorage storage,
			java.util.Map inheritedMetas) {

		property.setName(node.attributeValue("name"));

		Attribute accessNode = node.attribute("access");
		if (accessNode != null) {
			property.setPropertyAccessorName(accessNode.getValue());
		} else if (isDynamic) {
			property.setPropertyAccessorName("map");
		} else if (node.getName().equals("properties")) {
			property.setPropertyAccessorName("embedded");
		} else {
			property.setPropertyAccessorName(storage.getDefaultAccess());
		}

		Attribute cascadeNode = node.attribute("cascade");
		property.setCascade(cascadeNode == null ? storage.getDefaultCascade()
				: cascadeNode.getValue());

		Attribute updateNode = node.attribute("update");
		property.setUpdateable(updateNode == null
				|| "true".equals(updateNode.getValue()));

		Attribute insertNode = node.attribute("insert");
		property.setInsertable(insertNode == null
				|| "true".equals(insertNode.getValue()));

		Attribute lockNode = node.attribute("optimistic-lock");
		property.setOptimisticLocked(lockNode == null
				|| "true".equals(lockNode.getValue()));
/* rem by yk 06.07.2005
		boolean isLazyable = "property".equals(node.getName())
				|| "component".equals(node.getName())
				|| "many-to-one".equals(node.getName())
				|| "one-to-one".equals(node.getName())
				|| "any".equals(node.getName());
		if (isLazyable) {
			Attribute lazyNode = node.attribute("lazy");
			property.setLazy(lazyNode != null
					&& "true".equals(lazyNode.getValue()));
			
		}
*/
		property.setMetaAttributes(getMetas(node, inheritedMetas));

	}
	
	//automatically makes a column with the default name if none is specifed by
	// XML
	public void bindSimpleValue(Element node,
									   SimpleValueMapping simpleValue,
									   boolean isNullable,
									   String path,
									   XMLFileStorage storage) {
		// added by yk 2005/10/18
		if(node == null || simpleValue == null ) return;
		// added by yk 2005/10/18.
		String simpleValueName = node.attributeValue("name", null);
		if(simpleValueName != null)	simpleValue.setName(simpleValueName);
			
		//setTypeForValue(className, path, simpleValue);
		
		bindSimpleValueType( node, simpleValue, storage );
		bindColumnsOrFormula( node, simpleValue, path, isNullable, storage );

		Attribute fkNode = node.attribute( "foreign-key" );
		if ( fkNode != null ) simpleValue.setForeignKeyName( fkNode.getValue() );
	}	

	private void bindSimpleValueType(Element node, SimpleValueMapping simpleValue, XMLFileStorage storage) {
		String typeName = null;

		Properties parameters = new Properties();

		Attribute typeNode = node.attribute( "type" );
		if ( typeNode == null ) typeNode = node.attribute( "id-type" ); //for an any
		if ( typeNode != null ) typeName = typeNode.getValue();
		

		Element typeChild = node.element( "type" );
		if ( typeName == null && typeChild != null ) {
			typeName = typeChild.attribute( "name" ).getValue();
			Iterator typeParameters = typeChild.elementIterator( "param" );

			while ( typeParameters.hasNext() ) {
				Element paramElement = ( Element ) typeParameters.next();
				parameters.setProperty( paramElement.attributeValue( "name" ),
						paramElement.getTextTrim() );
			}
		}
		
		if ( !parameters.isEmpty() ) simpleValue.setTypeParameters( parameters );
		
		if ( typeName != null ) {
			simpleValue.setTypeName( typeName );
			Type type = TypeUtils.javaTypeToHibType(typeName);
			if(type==null)	type = Type.getOrCreateType(typeName);
			simpleValue.setType(type);
		}
	}

	private void bindColumnsOrFormula(Element node,
									 SimpleValueMapping simpleValue,
									 String path,
									 boolean isNullable,
									 XMLFileStorage storage) {
		Attribute formulaNode = node.attribute("formula");
		if (formulaNode != null) {
			/* rem by yk 23.09.2005 simpleValue.setFormula(formulaNode.getText()); */
			simpleValue.setFormula(getMultileneCDATA(node, "formula"));
		} else {
			bindColumns(node, simpleValue, isNullable, true, path, storage);
		}
	}

	public void bindColumns(Element node,
							SimpleValueMapping simpleValue,
							boolean isNullable,
							boolean autoColumn,
							String propertyPath,
							XMLFileStorage storage)	{
	
		//COLUMN(S)
		// added by yk 04.11.2005
		if(node == null || simpleValue == null) return;
		// added by yk 04.11.2005.

		Attribute columnAttribute = node.attribute("column");
		if (columnAttribute == null) {
			Iterator iter = node.elementIterator();
			Table table = (Table) simpleValue.getTable();
			while (iter.hasNext()) {
				Element columnElement = (Element) iter.next();
				if (columnElement.getName().equals("column")) {
					if(table != null)
					{
	                    String columnName = columnElement.attributeValue("name");
	                    Column column = (Column)table.getOrCreateColumn(columnName);
						column.setPersistentValueMapping(simpleValue);
						bindColumn(columnElement, column, isNullable);
						simpleValue.addColumn(column);
					
						//column index
						bindIndex(columnElement.attribute("index"), table, column);
						//column group index (although can server as a separate column index)
						bindIndex(node.attribute("index"), table, column);
					}
					//TODO: Slava (Slava) check it with unique-key
					///bindUniqueKey(columnElement.attribute("unique-key"), table,	column);
				} else if (columnElement.getName().equals("formula")) {
					/* rem by yk 23.09.2005  simpleValue.setFormula(columnElement.getText()); */
					simpleValue.setFormula(getMultileneCDATA(node, "formula"));
					autoColumn=false;
				}
			}
		} else {
			if (node.elementIterator("column").hasNext()) {
					ExceptionHandler.logInfo("column attribute may not be used together with <column> subelement in "+resource.getProjectRelativePath().toString() + ": " +node.getPath());
			}
			if (node.elementIterator("formula").hasNext()) {
					ExceptionHandler.logInfo("column attribute may not be used together with <formula> subelement in "+resource.getProjectRelativePath().toString() + ": " +node.getPath());
			}

			String columnname = columnAttribute.getValue();
			Table table = (Table) simpleValue.getTable();
			if(table != null)
			{
				Column column = (Column)table.getOrCreateColumn(columnname);
				column.setPersistentValueMapping(simpleValue);
				bindColumn(node, column, isNullable);
				simpleValue.addColumn(column);
				bindIndex(node.attribute("index"), table, column);
			}
		}

		if (autoColumn && simpleValue.getColumnSpan() == 0) {
			Column col = new Column();
			col.setPersistentValueMapping(simpleValue);
			col.setName(/* by Nick - 6.09.2005 - HibernateAutoMapping.propertyToColumnName(*/propertyPath/*)*/);
			bindColumn(node, col, isNullable);
			// added by yk 04.11.2005
			if(simpleValue.getTable() != null){
			// added by yk 04.11.2005.
				simpleValue.getTable().addColumn(col);
			}
			simpleValue.addColumn(col);
		}

	}

	private void bindIndex(Attribute indexAttribute, Table table, Column column) {
		if ( indexAttribute != null && table != null ) {
			StringTokenizer tokens = new StringTokenizer( indexAttribute.getValue(), ", " );
			while ( tokens.hasMoreTokens() ) {
				table.getOrCreateIndex( tokens.nextToken() );
				table.addColumn(column);
			}
		}
	}

	//XXX: toAlex (Slava). Check if it correct. Have no idea
	private void bindUniqueKey(Attribute uniqueKeyAttribute, Table table, Column column) {
		if ( uniqueKeyAttribute != null && table != null ) {
			StringTokenizer tokens = new StringTokenizer( uniqueKeyAttribute.getValue(), ", " );
			while ( tokens.hasMoreTokens() ) {
				///table.getUniqueKey( tokens.nextToken() ).addColumn( column );
				Iterator uniqueKeyIt = table.getUniqueKeyIterator();
				while(uniqueKeyIt.hasNext()){
					boolean uniqueKey = uniqueKeyIt.hasNext();
					Boolean booUniqueKey = Boolean.valueOf(uniqueKey);
					if(booUniqueKey.toString().equalsIgnoreCase(tokens.nextToken())){
						table.addColumn(column);
					}
				}
			}
		}
	}
	
	public void bindColumn(Element node, Column column, boolean isNullable) {
		//Attribute lengthNode = node.attribute( "length" );
		//if ( lengthNode != null ) column.setLength( Integer.parseInt( lengthNode.getValue() ) );
		try{
			int columnLength = Integer.parseInt(node.attributeValue("length", String.valueOf(Column.DEFAULT_LENGTH)/* changed by Nick 02.09.2005 "255" */));
			column.setLength(columnLength);
		}catch(NumberFormatException nfe){
        	//TODO (tau-tau) for Exception			
			//do nothing
		}
		
		SimpleValueMapping valueMapping = (SimpleValueMapping) column.getPersistentValueMapping();
		//Set owner table:
		IDatabaseTable ownerTable = valueMapping.getTable();
		if(ownerTable != null)	column.setOwnerTable((Table)ownerTable);
		
		Attribute lengthNode = node.attribute( "length" );
		if ( lengthNode != null ) column.setLength( Integer.parseInt( lengthNode.getValue() ) );
		
		Attribute precNode = node.attribute( "precision" );
		if ( precNode != null) column.setPrecision( Integer.parseInt( precNode.getValue() ) );

		Attribute scaleNode = node.attribute( "scale" );
		if ( scaleNode != null) column.setScale( Integer.parseInt( scaleNode.getValue() ) );

		Attribute nullNode = node.attribute( "not-null" );
		column.setNullable( nullNode == null ? isNullable : nullNode.getValue().equals( "false" ) );
		
		Attribute unqNode = node.attribute( "unique" );
		if ( unqNode != null ) column.setUnique( unqNode.getValue().equals( "true" ) );

		column.setCheckConstraint( node.attributeValue( "check" ) );

		Attribute typeNode = node.attribute( "sql-type" );
		if ( typeNode != null){
			
            // added by Nick 01.07.2005
            String typeName = typeNode.getValue();
            Pattern p = Pattern.compile("^(\\w+)\\(\\ *(\\d+)\\ *(?:,\\ *(\\d+)\\ *)?\\)([\\w\\s]*)");
            Matcher m = p.matcher(typeName);
            
            String realTypeName = typeName;
            if (m.matches() && m.groupCount() > 0)
            {
                realTypeName = m.replaceFirst("$1");
                if (m.groupCount() > 1)
                {
                    String lengthStr = m.group(2);
// added by yk 01.09.2005
                    String scaleStr = m.group(3);
                    int length, precision, scale;
                    length 		= Column.DEFAULT_LENGTH;
                    precision 	= Column.DEFAULT_PRECISION;
                    scale		= Column.DEFAULT_SCALE;
// added by yk 01.09.2005.
                    try
                    {
                        if(scaleStr != null)
                        {
                        	length 		= Column.DEFAULT_LENGTH;
	                    	precision 	= lengthStr != null ?  Integer.parseInt(lengthStr) : Column.DEFAULT_PRECISION;
	                    	scale  		= scaleStr != null ? Integer.parseInt(scaleStr) : Column.DEFAULT_SCALE;
                        }
                        else
                        {
                        	length		= lengthStr != null ?  Integer.parseInt(lengthStr) : Column.DEFAULT_LENGTH;
                        	precision	= Column.DEFAULT_PRECISION;
                        	scale		= Column.DEFAULT_SCALE;
                        }
                    }
                    catch(Exception exc){
                    	//TODO (tau-tau) for Exception                    	
                    }
                    column.setLength(length);
                    column.setPrecision(precision);
                    column.setScale(scale);
                }
// added by yk 01.09.2005.
                
            }
		    // by Nick
            
            // added by Nick 29.08.2005
            String additionalTypeInfo = "";
            p = Pattern.compile("^\\w+\\(\\ *\\d+\\ *(?:,\\ *\\d+\\ *)?\\)([\\w\\s]*)");
            m = p.matcher(typeName);
            if (m.matches() && m.groupCount() > 0)
            {
                additionalTypeInfo = m.replaceFirst("$1");
                additionalTypeInfo = additionalTypeInfo.trim();
                if (additionalTypeInfo.length() > 0)
                {
                    realTypeName = realTypeName + " " + additionalTypeInfo;
                }
            }
            // by Nick
            
            // added  by yk 30/08/2005
            final String identity = "identity";
            if(realTypeName.indexOf(identity) != -1)
            { 	
            	if(identity.equals(StringUtils.getWord(realTypeName," ", 1)) )
            		realTypeName = StringUtils.getWord(realTypeName," ", 0);		}
            // added  by yk 30/08/2005.
            
            column.setSqlTypeName(realTypeName);
			Type type = (Type.getType(typeNode.getValue()) != null ) ?  Type.getType(typeNode.getValue()) : Type.getType(typeNode.getValue().toLowerCase());
			if(type != null)	column.setSqlTypeCode(type.getSqlType());
		} else {
			if(valueMapping != null && valueMapping.getTypeName()!=null){
				String typeName = valueMapping.getTypeName();
				Type type = TypeUtils.javaTypeToHibType(typeName);
				if(type==null){
					type = Type.getType(typeName);
				}
				if(type!=null){
					int sqlTypeCode = type.getSqlType();
					column.setSqlTypeCode(sqlTypeCode);
				}
			}
		}
	}
/*	
	private void setTypeForValue(String className, String propertyName, SimpleValueMapping id){
		if(className!=null && propertyName!=null && id!=null){
			IPersistentField pf=getPersistentField(className, propertyName);
			if(pf!=null && pf.getType()!=null){
				String typeName = pf.getType();
				Type type = TypeUtils.javaTypeToHibType(typeName);
				if(type!=null){
					id.setType(type);
					id.setTypeName(type.getName());
				}
			}
		}else{
			if(logger.isLoggable(Level.CONFIG))	logger.info("null parameter in setTypeName");
		}
	}*/
	/*
	private IPersistentField getPersistentFielde(String className, String fieldName){
		IPersistentField pf = null;
		if(className != null || fieldName != null){
			PersistentClass pc=hbMapping.getOrCreatePersistentClass(className);
			pf=pc.getField(fieldName);
		}
		return pf;
	}*/

	private String getClassTableName(ClassMapping model, Element node, XMLFileStorage storage) {
		Attribute tableNameNode = node.attribute( "table" );
		if ( tableNameNode == null ) {
			return HibernateAutoMapping.classToTableName( model.getEntityName() );
		}
		else {
			//changed by Nick 19.05.2005
            return tableNameNode.getValue();
            //by Nick
            //return HibernateAutoMapping.tableName( tableNameNode.getValue() );
		}
	}
	
	private void handleCustomSQL(Element node, ClassMapping model){
		Element element = node.element( "sql-insert" );
		if ( element != null ) {
			boolean callable = false;
			callable = isCallable(element, true);
			model.setCustomSQLInsert( element.getText(), callable );
		}

		element = node.element( "sql-delete" );
		if ( element != null ) {
			boolean callable = false;
			callable = isCallable(element, true);
			model.setCustomSQLDelete( element.getText(), callable );
		}

		element = node.element( "sql-update" );
		if ( element != null ) {
			boolean callable = false;
			callable = isCallable(element, true);
			model.setCustomSQLUpdate( element.getText(), callable );
		}

		element = node.element( "loader" );
		if ( element != null ) {
			model.setLoaderName( element.attributeValue( "query-ref" ) );
		}
	}

	private void handleCustomSQL(Element node, JoinMapping model) {
		Element element = node.element( "sql-insert" );
		if ( element != null ) {
			boolean callable = false;
			callable = isCallable( element );
			model.setCustomSQLInsert( element.getText(), callable );
		}

		element = node.element( "sql-delete" );
		if ( element != null ) {
			boolean callable = false;
			callable = isCallable( element );
			model.setCustomSQLDelete( element.getText(), callable );
		}

		element = node.element( "sql-update" );
		if ( element != null ) {
			boolean callable = false;
			callable = isCallable( element );
			model.setCustomSQLUpdate( element.getText(), callable );
		}
	}
	private void handleCustomSQL(Element node, CollectionMapping model) {
		Element element = node.element( "sql-insert" );
		if ( element != null ) {
			boolean callable = false;
			callable = isCallable( element, true );
			model.setCustomSQLInsert( element.getText(), callable );
		}

		element = node.element( "sql-delete" );
		if ( element != null ) {
			boolean callable = false;
			callable = isCallable( element, true );
			model.setCustomSQLDelete( element.getText(), callable );
		}

		element = node.element( "sql-update" );
		if ( element != null ) {
			boolean callable = false;
			callable = isCallable( element, true );
			model.setCustomSQLUpdate( element.getText(), callable );
		}

		element = node.element( "sql-delete-all" );
		if ( element != null ) {
			boolean callable = false;
			callable = isCallable( element, true );
			model.setCustomSQLDeleteAll( element.getText(), callable );
		}
	}

	private boolean isCallable(Element e) {
		return isCallable( e, true );
	}
	
	private boolean isCallable(Element element, boolean supportsCallable){
		Attribute attrib = element.attribute( "callable" );
		if ( attrib != null && "true".equals( attrib.getValue() ) ) {
			if ( !supportsCallable ) {
				ExceptionHandler.logInfo("callable attribute not supported yet!" );
			}
			return true;
		}
		return false;
	}

	private String getMultileneCDATA(Element element, String name) {
		String item = element.attributeValue(name);
		if ( item != null ) {
			return item;
		}
		else {
			Element subselectElement = element.element(name);
			// added by yk 22.09.2005
			item = (subselectElement == null) ? null : getSubselectData(subselectElement);
			// added by yk 22.09.2005.

			/* rem by yk 22.09.2005   return subselectElement == null ? null : subselectElement.getText(); */
		}
		return item;
	}
	
	private String getSubselectData(Element subselect)
	{
		String result = "";
		List content = subselect.content();
		
		Iterator it = content.iterator();
		while(it.hasNext())
		{
			Object temp = it.next();
			if(temp instanceof Node)
			{
				int nodetype = ((Node)temp).getNodeType();
				if(nodetype == Node.CDATA_SECTION_NODE  || nodetype == Node.TEXT_NODE)
				{
					result += ((Node)temp).getText();
				}
			}
		}
		return result;
	}
	
	private String getClassName(Attribute att) {
		if ( att == null ) return null;
		return storage.getFullyQualifiedClassName(att.getValue());
	}

	private String getOptimisticLockMode(Attribute olAtt){

		if ( olAtt == null ) return OrmConfiguration.CHECK_VERSION;
		String olMode = olAtt.getValue();
		if ( olMode == null || OrmConfiguration.CHECK_VERSION.equals( olMode ) ) {
			return OrmConfiguration.CHECK_VERSION;
		}
		else if ( OrmConfiguration.CHECK_DIRTY_FIELDS.equals( olMode ) ) {
			return OrmConfiguration.CHECK_DIRTY_FIELDS;
		}
		else if ( OrmConfiguration.CHECK_ALL_FIELDS.equals( olMode ) ) {
			return OrmConfiguration.CHECK_ALL_FIELDS;
		}
		else if ( OrmConfiguration.CHECK_NONE.equals( olMode ) ) {
			return OrmConfiguration.CHECK_NONE;
		}
		else {
			ExceptionHandler.logInfo("Unsupported optimistic-lock style: " + olMode );
			return OrmConfiguration.CHECK_VERSION;			
		}
	}
	
	private void handleFilterDefs(Element root, XMLFileStorage storage) {
		Iterator filterDefs = root.elementIterator("filter-def");
		while (filterDefs.hasNext()){
			parseFilterDef((Element)filterDefs.next());
		}
	}

	private void parseFilterDef(Element element) {
		String name = element.attributeValue("name");
		Iterator params = element.elementIterator("filter-param");
		
		// added by yk 19.10.2005
		Properties parameters = new Properties();
		// added by yk 19.10.2005.

		while(params.hasNext()){
			Element param = (Element)params.next();
			String paramName = param.attributeValue("name");
			String paramType = param.attributeValue("type");
			if (OrmCore.TRACE || OrmCore.TRACE_INT_CORE ) 
				ExceptionHandler.logInfo("adding filter parameter : " + paramName + " -> " + paramType);
			// added by yk 19.10.2005
			parameters.setProperty(paramName, paramType);
			// added by yk 19.10.2005.
			
			//TODO: Slava: Not finished 
		}
		// added by yk 19.10.2005
		storage.addFilterDef(name, parameters);
		// added by yk 19.10.2005.

	}
	
	private void handleTypeDefs(Element root, XMLFileStorage storage) {
		Iterator typeDefs = root.elementIterator("typedef");
		while(typeDefs.hasNext()){
			Element typeDef = (Element)typeDefs.next();
			parseTypeDef(typeDef, storage);
		}	
	}

	private void parseTypeDef(Element typeDef, XMLFileStorage storage) {
		String typeName = typeDef.attributeValue("name");
		String typeClass = typeDef.attributeValue("class");
		Iterator paramIter = typeDef.elementIterator("param");
		Properties parameters = new Properties();
		while(paramIter.hasNext()){
			Element param = (Element)paramIter.next();
			parameters.setProperty(param.attributeValue("name"), param.getTextTrim());				
		}
		storage.addTypeDef(typeName, typeClass, parameters);
	}
	
	private Map getMetas(Element node, Map inheritedMeta) {
		return getMetas(node, inheritedMeta, false);
	}

	private Map getMetas(Element metaElement, Map inheritedMeta, boolean onlyInheritable){
		java.util.Map map = new HashMap();
		if(inheritedMeta!=null) map.putAll(inheritedMeta);
		
		Iterator metasIt = metaElement.elementIterator("meta");
		while(metasIt.hasNext()){
			Element metaNode = (Element)metasIt.next();
			boolean inheritable = Boolean.valueOf( metaNode.attributeValue("inherit")).booleanValue();
			if ( onlyInheritable & !inheritable ) {
				continue;
			}
			String name = metaNode.attributeValue("attribute");
			MetaAttribute meta = (MetaAttribute) map.get(name);
			if (meta == null) {
				meta = new MetaAttribute(name);
				map.put(name, meta);
			}
			meta.addValue(metaNode.getText());
		}
		return map;
	}
	
	/*
	 * 05/24/05 by alex: XXX : Nick It's not appropriate place for that function. 
	 * Field type update should be done in ComponentMapping.update and PropertyMapping.update etc. Also TypeAnalyzer should be moved to utils package.
	 * */
//    String findFieldType(String className, String propertyName)
//    {
//		/*
//        String typeName = null;
//        IType classType = null;
//        try {
//            classType = ScanProject.findClass(className,hbMapping.getProject().getProject());
//            if (classType != null)
//            {
//                TypeAnalyzer ta = new TypeAnalyzer(classType);
//                
//                if (ta != null)
//                {
//                    PersistableProperty pp = ta.getPropertyOrField(propertyName);
//                    if (pp != null)
//                        typeName = pp.getType();
//                }
//            }
//        }
//        catch (CoreException e) {
//            ExceptionHandler.log(e,"Exception processing property...");
//        }
//        return typeName;
//        */
//		return null;
//    }
}
