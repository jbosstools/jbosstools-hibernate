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
package org.hibernate.eclipse.console;

import org.eclipse.osgi.util.NLS;

public class Messages {
	public static final String BUNDLE_NAME = "org.hibernate.eclipse.console.messages"; //$NON-NLS-1$


	public static String popup_copy_text;
	public static String popup_paste_text;
	public static String popup_select_all;
	public static String find_replace_action_label;
	public static String find_replace_action_tooltip;
	public static String find_replace_action_image;
	public static String find_replace_action_description;

	private Messages() { 
		// noop		
	}
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
