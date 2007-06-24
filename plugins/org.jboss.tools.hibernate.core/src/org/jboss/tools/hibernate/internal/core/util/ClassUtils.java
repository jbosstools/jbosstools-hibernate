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
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.math.RandomUtils;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.internal.corext.codemanipulation.AddUnimplementedMethodsOperation;
//import org.eclipse.jdt.internal.corext.codemanipulation.ImportRewrite;
//import org.eclipse.jdt.internal.corext.codemanipulation.ImportsStructure;
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility2;
import org.eclipse.jdt.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.internal.corext.dom.NodeFinder;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
//import org.eclipse.jdt.internal.corext.util.WorkingCopyUtil;
import org.eclipse.jdt.internal.ui.preferences.JavaPreferencesSettings;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.jboss.tools.hibernate.core.CodeRendererService;
import org.jboss.tools.hibernate.core.IMapping;
import org.jboss.tools.hibernate.core.IMappingStorage;
import org.jboss.tools.hibernate.core.IOrmModel;
import org.jboss.tools.hibernate.core.IOrmProject;
import org.jboss.tools.hibernate.core.IPackage;
import org.jboss.tools.hibernate.core.IPersistentClass;
import org.jboss.tools.hibernate.core.IPersistentClassMapping;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.exception.ExceptionHandler;

/**
 * @author Nick
 *
 */
/**
 * @author kaa 
 * akuzmin@exadel.com
 */
public class ClassUtils {

	/**
	 * Return unquilified name
	 * @param qualifiedName
	 * @return
	 */
	public static String getUnqualifyName(String qualifiedName) {
		return qualifiedName.substring( qualifiedName.lastIndexOf(".") + 1 );
	}

	public static IType getSuperType(IType type) throws CoreException
	{
        if (type == null)
            return null;
        
		IType result = null;
		String superTypeName;
        superTypeName = type.getSuperclassName();
		if (superTypeName != null)
		{
			String fQSuperTypeName = ClassUtils.resolveTypeName(type,superTypeName);
			if (fQSuperTypeName != null)
				result = ScanProject.findClass(fQSuperTypeName,type.getJavaProject().getProject());
		}		
		return result;
	}
	
	/**
	 * checks if type is implementing interface
	 * @param type
	 * @param ifaceName
	 * @return
	 * @throws CoreException 
	 */
	public static boolean isImplementing(IType type, String ifaceName) throws CoreException
	{
		boolean result = false;
		if (type == null || ifaceName == null)
			return false;
		
		String[] names = type.getSuperInterfaceNames();
		
		for (int i = 0; i < names.length && !result ; i++) 
		{
			names[i] = ClassUtils.resolveTypeName(type,names[i]);
			if (names[i] != null)
			{
				if (names[i].equals(ifaceName))
					result = true;
				else
					result = isImplementing(ScanProject.findClass(names[i],type.getJavaProject().getProject()),ifaceName);
			}
		}
		
		return result || isImplementing(getSuperType(type),ifaceName);
	}


	public static String qualify(String prefix, String name) {
		if ( name == null || prefix == null ) {
			throw new NullPointerException();
		}
		return new StringBuffer( prefix.length() + name.length() + 1 )
				.append(prefix)
				.append('.')
				.append(name)
				.toString();
	}

	public static String resolveTypeName(IType type, String typeName) throws JavaModelException
	{

		if (typeName.equals("String")) {
			return "java.lang.String";
		} else if (typeName.equals("Integer")){
			return "java.lang.Integer";
		} else if (typeName.equals("Collection")){
			return "java.util.Collection";
		} else if (typeName.equals("Date")){
			return "java.util.Date";
		} else if (typeName.equals("Byte")){
			return "java.lang.Byte";
		} else if (typeName.equals("Short")){
			return "java.lang.Short";
		} else if (typeName.equals("BigDecimal")){
			return "java.math.BigDecimal";
		} else if (typeName.equals("Boolean")){
			return "java.lang.Boolean";
		} else if (typeName.equals("BigInteger")){
			return "java.math.BigInteger";
		} else if (typeName.equals("Long")){
			return "java.lang.Long";
		} else if (typeName.equals("Float")){
			return "java.lang.Float";
		}
		
			String[][] results = type.resolveType(typeName);
			if (results != null)
			{
				String[] segments = results[0];
				segments[1] = segments[1].replace('.','$');
				return Signature.toQualifiedName(segments);
			}
		
		//TODO check if resolving is neseccary
		return typeName;
	}
	
