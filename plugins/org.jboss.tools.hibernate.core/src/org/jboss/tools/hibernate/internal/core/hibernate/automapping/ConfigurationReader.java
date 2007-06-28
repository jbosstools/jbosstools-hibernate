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
package org.jboss.tools.hibernate.internal.core.hibernate.automapping;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.hibernate.core.CodeRendererService;
import org.jboss.tools.hibernate.core.ICodeRendererService;
import org.jboss.tools.hibernate.core.IDatabaseColumn;
import org.jboss.tools.hibernate.core.IOrmConfiguration;
import org.jboss.tools.hibernate.core.IOrmElement;
import org.jboss.tools.hibernate.core.OrmCore;
import org.jboss.tools.hibernate.core.IAutoMappingService.Settings;
import org.jboss.tools.hibernate.core.exception.NestableRuntimeException;
import org.jboss.tools.hibernate.core.hibernate.Type;
import org.jboss.tools.hibernate.internal.core.AbstractMapping;
import org.jboss.tools.hibernate.internal.core.CodeRendererServiceWrapper;
import org.jboss.tools.hibernate.internal.core.ExtensionBinder;
import org.jboss.tools.hibernate.internal.core.OrmConfiguration;
import org.jboss.tools.hibernate.internal.core.OrmPropertyDescriptorsHolder;
import org.jboss.tools.hibernate.internal.core.PersistentField;
import org.jboss.tools.hibernate.internal.core.hibernate.HibernateConfiguration;
import org.jboss.tools.hibernate.internal.core.hibernate.automapping.RangeParser.Range;
import org.jboss.tools.hibernate.internal.core.util.ClassUtils;
import org.jboss.tools.hibernate.internal.core.util.ScanProject;

/**
 * @author Nick
 *
 */
public class ConfigurationReader {
	
	private static ConfigurationReader instance = new ConfigurationReader();
    
	private AbstractMapping mapping;
	private HibernateConfiguration config;

	private int propertyAccessorMask = PersistentField.ACCESSOR_NONE;
	private int idAccessorMask = PersistentField.ACCESSOR_NONE;
	
	private String unindexedCollectionName = null;
	private String collectionElementsType = null;
	
	private String identifierColumnName = null;
	
	// add tau 07.08.2006
	private String identifierQuery = null;	
	
	private String versionColumnName = null;
	private String discriminatorColumnName = null;
//	private String optimisticLockMode = null;
	private String cascade = null;
    private String associationsCascade = null;
    
    private Boolean lazy = null;
    private boolean cascadeDeleteEnabled = false;
    private boolean collectionLazy = true;
    
	private Type idType;
	private Type versionType;
	private Type discriminatorType;
    private int inheritance = 0;
	
    public static final int TABLE_PER_HIERARCHY = 1;
    public static final int TABLE_PER_SUBCLASS = 2;
    public static final int TABLE_PER_CLASS = 3;

    private String discriminator_formula;
    
    private String STR_LONG;
    private String STR_SHORT;
    private String STR_INTEGER;
    private String STR_BYTE;
    private String STR_FLOAT;
    private String STR_DOUBLE;
    private String STR_CHARACTER;
    private String STR_BOOLEAN;
    private String STR_TIME;
    private String STR_DATE;
    private String STR_TIMESTAMP;
    private String STR_CALENDAR;
    private String STR_CALENDAR_DATE;
    
    private NumericsConversion numericsConversionInstance;
    private boolean convertNumericsToPrimitive = false;
    
    private String reachability;
    private boolean reachabilityReachEverything;
    
    private boolean discoverLinkTables = true;
    
//    private Settings settings;
    private Type numeric1_0_Type = Type.BOOLEAN;
    
    private IType reversingBaseClass;
    
    private boolean preserveNativeSQLTypes;
    
    // #added# by Konstantin Mishin on 30.11.2005 fixed for ESORM-401
    private boolean idGeneratorQuality;
    // #added#

