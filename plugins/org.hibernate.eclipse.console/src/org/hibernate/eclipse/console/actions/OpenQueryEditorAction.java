package org.hibernate.eclipse.console.actions;

import java.io.FileNotFoundException;
import java.util.Iterator;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.hibernate.HibernateException;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.HibernateConsolePlugin;

public abstract class OpenQueryEditorAction extends SelectionListenerAction {

	protected OpenQueryEditorAction(String text) {
		super( text );
	}

	public void runWithEvent(Event event) {
		doRun();
	}

	protected void doRun() {
		boolean showed = false;
		IStructuredSelection sel = getStructuredSelection();
		if (sel instanceof TreeSelection){
			TreePath[] paths = ((TreeSelection)sel).getPaths();
			for (int i = 0; i < paths.length; i++) {
				TreePath path = paths[i];
				ConsoleConfiguration config = (ConsoleConfiguration) path.getSegment(0);
				try {
				  openQueryEditor( config, "" );
				} catch(HibernateException he) {
					HibernateConsolePlugin.getDefault().showError(null, "Exception while trying to open HQL editor", he);
				}
				showed = true;
			}			
		}
		
		if(!showed) {
			openQueryEditor( null, "" );			
		}					
	}

	protected abstract void openQueryEditor(final ConsoleConfiguration config, String query);
}
