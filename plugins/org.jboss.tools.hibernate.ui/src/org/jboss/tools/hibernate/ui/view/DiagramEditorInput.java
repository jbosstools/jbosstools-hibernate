/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.ui.view;

import java.util.ArrayList;

import org.eclipse.core.resources.IStorage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.mapping.RootClass;
import org.jboss.tools.hibernate.ui.diagram.editors.model.OrmDiagram;

/**
 *
 * author: Vitali Yemialyanchyk
 */
public class DiagramEditorInput implements IEditorInput, IStorageEditorInput, IPersistableElement {
	
	protected OrmDiagram ormDiagram = null;
	/** The storage object used by this editor input. */
	private DiagramEditorStorage storage = new DiagramEditorStorage();

	public DiagramEditorInput() {
		ArrayList<RootClass> roots = new ArrayList<RootClass>();
		createOrmDiagram("", roots); //$NON-NLS-1$
	}

	public DiagramEditorInput(FileEditorInput fei) {
		ArrayList<RootClass> roots = new ArrayList<RootClass>();
		createOrmDiagram("", roots); //$NON-NLS-1$
		ormDiagram.loadFromFile(fei.getPath(), true);
		if (ormDiagram.getConsoleConfig() != null) {
			ormDiagram.recreateChildren();
			ormDiagram.loadFromFile(fei.getPath(), true);
			ormDiagram.refresh();
		}
		ormDiagram.setDirty(false);
	}

	public DiagramEditorInput(String configName, RootClass rc) {
		ArrayList<RootClass> roots = new ArrayList<RootClass>();
		roots.add(rc);
		createOrmDiagram(configName, roots);
	}

	public DiagramEditorInput(String configName, RootClass[] rcs) {
		ArrayList<RootClass> roots = new ArrayList<RootClass>();
    	for (int i = 0; i < rcs.length; i++) {
    		roots.add(rcs[i]);
    	}
    	createOrmDiagram(configName, roots);
	}
	
	protected void createOrmDiagram(String configName, ArrayList<RootClass> roots) {
		ormDiagram = new OrmDiagram(configName, roots);
	}
	
	public OrmDiagram getOrmDiagram() {
		return ormDiagram;
	}
	
	public ConsoleConfiguration getConsoleConfig() {
		return getOrmDiagram().getConsoleConfig();
	}

	public boolean exists() {
		if (storage != null) {
			return true;
		}
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
        return ImageDescriptor.getMissingImageDescriptor();
	}

	public String getName() {
		return ormDiagram.getDiagramName();
	}

	public String getStoreFileName() {
		return ormDiagram.getStoreFileName();
	}

	public IPersistableElement getPersistable() {
		return this;
	}

	public String getToolTipText() {
		return getName();
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return null;
	}

	public boolean equals(Object obj) {
		boolean res = false;
		if (!(obj instanceof DiagramEditorInput)) {
			return res;
		}
		final DiagramEditorInput oei = (DiagramEditorInput)obj;
		return ormDiagram.equals(oei.ormDiagram);
	}

	public int hashCode() {
		return ormDiagram.hashCode();
	}

	public IStorage getStorage() {
		return storage;
	}

	public String getFactoryId() {
		return DiagramEditorInputFactory.ID_FACTORY;
	}

	public void saveState(IMemento memento) {
		ormDiagram.saveState(memento);
		//DiagramEditorInputFactory.saveState(memento, this);
	}
	

	public void loadState(final IMemento memento) {
		ormDiagram.loadState(memento);
		if (ormDiagram.getConsoleConfig() != null) {
			//ormDiagram.recreateChildren();
			//ormDiagram.loadState(memento);
			ormDiagram.setMemento(memento);
			ormDiagram.refresh();
		} else {
			ormDiagram.setMemento(memento);
		}
		ormDiagram.setDirty(false);
	}
}
