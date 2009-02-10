/*******************************************************************************
 * Copyright (c) 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *     Dmitry Geraskov, Exadel Inc. - Extracted from Dali 2.0 to protect from changes.
 ******************************************************************************/
package org.jboss.tools.hibernate.jpt.ui.xpl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ListIterator;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jpt.core.context.persistence.MappingFileRef;
import org.eclipse.jpt.core.context.persistence.PersistenceUnit;
import org.eclipse.jpt.core.internal.resource.orm.translators.OrmXmlMapper;
import org.eclipse.jpt.ui.JptUiPlugin;
import org.eclipse.jpt.ui.internal.JptUiIcons;
import org.eclipse.jpt.ui.internal.persistence.JptUiPersistenceMessages;
import org.eclipse.jpt.ui.internal.util.SWTUtil;
import org.eclipse.jpt.ui.internal.widgets.PostExecution;
import org.eclipse.jpt.utility.internal.model.value.ItemPropertyListValueModelAdapter;
import org.eclipse.jpt.utility.internal.model.value.ListAspectAdapter;
import org.eclipse.jpt.utility.internal.model.value.SimplePropertyValueModel;
import org.eclipse.jpt.utility.internal.model.value.swing.ObjectListSelectionModel;
import org.eclipse.jpt.utility.model.value.ListValueModel;
import org.eclipse.jpt.utility.model.value.WritablePropertyValueModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.navigator.ResourceComparator;
import org.jboss.tools.hibernate.jpt.ui.HibernateJptUIPlugin;
import org.jboss.tools.hibernate.jpt.ui.xpl.AddRemovePane.Adapter;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Here the layout of this pane:
 * <pre>
 * -----------------------------------------------------------------------------
 * | ------------------------------------------------------------------------- |
 * | |                                                                       | |
 * | | AddRemoveListPane                                                     | |
 * | |                                                                       | |
 * | ------------------------------------------------------------------------- |
 * -----------------------------------------------------------------------------</pre>
 *
 * @see PersistenceUnit
 * @see PersistenceUnitGeneralComposite - The parent container
 * @see AddRemoveListPane
 *
 * @version 2.0
 * @since 2.0
 */
@SuppressWarnings("nls")
public class PersistenceUnitMappingFilesComposite extends AbstractPane<PersistenceUnit>
{
	/**
	 * Creates a new <code>PersistenceUnitMappingFilesComposite</code>.
	 *
	 * @param parentPane The parent pane of this one
	 * @param parent The parent container
	 */
	public PersistenceUnitMappingFilesComposite(AbstractPane<? extends PersistenceUnit> parentPane,
	                                            Composite parent) {

		super(parentPane, parent);
	}

	/**
	 * Prompts a dialog showing a tree structure of the source paths where the
	 * only files shown are JPA mapping descriptors file. The XML file has to be
	 * an XML file with the root tag: &lt;entity-mappings&gt;.
	 *
	 * @param listSelectionModel The selection model used to select the new files
	 */
	private void addJPAMappingDescriptor(ObjectListSelectionModel listSelectionModel) {

		IProject project = subject().getJpaProject().getProject();

		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
			shell(),
			new WorkbenchLabelProvider(),
			new WorkbenchContentProvider()
		);

		dialog.setHelpAvailable(false);
		dialog.setValidator(buildValidator());
		dialog.setTitle(JptUiPersistenceMessages.PersistenceUnitMappingFilesComposite_mappingFileDialog_title);
		dialog.setMessage(JptUiPersistenceMessages.PersistenceUnitMappingFilesComposite_mappingFileDialog_message);
		dialog.addFilter(new XmlFileViewerFilter(subject().getJpaProject().getJavaProject()));
		dialog.setInput(project);
		dialog.setComparator(new ResourceComparator(ResourceComparator.NAME));

