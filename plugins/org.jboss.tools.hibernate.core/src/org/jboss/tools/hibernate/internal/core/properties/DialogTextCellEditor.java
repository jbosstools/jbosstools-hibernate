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
package org.jboss.tools.hibernate.internal.core.properties;

import java.util.Arrays;
import java.util.ResourceBundle;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

/**
 * @author kaa
 * akuzmin@exadel.com
 */
public class DialogTextCellEditor extends DialogCellEditor {
	public static final String BUNDLE_NAME = "properties"; 
	public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(DialogTextCellEditor.class.getPackage().getName() + "." + BUNDLE_NAME);
	public static final String SQL_SEPARATORS = " \n\r\f\t,()=<>&|+-=/*'^![]#~\\";	
	public static final String SQL_WORDS[] = {//SQL-92
		"ABSOLUTE",
		"ACTION",
		"ADD",
		"ALL",
		"ALLOCATE",
		"ALTER",
		"AND",
		"ANY",
		"ARE",
		"AS",
		"ASC",
		"ASSERTION",
		"AT",
		"AUTHORIZATION",
		"AVG",
		"BEGIN",
		"BETWEEN",
		"BIT",
		"BIT_LENGTH",
		"BOTH",
		"BY",
		"CALL",
		"CASCADE",
		"CASCADED",
		"CASE",
		"CAST",
		"CATALOG",
		"CHAR",
		"CHAR_LENGTH",
		"CHARACTER",
		"CHARACTER_LENGTH",
		"CHECK",
		"CLOSE",
		"COALESCE",
		"COLLATE",
		"COLLATION",
		"COLUMN",
		"COMMIT",
		"CONDITION",
		"CONNECT",
		"CONNECTION",
		"CONSTRAINT",
		"CONSTRAINTS",
		"CONTAINS",
		"CONTINUE",
		"CONVERT",
		"CORRESPONDING",
		"COUNT",
		"CREATE",
		"CROSS",
		"CURRENT",
		"CURRENT_DATE",
		"CURRENT_PATH",
		"CURRENT_TIME",
		"CURRENT_TIMESTAMP",
		"CURRENT_USER",
		"CURSOR",
		"DATE",
		"DAY",
		"DEALLOCATE",
		"DEC",
		"DECIMAL",
		"DECLARE",
		"DEFAULT",
		"DEFERRABLE",
		"DEFERRED",
		"DELETE",
		"DESC",
		"DESCRIBE",
		"DESCRIPTOR",
		"DETERMINISTIC",
		"DIAGNOSTICS",
		"DISCONNECT",
		"DISTINCT",
		"DO",
		"DOMAIN",
		"DOUBLE",
		"DROP",
		"ELSE",
		"ELSEIF",
		"END",
		"ESCAPE",
		"EXCEPT",
		"EXCEPTION",
		"EXEC",
		"EXECUTE",
		"EXISTS",
		"EXIT",
		"EXTERNAL",
		"EXTRACT",
		"FALSE",
		"FETCH",
		"FIRST",
		"FLOAT",
		"FOR",
		"FOREIGN",
		"FOUND",
		"FROM",
		"FULL",
		"FUNCTION",
		"GET",
		"GLOBAL",
		"GO",
		"GOTO",
		"GRANT",
		"GROUP",
		"HANDLER",
		"HAVING",
		"HOUR",
		"IDENTITY",
		"IF",
		"IMMEDIATE",
		"IN",
		"INDICATOR",
		"INITIALLY",
		"INNER",
		"INOUT",
		"INPUT",
		"INSENSITIVE",
		"INSERT",
		"INT",
		"INTEGER",
		"INTERSECT",
		"INTERVAL",
		"INTO",
		"IS",
		"ISOLATION",
		"JOIN",
		"KEY",
		"LANGUAGE",
		"LAST",
		"LEADING",
		"LEAVE",
		"LEFT",
		"LEVEL",
		"LIKE",
		"LOCAL",
		"LOOP",
		"LOWER",
		"MATCH",
		"MAX",
		"MIN",
		"MINUTE",
		"MODULE",
		"MONTH",
		"NAMES",
		"NATIONAL",
		"NATURAL",
		"NCHAR",
		"NEXT",
		"NO",
		"NOT",
		"NULL",
		"NULLIF",
		"NUMERIC",
		"OCTET_LENGTH",
		"OF",
		"ON",
		"ONLY",
		"OPEN",
		"OPTION",
		"OR",
		"ORDER",
		"OUT",
		"OUTER",
		"OUTPUT",
		"OVERLAPS",
		"PAD",
		"PARAMETER",
		"PARTIAL",
		"PATH",
		"POSITION",
		"PRECISION",
		"PREPARE",
		"PRESERVE",
		"PRIMARY",
		"PRIOR",
		"PRIVILEGES",
		"PROCEDURE",
		"PUBLIC",
		"READ",
		"REAL",
		"REFERENCES",
		"RELATIVE",
		"REPEAT",
		"RESIGNAL",
		"RESTRICT",
		"RETURN",
		"RETURNS",
		"REVOKE",
		"RIGHT",
		"ROLLBACK",
		"ROUTINE",
		"ROWS",
		"SCHEMA",
		"SCROLL",
		"SECOND",
		"SECTION",
		"SELECT",
		"SESSION",
		"SESSION_USER",
		"SET",
		"SIGNAL",
		"SIZE",
		"SMALLINT",
		"SOME",
		"SPACE",
		"SPECIFIC",
		"SQL",
		"SQLCODE",
		"SQLERROR",
		"SQLEXCEPTION",
		"SQLSTATE",
		"SQLWARNING",
		"SUBSTRING",
		"SUM",
		"SYSTEM_USER",
		"TABLE",
		"TEMPORARY",
		"THEN",
		"TIME",
		"TIMESTAMP",
		"TIMEZONE_HOUR",
		"TIMEZONE_MINUTE",
		"TO",
		"TRAILING",
		"TRANSACTION",
		"TRANSLATE",
		"TRANSLATION",
		"TRIM",
		"TRUE",
		"UNDO",
		"UNION",
		"UNIQUE",
		"UNKNOWN",
		"UNTIL",
		"UPDATE",
		"UPPER",
		"USAGE",
		"USER",
		"USING",
		"VALUE",
		"VALUES",
		"VARCHAR",
		"VARYING",
		"VIEW",
		"WHEN",
		"WHENEVER",
		"WHERE",
		"WHILE",
		"WITH",
		"WORK",
		"WRITE",
		"YEAR",
		"ZONE"};


