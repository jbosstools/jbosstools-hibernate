/*******************************************************************************
  * Copyright (c) 2007-2009 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.hibernate.eclipse.jdt.ui.internal.jpa.process.wizard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.hibernate.eclipse.jdt.ui.internal.jpa.collect.AllEntitiesInfoCollector;
import org.hibernate.eclipse.jdt.ui.internal.jpa.collect.CompilationUnitCollector;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.EntityInfo;
import org.hibernate.eclipse.jdt.ui.internal.jpa.process.ChangeStructure;

/**
 * @author Vitali
 */
public class HibernateJPAWizardDataFactory {

	static public IHibernateJPAWizardData createHibernateJPAWizardData(
			final Map<String, EntityInfo> entities, 
			final IStructuredSelection selection2Update,
			final ArrayList<ChangeStructure> changes) {

		IHibernateJPAWizardData data = new IHibernateJPAWizardData() {

			public Map<String, EntityInfo> getEntities() {
				return entities;
			}

			public ArrayList<ChangeStructure> getChanges() {
				return changes;
			}

			public IStructuredSelection getSelection2Update() {
				return selection2Update;
			}
			
		};
		return data;
	}

	@SuppressWarnings("unchecked")
	static public IHibernateJPAWizardData createHibernateJPAWizardData(
			final IStructuredSelection selection2Update, 
			IHibernateJPAWizardParams params, int depth) {

		CompilationUnitCollector compileUnitCollector = new CompilationUnitCollector();
		Iterator itSelection2Update = selection2Update.iterator();
		while (itSelection2Update.hasNext()) {
			Object obj = itSelection2Update.next();
			compileUnitCollector.processJavaElements(obj, true);
		}
		Iterator<ICompilationUnit> it = compileUnitCollector.setSelectionCUIterator();
		AllEntitiesInfoCollector collector = new AllEntitiesInfoCollector();
		collector.initCollector();
		while (it.hasNext()) {
			ICompilationUnit cu = it.next();
			collector.collect(cu, depth);
		}
		collector.resolveRelations();
		final Map<String, EntityInfo> entities = collector.getMapCUs_Info();

		params.performDisconnect();
		params.reCollectModification(entities);
		
		final ArrayList<ChangeStructure> changes = params.getChanges();

		IHibernateJPAWizardData data = new IHibernateJPAWizardData() {

			public Map<String, EntityInfo> getEntities() {
				return entities;
			}

			public ArrayList<ChangeStructure> getChanges() {
				return changes;
			}

			public IStructuredSelection getSelection2Update() {
				return selection2Update;
			}
			
		};
		return data;
	}

}
