package org.jboss.tools.hibernate.orm.test;

import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.hibernate.eclipse.hqleditor.preferences.HQLEditorPreferencePage;
import org.junit.Assert;
import org.junit.Test;
/**
 * TODO Get rid of copy paste code
 * 
 * @author eskimo
 *
 */
public class HQLEditorPreferencePageTest {

	@Test
	public void testHQLEditorPreferencePageShow() {
		PreferenceDialog prefDialog = 
			createPreferenceDialog(HQLEditorPreferencePage.class.getName());

		try {
			prefDialog.setBlockOnOpen(false);
			prefDialog.open();
			
			Object selectedPage = prefDialog.getSelectedPage();
			Assert.assertTrue("Selected page is not an instance of HQLEditorPreferencePage", selectedPage instanceof HQLEditorPreferencePage); //$NON-NLS-1$
		} finally {
			prefDialog.close();
		}
	}
	
	@Test
	public void testHQLEditorPreferencePagePerformOk() {
		PreferenceDialog prefDialog = 
			createPreferenceDialog(HQLEditorPreferencePage.class.getName());

		try {
			prefDialog.setBlockOnOpen(false);
			prefDialog.open();
			
			HQLEditorPreferencePage selectedPage = (HQLEditorPreferencePage)prefDialog.getSelectedPage();
			selectedPage.performOk();
		} finally {
			prefDialog.close();
		}
	}

	private Shell getActiveShell() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	}

	private PreferenceDialog createPreferenceDialog(String pageId) {
		PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(
			getActiveShell(), pageId, new String[] {pageId}, null);
		dialog.setBlockOnOpen(false);
		return dialog;
	}
	
}