    private double fuzzinessSimilarityLevel = 0.8;
    private boolean useFuzzySearch = false;
    
    private Set<String> ignoredNamesSet;
    
    private static OrmPropertyDescriptorsHolder ormPropertyDescriptorsHolder;
    private static boolean save = false;
    
    private String tablePrefixQuery = null;
    
    private ICodeRendererService pojoRenderer = null;
    
	private ConfigurationReader()
	{
		super();
	}
	
    public static ConfigurationReader getAutomappingConfigurationInstance(AbstractMapping mapping)
    {
        return getAutomappingConfigurationInstance(mapping,Settings.DEFAULT_SETTINGS);
    }

    static class NumericsConversion {
        private ArrayList<Range[]> ranges = new ArrayList<Range[]>();

        NumericsConversion(String propertyValue) {            
            String[] pairs = propertyValue.split(";");
            
            try {
            	for (int i = 0; i < pairs.length; i++) {
            		String pair = pairs[i].trim();
                
            		if (pair.length() == 0)
            			continue;
                                        
                    String[] values = pair.split(",");
                    if (values.length <= 2 && values.length > 0)
                    {
                        Range length = RangeParser.getRange(values[0].trim());
                        String precStr = "";

                        if (values.length > 1)
                        {
                            precStr = values[1].trim();
                        }

                        Range prec = RangeParser.getRange(precStr);
                        
                        if (length != null && prec != null)
                        {
                            ranges.add(new Range[]{length,prec});
                        }
                    }
                    else if (values.length == 0)
                        ranges.add(new Range[]{RangeParser.getRange(""),RangeParser.getRange("")});
                        
                    
               }
            } catch (Exception e) {
            	OrmCore.getPluginLog().logError(e.getMessage(),e);
                instance.config.getOrmProject().getOrmConfiguration().setProperty(OrmConfiguration.REVTYPE_NUMERIC_X_Y,
                		(String)ormPropertyDescriptorsHolder.getDefaultPropertyValue(OrmConfiguration.REVTYPE_NUMERIC_X_Y));
                save = true;

            }
            
        }
        
