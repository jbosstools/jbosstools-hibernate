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
    public static final RGB HQL_IDENTIFIER_COLOR           = new RGB( 0, 128, 128 );   
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