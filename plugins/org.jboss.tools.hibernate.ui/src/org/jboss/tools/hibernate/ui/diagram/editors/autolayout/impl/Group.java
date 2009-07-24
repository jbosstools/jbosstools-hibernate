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
package org.jboss.tools.hibernate.ui.diagram.editors.autolayout.impl;

import java.util.ArrayList;
import java.util.List;

public class Group {
	LayoutConstants constants;
	int number;
	List<Integer> itemList = new ArrayList<Integer>();
	Item[] allitems = null;
	private int[] items = null;
	int miny = -1;
	int[] xDeltas = null;
	GroupArranger arranger = new GroupArranger(this);

	public Group() {
	}

	public void setItems(Item[] items) {
		allitems = items;
	}

	public void setConstants(LayoutConstants constants) {
		this.constants = constants;
	}

	public void expandGroup(int _item) {
		Item item = allitems[_item];
		item.group = this;
		itemList.add(Integer.valueOf(_item));
		int[] is = item.comments;
		for (int i = 0; i < is.length; i++) {
			Item item2 = allitems[is[i]];
			if (!item2.isSet()) {
				allitems[is[i]].ix = item.ix;
			}
			expandGroup(is[i]);
		}
		is = item.inputs;
		for (int i = 0; i < is.length; i++) {
			Item item2 = allitems[is[i]];
			if (item2.group != null)
				continue;
			if (!item2.isSet()) {
				item2.ix = item.ix - 1;
			}
			expandGroup(is[i]);
		}
		is = item.outputs;
		for (int i = 0; i < is.length; i++) {
			Item item2 = allitems[is[i]];
			if (item2.group != null) {
				continue;
			}
			if (!item2.isSet()) {
				item2.ix = item.ix + 1;
			}
			expandGroup(is[i]);
		}
	}

	int[] items() {
		if (items == null) {
			items = new int[itemList.size()];
			for (int i = 0; i < items.length; i++) {
				items[i] = itemList.get(i);
			}
		}
		return items;
	}

	Item getItem(int i) {
		return allitems[items[i]];
	}

	public void moveX() {
		items();
		int min = 0;
		for (int i = 0; i < items.length; i++) {
			if (getItem(i).ix < min) {
				min = getItem(i).ix;
			}
		}
		if (min == 0) {
			return;
		}
		for (int i = 0; i < items.length; i++) {
			if (!getItem(i).isSet()) {
				getItem(i).ix -= min;
				if (getItem(i).ix >= Groups.FX) {
					getItem(i).ix = Groups.FX - 1;
				}
			}
		}
	}

	public void buildY(Item item, int[][] field) {
		field[item.ix][item.iy] = 1;
		int[] is = item.comments;
		for (int i = 0; i < is.length; i++) {
			Item item2 = allitems[is[i]];
			if (item2.yAssigned) {
				continue;
			}
			item2.yAssigned = true;
			if (!item2.isSet()) {
				item2.iy = findFreeY(item2.ix, miny, item.iy, Groups.FY, field);
			}
			buildY(item2, field);
		}
		is = item.inputs;
		for (int i = 0; i < is.length; i++) {
			Item item2 = allitems[is[i]];
			if (item2.yAssigned) {
				continue;
			}
			item2.yAssigned = true;
			if (!item2.isSet()) {
				item2.iy = findFreeY(item2.ix, miny, item.iy, Groups.FY, field);
			}
			buildY(item2, field);
		}
		is = item.outputs;
		for (int i = 0; i < is.length; i++) {
			Item item2 = allitems[is[i]];
			if (item2.yAssigned) {
				continue;
			}
			item2.yAssigned = true;
			if (!item2.isSet()) {
				item2.iy = findFreeY(item2.ix, miny, item.iy, Groups.FY, field);
			}
			buildY(item2, field);
		}
	}

