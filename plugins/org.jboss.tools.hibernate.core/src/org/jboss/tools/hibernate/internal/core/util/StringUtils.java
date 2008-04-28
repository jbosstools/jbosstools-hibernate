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
package org.jboss.tools.hibernate.internal.core.util;

import java.beans.Introspector;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.swt.widgets.TreeItem;
import org.jboss.tools.hibernate.core.IOrmElement;

/**
 * @author Nick
 *
 */
public class StringUtils {

    public static final String EOL = System.getProperty("line.separator");
    
    public static final String JAVA_IDENTIFIER_REGEXP = "_*[a-zA-Z].*";
    
    public static boolean isValidJavaName(String fieldName)
    {
        return JavaConventions.validateFieldName(fieldName).matches(IStatus.INFO|IStatus.WARNING|IStatus.OK);
    }
    
    public static String beanDecapitalize(String name)
    {
        return Introspector.decapitalize(name);
    }
    
    private static void decapitalizeFlushWord(StringBuffer result, StringBuffer word)
    {
        if (word.length() > 0)
        {
            if (Character.isUpperCase(word.charAt(0)))
            {
                result.append(StringUtils.capitalize(word.toString().toLowerCase()));
            }
            else
            {
                result.append(word.toString().toLowerCase());
            }
        }
    }
    
    public static String decapitalize(String name) {
        if (name == null || name.length() == 0)
            return name;
        
        StringBuffer result = new StringBuffer();
        
        char[] chars = name.toCharArray();
        int index = 0;
        
//        String regexSeries = "[A-Z]{2}([A-Z]+)|([0-9_]+)";
        
        StringBuffer strikesString = new StringBuffer();
        
        while (index < chars.length && chars[index] == '_') {
            index++;
            strikesString.append('_');
        }
        
        StringBuffer word = new StringBuffer();
        
        while (index < chars.length - 1) {
            boolean flushWord = false;
            
            if (Character.isUpperCase(chars[index]) && (Character.isLowerCase(chars[index + 1])/* || !Character.isLetter(chars[index + 1])*/) ) {
                flushWord = true;
            }
            
            if (flushWord) {
                decapitalizeFlushWord(result,word);
                word = new StringBuffer();
            }
        
            word.append(chars[index]);

            if (!flushWord) {
                if (!flushWord && (Character.isLowerCase(chars[index]) || !Character.isLetter(chars[index])) && (Character.isUpperCase(chars[index + 1])))
                    flushWord = true;
                    
                if (flushWord) {
                    decapitalizeFlushWord(result,word);
                    word = new StringBuffer();
                }
            }

            index++;
        }
            
        while (index < chars.length) {
            word.append(chars[index]);
            index++;
        }
        decapitalizeFlushWord(result,word);
        
        if (result.length() != 0)
        {
            result.setCharAt(0,Character.toLowerCase(result.charAt(0)));
        }
        
        return strikesString.append(result).toString();
    }

    public static String capitalize(String name)
    {
        if (name == null || name.length() == 0)
            return name;
        
        StringBuffer sb = new StringBuffer(name);
        sb.setCharAt(0,Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }
    
    public static String safeStrCoupling(String str1, String str2)
    {
        if (str1 == null)
            return str2;
        else
            if (str2 != null)
                return str1 + "_" + str2;
            else
                return str1;
    }

    public static String getUnPrefixedName(IOrmElement child, IOrmElement parent)
    {
        if (parent == null || child == null)
            return null;
        
        String childName = child.getName();
        String parentName = parent.getName();

        String result = null;
        
        if (parentName == null || childName == null)
            return null;
        
        if (childName.endsWith(parentName))
        {
            int index = childName.length() - parentName.length();
            result = childName.substring(0,index);
        }
        
        if (result == null || result.length() == 0)
            return null;


        return result;
    }
    
    private static final String HIBERNATE_QUOTE = "`";
    
    public static String hibernateEscapeName(String name)
    {
        if (name == null)
            return name;
        
        // #changed# by Konstantin Mishin on 12.12.2005 fixed for ESORM-398
        //if ( (name.indexOf(' ') != -1 || name.length() == 0) && !name.startsWith(HIBERNATE_QUOTE))
        if ( (name.indexOf(' ') != -1 || name.length() == 0) && !name.startsWith(HIBERNATE_QUOTE) && !name.endsWith(HIBERNATE_QUOTE))
        // #changed#
                    {
            return HIBERNATE_QUOTE + name + HIBERNATE_QUOTE;
        }
        else
        {
            return name;
        }
    }

    public static String hibernateUnEscapeName(String name)
    {
        if (name == null)
            return name;
        
        String result = name;
        
        if (name.startsWith(HIBERNATE_QUOTE))
        {
            result = name.substring(1);
        }
        if (result.endsWith(HIBERNATE_QUOTE))
        {
            result = result.substring(0,result.length() - 1);
        }
        
        return result;
    }

    public static String beanCapitalize(String name)
    {
        StringBuffer bean_name = new StringBuffer();
        if (name.length() > 0)
        {
            if (name.length() >= 2 && Character.isUpperCase(name.charAt(1)))
            {
                return name;
            }
            
            bean_name.append(name.substring(0,1).toUpperCase());
            if (name.length() >= 1)
                bean_name.append(name.substring(1));
        }
        return bean_name.toString();
    }
    
    
// added by yk 25.08.2005
    public static String getWord(final String sentence, final String delim, final int pos)
    {
    	String[] words = sentence.split(delim);
    	return words[pos];
    }
// added by yk 25.08.2005.
    
	// add tau 02.12.2005
    public static String parentItemName(TreeItem argument, String iItemName) {
		TreeItem parentItem = argument.getParentItem();
		if (parentItem != null) {
			Object data = parentItem.getData();
			return ((IOrmElement) data).getQualifiedName(parentItem) + "#" + iItemName;
		} else return iItemName;
	}
    
}
