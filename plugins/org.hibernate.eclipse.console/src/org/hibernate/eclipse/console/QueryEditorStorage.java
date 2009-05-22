package org.hibernate.eclipse.console;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IPath;

public class QueryEditorStorage implements IStorage {

	private String contents;
    private String nameLabel;
	private String configurationName;

    public QueryEditorStorage( String source ) {
        this( "", source, source ); //$NON-NLS-1$
    }

    public QueryEditorStorage( String configurationName, String name, String source ) {
        super();
        setName( name );
        setQuery( source );
        setConfigurationName(configurationName);
    }

	public void setQuery(String source) {
		if(source==null) { return; }
		setContents(source);
	}

    @SuppressWarnings("unchecked")
	public Object getAdapter( Class key ) {
        return null;
    }

    public InputStream getContents() {
    	return new ByteArrayInputStream( contents.getBytes() );
    }

    /**
     * @return contents as a string
     */
    public String getContentsString() {
        String contentsString = "";  //$NON-NLS-1$
        
        InputStream contentsStream = getContents();
        
        // The following code was adapted from StorageDocumentProvider.setDocumentContent method.
        Reader in = null;
        try {
            in = new BufferedReader( new InputStreamReader( contentsStream ));
            StringBuffer buffer = new StringBuffer();
            char[] readBuffer = new char[2048];
            int n = in.read( readBuffer );
            while (n > 0) {
                buffer.append( readBuffer, 0, n );
                n = in.read( readBuffer );
            }
            contentsString = buffer.toString();
        } catch (IOException x) {
            // ignore and save empty content
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException x) {
                    // ignore, too late to do anything here
                }
            }
        }

        return contentsString;
    }
    
    public IPath getFullPath() {
        return null;
    }

    public String getName() {
        return nameLabel;
    }

    public boolean isReadOnly() {
        return false;
    }


    public void setName( String name ) {
        nameLabel = name;
    }

	public String getConfigurationName() {
		return configurationName;
	}

	public void setConfigurationName(String configurationName) {
		this.configurationName = configurationName;		
	}

	public void setContents(String query) {
		this.contents = query;
	}

}
