package org.hibernate.eclipse.console.actions;

import java.util.Iterator;

import org.eclipse.swt.widgets.Event;
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
		for (Iterator i = getSelectedNonResources().iterator(); i.hasNext();) {
			try {
				Object node = i.next();
				if(node instanceof ConsoleConfiguration) {
					final ConsoleConfiguration config = (ConsoleConfiguration) node;
					openQueryEditor( config, "" );
					showed = true;
				} 
			} catch(HibernateException he) {
				HibernateConsolePlugin.getDefault().showError(null, "Exception while trying to open HQL editor", he);
			}
		}
		
		if(!showed) {
			openQueryEditor( null, "" );			
		}					
	}

	protected abstract void openQueryEditor(final ConsoleConfiguration config, String query);
}
