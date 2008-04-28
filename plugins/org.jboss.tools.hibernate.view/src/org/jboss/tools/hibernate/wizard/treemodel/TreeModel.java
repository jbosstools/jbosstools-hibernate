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
package org.jboss.tools.hibernate.wizard.treemodel;

import java.util.Hashtable;


/**
 * @author kaa
 * akuzmin@exadel.com
 * Jul 18, 2005
 */
public class TreeModel {
    private String		 		Name;
	private TreeModel 		Root 		= null;
    private Hashtable 			Children 	= null;

    public TreeModel(String name, TreeModel root)
    {
    	if (root!=null) 
    		{
    		Root=root;
    		}
    	Name=name;
        Children = new Hashtable(  );
    }

	public void setName(final String newname)
	{
		Name = newname;
	}
	public void setParentName(TreeModel parent)
	{
		Root = parent;
	}
	public String getName()
    {
        return Name;
    }
	public TreeModel getParent()
	{
		return Root;
	}

    public void addNode(TreeModel node )
    {
        if(Children == null) 
		{
            Children = new Hashtable( );
        }
        Children.put(node.Name, node );
    }

    public void addNode(String nodeName)
    {
    	addNode(new TreeModel(nodeName,this));
    }
    
	public void removeNode(TreeModel node)
	{
		if(Children == null)
		{
			return;
		}
		removeNode(node.getName());
	}
	public void removeNode(final String name)
	{
		if(Children == null)
		{
			return;
		}
		if( Children.containsKey((Object)name) )
		{
			Children.remove((Object)name);
		}
	}

    public Object[] getChildren(  )
    {
        return (getContents()).values().toArray();
    }

    public Hashtable getContents(  )
    {
        if ( Children == null ) 
		{
            Children = new Hashtable(  );
        }
        return Children;
    }

    public TreeModel getRoot(  )
    {
    	TreeModel elem = getParent();
    	while (elem!=null)
    		elem = elem.getParent();
        return elem;
    }

	public TreeModel getNode(String nodeName) {
		if((Children == null)||(nodeName==null))
		{
			return null;
		}
		if( Children.containsKey(nodeName) )
		{
			return (TreeModel) Children.get(nodeName);
		}
		return null;

	}

}
