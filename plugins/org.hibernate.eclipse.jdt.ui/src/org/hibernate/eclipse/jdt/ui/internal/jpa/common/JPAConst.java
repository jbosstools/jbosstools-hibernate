package org.hibernate.eclipse.jdt.ui.internal.jpa.common;

import java.util.Vector;

public class JPAConst {

	static public String IMPORT_SERIALIZABLE = "java.io.Serializable"; //$NON-NLS-1$
	static public String IMPORT_ENTITY = "javax.persistence.Entity"; //$NON-NLS-1$
	static public String IMPORT_GENERATED_VALUE = "javax.persistence.GeneratedValue"; //$NON-NLS-1$
	static public String IMPORT_ID = "javax.persistence.Id"; //$NON-NLS-1$
	static public String IMPORT_ONE2ONE = "javax.persistence.OneToOne"; //$NON-NLS-1$
	static public String IMPORT_ONE2MANY = "javax.persistence.OneToMany"; //$NON-NLS-1$
	static public String IMPORT_MANY2ONE = "javax.persistence.ManyToOne"; //$NON-NLS-1$
	static public String IMPORT_MANY2MANY = "javax.persistence.ManyToMany"; //$NON-NLS-1$
	
	static public String ANNOTATION_SERIALIZABLE = "Serializable"; //$NON-NLS-1$
	static public String ANNOTATION_ENTITY = "Entity"; //$NON-NLS-1$
	static public String ANNOTATION_GENERATED_VALUE = "GeneratedValue"; //$NON-NLS-1$
	static public String ANNOTATION_ID = "Id"; //$NON-NLS-1$
	static public String ANNOTATION_ONE2ONE = "OneToOne"; //$NON-NLS-1$
	static public String ANNOTATION_ONE2MANY = "OneToMany"; //$NON-NLS-1$
	static public String ANNOTATION_MANY2ONE = "ManyToOne"; //$NON-NLS-1$
	static public String ANNOTATION_MANY2MANY = "ManyToMany"; //$NON-NLS-1$
	
	static public Vector<String> ALL_IMPORTS = new Vector<String>();
	
	static {
		//ALL_IMPORTS.add(IMPORT_SERIALIZABLE);
		ALL_IMPORTS.add(IMPORT_ENTITY);
		ALL_IMPORTS.add(IMPORT_GENERATED_VALUE);
		ALL_IMPORTS.add(IMPORT_ID);
		ALL_IMPORTS.add(IMPORT_ONE2ONE);
		ALL_IMPORTS.add(IMPORT_ONE2MANY);
		ALL_IMPORTS.add(IMPORT_MANY2ONE);
		ALL_IMPORTS.add(IMPORT_MANY2MANY);
	}

	static public String getRefType(RefType refType) {
		if (refType == RefType.ONE2ONE) {
			return ANNOTATION_ONE2ONE;
		}
		else if (refType == RefType.ONE2MANY) {
			return ANNOTATION_ONE2MANY;
		}
		else if (refType == RefType.MANY2ONE) {
			return ANNOTATION_MANY2ONE;
		}
		else if (refType == RefType.MANY2MANY) {
			return ANNOTATION_MANY2MANY;
		}
		return ""; //$NON-NLS-1$
	}

	static public boolean isAnnotationEntity(String fullyQualifiedName) {
		if (ANNOTATION_ENTITY.compareTo(fullyQualifiedName) == 0 ||
			IMPORT_ENTITY.compareTo(fullyQualifiedName) == 0) {
			return true;
		}
		return false;
	}

	static public boolean isAnnotationId(String fullyQualifiedName) {
		if (ANNOTATION_ID.compareTo(fullyQualifiedName) == 0 ||
			IMPORT_ID.compareTo(fullyQualifiedName) == 0) {
			return true;
		}
		return false;
	}

	static public boolean isAnnotationGeneratedValue(String fullyQualifiedName) {
		if (ANNOTATION_GENERATED_VALUE.compareTo(fullyQualifiedName) == 0 ||
			IMPORT_GENERATED_VALUE.compareTo(fullyQualifiedName) == 0) {
			return true;
		}
		return false;
	}

	static public boolean isAnnotationOne2One(String fullyQualifiedName) {
		if (ANNOTATION_ONE2ONE.compareTo(fullyQualifiedName) == 0 ||
			IMPORT_ONE2ONE.compareTo(fullyQualifiedName) == 0) {
			return true;
		}
		return false;
	}

	static public boolean isAnnotationOne2Many(String fullyQualifiedName) {
		if (ANNOTATION_ONE2MANY.compareTo(fullyQualifiedName) == 0 ||
			IMPORT_ONE2MANY.compareTo(fullyQualifiedName) == 0) {
			return true;
		}
		return false;
	}

	static public boolean isAnnotationMany2One(String fullyQualifiedName) {
		if (ANNOTATION_MANY2ONE.compareTo(fullyQualifiedName) == 0 ||
			IMPORT_MANY2ONE.compareTo(fullyQualifiedName) == 0) {
			return true;
		}
		return false;
	}

	static public boolean isAnnotationMany2Many(String fullyQualifiedName) {
		if (ANNOTATION_MANY2MANY.compareTo(fullyQualifiedName) == 0 ||
			IMPORT_MANY2MANY.compareTo(fullyQualifiedName) == 0) {
			return true;
		}
		return false;
	}
}
