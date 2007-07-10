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
package org.jboss.tools.hibernate.ui.veditor.editors.model;

import java.util.ArrayList;
import java.util.List;

public class Shape extends ModelElement {
	
	private int indent = 0;
		
	private List<Connection> sourceConnections = new ArrayList<Connection>();
	private List<Connection> targetConnections = new ArrayList<Connection>();
	
	public static final String HIDE_SELECTION = "hide selection";
	public static final String SHOW_SELECTION = "show selection";
	public static final String SET_FOCUS = "set focus";
	
	private Object  ormElement;
		
	protected Shape(Object ioe) {
		ormElement = ioe;
	}

	public void addConnection(Connection conn) {
		if (conn == null || conn.getSource() == conn.getTarget()) {
			throw new IllegalArgumentException();
		}
		if (conn.getSource() == this) {
			sourceConnections.add(conn);
		} else if (conn.getTarget() == this) {
			targetConnections.add(conn);
		}
	}
	
	
	public List<Connection> getSourceConnections() {
		return sourceConnections;
	}
	
	public List<Connection> getTargetConnections() {
		return targetConnections;
	}
	
	public Object getOrmElement() {
		return ormElement;
	}
	
	public void hideSelection() {
		firePropertyChange(HIDE_SELECTION, null, null);
	}

	public void showSelection() {
		firePropertyChange(SHOW_SELECTION, null, null);
	}

	public void setFocus() {
		firePropertyChange(SET_FOCUS, null, null);		
	}
	
	public int getIndent() {
		return indent;
	}

	protected void setIndent(int indent) {
		this.indent = indent;
	}
	
	protected void setHiden(boolean hiden) {
		for (int i = 0; i < sourceConnections.size(); i++)
			((Connection)sourceConnections.get(i)).setHiden(hiden);
		for (int i = 0; i < targetConnections.size(); i++)
			((Connection)targetConnections.get(i)).setHiden(hiden);
	}
}
