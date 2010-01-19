/*******************************************************************************
 * Copyright (c) 2007-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.jdt.ui.wizards;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.corext.refactoring.nls.changes.CreateTextFileChange;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.internal.ui.refactoring.PreviewWizardPage;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.console.utils.FileUtils;

/**
 * Preview wizard page for new hibernate mappings.
 *
 * @author Vitali Yemialyanchyk
 */
@SuppressWarnings("restriction")
public class NewHibernateMappingPreviewPage extends PreviewWizardPage {

	public static final String HIBERNATE_NEW_HBM_XML_FOLDER_NAME = "hibernateNewHbmXml"; //$NON-NLS-1$
	
	protected IPath rootPlace2GenBase = null;
	protected IPath rootPlace2Gen = null;
	protected Map<IJavaProject, IPath> places2Gen;
	protected Set<IPath> paths2Disconnect = new HashSet<IPath>();

	public NewHibernateMappingPreviewPage() {
		super(true);
	}
	
	@Override
	public void dispose() {
		performDisconnect();
		IPath place2Gen = getRootPlace2Gen();
		if (place2Gen != null) {
			File folder2Gen = new File(place2Gen.toOSString());
			FileUtils.delete(folder2Gen);
		}
		super.dispose();
	}

	public void setPlaces2Gen(Map<IJavaProject, IPath> places2Gen) {
		this.places2Gen = places2Gen;
		updateChanges();
	}

	public Set<IJavaProject> getJavaProjects() {
		if (places2Gen == null) {
			return new HashSet<IJavaProject>();
		}
		return places2Gen.keySet();
	}

	protected void performDisconnect() {
		final ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
		for (IPath filePathTo_Show : paths2Disconnect) {
			try {
				bufferManager.disconnect(filePathTo_Show, LocationKind.IFILE, null);
			} catch (CoreException e) {
				HibernateConsolePlugin.getDefault().logErrorMessage("CoreException: ", e); //$NON-NLS-1$
			}
		}
		paths2Disconnect.clear();
	}
	
	protected void performCommit() {
		final CompositeChange cc = (CompositeChange)getChange();
		if (cc == null) {
			return;
		}
		final ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
		Change[] changes = cc.getChildren();
		for (int i = 0; i < changes.length; i++) {
			Change change = changes[i];
			if (!(change instanceof TextFileChange)) {
				continue;
			}
			TextFileChange tfc = (TextFileChange)change;
			if (tfc.isEnabled() && tfc.getEdit() != null) {
				IPath path = new Path(tfc.getName());
				ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(path, LocationKind.IFILE);
				IDocument document = textFileBuffer.getDocument();
				try {
					tfc.getEdit().apply(document);
				} catch (MalformedTreeException e) {
					HibernateConsolePlugin.getDefault().logErrorMessage("MalformedTreeException: ", e); //$NON-NLS-1$
				} catch (BadLocationException e) {
					HibernateConsolePlugin.getDefault().logErrorMessage("BadLocationException: ", e); //$NON-NLS-1$
				}
				try {
					// commit changes to underlying file
					textFileBuffer.commit(null, true);
				} catch (CoreException e) {
					HibernateConsolePlugin.getDefault().logErrorMessage("CoreException: ", e); //$NON-NLS-1$
				}
			}
		}
	}
	
