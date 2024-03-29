/*******************************************************************************
 * Copyright (c) 2007-2020 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.hibernate.orm.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.hibernate.eclipse.jdt.ui.internal.jpa.actions.JPAMapToolActor;
import org.hibernate.eclipse.jdt.ui.internal.jpa.collect.AllEntitiesInfoCollector;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.EntityInfo;
import org.hibernate.eclipse.jdt.ui.internal.jpa.process.AllEntitiesProcessor;
import org.hibernate.eclipse.jdt.ui.internal.jpa.process.AnnotStyle;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * 
 * @author Vitali Yemialyanchyk
 */
public class JPAMapMockTest {


	public Mockery context = new Mockery() {{
		setImposteriser(ClassImposteriser.INSTANCE);
	}};
	
	//TODO JBIDE-28083: Hibernate Java 17 compability - Reenable test and investigate error
	@Ignore
	@Test
	public void testMockSave() {
		final IPreferenceStore preferenceStore = context.mock(IPreferenceStore.class);

		final AllEntitiesProcessor allEntitiesProcessor = new AllEntitiesProcessor();
		allEntitiesProcessor.setAnnotationStyle(AnnotStyle.AUTO);
		allEntitiesProcessor.setPreferenceStore(preferenceStore);
		
        context.checking(new Expectations() {{
        	oneOf(preferenceStore).setValue(AllEntitiesProcessor.storePropertyName, 2);
        	oneOf(preferenceStore).setValue(AllEntitiesProcessor.storeDefaultStrLength, 255);
        	oneOf(preferenceStore).setValue(AllEntitiesProcessor.storeEnableOptLock, false);
        }});
        allEntitiesProcessor.savePreferences();
        context.assertIsSatisfied();

        allEntitiesProcessor.setAnnotationStyle(null);
        context.checking(new Expectations() {{
        	oneOf(preferenceStore).setValue(AllEntitiesProcessor.storePropertyName, 0);
        	oneOf(preferenceStore).setValue(AllEntitiesProcessor.storeDefaultStrLength, 255);
        	oneOf(preferenceStore).setValue(AllEntitiesProcessor.storeEnableOptLock, false);
        }});
        allEntitiesProcessor.savePreferences();
        context.assertIsSatisfied();
	}

	//TODO JBIDE-28083: Hibernate Java 17 compability - Reenable test and investigate error
	@Ignore
	@Test
	public void testJPAMapToolActor() {

		final JPAMapToolActor jpaMapToolActor = new JPAMapToolActor();
		
		final AllEntitiesProcessor allEntitiesProcessor = context.mock(AllEntitiesProcessor.class);
		final AllEntitiesInfoCollector allEntitiesInfoCollector = context.mock(AllEntitiesInfoCollector.class);
		final ISelection selection = context.mock(ISelection.class);
		final ICompilationUnit compilationUnit = context.mock(ICompilationUnit.class);
		final IJavaProject javaProject = context.mock(IJavaProject.class);

		jpaMapToolActor.setAllEntitiesProcessor(allEntitiesProcessor);
		jpaMapToolActor.setAllEntitiesInfoCollector(allEntitiesInfoCollector);
		jpaMapToolActor.setSelection(selection);
		
		final IStructuredSelection selection2Update = new StructuredSelection();
        context.checking(new Expectations() {{
        	exactly(1).of(allEntitiesProcessor).modify(new HashMap<String, EntityInfo>(), true, selection2Update);
        }});
		jpaMapToolActor.updateSelected(Integer.MAX_VALUE);
        context.assertIsSatisfied();

		jpaMapToolActor.setSelection(null);
		Set<ICompilationUnit> selectionCU = new HashSet<ICompilationUnit>();
		selectionCU.add(compilationUnit);
		jpaMapToolActor.setSelectionCU(selectionCU);

		final Sequence sequence = context.sequence("updateSelected"); //$NON-NLS-1$
		context.checking(new Expectations() {{
        	allowing(compilationUnit).getJavaProject();
        	inSequence(sequence);
        	will(returnValue(javaProject));
        	
        	allowing(allEntitiesInfoCollector).initCollector();
        	inSequence(sequence);
        	
        	allowing(allEntitiesInfoCollector).collect(compilationUnit, Integer.MAX_VALUE);
        	inSequence(sequence);
        	
        	allowing(allEntitiesInfoCollector).resolveRelations();
        	inSequence(sequence);
        	
        	allowing(allEntitiesInfoCollector).getNonAbstractCUNumber();
        	inSequence(sequence);
        	will(returnValue(2));
        	
        	allowing(allEntitiesInfoCollector).getNonInterfaceCUNumber();
        	inSequence(sequence);
        	will(returnValue(2));
        	
        	allowing(allEntitiesInfoCollector).getAnnotationStylePreference();
        	inSequence(sequence);
        	will(returnValue(AnnotStyle.GETTERS));
        	
        	allowing(allEntitiesProcessor).setAnnotationStylePreference(AnnotStyle.GETTERS);
        	inSequence(sequence);

        	allowing(allEntitiesInfoCollector).getMapCUs_Info();
        	inSequence(sequence);
        	will(returnValue(null));
        	
        	allowing(allEntitiesProcessor).modify(null, true, null);
        	inSequence(sequence);
        	
        	allowing(allEntitiesProcessor).savePreferences();
        	inSequence(sequence);

        	exactly(1).of(allEntitiesProcessor).modify(null, true, selection2Update);
		}});
		jpaMapToolActor.updateSelected(Integer.MAX_VALUE);
        context.assertIsSatisfied();
	}
}
