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

import org.jboss.tools.hibernate.ui.diagram.editors.autolayout.IItemInfo;
import org.jboss.tools.hibernate.ui.diagram.editors.autolayout.ILinkInfo;
import org.jboss.tools.hibernate.ui.diagram.editors.autolayout.IDiagramInfo;


public class AutoLayoutImpl {
	LayoutConstants constants = new LayoutConstants();
    protected Items items;

    public AutoLayoutImpl() {}
    
    public void setGridStep(String gridStep) {
		constants.update(gridStep);
    }
    
    public void setItems(Items items) {
    	this.items = items;
		items.setConstants(constants);
    }

    public void setOverride(boolean b) {
        items.setOverride(b);
    }

    public void setProcess(IDiagramInfo process) {
//		constants.update();
        items.setProcess(process);
        apply();
        if (items.override) {
			TransitionArranger a = new TransitionArranger();
			a.setItems(items.items);
			a.execute();
        }
    }

    private void apply() {
		resetTransitions(); // temporal

        Item[] is = items.items;
        int[] yDeltas = items.groups.yDeltas;
        for (int i = 0; i < is.length; i++) {
            if (is[i].isSet()) {
            	continue;
            }
            IItemInfo o = is[i].itemInfo;
            int x = is[i].ix * constants.deltaX + constants.indentX;
            int y = is[i].iy * constants.deltaY + constants.indentY;
            if (is[i].ix % 2 == 1) {
            	y += 16;
            }
            x += is[i].group.xDeltas[is[i].ix] * constants.incX;
            y += yDeltas[is[i].iy] * constants.incY + is[i].yIndent;
            o.setShape(new int[]{x, y, 0, 0});
        }
    }
    
    private void resetTransitions() {
    	if (!items.override) {
    		return;
    	}
		Item[] is = items.items;
		for (int i = 0; i < is.length; i++) {
			IItemInfo o = is[i].itemInfo;
			if (o instanceof ILinkInfo) {
				((ILinkInfo)o).setLinkShape(new int[0]);
			}
			ILinkInfo[] os = items.getOutput(o);
			for (int j = 0; j < os.length; j++) {
				os[j].setLinkShape(new int[0]);
			}
		}
    	
    }

}
