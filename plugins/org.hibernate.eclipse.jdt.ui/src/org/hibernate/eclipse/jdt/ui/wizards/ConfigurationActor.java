/*******************************************************************************
  * Copyright (c) 2008-2009 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.hibernate.eclipse.jdt.ui.wizards;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.WildcardType;
import org.hibernate.FetchMode;
import org.hibernate.eclipse.console.HibernateConsolePlugin;
import org.hibernate.eclipse.jdt.ui.internal.jpa.collect.AllEntitiesInfoCollector;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.EntityInfo;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.RefEntityInfo;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.RefType;
import org.hibernate.eclipse.jdt.ui.internal.jpa.common.Utils;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.Subclass;
import org.hibernate.util.xpl.StringHelper;
import org.jboss.tools.hibernate.proxy.PersistentClassProxy;
import org.jboss.tools.hibernate.proxy.TableProxy;
import org.jboss.tools.hibernate.proxy.ValueProxy;
import org.jboss.tools.hibernate.spi.IColumn;
import org.jboss.tools.hibernate.spi.IConfiguration;
import org.jboss.tools.hibernate.spi.IMappings;
import org.jboss.tools.hibernate.spi.IPersistentClass;
import org.jboss.tools.hibernate.spi.IService;
import org.jboss.tools.hibernate.spi.ITable;
import org.jboss.tools.hibernate.spi.IValue;
import org.jboss.tools.hibernate.util.HibernateHelper;

/**
 * @author Dmitry Geraskov
 *
 */
public class ConfigurationActor {
	
	/**
	 * selected compilation units for startup processing,
	 * result of processing selection
	 */
	protected Set<ICompilationUnit> selectionCU;
	
	private IService service;
	
	public ConfigurationActor(Set<ICompilationUnit> selectionCU){
		this.selectionCU = selectionCU;
		service = HibernateHelper.INSTANCE.getHibernateService();
	}	

	/**
	 * 
	 * @return different configuration for different projects
	 */
	public Map<IJavaProject, IConfiguration> createConfigurations(int processDepth){
		Map<IJavaProject, IConfiguration> configs = new HashMap<IJavaProject, IConfiguration>();
		if (selectionCU.size() == 0) {
			return configs;
		}		
		
		AllEntitiesInfoCollector collector = new AllEntitiesInfoCollector();		
		Iterator<ICompilationUnit> it = selectionCU.iterator();

		Map<IJavaProject, Set<ICompilationUnit>> mapJP_CUSet =
			new HashMap<IJavaProject, Set<ICompilationUnit>>();
		//separate by parent project
		while (it.hasNext()) {
			ICompilationUnit cu = it.next();
			Set<ICompilationUnit> set = mapJP_CUSet.get(cu.getJavaProject());
			if (set == null) {
				set = new HashSet<ICompilationUnit>();
				mapJP_CUSet.put(cu.getJavaProject(), set);
			}
			set.add(cu);
		}
		Iterator<Map.Entry<IJavaProject, Set<ICompilationUnit>>>
			mapIt = mapJP_CUSet.entrySet().iterator();
		while (mapIt.hasNext()) {
			Map.Entry<IJavaProject, Set<ICompilationUnit>>
				entry = mapIt.next();
			IJavaProject javaProject = entry.getKey();
			Iterator<ICompilationUnit> setIt = entry.getValue().iterator();
			collector.initCollector();
			while (setIt.hasNext()) {
				ICompilationUnit icu = setIt.next();
				collector.collect(icu, processDepth);
			}
			collector.resolveRelations();
			//I don't check here if any non abstract class selected
			configs.put(javaProject, createConfiguration(javaProject, collector.getMapCUs_Info()));

		}
		return configs;
	}
	
