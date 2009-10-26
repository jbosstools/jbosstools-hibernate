/*******************************************************************************
 * Copyright (c) 2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.hibernate.jpt.core.internal.resource.java;

import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jpt.core.internal.resource.java.source.SourceAnnotation;
import org.eclipse.jpt.core.internal.utility.jdt.ConversionDeclarationAnnotationElementAdapter;
import org.eclipse.jpt.core.internal.utility.jdt.ShortCircuitAnnotationElementAdapter;
import org.eclipse.jpt.core.internal.utility.jdt.ShortCircuitArrayAnnotationElementAdapter;
import org.eclipse.jpt.core.internal.utility.jdt.SimpleDeclarationAnnotationAdapter;
import org.eclipse.jpt.core.internal.utility.jdt.StringArrayExpressionConverter;
import org.eclipse.jpt.core.internal.utility.jdt.StringExpressionConverter;
import org.eclipse.jpt.core.resource.java.Annotation;
import org.eclipse.jpt.core.resource.java.AnnotationDefinition;
import org.eclipse.jpt.core.resource.java.JavaResourceNode;
import org.eclipse.jpt.core.resource.java.JavaResourcePersistentMember;
import org.eclipse.jpt.core.utility.TextRange;
import org.eclipse.jpt.core.utility.jdt.AnnotationElementAdapter;
import org.eclipse.jpt.core.utility.jdt.Attribute;
import org.eclipse.jpt.core.utility.jdt.DeclarationAnnotationAdapter;
import org.eclipse.jpt.core.utility.jdt.DeclarationAnnotationElementAdapter;
import org.eclipse.jpt.core.utility.jdt.Member;

/**
 * @author Dmitry Geraskov
 *
 */
public class IndexAnnotationImpl extends SourceAnnotation<Attribute>
implements IndexAnnotation{
	
	public static final DeclarationAnnotationAdapter DECLARATION_ANNOTATION_ADAPTER = new SimpleDeclarationAnnotationAdapter(ANNOTATION_NAME);

	private static final DeclarationAnnotationElementAdapter<String> NAME_ADAPTER = buildNameAdapter(DECLARATION_ANNOTATION_ADAPTER);
	private final AnnotationElementAdapter<String> nameAdapter;
	private String name;
	
	private static DeclarationAnnotationElementAdapter<String[]> COLUMN_NAMES_ADAPTER = buildColumnNamesAdapter(DECLARATION_ANNOTATION_ADAPTER);
	private AnnotationElementAdapter<String[]> columnNamesAdapter;
	private String[] columnNames;

	protected IndexAnnotationImpl(JavaResourceNode parent, Attribute member) {
		super(parent, member, DECLARATION_ANNOTATION_ADAPTER);
		this.nameAdapter = this.buildNameAdapter(NAME_ADAPTER);
		this.columnNamesAdapter = this.buildColumnNamesAdapter(COLUMN_NAMES_ADAPTER);
	}

	public String getAnnotationName() {
		return ANNOTATION_NAME;
	}

	public void initialize(CompilationUnit astRoot) {
		this.name = this.buildName(astRoot);
		this.columnNames = this.buildColumnNames(astRoot);
	}

	public void update(CompilationUnit astRoot) {
		this.setName(this.buildName(astRoot));
		this.setColumnNames(this.buildColumnNames(astRoot));
	}

	public String[] getColumnNames() {
		return columnNames;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String newName) {
		if (this.attributeValueHasNotChanged(this.name, newName)) {
			return;
		}
		String old = this.name;
		this.name = newName;
		this.nameAdapter.setValue(newName);
		this.firePropertyChanged(NAME_PROPERTY, old, newName);
	}
	
	public void setColumnNames(String[] newColumnNames) {
		if (this.attributeValueHasNotChanged(this.columnNames, newColumnNames)) {
			return;
		}
		String[] old = this.columnNames;
		this.columnNames = newColumnNames;
		this.columnNamesAdapter.setValue(newColumnNames);
		this.firePropertyChanged(COLUMN_NAMES_PROPERTY, old, newColumnNames);
	}
	
	private String buildName(CompilationUnit astRoot) {
		return this.nameAdapter.getValue(astRoot);
	}
	
	private String[] buildColumnNames(CompilationUnit astRoot) {
		return this.columnNamesAdapter.getValue(astRoot);
	}
	
	private static DeclarationAnnotationElementAdapter<String> buildNameAdapter(DeclarationAnnotationAdapter adapter) {
		return ConversionDeclarationAnnotationElementAdapter.forStrings(adapter, NAME_PROPERTY, true);
	}
	
	AnnotationElementAdapter<String> buildNameAdapter(DeclarationAnnotationElementAdapter<String> daea) {
		return new ShortCircuitAnnotationElementAdapter<String>(this.member, daea);
	}
	
	private static DeclarationAnnotationElementAdapter<String[]> buildColumnNamesAdapter(DeclarationAnnotationAdapter adapter) {
		return new ConversionDeclarationAnnotationElementAdapter<String[]>(adapter, COLUMN_NAMES_PROPERTY, false,
				new StringArrayExpressionConverter(StringExpressionConverter.instance()));
	}
	
	AnnotationElementAdapter<String[]> buildColumnNamesAdapter(DeclarationAnnotationElementAdapter<String[]> daea) {
		return new ShortCircuitArrayAnnotationElementAdapter<String>(this.member, daea);
	}

	public TextRange getNameTextRange(CompilationUnit astRoot) {
		return this.getElementTextRange(NAME_ADAPTER, astRoot);
	}
	
	public TextRange getColumnNamesTextRange(CompilationUnit astRoot) {
		return this.getElementTextRange(COLUMN_NAMES_ADAPTER, astRoot);
	}
	
	public static class IndexAnnotationDefinition implements AnnotationDefinition
	{
		// singleton
		private static final IndexAnnotationDefinition INSTANCE = new IndexAnnotationDefinition();

		/**
		 * Return the singleton.
		 */
		public static AnnotationDefinition instance() {
			return INSTANCE;
		}

		/**
		 * Ensure non-instantiability.
		 */
		private IndexAnnotationDefinition() {
			super();
		}

		public Annotation buildAnnotation(JavaResourcePersistentMember parent, Member attribute) {
			return new IndexAnnotationImpl(parent, (Attribute) attribute);
		}
		
		public String getAnnotationName() {
			return IndexAnnotation.ANNOTATION_NAME;
		}

		public Annotation buildAnnotation(JavaResourcePersistentMember arg0,
				IAnnotation arg1) {
			throw new UnsupportedOperationException();
		}

		public Annotation buildNullAnnotation(JavaResourcePersistentMember parent) {
			throw new UnsupportedOperationException();
		}

	}

}