        private boolean shouldConvert(int length, int precision)
        {
            boolean found = false;
            Iterator lItr = ranges.iterator();
            
            while (lItr.hasNext() && !found)
            {
                Object o = lItr.next();
                if (o instanceof Range[]) {
                    Range[] ranges = (Range[]) o;
                    if (ranges[0].isInRange(length) && ranges[1].isInRange(precision))
                        found = true;
                }
            }
            
            return !found;
        }
    }
    
    
    public static ConfigurationReader getAutomappingConfigurationInstance(AbstractMapping mapping, Settings settings)
	{
		instance.mapping = mapping;   
		instance.config = (HibernateConfiguration) instance.mapping.getConfiguration();
		
        //instance.settings = settings;
        
		IOrmConfiguration ormConfig = instance.config.getOrmProject().getOrmConfiguration();
		ormPropertyDescriptorsHolder = OrmPropertyDescriptorsHolder.getInstance(instance.config.getOrmProject());
		//akuzmin 22.07.2005
		((OrmConfiguration)ormConfig).setPropertyDescriptorsHolder(ormPropertyDescriptorsHolder);		
		instance.unindexedCollectionName = ormConfig.getProperty(OrmConfiguration.HAM_SAFE_COLLECTION);
		String inheritanceStyle = ormConfig.getProperty(OrmConfiguration.HIBERNATE_INHERITANCE);
		if (inheritanceStyle.equals(OrmConfiguration.TABLE_PER_CLASS))
			instance.inheritance = TABLE_PER_CLASS;
		else if (inheritanceStyle.equals(OrmConfiguration.TABLE_PER_HIERARCHY))
			instance.inheritance = TABLE_PER_HIERARCHY;
		else if (inheritanceStyle.equals(OrmConfiguration.TABLE_PER_SUBCLASS))
			instance.inheritance = TABLE_PER_SUBCLASS;
		
		String idTypeString = ormConfig.getProperty(OrmConfiguration.HIBERNATE_ID_DATATYPE);
		instance.idType = Type.getType(idTypeString);
		
		String versionTypeString = ormConfig.getProperty(OrmConfiguration.HIBERNATE_VERSION_DATATYPE);
		instance.versionType = Type.getType(versionTypeString);
		
		String discriminatorTypeString = ormConfig.getProperty(OrmConfiguration.HIBERNATE_DISCRIMINATOR_DATATYPE);
		instance.discriminatorType = Type.getType(discriminatorTypeString);
		
		instance.collectionElementsType = ormConfig.getProperty(OrmConfiguration.HAM_COLLECTION_ELEMENT_TYPE);
//		String accessName = ormConfig.getProperty(OrmConfiguration.HIBERNATE_ACCESS);
//		if (!"property".equals(accessName) || (accessName == null && !OrmConfiguration.DEFAULT_ACCESS.equals("property")))
//			instance.propertyAccessorMask = PersistentField.ACCESSOR_FIELD;
//		else
//			instance.propertyAccessorMask = PersistentField.ACCESSOR_PROPERTY;
		

        String hamAccessorName = ormConfig.getProperty(OrmConfiguration.HAM_ACCESSOR);
        
        if (OrmConfiguration.HAM_ACCESSOR_VALUES[0].equals(hamAccessorName))
            instance.propertyAccessorMask = PersistentField.ACCESSOR_PROPERTY;
        else
            instance.propertyAccessorMask = PersistentField.ACCESSOR_FIELD|PersistentField.ACCESSOR_PROPERTY;
        
        // added by Nick 09.06.2005 - property accessor mask is not needed when auto-map
//        instance.propertyAccessorMask = PersistentField.ACCESSOR_FIELD|PersistentField.ACCESSOR_PROPERTY;
        // by Nick
        
/*		String idAccessName = ormConfig.getProperty(OrmConfiguration.DEFAULT_ACCESS);
		if (!"property".equals(accessName) || (accessName == null && !OrmConfiguration.DEFAULT_ACCESS.equals("property")))
			instance.propertyAccessorMask = PersistentField.ACCESSOR_FIELD;
		else
			instance.propertyAccessorMask = PersistentField.ACCESSOR_PROPERTY;
*/		
		
		
		instance.identifierColumnName = ormConfig.getProperty(OrmConfiguration.IDENTIFIER_COLUMN_NAME);
		
		//add tau 07.08.2006
		instance.identifierQuery = ormConfig.getProperty(OrmConfiguration.IDENTIFIER_QUERY);		
		
		instance.versionColumnName = ormConfig.getProperty(OrmConfiguration.VERSION_COLUMN_NAME);
		
		instance.discriminatorColumnName = ormConfig.getProperty(OrmConfiguration.DISCRIMINATOR_COLUMN_NAME);
		
        instance.cascade = ormConfig.getProperty(OrmConfiguration.HIBERNATE_CASCADE);
        
        instance.associationsCascade = ormConfig.getProperty(OrmConfiguration.HIBERNATE_ASSOCIATIONS_CASCADE);
        
        String lazyString = ormConfig.getProperty(OrmConfiguration.HIBERNATE_ASSOCIATIONS_LAZY);
        if (!(lazyString.equals("proxy")))
        {
//            boolean isLazy;
            try {
                instance.lazy = Boolean.valueOf(lazyString);
            } catch (Exception e) {
            	OrmCore.getPluginLog().logError("Exception parsing hibernate.associations.lazy parameter",e);
                ormConfig.setProperty(OrmConfiguration.HIBERNATE_ASSOCIATIONS_LAZY,
                		(String)ormPropertyDescriptorsHolder.getDefaultPropertyValue(OrmConfiguration.HIBERNATE_ASSOCIATIONS_LAZY));
                save = true;

            }
        }
        
        String keyOnDeleteString = ormConfig.getProperty(OrmConfiguration.HIBERNATE_KEY_ON_DELETE);
        try {
            Boolean bool = Boolean.valueOf(keyOnDeleteString);
            if (bool != null)
                instance.cascadeDeleteEnabled = bool.booleanValue();
        } catch (Exception e) {
        	OrmCore.getPluginLog().logError("Exception parsing "+OrmConfiguration.HIBERNATE_KEY_ON_DELETE+" parameter",e);
            ormConfig.setProperty(OrmConfiguration.HIBERNATE_KEY_ON_DELETE,
            		(String)ormPropertyDescriptorsHolder.getDefaultPropertyValue(OrmConfiguration.HIBERNATE_KEY_ON_DELETE));
            save = true;
        }
        
        // #added# by Konstantin Mishin on 30.11.2005 fixed for ESORM-401
        String idGeneratorQuality = ormConfig.getProperty(OrmConfiguration.HIBERNATE_ID_GENERATOR_QUALITY);
        try {
            Boolean bool = Boolean.valueOf(idGeneratorQuality);
            if (bool != null)
                instance.idGeneratorQuality = bool.booleanValue();
        } catch (Exception e) {
        	OrmCore.getPluginLog().logError("Exception parsing "+OrmConfiguration.HIBERNATE_ID_GENERATOR_QUALITY+" parameter",e);
            ormConfig.setProperty(OrmConfiguration.HIBERNATE_ID_GENERATOR_QUALITY,
            		(String)ormPropertyDescriptorsHolder.getDefaultPropertyValue(OrmConfiguration.HIBERNATE_ID_GENERATOR_QUALITY));
            save = true;
        }
        // #added#
        
        String fuzzinessSimilarityStr = ormConfig.getProperty(OrmConfiguration.HAM_QUERY_FUZZINESS);
        try
        {
            instance.fuzzinessSimilarityLevel = Double.valueOf(fuzzinessSimilarityStr).doubleValue();
            if (instance.fuzzinessSimilarityLevel < 0 || instance.fuzzinessSimilarityLevel > 1)
            {
                instance.fuzzinessSimilarityLevel = 0.8;
                throw new NestableRuntimeException("Fuzziness level should be in [0...1]");
            }
        }
        catch (Exception e)
        {
        	OrmCore.getPluginLog().logError("Exception parsing "+OrmConfiguration.HAM_QUERY_FUZZINESS+" parameter",e);
            ormConfig.setProperty(OrmConfiguration.HAM_QUERY_FUZZINESS,
            		(String)ormPropertyDescriptorsHolder.getDefaultPropertyValue(OrmConfiguration.HAM_QUERY_FUZZINESS));
            save = true;
            
        }
        
        instance.STR_LONG = ormConfig.getProperty("hibernate.type.long");
        
        instance.STR_SHORT = ormConfig.getProperty("hibernate.type.short");
        
        instance.STR_INTEGER = ormConfig.getProperty("hibernate.type.int");
        
        instance.STR_BYTE = ormConfig.getProperty("hibernate.type.byte");
        
        instance.STR_FLOAT = ormConfig.getProperty("hibernate.type.float");
        
        instance.STR_DOUBLE = ormConfig.getProperty("hibernate.type.double");
        
        instance.STR_CHARACTER = ormConfig.getProperty("hibernate.type.character");
        
        instance.STR_BOOLEAN = ormConfig.getProperty("hibernate.type.boolean");
        
        instance.STR_TIME = ormConfig.getProperty("hibernate.type.time");
        
        instance.STR_DATE = ormConfig.getProperty("hibernate.type.date");
        
        instance.STR_TIMESTAMP = ormConfig.getProperty("hibernate.type.timestamp");
        
        instance.STR_CALENDAR = ormConfig.getProperty("hibernate.type.calendar");
        
        instance.STR_CALENDAR_DATE = ormConfig.getProperty("hibernate.type.calendar_date");
                
        instance.discriminator_formula = ormConfig.getProperty("hibernate.discriminator.formula");

        instance.reachability = ormConfig.getProperty(OrmConfiguration.HAM_REACHABILITY);
        
        if (OrmConfiguration.HAM_REACHABILITY_VALUES[0].equals(instance.reachability))
            instance.reachabilityReachEverything = true;
        else
            instance.reachabilityReachEverything = false;
        
        String numericXY = ormConfig.getProperty(OrmConfiguration.REVTYPE_NUMERIC_X_Y);
        
        instance.numericsConversionInstance = new NumericsConversion(numericXY);

        String numeric_1_0 = ormConfig.getProperty(OrmConfiguration.REVTYPE_NUMERIC_1_0);
        
        try {
            instance.numeric1_0_Type = Type.getType(numeric_1_0);
            if (instance.numeric1_0_Type == null)
            {
                throw new NestableRuntimeException("Error parsing "+ OrmConfiguration.REVTYPE_NUMERIC_1_0 + " parameter");
            }
        } catch (Exception e) {
        	OrmCore.getPluginLog().logError(e.getMessage(),e);
            ormConfig.setProperty(OrmConfiguration.REVTYPE_NUMERIC_1_0,
            		(String)ormPropertyDescriptorsHolder.getDefaultPropertyValue(OrmConfiguration.REVTYPE_NUMERIC_1_0));
            save = true;
        }
        
        String numeric_convert = ormConfig.getProperty(OrmConfiguration.REVTYPE_NUMERIC_CONVERT);
        
        try {
            instance.convertNumericsToPrimitive = Boolean.valueOf(numeric_convert).booleanValue();
        } catch (Exception e) {
        	OrmCore.getPluginLog().logError(e.getMessage(),e);
            ormConfig.setProperty(OrmConfiguration.REVTYPE_NUMERIC_CONVERT,
            		(String)ormPropertyDescriptorsHolder.getDefaultPropertyValue(OrmConfiguration.REVTYPE_NUMERIC_CONVERT));
            save = true;
        }
        
        String preserve_native_types = ormConfig.getProperty(OrmConfiguration.REVERSING_NATIVE_SQL_TYPES);
        
        try {
            instance.preserveNativeSQLTypes = Boolean.valueOf(preserve_native_types).booleanValue();
        } catch (Exception e) {
        	OrmCore.getPluginLog().logError(e.getMessage(),e);
            ormConfig.setProperty(OrmConfiguration.REVERSING_NATIVE_SQL_TYPES,
                    (String)ormPropertyDescriptorsHolder.getDefaultPropertyValue(OrmConfiguration.REVERSING_NATIVE_SQL_TYPES));
            save = true;
        }

        String link_tables = ormConfig.getProperty(OrmConfiguration.HAM_LINK_TABLES);
        instance.discoverLinkTables = OrmConfiguration.HAM_LINK_TABLES_DEFAULT.equals(link_tables);
        
        String collection_lazy = ormConfig.getProperty(OrmConfiguration.HIBERNATE_COLLECTIONS_LAZY);
        
        try {
            instance.collectionLazy = Boolean.valueOf(collection_lazy).booleanValue();
        } catch (Exception e) {
        	OrmCore.getPluginLog().logError(e.getMessage(),e);
            ormConfig.setProperty(OrmConfiguration.HIBERNATE_COLLECTIONS_LAZY,
            		(String)ormPropertyDescriptorsHolder.getDefaultPropertyValue(OrmConfiguration.HIBERNATE_COLLECTIONS_LAZY));
            save = true;
       }
        
        String baseClassName = ormConfig.getProperty(OrmConfiguration.REVERSING_BASE_CLASS);
        try {
            instance.reversingBaseClass = ScanProject.findClass(baseClassName,mapping.getProject().getProject());
        } catch (CoreException e) {
        	OrmCore.getPluginLog().logError(e.getMessage(),e);
            ormConfig.setProperty(OrmConfiguration.REVERSING_BASE_CLASS,
            		(String)ormPropertyDescriptorsHolder.getDefaultPropertyValue(OrmConfiguration.REVERSING_BASE_CLASS));
            save = true;
        }
        
        instance.ignoredNamesSet = new HashSet<String>();
        String ignoredNames = ormConfig.getProperty(OrmConfiguration.HAM_IGNORE_LIST);
        
        String[] ignoredNamesSplit = ignoredNames.split("\\ ");
        for (int i = 0; i < ignoredNamesSplit.length; i++) {
            String string = ignoredNamesSplit[i];
            instance.ignoredNamesSet.add(string.toLowerCase());
        }
        
        String fuzzyOnStr = ormConfig.getProperty(OrmConfiguration.HAM_QUERY_FUZZY_ON);
        
        try {
            instance.useFuzzySearch = Boolean.valueOf(fuzzyOnStr).booleanValue();
        } catch (Exception e) {
        	OrmCore.getPluginLog().logError("Exception parsing "+OrmConfiguration.HAM_QUERY_FUZZY_ON+" parameter",e);
            ormConfig.setProperty(OrmConfiguration.HAM_QUERY_FUZZY_ON,
            		(String)ormPropertyDescriptorsHolder.getDefaultPropertyValue(OrmConfiguration.HAM_QUERY_FUZZY_ON));
            save = true;
        }

        try {
            String prefQuery = ormConfig.getProperty(OrmConfiguration.TABLE_PREFIX_QUERY);
            if (prefQuery != null && prefQuery.length() != 0)
            {
                Pattern.compile("^"+prefQuery);
            }
            instance.tablePrefixQuery = prefQuery;
        } catch (Exception e) {
        	OrmCore.getPluginLog().logError("Exception parsing "+OrmConfiguration.TABLE_PREFIX_QUERY+" parameter",e);
            ormConfig.setProperty(OrmConfiguration.TABLE_PREFIX_QUERY,
                    (String)ormPropertyDescriptorsHolder.getDefaultPropertyValue(OrmConfiguration.TABLE_PREFIX_QUERY));
            save = true;
        }
        
        ICodeRendererService service = ExtensionBinder.getCodeRendererService(ormConfig.getProperty(OrmConfiguration.POJO_RENDERER));
        if (service == null) {
            instance.pojoRenderer = new CodeRendererServiceWrapper(new CodeRendererService());
            ormConfig.setProperty(OrmConfiguration.POJO_RENDERER,
                    (String)ormPropertyDescriptorsHolder.getDefaultPropertyValue(OrmConfiguration.POJO_RENDERER));
            save = true;
        } else {
            instance.pojoRenderer = new CodeRendererServiceWrapper(service);
        }

        if(save)
        	try {
        		ormConfig.save();
        		save=false;
        	} catch (Exception e) {
        		OrmCore.getPluginLog().logError(e.getMessage(),e);
        	}
        
        return instance;
	}

