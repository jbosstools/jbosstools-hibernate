/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.internal.core.hibernate.automapping;

/**
 * @author Nick - mailto:n.belaevski@exadel.com
 * created: 22.07.2005
 * 
 */
public class RangeParser {
    
    public static class Range
    {
        private int min = -1;
        private int max = -1;
    
        boolean isInRange(int value)
        {
            return ( (min == -1 || min <= value) && (max == -1 || max >= value));
        }
    }
    
    static Range getRange(String rangeStr)
    {       
        Range result = new Range();
        
        // $changed$ by Konstantin Mishin on 2005/08/10 fixed fo ORMIISTUD-607
    	//boolean returnNull = false;
        
        String[] rangeValues = rangeStr.split("-");
        if (rangeValues.length <= 2 && rangeValues.length > 0)
        {
            for (int i = 0; i < rangeValues.length; i++) {
                String string = rangeValues[i].trim();
                
                // $changed$ by Konstantin Mishin on 2005/08/10 fixed fo ORMIISTUD-607
                //try {
                    if (string.length() != 0)
                    {
                        int value = Integer.parseInt(string);
                        //if (value < 0)
                            // $changed$ by Konstantin Mishin on 2005/08/10 fixed fo ORMIISTUD-607
                            //returnNull = true;
                        
                        if (i == 0)
                            result.min = value;
                        else
                            result.max = value;
                    }
                // $changed$ by Konstantin Mishin on 2005/08/10 fixed fo ORMIISTUD-607
                //} catch (NumberFormatException e) {
                    //ExceptionHandler.log(e,e.getMessage());
                    //returnNull = true;
                //}
                // $changed$
            }
            
            if (rangeStr.indexOf('-') == -1)
                result.max = result.min;
        }
        
        // $changed$ by Konstantin Mishin on 2005/08/10 fixed fo ORMIISTUD-607
        //return !returnNull ? result : null;
        return result;
        // $changed$    
        }
}
