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
package org.hibernate.eclipse.mapper.editors.reveng;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.hibernate.eclipse.mapper.editors.ReverseEngineeringEditor;

public class RevEngOverviewPage extends RevEngFormEditorPart {

	public static final String PART_ID = "overview";
	
	public RevEngOverviewPage(ReverseEngineeringEditor reditor) {
		super(reditor, PART_ID, "Overview");
		this.reditor = reditor;
	}
	
	private final ReverseEngineeringEditor reditor;
	private ConsoleConfigNamePart configNamePart;
	
	public void createFormContent(IManagedForm parent) {
		ScrolledForm form = parent.getForm();
				
		ColumnLayout layout = new ColumnLayout();
		layout.maxNumColumns = 2;
		
		form.getBody().setLayout(layout);
		
		createConsoleConfigName();
		createContentsSection();
		
		getManagedForm().setInput(reditor.getReverseEngineeringDefinition());
				
	}

	private void createConsoleConfigName() {
		Composite parent = getManagedForm().getForm().getBody();
				
		configNamePart = new ConsoleConfigNamePart(parent, getManagedForm(), reditor);
		
		//GridData gd = new GridData(GridData.FILL_HORIZONTAL|GridData.VERTICAL_ALIGN_BEGINNING);
		//configNamePart.getSection().setLayoutData(gd);
		
		getManagedForm().addPart(configNamePart);
			
	}
	
	private Section createStaticSection(FormToolkit toolkit, Composite parent, String text) {
		Section section = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR);
		section.clientVerticalSpacing = 4;
		section.setText(text);
		return section;
	}
	
	private void createContentsSection() {
		String sectionTitle;
		sectionTitle = "Contents";
		Section section = createStaticSection(
							getManagedForm().getToolkit(), 
							getManagedForm().getForm().getBody(), 
							sectionTitle);
	
		Composite container = getManagedForm().getToolkit().createComposite(section, SWT.NONE);
		TableWrapLayout layout = new TableWrapLayout();
		layout.leftMargin = layout.rightMargin = layout.topMargin = layout.bottomMargin = 0;
		container.setLayout(layout);
		container.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		
		FormText text = createClient(container, "<form><p>The content of the reveng.xml is made up of three sections:</p><li style=\"image\" value=\"page\" bindent=\"5\"><a href=\"typemappings\">Type Mappings</a>: lists the mappings from a JDBC/SQL type to Hibernate type.</li><li style=\"image\" value=\"page\" bindent=\"5\"><a href=\"tablefilter\">Table filters</a>: lists which tables that should be included or excluded during reverse engineering.</li><li style=\"image\" value=\"page\" bindent=\"5\"><a href=\"tables\">Tables &amp; Columns</a>: explicitly set properties for tables and columns.</li></form>", getManagedForm().getToolkit());
		//text.setImage("page", EclipseImages.getImage(ImageConstants.)); //$NON-NLS-1$
		
		section.setClient(container);
		//section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		//section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL,GridData.FILL_VERTICAL));
	
		//getManagedForm().addPart(s);		
	}
	
	private FormText createClient(Composite section, String content, FormToolkit toolkit) {
		FormText text = toolkit.createFormText(section, true);
		try {
			text.setText(content, true, false);
		} catch (SWTException e) {
			text.setText(e.getMessage(), false, false);
		}
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		text.addHyperlinkListener(new IHyperlinkListener() {
		
			public void linkEntered(HyperlinkEvent e) {}

			public void linkExited(HyperlinkEvent e) {}

			public void linkActivated(HyperlinkEvent e) {
				String href = (String) e.getHref();
				getEditor().setActivePage(href);				
			}
		});
		return text;
	}

	protected ReverseEngineeringEditor getRevEngEditor() {
		return reditor;
	}

	public String getConsoleConfigName() {
		return configNamePart.getConsoleConfigName();
	}

	public void setConsoleConfigName(String name) {
		configNamePart.setConsoleConfigName(name);		
	}
	
}
