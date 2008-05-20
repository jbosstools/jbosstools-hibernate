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
package org.hibernate.eclipse.console.actions;

import org.eclipse.jface.viewers.TreePath;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;

public class HQLScratchpadAction extends OpenQueryEditorAction {
	public HQLScratchpadAction() {
		super( "HQL Editor" );
		setImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.HQL_EDITOR));
		setToolTipText("Open HQL Editor");		
		setEnabled( true );
	}

	protected void openQueryEditor(ConsoleConfiguration config, String query) {
		HibernateConsolePlugin.getDefault().openScratchHQLEditor(config==null?null:config.getName(), query);		
	}
	
	/* (non-Javadoc)
	 * @see org.hibernate.eclipse.console.actions.OpenQueryEditorAction#generateQuery(org.eclipse.jface.viewers.TreePath)
	 */
	protected String generateQuery(TreePath path) {
		Object node = path.getLastSegment();		
		if (node instanceof PersistentClass){
			String name = ((PersistentClass)node).getEntityName();
			return "from " + name;
		} else if (node instanceof Property){
			String prName = ((Property)node).getName();			
			PersistentClass pClass = ((Property)node).getPersistentClass();
			String enName = "";
			if (pClass != null){
				enName = pClass.getEntityName();
				enName = enName.substring(enName.lastIndexOf('.') + 1);				
			} else {
				// Generate script for Component property
				for (int i = path.getSegmentCount() - 2; i > 0; i--) {
					if (path.getSegment(i) instanceof PersistentClass){
						enName = ((PersistentClass)path.getSegment(i)).getEntityName();
						enName = enName.substring(enName.lastIndexOf('.') + 1);						
					} else if (path.getSegment(i) instanceof Property){
						prName = ((Property)path.getSegment(i)).getName() + "." + prName;
					}					
				}
			}
			return "select o." + prName + " from " + enName + " o";
		}
		return "";
	}
}