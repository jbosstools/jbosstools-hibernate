//$Id$
package core.formulajoin;

import java.io.Serializable;

/**
 * @author Gavin King
 */
public class Master implements Serializable {
	private Long id;
	private String name;
	private Detail detail;
	public Detail getDetail() {
		return detail;
	}
	public void setDetail(Detail detail) {
		this.detail = detail;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
