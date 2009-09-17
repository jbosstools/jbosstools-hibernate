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
package org.jboss.tools.hibernate.ui.diagram.editors.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.hibernate.cfg.Configuration;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.hibernate.mapping.RootClass;
import org.jboss.tools.hibernate.ui.diagram.UiPlugin;
import org.jboss.tools.hibernate.ui.diagram.editors.model.Connection.ConnectionType;
import org.jboss.tools.hibernate.ui.diagram.rulers.DiagramRuler;
import org.jboss.tools.hibernate.ui.view.OrmLabelProvider;

/**
 * The whole diagram, all information about diagram elements are here.
 * @author some modifications from Vitali
 * @see BaseElement
*/
public class OrmDiagram extends BaseElement {
	
	// special folder name to store OrmDiagram layout and settings
	public static final String HIBERNATE_MAPPING_LAYOUT_FOLDER_NAME = "hibernateMapping"; //$NON-NLS-1$
	public static final String DIRTY = "dirty"; //$NON-NLS-1$
	public static final String AUTOLAYOUT = "autolayout"; //$NON-NLS-1$
	
	// hibernate console configuration is the source of diagram elements 
	private ConsoleConfiguration consoleConfig;
	private OrmLabelProvider labelProvider = new OrmLabelProvider();

	private	boolean dirty = false;
	private HashMap<String, OrmShape> elements = new HashMap<String, OrmShape>();
	private RootClass[] ormElements;
	private String[] entityNames;
	private boolean connectionsVisibilityClassMapping = true;
	private boolean connectionsVisibilityPropertyMapping = true;
	private boolean connectionsVisibilityAssociation = true;
	private boolean connectionsVisibilityForeignKeyConstraint = true;
	private ArrayList<Connection> connections = new ArrayList<Connection>();
	
	// editor elements settings
	protected DiagramRuler leftRuler, topRuler;
	private boolean rulersVisibility = false;
	private boolean snapToGeometry = false;
	private boolean gridEnabled = false;
	private double zoom = 1.0;
	private float fontHeight = 8.5f;
	private boolean deepIntoSort = false;

	//
	private boolean fileLoadSuccessfull = false;
	
	public OrmDiagram(ConsoleConfiguration consoleConfig, RootClass ioe) {
		initFontHeight();
		createRulers();
		this.consoleConfig = consoleConfig;
		labelProvider.setConfig(consoleConfig.getConfiguration());
		ormElements = new RootClass[1];
		ormElements[0] = ioe;
		entityNames = new String[1];
		entityNames[0] = ioe.getEntityName();
		recreateChildren();
		sortChildren(deepIntoSort);
		loadFromFile();
		refreshDiagramElements();
		setDirty(false);
	}
	
	public OrmDiagram(ConsoleConfiguration consoleConfig, RootClass[] ioe) {
		initFontHeight();
		createRulers();
		this.consoleConfig = consoleConfig;
		labelProvider.setConfig(consoleConfig.getConfiguration());
		ormElements = new RootClass[ioe.length];
		System.arraycopy(ioe, 0, ormElements, 0, ioe.length);
		// should sort elements - cause different sort order gives different file name
		// for the same thing
		Arrays.sort(ormElements, new OrmElCompare());
		entityNames = new String[ioe.length];
		for (int i = 0; i < ormElements.length; i++) {
			entityNames[i] = ormElements[i].getEntityName();
		}
		recreateChildren();
		sortChildren(deepIntoSort);
		loadFromFile();
		refreshDiagramElements();
		setDirty(false);
	}
	
	protected void recreateChildren() {
		deleteChildren();
		elements.clear();
		connections.clear();
		final ElementsFactory factory = new ElementsFactory(
			consoleConfig.getConfiguration(), elements, connections);
		for (int i = 0; i < ormElements.length; i++) {
			factory.getOrCreatePersistentClass(ormElements[i], null);
		}
		updateChildrenList();
		factory.createChildren(this);
		updateChildrenList();
	}

	protected void updateChildrenList() {
		Iterator<OrmShape> it = elements.values().iterator();
		while (it.hasNext()) {
			OrmShape ormShape = it.next();
			addChild(ormShape);
		}
	}

	protected void refreshDiagramElements() {
		Iterator<OrmShape> it = elements.values().iterator();
		while (it.hasNext()) {
			OrmShape ormShape = it.next();
			ormShape.refresh();
		}
		for (int i = 0; i < connections.size(); i++) {
			connections.get(i).refresh();
		}
	}

