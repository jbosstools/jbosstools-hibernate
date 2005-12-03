package org.hibernate.eclipse.launch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.hibernate.eclipse.console.wizards.UpDownListComposite;
import org.hibernate.tool.hbm2x.DAOExporter;
import org.hibernate.tool.hbm2x.ExporterProvider;
import org.hibernate.tool.hbm2x.GenericExporter;
import org.hibernate.tool.hbm2x.POJOExporter;

public class ExporterProvidersTab extends AbstractLaunchConfigurationTab {

	public void createControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 10;
		
		UpDownListComposite composite = buildExporterTable(container);
		GridData gd= new GridData();
		gd.horizontalSpan= 3;
		gd.horizontalAlignment= GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		composite.setLayoutData(gd);
		
		PropertiesPart part = new PropertiesPart(this);
		
		part.createControl(container, "Properties");
		setControl(container);
				
	}

	private UpDownListComposite buildExporterTable(Composite parent) {
		final ILabelProvider labelProvider = new LabelProvider() {
			public String getText(Object element) {
				ExporterProvider provider = (ExporterProvider) element;
				return provider.getExporterName();
			}
		};
		
		final IStructuredContentProvider contentsProvider = new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				return ((Collection)inputElement).toArray(new ExporterProvider[0]);
			}
			public void dispose() {}
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
		};

		
		UpDownListComposite exporterViewer = new UpDownListComposite(parent, SWT.NONE, "Exporters", labelProvider) {
			protected Object[] handleAdd(int idx) {

				/*TableItem[] items = getTable().getItems();
				IPath[] exclude = new IPath[items.length];
				
				for (int i = 0; i < items.length; i++) {
					TableItem item = items[i];
					exclude[i] = (IPath) item.getData();			
				}*/
				
				
				List providers = new ArrayList();
				Map props = commonProperties();
				providers.add(new ExporterProvider("hbm2java", POJOExporter.class.getName(), props));
				
				props = commonProperties();
				providers.add(new ExporterProvider("hbm2dao", DAOExporter.class.getName(), props));
				
				props = commonProperties();
				props.put("filepattern", "File pattern");
				props.put("template", "Single template name");
				props.put("exporter_classname", "Exporter classname");
				
				providers.add(new ExporterProvider("hbmtemplate", GenericExporter.class.getName(), props));
								
				ListSelectionDialog dlg = new ListSelectionDialog(getShell(), providers, contentsProvider, labelProvider, "Select exporters to add");
				
				//dlg.setInitialSelections(initialSelection);
				dlg.open();
				return dlg.getResult();
				
			}

			private Map commonProperties() {
				Map props = new HashMap();
				props.put("ejb3", "EJB3/JSR-220 annotations (experimental!)");
				props.put("jdk5", "JDK 1.5 Constructs (generics, etc.)");
				props.put("templatepath", "User provided templates");
				return props;
			}

			protected String[] getAddButtonLabels() {
				return new String[] { "Add..." };				
			}
			
			protected void listChanged() {
				//dialogChanged();
			}
						
			protected void createColumns(Table table) {
				// dont create any columns
				table.setHeaderVisible(false);
				table.setLinesVisible(false);
								
			}
			
		};
		return exporterViewer; 
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		
	}

	public String getName() {
		return "Exporters V2";
	}
}
