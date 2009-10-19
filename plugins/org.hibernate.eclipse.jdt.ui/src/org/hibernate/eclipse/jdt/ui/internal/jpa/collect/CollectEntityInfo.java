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
package org.hibernate.eclipse.jdt.ui.internal.jpa.collect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.WildcardType;
import org.eclipse.jdt.internal.core.BinaryType;
import org.eclipse.jdt.internal.core.SourceType;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.EntityInfo;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.JPAConst;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.RefType;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.Utils;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.EntityInfo.FieldGetterType;

/**
 * Visitor to collect information about JPA entity.
 * 
 * @author Vitali
 */
@SuppressWarnings({ "unchecked", "restriction" })
public class CollectEntityInfo extends ASTVisitor {

	/**
	 * storage of collected info
	 */
	protected EntityInfo entityInfo = new EntityInfo();

	protected String fullyQualifiedName = null;
	
	public CollectEntityInfo(String fullyQualifiedName) {
		this.fullyQualifiedName = fullyQualifiedName;
		entityInfo.setFullyQualifiedName(fullyQualifiedName);
	}
	
	public EntityInfo getEntityInfo() {
		return entityInfo;
	}

	public boolean visit(CompilationUnit node) {
		if (node.getProblems().length > 0) {//this includes warnings too
			entityInfo.setCompilerProblemsFlag(true);
		}
		return true;
	}

	public boolean visit(MarkerAnnotation node) {
		return processAnnotation(node, null);
	}
	
	public boolean visit(NormalAnnotation node) {
		// try to extract mapping prompts
		String mappedBy = null;
		Iterator<?> it = node.values().iterator();
		while (it.hasNext()) {
			MemberValuePair mvp = (MemberValuePair)it.next();
			if ("mappedBy".equals(mvp.getName().toString())) { //$NON-NLS-1$
				mappedBy = mvp.getValue().toString().replaceAll("\"", ""); //$NON-NLS-1$ //$NON-NLS-2$
				break;
			}
		}
		return processAnnotation(node, mappedBy);
	}

	public CompilationUnit getCUFromFieldMethod(ASTNode node) {
		CompilationUnit cu = null;
		ASTNode astNode = node.getParent();
		if (astNode instanceof FieldDeclaration || 
				astNode instanceof MethodDeclaration) {
			astNode = astNode.getParent();
			if (astNode instanceof TypeDeclaration) {
				astNode = astNode.getParent();
				if (astNode instanceof CompilationUnit) {
					cu = (CompilationUnit)astNode;
				}
			}
		}
		return cu;
	}

	public CompilationUnit getCUFromTypeDeclaration(ASTNode node) {
		CompilationUnit cu = null;
		ASTNode astNode = node.getParent();
		if (astNode instanceof TypeDeclaration) {
			astNode = astNode.getParent();
			if (astNode instanceof CompilationUnit) {
				cu = (CompilationUnit)astNode;
			}
		}
		return cu;
	}
	
