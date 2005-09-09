package org.hibernate.eclipse.mapper.editors;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.INestableKeyBindingService;
import org.eclipse.ui.PartInitException;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.ui.internal.provisional.StructuredTextEditorXML;
import org.eclipse.wst.xml.ui.internal.tabletree.XMLMultiPageEditorPart;
import org.hibernate.eclipse.console.model.IReverseEngineeringDefinition;
import org.hibernate.eclipse.mapper.MapperPlugin;
import org.hibernate.eclipse.mapper.editors.reveng.RevEngOverviewPage;
import org.hibernate.eclipse.mapper.model.DOMReverseEngineeringDefinition;
import org.w3c.dom.Document;

public class ReverseEngineeringEditor extends XMLMultiPageEditorPart {

	private StructuredTextEditorXML sourcePage;
	private RevEngOverviewPage formPage;
	private DOMReverseEngineeringDefinition definition;	

	public ReverseEngineeringEditor() {
		
	}
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.init( site, input );
		// GEF initActionRegistry();
		// GEF initEditDomain();
		// GEF initCommandStackListener();
		// MAYBE initSelectionListener();
		// MAYBE initPartName();
	}

	protected void createPages() {
		try {
			super.createPages();
			initSourcePage();
			addFormPage();
		}
		catch (PartInitException e) {
			MapperPlugin.getDefault().getLogger().logException(
					"Could not create graphical viewer", e );
		}
	}

	private void addFormPage() throws PartInitException {
		formPage = new RevEngOverviewPage( this );
		addPage( 0, formPage, getEditorInput() );
		setPageText( 0, "Overrides" );
		setActivePage( 0 );
	}

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
	
}