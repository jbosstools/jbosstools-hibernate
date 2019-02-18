//$Id$
package core.orphan;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Gavin King
 */
public class Product implements Serializable {
	private String name;
	private Set parts = new HashSet();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Set getParts() {
		return parts;
	}
	public void setParts(Set parts) {
		this.parts = parts;
	}
}
