package mapping.value.type.collection.set;

import java.util.HashSet;
import java.util.Set;

public class CollectionSet {

	private int id;
	private Set items = new HashSet(0);

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Set getItems() {
		return items;
	}
	public void setItems(Set items) {
		this.items = items;
	}
}
