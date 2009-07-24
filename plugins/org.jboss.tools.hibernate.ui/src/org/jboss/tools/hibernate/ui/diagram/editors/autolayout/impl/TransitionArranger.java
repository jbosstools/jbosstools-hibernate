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

import java.util.List;

import org.jboss.tools.hibernate.ui.diagram.editors.autolayout.ILinkInfo;


public class TransitionArranger {
	Item[] items;
	
	public void setItems(Item[] items) {
		this.items = items;
	}
	
	public void execute() {
		int maxX = getMaxX();
		int maxY = getMaxY();
		int[][] occ = new int[occ0.length][maxY + 1];
		Item[] v = new Item[maxY + 1];
		for (int ix = 0; ix <= maxX; ix++) {
			clean(occ);
			fill(v, ix);
			execute(ix, occ, v);
		} 
	}
	
	private void clean(int[][] occ) {
		for (int i = 0; i < occ.length; i++) for (int j = 0; j < occ[0].length; j++) occ[i][j] = 0;
	}
	
	private void fill(Item[] v, int ix) {
		for (int i = 0; i < v.length; i++) {
			v[i] = null;
		}
		for (int i = 0; i < items.length; i++) {
			if (items[i].inputs.length == 0 || items[i].isOwned) {
				continue;
			}
			if (items[i].ix == ix) {
				v[items[i].iy] = items[i];
			}
		}		
	}
	
	private int[] occ0 = new int[10];
			
	private void execute(int ix, int[][] occ, Item[] v) {
		int delta = 0;
		for (int iy = 0; iy < v.length; iy++) {
			if (v[iy] == null) {
				continue;
			}
			for (int i = 0; i < occ0.length; i++) {
				occ0[i] = 0;
			}
			int[] is = v[iy].inputs;
			delta = 0;
			for (int k = 0; k < is.length; k++) {
				int iy2 = items[is[k]].iy;
				int miny = Math.min(iy, iy2);
				int maxy = Math.max(iy, iy2);
				if (maxy - miny > delta) delta = maxy - miny;
				for (int m = 0; m < occ0.length; m++) {
					for (int y = miny; y <= maxy; y++) {
						if (occ[m][y] > 0) {
							occ0[m] += occ[m][y];
						}
					}
				}
			}
			int tg = findTransitionLine(delta, occ0);
			for (int k = 0; k < is.length; k++) {
				int iy2 = items[is[k]].iy;
				occ[tg][iy2]++;
			}
			apply(v[iy], tg);
		}
	}
	
	private int getMaxX() {
		int ix = 0;
		for (int i = 0; i < items.length; i++) {
			if (items[i].ix > ix) {
				ix = items[i].ix;
			}
		}
		return ix;
	}
	
	private int getMaxY() {
		int iy = 0;
		for (int i = 0; i < items.length; i++) {
			if (items[i].iy > iy) {
				iy = items[i].iy;
			}
		}
		return iy;
	}
	
	private int findTransitionLine(int pref, int[] occ0) {
		if (pref >= occ0.length) {
			pref = occ0.length - 1;
		}
		int h = 1000;
		int p = -1;
		for (int i = 0; i < occ0.length; i++) {
			int h1 = occ0[i] * 3 + Math.abs(i - pref);
			if (h1 < h) {
				h = h1;
				p = i;
			}			
		}
		return p;
	}
	
	private void apply(Item item, int tg) {
		List<ILinkInfo> links = item.inputLinks;
		for (int k = 0; k < links.size(); k++) {
			ILinkInfo io = links.get(k);
			io.setLinkShape(new int[]{-1, 8 * (tg + 2)});
		}
	}

}
