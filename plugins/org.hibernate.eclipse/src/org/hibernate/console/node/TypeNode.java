/*
 * Created on 03-08-2003
 *
 */
package org.hibernate.console.node;

import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;

/**
 * @author MAX
 *
 */
public class TypeNode extends ClassNode {

	Type type;
	public TypeNode(NodeFactory factory, BaseNode parent, Type type, ClassMetadata metadata, Object baseObject, boolean objectGraph) {
		super(factory, parent, type.getReturnedClass().getName(), metadata, baseObject, objectGraph);
		this.type = type;
				
		iconName = factory.getIconNameForType(type);
	}

	
	public String renderLabel(boolean b) {
		return super.renderLabel(b) + " : " + getLabel(type.getReturnedClass().getName(),b);
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
