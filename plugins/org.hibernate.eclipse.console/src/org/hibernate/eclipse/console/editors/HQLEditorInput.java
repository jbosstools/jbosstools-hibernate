package org.hibernate.eclipse.console.editors;

import org.eclipse.core.resources.IStorage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;

/**
 * This class implements the <code>ISQLEditorInput</code> interface on a
 * <code>IStorageEditorInput</code> base.  It is provided as a convenience to 
 * callers of the SQL Editor who want to open the editor on an input that 
 * isn't a file.
 */
public class HQLEditorInput implements IStorageEditorInput {

    /** The name of ths editor input. */
    private String fName;
    /** The storage object used by this editor input. */
    private IStorage fStorage;

    /**
     * Constructs an instance of this class with the given string as the editor
     * input source.
     * 
     * @param storageSource the editor input source string
     */
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

    /**
     * Gets an object which is an instance of the given class associated with
     * this object. Returns <code>null</code> if no such object can be found.
     * This default implementation returns null.
     * 
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter( Class adapter ) {
        return null;
    }

    /**
     * Gets the image descriptor for this input.
     * 
     * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
     */
    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    /**
     * Gets the name of this editor input for display purposes.
     * 
     * @see org.eclipse.ui.IEditorInput#getName()
     */
    public String getName() {
        return fName;
    }

    /**
     * Gets the underlying <code>IStorage</code> object. The default storage
     * object is implemented as a <code>InputStream</code>.)
     * 
     * @see org.eclipse.ui.IStorageEditorInput#getStorage()
     */
    public IStorage getStorage() {
        return fStorage;
    }

    /**
     * Gets the tool tip text for this editor input.
     * 
     * @see org.eclipse.ui.IEditorInput#getToolTipText()
     */
    public String getToolTipText() {        
        /*StringBuffer sb = new StringBuffer(30);
        if (fConnInfo != null) {
        	sb.append(fConnInfo.getName());
            sb.append("/");
        }
        sb.append(fName);*/
        return "Editor input tooltip";        
    }

  
    /**
     * Sets the name of this editor input to the given name.
     * 
     * @param name the name to set
     */
    public void setName( String name ) {
        fName = name;
    }

    /**
     * Sets the underlying <code>IStorage</code> object.
     * 
     * @param storage the storage object to use
     */
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
} // end class
