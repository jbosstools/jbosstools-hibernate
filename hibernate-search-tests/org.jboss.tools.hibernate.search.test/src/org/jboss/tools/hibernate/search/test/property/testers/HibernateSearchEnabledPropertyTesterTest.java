package org.jboss.tools.hibernate.search.test.property.testers;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.jboss.tools.hibernate.search.property.testers.HibernateSearchEnabledPropertyTester;
import org.junit.Test;

public class HibernateSearchEnabledPropertyTesterTest {

	@Test
	public void containsHibernateSearchLib() throws MalformedURLException {
		ConsoleConfiguration consoleConfiguration = mock(ConsoleConfiguration.class);
		ConsoleConfigurationPreferences prefs = mock(ConsoleConfigurationPreferences.class);
		
		when(consoleConfiguration.getPreferences()).thenReturn(prefs);
		when(prefs.getCustomClassPathURLS()).thenReturn(new URL[] {new URL("file", "", "hibernate-search-orm-version")});

		TreePath treePath = new TreePath(new Object[] { consoleConfiguration });
		ITreeSelection receiver = new TreeSelection(treePath);
		
		HibernateSearchEnabledPropertyTester tester = new HibernateSearchEnabledPropertyTester();
		assertTrue(tester.test(receiver, "doesn't matter", null, null));
	}
	
	@Test
	public void allMustContainHibernateSearchLib() throws MalformedURLException {
		ConsoleConfiguration consoleConfiguration1 = mock(ConsoleConfiguration.class);
		ConsoleConfiguration consoleConfiguration2 = mock(ConsoleConfiguration.class);
		ConsoleConfigurationPreferences prefs1 = mock(ConsoleConfigurationPreferences.class);
		ConsoleConfigurationPreferences prefs2 = mock(ConsoleConfigurationPreferences.class);
		
		when(consoleConfiguration1.getPreferences()).thenReturn(prefs1);
		when(consoleConfiguration2.getPreferences()).thenReturn(prefs2);
		when(prefs1.getCustomClassPathURLS()).thenReturn(new URL[] {new URL("file", "", "hibernate-search-orm-version")});
		when(prefs1.getCustomClassPathURLS()).thenReturn(new URL[] {
				new URL("file", "", "something"), new URL("file", "", "dont-we-need")});

		TreePath treePath = new TreePath(new Object[] { consoleConfiguration1, consoleConfiguration2 });
		ITreeSelection receiver = new TreeSelection(treePath);
		
		HibernateSearchEnabledPropertyTester tester = new HibernateSearchEnabledPropertyTester();
		assertFalse(tester.test(receiver, "doesn't matter", null, null));
	}

}
