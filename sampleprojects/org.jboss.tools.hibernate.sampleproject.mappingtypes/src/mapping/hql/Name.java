//$Id: Name.java 5686 2005-02-12 07:27:32Z steveebersole $
package mapping.hql;

/**
 * @author Gavin King
 */
public class Name {
	private String first;
	private char initial;
	private String last;
	
	protected Name() {}
	
	public Name(String first, char initial, String last) {
		this.first = first;
		this.initial = initial;
		this.last = last;
	}

	public String getFirst() {
		return first;
	}

	public void setFirst(String first) {
		this.first = first;
	}

	public char getInitial() {
		return initial;
	}

	public void setInitial(char initial) {
		this.initial = initial;
	}

	public String getLast() {
		return last;
	}

	public void setLast(String last) {
		this.last = last;
	}
}
