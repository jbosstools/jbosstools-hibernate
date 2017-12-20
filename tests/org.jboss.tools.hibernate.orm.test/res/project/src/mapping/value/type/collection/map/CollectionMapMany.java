package mapping.value.type.collection.map;

import java.io.Serializable;

public class CollectionMapMany implements Serializable {
	private int collectionMapId;
	private int collectionMapItemId;

	public int getCollectionMapId() {
		return collectionMapId;
	}
	public void setCollectionMapId(int collectionMapId) {
		this.collectionMapId = collectionMapId;
	}
	public int getCollectionMapItemId() {
		return collectionMapItemId;
	}
	public void setCollectionMapItemId(int collectionMapItemId) {
		this.collectionMapItemId = collectionMapItemId;
	}
}