	public DialogTextCellEditor(Composite parent) {
		super(parent);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.DialogCellEditor#openDialogBox(org.eclipse.swt.widgets.Control)
	 */
	protected Object openDialogBox(Control cellEditorWindow) {
        Dialog dialog = new Dialog(cellEditorWindow.getShell())
        {
			private Document document;
			private ColorManager colorManager;  	    
    	    protected void configureShell(Shell shell) {
    			super.configureShell(shell);
   				shell.setText(BUNDLE.getString("DialogTextCellEditor.Title"));
    		}

			/* (non-Javadoc)
			 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
			 */
			protected void okPressed() {
				doSetValue(document.get());
				super.okPressed();
			}

			/* (non-Javadoc)
			 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
			 */
			protected Control createDialogArea(Composite parent) {
			    Composite root = new Composite(parent, SWT.NULL);
			    root.setBounds(0,0,430,360);

	 			Label label = new Label(root, SWT.NULL);
	 			label.setText("");
				label.setBounds(10,0,420,5);
				colorManager = new ColorManager();			    
				SourceViewer queryEditor=new SourceViewer(root, null, SWT.BORDER|SWT.V_SCROLL|SWT.MULTI|SWT.WRAP);
				document = new Document();
				queryEditor.configure(new QLConfiguration(colorManager,SQL_WORDS,SQL_SEPARATORS));				
				document.set((String)getValue());
				queryEditor.setDocument(document);
				queryEditor.getControl().setBounds(10,10,410,285);

			    Menu popupMenu = new Menu(queryEditor.getControl());
			    MenuItem formatItem = new MenuItem(popupMenu, SWT.NONE);
			    formatItem.setText(BUNDLE.getString("DialogTextCellEditor.FormatItemText"));
			    formatItem.addSelectionListener(new SelectionAdapter(){
			    	public void widgetSelected(SelectionEvent e) {
			    		int i=0;
			    		char c, quotesChar;
			    		String str = document.get();
			    		String newStr = new String();
			    		String wordStr = new String();
			    		do {
			    			c = str.charAt(i++);
			    			if (SQL_SEPARATORS.indexOf(c)==-1) {
			    				wordStr="";
			    				do {
			    					wordStr+=c;
			    					c = str.charAt(i++);
			    				} while (c!=' ' && c!='\'' && i<str.length());
			    				newStr+=(Arrays.binarySearch(SQL_WORDS,wordStr.toUpperCase())>=0)?wordStr.toUpperCase():wordStr;
			    			}
			    			if (c=='"' || c=='\'') {
			    				quotesChar=c;
			    				do {
			    					newStr+=c;
			    					c = str.charAt(i++);
			    				} while (c!=quotesChar && i<str.length());
			    			}
			    			newStr+=c;
			    		} while (i<str.length());
			    		document.set(newStr);
			    	}
			    });
	 
			    queryEditor.getControl().setMenu(popupMenu);		
				
			
				return root;
			}
			
        };
        if (dialog.open()==Window.OK)
        	return getValue();
        else return null;
	}

}
