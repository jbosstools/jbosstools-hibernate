/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.hibernate.internal.core.util;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.collections.SequencedHashMap;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.internal.core.PersistentField;
import org.jboss.tools.hibernate.internal.core.hibernate.PersistableProperty;


/**
 * @author Nick
 *
 */
public class TypeAnalyzer {
	private IType type;
	private IType upperBoundType;
//	private IProject project;
	
	// edit tau 22.03.2006
	//private Vector hierarchy;
	private ArrayList<IType> hierarchy;	
    
    public PersistableProperty[] getPropertiesByMask(int accessorMask) throws CoreException {
        SequencedHashMap fieldMap = new SequencedHashMap();
        PersistableProperty[] props;
        if ((accessorMask & PersistentField.ACCESSOR_FIELD) != 0)
        {
            props = getPersistableFields();
            for (int i = 0; i < props.length; i++) {
                PersistableProperty property = props[i];
                fieldMap.put(property.getName(),property);
            }
        }

        if ((accessorMask & PersistentField.ACCESSOR_PROPERTY) != 0)
        {
            props = getProperties();
            for (int i = 0; i < props.length; i++) {
                PersistableProperty property = props[i];
                fieldMap.put(property.getName(),property);
            }
        }
        return (PersistableProperty[]) fieldMap.values().toArray(new PersistableProperty[0]);
    }
    
    public PersistableProperty[] getFilteredProperties(int accessorMask, String javaType) throws CoreException
    {
        PersistableProperty[] props = getPropertiesByMask(accessorMask);
        ArrayList<PersistableProperty> filteredProps = new ArrayList<PersistableProperty>();
        for (int i = 0; i < props.length; i++) {
            if (props[i].getType().equals(javaType))
                filteredProps.add(props[i]);
        }
        return filteredProps.toArray(new PersistableProperty[0]);
    }
    
    public static TypeAnalyzer getDefaultPCAnalyzer(IPersistentClass pc) throws CoreException
    {
        TypeAnalyzer ta = null;
        IType type = pc.getType();
        if (type != null)
        {
            ta = new TypeAnalyzer(type);
            if (pc.getSuperClass() != null)
                ta.setUpperBoundType(pc.getSuperClass().getType());
        }
        return ta;
    }
    
	public boolean hierarchyHasMethod(String methodName, String[] params, String returnType) throws CoreException
	{
		buildHierarchyVector();
		boolean exists = false;
		Iterator itr = hierarchy.iterator();
		while (itr.hasNext() && !exists)
		{
			TypeAnalyzer ta = new TypeAnalyzer((IType)itr.next());
			exists = ta.hasMethod(methodName,params,returnType);
		}
		return exists;
	}
	
	public boolean hierarchyHasMethod(String methodName, String[] params) throws CoreException
	{
		buildHierarchyVector();
		boolean exists = false;
		Iterator itr = hierarchy.iterator();
		while (itr.hasNext() && !exists)
		{
			TypeAnalyzer ta = new TypeAnalyzer((IType)itr.next());
			if (params != null)
				exists = ta.hasMethod(methodName,params);
			else
				exists = ta.hasMethod(methodName);
		}
		return exists;
	}
	
	public boolean hasMethod(String methodName) throws JavaModelException
	{
		boolean result = false;
		IMethod[] methods = type.getMethods();
		for (int i = 0; i < methods.length && !result; i++) {
			IMethod method = methods[i];
			result = methodName.equals(method.getElementName());
		}
		return result;
	}
	
	public boolean hasMethod(String methodName, String[] params) throws CoreException
	{
		IMethod method = type.getMethod(methodName,params);
		return (type.findMethods(method) != null);
	}
	
	public boolean hasMethod(String methodName, String[] params, String returnType) throws CoreException
	{
		boolean found = false;
		IMethod method = type.getMethod(methodName,params);
		IMethod[] methods = type.findMethods(method);
		if (methods != null)
			for (int i = 0; i < methods.length && !found; i++) {
				IMethod theMethod = methods[i];
				found = (returnType.equals(theMethod.getReturnType()));
			}
		return found;
	}
	
