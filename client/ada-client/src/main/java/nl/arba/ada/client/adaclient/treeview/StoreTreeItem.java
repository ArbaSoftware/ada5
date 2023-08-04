package nl.arba.ada.client.adaclient.treeview;

import nl.arba.ada.client.api.Store;

public class StoreTreeItem extends AdaClientTreeItem {
    public StoreTreeItem(Store store) {
        super(store, store.getName());
    }

    @Override
    public TreeItemType getType() {
        return TreeItemType.STORE;
    }

    public Store getStore() {
        return (Store) super.getObject();
    }
}
