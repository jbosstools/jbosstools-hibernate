package mapping.value.type.collection.map;

public class CollectionMapItem {

	private int id;
	private int collectionMapId;
	private int itemKey;
	private String item;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public int getCollectionMapId() {
		return collectionMapId;
	}
	public void setCollectionMapId(int collectionMapId) {
		this.collectionMapId = collectionMapId;
	}
	public int getItemKey() {
		return itemKey;
	}
	public void setItemKey(int itemKey) {
		this.itemKey = itemKey;
	}
}
