package org.hibernate.eclipse.console.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.part.ViewPart;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.actions.ExecuteHQLAction;
import org.hibernate.eclipse.console.editors.HQLEditor;
import org.hibernate.eclipse.console.utils.EclipseImages;

public class HQLEditorView extends ViewPart {
	
	private class ClearAction extends Action {
		
		public ClearAction() {
			setImageDescriptor(EclipseImages.getImageDescriptor(ImageConstants.CLEAR) );
			setToolTipText("Clear editor");
			//setText("Clear");
		}

		public void run() {
			setQuery("");
		}
	}
	
	
	private ExecuteHQLAction executeAction;
	private StyledText viewer;
	
	public HQLEditorView() {
		super();
	}

	public void createPartControl(Composite parent) {
		
		
	   	GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		parent.setLayout(layout);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH) );
		
		viewer = new StyledText(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setEditable(true);
		viewer.setLayoutData(new GridData(GridData.FILL_BOTH) );

		initActions();
	}

	
	private void initActions() {

		IToolBarManager toolBar = getViewSite().getActionBars().getToolBarManager();

		executeAction = new ExecuteHQLAction(this);
		toolBar.add(this.executeAction);
		toolBar.add(new ClearAction() );
		
        IActionBars actionBars = getViewSite().getActionBars();
		
        }

	/**
	 * Returns the query to be executed. The query is either 1) the 
	 * text currently highlighted/selected in the editor or 2) all of 
     * the text in the editor. 
	 * @return query string to be executed
	 */
	public String getQuery() {
	    String query; 
	    
	    if (viewer.getSelectionText().length() > 0)
	        query = viewer.getSelectionText();
	    else    
	        query = viewer.getText();
	    
		return query;
	}
	
	public void setQuery(String text) {
		viewer.setText(text);
	}
	
	
	public void setFocus() {
	}
	
}