package org.jboss.tools.hibernate.search.toolkit.search;

import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.hibernate.console.ConsoleConfiguration;
import org.jboss.tools.hibernate.search.HSearchConsoleConfigurationPreferences;
import org.jboss.tools.hibernate.search.console.ConsoleConfigurationUtils;
import org.jboss.tools.hibernate.search.runtime.spi.HSearchServiceLookup;
import org.jboss.tools.hibernate.search.runtime.spi.IHSearchService;
import org.jboss.tools.hibernate.search.toolkit.AbstractTabBuilder;
import org.jboss.tools.hibernate.search.toolkit.analyzers.AnalyzersCombo;

public class SearchTabBuilder extends AbstractTabBuilder {
	
	private static class SignletonHolder {
		private static final SearchTabBuilder instance = new SearchTabBuilder();
	}
	
	public static SearchTabBuilder getInstance() {
		return SignletonHolder.instance;
	}
	
	private AnalyzersCombo analyzersCombo;
	private Combo entityCombo;
	private Combo fieldsCombo;
	private SearchResultTable resultTable;
	
	protected Composite buildTab(CTabFolder folder, ConsoleConfiguration consoleConfig) {
		final String consoleConfigName = consoleConfig.getName();
		Composite container = new Composite(folder, SWT.VERTICAL);
		container.setLayout(new GridLayout());
		
		Composite entitiesContainer = new Composite(container, SWT.TOP);
		GridData entitiesGridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		entitiesContainer.setLayoutData(entitiesGridData);
		createEntityCombo(entitiesContainer, consoleConfig);
		entitiesContainer.pack();
		
		Composite searchDataComposite = new Composite(container, SWT.TOP);
		searchDataComposite.setLayout(new GridLayout(2, true));
		final Text query = new Text(searchDataComposite, SWT.MULTI | SWT.BORDER);
		GridData queryGridData = new GridData(GridData.FILL, GridData.BEGINNING, true, false);
		queryGridData.heightHint = 5 * query.getLineHeight();
		queryGridData.verticalSpan = 2;
		query.setLayoutData(queryGridData);
		
		analyzersCombo = new AnalyzersCombo(searchDataComposite, new GridData(), consoleConfigName);
		
		Button searchButton = new Button(searchDataComposite, SWT.PUSH);
		searchButton.setText("Search");
		searchButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				ClassLoader classloader =  ConsoleConfigurationUtils.getClassLoader(consoleConfig);
				Class<?> entity = null;
				try {
					entity = Class.forName(entityCombo.getText(), true, classloader);
				} catch (ClassNotFoundException e) {
					entity = null;
				}
				IHSearchService service = HSearchServiceLookup.findService(HSearchConsoleConfigurationPreferences.getHSearchVersion(consoleConfigName));
				resultTable.showResults(
						service.search(
								consoleConfig.getSessionFactory(), 
								entity,
								fieldsCombo.getText(), 
								analyzersCombo.getAnalyzer(), 
								query.getText()),
						consoleConfig.getSessionFactory().getAllClassMetadata().get(entityCombo.getText()));
			}
		});
		searchDataComposite.pack();

		this.resultTable = new SearchResultTable(container, consoleConfig);
		
		//container.pack();
		container.update();
		return container;
	}
	
	protected void createEntityCombo(Composite parent, final ConsoleConfiguration consoleConfig) {
		if (!ConsoleConfigurationUtils.loadSessionFactorySafely(consoleConfig)) {
			return;
		};
		
		Composite entitiesComposite = new Composite(parent, SWT.NONE);
		entitiesComposite.setLayout(new RowLayout());
		this.entityCombo = new Combo(entitiesComposite, SWT.NONE|SWT.READ_ONLY);
		
		for (Class<?> entity: ConsoleConfigurationUtils.getIndexedEntities(consoleConfig)) {
			entityCombo.add(entity.getName());
		}
		
		if (entityCombo.getItemCount() == 0) {
			new Label(entitiesComposite, SWT.NONE).setText("No entity classes anntotated @Indexed");
			return;
		}
		
		Label defaultFieldLabel = new Label(entitiesComposite, SWT.CENTER);
		defaultFieldLabel.setText("Default field:");
		
		this.fieldsCombo = new Combo(entitiesComposite, SWT.NONE|SWT.READ_ONLY);
		
		this.entityCombo.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				ClassLoader classloader =  ConsoleConfigurationUtils.getClassLoader(consoleConfig);
				try {
					fieldsCombo.removeAll();
					Class<?> clazz = Class.forName(((Combo)e.getSource()).getText(), true, classloader);
					Set<String> fields = ConsoleConfigurationUtils.getHSearchService(consoleConfig)
							.getIndexedFields(consoleConfig.getSessionFactory(), clazz);
					fields.stream().filter(s -> !"_hibernate_class".equals(s)).forEach(s -> fieldsCombo.add(s));
					fieldsCombo.select(0);
				} catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		});	
		entityCombo.select(0);
		entitiesComposite.pack();	
	}
}
