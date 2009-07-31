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
package org.jboss.tools.hibernate.jpt.core.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.jpt.core.JpaAnnotationProvider;
import org.eclipse.jpt.core.JpaFile;
import org.eclipse.jpt.core.JpaPlatform;
import org.eclipse.jpt.core.JpaProject;
import org.eclipse.jpt.core.JptCorePlugin;
import org.eclipse.jpt.core.context.persistence.Persistence;
import org.eclipse.jpt.core.context.persistence.PersistenceUnit;
import org.eclipse.jpt.core.internal.platform.GenericJpaAnnotationProvider;
import org.eclipse.jpt.core.internal.utility.jdt.JDTFieldAttribute;
import org.eclipse.jpt.core.resource.java.JavaResourceCompilationUnit;
import org.eclipse.jpt.core.resource.java.JavaResourcePersistentAttribute;
import org.eclipse.jpt.core.resource.java.JavaResourcePersistentType;
import org.eclipse.jpt.core.resource.persistence.PersistenceFactory;
import org.eclipse.jpt.core.resource.persistence.PersistencePackage;
import org.eclipse.jpt.core.resource.persistence.XmlJarFileRef;
import org.eclipse.jpt.core.resource.persistence.XmlJavaClassRef;
import org.eclipse.jpt.core.resource.persistence.XmlMappingFileRef;
import org.eclipse.jpt.core.resource.persistence.XmlPersistenceUnit;
import org.eclipse.jpt.core.resource.persistence.XmlPersistenceUnitTransactionType;
import org.eclipse.jpt.utility.CommandExecutor;
import org.eclipse.jpt.utility.CommandExecutor.Default;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.events.IProjectFacetActionEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent.Type;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaPlatform;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaPlatformFactory;
import org.jboss.tools.hibernate.jpt.core.internal.JPAPostInstallFasetListener;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;
import org.jboss.tools.hibernate.jpt.core.internal.resource.java.GenericGeneratorAnnotationImpl;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

/**
 * JUnit plugin test class for core Hibernate JPA platform
 * (org.jboss.tools.hibernate.jpt.core)
 * 
 * @author Vitali Yemialyanchyk
 */
public class HibernateJPACoreTests extends TestCase {

	/**
	 * annotated class name
	 */
	public static final String className = "TestPTR"; //$NON-NLS-1$
	/**
	 * annotated package name
	 */
	public static final String packageName = "org.test"; //$NON-NLS-1$
	/**
	 * fully qualified name of annotated class
	 */
	public static final String classFullName = packageName + "." + className; //$NON-NLS-1$
	/**
	 * annotated java file name
	 */
	public static final String javaFileName = className + ".java"; //$NON-NLS-1$
	/**
	 * content of annotated java file
	 */
	public static final String strJava = "package " + packageName + ";\n" + //$NON-NLS-1$ //$NON-NLS-2$
			"import javax.persistence.*;\n" + //$NON-NLS-1$
			"import org.hibernate.annotations.GenericGenerator;\n" + //$NON-NLS-1$
			"@Entity\n" + //$NON-NLS-1$
			"@NamedQueries( { @NamedQuery(name = \"arName\", query = \"From " + //$NON-NLS-1$
			className + " \") })\n" + //$NON-NLS-1$
			"public class " + className + " {\n" + //$NON-NLS-1$ //$NON-NLS-2$
			"@Id\n" + //$NON-NLS-1$
			"@GeneratedValue(generator=\"wrongGenerator\")\n" + //$NON-NLS-1$
			"private Short id_Article;\n" + //$NON-NLS-1$
			"@GenericGenerator(name=\"rightGenerator\", strategy=\"hilo\")\n" + //$NON-NLS-1$
			"@GeneratedValue(generator=\"rightGenerator\")\n" + //$NON-NLS-1$
			"private Short id_Article2;\n" + //$NON-NLS-1$
			"public Short getId_Article(){return id_Article;}\n" + //$NON-NLS-1$
			"public Short getId_Article2(){return id_Article2;}\n" + //$NON-NLS-1$
			"}\n"; //$NON-NLS-1$
	/**
	 * content of .classpath file
	 */
	public static final String strClassPath = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + //$NON-NLS-1$
			"<classpath>\n" + //$NON-NLS-1$
			"<classpathentry kind=\"src\" path=\"src\"/>\n" + //$NON-NLS-1$
			"<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER\"/>\n" + //$NON-NLS-1$
			"<classpathentry kind=\"con\" path=\"org.eclipse.pde.core.requiredPlugins\"/>\n" + //$NON-NLS-1$
			"<classpathentry kind=\"output\" path=\"bin\"/>\n" + //$NON-NLS-1$
			"</classpath>\n"; //$NON-NLS-1$

	
	public Mockery context = new Mockery() {
		{
			setImposteriser(ClassImposteriser.INSTANCE);
		}
	};

