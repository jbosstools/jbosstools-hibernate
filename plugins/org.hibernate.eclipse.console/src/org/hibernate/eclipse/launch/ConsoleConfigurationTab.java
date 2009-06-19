package org.hibernate.eclipse.launch;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.util.StringHelper;

abstract public class ConsoleConfigurationTab extends AbstractLaunchConfigurationTab {

	protected class ChangeListener implements ModifyListener, SelectionListener {

		public void modifyText(ModifyEvent e) {
			updateLaunchConfigurationDialog();
		}

		public void widgetDefaultSelected(SelectionEvent e) {/*do nothing*/}

		public void widgetSelected(SelectionEvent e) {
			// can use e.getSource() to handle button selection
			updateLaunchConfigurationDialog();
		}
	}

	ChangeListener changeListener = new ChangeListener();

	protected Button createBrowseButton(Group group, SelectionListener selectionListener) {
		Button button = createPushButton(group, HibernateConsoleMessages.ConsoleConfigurationTab_browse, null);
		button.addSelectionListener(selectionListener);
		return button;
	}

	protected Button createSetupButton(Group group, SelectionListener selectionListener) {
		Button button = createPushButton(group, HibernateConsoleMessages.ConsoleConfigurationTab_setup, null);
		button.addSelectionListener(selectionListener);
		return button;
	}

	protected Button createNewFileButton(Group group, SelectionListener selectionListener) {
		Button button = createPushButton(group, HibernateConsoleMessages.ConsoleConfigurationTab_create_new, null);
		button.addSelectionListener(selectionListener);
		return button;
	}

	protected Group createGroup(Composite parent, String title) {
		return createGroup(parent, title, 2);
	}

	protected Group createGroup(Composite parent, String title, int columns) {
		Font font = parent.getFont();
		Group group= new Group(parent, SWT.NONE);
		group.setText(title);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(gd);
		GridLayout layout = new GridLayout();
		layout.numColumns = columns;
		group.setLayout(layout);
		group.setFont(font);
		return group;
	}

	protected ChangeListener getChangeListener() {
		return changeListener;
	}

	protected Text createBrowseEditor(Composite parent, Group group) {
		Text text = new Text(group, SWT.SINGLE | SWT.BORDER);
		Font font=parent.getFont();
		GridData gd;
		gd = new GridData(GridData.FILL_HORIZONTAL);
		text.setLayoutData(gd);
		text.setFont(font);
		text.addModifyListener(getChangeListener());
		return text;
	}

	protected String nonEmptyTrimOrNull(Text t) {
		return nonEmptyTrimOrNull( t.getText() );
	}

	String nonEmptyTrimOrNull(String str) {
		if(StringHelper.isEmpty( str )) {
			return null;
		} else {
			return str.trim();
		}
	}


}
