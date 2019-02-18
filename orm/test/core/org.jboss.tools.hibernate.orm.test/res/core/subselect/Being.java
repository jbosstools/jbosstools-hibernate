//$Id$
package core.subselect;

/**
 * @author Gavin King
 */
public class Being {
	private long id;
	private String identity;
	private String location;
	private String species;
	
	public void setLocation(String location) {
		this.location = location;
	}
	public String getLocation() {
		return location;
	}
	public void setSpecies(String species) {
		this.species = species;
	}
	public String getSpecies() {
		return species;
	}
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	public String getIdentity() {
		return identity;
	}
}
