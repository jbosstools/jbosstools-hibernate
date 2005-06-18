package org.hibernate.eclipse.mapper.editors.reveng;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.hibernate.eclipse.mapper.editors.ReverseEngineeringMultiPageEditor;
import org.hibernate.eclipse.mapper.editors.SQLTypeContentProvider;

public class SQLTypeMappingFormPage extends FormPage {

	public SQLTypeMappingFormPage(ReverseEngineeringMultiPageEditor editor) {
		super(editor, SQLTypeMappingFormPage.class.getName(), "Type mapping");
	}

	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		form.setText("Mappings from JDBC types to Hibernate types");
				 new String();
		managedForm.getToolkit().paintBordersFor(form);
		
		GridLayout layout = new GridLayout(1,false);		
		form.getBody().setLayout(layout);
		
		Table table = managedForm.getToolkit().createTable(form.getBody(), SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		
		TableColumn col = new TableColumn(table, SWT.LEFT);
		col.setText("Test");
		col.setWidth(100);
		
		col = new TableColumn(table, SWT.LEFT);
		col.setWidth(100);
		col.setText("Test 2");
		
		table.setHeaderVisible(true);

		GridData gd = new GridData(GridData.FILL_BOTH);
		//gd.horizontalAlignment = GridData.F
		table.setLayoutData(gd);
		
		
		TableViewer tableViewer = new TableViewer(table);
		tableViewer.setContentProvider(new SQLTypeContentProvider() );
		
		
		/*tableViewer.setLabelProvider(new ITableLabelProvider() {
			
			String[] columns = new String[] { "SQL Type", "Length", "Precision", "Scale", "Hibernate Type"};
			public void removeListener(ILabelProviderListener listener) {	}
		
			public boolean isLabelProperty(Object element, String property) { return false;	}
		
			public void dispose() {	}
		
			public void addListener(ILabelProviderListener listener) {	}
		
			public String getColumnText(Object element, int columnIndex) {

				return null;
			}
		
			public Image getColumnImage(Object element, int columnIndex) {
				// TODO Auto-generated method stub
				return null;
			}
		
		});*/
	
	

	}
}