	/**
     * resolves type name to FQ name according to information provided in CompilationUnit for our class
     * @param classSource - ICompilationUnit of our class
     * @param typeSignature - standard type signature
     * @return FQ name of the type, preceded by a number(=array depth) of "[" if the type is array
     * @throws CoreException
     */
    public static String getQualifiedNameFromSignature(IType rootClass, String typeSignature) throws CoreException
    {
        StringBuffer array = new StringBuffer();
        int arrayDepth = Signature.getArrayCount(typeSignature);
        
        for (int i = 0; i < arrayDepth; i++) {
            array.append(Signature.C_ARRAY);
        }
        
		String fieldSignature = typeSignature;
		if (arrayDepth != 0)
			fieldSignature = Signature.getElementType(fieldSignature);
        try
        {
            fieldSignature = Signature.toString(fieldSignature);
        }
        catch (IllegalArgumentException e)
        {
            ExceptionHandler.logThrowableError(e,e.getMessage());
        }
        
        //primitive types don't support resolving
        if (isPrimitiveType(fieldSignature))
            return array+fieldSignature;
        
        //create fully qualified name
        if (rootClass != null)
        {
            String fqSignature = resolveTypeName(rootClass,fieldSignature);
            return array+fqSignature;
        }
        else
        {
            return fieldSignature;
        }
    }
    
    
    public static String[] getGenerifiedTypesFromSignature(IType resolveContext, String signature) throws CoreException
    {
        return null;
    }
        
    public static String getComplementarySimpleType(String typeName)
    {
    	if (typeName == null) return null;
    	
        if (typeName.equals("java.lang.Byte"))
            return "byte";
        if (typeName.equals("java.lang.Double"))
            return "double";
        if (typeName.equals("java.lang.Float"))
            return "float";
        if (typeName.equals("java.lang.Integer"))
            return "int";
        if (typeName.equals("java.lang.Long"))
            return "long";
        if (typeName.equals("java.lang.Short"))
            return "short";
        if (typeName.equals("java.lang.Character"))
            return "char";
        if (typeName.equals("java.lang.Boolean"))
            return "boolean";
        
        if (typeName.equals("byte"))
            return "java.lang.Byte";
        if (typeName.equals("double"))
            return "java.lang.Double";
        if (typeName.equals("float"))
            return "java.lang.Float";
        if (typeName.equals("int"))
            return "java.lang.Integer";
        if (typeName.equals("long"))
            return "java.lang.Long";
        if (typeName.equals("short"))
            return "java.lang.Short";
        if (typeName.equals("char"))
            return "java.lang.Character";
        if (typeName.equals("boolean"))
            return "java.lang.Boolean";
        
        return null;
    }
    
    public static boolean isPrimitiveType(String typeName)
    {
    	if (typeName == null) return false;    	
    	
        if (typeName.equals("byte"))
            return true;
        if (typeName.equals("double"))
            return true;
        if (typeName.equals("float"))
            return true;
        if (typeName.equals("int"))
            return true;
        if (typeName.equals("long"))
            return true;
        if (typeName.equals("short"))
            return true;
        if (typeName.equals("char"))
            return true;
        if (typeName.equals("boolean"))
            return true;
        return false;
    }
    
    public static final int G_NONE = 0;
    public static final int G_GETTER = 1;
    public static final int G_SETTER = 2;
    
    public static final String GDC_PUBLIC = "public";
    public static final String GDC_PRIVATE = "private";
    public static final String GDC_PROTECTED = "protected";

    public static final int BODY_JAVA = 1;
    public static final int BODY_HTML = 2;
    
    public static String formatLine(String line, int fmtType)
    {
        switch (fmtType)
        {
        	case (BODY_HTML):
        	{
        	    return "<p>"+line+"</p>";
        	}
        }
        return line;
    }
    
    public static String formatVarName(String name, int fmtType)
    {
        switch (fmtType)
        {
        	case (BODY_HTML):
        	    return "<b>"+name+"</b>";
        }
        return name;
    }
    
    
    public static String generateDefaultCtorBody(IType enclosingClass, String[] names, String[] types, String scope) throws CoreException
    {
        return generateDefaultCtorBody(enclosingClass,names,types,scope,BODY_JAVA);
    }
    
