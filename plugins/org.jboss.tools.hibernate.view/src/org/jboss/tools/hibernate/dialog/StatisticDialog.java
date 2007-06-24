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
package org.jboss.tools.hibernate.dialog;

import java.util.ResourceBundle;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;
import org.jboss.tools.hibernate.core.exception.NestableRuntimeException;
import org.jboss.tools.hibernate.view.ViewPlugin;



/**
 * @author kaa
 * akuzmin@exadel.com
 * Jul 27, 2005
 */
public class StatisticDialog extends Dialog{
	
	public static final String BUNDLE_NAME = "messages"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(StatisticDialog.class.getPackage().getName() + "." + BUNDLE_NAME); 

	private String[] info;
	private String[] labelinfo;
	private String title;
	private String[] messagetype;
	public StatisticDialog(Shell parent,String[] information,String[] labelinfo,String[] status,String title) {
		super(parent);
		info=information;
		this.setTitle(title);		
		this.labelinfo=labelinfo;
		messagetype=status;
		if ((info.length!=labelinfo.length)||(info.length!=messagetype.length))
		{
			RuntimeException myException = new NestableRuntimeException("Each information must have own labelinfo and status ");
			ExceptionHandler.displayMessageDialog(myException, ViewPlugin.getActiveWorkbenchShell(), BUNDLE.getString("StatisticDialog.ExceptionTitle"), null);
			throw myException;
			
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#getDialogArea()
	 */
	protected Control createDialogArea(Composite parent){
	    Composite root = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
		root.setLayout(layout);
	    
//		int maxwidth=getWith()*10;
//		if (maxwidth<200) maxwidth=200;
//		root.setBounds(0,0,maxwidth+40,40+info.length*15);
		Label label3 = new Label(root, SWT.NULL);
		label3.setText(title+"                           ");
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		label3.setLayoutData(data); 
		label3.setVisible(false);
	    for(int i=0;i<info.length;i++)
	    {
			CLabel label1 = new CLabel(root, SWT.NULL);
			data = new GridData(GridData.FILL_HORIZONTAL);
			
			if (messagetype[i].equals("INFO"))
				label1.setImage(JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_INFO));
			else
			if (messagetype[i].equals("ERROR"))
				label1.setImage(JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_ERROR));
			else
				label1.setImage(JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_WARNING));
 			label1.setText("      "+labelinfo[i]+":     "+info[i]+"                     ");
// 			label1.setBounds(20,20*(i+1),maxwidth,15);
 			label1.setLayoutData(data); 			
	    }
		Label label2 = new Label(root, SWT.NULL);
		label2.setText("");
		data = new GridData(GridData.FILL_HORIZONTAL);
		label2.setLayoutData(data); 
//		label2.setBounds(20,20*(info.length+1),maxwidth,15);
	    
	    return root;
	}

//    private int getWith() {
//    	int max=0;
//    	for(int i=0;i<info.length;i++)
//	    {
//    		String mess=labelinfo[i]+":   "+info[i];
//    		if (mess.length()>max) max=mess.length();
//	    }
//		return max;
//	}
	public void setTitle(String title) {
		this.title = title;
	}
    
    protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (title != null)
			shell.setText(title);
	}
    
	protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
                true);
	}
	

}
