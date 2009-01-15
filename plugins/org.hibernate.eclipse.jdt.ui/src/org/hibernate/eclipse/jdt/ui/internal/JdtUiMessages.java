package org.hibernate.eclipse.jdt.ui.internal;

import org.eclipse.osgi.util.NLS;

public class JdtUiMessages extends NLS {
	private static final String BUNDLE_NAME = "org.hibernate.eclipse.jdt.ui.internal.JdtUiMessages"; //$NON-NLS-1$
	public static String AllEntitiesProcessor_header;
	public static String AllEntitiesProcessor_message;
	public static String AllEntitiesProcessor_setup_annotation_generation_preference;
	public static String AllEntitiesProcessor_annotate_fields;
	public static String AllEntitiesProcessor_annotate_getters;
	public static String AllEntitiesProcessor_auto_select_from_class_preference;
	public static String CriteriaQuickAssistProcessor_copy_to_criteria_editor;
	public static String CriteriaQuickAssistProcessor_errormessage;
	public static String DebugJavaCompletionProposalComputer_displaystring;
	public static String HQLJavaCompletionProposalComputer_errormessage;
	public static String HQLQuickAssistProcessor_copy_to_hql_editor;
	public static String JPAMapToolActionPulldownDelegate_menu;
	public static String SaveQueryEditorListener_replacequestion;
	public static String SaveQueryEditorListener_replacetitle;
	public static String SaveQueryEditorListener_hql_editor;
	public static String SaveQueryEditorListener_cri_editor;
	public static String SaveQueryEditorListener_change_name;
	public static String SaveQueryEditorListener_composite_change_name;
	public static String SaveQueryEditorListener_refactoringtitle;
	public static String SaveQueryEditorListener_replacetitle_info;
	public static String SaveQueryEditorListener_replacequestion_confirm;
	public static String SaveQueryEditorListener_errormessage;
	public static String JPAMapToolActor_message_title;
	public static String JPAMapToolActor_message;
	public static String ResolveAmbiguous_column_Class;
	public static String ResolveAmbiguous_column_Property;
	public static String ResolveAmbiguous_column_Type;
	public static String ResolveAmbiguous_column_Related;
	public static String ResolveAmbiguous_column_Owner;
	public static String ResolveAmbiguous_empty;
	public static String ResolveAmbiguous_message;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, JdtUiMessages.class);
	}

	private JdtUiMessages() {
	}
}
