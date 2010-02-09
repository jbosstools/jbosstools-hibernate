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
package org.hibernate.eclipse.console.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.VisitorSupport;
import org.dom4j.io.SAXReader;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.PackageFragmentRoot;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.hibernate.console.ConsoleConfiguration;
import org.hibernate.console.execution.ExecutionContext;
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.ToOne;
import org.hibernate.mapping.Value;
import org.hibernate.tool.hbm2x.Cfg2HbmTool;
import org.hibernate.util.StringHelper;
import org.hibernate.util.XMLHelper;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * Utility class for useful open mapping file action functions. 
 * 
 * @author Dmitry Geraskov
 * @author Vitali Yemialyanchyk
 */
@SuppressWarnings("restriction")
public class OpenMappingUtils {
	
	public static final String HIBERNATE_TAG_CLASS = "class";                       //$NON-NLS-1$
	public static final String HIBERNATE_TAG_TABLE = "table";                       //$NON-NLS-1$
	public static final String HIBERNATE_TAG_SUBCLASS = "subclass";                 //$NON-NLS-1$
	public static final String HIBERNATE_TAG_JOINED_SUBCLASS = "joined-subclass";   //$NON-NLS-1$
	public static final String HIBERNATE_TAG_UNION_SUBCLASS = "union-subclass";     //$NON-NLS-1$
	public static final String HIBERNATE_TAG_NAME = "name";                         //$NON-NLS-1$
	public static final String HIBERNATE_TAG_ENTITY_NAME = "entity-name";           //$NON-NLS-1$
	public static final String HIBERNATE_TAG_SESSION_FACTORY = "session-factory";   //$NON-NLS-1$
	public static final String HIBERNATE_TAG_MAPPING = "mapping";                   //$NON-NLS-1$
	public static final String HIBERNATE_TAG_RESOURCE = "resource";                 //$NON-NLS-1$
	public static final String HIBERNATE_TAG_CATALOG = "catalog";                   //$NON-NLS-1$
	public static final String HIBERNATE_TAG_SCHEMA = "schema";                     //$NON-NLS-1$
	public static final String HIBERNATE_TAG_KEY = "key";                           //$NON-NLS-1$
	public static final String HIBERNATE_TAG_MANY2ONE = "many-to-one";              //$NON-NLS-1$
	public static final String HIBERNATE_TAG_PROPERTY = "property";                 //$NON-NLS-1$
	public static final String EJB_TAG_ENTITY = "entity";                           //$NON-NLS-1$
	public static final String EJB_TAG_CLASS = "class";                             //$NON-NLS-1$
	public static final String EJB_TAG_MAPPED_SUPERCLASS = "mapped-superclass";     //$NON-NLS-1$
	public static final String EJB_TAG_COLUMN = "column";                           //$NON-NLS-1$
	public static final String EJB_TAG_ID = "id";                                   //$NON-NLS-1$
	public static final String EJB_TAG_BASIC = "basic";                             //$NON-NLS-1$

	//prohibit constructor call
	private OpenMappingUtils() {}

	/**
	 * Get name of a persistent class.
	 * @param rootClass
	 * @return
	 */
	public static String getPersistentClassName(PersistentClass rootClass) {
		if (rootClass == null) {
			return ""; //$NON-NLS-1$
		}
		return rootClass.getEntityName() != null ? rootClass.getEntityName() : rootClass.getClassName();
	}

