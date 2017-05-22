package org.jboss.tools.hibernate.search.handler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.execution.ExecutionContext.Command;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.search.HSearchConsoleConfigurationPreferences;
import org.jboss.tools.hibernate.search.console.ConsoleConfigurationUtils;
import org.jboss.tools.hibernate.search.runtime.spi.HSearchServiceLookup;
import org.jboss.tools.hibernate.search.runtime.spi.IHSearchService;

public class IndexRebuildHandler extends AbstractHandler {

	@SuppressWarnings("unchecked")
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
		if (sel.isEmpty()) {
			return null;
		}
		
		ITreeSelection selection = (ITreeSelection) sel;
		if (selection.getFirstElement() instanceof ConsoleConfiguration) {
			indexRebuildForConfiguration(selection.iterator());
		}
		if (selection.getFirstElement() instanceof IPersistentClass) {
			indexRebuildForPersistentClass((ConsoleConfiguration)selection.getPaths()[0].getFirstSegment(), selection.iterator());
		}

		return null;
	}

	protected void indexRebuildForConfiguration(Iterator<ConsoleConfiguration> consoleConfigs) {
		while (consoleConfigs.hasNext()) {
			final ConsoleConfiguration consoleConfig = consoleConfigs.next();
			if (ConsoleConfigurationUtils.loadSessionFactorySafely(consoleConfig)) {
				run(consoleConfig, Collections.EMPTY_SET);
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	protected void indexRebuildForPersistentClass(ConsoleConfiguration consoleConfig, Iterator<IPersistentClass> persistClasses) {
		if (!ConsoleConfigurationUtils.loadSessionFactorySafely(consoleConfig)) {
			return;
		};
		ClassLoader classloader = ConsoleConfigurationUtils.getClassLoader(consoleConfig);
		final Set<Class> classes = new HashSet<Class>();
		try {
			while (persistClasses.hasNext()) {
				IPersistentClass clazz = persistClasses.next();
				classes.add(Class.forName(clazz.getClassName(), true, classloader));
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		run(consoleConfig, classes);
	}
	
	private void run(final ConsoleConfiguration consoleConfig, final Set<Class> classes) {
		try {
			consoleConfig.execute(new Command() {
				public Object execute() {
					final IConfiguration cfg = consoleConfig.getConfiguration();
					if (cfg == null) {
						return null;
					}
					IHSearchService service = HSearchServiceLookup.findService(HSearchConsoleConfigurationPreferences.getHSearchVersion(consoleConfig.getName()));
					service.newIndexRebuild(consoleConfig.getSessionFactory(), classes);
					return null;
				}
			});
			MessageDialog.openInformation(HibernateConsolePlugin.getDefault()
					.getWorkbench().getActiveWorkbenchWindow().getShell(), 
					"Initial index rebuild", 
					"Initial index rebuild succesfully finished");
		} catch (Exception e) {
			MessageDialog.openError(HibernateConsolePlugin.getDefault()
					.getWorkbench().getActiveWorkbenchWindow().getShell(), 
					"Initial index rebuild failed", 
					e.getMessage());
		}
	}
}
