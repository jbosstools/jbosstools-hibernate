package mapping.value.type.collection.list;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollectionList {

	private int id;
	private List items = new ArrayList();

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List getItems() {
		return items;
	}
	public void setItems(List items) {
		this.items = items;
	}
}