	/**
	 * Formulate a full table name.
	 * @param catalog
	 * @param schema
	 * @param name
	 * @return
	 */
	public static String getTableName(String catalog, String schema, String name) {
		return (catalog != null ? catalog + '.' : "") + (schema != null ? schema + '.' : "") + name; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Get a full table name.
	 * @param table
	 * @return
	 */
	public static String getTableName(Table table) {
		return getTableName(table.getCatalog(), table.getSchema(), table.getName());
	}

	/**
	 * Check has consoleConfig config.xml file a mapping class for provided rootClass.
	 * @param consoleConfig
	 * @param rootClass
	 * @return
	 */
	public static boolean hasConfigXMLMappingClassAnnotation(ConsoleConfiguration consoleConfig, PersistentClass rootClass) {
		java.io.File configXMLFile = consoleConfig.getPreferences().getConfigXMLFile();
		if (configXMLFile == null) {
			return true;
		}
		EntityResolver entityResolver = consoleConfig.getConfiguration().getEntityResolver(); 
		Document doc = getDocument(configXMLFile, entityResolver);
		return getElements(doc, HIBERNATE_TAG_MAPPING, HIBERNATE_TAG_CLASS, getPersistentClassName(rootClass)).hasNext();
	}

	/**
	 * Check has this particular element correspondence in the file.
	 * @param consoleConfig
	 * @param file
	 * @param element
	 * @return
	 */
	public static boolean elementInFile(ConsoleConfiguration consoleConfig, IFile file, Object element) {
		boolean res = false;
		if (element instanceof RootClass) {
			res = rootClassInFile(consoleConfig, file, (RootClass)element);
		} else if (element instanceof Subclass) {
			res = subclassInFile(consoleConfig, file, (Subclass)element);
		} else if (element instanceof Table) {
			res = tableInFile(consoleConfig, file, (Table)element);
		}
		return res;
	}
	
	private static String[][] classPairs = {
		{ HIBERNATE_TAG_CLASS, HIBERNATE_TAG_NAME, },
		{ HIBERNATE_TAG_CLASS, HIBERNATE_TAG_ENTITY_NAME, },
		{ EJB_TAG_ENTITY, HIBERNATE_TAG_CLASS, },
		{ EJB_TAG_ENTITY, HIBERNATE_TAG_NAME, },
	};

	/**
	 * Check has this particular rootClass correspondence in the file.
	 * @param consoleConfig
	 * @param file
	 * @param rootClass
	 * @return
	 */
	public static boolean rootClassInFile(ConsoleConfiguration consoleConfig, IFile file, RootClass rootClass) {
		EntityResolver entityResolver = consoleConfig.getConfiguration().getEntityResolver(); 
		Document doc = getDocument(file.getLocation().toFile(), entityResolver);
		final String clName = getPersistentClassName(rootClass);
		final String clNameUnq = StringHelper.unqualify(clName);
		boolean res = false;
		// TODO: getElements - this is *extremely* inefficient - no need to scan the whole tree again and again.
		for (int i = 0; i < classPairs.length; i++) {
			res = getElements(doc, classPairs[i][0], classPairs[i][1], clNameUnq).hasNext();
			if (res) break;
			res = getElements(doc, classPairs[i][0], classPairs[i][1], clName).hasNext();
			if (res) break;
		}
		return res;
	}
	
	private static String[][] subClassPairs = {
		{ HIBERNATE_TAG_SUBCLASS, HIBERNATE_TAG_NAME, },
		{ HIBERNATE_TAG_SUBCLASS, HIBERNATE_TAG_ENTITY_NAME, },
		{ HIBERNATE_TAG_JOINED_SUBCLASS, HIBERNATE_TAG_NAME, },
		{ HIBERNATE_TAG_JOINED_SUBCLASS, HIBERNATE_TAG_ENTITY_NAME, },
		{ HIBERNATE_TAG_UNION_SUBCLASS, HIBERNATE_TAG_NAME, },
		{ HIBERNATE_TAG_UNION_SUBCLASS, HIBERNATE_TAG_ENTITY_NAME, },
		{ EJB_TAG_ENTITY, HIBERNATE_TAG_CLASS, },
		{ EJB_TAG_ENTITY, HIBERNATE_TAG_NAME, },
	};

	/**
	 * Check has this particular subclass correspondence in the file.
	 * @param consoleConfig
	 * @param file
	 * @param subclass
	 * @return
	 */
	public static boolean subclassInFile(ConsoleConfiguration consoleConfig, IFile file, Subclass subclass) {
		EntityResolver entityResolver = consoleConfig.getConfiguration().getEntityResolver(); 
		Document doc = getDocument(file.getLocation().toFile(), entityResolver);
		final String clName = getPersistentClassName(subclass);
		final String clNameUnq = StringHelper.unqualify(clName);
		boolean res = false;
		// TODO: getElements - this is *extremely* inefficient - no need to scan the whole tree again and again.
		for (int i = 0; i < subClassPairs.length; i++) {
			res = getElements(doc, subClassPairs[i][0], subClassPairs[i][1], clNameUnq).hasNext();
			if (res) break;
			res = getElements(doc, subClassPairs[i][0], subClassPairs[i][1], clName).hasNext();
			if (res) break;
		}
		return res;
	}

	/**
	 * Check has this particular table correspondence in the file.
	 * @param consoleConfig
	 * @param file
	 * @param table
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean tableInFile(ConsoleConfiguration consoleConfig, IFile file, Table table) {
		EntityResolver entityResolver = consoleConfig.getConfiguration().getEntityResolver(); 
		Document doc = getDocument(file.getLocation().toFile(), entityResolver);
		Iterator<Element> classes = getElements(doc, HIBERNATE_TAG_CLASS);
		boolean res = false;
		while (classes.hasNext()) {
			Element element = classes.next();
			Attribute tableAttr = element.attribute(HIBERNATE_TAG_TABLE);
			if (tableAttr != null) {
				Attribute catalogAttr = element.attribute(HIBERNATE_TAG_CATALOG);
				if (catalogAttr == null) {
					catalogAttr = doc.getRootElement().attribute(HIBERNATE_TAG_CATALOG);
				}
				Attribute schemaAttr = element.attribute(HIBERNATE_TAG_SCHEMA);
				if (schemaAttr == null) {
					schemaAttr = doc.getRootElement().attribute(HIBERNATE_TAG_SCHEMA);
				}
				String catalog = catalogAttr != null ? catalogAttr.getValue() : null;
				String schema = schemaAttr != null ? schemaAttr.getValue() : null;
				String name = tableAttr.getValue();
				if (getTableName(catalog, schema, name).equals(getTableName(table))) {
					res = true;
					break;
				}
			}
			Attribute classNameAttr = element.attribute(HIBERNATE_TAG_NAME);
			if (classNameAttr == null) {
				classNameAttr = element.attribute(HIBERNATE_TAG_ENTITY_NAME);
			}
			if (classNameAttr != null) {
				String physicalTableName = consoleConfig.getConfiguration().getNamingStrategy().classToTableName(classNameAttr.getValue());
				if (table.getName().equals(physicalTableName)) {
					res = true;
					break;
				}
			}
		}
		if (!res && getElements(doc, HIBERNATE_TAG_TABLE, table.getName()).hasNext()) {
			res = true;
		}
		if (!res) {
			classes = getElements(doc, EJB_TAG_ENTITY);
			while (classes.hasNext() && !res) {
				Element element = classes.next();
				Iterator<Element> itTables = element.elements(HIBERNATE_TAG_TABLE).iterator();
				while (itTables.hasNext()) {
					element = itTables.next();
					Attribute tableAttr = element.attribute(HIBERNATE_TAG_NAME);
					if (tableAttr != null) {
						Attribute catalogAttr = element.attribute(HIBERNATE_TAG_CATALOG);
						if (catalogAttr == null) {
							catalogAttr = doc.getRootElement().attribute(HIBERNATE_TAG_CATALOG);
						}
						Attribute schemaAttr = element.attribute(HIBERNATE_TAG_SCHEMA);
						if (schemaAttr == null) {
							schemaAttr = doc.getRootElement().attribute(HIBERNATE_TAG_SCHEMA);
						}
						String catalog = catalogAttr != null ? catalogAttr.getValue() : null;
						String schema = schemaAttr != null ? schemaAttr.getValue() : null;
						String name = tableAttr.getValue();
						if (getTableName(catalog, schema, name).equals(getTableName(table))) {
							res = true;
							break;
						}
					}
				}
			}
		}
		return res;
	}

	private static Iterator<Element> getElements(Document doc, String elementName) {
		return getElements(doc, elementName, null, null);
	}

	private static Iterator<Element> getElements(Document doc, String attrName, String attrValue) {
		return getElements(doc, null, attrName, attrValue);
	}

	private static Iterator<Element> getElements(Document doc, String elementName, String attrName, String attrValue) {
		LVS visitor = new LVS(elementName, attrName, attrValue);
		doc.accept(visitor);
		return visitor.iterator();
	}

	private static class LVS extends VisitorSupport {
		private String nodeName;
		private String attrName;
		private String attrValue;
		private List<Element> ret = new ArrayList<Element>();

		public LVS(String nodeName, String attrName, String attrValue) {
			super();
			this.nodeName = nodeName;
			this.attrName = attrName;
			this.attrValue = attrValue;
		}

		public void visit(Element element) {
			if (nodeName == null) {
				if (attrName != null && attrValue != null) {
					if (inspectAttributeForValue(element, attrName, attrValue)) {
			        	ret.add(element);
					}
				}
			} else {
				if (nodeName.equals(element.getName())) {
					if (attrName != null) {
						if (inspectAttributeForValue(element, attrName, attrValue)) {
				        	ret.add(element);
						}
					} else {
			        	ret.add(element);
					}
				}
			}
		}

		public Iterator<Element> iterator() {
			return ret.iterator();
		}

		protected boolean inspectAttributeForValue(Element element, String attrName, String checkValue) {
			Attribute attr = element.attribute(attrName);
			if (attr != null && checkValue.equals(attr.getValue())) {
				return checkValue.equals(attr.getValue());
			}
			return false;
		}
	}

	/**
	 * Trying to find hibernate console config mapping file,
	 * which is corresponding to provided element.
	 *   
	 * @param configXMLFile
	 * @param entityResolver
	 * @return
	 */
	public static Document getDocument(java.io.File configXMLFile, EntityResolver entityResolver) {
		Document doc = null;
		if (configXMLFile == null) {
			return doc;
		}
		InputStream stream = null;
		try {
			stream = new FileInputStream(configXMLFile);
		} catch (FileNotFoundException e) {
			HibernateConsolePlugin.getDefault().logErrorMessage("Configuration file not found", e); //$NON-NLS-1$
		}
		try {
			List<Throwable> errors = new ArrayList<Throwable>();
			XMLHelper helper = new XMLHelper();
			SAXReader saxReader = helper.createSAXReader(configXMLFile.getPath(), errors, entityResolver);
			doc = saxReader.read(new InputSource( stream));
			if (errors.size() != 0) {
    			HibernateConsolePlugin.getDefault().logErrorMessage("invalid configuration", (Throwable)null);	//$NON-NLS-1$
			}
		}
		catch (DocumentException e) {
			HibernateConsolePlugin.getDefault().logErrorMessage("Could not parse configuration", e);			//$NON-NLS-1$
		}
		finally {
			try {
				if (stream != null) stream.close();
			}
			catch (IOException ioe) {
    			HibernateConsolePlugin.getDefault().logErrorMessage("could not close input stream for", ioe);	//$NON-NLS-1$
			}
		}
		return doc;
	}
	
	public static PackageFragmentRoot[] getCCPackageFragmentRoots(ConsoleConfiguration consoleConfiguration) {
		IJavaProject[] projs = ProjectUtils.findJavaProjects(consoleConfiguration);
		ArrayList<PackageFragmentRoot> res = new ArrayList<PackageFragmentRoot>(); 
		try {
			for (int i = 0; i < projs.length; i++) {
				IPackageFragmentRoot[] pfrs = projs[i].getAllPackageFragmentRoots();
				for (int j = 0; j < pfrs.length; j++) {
					if (pfrs[j].getClass() != PackageFragmentRoot.class) {
						continue;
					}
					res.add((PackageFragmentRoot)pfrs[j]);
				}
			}
		} catch (JavaModelException e) {
			HibernateConsolePlugin.getDefault().logErrorMessage(HibernateConsoleMessages.OpenFileActionUtils_problems_while_get_project_package_fragment_roots, e);
		}
		return res.toArray(new PackageFragmentRoot[0]);
	}

	/**
	 * Trying to find hibernate console config mapping file,
	 * which is corresponding to provided element.
	 *   
	 * @param consoleConfig
	 * @param element
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static IFile searchInMappingFiles(ConsoleConfiguration consoleConfig, Object element) {
		IFile file = null;
    	if (consoleConfig == null) {
        	return file;
    	}
		java.io.File configXMLFile = consoleConfig.getConfigXMLFile();
		EntityResolver entityResolver = consoleConfig.getConfiguration().getEntityResolver(); 
		Document doc = getDocument(configXMLFile, entityResolver);
		if (doc == null) {
        	return file;
		}
		//
		PackageFragmentRoot[] packageFragments = getCCPackageFragmentRoots(consoleConfig);
		//
		ArrayList<IPath> paths = new ArrayList<IPath>(); 
		for (int i = 0; i < packageFragments.length; i++) {
			paths.add(packageFragments[i].getPath());
		}
		// last chance to find file is the same place as configXMLFile
		paths.add(Path.fromOSString(configXMLFile.getParent()));
		//
		for (int i = 0; i < paths.size() && file == null; i++) {
	    	Element sfNode = doc.getRootElement().element(HIBERNATE_TAG_SESSION_FACTORY);
			Iterator<Element> elements = sfNode.elements(HIBERNATE_TAG_MAPPING).iterator();
			while (elements.hasNext() && file == null) {
				Element subelement = elements.next();
				Attribute resourceAttr = subelement.attribute(HIBERNATE_TAG_RESOURCE);
				if (resourceAttr == null) {
					continue;
				}
				IPath path = paths.get(i).append(resourceAttr.getValue());
				file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
				if (file == null || !file.exists()) {
					file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
				}
				if (file != null && file.exists()) {
					if (elementInFile(consoleConfig, file, element)) {
						break;
					}
				}
				file = null;
			}
		}
    	return file;
	}
	
	/**
	 * Trying to find console configuration additional mapping file,
	 * which is corresponding to provided element.
	 *   
	 * @param consoleConfig
	 * @param element
	 * @return
	 */
	public static IFile searchInAdditionalMappingFiles(ConsoleConfiguration consoleConfig, Object element) {
		IFile file = null;
    	if (consoleConfig == null) {
        	return file;
    	}
    	java.io.File[] files = consoleConfig.getPreferences().getMappingFiles();
		for (int i = 0; i < files.length; i++) {
			java.io.File fileTmp = files[i];
			if (fileTmp == null) {
				continue;
			}
			file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(fileTmp.getPath()));
			if (file == null) {
				continue;
			}
			if (file.exists() && elementInFile(consoleConfig, file, element)) {
				break;
			}
			file = null;
		}
		return file;
	}

