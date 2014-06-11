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

import org.eclipse.osgi.util.NLS;
import org.hibernate.type.Type;
import org.jboss.tools.hibernate.spi.IClassMetadata;

/**
 * @author MAX
 *
 */
class TypeNode extends ClassNode implements TypedNode{

	Type type;
	public TypeNode(NodeFactory factory, BaseNode parent, Type type, IClassMetadata metadata, Object baseObject, boolean objectGraph) {
		super(factory, parent, type.getReturnedClass().getName(), metadata, baseObject, objectGraph);
		this.type = type;
				
		iconName = factory.getIconNameForType(type);
	}

	@Override
	public String getCriteria() {
		final String criteria = ".createCriteria({0})"; //$NON-NLS-1$
		final String alias = "\n.createCriteria(\"{0}\", \"{1}\")"; //$NON-NLS-1$
		final String sess = "session"; //$NON-NLS-1$
		String enName = "";//$NON-NLS-1$
		String propCriteria = "";//$NON-NLS-1$
		if (getName() != null){
			if (getParent() instanceof BaseNode) {
				BaseNode baseNodeParent = (BaseNode)getParent();
				if (baseNodeParent instanceof TypedNode) {
					TypedNode typedNodeParent = (TypedNode)baseNodeParent;
					enName = typedNodeParent.getType().getName();
				} else {
					enName = baseNodeParent.getName();
				}
				enName = enName.substring(enName.lastIndexOf('.') + 1);
				propCriteria = NLS.bind(alias, getName(), getName().charAt(0));
			}
		}
		if ("".equals(enName)) { //$NON-NLS-1$
			return ""; //$NON-NLS-1$
		}
		String enCriteria = NLS.bind(criteria, enName + ".class"); //$NON-NLS-1$
		return sess + enCriteria + propCriteria;
	}
	
	public String renderLabel(boolean b) {
		return super.renderLabel(b) + " : " + getLabel(type.getReturnedClass().getName(),b); //$NON-NLS-1$
	}

	/**
	 * 
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @param string
	 */
	public void setName(String string) {
		name = string;
		
	}

}
