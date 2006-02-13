package org.hibernate.eclipse.hqleditor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class HQLColors {

    public static final RGB HQL_COMMENT_COLOR              = new RGB( 0, 0, 128 );  
    public static final RGB HQL_MULTILINE_COMMENT_COLOR    = new RGB( 128, 0, 0 );  
    public static final RGB HQL_QUOTED_LITERAL_COLOR       = new RGB( 0, 0, 255 );  
    public static final RGB HQL_KEYWORD_COLOR              = new RGB( 128, 0, 0 );  
    public static final RGB HQL_IDENTIFIER_COLOR           = new RGB( 0, 0, 128 );   
    public static final RGB HQL_DEFAULT_COLOR              = new RGB( 0, 0, 0 );    
        
    protected Map colorTable = new HashMap(10);

    public void dispose() {
        Iterator e = colorTable.values().iterator();
        while (e.hasNext())
            ((Color) e.next()).dispose();
    }
    
    public Color getColor( RGB rgb ) {
        Color color = (Color) colorTable.get( rgb );
        if (color == null) {
            color = new Color( Display.getCurrent(), rgb );
            colorTable.put( rgb, color );
        }
        return color;
    }
    
}