	/**
	 * The function reads file content into the string.
	 * @param fileSrc
	 * @return
	 */
	protected String readInto(File fileSrc) {
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		StringBuilder str = new StringBuilder();
		try {
			fis = new FileInputStream(fileSrc);
			bis = new BufferedInputStream(fis);
	        byte[] buff = new byte[1<<14];
			while (true) {
				int n = -1;
				try {
					n = bis.read(buff);
				} catch (IOException e) {
					HibernateConsolePlugin.getDefault().log(e);
				}
				if (n == -1) {
					break;
				}
				str.append(new String(buff, 0, n));
			}
		} catch (FileNotFoundException e) {
			HibernateConsolePlugin.getDefault().log(e);
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {}
			}
		}
		return str.toString();
	}

	public IPath getRootPlace2GenBase() {
		if (rootPlace2GenBase != null) {
			return rootPlace2GenBase;
		}
		String systemTmpDir = System.getProperty("java.io.tmpdir"); //$NON-NLS-1$
		rootPlace2GenBase = new Path(systemTmpDir);
		rootPlace2GenBase = rootPlace2GenBase.append(HIBERNATE_NEW_HBM_XML_FOLDER_NAME);
		return rootPlace2GenBase;
	}

	public IPath getRootPlace2Gen() {
		if (rootPlace2Gen != null) {
			return rootPlace2Gen;
		}
		rootPlace2Gen = getRootPlace2GenBase();
		String uuidName = UUID.randomUUID().toString();
		rootPlace2Gen = rootPlace2Gen.append(uuidName);
		return rootPlace2Gen;
	}
	
	/**
	 * Try to create one change according with input file (fileSrc).
	 * In case of success change be added into cc and returns true.
	 * @param cc
	 * @param proj
	 * @param fileSrc
	 * @return
	 */
	protected boolean updateOneChange(final CompositeChange cc, final IJavaProject proj, File fileSrc) {
		if (!fileSrc.exists()) {
			return false;
		}
		if (fileSrc.isDirectory()) {
			return false;
		}
		final IPath basePath = proj.getResource().getParent().getLocation();
		final IPath projPath = proj.getResource().getLocation();
		final IPath place2Gen = getRootPlace2Gen().append(proj.getElementName());
		IPath filePathFrom = new Path(fileSrc.getPath());
		IPath filePathTo = filePathFrom.makeRelativeTo(place2Gen);
		filePathTo = projPath.append(filePathTo);
		final IPath filePathTo_Show = filePathTo.makeRelativeTo(basePath);
		File fileOrig = filePathTo.toFile();
		if (fileOrig.exists()) {
			final IPath filePathTo_Proj = filePathTo.makeRelativeTo(projPath);
			class ResHolder {
				public IResource res2Update = null;
			}
			final ResHolder res2UpdateHolder = new ResHolder();
			IResourceVisitor visitor = new IResourceVisitor() {

				public boolean visit(IResource resource) throws CoreException {
					if (resource.getProjectRelativePath().equals(filePathTo_Proj)) {
						res2UpdateHolder.res2Update = resource;
						return false;
					}
					if (resource.getProjectRelativePath().isPrefixOf(filePathTo_Proj)) {
						return true;
					}
					return false;
				}
				
			};
			try {
				proj.getResource().accept(visitor);
			} catch (CoreException e1) {
				//ignore
			}
			if (res2UpdateHolder.res2Update != null) {
				final ITextFileBufferManager bufferManager = FileBuffers.getTextFileBufferManager();
				ITextFileBuffer textFileBuffer = bufferManager.getTextFileBuffer(filePathTo_Show, LocationKind.IFILE);
				if (textFileBuffer == null) {
					try {
						bufferManager.connect(filePathTo_Show, LocationKind.IFILE, null);
						paths2Disconnect.add(filePathTo_Show);
					} catch (CoreException e) {
						HibernateConsolePlugin.getDefault().logErrorMessage("CoreException: ", e); //$NON-NLS-1$
					}
					textFileBuffer = bufferManager.getTextFileBuffer(filePathTo_Show, LocationKind.IFILE);
				}
				if (textFileBuffer != null) {
					IDocument documentChange = textFileBuffer.getDocument();
					//
					String str = readInto(fileSrc);
					TextEdit textEdit = new ReplaceEdit(0, documentChange.getLength(), str.toString());
					//
					TextFileChange change = new TextFileChange(filePathTo_Show.toString(), 
							(IFile)res2UpdateHolder.res2Update);
					change.setSaveMode(TextFileChange.LEAVE_DIRTY);
					change.setEdit(textEdit);
					cc.add(change);
				}
			}
		} else {
			String str = readInto(fileSrc);
			CreateTextFileChange change = new CreateTextFileChange(filePathTo_Show, str.toString(), null, "hbm.xml"); //$NON-NLS-1$
			cc.add(change);
		}
		return true;
	}
	
	/**
	 * Try to create changes according with all files in the input directory (dir).
	 * Changes be added into cc.
	 * @param cc
	 * @param proj
	 * @param dir
	 */
	protected void updateChanges(final CompositeChange cc, final IJavaProject proj, File dir) {
		if (!dir.exists()) {
			return;
		}
		if (!dir.isDirectory()) {
			updateOneChange(cc, proj, dir);
			return;
		}
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				updateChanges(cc, proj, files[i]);
			} else {
				updateOneChange(cc, proj, files[i]);
			}
		}
	}
	
	protected void updateChanges() {
		performDisconnect();
		final CompositeChange cc = new CompositeChange(""); //$NON-NLS-1$
		for (Entry<IJavaProject, IPath> entry : places2Gen.entrySet()) {
			updateChanges(cc, entry.getKey(), entry.getValue().toFile());
		}
		cc.markAsSynthetic();
		setChange(cc);
	}
	
	/**
	 * Apply changes.
	 */
	@Override
	public boolean performFinish() {
		if (getChange() == null) {
			return false;
		}
		performCommit();
		try {
			getChange().perform(new NullProgressMonitor());
		} catch (CoreException e) {
			HibernateConsolePlugin.getDefault().logErrorMessage("CoreException: ", e); //$NON-NLS-1$
		}
		performDisconnect();
		return true;
	}
}