	/**
	 * Trying to find hibernate console config ejb3 mapping file,
	 * which is corresponding to provided element.
	 *   
	 * @param consoleConfig
	 * @param element
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static IFile searchInEjb3MappingFiles(ConsoleConfiguration consoleConfig, Object element) {
		IFile file = null;
    	if (consoleConfig == null) {
        	return file;
    	}
		final ConsoleConfiguration cc2 = consoleConfig;
		List<String> documentPaths = (List<String>)consoleConfig.getExecutionContext().execute(new ExecutionContext.Command() {
			public Object execute() {
				return OpenMappingUtilsEjb3.enumDocuments(cc2);
			}
		});
    	if (documentPaths == null) {
        	return file;
    	}
		IJavaProject[] projs = ProjectUtils.findJavaProjects(consoleConfig);
		ArrayList<IPath> pathsSrc = new ArrayList<IPath>(); 
		ArrayList<IPath> pathsOut = new ArrayList<IPath>(); 
		ArrayList<IPath> pathsFull = new ArrayList<IPath>(); 
		for (int i = 0; i < projs.length; i++) {
			IJavaProject proj = projs[i];
			IPath projPathFull = proj.getResource().getLocation();
			IPath projPath = proj.getPath();
			IPath projPathOut = null;
			try {
				projPathOut = proj.getOutputLocation();
				projPathOut = projPathOut.makeRelativeTo(projPath);
			} catch (JavaModelException e) {
				// just ignore
			}
			IPackageFragmentRoot[] pfrs = new IPackageFragmentRoot[0];
			try {
				pfrs = proj.getAllPackageFragmentRoots();
			} catch (JavaModelException e) {
				// just ignore
			}
			for (int j = 0; j < pfrs.length; j++) {
				// TODO: think about possibility to open resources from jar files
				if (pfrs[j].getClass() != PackageFragmentRoot.class) {
					continue;
				}
				final IPath pathSrc = ((PackageFragmentRoot)pfrs[j]).getPath();
				final IPath pathOut = projPathOut;
				final IPath pathFull = projPathFull;
				pathsSrc.add(pathSrc);
				pathsOut.add(pathOut);
				pathsFull.add(pathFull);
			}
		}
		int scanSize = Math.min(pathsSrc.size(), pathsOut.size());
		scanSize = Math.min(pathsFull.size(), scanSize);
		for (int i = 0; i < scanSize && file == null; i++) {
			final IPath pathSrc = pathsSrc.get(i);
			final IPath pathOut = pathsOut.get(i);
			final IPath pathFull = pathsFull.get(i);
			Iterator<String> it = documentPaths.iterator();
			while (it.hasNext() && file == null) {
				String docPath = it.next();
				IPath path2DocFull = Path.fromOSString(docPath);
				IPath resPath = path2DocFull.makeRelativeTo(pathFull);
				if (pathOut != null) {
					resPath = resPath.makeRelativeTo(pathOut);
				}
				resPath = pathSrc.append(resPath);
				file = ResourcesPlugin.getWorkspace().getRoot().getFile(resPath);
				if (file == null || !file.exists()) {
					file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(resPath);
				}
				if (file != null && file.exists()) {
					if (elementInFile(consoleConfig, file, element)) {
						break;
					}
				}
				file = null;
			}
		}
    	return file;
	}

	/**
	 * This function is trying to find hibernate console config file,
	 * which is corresponding to provided element.
	 *   
	 * @param consoleConfig
	 * @param element
	 * @return
	 */
	public static IFile searchFileToOpen(ConsoleConfiguration consoleConfig, Object element) {
		IFile file = searchInMappingFiles(consoleConfig, element);
		if (file == null) {
			file = searchInAdditionalMappingFiles(consoleConfig, element);
		}
		if (file == null) {
			file = searchInEjb3MappingFiles(consoleConfig, element);
		}
    	return file;
	}

