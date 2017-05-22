package org.jboss.tools.hibernate.search.property.testers;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.preferences.PreferencesClassPathUtils;

public class HibernateSearchEnabledPropertyTester extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (!(receiver instanceof ITreeSelection)) {
			return false;
		}
		ITreeSelection selection = (ITreeSelection)receiver;
		Set<ConsoleConfiguration> consoleConfigs = new HashSet<ConsoleConfiguration>();
		for (TreePath path : selection.getPaths()) {
			if (path.getFirstSegment() instanceof ConsoleConfiguration) {
				consoleConfigs.add((ConsoleConfiguration)path.getFirstSegment());
			} else {
				return false;
			}
		}
		for (ConsoleConfiguration consoleConfig: consoleConfigs) {
			boolean isHibernateSearch = false;
			URL[] classPathURLs = PreferencesClassPathUtils.getCustomClassPathURLs(consoleConfig.getPreferences());
			for (URL url: classPathURLs) {
				if (url.getFile().contains("hibernate-search")) {
					isHibernateSearch = true;
					break;
				}
			}
			if (!isHibernateSearch) {
				return false;
			}
		}
		
		return true;
	}

}
