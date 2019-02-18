package annotations.database.schema;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public enum ReferenceSystemProcessType {

	HOST_PATHOGEN("host-pathogen"),
	HOST_PATHOGEN_DISEASE("host-pathogen-disease"),
	HOST_PATHOGEN_DISEASE_CHEMICAL("host-pathogen-disease-chemical"),
	HOST_PATHOGEN_CHEMICAL("host-pathogen-chemical"),
	PATHOGEN_DISEASE("pathogen-disease")
	
}
