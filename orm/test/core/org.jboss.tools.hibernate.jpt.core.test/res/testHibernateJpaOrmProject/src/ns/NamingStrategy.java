package ns;


public class NamingStrategy implements org.hibernate.cfg.NamingStrategy{
	
	private static final long serialVersionUID = 1L;
	
	private static final String CT_PREFIX = "ctn_";
	
	private static final String TN_PREFIX = "tn_";
	
	private static final String PC_PREFIX = "pc_";
	
	private static final String CN_PREFIX = "cn_";
	
	private static final String FK_PREFIX = "fk_";
	
	private static final String COL_PREFIX = "col_";
	
	private static final String JKCN_PREFIX = "jkcn_";
	
	public String classToTableName(String className) {
		return CT_PREFIX + unqualify(className);
	}
	
	public String propertyToColumnName(String propertyName) {
		return PC_PREFIX + unqualify(propertyName);
	}
	
	public String tableName(String tableName) {
		return TN_PREFIX + tableName;
	}
	
	public String collectionTableName(String ownerEntity,
			String ownerEntityTable, String associatedEntity,
			String associatedEntityTable, String propertyName) {
		return COL_PREFIX + unqualify(ownerEntity) + "_"+ associatedEntity+ "_" +
		associatedEntityTable + "_" + unqualify( associatedEntity ) +
		"_" +propertyName;
	}
	
	public String columnName(String columnName) {
		return CN_PREFIX + columnName;
	}
	
	public String joinKeyColumnName(String joinedColumn, String joinedTable) {
		return JKCN_PREFIX + joinedColumn + '_' + joinedTable;
	}
	
	public String foreignKeyColumnName(String propertyName,
			String propertyEntityName, String propertyTableName,
			String referencedColumnName) {
		return FK_PREFIX + propertyName + "_"+ unqualify(propertyEntityName)
		+ "_" + propertyTableName + "_" + referencedColumnName;
	}
	
	private String unqualify(String s) {
		if (s != null) return s;//.replaceAll("\\.", "");
		return "null";
	}

	public String logicalCollectionColumnName(String arg0, String arg1,
			String arg2) {
		return (arg0 + "_" + arg1 + "_" + arg2);
	}

	public String logicalCollectionTableName(String arg0, String arg1,
			String arg2, String arg3) {
		return (arg0 + "_" + arg1 + "_" + arg2 + "_" + arg3).toUpperCase();
	}

	public String logicalColumnName(String arg0, String arg1) {
		return (arg0 + "_" + arg1).toUpperCase();
	}

}
