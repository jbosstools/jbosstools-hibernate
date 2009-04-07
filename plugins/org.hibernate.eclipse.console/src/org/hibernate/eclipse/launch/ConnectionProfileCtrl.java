/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.hibernate.eclipse.launch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.datatools.connectivity.ConnectionProfileException;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.IProfileListener;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.datatools.connectivity.internal.ui.wizards.NewCPWizard;
import org.eclipse.datatools.connectivity.internal.ui.wizards.NewCPWizardCategoryFilter;
import org.eclipse.datatools.connectivity.ui.wizards.NewConnectionProfileWizard;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.internal.dialogs.PropertyDialog;
import org.hibernate.annotations.common.util.StringHelper;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;

/**
 * 
 * 
 * @author Vitali Yemialyanchyk
 */
public class ConnectionProfileCtrl {

	protected ComboViewer comboControl;
	protected Button buttonNew;
	protected Button buttonEdit;
	
	static final String NO_CONNECTIN_NAME = HibernateConsoleMessages.ConnectionProfileCtrl_HibernateConfiguredConnection;
	static final String JPA_CONNECTIN_NAME = HibernateConsoleMessages.ConnectionProfileCtrl_JPAConfiguredConnection;

	static final protected ConnectionWrapper NO_CONNECTION_PLACEHOLDER = new ConnectionWrapper(NO_CONNECTIN_NAME, null);
	static final protected ConnectionWrapper JPA_CONNECTION_PLACEHOLDER = new ConnectionWrapper(JPA_CONNECTIN_NAME, null);

	private static class ConnectionWrapper {
		
		final private String id;
		final private IConnectionProfile profile;
		
		public ConnectionWrapper(String id, IConnectionProfile profile) {
			this.id = id;
			this.profile = profile;					
		}
		
		public String getId() {
			return id;
		}
		
		public IConnectionProfile getProfile() {
			return profile;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj == null || !(obj instanceof ConnectionWrapper)) {
				return false;
			}
			ConnectionWrapper cw = (ConnectionWrapper) obj;
			return this.getId().equals(cw.getId());
		}
		