	public Type getDiscriminatorType() {
		return discriminatorType;
	}

	public Type getIdType() {
		return idType;
	}

	public int getInheritance() {
		return inheritance;
	}

	public Type getVersionType() {
		return versionType;
	}

	public String getUnindexedCollectionName() {
		return unindexedCollectionName;
	}

	public int getIdAccessorMask() {
		return idAccessorMask;
	}

	public int getPropertyAccessorMask() {
		return propertyAccessorMask;
	}

	public String getCollectionElementsType() {
		return collectionElementsType;
	}

	public AbstractMapping getMapping() {
		return mapping;
	}

	public String getDiscriminatorColumnName() {
		return discriminatorColumnName;
	}

	public String getIdentifierColumnName() {
		return identifierColumnName;
	}
	
	//add tau 07.08.2006
	public String getIdentifierQuery() {
		return identifierQuery;
	}	

	public String getVersionColumnName() {
		return versionColumnName;
	}

    public String getCascade() {
        return cascade;
    }

    public String getAssociationsCascade() {
        return associationsCascade;
    }

    public Boolean getLazy() {
        return lazy;
    }

    public boolean isCascadeDeleteEnabled() {
        return cascadeDeleteEnabled;
    }
    
    public String getJavaTypeName(Type type) throws CoreException
    {
        if (type == null)
            return null;
        
        String result = null;
        
        if (type == Type.LONG)
            result = STR_LONG;
        if (type == Type.SHORT)
            result = STR_SHORT;
        if (type == Type.INTEGER)
            result = STR_INTEGER;
        if (type == Type.BYTE)
            result = STR_BYTE;
        if (type == Type.FLOAT)
            result = STR_FLOAT;
        if (type == Type.DOUBLE)
            result = STR_DOUBLE;
        if (type == Type.CHARACTER)
            result = STR_CHARACTER;
        if (type == Type.BOOLEAN)
            result = STR_BOOLEAN;

        if (type == Type.TIME)
            result = STR_TIME;
        if (type == Type.TIMESTAMP)
            result = STR_TIMESTAMP;
        if (type == Type.DATE)
            result = STR_DATE;
        if (type == Type.CALENDAR)
            result = STR_CALENDAR;
        if (type == Type.CALENDAR_DATE)
            result = STR_CALENDAR_DATE;
        
        if (result != null && result.length() != 0)
        {
            if (Character.isUpperCase(result.charAt(0)) && result.indexOf('.') == -1)
                result = "java.lang."+result;
            return result;
        }
        
        Class clazz = type.getJavaType();
        if (clazz == null)
            return null;
        else
        {
            String nameStr = clazz.getName();
            Class cmpClass = clazz.getComponentType();
            if (cmpClass != null)
            {
                return ClassUtils.getQualifiedNameFromSignature(null,nameStr);
            }
            else
            {
                return nameStr;
            }
        }
    }

