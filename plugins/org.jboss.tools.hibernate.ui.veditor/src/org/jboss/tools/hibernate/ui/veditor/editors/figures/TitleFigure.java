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
package org.jboss.tools.hibernate.ui.veditor.editors.figures;

import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;

public class TitleFigure extends Figure {
	
	private boolean hiden = false; 

	public void add(IFigure figure, Object constraint, int index) {
		if (index != -1) {
			if (index == -2) {
				index = 0;
			} else {
				index++;
			}
		}
		super.add(figure, constraint, index);
	}
	
	public List getChildren() {
		if (hiden) {
			return super.getChildren().subList(0,1);
		} 
		return super.getChildren();
	}

	public void setHidden(boolean hiden) {
		this.hiden = hiden;
	}
}
