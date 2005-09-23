package org.hibernate.eclipse.mapper.editors;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.INestableKeyBindingService;
import org.eclipse.ui.PartInitException;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.ui.internal.provisional.StructuredTextEditorXML;
import org.hibernate.eclipse.console.model.IReverseEngineeringDefinition;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.hibernate.eclipse.mapper.MapperPlugin;
import org.hibernate.eclipse.mapper.editors.reveng.RevEngOverviewPage;
import org.hibernate.eclipse.mapper.editors.reveng.RevEngTableFilterPage;
import org.hibernate.eclipse.mapper.editors.reveng.RevEngTablesPage;
import org.hibernate.eclipse.mapper.editors.reveng.RevEngTypeMappingPage;
import org.hibernate.eclipse.mapper.model.DOMReverseEngineeringDefinition;
import org.hibernate.eclipse.nature.HibernateNature;
import org.w3c.dom.Document;

public class ReverseEngineeringEditor extends XMLFormEditorPart {

	private StructuredTextEditorXML sourcePage;
	private RevEngTableFilterPage tableFilters;
	private DOMReverseEngineeringDefinition definition;
	private RevEngTypeMappingPage typeMappings;
	private RevEngOverviewPage overview;	
	private Map pageNameToIndex = new HashMap();
	private RevEngTablesPage tableProperties;
	
	public ReverseEngineeringEditor() {
		
	}
	
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init( site, input );
	}

	protected void addPages() {
		try {
			//getSite().getSelectionProvider().setSelection(StructuredSelection.EMPTY);
			super.addPages();
			initSourcePage();
			addFormPages();
		}
		catch (PartInitException e) {
			MapperPlugin.getDefault().getLogger().logException(
					"Could not create graphical viewer", e );
		}
	}

	private void addFormPages() throws PartInitException {
		int i = 0;
		overview = new RevEngOverviewPage(this);
		addPage( i, overview);
		setPageText(i, "Overview");
		pageNameToIndex.put(RevEngOverviewPage.PART_ID, new Integer(i));
		i++;
		
		typeMappings = new RevEngTypeMappingPage( this );
		addPage( i, typeMappings);
		setPageText( i, "Type Mappings" );
		pageNameToIndex.put(RevEngTypeMappingPage.PART_ID, new Integer(i));
		i++;
		

		tableFilters = new RevEngTableFilterPage( this );
		addPage( i, tableFilters);
		setPageText( i, "Table Filters" );
		pageNameToIndex.put(RevEngTableFilterPage.PART_ID, new Integer(i));		
		i++;
		
		tableProperties = new RevEngTablesPage(this );
		addPage( i, tableProperties);
		setPageText(i, "Table && Columns");
		pageNameToIndex.put(RevEngTablesPage.PART_ID, new Integer(i));
		i++;
		
	//	setActivePage( 0 );
	}

	/*public void setActivePage(String string) {
		Integer number = (Integer) pageNameToIndex.get(string);
		if(number!=null) {
			setActivePage(number.intValue());
		}		
	}*/
	
	private void initSourcePage() {
		int pageCount = getPageCount();
		for (int i = 0; i < pageCount; i++) {
			if ( getEditor( i ) instanceof StructuredTextEditorXML ) {
				sourcePage = (StructuredTextEditorXML) getEditor( i );
				IDOMDocument document = getDocument(sourcePage);
				definition = new DOMReverseEngineeringDefinition(document);				
			}
		}
	}

	private IDOMDocument getDocument(StructuredTextEditorXML source) {
		IDOMDocument document = (IDOMDocument) source
				.getAdapter( Document.class );
		return document;
	}

	protected void pageChange(int newPageIndex) {
		if (newPageIndex == 0) {
	        IKeyBindingService service = getSite().getKeyBindingService();
            if (service instanceof INestableKeyBindingService) {
                INestableKeyBindingService nestableService = (INestableKeyBindingService) service;
                nestableService.activateKeyBindingService(null);
            }	        
		}
		super.pageChange(newPageIndex);
	}
	
	public IReverseEngineeringDefinition getReverseEngineeringDefinition() {
		return definition;
	}
	public String getConsoleConfigurationName() {
		return overview.getConsoleConfigName();
	}
	
	public HibernateNature getHibernateNature() throws CoreException {
		if(getEditorInput()!=null) {
			IJavaProject project = ProjectUtils.findJavaProject(getEditorInput());
			if(project!=null && project.getProject().hasNature(HibernateNature.ID)) {
				HibernateNature nature = (HibernateNature) project.getProject().getNature(HibernateNature.ID);
				return nature;
			}
		}
		return null;
	}
	
}