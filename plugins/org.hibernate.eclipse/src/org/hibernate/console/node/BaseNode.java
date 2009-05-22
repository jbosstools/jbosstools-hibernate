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
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ImageConstants;
import org.hibernate.console.execution.ExecutionContext;
import org.hibernate.console.execution.ExecutionContextHolder;

/**
 * @author MAX
 *
 */
public abstract class BaseNode implements TreeNode, ExecutionContextHolder {

    protected BaseNode parent;
	protected NodeFactory factory;
	protected List<BaseNode> children = new ArrayList<BaseNode>();
	protected String name = "!"; //$NON-NLS-1$
    String iconName = ImageConstants.UNKNOWNPROPERTY;
    
    abstract protected void checkChildren();

    public ConsoleConfiguration getConsoleConfiguration() {
    	return factory.getConsoleConfiguration();
    }
    
    public ExecutionContext getExecutionContext() {
    	return factory.getConsoleConfiguration().getExecutionContext();
    }
    
    public String getIconName() {
        return iconName;
    }

    public BaseNode(NodeFactory f, BaseNode parent) {
        factory = f;
        this.parent = parent;
    }    
    
    public TreeNode getChildAt(int childIndex) {
    	checkChildren();
        return children.get(childIndex);
    }

    public int getChildCount() {
    	checkChildren();
        return children.size();
    }

    public TreeNode getParent() {    	
       return parent;
    }

    public int getIndex(TreeNode node) {
    	checkChildren();
        return children.indexOf(node);
    }

    public boolean getAllowsChildren() {
        return true;
    }

    public boolean isLeaf() {
    	checkChildren();
    	return getChildCount()==0;
    }

    public Enumeration<BaseNode> children() {
    	checkChildren();
        return Collections.enumeration(children);
    }

	public abstract String getHQL();

	public String getName() {
        return name;
	}
    
    final public String toString() {
        return renderLabel(true);
    }
    
    protected static String getLabel(String name, boolean fullyQualifiedName) {
    	if(!fullyQualifiedName && name!=null && name.length()>1 && name.indexOf('.')>=0) {
    		return name.substring(name.lastIndexOf('.')+1);
    	} else {
    		return name;
    	}
    }
    
    public String renderLabel(boolean fullyQualifiedNames) {
    	return getLabel(getName(), fullyQualifiedNames);
    }
    
    /** clear/reset this node. Mainly get rid of children. **/
    public void clear() {
    	for (BaseNode element : children) {
			element.clear();
		}
    }
}