	protected IConfiguration createConfiguration(IJavaProject project, Map<String, EntityInfo> entities) {
		IConfiguration config = HibernateHelper.INSTANCE.getHibernateService().newDefaultConfiguration();
		
		ProcessEntityInfo processor = new ProcessEntityInfo();
		processor.setEntities(entities);
		
		for (Entry<String, EntityInfo> entry : entities.entrySet()) {			
			String fullyQualifiedName = entry.getValue().getFullyQualifiedName();
			ICompilationUnit icu = Utils.findCompilationUnit(project, fullyQualifiedName);
			if (icu != null){
				CompilationUnit cu = Utils.getCompilationUnit(icu, true);
				
				processor.setEntityInfo(entry.getValue());			
				cu.accept(processor);
			}
		}
		
		IMappings mappings = config.createMappings();
		Collection<IPersistentClass> classesCollection = createHierarhyStructure(project, processor.getRootClasses());
		for (IPersistentClass persistentClass : classesCollection) {
			mappings.addClass(persistentClass);
		}
		return config;
	}
	
	/**
	 * Replace <class> element on <joined-subclass> or <subclass>.
	 * @param project
	 * @param rootClasses
	 * @return
	 */
	private Collection<IPersistentClass> createHierarhyStructure(IJavaProject project, Map<String, RootClass> rootClasses){
		Map<String, IPersistentClass> pcCopy = new HashMap<String, IPersistentClass>();
		for (Map.Entry<String, RootClass> entry : rootClasses.entrySet()) {
			pcCopy.put(entry.getKey(), entry.getValue() != null ? new PersistentClassProxy(entry.getValue()) : null);
		}
		for (Map.Entry<String, IPersistentClass> entry : pcCopy.entrySet()) {
			IPersistentClass pc = null;
			try {
				pc = getMappedSuperclass(project, pcCopy, (RootClass) ((PersistentClassProxy)entry.getValue()).getTarget());				
				Subclass subclass = null;
				if (pc != null){
					if (pc.isAbstract()){
						subclass = new SingleTableSubclass(((PersistentClassProxy)pc).getTarget());
						if (pc instanceof RootClass && pc.getDiscriminator() == null){
							IValue discr = service.newSimpleValue();
							discr.setTypeName("string"); //$NON-NLS-1$
							discr.addColumn(service.newColumn("DISCR_COL")); //$NON-NLS-1$
							((RootClass)pc).setDiscriminator(((ValueProxy)discr).getTarget());
						}
					} else {
						subclass = new JoinedSubclass(((PersistentClassProxy)pc).getTarget());
					}
				} else {
					pc = getMappedImplementedInterface(project, pcCopy, (RootClass) ((PersistentClassProxy)entry.getValue()).getTarget());
					if (pc != null){
						subclass = new SingleTableSubclass(((PersistentClassProxy)pc).getTarget());
					}
				}
				if (subclass != null){
					IPersistentClass pastClass = pcCopy.get(entry.getKey());
					subclass.setClassName(pastClass.getClassName());
					subclass.setEntityName(pastClass.getEntityName());
					subclass.setDiscriminatorValue(StringHelper.unqualify(pastClass.getClassName()));
					subclass.setAbstract(pastClass.isAbstract());
					if (subclass instanceof JoinedSubclass) {
						((JoinedSubclass) subclass).setTable(((TableProxy)HibernateHelper.INSTANCE.getHibernateService().newTable(StringHelper.unqualify(pastClass.getClassName()).toUpperCase())).getTarget());
						((JoinedSubclass) subclass).setKey((KeyValue) pc.getIdentifierProperty().getValue());
					}
					if (pastClass.getIdentifierProperty() != null) {
						subclass.addProperty(pastClass.getIdentifierProperty());
					}
					
					Iterator<?> it = pastClass.getPropertyIterator();
					while (it.hasNext()) {
						subclass.addProperty((Property) it.next());
					}
					entry.setValue(new PersistentClassProxy(subclass));
				}
			} catch (JavaModelException e) {
				HibernateConsolePlugin.getDefault().log(e);
			}			
		}
		return pcCopy.values();
	}
	