		@Override
		public int hashCode() {
			return this.getId().hashCode();
		}
	}
	
	protected ArrayList<ModifyListener> modifyListeners = new ArrayList<ModifyListener>();

	public ConnectionProfileCtrl(Composite comp, int hspan, String defaultValue) {
		createComboWithTwoButtons(comp, hspan, defaultValue,
				new NewConnectionProfileAction(), new EditConnectionProfileAction());
	}

	public class ButtonPressedAction extends Action implements SelectionListener {

		public ButtonPressedAction(String label) {
			super(label);
		}

		public void widgetDefaultSelected(SelectionEvent e) {
		}

		public void widgetSelected(SelectionEvent e) {
			run();
		}
	}

	public class ConnectionProfileChangeListener implements IProfileListener {
		/* (non-Javadoc)
		 * @see org.eclipse.datatools.connectivity.IProfileListener#profileAdded(org.eclipse.datatools.connectivity.IConnectionProfile)
		 */
		public void profileAdded(final IConnectionProfile profile) {
			Display.getDefault().asyncExec(new Runnable() {
	               public void run() {
	            	   updateInput();
	            	   selectValue(profile.getName());
	            	   validate();
	               }
			});
		}

		/* (non-Javadoc)
		 * @see org.eclipse.datatools.connectivity.IProfileListener#profileChanged(org.eclipse.datatools.connectivity.IConnectionProfile)
		 */
		public void profileChanged(IConnectionProfile profile) {
			profileAdded(profile);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.datatools.connectivity.IProfileListener#profileDeleted(org.eclipse.datatools.connectivity.IConnectionProfile)
		 */
		public void profileDeleted(IConnectionProfile profile) {
			// this event never happens
		}
	}

	public class NewConnectionProfileAction extends ButtonPressedAction {
		/**
		 * @param label
		 */
		public NewConnectionProfileAction() {
			super(HibernateConsoleMessages.ConnectionProfileCtrl_New);
		}

		@Override
		public void run() {
			IProfileListener listener = new ConnectionProfileChangeListener();

			ProfileManager.getInstance().addProfileListener(listener);
			NewCPWizardCategoryFilter filter = new NewCPWizardCategoryFilter("org.eclipse.datatools.connectivity.db.category"); //$NON-NLS-1$
			NewCPWizard wizard = new NewCPWizard(filter, null);
			new NewConnectionProfileWizard() {
				public boolean performFinish() {
					// create profile only
					try {
						ProfileManager.getInstance().createProfile(
								getProfileName() == null ? "" //$NON-NLS-1$
										: getProfileName(),
								getProfileDescription() == null ? "" //$NON-NLS-1$
										: getProfileDescription(),
								mProviderID,
								getProfileProperties(),
								mProfilePage.getRepository() == null ? "" //$NON-NLS-1$
										: mProfilePage.getRepository()
												.getName(), false);
					} catch (ConnectionProfileException e) {
						HibernateConsolePlugin.getDefault().log(e);
					}
					return true;
				}

				@Override
				public void addCustomPages() {
				}

				@Override
				public Properties getProfileProperties() {
					return null;
				}
			};
			WizardDialog wizardDialog = new WizardDialog(Display.getCurrent()
					.getActiveShell(), wizard);
			wizardDialog.open();
			ProfileManager.getInstance().removeProfileListener(listener);
		}
	}

	/**
	 *
	 */
	public class EditConnectionProfileAction extends ButtonPressedAction {

		/**
		 * @param label
		 */
		public EditConnectionProfileAction() {
			super(HibernateConsoleMessages.ConnectionProfileCtrl_Edit);
		}

		/**
		 *
		 */
		@Override
		public void run() {
			ConnectionWrapper currentConnection = getSelectedConnection();
			if (null == currentConnection || currentConnection.getProfile()==null) {
				return;
			}
			IConnectionProfile selectedProfile = currentConnection.getProfile();
			PropertyDialog.createDialogOn(
				Display.getCurrent().getActiveShell(),
				"org.eclipse.datatools.connectivity.db.generic.profileProperties", //$NON-NLS-1$
				selectedProfile).open();

			if (!currentConnection.getId().equals(selectedProfile.getName())) {
				updateInput();
				selectValue(selectedProfile.getName());				
			}
			validate();
		}
	};

	public Composite createComboWithTwoButtons(Composite container, int hspan, 
			String defaultValue, ButtonPressedAction action1, ButtonPressedAction action2) {

		Composite comp = SWTFactory.createComposite(container, container.getFont(), 3, 1, GridData.FILL_BOTH, 0, 0);
		Combo combo;
		combo = new Combo(comp, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		comboControl = new ComboViewer(combo);
		comboControl.setContentProvider(new IStructuredContentProvider() {

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}

			public Object[] getElements(Object inputElement) {
				return getProfileNameList().toArray();
			}
		});
		
		comboControl.setLabelProvider(new LabelProvider() {
			
			@Override
			public String getText(Object element) {
				ConnectionWrapper cw = (ConnectionWrapper) element;
				return cw.getId();
			}
		});
		
		comboControl.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				buttonEdit.setEnabled(getSelectedConnection().getProfile()!=null);
				notifyModifyListeners();
			}

		});
		combo.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		
		buttonNew = new Button(comp, SWT.PUSH);
		buttonNew.setText(HibernateConsoleMessages.ConnectionProfileCtrl_New);
		buttonNew.addSelectionListener(action1);

		buttonEdit = new Button(comp, SWT.PUSH);
		buttonEdit.setText(HibernateConsoleMessages.ConnectionProfileCtrl_Edit);
		buttonEdit.addSelectionListener(action2);

		updateInput();		
		return comp;
	}

	public void updateInput() {
		comboControl.setInput(getProfileNameList());
	}

	//TODO: handle if the connection profile does not exist
	// Require that we make the combobox editable
	protected ConnectionWrapper findMatchingConnection(String name) {
		if(StringHelper.isEmpty(name)) {
			return NO_CONNECTION_PLACEHOLDER;
		}
		
		List<ConnectionWrapper> list = getProfileNameList();
		
		for (Iterator<ConnectionWrapper> iterator = list.iterator(); iterator.hasNext();) {
			ConnectionWrapper object = iterator.next();
			if(name.equals(object.getId())) {
				return object;
			}
		}		
		return NO_CONNECTION_PLACEHOLDER;
	}

	public void selectValue(String name) {
		ConnectionWrapper connection = findMatchingConnection(name);		
		comboControl.setSelection(new StructuredSelection(connection), true);
		comboControl.refresh();
		notifyModifyListeners();
	}

	/**
	 * 
	 * @return current selected connection wrapper, will always return non-null.
	 */
	private ConnectionWrapper getSelectedConnection() {
		StructuredSelection selection = (StructuredSelection)comboControl.getSelection();
		if (null == selection || selection.isEmpty()) {
			return NO_CONNECTION_PLACEHOLDER;
		}
		ConnectionWrapper cw = (ConnectionWrapper) selection.getFirstElement();
		return cw;		
	}
	
	private String getSelectedId() {
		ConnectionWrapper cw = getSelectedConnection();
		return cw.getId();		
	}

	protected void validate() {
	}

	private List<ConnectionWrapper> getProfileNameList() {
		IConnectionProfile[] profiles = ProfileManager.getInstance()
				.getProfilesByCategory("org.eclipse.datatools.connectivity.db.category"); //$NON-NLS-1$
		List<ConnectionWrapper> names = new ArrayList<ConnectionWrapper>();
		names.add(JPA_CONNECTION_PLACEHOLDER);
		names.add(NO_CONNECTION_PLACEHOLDER);
		for (IConnectionProfile connectionProfile : profiles) {
			names.add(new ConnectionWrapper(connectionProfile.getName(), connectionProfile));
		}
		return names;
	}

	public void setEnabled(boolean enabled) {
		comboControl.getControl().setEnabled(enabled);
		buttonNew.setEnabled(enabled);
		buttonEdit.setEnabled(enabled);
	}

	public void notifyModifyListeners() {
		for (int i = 0; i < modifyListeners.size(); i++) {
			modifyListeners.get(i).modifyText(null);
		}
	}

	public void addModifyListener(ModifyListener listener) {
		modifyListeners.add(listener);
	}

	public void removeModifyListener(ModifyListener listener) {
		modifyListeners.remove(listener);
	}

	public String getSelectedConnectionName() {
		return getSelectedConnection().getId();
	}

	public boolean hasConnectionProfileSelected() {
		return getSelectedConnection().getProfile()!=null;
	}
}
