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
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.EntityInfo;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.JPAConst;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.RefEntityInfo;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.RefFieldInfo;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.RefType;

/**
 * 
 * 
 * @author Vitali
 */
public class ProcessEntityInfo extends ASTVisitor {

	protected EntityInfo entityInfo;
	protected ASTRewrite rewriter;
	
	public void setEntityInfo(EntityInfo entityInfo) {
		this.entityInfo = entityInfo;
	}
	
	public void setASTRewrite(ASTRewrite rewriter) {
		this.rewriter = rewriter;
	}

	public boolean visit(CompilationUnit node) {
		for (int i = 0; i < JPAConst.ALL_IMPORTS.size(); i++) {
			String tmp = JPAConst.ALL_IMPORTS.get(i);
			if (entityInfo.needImport(tmp)) {
				addImport(node, tmp);
			}
		}
		return true;
	}

	public boolean addImport(CompilationUnit node, String importDeclaration) {
		String[] importDeclarations =  importDeclaration.split("\\."); //$NON-NLS-1$
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
		// TODO: insert import declaration in the proper place
		// prefer alphabetic order and package separation
		lrw.insertLast(importDecl, null);
		return true;
	}

	public boolean visit(TypeDeclaration node) {
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
			List list = lrw.getOriginalList();
			MethodDeclaration insertBeforeNode = null;
			Iterator it = list.iterator();
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
		return true;		
	}
	
	public boolean visit(FieldDeclaration node) {
		if (node.getType().isSimpleType()) {
			if (entityInfo.isAddGeneratedValueFlag()) {
				String primaryIdName = entityInfo.getPrimaryIdName();
				Iterator itVarNames = node.fragments().iterator();
				boolean addGeneratedValueMarker = false;
				while (itVarNames.hasNext()) {
					VariableDeclarationFragment var = (VariableDeclarationFragment)itVarNames.next();
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
				Iterator itVarNames = node.fragments().iterator();
				boolean addIdMarker = false;
				while (itVarNames.hasNext()) {
					VariableDeclarationFragment var = (VariableDeclarationFragment)itVarNames.next();
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
		}
		if (node.getType().isSimpleType() || node.getType().isParameterizedType()) {
			Iterator itVarNames = node.fragments().iterator();
			String fieldId = ""; //$NON-NLS-1$
			RefType refType = RefType.UNDEF;
			boolean annotated = false;
			String fullyQualifiedName2 = ""; //$NON-NLS-1$
			while (itVarNames.hasNext()) {
				VariableDeclarationFragment var = (VariableDeclarationFragment)itVarNames.next();
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
			if (!annotated && setRFI != null) {
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
	
	public boolean addSimpleMarkerAnnotation(FieldDeclaration node, String name) {
		if (name == null || name.length() == 0) {
			return false;
		}
		MarkerAnnotation matd = rewriter.getAST().newMarkerAnnotation();
		matd.setTypeName(rewriter.getAST().newSimpleName(name));
		ListRewrite lrw = rewriter.getListRewrite(node, FieldDeclaration.MODIFIERS2_PROPERTY);
		lrw.insertFirst(matd, null);
		return true;
	}
	
	public boolean addComplexNormalAnnotation(FieldDeclaration node, String name, RefEntityInfo rei) {
		if (name == null || name.length() == 0) {
			return false;
		}
		NormalAnnotation natd = rewriter.getAST().newNormalAnnotation();
		MemberValuePair mvp = null;
		if (rei.mappedBy != null) {
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
		ListRewrite lrw = rewriter.getListRewrite(node, FieldDeclaration.MODIFIERS2_PROPERTY);
		lrw.insertFirst(natd, null);
		return true;
	}

}
