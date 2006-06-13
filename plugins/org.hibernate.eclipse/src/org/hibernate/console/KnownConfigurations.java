package org.hibernate.console;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.hibernate.SessionFactory;
import org.hibernate.console.node.BaseNode;
import org.hibernate.console.node.ConfigurationListNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class keeps track of the Hibernate Configurations that are known to
 * the Hibernate Console plugin.
 */
public class KnownConfigurations  {

	// TODO: is the best way for the querypage model ?
	private QueryPageModel queryPages = new QueryPageModel(); 
	private List configurationListeners = new ArrayList();
	private Map configurations;
	private ConsoleConfigurationListener sfListener = new ConsoleConfigurationListener() {
	
		public void sessionFactoryClosing(final ConsoleConfiguration configuration, final SessionFactory closingFactory) {
			fireNotification(new Notification() {
				public void notify(KnownConfigurationsListener listener) {
					listener.sessionFactoryClosing(configuration, closingFactory);
				}
			});
		}
			
		public void sessionFactoryBuilt(final ConsoleConfiguration ccfg, final SessionFactory builtSessionFactory) {
			fireNotification(new Notification() {
				public void notify(KnownConfigurationsListener listener) {
					listener.sessionFactoryBuilt(ccfg, builtSessionFactory);
				}
			});
		}

		public void queryPageCreated(QueryPage qp) {
			queryPages.add(qp); 						
		}
	
	};

	private static KnownConfigurations instance;
	
	public static synchronized KnownConfigurations getInstance() {
		if (instance == null) {
			instance = new KnownConfigurations();
		}
		return instance;
	}
	
	private abstract class Notification {
	
		public void run(KnownConfigurationsListener listener) {
			notify(listener);
		}

		/**
		 * Subsclasses overide this method to send an event safely to a lsistener
		 * @param listener
		 */
		protected abstract void notify(KnownConfigurationsListener listener);
	}
	
	/**
	 * Register to receive notification of repository creation and disposal
	 */
	public void addConsoleConfigurationListener(KnownConfigurationsListener listener) {
		synchronized(configurationListeners) {
			configurationListeners.add(listener);			
		}
	}
	
	/**
	 * De-register a listener
	 */
	public void removeConfigurationListener(KnownConfigurationsListener listener) {
		synchronized(configurationListeners) {
			configurationListeners.remove(listener);
		}
	}

	/**
	 * Add the repository to the receiver's list of known configurations. Doing this will enable
	 * 
	 */
	public ConsoleConfiguration addConfiguration(final ConsoleConfiguration configuration, boolean broadcast) {
		// Check the cache for an equivalent instance and if there is one, just update the cache
		ConsoleConfiguration existingConfiguration = internalGetRepository(configuration.getName() );
		if (existingConfiguration == null) {
			// Store the location
			// Cache the location instance for later retrieval
			getRepositoriesMap().put(configuration.getName(), configuration);
			configuration.addConsoleConfigurationListener(sfListener);			
			//TODO: location.storePreferences();
			existingConfiguration = configuration;
		}
		
		if (broadcast) {
			fireNotification(new Notification() {
				public void notify(KnownConfigurationsListener listener) {
					listener.configurationAdded(configuration);
				}
			});
		}
		return existingConfiguration;
	}
	
	public void removeAllConfigurations() {
		ConsoleConfiguration[] cfgs = getConfigurations();
		for (int i = 0; i < cfgs.length; i++) {
			ConsoleConfiguration configuration = cfgs[i];
			removeConfiguration(configuration);
		}
		
	}
	
	public void removeConfiguration(final ConsoleConfiguration configuration) {
		
		ConsoleConfiguration oldConfig = (ConsoleConfiguration) getRepositoriesMap().remove(configuration.getName() );
		if (oldConfig != null) {
			oldConfig.removeConsoleConfigurationListener(sfListener);
			fireNotification(new Notification() {
				public void notify(KnownConfigurationsListener listener) {
					listener.configurationRemoved(configuration);
				}
			});
			oldConfig.reset();
		}
	}

	/**
	 * Answer whether the provided configuration name is known by the provider or not.
	 * The name string corresponds to the Strin returned by ConsoleConfiguration#getName()
	 */
	public boolean isKnownConfiguration(String name) {
		return internalGetRepository(name) != null;
	}

	/** 
	 * Return a list of the know repository locations
	 */
	public ConsoleConfiguration[] getConfigurations() {
		return (ConsoleConfiguration[])getRepositoriesMap().values().toArray(new ConsoleConfiguration[getRepositoriesMap().size()]);
	}
	
	private ConsoleConfiguration internalGetRepository(String location) {
		return (ConsoleConfiguration) getRepositoriesMap().get(location);
	}
	
	
	private Map getRepositoriesMap() {
		if (configurations == null) {
			configurations = new TreeMap();
		}
		return configurations;
	}
	
	private KnownConfigurationsListener[] getListeners() {
		synchronized(configurationListeners) {
			return (KnownConfigurationsListener[]) configurationListeners.toArray(new KnownConfigurationsListener[configurationListeners.size()]);
		}
	}
	
	private void fireNotification(Notification notification) {
		// Get a snapshot of the listeners so the list doesn't change while we're firing
		KnownConfigurationsListener[] listeners = getListeners();
		// Notify each listener in a safe manner (i.e. so their exceptions don't kill us)
		for (int i = 0; i < listeners.length; i++) {
			KnownConfigurationsListener listener = listeners[i];
			notification.run(listener);
		}
	}

	BaseNode rootNode = new ConfigurationListNode(this);
			
	public BaseNode getRootNode() {
		return rootNode;		
	}

	public ConsoleConfiguration find(String lastUsedName) {
		if(configurations==null) return null;
		return (ConsoleConfiguration) configurations.get(lastUsedName);
	}
	
	public QueryPageModel getQueryPageModel() {
		return queryPages;
	}

	public void writeStateTo(File f) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();
			
			Element element = document.createElement("hibernate-console");
			Node node = document.appendChild(element);
			
			ConsoleConfiguration[] configs = getConfigurations();
			for (int i = 0; i < configs.length; i++) {
				ConsoleConfiguration cfg = configs[i];
				cfg.getPreferences().writeStateTo(element);
			}
			
			writeXml(document, f);
		} 
		catch (TransformerConfigurationException e) {
			throw new HibernateConsoleRuntimeException("Could not write state",e);
		} 
		catch (TransformerException e) {
			throw new HibernateConsoleRuntimeException("Could not write state",e);
		} 
		catch (ParserConfigurationException e) {
			throw new HibernateConsoleRuntimeException("Could not write state",e);
		}
	}

	private void writeXml(Document document, File f) throws TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		// Prepare the DOM document for writing
		Source source = new DOMSource(document);
   
		// Prepare the output file
		Result result = new StreamResult(f);
   
		// Write the DOM document to the file
		Transformer xformer = TransformerFactory.newInstance().newTransformer();
		xformer.setOutputProperty(OutputKeys.INDENT, "true");
		xformer.transform(source, result);
	}

	List queryParameters = new ArrayList();
	
	public ConsoleQueryParameter[] getQueryParameters() {
		return (ConsoleQueryParameter[]) queryParameters.toArray(new ConsoleQueryParameter[queryParameters.size()]);
	}
	
	public List getQueryParameterList() {
		return queryParameters;
	}	
}
