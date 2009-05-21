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
package org.hibernate.eclipse.console.actions;

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
import org.hibernate.eclipse.console.HibernateConsoleMessages;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Subclass;
import org.hibernate.mapping.Table;
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
public class OpenFileActionUtils {
	
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
	public static final String EJB_TAG_ENTITY = "entity";                           //$NON-NLS-1$
	public static final String EJB_TAG_CLASS = "class";                             //$NON-NLS-1$
	
	//prohibit constructor call
	private OpenFileActionUtils() {}

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
	 * Check has consoleConfiguration config.xml file a mapping class for provided rootClass.
	 * @param consoleConfiguration
	 * @param rootClass
	 * @return
	 */
	public static boolean hasConfigXMLMappingClassAnnotation(ConsoleConfiguration consoleConfiguration, PersistentClass rootClass) {
		java.io.File configXMLFile = consoleConfiguration.getPreferences().getConfigXMLFile();
		if (configXMLFile == null) {
			return true;
		}
		EntityResolver entityResolver = consoleConfiguration.getConfiguration().getEntityResolver(); 
		Document doc = getDocument(configXMLFile, entityResolver);
		return getElements(doc, HIBERNATE_TAG_MAPPING, HIBERNATE_TAG_CLASS, getPersistentClassName(rootClass)).hasNext();
	}

