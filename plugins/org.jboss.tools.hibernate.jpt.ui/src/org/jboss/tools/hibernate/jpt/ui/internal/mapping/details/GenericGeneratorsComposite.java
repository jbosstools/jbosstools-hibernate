/*******************************************************************************
 * Copyright (c) 2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.mapping.details;

import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.jpt.core.context.Generator;
import org.eclipse.jpt.ui.internal.mappings.db.SequenceCombo;
import org.eclipse.jpt.ui.internal.util.ControlEnabler;
import org.eclipse.jpt.ui.internal.widgets.AddRemoveListPane;
import org.eclipse.jpt.ui.internal.widgets.Pane;
import org.eclipse.jpt.ui.internal.widgets.AddRemovePane.Adapter;
import org.eclipse.jpt.utility.internal.CollectionTools;
import org.eclipse.jpt.utility.internal.StringConverter;
import org.eclipse.jpt.utility.internal.model.value.ItemPropertyListValueModelAdapter;
import org.eclipse.jpt.utility.internal.model.value.ListAspectAdapter;
import org.eclipse.jpt.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.utility.internal.model.value.SimpleListValueModel;
import org.eclipse.jpt.utility.internal.model.value.SimplePropertyValueModel;
import org.eclipse.jpt.utility.internal.model.value.TransformationPropertyValueModel;
import org.eclipse.jpt.utility.internal.model.value.swing.ObjectListSelectionModel;
import org.eclipse.jpt.utility.model.value.ListValueModel;
import org.eclipse.jpt.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.utility.model.value.WritablePropertyValueModel;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.hibernate.jpt.core.internal.context.GenericGenerator;
import org.jboss.tools.hibernate.jpt.core.internal.context.GenericGeneratorHolder;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.HibernateJavaIdMapping;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.JavaGenericGeneratorImpl;
import org.jboss.tools.hibernate.jpt.ui.internal.widgets.EnterNameDialog;

/**
 * @author Dmitry Geraskov
 *
 *
 * Here the layout of this pane:
 * <pre>
 * -----------------------------------------------------------------------------
 * |                     ----------------------------------------------------- |
 * | Name:               | I                                                 | |
 * |                     ----------------------------------------------------- |
 * |                     ----------------------------------------------------- |
 * | Sequence 		   : | SequenceCombo                                     | |
 * |                     ----------------------------------------------------- |
 * | - Parameters  ----------------------------------------------------------- |
 * | | --------------------------------------------------------------------- | |
 * | | |                                                                   | | |
 * | | | ParametersComposite                                               | | |
 * | | |                                                                   | | |
 * | | --------------------------------------------------------------------- | |
 * | ------------------------------------------------------------------------- |
 * -----------------------------------------------------------------------------</pre>
 *
 * @see HibernateGeneratorsComposite
 * @see GenericGenerator
 * @see HibernateGenerationComposite - The parent container
 * @see SequenceCombo
 *
 * @version 2.0
 * @since 1.0
 */
public class GenericGeneratorsComposite extends Pane<GenericGeneratorHolder> {
	
	private AddRemoveListPane<GenericGeneratorHolder> listPane;
	private WritablePropertyValueModel<GenericGenerator> generatorHolder;

	public GenericGeneratorsComposite(Pane<? extends GenericGeneratorHolder> parentPane, Composite parent) {
		super(parentPane, parent, false);
	}
	
	private void addGenericGenerator() {
		Set<String> generatorNames = new HashSet<String>();
		for (Iterator<Generator> generators = this.getSubject().getPersistenceUnit().generators(); generators.hasNext(); ) {
			generatorNames.add(generators.next().getName());
		}
		EnterNameDialog dialog = new EnterNameDialog(getControl().getShell(),
			HibernateUIMappingMessages.GenericGeneratorsComposite_addGeneratorNameDescription,
			null,
			generatorNames);
		if (dialog.open() != Window.OK) {
			return;
		}
		String name = dialog.getName();
		GenericGenerator generator = getSubject().addGenericGenerator(getSubject().genericGeneratorsSize());
		generator.setName(name);
	}
	
	private ListValueModel<GenericGenerator> buildDisplayableGeneratorsListHolder() {
		return new ItemPropertyListValueModelAdapter<GenericGenerator>(
			buildGeneratorsListHolder(),
			GenericGenerator.NAME_PROPERTY
		);
	}
	
	private ListValueModel<GenericGenerator> buildGeneratorsListHolder() {
		return new ListAspectAdapter<GenericGeneratorHolder, GenericGenerator>(
				getSubjectHolder(),
				GenericGeneratorHolder.GENERIC_GENERATORS_LIST)
			{
				@Override
				protected ListIterator<GenericGenerator> listIterator_() {
					return this.subject.genericGenerators();
				}

				@Override
				protected int size_() {
					return this.subject.genericGeneratorsSize();
				}
			};
	}
	
