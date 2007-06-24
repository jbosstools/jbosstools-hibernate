/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.wizard.persistentclasses;




import java.util.ResourceBundle;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;
import org.jboss.tools.hibernate.core.IOrmProject;



/**
 * @author kaa
 *
 * 
 * TODO Remove this page from wizard but do not delete the file. This page may be moved to a dialog.  
 */


public class PersistentClassesPage3 extends WizardPage {
	public static final String BUNDLE_NAME = "persistentclasses"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(PersistentClassesPage.class.getPackage().getName() + "." + BUNDLE_NAME);	
	
	private IOrmProject mod;
	private Text ReplText;
	private Text WithText;	
    private Button PersistentClassesButton;
    private Button PersistentClassesReplace;    
    private List list;
    private String SchemaTxt;
    private Composite container;
	public boolean isFinish=false;
	public class RenameDlg extends ListDialog{//New dialog to rename element of list
		private Text Newtext;
		private String str;//initilize text 
 	    public RenameDlg(Shell parent) {
 			super(parent);
 			this.setTitle(BUNDLE.getString("PersistentClassesPage3.DialogTitle"));
 		}
 	    public RenameDlg(Shell parent,String str) {
 			super(parent);
 			this.setTitle(BUNDLE.getString("PersistentClassesPage3.DialogTitle"));
 			this.str=str;
 		}
 	    
 	    protected Control createDialogArea(Composite parent) {
 	        Composite root = new Composite(parent, SWT.NULL);
 			GridLayout layout = new GridLayout();
 			root.setLayout(layout);
 			Newtext = new Text(root, SWT.BORDER | SWT.SINGLE);
 			GridData data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
 			data.widthHint = 150;
 			Newtext.setLayoutData(data);
 			Newtext.setText(str);
 	      return root;
 	    }
 	    
 	    protected void okPressed() {
        list.setItem(list.getSelectionIndex(),Newtext.getText());//rename element of list
        this.close();
 	}	 	
	}
	
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 6;
		layout.marginHeight=0;
		layout.horizontalSpacing=0;
		layout.verticalSpacing = 3;
		container.setLayout(layout);
		
		Label label = new Label(container, SWT.NULL);
		label.setText(BUNDLE.getString("PersistentClassesPage3.label"));
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 6;
		label.setLayoutData(data);
//add  list of tables

		list = new List(container,SWT.BORDER);
		list.setBackground(new Color(null,255,255,255));
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		data.horizontalSpan = 4;
		data.grabExcessVerticalSpace = true;
		data.grabExcessHorizontalSpace = true;
		list.setLayoutData(data);

		Label label1 = new Label(container, SWT.NULL);
		label1.setText("    ");
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		label1.setLayoutData(data);
		
//add rename button		
		PersistentClassesButton= new Button(container, SWT.PUSH);
		PersistentClassesButton.setText(BUNDLE.getString("PersistentClassesPage3.rename"));
		PersistentClassesButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Rename();
			}
		});
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
	    data.horizontalSpan = 1;
	    PersistentClassesButton.setLayoutData(data);

		Label label2 = new Label(container, SWT.NULL);
		label1.setText("    ");
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 6;
		label2.setLayoutData(data);

		
		Label label3 = new Label(container, SWT.NULL);		
		label3.setText(BUNDLE.getString("PersistentClassesPage3.label3"));
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		data.horizontalIndent = 5;		
		label3.setLayoutData(data);
//add replace text		
		ReplText = new Text(container, SWT.BORDER | SWT.SINGLE);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		data.horizontalIndent = 5;			
		ReplText.setLayoutData(data);		

		
		Label label4 = new Label(container, SWT.NULL);		
		label4.setText(BUNDLE.getString("PersistentClassesPage3.label4"));
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
	    data.horizontalSpan = 1;
	    data.horizontalIndent = 5;	    
	    label4.setLayoutData(data);		
		
//	  add with text		
		WithText = new Text(container, SWT.BORDER | SWT.SINGLE);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 1;
		data.horizontalIndent = 5;			
		WithText.setLayoutData(data);		

		Label label5 = new Label(container, SWT.NULL);
		label5.setText("    ");
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		label5.setLayoutData(data);
//add replace button		
		PersistentClassesReplace= new Button(container, SWT.PUSH);
		PersistentClassesReplace.setText(BUNDLE.getString("PersistentClassesPage3.replace"));
		PersistentClassesReplace.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ReplaceText();
			}
		});
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
	    data.horizontalSpan = 1;
	    PersistentClassesReplace.setLayoutData(data);
		
		
		setControl(container);		
	}
	
	public PersistentClassesPage3(IOrmProject mod) {
		super("wizardPage");
		setTitle(BUNDLE.getString("PersistentClassesPage.title"));
		setDescription(BUNDLE.getString("PersistentClassesPage3.description"));
		this.mod = mod;
	}
	 public void SetValue(String Schem)
	  {
	  	SchemaTxt=Schem;//set Schema value from previous page
		   }

	 public void AddElem(String Item)
	  {
        	list.add(SchemaTxt+"."+Item);//add new tables item
	  }

	 public void ClearElem()
	  {
       	list.removeAll();//remove all tables item
	  }
	 
	 
	 public void ReplaceText()
	  {//replace all text in tebles item
       for (int i=0; i<list.getItemCount(); i++) {
       	list.setItem(i,list.getItem(i).replaceAll(ReplText.getText(),WithText.getText()));
       	}
       ReplText.setText("");
       WithText.setText("");
	  }
	 
	 private void Rename()
	 {//reneme element of table list 
	 	if (list.getSelectionCount()==1)
	 	{
		Dialog dlg=new RenameDlg(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),list.getItem(list.getSelectionIndex()));
	 	dlg.open();
	 	}
	 }

}
