/*
 * Created on 19-Nov-2004
 *
 */
package org.hibernate.eclipse.mapper.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.eval.IEvaluationContext;
import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.CompletionProposalComparator;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.wst.sse.ui.internal.contentassist.CustomCompletionProposal;
import org.eclipse.wst.xml.ui.contentassist.ContentAssistRequest;
import org.eclipse.wst.xml.ui.contentassist.XMLRelevanceConstants;
import org.hibernate.eclipse.mapper.editors.HBMXmlResultCollector.Settings;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author max
 *
 */
public class HBMXmlTypeContributor {

	/** set of "tagname>attribname", used to decide which attributes we should react to */	
	final Map typedAttributes = new HashMap(); // completes a possible package or classname
	final Set canProvideTypeViaName = new HashSet();
	private final IJavaProject javaProject;
	//private HBMXmlResultCollector rc;
	
    static public abstract class CompletionHandler {
        
        protected final Settings settings;

        CompletionHandler(HBMXmlResultCollector.Settings settings) {
            this.settings = settings;
            
        }
        
        abstract public ICompletionProposal[] handle(Node node, String attributeName, String start, int offset); 
    }
    
    
    public HBMXmlTypeContributor(IJavaProject javaProject) {

		this.javaProject = javaProject;
		//rc = new HBMXmlResultCollector(javaProject);
        
        Settings settings = new Settings();
        settings.setAcceptClasses(true);
        settings.setAcceptInterfaces(true);
        settings.setAcceptPackages(true);
        settings.setAcceptTypes(true);
        CompletionHandler classFinder = new CompletionHandler(settings) {
            public ICompletionProposal[] handle(Node node, String attributeName, String start, int offset) {
                return handleTypes(this.settings,getPackageName(node), start, offset);            
            }
        };
        
		typedAttributes.put("class>name", classFinder);
		typedAttributes.put("subclass>name", classFinder);
		typedAttributes.put("joined-subclass>name", classFinder);
		typedAttributes.put("union-subclass>name", classFinder);
		
		typedAttributes.put("many-to-one>class", classFinder);
		typedAttributes.put("one-to-many>class", classFinder);
		typedAttributes.put("many-to-many>class", classFinder);
		typedAttributes.put("composite-element>class", classFinder);
		typedAttributes.put("component>class", classFinder);
		
        settings = new Settings();
        settings.setAcceptPackages(true);
        CompletionHandler packageFinder = new CompletionHandler(settings) {
            public ICompletionProposal[] handle(Node node, String attributeName, String start, int offset) {
                return handleTypes(this.settings,getPackageName(node), start, offset);            
            }
        };        
        typedAttributes.put("hibernate-mapping>package", packageFinder);
    
		//TODO: should also try to find properties getXXX()
		CompletionHandler fieldsFinder = new CompletionHandler(settings) {
            public ICompletionProposal[] handle(Node node, String attributeName, String start, int offset) {
                return handleFields(node, attributeName, start, offset);            
            }
        };
		
		typedAttributes.put("property>name", fieldsFinder);
		typedAttributes.put("id>name", fieldsFinder);;
		typedAttributes.put("set>name", fieldsFinder);;
		
		typedAttributes.put("property>name", fieldsFinder);;
		typedAttributes.put("many-to-one>name", fieldsFinder);;
		typedAttributes.put("one-to-one>name", fieldsFinder);;
		typedAttributes.put("component>name", fieldsFinder);;
		typedAttributes.put("dynamic-component>name", fieldsFinder);;
		typedAttributes.put("properties>name", fieldsFinder);;
		typedAttributes.put("any>name", fieldsFinder);;
		typedAttributes.put("map>name", fieldsFinder);;
		typedAttributes.put("set>name", fieldsFinder);;
		typedAttributes.put("list>name", fieldsFinder);;
		typedAttributes.put("bag>name", fieldsFinder);;
		typedAttributes.put("idbag>name", fieldsFinder);;
		typedAttributes.put("array>name", fieldsFinder);;
		typedAttributes.put("primitive-array>name", fieldsFinder);;
		typedAttributes.put("query-list>name", fieldsFinder);;
		
		CompletionHandler hibernateTypeFinder = new CompletionHandler(settings) {
            public ICompletionProposal[] handle(Node node, String attributeName, String start, int offset) {
                return handleHibernateTypes(node, attributeName, start, offset);            
            }
        };
		typedAttributes.put("filter-param>type", hibernateTypeFinder);
		typedAttributes.put("id>type", hibernateTypeFinder);
		typedAttributes.put("discriminator>type", hibernateTypeFinder);
		typedAttributes.put("version>type", hibernateTypeFinder);
		typedAttributes.put("property>type", hibernateTypeFinder);
		typedAttributes.put("key-property>type", hibernateTypeFinder);
		typedAttributes.put("element>type", hibernateTypeFinder);
		typedAttributes.put("map-key>type", hibernateTypeFinder);
		typedAttributes.put("index>type", hibernateTypeFinder);
		typedAttributes.put("collection-id>type", hibernateTypeFinder);
		typedAttributes.put("return-scalar>type", hibernateTypeFinder);

		
		
		canProvideTypeViaName.add("class");
		canProvideTypeViaName.add("subclass");
		canProvideTypeViaName.add("joined-subclass");
		canProvideTypeViaName.add("union-subclass");		
			
	}
	
	
	


