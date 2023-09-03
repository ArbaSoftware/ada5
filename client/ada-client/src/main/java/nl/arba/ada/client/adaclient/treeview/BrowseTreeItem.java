package nl.arba.ada.client.adaclient.treeview;

import nl.arba.ada.client.adaclient.utils.InternationalizationUtils;

public class BrowseTreeItem extends AdaClientTreeItem {
    public BrowseTreeItem() {
        super(null, InternationalizationUtils.get("treeview.item.browse.label"));
    }

    @Override
    public TreeItemType getType() {
        return TreeItemType.BROWSE;
    }
}
