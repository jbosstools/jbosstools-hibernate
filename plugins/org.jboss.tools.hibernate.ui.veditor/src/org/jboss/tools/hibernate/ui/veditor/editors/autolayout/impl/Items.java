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
package org.jboss.tools.hibernate.ui.veditor.editors.autolayout.impl;

import java.util.*;

import org.jboss.tools.hibernate.ui.veditor.VisualEditorPlugin;
import org.jboss.tools.hibernate.ui.veditor.editors.autolayout.IItemInfo;
import org.jboss.tools.hibernate.ui.veditor.editors.autolayout.ILinkInfo;
import org.jboss.tools.hibernate.ui.veditor.editors.autolayout.IDiagramInfo;

public class Items {
	protected LayuotConstants constants;
	protected IDiagramInfo process;
	protected Item[] items;
	protected Map<String, Item> paths = new HashMap<String, Item>();
	protected Groups groups = new Groups();
	protected boolean override = false;

	public Items() {
	}

	public void setConstants(LayuotConstants constants) {
		this.constants = constants;
		groups.setConstants(constants);
	}

	public void setOverride(boolean b) {
		override = b;
	}

	public void setProcess(IDiagramInfo process) {
		this.process = process;
		load();
	}

	private void load() {
		initItems();
		if (isAllSet()) {
			return;
		}
		buildBinds();
		groups.load(items);
		print();
	}

	private void initItems() {
		IItemInfo[] is = process.getItems();
		items = new Item[is.length];
		for (int i = 0; i < is.length; i++) {
			Item item = new Item();
			items[i] = item;
			paths.put(is[i].getID(), item);
			item.n = i;
			item.itemInfo = is[i];
			int[] shape = is[i].getShape();
			if (!override && shape != null && shape.length > 1) {
				item.x = shape[0];
				item.y = shape[1];
				if (item.x != 0 && item.y != 0) {
					item.isSet = true;
				}
				item.ix = (item.x / constants.deltaX);
				item.iy = (item.y / constants.deltaY);
				if (item.ix < 0) {
					item.ix = 0;
				}
				if (item.iy < 0) {
					item.iy = 0;
				}
				if (item.ix >= Groups.FX) {
					item.ix = Groups.FX - 1;
				}
				if (item.iy >= Groups.FY) {
					item.iy = Groups.FY - 1;
				}
			}
			initItem(item);
		}
	}

	// override
	protected void initItem(Item item) {
	}

	// override

	public ILinkInfo[] getOutput(IItemInfo itemObject) {
		return itemObject.getLinks();
	}

	private boolean isAllSet() {
		for (int i = 0; i < items.length; i++) {
			if (!items[i].isSet()) {
				return false;
			}
		}
		return true;
	}

	private void buildBinds() {
		for (int i = 0; i < items.length; i++) {
			ILinkInfo[] ts = (items[i].itemInfo instanceof ILinkInfo) ? new ILinkInfo[] { (ILinkInfo) items[i].itemInfo }
					: getOutput(items[i].itemInfo);
			for (int j = 0; j < ts.length; j++) {
				String target = ts[j].getTargetID();
				if (target == null || target.length() == 0) {
					continue;
				}
				Item item2 = paths.get(target);
				if (item2 == null) {
					continue;
				}
				if (items[i].isComment()) {
					item2.addComment(items[i].n);
					items[i].isOwned = true;
				} else if (item2.weight < 0) {
					continue;
				} else {
					item2.addInput(items[i].n, ts[j]);
					items[i].addOutput(item2.n);
				}
			}
		}
	}

	private void print() {
		for (int i = 0; i < items.length; i++) {
			items[i].print();
		}
	}

}