	private IPersistentClass getMappedSuperclass(IJavaProject project, Map<String, IPersistentClass> persistentClasses, RootClass rootClass) throws JavaModelException{
		IType type = Utils.findType(project, rootClass.getClassName());
		//TODO not direct superclass?
		if (type.getSuperclassName() != null){
			String[][] supertypes = type.resolveType(type.getSuperclassName());
			if (supertypes != null){
				String supertype = supertypes[0][0].length() > 0 ? supertypes[0][0] + '.' + supertypes[0][1]
				                                               : supertypes[0][1];
				return persistentClasses.get(supertype);
			}
		} 
		return null;
	}
	
	private IPersistentClass getMappedImplementedInterface(IJavaProject project, Map<String, IPersistentClass> persistentClasses, RootClass rootClass) throws JavaModelException{
		IType type = Utils.findType(project, rootClass.getClassName());	
		//TODO not direct interfaces?
		String[] interfaces = type.getSuperInterfaceNames();			
		for (String interfaze : interfaces) {
			String[][] fullInterfaces = type.resolveType(interfaze);
			for (String[] fullInterface : fullInterfaces) {
				String inrefaceName = fullInterface[0] + '.' + fullInterface[1];
				if (persistentClasses.get(inrefaceName) != null){
					return persistentClasses.get(inrefaceName);
				}
			}
		}
		return null;
	}
	
}

class ProcessEntityInfo extends ASTVisitor {
	
	private Map<String, RootClass> rootClasses = new HashMap<String, RootClass>();
	
	/**
	 * current rootClass
	 */
	private RootClass rootClass;
	
	/**
	 * information about entity
	 */
	protected EntityInfo entityInfo;
	
	private TypeDeclaration currentType;
	
	TypeVisitor typeVisitor;
	
	IService service;
	
	ProcessEntityInfo() {
		service = HibernateHelper.INSTANCE.getHibernateService();
	}
	