	protected void createRulers() {
		leftRuler = new DiagramRuler(false);
		topRuler = new DiagramRuler(true);
	}
	
	protected class OrmElCompare implements Comparator<RootClass> {

		public int compare(RootClass o1, RootClass o2) {
			return o1.getNodeName().compareTo(o2.getNodeName());
		}
		
	}

	/**
	 * It has no parent
	 */
	@Override
	public BaseElement getParent() {
		return null;
	}
	
	public IPath getStoreFolderPath() {
		IPath storePath = null;
		IJavaProject javaProject = ProjectUtils.findJavaProject(consoleConfig);
		if (javaProject != null && javaProject.getProject() != null) {
			storePath = javaProject.getProject().getLocation();
		}
		else {
			storePath = UiPlugin.getDefault().getStateLocation(); 
		}
		return storePath.append(".settings").append(HIBERNATE_MAPPING_LAYOUT_FOLDER_NAME); //$NON-NLS-1$
	}

	public IPath getStoreFilePath() {
		return getStoreFolderPath().append(getStoreFileName());
	}

	/**
	 * Generate file name to store diagram. File name consist of elements names,
	 * in case if result of elements names is too long md5sum calculated for generated name.
	 * @return
	 */
	public String getStoreFileName() {
		StringBuilder name = new StringBuilder();
		for (int i = 0; i < ormElements.length; i++) {
			name.append("_"); //$NON-NLS-1$
			name.append(ormElements[i].getNodeName());
		}
		String res = consoleConfig.getName() + name.toString();
		if (res.length() > 64) {
			res = consoleConfig.getName() + "_" + md5sum(name.toString()); //$NON-NLS-1$
		}
		return res;
	}
	