	/**
	 * mock input stream to simulate javaFileName file reading
	 */
	public static class MockJavaInputStream extends InputStream {

		protected int pointer = 0;

		@Override
		public int read() throws IOException {
			if (pointer < strJava.getBytes().length) {
				return strJava.getBytes()[pointer++];
			}
			return -1;
		}
	}

	/**
	 * mock input stream to simulate .classpath file reading
	 */
	public static class MockClassPathInputStream extends InputStream {

		protected int pointer = 0;

		@Override
		public int read() throws IOException {
			if (pointer < strClassPath.getBytes().length) {
				return strClassPath.getBytes()[pointer++];
			}
			return -1;
		}
	}

	/**
	 * The general goal of this test is cover org.jboss.tools.hibernate.jpt.core
	 * plugin functionality, it doesn't test org.eclipse.jdt functionality,
	 * so here all org.eclipse.jdt internal objects substituted with mock values. 
	 * 
	 * @throws CoreException
	 * @throws IOException
	 */
	public void testMockJPTCore() throws CoreException, IOException {

		// define/prepare mock objects for testing
		final HibernateJpaPlatformFactory hibernateJpaPlatformFactory = new HibernateJpaPlatformFactory();
		final JpaPlatform jpaPlatform = hibernateJpaPlatformFactory.buildJpaPlatform("hibernate"); //$NON-NLS-1$
		final String hibernatePlatformId = jpaPlatform.getId();
		assertTrue(HibernateJpaPlatform.ID.equals(hibernatePlatformId));
		//
		final JpaProject jpaProject = context.mock(JpaProject.class);
		final IProject project = context.mock(IProject.class);
		final IFile file = context.mock(IFile.class);
		final Persistence persistence = context.mock(Persistence.class);
		final XmlPersistenceUnit xmlPersistenceUnit = context
				.mock(XmlPersistenceUnit.class);
		final InternalEObject owner = xmlPersistenceUnit;
		// setup expectations to mock model notifications during
		// EList<XmlJavaClassRef> classes - value insertion 
		context.checking(new Expectations() {
			{
				oneOf(owner).eNotificationRequired();
				will(returnValue(false));

				oneOf(owner).eInternalResource();
				will(returnValue(null));
			}
		});
		final String testClassName = classFullName;
		final EList<XmlJavaClassRef> classes = new EObjectContainmentEList<XmlJavaClassRef>(
				XmlJavaClassRef.class, owner,
				PersistencePackage.XML_PERSISTENCE_UNIT__CLASSES);
		final XmlJavaClassRef xmlJavaClassRef1 = PersistenceFactory.eINSTANCE
				.createXmlJavaClassRef();
		xmlJavaClassRef1.setJavaClass(testClassName);
		// insert testClass into classes list
		classes.add(xmlJavaClassRef1);
		final EList<XmlMappingFileRef> mappingFiles = new EObjectContainmentEList<XmlMappingFileRef>(
				XmlMappingFileRef.class, xmlPersistenceUnit,
				PersistencePackage.XML_PERSISTENCE_UNIT__MAPPING_FILES);
		//
		final EList<XmlJarFileRef> jarFiles = new EObjectContainmentEList<XmlJarFileRef>(
				XmlJarFileRef.class, xmlPersistenceUnit, 
				PersistencePackage.XML_PERSISTENCE_UNIT__JAR_FILES);
		//
		final JavaResourcePersistentType javaResourcePersistentType = context
				.mock(JavaResourcePersistentType.class);
		//
		final List<JavaResourcePersistentAttribute> resourceAttributesList2 = new ArrayList<JavaResourcePersistentAttribute>();
		final JDTFieldAttribute jdtFieldAttribute = new JDTFieldAttribute(null,
				"", 1, null, null); //$NON-NLS-1$
		//final JavaResourcePersistentAttribute jrpa1 = new JavaResourcePersistentAttributeImpl(
		//		javaResourcePersistentType, jdtFieldAttribute);
		//resourceAttributesList2.add(jrpa1);
		//
		final GenericGeneratorAnnotationImpl genericGeneratorAnnotation = new GenericGeneratorAnnotationImpl(
				javaResourcePersistentType, null, null, null);
		//
		final InputStream classPathIStream = new MockClassPathInputStream();
		final InputStream javaIStream = new MockJavaInputStream();
		//
		final IPath pathProject = new Path(""); //$NON-NLS-1$
		//
		final IPath pathJavaFile = new Path(javaFileName);
		//
		final CommandExecutor commandExecutor = Default.INSTANCE;
		// define/check jpaPlatform.buildJpaFile expectations
		context.checking(new Expectations() {
			{

				allowing(jpaProject).getJpaPlatform();
				will(returnValue(jpaPlatform));

				oneOf(file).getProject();
				will(returnValue(project));

				allowing(project).getType();
				will(returnValue(IResource.PROJECT));

				oneOf(file).getFullPath();
				will(returnValue(pathProject));

				allowing(project).hasNature("org.eclipse.jdt.core.javanature"); //$NON-NLS-1$
				will(returnValue(true));

				oneOf(project).getFile(with(".classpath")); //$NON-NLS-1$
				will(returnValue(file));

				oneOf(file).exists();
				will(returnValue(true));

				oneOf(file).getContents(true);
				will(returnValue(classPathIStream));

				allowing(project).getFullPath();
				will(returnValue(pathProject));

				allowing(project).getName();
				will(returnValue("IProj")); //$NON-NLS-1$

				allowing(jpaProject).getModifySharedDocumentCommandExecutor();
				will(returnValue(commandExecutor));

				oneOf(file).getParent();
				will(returnValue(project));

				allowing(file).getName();
				will(returnValue(javaFileName));

				oneOf(project).getWorkingLocation("org.eclipse.jdt.core"); //$NON-NLS-1$
				will(returnValue(null));

				oneOf(project).getFile(new Path(javaFileName));
				will(returnValue(file));

				oneOf(file).getCharset();
				will(returnValue(null));

				oneOf(file).getLocation();
				will(returnValue(pathJavaFile));

				oneOf(file).getContents();
				will(returnValue(javaIStream));

				oneOf(file).getContents(true);
				will(returnValue(javaIStream));

				oneOf(project).getDefaultCharset();
				will(returnValue(null));
			}
		});
		// setup JptCorePlugin preferences:
		// a) setup hibernate as default jpa platform 
		JptCorePlugin.setDefaultJpaPlatformId(hibernatePlatformId);
		// b) setup hibernate as jpa platform for project
		JptCorePlugin.setJpaPlatformId(project, hibernatePlatformId);
		// FIRST TEST:
		// try to build jpa file using hibernate jpa platform
		final JpaFile jpaFile = jpaPlatform.buildJpaFile(jpaProject, file);
		//
		// define/prepare mock objects for further testing
		final JavaResourceCompilationUnit javaResourceCompilationUnit = context
			.mock(JavaResourceCompilationUnit.class);
		final JpaAnnotationProvider jpaAnnotationProvider = jpaPlatform.getAnnotationProvider();
			new GenericJpaAnnotationProvider();
		//
		final IProjectFacetActionEvent projectFacetActionEvent = context
				.mock(IProjectFacetActionEvent.class);
		final IFacetedProject facetedProject = context
				.mock(IFacetedProject.class);
		final IProjectFacet projectFacet = context.mock(IProjectFacet.class);
		// define/check jpaPlatform.getJpaFactory().buildPersistenceUnit,
		// pu.update and jpaPostInstallFasetListener.handleEvent expectations
		context.checking(new Expectations() {
			{
				allowing(xmlPersistenceUnit).getName();
				will(returnValue(className));

				oneOf(xmlPersistenceUnit).setName(className);

				allowing(xmlPersistenceUnit).getClasses();
				will(returnValue(classes));

				allowing(persistence).getJpaProject();
				will(returnValue(jpaProject));

				allowing(jpaProject).getJavaResourcePersistentType(classFullName);
				will(returnValue(javaResourcePersistentType));

				allowing(jpaProject).getJavaResourcePersistentType(null);
				will(returnValue(null));

				allowing(javaResourcePersistentType).getSupportingAnnotation(
					Hibernate.GENERIC_GENERATOR);
				will(returnValue(genericGeneratorAnnotation));

				allowing(javaResourcePersistentType)
						.getSuperclassQualifiedName();
				will(returnValue(null));

				allowing(javaResourcePersistentType).getAccess();
				will(returnValue(org.eclipse.jpt.core.resource.java.AccessType.PROPERTY));

				allowing(javaResourcePersistentType).getQualifiedName();
				will(returnValue(classFullName));

				allowing(javaResourcePersistentType).getMappingAnnotation();
				will(returnValue(null));

				allowing(javaResourcePersistentType).persistableProperties();
				will(returnValue(resourceAttributesList2.iterator()));

				allowing(xmlPersistenceUnit).getMappingFiles();
				will(returnValue(mappingFiles));

				allowing(jpaProject).getDefaultOrmXmlResource();
				will(returnValue(null));

				allowing(xmlPersistenceUnit).getProperties();
				will(returnValue(null));

				allowing(xmlPersistenceUnit).getJarFiles();
				will(returnValue(jarFiles));

				allowing(xmlPersistenceUnit).getTransactionType();
				will(returnValue(XmlPersistenceUnitTransactionType.JTA));

				allowing(xmlPersistenceUnit).getDescription();
				will(returnValue("description")); //$NON-NLS-1$

				allowing(xmlPersistenceUnit).getProvider();
				will(returnValue("provider")); //$NON-NLS-1$

				allowing(xmlPersistenceUnit).getJtaDataSource();
				will(returnValue("jtaDataSource")); //$NON-NLS-1$

				allowing(xmlPersistenceUnit).getNonJtaDataSource();
				will(returnValue("nonJtaDataSource")); //$NON-NLS-1$

				allowing(xmlPersistenceUnit).getExcludeUnlistedClasses();
				will(returnValue(true));

				oneOf(xmlPersistenceUnit).setTransactionType(
						XmlPersistenceUnitTransactionType.JTA);

				oneOf(xmlPersistenceUnit).setDescription("description"); //$NON-NLS-1$

				oneOf(xmlPersistenceUnit).setProvider("provider"); //$NON-NLS-1$

				oneOf(xmlPersistenceUnit).setJtaDataSource("jtaDataSource"); //$NON-NLS-1$

				oneOf(xmlPersistenceUnit).setNonJtaDataSource(
						"nonJtaDataSource"); //$NON-NLS-1$


				oneOf(javaResourcePersistentType).getFile();
				will(returnValue(file));

				oneOf(jpaProject).getJpaFile(file);
				will(returnValue(jpaFile));

				oneOf(xmlPersistenceUnit).setExcludeUnlistedClasses(true);

				allowing(jpaProject).getDefaultSchema();
				will(returnValue("schemaName")); //$NON-NLS-1$

				allowing(jpaProject).getDefaultCatalog();
				will(returnValue("catalogName")); //$NON-NLS-1$

				oneOf(projectFacetActionEvent).getType();
				will(returnValue(Type.POST_INSTALL));

				oneOf(projectFacetActionEvent).getProject();
				will(returnValue(facetedProject));

				oneOf(facetedProject).getProject();
				will(returnValue(project));

				oneOf(projectFacetActionEvent).getProjectFacet();
				will(returnValue(projectFacet));

				oneOf(projectFacet).getId();
				will(returnValue(JptCorePlugin.FACET_ID));
			}
		});
		// SECOND TEST:
		// build pu via hibernate jpa factory
		PersistenceUnit pu = jpaPlatform.getJpaFactory().buildPersistenceUnit(
				persistence, xmlPersistenceUnit);
		// THIRD TEST:
		// update persistence unit and check number of class refs
		int classRefSizeOld = pu.classRefsSize();
		pu.update(xmlPersistenceUnit);
		int classRefSizeNew = pu.classRefsSize();
		// check is the old number of ref classes remain the same after
		// pu.update call - should be the same
		assertTrue(classRefSizeOld == classRefSizeNew);
		// FOURTH TEST:
		// check for handleEvent of JPAPostInstallFasetListener
		JPAPostInstallFasetListener jpaPostInstallFasetListener = new JPAPostInstallFasetListener();
		jpaPostInstallFasetListener.handleEvent(projectFacetActionEvent);
		// GENERAL TEST:
		// check for all expectations
		context.assertIsSatisfied();
	}

}