	public void setEntities(Map<String, EntityInfo> entities) {
		rootClasses.clear();
		Iterator<Map.Entry<String, EntityInfo>> it = entities.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, EntityInfo> entry = it.next();
			EntityInfo entryInfo = entry.getValue();
			String className = entryInfo.getName();
			ITable table = HibernateHelper.INSTANCE.getHibernateService().newTable(className.toUpperCase());
			RootClass rootClass = new RootClass();
			rootClass.setEntityName( entryInfo.getFullyQualifiedName() );
			rootClass.setClassName( entryInfo.getFullyQualifiedName() );
			rootClass.setProxyInterfaceName( entryInfo.getFullyQualifiedName() );
			rootClass.setLazy(true);
			rootClass.setTable(((TableProxy)table).getTarget());
			rootClass.setAbstract(entryInfo.isAbstractFlag());//abstract or interface
			rootClasses.put(entryInfo.getFullyQualifiedName(), rootClass);
		}
		typeVisitor = new TypeVisitor(rootClasses);
	}

	
	public void setEntityInfo(EntityInfo entityInfo) {
		this.entityInfo = entityInfo;
		rootClass = rootClasses.get(entityInfo.getFullyQualifiedName());
	}
	
	@Override
	public boolean visit(CompilationUnit node) {
		Assert.isNotNull(rootClass);
		currentType = null;
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public boolean visit(TypeDeclaration node) {
		if (currentType == null){
			currentType = node;
			if ("".equals(entityInfo.getPrimaryIdName())){ //$NON-NLS-1$
				//try to guess id
				FieldDeclaration[] fields = node.getFields();
				String firstFieldName = ""; //$NON-NLS-1$
				for (int i = 0; i < fields.length; i++) {
					Iterator<VariableDeclarationFragment> itFieldsNames = fields[i].fragments().iterator();
					while(itFieldsNames.hasNext()) {
						VariableDeclarationFragment variable = itFieldsNames.next();
						Type type = ((FieldDeclaration)variable.getParent()).getType();
						if ("id".equals(variable.getName().getIdentifier()) //$NON-NLS-1$
								&& !type.isArrayType()
								&& !Utils.isImplementInterface(new ITypeBinding[]{type.resolveBinding()}, Collection.class.getName())){
							entityInfo.setPrimaryIdName(variable.getName().getIdentifier());
							return true;
						} else if ("".equals(firstFieldName)//$NON-NLS-1$
								&& !type.isArrayType()
								&& !Utils.isImplementInterface(
										new ITypeBinding[]{type.resolveBinding()}, Collection.class.getName())){
							//set first field as id
							firstFieldName = variable.getName().getIdentifier();
						}
					}
				}
				entityInfo.setPrimaryIdName(firstFieldName);
			}
			return true;
		}
		//do not visit inner type
		return false;
	}
	
	@Override
	public void endVisit(TypeDeclaration node) {
		if (currentType == node && rootClass.getIdentifierProperty() == null){
			//root class should always has id
			IValue sValue = service.newSimpleValue();
			sValue.addColumn(service.newColumn("id".toUpperCase()));//$NON-NLS-1$
			sValue.setTypeName(Long.class.getName());
			Property prop = new Property();
			prop.setName("id"); //$NON-NLS-1$
			prop.setValue(((ValueProxy)sValue).getTarget());
			rootClass.setIdentifierProperty(prop);
		}
	}
	
	@SuppressWarnings("unchecked")
	public boolean visit(FieldDeclaration node) {
		//do not map static or final fields
		if ((node.getModifiers() & (Modifier.FINAL | Modifier.STATIC)) != 0){
			return false;
		}
		
		Type type = node.getType();
		if (type == null) {
			return true;
		}

		String primaryIdName = entityInfo.getPrimaryIdName();
		Iterator<VariableDeclarationFragment> itVarNames = node.fragments().iterator();
		while (itVarNames.hasNext()) {
			VariableDeclarationFragment var = itVarNames.next();
			Property prop = createProperty(var);			
			if (prop == null) {
				continue;
			}
			if (!varHasGetterAndSetter(var)){
				prop.setPropertyAccessorName("field"); //$NON-NLS-1$
			}
			String name = var.getName().getIdentifier();
			if (name.equals(primaryIdName)) {
				rootClass.setIdentifierProperty(prop);
			} else {
				rootClass.addProperty(prop);
			}
		}

		return true;
	}
	
	private boolean varHasGetterAndSetter(VariableDeclarationFragment var){
		String name = var.getName().getIdentifier();
		String setterName = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1); //$NON-NLS-1$
		String getterName = "get" + Character.toUpperCase(name.charAt(0)) + name.substring(1); //$NON-NLS-1$
		String getterName2 = null;
		Type varType = ((FieldDeclaration)var.getParent()).getType();
		if (varType.isPrimitiveType()
				&& ((PrimitiveType)varType).getPrimitiveTypeCode() == PrimitiveType.BOOLEAN){
			getterName2 = "is" + Character.toUpperCase(name.charAt(0)) + name.substring(1); //$NON-NLS-1$
		}		
		boolean setterFound = false, getterFound = false;
		MethodDeclaration[] mds = ((TypeDeclaration)((FieldDeclaration)var.getParent()).getParent()).getMethods();
		for (MethodDeclaration methodDeclaration : mds) {
			String methodName = methodDeclaration.getName().getIdentifier();
			if (methodName.equals(setterName)) setterFound = true;
			if (methodName.equals(getterName)) getterFound = true;
			if (methodName.equals(getterName2)) getterFound = true;
			if (setterFound && getterFound) return true;
		}
		return false;
	}
	
	@Override
	public boolean visit(MethodDeclaration node) {
		if (!entityInfo.isInterfaceFlag()) return super.visit(node);
		org.eclipse.jdt.core.dom.TypeDeclaration type = (org.eclipse.jdt.core.dom.TypeDeclaration) node.getParent();
		//TODO check Abstract methods
		if (type.isInterface()){			
			String varName = Utils.getFieldNameByGetter(node);
			if (varName != null){
				String primaryIdName = entityInfo.getPrimaryIdName();
				Type methodType = node.getReturnType2();
				if (varName.toLowerCase().equals(primaryIdName.toLowerCase()))
					varName = primaryIdName;
				Property prop = createProperty(varName, methodType);
				if (varName.equals(primaryIdName)) {
					rootClass.setIdentifierProperty(prop);
				} else {
					rootClass.addProperty(prop);
				}
			}
		}
		return super.visit(node);
	}
	
	
	/**
	 * @return the rootClass
	 */
	public IPersistentClass getPersistentClass() {
		return new PersistentClassProxy(rootClass);
	}
	
	protected Property createProperty(VariableDeclarationFragment var) {
		return createProperty(var.getName().getIdentifier(), ((FieldDeclaration)var.getParent()).getType());
	}
	
	protected Property createProperty(String varName,  Type varType) {
			typeVisitor.init(varName, entityInfo);
			varType.accept(typeVisitor);
			Property p = typeVisitor.getProperty();
			return p;
	}

	/**
	 * @return the rootClasses
	 */
	public Map<String, RootClass> getRootClasses() {
		return rootClasses;
	}
	
}

