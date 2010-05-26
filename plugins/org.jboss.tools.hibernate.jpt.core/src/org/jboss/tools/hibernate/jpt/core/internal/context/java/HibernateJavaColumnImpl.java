/*******************************************************************************
 * Copyright (c) 2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.context.java;

import org.eclipse.jpt.core.context.java.JavaColumn;
import org.eclipse.jpt.core.context.java.JavaJpaContextNode;
import org.eclipse.jpt.core.internal.context.java.GenericJavaColumn;
import org.eclipse.jpt.core.resource.java.ColumnAnnotation;
import org.eclipse.jpt.db.Column;
import org.eclipse.jpt.db.Table;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.hibernate.cfg.NamingStrategy;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaProject;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJptPlugin;
import org.jboss.tools.hibernate.jpt.core.internal.context.Messages;
import org.jboss.tools.hibernate.jpt.core.internal.context.HibernatePersistenceUnit.LocalMessage;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJavaColumnImpl extends GenericJavaColumn implements HibernateJavaColumn {
	
	public HibernateJavaColumnImpl(JavaJpaContextNode parent, JavaColumn.Owner owner) {
		super(parent, owner);
	}

	@Override
	public HibernateJpaProject getJpaProject() {
		return (HibernateJpaProject) super.getJpaProject();
	}
	
	@Override
	public void initialize(ColumnAnnotation column) {
		super.initialize(column);
		//this.initializGenerated();
	}
	
	//********* DB Column name ******************

	@Override
	public Column getDbColumn() {
		Table table = this.getDbTable();
		return (table == null) ? null : table.getColumnForIdentifier(this.getDBColumnName());
	}

	public String getDBColumnName(){
		return getSpecifiedDBColumnName() != null ? getSpecifiedDBColumnName()
				: getDefaultDBColumnName();
	}

	public String getSpecifiedDBColumnName(){
		if (getSpecifiedName() == null) return null;
		NamingStrategy ns = getJpaProject().getNamingStrategy();
		if (getJpaProject().isNamingStrategyEnabled() && ns != null){
			try {
				return ns.columnName(getSpecifiedName());
			} catch (Exception e) {
				Message m = new LocalMessage(IMessage.HIGH_SEVERITY,
						Messages.NAMING_STRATEGY_EXCEPTION, new String[0], null);
				HibernateJptPlugin.logException(m.getText(), e);
			}
		}
		return this.getSpecifiedName();
	}
	
	public String getDefaultDBColumnName() {
		return getDefaultName();
	}
		
	@Override
	protected String buildDefaultName() {
		NamingStrategy ns = getJpaProject().getNamingStrategy();
		if ( getJpaProject().isNamingStrategyEnabled() && ns != null && super.buildDefaultName() != null){
			try {
				return ns.propertyToColumnName(super.buildDefaultName());
			} catch (Exception e) {
				Message m = new LocalMessage(IMessage.HIGH_SEVERITY, 
						Messages.NAMING_STRATEGY_EXCEPTION, new String[0], null);
				HibernateJptPlugin.logException(m.getText(), e);
			}
		}
		return super.buildDefaultName();
	}
	
	//************ DB Table name ***********

	public String getDBTableName() {
		return getSpecifiedDBTableName() != null ? getSpecifiedDBTableName()
				: getDefaultDBTableName();
	}

	public String getDefaultDBTableName() {
		return getDefaultTable();
	}

	public String getSpecifiedDBTableName() {
		if (getSpecifiedTable() == null) return null;
		NamingStrategy ns = getJpaProject().getNamingStrategy();
		if (getJpaProject().isNamingStrategyEnabled() && ns != null){
			try {
				return ns.tableName(getSpecifiedTable());
			} catch (Exception e) {
				Message m = new LocalMessage(IMessage.HIGH_SEVERITY,
						Messages.NAMING_STRATEGY_EXCEPTION, new String[0], null);
				HibernateJptPlugin.logException(m.getText(), e);
			}
		}
		return this.getSpecifiedTable();
	}
	
	//********** Generated ************
	/*private void initializGenerated() {
		GeneratedAnnotation generatedResource = getGeneratedResource();
		if (generatedResource != null) {
			this.generated = buildGenerated(generatedResource);
		}
	}
	
	public GeneratedAnnotation getGeneratedResource() {
		return (GeneratedAnnotation) this.javaResourcePersistentType.getSupportingAnnotation(GeneratedAnnotation.ANNOTATION_NAME);
	}

	public JavaGenerated addGenerated() {
		// TODO Auto-generated method stub
		return null;
	}

	public JavaGenerated getGenerated() {
		// TODO Auto-generated method stub
		return null;
	}

	public void removeGenerated() {
		// TODO Auto-generated method stub
		
	}*/
	
}
