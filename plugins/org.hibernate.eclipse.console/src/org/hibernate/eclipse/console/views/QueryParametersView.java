/*
 * JBoss, Home of Professional Open Source
 * Copyright 2005, JBoss Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.hibernate.eclipse.console.views;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.hqleditor.HQLEditor;

public class QueryParametersView extends PageBookView {


	public QueryParametersView() {
		super();
	}

	protected IPage createDefaultPage(PageBook book) {
		MessagePage page = new MessagePage();
        initPage(page);
        page.createControl(book);
        page.setMessage(HibernateConsoleMessages.QueryParametersView_no_hql_editor_open);
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
