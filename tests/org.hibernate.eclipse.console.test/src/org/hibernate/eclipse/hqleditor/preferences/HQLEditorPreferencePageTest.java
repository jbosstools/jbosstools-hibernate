package org.hibernate.eclipse.hqleditor.preferences;

import org.eclipse.jface.preference.PreferenceDialog;
import org.jboss.tools.test.util.WorkbenchUtils;

import junit.framework.TestCase;
/**
 * TODO Get rid of copy paste code
 * 
 * @author eskimo
 *
 */
public class HQLEditorPreferencePageTest extends TestCase {

	public void testHQLEditorPreferencePageShow() {
		PreferenceDialog prefDialog = 
			WorkbenchUtils.createPreferenceDialog(
					HQLEditorPreferencePage.class.getName());

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
			WorkbenchUtils.createPreferenceDialog(
					HQLEditorPreferencePage.class.getName());

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