    public boolean hasRedefinedConstructor() throws CoreException
    {
        boolean found = false;
        IMethod[] methods = type.getMethods();
        for (int i = 0; i < methods.length && !found; i++) {
            IMethod method = methods[i];
            if (type.getElementName().equals(method.getElementName()) && method.getParameterTypes().length != 0)
                found = true;
        }
        
        return found;
    }
    
	public boolean hasEqualsMethod() throws CoreException
	{
		return hasMethod("equals",new String[]{Signature.createTypeSignature("Object", false)});
	}

	public boolean hasHashCodeMethod() throws CoreException
	{
		return hasMethod("hashCode",new String[]{});
	}
	
    public boolean hasToStringMethod() throws CoreException
    {
        return hasMethod("toString",new String[]{});
    }
    
	private void buildHierarchyVector() throws CoreException {
		//TODO (tau -> tau) Vector to ArrayList?
		if (hierarchy != null)
			return;
		
		// edit tau 22.03.2006		
		hierarchy = new ArrayList<IType>();
		
		IType var_type = type;
		do {
			hierarchy.add(var_type);
			var_type = ClassUtils.getSuperType(var_type);
		} while (var_type != null && !var_type.equals(upperBoundType));
	}
	
	public TypeAnalyzer(IType type) {
		this.type = type;
//		this.project = type.getJavaProject().getProject();
	}
	
    public PersistableProperty getPropertyOrField(String propertyName) throws CoreException
    {
        PersistableProperty result = null;
        PersistableProperty[] props = this.getPersistableFields();
        boolean found = false;
        if(props != null)
        {
	        for (int i = 0; i < props.length && !found; i++) {
	            PersistableProperty property = props[i];
	            if (property.getName().equals(propertyName))
	            {
	                found = true;
	                result = property;
	            }
	        }
        }
        if (!found)
        {
            result = findProperty(propertyName);
        }
        return result;
    }
    
