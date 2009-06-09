package org.hibernate.eclipse.console.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.hibernate.HibernateException;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;

public abstract class OpenQueryEditorAction extends SelectionListenerAction {

	protected OpenQueryEditorAction(String text) {
		super( text );
	}

	public void runWithEvent(Event event) {
		boolean showed = false;
		IStructuredSelection sel = getStructuredSelection();
		if (sel instanceof TreeSelection){
			TreePath[] paths = ((TreeSelection)sel).getPaths();
			showed = doRun(paths);
		}
		if(!showed) {
			openQueryEditor( null, "" );			 //$NON-NLS-1$
		}
	}

	protected boolean doRun(TreePath[] paths) {
		boolean showed = false;
		for (int i = 0; i < paths.length; i++) {
			TreePath path = paths[i];
			ConsoleConfiguration config = (ConsoleConfiguration) path.getSegment(0);
			try {
			  openQueryEditor( config, generateQuery(path) );
			  showed = true;
			} catch(HibernateException he) {
				HibernateConsolePlugin.getDefault().showError(null, HibernateConsoleMessages.OpenQueryEditorAction_exception_open_hql_editor, he);
			}
		}
		return showed;
	}

	protected abstract void openQueryEditor(final ConsoleConfiguration config, String query);

	/**
	 * Generates default query for selected element.
	 * @param selection
	 * @return
	 */
	protected abstract String generateQuery(TreePath path);
}
