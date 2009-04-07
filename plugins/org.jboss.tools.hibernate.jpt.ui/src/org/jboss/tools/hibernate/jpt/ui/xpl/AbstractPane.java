/*******************************************************************************
 * Copyright (c) 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *     Dmitry Geraskov, Exadel Inc. - Extracted from Dali 2.0 to protect from changes.
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.xpl;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jpt.ui.WidgetFactory;
import org.eclipse.jpt.ui.internal.Tracing;
import org.eclipse.jpt.ui.internal.listeners.SWTPropertyChangeListenerWrapper;
import org.eclipse.jpt.ui.internal.swt.BooleanButtonModelAdapter;
import org.eclipse.jpt.ui.internal.swt.CComboModelAdapter;
import org.eclipse.jpt.ui.internal.swt.ComboModelAdapter;
import org.eclipse.jpt.ui.internal.swt.SpinnerModelAdapter;
import org.eclipse.jpt.ui.internal.swt.TextFieldModelAdapter;
import org.eclipse.jpt.ui.internal.swt.TriStateCheckBoxModelAdapter;
import org.eclipse.jpt.ui.internal.util.ControlAligner;
import org.eclipse.jpt.ui.internal.util.LabeledButton;
import org.eclipse.jpt.ui.internal.util.LabeledControlUpdater;
import org.eclipse.jpt.ui.internal.util.SWTUtil;
import org.eclipse.jpt.ui.internal.widgets.TriStateCheckBox;
import org.eclipse.jpt.utility.internal.ClassTools;
import org.eclipse.jpt.utility.internal.StringConverter;
import org.eclipse.jpt.utility.internal.model.value.SimplePropertyValueModel;
import org.eclipse.jpt.utility.model.Model;
import org.eclipse.jpt.utility.model.event.PropertyChangeEvent;
import org.eclipse.jpt.utility.model.listener.PropertyChangeListener;
import org.eclipse.jpt.utility.model.value.ListValueModel;
import org.eclipse.jpt.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.utility.model.value.WritablePropertyValueModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.part.PageBook;

/**
 * The abstract definition of a pane which holds onto a <code>PropertyValueModel</code>
 * that contains the subject of this pane.
 * <p>
 * It also contains convenience methods for building buttons, labels, check
 * boxes, and radio buttons, etc.
 * <p>
 * It is possible to easily listen to any property changes coming from the
 * subject, {@link #addPropertyNames(Collection)} is specify which properties
 * are of interest and {@link #propertyChanged(String)} is used to notify the
 * pane when the property has changed.
 *
 * @see AbstractFormPane
 * @see AbstractDialogPane
 *
 * @version 2.0
 * @since 2.0
 */
@SuppressWarnings("nls")
public abstract class AbstractPane<T extends Model>
{
	/**
	 * The listener registered with the subject in order to be notified when a
	 * property has changed, the property names are determined by
	 * {@link #propertyNames()}.
	 */
	private PropertyChangeListener aspectChangeListener;

	/**
	 * The container of this composite.
	 */
	private Composite container;

	/**
	 *
	 */
	private ArrayList<AbstractPane<?>> internalPanesForEnablementControl;

	/**
	 * The aligner responsible to align the left controls.
	 */
	private ControlAligner leftControlAligner;

	/**
	 * Flag used to stop the circular population of widgets.
	 */
	private boolean populating;

	/**
	 * The aligner responsible to align the left controls.
	 */
	private ControlAligner rightControlAligner;

	/**
	 * This listener is registered with the subject holder in order to
	 * automatically repopulate this pane with the new subject.
	 */
	private PropertyChangeListener subjectChangeListener;

	/**
	 * The subject of this pane.
	 */
	private PropertyValueModel<T> subjectHolder;

	/**
	 * The collection of registered sub-panes will be automatically notified
	 * when listeners need to be engaged or disengaged or when to populate its
	 * widgets.
	 */
	private Collection<AbstractPane<?>> subPanes;

	/**
	 * The factory used to create various common widgets.
	 */
	private WidgetFactory widgetFactory;

	/**
	 * The collection of <code>Control</code>s that are displayed in this pane,
	 * which will have their enablement state updated when
	 * {@link #enableWidgets(boolean)} is called.
	 */
	private ArrayList<Control> widgets;

	/**
	 * Creates a new <code>AbstractSubjectPane</code>.
	 *
	 * @category Constructor
	 */
	@SuppressWarnings("unused")
	private AbstractPane() {
		super();
	}

	/**
	 * Creates a new <code>AbstractSubjectPane</code>.
	 *
	 * @param parentPane The parent pane of this one
	 * @param parent The parent container
	 *
	 * @category Constructor
	 */
	protected AbstractPane(AbstractPane<? extends T> parentPane,
	                       Composite parent) {

		this(parentPane, parent, true);
	}

	/**
	 * Creates a new <code>AbstractSubjectPane</code>.
	 *
	 * @param parentPane The parent container of this one
	 * @param parent The parent container
	 * @param widgetFactory The factory used to create various widgets
	 * @param automaticallyAlignWidgets <code>true</code> to make the widgets
	 * this pane aligned with the widgets of the given parent pane;
	 * <code>false</code> to not align them
	 *
	 * @category Constructor
	 */
	protected AbstractPane(AbstractPane<? extends T> parentPane,
	                       Composite parent,
	                       boolean automaticallyAlignWidgets) {

		this(parentPane,
		     parentPane.getSubjectHolder(),
		     parent,
		     automaticallyAlignWidgets);
	}

	/**
	 * Creates a new <code>AbstractSubjectPane</code>.
	 *
	 * @param parentPane The parent container of this one
	 * @param subjectHolder The holder of this pane's subject
	 * @param parent The parent container
	 *
	 * @category Constructor
	 */
	protected AbstractPane(AbstractPane<?> parentPane,
	                       PropertyValueModel<? extends T> subjectHolder,
	                       Composite parent) {

		this(parentPane, subjectHolder, parent, true);
	}

	/**
	 * Creates a new <code>AbstractSubjectPane</code>.
	 *
	 * @param parentPane The parent container of this one
	 * @param subjectHolder The holder of this pane's subject
	 * @param parent The parent container
	 * @param widgetFactory The factory used to create various widgets
	 * @param automaticallyAlignWidgets <code>true</code> to make the widgets
	 * this pane aligned with the widgets of the given parent pane;
	 * <code>false</code> to not align them
	 *
	 * @category Constructor
	 */
	protected AbstractPane(AbstractPane<?> parentPane,
	                       PropertyValueModel<? extends T> subjectHolder,
	                       Composite parent,
	                       boolean automaticallyAlignWidgets) {

		this(subjectHolder,
		     parent,
		     parentPane.getWidgetFactory());

		initialize(parentPane, automaticallyAlignWidgets);
	}

	/**
	 * Creates a new <code>AbstractSubjectPane</code>.
	 *
	 * @param subjectHolder The holder of this pane's subject
	 * @param parent The parent container
	 * @param widgetFactory The factory used to create various common widgets
	 *
	 * @category Constructor
	 */
	protected AbstractPane(PropertyValueModel<? extends T> subjectHolder,
	                       Composite parent,
	                       WidgetFactory widgetFactory) {

		super();

		this.initialize(subjectHolder, widgetFactory);

		try {
			this.populating = true;

			this.container = this.buildContainer(parent);
			this.initializeLayout(this.container);
		}
		finally {
			this.populating = false;
		}
	}

	/**
	 * Adds the given pane's widgets (those that were registered with its
	 * left <code>ControlAligner</code>) to this pane's left
	 * <code>ControlAligner</code> so that their width can be adjusted to have
	 * the width of the widest widget.
	 *
	 * @param pane The pane containing the widgets to add
	 *
	 * @category Layout
	 */
	protected final void addAlignLeft(AbstractPane<?> container) {
		this.leftControlAligner.add(container.leftControlAligner);
	}

	/**
	 * Adds the given control to the collection of widgets that have their width
	 * adjust with the width of the widest widget. The left alignment is usually
	 * used for labels.
	 *
	 * @param pane The pane to add
	 *
	 * @category Layout
	 */
	protected final void addAlignLeft(Control control) {
		this.leftControlAligner.add(control);
	}

	/**
	 * Adds the given pane's widgets (those that were registered with its
	 * right <code>ControlAligner</code>) to this pane's right
	 * <code>ControlAligner</code> so that their width can be adjusted to have
	 * the width of the widest widget.
	 *
	 * @param pane The pane containing the widgets to add
	 *
	 * @category Layout
	 */
	protected final void addAlignRight(AbstractPane<?> container) {
		this.rightControlAligner.add(container.rightControlAligner);
	}

	/**
	 * Adds the given control to the collection of widgets that have their width
	 * adjust with the width of the widest widget. The left alignment is usually
	 * used for buttons.
	 *
	 * @param pane The pane to add
	 *
	 * @category Layout
	 */
	protected final void addAlignRight(Control control) {
		this.rightControlAligner.add(control);
	}

	/**
	 * Adds the given pane's controls (those that were registered for
	 * alignment) from this pane.
	 *
	 * @param pane The pane containing the widgets to add for
	 * alignment
	 *
	 * @category Layout
	 */
	protected final void addPaneForAlignment(AbstractPane<?> container) {
		addAlignLeft(container);
		addAlignRight(container);
	}

	/**
	 * Adds any property names to the given collection in order to be notified
	 * when the actual property changes in the subject.
	 *
	 * @param propertyNames The collection of property names to register with the
	 * subject
	 */
	protected void addPropertyNames(Collection<String> propertyNames) {
	}

	/**
	 * Indicates that the given <code>Control</code> has its enablement state
	 * managed by this pane, i.e. through {@link #enableWidgets(boolean)}.
	 *
	 * @param control The <code>Control</code> to manage its enablement state
	 * automatically
	 *
	 * @category Layout
	 */
	public final void addToEnablementControl(Control control) {
		control.setData("enablement", null);
	}

	private PropertyChangeListener buildAspectChangeListener() {
		return new SWTPropertyChangeListenerWrapper(buildAspectChangeListener_());
	}

	private PropertyChangeListener buildAspectChangeListener_() {
		return new PropertyChangeListener() {
			public void propertyChanged(PropertyChangeEvent e) {
				//subject() could have changed or is null because of the possibility of
				//"jumping" on the UI thread here and a selection change occuring
				if (e.getSource() == subject()) {
					updatePane(e.getPropertyName());
				}
			}
		};
	}

	/**
	 * Creates a new button using the given information.
	 *
	 * @param parent The parent container
	 * @param buttonText The button's text
	 * @param buttonAction The action to be invoked when the button is pressed
	 * @return The newly created <code>Button</code>
	 *
	 * @category Layout
	 */
	protected final Button buildButton(Composite container,
	                                   String text,
	                                   final Runnable buttonAction) {

		return this.buildButton(container, text, null, buttonAction);
	}

