package org.hibernate.eclipse.console.wizards;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.ComboDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.FileSelectionDialog;
import org.eclipse.ui.views.navigator.ResourceSelectionUtil;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.node.ConfigurationNode;
import org.hibernate.eclipse.console.utils.DialogSelectionHelper;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (mpe).
 */

public class BasicGeneratorSettingsPage extends WizardPage {
	private ComboDialogField consoleConfigurationName;

	private ISelection selection;

	private SelectionButtonDialogField reverseengineer;

	private SelectionButtonDialogField generatecfgfile;

	private SelectionButtonDialogField generatejava;

	private SelectionButtonDialogField generatemappings;

	private StringButtonDialogField outputdir;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public BasicGeneratorSettingsPage(ISelection selection) {
		super("wizardPage");
		setTitle("Basic settings for artifact generation");
		setDescription("This wizard allows you to generate artifacts (configuration, mapping & source code files)");
		this.selection = selection;
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
		
		consoleConfigurationName = new ComboDialogField(SWT.READ_ONLY);
		consoleConfigurationName.setLabelText("Console &configuration");
		ConsoleConfiguration[] cfg = KnownConfigurations.getInstance().getConfigurations();
		String[] names = new String[cfg.length];
		for (int i = 0; i < cfg.length; i++) {
			ConsoleConfiguration configuration = cfg[i];
			names[i] = configuration.getName();
		}
		consoleConfigurationName.setItems(names);

		consoleConfigurationName.setDialogFieldListener(new IDialogFieldListener() {
			public void dialogFieldChanged(DialogField field) {
				dialogChanged();
			}
		});
		
		outputdir = new StringButtonDialogField(new IStringButtonAdapter() {
			public void changeControlPressed(DialogField field) {
				IPath[] paths = DialogSelectionHelper.chooseFileEntries(getShell(),  getOutputDirectory(), new IPath[0], "Select output directory", "Choose directory in which the generated files will be stored", new String[] {"cfg.xml"}, false, true, false);
				if(paths!=null && paths.length==1) {
					outputdir.setText(((paths[0]).toOSString()));
				}					
			}
		});
		outputdir.setLabelText("Output &directory");
		outputdir.setButtonLabel("&Browse...");
		
		reverseengineer = new SelectionButtonDialogField(SWT.CHECK);
		reverseengineer.setLabelText("Reverse engineer from JDBC Connection");
		
		generatejava = new SelectionButtonDialogField(SWT.CHECK);
		generatejava.setLabelText("Generate domain code (.java)");
		
		generatemappings = new SelectionButtonDialogField(SWT.CHECK);
		generatemappings.setLabelText("Generate mappings (hbm.xml)");
		
		generatecfgfile = new SelectionButtonDialogField(SWT.CHECK);
		generatecfgfile.setLabelText("Generate hibernate configuration (hibernate.cfg.xml)");
		
		consoleConfigurationName.doFillIntoGrid(container, 3);
		Control[] controls = outputdir.doFillIntoGrid(container, 3);
		// Hack to tell the text field to stretch!
		((GridData)controls[1].getLayoutData()).grabExcessHorizontalSpace=true;
		reverseengineer.doFillIntoGrid(container, 3);
		generatejava.doFillIntoGrid(container, 3);
		generatemappings.doFillIntoGrid(container, 3);
		generatecfgfile.doFillIntoGrid(container, 3);

		initialize();
		dialogChanged();
		setControl(container);
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize() {
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1)
				return;
			Object obj = ssel.getFirstElement();
			if (obj instanceof ConfigurationNode) {
				consoleConfigurationName.setText(((ConfigurationNode)obj).getConsoleConfiguration().getName());
			}
		}
		
	}



	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged() {
		
		if (getConfigurationName().length() == 0) {
			updateStatus("Console configuration must be specified");
			return;
		}
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getConfigurationName() {
		return consoleConfigurationName.getText();
	}

	private Path pathOrNull(String p) {
		if(p==null || p.trim().length()==0) {
			return null;
		} else {
			return new Path(p);
		}
	}


	/**
	 * @return
	 */
	public boolean isReverseEngineerEnabled() {
		return reverseengineer.isSelected();
	}


	/**
	 * @return
	 */
	public boolean isGenerateJava() {
		return generatejava.isSelected();
	}


	/**
	 * @return
	 */
	public boolean isGenerateMappings() {
		return generatemappings.isSelected();
	}


	/**
	 * @return
	 */
	public boolean isGenerateCfg() {
		return generatecfgfile.isSelected();
	}


	/**
	 * @return
	 */
	public IPath getOutputDirectory() {
		return pathOrNull(outputdir.getText());
	}
}