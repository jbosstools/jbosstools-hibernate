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
package org.hibernate.eclipse.jdt.ui.internal.jpa.common;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.NormalAnnotation;

/**
 * Service place to collect information about entity class.
 * Then this info has been transformed according annotations
 * creation assumption.
 * 
 * @author Vitali
 */
public class EntityInfo {
	
	/*
	 * fully qualified entity name
	 */
	protected String fullyQualifiedName = ""; //$NON-NLS-1$
	/*
	 * fully qualified parent name
	 */
	protected String fullyQualifiedParentName = ""; //$NON-NLS-1$
	/*
	 * true in case of compiler problems
	 */
	protected boolean compilerProblemsFlag = false;
	/*
	 * true in case of implicit constructor
	 */
	protected boolean implicitConstructorFlag = true;
	/*
	 * true in case of default constructor
	 */
	protected boolean defaultConstructorFlag = false;
	/*
	 * if true - "@Entity" annotation should be added
	 */
	protected boolean addEntityFlag = true;
	/*
	 * if true - "@MappedSuperclass" annotation should be added
	 */
	protected boolean addMappedSuperclassFlag = false;
	/*
	 * if true - "@MappedSuperclass" exist for the entity
	 */
	protected boolean hasMappedSuperclassAnnotation = false;
	/*
	 * existing imports set
	 */
	protected Set<String> setExistingImports = new TreeSet<String>();
	/*
	 * required imports set
	 */
	protected Set<String> setRequiredImports = new TreeSet<String>();
	/*
	 * if true - "implements java.io.Serializable" or
	 * "implements Serializable" should be added
	 */
	protected boolean addSerializableInterfaceFlag = true;
	/*
	 * if true - this is abstract class declaration
	 */
	protected boolean isAbstractFlag = false;
	/*
	 * if true - this is interface declaration
	 */
	protected boolean isInterfaceFlag = false;
	/*
	 * paths to entity java files which has a reference from current entity
	 */
	protected Set<String> dependences = new TreeSet<String>();
	/*
	 * reference to primary id property
	 */
	protected String primaryIdName = ""; //$NON-NLS-1$
	/*
	 * if true - add primary id marker for primaryIdName 
	 */
	protected boolean addPrimaryIdFlag = true;
	/*
	 * if true - add generated value marker for primaryIdName 
	 */
	protected boolean addGeneratedValueFlag = true;
	/*
	 * if true - add version marker for "version" field 
	 */
	protected boolean addVersionFlag = false;
	//
	public enum FieldGetterType {
		UNDEF(0),
		FIELD(1),
		GETTER(2),
		FIELD_GETTER(3);
		
		FieldGetterType(int id) {
	        this.id = id;
	    }

	    private int id;

	    public int getId() {
	        return id;
	    }

	    public static String getClassName() {
	        return FieldGetterType.class.getName();
	    }
	}
	/*
	 * if true - entity has "version" field 
	 */
	protected FieldGetterType versionFieldGetter = FieldGetterType.UNDEF;
	/*
	 * if true - there is a property with @Version
	 */
	protected boolean hasVersionAnnotation = false;
	/*
	 * define relations between entities
	 * field id -> RefEntityInfo
	 */
	protected Map<String, RefEntityInfo> references = new TreeMap<String, RefEntityInfo>();
	/*
	 * store declared column information for particular field 
	 * field id -> RefColumnInfo
	 */
	protected Map<String, RefColumnInfo> columns = new TreeMap<String, RefColumnInfo>();
	/*
	 * fully qualified entity name -> Set<RefFieldInfo>
	 * this is generated from references map for easy information get
	 */
	protected Map<String, Set<RefFieldInfo>> mapRefFieldInfo = null;
	/*
	 */
	protected Set<String> primaryIdCandidates = new TreeSet<String>();
	/*
	 */
	protected int fromVariableCounter = 0;
	/*
	 */
	protected int fromMethodCounter = 0;
	
	public void generateRefFieldInfoMap() {
		mapRefFieldInfo = new TreeMap<String, Set<RefFieldInfo>>();
		Iterator<Map.Entry<String, RefEntityInfo>> referencesIt = 
			getReferences().entrySet().iterator();
		while (referencesIt.hasNext()) {
			Map.Entry<String, RefEntityInfo> entry = referencesIt.next();
			RefEntityInfo refEntityInfo = entry.getValue();
			Set<RefFieldInfo> fieldInfoSet = null;
			if (mapRefFieldInfo.containsKey(refEntityInfo.fullyQualifiedName)) {
				fieldInfoSet = mapRefFieldInfo.get(refEntityInfo.fullyQualifiedName);
			}
			else {
				fieldInfoSet = new TreeSet<RefFieldInfo>();
			}
			RefFieldInfo fieldInfo = new RefFieldInfo(entry.getKey(), refEntityInfo.refType);
			fieldInfoSet.add(fieldInfo);
			mapRefFieldInfo.put(refEntityInfo.fullyQualifiedName, fieldInfoSet);
		}
		referencesIt = null;
	}

