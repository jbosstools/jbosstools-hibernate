/*******************************************************************************
 * Copyright (c) 2008-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.internal.persistence.details;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jpt.ui.WidgetFactory;
import org.eclipse.jpt.ui.details.JpaPageComposite;
import org.eclipse.jpt.ui.internal.listeners.SWTPropertyChangeListenerWrapper;
import org.eclipse.jpt.ui.internal.widgets.FormPane;
import org.eclipse.jpt.utility.internal.StringConverter;
import org.eclipse.jpt.utility.internal.model.value.PropertyAspectAdapter;
import org.eclipse.jpt.utility.internal.model.value.SimpleListValueModel;
import org.eclipse.jpt.utility.model.event.PropertyChangeEvent;
import org.eclipse.jpt.utility.model.listener.PropertyChangeListener;
import org.eclipse.jpt.utility.model.value.PropertyValueModel;
import org.eclipse.jpt.utility.model.value.WritablePropertyValueModel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.utils.DialogSelectionHelper;
import org.hibernate.eclipse.console.utils.DriverClassHelpers;
import org.hibernate.eclipse.launch.PathHelper;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.BasicHibernateProperties;
import org.jboss.tools.hibernate.jpt.ui.wizard.Messages;

/**
 * @author Dmitry Geraskov
 * 
 */
