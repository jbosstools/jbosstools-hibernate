package org.jboss.tools.hibernate.search.toolkit.docs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.jboss.tools.hibernate.search.HSearchConsoleConfigurationPreferences;
import org.jboss.tools.hibernate.search.console.ConsoleConfigurationUtils;
import org.jboss.tools.hibernate.search.runtime.spi.HSearchServiceLookup;
import org.jboss.tools.hibernate.search.runtime.spi.IHSearchService;
import org.jboss.tools.hibernate.search.toolkit.AbstractTabBuilder;

public class ExploreDocsTabBuilder extends AbstractTabBuilder {
	
	private static class SignletonHolder {
		private static final ExploreDocsTabBuilder instance = new ExploreDocsTabBuilder();
	}
	
	public static ExploreDocsTabBuilder getInstance() {
		return SignletonHolder.instance;
	}
	
	private TableViewer tableViewer;
	private Set<Button> entityCheckBoxes = new HashSet<Button>();
	private List<Map<String, String>> docs = new ArrayList<Map<String, String>>();
	private Label docNumberLbl;

	
	protected Composite buildTab(CTabFolder folder, ConsoleConfiguration consoleConfig) {
		Composite container = new Composite(folder, SWT.TOP);
		
		container.setLayout(new GridLayout(1, true));
		createOperatingConrols(container, consoleConfig);
		createDocTable(container);   
		
		container.update();
		return container;
	}
	
	protected void createOperatingConrols(Composite parent, final ConsoleConfiguration consoleConfig) {
		createEntityCheckBoxes(parent, consoleConfig);
		
		Composite block = new Composite(parent, SWT.NONE);
		block.setLayout(new RowLayout());
		Button execButton = new Button(block, SWT.PUSH);
		new Label(block, SWT.NONE).setText("Document #");
		Button preButton = new Button(block, SWT.PUSH);
		preButton.setText("<---");
		this.docNumberLbl = new Label(block, SWT.NONE);
		this.docNumberLbl.setText("   ");
		Button nextButton = new Button(block, SWT.PUSH);		
		nextButton.setText("--->");
		
		preButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (docs.isEmpty()) {
					return;
				}
				int curNum = Integer.valueOf(docNumberLbl.getText());
				if (curNum > 0) {
					tableInsert(docs.get(curNum - 1));
					docNumberLbl.setText(String.valueOf(curNum - 1));
				}
			}
			
		});
		
		nextButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (docs.isEmpty()) {
					return;
				}
				int curNum = Integer.valueOf(docNumberLbl.getText());
				if (curNum < docs.size() - 1) {
					tableInsert(docs.get(curNum + 1));
					docNumberLbl.setText(String.valueOf(curNum + 1));
				}
			}
			
		});
		
		
		execButton.setImage(EclipseImages.getImage(ImageConstants.EXECUTE));
		execButton.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {				
				ClassLoader classloader =  ConsoleConfigurationUtils.getClassLoader(consoleConfig);
				
				Set<Class> classes = new HashSet<Class>();
				for (Button entityBtn: entityCheckBoxes) {
					if (entityBtn.getSelection()) {
						try {
							classes.add(Class.forName(entityBtn.getText(), true, classloader));
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
				}
				if (classes.isEmpty()) {
					return;
				}
				IHSearchService service = HSearchServiceLookup.findService(HSearchConsoleConfigurationPreferences.getHSearchVersion(consoleConfig.getName()));
				docs = service.getEntityDocuments(consoleConfig.getSessionFactory(), classes.toArray(new Class[0]));
				if (docs.isEmpty()) {
					tableViewer.getTable().removeAll();
					tableViewer.add(new TableObject("No Lucene index found", ""));
					tableViewer.add(new TableObject("or your console configuration is out of sync", ""));
					return;
				}
				tableInsert(docs.get(0));
				docNumberLbl.setText("0");
			}
			
		});				
	}
	
	private void tableInsert(Map<String, String> map) {
		this.tableViewer.getTable().removeAll();
		for (String field: map.keySet()) {
			this.tableViewer.add(new TableObject(field, map.get(field)));
		}
	}
	
	protected void createEntityCheckBoxes(Composite parent, ConsoleConfiguration consoleConfig) {
		if (!ConsoleConfigurationUtils.loadSessionFactorySafely(consoleConfig)) {
			return;
		};
		
		Composite entitiesComposite = new Composite(parent, SWT.NONE);
		entitiesComposite.setLayout(new RowLayout());

		for (Class<?> entity: ConsoleConfigurationUtils.getIndexedEntities(consoleConfig)) {
			Button button = new Button(entitiesComposite, SWT.CHECK);
			button.setText(entity.getName());
			this.entityCheckBoxes.add(button);
		}
		if (this.entityCheckBoxes.isEmpty()) {
			new Label(entitiesComposite, SWT.NONE).setText("No entity classes anntotated @Indexed");
		}
		entitiesComposite.pack();
	}
	
	protected void createDocTable(Composite parent) {
		this.tableViewer = new TableViewer(parent, SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns();
		
		Table table = tableViewer.getTable();
	    table.setHeaderVisible(true);
	    table.setLinesVisible(true);
	    
	    tableViewer.setContentProvider(new ArrayContentProvider());
	    GridData gridData = new GridData();
	    gridData.verticalAlignment = SWT.FILL;
	    gridData.horizontalSpan = 2;
	    gridData.horizontalAlignment = SWT.FILL;
	    gridData.grabExcessVerticalSpace = true;
	    tableViewer.getControl().setLayoutData(gridData);
	}
	
	protected void createColumns() {
		TableViewerColumn column = createSingleColumn("Field");
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TableObject map = (TableObject) element;
				return map.getField();				
			}
		});
		column = createSingleColumn("Value");
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TableObject map = (TableObject) element;
				return map.getValue();				
			}
		});
	}
	
	protected TableViewerColumn createSingleColumn(String title) {
		TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(150);
		column.setResizable(true);
		return viewerColumn;
	}
	
	@Override
	public void disposeAll() {
		this.entityCheckBoxes.clear();
		super.disposeAll();
	}
	
	private static class TableObject {
		private String field;
		private String value;
		public TableObject(String field, String value) {
			this.field = field;
			this.value = value;
		}
		public String getField() {
			return field;
		}
		public void setField(String field) {
			this.field = field;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
	}
}
