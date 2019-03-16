package org.jboss.tools.hibernate.search.test.property.testers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.hibernate.console.ConsoleConfiguration;
import org.jboss.tools.hibernate.search.property.testers.OneParentConfigPropertyTester;
import org.junit.Test;

public class OneParentConfigPropertyTesterTest {

	@Test
	public void testSameConfig() {
		ConsoleConfiguration consoleConfiguration = mock(ConsoleConfiguration.class);
		
		TreePath treePath1 = new TreePath(new Object[] { consoleConfiguration });
		TreePath treePath2 = new TreePath(new Object[] { consoleConfiguration });
		ITreeSelection receiver = new TreeSelection(new TreePath[] { treePath1, treePath2 });
		
		OneParentConfigPropertyTester propertyTester = new OneParentConfigPropertyTester();
		assertTrue(propertyTester.test(receiver, "doesn't matter", null, null));
	}
	
	@Test
	public void testDifferentConfigs() {
		ConsoleConfiguration consoleConfiguration1 = mock(ConsoleConfiguration.class);
		ConsoleConfiguration consoleConfiguration2 = mock(ConsoleConfiguration.class);
		
		TreePath treePath1 = new TreePath(new Object[] { consoleConfiguration1 });
		TreePath treePath2 = new TreePath(new Object[] { consoleConfiguration2 });
		ITreeSelection receiver = new TreeSelection(new TreePath[] { treePath1, treePath2 });
		
		OneParentConfigPropertyTester propertyTester = new OneParentConfigPropertyTester();
		assertFalse(propertyTester.test(receiver, "doesn't matter", null, null));
	}
}