    private PersistableProperty findProperty(String name) throws CoreException
    {
        buildHierarchyVector();
        
    	// edit tau 22.03.2006        
        //Enumeration elements = hierarchy.elements();
        Iterator hierarchyIterator = hierarchy.iterator();

    	// edit tau 22.03.2006        
        //boolean found = false;
        
        PersistableProperty result = null;
        
    	// edit tau 22.03.2006
        //while (elements.hasMoreElements() && result == null)
        while (hierarchyIterator.hasNext() && result == null)        
        {
        	// edit tau 22.03.2006        	
            //IType aType = (IType) elements.nextElement();
        	IType aType = (IType) hierarchyIterator.next();
        	
            String typeString = null;
            
            IMethod getterMethod = aType.getMethod("get"+StringUtils.beanCapitalize(name),new String[]{});
            IMethod isMethod = aType.getMethod("is"+StringUtils.beanCapitalize(name),new String[]{});
            if (getterMethod != null && getterMethod.exists())
                typeString = getterMethod.getReturnType();
            if (isMethod != null && isMethod.exists() && Signature.SIG_BOOLEAN.equals(isMethod.getReturnType()))
                typeString = isMethod.getReturnType();
        
            if (typeString != null && 
                    hierarchyHasMethod("set"+StringUtils.beanCapitalize(name),new String[]{typeString}))
            {
                result = new PersistableProperty(name,ClassUtils.getQualifiedNameFromSignature(aType,typeString),
                        ClassUtils.getGenerifiedTypesFromSignature(aType, typeString), PersistentField.ACCESSOR_PROPERTY);
            }
        }
        
        return result;
    }
    
//	private Comparator getComparator()
//	{
//		Comparator cmp = new Comparator()
//		{
//			public int compare(Object arg0, Object arg1) 
//			{
//				String name1	 	= ((IMethod)arg0).getElementName();
//				String name2		= ((IMethod)arg1).getElementName();
//				if(name1 == null)	return 0;
//				return name1.compareTo(name2);
//			}
//		};
//		return cmp;
//	}
    
//	private Comparator getComparator2()
//	{
//		Comparator cmp = new Comparator()
//		{
//			public int compare(Object arg0, Object arg1) 
//			{
//				String name1	 	= ((IMethod)arg0).getElementName();
//				String name2		= (String)arg1;
//				if(name1 == null)	return 0;
//				return name1.compareTo(name2);
//			}
//		};
//		return cmp;
//	}

//    private PersistableProperty[] getProperties() throws CoreException
//	{
//		buildHierarchyVector();
//		Vector properties = new Vector();
//		Comparator cmp = getComparator();
//		Comparator cmp2 = getComparator2();
//		int aSize = hierarchy.size();
//		for (int i = 0 ; i < aSize ; i++ )
//		{
//			IType aType = (IType)hierarchy.elementAt(i);
//			IField[] fields = aType.getFields();
//			IMethod[] methods = aType.getMethods();
//			Arrays.sort(methods, cmp);
//			for(int t = 0; t < fields.length; t++)
//			{
//				
//				String settername = "set" + StringUtils.capitalize(fields[t].getElementName());
//				int index = Arrays.binarySearch(methods, settername,cmp2);
//				if(index >= 0)
//				{
//					IMethod method = methods[index];
//					String name = method.getElementName();
//					if (name.startsWith("set"))
//					{
//						String[] params = method.getParameterTypes();
//						if (params != null && params.length == 1)
//						{
//							String param = params[0];
//							String propertyName = StringUtils.beanDecapitalize(name.substring("set".length()));
//							String tailName = name.substring("set".length());
//							boolean getterExists = hierarchyHasMethod("get"+tailName,new String[]{},param);
//							if (!getterExists)
//							{
//								String simpleParamName = Signature.getSimpleName(param);
//								if (simpleParamName.equals(Signature.SIG_BOOLEAN))
//									getterExists = hierarchyHasMethod("is"+tailName,new String[]{},param);
//							}
//							
//							if (getterExists)
//							{
//								PersistableProperty aField = new PersistableProperty(propertyName,ClassUtils.getQualifiedNameFromSignature(aType,param),
//                                        ClassUtils.getGenerifiedTypesFromSignature(aType,param), PersistentField.ACCESSOR_PROPERTY);
//								properties.add(aField);			
//							}
//						}
//					}
//					}
//			}
//		}
//		return (PersistableProperty[]) properties.toArray(new PersistableProperty[]{});
//	}
	