    public String getDiscriminatorFormula() {
        return discriminator_formula;
    }

    public boolean isReachabilityReachEverything() {
        return reachabilityReachEverything;
    }

//    public Settings getSettings() {
//        return settings;
//    }
    
    public boolean numericShouldConvert(IDatabaseColumn column)
    {
        if (column == null)
            return false;
            
        if (column.getSqlTypeCode() != Types.NUMERIC && column.getSqlTypeCode() != Types.DECIMAL)
            return false;
            
        return instance.convertNumericsToPrimitive && instance.numericsConversionInstance.shouldConvert(column.getPrecision(),column.getScale());
    }

    public Type getNumeric1_0_Type() {
        return numeric1_0_Type;
    }

    boolean isCollectionLazy() {
        return collectionLazy;
    }

    IType getReversingBaseClass() {
        return reversingBaseClass;
    }
    
    boolean isIgnored(IOrmElement elt)
    {
        if (ignoredNamesSet != null && elt != null && elt.getName() != null)
        {
            return ignoredNamesSet.contains(elt.getName().toLowerCase());
        }
        else
        {
            return false;
        }
    }

    double getFuzzinessSimilarityLevel() {
        return fuzzinessSimilarityLevel;
    }

    boolean isUseFuzzySearch() {
        return useFuzzySearch;
    }

    public boolean isPreserveNativeSQLTypes() {
        return preserveNativeSQLTypes;
    }

    boolean isDiscoverLinkTables() {
        return discoverLinkTables;
    }

    String getTableNamePrefix()
    {
        return tablePrefixQuery;
    }
//akuzmin 12.10.2005
    public ICodeRendererService getPojoRenderer() {
        return pojoRenderer;
    }

    // #added# by Konstantin Mishin on 30.11.2005 fixed for ESORM-401
    public boolean isIdGeneratorQuality() {
		return idGeneratorQuality;
	}
    // #added#
}
