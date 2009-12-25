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

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.jboss.tools.common.meta.XAttribute;
import org.jboss.tools.common.meta.XChild;
import org.jboss.tools.common.meta.XModelEntity;
import org.jboss.tools.common.meta.constraint.XAttributeConstraint;
import org.jboss.tools.common.meta.constraint.impl.XAttributeConstraintAList;
import org.jboss.tools.common.meta.key.WizardKeys;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.ui.texteditors.propertyeditor.AbstractPropertiesContentAssistProcessor;
import org.jboss.tools.hibernate.ui.xml.form.HibConfig3PropertyFormLayoutData;
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

		List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();

		Map<String, XAttribute> attributes = getAttributes(object);

		if(context.isInComment()) {
			return new ICompletionProposal[0];
		}
		if(context.isInPropertyName()) {
			String[] ps = attributes.keySet().toArray(new String[0]);
			for (int i = 0; i < ps.length; i++) {
				String description = getDescription(ps[i]); //set more substantial description
				ICompletionProposal proposal = getNameProposal(ps[i], description, context);
				if(proposal != null) {
					result.add( proposal);
				}
			}	
		} else if(context.isInValue()) {
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
						String description = vs[i]; //set more substantial description
						ICompletionProposal proposal = getValueProposal(vs[i], description, context);
						if(proposal != null) {
							result.add( proposal);
						}
					}
				} else if("AccessibleJava".equals(attr.getEditor().getName())) { //$NON-NLS-1$
					result.addAll(getJavaTypeContentProposals(attr, context));
				} else {
					//TODO
				}
			}
		}
		return result.toArray(new ICompletionProposal[0]);
	}

	static Map<String, XAttribute> attributes = null;

	public static Map<String, XAttribute> getAttributes(XModelObject object) {
		if(attributes == null) {
			attributes = new TreeMap<String, XAttribute>();
			XModelEntity entity = object.getModel().getMetaData().getEntity(HibConfig3PropertyFormLayoutData.PROPERTY_FOLDER_ENTITY);
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

	public static String getDescription(String propertyName) {
		String dot = "."; //$NON-NLS-1$
		String key = propertyName;
		if(!key.endsWith(dot)) key += dot;
		key += "description"; //$NON-NLS-1$
		String description = WizardKeys.getString(key);
		if("@NULL_KEY@".equals(description) || description == null) {
			description = propertyName + "<p>TODO - add description into keys-hibernate-config.properties";
		}
		return description;
	}

}
