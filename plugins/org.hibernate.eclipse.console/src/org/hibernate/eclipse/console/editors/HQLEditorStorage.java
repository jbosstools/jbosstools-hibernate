package org.hibernate.eclipse.console.editors;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IPath;

/**
 * Storage for HQL editors without a file
 */
public class HQLEditorStorage implements IStorage {
    
	private InputStream contents;
    private String nameLabel;

    public HQLEditorStorage( String source ) {
        this( source, source );
    }

    public HQLEditorStorage( String name, String source ) {
        super();
        setName( name );
        setQuery( source );
    }

	public void setQuery(String source) {
		if(source==null) { return; }
		setContents( new ByteArrayInputStream( source.getBytes() ) );
	}

    public Object getAdapter( Class key ) {
        return null;
    }

    public InputStream getContents() {
        return contents;
    }

    /**
     * @return contents as a string
     */
    public String getContentsString() {
        String contentsString = ""; 
        
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

    public void setContents( InputStream contents ) {
        this.contents = contents;
    }

    public void setName( String name ) {
        nameLabel = name;
    }

}