	public List getAttributeValueProposals(String attributeName, String start, int offset, ContentAssistRequest contentAssistRequest) {
		Node node = contentAssistRequest.getNode();
		List proposals = new ArrayList();
		
		String path = node.getNodeName() + ">" + attributeName;
        CompletionHandler handler = (CompletionHandler) typedAttributes.get(path);
		if (handler != null) {
			proposals.addAll(Arrays.asList(handler.handle(node, attributeName, start, offset)));
		}
		
		if (true) {
			String string = contentAssistRequest.getDocumentRegion().getText();
			string = string.replace('<', '[');
			string = string.replace('>', ']');
			CompletionProposal completionProposal = new CompletionProposal("[" + start + "],[" + path + "],[" + offset + "]", offset, 1, 4, null, null, null, string);
			
			proposals.add(completionProposal);
		}

		return proposals;
	}

	/**
	 * @param node
	 * @param attributeName
	 * @param start
	 * @param offset
	 * @return
	 */
	private ICompletionProposal[] handleFields(Node node, String attributeName, String start, int offset) {
		if(javaProject!=null) {
			String typename = getNearestType(node);			
			if(typename!=null && typename.indexOf('.')<0) {
				typename = getPackageName(node) + "." + typename;
			}			
			HBMXmlResultCollector rc = null;
			try {
				IType type = javaProject.findType(typename);
				if(type==null) return new ICompletionProposal[0]; //nothing to look for then
				rc = new HBMXmlResultCollector(javaProject);
				rc.setAccepts(false,false,false,false,true,false); // TODO: only handle properties ?
				//rc.reset(offset, javaProject, null);
				
				
				type.codeComplete(start.toCharArray(), -1, start.length(), new char[0][0], new char[0][0], new int[0], false, rc);
			} catch(JavaModelException jme) {
				// TODO: report
			}
			
			IJavaCompletionProposal[] results = rc.getJavaCompletionProposals();
			transpose(start, offset, results);
			return results; 
		}
		
		return new ICompletionProposal[0];
	}

