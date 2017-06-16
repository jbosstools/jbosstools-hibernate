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
package org.jboss.tools.hibernate.reddeer.wizard;

import org.jboss.reddeer.jface.wizard.WizardPage;
import org.jboss.reddeer.core.condition.JobIsRunning;
import org.jboss.reddeer.core.condition.ShellWithTextIsAvailable;
import org.jboss.reddeer.swt.condition.WidgetIsEnabled;
import org.jboss.reddeer.swt.impl.button.PushButton;
import org.jboss.reddeer.swt.impl.button.RadioButton;
import org.jboss.reddeer.swt.impl.combo.DefaultCombo;
import org.jboss.reddeer.swt.impl.group.DefaultGroup;
import org.jboss.reddeer.common.wait.TimePeriod;
import org.jboss.reddeer.common.wait.WaitUntil;
import org.jboss.reddeer.common.wait.WaitWhile;

/**
 * JPA Facets wizard page reddeer implementation
 * @author jpeterka
 * TOTO move to reddeer
 *
 */
public class JpaFacetInstallPage extends WizardPage {
	
	/**
	 * Sets JPA platform
	 * @param platform given platform
	 */
	public void setPlatform(String platform) {
		new DefaultCombo(0).setSelection(platform);
	}	

	/**
	 * sets JPA implementation 
	 * @param impl given implementation
	 */
	public void setJpaImplementation(String impl) {
		new DefaultCombo(1).setSelection(impl.toString());
	}

	/**
	 * Sets connection profile for JPA 
	 * @param profileName given connection profile
	 */
	public void setConnectionProfile(String profileName) {
		DefaultGroup group = new DefaultGroup("Connection");
		new WaitUntil(new WidgetIsEnabled(new DefaultCombo(group)));
		new DefaultCombo(group).setSelection(profileName);
		PushButton apply = new PushButton("Apply");
		apply.click();
		new WaitWhile(new JobIsRunning());
		new WaitWhile(new ShellWithTextIsAvailable("Progress Information"), TimePeriod.LONG);
	}

	/**
	 * Set JPA autodiscovery
	 * @param autoDiscovery if set to true autodiscovery is set
	 */
	public void setAutoDiscovery(boolean autoDiscovery) {
		if (autoDiscovery) {
			new RadioButton("Discover annotated classes automatically").click();
		}
		
	}

}
