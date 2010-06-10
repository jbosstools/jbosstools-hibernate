package ns;

import org.hibernate.cfg.DefaultNamingStrategy;

public class NamingStrategy extends DefaultNamingStrategy{
	
	private static final long serialVersionUID = 1L;
	
	private static final String CT_PREFIX = "ctn_";
	
	private static final String TN_PREFIX = "tn_";
	
	private static final String PC_PREFIX = "pc_";
	
	private static final String CN_PREFIX = "cn_";
	
	private static final String FK_PREFIX = "fk_";
	
	private static final String COL_PREFIX = "col_";
	
	private static final String JKCN_PREFIX = "jkcn_";
	
	@Override
	public String classToTableName(String className) {
		return CT_PREFIX + unqualify(className);
	}
	
	public String propertyToColumnName(String propertyName) {
		return PC_PREFIX + unqualify(propertyName);
	}
	
	@Override
	public String tableName(String tableName) {
		return TN_PREFIX + tableName;
	}
	
	@Override
	public String collectionTableName(String ownerEntity,
			String ownerEntityTable, String associatedEntity,
			String associatedEntityTable, String propertyName) {
		return COL_PREFIX + unqualify(ownerEntity) + "_"+ associatedEntity+ "_" +
		associatedEntityTable + "_" + unqualify( associatedEntity ) +
		"_" +propertyName;
	}
	
	@Override
	public String columnName(String columnName) {
		return CN_PREFIX + columnName;
	}
	
	@Override
	public String joinKeyColumnName(String joinedColumn, String joinedTable) {
		return JKCN_PREFIX + joinedColumn + '_' + joinedTable;
	}
	
	@Override
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

}
