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
package org.hibernate.console.node;

import java.util.Arrays;
import java.util.Comparator;

import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.KnownConfigurationsAdapter;

/**
 * @author max
 *
 */
public class ConfigurationListNode extends BaseNode {

	private boolean childrenCreated;
	private final KnownConfigurations kc;

	public ConfigurationListNode(KnownConfigurations kc) {
		super(null,null);
		this.kc = kc;
		
		kc.addConsoleConfigurationListener(new KnownConfigurationsAdapter() {
			public void configurationAdded(ConsoleConfiguration root) {
				markChildrenForReload();				
			}

			private void markChildrenForReload() {
				children.clear();
				childrenCreated=false;
			}

			public void configurationRemoved(ConsoleConfiguration root, boolean forUpdate) {
				markChildrenForReload();
			}
		});
	}
	
	protected void checkChildren() {
		if(!childrenCreated) {
			ConsoleConfiguration[] configurations = kc.getConfigurations();
			Arrays.sort(configurations, new Comparator<ConsoleConfiguration>() {
				public int compare(ConsoleConfiguration o1, ConsoleConfiguration o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
			for (int i = 0; i < configurations.length; i++) {
				children.add(new ConfigurationNode(this, configurations[i]) );	
			}
			childrenCreated = true;
		}
	}

	public String getHQL() {
		return null;
	}

}