	public void adjustPrimaryId(EntityInfo parentEI) {
		if (parentEI != null) {
			String parentPrimaryIdName = parentEI.getPrimaryIdName();
			if (parentPrimaryIdName != null && parentPrimaryIdName.length() > 0) {
				primaryIdName = ""; //$NON-NLS-1$
			}
		}
		if (isAddPrimaryIdFlag()) {
			setAddPrimaryIdFlag(primaryIdName.length() > 0);
			if (isAddPrimaryIdFlag()) {
				addRequiredImport(JPAConst.IMPORT_ID);
			}
		}
		if (isAddGeneratedValueFlag()) {
			setAddGeneratedValueFlag(primaryIdName.length() > 0);
			if (isAddGeneratedValueFlag()) {
				addRequiredImport(JPAConst.IMPORT_GENERATED_VALUE);
			}
		}
	}
	
	public RefType getFieldIdRelValue(String fieldId) {
		if (references == null || fieldId == null || !references.containsKey(fieldId)) {
			return RefType.UNDEF;
		}
		return references.get(fieldId).refType;
	}
	
	public boolean getFieldIdAnnotatedValue(String fieldId) {
		if (references == null || fieldId == null || !references.containsKey(fieldId)) {
			return false;
		}
		return references.get(fieldId).annotated;
	}
	
	public String getFieldIdFQNameValue(String fieldId) {
		if (references == null || fieldId == null || !references.containsKey(fieldId)) {
			return ""; //$NON-NLS-1$
		}
		return references.get(fieldId).fullyQualifiedName;
	}
	
	public RefEntityInfo getFieldIdRefEntityInfo(String fieldId) {
		if (references == null || fieldId == null || !references.containsKey(fieldId)) {
			return null;
		}
		return references.get(fieldId);
	}
	
	public void adjustParameters() {
		if (isImplicitConstructorFlag() == true || isDefaultConstructorFlag() == true) {
			setAddSerializableInterfaceFlag(false);
		}
		else {
			addRequiredImport(JPAConst.IMPORT_SERIALIZABLE);
		}
		if (isAddEntityFlag()) {
			addRequiredImport(JPAConst.IMPORT_ENTITY);
		}
		else {
			removeRequiredImport(JPAConst.IMPORT_ENTITY);
		}
		if (isAddMappedSuperclassFlag()) {
			addRequiredImport(JPAConst.IMPORT_MAPPEDSUPERCLASS);
		}
		else {
			removeRequiredImport(JPAConst.IMPORT_MAPPEDSUPERCLASS);
		}
		updateVersionImport(FieldGetterType.UNDEF != getVersionFieldGetter());
		updateColumnAnnotationImport(false);
		Iterator<Map.Entry<String, RefEntityInfo>> referencesIt = 
			getReferences().entrySet().iterator();
		while (referencesIt.hasNext()) {
			Map.Entry<String, RefEntityInfo> entry = referencesIt.next();
			RefEntityInfo refEntityInfo = entry.getValue();
			if (refEntityInfo.owner == OwnerType.NO) {
				addRequiredImport(JPAConst.IMPORT_JOINCOLUMN);
			}
		}
		// try to intellectually get primary id
		primaryIdName = null;
		String entityName = getName().toLowerCase();
		Iterator<String> it = primaryIdCandidates.iterator();
		while (it.hasNext()) {
			String name = it.next();
			String check = name.toLowerCase();
			if ("id".equalsIgnoreCase(check) //$NON-NLS-1$
					|| "identity".equalsIgnoreCase(check)) { //$NON-NLS-1$
				primaryIdName = name;
				break;
			}
			else if (check.indexOf("id") != -1 && check.indexOf(entityName) != -1) { //$NON-NLS-1$
				if (primaryIdName == null) {
					primaryIdName = name;
				}
				else if (primaryIdName.toLowerCase().indexOf(entityName) == -1 ||
						primaryIdName.length() > name.length()) {
					primaryIdName = name;
				}
			}
			else if (check.indexOf("id") != -1) { //$NON-NLS-1$
				if (primaryIdName == null) {
					primaryIdName = name;
				}
				else if (primaryIdName.toLowerCase().indexOf(entityName) == -1 && 
						primaryIdName.length() > name.length()) {
					primaryIdName = name;
				}
			}
		}
		if (primaryIdName == null) {
			primaryIdName = ""; //$NON-NLS-1$
		}
	}
	
	public void updateVersionImport(boolean includeFlag) {
		if (includeFlag) {
			addRequiredImport(JPAConst.IMPORT_VERSION);
		}
		else {
			removeRequiredImport(JPAConst.IMPORT_VERSION);
		}
	}
	