	/**
	 * Check has this particular element correspondence in the file.
	 * @param consoleConfiguration
	 * @param file
	 * @param element
	 * @return
	 */
	public static boolean elementInFile(ConsoleConfiguration consoleConfiguration, IFile file, Object element) {
		boolean res = false;
		if (element instanceof RootClass) {
			res = rootClassInFile(consoleConfiguration, file, (RootClass)element);
		} else if (element instanceof Subclass) {
			res = subclassInFile(consoleConfiguration, file, (Subclass)element);
		} else if (element instanceof Table) {
			res = tableInFile(consoleConfiguration, file, (Table)element);
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
	 * @param consoleConfiguration
	 * @param file
	 * @param rootClass
	 * @return
	 */
	public static boolean rootClassInFile(ConsoleConfiguration consoleConfiguration, IFile file, RootClass rootClass) {
		EntityResolver entityResolver = consoleConfiguration.getConfiguration().getEntityResolver(); 
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
	 * @param consoleConfiguration
	 * @param file
	 * @param subclass
	 * @return
	 */
	public static boolean subclassInFile(ConsoleConfiguration consoleConfiguration, IFile file, Subclass subclass) {
		EntityResolver entityResolver = consoleConfiguration.getConfiguration().getEntityResolver(); 
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
	 * @param consoleConfiguration
	 * @param file
	 * @param table
	 * @return
	 */
	public static boolean tableInFile(ConsoleConfiguration consoleConfiguration, IFile file, Table table) {
		EntityResolver entityResolver = consoleConfiguration.getConfiguration().getEntityResolver(); 
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
				String physicalTableName = consoleConfiguration.getConfiguration().getNamingStrategy().classToTableName(classNameAttr.getValue());
				if (table.getName().equals(physicalTableName)) {
					res = true;
					break;
				}
			}
		}
		if (!res && getElements(doc, HIBERNATE_TAG_TABLE, table.getName()).hasNext()) {
			res = true;
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

	/**
	 * Trying to find hibernate console config mapping file,
	 * which is corresponding to provided element.
	 *   
	 * @param consoleConfiguration
	 * @param proj
	 * @param element
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static IFile searchInMappingFiles(ConsoleConfiguration consoleConfiguration, IJavaProject proj, Object element) {
		IFile file = null;
    	if (consoleConfiguration == null || proj == null) {
        	return file;
    	}
		java.io.File configXMLFile = consoleConfiguration.getPreferences().getConfigXMLFile();
		EntityResolver entityResolver = consoleConfiguration.getConfiguration().getEntityResolver(); 
		Document doc = getDocument(configXMLFile, entityResolver);
		if (doc == null) {
        	return file;
		}
    	Element sfNode = doc.getRootElement().element(HIBERNATE_TAG_SESSION_FACTORY);
		Iterator<Element> elements = sfNode.elements(HIBERNATE_TAG_MAPPING).iterator();
		while (elements.hasNext() && file == null) {
			Element subelement = elements.next();
			Attribute resourceAttr = subelement.attribute(HIBERNATE_TAG_RESOURCE);
			if (resourceAttr == null) {
				continue;
			}
			IPackageFragmentRoot[] packageFragmentRoots = new IPackageFragmentRoot[0]; 
			try {
				packageFragmentRoots = proj.getAllPackageFragmentRoots();
			} catch (JavaModelException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage(HibernateConsoleMessages.OpenFileActionUtils_problems_while_get_project_package_fragment_roots, e);
			}
			for (int i = 0; i < packageFragmentRoots.length; i++) {
				//search in source folders.
				if (packageFragmentRoots[i].getClass() != PackageFragmentRoot.class) {
					continue;
				}
				IPackageFragmentRoot packageFragmentRoot = packageFragmentRoots[i];
				IPath path = packageFragmentRoot.getPath().append(resourceAttr.getValue());
				file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
				if (file == null) {
					continue;
				}
				if (file.exists() && elementInFile(consoleConfiguration, file, element)) {
					break;
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
	 * @param consoleConfiguration
	 * @param element
	 * @return
	 */
	public static IFile searchInAdditionalMappingFiles(ConsoleConfiguration consoleConfiguration, Object element) {
		IFile file = null;
    	if (consoleConfiguration == null) {
        	return file;
    	}
    	java.io.File[] files = consoleConfiguration.getPreferences().getMappingFiles();
		for (int i = 0; i < files.length; i++) {
			java.io.File fileTmp = files[i];
			if (fileTmp == null) {
				continue;
			}
			file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(fileTmp.getPath()));
			if (file == null) {
				continue;
			}
			if (file.exists() && elementInFile(consoleConfiguration, file, element)) {
				break;
			}
			file = null;
		}
		return file;
	}

	/**
	 * This function is trying to find hibernate console config file,
	 * which is corresponding to provided element.
	 *   
	 * @param consoleConfiguration
	 * @param proj
	 * @param element
	 * @return
	 */
	public static IFile searchFileToOpen(ConsoleConfiguration consoleConfiguration, IJavaProject proj, Object element) {
		IFile file = searchInMappingFiles(consoleConfiguration, proj, element);
		if (file == null) {
			file = searchInAdditionalMappingFiles(consoleConfiguration, element);
		}
		//if (file == null) {
		//	file = searchInEjb3MappingFiles(consoleConfiguration, proj, element);
		//}
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
	 * @param findAdapter
	 * @param selection
	 * @return a proper document region
	 */
	public static IRegion findSelectRegion(FindReplaceDocumentAdapter findAdapter, Object selection) {
		IRegion selectRegion = null;
		if (selection instanceof RootClass || selection instanceof Subclass) {
			selectRegion = findSelectRegion(findAdapter, (PersistentClass)selection);
		} else if (selection instanceof Property){
			selectRegion = findSelectRegion(findAdapter, (Property)selection);
		}
		return selectRegion;
	}

	/**
	 * Finds a document region, which corresponds of given property.
	 * @param findAdapter
	 * @param property
	 * @return a proper document region
	 */
	public static IRegion findSelectRegion(FindReplaceDocumentAdapter findAdapter, Property property) {
		Assert.isNotNull(property.getPersistentClass());
		IRegion classRegion = findSelectRegion(findAdapter, property.getPersistentClass());
		if (classRegion == null) {
			return null;
		}
		final Cfg2HbmTool tool = new Cfg2HbmTool();
		final String tagName = tool.getTag(property.getPersistentClass());
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
		IRegion res = null;
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
	 * @param findAdapter
	 * @param persistentClass
	 * @return a proper document region
	 */
	public static IRegion findSelectRegion(FindReplaceDocumentAdapter findAdapter, PersistentClass persistentClass) {
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
	};
	
	/**
	 * Generates a persistent class xml tag search patterns.
	 * 
	 * @param persClass
	 * @return an arrays of search patterns
	 */
	public static String[] generatePersistentClassPatterns(PersistentClass persClass){
		String fullClassName = null;
		String shortClassName = null;
		if (persClass.getEntityName() != null){
			fullClassName = persClass.getEntityName();
		} else {
			fullClassName = persClass.getClassName();
		}
		shortClassName = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
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
				toolTag = "composite-id"; //$NON-NLS-1$
			} else {
				toolTag = "id"; //$NON-NLS-1$
			}
		} else {
			toolTag = "basic"; //$NON-NLS-1$
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
