package org.hibernate.eclipse.mapper.extractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.CompletionProposalComparator;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.ui.contentassist.XMLRelevanceConstants;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Helper class that can extract information about a hbm.xml document based on e.g. DOM Nodes.
 * 
 * @author max
 *
 */
public class HBMInfoExtractor {

	final List hibernateTypes = new ArrayList(); // key: element>attributename, value: handler
	
	final Map javaTypeProvider = new HashMap(); // key: element name, value: attribute which contains javaType
	
	/** set of "tagname>attribname", used to decide which attributes we should react to */	
	final Map attributeHandlers = new HashMap(); // completes a possible package or classname
	
	public HBMInfoExtractor() {
		setupTypeFinder();
		        
        setupJavaTypeHandlers();
		        
        setupPackageHandlers();
    
		setupFieldsPropertyHandlers();
		
		setupHibernateTypeHandlers();
		
		setupHibernateTypeDescriptors();
	}

	private void setupTypeFinder() {

		javaTypeProvider.put("class", "name");
		javaTypeProvider.put("subclass", "name");
		javaTypeProvider.put("joined-subclass", "name");
		javaTypeProvider.put("union-subclass", "name");
		// TODO: use eclipse java model to infer types of components property/fields
		javaTypeProvider.put("composite-id", "class");
		javaTypeProvider.put("component", "class");
	}

	private void setupHibernateTypeDescriptors() {
		addHibernateType("long","java.lang.Long","long");
		addHibernateType("short","java.lang.Short","short");
		addHibernateType("integer","java.lang.Integer","int");
		addHibernateType("byte","java.lang.Byte","byte");
		addHibernateType("float","java.lang.Float","float");
		addHibernateType("double","java.lang.Double","double");
		addHibernateType("character","java.lang.Character","char");
		addHibernateType("string","java.lang.String",null);
		addHibernateType("time","java.util.Date",null);
		addHibernateType("date","java.util.Date",null);
		addHibernateType("timestamp","java.util.Date",null);
		addHibernateType("boolean","java.lang.Boolean","boolean");
		addHibernateType("true_false","java.lang.Boolean","boolean");
		addHibernateType("yes_no","java.lang.Boolean","boolean");
		addHibernateType("big_decimal","java.math.BigDecimal",null);
		addHibernateType("binary","byte[]",null);
		addHibernateType("text","java.lang.String",null);
		addHibernateType("blob","java.sql.Blob",null);
		addHibernateType("clob","java.sql.Clob",null);
		addHibernateType("calendar","java.util.Calendar",null);
		addHibernateType("calendar_date","java.util.Calendar",null);
		addHibernateType("locale","java.util.Locale",null);
		addHibernateType("currency","java.util.Currency",null);
		addHibernateType("timezone","java.util.TimeZone",null);
		addHibernateType("class","java.lang.Class",null);
		addHibernateType("serializable","java.io.Serializable",null);
		addHibernateType("object","java.lang.Object",null);
		Collections.sort(hibernateTypes);
	}

	private void setupHibernateTypeHandlers() {
		HBMInfoHandler hibernateTypeFinder = new HibernateTypeHandler(this);
		attributeHandlers.put("filter-param>type", hibernateTypeFinder);
		attributeHandlers.put("id>type", hibernateTypeFinder);
		attributeHandlers.put("discriminator>type", hibernateTypeFinder);
		attributeHandlers.put("version>type", hibernateTypeFinder);
		attributeHandlers.put("property>type", hibernateTypeFinder);
		attributeHandlers.put("key-property>type", hibernateTypeFinder);
		attributeHandlers.put("element>type", hibernateTypeFinder);
		attributeHandlers.put("map-key>type", hibernateTypeFinder);
		attributeHandlers.put("index>type", hibernateTypeFinder);
		attributeHandlers.put("collection-id>type", hibernateTypeFinder);
		attributeHandlers.put("return-scalar>type", hibernateTypeFinder);
	}

	private void setupFieldsPropertyHandlers() {
		
		HBMInfoHandler fieldsFinder = new FieldPropertyHandler(this);
		attributeHandlers.put("property>name", fieldsFinder);
		attributeHandlers.put("key-property>name", fieldsFinder);
		attributeHandlers.put("id>name", fieldsFinder);
		attributeHandlers.put("composite-id>name", fieldsFinder);
		attributeHandlers.put("set>name", fieldsFinder);
		attributeHandlers.put("property>name", fieldsFinder);
		attributeHandlers.put("many-to-one>name", fieldsFinder);
		attributeHandlers.put("one-to-one>name", fieldsFinder);
		attributeHandlers.put("component>name", fieldsFinder);
		attributeHandlers.put("dynamic-component>name", fieldsFinder);
		attributeHandlers.put("properties>name", fieldsFinder);
		attributeHandlers.put("any>name", fieldsFinder);
		attributeHandlers.put("map>name", fieldsFinder);
		attributeHandlers.put("set>name", fieldsFinder);
		attributeHandlers.put("list>name", fieldsFinder);
		attributeHandlers.put("bag>name", fieldsFinder);
		attributeHandlers.put("idbag>name", fieldsFinder);
		attributeHandlers.put("array>name", fieldsFinder);
		attributeHandlers.put("primitive-array>name", fieldsFinder);
		attributeHandlers.put("query-list>name", fieldsFinder);
	}

	private void setupPackageHandlers() {
		HBMInfoHandler packageFinder = new PackageHandler(this);
		attributeHandlers.put("hibernate-mapping>package", packageFinder);
	}