		SWTUtil.show(
			dialog,
			buildSelectionDialogPostExecution(listSelectionModel)
		);
	}

	private Adapter buildAdapter() {
		return new AddRemoveListPane.AbstractAdapter() {
			public void addNewItem(ObjectListSelectionModel listSelectionModel) {
				addJPAMappingDescriptor(listSelectionModel);
			}

			public void removeSelectedItems(ObjectListSelectionModel listSelectionModel) {
				for (Object item : listSelectionModel.selectedValues()) {
					subject().removeSpecifiedMappingFileRef((MappingFileRef) item);
				}
			}
		};
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	protected Composite buildContainer(Composite parent) {

		GridLayout layout = new GridLayout(1, true);
		layout.marginHeight = 0;
		layout.marginWidth  = 0;
		layout.marginTop    = 0;
		layout.marginLeft   = 0;
		layout.marginBottom = 0;
		layout.marginRight  = 0;

		Composite container = buildPane(parent, layout);
		updateGridData(container);

		return container;
	}

	private ListValueModel<MappingFileRef> buildItemListHolder() {
		return new ItemPropertyListValueModelAdapter<MappingFileRef>(
			buildListHolder(),
			MappingFileRef.FILE_NAME_PROPERTY
		);
	}

	private ILabelProvider buildLabelProvider() {
		return new LabelProvider() {
			@Override
			public Image getImage(Object element) {
				return JptUiPlugin.getImage(JptUiIcons.MAPPING_FILE_REF);
			}

			@Override
			public String getText(Object element) {
				MappingFileRef mappingFileRef = (MappingFileRef) element;
				String name = mappingFileRef.getFileName();

				if (name == null) {
					name = JptUiPersistenceMessages.PersistenceUnitMappingFilesComposite_ormNoName;
				}

				return name;
			}
		};
	}

	private ListValueModel<MappingFileRef> buildListHolder() {
		return new ListAspectAdapter<PersistenceUnit, MappingFileRef>(getSubjectHolder(), "specifiedMappingFileRefs") {
			@Override
			protected ListIterator<MappingFileRef> listIterator_() {
				return subject.specifiedMappingFileRefs();
			}

			@Override
			protected int size_() {
				return subject.specifiedMappingFileRefsSize();
			}
		};
	}

	private WritablePropertyValueModel<MappingFileRef> buildSelectedItemHolder() {
		return new SimplePropertyValueModel<MappingFileRef>();
	}

	private PostExecution<ElementTreeSelectionDialog> buildSelectionDialogPostExecution(final ObjectListSelectionModel listSelectionModel) {
		return new PostExecution<ElementTreeSelectionDialog>() {
			public void execute(ElementTreeSelectionDialog dialog) {

				if (dialog.getReturnCode() == IDialogConstants.CANCEL_ID) {
					return;
				}

				int index = subject().specifiedMappingFileRefsSize();

				for (Object result : dialog.getResult()) {
					IFile file = (IFile) result;
					IPath filePath = removeSourcePath(file);

					MappingFileRef mappingFileRef = subject().addSpecifiedMappingFileRef(index++);
					mappingFileRef.setFileName(filePath.toPortableString());

					listSelectionModel.addSelectedValue(mappingFileRef);
				}
			}
		};
	}

	private ISelectionStatusValidator buildValidator() {
		return new ISelectionStatusValidator() {
			public IStatus validate(Object[] selection) {

				if (selection.length == 0) {
					return new Status(IStatus.ERROR, JptUiPlugin.PLUGIN_ID, "");
				}

				for (Object item : selection) {
					if (item instanceof IFolder) {
						return new Status(IStatus.ERROR, JptUiPlugin.PLUGIN_ID, "");
					}
				}

				return new Status(IStatus.OK, JptUiPlugin.PLUGIN_ID, "");
			}
		};
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	protected void initializeLayout(Composite container) {

		// List pane
		new AddRemoveListPane<PersistenceUnit>(
			this,
			container,
			buildAdapter(),
			buildItemListHolder(),
			buildSelectedItemHolder(),
			buildLabelProvider()
		) {
			@Override
			protected Composite buildContainer(Composite parent) {
				parent = super.buildContainer(parent);
				updateGridData(parent);
				return parent;
			}

			@Override
			protected void initializeLayout(Composite container) {
				super.initializeLayout(container);
				updateGridData(getContainer());
			}
		};
	}

	/**
	 * Returns the path of the given file excluding the source folder.
	 *
	 * @param file The file to retrieve its path minus the source folder
	 * @return The relative path of the given path, the path is relative to the
	 * source path
	 */
	private IPath removeSourcePath(IFile file) {
		IJavaProject javaProject = subject().getJpaProject().getJavaProject();
		IPath filePath = file.getProjectRelativePath();

		try {
			for (IClasspathEntry entry : javaProject.getRawClasspath()) {

				// Only check for source paths
				if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {

					// Retrieve the source path relative to the project
					IPath sourcePath = entry.getPath().removeFirstSegments(1);

					// Check to see if the file path starts with the source path
					if (sourcePath.isPrefixOf(filePath)) {
						int count = sourcePath.segmentCount();
						filePath = filePath.removeFirstSegments(count);
						break;
					}
				}
			}
		}
		catch (JavaModelException e) {
			JptUiPlugin.log(e);
		}

		return filePath;
	}

	private void updateGridData(Composite container) {

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace   = true;
		gridData.horizontalAlignment       = SWT.FILL;
		gridData.verticalAlignment         = SWT.FILL;
		container.setLayoutData(gridData);
	}

	//TODO might we want to do this with content-types instead?  is there
	//the potential that an extender could hae a mapping file that doesn't have
	//entity-mappings as the root node??
	/**
	 * This handler is responsible to parse the root tag (local name) only.
	 */
	private static class SAXHandler extends DefaultHandler {

		private String rootTagName;

		public String getRootTagName() {
			return rootTagName;
		}

		@Override
		public InputSource resolveEntity(String publicId,
		                                 String systemId) throws IOException, SAXException {

			InputSource inputSource = new InputSource();
			inputSource.setByteStream(new ByteArrayInputStream(new byte[0]));
			return inputSource;
		}

		@Override
		public void startElement(String uri,
		                         String localName,
		                         String name,
		                         Attributes attributes) throws SAXException {

			this.rootTagName = name;
			throw new SAXException();
		}
	}

	/**
	 * This filter will deny showing any file that are not XML files or folders
	 * that don't contain any XML files in its sub-hierarchy. The XML files are
	 * partially parsed to only accept JPA mapping descriptors.
	 */
	private static class XmlFileViewerFilter extends ViewerFilter {

		private final IJavaProject javaProject;

		XmlFileViewerFilter(IJavaProject javaProject) {
			super();
			this.javaProject = javaProject;
		}

		/**
		 * Determines whether the given file (an XML file) is a JPA mapping
		 * descriptor file. It has to be a valid XML file with a root element
		 * named "entity-mappings".
		 *
		 * @param file The file to parse and see if it's a mapping descriptor file
		 * @return <code>true</code> if the given file is a valid XML file with a
		 * root element named "entity-mappings"; <code>false</code> in any other
		 * case
		 */
		private boolean isMappingFile(IFile file) {
			try {
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser saxParser = factory.newSAXParser();
				SAXHandler handler = new SAXHandler();
				try {
					saxParser.parse(file.getRawLocationURI().toURL().openStream(), handler);
				}
				catch (Exception e) {
					// Ignore since it's caused by SAXHandler to stop the parsing
					// the moment the local name is retrieved
				}
				return OrmXmlMapper.ENTITY_MAPPINGS.equalsIgnoreCase(handler.getRootTagName());
			}
			catch (Exception e) {
				JptUiPlugin.log(e);
				return false;
			}
		}

		private boolean isXmlFile(IFile file) {
			return "xml".equalsIgnoreCase(file.getFileExtension());
		}

		@Override
		public boolean select(Viewer viewer,
		                      Object parentElement,
		                      Object element) {

			if (element instanceof IFile) {
				IFile file = (IFile) element;
				return isXmlFile(file) && isMappingFile(file);
			}
			else if (element instanceof IFolder) {
				IFolder folder = (IFolder) element;

				try {
					for (IClasspathEntry entry : javaProject.getRawClasspath()) {
						if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
							if (!entry.getPath().isPrefixOf(folder.getFullPath().makeRelative()))
								return false;
						}
					}

					for (IResource resource : folder.members()) {
						if (select(viewer, folder, resource)) {
							return true;
						}
					}
				}
				catch (JavaModelException e) {
					JptUiPlugin.log(e.getStatus());
				}
				catch (CoreException e) {
					JptUiPlugin.log(e.getStatus());
				}
			}

			return false;
		}
	}
}