	public void buildY_2(Item item, int[][] field) {
		field[item.ix][item.iy] = 1;
		int[] is = item.comments;
		for (int i = 0; i < is.length; i++) {
			Item item2 = allitems[is[i]];
			if (item2.yAssigned) {
				continue;
			}
			item2.yAssigned = true;
			if (!item2.isSet()) {
				item2.iy = findFreeY(item2.ix, miny, item.iy, Groups.FY, field);
			}
			buildY(item2, field);
		}
		is = item.inputs;
		for (int i = 0; i < is.length; i++) {
			Item item2 = allitems[is[i]];
			if (item2.yAssigned) {
				continue;
			}
			if (item.ix != item2.ix + 1) {
				continue;
			}
			item2.yAssigned = true;
			if (!item2.isSet()) {
				item2.iy = findFreeY(item2.ix, miny, item.iy, Groups.FY, field);
			}
			buildY(item2, field);
		}
		is = item.outputs;
		for (int i = 0; i < is.length; i++) {
			Item item2 = allitems[is[i]];
			if (item2.yAssigned) {
				continue;
			}
			if (item.ix != item2.ix - 1) {
				continue;
			}
			item2.yAssigned = true;
			if (!item2.isSet()) {
				item2.iy = findFreeY(item2.ix, miny, item.iy, Groups.FY, field);
			}
			buildY(item2, field);
		}
	}

	private int findFreeY(int ix, int miny, int prefy, int maxy, int[][] field) {
		for (int i = 0; i < 20; i++) {
			int iy = prefy + i;
			if (iy >= miny && iy < maxy && field[ix][iy] == 0) {
				return iy;
			}
			iy = prefy - i;
			if (iy >= miny && iy < maxy && field[ix][iy] == 0) {
				return iy;
			}
		}
		return prefy;
	}

	public int getMaxY() {
		int maxy = 0;
		for (int i = 0; i < items.length; i++) {
			if (getItem(i).iy > maxy) {
				maxy = getItem(i).iy;
			}
		}
		return maxy;
	}

	public boolean hasSetItems() {
		items();
		for (int i = 0; i < items.length; i++) {
			if (getItem(i).isSet()) {
				return true;
			}
		}
		return false;
	}

	public void buildXDeltas() {
		xDeltas = new int[getMaxX() + 1];
		for (int i = 0; i < xDeltas.length; i++) {
			xDeltas[i] = 0;
		}
		// /if (hasSetItems()) return;
		for (int i = 0; i < items.length; i++) {
			int c = getItem(i).ix;
			if (c >= xDeltas.length) {
				continue;
			}
			int sz = getItem(i).inputs.length - 1;
			if (sz > xDeltas[c]) {
				xDeltas[c] = sz;
			}
			++c;
			if (c >= xDeltas.length) {
				continue;
			}
			sz = getItem(i).outputs.length - 1;
			if (sz > xDeltas[c]) {
				xDeltas[c] = sz;
			}
		}
		for (int i = 0; i < xDeltas.length; i++) {
			if (xDeltas[i] > 4) {
				xDeltas[i] = 4;
			}
		}
		for (int i = 0; i < items.length; i++) {
			int c = getItem(i).ix;
			++c;
			if (c >= xDeltas.length) {
				continue;
			}
			int[] shape = getItem(i).getObject().getShape();
			if (shape == null || shape.length < 4) {
				continue;
			}
			int wi = (shape[2] - (constants.deltaX / 2)) / constants.incX;
			if (wi > xDeltas[c]) {
				xDeltas[c] = wi;
			}
		}
		for (int i = 1; i < xDeltas.length; i++) {
			xDeltas[i] += xDeltas[i - 1];
		}
	}

	public int getMaxX() {
		int maxx = 0;
		for (int i = 0; i < items.length; i++) {
			if (getItem(i).ix > maxx) {
				maxx = getItem(i).ix;
			}
		}
		return maxx;
	}

	// ////////////////////////////////////////

	public void createGroup(int _item) {
		expandGroup2(_item);
		int length = items().length;
		for (int i = 0; i < length; i++) {
			getItem(i).initActivities();
		}
		arranger.arrange();
	}

	private void expandGroup2(int _item) {
		Item item = allitems[_item];
		item.group = this;
		itemList.add(Integer.valueOf(_item));
		int[] is = item.comments;
		for (int i = 0; i < is.length; i++) {
			expandGroup2(is[i]);
		}
		is = item.inputs;
		for (int i = 0; i < is.length; i++) {
			if (allitems[is[i]].group == null) {
				expandGroup2(is[i]);
			}
		}
		is = item.outputs;
		for (int i = 0; i < is.length; i++) {
			if (allitems[is[i]].group == null) {
				expandGroup2(is[i]);
			}
		}
	}

}
