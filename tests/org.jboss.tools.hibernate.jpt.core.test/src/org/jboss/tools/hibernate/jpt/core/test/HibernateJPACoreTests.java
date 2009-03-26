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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jpt.core.JpaAnnotationProvider;
import org.eclipse.jpt.core.JpaFactory;
import org.eclipse.jpt.core.JpaFile;
import org.eclipse.jpt.core.JpaPlatform;
import org.eclipse.jpt.core.JpaProject;
import org.eclipse.jpt.core.JptCorePlugin;
import org.eclipse.jpt.core.ResourceModel;
import org.eclipse.jpt.core.context.persistence.Persistence;
import org.eclipse.jpt.core.context.persistence.PersistenceUnit;
import org.eclipse.jpt.core.internal.resource.java.JavaResourcePersistentAttributeImpl;
import org.eclipse.jpt.core.internal.utility.jdt.JDTFieldAttribute;
import org.eclipse.jpt.core.resource.java.JavaResourceModel;
import org.eclipse.jpt.core.resource.java.JavaResourcePersistentAttribute;
import org.eclipse.jpt.core.resource.java.JavaResourcePersistentType;
import org.eclipse.jpt.core.resource.java.JpaCompilationUnit;
import org.eclipse.jpt.core.resource.orm.OrmArtifactEdit;
import org.eclipse.jpt.core.resource.persistence.PersistenceFactory;
import org.eclipse.jpt.core.resource.persistence.PersistencePackage;
import org.eclipse.jpt.core.resource.persistence.XmlJavaClassRef;
import org.eclipse.jpt.core.resource.persistence.XmlMappingFileRef;
import org.eclipse.jpt.core.resource.persistence.XmlPersistenceUnit;
import org.eclipse.jpt.core.resource.persistence.XmlPersistenceUnitTransactionType;
import org.eclipse.jpt.db.Catalog;
import org.eclipse.jpt.db.ConnectionProfile;
import org.eclipse.jpt.db.Schema;
import org.eclipse.wst.common.componentcore.ModuleCoreNature;
import org.eclipse.wst.common.componentcore.internal.ArtifactEditModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.events.IProjectFacetActionEvent;
import org.eclipse.wst.common.project.facet.core.events.IFacetedProjectEvent.Type;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateFactory;
import org.jboss.tools.hibernate.jpt.core.internal.HibernateJpaAnnotationProvider;
import org.jboss.tools.hibernate.jpt.core.internal.HibernatePlatform;
import org.jboss.tools.hibernate.jpt.core.internal.JPAPostInstallFasetListener;
import org.jboss.tools.hibernate.jpt.core.internal.context.basic.Hibernate;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.GenericGeneratorAnnotationImpl;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

import junit.framework.TestCase;

/**
 * JUnit plugin test class for core Hibernate JPA platform
 * (org.jboss.tools.hibernate.jpt.core)
 * 
 * @author Vitali Yemialyanchyk
 */
public class HibernateJPACoreTests extends TestCase {

	public static final String className = "TestPTR"; //$NON-NLS-1$
	public static final String packageName = "org.test"; //$NON-NLS-1$
	public static final String classFullName = packageName + "." + className; //$NON-NLS-1$
	public static final String javaFileName = className + ".java"; //$NON-NLS-1$

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

	public class MockHibernateFactory extends HibernateFactory {
		public boolean hasRelevantContent(IFile file) {
			return true;
		}

		public ResourceModel buildResourceModel(JpaProject jpaProject,
				IFile file) {
			return buildResourceModel(jpaProject, file,
					JavaCore.JAVA_SOURCE_CONTENT_TYPE);
		}
	}

	public class MockHibernatePlatform extends HibernatePlatform {
		protected JpaFactory buildJpaFactory() {
			JpaFactory jpaFactory = super.buildJpaFactory();
			assertTrue(jpaFactory instanceof HibernateFactory);
			return new MockHibernateFactory();
		}
	}

	public class MockJavaInputStream extends InputStream {

		protected int pointer = 0;

		@Override
		public int read() throws IOException {
			if (pointer < strJava.getBytes().length) {
				return strJava.getBytes()[pointer++];
			}
			return -1;
		}
	}

	public class MockClassPathInputStream extends InputStream {

		protected int pointer = 0;

		@Override
		public int read() throws IOException {
			if (pointer < strClassPath.getBytes().length) {
				return strClassPath.getBytes()[pointer++];
			}
			return -1;
		}
	}

