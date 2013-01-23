package org.jboss.tools.hibernate.jpt.ui.internal.mapping.details;

import org.eclipse.jpt.common.ui.internal.widgets.IntegerCombo;
import org.eclipse.jpt.common.ui.internal.widgets.Pane;
import org.eclipse.jpt.common.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.common.utility.model.value.ModifiablePropertyValueModel;
import org.eclipse.jpt.common.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.jpa.core.JpaProject;
import org.eclipse.jpt.jpa.core.context.DbGenerator;
import org.eclipse.jpt.jpa.core.context.Generator;
import org.eclipse.swt.widgets.Composite;

public abstract class GeneratorComposite<T extends DbGenerator> extends Pane<T>
{

	protected GeneratorBuilder<T> generatorBuilder;

	protected GeneratorComposite(Pane<?> parentPane,
        PropertyValueModel<T> subjectHolder,
        Composite parent,
        GeneratorBuilder<T> generatorBuilder) {

		super(parentPane, subjectHolder, parent);
		this.generatorBuilder = generatorBuilder;
	}

	protected final T buildGenerator() {
		return this.generatorBuilder.addGenerator();
	}

	protected final T retrieveGenerator() {
		T generator = getSubject();

		if (generator == null) {
			generator = this.buildGenerator();
		}

		return generator;
	}

	protected final ModifiablePropertyValueModel<String> buildGeneratorNameHolder() {
		return new PropertyAspectAdapter<Generator, String>(getSubjectHolder(), Generator.NAME_PROPERTY) {
			@Override
			protected String buildValue_() {
				return this.subject.getName();
			}

			@Override
			public void setValue(String value) {
				if (this.subject != null) {
					setValue_(value);
					return;
				}
				if (value.length() == 0) {
					return;
				}
				retrieveGenerator().setName(value);
			}

			@Override
			protected void setValue_(String value) {
				if (value.length() == 0) {
					value = null;
				}
				this.subject.setName(value);
			}
		};
	}

	protected void addAllocationSizeCombo(Composite container) {
		new IntegerCombo<DbGenerator>(this, getSubjectHolder(), container) {

			@Override
			protected String getHelpId() {
				return null;//JpaHelpContextIds.MAPPING_COLUMN_LENGTH;
			}

			@Override
			protected PropertyValueModel<Integer> buildDefaultHolder() {
				return new PropertyAspectAdapter<DbGenerator, Integer>(getSubjectHolder(), DbGenerator.DEFAULT_ALLOCATION_SIZE_PROPERTY) {
					@Override
					protected Integer buildValue_() {
						return Integer.valueOf(this.subject.getDefaultAllocationSize());
					}
				};
			}
			
			@Override
			protected ModifiablePropertyValueModel<Integer> buildSelectedItemHolder() {
				return new PropertyAspectAdapter<DbGenerator, Integer>(getSubjectHolder(), DbGenerator.SPECIFIED_ALLOCATION_SIZE_PROPERTY) {
					@Override
					protected Integer buildValue_() {
						return this.subject.getSpecifiedAllocationSize();
					}

					@Override
					public void setValue(Integer value) {
						retrieveGenerator().setSpecifiedAllocationSize(value);
					}
				};
			}
		};	
	}
	
	protected void addInitialValueCombo(Composite container) {
		new IntegerCombo<DbGenerator>(this, getSubjectHolder(), container) {
			@Override
			protected String getHelpId() {
				return null;//JpaHelpContextIds.MAPPING_COLUMN_LENGTH;
			}

			@Override
			protected PropertyValueModel<Integer> buildDefaultHolder() {
				return new PropertyAspectAdapter<DbGenerator, Integer>(getSubjectHolder(), DbGenerator.DEFAULT_INITIAL_VALUE_PROPERTY) {
					@Override
					protected Integer buildValue_() {
						return Integer.valueOf(this.subject.getDefaultInitialValue());
					}
				};
			}
			
			@Override
			protected ModifiablePropertyValueModel<Integer> buildSelectedItemHolder() {
				return new PropertyAspectAdapter<DbGenerator, Integer>(getSubjectHolder(), DbGenerator.SPECIFIED_INITIAL_VALUE_PROPERTY) {
					@Override
					protected Integer buildValue_() {
						return this.subject.getSpecifiedInitialValue();
					}

					@Override
					public void setValue(Integer value) {
						retrieveGenerator().setSpecifiedInitialValue(value);
					}
				};
			}
		};	
	}

	protected final JpaProject getJpaProject() {
		return this.getSubject() == null ? null : this.getSubject().getJpaProject();
	}

	protected abstract String getPropertyName();


	public interface GeneratorBuilder<T> {
		T addGenerator();
	}
}