	public static final String md5sum(String input) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5"); //$NON-NLS-1$
		} catch (NoSuchAlgorithmException e) {
		}
		if (md == null || input == null) {
			return input;
		}
		StringBuffer sbuf = new StringBuffer();
		byte [] raw = md.digest(input.getBytes());
		for (int i = 0; i < raw.length; i++) {
			int c = (int)raw[i];
			if (c < 0) {
				c = (Math.abs(c) - 1) ^ 255;
			}
			final String block = toHex(c >>> 4) + toHex(c & 15);
			sbuf.append(block);
		}
		return sbuf.toString();
	}

	private static final String toHex(int s) {
		if (s < 10) {
			return String.valueOf((char)('0' + s));
		}
		return String.valueOf((char)('a' + (s - 10)));
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, OrmShape> getCloneElements() {
		return (HashMap<String, OrmShape>)elements.clone();
	}

	public RootClass getOrmElement(int idx) {
		if (0 > idx || idx >= ormElements.length) {
			return null;
		}
		return ormElements[idx];
	}

	public RootClass[] getOrmElements() {
		return ormElements;
	}

	@Override
	public void refresh() {
		final Configuration config = consoleConfig.getConfiguration();
		for (int i = 0; i < ormElements.length; i++) {
			RootClass newOrmElement = (RootClass)config.getClassMapping(entityNames[i]);
			if (ormElements[i].equals(newOrmElement)) {
				continue;
			}
			ormElements[i] = newOrmElement;
		}
		// -> save just into properties
		Properties properties = new Properties();
		saveInProperties(properties);
		recreateChildren();
		sortChildren(deepIntoSort);
		// -> load just from properties
		loadFromProperties(properties);
		refreshDiagramElements();
		updateDirty(true);
		super.refresh();
	}
	
	public void collapseAll() {
		toggleModelExpandState(this, false);
	}
	
	public void expandAll() {
		toggleModelExpandState(this, true);
	}
	
	private void toggleModelExpandState(BaseElement element, final boolean expandState) {
		if (element instanceof OrmShape) {
			OrmShape ormShape = (OrmShape)element;
			if (expandState != ormShape.isExpanded()) {
				if (expandState) {
					ormShape.expand();
				} else {
					ormShape.collapse();
				}
			}
		}
		Iterator<Shape> it = element.getChildrenList().iterator();
		while (it.hasNext()) {
			toggleModelExpandState(it.next(), expandState);
		}
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		if (this.dirty != dirty) {
			this.dirty = dirty;
			firePropertyChange(DIRTY, null, null);
		}
	}

	public void updateDirty(boolean dirtyUpdate) {
		if (dirtyUpdate) {
			setDirty(true);
		}
	}
	
	public void autolayout() {
		firePropertyChange(AUTOLAYOUT, null, null);
	}
	
	public boolean getConnectionsVisibilityAssociation() {
		return connectionsVisibilityAssociation;
	}
	
	public void setConnectionsVisibilityAssociation(boolean connectionsVisibilityAssociation) {
		if (this.connectionsVisibilityAssociation == connectionsVisibilityAssociation) {
			return;
		}
		this.connectionsVisibilityAssociation = connectionsVisibilityAssociation;
		for (Connection connection : connections) {
			ConnectionType ct = connection.getConnectionType();
			if (ct == ConnectionType.Association) {
				connection.setVisible(connectionsVisibilityAssociation);
			}
		}
	}
	
	public boolean getConnectionsVisibilityClassMapping() {
		return connectionsVisibilityClassMapping;
	}
	
	public void setConnectionsVisibilityClassMapping(boolean connectionsVisibilityClassMapping) {
		if (this.connectionsVisibilityClassMapping == connectionsVisibilityClassMapping) {
			return;
		}
		this.connectionsVisibilityClassMapping = connectionsVisibilityClassMapping;
		for (Connection connection : connections) {
			ConnectionType ct = connection.getConnectionType();
			if (ct == ConnectionType.ClassMapping) {
				connection.setVisible(connectionsVisibilityClassMapping);
			}
		}
	}
	
	public boolean getConnectionsVisibilityForeignKeyConstraint() {
		return connectionsVisibilityForeignKeyConstraint;
	}
	
	public void setConnectionsVisibilityForeignKeyConstraint(boolean connectionsVisibilityForeignKeyConstraint) {
		if (this.connectionsVisibilityForeignKeyConstraint == connectionsVisibilityForeignKeyConstraint) {
			return;
		}
		this.connectionsVisibilityForeignKeyConstraint = connectionsVisibilityForeignKeyConstraint;
		for (Connection connection : connections) {
			ConnectionType ct = connection.getConnectionType();
			if (ct == ConnectionType.ForeignKeyConstraint) {
				connection.setVisible(connectionsVisibilityForeignKeyConstraint);
			}
		}
	}
	
	public boolean getConnectionsVisibilityPropertyMapping() {
		return connectionsVisibilityPropertyMapping;
	}
	
	public void setConnectionsVisibilityPropertyMapping(boolean connectionsVisibilityPropertyMapping) {
		if (this.connectionsVisibilityPropertyMapping == connectionsVisibilityPropertyMapping) {
			return;
		}
		this.connectionsVisibilityPropertyMapping = connectionsVisibilityPropertyMapping;
		for (Connection connection : connections) {
			ConnectionType ct = connection.getConnectionType();
			if (ct == ConnectionType.PropertyMapping) {
				connection.setVisible(connectionsVisibilityPropertyMapping);
			}
		}
	}
	
	@Override
	protected void loadFromProperties(Properties properties) {
		super.loadFromProperties(properties);
		String str = properties.getProperty("rulersVisibility", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		rulersVisibility = Boolean.valueOf(str).booleanValue();
		str = properties.getProperty("snapToGeometry", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		snapToGeometry = Boolean.valueOf(str).booleanValue();
		str = properties.getProperty("gridEnabled", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		gridEnabled = Boolean.valueOf(str).booleanValue();
		str = properties.getProperty("zoom", "1.0"); //$NON-NLS-1$ //$NON-NLS-2$
		zoom = Double.valueOf(str).doubleValue();
		str = properties.getProperty("deepIntoSort", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		deepIntoSort = Boolean.valueOf(str).booleanValue();
		str = properties.getProperty("connectionsVisibilityAssociation", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		connectionsVisibilityAssociation = Boolean.valueOf(str).booleanValue();
		str = properties.getProperty("connectionsVisibilityClassMapping", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		connectionsVisibilityClassMapping = Boolean.valueOf(str).booleanValue();
		str = properties.getProperty("connectionsVisibilityForeignKeyConstraint", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		connectionsVisibilityForeignKeyConstraint = Boolean.valueOf(str).booleanValue();
		str = properties.getProperty("connectionsVisibilityPropertyMapping", "true"); //$NON-NLS-1$ //$NON-NLS-2$
		connectionsVisibilityPropertyMapping = Boolean.valueOf(str).booleanValue();
	}

	@Override
	protected void saveInProperties(Properties properties) {
		properties.put("rulersVisibility", "" + rulersVisibility); //$NON-NLS-1$ //$NON-NLS-2$
		properties.put("snapToGeometry", "" + snapToGeometry); //$NON-NLS-1$ //$NON-NLS-2$
		properties.put("gridEnabled", "" + gridEnabled); //$NON-NLS-1$ //$NON-NLS-2$
		properties.put("zoom", "" + zoom); //$NON-NLS-1$ //$NON-NLS-2$
		properties.put("deepIntoSort", "" + deepIntoSort); //$NON-NLS-1$ //$NON-NLS-2$
		properties.put("connectionsVisibilityAssociation", "" + connectionsVisibilityAssociation); //$NON-NLS-1$ //$NON-NLS-2$
		properties.put("connectionsVisibilityClassMapping", "" + connectionsVisibilityClassMapping); //$NON-NLS-1$ //$NON-NLS-2$
		properties.put("connectionsVisibilityForeignKeyConstraint", "" + connectionsVisibilityForeignKeyConstraint); //$NON-NLS-1$ //$NON-NLS-2$
		properties.put("connectionsVisibilityPropertyMapping", "" + connectionsVisibilityPropertyMapping); //$NON-NLS-1$ //$NON-NLS-2$
		super.saveInProperties(properties);
	}
	
	public void saveInFile() {
		Properties properties = new Properties();
		saveInProperties(properties);
		FileOutputStream fos = null;
		try {
			File folder = new File(getStoreFolderPath().toOSString());
			if (!folder.exists()) {
				folder.mkdirs();
			}
			File file = new File(getStoreFilePath().toOSString());
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			properties.store(fos, ""); //$NON-NLS-1$
		} catch (IOException e) {
			HibernateConsolePlugin.getDefault().logErrorMessage("Can't save layout of mapping.", e); //$NON-NLS-1$
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	public IFile createLayoutFile(InputStream source) {
		IFile file = null;
		IPath path = getStoreFolderPath();
		IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(path);
		if (!folder.exists()) {
			try {
				folder.create(true, true, null);
				file = folder.getFile(getStoreFileName());
				if (!file.exists()) {
					file.create(source, true, null);
				}
			} catch (CoreException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage("CoreException: ", e); //$NON-NLS-1$
			}
		}
		return file;
	}

	public void loadFromFile() {
		fileLoadSuccessfull = false;
		FileInputStream fis = null;
		try {
			File file = new File(getStoreFilePath().toOSString());
			if (file.exists()) {
				fis = new FileInputStream(file);
				Properties properties = new Properties();
				properties.load(fis);
				loadFromProperties(properties);
				fileLoadSuccessfull = true;
			}
		} catch (IOException ex) {
			HibernateConsolePlugin.getDefault().logErrorMessage("Can't load layout of mapping.", ex); //$NON-NLS-1$
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}
	
	public boolean isFileLoadSuccessfull() {
		return fileLoadSuccessfull;
	}

	public ConsoleConfiguration getConsoleConfig() {
		return consoleConfig;
	}
	
	public DiagramRuler getRuler(int orientation) {
		DiagramRuler result = null;
		switch (orientation) {
			case PositionConstants.NORTH :
				result = topRuler;
				break;
			case PositionConstants.WEST :
				result = leftRuler;
				break;
		}
		return result;
	}

	public void setRulerVisibility(boolean newValue) {
		rulersVisibility = newValue;
	}

	public boolean getRulerVisibility() {
		return rulersVisibility;
	}

	public void setGridEnabled(boolean isEnabled) {
		gridEnabled = isEnabled;
	}

	public boolean isGridEnabled() {
		return gridEnabled;
	}

	public void setSnapToGeometry(boolean isEnabled) {
		snapToGeometry = isEnabled;
	}

	public boolean isSnapToGeometryEnabled() {
		return snapToGeometry;
	}

	public void setZoom(double zoom) {
		this.zoom = zoom;
	}

	public double getZoom() {
		return zoom;
	}
	
	protected void initFontHeight() {
		FontData fontData[] = Display.getCurrent().getSystemFont().getFontData();
		if (fontData.length > 0) {
			fontHeight = fontData[0].height;
		}
	}

	public void setFontHeight(float fontHeight) {
		this.fontHeight = fontHeight;
	}

	public float getFontHeight() {
		return fontHeight;
	}

	@Override
	public String getKey() {
		return null;
	}

	public OrmLabelProvider getLabelProvider() {
		return labelProvider;
	}
	
	public boolean isDeepIntoSort() {
		return deepIntoSort;
	}

	public void setDeepIntoSort(boolean deepIntoSort) {
		this.deepIntoSort = deepIntoSort;
	}
}
