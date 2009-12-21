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
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.EntityInfo;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.JPAConst;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.OwnerType;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.RefEntityInfo;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.RefFieldInfo;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.RefType;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.Utils;
import org.hibernate.eclipse.jdt.ui.internal.jpa.process.AnnotStyle;

/**
 * Collect information about JPA entity and it's dependences.
 * 
 * @author Vitali Yemialyanchyk
 */
public class AllEntitiesInfoCollector {

	/**
	 * map: 
	 * javaProjectName + "/" + fullyQualifiedName entity name -> EntityInfo
	 * this collection of processed entities 
	 */
	protected Map<String, EntityInfo> mapCUs_Info = new TreeMap<String, EntityInfo>();
	/**
	 * annotation style preference
	 */
	protected AnnotStyle annotationStylePreference = AnnotStyle.FIELDS;

	public void initCollector() {
		// clear collection
		mapCUs_Info.clear();
	}
	
	/**
	 * pair of 2 particular fields from 2 particular entities
	 */
	static protected class ProcessItem {
		// field names
		public String fieldId = null;
		public String fieldId2 = null;
		// information about field from both entities
		public RefEntityInfo refEntityInfo = null;
		public RefEntityInfo refEntityInfo2 = null;
	}
	
	/**
	 * abstract interface for connection of entities
	 */
	protected abstract class IConnector {
		
		/**
		 * object for processing
		 */
		protected ProcessItem pi;

		public void setProcessItem(ProcessItem processItem) {
			this.pi = processItem;
		}
		public abstract boolean updateRelation();
	}

	/**
	 * responsible for enumeration all entities pairs which
	 * succeed in recognize
	 */
	protected class EntityProcessor {
		
		protected IConnector connector;

		public void setConnector(IConnector connector) {
			this.connector = connector;
		}
		
		/**
		 * enumerate function
		 */
		public void enumEntityPairs() {
			Iterator<Map.Entry<String, EntityInfo>> it = mapCUs_Info.entrySet().iterator();
			ProcessItem pi = new ProcessItem();
			while (it.hasNext()) {
				Map.Entry<String, EntityInfo> entry = it.next();
				// entry.getKey() - fully qualified name
				// entry.getValue() - EntityInfo
				EntityInfo entryInfo = entry.getValue();
				String fullyQualifiedName = entryInfo.getFullyQualifiedName();
				String javaProjectName = entryInfo.getJavaProjectName();
				assert(entry.getKey().equals(javaProjectName + "/" + fullyQualifiedName)); //$NON-NLS-1$
				// get references map:
				// * field id -> RefEntityInfo
				Iterator<Map.Entry<String, RefEntityInfo>> referencesIt = 
					entryInfo.getReferences().entrySet().iterator();
				while (referencesIt.hasNext()) {
					Map.Entry<String, RefEntityInfo> entry2 = referencesIt.next();
					// entry2.getKey() - field id
					// entry2.getValue() - RefEntityInfo
					pi.fieldId = entry2.getKey();
					pi.refEntityInfo = entry2.getValue();
					String fullyQualifiedName2 = pi.refEntityInfo.fullyQualifiedName;
					EntityInfo entryInfo2 = mapCUs_Info.get(javaProjectName + "/" + fullyQualifiedName2); //$NON-NLS-1$
					assert(fullyQualifiedName2.equals(entryInfo2.getFullyQualifiedName()));
					if (entryInfo2 != null && pi.refEntityInfo != null) {
						pi.refEntityInfo2 = null;
						pi.fieldId2 = null;
						Set<RefFieldInfo> setRefEntityInfo = entryInfo2.getRefFieldInfoSet(fullyQualifiedName);
						if (setRefEntityInfo != null) {
							if (setRefEntityInfo.size() == 1) {
								Iterator<RefFieldInfo> itTmp = setRefEntityInfo.iterator();
								RefFieldInfo rfi = itTmp.next();
								pi.fieldId2 = rfi.fieldId;
								pi.refEntityInfo2 = entryInfo2.getFieldIdRefEntityInfo(pi.fieldId2);
							}
							else if (setRefEntityInfo.size() > 1) {
								// this case of complex decision - omit this,
								// give other entities opportunity to solve this.
								// in case of no solution - user should define this himself
								pi.refEntityInfo2 = null;
							}
						}
						if (connector != null) {
							connector.setProcessItem(pi);
							connector.updateRelation();
						}
					}
				}
			}
		}
	}
	
