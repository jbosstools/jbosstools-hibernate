/*
 * Created on 03-08-2003
 *
 */
package org.hibernate.console.node;

import java.util.List;

import org.hibernate.console.ImageConstants;


/**
 * @author MAX
 */
public class ConfigurationEntitiesNode extends BaseNode {

	public ConfigurationEntitiesNode(String name, NodeFactory factory, List classes) {
        super(factory,null);
        for (int i = 0; i < classes.size(); i++) {
			children.add(factory.createClassNode(this,(String)classes.get(i) ) );
		}
        iconName = ImageConstants.TYPES;
        this.name = name;
	}
	
	protected void checkChildren() {
		
	}
	
	public String getHQL() {
		return "";
	}
}
