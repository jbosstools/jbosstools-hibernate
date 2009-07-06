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
package org.jboss.tools.hibernate.ui.diagram.editors.autolayout;

import org.jboss.tools.hibernate.ui.diagram.editors.autolayout.impl.AutoLayoutImpl;
import org.jboss.tools.hibernate.ui.diagram.editors.autolayout.impl.Items;


public class AutoLayout {
	AutoLayoutImpl engine = new AutoLayoutImpl();

    public AutoLayout() {
    	this(new Items());
    }
    
    public AutoLayout(Items items) {
    	setItems(items);
    }
    
    public void setGridStep(String gridStep) {
    	engine.setGridStep(gridStep);
    }
    
    public void setItems(Items items) {
    	engine.setItems(items);
    }

    public void setOverride(boolean b) {
    	engine.setOverride(b);
    }

    public void setProcess(IDiagramInfo process) {
    	engine.setProcess(process);
    }

}