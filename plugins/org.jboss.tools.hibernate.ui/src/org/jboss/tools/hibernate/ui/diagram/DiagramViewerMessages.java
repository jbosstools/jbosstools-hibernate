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
package org.jboss.tools.hibernate.ui.diagram;

import org.eclipse.osgi.util.NLS;

public class DiagramViewerMessages extends NLS {
	private static final String BUNDLE_NAME = "org.jboss.tools.hibernate.ui.diagram.DiagramViewerMessages"; //$NON-NLS-1$

	private DiagramViewerMessages() {
	}

	static {
		NLS.initializeMessages(BUNDLE_NAME, DiagramViewerMessages.class);
	}

	public static String ViewMenu_label_text;
	public static String EditorActionContributor_refresh_visual_mapping;
	public static String DiagramViewer_diagram_for;
	public static String AutoLayoutAction_auto_layout;
	public static String CollapseAllAction_collapse_all;
	public static String ExpandAllAction_expand_all;
	public static String ExportImageAction_export_as_image;
	public static String ExportImageAction_bmp_format;
	public static String ExportImageAction_error;
	public static String ExportImageAction_failed_to_export_image;
	public static String ExportImageAction_jpg_format;
	public static String ExportImageAction_png_format;
	public static String OpenMappingAction_canot_find_or_open_mapping_file;
	public static String OpenMappingAction_open_mapping_file;
	public static String OpenSourceAction_canot_find_source_file;
	public static String OpenSourceAction_canot_open_source_file;
	public static String OpenSourceAction_open_source_file;
	public static String ToggleShapeExpandStateAction_toggle_expand_state;
	public static String ToggleShapeVisibleStateAction_toggle_visible_state;
	public static String ToggleConnectionsAction_toggle_connections;
	public static String ShapeSetConstraintCommand_move;
	public static String PartFactory_canot_create_part_for_model_element;
	public static String PartFactory_null;
	public static String CreateGuideCommand_Label;
	public static String DeleteGuideCommand_Label;
	public static String MoveGuideCommand_Label;
	public static String DiagramContentOutlinePage_Outline;
	public static String DiagramContentOutlinePage_Overview;
	public static String DiagramViewer_OutlinePage_Sort_label;
	public static String DiagramViewer_OutlinePage_Sort_tooltip;
	public static String DiagramViewer_OutlinePage_Sort_description;
}
