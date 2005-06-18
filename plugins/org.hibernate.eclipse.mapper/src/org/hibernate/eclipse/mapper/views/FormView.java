package org.hibernate.eclipse.mapper.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;

public class FormView extends ViewPart {
	private FormToolkit toolkit;

	private ScrolledForm form;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay() );
		form = toolkit.createScrolledForm(parent);
		// form.setAlwaysShowScrollBars(true);
		form.setText("Hello, Eclipse Forms");
		TableWrapLayout layout = new TableWrapLayout();
		form.getBody().setLayout(layout);
		Hyperlink link = toolkit.createHyperlink(form.getBody(), "Click here.",
				SWT.WRAP);
		link.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				System.out.println("Link activated!");
			}
		});
		link
				.setText("This is an example of a form that is much longer and will need to wrap.");
		layout.numColumns = 2;
		TableWrapData td = new TableWrapData();
		td.colspan = 2;
		link.setLayoutData(td);
		Label label = toolkit.createLabel(form.getBody(), "Text field label:");
		Text text = toolkit.createText(form.getBody(), "");
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		text.setLayoutData(td);
		Button button = toolkit.createButton(form.getBody(),
				"An example of a checkbox in a form", SWT.CHECK);
		td = new TableWrapData();
		td.colspan = 2;
		button.setLayoutData(td);

		layout.numColumns = 3;

		label = toolkit.createLabel(form.getBody(),
				"Some text to put in the first column", SWT.WRAP);
		label = toolkit
				.createLabel(
						form.getBody(),
						"Some text to put in the second column and make it a bit longer so that we can see what happens with column distribution. This text must be the longest so that it can get more space allocated to the columns it belongs to.",
						SWT.WRAP);
		td = new TableWrapData();
		td.colspan = 2;
		label.setLayoutData(td);
		label = toolkit.createLabel(form.getBody(),
				"This text will span two rows and should not grow the column.",
				SWT.WRAP);
		td = new TableWrapData();
		td.rowspan = 2;
		label.setLayoutData(td);
		label = toolkit.createLabel(form.getBody(),
				"This text goes into column 2 and consumes only one cell",
				SWT.WRAP);
		label.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB) );
		label = toolkit.createLabel(form.getBody(),
				"This text goes into column 3 and consumes only one cell too",
				SWT.WRAP);
		label.setLayoutData(new TableWrapData(TableWrapData.FILL) );
		label = toolkit.createLabel(form.getBody(),
				"This text goes into column 2 and consumes only one cell",
				SWT.WRAP);
		label.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB) );
		label = toolkit.createLabel(form.getBody(),
				"This text goes into column 3 and consumes only one cell too",
				SWT.WRAP);
		label.setLayoutData(new TableWrapData(TableWrapData.FILL) );
		form.getBody().setBackground(
				form.getBody().getDisplay().getSystemColor(
						SWT.COLOR_WIDGET_BACKGROUND) );
	}

	/**
	 * Passing the focus request to the form.
	 */
	public void setFocus() {
		form.setFocus();
	}

	/**
	 * Disposes the toolkit.
	 */
	public void dispose() {
		toolkit.dispose();
		super.dispose();
	}
}