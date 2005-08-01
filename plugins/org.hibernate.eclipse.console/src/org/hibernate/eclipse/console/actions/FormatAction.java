package org.hibernate.eclipse.console.actions;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.pretty.Formatter;

/**
 * Format the output in a text viewer.
 * 
 */
public class FormatAction extends Action {

	private ITextViewer fViewer;
		
	/**
	 * Constructs an action to clear the document associated with a text viewer.
	 * 
	 * @param viewer viewer whose document this action is associated with 
	 */
	public FormatAction(ITextViewer viewer) {
	    fViewer = viewer;
	    
		//setToolTipText(ConsoleMessages.ClearOutputAction_toolTipText); //$NON-NLS-1$
		setImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.FORMAT_QL) );
		setToolTipText("Format HQL/SQL");
		//setHoverImageDescriptor(ConsolePluginImages.getImageDescriptor(IConsoleConstants.IMG_LCL_CLEAR));		
		//setDisabledImageDescriptor(ConsolePluginImages.getImageDescriptor(IInternalConsoleConstants.IMG_DLCL_CLEAR));
		//setImageDescriptor(ConsolePluginImages.getImageDescriptor(IInternalConsoleConstants.IMG_ELCL_CLEAR));
		//PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IConsoleHelpContextIds.CLEAR_CONSOLE_ACTION);	    
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		BusyIndicator.showWhile(getStandardDisplay(), new Runnable() {
			public void run() {
				IDocument document = fViewer.getDocument();
				String contents = document.get();
				
				if (document != null) {
					document.set(new Formatter(contents).setInitialString("").setIndentString(" ").format()); //$NON-NLS-1$
				}
				fViewer.setSelectedRange(0, 0);
			}
		});
	}
	
	/**
	 * Returns the standard display to be used. The method first checks, if
	 * the thread calling this method has an associated display. If so, this
	 * display is returned. Otherwise the method returns the default display.
	 */
	private static Display getStandardDisplay() {
		Display display= Display.getCurrent();
		if (display == null) {
			display= Display.getDefault();
		}
		return display;		
	}
}