	public boolean processAnnotation(Annotation node, String mappedBy) {
		String fullyQualifiedName = node.getTypeName().getFullyQualifiedName();
		if (JPAConst.isAnnotationEntity(fullyQualifiedName)) {
			ITypeBinding tb = node.resolveTypeBinding();
			CompilationUnit cu = getCUFromTypeDeclaration(node);
			if (cu != null) {
				if (tb == null) {
					entityInfo.addRequiredImport(JPAConst.IMPORT_ENTITY);
				}
				entityInfo.setAddEntityFlag(false);
			}
		}
		else if (JPAConst.isAnnotationId(fullyQualifiedName)) {
			ITypeBinding tb = node.resolveTypeBinding();
			CompilationUnit cu = getCUFromFieldMethod(node);
			if (cu != null) {
				if (tb == null) {
					entityInfo.addRequiredImport(JPAConst.IMPORT_ID);
				}
				entityInfo.setAddPrimaryIdFlag(false);
			}
		}
		else if (JPAConst.isAnnotationGeneratedValue(fullyQualifiedName)) {
			ITypeBinding tb = node.resolveTypeBinding();
			CompilationUnit cu = getCUFromFieldMethod(node);
			if (cu != null) {
				if (tb == null) {
					entityInfo.addRequiredImport(JPAConst.IMPORT_GENERATED_VALUE);
				}
				entityInfo.setAddGeneratedValueFlag(false);
			}
		}
		else if (JPAConst.isAnnotationOne2One(fullyQualifiedName)) {
			updateAnnotationRelInfo(node, mappedBy, fullyQualifiedName,
				RefType.ONE2ONE, JPAConst.ANNOTATION_ONE2ONE, JPAConst.IMPORT_ONE2ONE);
		}
		else if (JPAConst.isAnnotationOne2Many(fullyQualifiedName)) {
			updateAnnotationRelInfo(node, mappedBy, fullyQualifiedName,
				RefType.ONE2MANY, JPAConst.ANNOTATION_ONE2MANY, JPAConst.IMPORT_ONE2MANY);
		}
		else if (JPAConst.isAnnotationMany2One(fullyQualifiedName)) {
			updateAnnotationRelInfo(node, mappedBy, fullyQualifiedName,
				RefType.MANY2ONE, JPAConst.ANNOTATION_MANY2ONE, JPAConst.IMPORT_MANY2ONE);
		}
		else if (JPAConst.isAnnotationMany2Many(fullyQualifiedName)) {
			updateAnnotationRelInfo(node, mappedBy, fullyQualifiedName,
				RefType.MANY2MANY, JPAConst.ANNOTATION_MANY2MANY, JPAConst.IMPORT_MANY2MANY);
		}
		else if (JPAConst.isAnnotationMappedSuperclass(fullyQualifiedName)) {
			ITypeBinding tb = node.resolveTypeBinding();
			CompilationUnit cu = getCUFromTypeDeclaration(node);
			if (cu != null) {
				if (tb == null) {
					entityInfo.addRequiredImport(JPAConst.IMPORT_MAPPEDSUPERCLASS);
				}
				entityInfo.setAddEntityFlag(false);
				entityInfo.setAddMappedSuperclassFlag(false);
				entityInfo.setHasMappedSuperclassAnnotation(true);
			}
		}
		else if (JPAConst.isAnnotationVersion(fullyQualifiedName)) {
			ITypeBinding tb = node.resolveTypeBinding();
			CompilationUnit cu = getCUFromFieldMethod(node);
			if (cu != null) {
				if (tb == null) {
					entityInfo.addRequiredImport(JPAConst.IMPORT_VERSION);
				}
				entityInfo.setHasVersionAnnotation(true);
			}
		}
		else if (JPAConst.isAnnotationColumn(fullyQualifiedName) && node instanceof NormalAnnotation) {
			updateAnnotationColumn((NormalAnnotation)node, mappedBy, fullyQualifiedName);
		}
		return true;
	}
	
	public void updateAnnotationColumn(NormalAnnotation node, String mappedBy, String fullyQualifiedName) {
		ITypeBinding tb = node.resolveTypeBinding();
		CompilationUnit cu = null;
		ASTNode astNode = node.getParent();
		if (astNode instanceof FieldDeclaration) {
			FieldDeclaration fd = (FieldDeclaration)astNode;
			Iterator<?> itVarNames = fd.fragments().iterator();
			while (itVarNames.hasNext()) {
				VariableDeclarationFragment var = (VariableDeclarationFragment)itVarNames.next();
				String name = var.getName().getIdentifier();
				entityInfo.updateAnnotationColumn(name, node, true);
			}
			cu = getCUFromTypeDeclaration(node);
		}
		else if (astNode instanceof MethodDeclaration) {
			MethodDeclaration md = (MethodDeclaration)astNode;
			if (md.getName().getIdentifier().startsWith("get")) { //$NON-NLS-1$
				// the getter - process it
				String name = getReturnIdentifier(md);
				// process it like FieldDeclaration
				entityInfo.updateAnnotationColumn(name, node, true);
				cu = getCUFromFieldMethod(node);
			}
			else {
				// ignore others
			}
		}
		if (cu != null) {
			if (tb == null) {
				entityInfo.addRequiredImport(JPAConst.IMPORT_COLUMN);
			}
		}
	}
	
