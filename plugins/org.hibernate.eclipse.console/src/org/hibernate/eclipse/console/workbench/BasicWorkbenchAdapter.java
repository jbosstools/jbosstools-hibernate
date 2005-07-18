package org.hibernate.eclipse.console.workbench;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.eclipse.ui.progress.IElementCollector;

public abstract class BasicWorkbenchAdapter implements IDeferredWorkbenchAdapter {

	protected Object[] toArray(Iterator iterator, Class clazz) {
		List obj = toList( iterator );
		return obj.toArray((Object[]) Array.newInstance(clazz, obj.size()));		
	}

	private List toList(Iterator iterator) {
		List obj = new ArrayList();
		while ( iterator.hasNext() ) {
			obj.add(iterator.next());
		}
		return obj;
	}
	
	protected Object[] toArray(Enumeration enumeration, Class clazz) {
		List obj = new ArrayList();
		while ( enumeration.hasMoreElements() ) {
			obj.add(enumeration.nextElement());
		}
		return obj.toArray((Object[]) Array.newInstance(clazz, obj.size()));
	}


	public void fetchDeferredChildren(Object object,
			IElementCollector collector, IProgressMonitor monitor) {
		collector.add(getChildren(object), monitor);
	}

	public boolean isContainer() {
		return true;
	}

	public ISchedulingRule getRule(Object object) {
		return null;
	}

}
