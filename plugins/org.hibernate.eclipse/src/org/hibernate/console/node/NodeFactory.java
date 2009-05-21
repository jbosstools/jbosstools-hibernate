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
package org.hibernate.console.node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.cglib.proxy.Enhancer;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ConsoleMessages;
import org.hibernate.console.ImageConstants;
import org.hibernate.mapping.Table;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.type.CollectionType;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;

/**
 * @author MAX
 */
public class NodeFactory {

	private Map<String, ClassMetadata> classMetaData;
	private List<String> classes;
	private Map<String, CollectionMetadata> collectionMetaData;
	private ConsoleConfiguration consoleConfiguration;





	/**
	 * @param c
	 */
	public NodeFactory(ConsoleConfiguration c) throws HibernateException {
		setConsoleConfiguration(c);
	}

	@SuppressWarnings("unchecked")
	private void setConsoleConfiguration(ConsoleConfiguration c) {
		consoleConfiguration = c;
		SessionFactory sf = c.getSessionFactory();
		classMetaData = sf.getAllClassMetadata();
        collectionMetaData = sf.getAllCollectionMetadata();
		classes = new ArrayList<String>();
		classes.addAll(classMetaData.keySet());
	}

    public ConfigurationEntitiesNode createConfigurationEntitiesNode(String name) {
    	Enhancer e = ProxyFactory.createEnhancer(ConfigurationEntitiesNode.class);

        return (ConfigurationEntitiesNode) e.create(new Class[] { String.class, NodeFactory.class, List.class },
        				new Object[] { name, this, classes });

        //return new RootNode(this, classes);
    }

    public BaseNode createObjectNode(Session session, Object o) throws HibernateException {
		ClassMetadata md = getMetaData(session.getEntityName(o) );
		return internalCreateClassNode(null, md.getEntityName(), md, o, false);
		//return new ClassNode(this,null,md.getEntityName(),md,o,true);
	}

	public ClassNode createClassNode(BaseNode node, String clazz) {
		return internalCreateClassNode(node, clazz, getMetaData(clazz), null, false);
		//return new ClassNode(this, node, clazz, getMetaData(clazz),null,false);
	}

	private ClassNode internalCreateClassNode(BaseNode node, String clazz,ClassMetadata md, Object o, boolean objectGraph) {

		Enhancer e = ProxyFactory.createEnhancer(ClassNode.class);

        return (ClassNode) e.create(new Class[] { NodeFactory.class, BaseNode.class, String.class, ClassMetadata.class, Object.class, boolean.class},
       		 new Object[] { this, node, clazz, md,o, Boolean.valueOf(objectGraph) } );
	}

	public ClassMetadata getMetaData(String clazz) {
		return classMetaData.get(clazz);
	}

	public ClassMetadata getMetaData(Class<?> clazz) {
		return getMetaData(clazz.getName() );
	}

     public CollectionMetadata getCollectionMetaData(String role) {
        return collectionMetaData.get(role);
     }

	public BaseNode createPropertyNode(BaseNode parent, int idx, ClassMetadata metadata) {
		return createPropertyNode(parent, idx, metadata, null,false);
	}

	public BaseNode createPropertyNode(BaseNode node, int i, ClassMetadata md, Object baseObject, boolean objectGraph) {
		Enhancer e = ProxyFactory.createEnhancer(PropertyNode.class);

        return (BaseNode) e.create(new Class[] { NodeFactory.class, BaseNode.class, int.class, ClassMetadata.class, Object.class, boolean.class},
        		 new Object[] { this, node, Integer.valueOf(i),md,baseObject,Boolean.valueOf(objectGraph) } );
	}


	/**
	 * @param node
	 * @param md
	 * @return
	 */
	public IdentifierNode createIdentifierNode(BaseNode parent, ClassMetadata md) {
		Enhancer e = ProxyFactory.createEnhancer(IdentifierNode.class);

        return (IdentifierNode) e.create(new Class[] { NodeFactory.class, BaseNode.class, ClassMetadata.class},
        		 new Object[] { this, parent, md } );
		//return new IdentifierNode(this, parent, md);
	}

	public BaseNode createNode(BaseNode parent, final Class<?> clazz) {
		ClassMetadata metadata = getMetaData(clazz);
		if(metadata!=null) {
			return createClassNode(parent, clazz.getName() );
		}

		return new BaseNode(this, parent) {
			public String getHQL() {
				return null;
			}

			public String getName() {
				return ConsoleMessages.NodeFactory_unknown + clazz;
			}

			protected void checkChildren() {
				// TODO Auto-generated method stub
			}
		};
	}

	public PersistentCollectionNode createPersistentCollectionNode(ClassNode node, String name, ClassMetadata md, CollectionType type, Object baseObject, boolean objectGraph) {
		Enhancer e = ProxyFactory.createEnhancer(PersistentCollectionNode.class);

        return (PersistentCollectionNode) e.create(
        		 new Class[] { NodeFactory.class, BaseNode.class, String.class, CollectionType.class, ClassMetadata.class, CollectionMetadata.class, Object.class, boolean.class},
        		 new Object[] { this, node, name, type,  md, getCollectionMetaData(type.getRole() ), baseObject, Boolean.valueOf(objectGraph) } );
		//return new PersistentCollectionNode(this, node, name, type,  md, getCollectionMetaData(type.getRole() ), baseObject, objectGraph);
	}

		public String getIconNameForType(Type type) {
			String result = ImageConstants.UNKNOWNPROPERTY;
			if(type.isEntityType() ) {
				EntityType et = (EntityType) type;
				if(!et.isOneToOne() ) {
					result = ImageConstants.MANYTOONE;
				} else {
					result = ImageConstants.ONETOONE;
				}
			} else if (type.isAnyType() ) {
				result = ImageConstants.ANY;
			} else if (type.isComponentType() ) {
				result = ImageConstants.COMPONENT;
			} else if (type.isCollectionType() ) {
				//CollectionType pct = (CollectionType)type;
				result = ImageConstants.ONETOMANY; //could also be values/collecionts?
			} else {
				result = ImageConstants.PROPERTY;
			}

			return result;
		}


		public ConsoleConfiguration getConsoleConfiguration() {
			return consoleConfiguration;
		}

		public static TableNode createTableNode(BaseNode parent, Table table) {
			return new TableNode(parent, table);
		}

}
