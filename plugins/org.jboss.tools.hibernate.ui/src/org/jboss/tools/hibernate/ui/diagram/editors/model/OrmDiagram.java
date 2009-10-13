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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;
import org.hibernate.cfg.Configuration;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.KnownConfigurations;
import org.hibernate.console.execution.ExecutionContext;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.ProjectUtils;
import org.hibernate.mapping.RootClass;
import org.jboss.tools.hibernate.ui.diagram.UiPlugin;
import org.jboss.tools.hibernate.ui.diagram.editors.model.Connection.ConnectionType;
import org.jboss.tools.hibernate.ui.diagram.rulers.DiagramRuler;
import org.jboss.tools.hibernate.ui.view.OrmLabelProvider;

/**
 * The whole diagram, all information about diagram elements are here.
 * @author Vitali Yemialianchyk
 * @see BaseElement
*/
public class OrmDiagram extends BaseElement {
	
	// special folder name to store OrmDiagram layout and settings
	public static final String HIBERNATE_MAPPING_LAYOUT_FOLDER_NAME = "hibernateMapping"; //$NON-NLS-1$
	public static final String DIRTY = "dirty"; //$NON-NLS-1$
	public static final String AUTOLAYOUT = "autolayout"; //$NON-NLS-1$
	
	// hibernate console configuration is the source of diagram elements 
	protected String consoleConfigName;
	protected ArrayList<RootClass> roots = new ArrayList<RootClass>();
	protected ArrayList<String> entityNames = new ArrayList<String>();

	private	boolean dirty = false;

	protected HashMap<String, OrmShape> elements = new HashMap<String, OrmShape>();
	protected ArrayList<Connection> connections = new ArrayList<Connection>();

	protected OrmLabelProvider labelProvider = new OrmLabelProvider();
	
	protected boolean connectionsVisibilityClassMapping = true;
	protected boolean connectionsVisibilityPropertyMapping = true;
	protected boolean connectionsVisibilityAssociation = true;
	protected boolean connectionsVisibilityForeignKeyConstraint = true;
	
	// editor elements settings
	protected DiagramRuler leftRuler, topRuler;
	protected boolean rulersVisibility = false;
	protected boolean snapToGeometry = false;
	protected boolean gridEnabled = false;
	protected double zoom = 1.0;
	protected float fontHeight = 8.5f;
	protected boolean deepIntoSort = false;
	//
	private boolean fileLoadSuccessfull = false;
	// this is workaround to load diagram state in the case if Console Config loaded later
	// so we can correctly refresh diagram state
	private IMemento memento = null;
	
	public class RootClassComparator implements Comparator<RootClass> {
		public int compare(RootClass o1, RootClass o2) {
			return getItemName(o1).compareTo(getItemName(o2));
		}
	}
	
	public OrmDiagram(String consoleConfigName, ArrayList<RootClass> rts) {
		initFontHeight();
		createRulers();
		this.consoleConfigName = consoleConfigName;
		@SuppressWarnings("unused")
		ConsoleConfiguration consoleConfig = getConsoleConfig();
		labelProvider.setConsoleConfigName(consoleConfigName);
		roots.addAll(rts);
		// should sort elements - cause different sort order gives different file name
		// for the same thing
		Collections.sort(roots, new RootClassComparator());
		entityNames.clear();
		for (int i = 0; i < roots.size(); i++) {
			entityNames.add(getItemFullName(roots.get(i)));
		}
		recreateChildren();
		sortChildren(deepIntoSort);
		if (consoleConfigName.length() > 0) {
			////loadFromFile();
			loadFromXmlFile();
		}
		refreshDiagramElements();
		setDirty(false);
	}

	public String getDiagramName() {
		String name = ""; //$NON-NLS-1$
		ArrayList<String> names = new ArrayList<String>();
		for (int i = 0; i < entityNames.size(); i++) {
			names.add(getItemName(entityNames.get(i)));
		}
		// sort to get same name for same combinations of entities
		Collections.sort(names);
		name = names.size() > 0 ? names.get(0) : ""; //$NON-NLS-1$
		for (int i = 1; i < names.size(); i++) {
			name += " & " + names.get(i); //$NON-NLS-1$
		}
		return name;
	}
	
