/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.ui.xml.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.text.javadoc.JavadocContentAccess2;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.meta.XChild;
import org.jboss.tools.common.meta.XModelEntity;
import org.jboss.tools.common.meta.constraint.XAttributeConstraint;
import org.jboss.tools.common.meta.constraint.impl.XAttributeConstraintAList;
import org.jboss.tools.common.meta.key.WizardKeys;
import org.jboss.tools.common.model.ui.attribute.adapter.JavaClassContentAssistProvider;
import org.jboss.tools.common.model.ui.texteditors.propertyeditor.AbstractPropertiesContentAssistProcessor;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.hibernate.xml.model.impl.HibConfigComplexPropertyImpl;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class HibernatePropertiesContentAssistProcessor extends
		AbstractPropertiesContentAssistProcessor {

	public HibernatePropertiesContentAssistProcessor() {}

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {
		Context context = getContext(viewer, offset);
		String text = viewer.getDocument().get();

		List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();

		Map<String, XAttribute> attributes = getAttributes();

		if(context.isInComment()) {
			return new ICompletionProposal[0];
		}
		if(context.isInPropertyName()) {
			int nameOffset = context.getNameOffset();
			String namePrefix = nameOffset < offset ? text.substring(nameOffset, offset) : ""; //$NON-NLS-1$
			String[] ps = attributes.keySet().toArray(new String[0]);
			for (int i = 0; i < ps.length; i++) {
				if(context.hasProperty(ps[i])) continue;
				String description = WizardKeys.getString(ps[i] + ".description"); //$NON-NLS-1$
				if("@NULL_KEY@".equals(description) || description == null) {
					description = ps[i] + "<p>TODO - add description into keys-hibernate-config.properties";
				}
				if(ps[i].startsWith(namePrefix)) {
					CompletionProposal proposal = new CompletionProposal(
							ps[i],
							nameOffset,
							context.getNameLength(),
							ps[i].length(),
							null,
							ps[i], 
							null, 
							description);
					result.add( proposal);
				}
			}	
		} else if(context.isInValue()) {
			int valueOffset = context.getValueOffset();
			String valuePrefix = valueOffset < offset && valueOffset >= 0 ? text.substring(valueOffset, offset) : ""; //$NON-NLS-1$
			String propertyName = context.getPropertyName();
			if(attributes.containsKey(propertyName)) {
				XAttribute attr = attributes.get(propertyName);
				if(attr == null) {
					return new ICompletionProposal[0];
				}
				XAttributeConstraint c = attr.getConstraint();
				if(c instanceof XAttributeConstraintAList) {
					String[] vs = ((XAttributeConstraintAList)c).getValues();
					for (int i = 0; i < vs.length; i++) {
						if(vs[i].length() == 0) continue;
						if(vs[i].startsWith(valuePrefix)) {
							CompletionProposal proposal = new CompletionProposal(
									vs[i],
									valueOffset,
									context.getValueLength(),
									vs[i].length(),
									null,
									vs[i], 
									null, 
									vs[i]); //should we put more substantial description?
							result.add( proposal);
						}
					}
				} else if("AccessibleJava".equals(attr.getEditor().getName())) { //$NON-NLS-1$
					JavaClassContentAssistProvider p = new JavaClassContentAssistProvider();
					p.init(object, attr);
					IContentProposalProvider pp = p.getContentProposalProvider();
					IContentProposal[] ps = pp.getProposals(valuePrefix, valuePrefix.length());
					IProject project = EclipseResourceUtil.getProject(object);
					IJavaProject jp = EclipseResourceUtil.getJavaProject(project);
					if(ps != null) for (int i = 0; i < ps.length; i++) {
						String value = ps[i].getContent();
						String descr = null;
						if(jp != null) try {
							IType type = EclipseJavaUtil.findType(jp, value);
							if(type != null) descr = JavadocContentAccess2.getHTMLContent(type, true);
						} catch (JavaModelException e) {
							//ignore
						}
						CompletionProposal proposal = new CompletionProposal(
								value,
								valueOffset,
								context.getValueLength(),
								value.length(),
								null,
								ps[i].getLabel(),
								null, 
								descr != null ? descr : ps[i].getDescription());
						result.add(proposal);
					}
				} else {
					//TODO
				}
			}
		}
		return result.toArray(new ICompletionProposal[0]);
	}

	static Map<String, XAttribute> attributes = null;

	public Map<String, XAttribute> getAttributes() {
		if(attributes == null) {
			attributes = new TreeMap<String, XAttribute>();
			XModelEntity entity = object.getModel().getMetaData().getEntity("HibConfig3PropertiesFolder"); //$NON-NLS-1$
			XChild[] cs = entity.getChildren();
			for (int i = 0; i < cs.length;  i++) {
				if(cs[i].isRequired()) {
					XModelEntity cEntity = object.getModel().getMetaData().getEntity(cs[i].getName());
					if(cEntity == null) continue;
					XAttribute[] as = cEntity.getAttributes();
					for (int j = 0; j < as.length; j++) {
						String hProperty = as[j].getProperty(HibConfigComplexPropertyImpl.H_PROPERTY);
						if(hProperty == null || hProperty.length() == 0) continue;
						attributes.put(hProperty, as[j]);
					}
				}
			}
		}
		return attributes;
		
	}

}
