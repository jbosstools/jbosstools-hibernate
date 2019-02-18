//$Id$
package core.unidir;


/**
 * @author Gavin King
 */
public class Child {
	private String name;
	private int age;
	Child() {}
	public Child(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
}
