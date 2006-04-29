package org.hibernate.eclipse.console.wizards;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.xpl.SelectionHelper;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (hbm.xml).
 */

public class NewHibernateMappingFilePage extends WizardPage {
	private Label containerText;

	private Label fileText;

	private ISelection selection;

	private Text classToMap;

	private WizardNewFileCreationPage fileCreation;

	private boolean beenShown;

	/**
	 * Constructor for SampleNewWizardPage.
	 * @param page 
	 * 
	 * @param pageName
	 */
	public NewHibernateMappingFilePage(ISelection selection, WizardNewFileCreationPage page) {
		super("wizardPage");
		this.fileCreation = page;
		setTitle("Hibernate XML Mapping file");
		setDescription("This wizard creates a new Hibernate XML Mapping file");
		this.selection = selection;
	}

	public void setVisible(boolean visible) {       
        containerText.setText(fileCreation.getContainerFullPath().toPortableString() );
        fileText.setText(fileCreation.getFileName() );        
        super.setVisible(visible);
        if(visible) {
            classToMap.setFocus();
        }
        beenShown = true;        
        dialogChanged();               
    }

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;

		Label label = new Label(container, SWT.NULL);
		label.setText("&Container:");

		containerText = new Label(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		containerText.setLayoutData(gd);

		label = new Label(container, SWT.NULL);
		label.setText("");

		label = new Label(container, SWT.NULL);
		label.setText("&File name:");

		fileText = new Label(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		fileText.setLayoutData(gd);
		
		label = new Label(container, SWT.NULL);
		label.setText("");
		
		label = new Label(container, SWT.NULL);
		label.setText("Class to &map:");

		classToMap = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		classToMap.setLayoutData(gd);
		classToMap.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		//TODO (internal api!): ControlContentAssistHelper.createTextContentAssistant(classToMap, aCompletionProcessor);

		Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleClassToMapBrowse();
			}
		});
		
		initialize();
		dialogChanged();
		setControl(container);
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize() {
		IType initialJavaElement = SelectionHelper.getClassFromElement(SelectionHelper.getInitialJavaElement(selection));
		if(initialJavaElement!=null) {
			classToMap.setText(initialJavaElement.getFullyQualifiedName('.'));
		}					
	}

	/**
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */

	private void handleBrowse() {
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
				getShell(), ResourcesPlugin.getWorkspace().getRoot(), false,
				"Select new file container");
		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				containerText.setText(((Path) result[0]).toString());
			}
		}
	}
	
	private void handleClassToMapBrowse() {
		IType type = findClassToMap();
		if(type!=null) {
			classToMap.setText(type.getFullyQualifiedName('.'));
		}
	}

	IType findClassToMap() {
		IJavaProject root= getRootJavaProject();
		if (root == null) 
			return null;

		IJavaElement[] elements= new IJavaElement[] { root };
		IJavaSearchScope scope= SearchEngine.createJavaSearchScope(elements);
		
		try {		
			SelectionDialog dialog= JavaUI.createTypeDialog(getShell(), getWizard().getContainer(), scope, IJavaElementSearchConstants.CONSIDER_CLASSES, false, getClassToMapText());
			dialog.setTitle("Select class to map"); 
			dialog.setMessage("The class will be used when generating the hbm.xml file"); 
			if (dialog.open() == Window.OK) {
				Object[] resultArray= dialog.getResult();
				if (resultArray != null && resultArray.length > 0)
					return (IType) resultArray[0];
			}
		} catch (JavaModelException e) {
			HibernateConsolePlugin.getDefault().log(e);
		}
		return null;
	}
	
	private IJavaProject getRootJavaProject() {
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(containerText.getText());
		if(resource!=null) {
			if(resource.getProject()!=null) {
				IJavaProject project = JavaCore.create(resource.getProject());
				return project;
			}
		}
		return null;
	}

	String getClassToMapText() {
		return classToMap.getText();
	}

	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged() {
		IResource container = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(new Path(getContainerName()));
		String fileName = getFileName();

		if (getContainerName().length() == 0) {
			updateStatus("File container must be specified");
			return;
		}
		if (container == null
				|| (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
			updateStatus("File container must exist");
			return;
		}
		if (!container.isAccessible()) {
			updateStatus("Project must be writable");
			return;
		}
		if (fileName.length() == 0) {
			updateStatus("File name must be specified");
			return;
		}
		if (fileName.replace('\\', '/').indexOf('/', 1) > 0) {
			updateStatus("File name must be valid");
			return;
		}
		
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getContainerName() {
		return containerText.getText();
	}

	public String getFileName() {
		return fileText.getText();
	}
}