	public void updateOwner(ProcessItem pi) {
		if (pi.refEntityInfo.refType == RefType.ONE2ONE) {
			//pi.refEntityInfo.owner = OwnerType.UNDEF;
			//pi.refEntityInfo2.owner = OwnerType.UNDEF;
			if ((pi.refEntityInfo.owner == OwnerType.UNDEF && 
					pi.refEntityInfo2.owner == OwnerType.UNDEF) ||
					(pi.refEntityInfo.owner == OwnerType.YES &&
					pi.refEntityInfo2.owner == OwnerType.YES) ||
					(pi.refEntityInfo.owner == OwnerType.NO &&
					pi.refEntityInfo2.owner == OwnerType.NO))
			{
				// this is the ambiguous case
				// TODO: this is temporary solution for GA
				// select owner in lexicographical order
				if (pi.refEntityInfo.fullyQualifiedName.compareTo(
						pi.refEntityInfo2.fullyQualifiedName) > 0) {
					pi.refEntityInfo.owner = OwnerType.YES;
					pi.refEntityInfo2.owner = OwnerType.NO;
				}
				else {
					pi.refEntityInfo.owner = OwnerType.NO;
					pi.refEntityInfo2.owner = OwnerType.YES;
				}
			}
			else if (pi.refEntityInfo.owner == OwnerType.UNDEF || 
					pi.refEntityInfo2.owner == OwnerType.UNDEF) {
				if (pi.refEntityInfo.owner == OwnerType.YES) {
					pi.refEntityInfo2.owner = OwnerType.NO;
				}
				else if (pi.refEntityInfo.owner == OwnerType.NO) {
					pi.refEntityInfo2.owner = OwnerType.YES;
				}
				else if (pi.refEntityInfo2.owner == OwnerType.YES) {
					pi.refEntityInfo.owner = OwnerType.NO;
				}
				else if (pi.refEntityInfo2.owner == OwnerType.NO) {
					pi.refEntityInfo.owner = OwnerType.YES;
				}
			}
		}
		else if (pi.refEntityInfo.refType == RefType.ONE2MANY) {
			pi.refEntityInfo.owner = OwnerType.YES;
			pi.refEntityInfo2.owner = OwnerType.NO;
		}
		else if (pi.refEntityInfo.refType == RefType.MANY2ONE) {
			pi.refEntityInfo.owner = OwnerType.NO;
			pi.refEntityInfo2.owner = OwnerType.YES;
		}
		else if (pi.refEntityInfo.refType == RefType.MANY2MANY) {
			//pi.refEntityInfo.owner = OwnerType.UNDEF;
			//pi.refEntityInfo2.owner = OwnerType.UNDEF;
			if ((pi.refEntityInfo.owner == OwnerType.UNDEF && 
					pi.refEntityInfo2.owner == OwnerType.UNDEF) ||
					(pi.refEntityInfo.owner == OwnerType.YES &&
					pi.refEntityInfo2.owner == OwnerType.YES) ||
					(pi.refEntityInfo.owner == OwnerType.NO &&
					pi.refEntityInfo2.owner == OwnerType.NO))
			{
				// this is the ambiguous case
				// TODO: this is temporary solution for GA
				// select owner in lexicographical order
				if (pi.refEntityInfo.fullyQualifiedName.compareTo(
						pi.refEntityInfo2.fullyQualifiedName) > 0) {
					pi.refEntityInfo.owner = OwnerType.YES;
					pi.refEntityInfo2.owner = OwnerType.NO;
				}
				else {
					pi.refEntityInfo.owner = OwnerType.NO;
					pi.refEntityInfo2.owner = OwnerType.YES;
				}
			}
			else if (pi.refEntityInfo.owner == OwnerType.UNDEF || 
					pi.refEntityInfo2.owner == OwnerType.UNDEF) {
				if (pi.refEntityInfo.owner == OwnerType.YES) {
					pi.refEntityInfo2.owner = OwnerType.NO;
				}
				else if (pi.refEntityInfo.owner == OwnerType.NO) {
					pi.refEntityInfo2.owner = OwnerType.YES;
				}
				else if (pi.refEntityInfo2.owner == OwnerType.YES) {
					pi.refEntityInfo.owner = OwnerType.NO;
				}
				else if (pi.refEntityInfo2.owner == OwnerType.NO) {
					pi.refEntityInfo.owner = OwnerType.YES;
				}
			}
		}
	}

