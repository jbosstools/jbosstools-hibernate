//$Id$
package core.compositeelement;

/**
 * @author gavin
 */
public class Child {
	private String name;
	private String bio;
	private Parent parent;
	private int bioLength;
	Child() {}
	public Child(String name) {
		this.name = name;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return Returns the parent.
	 */
	public Parent getParent() {
		return parent;
	}
	/**
	 * @param parent The parent to set.
	 */
	public void setParent(Parent parent) {
		this.parent = parent;
	}
	public String getBio() {
		return bio;
	}
	public void setBio(String bio) {
		this.bio = bio;
	}
	public int hashCode() {
		return name.hashCode();
	}
	public boolean equals(Object other) {
		Child c = (Child) other;
		return c.parent.getId().equals(parent.getId()) 
			&& c.name.equals(name);
	}
	public int getBioLength() {
		return bioLength;
	}
	public void setBioLength(Integer bioLength) {
		this.bioLength = bioLength==null ? 0 : bioLength.intValue();
	}
}
