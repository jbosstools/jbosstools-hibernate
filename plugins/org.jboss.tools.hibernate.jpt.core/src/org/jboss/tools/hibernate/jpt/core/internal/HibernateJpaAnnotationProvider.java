/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.hibernate.jpt.core.internal;

import java.util.Collection;
import java.util.List;

import org.eclipse.jpt.core.internal.platform.GenericJpaAnnotationProvider;
import org.eclipse.jpt.core.internal.resource.java.AssociationOverrideImpl.AssociationOverrideAnnotationDefinition;
import org.eclipse.jpt.core.internal.resource.java.AssociationOverridesImpl.AssociationOverridesAnnotationDefinition;
import org.eclipse.jpt.core.internal.resource.java.AttributeOverrideImpl.AttributeOverrideAnnotationDefinition;
import org.eclipse.jpt.core.internal.resource.java.AttributeOverridesImpl.AttributeOverridesAnnotationDefinition;
import org.eclipse.jpt.core.internal.resource.java.ColumnImpl.ColumnAnnotationDefinition;
import org.eclipse.jpt.core.internal.resource.java.DiscriminatorColumnImpl.DiscriminatorColumnAnnotationDefinition;
import org.eclipse.jpt.core.internal.resource.java.DiscriminatorValueImpl.DiscriminatorValueAnnotationDefinition;
import org.eclipse.jpt.core.internal.resource.java.EnumeratedImpl.EnumeratedAnnotationDefinition;
import org.eclipse.jpt.core.internal.resource.java.GeneratedValueImpl.GeneratedValueAnnotationDefinition;
import org.eclipse.jpt.core.internal.resource.java.IdClassImpl.IdClassAnnotationDefinition;
import org.eclipse.jpt.core.internal.resource.java.InheritanceImpl.InheritanceAnnotationDefinition;
import org.eclipse.jpt.core.internal.resource.java.JoinColumnImpl.JoinColumnAnnotationDefinition;
import org.eclipse.jpt.core.internal.resource.java.JoinColumnsImpl.JoinColumnsAnnotationDefinition;
import org.eclipse.jpt.core.internal.resource.java.JoinTableImpl.JoinTableAnnotationDefinition;
import org.eclipse.jpt.core.internal.resource.java.LobImpl.LobAnnotationDefinition;
import org.eclipse.jpt.core.internal.resource.java.MapKeyImpl.MapKeyAnnotationDefinition;
import org.eclipse.jpt.core.internal.resource.java.NamedNativeQueriesImpl.NamedNativeQueriesAnnotationDefinition;
import org.eclipse.jpt.core.internal.resource.java.NamedNativeQueryImpl.NamedNativeQueryAnnotationDefinition;
import org.eclipse.jpt.core.internal.resource.java.NamedQueriesImpl.NamedQueriesAnnotationDefinition;
import org.eclipse.jpt.core.internal.resource.java.NamedQueryImpl.NamedQueryAnnotationDefinition;
import org.eclipse.jpt.core.internal.resource.java.OrderByImpl.OrderByAnnotationDefinition;
import org.eclipse.jpt.core.internal.resource.java.PrimaryKeyJoinColumnImpl.PrimaryKeyJoinColumnAnnotationDefinition;
import org.eclipse.jpt.core.internal.resource.java.PrimaryKeyJoinColumnsImpl.PrimaryKeyJoinColumnsAnnotationDefinition;
import org.eclipse.jpt.core.internal.resource.java.SecondaryTableImpl.SecondaryTableAnnotationDefinition;
import org.eclipse.jpt.core.internal.resource.java.SecondaryTablesImpl.SecondaryTablesAnnotationDefinition;
import org.eclipse.jpt.core.internal.resource.java.SequenceGeneratorImpl.SequenceGeneratorAnnotationDefinition;
import org.eclipse.jpt.core.internal.resource.java.TableGeneratorImpl.TableGeneratorAnnotationDefinition;
import org.eclipse.jpt.core.internal.resource.java.TableImpl.TableAnnotationDefinition;
import org.eclipse.jpt.core.internal.resource.java.TemporalImpl.TemporalAnnotationDefinition;
import org.eclipse.jpt.core.resource.java.AnnotationDefinition;
import org.jboss.tools.hibernate.jpt.core.internal.context.java.GenericGeneratorAnnotationImpl.GenericGeneratorAnnotationDefinition;

