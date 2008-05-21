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
package org.jboss.tools.hibernate.internal.core.properties;

import java.util.ArrayList;

import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * @author kaa
 * akuzmin@exadel.com
 * Sep 29, 2005
 */
public class TextPropertyDescriptorWithTemplate extends TextPropertyDescriptor
		implements IDescriptorWithTemplates {

	private String frazeSeparator;
	private String wordSeparator;
	private int numberWord;
	private String requiredWord;
	private String allChars;
	public TextPropertyDescriptorWithTemplate(Object id, String displayName,String frazeSeparator,String wordSeparator,int numberWord,String requiredWord,String allChars) {
		super(id, displayName);
		this.frazeSeparator=frazeSeparator;
		this.wordSeparator=wordSeparator;
		this.numberWord=numberWord;
		this.requiredWord=requiredWord;
		this.allChars=allChars;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.properties.IDescriptorWithTemplates#isCorrectValue(java.lang.String)
	 */
	public boolean isCorrectValue(String value) {
		String[] frazes=value.split(frazeSeparator);
		for(int i=0;i<frazes.length;i++)
		{
			String[] words=frazes[i].split(wordSeparator);
			if (words.length!=numberWord)
				return false;
			if (words[0].split(requiredWord).length!=numberWord)
				return false;
			if (words[1].split(requiredWord).length!=numberWord)
				return false;
			String tempstr=words[0];
			for(int z=0;z<allChars.length();z++)
				tempstr=tempstr.replace(allChars.charAt(z),' ');
			if (tempstr.trim().length()>0)
				return false;
			tempstr=words[1];
			for(int z=0;z<allChars.length();z++)
				tempstr=tempstr.replace(allChars.charAt(z),' ');
			if (tempstr.trim().length()>0)
				return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.hibernate.internal.core.properties.IDescriptorWithTemplates#getCorrectedValue(java.lang.String)
	 */
	public String getCorrectedValue(String value) {
		String[] frazes=value.split(frazeSeparator);
		ArrayList newstr = new ArrayList();
		for(int i=0;i<frazes.length;i++)
		{
			String[] words=frazes[i].split(wordSeparator);
			if (words.length!=numberWord)
				continue;
			if ((" "+words[0]+" ").split(requiredWord).length!=numberWord)
				continue;
			if ((" "+words[1]+" ").split(requiredWord).length!=numberWord)
				continue;
			String tempstr=words[0];
			for(int z=0;z<allChars.length();z++)
				tempstr=tempstr.replace(allChars.charAt(z),' ');
			if (tempstr.trim().length()>0)
				continue;
			tempstr=words[1];
			for(int z=0;z<allChars.length();z++)
				tempstr=tempstr.replace(allChars.charAt(z),' ');
			if (tempstr.trim().length()>0)
				continue;
			newstr.add(frazes[i]);
		}
		
		if (newstr.size()==0) return null;
		String res="";
		for(int i=0;i<newstr.size();i++)
			res=res+(String) newstr.get(i)+";";
			
		return res;
	}

}
