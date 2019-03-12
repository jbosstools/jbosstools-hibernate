package org.jboss.tools.hibernate.search.toolkit.search;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.hibernate.console.ConsoleConfiguration;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.search.console.ConsoleConfigurationUtils;

public class SearchResultTable {
	
	private TableViewer resultViewer;
	private ClassLoader classloader;
	private Composite parentComposite;
	
	public SearchResultTable(Composite parent, ConsoleConfiguration consoleConfig) {
		this.parentComposite = parent;
		this.resultViewer = new TableViewer(parent, SWT.MULTI | SWT.BORDER);
		this.resultViewer.getTable().setVisible(false);
		this.classloader = ConsoleConfigurationUtils.getClassLoader(consoleConfig);
	}
	
	public void showResults(List<Object> results, IClassMetadata classMeta) {
		this.resultViewer.getTable().setVisible(false);
		Arrays.stream(this.resultViewer.getTable().getColumns()).forEach(column -> column.dispose());
		createTable(classMeta);
		if (!results.isEmpty()) {
			addResults(results);
		}
		this.resultViewer.getTable().pack();
		this.resultViewer.getTable().update();
		this.resultViewer.getTable().setVisible(true);
	}
	
	protected void createTable(IClassMetadata classMeta) {
		createColumns(classMeta);
		Table table = this.resultViewer.getTable();
	    table.setHeaderVisible(true);
	    table.setLinesVisible(true);
	    
	    this.resultViewer.setContentProvider(new ArrayContentProvider());
	    GridData gridData = new GridData();
	    gridData.verticalAlignment = SWT.FILL;
	    gridData.horizontalSpan = classMeta.getPropertyNames().length;
	    gridData.horizontalAlignment = SWT.FILL;
	    gridData.grabExcessVerticalSpace = true;
	    gridData.grabExcessHorizontalSpace = true;
	    this.resultViewer.getControl().setLayoutData(gridData);
	}
	
	protected void addResults(List<Object> results) {
		this.resultViewer.add(results.toArray());
	}
	
	protected void createColumns(IClassMetadata classMeta) {
		List<String> allColumnsIncludingId = new ArrayList<String>(classMeta.getPropertyNames().length + 1);
		allColumnsIncludingId.add(classMeta.getIdentifierPropertyName());
		Collections.addAll(allColumnsIncludingId, classMeta.getPropertyNames());
		int columnSize = (int)(0.75 * this.parentComposite.getSize().x) / classMeta.getPropertyNames().length;
		for (String property: allColumnsIncludingId) {
			TableViewerColumn column = createSingleColumn(property, columnSize);
			column.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object result) {
					try {
						Class<?> clazz = Class.forName(classMeta.getMappedClass().getCanonicalName(), true, classloader);
						Field field = clazz.getDeclaredField(property);
						field.setAccessible(true);
						return String.valueOf(field.get(result));
					} catch (IllegalArgumentException | IllegalAccessException | 
							NoSuchFieldException | SecurityException | ClassNotFoundException e) {
						return null;
					}
				}
			});
		}
	}
	
	protected TableViewerColumn createSingleColumn(String title, int width) {
		TableViewerColumn viewerColumn = new TableViewerColumn(this.resultViewer, SWT.NONE);
		TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(width);
		column.setResizable(true);
		return viewerColumn;
	}

}