	/**
	 * responsible for enumeration all entities pairs and
	 * setup relations between single candidate for several items case 
	 */
	protected class EntitySingleCandidateResolver {
		
		/**
		 * enumerate function
		 */
		public void enumEntityPairs() {
			Iterator<Map.Entry<String, EntityInfo>> it = mapCUs_Info.entrySet().iterator();
			ProcessItem pi = new ProcessItem();
			while (it.hasNext()) {
				Map.Entry<String, EntityInfo> entry = it.next();
				// entry.getKey() - fully qualified name
				// entry.getValue() - EntityInfo
				EntityInfo entryInfo = entry.getValue();
				String fullyQualifiedName = entryInfo.getFullyQualifiedName();
				String javaProjectName = entryInfo.getJavaProjectName();
				assert(entry.getKey().equals(javaProjectName + "/" + fullyQualifiedName)); //$NON-NLS-1$
				// get references map:
				// * field id -> RefEntityInfo
				Iterator<Map.Entry<String, RefEntityInfo>> referencesIt = 
					entryInfo.getReferences().entrySet().iterator();
				while (referencesIt.hasNext()) {
					Map.Entry<String, RefEntityInfo> entry2 = referencesIt.next();
					// entry2.getKey() - field id
					// entry2.getValue() - RefEntityInfo
					pi.fieldId = entry2.getKey();
					pi.refEntityInfo = entry2.getValue();
					String fullyQualifiedName2 = pi.refEntityInfo.fullyQualifiedName;
					EntityInfo entryInfo2 = mapCUs_Info.get(javaProjectName + "/" + fullyQualifiedName2); //$NON-NLS-1$
					assert(fullyQualifiedName2.equals(entryInfo2.getFullyQualifiedName()));
					if (entryInfo2 != null && pi.refEntityInfo != null) {
						pi.refEntityInfo2 = null;
						pi.fieldId2 = null;
						Set<RefFieldInfo> setRefEntityInfo = entryInfo2.getRefFieldInfoSet(fullyQualifiedName);
						if (setRefEntityInfo != null && setRefEntityInfo.size() > 1) {
							// this case of complex decision - but if there is 1 candidate
							// it is possible to select this candidate automatically
							// in case of no solution - user should define this himself
							// try to find 1 suitable candidate
							RefType suitableRefType = RefType.UNDEF;
							if (pi.refEntityInfo.refType == RefType.ONE2ONE) {
								suitableRefType = RefType.ONE2ONE;
							}
							else if (pi.refEntityInfo.refType == RefType.ONE2MANY) {
								suitableRefType = RefType.MANY2ONE;
							}
							else if (pi.refEntityInfo.refType == RefType.MANY2ONE) {
								suitableRefType = RefType.ONE2MANY;
							}
							else if (pi.refEntityInfo.refType == RefType.MANY2MANY) {
								suitableRefType = RefType.MANY2MANY;
							}
							RefFieldInfo rfiSingleCandidat = null;
							Iterator<RefFieldInfo> itTmp = setRefEntityInfo.iterator();
							while (itTmp.hasNext()) {
								RefFieldInfo rfi = itTmp.next();
								if (rfi.refType == suitableRefType) {
									if (rfiSingleCandidat == null) {
										rfiSingleCandidat = rfi;
									}
									else {
										// there are a least 2 candidates
										break;
									}
								}
							}
							if (!itTmp.hasNext() && rfiSingleCandidat != null) {
								pi.fieldId2 = rfiSingleCandidat.fieldId;
								pi.refEntityInfo2 = entryInfo2.getFieldIdRefEntityInfo(pi.fieldId2);
								pi.refEntityInfo.mappedBy = pi.fieldId2;
								pi.refEntityInfo2.mappedBy = pi.fieldId;
								updateOwner(pi);
							}
						}
					}
				}
			}
		}
	}