	// TODO: externalize this list and additional info!
	static List hibernateTypes = new ArrayList();
	static {
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
	
	static class HibernateTypeDescriptor implements Comparable {

		private final String name;
		private final String returnClass;
		private final String primitiveClass;

		public HibernateTypeDescriptor(String name, String returnClass, String primitiveClass) {
			this.name = name;
			this.returnClass = returnClass;
			this.primitiveClass = primitiveClass;
		}

		public String getName() {
			return name;
		}
		

		public String getPrimitiveClass() {
			return primitiveClass;
		}
		

		public String getReturnClass() {
			return returnClass;
		}

		public int compareTo(Object o) {
			
			return name.compareTo(((HibernateTypeDescriptor)o).getName());
		}
		
		
	}
	
	private static void addHibernateType(String name, String returnClass, String primitiveClass) {
		hibernateTypes.add(new HibernateTypeDescriptor(name, returnClass, primitiveClass));
	}
	
	protected ICompletionProposal[] handleHibernateTypes(Node node, String attributeName, String matchString, int offset) {
		List types = findMatchingHibernateTypes(matchString);
		
		List proposals = new ArrayList(types.size());		
		for (Iterator iter = types.iterator(); iter.hasNext();) {
			HibernateTypeDescriptor element = (HibernateTypeDescriptor) iter.next();
			String extendedinfo = "<b>Hibernate type</b>: " + element.getName();
			if(element.getReturnClass()!=null) {
				extendedinfo += "<br><b>Return class</b>: " + element.getReturnClass();				
			}
			if(element.getPrimitiveClass()!=null) {
				extendedinfo += "<br><b>Return primitive</b>: " + element.getPrimitiveClass();
			}
	 		proposals.add(new CompletionProposal(element.getName(), offset, matchString.length(), element.getName().length(), null, null, null, extendedinfo));
		}
		
		try {
			IType typeInterface = javaProject.findType("org.hibernate.usertype.CompositeUserType");
			Set alreadyFound = new HashSet();			
			if (typeInterface != null) {
				ITypeHierarchy hier = typeInterface.newTypeHierarchy(javaProject, new NullProgressMonitor());
				IType[] classes = hier.getAllSubtypes(typeInterface); // TODO: cache these results ?
				generateTypeProposals(matchString, offset, proposals, alreadyFound, classes);				
			}
			
			typeInterface = javaProject.findType("org.hibernate.usertype.UserType");
			if (typeInterface != null) {
				ITypeHierarchy hier = typeInterface.newTypeHierarchy(javaProject, new NullProgressMonitor());
				IType[] classes = hier.getAllSubtypes(typeInterface); // TODO: cache these results ?
				generateTypeProposals(matchString, offset, proposals, alreadyFound, classes);				
			}
		} catch (CoreException e) {
			throw new RuntimeException(e); // TODO: log as error!
		}
		
		ICompletionProposal[] result = (ICompletionProposal[]) proposals.toArray(new ICompletionProposal[proposals.size()]);
		return result;
	}





	private void generateTypeProposals(String matchString, int offset, List proposals, Set alreadyFound, IType[] classes) throws JavaModelException {
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
	
	





	private List findMatchingHibernateTypes(String item) {
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
	 * @param node
	 * @return
	 */
	private String getNearestType(Node node) {
		while(!canProvideTypeViaName.contains(node.getNodeName())) {
			node = node.getParentNode();
			if(node==null) return null;
		}
		NamedNodeMap attributes = node.getAttributes();
		int count = 0;
		while (count<attributes.getLength()-1) {
			Node att = attributes.item(count);
			if("name".equals(att.getNodeName())) {
				return att.getNodeValue();
			}
		}
		
		return null;
	}


	/**
	 * @param settings 
	 * @param node
	 * @param attribute
	 * @param start
	 * @param offset
	 * @param proposals
	 * @return
	 */
	private ICompletionProposal[] handleTypes(Settings settings, String packageName, String start, int offset) {
		
			if (javaProject != null) {
				IEvaluationContext context = javaProject.newEvaluationContext();                
                if(packageName!=null) {
                    context.setPackageName(packageName);
                }
				
				
				HBMXmlResultCollector rc = new HBMXmlResultCollector(javaProject);
				//rc.reset(offset, javaProject, null);
				rc.setAccepts(settings);
				try {
					// cannot send in my own document as it won't compile as
					// java - so we just send in
					// the smallest snippet possible
					context.codeComplete(start, start.length(), rc);
				} catch (JavaModelException jme) {
					// TODO: handle/report!
					jme.printStackTrace();
				}
				IJavaCompletionProposal[] results = rc.getJavaCompletionProposals();
				transpose(start, offset, results);
				return results;
			}
		return new JavaCompletionProposal[0];
	}


	/**
	 * @param start
	 * @param offset
	 * @param results replacementoffset is changed and array is sorted inplace for relevance
	 */
	private void transpose(String start, int offset, IJavaCompletionProposal[] results) {
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
	private String getPackageName(Node root) {
		if(root!=null) {
			while(!"hibernate-mapping".equals(root.getNodeName())) {
				root = root.getParentNode();
				if(root==null) return null;
			}
			NamedNodeMap attributes = root.getAttributes();
			int count = 0;
			while (count<attributes.getLength()-1) {
				Node att = attributes.item(count);
				if("package".equals(att.getNodeName())) {
					return att.getNodeValue();
				}
			}
		}
		return null;		
	}


	/** presumably used to prioritize contributions...why not use a comparator instead ? */
	public boolean appendAtStart() {
		return true;
	}

	protected boolean beginsWith(String aString, String prefix) {
		if (aString == null || prefix == null)
			return true;
		// (pa) 221190 matching independent of case to be consistant with Java
		// editor CA
		return aString.toLowerCase().startsWith(prefix.toLowerCase());
	}
}