	/**
	 * Creates FindReplaceDocumentAdapter for provided text editor.
	 * 
	 * @param textEditor
	 * @return
	 */
	public static FindReplaceDocumentAdapter createFindDocAdapter(ITextEditor textEditor) {
		IDocument document = null;
		if (textEditor.getDocumentProvider() != null){
			document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
		}
		if (document == null) {
			return null;
		}
		return new FindReplaceDocumentAdapter(document);
	}

	/**
	 * Opens an editor on the given file resource.
	 * @param file the editor input
	 * @return an open editor or <code>null</code> if an external editor was opened
	 * @exception PartInitException if the editor could not be initialized
	 */
	public static IEditorPart openFileInEditor(IFile file) throws PartInitException {
		IWorkbenchPage page =
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		return IDE.openEditor(page, file);
	}

	/**
	 * Finds a document region, which corresponds of given selection object.
	 * @param proj
	 * @param findAdapter
	 * @param selection
	 * @return a proper document region
	 */
	public static IRegion findSelectRegion(IJavaProject proj, FindReplaceDocumentAdapter findAdapter, Object selection) {
		IRegion selectRegion = null;
		if (selection instanceof RootClass || selection instanceof Subclass) {
			selectRegion = findSelectRegion(proj, findAdapter, (PersistentClass)selection);
		} else if (selection instanceof Property){
			selectRegion = findSelectRegion(proj, findAdapter, (Property)selection);
		} else if (selection instanceof Table) {
			selectRegion = findSelectRegion(proj, findAdapter, (Table)selection);
		} else if (selection instanceof Column) {
			selectRegion = findSelectRegion(proj, findAdapter, (Column)selection);
		}
		return selectRegion;
	}
	
