package org.hibernate.eclipse.console.views;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
import org.hibernate.eclipse.hqleditor.HQLEditor;

public class QueryParametersView extends PageBookView {

		
	public QueryParametersView() {
		super();
	}
	
	protected IPage createDefaultPage(PageBook book) {
		MessagePage page = new MessagePage();
        initPage(page);
        page.createControl(book);
        page.setMessage("No HQL editor open");
        return page;		
	}

	protected PageRec doCreatePage(IWorkbenchPart part) {
		Object obj = part.getAdapter(IQueryParametersPage.class);
        if (obj instanceof IQueryParametersPage) {
            IQueryParametersPage page = (IQueryParametersPage) obj;
            if (page instanceof IPageBookViewPage)
                initPage((IPageBookViewPage) page);
            page.createControl(getPageBook());
            return new PageRec(part, page);
        }
        // There is no query parameters to outline
        return null;		
	}

	protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord) {
		QueryParametersPage qppage = (QueryParametersPage)pageRecord.page;
		qppage.dispose();
		pageRecord.dispose();
	}

	protected IWorkbenchPart getBootstrapPart() {
		IWorkbenchPage page = getSite().getPage();
        if (page != null && isImportant(page.getActiveEditor()))
            return page.getActiveEditor();

        return null;
	}

	protected boolean isImportant(IWorkbenchPart part) {
		return part!=null && part instanceof HQLEditor;
	}
}
