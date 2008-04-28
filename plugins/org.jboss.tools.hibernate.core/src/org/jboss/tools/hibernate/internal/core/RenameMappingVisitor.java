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

import java.util.Map;
import org.jboss.tools.hibernate.core.hibernate.IAnyMapping;
import org.jboss.tools.hibernate.core.hibernate.IArrayMapping;
import org.jboss.tools.hibernate.core.hibernate.IComponentMapping;
import org.jboss.tools.hibernate.core.hibernate.IHibernateClassMapping;
import org.jboss.tools.hibernate.core.hibernate.IManyToManyMapping;
import org.jboss.tools.hibernate.core.hibernate.IManyToOneMapping;
import org.jboss.tools.hibernate.core.hibernate.IOneToManyMapping;
import org.jboss.tools.hibernate.core.hibernate.IOneToOneMapping;

public class RenameMappingVisitor extends BaseMappingVisitor {

	private String oldClassName;
	private String newClassName;
	public RenameMappingVisitor(String oldClassName, String newClassName){
		this.oldClassName=oldClassName;
		this.newClassName=newClassName;
	}
	
	public Object visitHibernateClassMapping(IHibernateClassMapping mapping, Object argument){
		if(oldClassName.equals(mapping.getClassName())){
			mapping.setClassName(newClassName);
		}
		if(oldClassName.equals(mapping.getEntityName())){
			mapping.setEntityName(newClassName);
		}
		return super.visitHibernateClassMapping(mapping, argument);
	}	
	public Object visitAnyMapping(IAnyMapping mapping, Object argument) {
		Map<String,String> metaValues = mapping.getMetaValues();
		if(metaValues!=null && metaValues.size()>0){
			String keys[] = (String[])metaValues.keySet().toArray(new String[metaValues.size()]);
			for(int i=0;i<keys.length;++i){
				String className = (String) metaValues.get(keys[i]);
				if(oldClassName.equals(className)) metaValues.put(keys[i],newClassName);
			}
		}
		return super.visitAnyMapping(mapping, argument);
	}
	public Object visitArrayMapping(IArrayMapping mapping, Object argument) {
		if(oldClassName.equals(mapping.getElementClassName())){
			mapping.setElementClassName(newClassName);
		}
		return super.visitArrayMapping(mapping, argument);
	}
	
	public Object visitComponentMapping(IComponentMapping mapping, Object argument) {
		if(oldClassName.equals(mapping.getComponentClassName())){
			mapping.setComponentClassName(newClassName);
		}
		return super.visitComponentMapping(mapping, argument);
	}
	public Object visitManyToManyMapping(IManyToManyMapping mapping, Object argument) {
		if(oldClassName.equals(mapping.getReferencedEntityName())){
			mapping.setReferencedEntityName(newClassName);
		}
		return super.visitManyToManyMapping(mapping, argument);
	}
	public Object visitManyToOneMapping(IManyToOneMapping mapping, Object argument) {
		if(oldClassName.equals(mapping.getReferencedEntityName())){
			mapping.setReferencedEntityName(newClassName);
		}
		return super.visitManyToOneMapping(mapping, argument);
	}
	public Object visitOneToManyMapping(IOneToManyMapping mapping, Object argument) {
		if(oldClassName.equals(mapping.getReferencedEntityName())){
			mapping.setReferencedEntityName(newClassName);
		}
		return super.visitOneToManyMapping(mapping, argument);
	}
	public Object visitOneToOneMapping(IOneToOneMapping mapping, Object argument) {
		if(oldClassName.equals(mapping.getReferencedEntityName())){
			mapping.setReferencedEntityName(newClassName);
		}
		return super.visitOneToOneMapping(mapping, argument);
	}
}