	/**
	 * Finds a document region, which corresponds of given property.
	 * @param proj
	 * @param findAdapter
	 * @param property
	 * @return a proper document region
	 */
	public static IRegion findSelectRegion(IJavaProject proj, FindReplaceDocumentAdapter findAdapter, Property property) {
		Assert.isNotNull(property.getPersistentClass());
		IRegion classRegion = findSelectRegion(proj, findAdapter, property.getPersistentClass());
		IRegion res = null;
		if (classRegion == null) {
			return res;
		}
		// in case if we could not find property - we select class
		res = classRegion;
		final Cfg2HbmTool tool = new Cfg2HbmTool();
		final PersistentClass persistentClass = property.getPersistentClass();
		final String tagName = tool.getTag(persistentClass);
		IRegion finalRegion = null;
		IRegion propRegion = null;
		int startOffset = classRegion.getOffset() + classRegion.getLength();
		try {
			String tagClose = "</" + tagName; //$NON-NLS-1$
			finalRegion = findAdapter.find(startOffset, tagClose, true, true, false, false);
			if (finalRegion == null) {
				tagClose = "</" + EJB_TAG_ENTITY; //$NON-NLS-1$
				finalRegion = findAdapter.find(startOffset, tagClose, true, true, false, false);
			}
			propRegion = findAdapter.find(startOffset, generateHbmPropertyPattern(property), true, true, false, true);
			if (propRegion == null) {
				propRegion = findAdapter.find(startOffset, generateEjbPropertyPattern(property), true, true, false, true);
			}
		} catch (BadLocationException e) {
			//ignore
		}
		String className = persistentClass.getClassName();
		while (propRegion == null) {
			className = ProjectUtils.getParentTypename(proj, className);
			if (className == null) {
				break;
			}
			classRegion = findSelectRegion(proj, findAdapter, className);
			if (classRegion == null) {
				break;
			}
			startOffset = classRegion.getOffset() + classRegion.getLength();
			try {
				String tagClose = "</" + EJB_TAG_MAPPED_SUPERCLASS; //$NON-NLS-1$
				finalRegion = findAdapter.find(startOffset, tagClose, true, true, false, false);
				propRegion = findAdapter.find(startOffset, generateEjbPropertyPattern(property), true, true, false, true);
			} catch (BadLocationException e) {
				//ignore
			}
		}
		if (propRegion != null) {
			int length = property.getName().length();
			int offset = propRegion.getOffset() + propRegion.getLength() - length - 1;
			res = new Region(offset, length);
			if (finalRegion != null && propRegion.getOffset() > finalRegion.getOffset()) {
				res = null;
			}
		}
		return res;
	}
	