class TypeVisitor extends ASTVisitor{
	
	private String varName;
	
	private Map<String, RootClass> rootClasses;
	
	private RootClass rootClass;
	
	private EntityInfo entityInfo;
	
	private RefEntityInfo ref;
	
	private Property prop;
	
	private IService service;
	
	TypeVisitor(Map<String, RootClass> rootClasses){
		this.rootClasses = rootClasses;
		service = HibernateHelper.INSTANCE.getHibernateService();
	}
	
	public void init(String varName, EntityInfo entityInfo){
		this.varName = varName;
		this.entityInfo = entityInfo;
		Map<String, RefEntityInfo> refs = entityInfo.getReferences();
		ref = refs.get(varName);
		prop = null;
		rootClass = rootClasses.get(entityInfo.getFullyQualifiedName());
	}

	@Override
	public boolean visit(ArrayType type) {
		IValue array = null;
		Type componentType = type.getComponentType();
		ITypeBinding tb = componentType.resolveBinding();
		if (tb == null) return false;//Unresolved binding. Omit the property.
		if (tb.isPrimitive()){
			array = service.newPrimitiveArray(rootClass != null ? new PersistentClassProxy(rootClass) : null);
			
			IValue value = buildSimpleValue(tb.getName());
			value.setTable(rootClass.getTable() != null ? new TableProxy(rootClass.getTable()) : null);
			array.setElement(value);
			array.setCollectionTable(new TableProxy(rootClass.getTable()));//TODO what to set?
		} else {
			RootClass associatedClass = rootClasses.get(tb.getBinaryName());
			array = service.newArray(rootClass != null ? new PersistentClassProxy(rootClass) : null);
			array.setElementClassName(tb.getBinaryName());
			array.setCollectionTable(new TableProxy(associatedClass.getTable()));
			
			IValue oValue = service.newOneToMany(rootClass != null ? new PersistentClassProxy(rootClass) : null);
			oValue.setAssociatedClass(associatedClass != null ? new PersistentClassProxy(associatedClass) : null);
			oValue.setReferencedEntityName(tb.getBinaryName());
			
			array.setElement(oValue);
		}
		
		IValue key = service.newSimpleValue();
		if (StringHelper.isNotEmpty(entityInfo.getPrimaryIdName())) {
			key.addColumn(service.newColumn(entityInfo.getPrimaryIdName().toUpperCase()));
		}
		array.setKey((KeyValue)((ValueProxy)key).getTarget());
		array.setFetchMode(FetchMode.JOIN);
		IValue index = service.newSimpleValue();
		
		//add default index
		//index.addColumn(new Column(varName.toUpperCase()+"_POSITION"));
		
		array.setIndex(index);
		buildProperty(array);
		prop.setCascade("none");//$NON-NLS-1$
		return false;//do not visit children
	}

