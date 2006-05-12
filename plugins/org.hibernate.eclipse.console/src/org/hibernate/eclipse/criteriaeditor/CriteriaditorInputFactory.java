package org.hibernate.eclipse.criteriaeditor;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;

public class CriteriaditorInputFactory implements IElementFactory {

    public final static String ID_FACTORY =  "org.hibernate.eclipse.criteriaeditor.CriteriaEditorInputFactory"; //$NON-NLS-1$
    public final static String ID_STORAGE_EDITOR_INPUT = "CriteriaEditorStorageEditorInput"; //$NON-NLS-1$
    
    public final static String KEY_CONFIGURATION_NAME = "configurationname"; //$NON-NLS-1$
    public final static String KEY_EDITOR_INPUT_TYPE = "editorInputType"; //$NON-NLS-1$ 
    public final static String KEY_STORAGE_CONTENT = "storageContent"; //$NON-NLS-1$
    public final static String KEY_STORAGE_NAME = "storageName"; //$NON-NLS-1$

    public IAdaptable createElement( IMemento memento ) {
        IAdaptable input = null;
        
        // Get the editor input type from the memento.
        String editorInputType = memento.getString( KEY_EDITOR_INPUT_TYPE );
        
        // Create a Storage object from the memento.
        String contentName = memento.getString( KEY_STORAGE_NAME );
        String contentString = memento.getString( KEY_STORAGE_CONTENT );
        String configurationName = memento.getString(KEY_CONFIGURATION_NAME);
        CriteriaEditorStorage storage = new CriteriaEditorStorage( configurationName, contentName, contentString );
        
        CriteriaEditorInput criteriaStorageInput = new CriteriaEditorInput( storage );
        
                
        input = criteriaStorageInput;        

        return input; 
    }

    public static void saveState(IMemento memento, CriteriaEditorInput input) {
        // Save the editor input type.
        memento.putString( KEY_EDITOR_INPUT_TYPE, ID_STORAGE_EDITOR_INPUT );
        
        String storageName = null;
        String storageContent = ""; //$NON-NLS-1$
        String configurationName = "";
        IStorage storage = input.getStorage();
        if (storage != null) {
            storageName = storage.getName();            
            if (storage instanceof CriteriaEditorStorage) {
                CriteriaEditorStorage criteriaEditorStorage = (CriteriaEditorStorage) storage;
                storageContent = criteriaEditorStorage.getContentsString();
                configurationName = criteriaEditorStorage.getConfigurationName();
            }
        }
     
        // Save the storage content name in the memento.
        memento.putString( KEY_STORAGE_NAME, storageName );
        
        // Save the storage content string in the memento.
        memento.putString( KEY_STORAGE_CONTENT, storageContent );
        
        memento.putString( KEY_CONFIGURATION_NAME, configurationName);
    }
}