    public static String generateDefaultCtorBody(IType enclosingClass, String[] names, String[] types, String scope, int fmtType) throws CoreException
    {
        String body = formatLine("{"+StringUtils.EOL,fmtType);
        String superStr = "\t super();"+StringUtils.EOL;
        String paramStr = "(";
        if (names != null && types != null)
        {
            for (int i = 0; i < names.length; i++) {
                
                paramStr += types[i] + " " + names[i];
                superStr += "\t this."+names[i]+" = "+names[i]+";";
                if (i < (names.length - 1) )
                {
                    paramStr += ", ";
                    superStr += StringUtils.EOL;
                }
            }
        }
        paramStr += ")"+StringUtils.EOL;
        
        superStr = formatLine(superStr,fmtType);
        body += superStr;
        body += formatLine("}"+StringUtils.EOL,fmtType);
        
        String header = scope + " " + formatVarName(enclosingClass.getElementName(),fmtType) + paramStr;
        
        return formatLine(CodeRendererService.autoGenerated,fmtType) + formatLine(header,fmtType) + body + StringUtils.EOL;        
    }
    
    public static IPackageFragment getPackageFragment(ICompilationUnit unit) throws CoreException
    {
        IPackageFragment fragment = null;
        if (unit != null)
        {
            IJavaElement parent = unit.getParent();
            if (parent instanceof IPackageFragment)
            {
                fragment = (IPackageFragment) parent;
            }
        }
        return fragment;
    }

    public static List<IPackageFragment> getUnderlyingPackageFragments(IPackageFragment[] fragments)
    {
        List<IPackageFragment> result = new ArrayList<IPackageFragment>();
        
        for (int i = 0; i < fragments.length; i++) {
            try {
                IPackageFragment fragment = fragments[i];
                String packageName = fragment.getElementName();
                
                IJavaElement parent = fragment.getParent();
                if (parent instanceof IPackageFragmentRoot) {
                    IPackageFragmentRoot root = (IPackageFragmentRoot) parent;
                    
                    IJavaElement[] packages = root.getChildren();
                    for (int j = 0; j < packages.length; j++) {
                        IJavaElement element = packages[j];
                        if (element instanceof IPackageFragment) {
                            IPackageFragment underPackage = (IPackageFragment) element;
                            if (underPackage.getElementName().startsWith(packageName))
                                result.add(underPackage);
                        }
                    }
                }
            } catch (JavaModelException e) {
                ExceptionHandler.logThrowableError(e,e.getMessage());
            }
            
        }
        
        return result;
    }
    
    public static String getHolderClassName(String className)
    {
        int index = className.indexOf(".",0);
        if (index != -1)
        {
            return className.substring(0,index);
        }
        else
        {
            return className;
        }
    }
    
    public static void formatMember(IMember member,ICompilationUnit unit) throws CoreException
    {
		if (member == null || unit == null)
		    return;
        ISourceRange range = null;
		if (member != null)
			range = member.getSourceRange();
        
		int start = 0;
		int length = 0;
		if (range != null)
		{
			start = range.getOffset();
			length = range.getLength();
		}
		Document doc = new Document(unit.getBuffer().getContents());
		CodeFormatter cf = ToolFactory.createCodeFormatter(null);
		TextEdit te = cf.format(CodeFormatter.K_COMPILATION_UNIT,doc.get(),start,
				length,0,null);

			try {
                if (te != null)
                    te.apply(doc,TextEdit.UPDATE_REGIONS);
            } catch (MalformedTreeException e) {
				ExceptionHandler.logThrowableError(e, null);
			} catch (BadLocationException e) {
				ExceptionHandler.logThrowableError(e, null);
            }
		String newSource = doc.get();
		unit.getBuffer().setContents(newSource);
    }
    
	public static boolean hasClassErrors(IPersistentClass clazz) {
		IMarker[] markers = new IMarker[0]; 
		try {
			if (clazz.getSourceCode() == null) return true;
			if (clazz.getSourceCode().getResource() == null) return true;
			if (!clazz.getSourceCode().getResource().exists()) return true;			
			markers = clazz.getSourceCode().getResource().findMarkers(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER, false, IResource.DEPTH_ZERO);
		} catch (CoreException e) {
			ExceptionHandler.logThrowableError(e, "Error in hasClassErrors(...)");
		}
		for (int i = 0; i < markers.length; i++) {
			IMarker marker = markers[i];
			if (marker.getAttribute(IMarker.SEVERITY,0) == IMarker.SEVERITY_ERROR){
				return true;
			}
		}
		return false;
	}