	@Override
	public boolean visit(ParameterizedType type) {
		Assert.isNotNull(type, "Type object cannot be null"); //$NON-NLS-1$
		Assert.isNotNull(entityInfo, "EntityInfo object cannot be null"); //$NON-NLS-1$
		ITypeBinding tb = type.resolveBinding();
		if (tb == null) return false;//Unresolved binding. Omit the property.
		rootClass = rootClasses.get(entityInfo.getFullyQualifiedName());
		Assert.isNotNull(rootClass, "RootClass not found."); //$NON-NLS-1$
		
		ITypeBinding[] interfaces = Utils.getAllInterfaces(tb);
		IValue value = buildCollectionValue(interfaces);
		if (value != null) {
			if (ref != null && rootClasses.get(ref.fullyQualifiedName) != null){
				IValue oValue = service.newOneToMany(rootClass != null ? new PersistentClassProxy(rootClass) : null);
				RootClass associatedClass = rootClasses.get(ref.fullyQualifiedName);
				oValue.setAssociatedClass(associatedClass != null ? new PersistentClassProxy(associatedClass) : null);
				oValue.setReferencedEntityName(associatedClass.getEntityName());
				//Set another table
				value.setCollectionTable(associatedClass.getTable() != null ? new TableProxy(associatedClass.getTable()) : null);				
				value.setElement(oValue);				
			} else {
				IValue elementValue = buildSimpleValue(tb.getTypeArguments()[0].getQualifiedName());
				elementValue.setTable(rootClass.getTable() != null ? new TableProxy(rootClass.getTable()) : null);
				value.setElement(elementValue);
				value.setCollectionTable(rootClass.getTable() != null ? new TableProxy(rootClass.getTable()) : null);//TODO what to set?
			}
			if (value.isList()){
				value.setIndex(service.newSimpleValue());
			} else if (value.isMap()){
				IValue map_key = service.newSimpleValue();
				//FIXME: is it possible to map Map<SourceType, String>?
				//Or only Map<String, SourceType>
				map_key.setTypeName(tb.getTypeArguments()[0].getBinaryName());
				value.setIndex(map_key);
			}
		}
				
		if (value == null) {
			value = buildSimpleValue(tb.getBinaryName());
		}
		
		buildProperty(value);
		if (!(value.isSimpleValue())){
			prop.setCascade("none");//$NON-NLS-1$
		}
		return false;//do not visit children
	}

	@Override
	public boolean visit(PrimitiveType type) {
		buildProperty(buildSimpleValue(type.getPrimitiveTypeCode().toString()));
		return false;
	}

	@Override
	public boolean visit(QualifiedType type) {
		return super.visit(type);
	}

