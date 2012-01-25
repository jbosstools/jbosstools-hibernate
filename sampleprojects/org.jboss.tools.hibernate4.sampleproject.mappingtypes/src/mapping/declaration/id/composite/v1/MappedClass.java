package mapping.declaration.id.composite.v1;

import java.io.Serializable;

public class MappedClass implements Serializable {
	private int id;
	private KeyManyToOneClass key;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public KeyManyToOneClass getKey() {
		return key;
	}
	public void setKey(KeyManyToOneClass key) {
		this.key = key;
	}
}