	/**
	 * Creates a new button using the given information.
	 *
	 * @param parent The parent container
	 * @param buttonText The button's text
	 * @param helpId The topic help ID to be registered for the new check box
	 * @param buttonAction The action to be invoked when the button is pressed
	 * @return The newly created <code>Button</code>
	 *
	 * @category Layout
	 */
	protected final Button buildButton(Composite container,
	                                   String text,
	                                   String helpId,
	                                   final Runnable buttonAction) {

		Button button = this.widgetFactory.createButton(container, text);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SWTUtil.asyncExec(buttonAction);
			}
		});

		if (helpId != null) {
			helpSystem().setHelp(button, helpId);
		}

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = false;
		gridData.horizontalAlignment       = GridData.FILL;
		button.setLayoutData(gridData);

		return button;
	}

	/**
	 * Creates a new non-editable <code>CCombo</code>.
	 *
	 * @param container The parent container
	 * @return The newly created <code>Combo</code>
	 *
	 * @category Layout
	 */
	protected final CCombo buildCCombo(Composite container) {

		CCombo combo = this.widgetFactory.createCCombo(container);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return combo;
	}

	/**
	 * Creates a new non-editable <code>CCombo</code>.
	 *
	 * @param container The parent container
	 * @param listHolder The <code>ListValueHolder</code>
	 * @param selectedItemHolder The holder of the selected item
	 * @return The newly created <code>CCombo</code>
	 *
	 * @category Layout
	 */
	protected final <V> CCombo buildCCombo(Composite container,
	                                       ListValueModel<V> listHolder,
	                                       WritablePropertyValueModel<V> selectedItemHolder) {

		return this.buildCCombo(
			container,
			listHolder,
			selectedItemHolder,
			StringConverter.Default.<V>instance()
		);
	}

	/**
	 * Creates a new non-editable <code>CCombo</code>.
	 *
	 * @param container The parent container
	 * @param listHolder The <code>ListValueHolder</code>
	 * @param selectedItemHolder The holder of the selected item
	 * @param stringConverter The converter responsible to transform each item
	 * into a string representation
	 * @return The newly created <code>CCombo</code>
	 *
	 * @category Layout
	 */
	protected final <V> CCombo buildCCombo(Composite container,
	                                       ListValueModel<V> listHolder,
	                                       WritablePropertyValueModel<V> selectedItemHolder,
	                                       StringConverter<V> stringConverter) {

		CCombo combo = this.buildCCombo(container);

		CComboModelAdapter.adapt(
			listHolder,
			selectedItemHolder,
			combo,
			stringConverter
		);

		return combo;
	}

	/**
	 * Creates a new <code>ComboViewer</code> using a <code>CCombo</code>.
	 *
	 * @param container The parent container
	 * @param labelProvider The provider responsible to convert the combo's items
	 * into human readable strings
	 * @return The newly created <code>ComboViewer</code>
	 *
	 * @category Layout
	 */
	protected final ComboViewer buildCComboViewer(Composite container,
	                                              IBaseLabelProvider labelProvider) {

		CCombo combo = this.buildCCombo(container);
		ComboViewer viewer = new ComboViewer(combo);
		viewer.setLabelProvider(labelProvider);
		return viewer;
	}

	/**
	 * Creates a new check box using the given information.
	 *
	 * @param parent The parent container
	 * @param buttonText The button's text
	 * @param booleanHolder The holder of the selection state
	 * @return The newly created <code>Button</code>
	 *
	 * @category Layout
	 */
	protected final Button buildCheckBox(Composite parent,
	                                     String buttonText,
	                                     WritablePropertyValueModel<Boolean> booleanHolder) {

		return this.buildCheckBox(parent, buttonText, booleanHolder, null);
	}

	/**
	 * Creates a new check box using the given information.
	 *
	 * @param parent The parent container
	 * @param buttonText The button's text
	 * @param booleanHolder The holder of the selection state
	 * @param helpId The topic help ID to be registered for the new check box
	 * @return The newly created <code>Button</code>
	 *
	 * @category Layout
	 */
	protected final Button buildCheckBox(Composite parent,
	                                     String buttonText,
	                                     WritablePropertyValueModel<Boolean> booleanHolder,
	                                     String helpId) {

		return this.buildToggleButton(
			parent,
			buttonText,
			booleanHolder,
			helpId,
			SWT.CHECK
		);
	}

	/**
	 * Creates a new <code>Section</code> that can be collapsed. A sub-pane is
	 * automatically added as its client and is the returned <code>Composite</code>.
	 *
	 * @param container The container of the new widget
	 * @param sectionText The text of the new section
	 * @return The <code>Section</code>'s sub-pane
	 *
	 * @category Layout
	 */
	protected final Composite buildCollapsableSection(Composite container,
	                                                  String sectionText) {

		return this.buildCollapsableSection(
			container,
			sectionText,
			new SimplePropertyValueModel<Boolean>(Boolean.FALSE)
		);
	}

	/**
	 * Creates a new <code>Section</code>. A sub-pane is automatically added as
	 * its client and is the returned <code>Composite</code>.
	 *
	 * @param container The container of the new widget
	 * @param sectionText The text of the new section
	 * @param type The type of section to create
	 * @param expandedStateHolder The holder of the boolean that will dictate
	 * when to expand or collapse the section
	 * @return The <code>Section</code>'s sub-pane
	 *
	 * @category Layout
	 */
	private Composite buildCollapsableSection(Composite container,
	                                          String sectionText,
	                                          int type,
	                                          PropertyValueModel<Boolean> expandedStateHolder) {

		Composite subPane = this.buildSection(
			container,
			sectionText,
			ExpandableComposite.TWISTIE | type
		);

		Section section = (Section) subPane.getParent();

		expandedStateHolder.addPropertyChangeListener(
			PropertyValueModel.VALUE,
			buildExpandedStateChangeListener(section)
		);

		section.setExpanded(
			expandedStateHolder.getValue() != null ? expandedStateHolder.getValue() : true
		);

		return subPane;
	}

	/**
	 * Creates a new <code>Section</code>. A sub-pane is automatically added as
	 * its client and is the returned <code>Composite</code>.
	 *
	 * @param container The container of the new widget
	 * @param sectionText The text of the new section
	 * @param expandedStateHolder The holder of the boolean that will dictate
	 * when to expand or collapse the section
	 * @return The <code>Section</code>'s sub-pane
	 *
	 * @category Layout
	 */
	protected final Composite buildCollapsableSection(Composite container,
	                                                  String sectionText,
	                                                  PropertyValueModel<Boolean> expandedStateHolder) {

		return this.buildCollapsableSection(
			container,
			sectionText,
			ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE,
			expandedStateHolder
		);
	}

	/**
	 * Creates a new <code>Section</code>. A sub-pane is automatically added as
	 * its client which can be typed cast directly as a <code>Composite</code>.
	 *
	 * @param container The container of the new widget
	 * @param sectionText The text of the new section
	 * @param expandedStateHolder The holder of the boolean that will dictate
	 * when to expand or collapse the section
	 * @return The <code>Section</code>'s sub-pane
	 *
	 * @category Layout
	 */
	protected final Composite buildCollapsableSubSection(Composite container,
	                                          String sectionText,
	                                          PropertyValueModel<Boolean> expandedStateHolder) {

		return this.buildCollapsableSection(
			container,
			sectionText,
			SWT.NULL,
			expandedStateHolder
		);
	}

	/**
	 * Creates a new non-editable <code>Combo</code>.
	 *
	 * @param container The parent container
	 * @return The newly created <code>Combo</code>
	 *
	 * @category Layout
	 */
	protected final Combo buildCombo(Composite container) {

		Combo combo = this.widgetFactory.createCombo(container);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return combo;
	}

	/**
	 * Creates a new non-editable <code>Combo</code>.
	 *
	 * @param container The parent container
	 * @param listHolder The <code>ListValueHolder</code>
	 * @param selectedItemHolder The holder of the selected item
	 * @return The newly created <code>Combo</code>
	 *
	 * @category Layout
	 */
	protected final <V> Combo buildCombo(Composite container,
	                                     ListValueModel<V> listHolder,
	                                     WritablePropertyValueModel<V> selectedItemHolder) {

		return this.buildCombo(
			container,
			listHolder,
			selectedItemHolder,
			StringConverter.Default.<V>instance()
		);
	}

	/**
	 * Creates a new non-editable <code>Combo</code>.
	 *
	 * @param container The parent container
	 * @param listHolder The <code>ListValueHolder</code>
	 * @param selectedItemHolder The holder of the selected item
	 * @param stringConverter The converter responsible to transform each item
	 * into a string representation
	 * @return The newly created <code>Combo</code>
	 *
	 * @category Layout
	 */
	protected final <V> Combo buildCombo(Composite container,
	                                     ListValueModel<V> listHolder,
	                                     WritablePropertyValueModel<V> selectedItemHolder,
	                                     StringConverter<V> stringConverter) {

		Combo combo = this.buildCombo(container);

		ComboModelAdapter.adapt(
			listHolder,
			selectedItemHolder,
			combo,
			stringConverter
		);

		return combo;
	}

	/**
	 * Creates a new <code>ComboViewer</code> using a <code>Combo</code>.
	 *
	 * @param container The parent container
	 * @param labelProvider The provider responsible to convert the combo's items
	 * into human readable strings
	 * @return The newly created <code>ComboViewer</code>
	 *
	 * @category Layout
	 */
	protected final ComboViewer buildComboViewer(Composite container,
	                                             IBaseLabelProvider labelProvider) {

		Combo combo = this.buildCombo(container);
		ComboViewer viewer = new ComboViewer(combo);
		viewer.setLabelProvider(labelProvider);
		return viewer;
	}

	/**
	 * Creates the main container of this pane. The layout and layout data are
	 * automatically set.
	 *
	 * @param parent The parent container
	 * @return The newly created <code>Composite</code> that will holds all the
	 * widgets created by this pane through {@link #initializeLayout(Composite)}
	 *
	 * @category Layout
	 */
	protected Composite buildContainer(Composite parent) {
		return this.buildSubPane(parent);
	}

	/**
	 * Creates a new editable <code>CCombo</code>.
	 *
	 * @param container The parent container
	 * @return The newly created <code>CCombo</code>
	 *
	 * @category Layout
	 */
	protected final CCombo buildEditableCCombo(Composite container) {

		CCombo combo = this.widgetFactory.createEditableCCombo(container);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return combo;
	}

	/**
	 * Creates a new editable <code>CCombo</code>.
	 *
	 * @param container The parent container
	 * @param listHolder The <code>ListValueHolder</code>
	 * @param selectedItemHolder The holder of the selected item
	 * @return The newly created <code>CCombo</code>
	 *
	 * @category Layout
	 */
	protected final <V> CCombo buildEditableCCombo(Composite container,
	                                               ListValueModel<V> listHolder,
	                                               WritablePropertyValueModel<V> selectedItemHolder) {

		return this.buildEditableCCombo(
			container,
			listHolder,
			selectedItemHolder,
			StringConverter.Default.<V>instance()
		);
	}

	/**
	 * Creates a new editable <code>CCombo</code>.
	 *
	 * @param container The parent container
	 * @param listHolder The <code>ListValueHolder</code>
	 * @param selectedItemHolder The holder of the selected item
	 * @param stringConverter The converter responsible to transform each item
	 * into a string representation
	 * @return The newly created <code>CCombo</code>
	 *
	 * @category Layout
	 */
	protected final <V> CCombo buildEditableCCombo(Composite container,
	                                               ListValueModel<V> listHolder,
	                                               WritablePropertyValueModel<V> selectedItemHolder,
	                                               StringConverter<V> stringConverter) {

		CCombo combo = this.buildEditableCCombo(container);

		CComboModelAdapter.adapt(
			listHolder,
			selectedItemHolder,
			combo,
			stringConverter
		);

		return combo;
	}

	/**
	 * Creates a new editable <code>ComboViewer</code> using a <code>CCombo</code>.
	 *
	 * @param container The parent container
	 * @param labelProvider The provider responsible to convert the combo's items
	 * into human readable strings
	 * @return The newly created <code>ComboViewer</code>
	 *
	 * @category Layout
	 */
	protected final ComboViewer buildEditableCComboViewer(Composite container,
	                                                      IBaseLabelProvider labelProvider) {

		CCombo combo = this.buildEditableCCombo(container);
		ComboViewer viewer = new ComboViewer(combo);
		viewer.setLabelProvider(labelProvider);
		return viewer;
	}

	/**
	 * Creates a new editable <code>Combo</code>.
	 *
	 * @param container The parent container
	 * @return The newly created <code>Combo</code>
	 *
	 * @category Layout
	 */
	protected final Combo buildEditableCombo(Composite container) {

		Combo combo = this.widgetFactory.createEditableCombo(container);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return combo;
	}

	/**
	 * Creates a new editable <code>Combo</code>.
	 *
	 * @param container The parent container
	 * @param listHolder The <code>ListValueHolder</code>
	 * @param selectedItemHolder The holder of the selected item
	 * @return The newly created <code>Combo</code>
	 *
	 * @category Layout
	 */
	protected final <V> Combo buildEditableCombo(Composite container,
	                                             ListValueModel<V> listHolder,
	                                             WritablePropertyValueModel<V> selectedItemHolder) {

		return this.buildEditableCombo(
			container,
			listHolder,
			selectedItemHolder,
			StringConverter.Default.<V>instance()
		);
	}

	/**
	 * Creates a new editable <code>Combo</code>.
	 *
	 * @param container The parent container
	 * @param listHolder The <code>ListValueHolder</code>
	 * @param selectedItemHolder The holder of the selected item
	 * @param stringConverter The converter responsible to transform each item
	 * into a string representation
	 * @return The newly created <code>Combo</code>
	 *
	 * @category Layout
	 */
	protected final <V> Combo buildEditableCombo(Composite container,
	                                             ListValueModel<V> listHolder,
	                                             WritablePropertyValueModel<V> selectedItemHolder,
	                                             StringConverter<V> stringConverter) {

		Combo combo = this.buildEditableCombo(container);

		ComboModelAdapter.adapt(
			listHolder,
			selectedItemHolder,
			combo,
			stringConverter
		);

		return combo;
	}

	/**
	 * Creates a new editable <code>ComboViewer</code> using a <code>Combo</code>.
	 *
	 * @param container The parent container
	 * @param labelProvider The provider responsible to convert the combo's items
	 * into human readable strings
	 * @return The newly created <code>ComboViewer</code>
	 *
	 * @category Layout
	 */
	protected final ComboViewer buildEditableComboViewer(Composite container,
	                                                     IBaseLabelProvider labelProvider) {

		Combo combo = this.buildEditableCombo(container);
		ComboViewer viewer = new ComboViewer(combo);
		viewer.setLabelProvider(labelProvider);
		return viewer;
	}

	private PropertyChangeListener buildExpandedStateChangeListener(final Section section) {
		return new SWTPropertyChangeListenerWrapper(buildExpandedStateChangeListener_(section));
	}

	private PropertyChangeListener buildExpandedStateChangeListener_(final Section section) {
		return new PropertyChangeListener() {
			public void propertyChanged(final PropertyChangeEvent e) {
				Boolean value = (Boolean) e.getNewValue();
				if (value == null) {
					value = Boolean.TRUE;
				}
				section.setExpanded(value);
			}
		};
	}

	/**
	 * Creates a new <code>Hyperlink</code> that will invoked the given
	 * <code>Runnable</code> when selected. The given action is always invoked
	 * from the UI thread.
	 *
	 * @param parent The parent container
	 * @param text The hyperlink's text
	 * @param hyperLinkAction The action to be invoked when the link was selected
	 * return The newly created <code>Hyperlink</code>
	 *
	 * @category Layout
	 */
	protected final Hyperlink buildHyperLink(Composite parent,
	                                         String text,
	                                         final Runnable hyperLinkAction) {

		Hyperlink link = this.widgetFactory.createHyperlink(parent, text);
		this.widgets.add(link);

		link.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {

				Hyperlink hyperLink = (Hyperlink) e.widget;

				if (hyperLink.isEnabled()) {
					SWTUtil.asyncExec(hyperLinkAction);
				}
			}
		});

		return link;
	}

	/**
	 * Creates a new lable using the given information.
	 *
	 * @param parent The parent container
	 * @param labelText The label's text
	 *
	 * @category Layout
	 */
	protected final Label buildLabel(Composite container,
	                                 String labelText) {

		return this.widgetFactory.createLabel(container, labelText);
	}

	/**
	 * Creates a new container that will have the given center control labeled
	 * with the given label.
	 *
	 * @param container The parent container
	 * @param labelText The text of the label
	 * @param listHolder The <code>ListValueHolder</code>
	 * @param selectedItemHolder The holder of the selected item
	 * @param helpId The topic help ID to be registered for the given center
	 * compositer
	 * @return The newly created <code>CCombo</code>
	 *
	 * @category Layout
	 */
	protected final <V> CCombo buildLabeledCCombo(Composite container,
	                                              String labelText,
	                                              ListValueModel<V> listHolder,
	                                              WritablePropertyValueModel<V> selectedItemHolder,
	                                              String helpId) {

		return this.buildLabeledCCombo(
			container,
			labelText,
			listHolder,
			selectedItemHolder,
			StringConverter.Default.<V>instance(),
			helpId
		);
	}

	/**
	 * Creates a new container that will have a non-editable combo labeled with
	 * the given text.
	 *
	 * @param container The parent container
	 * @param labelText The text of the label
	 * @param listHolder The <code>ListValueHolder</code>
	 * @param selectedItemHolder The holder of the selected item
	 * @param rightControl The control shown to the right of the main widget
	 * @param helpId The topic help ID to be registered for the given center
	 * compositer
	 * @return The newly created <code>CCombo</code>
	 *
	 * @category Layout
	 */
	protected final <V> CCombo buildLabeledCCombo(Composite container,
	                                              String labelText,
	                                              ListValueModel<V> listHolder,
	                                              WritablePropertyValueModel<V> selectedItemHolder,
	                                              StringConverter<V> stringConverter,
	                                              Control rightControl,
	                                              String helpId) {

		CCombo combo = this.buildCCombo(
			container,
			listHolder,
			selectedItemHolder,
			stringConverter
		);

		this.buildLabeledComposite(
			container,
			labelText,
			(combo.getParent() != container) ? combo.getParent() : combo,
			rightControl,
			helpId
		);

		return combo;
	}

	/**
	 * Creates a new container that will have a non-editable combo labeled with
	 * the given text.
	 *
	 * @param container The parent container
	 * @param labelText The text of the label
	 * @param listHolder The <code>ListValueHolder</code>
	 * @param selectedItemHolder The holder of the selected item
	 * @param rightControl The control shown to the right of the main widget
	 * @param helpId The topic help ID to be registered for the given center
	 * compositer
	 * @return The newly created <code>CCombo</code>
	 *
	 * @category Layout
	 */
	protected final <V> CCombo buildLabeledCCombo(Composite container,
	                                              String labelText,
	                                              ListValueModel<V> listHolder,
	                                              WritablePropertyValueModel<V> selectedItemHolder,
	                                              StringConverter<V> stringConverter,
	                                              String helpId) {

		return this.buildLabeledCCombo(
			container,
			labelText,
			listHolder,
			selectedItemHolder,
			stringConverter,
			null,
			helpId
		);
	}

	/**
	 * Creates a new container that will have a non-editable combo labeled with
	 * the given text.
	 *
	 * @param container The parent container
	 * @param labelText The text of the label
	 * @param comboListener The listener that will be notified when the selection
	 * changes
	 * @param rightControl The control shown to the right of the main widget
	 * @param helpId The topic help ID to be registered for the given center
	 * compositer
	 * @return The newly created <code>CCombo</code>
	 *
	 * @category Layout
	 */
	protected final CCombo buildLabeledCCombo(Composite container,
	                                          String labelText,
	                                          ModifyListener comboListener,
	                                          Control rightControl,
	                                          String helpId) {

		CCombo combo = this.buildCCombo(container);
		combo.addModifyListener(comboListener);

		this.buildLabeledComposite(
			container,
			labelText,
			(combo.getParent() != container) ? combo.getParent() : combo,
			rightControl,
			helpId
		);

		return combo;
	}

	/**
	 * Creates a new container that will have the given center control labeled
	 * with the given label.
	 *
	 * @param container The parent container
	 * @param leftControl The widget shown to the left of the main widget
	 * @param centerControl The main widget
	 * @param helpId The topic help ID to be registered for the given center
	 * compositer
	 * @return The newly created <code>CCombo</code>
	 *
	 * @category Layout
	 */
	protected final CCombo buildLabeledCCombo(Composite container,
	                                          String labelText,
	                                          ModifyListener comboListener,
	                                          String helpId) {

		return this.buildLabeledCCombo(
			container,
			labelText,
			comboListener,
			null,
			helpId
		);
	}

	/**
	 * Creates a new container that will have a non-editable combo labeled with
	 * the given text.
	 *
	 * @param container The parent container
	 * @param labelText The text of the label
	 * @param listHolder The <code>ListValueHolder</code>
	 * @param selectedItemHolder The holder of the selected item
	 * @param rightControl The control shown to the right of the main widget
	 * @param helpId The topic help ID to be registered for the given center
	 * compositer
	 * @return The container of the label and the given center control
	 *
	 * @category Layout
	 */
	protected final <V> Combo buildLabeledCombo(Composite container,
	                                            String labelText,
	                                            ListValueModel<V> listHolder,
	                                            WritablePropertyValueModel<V> selectedItemHolder,
	                                            Control rightControl,
	                                            String helpId) {

		return this.buildLabeledCombo(
			container,
			labelText,
			listHolder,
			selectedItemHolder,
			StringConverter.Default.<V>instance(),
			rightControl,
			helpId
		);
	}

	/**
	 * Creates a new container that will have the given center control labeled
	 * with the given label.
	 *
	 * @param container The parent container
	 * @param labelText The text of the label
	 * @param listHolder The <code>ListValueHolder</code>
	 * @param selectedItemHolder The holder of the selected item
	 * @param helpId The topic help ID to be registered for the given center
	 * compositer
	 * @return The container of the label and the given center control
	 *
	 * @category Layout
	 */
	protected final <V> Combo buildLabeledCombo(Composite container,
	                                            String labelText,
	                                            ListValueModel<V> listHolder,
	                                            WritablePropertyValueModel<V> selectedItemHolder,
	                                            String helpId) {

		return this.buildLabeledCombo(
			container,
			labelText,
			listHolder,
			selectedItemHolder,
			StringConverter.Default.<V>instance(),
			helpId
		);
	}

	/**
	 * Creates a new container that will have a non-editable combo labeled with
	 * the given text.
	 *
	 * @param container The parent container
	 * @param labelText The text of the label
	 * @param listHolder The <code>ListValueHolder</code>
	 * @param selectedItemHolder The holder of the selected item
	 * @param rightControl The control shown to the right of the main widget
	 * @param helpId The topic help ID to be registered for the given center
	 * compositer
	 * @return The container of the label and the given center control
	 *
	 * @category Layout
	 */
	protected final <V> Combo buildLabeledCombo(Composite container,
	                                            String labelText,
	                                            ListValueModel<V> listHolder,
	                                            WritablePropertyValueModel<V> selectedItemHolder,
	                                            StringConverter<V> stringConverter,
	                                            Control rightControl,
	                                            String helpId) {

		Combo combo = this.buildCombo(
			container,
			listHolder,
			selectedItemHolder,
			stringConverter
		);

		this.buildLabeledComposite(
			container,
			labelText,
			(combo.getParent() != container) ? combo.getParent() : combo,
			rightControl,
			helpId
		);

		return combo;
	}

	/**
	 * Creates a new container that will have a non-editable combo labeled with
	 * the given text.
	 *
	 * @param container The parent container
	 * @param labelText The text of the label
	 * @param listHolder The <code>ListValueHolder</code>
	 * @param selectedItemHolder The holder of the selected item
	 * @param helpId The topic help ID to be registered for the given center
	 * compositer
	 * @return The container of the label and the given center control
	 *
	 * @category Layout
	 */
	protected final <V> Combo buildLabeledCombo(Composite container,
	                                            String labelText,
	                                            ListValueModel<V> listHolder,
	                                            WritablePropertyValueModel<V> selectedItemHolder,
	                                            StringConverter<V> stringConverter,
	                                            String helpId) {

		return this.buildLabeledCombo(
			container,
			labelText,
			listHolder,
			selectedItemHolder,
			stringConverter,
			null,
			helpId
		);
	}

	/**
	 * Creates a new container that will have a non-editable combo labeled with
	 * the given text.
	 *
	 * @param container The parent container
	 * @param labelText The text of the label
	 * @param comboListener The listener that will be notified when the selection
	 * changes
	 * @param rightControl The control shown to the right of the main widget
	 * @param helpId The topic help ID to be registered for the given center
	 * compositer
	 * @return The container of the label and the given center control
	 *
	 * @category Layout
	 */
	protected final Combo buildLabeledCombo(Composite container,
	                                        String labelText,
	                                        ModifyListener comboListener,
	                                        Control rightControl,
	                                        String helpId) {

		Combo combo = this.buildCombo(container);
		combo.addModifyListener(comboListener);

		this.buildLabeledComposite(
			container,
			labelText,
			(combo.getParent() != container) ? combo.getParent() : combo,
			rightControl,
			helpId
		);

		return combo;
	}

	/**
	 * Creates a new container that will have the given center control labeled
	 * with the given label.
	 *
	 * @param container The parent container
	 * @param leftControl The widget shown to the left of the main widget
	 * @param centerControl The main widget
	 * @param helpId The topic help ID to be registered for the given center
	 * compositer
	 * @return The container of the label and the given center control
	 *
	 * @category Layout
	 */
	protected final Combo buildLabeledCombo(Composite container,
	                                        String labelText,
	                                        ModifyListener comboListener,
	                                        String helpId) {

		return this.buildLabeledCombo(
			container,
			labelText,
			comboListener,
			null,
			helpId
		);
	}

	/**
	 * Creates a new container that will have the given center control labeled
	 * with the given label.
	 *
	 * @param container The parent container
	 * @param leftControl The widget shown to the left of the main widget
	 * @param centerControl The main widget
	 * @param rightControl The control shown to the right of the main widget
	 * @param helpId The topic help ID to be registered for the given center
	 * compositer
	 * @return The container of the label and the given center control
	 *
	 * @category Layout
	 */
	protected final Composite buildLabeledComposite(Composite container,
	                                                Control leftControl,
	                                                Control centerControl,
	                                                Control rightControl,
	                                                String helpId) {

		// Container for the label and main composite
		container = this.buildSubPane(container, 3, 5, 0, 0, 0);

		// Left control
		GridData gridData = new GridData();
		gridData.horizontalAlignment       = GridData.BEGINNING;
		gridData.grabExcessHorizontalSpace = false;
		leftControl.setLayoutData(gridData);

		// Re-parent the left control to the new sub pane
		leftControl.setParent(container);
		this.leftControlAligner.add(leftControl);
		this.widgets.add(leftControl);

		// Center control
		centerControl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.widgets.add(centerControl);

		// Re-parent the center control to the new sub pane
		centerControl.setParent(container);

		// Register the help id for the center control
		if (helpId != null) {
			helpSystem().setHelp(centerControl, helpId);
		}

		// Right control
		if (rightControl == null) {
			Composite spacer = this.buildPane(container);
			spacer.setLayout(this.buildSpacerLayout());
			rightControl = spacer;
		}
		else {
			rightControl.setParent(container);

			// Register the help id for the right control
			if (helpId != null) {
				helpSystem().setHelp(rightControl, helpId);
			}
		}

		gridData = new GridData();
		gridData.horizontalAlignment       = GridData.FILL_HORIZONTAL;
		gridData.grabExcessHorizontalSpace = false;

		rightControl.setLayoutData(gridData);
		this.rightControlAligner.add(rightControl);
		this.widgets.add(rightControl);

		return container;
	}

	/**
	 * Creates a new container that will have the given center control labeled
	 * with the given label.
	 *
	 * @param container The parent container
	 * @param label The label used to describe the center control
	 * @param centerControl The main widget
	 * @param helpId The topic help ID to be registered for the given center
	 * control
	 * @return The container of the label and the given center control
	 *
	 * @category Layout
	 */
	protected final Composite buildLabeledComposite(Composite container,
	                                                Control label,
	                                                Control centerControl,
	                                                String helpId) {

		return this.buildLabeledComposite(
			container,
			label,
			centerControl,
			null,
			helpId
		);
	}

	/**
	 * Creates a new container that will have the given center control labeled
	 * with the given label.
	 *
	 * @param container The parent container
	 * @param label The label used to describe the center control
	 * @param centerControl The main widget
	 * @return The container of the label and the given center control
	 *
	 * @category Layout
	 */
	protected final Composite buildLabeledComposite(Composite container,
	                                                Label label,
	                                                Control centerControl) {

		return this.buildLabeledComposite(
			container,
			label,
			centerControl,
			null
		);
	}

	/**
	 * Creates a new container that will have the given center composite labeled
	 * with the given label text.
	 *
	 * @param container The parent container
	 * @param labelText The text to label the main composite
	 * @param centerPane The main widget
	 * @param rightControl The control shown to the right of the main widget
	 * @param helpId The topic help ID to be registered for the given center
	 * compositer
	 * @return The container of the label and the given center control
	 *
	 * @category Layout
	 */
	protected final Composite buildLabeledComposite(Composite container,
	                                                String labelText,
	                                                AbstractPane<?> centerPane,
	                                                Control rightCentrol,
	                                                String helpId) {

		return this.buildLabeledComposite(
			container,
			labelText,
			centerPane.getControl(),
			rightCentrol,
			helpId
		);
	}

	/**
	 * Creates a new container that will have the given center composite labeled
	 * with the given label text.
	 *
	 * @param container The parent container
	 * @param labelText The text to label the main composite
	 * @param centerPane The main widget
	 * @param helpId The topic help ID to be registered for the given center
	 * compositer
	 * @return The container of the label and the given center control
	 *
	 * @category Layout
	 */
	protected final Composite buildLabeledComposite(Composite container,
	                                                String labelText,
	                                                AbstractPane<?> centerPane,
	                                                String helpId) {

		return this.buildLabeledComposite(
			container,
			labelText,
			centerPane.getControl(),
			helpId
		);
	}

	/**
	 * Creates a new container that will have the given center composite labeled
	 * with the given label text.
	 *
	 * @param container The parent container
	 * @param labelText The text to label the main composite
	 * @param centerControl The main widget
	 * @return The container of the label and the given center control
	 *
	 * @category Layout
	 */
	protected final Composite buildLabeledComposite(Composite container,
	                                                String labelText,
	                                                Control centerControl) {


		return this.buildLabeledComposite(
			container,
			labelText,
			centerControl,
			null,
			null
		);
	}

	/**
	 * Creates a new container that will have the given center composite labeled
	 * with the given label text.
	 *
	 * @param container The parent container
	 * @param labelText The text to label the main composite
	 * @param centerControl The main widget
	 * @param rightControl The control shown to the right of the main widget
	 * @return The container of the label and the given center control
	 *
	 * @category Layout
	 */
	protected final Composite buildLabeledComposite(Composite container,
	                                                String labelText,
	                                                Control centerControl,
	                                                Control rightControl) {


		return this.buildLabeledComposite(
			container,
			labelText,
			centerControl,
			rightControl,
			null
		);
	}

	/**
	 * Creates a new container that will have the given center composite labeled
	 * with the given label text.
	 *
	 * @param container The parent container
	 * @param labelText The text to label the main composite
	 * @param centerControl The main widget
	 * @param rightControl The control shown to the right of the main widget
	 * @param helpId The topic help ID to be registered for the given center
	 * compositer
	 * @return The container of the label and the given center control
	 *
	 * @category Layout
	 */
	protected final Composite buildLabeledComposite(Composite container,
	                                                String labelText,
	                                                Control centerControl,
	                                                Control rightCentrol,
	                                                String helpId) {

		return this.buildLabeledComposite(
			container,
			this.buildLabel(container, labelText),
			centerControl,
			rightCentrol,
			helpId
		);
	}

	/**
	 * Creates a new container that will have the given center composite labeled
	 * with the given label text.
	 *
	 * @param container The parent container
	 * @param labelText The text to label the main composite
	 * @param centerControl The main widget
	 * @param helpId The topic help ID to be registered for the given center
	 * compositer
	 * @return The container of the label and the given center control
	 *
	 * @category Layout
	 */
	protected final Composite buildLabeledComposite(Composite container,
	                                                String labelText,
	                                                Control centerControl,
	                                                String helpId) {

		Label label = this.buildLabel(container, labelText);

		return this.buildLabeledComposite(
			container,
			label,
			centerControl,
			helpId
		);
	}

	/**
	 * Creates a new container that will have the given center control labeled
	 * with the given label.
	 *
	 * @param container The parent container
	 * @param labelText The text of the label
	 * @param listHolder The <code>ListValueHolder</code>
	 * @param selectedItemHolder The holder of the selected item
	 * @param rightControl The control shown to the right of the main widget
	 * @param helpId The topic help ID to be registered for the given center
	 * compositer
	 * @return The newly created <code>CCombo</code>
	 *
	 * @category Layout
	 */
	protected final <V> CCombo buildLabeledEditableCCombo(Composite container,
	                                                      String labelText,
	                                                      ListValueModel<V> listHolder,
	                                                      WritablePropertyValueModel<V> selectedItemHolder,
	                                                      Control rightControl,
	                                                      String helpId) {

		return this.buildLabeledEditableCCombo(
			container,
			labelText,
			listHolder,
			selectedItemHolder,
			StringConverter.Default.<V>instance(),
			rightControl,
			helpId
		);
	}

	/**
	 * Creates a new container that will have an editable combo labeled with the
	 * given text.
	 *
	 * @param container The parent container
	 * @param labelText The text of the label
	 * @param listHolder The <code>ListValueHolder</code>
	 * @param selectedItemHolder The holder of the selected item
	 * @param helpId The topic help ID to be registered for the given center
	 * compositer
	 * @return The newly created <code>CCombo</code>
	 *
	 * @category Layout
	 */
	protected final <V> CCombo buildLabeledEditableCCombo(Composite container,
	                                                      String labelText,
	                                                      ListValueModel<V> listHolder,
	                                                      WritablePropertyValueModel<V> selectedItemHolder,
	                                                      String helpId) {

		return this.buildLabeledEditableCCombo(
			container,
			labelText,
			listHolder,
			selectedItemHolder,
			StringConverter.Default.<V>instance(),
			null,
			helpId
		);
	}

	/**
	 * Creates a new container that will have the given center control labeled
	 * with the given label.
	 *
	 * @param container The parent container
	 * @param labelText The text of the label
	 * @param listHolder The <code>ListValueHolder</code>
	 * @param selectedItemHolder The holder of the selected item
	 * @param stringConverter The converter responsible to transform each item
	 * into a string representation
	 * @param rightControl The control shown to the right of the main widget
	 * @param helpId The topic help ID to be registered for the given center
	 * compositer
	 * @return The newly created <code>CCombo</code>
	 *
	 * @category Layout
	 */
	protected final <V> CCombo buildLabeledEditableCCombo(Composite container,
	                                                      String labelText,
	                                                      ListValueModel<V> listHolder,
	                                                      WritablePropertyValueModel<V> selectedItemHolder,
	                                                      StringConverter<V> stringConverter,
	                                                      Control rightControl,
	                                                      String helpId) {

		CCombo combo = this.buildEditableCCombo(
			container,
			listHolder,
			selectedItemHolder,
			stringConverter
		);

		this.buildLabeledComposite(
			container,
			labelText,
			(combo.getParent() != container) ? combo.getParent() : combo,
			rightControl,
			helpId
		);

		return combo;
	}

	/**
	 * Creates a new container that will have an editable combo labeled with the
	 * given text.
	 *
	 * @param container The parent container
	 * @param labelText The text of the label
	 * @param listHolder The <code>ListValueHolder</code>
	 * @param selectedItemHolder The holder of the selected item
	 * @param stringConverter The converter responsible to transform each item
	 * into a string representation
	 * @param helpId The topic help ID to be registered for the given center
	 * compositer
	 * @return The newly created <code>CCombo</code>
	 *
	 * @category Layout
	 */
	protected final <V> CCombo buildLabeledEditableCCombo(Composite container,
	                                                      String labelText,
	                                                      ListValueModel<V> listHolder,
	                                                      WritablePropertyValueModel<V> selectedItemHolder,
	                                                      StringConverter<V> stringConverter,
	                                                      String helpId) {

		return this.buildLabeledEditableCCombo(
			container,
			labelText,
			listHolder,
			selectedItemHolder,
			stringConverter,
			null,
			helpId
		);
	}

	/**
	 * Creates a new container that will have the given center control labeled
	 * with the given label.
	 *
	 * @param container The parent container
	 * @param leftControl The widget shown to the left of the main widget
	 * @param centerControl The main widget
	 * @param rightControl The control shown to the right of the main widget
	 * @param helpId The topic help ID to be registered for the given center
	 * compositer
	 * @return The newly created <code>CCombo</code>
	 *
	 * @category Layout
	 */
	protected final CCombo buildLabeledEditableCCombo(Composite container,
	                                                  String labelText,
	                                                  ModifyListener comboListener,
	                                                  Control rightControl,
	                                                  String helpId) {

		CCombo combo = this.buildEditableCCombo(container);
		combo.addModifyListener(comboListener);

		this.buildLabeledComposite(
			container,
			labelText,
			(combo.getParent() != container) ? combo.getParent() : combo,
			rightControl,
			helpId
		);

		return combo;
	}

	/**
	 * Creates a new container that will have an editable combo labeled with the
	 * given text.
	 *
	 * @param container The parent container
	 * @param labelText The text of the label
	 * @param comboListener The listener that will be notified when the selection
	 * changes
	 * @param helpId The topic help ID to be registered for the given center
	 * compositer
	 * @return The newly created <code>CCombo</code>
	 *
	 * @category Layout
	 */
	protected final CCombo buildLabeledEditableCCombo(Composite container,
	                                                  String labelText,
	                                                  ModifyListener comboListener,
	                                                  String helpId) {

		return this.buildLabeledEditableCCombo(
			container,
			labelText,
			comboListener,
			null,
			helpId
		);
	}

	/**
	 * Creates a new container that will have the given center control labeled
	 * with the given label.
	 *
	 * @param container The parent container
	 * @param leftControl The widget shown to the left of the main widget
	 * @param centerControl The main widget
	 * @param labelProvider The provider responsible to convert the combo's items
	 * into human readable strings
	 * @param rightControl The control shown to the right of the main widget
	 * @param helpId The topic help ID to be registered for the given center
	 * compositer
	 * @return The newly created <code>CCombo</code>
	 *
	 * @category Layout
	 */
	protected final CCombo buildLabeledEditableCComboViewer(Composite container,
	                                                        String labelText,
	                                                        ModifyListener comboListener,
	                                                        ILabelProvider labelProvider,
	                                                        Control rightControl,
	                                                        String helpId) {

		ComboViewer comboViewer = this.buildEditableCComboViewer(
			container,
			labelProvider
		);

		CCombo combo = comboViewer.getCCombo();
		combo.addModifyListener(comboListener);

		this.buildLabeledComposite(
			container,
			labelText,
			(combo.getParent() != container) ? combo.getParent() : combo,
			rightControl,
			helpId
		);

		return combo;
	}

	/**
	 * Creates a new container that will have an editable combo labeled with the
	 * given text.
	 *
	 * @param container The parent container
	 * @param labelText The text of the label
	 * @param comboListener The listener that will be notified when the selection
	 * changes
	 * @param labelProvider The provider responsible to convert the combo's items
	 * into human readable strings
	 * @param helpId The topic help ID to be registered for the given center
	 * compositer
	 * @return The newly created <code>CCombo</code>
	 *
	 * @category Layout
	 */
	protected final CCombo buildLabeledEditableCComboViewer(Composite container,
	                                                        String labelText,
	                                                        ModifyListener comboListener,
	                                                        ILabelProvider labelProvider,
	                                                        String helpId) {

		return this.buildLabeledEditableCComboViewer(
			container,
			labelText,
			comboListener,
			labelProvider,
			null,
			helpId
		);
	}

	/**
	 * Creates a new container that will have the given center control labeled
	 * with the given label.
	 *
	 * @param container The parent container
	 * @param labelText The text of the label
	 * @param listHolder The <code>ListValueHolder</code>
	 * @param selectedItemHolder The holder of the selected item
	 * @param rightControl The control shown to the right of the main widget
	 * @param helpId The topic help ID to be registered for the given center
	 * compositer
	 * @return The newly created <code>Combo</code>
	 *
	 * @category Layout
	 */
	protected final <V> Combo buildLabeledEditableCombo(Composite container,
	                                                    String labelText,
	                                                    ListValueModel<V> listHolder,
	                                                    WritablePropertyValueModel<V> selectedItemHolder,
	                                                    Control rightControl,
	                                                    String helpId) {

		return this.buildLabeledEditableCombo(
			container,
			labelText,
			listHolder,
			selectedItemHolder,
			StringConverter.Default.<V>instance(),
			rightControl,
			helpId
		);
	}

	/**
	 * Creates a new container that will have an editable combo labeled with the
	 * given text.
	 *
	 * @param container The parent container
	 * @param labelText The text of the label
	 * @param listHolder The <code>ListValueHolder</code>
	 * @param selectedItemHolder The holder of the selected item
	 * @param helpId The topic help ID to be registered for the given center
	 * compositer
	 * @return The newly created <code>Combo</code>
	 *
	 * @category Layout
	 */
	protected final <V> Combo buildLabeledEditableCombo(Composite container,
	                                                    String labelText,
	                                                    ListValueModel<V> listHolder,
	                                                    WritablePropertyValueModel<V> selectedItemHolder,
	                                                    String helpId) {

		return this.buildLabeledEditableCombo(
			container,
			labelText,
			listHolder,
			selectedItemHolder,
			StringConverter.Default.<V>instance(),
			null,
			helpId
		);
	}

	/**
	 * Creates a new container that will have the given center control labeled
	 * with the given label.
	 *
	 * @param container The parent container
	 * @param labelText The text of the label
	 * @param listHolder The <code>ListValueHolder</code>
	 * @param selectedItemHolder The holder of the selected item
	 * @param stringConverter The converter responsible to transform each item
	 * into a string representation
	 * @param rightControl The control shown to the right of the main widget
	 * @param helpId The topic help ID to be registered for the given center
	 * compositer
	 * @return The newly created <code>Combo</code>
	 *
	 * @category Layout
	 */
	protected final <V> Combo buildLabeledEditableCombo(Composite container,
	                                                    String labelText,
	                                                    ListValueModel<V> listHolder,
	                                                    WritablePropertyValueModel<V> selectedItemHolder,
	                                                    StringConverter<V> stringConverter,
	                                                    Control rightControl,
	                                                    String helpId) {

		Combo combo = this.buildEditableCombo(
			container,
			listHolder,
			selectedItemHolder,
			stringConverter
		);

		this.buildLabeledComposite(
			container,
			labelText,
			(combo.getParent() != container) ? combo.getParent() : combo,
			rightControl,
			helpId
		);

		return combo;
	}

	/**
	 * Creates a new container that will have an editable combo labeled with the
	 * given text.
	 *
	 * @param container The parent container
	 * @param labelText The text of the label
	 * @param listHolder The <code>ListValueHolder</code>
	 * @param selectedItemHolder The holder of the selected item
	 * @param stringConverter The converter responsible to transform each item
	 * into a string representation
	 * @param helpId The topic help ID to be registered for the given center
	 * compositer
	 * @return The newly created <code>Combo</code>
	 *
	 * @category Layout
	 */
	protected final <V> Combo buildLabeledEditableCombo(Composite container,
	                                                    String labelText,
	                                                    ListValueModel<V> listHolder,
	                                                    WritablePropertyValueModel<V> selectedItemHolder,
	                                                    StringConverter<V> stringConverter,
	                                                    String helpId) {

		return this.buildLabeledEditableCombo(
			container,
			labelText,
			listHolder,
			selectedItemHolder,
			stringConverter,
			null,
			helpId
		);
	}

	/**
	 * Creates a new container that will have the given center control labeled
	 * with the given label.
	 *
	 * @param container The parent container
	 * @param leftControl The widget shown to the left of the main widget
	 * @param centerControl The main widget
	 * @param rightControl The control shown to the right of the main widget
	 * @param helpId The topic help ID to be registered for the given center
	 * compositer
	 * @return The container of the label and the given center control
	 *
	 * @category Layout
	 */
	protected final Combo buildLabeledEditableCombo(Composite container,
	                                                String labelText,
	                                                ModifyListener comboListener,
	                                                Control rightControl,
	                                                String helpId) {

		Combo combo = this.buildEditableCombo(container);
		combo.addModifyListener(comboListener);

		this.buildLabeledComposite(
			container,
			labelText,
			(combo.getParent() != container) ? combo.getParent() : combo,
			rightControl,
			helpId
		);

		return combo;
	}

	/**
	 * Creates a new container that will have an editable combo labeled with the
	 * given text.
	 *
	 * @param container The parent container
	 * @param labelText The text of the label
	 * @param comboListener The listener that will be notified when the selection
	 * changes
	 * @param helpId The topic help ID to be registered for the given center
	 * compositer
	 * @return The container of the label and the given center control
	 *
	 * @category Layout
	 */
	protected final Combo buildLabeledEditableCombo(Composite container,
	                                                String labelText,
	                                                ModifyListener comboListener,
	                                                String helpId) {

		return this.buildLabeledEditableCombo(
			container,
			labelText,
			comboListener,
			null,
			helpId
		);
	}

	/**
	 * Creates a new container that will have a text field as the center control
	 * labeled with the given label.
	 *
	 * @param container The parent container
	 * @param labelText The text area's label
	 * @param textHolder The holder of the text field's input
	 * @return The newly created <code>Text</code>
	 *
	 * @category Layout
	 */
	protected final Text buildLabeledMultiLineText(Composite container,
	                                               String labelText,
	                                               WritablePropertyValueModel<String> textHolder) {

		return this.buildLabeledMultiLineText(
			container,
			labelText,
			textHolder,
			null
		);
	}

	/**
	 * Creates a new container that will have a text field as the center control
	 * labeled with the given label.
	 *
	 * @param container The parent container
	 * @param labelText The text area's label
	 * @param textHolder The holder of the text field's input
	 * @param rightControl The widget to be placed to the right of the text area
	 * @param helpId The topic help ID to be registered for the text field
	 * @return The newly created <code>Text</code>
	 *
	 * @category Layout
	 */
	protected final Text buildLabeledMultiLineText(Composite container,
	                                               String labelText,
	                                               WritablePropertyValueModel<String> textHolder,
	                                               Control rightControl,
	                                               String helpId) {

		Text text = this.buildMultiLineText(container, textHolder);

		this.buildLabeledComposite(
			container,
			labelText,
			text,
			rightControl,
			helpId
		);

		return text;
	}

	/**
	 * Creates a new container that will have a text field as the center control
	 * labeled with the given label.
	 *
	 * @param container The parent container
	 * @param labelText The text area's label
	 * @param textHolder The holder of the text field's input
	 * @param lineCount The number of lines the text area should display
	 * @return The newly created <code>Text</code>
	 *
	 * @category Layout
	 */
	protected final Text buildLabeledMultiLineText(Composite container,
	                                               String labelText,
	                                               WritablePropertyValueModel<String> textHolder,
	                                               int lineCount) {

		return this.buildLabeledMultiLineText(
			container,
			labelText,
			textHolder,
			lineCount,
			null
		);
	}

	/**
	 * Creates a new container that will have a text field as the center control
	 * labeled with the given label.
	 *
	 * @param container The parent container
	 * @param labelText The text area's label
	 * @param textHolder The holder of the text field's input
	 * @param lineCount The number of lines the text area should display
	 * @param helpId The topic help ID to be registered for the text field
	 * @return The newly created <code>Text</code>
	 *
	 * @category Layout
	 */
	protected final Text buildLabeledMultiLineText(Composite container,
	                                               String labelText,
	                                               WritablePropertyValueModel<String> textHolder,
	                                               int lineCount,
	                                               String helpId) {

		Text text = this.buildMultiLineText(container, textHolder, lineCount);

		container = this.buildLabeledComposite(
			container,
			labelText,
			text,
			helpId
		);

		int textHeight = text.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;

		// Specify the number of lines the text area should display
		GridData gridData = (GridData) text.getLayoutData();
		gridData.heightHint = text.getLineHeight() * lineCount;

		// Move the label to the top of its cell
		Control label = container.getChildren()[0];
		int labelHeight = label.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;

		gridData = (GridData) label.getLayoutData();
		gridData.verticalAlignment = SWT.TOP;
		gridData.verticalIndent   += (Math.abs(textHeight - labelHeight) / 2);

		return text;
	}

	/**
	 * Creates a new container that will have a text field as the center control
	 * labeled with the given label.
	 *
	 * @param container The parent container
	 * @param labelText The text area's label
	 * @param textHolder The holder of the text field's input
	 * @param helpId The topic help ID to be registered for the text field
	 * @return The newly created <code>Text</code>
	 *
	 * @category Layout
	 */
	protected final Text buildLabeledMultiLineText(Composite container,
	                                               String labelText,
	                                               WritablePropertyValueModel<String> textHolder,
	                                               String helpId) {

		return this.buildLabeledMultiLineText(
			container,
			labelText,
			textHolder,
			3,
			helpId
		);
	}

	/**
	 * Creates a new container that will have a text field as the center control
	 * labeled with the given label.
	 *
	 * @param container The parent container
	 * @param textHolder The holder of the text field's input
	 * @return The newly created <code>Text</code>
	 *
	 * @category Layout
	 */
	protected final Text buildLabeledPasswordText(Composite container,
	                                              String labelText,
	                                              WritablePropertyValueModel<String> textHolder) {

		return this.buildLabeledPasswordText(
			container,
			labelText,
			textHolder,
			null
		);
	}

	/**
	 * Creates a new container that will have a text field as the center control
	 * labeled with the given label.
	 *
	 * @param container The parent container
	 * @param labelText The text field's label
	 * @param rightComponent The component to be placed to the right of the text
	 * field
	 * @param textHolder The holder of the text field's input
	 * @param helpId The topic help ID to be registered for the text field
	 * @return The newly created <code>Text</code>
	 *
	 * @category Layout
	 */
	protected final Text buildLabeledPasswordText(Composite container,
	                                              String labelText,
	                                              WritablePropertyValueModel<String> textHolder,
	                                              Control rightComponent,
	                                              String helpId) {

		Text text = this.buildPasswordText(container, textHolder);

		this.buildLabeledComposite(
			container,
			labelText,
			text,
			rightComponent,
			helpId
		);

		return text;
	}

	/**
	 * Creates a new container that will have a text field as the center control
	 * labeled with the given label.
	 *
	 * @param container The parent container
	 * @param textHolder The holder of the text field's input
	 * @param helpId The topic help ID to be registered for the text field
	 * @return The newly created <code>Text</code>
	 *
	 * @category Layout
	 */
	protected final Text buildLabeledPasswordText(Composite container,
	                                              String labelText,
	                                              WritablePropertyValueModel<String> textHolder,
	                                              String helpId) {

		return this.buildLabeledPasswordText(
			container,
			labelText,
			textHolder,
			null,
			helpId
		);
	}

	/**
	 * Creates a new container that will have a text field as the center control
	 * labeled with the given label.
	 *
	 * @param container The parent container
	 * @param textHolder The holder of the text field's input
	 * @param helpId The topic help ID to be registered for the text field
	 * @return The newly created <code>Text</code>
	 *
	 * @category Layout
	 */
	protected final Text buildLabeledPawordText(Composite container,
	                                            String labelText,
	                                            WritablePropertyValueModel<String> textHolder,
	                                            String helpId) {

		return this.buildLabeledPasswordText(
			container,
			labelText,
			textHolder,
			null,
			helpId
		);
	}

	/**
	 * Creates a new spinner.
	 *
	 * @param parent The parent container
	 * @param labelText The label's text
	 * @param numberHolder The holder of the integer value
	 * @param defaultValue The value shown when the holder has <code>null</code>
	 * @return The newly created <code>Spinner</code>
	 *
	 * @category Layout
	 */
	protected final Spinner buildLabeledSpinner(Composite parent,
	                                            String labelText,
	                                            WritablePropertyValueModel<Integer> numberHolder,
	                                            int defaultValue) {

		return this.buildLabeledSpinner(
			container,
			labelText,
			numberHolder,
			defaultValue,
			0,
			Integer.MAX_VALUE,
			null,
			null
		);
	}

	/**
	 * Creates a new spinner.
	 *
	 * @param parent The parent container
	 * @param labelText The label's text
	 * @param numberHolder The holder of the integer value
	 * @param defaultValue The value shown when the holder has <code>null</code>
	 * @param rightControl The widget to be placed to the right of spinner
	 * @return The newly created <code>Spinner</code>
	 *
	 * @category Layout
	 */
	protected final Spinner buildLabeledSpinner(Composite parent,
	                                            String labelText,
	                                            WritablePropertyValueModel<Integer> numberHolder,
	                                            int defaultValue,
	                                            Control rightControl) {

		return this.buildLabeledSpinner(
			container,
			labelText,
			numberHolder,
			defaultValue,
			0,
			Integer.MAX_VALUE,
			rightControl,
			null
		);
	}

	/**
	 * Creates a new spinner.
	 *
	 * @param parent The parent container
	 * @param labelText The label's text
	 * @param numberHolder The holder of the integer value
	 * @param defaultValue The value shown when the holder has <code>null</code>
	 * @param minimumValue The minimum value that the spinner will allow
	 * @param maximumValue The maximum value that the spinner will allow
	 * @param rightControl The widget to be placed to the right of spinner
	 * @return The newly created <code>Spinner</code>
	 *
	 * @category Layout
	 */
	protected final Spinner buildLabeledSpinner(Composite parent,
	                                            String labelText,
	                                            WritablePropertyValueModel<Integer> numberHolder,
	                                            int defaultValue,
	                                            int minimumValue,
	                                            int maximumValue,
	                                            Control rightControl) {

		return this.buildLabeledSpinner(
			parent,
			labelText,
			numberHolder,
			defaultValue,
			minimumValue,
			maximumValue,
			rightControl,
			null
		);
	}

	/**
	 * Creates a new spinner.
	 *
	 * @param parent The parent container
	 * @param labelText The label's text
	 * @param numberHolder The holder of the integer value
	 * @param defaultValue The value shown when the holder has <code>null</code>
	 * @param minimumValue The minimum value that the spinner will allow
	 * @param maximumValue The maximum value that the spinner will allow
	 * @param rightControl The widget to be placed to the right of spinner
	 * @param helpId The topic help ID to be registered for the spinner
	 * @return The newly created <code>Spinner</code>
	 *
	 * @category Layout
	 */
	protected final Spinner buildLabeledSpinner(Composite parent,
	                                            String labelText,
	                                            WritablePropertyValueModel<Integer> numberHolder,
	                                            int defaultValue,
	                                            int minimumValue,
	                                            int maximumValue,
	                                            Control rightControl,
	                                            String helpId) {

		Spinner spinner = this.buildSpinner(
			parent,
			numberHolder,
			defaultValue,
			minimumValue,
			maximumValue
		);

		buildLabeledComposite(
			parent,
			labelText,
			(spinner.getParent() != parent) ? spinner.getParent() : spinner,
			rightControl,
			helpId
		);

		GridData gridData = (GridData) spinner.getLayoutData();
		gridData.horizontalAlignment = GridData.BEGINNING;

		return spinner;
	}

	/**
	 * Creates a new spinner.
	 *
	 * @param parent The parent container
	 * @param labelText The label's text
	 * @param numberHolder The holder of the integer value
	 * @param defaultValue The value shown when the holder has <code>null</code>
	 * @param minimumValue The minimum value that the spinner will allow
	 * @param maximumValue The maximum value that the spinner will allow
	 * @param helpId The topic help ID to be registered for the spinner
	 * @return The newly created <code>Spinner</code>
	 *
	 * @category Layout
	 */
	protected final Spinner buildLabeledSpinner(Composite parent,
	                                            String labelText,
	                                            WritablePropertyValueModel<Integer> numberHolder,
	                                            int defaultValue,
	                                            int minimumValue,
	                                            int maximumValue,
	                                            String helpId) {

		return this.buildLabeledSpinner(
			parent,
			labelText,
			numberHolder,
			defaultValue,
			0,
			Integer.MAX_VALUE,
			null,
			helpId
		);
	}

	/**
	 * Creates a new spinner.
	 *
	 * @param parent The parent container
	 * @param labelText The label's text
	 * @param numberHolder The holder of the integer value
	 * @param defaultValue The value shown when the holder has <code>null</code>
	 * @param helpId The topic help ID to be registered for the spinner
	 * @return The newly created <code>Spinner</code>
	 *
	 * @category Layout
	 */
	protected final Spinner buildLabeledSpinner(Composite parent,
	                                            String labelText,
	                                            WritablePropertyValueModel<Integer> numberHolder,
	                                            int defaultValue,
	                                            String helpId) {

		return this.buildLabeledSpinner(
			parent,
			labelText,
			numberHolder,
			defaultValue,
			0,
			Integer.MAX_VALUE,
			null,
			null
		);
	}

	/**
	 * Creates a new container that will have a text field as the center control
	 * labeled with the given label.
	 *
	 * @param container The parent container
	 * @param textHolder The holder of the text field's input
	 * @return The newly created <code>Text</code>
	 *
	 * @category Layout
	 */
	protected final Text buildLabeledText(Composite container,
	                                      String labelText,
	                                      WritablePropertyValueModel<String> textHolder) {

		return this.buildLabeledText(container, labelText, textHolder, null);
	}

	/**
	 * Creates a new container that will have a text field as the center control
	 * labeled with the given label.
	 *
	 * @param container The parent container
	 * @param labelText The text field's label
	 * @param rightComponent The component to be placed to the right of the text
	 * field
	 * @param textHolder The holder of the text field's input
	 * @param helpId The topic help ID to be registered for the text field
	 * @return The newly created <code>Text</code>
	 *
	 * @category Layout
	 */
	protected final Text buildLabeledText(Composite container,
	                                      String labelText,
	                                      WritablePropertyValueModel<String> textHolder,
	                                      Control rightComponent,
	                                      String helpId) {

		Text text = this.buildText(container);
		TextFieldModelAdapter.adapt(textHolder, text);

		this.buildLabeledComposite(
			container,
			labelText,
			text,
			rightComponent,
			helpId
		);

		return text;
	}

	/**
	 * Creates a new container that will have a text field as the center control
	 * labeled with the given label.
	 *
	 * @param container The parent container
	 * @param textHolder The holder of the text field's input
	 * @param helpId The topic help ID to be registered for the text field
	 * @return The newly created <code>Text</code>
	 *
	 * @category Layout
	 */
	protected final Text buildLabeledText(Composite container,
	                                      String labelText,
	                                      WritablePropertyValueModel<String> textHolder,
	                                      String helpId) {

		return this.buildLabeledText(
			container,
			labelText,
			textHolder,
			null,
			helpId
		);
	}

	/**
	 * Creates a new list and notify the given selection holder when the
	 * selection changes. If the selection count is different than one than the
	 * holder will receive <code>null</code>.
	 *
	 * @param container The parent container
	 * @return The newly created <code>List</code>
	 *
	 * @category Layout
	 */
	protected final List buildList(Composite container) {
		return this.buildList(container, (String) null);
	}

	/**
	 * Creates a new list and notify the given selection holder when the
	 * selection changes. If the selection count is different than one than the
	 * holder will receive <code>null</code>.
	 *
	 * @param container The parent container
	 * @param helpId The topic help ID to be registered for the new radio button
	 * @return The newly created <code>List</code>
	 *
	 * @category Layout
	 */
	protected final List buildList(Composite container, String helpId) {

		return this.buildList(
			container,
			new SimplePropertyValueModel<String>(),
			helpId
		);
	}

	/**
	 * Creates a new list and notify the given selection holder when the
	 * selection changes. If the selection count is different than one than the
	 * holder will receive <code>null</code>.
	 *
	 * @param container The parent container
	 * @param selectionHolder The holder of the unique selected item
	 * @return The newly created <code>List</code>
	 *
	 * @category Layout
	 */
	protected final List buildList(Composite container,
	                               WritablePropertyValueModel<String> selectionHolder) {

		return this.buildList(container, selectionHolder, null);
	}

	/**
	 * Creates a new list and notify the given selection holder when the
	 * selection changes. If the selection count is different than one than the
	 * holder will receive <code>null</code>.
	 *
	 * @param container The parent container
	 * @param selectionHolder The holder of the unique selected item
	 * @param helpId The topic help ID to be registered for the new radio button
	 * @return The newly created <code>List</code>
	 *
	 * @category Layout
	 */
	protected final List buildList(Composite container,
	                               WritablePropertyValueModel<String> selectionHolder,
	                               String helpId) {

		List list = this.widgetFactory.createList(
			container,
			SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI
		);

		list.addSelectionListener(buildSelectionListener(selectionHolder));
		list.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.widgets.add(list);

		if (helpId != null) {
			helpSystem().setHelp(list, helpId);
		}

		return list;
	}

	/**
	 * Creates a new lable expanding on multiple lines.
	 *
	 * @param parent The parent container
	 * @param labelText The label's text
	 *
	 * @category Layout
	 */
	protected final void buildMultiLineLabel(Composite container,
	                                         String labelText) {

		this.widgetFactory.createMultiLineLabel(container, labelText);
	}

	/**
	 * Creates a new <code>Text</code> widget that has multiple lines.
	 *
	 * @param container The parent container
	 * @return The newly created <code>Text</code> widget
	 *
	 * @category Layout
	 */
	protected final Text buildMultiLineText(Composite container) {
		return this.buildMultiLineText(container, 3, null);
	}

	/**
	 * Creates a new <code>Text</code> widget that has multiple lines.
	 *
	 * @param container The parent container
	 * @param lineCount The number of lines the text area should display
	 * @param helpId The topic help ID to be registered for the new text
	 * @return The newly created <code>Text</code> widget
	 *
	 * @category Layout
	 */
	protected final Text buildMultiLineText(Composite container,
	                                        int lineCount,
	                                        String helpId) {

		Text text = this.widgetFactory.createMultiLineText(container);

		GridData gridData   = new GridData(GridData.FILL_HORIZONTAL);
		gridData.heightHint = text.getLineHeight() * lineCount;
		text.setLayoutData(gridData);

		if (helpId != null) {
			helpSystem().setHelp(text, helpId);
		}

		return text;
	}

	/**
	 * Creates a new <code>Text</code> widget that has multiple lines.
	 *
	 * @param container The parent container
	 * @param helpId The topic help ID to be registered for the new text
	 * @return The newly created <code>Text</code> widget
	 *
	 * @category Layout
	 */
	protected final Text buildMultiLineText(Composite container, String helpId) {

		return this.buildMultiLineText(container, 3, helpId);
	}

	/**
	 * Creates a new <code>Text</code> widget that has multiple lines.
	 *
	 * @param container The parent container
	 * @param textHolder The holder of the text field's input
	 * @return The newly created <code>Text</code> widget
	 *
	 * @category Layout
	 */
	protected final Text buildMultiLineText(Composite container,
	                                        WritablePropertyValueModel<String> textHolder) {

		return this.buildMultiLineText(container, textHolder, null);
	}

	/**
	 * Creates a new <code>Text</code> widget that has multiple lines.
	 *
	 * @param container The parent container
	 * @param textHolder The holder of the text field's input
	 * @param lineCount The number of lines the text area should display
	 * @return The newly created <code>Text</code> widget
	 *
	 * @category Layout
	 */
	protected final Text buildMultiLineText(Composite container,
	                                        WritablePropertyValueModel<String> textHolder,
	                                        int lineCount) {

		return this.buildMultiLineText(container, textHolder, lineCount, null);
	}

	/**
	 * Creates a new <code>Text</code> widget that has multiple lines.
	 *
	 * @param container The parent container
	 * @param textHolder The holder of the text field's input
	 * @param helpId The topic help ID to be registered for the new text
	 * @return The newly created <code>Text</code> widget
	 *
	 * @category Layout
	 */
	protected final Text buildMultiLineText(Composite container,
	                                        WritablePropertyValueModel<String> textHolder,
	                                        int lineCount,
	                                        String helpId) {

		Text text = this.buildMultiLineText(container, lineCount, helpId);
		TextFieldModelAdapter.adapt(textHolder, text);
		return text;
	}

	/**
	 * Creates a new <code>Text</code> widget that has multiple lines.
	 *
	 * @param container The parent container
	 * @param textHolder The holder of the text field's input
	 * @param helpId The topic help ID to be registered for the new text
	 * @return The newly created <code>Text</code> widget
	 *
	 * @category Layout
	 */
	protected final Text buildMultiLineText(Composite container,
	                                        WritablePropertyValueModel<String> textHolder,
	                                        String helpId) {

		return this.buildMultiLineText(container, textHolder, 3, helpId);
	}

	/**
	 * Creates a new <code>PageBook</code> and set the proper layout and layout
	 * data.
	 *
	 * @param container The parent container
	 * @return The newly created <code>PageBook</code>
	 *
	 * @category Layout
	 */
	protected final PageBook buildPageBook(Composite container) {

		PageBook pageBook = new PageBook(container, SWT.NULL);
		pageBook.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return pageBook;
	}

	/**
	 * Creates a new container without specifying any layout manager.
	 *
	 * @param container The parent of the new container
	 * @return The newly created <code>Composite</code>
	 *
	 * @category Layout
	 */
	protected final Composite buildPane(Composite parent) {
		return this.widgetFactory.createComposite(parent);
	}

	/**
	 * Creates a new container using the given layout manager.
	 *
	 * @param parent The parent of the new container
	 * @param layout The layout manager of the new container
	 * @return The newly created container
	 *
	 * @category Layout
	 */
	protected final Composite buildPane(Composite container, Layout layout) {

		container = this.widgetFactory.createComposite(container);
		container.setLayout(layout);
		container.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return container;
	}

	/**
	 * Creates a new <code>Text</code> widget.
	 *
	 * @param container The parent container
	 * @param textHolder The holder of the text field's input
	 * @return The newly created <code>Text</code> widget
	 *
	 * @category Layout
	 */
	protected final Text buildPasswordText(Composite container,
	                                       WritablePropertyValueModel<String> textHolder) {

		Text text = this.widgetFactory.createPasswordText(container);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		TextFieldModelAdapter.adapt(textHolder, text);

		return text;
	}

	/**
	 * Creates a new push button using the given information.
	 *
	 * @param parent The parent container
	 * @param buttonText The button's text
	 * @param buttonAction The action to be invoked when the button is pressed
	 * @return The newly created <code>Button</code>
	 *
	 * @category Layout
	 */
	protected final Button buildPushButton(Composite parent,
	                                       String buttonText,
	                                       final Runnable buttonAction) {

		return this.buildPushButton(parent, buttonText, null, buttonAction);
	}

	/**
	 * Creates a new push button using the given information.
	 *
	 * @param parent The parent container
	 * @param buttonText The button's text
	 * @param buttonAction The action to be invoked when the button is pressed
	 * @param helpId The topic help ID to be registered for the new radio button
	 * @return The newly created <code>Button</code>
	 *
	 * @category Layout
	 */
	protected final Button buildPushButton(Composite parent,
	                                       String buttonText,
	                                       String helpId,
	                                       final Runnable buttonAction) {

		Button button = this.widgetFactory.createPushButton(parent, buttonText);

		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SWTUtil.asyncExec(buttonAction);
			}
		});

		button.setLayoutData(new GridData());

		if (helpId != null) {
			helpSystem().setHelp(button, helpId);
		}

		return button;
	}

	/**
	 * Creates a new radio button using the given information.
	 *
	 * @param parent The parent container
	 * @param buttonText The button's text
	 * @param booleanHolder The holder of the selection state
	 * @return The newly created <code>Button</code>
	 *
	 * @category Layout
	 */
	protected final Button buildRadioButton(Composite parent,
	                                        String buttonText,
	                                        WritablePropertyValueModel<Boolean> booleanHolder) {

		return this.buildRadioButton(parent, buttonText, booleanHolder, null);
	}

	/**
	 * Creates a new check box using the given information.
	 *
	 * @param parent The parent container
	 * @param buttonText The button's text
	 * @param booleanHolder The holder of the selection state
	 * @param helpId The topic help ID to be registered for the new radio button
	 * @return The newly created <code>Button</code>
	 *
	 * @category Layout
	 */
	protected final Button buildRadioButton(Composite parent,
	                                        String buttonText,
	                                        WritablePropertyValueModel<Boolean> booleanHolder,
	                                        String helpId) {

		return this.buildToggleButton(
			parent,
			buttonText,
			booleanHolder,
			helpId,
			SWT.RADIO
		);
	}

	/**
	 * Creates a new <code>Section</code>. A sub-pane is automatically added as
	 * its client and is the returned <code>Composite</code>.
	 *
	 * @param container The container of the new widget
	 * @param sectionText The text of the new section
	 * @return The <code>Section</code>'s sub-pane
	 *
	 * @category Layout
	 */
	protected final Composite buildSection(Composite container,
	                                       String sectionText) {

		return this.buildSection(
			container,
			sectionText,
			ExpandableComposite.TITLE_BAR
		);
	}

	/**
	 * Creates a new <code>Section</code>. A sub-pane is automatically added as
	 * its client and is the returned <code>Composite</code>.
	 *
	 * @param container The container of the new widget
	 * @param sectionText The text of the new section
	 * @param type The type of section to create
	 * @param expandedStateHolder The holder of the boolean that will dictate
	 * when to expand or collapse the section
	 * @return The <code>Section</code>'s sub-pane
	 *
	 * @category Layout
	 */
	private Composite buildSection(Composite container,
	                               String sectionText,
	                               int type) {

		return this.buildSection(container, sectionText, null, type);
	}

	/**
	 * Creates a new <code>Section</code>. A sub-pane is automatically added as
	 * its client and is the returned <code>Composite</code>.
	 *
	 * @param container The container of the new widget
	 * @param sectionText The text of the new section
	 * @param description The section's description
	 * @return The <code>Section</code>'s sub-pane
	 *
	 * @category Layout
	 */
	protected final Composite buildSection(Composite container,
	                                       String sectionText,
	                                       String description) {

		return this.buildSection(
			container,
			sectionText,
			description,
			ExpandableComposite.TITLE_BAR
		);
	}

	/**
	 * Creates a new <code>Section</code>. A sub-pane is automatically added as
	 * its client and is the returned <code>Composite</code>.
	 *
	 * @param container The container of the new widget
	 * @param sectionText The text of the new section
	 * @param description The section's description or <code>null</code> if none
	 * was provider
	 * @param type The type of section to create
	 * @param expandedStateHolder The holder of the boolean that will dictate
	 * when to expand or collapse the section
	 * @return The <code>Section</code>'s sub-pane
	 *
	 * @category Layout
	 */
	private Composite buildSection(Composite container,
	                               String sectionText,
	                               String description,
	                               int type) {

		Section section = this.widgetFactory.createSection(container, type | ((description != null) ? Section.DESCRIPTION : SWT.NULL));
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		section.setText(sectionText);
		section.marginWidth  = 0;
		section.marginHeight = 0;

		if (description != null) {
			section.setDescription(description);
		}

		Composite subPane = this.buildSubPane(section);
		section.setClient(subPane);

		return subPane;
	}

	private SelectionListener buildSelectionListener(final WritablePropertyValueModel<String> selectionHolder) {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List list = (List) e.widget;
				String[] selectedItems = list.getSelection();
				if ((selectedItems == null) || (selectedItems.length != 1)) {
					selectionHolder.setValue(null);
				}
				else {
					selectionHolder.setValue(selectedItems[0]);
				}
			}
		};
	}

	/**
	 * Creates the layout responsible to compute the size of the spacer created
	 * for the right control when none was given. The spacer helps to align all
	 * the right controls.
	 *
	 * @category Layout
	 */
	private Layout buildSpacerLayout() {
		return new Layout() {
			@Override
			protected Point computeSize(Composite composite,
			                            int widthHint,
			                            int heightHint,
			                            boolean flushCache) {

				return new Point(widthHint, heightHint);
			}

			@Override
			protected void layout(Composite composite, boolean flushCache) {
				GridData data = (GridData) composite.getLayoutData();
				composite.setBounds(0, 0, data.widthHint, data.heightHint);
			}
		};
	}

	/**
	 * Creates a new spinner.
	 *
	 * @param parent The parent container
	 * @param numberHolder The holder of the integer value
	 * @param defaultValue The value shown when the holder has <code>null</code>
	 * @return The newly created <code>Spinner</code>
	 *
	 * @category Layout
	 */
	protected final Spinner buildSpinner(Composite parent,
	                                     WritablePropertyValueModel<Integer> numberHolder,
	                                     int defaultValue) {

		return this.buildSpinner(
			parent,
			numberHolder,
			defaultValue,
			0,
			Integer.MAX_VALUE
		);
	}

	/**
	 * Creates a new spinner.
	 *
	 * @param parent The parent container
	 * @param numberHolder The holder of the integer value
	 * @param defaultValue The value shown when the holder has <code>null</code>
	 * @param minimumValue The minimum value that the spinner will allow
	 * @param maximumValue The maximum value that the spinner will allow
	 * @return The newly created <code>Spinner</code>
	 *
	 * @category Layout
	 */
	protected final Spinner buildSpinner(Composite parent,
	                                     WritablePropertyValueModel<Integer> numberHolder,
	                                     int defaultValue,
	                                     int minimumValue,
	                                     int maximumValue) {

		return this.buildSpinner(
			parent,
			numberHolder,
			defaultValue,
			minimumValue,
			maximumValue,
			null
		);
	}

	/**
	 * Creates a new spinner.
	 *
	 * @param parent The parent container
	 * @param numberHolder The holder of the integer value
	 * @param defaultValue The value shown when the holder has <code>null</code>
	 * @param minimumValue The minimum value that the spinner will allow
	 * @param maximumValue The maximum value that the spinner will allow
	 * @param helpId The topic help ID to be registered for the new button
	 * @return The newly created <code>Spinner</code>
	 *
	 * @category Layout
	 */
	protected final Spinner buildSpinner(Composite parent,
	                                     WritablePropertyValueModel<Integer> numberHolder,
	                                     int defaultValue,
	                                     int minimumValue,
	                                     int maximumValue,
	                                     String helpId) {

		Spinner spinner = this.widgetFactory.createSpinner(parent);
		spinner.setMinimum(minimumValue);
		spinner.setMaximum(maximumValue);
		spinner.setLayoutData(new GridData(GridData.BEGINNING));

		SpinnerModelAdapter.adapt(numberHolder, spinner, defaultValue);

		if (helpId != null) {
			helpSystem().setHelp(spinner, helpId);
		}

		return spinner;
	}

	private PropertyChangeListener buildSubjectChangeListener() {
		return new SWTPropertyChangeListenerWrapper(this.buildSubjectChangeListener_());
	}

	private PropertyChangeListener buildSubjectChangeListener_() {
		return new PropertyChangeListener() {
			@SuppressWarnings("unchecked")
			public void propertyChanged(PropertyChangeEvent e) {
				AbstractPane.this.subjectChanged((T) e.getOldValue(), (T) e.getNewValue());
			}
		};
	}

	/**
	 * Creates a new <code>Composite</code> used as a sub-pane.
	 *
	 * @param container The parent container
	 * @return The newly created <code>Composite</code> used as a sub-pane
	 *
	 * @category Layout
	 */
	protected final Composite buildSubPane(Composite container) {
		return this.buildSubPane(container, 0);
	}

	/**
	 * Creates a new <code>Composite</code> used as a sub-pane.
	 *
	 * @param container The parent container
	 * @param topMargin The extra spacing to add at the top of the pane
	 * @return The newly created <code>Composite</code> used as a sub-pane
	 *
	 * @category Layout
	 */
	protected final Composite buildSubPane(Composite container, int topMargin) {
		return this.buildSubPane(container, topMargin, 0);
	}

	/**
	 * Creates a new <code>Composite</code> used as a sub-pane.
	 *
	 * @param container The parent container
	 * @param topMargin The extra spacing to add at the top of the pane
	 * @param leftMargin The extra spacing to add to the left of the pane
	 * @return The newly created <code>Composite</code> used as a sub-pane
	 *
	 * @category Layout
	 */
	protected final Composite buildSubPane(Composite container,
	                                       int topMargin,
	                                       int leftMargin) {

		return this.buildSubPane(container, topMargin, leftMargin, 0, 0);
	}

	/**
	 * Creates a new <code>Composite</code> used as a sub-pane, the new widget
	 * will have its layout and layout data already initialized, the layout will
	 * be a <code>GridLayout</code> with 1 column.
	 *
	 * @param container The parent container
	 * @param topMargin The extra spacing to add at the top of the pane
	 * @param leftMargin The extra spacing to add to the left of the pane
	 * @param bottomMargin The extra spacing to add at the bottom of the pane
	 * @param rightMargin The extra spacing to add to the right of the pane
	 * @return The newly created <code>Composite</code> used as a sub-pane
	 *
	 * @category Layout
	 */
	protected final Composite buildSubPane(Composite container,
	                                       int topMargin,
	                                       int leftMargin,
	                                       int bottomMargin,
	                                       int rightMargin) {

		return this.buildSubPane(
			container,
			1,
			topMargin,
			leftMargin,
			bottomMargin,
			rightMargin);
	}

	/**
	 * Creates a new <code>Composite</code> used as a sub-pane, the new widget
	 * will have its layout and layout data already initialized, the layout will
	 * be a <code>GridLayout</code> with 1 column.
	 *
	 * @param container The parent container
	 * @param topMargin The extra spacing to add at the top of the pane
	 * @param leftMargin The extra spacing to add to the left of the pane
	 * @param bottomMargin The extra spacing to add at the bottom of the pane
	 * @param rightMargin The extra spacing to add to the right of the pane
	 * @return The newly created <code>Composite</code> used as a sub-pane
	 *
	 * @category Layout
	 */
	protected final Composite buildSubPane(Composite container,
	                                       int columnCount,
	                                       int topMargin,
	                                       int leftMargin,
	                                       int bottomMargin,
	                                       int rightMargin) {

		GridLayout layout = new GridLayout(columnCount, false);
		layout.marginHeight = 0;
		layout.marginWidth  = 0;
		layout.marginTop    = topMargin;
		layout.marginLeft   = leftMargin;
		layout.marginBottom = bottomMargin;
		layout.marginRight  = rightMargin;

		container = this.buildPane(container, layout);

		return container;
	}

	/**
	 * Creates a new <code>Section</code>. A sub-pane is automatically added as
	 * its client which can be typed cast directly as a <code>Composite</code>.
	 *
	 * @param container The container of the new widget
	 * @param sectionText The text of the new section
	 * @return The <code>Section</code>'s sub-pane
	 *
	 * @category Layout
	 */
	protected final Composite buildSubSection(Composite container,
	                                          String sectionText) {

		return this.buildCollapsableSubSection(
			container,
			sectionText,
			new SimplePropertyValueModel<Boolean>(Boolean.TRUE)
		);
	}

	/**
	 * Creates a new table.
	 *
	 * @param container The parent container
	 * @return The newly created <code>Table</code>
	 *
	 * @category Layout
	 */
	protected final Table buildTable(Composite container) {
		return this.buildTable(container, null);
	}

	/**
	 * Creates a new table.
	 *
	 * @param container The parent container
	 * @param style The style to apply to the table
	 * @return The newly created <code>Table</code>
	 *
	 * @category Layout
	 */
	protected final Table buildTable(Composite container, int style) {
		return this.buildTable(container, style, null);
	}

	/**
	 * Creates a new table.
	 *
	 * @param container The parent container
	 * @param style The style to apply to the table
	 * @param helpId The topic help ID to be registered for the new table or
	 * <code>null</code> if no help ID is required
	 * @return The newly created <code>Table</code>
	 *
	 * @category Layout
	 */
	protected final Table buildTable(Composite container,
	                                 int style,
	                                 String helpId) {

		Table table = this.widgetFactory.createTable(container, style);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		GridData gridData   = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = table.getItemHeight() * 4;
		table.setLayoutData(gridData);
		this.widgets.add(table);

		if (helpId != null) {
			helpSystem().setHelp(table, helpId);
		}

		return table;
	}

	/**
	 * Creates a new table.
	 *
	 * @param container The parent container
	 * @param helpId The topic help ID to be registered for the new table or
	 * <code>null</code> if no help ID is required
	 * @return The newly created <code>Table</code>
	 *
	 * @category Layout
	 */
	protected final Table buildTable(Composite container, String helpId) {

		return this.buildTable(
			container,
			SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.MULTI,
			helpId
		);
	}

	/**
	 * Creates a new <code>Text</code> widget.
	 *
	 * @param container The parent container
	 * @return The newly created <code>Text</code> widget
	 *
	 * @category Layout
	 */
	protected final Text buildText(Composite container) {
		return this.widgetFactory.createText(container);
	}

	/**
	 * Creates a new <code>Text</code> widget.
	 *
	 * @param container The parent container
	 * @param helpId The topic help ID to be registered for the new text
	 * @return The newly created <code>Text</code> widget
	 *
	 * @category Layout
	 */
	protected final Text buildText(Composite container, String helpId) {

		Text text = this.widgetFactory.createText(container);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		if (helpId != null) {
			helpSystem().setHelp(text, helpId);
		}

		return text;
	}

	/**
	 * Creates a new <code>Text</code> widget.
	 *
	 * @param container The parent container
	 * @param textHolder The holder of the text field's input
	 * @return The newly created <code>Text</code> widget
	 *
	 * @category Layout
	 */
	protected final Text buildText(Composite container,
	                               WritablePropertyValueModel<String> textHolder) {

		return this.buildText(container, textHolder, null);
	}

	/**
	 * Creates a new <code>Text</code> widget.
	 *
	 * @param container The parent container
	 * @param textHolder The holder of the text field's input
	 * @param helpId The topic help ID to be registered for the new text
	 * @return The newly created <code>Text</code> widget
	 *
	 * @category Layout
	 */
	protected final Text buildText(Composite container,
	                               WritablePropertyValueModel<String> textHolder,
	                               String helpId) {

		Text text = this.widgetFactory.createText(container);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		TextFieldModelAdapter.adapt(textHolder, text);

		if (helpId != null) {
			helpSystem().setHelp(text, helpId);
		}

		return text;
	}

	/**
	 * Creates a new container with a titled border.
	 *
	 * @param title The text of the titled border
	 * @param container The parent container
	 * @return The newly created <code>Composite</code> with a titled border
	 *
	 * @category Layout
	 */
	protected final Group buildTitledPane(Composite container, String title) {
		return this.buildTitledPane(container, title, null);
	}

	/**
	 * Creates a new container with a titled border.
	 *
	 * @param title The text of the titled border
	 * @param container The parent container
	 * @param helpId The topic help ID to be registered for the new group
	 * @return The newly created <code>Composite</code> with a titled border
	 *
	 * @category Layout
	 */
	protected final Group buildTitledPane(Composite container,
	                                      String title,
	                                      String helpId) {

		Group group = this.widgetFactory.createGroup(container, title);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		layout.marginWidth  = 0;
		layout.marginTop    = 5;
		layout.marginLeft   = 5;
		layout.marginBottom = 5;
		layout.marginRight  = 5;
		group.setLayout(layout);

		if (helpId != null) {
			helpSystem().setHelp(group, helpId);
		}

		return group;
	}

	/**
	 * Creates a new toggle button (radio button or check box) using the given
	 * information.
	 *
	 * @param parent The parent container
	 * @param buttonText The button's text
	 * @param booleanHolder The holder of the selection state
	 * @param helpId The topic help ID to be registered for the new button
	 * @return The newly created <code>Button</code>
	 *
	 * @category Layout
	 */
	private Button buildToggleButton(Composite parent,
	                                 String buttonText,
	                                 WritablePropertyValueModel<Boolean> booleanHolder,
	                                 String helpId,
	                                 int toggleButtonType) {

		Button button;

		if (toggleButtonType == SWT.PUSH) {
			button = this.widgetFactory.createPushButton(parent, buttonText);
		}
		else if (toggleButtonType == SWT.RADIO) {
			button = this.widgetFactory.createRadioButton(parent, buttonText);
		}
		else if (toggleButtonType == SWT.CHECK) {
			button = this.widgetFactory.createCheckBox(parent, buttonText);
		}
		else {
			button = this.widgetFactory.createButton(parent, buttonText);
		}

		button.setLayoutData(new GridData());
		BooleanButtonModelAdapter.adapt(booleanHolder, button);
		this.widgets.add(button);

		if (helpId != null) {
			helpSystem().setHelp(button, helpId);
		}

		return button;
	}

	/**
	 * Creates a new check box that can have 3 selection states (selected,
	 * unselected and partially selected.
	 *
	 * @param parent The parent container
	 * @param text The button's text
	 * @param booleanHolder The holder of the boolean value where <code>null</code>
	 * means partially selected
	 * @return The newly created <code>TriStateCheckBox</code>
	 *
	 * @category Layout
	 */
	protected final TriStateCheckBox buildTriStateCheckBox(Composite parent,
	                                                       String text,
	                                                       WritablePropertyValueModel<Boolean> booleanHolder) {

		return this.buildTriStateCheckBox(parent, text, booleanHolder, null);
	}

	/**
	 * Creates a new check box that can have 3 selection states (selected,
	 * unselected and partially selected.
	 *
	 * @param parent The parent container
	 * @param text The button's text
	 * @param booleanHolder The holder of the boolean value where <code>null</code>
	 * means partially selected
	 * @param helpId The topic help ID to be registered for the new check box
	 * @return The newly created <code>TriStateCheckBox</code>
	 *
	 * @category Layout
	 */
	protected final TriStateCheckBox buildTriStateCheckBox(Composite parent,
	                                                       String text,
	                                                       WritablePropertyValueModel<Boolean> booleanHolder,
	                                                       String helpId) {

		TriStateCheckBox checkBox = new TriStateCheckBox(
			parent,
			text,
			this.getWidgetFactory()
		);

		TriStateCheckBoxModelAdapter.adapt(
			booleanHolder,
			checkBox
		);

		this.widgets.add(checkBox.getCheckBox());

		if (helpId != null) {
			helpSystem().setHelp(checkBox.getCheckBox(), helpId);
		}

		return checkBox;
	}

	/**
	 * Creates a new check box that can have 3 selection states (selected,
	 * unselected and partially selected.
	 *
	 * @param parent The parent container
	 * @param text The button's text
	 * @param booleanHolder The holder of the boolean value where <code>null</code>
	 * means partially selected
	 * @param stringHolder The holder of the string to put in parenthesis after
	 * the check box's text when it is partially selected
	 * @return The newly created <code>TriStateCheckBox</code>
	 *
	 * @category Layout
	 */
	protected final TriStateCheckBox buildTriStateCheckBoxWithDefault(Composite parent,
	                                                                  String text,
	                                                                  WritablePropertyValueModel<Boolean> booleanHolder,
	                                                                  PropertyValueModel<String> stringHolder) {

		return this.buildTriStateCheckBoxWithDefault(
			parent,
			text,
			booleanHolder,
			stringHolder,
			null
		);
	}

	/**
	 * Creates a new check box that can have 3 selection states (selected,
	 * unselected and partially selected.
	 *
	 * @param parent The parent container
	 * @param text The button's text
	 * @param booleanHolder The holder of the boolean value where <code>null</code>
	 * means partially selected
	 * @param stringHolder The holder of the string to put in parenthesis after
	 * the check box's text when it is partially selected
	 * @param helpId The topic help ID to be registered for the new check box
	 * @return The newly created <code>TriStateCheckBox</code>
	 *
	 * @category Layout
	 */
	protected final TriStateCheckBox buildTriStateCheckBoxWithDefault(Composite parent,
	                                                                  String text,
	                                                                  WritablePropertyValueModel<Boolean> booleanHolder,
	                                                                  PropertyValueModel<String> stringHolder,
	                                                                  String helpId) {

		TriStateCheckBox checkBox = this.buildTriStateCheckBox(
			parent,
			text,
			booleanHolder,
			helpId
		);

		new LabeledControlUpdater(
			new LabeledButton(checkBox.getCheckBox()),
			stringHolder
		);

		return checkBox;
	}

	/**
	 * Uninstalls any listeners from the subject in order to stop being notified
	 * for changes made outside of this panes.
	 *
	 * @category Populate
	 */
	protected void disengageListeners() {

		this.log(Tracing.UI_LAYOUT, "   ->disengageListeners()");

		this.subjectHolder.removePropertyChangeListener(
			PropertyValueModel.VALUE,
			this.subjectChangeListener
		);

		this.disengageListeners(this.subject());

		for (AbstractPane<?> subPane : this.subPanes) {
			subPane.disengageListeners();
		}
	}

	/**
	 * Removes any property change listeners from the given subject.
	 *
	 * @param subject The old subject
	 *
	 * @category Populate
	 */
	protected void disengageListeners(T subject) {
		if (subject != null) {
//			this.log("   ->disengageListeners() from " + subject);

			for (String propertyName : this.propertyNames()) {
				subject.removePropertyChangeListener(propertyName, this.aspectChangeListener);
			}
		}
	}

	/**
	 * Notifies this pane is should dispose itself.
	 *
	 * @category Populate
	 */
	public final void dispose() {
		if (!this.container.isDisposed()) {
			this.log(Tracing.UI_LAYOUT, "dispose()");
			this.performDispose();
			this.disengageListeners();
		}
	}

	/**
	 * Requests this pane to dispose itself.
	 *
	 * @category Populate
	 */
	protected void doDispose() {
		this.log(Tracing.UI_LAYOUT, "   ->doDispose()");

		this.leftControlAligner.dispose();
		this.rightControlAligner.dispose();
	}

	/**
	 * Requests this pane to populate its widgets with the subject's values.
	 *
	 * @category Populate
	 */
	protected void doPopulate() {
		this.log(Tracing.UI_LAYOUT, "   ->doPopulate()");
	}

	/**
	 * Changes the enablement state of the children <code>Control</code>s.
	 *
	 * @param enabled <code>true</code> to enable the widgets or <code>false</code>
	 * to disable them
	 *
	 * @category Layout
	 */
	private void enableChildren(boolean enabled) {

		// Only update the enablement state of the child widgets if this pane
		// has its enablement managed by enableWidgets() (i.e. not done manually)
		if (this.isEnablementManaged(container)) {

			for (Control control : this.widgets) {

				// Make sure to check if the child control doesn't have its
				// enablement state manually managed
				if (this.isEnablementManaged(control)) {
					control.setEnabled(enabled);
				}
			}
		}
	}

	/**
	 * Changes the enablement state of the widgets of this pane.
	 *
	 * @param enabled <code>true</code> to enable the widgets or <code>false</code>
	 * to disable them
	 *
	 * @category Layout
	 */
	public void enableWidgets(boolean enabled) {

		if (!container.isDisposed()) {
			this.enableChildren(enabled);

			for (AbstractPane<?> subPane : this.subPanes) {
				if (this.isPaneEnablementManaged(subPane)) {
					subPane.enableWidgets(enabled);
				}
			}
		}
	}

	/**
	 * Installs the listeners on the subject in order to be notified from changes
	 * made outside of this panes and notifies the sub-panes to do the same.
	 *
	 * @category Populate
	 */
	protected void engageListeners() {

		this.log(Tracing.UI_LAYOUT, "   ->engageListeners()");

		this.engageSubjectHolder();
		this.engageListeners(this.subject());

		for (AbstractPane<?> subPane : this.subPanes) {
			subPane.engageListeners();
		}
	}

	/**
	 * TODO
	 *
	 * @param subject
	 *
	 * @category Populate
	 */
	protected void engageListeners(T subject) {
		if (subject != null) {

//			this.log("   ->engageListeners() on " + subject);

			for (String propertyName : this.propertyNames()) {
				subject.addPropertyChangeListener(propertyName, this.aspectChangeListener);
			}
		}
	}

	private void engageSubjectHolder() {
		this.subjectHolder.addPropertyChangeListener(
			PropertyValueModel.VALUE,
			this.subjectChangeListener
		);
	}

	/**
	 * Returns the main <code>Control</code> of this pane.
	 *
	 * @return The main container
	 *
	 * @category Layout
	 */
	public Composite getControl() {
		return this.container;
	}

	/**
	 * Returns the subject holder used by this pane.
	 *
	 * @return The holder of the subject
	 *
	 * @category Populate
	 */
	protected final PropertyValueModel<T> getSubjectHolder() {
		return this.subjectHolder;
	}

	/**
	 * Returns the factory responsible for creating the widgets.
	 *
	 * @return The factory used by this pane to create the widgets
	 *
	 * @category Layout
	 */
	protected final WidgetFactory getWidgetFactory() {
		return this.widgetFactory;
	}

	/**
	 * Returns the margin taken by a group box, which is the number of pixel the
	 * group box border and its margin takes before displaying its widgets plus
	 * 5 pixels since the widgets inside of the group box and the border should
	 * have that extra 5 pixels.
	 *
	 * @return The width taken by the group box border with its margin
	 *
	 * @category Layout
	 */
	protected final int groupBoxMargin() {
		Group group = this.widgetFactory.createGroup(SWTUtil.getShell(), "");
		Rectangle clientArea = group.getClientArea();
		group.dispose();
		return clientArea.x + 5;
	}

	/**
	 * Returns the helps system.
	 *
	 * @return The platform's help system
	 *
	 * @category Helper
	 */
	protected final IWorkbenchHelpSystem helpSystem() {
		return PlatformUI.getWorkbench().getHelpSystem();
	}

	/**
	 * Initializes this <code>AbstractSubjectPane</code>.
	 *
	 * @category Initialization
	 */
	protected void initialize() {
	}

	/**
	 * Registers this pane with the parent pane.
	 *
	 * @param parentPane The parent pane
	 * @param automaticallyAlignWidgets <code>true</code> to make the widgets
	 * this pane aligned with the widgets of the given parent pane;
	 * <code>false</code> to not align them
	 *
	 * @category Initialization
	 */
	private void initialize(AbstractPane<?> parentPane,
	                        boolean automaticallyAlignWidgets) {

		// Register this pane with the parent pane, it will call the methods
		// automatically (engageListeners(), disengageListeners(), populate(),
		// dispose(), etc)
		parentPane.registerSubPane(this);

		// Align the left and right controls with the controls from the parent
		// pane
		if (automaticallyAlignWidgets) {
			parentPane.leftControlAligner .add(this.leftControlAligner);
			parentPane.rightControlAligner.add(this.rightControlAligner);
		}
	}

	/**
	 * Initializes this <code>AbstractSubjectPane</code>.
	 *
	 * @param subjectHolder The holder of this pane's subject
	 * @param widgetFactory The factory used to create various widgets
	 *
	 * @category Initialization
	 */
	@SuppressWarnings("unchecked")
	private void initialize(PropertyValueModel<? extends T> subjectHolder,
	                        WidgetFactory widgetFactory)
	{
		Assert.isNotNull(subjectHolder, "The subject holder cannot be null");

		this.subjectHolder         = (PropertyValueModel<T>) subjectHolder;
		this.widgetFactory         = widgetFactory;
		this.subPanes              = new ArrayList<AbstractPane<?>>();
		this.widgets               = new ArrayList<Control>();
		this.leftControlAligner    = new ControlAligner();
		this.rightControlAligner   = new ControlAligner();
		this.subjectChangeListener = this.buildSubjectChangeListener();
		this.aspectChangeListener  = this.buildAspectChangeListener();
		this.internalPanesForEnablementControl = new ArrayList<AbstractPane<?>>();

		this.initialize();
	}

	/**
	 * Initializes the layout of this pane.
	 *
	 * @param container The parent container
	 *
	 * @category Layout
	 */
	protected abstract void initializeLayout(Composite container);

	/**
	 * Determines whether the enablement state is managed by this pane or is
	 * manually managed.
	 *
	 * @param control The <code>Control</code> to verify how its enablement state
	 * is managed
	 * @return <code>true</code> if the enablement state can be changed when
	 * {@link #enableWidgets(boolean)} is called; <code>false</code> if its
	 * enablement state is manually changed
	 */
	public final boolean isEnablementManaged(Control control) {
		return control.getData("enablement") != Boolean.FALSE;
	}

	/**
	 * Determines whether the given pane has its enablement state managed by this
	 * pane.
	 *
	 * @param subPane The sub-pane to verify how its enablement state is being
	 * managed
	 * @return <code>true</code> if the sub-pane's enablement state is managed by
	 * this state; <code>false</code> if it's manually managed
	 *
	 * @category Layout
	 */
	private boolean isPaneEnablementManaged(AbstractPane<?> subPane) {

		return // Test 1: This pane is being automatically managed and the
		       // sub-pane was registered has an internal pane, which means its
		       // enablement state is being managed automatically as well. A
		       // sub-pane is managed automatically only when PaneEnabler changes
		       // the enablement management checks
		       this.isEnablementManaged(container) &&
		       this.internalPanesForEnablementControl.contains(subPane) ||

		       // Test 2: Check to see if the pane is automatically managed
		       this.isEnablementManaged(subPane.getControl());
	}

	/**
	 * Determines whether
	 *
	 * @return
	 *
	 * @category Populate
	 */
	protected final boolean isPopulating() {
		return this.populating;
	}

	/**
	 * Logs the given message if the <code>Tracing.DEBUG_LAYOUT</code> is enabled.
	 *
	 * @param flag
	 * @param message The logging message
	 */
	protected void log(String flag, String message) {

		if (Tracing.UI_LAYOUT.equals(flag) &&
		    Tracing.booleanDebugOption(Tracing.UI_LAYOUT)) {

			Class<?> thisClass = getClass();
			String className = ClassTools.shortNameFor(thisClass);

			if (thisClass.isAnonymousClass()) {
				className = className.substring(0, className.indexOf('$'));
				className += "->" + ClassTools.shortNameFor(thisClass.getSuperclass());
			}

			Tracing.log(className + ": " + message);
		}
	}

	/**
	 * Notifies this pane is should dispose itself.
	 *
	 * @category Populate
	 */
	protected void performDispose() {
		this.log(Tracing.UI_LAYOUT, "   ->performDispose()");

		// Dispose this pane
		doDispose();

		// Ask the sub-panes to perform the dispose themselves
		for (AbstractPane<?> subPane : this.subPanes) {
			subPane.performDispose();
		}
	}

	/**
	 * Notifies this pane to populate itself using the subject's information.
	 *
	 * @category Populate
	 */
	public final void populate() {
		if (!this.container.isDisposed()) {
			this.log(Tracing.UI_LAYOUT, "populate()");
			this.engageListeners();
			this.repopulate();
		}
	}

	/**
	 * Notifies the subject's property associated with the given property name
	 * has changed.
	 *
	 * @param propertyName The property name associated with the property change
	 *
	 * @category Populate
	 */
	protected void propertyChanged(String propertyName) {
	}

	/**
	 * Returns the list of names to listen for properties changing from the
	 * subject.
	 *
	 * @return A non-<code>null</code> list of property names
	 *
	 * @category Populate
	 */
	protected Collection<String> propertyNames() {
		ArrayList<String> propertyNames = new ArrayList<String>();
		addPropertyNames(propertyNames);
		return propertyNames;
	}

	/**
	 * Registers another <code>AbstractSubjectPane</code> with this one so it can
	 * be automatically notified about certain events such as engaging or
	 * disengaging the listeners, etc.
	 *
	 * @param subPane The sub-pane to register
	 *
	 * @category Controller
	 */
	protected final void registerSubPane(AbstractPane<?> subPane) {
		this.subPanes.add(subPane);
	}

	/**
	 * Removes the given pane's widgets (those that were registered with
	 * its left <code>ControlAligner</code>) from this pane's left
	 * <code>ControlAligner</code> so that their width will no longer be adjusted
	 * with the width of the widest widget.
	 *
	 * @param pane The pane containing the widgets to remove
	 *
	 * @category Layout
	 */
	protected final void removeAlignLeft(AbstractPane<?> pane) {
		this.leftControlAligner.remove(pane.leftControlAligner);
	}

	/**
	 * Removes the given control from the collection of widgets that are aligned
	 * to have the same width when they are shown to the left side of the 3
	 * widget colums.
	 *
	 * @param pane The pane to remove, its width will no longer be
	 * ajusted to be the width of the longest widget
	 *
	 * @category Layout
	 */
	protected final void removeAlignLeft(Control control) {
		this.leftControlAligner.remove(control);
	}

	/**
	 * Removes the given pane's widgets (those that were registered with
	 * its right <code>ControlAligner</code>) from this pane's right
	 * <code>ControlAligner</code> so that their width will no longer be adjusted
	 * with the width of the widest widget.
	 *
	 * @param pane The pane containing the widgets to remove
	 *
	 * @category Layout
	 */
	protected final void removeAlignRight(AbstractPane<?> pane) {
		this.rightControlAligner.remove(pane.rightControlAligner);
	}

	/**
	 * Removes the given control from the collection of widgets that are aligned
	 * to have the same width when they are shown to the right side of the 3
	 * widget colums.
	 *
	 * @param pane The pane to remove, its width will no longer be
	 * ajusted to be the width of the longest widget
	 *
	 * @category Layout
	 */
	protected final void removeAlignRight(Control control) {
		this.rightControlAligner.remove(control);
	}

	/**
	 * Removes the given <code>AbstractPane</code>'s <code>Control</code> from
	 * having its enablement state managed by this pane. However, if this pane
	 * has its enablement state modified, the given pane will receive that
	 * notification.
	 *
	 * @param pane The pane to have its enablement state not managed by this pane
	 */
	public void removeFromEnablementControl(AbstractPane<?> pane) {
		this.removeFromEnablementControl(pane.getControl());
		this.internalPanesForEnablementControl.add(pane);
	}

	/**
	 * Removes the given <code>Control</code> from having its enablement state
	 * being managed by this pane.
	 *
	 * @param control The <code>Control</code> that has its enablement state
	 * manually controlled
	 */
	public void removeFromEnablementControl(Control control) {
		control.setData("enablement", Boolean.FALSE);
	}

	/**
	 * Removes the given pane's controls (those that were registered for
	 * alignment) from this pane.
	 *
	 * @param pane The pane containing the widgets that no longer
	 * requires their width adjusted with the width of the longest widget
	 *
	 * @category Layout
	 */
	protected final void removePaneForAlignment(AbstractPane<?> pane) {
		removeAlignLeft(pane);
		removeAlignRight(pane);
	}

	/**
	 * This method is called (perhaps internally) when this needs to repopulate
	 * but the object of interest has not changed.
	 *
	 * @category Populate
	 */
	protected final void repopulate() {

		this.log(Tracing.UI_LAYOUT, "   ->repopulate()");

		// Populate this pane
		try {
			setPopulating(true);
			doPopulate();
		}
		finally {
			setPopulating(false);
		}

		// Ask the sub-panes to repopulate themselves
		for (AbstractPane<?> subPane : this.subPanes) {
			subPane.repopulate();
		}
	}

	/**
	 * Determines whether this pane should be repopulate even if the subject if
	 * <code>null</code>.
	 *
	 * @return <code>true</code> is returned by default
	 * @category Populate
	 */
	protected boolean repopulateWithNullSubject() {
		return true;
	}

	/**
	 * Sets the internal flag that is used to determine whether the pane is being
	 * populated or not. During population, it is required to not update the
	 * widgets when the model is updated nor to update the model when the widgets
	 * are being synchronized with the model's values.
	 *
	 * @param populating
	 *
	 * @category Populate
	 */
	protected final void setPopulating(boolean populating) {
		this.populating = populating;
	}

	/**
	 * Either show or hides this pane.
	 *
	 * @param visible The new visibility state
	 */
	public void setVisible(boolean visible) {
		if (!this.container.isDisposed()) {
			this.container.setVisible(visible);
		}
	}

	/**
	 * Returns the nearest <code>Shell</code> displaying the main widget of this
	 * pane.
	 *
	 * @return The nearest window displaying this pane
	 */
	protected final Shell shell() {
		return this.container.getShell();
	}

	/**
	 * Returns the subject of this pane.
	 *
	 * @return The subject if this pane was not disposed; <code>null</code>
	 * if it was
	 *
	 * @category Populate
	 */
	protected T subject() {
		return this.subjectHolder.getValue();
	}

	/**
	 * The subject has changed, disconnects any listeners from the old subject
	 * and connects those listeners onto the new subject.
	 *
	 * @param oldsubject The old subject or <code>null</code> if none was set
	 * @param newSubject The new subject or <code>null</code> if none needs to be
	 * set
	 *
	 * @category Populate
	 */
	protected final void subjectChanged(T oldSubject, T newSubject) {
		if (!this.container.isDisposed()) {

			this.log(Tracing.UI_LAYOUT, "subjectChanged()");
			this.disengageListeners(oldSubject);

			// Only repopulate if it is allowed when the subject is null
			if (newSubject != null ||
			   (newSubject == null && repopulateWithNullSubject()))
			{
				this.repopulate();
			}

			this.engageListeners(newSubject);
		}
	}

	/**
	 * Unregisters the given <code>AbstractSubjectPane</code> from this one so it
	 * can no longer be automatically notified about certain events such as
	 * engaging or disengaging the listeners, etc.
	 *
	 * @param subPane The sub-pane to unregister
	 *
	 * @category Controller
	 */
	protected final void unregisterSubPane(AbstractPane<?> subPane) {
		this.subPanes.remove(subPane);
	}

	private void updatePane(String propertyName) {
		if (!isPopulating() && !container.isDisposed()) {
			populating = true;

			try {
				propertyChanged(propertyName);
			}
			finally {
				populating = false;
			}
		}
	}
}