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
package org.hibernate.eclipse.jdt.ui.internal.jpa.process;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.hibernate.eclipse.jdt.ui.internal.jpa.collect.CollectEntityInfo;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.EntityInfo;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.EntityInfosCollection;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.JPAConst;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.OwnerType;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.RefColumnInfo;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.RefEntityInfo;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.RefFieldInfo;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.RefType;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.EntityInfo.FieldGetterType;

/**
 * Visitor to insert JPA annotations into proper 
 * places of entity class.
 * 
 * @author Vitali
 */
public class ProcessEntityInfo extends ASTVisitor {

	/**
	 * information about entity's annotations for whole compilation unit
	 */
	protected EntityInfosCollection entityInfos;
	/**
	 * information about entity's annotations
	 */
	protected EntityInfo entityInfo;
	/**
	 * information about all entities
	 */
	protected Map<String, EntityInfo> entities;
	/**
	 * rewriter to generate new AST blocks
	 */
	protected ASTRewrite rewriter;
	/**
	 * annotation style
	 */
	protected AnnotStyle annotationStyle = AnnotStyle.FIELDS;
	/**
	 * default length for column which corresponds to String field
	 */
	protected int defaultStrLength = AllEntitiesProcessor.columnLength;
	/**
	 * flag to enable optimistic locking
	 */
	protected boolean enableOptLock = false;

	public void setEntityInfos(EntityInfosCollection entityInfos) {
		this.entityInfos = entityInfos;
	}

	public void setEntities(Map<String, EntityInfo> entities) {
		this.entities = entities;
	}
	
	public void setASTRewrite(ASTRewrite rewriter) {
		this.rewriter = rewriter;
	}

	public boolean visit(CompilationUnit node) {
		entityInfo = null;
		// TODO: sort all imports in alphabetic order
		//ListRewrite lrw = rewriter.getListRewrite(node, CompilationUnit.IMPORTS_PROPERTY);
		for (int i = 0; i < JPAConst.ALL_IMPORTS.size(); i++) {
			String tmp = JPAConst.ALL_IMPORTS.get(i);
			if (entityInfos.needImport(tmp)) {
				addImport(node, tmp);
			}
		}
		return true;
	}

