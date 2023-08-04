package nl.arba.ada.client.adaclient.treeview;

import javafx.scene.control.TreeItem;

public abstract class AdaClientTreeItem extends TreeItem {
    private Object object;

    public AdaClientTreeItem(Object object, String name) {
        super(name);
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public abstract TreeItemType getType();

}
