package org.hibernate.eclipse.launch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.hibernate.console.ImageConstants;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.DialogSelectionHelper;
import org.hibernate.eclipse.console.utils.EclipseImages;
import org.hibernate.eclipse.console.wizards.UpDownListComposite;

public class ConsoleConfigurationMappingsTab extends ConsoleConfigurationTab {

	private UpDownListComposite mappingFilesViewer;
	
	public void createControl(Composite parent) {
		Composite composite = buildMappingFileTable(parent);
		setControl( composite );
	}

	private UpDownListComposite buildMappingFileTable(Composite parent) {
		mappingFilesViewer = new UpDownListComposite(parent, SWT.NONE, "Additonal mapping files (not listed in cfg.xml)") {
			protected Object[] handleAdd(int idx) {
				TableItem[] items = getTable().getItems();
				IPath[] exclude = new IPath[items.length];
				
				for (int i = 0; i < items.length; i++) {
					TableItem item = items[i];
					exclude[i] = (IPath) item.getData();			
				}
				
				return DialogSelectionHelper.chooseFileEntries(getShell(), null, exclude, "Add hbm.xml file", "Add a Hibernate Mapping file", new String[] { "hbm.xml" }, true, false, true);
			}

			protected void listChanged() {
				updateLaunchConfigurationDialog();
			}
		};
		
		GridData gd;
		gd = new GridData(GridData.FILL_BOTH);
		
		gd.horizontalSpan = 3;
		gd.verticalSpan = 1;
		
		mappingFilesViewer.setLayoutData( gd );
		return mappingFilesViewer;
	}

	public String getName() {
		return "Mappings";
	}

	private IPath[] getMappings() {
		Table table = mappingFilesViewer.getTable();
		TableItem[] items = table.getItems();
		IPath[] str = new IPath[items.length];
		for (int i = 0; i < items.length; i++) {
			TableItem item = items[i];
			IPath path = (IPath) item.getData();
			str[i] = path;			
		}
		return str;
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			mappingFilesViewer.clear();
			List mappings = configuration.getAttribute( IConsoleConfigurationLaunchConstants.FILE_MAPPINGS, Collections.EMPTY_LIST );
			IPath[] mapA = new IPath[mappings.size()];
			int i=0;
			for (Iterator iter = mappings.iterator(); iter.hasNext();) {
				String file = (String) iter.next();
				mapA[i++] = Path.fromPortableString( file );
			}
			mappingFilesViewer.add(mapA, false);
		}
		catch (CoreException e) {
			HibernateConsolePlugin.getDefault().log( e );
		}		
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		IPath[] mappings = getMappings();
		List l = new ArrayList();
		for (int i = 0; i < mappings.length; i++) {
			IPath path = mappings[i];
			l.add(path.toPortableString());
		}
		configuration.setAttribute( IConsoleConfigurationLaunchConstants.FILE_MAPPINGS, l );
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		
	}

	public Image getImage() {
		return EclipseImages.getImage( ImageConstants.MAPPEDCLASS );
	}
	
}
