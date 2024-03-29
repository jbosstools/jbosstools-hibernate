/*******************************************************************************
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.orm.test;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.SaveAsDialog;
import org.jboss.tools.hibernate.orm.test.utils.project.TestProject;
import org.jboss.tools.hibernate.ui.diagram.editors.DiagramViewer;
import org.jboss.tools.hibernate.ui.diagram.editors.actions.ExportImageAction;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * for ExportImageAction class functionality test
 * 
 * @author Vitali Yemialyanchyk
 */
public class ExportImageActionTest {

	public static final String PROJECT_NAME = "TestProject"; //$NON-NLS-1$
	
	protected TestProject project = null;

	@Before
	public void setUp() throws Exception {
		project = new TestProject(PROJECT_NAME);
	}

	@After
	public void tearDown() throws Exception {
		project.deleteIProject();
		project = null;
	}
	
	public Mockery context = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	//TODO JBIDE-28083: Hibernate Java 17 compability - Reenable test and investigate error
	@Ignore
	@Test
	public void testAction() {
		
		final DiagramViewer editor = context.mock(DiagramViewer.class);
		final SaveAsDialog saveDialog = context.mock(SaveAsDialog.class);
		final GraphicalViewer graphicalViewer = context.mock(GraphicalViewer.class);
		final ScalableFreeformRootEditPart scalableFreeformRootEditPart = context.mock(ScalableFreeformRootEditPart.class);
		final IFigure figure = context.mock(IFigure.class);
		final Control control = context.mock(Control.class);
		final Display display = context.mock(Display.class);
		final Rectangle rectangle = new Rectangle(0, 0, 20, 10);
		final String filePath = PROJECT_NAME + File.separator + "test.jpg"; //$NON-NLS-1$
		final IPath resPath = new Path(filePath);
		
		context.checking(new Expectations() {
			{
				allowing(editor).getStoreFileName();
				will(returnValue(filePath));

				allowing(saveDialog).setOriginalName(filePath);

				oneOf(saveDialog).open();
				will(returnValue(0));

				oneOf(saveDialog).getResult();
				will(returnValue(resPath));

				allowing(editor).getEditPartViewer();
				will(returnValue(graphicalViewer));

				allowing(graphicalViewer).getRootEditPart();
				will(returnValue(scalableFreeformRootEditPart));

				allowing(scalableFreeformRootEditPart).getLayer(LayerConstants.PRINTABLE_LAYERS);
				will(returnValue(figure));

				allowing(figure).getBounds();
				will(returnValue(rectangle));
			}
		});
		final ExportImageAction exportImageAction = new ExportImageAction(editor);
		exportImageAction.setSaveDialog(saveDialog);
		exportImageAction.setShowErrDialog(false);
		exportImageAction.run();
		// test is the file created
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IPath path = workspace.getRoot().getFullPath().append(resPath);
		path = workspace.getRoot().getLocation().append(path);
		File file = path.toFile();
		assertTrue(file.exists() && file.isFile());
		//
		boolean res = file.delete();
		assertTrue(res);
		// GENERAL TEST:
		// check for all expectations
		context.assertIsSatisfied();
	}
		

}
