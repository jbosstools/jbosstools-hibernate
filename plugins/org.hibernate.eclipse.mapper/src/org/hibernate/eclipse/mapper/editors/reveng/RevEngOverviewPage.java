package org.hibernate.eclipse.mapper.editors.reveng;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.EditorPart;
import org.hibernate.eclipse.console.wizards.TableFilterComposite;
import org.hibernate.eclipse.console.wizards.TableFilterView;
import org.hibernate.eclipse.mapper.editors.ReverseEngineeringEditor;
import org.hibernate.eclipse.mapper.model.ReverseEngineeringDefinitionElement;

public class RevEngOverviewPage extends EditorPart {

	private FormToolkit toolkit;
	private ScrolledForm form;
	private final ReverseEngineeringEditor reditor;
	
	public RevEngOverviewPage(ReverseEngineeringEditor reditor) {
		this.reditor = reditor;		
	}
	
	public void doSave(IProgressMonitor monitor) {
		
	}

	public void doSaveAs() {

	}

	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		ReverseEngineeringDefinitionElement reverseEngineeringDefinition = reditor.getReverseEngineeringDefinition();
		reverseEngineeringDefinition.addPropertyChangeListener(new PropertyChangeListener() {
		
			public void propertyChange(PropertyChangeEvent evt) {
				updateTableFiltersTable( (ReverseEngineeringDefinitionElement) evt.getSource() );				
			}
		
		});		
	}

	public boolean isDirty() {
		return false;
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		form.setText("Overview");
		
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		
		form.getBody().setLayout(layout);
			
		
		createTableFilterSection();
		
		
		updateTableFiltersTable(reditor.getReverseEngineeringDefinition());
	}

	private void createTableFilterSection() {
		Section section = toolkit.createSection(form.getBody(), 
				Section.DESCRIPTION|ExpandableComposite.TWISTIE|ExpandableComposite.EXPANDED);
		TableWrapData td = new TableWrapData(TableWrapData.FILL);
		td.colspan = 2;
		section.setLayoutData(td);
		section.addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
			}
		});
		section.setText("Table filters");
		toolkit.createCompositeSeparator(section);
		section.setDescription("Table filters defines which tables/views are included when performing reverse engineering.");
		Composite sectionClient = toolkit.createComposite(section);
		sectionClient.setLayout(new GridLayout());
		section.setClient(sectionClient);
		
		TableFilterComposite composite = new TableFilterView(reditor.getReverseEngineeringDefinition(),sectionClient, SWT.NULL) {

			protected String getConsoleConfigurationName() {
				return "hibernate-adv-labA";
			}			
		};
		
		adaptRecursively( composite );
		
		
	}

	private void adaptRecursively(Composite composite) {
		toolkit.adapt(composite);
		Control[] children = composite.getChildren();
		for (int i = 0; i < children.length; i++) {
			Control control = children[i];
			if(control instanceof Composite) {
				adaptRecursively((Composite) control);
			} else {
				toolkit.adapt(control, true,false);
			}
		}
	}

	public void setFocus() {
		form.setFocus();
	}
	
	public void dispose() {
		toolkit.dispose();
		super.dispose();
	}

	private void updateTableFiltersTable(ReverseEngineeringDefinitionElement o) {
		//tableViewer.setInput(o.getTableFilters().toArray());
	}
}
