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
import org.eclipse.osgi.util.NLS;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;

public class CriteriaEditorAction extends OpenQueryEditorAction {
	public CriteriaEditorAction() {
		super( HibernateConsoleMessages.CriteriaEditorAction_hibernate_criteria_editor );
		setImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.CRITERIA_EDITOR));
		setToolTipText(HibernateConsoleMessages.CriteriaEditorAction_open_hibernate_criteria_editor);
		setEnabled( true );
	}

	protected void openQueryEditor(ConsoleConfiguration config, String query) {
		HibernateConsolePlugin.getDefault().openCriteriaEditor( config==null?null:config.getName(), query );
	}

	/* (non-Javadoc)
	 * @see org.hibernate.eclipse.console.actions.OpenQueryEditorAction#generateQuery(org.eclipse.jface.viewers.TreePath)
	 */
	protected String generateQuery(TreePath path) {
		final String criteria = ".createCriteria({0})"; //$NON-NLS-1$
		final String alias = "\n.createCriteria(\"{0}\", \"{1}\")"; //$NON-NLS-1$
		final String projection = "\n.setProjection( Property.forName(\"{0}\").as(\"{0}\"))"; //$NON-NLS-1$
		final String sess = "session"; //$NON-NLS-1$
		String enCriteria = ""; //$NON-NLS-1$
		String propCriteria = ""; //$NON-NLS-1$
		String enName = ""; //$NON-NLS-1$
		Object node = path.getLastSegment();
		if (node instanceof PersistentClass){
			enName = ((PersistentClass)node).getEntityName();
			enName = enName.substring(enName.lastIndexOf('.') + 1);
		} else if (node instanceof Property){
			Property prop = (Property)node;
			String prName = prop.getName();
			PersistentClass pClass = ((Property)node).getPersistentClass();
			if (pClass != null){
				enName = pClass.getEntityName();
				enName = enName.substring(enName.lastIndexOf('.') + 1);
				if (prop.getValue().isSimpleValue()) {
				    propCriteria = NLS.bind(projection, prName);
				} else {
					propCriteria = NLS.bind(alias, prName, prName.charAt(0));
				}
			} else {
				// Generate script for Component property
				for (int i = path.getSegmentCount() - 1; i > 0; i--) {
					if (path.getSegment(i) instanceof PersistentClass){
						enName = ((PersistentClass)path.getSegment(i)).getEntityName();
						enName = enName.substring(enName.lastIndexOf('.') + 1);
					} else if (path.getSegment(i) instanceof Property){
						prName = ((Property)path.getSegment(i)).getName();
						if (prop.getValue().isSimpleValue()) {
						    propCriteria += NLS.bind(projection, prName);
						} else {
							propCriteria += NLS.bind(alias, prName, prName.charAt(0));
						}
						//propCriteria += NLS.bind(projection, prName);
					}
				}
			}
		} else {
		  return ""; //$NON-NLS-1$
		}
		enCriteria = NLS.bind(criteria, enName + ".class"); //$NON-NLS-1$
		return sess + enCriteria + propCriteria + "\n.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP)"; //$NON-NLS-1$
	}
}