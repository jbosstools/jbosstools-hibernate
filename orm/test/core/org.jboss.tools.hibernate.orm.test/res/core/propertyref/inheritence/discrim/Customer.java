//$Id$
package core.propertyref.inheritence.discrim;

/**
 * @author Gavin King
 */
public class Customer extends Person {
	private String customerId;

	public String getCustomerId() {
		return customerId;
	}
	
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
}
