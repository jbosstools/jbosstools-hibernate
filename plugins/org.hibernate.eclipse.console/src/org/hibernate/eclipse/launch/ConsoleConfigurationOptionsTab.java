package org.hibernate.eclipse.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.IInternalDebugUIConstants;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.hibernate.cfg.NamingStrategy;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.DialogSelectionHelper;
import org.xml.sax.EntityResolver;

public class ConsoleConfigurationOptionsTab extends ConsoleConfigurationTab {

	
	private Text entityResolverClassNameText;
	private Text namingStrategyClassNameText;
	
	public void createControl(Composite parent) {
		Font font = parent.getFont();
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 0;
		comp.setLayout(layout);
		comp.setFont(font);
	
		createNamingStrategyClassNameEditor( comp );
		createEntityResolverClassNameEditor( comp );
		
	}
	
	private void createNamingStrategyClassNameEditor(Composite parent) {
		Group group = createGroup( parent, "Naming strategy:" );
		namingStrategyClassNameText = createBrowseEditor( parent, group);
		createBrowseButton( group, new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleNamingStrategyBrowse();
			}
		});	 		
	}

	private void createEntityResolverClassNameEditor(Composite parent) {
		Group group = createGroup( parent, "Entity resolver:" );
		entityResolverClassNameText = createBrowseEditor( parent, group);
		createBrowseButton( group, new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleEntityResolverBrowse();
			}
		});	 		
	}


	public String getName() {
		return "Options";
	}

	public Image getImage() {
		return DebugUITools.getImage(IInternalDebugUIConstants.IMG_OBJS_COMMON_TAB);
	}
	
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			namingStrategyClassNameText.setText( configuration.getAttribute( IConsoleConfigurationLaunchConstants.NAMING_STRATEGY, "" ) );
			entityResolverClassNameText.setText( configuration.getAttribute( IConsoleConfigurationLaunchConstants.ENTITY_RESOLVER, "" ) );
		}
		catch (CoreException e) {
			HibernateConsolePlugin.getDefault().log(e);
		}			
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute( IConsoleConfigurationLaunchConstants.NAMING_STRATEGY, nonEmptyTrimOrNull( namingStrategyClassNameText ) );
		configuration.setAttribute( IConsoleConfigurationLaunchConstants.ENTITY_RESOLVER, nonEmptyTrimOrNull( entityResolverClassNameText ) );		
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		
	}

	
	private void handleEntityResolverBrowse() {
		String string = DialogSelectionHelper.chooseImplementation(EntityResolver.class.getName(), entityResolverClassNameText.getText(), "Select entity resolver class", getShell());
		if(string!=null) {
			entityResolverClassNameText.setText(string);
		}
	}
	
	private void handleNamingStrategyBrowse() {
		String string = DialogSelectionHelper.chooseImplementation(NamingStrategy.class.getName(), namingStrategyClassNameText.getText(), "Select naming strategy class", getShell());
		if(string!=null) {
			namingStrategyClassNameText.setText(string);
		}		
	}

}
