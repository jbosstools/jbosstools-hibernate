/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.mapper.extractor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.ui.internal.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.internal.contentassist.XMLRelevanceConstants;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.eclipse.mapper.MapperMessages;
import org.hibernate.util.StringHelper;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Helper class that can extract information about a hbm.xml document based on e.g. DOM Nodes.
 *
 * @author max
 *
 */
public class HBMInfoExtractor {

	HibernateTypeDescriptor[] hibernateTypes;

	final Map javaTypeProvider = new HashMap(); // key: element name, value: attribute which contains javaType
	final Map tableProvider = new HashMap(); // key: element name, value: attribute which contains table

	/** set of "tagname>attribname", used to decide which attributes we should react to */
	final Map attributeHandlers = new HashMap(); // completes a possible package or classname

	private String[] hibernatePropertyNames;
	private Map hibernatePropertyValues;

	private HibernateTypeDescriptor[] generatorTypes;

	private HibernateTypeDescriptor[] propertyAccessors;



	public HBMInfoExtractor() {
		setupTypeFinder();
		setupTableFinder();

        setupJavaTypeHandlers();

        setupPackageHandlers();

		setupFieldsPropertyHandlers();

		setupHibernateTypeHandlers();

		setupHibernateTypeDescriptors();

		setupTableNameHandlers();
		setupColumnNameHandlers();
		setupHibernateProperties();

		setupGeneratorClassHandlers();

		setupAccessHandlers();
	}


	String[] TRUE_FALSE = new String[] { "true", "false" }; //$NON-NLS-1$ //$NON-NLS-2$

