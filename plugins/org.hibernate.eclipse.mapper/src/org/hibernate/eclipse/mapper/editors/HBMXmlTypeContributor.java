/*
 * Created on 19-Nov-2004
 *
 */
package org.hibernate.eclipse.mapper.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.eval.IEvaluationContext;
import org.eclipse.jdt.internal.codeassist.CompletionEngine;
import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposal;
import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposalComparator;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.hibernate.eclipse.mapper.editors.HBMXmlResultCollector.Settings;
import org.jboss.ide.eclipse.jdt.xml.ui.assist.contributor.IAttributeValueContributor;
import org.jboss.ide.eclipse.jdt.xml.ui.reconciler.IReconcilierHolder;
import org.jboss.ide.eclipse.jdt.xml.ui.reconciler.XMLNode;

/**
 * @author max
 *
 */
public class HBMXmlTypeContributor implements IAttributeValueContributor {

	/** set of "tagname>attribname", used to decide which attributes we should react to */	
	final Map typedAttributes = new HashMap();
	final Set fieldAttributes = new HashSet();
	final Set canProvideTypeViaName = new HashSet();
	private final IJavaProject javaProject;
	private HBMXmlResultCollector rc;
	
    static public abstract class CompletionHandler {
        
        protected final Settings settings;

        CompletionHandler(HBMXmlResultCollector.Settings settings) {
            this.settings = settings;
            
        }
        
        abstract public ICompletionProposal[] handle(XMLNode node, XMLNode attribute, String start, int offset); 
    }
    
    
    public HBMXmlTypeContributor(IJavaProject javaProject) {

		this.javaProject = javaProject;
		rc = new HBMXmlResultCollector();
        
        Settings settings = new Settings();
        settings.setAcceptClasses(true);
        settings.setAcceptInterfaces(true);
        settings.setAcceptPackages(true);
        settings.setAcceptTypes(true);
        CompletionHandler classFinder = new CompletionHandler(settings) {
            public ICompletionProposal[] handle(XMLNode node, XMLNode attribute, String start, int offset) {
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
            public ICompletionProposal[] handle(XMLNode node, XMLNode attribute, String start, int offset) {
                return handleTypes(this.settings,getPackageName(node), start, offset);            
            }
        };        
        typedAttributes.put("hibernate-mapping>package", packageFinder);
        		
		fieldAttributes.add("property>name");
		fieldAttributes.add("id>name");
		fieldAttributes.add("set>name");
		
		fieldAttributes.add("property>name");
		fieldAttributes.add("many-to-one>name");
		fieldAttributes.add("one-to-one>name");
		fieldAttributes.add("component>name");
		fieldAttributes.add("dynamic-component>name");
		fieldAttributes.add("properties>name");
		fieldAttributes.add("any>name");
		fieldAttributes.add("map>name");
		fieldAttributes.add("set>name");
		fieldAttributes.add("list>name");
		fieldAttributes.add("bag>name");
		fieldAttributes.add("idbag>name");
		fieldAttributes.add("array>name");
		fieldAttributes.add("primitive-array>name");
		fieldAttributes.add("query-list>name");
		
		canProvideTypeViaName.add("class");
		canProvideTypeViaName.add("subclass");
		canProvideTypeViaName.add("joined-subclass");
		canProvideTypeViaName.add("union-subclass");		
			
	}
	
	
	public List getAttributeValueProposals(IReconcilierHolder holder,
			IDocument doc, XMLNode node, XMLNode attribute, char quote,
			String start, int offset) {
		
		List proposals = new ArrayList();
		
		String path = node.getName() + ">" + attribute.getName();
        CompletionHandler handler = (CompletionHandler) typedAttributes.get(path);
		if (handler != null) {
			proposals.addAll(Arrays.asList(handler.handle(node, attribute, start, offset)));
		}
		if (fieldAttributes.contains(path)) {	
			proposals.addAll(Arrays.asList(handleFields(node, attribute, start, offset)));
		}
		
		//if (true) proposals.add(new CompletionProposal("start=[" + start + "], path=[" + path + "]", offset, 1, 4));

		return proposals;
	}

	/**
	 * @param node
	 * @param attribute
	 * @param start
	 * @param offset
	 * @return
	 */
	private ICompletionProposal[] handleFields(XMLNode node, XMLNode attribute, String start, int offset) {
		if(javaProject!=null) {
			String typename = getNearestType(node);
			if(typename.indexOf('.')<0) {
				typename = getPackageName(node) + "." + typename;
			}
			try {
				IType type = javaProject.findType(typename);
				if(type==null) return new ICompletionProposal[0]; //nothing to look for then
				rc.reset(offset, javaProject, null);
				rc.setAccepts(false,false,false,false,true,false); // TODO: only handle properties ?
				
				type.codeComplete(start.toCharArray(), -1, start.length(), new char[0][0], new char[0][0], new int[0], false, rc);
			} catch(JavaModelException jme) {
				// TODO: report
			}
			
			JavaCompletionProposal[] results = rc.getResults();
			transpose(start, offset, results);
			return results; 
		}
		
		return new ICompletionProposal[0];
	}

	
	
	/**
	 * @param node
	 * @return
	 */
	private String getNearestType(XMLNode node) {
		while(!canProvideTypeViaName.contains(node.getName())) {
			node = node.getParent();
			if(node==null) return null;
		}
		List attributes = node.getAttributes();
		Iterator iterator = attributes.iterator();
		while (iterator.hasNext()) {
			XMLNode att = (XMLNode) iterator.next();
			if("name".equals(att.getName())) {
				return att.getValue();
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

				rc.reset(offset, javaProject, null);
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
				JavaCompletionProposal[] results = rc.getResults();
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
	private void transpose(String start, int offset, JavaCompletionProposal[] results) {
		// As all completions have made with the assumption on a empty
		// (or almost empty) string
		// we move the replacementoffset on every proposol to fit nicely
		// into our non-java code
		for (int i = 0; i < results.length; i++) {
			JavaCompletionProposal proposal = results[i];
			proposal.setReplacementOffset(proposal.getReplacementOffset() + (offset - start.length()));
		}
		Arrays.sort(results, JavaCompletionProposalComparator
				.getInstance());		
	}


	/**
	 * @param holder
	 * @param root TODO
	 * @return nearest package attribute, null if none found. 
	 */
	private String getPackageName(XMLNode root) {
		if(root!=null) {
			while(!"hibernate-mapping".equals(root.getName())) {
				root = root.getParent();
				if(root==null) return null;
			}
			List attributes = root.getAttributes();
			Iterator iterator = attributes.iterator();
			while (iterator.hasNext()) {
				XMLNode att = (XMLNode) iterator.next();
				if("package".equals(att.getName())) {
					return att.getValue();
				}
			}
		}
		return null;		
	}


	/** presumably used to prioritize contributions...why not use a comparator instead ? */
	public boolean appendAtStart() {
		return true;
	}

}