	public void updateAnnotationRelInfo(Annotation node, String mappedBy, String fullyQualifiedName,
			RefType type, String annNameShort, String annNameFull) {
		ITypeBinding tb = node.resolveTypeBinding();
		CompilationUnit cu = null;
		ASTNode astNode = node.getParent();
		if (astNode instanceof FieldDeclaration) {
			FieldDeclaration fd = (FieldDeclaration)astNode;
			Iterator<?> itVarNames = fd.fragments().iterator();
			while (itVarNames.hasNext()) {
				VariableDeclarationFragment var = (VariableDeclarationFragment)itVarNames.next();
				String name = var.getName().getIdentifier();
				entityInfo.updateReference(name, true, type, mappedBy,
						0 != annNameShort.compareTo(fullyQualifiedName), true);
			}
			cu = getCUFromTypeDeclaration(node);
		}
		else if (astNode instanceof MethodDeclaration) {
			MethodDeclaration md = (MethodDeclaration)astNode;
			if (md.getName().getIdentifier().startsWith("get")) { //$NON-NLS-1$
				// the getter - process it
				String name = getReturnIdentifier(md);
				// process it like FieldDeclaration
				entityInfo.updateReference(name, true, type, mappedBy,
						0 != annNameShort.compareTo(fullyQualifiedName), false);
				cu = getCUFromTypeDeclaration(node);
			}
			else {
				// ignore others
			}
		}
		if (cu != null) {
			if (tb == null) {
				entityInfo.addRequiredImport(annNameFull);
			}
		}
	}

	public boolean visit(ImportDeclaration node) {
		String name = node.getName().getFullyQualifiedName();
		for (int i = 0; i < JPAConst.ALL_IMPORTS.size(); i++) {
			String tmp = JPAConst.ALL_IMPORTS.get(i);
			if (tmp.compareTo(name) == 0) {
				entityInfo.addExistingImport(name);
				break;
			}
		}
		return true;
	}

	public boolean visit(Modifier node) {
		if (node.isAbstract()) {
			entityInfo.setAbstractFlag(true);
		}
		return true;
	}

	
	public boolean visit(TypeDeclaration node) {
		ITypeBinding typeBinding = node.resolveBinding();
		String nodeName = typeBinding == null ? null : typeBinding.getBinaryName();
		if (fullyQualifiedName == null || !fullyQualifiedName.equalsIgnoreCase(nodeName)) {
			return false;
		}
		boolean isAbstruct = entityInfo.isAbstractFlag() || 
			Modifier.isAbstract(node.getModifiers()) || node.isInterface();
		entityInfo.setAbstractFlag(isAbstruct);
		if (isAbstruct) {
			entityInfo.setAddEntityFlag(false);
			entityInfo.setAddMappedSuperclassFlag(true);
		}
		entityInfo.setInterfaceFlag(node.isInterface());
		Type superType = node.getSuperclassType();
		if (superType != null) {
			ITypeBinding tb = superType.resolveBinding();
			if (tb != null) {
				String entityFullyQualifiedName = ""; //$NON-NLS-1$
				if (tb.getJavaElement() instanceof SourceType) {
					SourceType sourceT = (SourceType)tb.getJavaElement();
					try {
						entityFullyQualifiedName = sourceT.getFullyQualifiedParameterizedName();
					}
					catch (JavaModelException e) {
						HibernateConsolePlugin.getDefault().logErrorMessage("JavaModelException: ", e); //$NON-NLS-1$
					}
				}
				entityInfo.addDependency(entityFullyQualifiedName);
				entityInfo.setFullyQualifiedParentName(entityFullyQualifiedName);
			}
		}
		List<?> superInterfaces = node.superInterfaceTypes();
		Iterator<?> it = superInterfaces.iterator();
		while (it.hasNext()) {
			Object obj = it.next();
			if (obj instanceof SimpleType) {
				//TODO process interfaces
				SimpleType st = (SimpleType)obj;
				String fullyQualifiedName = st.getName().getFullyQualifiedName();
				if (JPAConst.IMPORT_SERIALIZABLE.compareTo(fullyQualifiedName) == 0) {
					entityInfo.setAddSerializableInterfaceFlag(false);
				}
				else if (JPAConst.ANNOTATION_SERIALIZABLE.compareTo(fullyQualifiedName) == 0) {
					entityInfo.setAddSerializableInterfaceFlag(false);
					entityInfo.addRequiredImport(JPAConst.IMPORT_SERIALIZABLE);
				}
			}
		}
		node.resolveBinding();
		return true;
	}

