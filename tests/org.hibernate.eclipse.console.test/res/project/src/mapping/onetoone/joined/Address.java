//$Id$
package mapping.onetoone.joined;

/**
 * @author Gavin King
 */
public class Address {
	public String entityName;
	public String street;
	public String state;
	public String zip;
	
	public String toString() {
		return this.getClass() + ":" + street;
	}
}
