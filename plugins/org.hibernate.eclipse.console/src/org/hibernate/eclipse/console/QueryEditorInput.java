package org.hibernate.eclipse.console;

import org.eclipse.core.resources.IStorage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;

public abstract class QueryEditorInput  implements IStorageEditorInput, IPersistableElement {

    /** The name of ths editor input. */
    private String name;
    /** The storage object used by this editor input. */
    private IStorage storage;

    /**
     * Constructs an instance of this class with the given <code>IStorage</code>
     * object as the editor input source.
     *
     * @param storage the storage object for this editor input
     */
    public QueryEditorInput( IStorage storage ) {
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

    @SuppressWarnings("unchecked")
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


	public boolean equals(Object obj) { // used to identify if HQL editor is the same
		/*if(obj instanceof HQLEditorInput) {

			return ((HQLEditorInput)obj).getConsoleConfigurationName().equals(getConsoleConfigurationName());
		}*/
		return super.equals(obj);
	}

	public int hashCode() {
		return name==null?0:name.hashCode();
	}


	public void setQuery(String query) {
		if (getStorage() instanceof QueryEditorStorage) {
			((QueryEditorStorage)getStorage()).setContents(query);
		}
	}

	public String getConsoleConfigurationName() {
		return ((QueryEditorStorage)getStorage()).getConfigurationName();
	}

	public void setConsoleConfigurationName(String name2) {
		((QueryEditorStorage)getStorage()).setConfigurationName(name2);
	}

	abstract public void resetName();

}
