package org.hibernate.console.node;

import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ImageConstants;
import org.hibernate.console.views.SessionFactoryListener;

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
		configuration.addListener(new SessionFactoryListener() {
			public void factoryCreated(ConsoleConfiguration ccfg) {
				clear();
			}

			public void factoryUpdated(ConsoleConfiguration ccfg) {
			}

			public void factoryClosed(ConsoleConfiguration configuration) {
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
				children.add(fac.createConfigurationEntitiesNode("Mapped entities") );
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