	public static boolean hasPackageErrors(IPackage pakage) {
		IPersistentClass[] persistentClasses = pakage.getPersistentClasses();
		for (int i = 0; i < persistentClasses.length; i++) {
			IPersistentClass persistentClass = persistentClasses[i];
			if (hasClassErrors(persistentClass)){
				return true;
			}
		}
		return false;
	}

	public static boolean hasMappingClassErrors(IMapping mapping) {
		IPersistentClass[] persistentClasses = mapping.getPertsistentClasses();
		for (int i = 0; i < persistentClasses.length; i++) {
			IPersistentClass persistentClass = persistentClasses[i];
			if (hasClassErrors(persistentClass)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Method create random SerialVersionID 
	 * @param type
	 */
	public static void createSerialVersion(IType type) throws CoreException {
        Random rdmz = new Random();
        ICompilationUnit unit = type.getCompilationUnit();
        
        if (unit == null || !unit.exists())
            return ;
        Long value=new Long(RandomUtils.nextLong(rdmz));
        StringBuffer serialVersionBody = new StringBuffer();
        serialVersionBody.append(CodeRendererService.autoGenerated);
		serialVersionBody.append("private static final long serialVersionUID = "+value.toString()+"L;");
		serialVersionBody.append(StringUtils.EOL+"\t "+StringUtils.EOL);
    	if (!type.getField("serialVersionUID").exists())
    	{
    	    type.createField(serialVersionBody.toString(),
    	            null,true,null);
    	}
	}

    /**
     * Method generate method stubs for each of the abstract methods that 
     * the type will inherit from its superclass and implemented interfaces. 
     * @param type - IType
     * @throws CoreException
     */
    public static void createMethods(IType type) throws CoreException {
        IProgressMonitor monitor = new IProgressMonitor() {
            boolean isCanceled = false;
            public void beginTask(String name, int totalWork) {
            }
            public void done() {
            }
            public void internalWorked(double work) {
            }
            public boolean isCanceled() {
                return isCanceled;
            }
            public void setCanceled(boolean value) {
                isCanceled = value;
            }
            public void setTaskName(String name) {
            }
            public void subTask(String name) {
            }
            public void worked(int work) {

            }
        };

        ICompilationUnit cu=type.getCompilationUnit();
//        CodeGenerationSettings settings= 
        	JavaPreferencesSettings.getCodeGenerationSettings(type.getJavaProject());
        ITypeBinding binding= null;
        ASTParser parser= ASTParser.newParser(AST.JLS3);
        parser.setResolveBindings(true);
        parser.setSource(cu);
        CompilationUnit unit= (CompilationUnit) parser.createAST(new SubProgressMonitor(monitor, 1));
        if (type.isAnonymous()) {
            ClassInstanceCreation creation= (ClassInstanceCreation) ASTNodes.getParent(NodeFinder.perform(unit, type.getNameRange()), ClassInstanceCreation.class);
            if (creation != null)
                binding= creation.resolveTypeBinding();
        } else {
            AbstractTypeDeclaration declaration= (AbstractTypeDeclaration) ASTNodes.getParent(NodeFinder.perform(unit, type.getNameRange()), AbstractTypeDeclaration.class);
            if (declaration != null)
                binding= declaration.resolveBinding();
        }
        if (binding != null) {
       		IMethodBinding[] methodBindings = StubUtility2.getUnimplementedMethods(binding);        	
        		AddUnimplementedMethodsOperation operation = new AddUnimplementedMethodsOperation(unit, binding, methodBindings, -1, true, true, true);                
                operation.run(monitor);
        }
        JavaModelUtil.reconcile(cu);
    }    


    public static boolean isESGenerated(final IMember member)
    {
        final class Holder
        {
            boolean value;
        }
        final Holder result = new Holder();
        result.value = false;
        
        ASTParser parser = ASTParser.newParser(AST.JLS3);
        try {
            if (member.getSource() != null)
                parser.setSource(member.getSource().toCharArray());
            parser.setKind(ASTParser.K_CLASS_BODY_DECLARATIONS);
            
            ASTNode node = parser.createAST(null);

            ASTVisitor visitor = new ASTVisitor(true)
            {
                ASTNode firstBodyNode = null;
                
                public void preVisit(ASTNode node) {
                    if (node instanceof BodyDeclaration && firstBodyNode == null)
                    {
                        firstBodyNode = node;
                    }
                    super.preVisit(node);
                }
                
                /* (non-Javadoc)
                 * @see org.eclipse.jdt.core.dom.ASTVisitor#visit(org.eclipse.jdt.core.dom.Javadoc)
                 */
                public boolean visit(Javadoc node) {
                    ASTNode parent = node.getParent();
                    if (parent == null)
                        return false;
                        
                    if (parent.getParent() != firstBodyNode)
                        return false;
                    
                    List tags = node.tags();
                    
                    Iterator itrTags = tags.iterator();
                    while (itrTags.hasNext())
                    {
                        Object o = itrTags.next();
                        if (o instanceof TagElement) {
                            TagElement element = (TagElement) o;
                            
                            if (CodeRendererService.esGenerated.equals(element.getTagName()))
                            {
                                result.value = true;
                                break;
                            }
                        }
                    }
                    return false;
                }
            };
            
            node.accept(visitor);
            
        } catch (JavaModelException e) {
            ExceptionHandler.logThrowableWarning(e ,e.getMessage());
        }
        
        return result.value;
    }
    
    public static void implementsNewInterface(ICompilationUnit cu,String[] NewInterfaces)
    {
 		try {    	
    	   ASTParser parser = ASTParser.newParser(AST.JLS3);
    	   parser.setSource(cu);
    	   CompilationUnit astRoot = (CompilationUnit) parser.createAST(null);
    	   String source;

			source = cu.getBuffer().getContents();
    	   Document document= new Document(source);
    	   ASTRewrite rewrite = ASTRewrite.create(astRoot.getAST());
    	   SimpleName typeName = astRoot.getAST().newSimpleName(cu.getElementName().split("\\.")[0]);
		   AST ast= ((TypeDeclaration)astRoot.types().get(0)).getAST();
    	   TypeDeclaration oldName = (TypeDeclaration)astRoot.types().get(0);
    	   TypeDeclaration newName = astRoot.getAST().newTypeDeclaration();
    	   newName.setName(typeName);
//    	   newName.setSuperclassType(newSt);
    	   for(int i=0;i<NewInterfaces.length;i++)
    	   {
    		   SimpleType newSt = ast.newSimpleType(ast.newSimpleName(NewInterfaces[i]));
    		   newName.superInterfaceTypes().add(newSt);
    	   }
    	   rewrite.replace(oldName, newName, null);
    	   TextEdit edits = rewrite.rewriteAST(document, cu.getJavaProject().getOptions(true));
    	   edits.apply(document);
    	   String newSource = document.get();
    	   cu.getBuffer().setContents(newSource);
		} catch (JavaModelException e) {
			ExceptionHandler.logThrowableError(e, null);
		} catch (MalformedTreeException e) {
			ExceptionHandler.logThrowableError(e, null);
		} catch (BadLocationException e) {
			ExceptionHandler.logThrowableError(e, null);
		}
    	   
    }
    
    /**
     * @author yan
     * @param pc - persistent class
     * @return array of persistent class mappings
     */
    public static IMapping[] getReferencedMappings(IPersistentClass pc) {
   	 ArrayList<IMapping> list = new ArrayList<IMapping>();
 		IOrmModel model=OrmCore.getDefault().getOrmModel();
		if (model!=null) {
			IOrmProject[] projects=model.getOrmProjects();
			for(int i=0; i<projects.length; i++) {
				IMapping[] maps=projects[i].getMappings();
				for(int j=0; j<maps.length; j++) {
					IPersistentClass tpc=maps[j].findClass(pc.getName());
					if (tpc!=null) {
						IPersistentClassMapping tpcClassMapping=tpc.getPersistentClassMapping();
						IPersistentClassMapping pcClassMapping=pc.getPersistentClassMapping();
						if (tpcClassMapping!=null && pcClassMapping!=null) {
							IMappingStorage tpcSt=tpcClassMapping.getStorage();
							IMappingStorage pcSt=pcClassMapping.getStorage();
							if (tpcSt!=null && pcSt!=null) {
								IResource tpcRes=tpcSt.getResource();
								if (tpcRes!=null && tpcRes.equals(pcSt.getResource())) {
									list.add(maps[j]);
									continue;
								}
							}
							
						}
						
					}
				}
			}
		}
		
		return list.toArray(new IMapping[0]);   	 
    }
    
}