	public static String getReturnIdentifier(MethodDeclaration node) {
		String res = null;
		if (node.getBody() == null) {
			return res;
		}	
		List<?> bodyStatemants = node.getBody().statements();
		Iterator<?> it = bodyStatemants.iterator();
		for ( ; it.hasNext(); ) {
			Object obj = it.next();
			if (obj instanceof ReturnStatement) {
				ReturnStatement ret_statement = (ReturnStatement)obj;
				obj = ret_statement.getExpression();
				if (obj instanceof SimpleName) {
					SimpleName sn = (SimpleName)obj;
					res = sn.getIdentifier();
				}
				else if (obj instanceof FieldAccess) {
					FieldAccess fa = (FieldAccess)obj;
					SimpleName sn = fa.getName();
					res = sn.getIdentifier();
				}
				break;
			}
		}
		return res;
	}

	public boolean visit(MethodDeclaration node) {
		if (node.getName().getFullyQualifiedName().compareTo(entityInfo.getName()) == 0 || node.isConstructor()) {
			// this is constructor declaration - process it separately
			entityInfo.setImplicitConstructorFlag(false);
			if (node.parameters().size() == 0) {
				entityInfo.setDefaultConstructorFlag(true);
			}
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
		// ?) has it an annotation? - updateAnnotationRelInfo
		// 4) try to define its return type
		Type type = node.getReturnType2();
		// 5) try to define name
		String returnIdentifier = getReturnIdentifier(node);
		List<String> list = new ArrayList<String>();
		if (returnIdentifier != null){
			list.add(returnIdentifier);
		}
		else {
			//this need to do not lost primiry id
			//and add references for interface
			if (entityInfo.isInterfaceFlag()){
				String probablyFieldName = Utils.getFieldNameByGetter(node);
				list.add(probablyFieldName);
			}
		}
		// process it as a field declaration
		boolean res = processFieldOrGetter(type, list, false);
		return res;
	}
	
	public boolean visit(FieldDeclaration node) {
		Type type = node.getType();
		List<String> list = new ArrayList<String>();
		Iterator<VariableDeclarationFragment> itVarNames = node.fragments().iterator();
		while (itVarNames.hasNext()) {
			VariableDeclarationFragment var = itVarNames.next();
			String name = var.getName().getIdentifier();
			list.add(name);
		}
		boolean res = processFieldOrGetter(type, list, true);
		return res;
	}

	public FieldGetterType updateFieldGetter(FieldGetterType fieldGetter, boolean fieldFlag) {
		if (fieldGetter == FieldGetterType.FIELD) {
			if (!fieldFlag) {
				fieldGetter = FieldGetterType.FIELD_GETTER;
			}
		}
		else if (fieldGetter == FieldGetterType.GETTER) {
			if (fieldFlag) {
				fieldGetter = FieldGetterType.FIELD_GETTER;
			}
		}
		else if (fieldGetter == FieldGetterType.UNDEF) {
			fieldGetter = fieldFlag ? FieldGetterType.FIELD : FieldGetterType.GETTER;
		}
		return fieldGetter;
	}
	
	public boolean processFieldOrGetter(Type type, List<String> list, boolean fieldFlag) {
		if (type == null) {
			return false;
		}
		if (type.isPrimitiveType()) {
			PrimitiveType pt = (PrimitiveType)type;
			if (!pt.getPrimitiveTypeCode().equals(PrimitiveType.BOOLEAN)) {
				// this is candidate for primary id
				Iterator<String> itVarNames = list.iterator();
				while (itVarNames.hasNext()) {
					String name = itVarNames.next();
					if ("version".equalsIgnoreCase(name)) { //$NON-NLS-1$
						FieldGetterType versionFieldGetter = 
							updateFieldGetter(entityInfo.getVersionFieldGetter(), fieldFlag);
						entityInfo.setVersionFieldGetter(versionFieldGetter);
					}
					else {
						entityInfo.addPrimaryIdCandidate(name);
					}
				}
			}
		}
		else if (type.isSimpleType()) {
			SimpleType st = (SimpleType)type;
			ITypeBinding tb = st.resolveBinding();
			if (tb != null) {
				String entityFullyQualifiedName = ""; //$NON-NLS-1$
				if (tb.getJavaElement() instanceof SourceType) {
					SourceType sourceT = (SourceType)tb.getJavaElement();
					try {
						entityFullyQualifiedName = sourceT.getFullyQualifiedParameterizedName();
					}
					catch (JavaModelException e) {
						HibernateConsolePlugin.getDefault().logErrorMessage("JavaModelException: ", e); //$NON-NLS-1$
					}
					entityInfo.addDependency(entityFullyQualifiedName);
					Iterator<String> itVarNames = list.iterator();
					while (itVarNames.hasNext()) {
						String name = itVarNames.next();
						entityInfo.addReference(name, entityFullyQualifiedName, RefType.MANY2ONE);
					}
				}
				else if (tb.getJavaElement() instanceof BinaryType) {
					ITypeBinding tbParent = tb.getTypeDeclaration().getSuperclass();
					if (tbParent != null) {
						if ("java.lang.Number".equals(tbParent.getBinaryName())) { //$NON-NLS-1$
							// this is candidate for primary id
							Iterator<String> itVarNames = list.iterator();
							while (itVarNames.hasNext()) {
								String name = itVarNames.next();
								if ("version".equalsIgnoreCase(name)) { //$NON-NLS-1$
									FieldGetterType versionFieldGetter = 
										updateFieldGetter(entityInfo.getVersionFieldGetter(), fieldFlag);
									entityInfo.setVersionFieldGetter(versionFieldGetter);
								}
								else {
									entityInfo.addPrimaryIdCandidate(name);
								}
							}
						}
						else if ("java.util.Date".equals(tbParent.getBinaryName())) { //$NON-NLS-1$
							// this is candidate for version
							Iterator<String> itVarNames = list.iterator();
							while (itVarNames.hasNext()) {
								String name = itVarNames.next();
								if ("version".equalsIgnoreCase(name)) { //$NON-NLS-1$
									FieldGetterType versionFieldGetter = 
										updateFieldGetter(entityInfo.getVersionFieldGetter(), fieldFlag);
									entityInfo.setVersionFieldGetter(versionFieldGetter);
								}
							}
						}
					}
					if ("java.lang.String".equals(tb.getBinaryName())) { //$NON-NLS-1$
						Iterator<String> itVarNames = list.iterator();
						while (itVarNames.hasNext()) {
							String name = itVarNames.next();
							entityInfo.updateAnnotationColumn(name, null, false);
							entityInfo.addPrimaryIdCandidate(name);
						}
					}
				}
			}
		}
		else if (type.isArrayType()) {
			ArrayType at = (ArrayType)type;
			Type componentType = at;
			while (componentType.isArrayType()){
				componentType = ((ArrayType)componentType).getComponentType();
			}
			ITypeBinding tb = componentType.resolveBinding();
			if (tb != null) {
				if (tb.getJavaElement() instanceof SourceType) {
					String entityFullyQualifiedName = ""; //$NON-NLS-1$
					SourceType sourceT = (SourceType)tb.getJavaElement();
					try {
						entityFullyQualifiedName = sourceT.getFullyQualifiedParameterizedName();
					}
					catch (JavaModelException e) {
						HibernateConsolePlugin.getDefault().logErrorMessage("JavaModelException: ", e); //$NON-NLS-1$
					}
					entityInfo.addDependency(entityFullyQualifiedName);
					Iterator<String> itVarNames = list.iterator();
					while (itVarNames.hasNext()) {
						String name = itVarNames.next();
						entityInfo.addReference(name, entityFullyQualifiedName, RefType.ONE2MANY);
					}
				}
			}
		}
		else if (type.isParameterizedType()) {
			ParameterizedType pt = (ParameterizedType)type;
			Type typeP = pt.getType();
			ITypeBinding tb = typeP.resolveBinding();
			if (tb != null) {
				ITypeBinding[] interfaces = Utils.getAllInterfaces(tb);
				String fullyQualifiedNameTypeName = ""; //$NON-NLS-1$
				if (Utils.isImplementInterface(interfaces, "java.util.Collection")){//$NON-NLS-1$
					fullyQualifiedNameTypeName = "java.util.Collection";//$NON-NLS-1$
				}
				if (Utils.isImplementInterface(interfaces, "java.util.Map")){//$NON-NLS-1$
					fullyQualifiedNameTypeName = "java.util.Map";//$NON-NLS-1$
				}
				/*for (int i = 0; i < interfaces.length; i++) {
					if (interfaces[i].getJavaElement() instanceof BinaryType) {
						BinaryType binaryT = (BinaryType)interfaces[i].getJavaElement();
						String tmp = binaryT.getFullyQualifiedName('.');
						if (0 == "java.util.Collection".compareTo(tmp)) { //$NON-NLS-1$
							fullyQualifiedNameTypeName = tmp;
							break;
						}
					}
				}*/
				if (fullyQualifiedNameTypeName.length() > 0) {
					Iterator<Type> typeArgsIt = pt.typeArguments().iterator();
					while (typeArgsIt.hasNext()) {
						typeP = typeArgsIt.next();
						tb = typeP.resolveBinding();
						String entityFullyQualifiedName = ""; //$NON-NLS-1$
						if (tb.getJavaElement() instanceof SourceType) {
							SourceType sourceT = (SourceType)tb.getJavaElement();
							try {
								entityFullyQualifiedName = sourceT.getFullyQualifiedParameterizedName();
							} catch (JavaModelException e) {
								HibernateConsolePlugin.getDefault().logErrorMessage("JavaModelException: ", e); //$NON-NLS-1$
							}
							entityInfo.addDependency(entityFullyQualifiedName);
							Iterator<String> itVarNames = list.iterator();
							while (itVarNames.hasNext()) {
								String name = itVarNames.next();
								entityInfo.addReference(name, entityFullyQualifiedName, RefType.ONE2MANY);
							}
						}
					}
				}
			}
		}
		else if (type.isQualifiedType()) {
			QualifiedType qt = (QualifiedType)type;
			@SuppressWarnings("unused")
			ITypeBinding tb = qt.resolveBinding();
		}
		else if (type.isWildcardType()) {
			WildcardType wt = (WildcardType)type;
			@SuppressWarnings("unused")
			ITypeBinding tb = wt.resolveBinding();
		}
		return true;
	}
}
