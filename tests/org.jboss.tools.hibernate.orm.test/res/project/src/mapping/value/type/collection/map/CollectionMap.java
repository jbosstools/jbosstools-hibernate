package mapping.value.type.collection.map;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CollectionMap {

	private int id;
	private Map items = new HashMap();
	private Map itemsOfClass = new HashMap();
	private Collection itemsMany;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Map getItems() {
		return items;
	}
	public void setItems(Map items) {
		this.items = items;
	}
	public Map getItemsOfClass() {
		return itemsOfClass;
	}
	public void setItemsOfClass(Map itemsOfClass) {
		this.itemsOfClass = itemsOfClass;
	}
	public Collection getItemsMany() {
		return itemsMany;
	}
	public void setItemsMany(Collection itemsMany) {
		this.itemsMany = itemsMany;
	}
}
