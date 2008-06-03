package mapping.value.type.collection.list;

public class CollectionListItem {

	private int id;
	private int collectionListId;
	private int itemIndex;
	private String item;

	public int getCollectionListId() {
		return collectionListId;
	}
	public void setCollectionListId(int collectionListId) {
		this.collectionListId = collectionListId;
	}
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
	public int getItemIndex() {
		return itemIndex;
	}
	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}
}
