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

import org.hibernate.SessionFactory;
import org.hibernate.console.ConcoleConfigurationAdapter;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ConsoleMessages;
import org.hibernate.console.ImageConstants;

/**
 * @author max
 *
 */
public class ConfigurationNode extends BaseNode {

	private final ConsoleConfiguration configuration;
	private boolean childrenCreated;

	public ConfigurationNode(BaseNode parent, ConsoleConfiguration configuration) {
		super(null,parent);
		this.configuration = configuration;
		configuration.addConsoleConfigurationListener(new ConcoleConfigurationAdapter() {
			public void sessionFactoryBuilt(ConsoleConfiguration ccfg, SessionFactory builtSessionFactory) {
				clear();
			}

			public void sessionFactoryClosing(ConsoleConfiguration configuration, SessionFactory closedSessionFactory) {
				clear();
			}
		});

		name = configuration.getName();
		iconName = ImageConstants.CONFIGURATION;
	}

	public ConsoleConfiguration getConsoleConfiguration() {
		return configuration;
	}

	protected void checkChildren() {
		if(!childrenCreated) {
			if(configuration.isSessionFactoryCreated() ) {
				NodeFactory fac = new NodeFactory(configuration);
				children.add(fac.createConfigurationEntitiesNode(ConsoleMessages.ConfigurationNode_mapped_entities) );
				childrenCreated=true;

				/*Configuration cfg = configuration.getConfiguration();
				Iterator iter = cfg.getTableMappings();
				while (iter.hasNext() ) {// todo: move to nodefactory.
					Table table = (Table) iter.next();
					TableNode node = NodeFactory.createTableNode(this,table);
					children.add(node);
				}	*/
			}else {
				children.clear();
			}
		}
	}

	public String getHQL() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.hibernate.console.node.BaseNode#clear()
	 */
	public void clear() {
		super.clear();
		childrenCreated = false;
	}
}
