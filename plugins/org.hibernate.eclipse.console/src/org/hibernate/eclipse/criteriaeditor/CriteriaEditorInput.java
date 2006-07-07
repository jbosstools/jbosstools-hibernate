/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.criteriaeditor;

import org.eclipse.core.resources.IStorage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;

/**
 * input for criteria editor on non file based storage.
 */
public class CriteriaEditorInput implements IStorageEditorInput, IPersistableElement {

    /** The name of ths editor input. */
    private String name;
    /** The storage object used by this editor input. */
    private IStorage storage;

    public CriteriaEditorInput( String storageSource ) {
        this( new CriteriaEditorStorage( storageSource ) );
    }

    /**
     * Constructs an instance of this class with the given <code>IStorage</code>
     * object as the editor input source.
     * 
     * @param storage the storage object for this editor input
     */
    public CriteriaEditorInput( IStorage storage ) {
		if (storage == null) {
			throw new IllegalArgumentException();
        }
        setStorage( storage );
        setName( storage.getName() );
    }    

    /**
     * Gets whether the editor input exists.
     * 
     * @see org.eclipse.ui.IEditorInput#exists()
     */
    public boolean exists() {
        if (storage != null)
            return true;
        
        return false;
    }

    public Object getAdapter( Class adapter ) {
        return null;
    }

    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    public String getName() {
        return name;
    }

    public IStorage getStorage() {
        return storage;
    }

    public String getToolTipText() {        
        return getName();        
    }

  
    public void setName( String name ) {
        this.name = name;
    }

    public void setStorage( IStorage storage ) {
        this.storage = storage;
    }

	public IPersistableElement getPersistable() {
		return this;
	}

	public boolean equals(Object obj) {
		if(obj instanceof CriteriaEditorInput) {
			return ((CriteriaEditorInput)obj).getName().equals(name);
		}
		return super.equals(obj);
	}
	
	public int hashCode() {
		return name.hashCode();
	}

    public String getFactoryId() {
        return CriteriaditorInputFactory.ID_FACTORY;
    }

    public void saveState(IMemento memento) {
        CriteriaditorInputFactory.saveState( memento, this );
    }

	public void setQuery(String query) {
		((CriteriaEditorStorage)storage).setContents(query);		
	}

	public String getConsoleConfigurationName() {
		return ((CriteriaEditorStorage)storage).getConfigurationName();
	}

} 
