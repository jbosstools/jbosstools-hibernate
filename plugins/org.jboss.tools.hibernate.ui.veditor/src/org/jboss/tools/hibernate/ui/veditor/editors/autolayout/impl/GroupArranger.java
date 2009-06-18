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

public class GroupArranger {
	Group group;
	int mgi = 0; // items with indefinite gravity
	boolean inited = false;

	public GroupArranger(Group group) {
		this.group = group;
	}

	private void init() {
		inited = true;
		for (int i = 0; i < group.items().length; i++) {
			Item item = group.getItem(i);
			if (item.isOwned) {
				item.gravity = -1;
			} else if (item.inputs.length == 0) {
				item.gravity = 0;
			} else if (item.outputs.length == 0) {
				item.gravity = 2000;
			} else {
				item.gravity = 1000;
				++mgi;
			}
		}
	}

	public void arrange() {
		init();
		while (mgi > 0) {
			split();
			if (mgi > 0) {
				cut(findBestCutIndex());
			}
		}
		reduce();
	}

	private void split() {
		int length = group.items().length;
		boolean work = true;
		while (work) {
			work = false;
			for (int i = 0; i < length; i++) {
				Item item = group.getItem(i);
				if (item.gravity != 1000) {
					continue;
				}
				int q = computeLowerWeight(i);
				if (q < item.gravity) {
					item.gravity = q;
					work = true;
					--mgi;
				}
				if (item.gravity != 1000) {
					continue;
				}
				q = computeUpperWeight(i);
				if (q > item.gravity) {
					item.gravity = q;
					work = true;
					--mgi;
				}
			}
		}
	}

	private int computeLowerWeight(int i0) {
		Item item = group.getItem(i0);
		int[] is = item.inputs;
		int q = 0;
		for (int i = 0; i < is.length; i++) {
			if (!item.inputActivities[i]) {
				continue;
			}
			Item si = group.allitems[is[i]];
			if (si.gravity + 1 > q) {
				q = si.gravity + 1;
			}
		}
		return q;
	}

	private int computeUpperWeight(int i0) {
		Item item = group.getItem(i0);
		int[] is = item.outputs;
		if (is.length == 0) {
			return item.gravity;
		}
		int q = 2000;
		for (int i = 0; i < is.length; i++) {
			if (!item.outputActivities[i]) {
				continue;
			}
			Item si = group.allitems[is[i]];
			if (si.gravity - 1 < q) {
				q = si.gravity - 1;
			}
		}
		return (q == 2000) ? item.gravity : q;
	}

	void activateInput(Item item, int input, boolean b) {
		item.inputActivities[input] = b;
		Item i2 = group.allitems[item.inputs[input]];
		int[] is = i2.outputs;
		for (int i = 0; i < is.length; i++) {
			if (is[i] == item.n) {
				i2.outputActivities[i] = b;
			}
		}
	}

	private int findBestCutIndex() {
		int k = -1;
		int w = 20000;
		for (int i = 0; i < group.items().length; i++) {
			Item item = group.getItem(i);
			if (item.gravity != 1000) {
				continue;
			}
			int w2 = getCuttingPreference(i);
			if (w2 < w) {
				w = w2;
				k = i;
			}
		}
		return k;
	}

	private int getCuttingPreference(int i0) {
		Item item = group.getItem(i0);
		int[] is = item.inputs;
		int q = 0;
		for (int i = 0; i < is.length; i++) {
			if (!item.inputActivities[i]) {
				continue;
			}
			Item si = group.allitems[is[i]];
			if (si.gravity == 1000) {
				++q;
			}
		}
		q = 10 * q;
		is = item.outputs;
		for (int i = 0; i < is.length; i++) {
			if (!item.outputActivities[i]) {
				continue;
			}
			Item si = group.allitems[is[i]];
			if (si.gravity == 1000) {
				--q;
			}
		}
		return q;
	}

	private void cut(int i0) {
		Item item = group.getItem(i0);
		int[] is = item.inputs;
		for (int i = 0; i < is.length; i++) {
			if (!item.inputActivities[i]) {
				continue;
			}
			Item si = group.allitems[is[i]];
			if (si.gravity == 1000) {
				activateInput(item, i, false);
			}
		}
	}

	private void reduce() {
		boolean work = true;
		while (work) {
			work = false;
			for (int i = 0; i < group.items().length; i++) {
				Item item = group.getItem(i);
				if (item.gravity < 0) {
					continue;
				}
				int q = computeLowerWeight(i);
				if (q < item.gravity) {
					item.gravity = q;
					work = true;
				}
			}
		}
		work = true;
		while (work) {
			work = false;
			for (int i = 0; i < group.items().length; i++) {
				Item item = group.getItem(i);
				if (item.gravity < 0) {
					continue;
				}
				int q = computeUpperWeight(i);
				if (q > item.gravity) {
					item.gravity = q;
					work = true;
				}
			}
		}
		for (int i = 0; i < group.items().length; i++) {
			Item item = group.getItem(i);
			item.ix = item.gravity;
		}
		for (int i = 0; i < group.items().length; i++) {
			Item item = group.getItem(i);
			int[] cs = item.comments;
			for (int j = 0; j < cs.length; j++) {
				group.allitems[cs[j]].ix = item.ix;
			}
		}
	}

}