	public void updateColumnAnnotationImport(boolean nonDefault) {
		if (isNonColumnAnnotatedStringField() && nonDefault) {
			addRequiredImport(JPAConst.IMPORT_COLUMN);
		}
		else {
			removeRequiredImport(JPAConst.IMPORT_COLUMN);
		}
	}

	public String getName() {
		String[] arr = fullyQualifiedName.split("\\."); //$NON-NLS-1$
		if (arr.length > 0) {
			return arr[arr.length - 1];
		}
		return ""; //$NON-NLS-1$
	}

	public String getFullyQualifiedName() {
		return fullyQualifiedName;
	}

	public void setFullyQualifiedName(String fullyQualifiedName) {
		this.fullyQualifiedName = fullyQualifiedName;
	}

	public String getFullyQualifiedParentName() {
		return fullyQualifiedParentName;
	}

	public void setFullyQualifiedParentName(String fullyQualifiedParentName) {
		this.fullyQualifiedParentName = fullyQualifiedParentName;
	}

	public boolean isCompilerProblemsFlag() {
		return compilerProblemsFlag;
	}

	public void setCompilerProblemsFlag(boolean compilerProblemsFlag) {
		this.compilerProblemsFlag = compilerProblemsFlag;
	}

	public boolean isImplicitConstructorFlag() {
		return implicitConstructorFlag;
	}

	public void setImplicitConstructorFlag(boolean implicitConstructorFlag) {
		this.implicitConstructorFlag = implicitConstructorFlag;
	}

	public boolean isDefaultConstructorFlag() {
		return defaultConstructorFlag;
	}

	public void setDefaultConstructorFlag(boolean defaultConstructorFlag) {
		this.defaultConstructorFlag = defaultConstructorFlag;
	}

	public boolean isAddEntityFlag() {
		return addEntityFlag;
	}

	public void setAddEntityFlag(boolean addEntityFlag) {
		this.addEntityFlag = addEntityFlag;
	}

	public boolean isAddMappedSuperclassFlag() {
		return addMappedSuperclassFlag;
	}

	public void setAddMappedSuperclassFlag(boolean addMappedSuperclassFlag) {
		this.addMappedSuperclassFlag = addMappedSuperclassFlag;
	}

	public boolean hasMappedSuperclassAnnotation() {
		return hasMappedSuperclassAnnotation;
	}

	public void setHasMappedSuperclassAnnotation(boolean hasMappedSuperclassAnnotation) {
		this.hasMappedSuperclassAnnotation = hasMappedSuperclassAnnotation;
	}

	public boolean isAddSerializableInterfaceFlag() {
		return addSerializableInterfaceFlag;
	}

	public void setAddSerializableInterfaceFlag(boolean addSerializableInterfaceFlag) {
		this.addSerializableInterfaceFlag = addSerializableInterfaceFlag;
	}

	public boolean isAbstractFlag() {
		return isAbstractFlag;
	}

	public void setAbstractFlag(boolean isAbstractFlag) {
		this.isAbstractFlag = isAbstractFlag;
	}

	public boolean isInterfaceFlag() {
		return isInterfaceFlag;
	}

	public void setInterfaceFlag(boolean isInterfaceFlag) {
		this.isInterfaceFlag = isInterfaceFlag;
	}

	public String getPrimaryIdName() {
		return primaryIdName;
	}

	public void setPrimaryIdName(String primaryIdName) {
		this.primaryIdName = primaryIdName;
	}

	public Map<String, RefEntityInfo> getReferences() {
		return references;
	}

	public void addReference(String fieldId, String fullyQualifiedName, RefType refType) {
		if (references == null || fieldId == null) {
			return;
		}
		if (references.containsKey(fieldId)) {
			RefEntityInfo rei = references.get(fieldId);
			if (rei != null) {
				if (rei.fullyQualifiedName != null) {
					assert(rei.fullyQualifiedName.equals(fullyQualifiedName));
				}
				if (rei.refType != null) {
					assert(rei.refType.equals(refType));
				}
			}
			return;
		}
		references.put(fieldId, new RefEntityInfo(fullyQualifiedName, refType));
	}

	public void updateReference(String fieldId, boolean annotated, RefType refType, String mappedBy, 
			boolean resolvedAnnotationName, boolean fromVariable) {
		if (references == null || fieldId == null || !references.containsKey(fieldId)) {
			return;
		}
		RefEntityInfo rei = references.get(fieldId);
		if (rei != null) {
			if (rei.updateCounter == 0) {
				rei.annotated = annotated;
				rei.refType = refType;
				rei.mappedBy = mappedBy;
				if (rei.mappedBy != null) {
					rei.owner = OwnerType.YES;
				}
				rei.resolvedAnnotationName = resolvedAnnotationName;
			}
			else {
				// TODO: possible conflicting info - think about it
				assert(false);
			}
			rei.updateCounter++;
		}
		if (fromVariable) {
			fromVariableCounter++;
		}
		else {
			fromMethodCounter++;
		}
	}