	protected String getItemFullName(RootClass rootClass) {
		if (rootClass == null) {
			return ""; //$NON-NLS-1$
		}
		String res = rootClass.getEntityName();
		if (res == null) {
			res = rootClass.getClassName();
		}
		if (res == null) {
			res = rootClass.getNodeName();
		}
		return res;
	}
	
	protected String getItemName(String name) {
		String res = name;
		return res.substring(res.lastIndexOf(".") + 1); //$NON-NLS-1$
	}
	
	protected String getItemName(RootClass rootClass) {
		return getItemName(getItemFullName(rootClass));
	}
	
	public void recreateChildren() {
		deleteChildren();
		elements.clear();
		connections.clear();
		final ElementsFactory factory = new ElementsFactory(
			getConfig(), elements, connections);
		for (int i = 0; i < roots.size(); i++) {
			RootClass rc = roots.get(i);
			if (rc != null) {
				factory.getOrCreatePersistentClass(rc, null);
			}
		}
		updateChildrenList();
		factory.createChildren(this);
		updateChildrenList();
		if (getChildrenNumber() == 0) {
			addChild(new MessageShape());
		}
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
		ConsoleConfiguration consoleConfig = getConsoleConfig();
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
		for (int i = 0; i < entityNames.size(); i++) {
			name.append("_"); //$NON-NLS-1$
			name.append(getItemName(entityNames.get(i)));
		}
		String res = getConsoleConfigName() + name.toString();
		if (res.length() > 64) {
			res = getConsoleConfigName() + "_" + md5sum(name.toString()); //$NON-NLS-1$
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
		if (0 > idx || idx >= roots.size()) {
			return null;
		}
		return roots.get(idx);
	}

	public boolean refreshRootsFromNames() {
		final Configuration config = getConfig();
		if (config == null) {
			return false;
		}
		for (int i = 0; i < roots.size(); i++) {
			RootClass newOrmElement = (RootClass)config.getClassMapping(entityNames.get(i));
			if (roots.get(i) == null) {
				if (newOrmElement == null) {
					continue;
				}
			}
			else if (roots.get(i).equals(newOrmElement)) {
				continue;
			}
			roots.set(i, newOrmElement);
		}
		return true;
	}

	@Override
	public void refresh() {
		refreshRootsFromNames();
		// -> save just into properties
		Properties properties = new Properties();
		if (memento == null) {
			saveInProperties(properties);
		}
		recreateChildren();
		sortChildren(deepIntoSort);
		if (memento == null) {
			// -> load just from properties
			loadFromProperties(properties);
		} else {
			loadState(memento);
		}
		refreshDiagramElements();
		updateDirty(memento != null ? false : true);
		if (memento != null && getConsoleConfig() != null) {
			memento = null;
		}
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
	
	static public String getConsoleConfigName(IMemento memento) {
		String str = memento.getString("consoleConfig_name"); //$NON-NLS-1$
		if (str == null) {
			str = ""; //$NON-NLS-1$
		}
		return str;
	}
	
	@Override
	public void loadState(IMemento memento) {
		super.loadState(memento);
		consoleConfigName = getPrValue(memento, "consoleConfig_name", ""); //$NON-NLS-1$ //$NON-NLS-2$
		@SuppressWarnings("unused")
		ConsoleConfiguration consoleConfig = getConsoleConfig();
		labelProvider.setConsoleConfigName(consoleConfigName);
		int size = getPrValue(memento, "entityNames_size", 0); //$NON-NLS-1$
		roots.clear();
		entityNames.clear();
		for (int i = 0; i < size; i++) {
			roots.add(null);
			entityNames.add(getPrValue(memento, "entityNames_" + Integer.toString(i), "")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		rulersVisibility = getPrValue(memento, "rulersVisibility", false); //$NON-NLS-1$
		snapToGeometry = getPrValue(memento, "snapToGeometry", false); //$NON-NLS-1$
		gridEnabled = getPrValue(memento, "gridEnabled", false); //$NON-NLS-1$
		zoom = getPrValue(memento, "zoom", 1.0); //$NON-NLS-1$
		deepIntoSort = getPrValue(memento, "deepIntoSort", false); //$NON-NLS-1$
		connectionsVisibilityAssociation = getPrValue(memento, "connectionsVisibilityAssociation", true); //$NON-NLS-1$
		connectionsVisibilityClassMapping = getPrValue(memento, "connectionsVisibilityClassMapping", true); //$NON-NLS-1$
		connectionsVisibilityForeignKeyConstraint = getPrValue(memento, "connectionsVisibilityForeignKeyConstraint", true); //$NON-NLS-1$
		connectionsVisibilityPropertyMapping = getPrValue(memento, "connectionsVisibilityPropertyMapping", true); //$NON-NLS-1$
		refreshRootsFromNames();
	}

	@Override
	protected void loadFromProperties(Properties properties) {
		super.loadFromProperties(properties);
		consoleConfigName = getPrValue(properties, "consoleConfig_name", ""); //$NON-NLS-1$ //$NON-NLS-2$
		@SuppressWarnings("unused")
		ConsoleConfiguration consoleConfig = getConsoleConfig();
		labelProvider.setConsoleConfigName(consoleConfigName);
		int size = getPrValue(properties, "entityNames_size", 0); //$NON-NLS-1$
		roots.clear();
		entityNames.clear();
		for (int i = 0; i < size; i++) {
			roots.add(null);
			entityNames.add(getPrValue(properties, "entityNames_" + Integer.toString(i), "")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		rulersVisibility = getPrValue(properties, "rulersVisibility", false); //$NON-NLS-1$
		snapToGeometry = getPrValue(properties, "snapToGeometry", false); //$NON-NLS-1$
		gridEnabled = getPrValue(properties, "gridEnabled", false); //$NON-NLS-1$
		zoom = getPrValue(properties, "zoom", 1.0); //$NON-NLS-1$
		deepIntoSort = getPrValue(properties, "deepIntoSort", false); //$NON-NLS-1$
		connectionsVisibilityAssociation = getPrValue(properties, "connectionsVisibilityAssociation", true); //$NON-NLS-1$
		connectionsVisibilityClassMapping = getPrValue(properties, "connectionsVisibilityClassMapping", true); //$NON-NLS-1$
		connectionsVisibilityForeignKeyConstraint = getPrValue(properties, "connectionsVisibilityForeignKeyConstraint", true); //$NON-NLS-1$
		connectionsVisibilityPropertyMapping = getPrValue(properties, "connectionsVisibilityPropertyMapping", true); //$NON-NLS-1$
		refreshRootsFromNames();
	}

	@Override
	public void saveState(IMemento memento) {
		setPrValue(memento, "consoleConfig_name", consoleConfigName); //$NON-NLS-1$
		setPrValue(memento, "entityNames_size", "" + entityNames.size()); //$NON-NLS-1$ //$NON-NLS-2$
		for (int i = 0; i < entityNames.size(); i++) {
			setPrValue(memento, "entityNames_" + Integer.toString(i), "" + entityNames.get(i)); //$NON-NLS-1$ //$NON-NLS-2$
		}
		setPrValue(memento, "rulersVisibility", "" + rulersVisibility); //$NON-NLS-1$ //$NON-NLS-2$
		setPrValue(memento, "snapToGeometry", "" + snapToGeometry); //$NON-NLS-1$ //$NON-NLS-2$
		setPrValue(memento, "gridEnabled", "" + gridEnabled); //$NON-NLS-1$ //$NON-NLS-2$
		setPrValue(memento, "zoom", "" + zoom); //$NON-NLS-1$ //$NON-NLS-2$
		setPrValue(memento, "deepIntoSort", "" + deepIntoSort); //$NON-NLS-1$ //$NON-NLS-2$
		setPrValue(memento, "connectionsVisibilityAssociation", "" + connectionsVisibilityAssociation); //$NON-NLS-1$ //$NON-NLS-2$
		setPrValue(memento, "connectionsVisibilityClassMapping", "" + connectionsVisibilityClassMapping); //$NON-NLS-1$ //$NON-NLS-2$
		setPrValue(memento, "connectionsVisibilityForeignKeyConstraint", "" + connectionsVisibilityForeignKeyConstraint); //$NON-NLS-1$ //$NON-NLS-2$
		setPrValue(memento, "connectionsVisibilityPropertyMapping", "" + connectionsVisibilityPropertyMapping); //$NON-NLS-1$ //$NON-NLS-2$
		super.saveState(memento);
	}
	
	@Override
	protected void saveInProperties(Properties properties) {
		setPrValue(properties, "consoleConfig_name", consoleConfigName); //$NON-NLS-1$
		setPrValue(properties, "entityNames_size", "" + entityNames.size()); //$NON-NLS-1$ //$NON-NLS-2$
		for (int i = 0; i < entityNames.size(); i++) {
			setPrValue(properties, "entityNames_" + Integer.toString(i), "" + entityNames.get(i)); //$NON-NLS-1$ //$NON-NLS-2$
		}
		setPrValue(properties, "rulersVisibility", "" + rulersVisibility); //$NON-NLS-1$ //$NON-NLS-2$
		setPrValue(properties, "snapToGeometry", "" + snapToGeometry); //$NON-NLS-1$ //$NON-NLS-2$
		setPrValue(properties, "gridEnabled", "" + gridEnabled); //$NON-NLS-1$ //$NON-NLS-2$
		setPrValue(properties, "zoom", "" + zoom); //$NON-NLS-1$ //$NON-NLS-2$
		setPrValue(properties, "deepIntoSort", "" + deepIntoSort); //$NON-NLS-1$ //$NON-NLS-2$
		setPrValue(properties, "connectionsVisibilityAssociation", "" + connectionsVisibilityAssociation); //$NON-NLS-1$ //$NON-NLS-2$
		setPrValue(properties, "connectionsVisibilityClassMapping", "" + connectionsVisibilityClassMapping); //$NON-NLS-1$ //$NON-NLS-2$
		setPrValue(properties, "connectionsVisibilityForeignKeyConstraint", "" + connectionsVisibilityForeignKeyConstraint); //$NON-NLS-1$ //$NON-NLS-2$
		setPrValue(properties, "connectionsVisibilityPropertyMapping", "" + connectionsVisibilityPropertyMapping); //$NON-NLS-1$ //$NON-NLS-2$
		super.saveInProperties(properties);
	}
	
	public void saveInFile(IPath path, boolean format) {
		FileOutputStream fos = null;
		try {
			File file = new File(path.toOSString());
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			if (format) {
				XMLMemento memento = XMLMemento.createWriteRoot("OrmDiagram"); //$NON-NLS-1$
				saveState(memento);
				OutputStreamWriter writer = new OutputStreamWriter(fos, "utf-8"); //$NON-NLS-1$
				memento.save(writer);
				writer.close();
			} else {
				Properties properties = new Properties();
				saveInProperties(properties);
				properties.store(fos, ""); //$NON-NLS-1$
			}
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
	
	public void saveInXmlFile() {
		File folder = new File(getStoreFolderPath().toOSString());
		if (!folder.exists()) {
			folder.mkdirs();
		}
		saveInFile(getStoreFilePath(), true);
	}
	
	public void saveInFile() {
		File folder = new File(getStoreFolderPath().toOSString());
		if (!folder.exists()) {
			folder.mkdirs();
		}
		saveInFile(getStoreFilePath(), false);
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

	public void loadFromFile(IPath path, boolean format) {
		fileLoadSuccessfull = false;
		FileInputStream fis = null;
		try {
			File file = new File(path.toOSString());
			if (file.exists()) {
				fis = new FileInputStream(file);
				if (format) {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(fis, "utf-8")); //$NON-NLS-1$
					try {
						IMemento memento = XMLMemento.createReadRoot(reader);
						loadState(memento);
						fileLoadSuccessfull = true;
					} catch (WorkbenchException e) {
						HibernateConsolePlugin.getDefault().logErrorMessage("Can't load layout of mapping.", e); //$NON-NLS-1$
					}
				} else {
					Properties properties = new Properties();
					properties.load(fis);
					loadFromProperties(properties);
					fileLoadSuccessfull = true;
				}
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

	public void loadFromXmlFile() {
		loadFromFile(getStoreFilePath(), true);
	}

	public void loadFromFile() {
		loadFromFile(getStoreFilePath(), false);
	}
	
	public boolean isFileLoadSuccessfull() {
		return fileLoadSuccessfull;
	}

	public String getConsoleConfigName() {
		return consoleConfigName;
	}

	protected Configuration getConfig() {
		final ConsoleConfiguration consoleConfig = getConsoleConfig();
		if (consoleConfig != null) {
			Configuration config = consoleConfig.getConfiguration();
			if (config == null) {
				consoleConfig.build();
				consoleConfig.execute(new ExecutionContext.Command() {
					public Object execute() {
						if (consoleConfig.hasConfiguration()) {
							consoleConfig.getConfiguration().buildMappings();
						}
						return consoleConfig;
					}
				} );
				config = consoleConfig.getConfiguration();
			}
			return config;
		}
		return null;
	}

	public ConsoleConfiguration getConsoleConfig() {
		final KnownConfigurations knownConfigurations = KnownConfigurations.getInstance();
		ConsoleConfiguration consoleConfig = knownConfigurations.find(consoleConfigName);
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

	public boolean equals(Object obj) {
		boolean res = false;
		if (!(obj instanceof OrmDiagram)) {
			return res;
		}
		final OrmDiagram od = (OrmDiagram)obj;
		if (consoleConfigName == null) {
			if (od.getConsoleConfigName() != null) {
				return res;
			}
		} else if (!consoleConfigName.equals(od.getConsoleConfigName())) {
			return res;
		}
		final ArrayList<RootClass> rootsOd = od.roots;
		if (roots.size() != rootsOd.size()) {
			return res;
		}
		res = true;
		for (int i = 0; i < roots.size(); i++) {
			RootClass rc = roots.get(i);
			if (rc == null) {
				if (rc != rootsOd.get(i)) {
					res = false;
					break;
				}
			} else if (!rc.equals(rootsOd.get(i))) {
				res = false;
				break;
			}
		}
		return res;
	}

	public int hashCode() {
		if (consoleConfigName == null) {
			return roots.hashCode();
		}
		return roots.hashCode() + consoleConfigName.hashCode();
	}
	
	public void setMemento(IMemento memento) {
		this.memento = memento;
	}
	
	public HashMap<String, Point> getElementsLocations() {
		HashMap<String, Point> elLocations = new HashMap<String, Point>();
		Iterator< Entry<String, OrmShape> > it = elements.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, OrmShape> entry = it.next();
			elLocations.put(entry.getKey(), entry.getValue().getLocation());
		}
		return elLocations;
	}
	
	public void setElementsLocations(HashMap<String, Point> elLocations) {
		Iterator< Entry<String, Point> > it = elLocations.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Point> entry = it.next();
			elements.get(entry.getKey()).setLocation(entry.getValue());
		}
	}
	
	public HashMap<String, Boolean> getElementsExpState() {
		HashMap<String, Boolean> elExpState = new HashMap<String, Boolean>();
		Iterator< Entry<String, OrmShape> > it = elements.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, OrmShape> entry = it.next();
			elExpState.put(entry.getKey(), entry.getValue().isExpanded());
		}
		return elExpState;
	}
	
	public void setElementsExpState(HashMap<String, Boolean> elExpState) {
		Iterator< Entry<String, Boolean> > it = elExpState.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Boolean> entry = it.next();
			elements.get(entry.getKey()).setExpanded(entry.getValue());
		}
	}
}
