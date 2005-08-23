/*
 * Created on 2004-10-29 by max
 * 
 */
package org.hibernate.console.node;

import java.util.Arrays;
import java.util.Comparator;

import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.KnownConfigurations.IConsoleConfigurationListener;

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
		
		kc.addConsoleConfigurationListener(new IConsoleConfigurationListener() {
			public void configurationAdded(ConsoleConfiguration root) {
				markChildrenForReload();				
			}

			private void markChildrenForReload() {
				children.clear();
				childrenCreated=false;
			}

			public void configurationRemoved(ConsoleConfiguration root) {
				markChildrenForReload();
			}

			public void factoryClosed(ConsoleConfiguration configuration) {
			}

			public void factoryCreated(ConsoleConfiguration ccfg) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	protected void checkChildren() {
		if(!childrenCreated) {
			ConsoleConfiguration[] configurations = kc.getConfigurations();
			Arrays.sort(configurations, new Comparator() {
				public boolean equals(Object obj) {
					return this==obj;
				}

				public int compare(Object o1, Object o2) {
					return ( (ConsoleConfiguration)o1).getName()
						.compareTo(
								( (ConsoleConfiguration)o2).getName() );
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
