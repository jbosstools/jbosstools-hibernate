/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.hibernate.eclipse.console.test.mappingproject;

import java.lang.reflect.Field;

import org.apache.tools.ant.filters.StringInputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.internal.ErrorEditorPart;
import org.hibernate.console.preferences.ConsoleConfigurationPreferences;
import org.hibernate.eclipse.console.wizards.ConsoleConfigurationCreationWizard;
import org.hibernate.mapping.PersistentClass;

/**
 * @author Dmitry Geraskov
 *
 */
public class ProjectUtil {
	
	private static final StringBuilder XML_HEADER = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
													.append("<!DOCTYPE hibernate-configuration PUBLIC\n")
													.append("\"-//Hibernate/Hibernate Configuration DTD 3.0//EN\"\n")
													.append("\"http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd\">\n");
	
	private static final StringBuilder XML_CFG_START = new StringBuilder("<hibernate-configuration>\n")
													.append("<session-factory>\n")
													.append("<property name=\"hibernate.dialect\">")
													.append("org.hibernate.dialect.PostgreSQLDialect</property>");
	
	private static final StringBuilder XML_CFG_END = new StringBuilder("</session-factory>\n")
													.append("</hibernate-configuration>\n");	
	
	
	public static final String CFG_FILE_NAME = "hibernate.cfg.xml";
	
	public static final String ConsoleCFGName = "testConfigName";
	
	public static void customizeCFGFileForPack(IPackageFragment pack) throws CoreException{
		IFolder srcFolder = (IFolder) pack.getParent().getResource();
		IFile iFile = srcFolder.getFile(CFG_FILE_NAME);
		if (iFile.exists()) {
			iFile.delete(true, null);
		}
		String file_boby = XML_HEADER.toString() + XML_CFG_START.toString();
		if (pack.getNonJavaResources().length > 0){
			Object[] ress = pack.getNonJavaResources();
			for (int i = 0; i < ress.length; i++) {
				if (ress[i] instanceof IFile){
					IFile res = (IFile)ress[i];
					if (res.getName().endsWith(".hbm.xml")){
						file_boby += "<mapping resource=\"" + pack.getElementName().replace('.', '/') + '/' + res.getName() + "\"/>\n";
					}
				}
			}
		}		
		/*if (pack.getCompilationUnits().length > 0){
			ICompilationUnit[] comps = pack.getCompilationUnits();
			for (int i = 0; i < comps.length; i++) {
				ICompilationUnit compilationUnit = comps[i];
				IType[] types = compilationUnit.getTypes();
				for (int j = 0; j < types.length; j++) {
					IType type = types[j];
					if (type.isAnnotation()){
						System.out.println(type);
					}
				}
			}
		}*/

		file_boby += XML_CFG_END.toString();
		iFile.create(new StringInputStream(file_boby),
				   true, null);
	}
	
	public static String getPersistentClassName(PersistentClass persClass) {
		if (persClass == null) {
			return "";
		} else { 
			return persClass.getEntityName() != null ? persClass.getEntityName() : persClass.getClassName();
		}
	}
	
	public static void createConsoleCFG() throws CoreException{
		new ConsoleConfigurationCreationWizard2().run();
	}
	
	private static class ConsoleConfigurationCreationWizard2 extends ConsoleConfigurationCreationWizard{
		
		public void run() throws CoreException {
			IPath cfgFilePath = new Path(MappingTestProject.PROJECT_NAME + "/" + 
					MappingTestProject.SRC_FOLDER + "/" + ProjectUtil.CFG_FILE_NAME);
			createConsoleConfiguration(null, null, ConsoleCFGName, ConsoleConfigurationPreferences.ConfigurationMode.CORE, 
					MappingTestProject.PROJECT_NAME, true, "",
					null, cfgFilePath, new Path[0], new Path[0], "", "", new NullProgressMonitor());

		}
	}
	/**
	 * Sometimes we have exceptions while opening editors.
	 * IDE catches this exceptions and opens ErrorEditorPart instead of 
	 * our editor. To be sure that editor opened without exception use this method.
	 * It gets occurred exception from the editor if it was and passes it up.
	 *  
	 * @param editor
	 * @throws Throwable
	 */
	public static void throwExceptionIfItOccured(IEditorPart editor) throws Throwable {
		if (editor instanceof ErrorEditorPart){
			Class<ErrorEditorPart> clazz = ErrorEditorPart.class;
			Field field;
			try {
				field = clazz.getDeclaredField("error");
			
				field.setAccessible(true);
		
				Object error = field.get(editor);
				if (error instanceof IStatus) {
					IStatus err_status = (IStatus) error;
					if (err_status.getSeverity() == Status.ERROR){
						throw err_status.getException();
					}
				}
			// catch close means that exception occurred but we can't get it
			} catch (SecurityException e) {
				throw new RuntimeException("Can't get exception from ErrorEditorPart. " + e.getMessage());
			} catch (NoSuchFieldException e) {
				throw new RuntimeException("Can't get error field from ErrorEditorPart. " + e.getMessage());
			} catch (IllegalArgumentException e) {
				throw new RuntimeException("Can't get error field from ErrorEditorPart. " + e.getMessage());
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Can't get error field from ErrorEditorPart. " + e.getMessage());
			}
		}
	}

}
