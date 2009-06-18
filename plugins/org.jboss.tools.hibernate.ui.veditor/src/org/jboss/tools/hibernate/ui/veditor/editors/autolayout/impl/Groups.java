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

public class Groups {
	protected LayuotConstants constants;
	static int FX = 30, FY = 120;
	List<Group> groups = new ArrayList<Group>();
	Item[] items;
	int[][] field;
	int[] yDeltas = null;

	public Groups() {
	}

	public void setConstants(LayuotConstants constants) {
		this.constants = constants;
	}

	public void load(Item[] items) {
		this.items = items;
		init();
		initField();
		buildY();
		buildDeltas();
	}

	public void init() {
		groups.clear();
		int g = 0;
		boolean isSomethingSet = isSomethingSet();
		for (int i = 0; i < items.length; i++) {
			if (items[i].group != null || items[i].isOwned) {
				continue;
			}
			++g;
			Group group = new Group();
			group.setConstants(constants);
			group.number = g;
			groups.add(group);
			if (!items[i].isSet()) {
				items[i].ix = 0;
			}
			group.setItems(items);
			if (isSomethingSet) {
				group.expandGroup(i);
			} else {
				group.createGroup(i);
			}
			group.moveX();
		}
	}

	private boolean isSomethingSet() {
		for (int i = 0; i < items.length; i++) {
			if (items[i].isSet) {
				return true;
			}
		}
		return false;
	}

	public void initField() {
		field = new int[FX][];
		for (int i = 0; i < FX; i++) {
			field[i] = new int[FY];
			for (int j = 0; j < FY; j++) {
				field[i][j] = 0;
			}
		}
		for (int i = 0; i < items.length; i++) {
			if (items[i].isSet()) {
				field[items[i].ix][items[i].iy] = 1;
			}
		}
	}

	static int MAX_SINGLES_PER_ROW = 5;

	public void buildY() {
		int miny = 0;
		miny = buildYForSingleComments();
		boolean isSomethingSet = isSomethingSet();
		for (int i = 0; i < groups.size(); i++) {
			Group group = groups.get(i);
			if (group.items().length < 2) {
				continue;
			}
			Item item = group.getItem(0);
			if (item.yAssigned) {
				continue;
			}
			group.miny = miny;
			item.yAssigned = true;
			if (!item.isSet()) {
				item.iy = miny;
			}
			if (!isSomethingSet) {
				group.buildY_2(item, field);
			}
			group.buildY(item, field);
			miny = group.getMaxY() + 1;
			if (miny > FY - 2) {
				miny = FY - 2;
			}
		}
		buildYForSingles(miny);
	}

	private int buildYForSingleComments() {
		int miny = 0;
		int ix = 0;
		for (int i = 0; i < groups.size(); i++) {
			Group group = groups.get(i);
			if (group.items().length != 1) {
				continue;
			}
			Item item = group.getItem(0);
			if (!item.isComment()) {
				continue;
			}
			if (!item.isSet()) {
				group.miny = miny;
				item.ix = ix;
				item.iy = miny;
				ix += 2;
				if (ix > 2) {
					ix = 0;
					++miny;
				}
			} else {
				group.miny = 0;
			}
		}
		if (ix > 0) {
			++miny;
		}
		return miny;
	}

	private void buildYForSingles(int miny) {
		int ix = 0;
		for (int i = 0; i < groups.size(); i++) {
			Group group = groups.get(i);
			if (group.items().length != 1 || group.miny >= 0) {
				continue;
			}
			group.miny = miny;
			Item item = group.getItem(0);
			while (true) {
				while (ix < field.length && field[ix][miny] == 1) {
					++ix;
				}
				if (ix > MAX_SINGLES_PER_ROW) {
					ix = 0;
					if (miny < FY - 1) {
						++miny;
					} else {
						break;
					}
				} else {
					break;
				}
			}
			if (!item.isSet()) {
				item.iy = miny;
				item.ix = ix;
				++ix;
			}
			field[item.ix][item.iy] = 1;
		}
	}

	void buildXDeltasForSingles() {
		int[] xDeltas = new int[100];
		for (int i = 0; i < groups.size(); i++) {
			Group group = groups.get(i);
			if (group.items().length != 1) {
				continue;
			}
			group.xDeltas = xDeltas;
			Item item = group.getItem(0);
			int c = item.ix;
			c++;
			if (c >= xDeltas.length) {
				continue;
			}
			int[] shape = item.getObject().getShape();
			if (shape == null || shape.length < 4) {
				continue;
			}
			int wi = (shape[2] - (constants.deltaX / 2)) / constants.incX;
			if (wi > xDeltas[c]) {
				xDeltas[c] = wi;
			}
		}
		for (int i = 1; i < xDeltas.length; i++)
			xDeltas[i] += xDeltas[i - 1];
	}

	public void buildDeltas() {
		for (int i = 0; i < groups.size(); i++) {
			Group group = groups.get(i);
			group.buildXDeltas();
		}
		buildYDeltas();
		buildXDeltasForSingles();
	}

	public void buildYDeltas() {
		yDeltas = new int[getMaxY() + 1];
		for (int i = 0; i < yDeltas.length; i++) {
			yDeltas[i] = 0;
		}
		for (int i = 0; i < items.length; i++) {
			int c = items[i].iy + 1;
			if (c >= yDeltas.length) {
				continue;
			}
			int sz = items[i].outputs.length - 1;
			if (sz > yDeltas[c]) {
				yDeltas[c] = sz;
			}
			int[] shape = items[i].getObject().getShape();
			if (shape == null || shape.length < 4) {
				continue;
			}
			int wi = (shape[3] - (constants.deltaY / 2)) / constants.incY;
			if (wi > yDeltas[c]) {
				yDeltas[c] = wi;
			}
		}
		for (int i = 1; i < yDeltas.length; i++) {
			yDeltas[i] += yDeltas[i - 1];
		}
	}

	public int getMaxY() {
		int max = 0;
		for (int i = 0; i < items.length; i++) {
			if (items[i].iy > max) {
				max = items[i].iy;
			}
		}
		return max;
	}

}