	private void setupHibernateProperties() {
		hibernatePropertyNames = extractHibernateProperties();
		hibernatePropertyValues = new HashMap();

		 hibernatePropertyValues.put("bytecode.provider", new String[] { "cglib", "javassist"} );  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		 hibernatePropertyValues.put("bytecode.use_reflection_optimizer", TRUE_FALSE ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("c3p0.acquire_increment", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("c3p0.idle_test_period", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("c3p0.max_size", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("c3p0.max_statements", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("c3p0.min_size", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("c3p0.timeout", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("cache.jndi", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("cache.provider_class", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("cache.provider_configuration_file_resource_path", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("cache.query_cache_factory", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("cache.region_prefix", new String[] { } ); //$NON-NLS-1$
		 hibernatePropertyValues.put("cache.use_minimal_puts", TRUE_FALSE ); //$NON-NLS-1$
		 hibernatePropertyValues.put("cache.use_query_cache", TRUE_FALSE ); //$NON-NLS-1$
		 hibernatePropertyValues.put("cache.use_second_level_cache", TRUE_FALSE ); //$NON-NLS-1$
		 hibernatePropertyValues.put("cache.use_structured_entries", TRUE_FALSE ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("connection", new String[] { } ); //$NON-NLS-1$
		 hibernatePropertyValues.put("connection.autocommit", TRUE_FALSE ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("connection.datasource", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("connection.driver_class", new String[] { } ); //$NON-NLS-1$

		 hibernatePropertyValues.put("connection.isolation", new String[] { "0", "1", "2", "4", "8"} );    //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$//$NON-NLS-6$
		 //hibernatePropertyValues.put("connection.password", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("connection.pool_size", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("connection.provider_class", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("connection.release_mode", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("connection.url", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("connection.username", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("current_session_context_class", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("default_batch_fetch_size", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("default_catalog", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("default_entity_mode", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("default_schema", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("dialect", new String[] { } ); //$NON-NLS-1$
		 hibernatePropertyValues.put("format_sql", TRUE_FALSE ); //$NON-NLS-1$
		 hibernatePropertyValues.put("generate_statistics", TRUE_FALSE ); //$NON-NLS-1$
		 hibernatePropertyValues.put("hbm2ddl.auto", new String[] { "validate", "update", "create", "create-drop" } ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		 //hibernatePropertyValues.put("jacc_context_id", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("jdbc.batch_size", new String[] { } ); //$NON-NLS-1$
		 hibernatePropertyValues.put("jdbc.batch_versioned_data", TRUE_FALSE ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("jdbc.factory_class", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("jdbc.fetch_size", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("jdbc.sql_exception_converter", new String[] { } ); //$NON-NLS-1$
		 hibernatePropertyValues.put("jdbc.use_get_generated_keys", TRUE_FALSE ); //$NON-NLS-1$
		 hibernatePropertyValues.put("jdbc.use_scrollable_resultset", TRUE_FALSE ); //$NON-NLS-1$
		 hibernatePropertyValues.put("jdbc.use_streams_for_binary", TRUE_FALSE ); //$NON-NLS-1$
		 hibernatePropertyValues.put("jdbc.wrap_result_sets", TRUE_FALSE ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("jndi", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("jndi.class", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("jndi.url", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("max_fetch_depth", new String[] { } ); //$NON-NLS-1$
		 hibernatePropertyValues.put("order_inserts", TRUE_FALSE ); //$NON-NLS-1$
		 hibernatePropertyValues.put("order_updates", TRUE_FALSE ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("proxool", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("proxool.existing_pool", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("proxool.pool_alias", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("proxool.properties", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("proxool.xml", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("query.factory_class", new String[] { } ); //$NON-NLS-1$
		 hibernatePropertyValues.put("query.jpaql_strict_compliance", TRUE_FALSE ); //$NON-NLS-1$
		 hibernatePropertyValues.put("query.startup_check", TRUE_FALSE ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("query.substitutions", new String[] { } ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("session_factory_name", new String[] { } ); //$NON-NLS-1$
		 hibernatePropertyValues.put("show_sql", TRUE_FALSE ); //$NON-NLS-1$
		 hibernatePropertyValues.put("transaction.auto_close_session", TRUE_FALSE ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("transaction.factory_class", new String[] { } ); //$NON-NLS-1$
		 hibernatePropertyValues.put("transaction.flush_before_completion", TRUE_FALSE ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("transaction.manager_lookup_class", new String[] { } ); //$NON-NLS-1$
		 hibernatePropertyValues.put("use_identifier_rollback", TRUE_FALSE ); //$NON-NLS-1$
		 hibernatePropertyValues.put("use_sql_comments", TRUE_FALSE ); //$NON-NLS-1$
		 //hibernatePropertyValues.put("xml.output_stylesheet]", new String[] { } ); //$NON-NLS-1$

	}

	private String[] extractHibernateProperties() {
		try {
			// TODO: extract property names from the Environment class in the users hibernate configuration.
			Class cl = Environment.class;
			List names = new ArrayList();
			Field[] fields = cl.getFields();
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				if(Modifier.isStatic(field.getModifiers() ) &&
						field.getType().equals(String.class) ) {
					String str = (String) field.get(cl);
					if(str.startsWith("hibernate.") ) { //$NON-NLS-1$
						names.add(str);
					}
				}
			}
			String[] propertyNames = (String[]) names.toArray(new String[names.size()]);
			Arrays.sort(propertyNames);
			return propertyNames;
		} catch (IllegalAccessException iae) {
			// ignore
			return new String[0];
		}
	}

	private void setupTypeFinder() {

		javaTypeProvider.put("class", "name"); //$NON-NLS-1$ //$NON-NLS-2$
		javaTypeProvider.put("subclass", "name"); //$NON-NLS-1$ //$NON-NLS-2$
		javaTypeProvider.put("joined-subclass", "name"); //$NON-NLS-1$ //$NON-NLS-2$
		javaTypeProvider.put("union-subclass", "name"); //$NON-NLS-1$ //$NON-NLS-2$
		// TODO: use eclipse java model to infer types of components property/fields
		javaTypeProvider.put("composite-id", "class"); //$NON-NLS-1$ //$NON-NLS-2$
		javaTypeProvider.put("component", "class"); //$NON-NLS-1$ //$NON-NLS-2$
		javaTypeProvider.put("composite-element", "class"); //$NON-NLS-1$ //$NON-NLS-2$

		javaTypeProvider.put("many-to-one", "class"); //$NON-NLS-1$ //$NON-NLS-2$
		javaTypeProvider.put("one-to-many", "class"); //$NON-NLS-1$ //$NON-NLS-2$
		javaTypeProvider.put("many-to-many", "class"); //$NON-NLS-1$ //$NON-NLS-2$
		javaTypeProvider.put("composite-element", "class"); //$NON-NLS-1$ //$NON-NLS-2$
		javaTypeProvider.put("composite-id", "class"); //$NON-NLS-1$ //$NON-NLS-2$
		javaTypeProvider.put("key-many-to-one", "class"); //$NON-NLS-1$ //$NON-NLS-2$
		javaTypeProvider.put("one-to-many", "class"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void setupTableFinder() {
		tableProvider.put("class", "table"); //$NON-NLS-1$ //$NON-NLS-2$
		tableProvider.put("join", "table"); //$NON-NLS-1$ //$NON-NLS-2$
		tableProvider.put("joined-subclass", "table"); //$NON-NLS-1$ //$NON-NLS-2$
		tableProvider.put("union-subclass", "table"); //$NON-NLS-1$ //$NON-NLS-2$
		tableProvider.put("map", "table"); //$NON-NLS-1$ //$NON-NLS-2$
		tableProvider.put("set", "table"); //$NON-NLS-1$ //$NON-NLS-2$
		tableProvider.put("bag", "table"); //$NON-NLS-1$ //$NON-NLS-2$
		tableProvider.put("idbag", "table"); //$NON-NLS-1$ //$NON-NLS-2$
		tableProvider.put("list", "table"); //$NON-NLS-1$ //$NON-NLS-2$
		tableProvider.put("array", "table"); //$NON-NLS-1$ //$NON-NLS-2$
		tableProvider.put("primitive-array", "table"); //$NON-NLS-1$ //$NON-NLS-2$
		tableProvider.put("synchronize", "table"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void setupHibernateTypeDescriptors() {
		List types = new ArrayList();
		addType("long","java.lang.Long","long", types);  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
		addType("short","java.lang.Short","short", types);  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
		addType("integer","java.lang.Integer","int", types);  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
		addType("byte","java.lang.Byte","byte", types);  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
		addType("float","java.lang.Float","float", types);  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
		addType("double","java.lang.Double","double", types);  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
		addType("character","java.lang.Character","char", types);  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
		addType("string","java.lang.String",null, types);  //$NON-NLS-1$ //$NON-NLS-2$
		addType("time","java.util.Date",null, types);  //$NON-NLS-1$ //$NON-NLS-2$
		addType("date","java.util.Date",null, types);  //$NON-NLS-1$ //$NON-NLS-2$
		addType("timestamp","java.util.Date",null, types);  //$NON-NLS-1$ //$NON-NLS-2$
		addType("boolean","java.lang.Boolean","boolean", types);  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
		addType("true_false","java.lang.Boolean","boolean", types);  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
		addType("yes_no","java.lang.Boolean","boolean", types);  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
		addType("big_decimal","java.math.BigDecimal",null, types);  //$NON-NLS-1$ //$NON-NLS-2$
		addType("binary","byte[]",null, types);  //$NON-NLS-1$ //$NON-NLS-2$
		addType("text","java.lang.String",null, types);  //$NON-NLS-1$ //$NON-NLS-2$
		addType("blob","java.sql.Blob",null, types);  //$NON-NLS-1$ //$NON-NLS-2$
		addType("clob","java.sql.Clob",null, types);  //$NON-NLS-1$ //$NON-NLS-2$
		addType("calendar","java.util.Calendar",null, types);  //$NON-NLS-1$ //$NON-NLS-2$
		addType("calendar_date","java.util.Calendar",null, types);  //$NON-NLS-1$ //$NON-NLS-2$
		addType("locale","java.util.Locale",null, types);  //$NON-NLS-1$ //$NON-NLS-2$
		addType("currency","java.util.Currency",null, types);  //$NON-NLS-1$ //$NON-NLS-2$
		addType("timezone","java.util.TimeZone",null, types);  //$NON-NLS-1$ //$NON-NLS-2$
		addType("class","java.lang.Class",null, types);  //$NON-NLS-1$ //$NON-NLS-2$
		addType("serializable","java.io.Serializable",null, types);  //$NON-NLS-1$ //$NON-NLS-2$
		addType("object","java.lang.Object",null, types);  //$NON-NLS-1$ //$NON-NLS-2$
		Collections.sort(types);
		hibernateTypes = (HibernateTypeDescriptor[]) types.toArray(new HibernateTypeDescriptor[types.size()]);
	}

	private void setupGeneratorClassHandlers() {
		List types = new ArrayList();
		addType("native", "Database dependent", null, types); //$NON-NLS-1$ //$NON-NLS-2$
		addType("uuid", "UUIDHexGenerator", null, types); //$NON-NLS-1$ //$NON-NLS-2$
		addType("hilo", "TableHiLoGenerator", null, types); //$NON-NLS-1$ //$NON-NLS-2$
		addType("assigned", "Assigned", null, types); //$NON-NLS-1$ //$NON-NLS-2$
		addType("identity", "IdentityGenerator", null, types); //$NON-NLS-1$ //$NON-NLS-2$
		addType("select", "SelectGenerator", null, types); //$NON-NLS-1$ //$NON-NLS-2$
		addType("sequence", "SequenceGenerator", null, types); //$NON-NLS-1$ //$NON-NLS-2$
		addType("seqhilo", "SequenceHiLoGenerator", null, types); //$NON-NLS-1$ //$NON-NLS-2$
		addType("increment", "IncrementGenerator", null, types); //$NON-NLS-1$ //$NON-NLS-2$
		addType("foreign", "ForeignGenerator", null, types); //$NON-NLS-1$ //$NON-NLS-2$
		addType("guid", "GUIDGenerator", null, types); //$NON-NLS-1$ //$NON-NLS-2$
		Collections.sort(types);
		generatorTypes = (HibernateTypeDescriptor[]) types.toArray(new HibernateTypeDescriptor[types.size()]);
	}

	private void setupHibernateTypeHandlers() {
		HBMInfoHandler hibernateTypeFinder = new HibernateTypeHandler(this);
		attributeHandlers.put("filter-param>type", hibernateTypeFinder); //$NON-NLS-1$
		attributeHandlers.put("id>type", hibernateTypeFinder); //$NON-NLS-1$
		attributeHandlers.put("discriminator>type", hibernateTypeFinder); //$NON-NLS-1$
		attributeHandlers.put("version>type", hibernateTypeFinder); //$NON-NLS-1$
		attributeHandlers.put("property>type", hibernateTypeFinder); //$NON-NLS-1$
		attributeHandlers.put("key-property>type", hibernateTypeFinder); //$NON-NLS-1$
		attributeHandlers.put("element>type", hibernateTypeFinder); //$NON-NLS-1$
		attributeHandlers.put("map-key>type", hibernateTypeFinder); //$NON-NLS-1$
		attributeHandlers.put("index>type", hibernateTypeFinder); //$NON-NLS-1$
		attributeHandlers.put("collection-id>type", hibernateTypeFinder); //$NON-NLS-1$
		attributeHandlers.put("return-scalar>type", hibernateTypeFinder); //$NON-NLS-1$
		HBMInfoHandler generatorClassFinder = new GeneratorTypeHandler(this);
		attributeHandlers.put("generator>class", generatorClassFinder); //$NON-NLS-1$
	}

	private void setupTableNameHandlers() {
		HBMInfoHandler hih = new TableNameHandler();
		attributeHandlers.put("class>table", hih); //$NON-NLS-1$
		attributeHandlers.put("join>table", hih); //$NON-NLS-1$
		attributeHandlers.put("joined-subclass>table", hih); //$NON-NLS-1$
		attributeHandlers.put("union-subclass>table", hih); //$NON-NLS-1$
		attributeHandlers.put("map>table", hih); //$NON-NLS-1$
		attributeHandlers.put("set>table", hih); //$NON-NLS-1$
		attributeHandlers.put("bag>table", hih); //$NON-NLS-1$
		attributeHandlers.put("idbag>table", hih); //$NON-NLS-1$
		attributeHandlers.put("list>table", hih); //$NON-NLS-1$
		attributeHandlers.put("array>table", hih); //$NON-NLS-1$
		attributeHandlers.put("primitive-array>table", hih); //$NON-NLS-1$
		attributeHandlers.put("synchronize>table", hih);	 //$NON-NLS-1$
	}

	private void setupColumnNameHandlers() {
		HBMInfoHandler hih = new ColumnNameHandler(this);
		attributeHandlers.put("id>column", hih); //$NON-NLS-1$
		attributeHandlers.put("discriminator>column", hih); //$NON-NLS-1$
		attributeHandlers.put("version>column", hih); //$NON-NLS-1$
		attributeHandlers.put("timestamp>column", hih); //$NON-NLS-1$
		attributeHandlers.put("property>column", hih); //$NON-NLS-1$
		attributeHandlers.put("many-to-one>column", hih); //$NON-NLS-1$
		attributeHandlers.put("key-property>column", hih); //$NON-NLS-1$
		attributeHandlers.put("key-many-to-one>column", hih); //$NON-NLS-1$
		attributeHandlers.put("element>column", hih); //$NON-NLS-1$
		attributeHandlers.put("many-to-many>column", hih); //$NON-NLS-1$
		attributeHandlers.put("key>column", hih); //$NON-NLS-1$
		attributeHandlers.put("list-index>column", hih); //$NON-NLS-1$
		attributeHandlers.put("map-key>column", hih); //$NON-NLS-1$
		attributeHandlers.put("index>column", hih); //$NON-NLS-1$
		attributeHandlers.put("map-key-many-to-many>column", hih); //$NON-NLS-1$
		attributeHandlers.put("index-many-to-many>column", hih); //$NON-NLS-1$
		attributeHandlers.put("collection-id>column", hih); //$NON-NLS-1$
		attributeHandlers.put("column>name", hih); //$NON-NLS-1$
		attributeHandlers.put("return-property>column", hih); //$NON-NLS-1$
		attributeHandlers.put("return-column>column", hih); //$NON-NLS-1$
		attributeHandlers.put("return-discriminator>column", hih); //$NON-NLS-1$
		attributeHandlers.put("return-scalar>column", hih); //$NON-NLS-1$

	}

	private void setupAccessHandlers() {
		List types = new ArrayList();
		addType("property", MapperMessages.HBMInfoExtractor_use_javabean_accessor_methods, null, types); //$NON-NLS-1$
		addType("field", MapperMessages.HBMInfoExtractor_access_fields_directly, null, types); //$NON-NLS-1$
		addType("noop", MapperMessages.HBMInfoExtractor_do_not_perform_any_access, null, types); //$NON-NLS-1$
		Collections.sort(types);
		propertyAccessors = (HibernateTypeDescriptor[]) types.toArray(new HibernateTypeDescriptor[types.size()]);

		HBMInfoHandler hih = new PropertyAccessHandler(this);
		attributeHandlers.put("hibernate-mapping>default-access", hih); //$NON-NLS-1$
		attributeHandlers.put("id>access", hih); //$NON-NLS-1$
		attributeHandlers.put("composite-id>access", hih); //$NON-NLS-1$
		attributeHandlers.put("version>access", hih); //$NON-NLS-1$
		attributeHandlers.put("timestamp>access", hih); //$NON-NLS-1$
		attributeHandlers.put("property>access", hih); //$NON-NLS-1$
		attributeHandlers.put("many-to-one>access", hih); //$NON-NLS-1$
		attributeHandlers.put("one-to-one>access", hih); //$NON-NLS-1$
		attributeHandlers.put("key-property>access", hih); //$NON-NLS-1$
		attributeHandlers.put("key-many-to-one>access", hih); //$NON-NLS-1$
		attributeHandlers.put("any>access", hih); //$NON-NLS-1$
		attributeHandlers.put("component>access", hih); //$NON-NLS-1$
		attributeHandlers.put("dynamic-component>access", hih); //$NON-NLS-1$
		attributeHandlers.put("map>access", hih); //$NON-NLS-1$
		attributeHandlers.put("set>access", hih); //$NON-NLS-1$
		attributeHandlers.put("bag>access", hih); //$NON-NLS-1$
		attributeHandlers.put("idbag>access", hih); //$NON-NLS-1$
		attributeHandlers.put("list>access", hih); //$NON-NLS-1$
		attributeHandlers.put("array>access", hih); //$NON-NLS-1$
		attributeHandlers.put("primitive-array>access", hih); //$NON-NLS-1$
		attributeHandlers.put("nested-composite-element>access", hih); //$NON-NLS-1$
	}


	private void setupFieldsPropertyHandlers() {

		HBMInfoHandler fieldsFinder = new FieldPropertyHandler(this);
		attributeHandlers.put("version>name", fieldsFinder); //$NON-NLS-1$
		attributeHandlers.put("timestamp>name", fieldsFinder); //$NON-NLS-1$
		attributeHandlers.put("property>name", fieldsFinder); //$NON-NLS-1$
		attributeHandlers.put("key-property>name", fieldsFinder); //$NON-NLS-1$
		attributeHandlers.put("id>name", fieldsFinder); //$NON-NLS-1$
		attributeHandlers.put("composite-id>name", fieldsFinder); //$NON-NLS-1$
		attributeHandlers.put("set>name", fieldsFinder); //$NON-NLS-1$
		attributeHandlers.put("key-property>name", fieldsFinder); //$NON-NLS-1$
		attributeHandlers.put("property>name", fieldsFinder); //$NON-NLS-1$
		attributeHandlers.put("key-many-to-one>name", fieldsFinder); //$NON-NLS-1$
		attributeHandlers.put("many-to-one>name", fieldsFinder); //$NON-NLS-1$
		attributeHandlers.put("one-to-one>name", fieldsFinder); //$NON-NLS-1$
		attributeHandlers.put("component>name", fieldsFinder); //$NON-NLS-1$
		attributeHandlers.put("dynamic-component>name", fieldsFinder); //$NON-NLS-1$
		attributeHandlers.put("properties>name", fieldsFinder); //$NON-NLS-1$
		attributeHandlers.put("any>name", fieldsFinder); //$NON-NLS-1$
		attributeHandlers.put("map>name", fieldsFinder); //$NON-NLS-1$
		attributeHandlers.put("set>name", fieldsFinder); //$NON-NLS-1$
		attributeHandlers.put("list>name", fieldsFinder); //$NON-NLS-1$
		attributeHandlers.put("bag>name", fieldsFinder); //$NON-NLS-1$
		attributeHandlers.put("idbag>name", fieldsFinder); //$NON-NLS-1$
		attributeHandlers.put("array>name", fieldsFinder); //$NON-NLS-1$
		attributeHandlers.put("primitive-array>name", fieldsFinder); //$NON-NLS-1$
		attributeHandlers.put("query-list>name", fieldsFinder); //$NON-NLS-1$
	}

	private void setupPackageHandlers() {
		HBMInfoHandler packageFinder = new PackageHandler(this);
		attributeHandlers.put("hibernate-mapping>package", packageFinder); //$NON-NLS-1$
	}

	private void setupJavaTypeHandlers() {
		HBMInfoHandler classFinder = new JavaTypeHandler(this);
		attributeHandlers.put("class>name", classFinder); //$NON-NLS-1$
		attributeHandlers.put("subclass>name", classFinder); //$NON-NLS-1$
		attributeHandlers.put("joined-subclass>name", classFinder); //$NON-NLS-1$
		attributeHandlers.put("union-subclass>name", classFinder); //$NON-NLS-1$
		attributeHandlers.put("many-to-one>class", classFinder); //$NON-NLS-1$
		attributeHandlers.put("one-to-many>class", classFinder); //$NON-NLS-1$
		attributeHandlers.put("many-to-many>class", classFinder); //$NON-NLS-1$
		attributeHandlers.put("composite-element>class", classFinder); //$NON-NLS-1$
		attributeHandlers.put("component>class", classFinder); //$NON-NLS-1$
		attributeHandlers.put("composite-id>class", classFinder); //$NON-NLS-1$
		attributeHandlers.put("key-many-to-one>class", classFinder); //$NON-NLS-1$
	}

	List findMatchingHibernateTypes(String item) {
		return findInTypes( item, hibernateTypes );
	}

	private List findInTypes(String item, HibernateTypeDescriptor[] types) {
		List l = new ArrayList();
		boolean foundFirst = false;
		for (int i = 0; i < types.length; i++) {
			HibernateTypeDescriptor element = types[i];
			if(element.getName().startsWith(item) ) {
				foundFirst = true;
				l.add(element);
			} else if (foundFirst) {
				return l; // fail fast since if we dont get a match no future match can be found.
			}
		}
		return l;
	}

	public List findMatchingGenerators(String start) {
		return findInTypes(start, generatorTypes);
	}


	public List findMatchingPropertyTypes(String prefix) {
		List l = new ArrayList();
		boolean foundFirst = false;
		for (int i = 0; i < hibernatePropertyNames.length; i++) {
			String element = hibernatePropertyNames[i];
			if(element.startsWith(prefix) ) {
				foundFirst = true;
				l.add(element);
			} else if (element.startsWith("hibernate." + prefix) ) { //$NON-NLS-1$
				foundFirst = true;
				l.add(element.substring("hibernate.".length() ) ); //$NON-NLS-1$
			} else if (foundFirst) {
				return l; // fail fast since if we dont get a match no future match can be found.
			}
		}
		return l;
	}

	/**
	 * @param holder
	 * @param root TODO
	 * @return nearest package attribute, null if none found.
	 */
	protected String getPackageName(Node root) {
		if(root!=null) {
			while(!"hibernate-mapping".equals(root.getNodeName() ) ) { //$NON-NLS-1$
				root = root.getParentNode();
				if(root==null) return null;
			}
			NamedNodeMap attributes = root.getAttributes();
			for(int count = 0; count<attributes.getLength(); count++) {
				Node att = attributes.item(count);
				if("package".equals(att.getNodeName() ) ) { //$NON-NLS-1$
					return att.getNodeValue();
				}
			}
		}
		return null;
	}

	protected boolean beginsWith(String aString, String prefix) {
		if (aString == null || prefix == null)
			return true;
		// (pa) 221190 matching independent of case to be consistant with Java
		// editor CA
		return aString.toLowerCase().startsWith(prefix.toLowerCase() );
	}

	void generateTypeProposals(String matchString, int offset, List proposals, Set alreadyFound, IType[] classes, String filterPackage) throws JavaModelException {
		for (int j = 0; j < classes.length; j++) {
			IType type = classes[j];
			if (!Flags.isAbstract(type.getFlags() ) && (filterPackage==null || !type.getFullyQualifiedName().startsWith(filterPackage)) ) {
				String fullName = type.getFullyQualifiedName();
				String shortName = type.getElementName();
				if(alreadyFound.contains(fullName) ) {
					continue;
				} else {
					alreadyFound.add(fullName);
				}
				if (beginsWith(fullName,matchString) || beginsWith(shortName,matchString) ) {
					CustomCompletionProposal proposal = new CustomCompletionProposal(fullName,
							offset, matchString.length(), fullName.length() + 1, null/*XMLEditorPluginImageHelper.getInstance().getImage(XMLEditorPluginImages.IMG_OBJ_ATTRIBUTE)*/,
							fullName, null, null, XMLRelevanceConstants.R_XML_ATTRIBUTE_VALUE);
					proposals.add(proposal);
				}
			}
		}
	}



	private void addType(String name, String returnClass, String primitiveClass, Collection hibernateTypes) {
		hibernateTypes.add(new HibernateTypeDescriptor(name, returnClass, primitiveClass) );
	}

	/**
	 * Returns attribute handler for the path.
	 * @param path a string on the form [xmltag]>[attributename] e.g. property>name
	 * @return
	 */
	public HBMInfoHandler getAttributeHandler(String path) {
		HBMInfoHandler infoHandler = (HBMInfoHandler) attributeHandlers.get(path);
		return infoHandler;
	}

	/**
	 * @param node
	 * @return the name of the nearest type from the node or null if none found.
	 */
	private String getNearestType(Node node) {
		Map map = javaTypeProvider;

		if(node==null) return null;

		while(!map.containsKey(node.getNodeName() ) ) {
			node = node.getParentNode();
			if(node==null) return null;
		}

		String attributeName = (String) map.get(node.getNodeName() );
		NamedNodeMap attributes = node.getAttributes();

		Node att = attributes.getNamedItem(attributeName);
		if(att!=null && attributeName.equals(att.getNodeName() ) ) {
			String typename = att.getNodeValue();
			if(typename!=null && typename.indexOf('.')<0) {
				String packageName = getPackageName(node);
				if(packageName!=null) {
					typename = packageName + "." + typename; //$NON-NLS-1$
				}
			}
			return typename;
		}

		return null;
	}

	public String getNearestType(IJavaProject project, Node parentNode) {
		String typename = getNearestType(parentNode);
		if(typename!=null) return typename;

		try {
			if("component".equals(parentNode.getNodeName())) { // probably need to integrate this into extractor? //$NON-NLS-1$
				Node componentPropertyNodeName = parentNode.getAttributes().getNamedItem("name"); //$NON-NLS-1$
				if(componentPropertyNodeName!=null) {
					String parentTypeName = getNearestType(project, parentNode.getParentNode());
					if(parentTypeName!=null) {
						String componentName = componentPropertyNodeName.getNodeValue();
						IType parentType = project.findType(parentTypeName);
						if(parentType!=null) { // check to avoid null pointer exception
							IField field = parentType.getField(componentName);
							if(field.exists()) {
								String fieldTypeSignature = field.getTypeSignature();
								String qualifier = Signature.getSignatureQualifier(fieldTypeSignature);
								String simpleName = Signature.getSignatureSimpleName(fieldTypeSignature);
								if(!StringHelper.isEmpty(qualifier)) {
									simpleName = Signature.toQualifiedName(new String[] { qualifier, simpleName });
								}
	
								String[][] possibleTypes = null;
								possibleTypes = parentType.resolveType(simpleName);
								if(possibleTypes != null && possibleTypes.length>0) {
									typename = Signature.toQualifiedName(possibleTypes[0]);
								}
	
							}
						}
					}
				}
			}
		} catch(JavaModelException jme) {
			// ignore, reset typename for safety
			typename=null;
		}
		return typename;
	}

	public TableIdentifier getNearestTableName(Node node) {
		Map map = tableProvider;

		if(node==null) return null;

		while(!map.containsKey(node.getNodeName() ) ) {
			node = node.getParentNode();
			if(node==null) return null;
		}

		String attributeName = (String) map.get(node.getNodeName() );
		NamedNodeMap attributes = node.getAttributes();

		Node att = attributes.getNamedItem(attributeName);
		if(att!=null && attributeName.equals(att.getNodeName() ) ) {
			String typename = att.getNodeValue();
			String catalog = null;
			String schema = null;

			Node namedItem = attributes.getNamedItem("catalog"); //$NON-NLS-1$
			if(namedItem!=null) {
				catalog = namedItem.getNodeValue();
			}

			namedItem = attributes.getNamedItem("schema"); //$NON-NLS-1$
			if(namedItem!=null) {
				schema = namedItem.getNodeValue();
			}

			return new TableIdentifier(catalog,schema,typename);
		}

		return null;
	}

	public IType getNearestTypeJavaElement(IJavaProject project, Node currentNode) {
		String nearestType = getNearestType(project, currentNode);
		if(nearestType!=null) {
			try {
				IType type = project.findType(nearestType);
				return type;
			} catch (JavaModelException e) {
				//ignore
			}
		}
		return null;
	}


	public List findMatchingAccessMethods(String start) {
		return findInTypes(start, propertyAccessors);
	}


	public List findMatchingPropertyValues(String matchString, Node node) {
		if(node==null) return Collections.EMPTY_LIST;

		NamedNodeMap attributes = node.getAttributes();
		Node namedItem = attributes.getNamedItem("name"); //$NON-NLS-1$
		String propName = namedItem.getNodeValue();
		if(propName.startsWith("hibernate.")) { //$NON-NLS-1$
			propName = propName.substring("hibernate.".length()); //$NON-NLS-1$
		}
		String[] strings = (String[]) hibernatePropertyValues.get(propName);
		if(strings==null) {
			return Collections.EMPTY_LIST;
		} else {
			List matches = new ArrayList(strings.length);
			for (int i = 0; i < strings.length; i++) {
				String string = strings[i];
				if(string.startsWith(matchString)) {
					matches.add(string);
				}
			}

			return  matches;
		}
	}

	}
