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
package org.jboss.tools.hibernate.wizard.hibernatecachewizard.datamodel;

import java.util.Hashtable;

public class TreeDataModel implements ICacheable {
	private String parentName;
	private String cacheParameter;
    private String name;
	private String rootName;
    private int type;	
	public TreeDataModel root = null;
    public Hashtable<String,TreeDataModel> children = null;
	
    public TreeDataModel(String name, int type, final String parentname, final String cacheparam) {
		initNodeParam(name, type, parentname, cacheparam);
    }

    public TreeDataModel(String name, int type, final String rootname) {
		rootName = rootname;
		initNodeParam(name, type, "", "");
        createRoot();
    }

	private void initNodeParam(final String name, final int type, 
							   final String parentname, final String cacheparam)
	{
		this.name =(name != null) ? name : ICacheable.bundle.getString("HibernateCacheWizard.DEFAULT_NAME");
		parentName =(parentname != null) ? parentname : ICacheable.bundle.getString("HibernateCacheWizard.DEFAULT_NAME");
		cacheParameter = (cacheparam != null) ? cacheparam : ICacheable.bundle.getString("HibernateCacheWizard.read-only");
		this.type = type;
        children = new Hashtable<String,TreeDataModel > ();
	}

	public void setName(final String newname) {
		name = newname;
	}
	public void setParentName(final String parentname) {
		parentName = parentname;
	}
	public String getName() {
        return name;
    }
	public final String getCacheParameter() {
		return cacheParameter;
	}
	public String getParentName() {
		return parentName;
	}

    private void createRoot() {
        root = new TreeDataModel(rootName, ICacheable.CACHEABLE_ELEMENT, "","");
    }

    public void addNode(TreeDataModel node) {
        if(children == null) {
            children = new Hashtable<String,TreeDataModel>( );
        }
        children.put(node.name, node);
    }
	public void removeNode(TreeDataModel node) {
		if(children == null) {
			return;
		}
		String nodename = node.getName();
		if(children.containsKey(nodename)) {
			children.remove(nodename);
		}
	}
	public void removeNode(final String name) {
		if(children == null) {
			return;
		}
		if(children.containsKey(name)) {
			children.remove(name);
		}
	}

    public Object[] getChildren() {
        return (getContents()).values().toArray();
    }

    public Hashtable getContents() {
        if (children == null) {
            children = new Hashtable<String,TreeDataModel>(  );
        }
        return children;
    }

    public int getType() {
        return type;
    }

    public TreeDataModel getRoot(  ) {
        return root;
    }

}

