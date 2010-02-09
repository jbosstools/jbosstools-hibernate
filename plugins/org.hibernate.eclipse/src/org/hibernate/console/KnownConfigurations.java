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
package org.hibernate.console;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
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

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.hibernate.SessionFactory;
import org.hibernate.console.node.BaseNode;
import org.hibernate.console.node.ConfigurationListNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class keeps track of the Hibernate Configurations that are known to
 * the Hibernate Console plugin.
 */
public class KnownConfigurations  {

	// TODO: is the best way for the querypage model ?
	private QueryPageModel queryPages = new QueryPageModel();
	private List<KnownConfigurationsListener> configurationListeners = new ArrayList<KnownConfigurationsListener>();
	private Map<String, ConsoleConfiguration> configurations;
	private ConsoleConfigurationListener sfListener = new ConcoleConfigurationAdapter() {

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
		
		public void configurationBuilt(final ConsoleConfiguration ccfg) {
			fireNotification(new Notification() {
				public void notify(KnownConfigurationsListener listener) {
					listener.configurationBuilt(ccfg);
				}
			});
		}

		public void configurationReset(final ConsoleConfiguration ccfg) {
			fireNotification(new Notification() {
				public void notify(KnownConfigurationsListener listener) {
					listener.configurationReset(ccfg);
				}
			}); 			
		};

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
			removeConfiguration(configuration, false);
		}

	}

	// added forUpdate as a workaround for letting listeners know it is done to update the configuration so they don't cause removal issues.
	public void removeConfiguration(final ConsoleConfiguration configuration, final boolean forUpdate) {

		ConsoleConfiguration oldConfig = getRepositoriesMap().remove(configuration.getName() );
		if (oldConfig != null) {
			oldConfig.removeConsoleConfigurationListener(sfListener);
			fireNotification(new Notification() {
				public void notify(KnownConfigurationsListener listener) {
					listener.configurationRemoved(configuration, forUpdate);
				}
			});
			oldConfig.reset();
			removeLoggingStream( oldConfig );

		}


	}


	/**
	 * Answer whether the provided configuration name is known by the provider or not.
	 * The name string corresponds to the String returned by ConsoleConfiguration#getName()
	 */
	public boolean isKnownConfiguration(String name) {
		return internalGetRepository(name) != null;
	}

	/**
	 * Return a list of the know repository locations
	 */
	public ConsoleConfiguration[] getConfigurations() {
		return getRepositoriesMap().values().toArray(new ConsoleConfiguration[getRepositoriesMap().size()]);
	}

	public ConsoleConfiguration[] getConfigurationsSortedByName() {
		return getConfigurations(new Comparator<ConsoleConfiguration>() {
			public int compare(ConsoleConfiguration o1, ConsoleConfiguration o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
	}

	public ConsoleConfiguration[] getConfigurations(Comparator<ConsoleConfiguration> c) {
		ConsoleConfiguration[] configurations = getConfigurations();
		Arrays.sort(configurations, c);
		return configurations;
	}

	private ConsoleConfiguration internalGetRepository(String location) {
		return getRepositoriesMap().get(location);
	}


	private Map<String, ConsoleConfiguration> getRepositoriesMap() {
		if (configurations == null) {
			configurations = new TreeMap<String, ConsoleConfiguration>();
		}
		return configurations;
	}

	private KnownConfigurationsListener[] getListeners() {
		synchronized(configurationListeners) {
			return configurationListeners.toArray(new KnownConfigurationsListener[configurationListeners.size()]);
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

	// TODO: decouple this logging from Eclipse platform!
	private Map<String, Object[]> loggingStreams = new HashMap<String, Object[]>();
	public MessageConsoleStream findLoggingStream(String name) {
		Object[] console = loggingStreams.get(name);
		if(console==null) {
			console = new Object[2];
			String secondaryId = ConsoleMessages.KnownConfigurations_hibernate_log + (name==null?ConsoleMessages.KnownConfigurations_unknown:name);
        	console[0] = new MessageConsole(secondaryId, null);
        	IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
        	consoleManager.addConsoles(new IConsole[] { (IConsole) console[0] });
        	console[1] = ((MessageConsole)console[0]).newMessageStream();
        	loggingStreams.put(name, console);
		}
		return (MessageConsoleStream) console[1];
	}

	private void removeLoggingStream(ConsoleConfiguration oldConfig) {
		Object[] object = loggingStreams.remove( oldConfig.getName() );
		if(object!=null) {
			MessageConsole mc = (MessageConsole)object[0];
			MessageConsoleStream stream = (MessageConsoleStream)object[1];
			try { stream.close(); } catch(IOException ie) { /* ignore */ };
			IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
			consoleManager.removeConsoles( new IConsole[] { mc } );
		}
	}


	public ConsoleConfiguration find(String lastUsedName) {
		if(configurations==null) return null;
		if(lastUsedName==null) return null;
		return configurations.get(lastUsedName);
	}

	public QueryPageModel getQueryPageModel() {
		return queryPages;
	}

	public void writeStateTo(File f) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.newDocument();

			Element element = document.createElement("hibernate-console"); //$NON-NLS-1$
			document.appendChild(element);

			ConsoleConfiguration[] configs = getConfigurations();
			for (int i = 0; i < configs.length; i++) {
				ConsoleConfiguration cfg = configs[i];
				cfg.getPreferences().writeStateTo(element);
			}

			writeXml(document, f);
		}
		catch (TransformerConfigurationException e) {
			throw new HibernateConsoleRuntimeException(ConsoleMessages.KnownConfigurations_could_not_write_state,e);
		}
		catch (TransformerException e) {
			throw new HibernateConsoleRuntimeException(ConsoleMessages.KnownConfigurations_could_not_write_state,e);
		}
		catch (ParserConfigurationException e) {
			throw new HibernateConsoleRuntimeException(ConsoleMessages.KnownConfigurations_could_not_write_state,e);
		}
	}

	private void writeXml(Document document, File f) throws TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		// Prepare the DOM document for writing
		Source source = new DOMSource(document);

		// Prepare the output file
		Result result = new StreamResult(f);

		// Write the DOM document to the file
		Transformer xformer = TransformerFactory.newInstance().newTransformer();
		xformer.setOutputProperty(OutputKeys.INDENT, "true"); //$NON-NLS-1$
		xformer.transform(source, result);
	}

	List<ConsoleQueryParameter> queryParameters = new ArrayList<ConsoleQueryParameter>();

	public ConsoleQueryParameter[] getQueryParameters() {
		return queryParameters.toArray(new ConsoleQueryParameter[queryParameters.size()]);
	}

	public List<ConsoleQueryParameter> getQueryParameterList() {
		return queryParameters;
	}


}
