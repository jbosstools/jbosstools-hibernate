package org.hibernate.eclipse.launch;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.internal.ui.util.SWTUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.utils.DialogSelectionHelper;

public class DirectoryBrowseField extends StringDialogField {

	protected Button filesystemBrowse, workspaceBrowse;
	protected String filesystemBrowseLabel, workspaceBrowseLabel;
	protected IPath initialFilesystemPath, initialWorkspacePath;
	protected String dialogTitle, dialogDescription;

	public DirectoryBrowseField (IPath initialFilesystemPath, IPath initialWorkspacePath, String dialogTitle, String dialogDescription)
	{
		super();

		filesystemBrowseLabel =  HibernateConsoleMessages.DirectoryBrowseField_filesystem;
		workspaceBrowseLabel = HibernateConsoleMessages.DirectoryBrowseField_workspace;

		this.initialFilesystemPath = initialFilesystemPath;
		this.initialWorkspacePath = initialWorkspacePath;
		this.dialogTitle = dialogTitle;
		this.dialogDescription = dialogDescription;
	}

	protected void updateEnableState() {
		super.updateEnableState();
		boolean enabled = isEnabled();

		if (filesystemBrowse != null)
			filesystemBrowse.setEnabled(enabled);
		if (workspaceBrowse != null)
			workspaceBrowse.setEnabled(enabled);
	}

	protected static GridData gridDataForButton(Button button, int span) {
		GridData gd= new GridData();
		gd.horizontalAlignment= GridData.FILL;
		gd.grabExcessHorizontalSpace= false;
		gd.horizontalSpan= span;
		gd.widthHint = SWTUtil.getButtonWidthHint(button);
		return gd;
	}

	public int getNumberOfControls() {
		return 4;
	}

	public Control[] doFillIntoGrid(Composite parent, int columns) {
		assertEnoughColumns(columns);

		Label label = getLabelControl(parent);
		label.setLayoutData(gridDataForLabel(1));

		Text text = getTextControl(parent);
		text.setLayoutData(gridDataForText(columns - 3));


		Button filesystemButton = getFilesystemBrowseButton(parent);
		filesystemButton.setLayoutData(gridDataForButton(filesystemButton, 1));
		Button workspaceButton = getWorkspaceBrowseButton(parent);
		filesystemButton.setLayoutData(gridDataForButton(workspaceButton, 1));

		return new Control[] { label, text, filesystemButton, workspaceButton };
	}

	protected void browseFilesystem ()
	{
		DirectoryDialog dialog = new DirectoryDialog(filesystemBrowse.getShell());
		dialog.setText(dialogTitle);
		dialog.setMessage(dialogDescription);
		if (initialFilesystemPath != null) {
			dialog.setFilterPath(initialFilesystemPath.toOSString());
		}

		String dir = dialog.open();
		if (dir != null)
		{
			setText(dir);
		}
	}

	protected void browseWorkspace ()
	{
		IPath[] paths = DialogSelectionHelper.chooseFolderEntries(filesystemBrowse.getShell(),  initialWorkspacePath, dialogTitle, dialogDescription, false);
        if(paths!=null && paths.length==1) {
        	setText(paths[0].toOSString());
        }
	}

	public Button getFilesystemBrowseButton (Composite parent)
	{
		if (filesystemBrowse == null)
		{
			filesystemBrowse = new Button(parent, SWT.PUSH);
			filesystemBrowse.setFont(parent.getFont());
			filesystemBrowse.setText(filesystemBrowseLabel);
			filesystemBrowse.setEnabled(isEnabled());
			filesystemBrowse.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
				public void widgetSelected(SelectionEvent e) {
					browseFilesystem();
				}
			});
		}

		return filesystemBrowse;
	}

	public Button getWorkspaceBrowseButton (Composite parent)
	{
		if (workspaceBrowse == null)
		{
			workspaceBrowse = new Button(parent, SWT.PUSH);
			workspaceBrowse.setFont(parent.getFont());
			workspaceBrowse.setText(workspaceBrowseLabel);
			workspaceBrowse.setEnabled(isEnabled());
			workspaceBrowse.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
				public void widgetSelected(SelectionEvent e) {
					browseWorkspace();
				}
			});
		}
		return workspaceBrowse;
	}

	public void setFilesystemBrowseLabel(String filesystemBrowseLabel) {
		this.filesystemBrowseLabel = filesystemBrowseLabel;
	}

	public void setWorkspaceBrowseLabel(String workspaceBrowseLabel) {
		this.workspaceBrowseLabel = workspaceBrowseLabel;
	}
}
