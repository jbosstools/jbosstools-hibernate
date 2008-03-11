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

import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * @author kaa
 * akuzmin@exadel.com
 * Aug 3, 2005
 */
public class TextPropertyDescriptorWithType extends
	TextPropertyDescriptor implements IDescriptorWithType{
	public TextPropertyDescriptorWithType(Object id, String displayName,boolean isint,boolean isUseLeftRange,boolean isUseRightRange,Object leftRange,Object rightRange) {
		super(id, displayName);
		this.isint=isint;
		this.isUseLeftRange=isUseLeftRange;
		this.isUseRightRange=isUseRightRange;		
		if (isUseLeftRange)
		{
			if (isint)
			{
				if (leftRange instanceof Integer)
					this.leftRange=leftRange;
				else this.isUseLeftRange=false;
			}
			else
			{
				if (leftRange instanceof Float)
					this.leftRange=leftRange;
				else this.isUseLeftRange=false;
			}
		}
		if (isUseRightRange)
		{
			if (isint)
				{
					if (rightRange instanceof Integer)
						this.rightRange=rightRange;
					else this.isUseRightRange=false;
				}
			else 
			{
				if (rightRange instanceof Float)
					this.rightRange=rightRange;
				else this.isUseRightRange=false;
				
			}
		}
	}

	private boolean isint;
	private boolean isUseLeftRange;
	private boolean isUseRightRange;	
	private Object leftRange;
	private Object rightRange;
	

	/**
	 * @return Returns the isint.
	 */
	public boolean isIsint() {
		return isint;
	}

	/**
	 * @param isint The isint to set.
	 */
	public void setIsint(boolean isint) {
		this.isint = isint;
	}

	/**
	 * @return Returns the isUseLeftRange.
	 */
	public boolean isUseLeftRange() {
		return isUseLeftRange;
	}

	/**
	 * @return Returns the isUseRightRange.
	 */
	public boolean isUseRightRange() {
		return isUseRightRange;
	}

	/**
	 * @return Returns the leftRange.
	 */
	public Object getLeftRange() {
		return leftRange;
	}

	/**
	 * @return Returns the rightRange.
	 */
	public Object getRightRange() {
		return rightRange;
	}
	
	
}