/**
 * @author Dmitry Geraskov
 *
 */
public class HibernateJpaAnnotationProvider extends GenericJpaAnnotationProvider {
	
	/**
	 * Method for Dali 2.1 instead of {@link #addTypeAnnotationDefinitionsTo(Collection)}
	 */
	protected void addTypeSupportingAnnotationDefinitionsTo(List<AnnotationDefinition> definitions){
		addTypeAnnotationDefinitionsTo(definitions);
	}
	
	/**
	 * Method for Dali 2.0
	 */
	protected void addTypeAnnotationDefinitionsTo(Collection<AnnotationDefinition> definitions) {
		definitions.add(GenericGeneratorAnnotationDefinition.instance());
		//add Dali's annotation definitions 
		definitions.add(AssociationOverrideAnnotationDefinition.instance());
		definitions.add(AssociationOverridesAnnotationDefinition.instance());
		definitions.add(AttributeOverrideAnnotationDefinition.instance());
		definitions.add(AttributeOverrideAnnotationDefinition.instance());
		definitions.add(AttributeOverridesAnnotationDefinition.instance());
		definitions.add(DiscriminatorColumnAnnotationDefinition.instance());
		definitions.add(DiscriminatorValueAnnotationDefinition.instance());
		definitions.add(IdClassAnnotationDefinition.instance());
		definitions.add(InheritanceAnnotationDefinition.instance());
		definitions.add(NamedQueryAnnotationDefinition.instance());
		definitions.add(NamedQueriesAnnotationDefinition.instance());
		definitions.add(NamedNativeQueryAnnotationDefinition.instance());
		definitions.add(NamedNativeQueriesAnnotationDefinition.instance());
		definitions.add(PrimaryKeyJoinColumnAnnotationDefinition.instance());
		definitions.add(PrimaryKeyJoinColumnsAnnotationDefinition.instance());
		definitions.add(SecondaryTableAnnotationDefinition.instance());
		definitions.add(SecondaryTablesAnnotationDefinition.instance());
		definitions.add(SequenceGeneratorAnnotationDefinition.instance());
		definitions.add(TableAnnotationDefinition.instance());
		definitions.add(TableGeneratorAnnotationDefinition.instance());
	}	
	
	/**
	 * Method for Dali 2.1 instead of {@link #addAttributeAnnotationDefinitionsTo(Collection)}
	 */
	protected void addAttributeSupportingAnnotationDefinitionsTo(List<AnnotationDefinition> definitions) {
		addAttributeAnnotationDefinitionsTo(definitions);
	}	
	
	/**
	 * Method for Dali 2.0
	 */
	protected void addAttributeAnnotationDefinitionsTo(Collection<AnnotationDefinition> definitions) {
		definitions.add(GenericGeneratorAnnotationDefinition.instance());
		//add Dali's annotation definitions 
		definitions.add(AssociationOverrideAnnotationDefinition.instance());
		definitions.add(AssociationOverridesAnnotationDefinition.instance());
		definitions.add(AttributeOverrideAnnotationDefinition.instance());
		definitions.add(AttributeOverridesAnnotationDefinition.instance());
		definitions.add(ColumnAnnotationDefinition.instance());
		definitions.add(EnumeratedAnnotationDefinition.instance());
		definitions.add(GeneratedValueAnnotationDefinition.instance());
		definitions.add(JoinColumnAnnotationDefinition.instance());
		definitions.add(JoinColumnsAnnotationDefinition.instance());
		definitions.add(JoinTableAnnotationDefinition.instance());
		definitions.add(LobAnnotationDefinition.instance());
		definitions.add(MapKeyAnnotationDefinition.instance());
		definitions.add(OrderByAnnotationDefinition.instance());
		definitions.add(PrimaryKeyJoinColumnAnnotationDefinition.instance());
		definitions.add(PrimaryKeyJoinColumnsAnnotationDefinition.instance());
		definitions.add(SequenceGeneratorAnnotationDefinition.instance());
		definitions.add(TableGeneratorAnnotationDefinition.instance());
		definitions.add(TemporalAnnotationDefinition.instance());		
	}	
	
}