	/**
	 * Finds a document region, which corresponds of given persistent class.
	 * @param proj
	 * @param findAdapter
	 * @param persistentClass
	 * @return a proper document region
	 */
	public static IRegion findSelectRegion(IJavaProject proj, FindReplaceDocumentAdapter findAdapter, PersistentClass persistentClass) {
		IRegion res = null;
		String[] classPatterns = generatePersistentClassPatterns(persistentClass);
		IRegion classRegion = null;
		try {
			for (int i = 0; (classRegion == null) && (i < classPatterns.length); i++){
				classRegion = findAdapter.find(0, classPatterns[i], true, true, false, true);
			}
		} catch (BadLocationException e) {
			//ignore
		}
		if (classRegion != null) {
			int length = persistentClass.getNodeName().length();
			int offset = classRegion.getOffset() + classRegion.getLength() - length - 1;
			res = new Region(offset, length);
		}
		return res;
	}
	
	/**
	 * Finds a document region, which corresponds of given persistent class.
	 * @param proj
	 * @param findAdapter
	 * @param className
	 * @return a proper document region
	 */
	public static IRegion findSelectRegion(IJavaProject proj, FindReplaceDocumentAdapter findAdapter, String className) {
		IRegion res = null;
		String[] classPatterns = generatePersistentClassPatterns(className);
		IRegion classRegion = null;
		try {
			for (int i = 0; (classRegion == null) && (i < classPatterns.length); i++){
				classRegion = findAdapter.find(0, classPatterns[i], true, true, false, true);
			}
		} catch (BadLocationException e) {
			//ignore
		}
		if (classRegion != null) {
			int length = getShortClassName(className).length();
			int offset = classRegion.getOffset() + classRegion.getLength() - length - 1;
			res = new Region(offset, length);
		}
		return res;
	}
	
	/**
	 * Finds a document region, which corresponds of given persistent class.
	 * @param proj
	 * @param findAdapter
	 * @param table
	 * @return a proper document region
	 */
	public static IRegion findSelectRegion(IJavaProject proj, FindReplaceDocumentAdapter findAdapter, Table table) {
		IRegion res = null;
		String[] tablePatterns = generateTablePatterns(table.getName());
		IRegion tableRegion = null;
		try {
			for (int i = 0; (tableRegion == null) && (i < tablePatterns.length); i++){
				tableRegion = findAdapter.find(0, tablePatterns[i], true, true, false, true);
			}
		} catch (BadLocationException e) {
			//ignore
		}
		if (tableRegion != null) {
			int length = table.getName().length();
			int offset = tableRegion.getOffset() + tableRegion.getLength() - length - 1;
			res = new Region(offset, length);
		}
		return res;
	}
	
