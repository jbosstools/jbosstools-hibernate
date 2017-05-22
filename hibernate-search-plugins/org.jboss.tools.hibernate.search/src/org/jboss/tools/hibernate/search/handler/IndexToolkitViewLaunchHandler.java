package org.jboss.tools.hibernate.search.handler;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.hibernate.console.ConsoleConfiguration;
import org.jboss.tools.hibernate.search.HibernateSearchConsolePlugin;
import org.jboss.tools.hibernate.search.console.ConsoleConfigurationUtils;

public class IndexToolkitViewLaunchHandler implements IHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
		if (sel.isEmpty()) {
			return null;
		}
		ITreeSelection selection = (ITreeSelection) sel;
		if (selection.getFirstElement() instanceof ConsoleConfiguration) {
			ConsoleConfiguration consoleConfig = (ConsoleConfiguration)selection.getFirstElement();
			if (ConsoleConfigurationUtils.isConnectionExist(consoleConfig)) {
				HibernateSearchConsolePlugin.getDefault().showIndexToolkitView(consoleConfig);
			}
		}
		return null;
	}
	
	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean isHandled() {
		return true;
	}

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		//do nothing
	}

	@Override
	public void dispose() {
		//do nothing
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		//do nothing
	}

}
