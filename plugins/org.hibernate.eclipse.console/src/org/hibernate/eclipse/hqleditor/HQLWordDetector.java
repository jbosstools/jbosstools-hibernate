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

import org.eclipse.jface.text.rules.IWordDetector;

public class HQLWordDetector implements IWordDetector {

    public boolean isWordStart( char c ) {
        String[] reservedWords = HQLCodeScanner.getHQLKeywords();
        for (int i = 0; i < reservedWords.length; i++) {
            if ( (reservedWords[i].charAt(0) == c)
              || (reservedWords[i].toUpperCase().charAt(0) == c) ) {
                return true;
            }
        }   
        
        return false;
    }

    public boolean isWordPart( char c ) {
        String[] keywords = HQLCodeScanner.getHQLKeywords();
        for (int i = 0; i < keywords.length; i++) {
            if ( (keywords[i].indexOf(c) != -1)
              || (keywords[i].toUpperCase().indexOf(c) != -1) ) {
                return true;
            }
        }

        return false;
    }

} 
