package org.jboss.tools.hibernate.search.toolkit;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.jboss.tools.hibernate.search.toolkit.analyzers.AnalyzersTabBuilder;
import org.jboss.tools.hibernate.search.toolkit.docs.ExploreDocsTabBuilder;
import org.jboss.tools.hibernate.search.toolkit.search.SearchTabBuilder;

public class IndexToolkitView extends ViewPart {
	
	public static final String INDEX_TOOLKIT_VIEW_ID = "org.jboss.tools.hibernate.search.IndexToolkitView";
	
	private static final String ANALYZERS_TAB_NAME = "Analyzers";
	private static final String EXPLORE_DOCS_TAB_NAME = "Explore Documents";
	private static final String SEARCH_TAB_NAME = "Search";
	
	private ConfigurationCombo consoleConfigCombo;
	
	public IndexToolkitView() {
	}
	
	private ConsoleConfiguration initialConsoleConfig = null;

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(1, true));
		this.consoleConfigCombo = new ConfigurationCombo(parent, initialConsoleConfig == null ? null : initialConsoleConfig.getName());
		
		final CTabFolder folder = new CTabFolder(parent, SWT.TOP);
		folder.setLayoutData(new GridData(GridData.FILL, SWT.FILL, true, true));
				
		ConsoleConfiguration consoleConfig = KnownConfigurations.getInstance().find(this.consoleConfigCombo.getConsoleConfigSelected());
		
		final CTabItem analyzersTab = new CTabItem(folder, SWT.NONE);
		analyzersTab.setText(ANALYZERS_TAB_NAME);	
		final AnalyzersTabBuilder analyzersBuilder = AnalyzersTabBuilder.getInstance();
		analyzersTab.setControl(analyzersBuilder.getTab(folder, consoleConfig));
		
		final CTabItem exploreDocsTab = new CTabItem(folder, SWT.NONE);
		exploreDocsTab.setText(EXPLORE_DOCS_TAB_NAME);
		final ExploreDocsTabBuilder exploreDocsBuilder = ExploreDocsTabBuilder.getInstance();
		exploreDocsTab.setControl(exploreDocsBuilder.getTab(folder, consoleConfig));		
		
		final CTabItem searchTab = new CTabItem(folder, SWT.NONE);
		searchTab.setText(SEARCH_TAB_NAME);
		final SearchTabBuilder searchBuilder = SearchTabBuilder.getInstance();
		searchTab.setControl(searchBuilder.getTab(folder, consoleConfig));
			
		folder.setSelection(0);
		
		this.consoleConfigCombo.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				ConsoleConfiguration consoleConfig = KnownConfigurations.getInstance().find(((Combo)e.getSource()).getText());
				analyzersTab.setControl(analyzersBuilder.getTab(folder, consoleConfig));
				exploreDocsTab.setControl(exploreDocsBuilder.getTab(folder, consoleConfig));
				searchTab.setControl(searchBuilder.getTab(folder, consoleConfig));
			}
		});
	}
	
	public void setInitialConsoleConfig(ConsoleConfiguration consoleConfig) {
		if (this.initialConsoleConfig != null) {
			// change config selected in combo, if the view was already opened before
			this.consoleConfigCombo.setConsoleConfigSelected(consoleConfig.getName()); 
		}
		this.initialConsoleConfig = consoleConfig;
	}

	@Override
	public void dispose() {
		AnalyzersTabBuilder.getInstance().disposeAll();
		ExploreDocsTabBuilder.getInstance().disposeAll();
		SearchTabBuilder.getInstance().disposeAll();
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

}