	public boolean addImport(CompilationUnit node, String importDeclaration) {
		String[] importDeclarations = importDeclaration.split("\\."); //$NON-NLS-1$
		if (importDeclarations.length <= 1) {
			return false;
		}
		ImportDeclaration importDecl = rewriter.getAST().newImportDeclaration();
		SimpleName simpleName0 = rewriter.getAST().newSimpleName(importDeclarations[0]);
		SimpleName simpleName1 = rewriter.getAST().newSimpleName(importDeclarations[1]);
		QualifiedName qualifiedName0 = rewriter.getAST().newQualifiedName(simpleName0, simpleName1);
		for (int i = 2; i < importDeclarations.length; i++) {
			SimpleName simpleNameI = rewriter.getAST().newSimpleName(importDeclarations[i]);
			qualifiedName0 = rewriter.getAST().newQualifiedName(qualifiedName0, simpleNameI);
		}
		importDecl.setName(qualifiedName0);
		ListRewrite lrw = rewriter.getListRewrite(node, CompilationUnit.IMPORTS_PROPERTY);
		// insert import declaration in the proper place
		// prefer alphabetic order and package separation
		Iterator<?> it = lrw.getRewrittenList().iterator();
		ASTNode insertBeforeNode = null;
		for ( ; it.hasNext(); ) {
			Object obj = it.next();
			if (!(obj instanceof ImportDeclaration)) {
				continue;
			}
			ImportDeclaration id = (ImportDeclaration)obj;
			String idName = id.getName().getFullyQualifiedName();
			if (idName.compareTo(importDeclaration) > 0) {
				insertBeforeNode = id;
				break;
			}
		}
		if (insertBeforeNode == null) {
			lrw.insertLast(importDecl, null);
		}
		else {
			lrw.insertBefore(importDecl, insertBeforeNode, null);
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public boolean visit(TypeDeclaration node) {
		ITypeBinding typeBinding = node.resolveBinding();
		String nodeName = typeBinding == null ? null : typeBinding.getBinaryName();
		if (nodeName == null) {
			return false;
		}
		entityInfo = entityInfos.getEntityInfo(nodeName);
		if (entityInfo == null) {
			return false;
		}
		if (entityInfo.isAddMappedSuperclassFlag()) {
			MarkerAnnotation matd = rewriter.getAST().newMarkerAnnotation();
			matd.setTypeName(rewriter.getAST().newSimpleName(JPAConst.ANNOTATION_MAPPEDSUPERCLASS));
			ListRewrite lrw = rewriter.getListRewrite(node, TypeDeclaration.MODIFIERS2_PROPERTY);
			lrw.insertFirst(matd, null);
		}
		if (entityInfo.isAddEntityFlag()) {
			MarkerAnnotation matd = rewriter.getAST().newMarkerAnnotation();
			matd.setTypeName(rewriter.getAST().newSimpleName(JPAConst.ANNOTATION_ENTITY));
			ListRewrite lrw = rewriter.getListRewrite(node, TypeDeclaration.MODIFIERS2_PROPERTY);
			lrw.insertFirst(matd, null);
		}
		/** /
		if (!entityInfo.isImplicitConstructorFlag() && !entityInfo.isDefaultConstructorFlag() &&
				entityInfo.isAddSerializableInterfaceFlag()) {
			// add serializable interface
			SimpleName sn = null;
			//if (!entityInfo.isAddSerializableInterfaceImportFlag()) {
				sn = rewriter.getAST().newSimpleName(JPAConst.ANNOTATION_SERIALIZABLE);
			//}
			//else {
			//	sn = rewriter.getAST().newSimpleName(JPAConst.IMPORT_SERIALIZABLE);
			//}
			SimpleType st = rewriter.getAST().newSimpleType(sn);
			ListRewrite lrw = rewriter.getListRewrite(node, TypeDeclaration.SUPER_INTERFACE_TYPES_PROPERTY);
			lrw.insertFirst(st, null);
			// add "private static final long serialVersionUID = 1L;"
			// ...
		}
		/**/
		if (!entityInfo.isImplicitConstructorFlag() && !entityInfo.isDefaultConstructorFlag() &&
				entityInfo.isAddSerializableInterfaceFlag()) {

			MethodDeclaration md = rewriter.getAST().newMethodDeclaration();
			md.setConstructor(true);
			Modifier modifier = rewriter.getAST().newModifier(Modifier.ModifierKeyword.PROTECTED_KEYWORD);
			md.modifiers().add(modifier);
			Block body = rewriter.getAST().newBlock();
			md.setBody(body);
			SimpleName sn = rewriter.getAST().newSimpleName(entityInfo.getName());
			md.setName(sn);
			ListRewrite lrw = rewriter.getListRewrite(node, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
			List<?> list = lrw.getOriginalList();
			MethodDeclaration insertBeforeNode = null;
			Iterator<?> it = list.iterator();
			while (it.hasNext()) {
				Object obj = it.next();
				if (obj instanceof MethodDeclaration) {
					insertBeforeNode = (MethodDeclaration)obj;
					break;
				}
			}
			if (insertBeforeNode == null) {
				lrw.insertLast(md, null);
			}
			else {
				lrw.insertBefore(md, insertBeforeNode, null);
			}
		}
		if (enableOptLock && entityInfo.isAddVersionFlag() && !entityInfo.hasVersionAnnotation()) {
			// add property "version", add getter/setter getVersion/setVersion,
			// add annotation for the property or for the getter
			//
			final String version = "version"; //$NON-NLS-1$
			final String versionType = "Integer"; //$NON-NLS-1$
			//
			VariableDeclarationFragment vdFragment = rewriter.getAST().newVariableDeclarationFragment();
			SimpleName variableName = rewriter.getAST().newSimpleName(version);
			vdFragment.setName(variableName);
			FieldDeclaration fieldVersion = rewriter.getAST().newFieldDeclaration(vdFragment);
			Modifier modifier = rewriter.getAST().newModifier(Modifier.ModifierKeyword.PROTECTED_KEYWORD);
			fieldVersion.modifiers().add(modifier);
			Name typeName = rewriter.getAST().newName(versionType);
			SimpleType type = rewriter.getAST().newSimpleType(typeName);
			fieldVersion.setType(type);
			//
			MethodDeclaration mdGetter = rewriter.getAST().newMethodDeclaration();
			modifier = rewriter.getAST().newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
			mdGetter.modifiers().add(modifier);
			Block body = rewriter.getAST().newBlock();
			ReturnStatement returnVersion = rewriter.getAST().newReturnStatement();
			variableName = rewriter.getAST().newSimpleName(version);
			returnVersion.setExpression(variableName);
			body.statements().add(returnVersion);
			mdGetter.setBody(body);
			SimpleName sn = rewriter.getAST().newSimpleName("getVersion"); //$NON-NLS-1$
			mdGetter.setName(sn);
			typeName = rewriter.getAST().newName(versionType);
			type = rewriter.getAST().newSimpleType(typeName);
			mdGetter.setReturnType2(type);
			//
			MethodDeclaration mdSetter = rewriter.getAST().newMethodDeclaration();
			modifier = rewriter.getAST().newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
			mdSetter.modifiers().add(modifier);
			body = rewriter.getAST().newBlock();
			Assignment assignment = rewriter.getAST().newAssignment();
			FieldAccess fieldAccess = rewriter.getAST().newFieldAccess();
			ThisExpression thisExpression = rewriter.getAST().newThisExpression();
			fieldAccess.setExpression(thisExpression);
			variableName = rewriter.getAST().newSimpleName(version);
			fieldAccess.setName(variableName);
			assignment.setLeftHandSide(fieldAccess);
			variableName = rewriter.getAST().newSimpleName(version);
			assignment.setRightHandSide(variableName);
			ExpressionStatement expressionStatement = rewriter.getAST().newExpressionStatement(assignment);
			body.statements().add(expressionStatement);
			mdSetter.setBody(body);
			sn = rewriter.getAST().newSimpleName("setVersion"); //$NON-NLS-1$
			mdSetter.setName(sn);
			SingleVariableDeclaration svd = rewriter.getAST().newSingleVariableDeclaration();
			variableName = rewriter.getAST().newSimpleName(version);
			svd.setName(variableName);
			typeName = rewriter.getAST().newName(versionType);
			type = rewriter.getAST().newSimpleType(typeName);
			svd.setType(type);
			mdSetter.parameters().add(svd);
			//
			ListRewrite lrw = rewriter.getListRewrite(node, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
			if (entityInfo.getVersionFieldGetter() != FieldGetterType.FIELD && 
					entityInfo.getVersionFieldGetter() != FieldGetterType.FIELD_GETTER) {
				lrw.insertLast(fieldVersion, null);
			}
			if (entityInfo.getVersionFieldGetter() != FieldGetterType.GETTER && 
					entityInfo.getVersionFieldGetter() != FieldGetterType.FIELD_GETTER) {
				lrw.insertLast(mdGetter, null);
				lrw.insertLast(mdSetter, null);
			}
			if (annotationStyle == AnnotStyle.FIELDS) {
				MarkerAnnotation matd = rewriter.getAST().newMarkerAnnotation();
				matd.setTypeName(rewriter.getAST().newSimpleName(JPAConst.ANNOTATION_VERSION));
				lrw = rewriter.getListRewrite(fieldVersion, FieldDeclaration.MODIFIERS2_PROPERTY);
				lrw.insertFirst(matd, null);
			}
			else if (annotationStyle == AnnotStyle.GETTERS) {
				MarkerAnnotation matd = rewriter.getAST().newMarkerAnnotation();
				matd.setTypeName(rewriter.getAST().newSimpleName(JPAConst.ANNOTATION_VERSION));
				lrw = rewriter.getListRewrite(mdGetter, MethodDeclaration.MODIFIERS2_PROPERTY);
				lrw.insertFirst(matd, null);
			}
		}
		return true;		
	}
	
	@SuppressWarnings("unchecked")
	public boolean visit(FieldDeclaration node) {
		if (entityInfo == null) {
			return false;
		}
		if (annotationStyle != AnnotStyle.FIELDS) {
			return true;
		}
		Type type = node.getType();
		if (type == null) {
			return true;
		}
		if (type.isSimpleType() || type.isPrimitiveType()) {
			if (entityInfo.isAddGeneratedValueFlag()) {
				String primaryIdName = entityInfo.getPrimaryIdName();
				Iterator<VariableDeclarationFragment> itVarNames = node.fragments().iterator();
				boolean addGeneratedValueMarker = false;
				while (itVarNames.hasNext()) {
					VariableDeclarationFragment var = itVarNames.next();
					String name = var.getName().getIdentifier();
					if (primaryIdName.equals(name)) {
						addGeneratedValueMarker = true;
						break;
					}
				}
				if (addGeneratedValueMarker) {
					MarkerAnnotation matd = rewriter.getAST().newMarkerAnnotation();
					matd.setTypeName(rewriter.getAST().newSimpleName(JPAConst.ANNOTATION_GENERATED_VALUE));
					ListRewrite lrw = rewriter.getListRewrite(node, FieldDeclaration.MODIFIERS2_PROPERTY);
					lrw.insertFirst(matd, null);
				}
			}
			if (entityInfo.isAddPrimaryIdFlag()) {
				String primaryIdName = entityInfo.getPrimaryIdName();
				Iterator<VariableDeclarationFragment> itVarNames = node.fragments().iterator();
				boolean addIdMarker = false;
				while (itVarNames.hasNext()) {
					VariableDeclarationFragment var = itVarNames.next();
					String name = var.getName().getIdentifier();
					if (primaryIdName.equals(name)) {
						addIdMarker = true;
						break;
					}
				}
				if (addIdMarker) {
					MarkerAnnotation matd = rewriter.getAST().newMarkerAnnotation();
					matd.setTypeName(rewriter.getAST().newSimpleName(JPAConst.ANNOTATION_ID));
					ListRewrite lrw = rewriter.getListRewrite(node, FieldDeclaration.MODIFIERS2_PROPERTY);
					lrw.insertFirst(matd, null);
				}
			}
			if (enableOptLock && entityInfo.isAddVersionFlag() && !entityInfo.hasVersionAnnotation()) {
				Iterator<VariableDeclarationFragment> itVarNames = node.fragments().iterator();
				boolean addVersionMarker = false;
				while (itVarNames.hasNext()) {
					VariableDeclarationFragment var = itVarNames.next();
					String name = var.getName().getIdentifier();
					if ("version".equals(name)) { //$NON-NLS-1$
						addVersionMarker = true;
						break;
					}
				}
				if (addVersionMarker) {
					MarkerAnnotation matd = rewriter.getAST().newMarkerAnnotation();
					matd.setTypeName(rewriter.getAST().newSimpleName(JPAConst.ANNOTATION_VERSION));
					ListRewrite lrw = rewriter.getListRewrite(node, FieldDeclaration.MODIFIERS2_PROPERTY);
					lrw.insertFirst(matd, null);
				}
			}
		}
		if (type.isSimpleType() && (AllEntitiesProcessor.columnLength != defaultStrLength)) {
			SimpleType simpleType = (SimpleType)type;
			String typeName = simpleType.getName().getFullyQualifiedName();
			if ("java.lang.String".equals(typeName) || "String".equals(typeName)) { //$NON-NLS-1$ //$NON-NLS-2$
				String fieldId = null;
				Iterator<VariableDeclarationFragment> itVarNames = node.fragments().iterator();
				while (itVarNames.hasNext()) {
					VariableDeclarationFragment var = itVarNames.next();
					fieldId = var.getName().getIdentifier();
					if (fieldId != null) {
						break;
					}
				}
				RefColumnInfo rci = entityInfo.getRefColumnInfo(fieldId);
				if (rci == null || !rci.isExist()) {
					// if there is no @Column annotation - create new @Column annotation
					// with user defined default value length 
					NormalAnnotation natd = rewriter.getAST().newNormalAnnotation();
					natd.setTypeName(rewriter.getAST().newSimpleName(JPAConst.ANNOTATION_COLUMN));
					ListRewrite lrw = rewriter.getListRewrite(node, FieldDeclaration.MODIFIERS2_PROPERTY);
					lrw.insertFirst(natd, null);
					MemberValuePair mvp = rewriter.getAST().newMemberValuePair();
					mvp.setName(rewriter.getAST().newSimpleName("length")); //$NON-NLS-1$
					NumberLiteral nl = rewriter.getAST().newNumberLiteral(String.valueOf(defaultStrLength));
					mvp.setValue(nl);
					natd.values().add(mvp);
				}
			}
		}
		if (type.isSimpleType() || type.isParameterizedType() || type.isArrayType()) {
			Iterator<VariableDeclarationFragment> itVarNames = node.fragments().iterator();
			String fieldId = ""; //$NON-NLS-1$
			RefType refType = RefType.UNDEF;
			boolean annotated = false;
			String fullyQualifiedName2 = ""; //$NON-NLS-1$
			while (itVarNames.hasNext()) {
				VariableDeclarationFragment var = itVarNames.next();
				String name = var.getName().getIdentifier();
				fieldId = name;
				refType = entityInfo.getFieldIdRelValue(fieldId);
				annotated = entityInfo.getFieldIdAnnotatedValue(fieldId);
				fullyQualifiedName2 = entityInfo.getFieldIdFQNameValue(fieldId);
				if (refType != RefType.UNDEF) {
					break;
				}
			}
			Set<RefFieldInfo> setRFI = entityInfo.getRefFieldInfoSet(fullyQualifiedName2);
			if (!annotated && setRFI != null && isSimilarType(type, fullyQualifiedName2)) {
				RefEntityInfo rei = entityInfo.getFieldIdRefEntityInfo(fieldId);
				// try to process bidirectional relationships:
				// nRefType == JPAConst.ONE2ONE - OneToOne - the owning side corresponds
				//              to the side that contains the corresponding foreign key
				// nRefType == JPAConst.MANY2ONE - ManyToOne - owning side is always the "many" side
				// nRefType == JPAConst.MANY2MANY - ManyToMany bidirectional relationships 
				//              either side may be the owning side
				if (setRFI.size() > 1 && refType != RefType.MANY2ONE) {
					if (rei.mappedBy == null || rei.mappedBy == "") { //$NON-NLS-1$
						addSimpleMarkerAnnotation(node, JPAConst.getRefType(refType));
					}
					else {
						// give to the user information about selected mapping
						addComplexNormalAnnotation(node, JPAConst.getRefType(refType), rei);
					}
				}
				else if (refType == RefType.MANY2ONE || rei.mappedBy == null || rei.mappedBy == "") { //$NON-NLS-1$
					addSimpleMarkerAnnotation(node, JPAConst.getRefType(refType));
				}
				else {
					// in case of bidirectional OneToOne - mark both sides with mappedBy - 
					// user should select the right decision 
					addComplexNormalAnnotation(node, JPAConst.getRefType(refType), rei);
				}
			}
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public boolean visit(MethodDeclaration node) {
		if (entityInfo == null) {
			return false;
		}
		if (annotationStyle != AnnotStyle.GETTERS) {
			return true;
		}
		if (node.getName().getFullyQualifiedName().compareTo(entityInfo.getName()) == 0 || node.isConstructor()) {
			// this is constructor declaration
			return true;
		}
		// -) is it setter?
		if (node.getName().getIdentifier().startsWith("set") //$NON-NLS-1$
				&& node.parameters().size() == 1) { 
			// setter - do not process it
			return true;
		}
		// +) is it getter?
		if (!(node.getName().getIdentifier().startsWith("get") //$NON-NLS-1$
				|| node.getName().getIdentifier().startsWith("is")) //$NON-NLS-1$
				|| node.parameters().size() > 0) {
			// not the getter - do not process it
			return true;
		}
		Type type = node.getReturnType2();
		if (type == null) {
			return true;
		}
		String returnIdentifier = CollectEntityInfo.getReturnIdentifier(node);
		if (type.isSimpleType() || type.isPrimitiveType()) {
			if (entityInfo.isAddGeneratedValueFlag()) {
				String primaryIdName = entityInfo.getPrimaryIdName();
				boolean addGeneratedValueMarker = false;
				if (primaryIdName.equals(returnIdentifier)) {
					addGeneratedValueMarker = true;
				}
				if (addGeneratedValueMarker) {
					MarkerAnnotation matd = rewriter.getAST().newMarkerAnnotation();
					matd.setTypeName(rewriter.getAST().newSimpleName(JPAConst.ANNOTATION_GENERATED_VALUE));
					ListRewrite lrw = rewriter.getListRewrite(node, MethodDeclaration.MODIFIERS2_PROPERTY);
					lrw.insertFirst(matd, null);
				}
			}
			if (entityInfo.isAddPrimaryIdFlag()) {
				String primaryIdName = entityInfo.getPrimaryIdName();
				boolean addIdMarker = false;
				if (primaryIdName.equals(returnIdentifier)) {
					addIdMarker = true;
				}
				if (addIdMarker) {
					MarkerAnnotation matd = rewriter.getAST().newMarkerAnnotation();
					matd.setTypeName(rewriter.getAST().newSimpleName(JPAConst.ANNOTATION_ID));
					ListRewrite lrw = rewriter.getListRewrite(node, MethodDeclaration.MODIFIERS2_PROPERTY);
					lrw.insertFirst(matd, null);
				}
			}
			if (enableOptLock && entityInfo.isAddVersionFlag() && !entityInfo.hasVersionAnnotation()) {
				boolean addVersionMarker = false;
				if ("version".equals(returnIdentifier)) { //$NON-NLS-1$
					addVersionMarker = true;
				}
				if (addVersionMarker) {
					MarkerAnnotation matd = rewriter.getAST().newMarkerAnnotation();
					matd.setTypeName(rewriter.getAST().newSimpleName(JPAConst.ANNOTATION_VERSION));
					ListRewrite lrw = rewriter.getListRewrite(node, MethodDeclaration.MODIFIERS2_PROPERTY);
					lrw.insertFirst(matd, null);
				}
			}
		}
		if (type.isSimpleType() && (AllEntitiesProcessor.columnLength != defaultStrLength)) {
			SimpleType simpleType = (SimpleType)type;
			String typeName = simpleType.getName().getFullyQualifiedName();
			if ("java.lang.String".equals(typeName) || "String".equals(typeName)) { //$NON-NLS-1$ //$NON-NLS-2$
				String fieldId = returnIdentifier;
				RefColumnInfo rci = entityInfo.getRefColumnInfo(fieldId);
				if (rci == null || !rci.isExist()) {
					// if there is no @Column annotation - create new @Column annotation
					// with user defined default value length 
					NormalAnnotation natd = rewriter.getAST().newNormalAnnotation();
					natd.setTypeName(rewriter.getAST().newSimpleName(JPAConst.ANNOTATION_COLUMN));
					ListRewrite lrw = rewriter.getListRewrite(node, MethodDeclaration.MODIFIERS2_PROPERTY);
					lrw.insertFirst(natd, null);
					MemberValuePair mvp = rewriter.getAST().newMemberValuePair();
					mvp.setName(rewriter.getAST().newSimpleName("length")); //$NON-NLS-1$
					NumberLiteral nl = rewriter.getAST().newNumberLiteral(String.valueOf(defaultStrLength));
					mvp.setValue(nl);
					natd.values().add(mvp);
				}
			}
		}
		if (type.isSimpleType() || type.isParameterizedType() || type.isArrayType()) {
			String fieldId = ""; //$NON-NLS-1$
			RefType refType = RefType.UNDEF;
			boolean annotated = false;
			String fullyQualifiedName2 = ""; //$NON-NLS-1$
			fieldId = returnIdentifier;
			refType = entityInfo.getFieldIdRelValue(fieldId);
			annotated = entityInfo.getFieldIdAnnotatedValue(fieldId);
			fullyQualifiedName2 = entityInfo.getFieldIdFQNameValue(fieldId);
			Set<RefFieldInfo> setRFI = entityInfo.getRefFieldInfoSet(fullyQualifiedName2);
			if (!annotated && setRFI != null && isSimilarType(type, fullyQualifiedName2)) {
				RefEntityInfo rei = entityInfo.getFieldIdRefEntityInfo(fieldId);
				// try to process bidirectional relationships:
				// nRefType == JPAConst.ONE2ONE - OneToOne - the owning side corresponds
				//              to the side that contains the corresponding foreign key
				// nRefType == JPAConst.MANY2ONE - ManyToOne - owning side is always the "many" side
				// nRefType == JPAConst.MANY2MANY - ManyToMany bidirectional relationships 
				//              either side may be the owning side
				if (setRFI.size() > 1 && refType != RefType.MANY2ONE) {
					if (rei.mappedBy == null || rei.mappedBy == "") { //$NON-NLS-1$
						addSimpleMarkerAnnotation(node, JPAConst.getRefType(refType));
					}
					else {
						// give to the user information about selected mapping
						addComplexNormalAnnotation(node, JPAConst.getRefType(refType), rei);
					}
				}
				else if (refType == RefType.MANY2ONE || rei.mappedBy == null || rei.mappedBy == "") { //$NON-NLS-1$
					addSimpleMarkerAnnotation(node, JPAConst.getRefType(refType));
				}
				else {
					// in case of bidirectional OneToOne - mark both sides with mappedBy - 
					// user should select the right decision 
					addComplexNormalAnnotation(node, JPAConst.getRefType(refType), rei);
				}
			}
		}
		return true;
	}

	// simple type name check 
	public boolean isSimilarType(Type type, String fullyQualifiedName) {
		String typeName = null;
		if (type.isSimpleType()) {
			SimpleType st = (SimpleType)type;
			typeName = st.getName().getFullyQualifiedName();
		}
		else if (type.isArrayType()) {
			ArrayType at = (ArrayType)type;
			Type componentType = at;
			while (componentType.isArrayType()){
				componentType = ((ArrayType)componentType).getComponentType();
			}
			if (componentType.isSimpleType()) {
				SimpleType st = (SimpleType)componentType;
				typeName = st.getName().getFullyQualifiedName();
			}
		}
		if (typeName != null && fullyQualifiedName.indexOf(typeName) == -1) {
			return false;
		}
		return true;
	}
	
	public boolean addSimpleMarkerAnnotation(BodyDeclaration node, String name) {
		if (name == null || name.length() == 0) {
			return false;
		}
		MarkerAnnotation matd = rewriter.getAST().newMarkerAnnotation();
		matd.setTypeName(rewriter.getAST().newSimpleName(name));
		ListRewrite lrw = null;
		if (node instanceof FieldDeclaration) {
			lrw = rewriter.getListRewrite(node, FieldDeclaration.MODIFIERS2_PROPERTY);
		}
		else if (node instanceof MethodDeclaration) {
			lrw = rewriter.getListRewrite(node, MethodDeclaration.MODIFIERS2_PROPERTY);
		}
		if (lrw != null) {
			lrw.insertFirst(matd, null);
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public boolean addComplexNormalAnnotation(BodyDeclaration node, String name, RefEntityInfo rei) {
		if (name == null || name.length() == 0) {
			return false;
		}
		NormalAnnotation natd = rewriter.getAST().newNormalAnnotation();
		MemberValuePair mvp = null;
		if (rei.mappedBy != null && (rei.owner == OwnerType.YES || 
				rei.owner == OwnerType.UNDEF)) {
			mvp = rewriter.getAST().newMemberValuePair();
			mvp.setName(rewriter.getAST().newSimpleName("mappedBy")); //$NON-NLS-1$
			StringLiteral sl = rewriter.getAST().newStringLiteral();
			sl.setLiteralValue(rei.mappedBy);
			mvp.setValue(sl);
		}
		natd.setTypeName(rewriter.getAST().newSimpleName(name));
		if (mvp != null) {
			natd.values().add(mvp);
		}
		NormalAnnotation natd2 = null;
		/** /
		if (rei.owner == OwnerType.NO) {
			natd2 = rewriter.getAST().newNormalAnnotation();
			natd2.setTypeName(rewriter.getAST().newSimpleName(JPAConst.ANNOTATION_JOINCOLUMN));
			mvp = null;
			String fullyQualifiedName2 = rei.fullyQualifiedName;
			EntityInfo entryInfo2 = entities.get(fullyQualifiedName2);
			if (entryInfo2 != null) {
				mvp = rewriter.getAST().newMemberValuePair();
				mvp.setName(rewriter.getAST().newSimpleName("name")); //$NON-NLS-1$
				StringLiteral sl = rewriter.getAST().newStringLiteral();
				sl.setLiteralValue(entryInfo2.getPrimaryIdName());
				mvp.setValue(sl);
			}
			if (mvp != null) {
				natd2.values().add(mvp);
			}
		}
		/**/
		ListRewrite lrw = null;
		if (node instanceof FieldDeclaration) {
			lrw = rewriter.getListRewrite(node, FieldDeclaration.MODIFIERS2_PROPERTY);
		}
		else if (node instanceof MethodDeclaration) {
			lrw = rewriter.getListRewrite(node, MethodDeclaration.MODIFIERS2_PROPERTY);
		}
		if (lrw != null) {
			if (natd2 != null) {
				lrw.insertFirst(natd2, null);
			}
			lrw.insertFirst(natd, null);
		}
		return true;
	}

	public AnnotStyle getAnnotationStyle() {
		return annotationStyle;
	}

	public void setAnnotationStyle(AnnotStyle annotationStyle) {
		this.annotationStyle = annotationStyle;
	}
	
	public int getDefaultStrLength() {
		return defaultStrLength;
	}

	public void setDefaultStrLength(int defaultStrLength) {
		this.defaultStrLength = defaultStrLength;
	}

	public boolean getEnableOptLock() {
		return enableOptLock;
	}

	public void setEnableOptLock(boolean enableOptLock) {
		this.enableOptLock = enableOptLock;
	}
}