	public void updateAnnotationColumn(String fieldId, NormalAnnotation node, boolean exist) {
		if (columns == null || fieldId == null) {
			return;
		}
		RefColumnInfo rci = columns.get(fieldId);
		if (rci == null) {
			rci = new RefColumnInfo(fieldId);
		}
		if (!rci.isExist()) {
			rci.setExist(exist);
		}
		if (node != null) {
			Map<String, Expression> rciValues = rci.getValues();
			Iterator<?> it = node.values().iterator();
			while (it.hasNext()) {
				Object obj = it.next();
				if (obj instanceof MemberValuePair) {
					MemberValuePair mvp = (MemberValuePair)obj;
					rciValues.put(mvp.getName().getIdentifier(), mvp.getValue());
				}
			}
		}
		columns.put(fieldId, rci);
	}

	public RefColumnInfo getRefColumnInfo(String fieldId) {
		return columns.get(fieldId);
	}

	/**
	 * returns true in case of there is String field which is not
	 * annotated with @Column
	 */
	public boolean isNonColumnAnnotatedStringField() {
		boolean res = false;
		Iterator<Map.Entry<String, RefColumnInfo>> it = columns.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, RefColumnInfo> entry = it.next();
			if (!entry.getValue().isExist()) {
				res = true;
				break;
			}
		}
		return res;
	}

	public Set<RefFieldInfo> getRefFieldInfoSet(String fullyQualifiedName) {
		if (mapRefFieldInfo == null) {
			return null;
		}
		return mapRefFieldInfo.get(fullyQualifiedName);
	}

	public Iterator<String> getDependences() {
		return dependences.iterator();
	}

	public void addDependency(String entityFullyQualifiedName) {
		dependences.add(entityFullyQualifiedName);
	}

	public void addPrimaryIdCandidate(String name) {
		if (name != null && name.length() > 0) {
			primaryIdCandidates.add(name);
		}
	}

	public boolean isAddPrimaryIdFlag() {
		return addPrimaryIdFlag;
	}

	public void setAddPrimaryIdFlag(boolean addPrimaryIdFlag) {
		this.addPrimaryIdFlag = addPrimaryIdFlag;
	}

	public boolean isAddGeneratedValueFlag() {
		return addGeneratedValueFlag;
	}

	public void setAddGeneratedValueFlag(boolean addGeneratedValueFlag) {
		this.addGeneratedValueFlag = addGeneratedValueFlag;
	}

	public boolean isAddVersionFlag() {
		return addVersionFlag;
	}

	public void setAddVersionFlag(boolean addVersionFlag) {
		this.addVersionFlag = addVersionFlag;
	}

	public FieldGetterType getVersionFieldGetter() {
		return versionFieldGetter;
	}

	public void setVersionFieldGetter(FieldGetterType versionFieldGetter) {
		this.versionFieldGetter = versionFieldGetter;
	}

	public boolean hasVersionAnnotation() {
		return hasVersionAnnotation;
	}

	public void setHasVersionAnnotation(boolean hasVersionAnnotation) {
		this.hasVersionAnnotation = hasVersionAnnotation;
	}

	public void addExistingImport(String existingImport) {
		setExistingImports.add(existingImport);
	}

	public void removeExistingImport(String existingImport) {
		setExistingImports.remove(existingImport);
	}

	public void collectExistingImport(Set<String> setExistingImports) {
		setExistingImports.addAll(this.setExistingImports);
	}

	public void addRequiredImport(String requiredImport) {
		setRequiredImports.add(requiredImport);
	}

	public void removeRequiredImport(String requiredImport) {
		setRequiredImports.remove(requiredImport);
	}

	public void collectRequiredImport(Set<String> setRequiredImports) {
		setRequiredImports.addAll(this.setRequiredImports);
	}

	public boolean needImport(String checkImport) {
		return (!setExistingImports.contains(checkImport) && setRequiredImports.contains(checkImport));
	}

	public int getFromVariableCounter() {
		return fromVariableCounter;
	}

	public int getFromMethodCounter() {
		return fromMethodCounter;
	}
	
	public String toString() {
		return getFullyQualifiedName();
	}

	public int hashCode() {
		return getFullyQualifiedName().hashCode();
	}
	
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof EntityInfo)) {
			return false;
		}
		EntityInfo ei = (EntityInfo)obj;
		if (getFullyQualifiedName().equals(ei.getFullyQualifiedName()) &&
			getFullyQualifiedParentName().equals(ei.getFullyQualifiedParentName())) {
			return true;
		}
		return false;
	}
}
