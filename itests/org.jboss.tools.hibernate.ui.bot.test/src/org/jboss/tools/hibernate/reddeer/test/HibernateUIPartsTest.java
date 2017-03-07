/*******************************************************************************
 * Copyright (c) 2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.reddeer.test;

import static org.junit.Assert.*;

import org.jboss.reddeer.eclipse.ui.perspectives.JavaPerspective;
import org.jboss.reddeer.workbench.impl.view.WorkbenchView;
import org.jboss.tools.hibernate.reddeer.console.views.DynamicSQLPreviewView;
import org.jboss.tools.hibernate.reddeer.console.views.KnownConfigurationsView;
import org.jboss.tools.hibernate.reddeer.console.views.QueryPageTabView;
import org.jboss.tools.hibernate.reddeer.console.views.QueryParametersView;
import org.jboss.tools.hibernate.reddeer.perspective.HibernatePerspective;
import org.junit.After;
import org.junit.Test;

/**
 * Tests Hibernate UI Parts - perspective and views
 * @author Jiri Peterka
 *
 */
public class HibernateUIPartsTest {
	
	@After
	public void after() {
		JavaPerspective p = new JavaPerspective();
		p.open();
		p.reset();
	}
	
	
	/**
	 * Tests Hibernate perspective
	 * Tests Hibernate views
	 * - Hibernate Configurations
	 * - Hibernate Dynamic SQL Preview
	 * - Hibernate Query Result
	 * - Query Parameters 
	 */
	@Test
	public void testHibernateViews() {
		checkView(new KnownConfigurationsView());
		checkView(new DynamicSQLPreviewView());
		checkView(new QueryPageTabView());
		checkView(new QueryParametersView());
	}
	
	/**
	 * Tests hibernate perspective
	 */
	@Test
	public void testHibernatePerspective() {
		HibernatePerspective p = new HibernatePerspective();
		p.open();
		p.reset();
		
		assertTrue(p.getPerspectiveLabel().equals("Hibernate"));
	}
	
	/**
	 * Check bassic view operation for given view
	 * @param given view
	 */
	private void checkView(WorkbenchView view) {
		view.open();
		view.maximize();
		view.restore();
		view.minimize();
		view.restore();
		view.close();		
	}
	
	
}
