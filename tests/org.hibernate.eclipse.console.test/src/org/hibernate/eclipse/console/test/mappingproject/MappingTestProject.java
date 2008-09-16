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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osgi.util.NLS;
import org.hibernate.eclipse.console.test.ConsoleTestMessages;
import org.hibernate.eclipse.console.test.HibernateConsoleTestPlugin;


/**
 * @author Dmitry Geraskov
 *
 */
public class MappingTestProject{

	private static MappingTestProject singleton= null;

	public static String PROJECT_NAME = "MappingTestProject"; //$NON-NLS-1$
	public static String RESOURCE_PATH = "res/project/"; //$NON-NLS-1$

	private IProject project;
	private IJavaProject javaProject;

	public static MappingTestProject getTestProject(){
		if (singleton == null){
			singleton = new MappingTestProject();
		}
		return singleton;
	}

	private MappingTestProject() {
		initialize();
	}

	private void initialize(){
		try{
			buildBigTestProject();
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}

	public IProject getIProject(){
		return this.project;
	}

	public IJavaProject getIJavaProject(){
		return this.javaProject;
	}

	public void deleteIProject() throws CoreException {
		project.delete(true, true, null);
	}

	private void buildBigTestProject() throws JavaModelException, CoreException, IOException {
		TestUtilsCommon commonUtil = new TestUtilsCommon();
		project = commonUtil.buildNewProject(PROJECT_NAME);
		javaProject = commonUtil.buildJavaProject(project);

		IPath resourcePath = new Path(RESOURCE_PATH);
		File resourceFolder = resourcePath.toFile();
		URL entry = HibernateConsoleTestPlugin.getDefault().getBundle().getEntry(RESOURCE_PATH);
		URL resProject = FileLocator.resolve(entry);
		String tplPrjLcStr= FileLocator.resolve(resProject).getFile();
		resourceFolder = new File(tplPrjLcStr);
		if (!resourceFolder.exists()) {
			String out = NLS.bind(ConsoleTestMessages.MappingTestProject_folder_not_found,
					RESOURCE_PATH);
			throw new RuntimeException(out);
		}

		IPackageFragmentRoot sourceFolder = commonUtil.createSourceFolder(project, javaProject);
		commonUtil.recursiveCopyFiles(resourceFolder, (IFolder) sourceFolder.getResource());
		List<IPath> libs = commonUtil.copyLibs(project, javaProject, resourceFolder);
		commonUtil.generateClassPath(javaProject, libs, sourceFolder);
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());
	}



	/*private boolean removePackage(String name, IProject project,
			IJavaProject javaProject) {
		IFolder folder = project.getFolder("src");
		IPackageFragmentRoot root = javaProject.getPackageFragmentRoot(folder);
		IPackageFragment useless = root.getPackageFragment(name);
		if (useless != null){
			try {
				useless.delete(true, new NullProgressMonitor());
			} catch (JavaModelException e) {
				return false;
			}
			return true;
		}
		return false;
	}

	/*private IPackageFragment buildPackage(String name, IProject project,
			IJavaProject javaProject) throws CoreException {
		IPackageFragmentRoot sourceFolder = buildSourceFolder(project,
				javaProject);
		return sourceFolder.createPackageFragment(name, false, null);
	}

	private IType[] buildTypes(IProject project, IJavaProject javaProject, CompilationPack compPack) throws CoreException
	/*throws CoreException*/ //{

		//create empty ICompilationUnit
		//String cuName = compPack.getPack().getName();
		/*IPackageFragment jPack = buildPackage(compPack.getPack(), project, javaProject);
		ICompilationUnit cu = null;//jPack.createCompilationUnit(cuName,
				//"", false, null);

		List<IType> result = new ArrayList<IType>();

		InputStream is;
		try {
			File[] files = compPack.getFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().endsWith(".java")){
					is = new FileInputStream(files[i]);
					String fileBody = getStringFromStream(is);

					//ASTParser parser = ASTParser.newParser(AST.JLS3);
					//parser.setSource(fileBody.toCharArray());
					//CompilationUnit cu2 = (CompilationUnit) parser.createAST(null);
					String cuName = files[i].getName();

					try {
						cu = jPack.createCompilationUnit(cuName, fileBody, false, null);
						result.addAll(Arrays.asList(cu.getAllTypes()));
					} catch (JavaModelException e) {
						e.printStackTrace();
						System.out.println("Error compiling file " + files[i].getAbsolutePath());
					}
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < result.size(); i++) {
			System.out.println(((IType)result.get(i)).getElementName());
		}
		return (IType[])result.toArray(new IType[0]);
	}

	public static String getStringFromStream (InputStream is)
	throws IOException
	{
		try {
			InputStreamReader reader = new InputStreamReader(is);
			char[] buffer = new char[1024];
			StringWriter writer = new StringWriter();
			int bytes_read;
			while ((bytes_read = reader.read(buffer)) != -1)
			{resourceFolder.exists()
				writer.write(buffer, 0, bytes_read);
			}
			return (writer.toString());
		}
		finally {
			if (null != is) is.close();
		}
	}

	private void createCompilationPacks(File pack, List<CompilationPack> compPacks, String packName){
		if (pack.isDirectory()){
			if (packName.length() != 0)	packName += '.';
			packName += pack.getName();

			File[] files = pack.listFiles(fileFilter);
			if (files.length > 0) compPacks.add(new CompilationPack(packName, files));

			File[] dirs = pack.listFiles(dirFilter);
			for (int i = 0; i < dirs.length; i++) {
				createCompilationPacks(dirs[i], compPacks, packName);
			}
		}
	}

	public class CompilationPack {

		private String pack = null;

		private File[] files = null;

		CompilationPack (String pack, File[] files){
			this.pack = pack;
			this.files = files;
		}

		public String getPack() {
			return pack;
		}

		public File[] getFiles() {
			return files;
		}
	}*/

}