public class HibernatePropertiesComposite extends FormPane<BasicHibernateProperties> implements
		JpaPageComposite {

	private Text cfgFile;
	
	DriverClassHelpers helper;

	/**
	 * @param subjectHolder
	 * @param container
	 * @param widgetFactory
	 */
	public HibernatePropertiesComposite(PropertyValueModel<BasicHibernateProperties> subjectHolder,
			Composite container, WidgetFactory widgetFactory) {
		super(subjectHolder, container, widgetFactory);
	}

	protected void initializeLayout(Composite container) {		

		Composite section = addSection(container, Messages.HibernatePropertiesComposite_basic_properties);
		
		helper = new DriverClassHelpers();

		final SimpleListValueModel<String> lvmDialect = new SimpleListValueModel<String>(Arrays.asList(helper
				.getDialectNames()));
		PropertyValueModel<BasicHibernateProperties> p = getSubjectHolder();
		List<String> drivers = new ArrayList<String>();
		BasicHibernateProperties props = p.getValue();
		if (props != null) {
			String dialectClass = helper.getDialectClass(props.getDialect());
			String[] driverClasses = helper.getDriverClasses(dialectClass);
			drivers.addAll(Arrays.asList(driverClasses));
		}

		final SimpleListValueModel<String> lvmDriver = new SimpleListValueModel<String>(drivers);

		List<String> urls = new ArrayList<String>();
		if (props != null) {
			String driverClass = props.getDriver();
			String[] connectionURLS = helper.getConnectionURLS(driverClass);
			urls.addAll(Arrays.asList(connectionURLS));
		}
		final SimpleListValueModel<String> lvmUrl = new SimpleListValueModel<String>(urls);

		WritablePropertyValueModel<String> dialectHolder = buildDialectHolder();
		final WritablePropertyValueModel<String> driverHolder = buildDriverHolder();
		final WritablePropertyValueModel<String> urlHolder = buildUrlHolder();

		Button b = addButton(section, HibernateConsoleMessages.CodeGenerationSettingsTab_browse, createSetupAction());
		cfgFile = addLabeledText(section,
				HibernateConsoleMessages.ConsoleConfigurationPropertySource_config_file + ':', buildConfigFileHolder(),
				b, null);

		addLabeledEditableCombo(
				section, 
				HibernateConsoleMessages.NewConfigurationWizardPage_database_dialect,
				lvmDialect, 
				dialectHolder, 
				StringConverter.Default.<String>instance(),
				null);

		addLabeledEditableCombo(
				section, 
				HibernateConsoleMessages.NewConfigurationWizardPage_driver_class, 
				lvmDriver,
				driverHolder,
				StringConverter.Default.<String>instance(),
				null);
		
		addLabeledEditableCombo(
				section,
				HibernateConsoleMessages.NewConfigurationWizardPage_connection_url,
				lvmUrl,
				urlHolder,
				StringConverter.Default.<String>instance(),
				null);
				
		dialectHolder.addPropertyChangeListener(new SWTPropertyChangeListenerWrapper(
				new PropertyChangeListener() {
					public void propertyChanged(PropertyChangeEvent event) {
						String dialectClass = helper.getDialectClass((String) event.getNewValue());
						String[] driverClasses = helper.getDriverClasses(dialectClass);
						String driver = driverHolder.getValue();//save value
						lvmDriver.clear();
						lvmDriver.addAll(Arrays.asList(driverClasses));							
						driverHolder.setValue(driver);		//restore value	
					}
				}
			)
		);

		driverHolder.addPropertyChangeListener( new SWTPropertyChangeListenerWrapper(
				new PropertyChangeListener() {
					public void propertyChanged(PropertyChangeEvent event) {
						String driverClass = (String) event.getNewValue();
						String[] connectionURLS = helper.getConnectionURLS(driverClass);
						String url = urlHolder.getValue();//save value
						lvmUrl.clear();
						lvmUrl.addAll(Arrays.asList(connectionURLS));
						urlHolder.setValue(url);		//restore value
					}
				}
			) );

		addLabeledText(
				section, 
				HibernateConsoleMessages.NewConfigurationWizardPage_default_schema,
				buildSchemaDefaultHolder());
		
		addLabeledText(
				section, 
				HibernateConsoleMessages.NewConfigurationWizardPage_default_catalog,
				buildCatalogDefaultHolder());
		
		addLabeledText(
				section, 
				HibernateConsoleMessages.NewConfigurationWizardPage_user_name, 
				buildUsernameHolder());
		
		addLabeledText(
				section, 
				HibernateConsoleMessages.NewConfigurationWizardPage_password, 
				buildPasswordHolder());
	}

	private IPath getConfigurationFilePath() {
		return PathHelper.pathOrNull(cfgFile.getText());
	}

	private Runnable createSetupAction() {
		return new Runnable() {
			public void run() {
				IPath initialPath = getConfigurationFilePath();
				IPath[] paths = DialogSelectionHelper.chooseFileEntries(getControl().getShell(), initialPath,
						new IPath[0],
						HibernateConsoleMessages.ConsoleConfigurationMainTab_select_hibernate_cfg_xml_file,
						HibernateConsoleMessages.ConsoleConfigurationMainTab_choose_file_to_use_as_hibernate_cfg_xml,
						new String[] { HibernateConsoleMessages.ConsoleConfigurationMainTab_cfg_xml }, false, false,
						true);
				if (paths != null && paths.length == 1) {
					// TODO update to subpath
					cfgFile.setText((paths[0]).toOSString());
				}
			}
		};
	}

	private WritablePropertyValueModel<String> buildConfigFileHolder() {
		return new PropertyAspectAdapter<BasicHibernateProperties, String>(getSubjectHolder(),
				BasicHibernateProperties.CONFIG_FILE_PROPERTY) {
			@Override
			protected String buildValue_() {
				return subject.getConfigurationFile();
			}

			@Override
			protected void setValue_(String value) {
				if ("".equals(value))value = null;//$NON-NLS-1$
				subject.setConfigurationFile(value);
			}
		};
	}

	private WritablePropertyValueModel<String> buildDialectHolder() {
		return new PropertyAspectAdapter<BasicHibernateProperties, String>(getSubjectHolder(),
				BasicHibernateProperties.DIALECT_PROPERTY) {
			@Override
			protected String buildValue_() {
				return helper.getShortDialectName(subject.getDialect());
			}

			@Override
			protected void setValue_(String value) {
				if ("".equals(value))value = null; //$NON-NLS-1$
				subject.setDialect(helper.getDialectClass(value));
			}
		};
	}

	private WritablePropertyValueModel<String> buildDriverHolder() {
		return new PropertyAspectAdapter<BasicHibernateProperties, String>(getSubjectHolder(),
				BasicHibernateProperties.DRIVER_PROPERTY) {
			@Override
			protected String buildValue_() {
				return subject.getDriver();
			}

			@Override
			protected void setValue_(String value) {
				if ("".equals(value))value = null;//$NON-NLS-1$
				subject.setDriver(value);
			}
		};
	}

	private WritablePropertyValueModel<String> buildUrlHolder() {
		return new PropertyAspectAdapter<BasicHibernateProperties, String>(getSubjectHolder(),
				BasicHibernateProperties.URL_PROPERTY) {
			@Override
			protected String buildValue_() {
				return subject.getUrl();
			}

			@Override
			protected void setValue_(String value) {
				if ("".equals(value))value = null;//$NON-NLS-1$
				subject.setUrl(value);
			}
		};
	}

	private WritablePropertyValueModel<String> buildSchemaDefaultHolder() {
		return new PropertyAspectAdapter<BasicHibernateProperties, String>(getSubjectHolder(),
				BasicHibernateProperties.SCHEMA_DEFAULT_PROPERTY) {
			@Override
			protected String buildValue_() {
				return subject.getSchemaDefault();
			}

			@Override
			protected void setValue_(String value) {
				if ("".equals(value))value = null;//$NON-NLS-1$
				subject.setSchemaDefault(value);
			}
		};
	}

	private WritablePropertyValueModel<String> buildCatalogDefaultHolder() {
		return new PropertyAspectAdapter<BasicHibernateProperties, String>(getSubjectHolder(),
				BasicHibernateProperties.CATALOG_DEFAULT_PROPERTY) {
			@Override
			protected String buildValue_() {
				return subject.getCatalogDefault();
			}

			@Override
			protected void setValue_(String value) {
				if ("".equals(value))value = null;//$NON-NLS-1$
				subject.setCatalogDefault(value);
			}
		};
	}

	private WritablePropertyValueModel<String> buildUsernameHolder() {
		return new PropertyAspectAdapter<BasicHibernateProperties, String>(getSubjectHolder(),
				BasicHibernateProperties.USERNAME_PROPERTY) {
			@Override
			protected String buildValue_() {
				return subject.getUsername();
			}

			@Override
			protected void setValue_(String value) {
				if ("".equals(value))value = null;//$NON-NLS-1$
				subject.setUsername(value);
			}
		};
	}

	private WritablePropertyValueModel<String> buildPasswordHolder() {
		return new PropertyAspectAdapter<BasicHibernateProperties, String>(getSubjectHolder(),
				BasicHibernateProperties.PASSWORD_PROPERTY) {
			@Override
			protected String buildValue_() {
				return subject.getPassword();
			}

			@Override
			protected void setValue_(String value) {
				if ("".equals(value))value = null;//$NON-NLS-1$
				subject.setPassword(value);
			}
		};
	}

	public String getHelpID() {
		// TODO help
		return null;
	}

	public Image getPageImage() {
		return null;
	}

	public String getPageText() {
		return Messages.HibernatePropertiesComposite_hibernate;
	}
}
