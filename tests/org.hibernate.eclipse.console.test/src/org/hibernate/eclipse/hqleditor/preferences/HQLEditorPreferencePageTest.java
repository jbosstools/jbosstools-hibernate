package org.hibernate.eclipse.hqleditor.preferences;

import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

import junit.framework.TestCase;
/**
 * TODO Get rid of copy paste code
 * 
 * @author eskimo
 *
 */
public class HQLEditorPreferencePageTest extends TestCase {

	public static Shell getActiveShell() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
	}

	public static PreferenceDialog createPreferenceDialog(String pageId) {
		PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(
			getActiveShell(), pageId, new String[] {pageId}, null);
		dialog.setBlockOnOpen(false);
		return dialog;
	}
	
	public void testHQLEditorPreferencePageShow() {
		PreferenceDialog prefDialog = 
			createPreferenceDialog(HQLEditorPreferencePage.class.getName());

		try {
			prefDialog.setBlockOnOpen(false);
			prefDialog.open();
			
			Object selectedPage = prefDialog.getSelectedPage();
			assertTrue("Selected page is not an instance of HQLEditorPreferencePage", selectedPage instanceof HQLEditorPreferencePage); //$NON-NLS-1$
		} finally {
			prefDialog.close();
		}
	}
	
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

}
