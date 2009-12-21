package org.hibernate.eclipse.jdt.ui.internal;

import org.eclipse.osgi.util.NLS;

public class JdtUiMessages extends NLS {
	private static final String BUNDLE_NAME = "org.hibernate.eclipse.jdt.ui.internal.JdtUiMessages"; //$NON-NLS-1$
	public static String AllEntitiesProcessor_header;
	public static String AllEntitiesProcessor_message;
	public static String AllEntitiesProcessor_preferred_location_annotations;
	public static String AllEntitiesProcessor_enable_optimistic_locking;
	public static String AllEntitiesProcessor_default_string_length;
	public static String AllEntitiesProcessor_fields;
	public static String AllEntitiesProcessor_getters;
	public static String AllEntitiesProcessor_auto_select_from_class_preference;
	public static String CriteriaQuickAssistProcessor_copy_to_criteria_editor;
	public static String CriteriaQuickAssistProcessor_errormessage;
	public static String DebugJavaCompletionProposalComputer_displaystring;
	public static String HQLJavaCompletionProposalComputer_errormessage;
	public static String HQLQuickAssistProcessor_copy_to_hql_editor;
	public static String JPAMapToolActionPulldownDelegate_menu;
	public static String SaveQueryEditorListener_replacequestion;
	public static String SaveQueryEditorListener_hql_editor;
	public static String SaveQueryEditorListener_cri_editor;
	public static String SaveQueryEditorListener_change_name;
	public static String SaveQueryEditorListener_composite_change_name;
	public static String SaveQueryEditorListener_refactoringtitle;
	public static String SaveQueryEditorListener_replacetitle_info;
	public static String SaveQueryEditorListener_replacequestion_confirm;
	public static String JPAMapToolActor_message_title;
	public static String JPAMapToolActor_message;
	public static String ResolveAmbiguous_column_Class;
	public static String ResolveAmbiguous_column_Property;
	public static String ResolveAmbiguous_column_Type;
	public static String ResolveAmbiguous_column_Related;
	public static String ResolveAmbiguous_column_Owner;
	public static String ResolveAmbiguous_empty;
	public static String ResolveAmbiguous_message;
	public static String AddRemoveTableComposite_add_class;
	public static String AddRemoveTableComposite_add_package;
	public static String AddRemoveTableComposite_java_types_title;
	public static String AddRemoveTableComposite_java_packages_title;
	public static String AddRemoveTableComposite_java_select_types;
	public static String AddRemoveTableComposite_java_select_packages;
	
	public static String NewHibernateMappingElementsSelectionPage2_description;
	public static String NewHibernateMappingFilePage_class_name_column;
	public static String NewHibernateMappingFilePage_file_name_column;
	public static String NewHibernateMappingFilePage_hibernate_xml_mapping_file;
	public static String NewHibernateMappingFilePage_project_name_column;
	public static String NewHibernateMappingFilePage_this_wizard_creates;
	
	public static String NewHibernateMappingFileWizard_create_hibernate_xml_mapping_file;
	public static String NewHibernateMappingFileWizard_create_empty_xml_mapping_file;
	public static String NewHibernateMappingFileWizard_look_for_dependent_cu;
	public static String NewHibernateMappingFileWizard_selection_cant_be_empty;
	
	public static String EntitiesSource_header;
	public static String EntitiesSource_description;
	public static String EntitiesList_header;
	public static String EntitiesList_description;
	
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, JdtUiMessages.class);
	}

	private JdtUiMessages() {
	}
}