	@Override
	public boolean visit(SimpleType type) {
		ITypeBinding tb = type.resolveBinding();
		if (tb == null) return false;//Unresolved binding. Omit the property.
		ITypeBinding[] interfaces = Utils.getAllInterfaces(tb);
		IValue value = buildCollectionValue(interfaces);
		if (value != null){
			IValue element = buildSimpleValue("string");//$NON-NLS-1$
			value.setElement(element);
			value.setCollectionTable(rootClass.getTable() != null ? new TableProxy(rootClass.getTable()) : null);//TODO what to set?
			buildProperty(value);
			if (value.isList()){
				value.setIndex(service.newSimpleValue());
			} else if (value.isMap()){
				IValue map_key = service.newSimpleValue();
				//FIXME: how to detect key-type here
				map_key.setTypeName("string"); //$NON-NLS-1$
				value.setIndex(map_key);
			}
			prop.setCascade("none");//$NON-NLS-1$
		} else if (tb.isEnum()){
			value = buildSimpleValue(org.hibernate.type.EnumType.class.getName());
			Properties typeParameters = new Properties();
			typeParameters.put(org.hibernate.type.EnumType.ENUM, tb.getBinaryName());
			typeParameters.put(org.hibernate.type.EnumType.TYPE, java.sql.Types.VARCHAR);
			value.setTypeParameters(typeParameters);
			buildProperty(value);
		} else if (ref != null /*&& ref.fullyQualifiedName.indexOf('$') < 0*/){
			IValue sValue = null;
			if (ref.refType == RefType.MANY2ONE){
				sValue = service.newManyToOne(new TableProxy(rootClass.getTable()));
			} else if (ref.refType == RefType.ONE2ONE){
				sValue = service.newOneToOne(rootClass != null ? new PersistentClassProxy(rootClass) : null);
			} else if (ref.refType == RefType.UNDEF){
				sValue = service.newOneToOne(rootClass != null ? new PersistentClassProxy(rootClass) : null);
			} else {
				//OneToMany and ManyToMany must be a collection
				throw new IllegalStateException(ref.refType.toString());
			}
			
			IColumn column = HibernateHelper.INSTANCE.getHibernateService().newColumn(varName.toUpperCase());
			sValue.addColumn(column);					
			sValue.setTypeName(tb.getBinaryName());
			sValue.setFetchMode(FetchMode.JOIN);
			sValue.setReferencedEntityName(ref.fullyQualifiedName);
			buildProperty(sValue);
			prop.setCascade("none");//$NON-NLS-1$
		} else {
			value = buildSimpleValue(tb.getBinaryName());
			buildProperty(value);
		}
		return super.visit(type);
	}

	@Override
	public boolean visit(WildcardType type) {
		return super.visit(type);
	}
	
	public Property getProperty(){
		return prop;
	}
	
	protected void buildProperty(IValue value){
		prop = new Property();
		prop.setName(varName);
		prop.setValue(((ValueProxy)value).getTarget());
	}
	
	private IValue buildSimpleValue(String typeName){
		IValue sValue = service.newSimpleValue();
		sValue.addColumn(service.newColumn(varName.toUpperCase()));
		sValue.setTypeName(typeName);
		return sValue;
	}
	
	private IValue buildCollectionValue(ITypeBinding[] interfaces){
		IValue cValue = null;
		if (Utils.isImplementInterface(interfaces, Set.class.getName())){
			cValue = service.newSet(rootClass != null ? new PersistentClassProxy(rootClass) : null);
		} else if (Utils.isImplementInterface(interfaces, List.class.getName())){
			cValue = service.newList(rootClass != null ? new PersistentClassProxy(rootClass) : null);
		} else if (Utils.isImplementInterface(interfaces, Map.class.getName())){
			cValue = service.newMap(rootClass != null ? new PersistentClassProxy(rootClass) : null);
		} else if (Utils.isImplementInterface(interfaces, Collection.class.getName())){
			cValue = service.newBag(rootClass != null ? new PersistentClassProxy(rootClass) : null);
		}
		
		if (cValue == null) return null;
		
		//By default set the same table, but for one-to-many should change it to associated class's table
		cValue.setCollectionTable(new TableProxy(rootClass.getTable()));

		IValue key = service.newSimpleValue();
		key.setTypeName("string");//$NON-NLS-1$
		if (StringHelper.isNotEmpty(entityInfo.getPrimaryIdName())){
			key.addColumn(service.newColumn(entityInfo.getPrimaryIdName().toUpperCase()));
		}
		cValue.setKey((KeyValue)((ValueProxy)key).getTarget());
		cValue.setLazy(true);
		cValue.setRole(StringHelper.qualify(rootClass.getEntityName(), varName));
		return cValue;
	}
}