	public void testMockSave() throws CoreException, IOException {

		final JpaPlatform jpaPlatform = new MockHibernatePlatform();
		final String hibernatePlatformId = jpaPlatform.getId();
		assertTrue(HibernatePlatform.ID.equals(hibernatePlatformId));

		final JpaProject jpaProject = context.mock(JpaProject.class);
		final IProject project = context.mock(IProject.class);
		final IFile file = context.mock(IFile.class);
		final JavaResourceModel javaResourceModel = context
				.mock(JavaResourceModel.class);
		final Schema schema = context.mock(Schema.class);
		final ConnectionProfile connectionProfile = context
				.mock(ConnectionProfile.class);
		final Catalog catalog = context.mock(Catalog.class);
		final ArtifactEditModel artifactEditModel = context
				.mock(ArtifactEditModel.class);
		final ModuleCoreNature moduleCoreNature = context
				.mock(ModuleCoreNature.class);
		final Persistence persistence = context.mock(Persistence.class);
		final XmlPersistenceUnit xmlPersistenceUnit = context
				.mock(XmlPersistenceUnit.class);
		final InternalEObject owner = xmlPersistenceUnit;
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
		classes.add(xmlJavaClassRef1);
		final EList<XmlMappingFileRef> mappingFiles = new EObjectContainmentEList<XmlMappingFileRef>(
				XmlMappingFileRef.class, xmlPersistenceUnit,
				PersistencePackage.XML_PERSISTENCE_UNIT__MAPPING_FILES);
		final List<String> annotatedClassNamesList = new ArrayList<String>();
		final Iterator<String> annotatedClassNames = annotatedClassNamesList
				.iterator();

		final JavaResourcePersistentType javaResourcePersistentType = context
				.mock(JavaResourcePersistentType.class);

		final List<JavaResourcePersistentAttribute> resourceAttributesList1 = new ArrayList<JavaResourcePersistentAttribute>();

		final List<JavaResourcePersistentAttribute> resourceAttributesList2 = new ArrayList<JavaResourcePersistentAttribute>();
		final JDTFieldAttribute jdtFieldAttribute = new JDTFieldAttribute(null,
				"", 1, null, null); //$NON-NLS-1$
		final JavaResourcePersistentAttribute jrpa1 = new JavaResourcePersistentAttributeImpl(
				javaResourcePersistentType, jdtFieldAttribute);
		resourceAttributesList2.add(jrpa1);

		final GenericGeneratorAnnotationImpl genericGeneratorAnnotation = new GenericGeneratorAnnotationImpl(
				javaResourcePersistentType, null);

		final InputStream classPathIStream = new MockClassPathInputStream();
		final InputStream javaIStream = new MockJavaInputStream();

		final IPath pathProject = new Path(""); //$NON-NLS-1$

		final IPath pathJavaFile = new Path(javaFileName);

		context.checking(new Expectations() {
			{

				allowing(jpaProject).getJpaPlatform();
				will(returnValue(jpaPlatform));

				oneOf(jpaProject)
						.getModifySharedDocumentCommandExecutorProvider();
				will(returnValue(null));

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

				allowing(jpaProject).getProject();
				will(returnValue(project));

				oneOf(file).getParent();
				will(returnValue(project));

				oneOf(file).getName();
				will(returnValue(javaFileName));

				oneOf(project).getWorkingLocation("org.eclipse.jdt.core"); //$NON-NLS-1$
				will(returnValue(null));

				oneOf(project).getFile(new Path(javaFileName));
				will(returnValue(file));

				oneOf(file).getCharset();
				will(returnValue(null));

				oneOf(file).getLocation();
				will(returnValue(pathJavaFile));

				oneOf(file).getContents(true);
				will(returnValue(javaIStream));

				oneOf(project).getDefaultCharset();
				will(returnValue(null));
			}
		});
		JptCorePlugin.setDefaultJpaPlatformId(hibernatePlatformId);
		JptCorePlugin.setJpaPlatformId(project, hibernatePlatformId);

		final JpaFile jpaFile = jpaPlatform.buildJpaFile(jpaProject, file);

		final JpaCompilationUnit jpaCompilationUnit = context
				.mock(JpaCompilationUnit.class);
		final JpaAnnotationProvider jpaAnnotationProvider = new HibernateJpaAnnotationProvider();

		final IProjectFacetActionEvent projectFacetActionEvent = context
				.mock(IProjectFacetActionEvent.class);
		final IFacetedProject facetedProject = context
				.mock(IFacetedProject.class);
		final IProjectFacet projectFacet = context.mock(IProjectFacet.class);

		context.checking(new Expectations() {
			{
				allowing(xmlPersistenceUnit).getName();
				will(returnValue("xmlPUName")); //$NON-NLS-1$

				oneOf(xmlPersistenceUnit).setName("xmlPUName"); //$NON-NLS-1$

				allowing(xmlPersistenceUnit).getClasses();
				will(returnValue(classes));

				allowing(persistence).getJpaProject();
				will(returnValue(jpaProject));

				// Dali 2.0
				allowing(jpaProject).getJavaPersistentTypeResource(
						classFullName);
				will(returnValue(javaResourcePersistentType));

				allowing(jpaProject).getJavaPersistentTypeResource(null);
				will(returnValue(null));

				// Dali 2.1
				//allowing(jpaProject).getJavaResourcePersistentType(classFullName); //$NON-NLS-1$
				// will(returnValue(factory));

				// Dali 2.0
				allowing(javaResourcePersistentType).getAnnotation(
						Hibernate.GENERIC_GENERATOR);
				will(returnValue(genericGeneratorAnnotation));
				// Dali 2.1
				// allowing(javaResourcePersistentType).getSupportingAnnotation(
				// Hibernate.GENERIC_GENERATOR);
				// will(returnValue(genericGeneratorAnnotation));

				allowing(javaResourcePersistentType)
						.getSuperClassQualifiedName();
				will(returnValue(null));

				allowing(persistence).getOrmPersistentType();
				will(returnValue(null));

				allowing(javaResourcePersistentType).getAccess();
				will(returnValue(org.eclipse.jpt.core.resource.java.AccessType.PROPERTY));

				allowing(javaResourcePersistentType).getQualifiedName();
				will(returnValue(classFullName));

				allowing(javaResourcePersistentType).getMappingAnnotation();
				will(returnValue(null));

				allowing(javaResourcePersistentType).fields();
				will(returnValue(resourceAttributesList1.iterator()));

				allowing(javaResourcePersistentType).properties();
				will(returnValue(resourceAttributesList2.iterator()));

				oneOf(javaResourcePersistentType).getJpaCompilationUnit();
				will(returnValue(jpaCompilationUnit));

				oneOf(jpaCompilationUnit).getAnnotationProvider();
				will(returnValue(jpaAnnotationProvider));

				allowing(project).isAccessible();
				will(returnValue(true));

				allowing(xmlPersistenceUnit).getMappingFiles();
				will(returnValue(mappingFiles));

				allowing(project).getNature(
						"org.eclipse.wst.common.modulecore.ModuleCoreNature"); //$NON-NLS-1$
				will(returnValue(moduleCoreNature));

				allowing(project).isNatureEnabled(
						"org.eclipse.wst.common.project.facet.core.nature"); //$NON-NLS-1$
				will(returnValue(true));

				oneOf(project)
						.getFile(
								with(".settings/org.eclipse.wst.common.project.facet.core.xml")); //$NON-NLS-1$
				will(returnValue(file));

				allowing(file).getModificationStamp();
				will(returnValue(-1L));

				allowing(moduleCoreNature).getArtifactEditModelForRead(
						with(any(URI.class)), with(any(Object.class)),
						with(any(String.class)), with(any(Map.class)));
				will(returnValue(artifactEditModel));

				allowing(artifactEditModel).getResource(with(any(URI.class)));
				will(returnValue(null));

				allowing(artifactEditModel).releaseAccess(
						with(any(OrmArtifactEdit.class)));

				oneOf(jpaProject).discoversAnnotatedClasses();
				will(returnValue(true));

				allowing(jpaProject).annotatedClassNames();
				will(returnValue(annotatedClassNames));

				allowing(xmlPersistenceUnit).getProperties();
				will(returnValue(null));

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

				oneOf(javaResourcePersistentType).getResourceModel();
				will(returnValue(javaResourceModel));

				oneOf(javaResourceModel).getFile();
				will(returnValue(null));

				oneOf(jpaProject).getJpaFile(null);
				will(returnValue(jpaFile));

				oneOf(xmlPersistenceUnit).setExcludeUnlistedClasses(true);

				oneOf(jpaProject).getDefaultSchema();
				will(returnValue(schema));

				allowing(jpaProject).update();

				oneOf(schema).getName();
				will(returnValue("schemaName")); //$NON-NLS-1$

				oneOf(jpaProject).getConnectionProfile();
				will(returnValue(connectionProfile));

				oneOf(connectionProfile).getDefaultCatalog();
				will(returnValue(catalog));

				oneOf(catalog).getName();
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

		PersistenceUnit pu = jpaPlatform.getJpaFactory().buildPersistenceUnit(
				persistence, xmlPersistenceUnit);
		int classRefSizeOld = pu.classRefsSize();
		pu.update(xmlPersistenceUnit);
		int classRefSizeNew = pu.classRefsSize();
		assertTrue(classRefSizeOld == classRefSizeNew);

		JPAPostInstallFasetListener jpaPostInstallFasetListener = new JPAPostInstallFasetListener();
		jpaPostInstallFasetListener.handleEvent(projectFacetActionEvent);

		context.assertIsSatisfied();
	}

}
