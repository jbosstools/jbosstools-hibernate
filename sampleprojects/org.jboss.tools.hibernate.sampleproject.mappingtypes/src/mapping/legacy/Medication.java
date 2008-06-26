package mapping.legacy;

/**
 * @author hbm2java
 */
public class Medication extends mapping.legacy.Intervention {

	mapping.legacy.Drug prescribedDrug;

  
	mapping.legacy.Drug getPrescribedDrug() {
    return prescribedDrug;
  }

  void  setPrescribedDrug(mapping.legacy.Drug newValue) {
    prescribedDrug = newValue;
  }


}