    private PersistableProperty[] getProperties() throws CoreException
    {
        buildHierarchyVector();
        
    	// edit tau 22.03.2006        
        //Vector properties1 = new Vector();
        ArrayList<PersistableProperty> properties = new ArrayList<PersistableProperty>();        

    	// edit tau 22.03.2006        
        //int aSize = hierarchy.size();
        //for (int i = 0 ; i < aSize ; i++ )
        
        Iterator hierarchyIterator = hierarchy.iterator();
        while (hierarchyIterator.hasNext()) 
        {
        	// edit tau 22.03.2006        	
            //IType aType = (IType)hierarchy.elementAt(i);
            IType aType = (IType)hierarchyIterator.next();            
            
            IMethod[] methods = aType.getMethods();
            
            for (int j = 0; j < methods.length; j++) 
            {
                String propertyName = null;
                
                IMethod method = methods[j];
                String name = method.getElementName();
                if (name.startsWith("get"))
                {
                    String[] params = method.getParameterTypes();
                    if (params == null || params.length == 0)
                    {
                        propertyName = StringUtils.beanDecapitalize(name.substring("get".length()));
                        String tailName = name.substring("get".length());
                        boolean setterExists = hierarchyHasMethod("set"+tailName,new String[]{method.getReturnType()},Signature.SIG_VOID);
                        if (!setterExists)
                            propertyName = null;
                    }   
                }

                if (name.startsWith("is"))
                {
                    String[] params = method.getParameterTypes();
                    if (params == null || params.length == 0)
                    {
                        propertyName = StringUtils.beanDecapitalize(name.substring("is".length()));
                        String tailName = name.substring("is".length());
                        boolean setterExists = Signature.SIG_BOOLEAN.equals(method.getReturnType()) && hierarchyHasMethod("set"+tailName,new String[]{Signature.SIG_BOOLEAN},Signature.SIG_VOID);
                        if (!setterExists)
                            propertyName = null;
                    }   
                }
            
                if (propertyName != null/*&& StringUtils.isValidJavaName(propertyName)*/)
                {
                    PersistableProperty aField = new PersistableProperty(propertyName,ClassUtils.getQualifiedNameFromSignature(aType,method.getReturnType()),null,PersistentField.ACCESSOR_PROPERTY);
                    properties.add(aField);         
                }
                
//                String[] params = method.getParameterTypes();
//                if (params != null && params.length == 1)
//                {
//                    String param = params[0];
//                    String propertyName = StringUtils.beanDecapitalize(name.substring("set".length()));
//                    String tailName = name.substring("set".length());
//                    boolean getterExists = hierarchyHasMethod("get"+tailName,new String[]{},param);
//                    if (!getterExists)
//                    {
//                        String simpleParamName = Signature.getSimpleName(param);
//                        if (simpleParamName.equals(Signature.SIG_BOOLEAN))
//                            getterExists = hierarchyHasMethod("is"+tailName,new String[]{},param);
//                    }
//                    
//                    if (getterExists)
//                    {
//                        PersistableProperty aField = new PersistableProperty(propertyName,ClassUtils.getQualifiedNameFromSignature(aType,param),null,PersistentField.ACCESSOR_PROPERTY);
//                        properties.add(aField);         
//                    }
//                }
                
            }
        }
        return (PersistableProperty[]) properties.toArray(new PersistableProperty[]{});
    }

    
    private PersistableProperty[] getPersistableFields(IField[] fields) throws CoreException
    {
    	// edit tau 22.03.2006    	
        //Vector persistables = new Vector();
    	ArrayList<PersistableProperty> persistables = new ArrayList<PersistableProperty>();    	
        
        if (fields != null) {
            for (int i = 0; i < fields.length; i++) {
                IField field = fields[i];
                int fieldFlags = field.getFlags();
                if (Flags.isTransient(fieldFlags) || Flags.isStatic(fieldFlags))
                    continue;
                PersistableProperty pp = new PersistableProperty(field.getElementName(),ClassUtils.getQualifiedNameFromSignature(field.getDeclaringType(),field.getTypeSignature()),
                        ClassUtils.getGenerifiedTypesFromSignature(field.getDeclaringType(), field.getTypeSignature()), PersistentField.ACCESSOR_FIELD);
                persistables.add(pp);
            } 
        }

        return persistables.toArray(new PersistableProperty[0]);
    }

    private PersistableProperty[] getPersistableFields() throws CoreException
	{
        return getPersistableFields(this.getFields());
    }
	
	public IField[] getFields() throws CoreException
	{
		SequencedHashMap result = new SequencedHashMap();
		buildHierarchyVector();
		
		Iterator itr = hierarchy.iterator();
		while (itr.hasNext())
		{
			IField[] fields = ((IType)itr.next()).getFields();
			if (fields != null)
				for (int i = 0; i < fields.length; i++) 
					result.put(fields[i].getElementName(),fields[i]);
		}
		if (result.isEmpty())
			return null;
		return (IField[])result.values().toArray(new IField[]{});
	}

	public IType getUpperBoundType() {
		return upperBoundType;
	}

	public void setUpperBoundType(IType upperBoundType) throws CoreException {
		this.upperBoundType = upperBoundType;
		if (hierarchy != null)
		{
			//we should rebuild hierarchy vector
			hierarchy = null;
			buildHierarchyVector();
		}
	}

    public IType getType() {
        return type;
    }
}
