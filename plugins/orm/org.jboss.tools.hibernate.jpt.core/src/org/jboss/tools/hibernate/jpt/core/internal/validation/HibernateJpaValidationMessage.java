/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal.validation;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jpt.common.core.internal.utility.EmptyTextRange;
import org.eclipse.jpt.common.core.utility.TextRange;
import org.eclipse.jpt.jpa.core.JpaModel;
import org.eclipse.jpt.jpa.core.JpaProject;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJptPlugin;
import org.jboss.tools.hibernate.jpt.core.internal.context.Messages;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJpaValidationMessage {
	
	private static String[] DEFAULT_PARMS = new String[0];
	private static TextRange DEFAULT_TEXT_RANGE = EmptyTextRange.instance();
	
	public static IMessage buildMessage(
			int defaultSeverity, String messageId, JpaModel targetObject) {
		return buildMessage(defaultSeverity, messageId, DEFAULT_PARMS, targetObject);
	}
	
	public static IMessage buildMessage(
			int defaultSeverity, String messageId, String[] parms, JpaModel targetObject) {
		return buildMessage(defaultSeverity, messageId, parms, targetObject, DEFAULT_TEXT_RANGE);
	}
	
	public static IMessage buildMessage(
			int defaultSeverity, String messageId, JpaModel targetObject, TextRange textRange) {
		return buildMessage(defaultSeverity, messageId, DEFAULT_PARMS, targetObject, textRange);
	}
	
	public static IMessage buildMessage(
			int defaultSeverity, String messageId, String[] parms, JpaModel targetObject, TextRange textRange) {
		return buildMessage(defaultSeverity, messageId, parms, targetObject.getResource(), DEFAULT_TEXT_RANGE);
	}

	public static IMessage buildMessage(
			int defaultSeverity, String messageId, IResource targetObject) {
		return buildMessage(defaultSeverity, messageId, DEFAULT_PARMS, targetObject);
	}
	
	public static IMessage buildMessage(
			int defaultSeverity, String messageId, String[] parms, IResource targetObject) {
		return buildMessage(defaultSeverity, messageId, parms, targetObject, DEFAULT_TEXT_RANGE);
	}
	
	public static IMessage buildMessage(
			int defaultSeverity, String messageId, IResource targetObject, TextRange textRange) {
		return buildMessage(defaultSeverity, messageId, DEFAULT_PARMS, targetObject, textRange);
	}
	
	public static IMessage buildMessage(
			int defaultSeverity, String messageId, String[] parms, IResource targetObject, TextRange textRange) {
		
		//determine whether default severity should be overridden
		int severity = defaultSeverity;
		int severityPreference = getProblemSeverityPreference(targetObject, messageId);
		if (severityPreference != -1){
			severity = severityPreference;
		}
		IMessage message = new LocalMessage(severity, messageId, parms, targetObject);
		message.setMarkerId(JpaProject.MARKER_TYPE);
		if (textRange == null) {
			//log an exception and then continue without setting location information
			//At least the user will still get the validation message and will
			//be able to see other validation messages with valid textRanges
			HibernateJptPlugin.logException(new NullPointerException("Null text range for message ID: " + messageId)); //$NON-NLS-1$
		}
		else {
			message.setLineNo(textRange.getLineNumber());
			message.setOffset(textRange.getOffset());
			message.setLength(textRange.getLength());
		}
		return message;
	}
	
	
	private HibernateJpaValidationMessage() {
		super();
		throw new UnsupportedOperationException();
	}	
	
	/* Start inlined code to deal with getProblemSeverityPreference compilation problem */
	private static final String ERROR = "error"; //$NON-NLS-1$
	private static final String WARNING = "warning"; //$NON-NLS-1$
	private static final String INFO = "info"; //$NON-NLS-1$
	private static final int NO_SEVERITY_PREFERENCE = -1;
	private static final String PROBLEM_PREFIX = "problem."; //$NON-NLS-1$
	public static final String LEGACY_PLUGIN_ID = "org.eclipse.jpt.core";  //$NON-NLS-1$
	private static String appendProblemPrefix(String messageId) {
		return PROBLEM_PREFIX + messageId;
	}
	private static IEclipsePreferences getLegacyPreferences(IScopeContext context) {
		return context.getNode(LEGACY_PLUGIN_ID);
	}
	private static IEclipsePreferences getLegacyWorkspacePreferences() {
		return getLegacyPreferences(InstanceScope.INSTANCE);
	}
	private static String getLegacyWorkspacePreference(IProject project, String key) {
		return getLegacyWorkspacePreferences().get(key, null);
	}
	private static String getProblemPreference(IResource targetObject, String messageId) {
		return getLegacyWorkspacePreference(
				targetObject.getProject(), 
				appendProblemPrefix(messageId));

	}
	private static int getProblemSeverityPreference(IResource targetObject, String messageId) {
		String problemPreference = getProblemPreference(targetObject, messageId);		
		if(problemPreference == null) {
			return NO_SEVERITY_PREFERENCE;
		} 
		else if (problemPreference.equals(ERROR)) {
			return IMessage.HIGH_SEVERITY;
		} 
		else if (problemPreference.equals(WARNING)) {
			return IMessage.NORMAL_SEVERITY;
		} 
		else if (problemPreference.equals(INFO)) {
			return IMessage.LOW_SEVERITY;
		}
		return NO_SEVERITY_PREFERENCE;
	}
	/* End inlined code to deal with getProblemSeverityPreference compilation problem */
	
	/**
	 * Hack class needed to make JPA/Validation API pick up our classloader instead of its own.
	 * 
	 * @author max
	 *
	 */
	static class LocalMessage extends Message {

		public LocalMessage(int severity, String message,
				String[] strings, Object resource) {
			super(Messages.class.getName(), severity, message, strings, resource);
		}
	}

}
