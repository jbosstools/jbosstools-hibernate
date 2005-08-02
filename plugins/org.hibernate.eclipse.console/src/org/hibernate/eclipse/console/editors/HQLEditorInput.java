package org.hibernate.eclipse.console.editors;

import org.eclipse.core.resources.IStorage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;

/**
 * input for hql editor on non file based storage.
 */
public class HQLEditorInput implements IStorageEditorInput {

    /** The name of ths editor input. */
    private String fName;
    /** The storage object used by this editor input. */
    private IStorage fStorage;

    public HQLEditorInput( String storageSource ) {
        this( new HQLEditorStorage( storageSource ) );
    }

    /**
     * Constructs an instance of this class with the given <code>IStorage</code>
     * object as the editor input source.
     * 
     * @param storage the storage object for this editor input
     */
    public HQLEditorInput( IStorage storage ) {
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
        if (fStorage != null)
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
        return fName;
    }

    public IStorage getStorage() {
        return fStorage;
    }

    public String getToolTipText() {        
        return getName();        
    }

  
    public void setName( String name ) {
        fName = name;
    }

    public void setStorage( IStorage storage ) {
        fStorage = storage;
    }

	public IPersistableElement getPersistable() {
		return null;
	}

	public boolean equals(Object obj) {
		return obj instanceof HQLEditorInput;
	}
	
	public int hashCode() {
		return 0;
	}
} 
