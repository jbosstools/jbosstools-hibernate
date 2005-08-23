package org.hibernate.console.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
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
	protected List children = new ArrayList();
	protected String name = "!";
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
        return (TreeNode) children.get(childIndex);
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

    public Enumeration children() {
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
    	for (Iterator iter = children.iterator(); iter.hasNext();) {
			BaseNode element = (BaseNode) iter.next();
			element.clear();
		}
    }
}
