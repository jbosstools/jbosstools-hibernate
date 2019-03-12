package org.jboss.tools.hibernate.search.toolkit.analyzers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.hibernate.console.ConsoleConfiguration;
import org.jboss.tools.hibernate.search.HSearchConsoleConfigurationPreferences;
import org.jboss.tools.hibernate.search.runtime.spi.HSearchServiceLookup;
import org.jboss.tools.hibernate.search.runtime.spi.IHSearchService;
import org.jboss.tools.hibernate.search.toolkit.AbstractTabBuilder;

public class AnalyzersTabBuilder extends AbstractTabBuilder {
	
	private static class SignletonHolder {
		private static final AnalyzersTabBuilder instance = new AnalyzersTabBuilder();
	}
	
	public static AnalyzersTabBuilder getInstance() {
		return SignletonHolder.instance;
	}
	
	private AnalyzersCombo analyzersCombo;
	
	protected Composite buildTab(CTabFolder folder, ConsoleConfiguration consoleConfig) {
		final String consoleConfigName = consoleConfig.getName();
		Composite container = new Composite(folder, SWT.TOP);
		container.setLayout(new GridLayout(2, true));

		GridData comboGridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING, GridData.VERTICAL_ALIGN_CENTER, false, false);
		comboGridData.horizontalSpan = 2;
		analyzersCombo = new AnalyzersCombo(container, comboGridData, consoleConfigName);	
		
		final Text input = new Text(container, SWT.MULTI | SWT.BORDER);
		input.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		final Text output = new Text(container, SWT.MULTI | SWT.BORDER);
		output.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		output.setEditable(false);
		input.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				IHSearchService service = HSearchServiceLookup.findService(HSearchConsoleConfigurationPreferences.getHSearchVersion(consoleConfigName));
				String result = service.doAnalyze(((Text)e.getSource()).getText(), analyzersCombo.getAnalyzer());
				output.setText(result);			
			}
		});
		container.pack();
		container.update();
		return container;
	}
}