	public boolean hasMappedSuperclassVersion(String javaProjectName, String parentName) {
		if (parentName == null) {
			return false;
		}
		EntityInfo entryInfoParent = mapCUs_Info.get(javaProjectName + "/" + parentName); //$NON-NLS-1$
		if (entryInfoParent == null) {
			return false;
		}
		if (entryInfoParent.isAddMappedSuperclassFlag() || 
			entryInfoParent.hasMappedSuperclassAnnotation()) {
			return true;
		}
		return false;
	}

	/**
	 * process all entities pairs iteratively:
	 * firstly process pairs with more information about and
	 * pairs with small information about - process in last order
	 */
	public void resolveRelations() {
		Iterator<Map.Entry<String, EntityInfo>> it = null;
		// resolve parent/child relations
		// in the case if a child has a @MappedSuperclass parent with version property
		// we shouldn't add version property to the child
		it = mapCUs_Info.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, EntityInfo> entry = it.next();
			EntityInfo entityInfo = entry.getValue();
			String javaProjectName = entityInfo.getJavaProjectName();
			String parentName = entityInfo.getFullyQualifiedParentName();
			if (hasMappedSuperclassVersion(javaProjectName, parentName)) {
				entityInfo.setAddVersionFlag(false);
			}
			else {
				entityInfo.setAddVersionFlag(true);
			}
			entityInfo.updateVersionImport(entityInfo.isAddVersionFlag());
		}
		//
		// resolve connection relations
		int fromVariableCounter = 0;
		int fromMethodCounter = 0;
		// generate RefFieldInfoMap (for simple process)
		it = mapCUs_Info.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, EntityInfo> entry = it.next();
			EntityInfo entryInfo = entry.getValue();
			entryInfo.generateRefFieldInfoMap();
			fromVariableCounter += entryInfo.getFromVariableCounter();
			fromMethodCounter += entryInfo.getFromMethodCounter();
		}
		if (fromVariableCounter >= fromMethodCounter) {
			annotationStylePreference = AnnotStyle.FIELDS;
		}
		else {
			annotationStylePreference = AnnotStyle.GETTERS;
		}
		// update relations
		EntityProcessor ep = new EntityProcessor();
		// 0)
		// process the user prompts
		IConnector promptsConnector = new IConnector() {
			public boolean updateRelation() {
				if (pi == null) {
					return false;
				}
				if (pi.refEntityInfo == null || pi.refEntityInfo2 == null) {
					return false;
				}
				int hasPrompt = 0; // no user prompt
				// - use it as prompting from the user
				if ((pi.fieldId != null && pi.fieldId.equals(pi.refEntityInfo2.mappedBy))) {
					hasPrompt |= 1;
				}
				if ((pi.fieldId2 != null && pi.fieldId2.equals(pi.refEntityInfo.mappedBy))) {
					hasPrompt |= 2;
				}
				if (hasPrompt != 0) {
					// remember: in case of incorrect user prompts - we proceed his suggestions
					if (hasPrompt == 1) {
						if (pi.refEntityInfo2.refType == RefType.ONE2ONE) {
							pi.refEntityInfo.refType = RefType.ONE2ONE;
							pi.refEntityInfo.mappedBy = pi.fieldId2;
							pi.refEntityInfo2.refType = RefType.ONE2ONE;
							pi.refEntityInfo2.mappedBy = pi.fieldId;
							updateOwner(pi);
						}
						else if (pi.refEntityInfo2.refType == RefType.ONE2MANY) {
							pi.refEntityInfo.refType = RefType.MANY2ONE;
							pi.refEntityInfo.mappedBy = pi.fieldId2;
							pi.refEntityInfo2.refType = RefType.ONE2MANY;
							pi.refEntityInfo2.mappedBy = pi.fieldId;
							updateOwner(pi);
						}
						else if (pi.refEntityInfo2.refType == RefType.MANY2ONE) {
							pi.refEntityInfo.refType = RefType.ONE2MANY;
							pi.refEntityInfo.mappedBy = pi.fieldId2;
							pi.refEntityInfo2.refType = RefType.MANY2ONE;
							pi.refEntityInfo2.mappedBy = pi.fieldId;
							updateOwner(pi);
						}
						else if (pi.refEntityInfo2.refType == RefType.MANY2MANY) {
							pi.refEntityInfo.refType = RefType.MANY2MANY;
							pi.refEntityInfo.mappedBy = pi.fieldId2;
							pi.refEntityInfo2.refType = RefType.MANY2MANY;
							pi.refEntityInfo2.mappedBy = pi.fieldId;
							updateOwner(pi);
						}
					}
					else if (hasPrompt == 2) {
						if (pi.refEntityInfo.refType == RefType.ONE2ONE) {
							pi.refEntityInfo.refType = RefType.ONE2ONE;
							pi.refEntityInfo.mappedBy = pi.fieldId;
							pi.refEntityInfo2.refType = RefType.ONE2ONE;
							pi.refEntityInfo2.mappedBy = pi.fieldId2;
							updateOwner(pi);
						}
						else if (pi.refEntityInfo.refType == RefType.ONE2MANY) {
							pi.refEntityInfo.refType = RefType.ONE2MANY;
							pi.refEntityInfo.mappedBy = pi.fieldId;
							pi.refEntityInfo2.refType = RefType.MANY2ONE;
							pi.refEntityInfo2.mappedBy = pi.fieldId2;
							updateOwner(pi);
						}
						else if (pi.refEntityInfo.refType == RefType.MANY2ONE) {
							pi.refEntityInfo.refType = RefType.MANY2ONE;
							pi.refEntityInfo.mappedBy = pi.fieldId;
							pi.refEntityInfo2.refType = RefType.ONE2MANY;
							pi.refEntityInfo2.mappedBy = pi.fieldId2;
							updateOwner(pi);
						}
						else if (pi.refEntityInfo.refType == RefType.MANY2MANY) {
							pi.refEntityInfo.refType = RefType.MANY2MANY;
							pi.refEntityInfo.mappedBy = pi.fieldId;
							pi.refEntityInfo2.refType = RefType.MANY2MANY;
							pi.refEntityInfo2.mappedBy = pi.fieldId2;
							updateOwner(pi);
						}
					}
					//if (hasPrompt == 3) - this is case where we get prompt from both sides -
					// and it is possible this is not correct info... so try to adjust it.
					if (pi.refEntityInfo.refType == RefType.ONE2ONE) {
						if (pi.refEntityInfo2.refType == RefType.ONE2ONE) {
							pi.refEntityInfo.refType = RefType.ONE2ONE;
							pi.refEntityInfo.mappedBy = pi.fieldId2;
							pi.refEntityInfo2.refType = RefType.ONE2ONE;
							pi.refEntityInfo2.mappedBy = pi.fieldId;
							updateOwner(pi);
						}
						else if (pi.refEntityInfo2.refType == RefType.ONE2MANY) {
							pi.refEntityInfo.refType = RefType.MANY2ONE;
							pi.refEntityInfo.mappedBy = pi.fieldId2;
							pi.refEntityInfo2.refType = RefType.ONE2MANY;
							pi.refEntityInfo2.mappedBy = pi.fieldId;
							updateOwner(pi);
						}
					}
					else if (pi.refEntityInfo.refType == RefType.ONE2MANY) {
						if (pi.refEntityInfo2.refType == RefType.ONE2ONE) {
							pi.refEntityInfo.refType = RefType.ONE2MANY;
							pi.refEntityInfo.mappedBy = pi.fieldId2;
							pi.refEntityInfo2.refType = RefType.MANY2ONE;
							pi.refEntityInfo2.mappedBy = pi.fieldId;
							updateOwner(pi);
						}
						else if (pi.refEntityInfo2.refType == RefType.ONE2MANY) {
							pi.refEntityInfo.refType = RefType.MANY2MANY;
							pi.refEntityInfo.mappedBy = pi.fieldId2;
							pi.refEntityInfo2.refType = RefType.MANY2MANY;
							pi.refEntityInfo2.mappedBy = pi.fieldId;
							updateOwner(pi);
						}
					}
				}
				return true;
			}
		};
		ep.setConnector(promptsConnector);
		ep.enumEntityPairs();
		// prefer other mapping type to ManyToMany, so
		// 1)
		// first - try to assign other relations
		IConnector simpleRelConnector = new IConnector() {
			public boolean updateRelation() {
				if (pi == null) {
					return false;
				}
				if (pi.refEntityInfo == null || pi.refEntityInfo2 == null) {
					return false;
				}
				if (pi.refEntityInfo.mappedBy == null && pi.refEntityInfo2.mappedBy == null) {
					if (pi.refEntityInfo.refType == RefType.ONE2ONE) {
						if (pi.refEntityInfo2.refType == RefType.ONE2ONE) {
							pi.refEntityInfo.refType = RefType.ONE2ONE;
							pi.refEntityInfo.mappedBy = pi.fieldId2;
							pi.refEntityInfo2.refType = RefType.ONE2ONE;
							pi.refEntityInfo2.mappedBy = pi.fieldId;
							updateOwner(pi);
						}
						else if (pi.refEntityInfo2.refType == RefType.ONE2MANY) {
							pi.refEntityInfo.refType = RefType.MANY2ONE;
							pi.refEntityInfo.mappedBy = pi.fieldId2;
							pi.refEntityInfo2.refType = RefType.ONE2MANY;
							pi.refEntityInfo2.mappedBy = pi.fieldId;
							updateOwner(pi);
						}
					}
					else if (pi.refEntityInfo.refType == RefType.ONE2MANY) {
						if (pi.refEntityInfo2.refType == RefType.ONE2ONE) {
							pi.refEntityInfo.refType = RefType.ONE2MANY;
							pi.refEntityInfo.mappedBy = pi.fieldId2;
							pi.refEntityInfo2.refType = RefType.MANY2ONE;
							pi.refEntityInfo2.mappedBy = pi.fieldId;
							updateOwner(pi);
						}
						else if (pi.refEntityInfo2.refType == RefType.MANY2ONE) {
							pi.refEntityInfo.refType = RefType.ONE2MANY;
							pi.refEntityInfo.mappedBy = pi.fieldId2;
							pi.refEntityInfo2.refType = RefType.MANY2ONE;
							pi.refEntityInfo2.mappedBy = pi.fieldId;
							updateOwner(pi);
						}
					}
					else if (pi.refEntityInfo.refType == RefType.MANY2ONE) {
						if (pi.refEntityInfo2.refType == RefType.ONE2MANY) {
							pi.refEntityInfo.refType = RefType.MANY2ONE;
							pi.refEntityInfo.mappedBy = pi.fieldId2;
							pi.refEntityInfo2.refType = RefType.ONE2MANY;
							pi.refEntityInfo2.mappedBy = pi.fieldId;
							updateOwner(pi);
						}
						else if (pi.refEntityInfo2.refType == RefType.MANY2ONE) {
							pi.refEntityInfo.refType = RefType.ONE2ONE;
							pi.refEntityInfo.mappedBy = pi.fieldId2;
							pi.refEntityInfo2.refType = RefType.ONE2ONE;
							pi.refEntityInfo2.mappedBy = pi.fieldId;
							updateOwner(pi);
						}
					}
				}
				return true;
			}
		};
		ep.setConnector(simpleRelConnector);
		ep.enumEntityPairs();
		// 2)
		// second - try to assign - ManyToMany
		// remember - here prefer other mapping type to ManyToMany
		IConnector m2mRelConnector = new IConnector() {
			public boolean updateRelation() {
				if (pi == null) {
					return false;
				}
				if (pi.refEntityInfo == null || pi.refEntityInfo2 == null) {
					return false;
				}
				if (pi.refEntityInfo.mappedBy == null && pi.refEntityInfo2.mappedBy == null) {
					if (pi.refEntityInfo.refType == RefType.ONE2MANY) {
						if (pi.refEntityInfo2.refType == RefType.ONE2MANY) {
							if (pi.refEntityInfo2.mappedBy == null) {
								pi.refEntityInfo.refType = RefType.MANY2MANY;
								pi.refEntityInfo.mappedBy = pi.fieldId2;
								pi.refEntityInfo2.refType = RefType.MANY2MANY;
								pi.refEntityInfo2.mappedBy = pi.fieldId;
								updateOwner(pi);
							}
						}
					}
				}
				return true;
			}
		};
		ep.setConnector(m2mRelConnector);
		ep.enumEntityPairs();
		// 3)
		// third - try to assign - "single candidates"
		EntitySingleCandidateResolver escr = new EntitySingleCandidateResolver();
		escr.enumEntityPairs();
		// update import flags
		it = mapCUs_Info.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, EntityInfo> entry = it.next();
			EntityInfo entryInfo = entry.getValue();
			Iterator<Map.Entry<String, RefEntityInfo>> referencesIt = 
				entryInfo.getReferences().entrySet().iterator();
			boolean isOne2One = false;
			boolean isOne2Many = false;
			boolean isMany2One = false;
			boolean isMany2Many = false;
			for ( ; referencesIt.hasNext(); ) {
				Map.Entry<String, RefEntityInfo> entry2 = referencesIt.next();
				RefEntityInfo refEntityInfo = entry2.getValue();
				if (refEntityInfo != null) {
					if (refEntityInfo.refType == RefType.ONE2ONE && !refEntityInfo.resolvedAnnotationName) {
						isOne2One = true;
					}
					else if (refEntityInfo.refType == RefType.ONE2MANY && !refEntityInfo.resolvedAnnotationName) {
						isOne2Many = true;
					}
					else if (refEntityInfo.refType == RefType.MANY2ONE && !refEntityInfo.resolvedAnnotationName) {
						isMany2One = true;
					}
					else if (refEntityInfo.refType == RefType.MANY2MANY && !refEntityInfo.resolvedAnnotationName) {
						isMany2Many = true;
					}
				}
			}
			if (isOne2One) {
				entryInfo.addRequiredImport(JPAConst.IMPORT_ONE2ONE);
			}
			if (isOne2Many) {
				entryInfo.addRequiredImport(JPAConst.IMPORT_ONE2MANY);
			}
			if (isMany2One) {
				entryInfo.addRequiredImport(JPAConst.IMPORT_MANY2ONE);
			}
			if (isMany2Many) {
				entryInfo.addRequiredImport(JPAConst.IMPORT_MANY2MANY);
			}
		}
		// re-generate RefFieldInfoMap (for simple process)
		it = mapCUs_Info.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, EntityInfo> entry = it.next();
			EntityInfo entryInfo = entry.getValue();
			entryInfo.generateRefFieldInfoMap();
		}
		// if the parent has primary id - child should not generate it
		it = mapCUs_Info.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, EntityInfo> entry = it.next();
			EntityInfo entryInfo = entry.getValue();
			adjustParentId(entryInfo);
		}
	}

	public void adjustParentId(EntityInfo ei) {
		if (ei == null) {
			return;
		}
		EntityInfo parentEI = mapCUs_Info.get(ei.getJavaProjectName() + "/" + ei.getFullyQualifiedParentName()); //$NON-NLS-1$
		adjustParentId(parentEI);
		ei.adjustPrimaryId(parentEI);
	}
	
	/**
	 * start to collect information from particular entity class and
	 * its dependencies
	 * @param fullyQualifiedName of startup point entity fully qualified name
	 * example: "org.hibernate.eclipse.jdt.ui.internal.jpa.collect.AllEntitiesInfoCollector" 
	 */
	public void collect(String fullyQualifiedName, String projectName) {
		
		if (fullyQualifiedName == null) {
			return;
		}
		if (mapCUs_Info.containsKey(projectName + "/" + fullyQualifiedName)) { //$NON-NLS-1$
			return;
		}
		final IJavaProject javaProject = Utils.findJavaProject(projectName);
		ICompilationUnit icu = Utils.findCompilationUnit(javaProject, fullyQualifiedName);
		collect(icu);
	}
	
	/**
	 * start to collect information from particular entity class and
	 * its dependencies
	 * @param icu - startup point entity compilation unit
	 */
	@SuppressWarnings("unchecked")
	public void collect(ICompilationUnit icu) {
		
		if (icu == null) {
			return;
		}
		org.eclipse.jdt.core.dom.CompilationUnit cu = Utils.getCompilationUnit(icu, true);
		if (cu == null) {
			return;
		}
		if (cu.types() == null || cu.types().size() == 0 ) {
			return;
		}
		boolean hasTypeDeclaration = false;
		Iterator it = cu.types().iterator();
		while (it.hasNext()) {
			Object tmp = it.next();
			if (tmp instanceof TypeDeclaration) {
				hasTypeDeclaration = true;
				break;
			}
		}
		if (!hasTypeDeclaration) {
			// ignore EnumDeclaration & AnnotationTypeDeclaration
			return;
		}
		IJavaProject javaProject = icu.getJavaProject();
		String projectName = (javaProject != null) ? javaProject.getProject().getName() : ""; //$NON-NLS-1$
		ArrayList<String> fullyQualifiedNames = new ArrayList<String>();
		//TODO: should inspect all types in cu? so next method to get fullyQualifiedName:
		if (cu.getTypeRoot() == null || cu.getTypeRoot().findPrimaryType() == null) {
			it = cu.types().iterator();
			while (it.hasNext()) {
				Object tmp = it.next();
				if (tmp instanceof TypeDeclaration) {
					fullyQualifiedNames.add(((TypeDeclaration)tmp).resolveBinding().getBinaryName());
				}
			}
		} else {
			fullyQualifiedNames.add(cu.getTypeRoot().findPrimaryType().getFullyQualifiedName());
		}
		Iterator<String> itFQNames = fullyQualifiedNames.iterator();
		while (itFQNames.hasNext()) {
			String fullyQualifiedName = itFQNames.next();
			if (!mapCUs_Info.containsKey(projectName + "/" + fullyQualifiedName)) { //$NON-NLS-1$
				CollectEntityInfo finder = new CollectEntityInfo(fullyQualifiedName);
				cu.accept(finder);
				EntityInfo result = finder.getEntityInfo();
				if (result != null) {
					result.adjustParameters();
					result.setJavaProjectName(projectName);
					mapCUs_Info.put(projectName + "/" + fullyQualifiedName, result); //$NON-NLS-1$
					Iterator<String> itDep = result.getDependences();
					while (itDep.hasNext()) {
						String fullyQualifiedNameTmp = itDep.next();
						collect(fullyQualifiedNameTmp, projectName);
					}
				}
			}
		}
	}

	/**
	 * collection of processed entities getter 
	 */
	public Map<String, EntityInfo> getMapCUs_Info() {
		return mapCUs_Info;
	}

	public int getNonAbstractCUNumber() {
		Iterator<Map.Entry<String, EntityInfo>> it = null;
		int nonAbstractCUNumber = 0;
		// generate RefFieldInfoMap (for simple process)
		it = mapCUs_Info.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, EntityInfo> entry = it.next();
			EntityInfo entryInfo = entry.getValue();
			if (!entryInfo.isAbstractFlag()) {
				nonAbstractCUNumber++;
			}
		}
		return nonAbstractCUNumber;
	}

	public int getNonInterfaceCUNumber() {
		Iterator<Map.Entry<String, EntityInfo>> it = null;
		int nonInterfaceCUNumber = 0;
		// generate RefFieldInfoMap (for simple process)
		it = mapCUs_Info.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, EntityInfo> entry = it.next();
			EntityInfo entryInfo = entry.getValue();
			if (!entryInfo.isInterfaceFlag()) {
				nonInterfaceCUNumber++;
			}
		}
		return nonInterfaceCUNumber;
	}

	public AnnotStyle getAnnotationStylePreference() {
		return annotationStylePreference;
	}
}
