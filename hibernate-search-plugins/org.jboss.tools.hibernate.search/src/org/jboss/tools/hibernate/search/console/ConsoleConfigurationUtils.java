package org.jboss.tools.hibernate.search.console;

import java.lang.reflect.Field;
import java.util.Set;

import org.eclipse.datatools.connectivity.IConnection;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.ui.PingJob;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;
import org.hibernate.console.ConsoleConfigClassLoader;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.jboss.tools.hibernate.search.HSearchConsoleConfigurationPreferences;
import org.jboss.tools.hibernate.search.HibernateSearchConsolePlugin;
import org.jboss.tools.hibernate.search.runtime.spi.HSearchServiceLookup;
import org.jboss.tools.hibernate.search.runtime.spi.IHSearchService;

public class ConsoleConfigurationUtils {
	
	public static ClassLoader getClassLoader(ConsoleConfiguration cc) {
		try {
			Field loaderField = cc.getClass().getDeclaredField("classLoader");
			loaderField.setAccessible(true);
			return (ConsoleConfigClassLoader)loaderField.get(cc);
			
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e1) {
			return null;
		}
	}
	
	public static boolean loadSessionFactorySafely(ConsoleConfiguration cc) {
		try {
			if (cc.getSessionFactory() == null && isConnectionExist(cc)) {
				if (!cc.hasConfiguration() && askUserForConfiguration(cc.getName())) {
					cc.build();
				}
				cc.buildSessionFactory();
			}
			if (!"true".equals(cc.getConfiguration().getProperty("hibernate.search.autoregister_listeners"))) {
				
				String out = NLS.bind("Hiberante search wasn't enabled by default for some reason "
						+ "(see \"hibernate.search.autoregister_listeners\" property). Some options may not work. "
						+ "Would you like to enable it and rebuild the configuration and session factory?", cc.getName());
				boolean enable = MessageDialog.openQuestion(
						HibernateSearchConsolePlugin.getActiveWorkbenchWindow().getShell(), 
						"Enable hibernate search", 
						out);
				
				if (enable) {
					cc.reset();
					cc.build();
					cc.getConfiguration().setProperty("hibernate.search.autoregister_listeners", "true");
					cc.buildSessionFactory();;
				}
			}
		} catch (Exception e) {
			MessageDialog.openError(HibernateSearchConsolePlugin.getDefault()
					.getWorkbench().getActiveWorkbenchWindow().getShell(), 
					"Loading session failed", 
					e.getMessage() + (e.getCause() == null ? "" : "\n" + e.getCause().getMessage()));
			return false;
		}
		return true;
	}
	
	private static boolean askUserForConfiguration(String name) {
		String out = NLS.bind(HibernateConsoleMessages.AbstractQueryEditor_do_you_want_open_session_factory, name);
		return MessageDialog.openQuestion(HibernateSearchConsolePlugin.getActiveWorkbenchWindow().getShell(),
				HibernateConsoleMessages.AbstractQueryEditor_open_session_factory, out );
	}
	
	public static IHSearchService getHSearchService(ConsoleConfiguration consoleConfig) {
		return HSearchServiceLookup.findService(HSearchConsoleConfigurationPreferences.getHSearchVersion(consoleConfig.getName()));
	}
	
	public static Set<Class<?>> getIndexedEntities(ConsoleConfiguration consoleConfig) {
		IHSearchService service = getHSearchService(consoleConfig);
		return service.getIndexedTypes(consoleConfig.getSessionFactory());
	}
	
	public static boolean isConnectionExist(ConsoleConfiguration consoleConfig) {
		String connProfileName = consoleConfig.getPreferences().getConnectionProfileName();
		IConnectionProfile profile = ProfileManager.getInstance().getProfileByName(connProfileName);
		IConnection conn = PingJob.createTestConnection(profile);
		if (conn.getConnectException() != null) {
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					"Establishing connection to database error",
					conn.getConnectException().getLocalizedMessage());
			return false;
		}
		return true;
	}

}