	/**
	 * Finds a document region, which corresponds of given persistent class.
	 * @param proj
	 * @param findAdapter
	 * @param table
	 * @return a proper document region
	 */
	public static IRegion findSelectRegion(IJavaProject proj, FindReplaceDocumentAdapter findAdapter, Column column) {
		IRegion res = null;
		String[] columnPatterns = generateColumnPatterns(column.getName());
		IRegion columnRegion = null;
		try {
			for (int i = 0; (columnRegion == null) && (i < columnPatterns.length); i++){
				columnRegion = findAdapter.find(0, columnPatterns[i], true, true, false, true);
			}
		} catch (BadLocationException e) {
			//ignore
		}
		if (columnRegion != null) {
			int length = column.getName().length();
			int offset = columnRegion.getOffset() + columnRegion.getLength() - length - 1;
			res = new Region(offset, length);
		}
		return res;
	}

	/**
	 * Creates a xml tag search pattern with given tag name which should contains
	 * proper name-value pair.
	 * 
	 * @param tagName
	 * @param name
	 * @param value
	 * @return a result search pattern
	 */
	public static String createPattern(String tagName, String name, String value) {
		StringBuffer pattern = new StringBuffer("<"); //$NON-NLS-1$
		pattern.append(tagName);
		pattern.append("[\\s]+[.[^>]]*"); //$NON-NLS-1$
		pattern.append(name);
		pattern.append("[\\s]*=[\\s]*\""); //$NON-NLS-1$
		pattern.append(value);
		pattern.append('\"');
		return pattern.toString();
	}
	
	private static String[][] persistentClassPairs = {
		{ HIBERNATE_TAG_CLASS, HIBERNATE_TAG_NAME, },
		{ HIBERNATE_TAG_CLASS, HIBERNATE_TAG_ENTITY_NAME, },
		{ EJB_TAG_ENTITY, HIBERNATE_TAG_NAME, },
		{ EJB_TAG_ENTITY, EJB_TAG_CLASS, },
		{ EJB_TAG_MAPPED_SUPERCLASS, HIBERNATE_TAG_NAME, },
		{ EJB_TAG_MAPPED_SUPERCLASS, EJB_TAG_CLASS, },
	};
	
	private static String[][] tablePairs = {
		{ HIBERNATE_TAG_TABLE, HIBERNATE_TAG_NAME, },
		{ HIBERNATE_TAG_CLASS, HIBERNATE_TAG_NAME, },
		{ HIBERNATE_TAG_CLASS, HIBERNATE_TAG_ENTITY_NAME, },
		{ EJB_TAG_ENTITY, HIBERNATE_TAG_NAME, },
		{ EJB_TAG_ENTITY, EJB_TAG_CLASS, },
	};
	
	private static String[][] columnPairs = {
		{ EJB_TAG_COLUMN, HIBERNATE_TAG_NAME, },
		{ EJB_TAG_ID, EJB_TAG_COLUMN, },
		{ HIBERNATE_TAG_MANY2ONE, EJB_TAG_COLUMN, },
		{ HIBERNATE_TAG_KEY, EJB_TAG_COLUMN, },
		{ EJB_TAG_ID, HIBERNATE_TAG_NAME, },
		{ HIBERNATE_TAG_MANY2ONE, HIBERNATE_TAG_NAME, },
		{ EJB_TAG_BASIC, HIBERNATE_TAG_NAME, },
		{ HIBERNATE_TAG_PROPERTY, HIBERNATE_TAG_NAME, },
	};

	/**
	 * Extract short name of the class from fullClassName.
	 * 
	 * @param fullClassName
	 * @return a short class name
	 */
	public static String getShortClassName(String fullClassName) {
		return fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
	}
	
	/**
	 * Generates a persistent class xml tag search patterns.
	 * 
	 * @param persClass
	 * @return an arrays of search patterns
	 */
	public static String[] generatePersistentClassPatterns(PersistentClass persClass) {
		String fullClassName = null;
		String shortClassName = null;
		if (persClass.getEntityName() != null){
			fullClassName = persClass.getEntityName();
		} else {
			fullClassName = persClass.getClassName();
		}
		shortClassName = getShortClassName(fullClassName);
		final Cfg2HbmTool tool = new Cfg2HbmTool();
		final String tagName = tool.getTag(persClass);
		persistentClassPairs[0][0] = tagName;
		persistentClassPairs[1][0] = tagName;
		List<String> patterns = new ArrayList<String>();
		for (int i = 0; i < persistentClassPairs.length; i++) {
			patterns.add(createPattern(persistentClassPairs[i][0], persistentClassPairs[i][1], shortClassName));
			patterns.add(createPattern(persistentClassPairs[i][0], persistentClassPairs[i][1], fullClassName));
		}
		return patterns.toArray(new String[0]);
	}
	