	private void setupJavaTypeHandlers() {
		HBMInfoHandler classFinder = new JavaTypeHandler(this);
		attributeHandlers.put("class>name", classFinder);
		attributeHandlers.put("subclass>name", classFinder);
		attributeHandlers.put("joined-subclass>name", classFinder);
		attributeHandlers.put("union-subclass>name", classFinder);
		attributeHandlers.put("many-to-one>class", classFinder);
		attributeHandlers.put("one-to-many>class", classFinder);
		attributeHandlers.put("many-to-many>class", classFinder);
		attributeHandlers.put("composite-element>class", classFinder);
		attributeHandlers.put("component>class", classFinder);
		attributeHandlers.put("composite-id>class", classFinder);
	}
	
	List findMatchingHibernateTypes(String item) {
		List l = new ArrayList();
		boolean foundFirst = false;
		for (int i = 0; i < hibernateTypes.size(); i++) {
			HibernateTypeDescriptor element = (HibernateTypeDescriptor) hibernateTypes.get(i);
			if(element.getName().startsWith(item)) {
				foundFirst = true;
				l.add(element);
			} else if (foundFirst) {
				return l; // fail fast since if we dont get a match no future match can be found.
			}
		}
		return l;
	}


	/**
	 * @param start
	 * @param offset
	 * @param results replacementoffset is changed and array is sorted inplace for relevance
	 */
	void transpose(String start, int offset, IJavaCompletionProposal[] results) {
		// As all completions have made with the assumption on a empty
		// (or almost empty) string
		// we move the replacementoffset on every proposol to fit nicely
		// into our non-java code
		for (int i = 0; i < results.length; i++) {
			JavaCompletionProposal proposal = (JavaCompletionProposal) results[i]; // TODO: eclipse bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=84998
			System.out.println(proposal.getReplacementOffset());
			proposal.setReplacementOffset(proposal.getReplacementOffset() + (offset /*- start.length()*/));
		}
		Arrays.sort(results, new CompletionProposalComparator());		
	}


	

	/**
	 * @param holder
	 * @param root TODO
	 * @return nearest package attribute, null if none found. 
	 */
	String getPackageName(Node root) {
		if(root!=null) {
			while(!"hibernate-mapping".equals(root.getNodeName())) {
				root = root.getParentNode();
				if(root==null) return null;
			}
			NamedNodeMap attributes = root.getAttributes();
			for(int count = 0; count<attributes.getLength(); count++) {
				Node att = attributes.item(count);
				if("package".equals(att.getNodeName())) {
					return att.getNodeValue();
				}	
			}			
		}
		return null;		
	}

	protected boolean beginsWith(String aString, String prefix) {
		if (aString == null || prefix == null)
			return true;
		// (pa) 221190 matching independent of case to be consistant with Java
		// editor CA
		return aString.toLowerCase().startsWith(prefix.toLowerCase());
	}

	void generateTypeProposals(String matchString, int offset, List proposals, Set alreadyFound, IType[] classes) throws JavaModelException {
		for (int j = 0; j < classes.length; j++) {
			IType type = classes[j];
			if (!Flags.isAbstract(type.getFlags())) {
				String fullName = type.getFullyQualifiedName();
				String shortName = type.getElementName();
				if(alreadyFound.contains(fullName)) {
					continue;							
				} else {
					alreadyFound.add(fullName);
				}
				if (beginsWith(fullName,matchString) || beginsWith(shortName,matchString)) {
					CustomCompletionProposal proposal = new CustomCompletionProposal(fullName, //$NON-NLS-2$//$NON-NLS-1$
							offset, matchString.length(), fullName.length() + 1, null/*XMLEditorPluginImageHelper.getInstance().getImage(XMLEditorPluginImages.IMG_OBJ_ATTRIBUTE)*/,
							fullName, null, null, XMLRelevanceConstants.R_XML_ATTRIBUTE_VALUE);
					proposals.add(proposal);
				}
			}
		}
	}


	
	private void addHibernateType(String name, String returnClass, String primitiveClass) {
		hibernateTypes.add(new HibernateTypeDescriptor(name, returnClass, primitiveClass));
	}

	/**
	 * Returns attribute handler for the path.
	 * @param path a string on the form [xmltag]>[attributename] e.g. property>name
	 * @return
	 */
	public HBMInfoHandler getAttributeHandler(String path) {
		return (HBMInfoHandler) attributeHandlers.get(path);
	}

	/**
	 * @param node
	 * @return the name of the nearest type from the node or null if none found. 
	 */
	public String getNearestType(Node node) {
		if(node==null) return null;
		while(!javaTypeProvider.containsKey(node.getNodeName())) {
			node = node.getParentNode();			
			if(node==null) return null;
		}
		String attributeName = (String) javaTypeProvider.get(node.getNodeName());
		NamedNodeMap attributes = node.getAttributes();
		
		for(int count = 0; count<attributes.getLength(); count++) {
			Node att = attributes.item(count);
			if(attributeName.equals(att.getNodeName())) {
				String typename = att.getNodeValue();
				if(typename!=null && typename.indexOf('.')<0) {
					typename = getPackageName(node) + "." + typename;					
				}
				return typename;
			}
		}
		
		return null;
	}

	public IType getNearestTypeJavaElement(IJavaProject project, Node currentNode) {
		String nearestType = getNearestType(currentNode);
		if(nearestType!=null) {
			try {
				IType type = project.findType(nearestType);
				return type;
			} catch (JavaModelException e) {
				//ignore
			}
		}
		return null;
	}
}
