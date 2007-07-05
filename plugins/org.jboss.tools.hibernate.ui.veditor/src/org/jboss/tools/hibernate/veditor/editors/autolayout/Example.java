/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.veditor.editors.autolayout;

import java.util.ArrayList;

public class Example {
	
	public static IDiagramInfo generateRandomProcess(int nodeCount, int linkCount) {
		ProcessInfoImpl process = new ProcessInfoImpl();
		for (int i = 0; i < nodeCount; i++) {
			ItemInfoImpl item = new ItemInfoImpl();
			item.setID("n_" + i);
			process.addItem(item);
		}
		IItemInfo[] items = process.getItems();
		for (int i = 0; i < linkCount; i++) {
			int n1 = (int)(nodeCount * Math.random());
			int n2 = (int)(nodeCount * Math.random());
			String target = ((ItemInfoImpl)items[n2]).getID();
			LinkInfoImpl link = new LinkInfoImpl();
			link.setTargetID(target);
			((ItemInfoImpl)items[n1]).addLink(link);
		}
		return process;
	}
	
	static void printProcess(IDiagramInfo process) {
		IItemInfo[] items = process.getItems();
		for (int i = 0; i < items.length; i++) printItem(items[i]);
	}

	static void printItem(IItemInfo item) {
		System.out.print(item.getID() + " (");
		int[] shape = item.getShape();
		for (int i = 0; i < shape.length; i++) {
			if(i > 0) System.out.print(",");
			System.out.print(shape[i]);
		}
		System.out.print(") -->");
		ILinkInfo[] links = item.getLinks();
		for (int i = 0; i < links.length; i++) {
			if(i > 0) System.out.print(",");
			System.out.print(links[i].getTargetID());			
		}
		System.out.println("");		
	}

	public static void main(String[] args) {
		IDiagramInfo process = generateRandomProcess(10, 17);
		System.out.println("Before Layout");
		printProcess(process);
		AutoLayout layout = new AutoLayout();
		layout.setGridStep("" + 8);
		layout.setOverride(true);
		layout.setProcess(process);
		System.out.println("After Layout");
		printProcess(process);
	}
}

class ProcessInfoImpl implements IDiagramInfo {
	ArrayList items = new ArrayList();

	public IItemInfo[] getItems() {
		return (IItemInfo[])items.toArray(new IItemInfo[0]);
	}
	
	public void addItem(IItemInfo item) {
		items.add(item);
	}
	
}

class ItemInfoImpl implements IItemInfo {
	String id = "";
	int[] shape = new int[0];
	ArrayList links = new ArrayList();
	
	public void setID(String id) {
		this.id = id;
	}

	public String getID() {
		return id;
	}

	public boolean isComment() {
		return false;
	}

	public int[] getShape() {
		return shape;
	}

	public ILinkInfo[] getLinks() {
		return (ILinkInfo[])links.toArray(new ILinkInfo[0]);
	}

	public void addLink(ILinkInfo link) {
		links.add(link);
	}

	public void setShape(int[] s) {
		this.shape = s;
	}
	
}

class LinkInfoImpl implements ILinkInfo {
	String target;

	public void setTargetID(String target) {
		this.target = target;
	}

	public String getTargetID() {
		return target;
	}

	public void setLinkShape(int[] vs) {
	}
	
}