	/**
	 * Generates a persistent class xml tag search patterns.
	 * 
	 * @param fullClassName
	 * @return an arrays of search patterns
	 */
	public static String[] generatePersistentClassPatterns(String fullClassName) {
		String shortClassName = getShortClassName(fullClassName);
		List<String> patterns = new ArrayList<String>();
		for (int i = 0; i < persistentClassPairs.length; i++) {
			patterns.add(createPattern(persistentClassPairs[i][0], persistentClassPairs[i][1], shortClassName));
			patterns.add(createPattern(persistentClassPairs[i][0], persistentClassPairs[i][1], fullClassName));
		}
		return patterns.toArray(new String[0]);
	}

	/**
	 * Generates a table xml tag search patterns.
	 * 
	 * @param tableName
	 * @return an arrays of search patterns
	 */
	public static String[] generateTablePatterns(String tableName) {
		List<String> patterns = new ArrayList<String>();
		for (int i = 0; i < tablePairs.length; i++) {
			patterns.add(createPattern(tablePairs[i][0], tablePairs[i][1], tableName));
		}
		return patterns.toArray(new String[0]);
	}

	/**
	 * Generates a column xml tag search patterns.
	 * 
	 * @param columnName
	 * @return an arrays of search patterns
	 */
	public static String[] generateColumnPatterns(String columnName) {
		List<String> patterns = new ArrayList<String>();
		for (int i = 0; i < columnPairs.length; i++) {
			patterns.add(createPattern(columnPairs[i][0], columnPairs[i][1], columnName));
		}
		return patterns.toArray(new String[0]);
	}

	/**
	 * Generates a property xml tag search pattern, which corresponds hibernate hbm syntax.
	 * 
	 * @param property
	 * @return a search patterns
	 */
	public static String generateHbmPropertyPattern(Property property) {
		final Cfg2HbmTool tool = new Cfg2HbmTool();
		String toolTag = ""; //$NON-NLS-1$
		PersistentClass pc = property.getPersistentClass();
		if (pc != null && pc.getIdentifierProperty() == property) {
			if (property.isComposite()) {
				toolTag = "composite-id"; //$NON-NLS-1$
			} else {
				toolTag = "id"; //$NON-NLS-1$
			}
		} else {
			toolTag = tool.getTag(property);
			if ("component".equals(toolTag) && "embedded".equals(property.getPropertyAccessorName())) {  //$NON-NLS-1$//$NON-NLS-2$
				toolTag = "properties"; //$NON-NLS-1$
			}
		}
		return createPattern(toolTag, HIBERNATE_TAG_NAME, property.getName());
	}

	/**
	 * Generates a property xml tag search pattern, which corresponds ejb3 syntax.
	 * 
	 * @param property
	 * @return a search patterns
	 */
	public static String generateEjbPropertyPattern(Property property) {
		String toolTag = ""; //$NON-NLS-1$
		PersistentClass pc = property.getPersistentClass();
		if (pc != null && pc.getIdentifierProperty() == property) {
			if (property.isComposite()) {
				toolTag = "embedded-id"; //$NON-NLS-1$
			} else {
				toolTag = "id"; //$NON-NLS-1$
			}
		} else {
			Value value = property.getValue();
			toolTag = "basic"; //$NON-NLS-1$
			if (!value.isSimpleValue()) {
				if (value instanceof Collection) {
					value = ((Collection)value).getElement();
				}
			}
			if (value instanceof OneToMany) {
				toolTag = "one-to-many"; //$NON-NLS-1$
			}
			else if (value instanceof ManyToOne) {
				// could be many-to-one | many-to-many
				toolTag = "many-to-((one)|(many))"; //$NON-NLS-1$
			}
			else if (value instanceof OneToOne) {
				toolTag = "one-to-one"; //$NON-NLS-1$
			}
			else if (value instanceof Map) {
				toolTag = "many-to-many"; //$NON-NLS-1$
			}
			else if (value instanceof Component) {
				if (((Component)value).isEmbedded()) {
					toolTag = "embedded"; //$NON-NLS-1$
				}
			}
			if (value instanceof ToOne) {
				if (((ToOne)value).isEmbedded()) {
					toolTag = "embedded"; //$NON-NLS-1$
				}
			}
		}
		return createPattern(toolTag, HIBERNATE_TAG_NAME, property.getName());
	}

	/**
	 * Method gets all ITextEditors from IEditorPart. Shouldn't returns null value.
	 * 
	 * @param editorPart
	 * @return
	 */
	public static ITextEditor[] getTextEditors(IEditorPart editorPart) {
		// if EditorPart is MultiPageEditorPart then get ITextEditor from it.
		ITextEditor[] res = new ITextEditor[0];
		if (editorPart instanceof MultiPageEditorPart) {
			List<ITextEditor> testEditors = new ArrayList<ITextEditor>();
    		IEditorPart[] editors = ((MultiPageEditorPart)editorPart).findEditors(editorPart.getEditorInput());
    		for (int i = 0; i < editors.length; i++) {
				if (editors[i] instanceof ITextEditor){
					testEditors.add((ITextEditor)editors[i]);
				}
			}
    		res = testEditors.toArray(res);
		} else if (editorPart instanceof ITextEditor){
			res = new ITextEditor[]{(ITextEditor) editorPart};
		}
		return res;
	}
}
