/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.diagram.editors.autolayout.impl;

import java.util.*;

import org.jboss.tools.hibernate.ui.diagram.editors.autolayout.IItemInfo;
import org.jboss.tools.hibernate.ui.diagram.editors.autolayout.ILinkInfo;

/**
 * Auto layout item.
 */
public class Item {
	
    private int nId;
    private IItemInfo itemInfo;
    
    protected int x = 0;
    protected int y = 0;
    protected int ix = -1;
    protected int iy = -1;
    protected int[] inputs = new int[0];
    protected int[] outputs = new int[0];
    protected int[] comments = new int[0];
    protected ArrayList<ILinkInfo> inputLinks = new ArrayList<ILinkInfo>();
    protected boolean isOwned = false;
    protected int weight = 0;
    protected Group group = null;
	protected int yIndent = 1;  // Page = 2; other = 1;

    protected boolean isSet = false;
    protected boolean yAssigned = false;

    public Item(int nId, IItemInfo itemInfo) {
    	this.nId = nId;
    	this.itemInfo = itemInfo;
    	if (itemInfo != null && !itemInfo.isVisible()) {
    		isSet = true;
    	    ix = 0;
    	    iy = 0;
    	}
    }
    
    public int getId() {
    	return nId;
    }
    
    public IItemInfo getItemInfo() {
    	return itemInfo;
    }

    public boolean isSet() {
        return isSet;
    }

    public void addInput(int i, ILinkInfo link) {
        int[] k = new int[inputs.length + 1];
        System.arraycopy(inputs, 0, k, 0, inputs.length);
        k[inputs.length] = i;
        inputs = k;
        inputLinks.add(link);
    }

    public void addOutput(int i) {
        int[] k = new int[outputs.length + 1];
        System.arraycopy(outputs, 0, k, 0, outputs.length);
        k[outputs.length] = i;
        outputs = k;
    }
    
    public void addComment(int i) {
		int[] k = new int[comments.length + 1];
		System.arraycopy(comments, 0, k, 0, comments.length);
		k[comments.length] = i;
		comments = k;
    }

    public boolean isSingle() {
        return inputs.length == 0 && outputs.length == 0;
    }

    public void print() {
    }

    public boolean isComment() {
        return itemInfo != null && itemInfo.isComment();
    }

    public boolean isVisible() {
        return itemInfo != null && itemInfo.isVisible();
    }
    
    int gravity;
    boolean[] outputActivities;
	boolean[] inputActivities;
	
	public void initActivities() {
		outputActivities = new boolean[outputs.length];
		for (int i = 0; i < outputActivities.length; i++) {
			outputActivities[i] = (outputs[i] != nId);
		}
		inputActivities = new boolean[inputs.length];
		for (int i = 0; i < inputActivities.length; i++) {
			inputActivities[i] = (inputs[i] != nId);
		}
	}
	
	public void setWeight(int w) {
		weight = w;
	}
	
	public void setYIndent(int y) {
		yIndent = y;
	}

}