	private AddRemoveListPane<GenericGeneratorHolder> addListPane(Composite container) {

		return new AddRemoveListPane<GenericGeneratorHolder>(
			this,
			container,
			buildGenericGeneratorsAdapter(),
			buildDisplayableGeneratorsListHolder(),
			this.generatorHolder,
			buildGeneratorsListLabelProvider(),
			null//TODO help
		);
	}
	
	private Adapter buildGenericGeneratorsAdapter() {

		return new AddRemoveListPane.AbstractAdapter() {

			public void addNewItem(ObjectListSelectionModel listSelectionModel) {
				addGenericGenerator();
			}

			public void removeSelectedItems(ObjectListSelectionModel listSelectionModel) {
				for (Object item : listSelectionModel.selectedValues()) {
					getSubject().removeGenericGenerator((GenericGenerator) item);
				}
			}
		};
	}


	private ILabelProvider buildGeneratorsListLabelProvider() {
		return new LabelProvider() {
			@Override
			public String getText(Object element) {
				GenericGenerator genericGenerator = (GenericGenerator) element;
				String name = genericGenerator.getName();

				if (name == null) {
					int index = CollectionTools.indexOf(getSubject().genericGenerators(), genericGenerator);
					name = NLS.bind(HibernateUIMappingMessages.GenericGeneratorsComposite_generatorNullName, index);
				}

				return name;
			}
		};
	}

	private WritablePropertyValueModel<GenericGenerator> buildGenericGeneratorHolder() {
		return new SimplePropertyValueModel<GenericGenerator>();
	}

	@Override
	public void enableWidgets(boolean enabled) {
		super.enableWidgets(enabled);
		this.listPane.enableWidgets(enabled);
	}

	@Override
	protected void initialize() {
		super.initialize();
		this.generatorHolder = buildGenericGeneratorHolder();
	}

	@Override
	protected void initializeLayout(Composite container) {

		if (getSubject() instanceof HibernateJavaIdMapping){
			// Name widgets
			addLabeledText(
				container,
				HibernateUIMappingMessages.GenericGeneratorComposite_name,
				buildGeneratorNameHolder(),
				null//TODO add help
			);
		} else {
			// List pane
			this.listPane = addListPane(container);
		}		
		
		Combo c = addLabeledEditableCombo(
			container,
			HibernateUIMappingMessages.GenericGeneratorComposite_strategy,
			new SimpleListValueModel<String>(JavaGenericGeneratorImpl.generatorClasses),
			buildStrategyHolder(),
			StringConverter.Default.<String>instance(),
			null);//TODO add help
		new ControlEnabler(buildControlEnabler(), c);	
		
		new ParametersComposite(this, container, generatorHolder);
		
			
	}
	
	private PropertyValueModel<Boolean> buildControlEnabler() {
		return new TransformationPropertyValueModel<GenericGenerator, Boolean>(generatorHolder){
			public Boolean transform(GenericGenerator generator) {
				return generator != null;
			}
		};
	}
	
	protected final WritablePropertyValueModel<String> buildGeneratorNameHolder() {
		return new PropertyAspectAdapter<GenericGenerator, String>(this.generatorHolder, GenericGenerator.NAME_PROPERTY) {
			@Override
			protected String buildValue_() {
				return subject == null ? null : subject.getName();
			}

			@Override
			public void setValue(String value) {
				if (subject != null) {
					setValue_(value);
					return;
				}
				
				if ("".equals(value)){ //$NON-NLS-1$
					return;
				}
				
				GenericGenerator generator = 
					(getSubject().genericGeneratorsSize() == 0) ? getSubject().addGenericGenerator(0)
																: getSubject().genericGenerators().next();

				generator.setName(value);
				generatorHolder.setValue(generator);
			}

			@Override
			protected void setValue_(String value) {
				if ("".equals(value)) {//$NON-NLS-1$
					value = null;
				}
				subject.setName(value);
			}
		};
	}

	protected WritablePropertyValueModel<String> buildStrategyHolder() {
		return new PropertyAspectAdapter<GenericGenerator, String>(this.generatorHolder,
				GenericGenerator.GENERIC_STRATEGY_PROPERTY) {
			@Override
			protected String buildValue_() {
				return subject == null ? null : subject.getStrategy();
			}
			
			@Override
			public void setValue(String value) {
				if (subject != null) {
					setValue_(value);
					return;
				}
				
				if ("".equals(value)){ //$NON-NLS-1$
					return;
				}
				
				GenericGenerator generator = 
					(getSubject().genericGeneratorsSize() == 0) ? getSubject().addGenericGenerator(0)
																: getSubject().genericGenerators().next();

				generator.setStrategy(value);
			}

			@Override
			protected void setValue_(String value) {
				if ("".equals(value)) {//$NON-NLS-1$
					value = null;
				}
				subject.setStrategy(value);
			}
		};
	}
	

}
