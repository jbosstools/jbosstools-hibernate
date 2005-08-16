package org.hibernate.eclipse.hqleditor;

import org.eclipse.core.resources.IStorage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.hibernate.console.ConsoleConfiguration;

/**
 * input for hql editor on non file based storage.
 */
public class HQLEditorInput implements IStorageEditorInput, IPersistableElement {

    /** The name of ths editor input. */
    private String name;
    /** The storage object used by this editor input. */
    private IStorage storage;

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
        this.storage = (HQLEditorStorage) storage;
    }

	public IPersistableElement getPersistable() {
		return this;
	}

	public boolean equals(Object obj) {
		if(obj instanceof HQLEditorInput) {
			return ((HQLEditorInput)obj).getName().equals(name);
		}
		return super.equals(obj);
	}
	
	public int hashCode() {
		return name.hashCode();
	}

    public String getFactoryId() {
        return HQLEditorInputFactory.ID_FACTORY;
    }

    public void saveState(IMemento memento) {
        HQLEditorInputFactory.saveState( memento, this );
    }

	public void setQuery(String query) {
		((HQLEditorStorage)storage).setContents(query);		
	}

	public String getConsoleConfigurationName() {
		return ((HQLEditorStorage)storage).getConfigurationName();
	}

} 
