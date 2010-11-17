/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.hqleditor;

import junit.framework.TestCase;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.IPage;
import org.hibernate.console.QueryInputModel;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.views.QueryParametersPage;
import org.hibernate.eclipse.console.views.QueryParametersView;

/**
 * @author Dmitry Geraskov
 *
 */
public class HQLEditorTest extends TestCase {
	
	public void testHQLEditorOpen(){
		IEditorPart editorPart = HibernateConsolePlugin.getDefault()
			.openScratchHQLEditor(null, "");
		assertNotNull("Editor was not opened", editorPart);
		assertTrue("Opened editor is not HQLEditor", editorPart instanceof HQLEditor);
		
		HQLEditor editor = (HQLEditor)editorPart;		
		QueryInputModel model = editor.getQueryInputModel();
		assertNotNull("Model is NULL", model);
	}
	
	public void testSingleLineCommentsCutOff() throws PartInitException{
		String query = "from pack.Article a\n" +
				"where a.articleid in (:a, :b) --or a.articleid = :c";
		IEditorPart editorPart = HibernateConsolePlugin.getDefault()
			.openScratchHQLEditor(null, query);
		assertTrue("Opened editor is not HQLEditor", editorPart instanceof HQLEditor);
		
		HQLEditor editor = (HQLEditor)editorPart;
		assertEquals(editor.getEditorText(), query);
		assertFalse("Comments were not cut off", editor.getQueryString().contains("--"));
		
		QueryInputModel model = editor.getQueryInputModel();
		assertTrue(model.getParameterCount() == 0);
		
		IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
			.getActivePage().showView("org.hibernate.eclipse.console.views.QueryParametersView");
		assertNotNull("View was not opened", view);
		assertTrue("Opened view is not QueryParametersView", view instanceof QueryParametersView);
		
		QueryParametersView paramView = (QueryParametersView)view;
		IPage ipage = paramView.getCurrentPage();
		assertNotNull("Current Page is NULL", ipage);
		assertTrue("Page is not Query Parameters Page", ipage instanceof QueryParametersPage);
		
		QueryParametersPage page = (QueryParametersPage)ipage;
		IToolBarManager manager = page.getSite().getActionBars().getToolBarManager();
		IContributionItem[] items = manager.getItems();
		ActionContributionItem addParamItem = null;
		for (int i = 0; i < items.length; i++) {
			ActionContributionItem item = (ActionContributionItem) items[i];
			if (item.getAction().getClass().getName().endsWith("NewRowAction")){
				addParamItem = item;
				break;
			}
		}
		assertNotNull(HibernateConsoleMessages.QueryParametersPage_add_query_parameter_tooltip
				+ " item not found", addParamItem);
		
		addParamItem.getAction().run();//add query parameters automatically
		assertTrue(model.getParameterCount() == 2);//a and b
		
	}

}
