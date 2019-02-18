package annotations.database.schema;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class ReferenceSystemProcessType implements Serializable {

	/**
	 * host-pathogen,host-pathogen-disease,host-pathogen-disease-chemical,host-pathogen-chemical,pathogen-disease
	 */
	private static final long serialVersionUID = 1L;
	
	private static final Map<String, ReferenceSystemProcessType> types = 
		new HashMap<String, ReferenceSystemProcessType>(0);

	/**
	 * Create list of all cases according the column type:
	 * enum('host-pathogen','host-pathogen-disease','host-pathogen-disease-chemical','host-pathogen-chemical','pathogen-disease')
	 */
	private static final ReferenceSystemProcessType host_pathogen = new ReferenceSystemProcessType("host-pathogen");
	private static final ReferenceSystemProcessType host_pathogen_disease = new ReferenceSystemProcessType("host-pathogen-disease");
	private static final ReferenceSystemProcessType host_pathogen_disease_chemical = new ReferenceSystemProcessType("host-pathogen-disease-chemical");
	private static final ReferenceSystemProcessType host_pathogen_chemical = new ReferenceSystemProcessType("host-pathogen-chemical");
	private static final ReferenceSystemProcessType pathogen_disease = new ReferenceSystemProcessType("pathogen-disease");
	
	private String name;
	
	public ReferenceSystemProcessType(String name) {
		this.name = name;
		ReferenceSystemProcessType.types.put(name, this);
	}
	
	public static ReferenceSystemProcessType valueOf(String name) {
		return types.get(name);
	}
	
	public String toString() {
		return name;
	}
}
