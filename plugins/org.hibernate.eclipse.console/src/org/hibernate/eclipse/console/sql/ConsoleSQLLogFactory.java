package org.hibernate.eclipse.console.sql;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleFactory;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.utils.EclipseImages;

public class ConsoleSQLLogFactory implements IConsoleFactory {

	int counter = 0;
	
	public void openConsole() {
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (window != null) {
            IWorkbenchPage page = window.getActivePage();
            if (page != null) {
            	String secondaryId = "Hibernate SQL Log #" + counter;
            	MessageConsole console = new MessageConsole(secondaryId, EclipseImages.getImageDescriptor(ImageConstants.HQL_EDITOR));
            	IConsoleManager consoleManager = ConsolePlugin.getDefault().getConsoleManager();
            	consoleManager.addConsoles(new IConsole[] { console });
            	counter++;            	
            }
        }
	}

}
