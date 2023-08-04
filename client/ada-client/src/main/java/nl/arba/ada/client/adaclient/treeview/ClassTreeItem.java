package nl.arba.ada.client.adaclient.treeview;

import javafx.scene.control.TreeItem;
import nl.arba.ada.client.api.AdaClass;

public class ClassTreeItem extends AdaClientTreeItem {
    public ClassTreeItem(AdaClass clazz) {
        super(clazz, clazz.getName());
    }

    @Override
    public TreeItemType getType() {
        return TreeItemType.CLASS;
    }
}
