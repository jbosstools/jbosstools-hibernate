package org.jboss.tools.hibernate.search.property.testers;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;

public class OneParentConfigPropertyTester extends PropertyTester {

	public OneParentConfigPropertyTester() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (!(receiver instanceof ITreeSelection)) {
			return false;
		}
		ITreeSelection selection = (ITreeSelection)receiver;
		Set<Object> consoleConfigs = new HashSet<Object>();
		for (TreePath path : selection.getPaths()) {
			consoleConfigs.add(path.getFirstSegment());
		}
		return consoleConfigs.size() == 1;
	}

}
