/*
 * Created on 03-08-2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.hibernate.console.node;

import org.hibernate.console.ImageConstants;
import org.hibernate.metadata.ClassMetadata;

/**
 * @author MAX
 *
 */
public class IdentifierNode extends TypeNode {

	/**
	 * @param factory
	 * @param parent
	 * @param md
	 */
	public IdentifierNode(NodeFactory factory, BaseNode parent, ClassMetadata md) {
        super(factory, parent, md.getIdentifierType(), factory.getMetaData(md.getIdentifierType().getReturnedClass() ), null, false);
        name = md.getIdentifierPropertyName();
        iconName = ImageConstants.IDPROPERTY;
	}

}
