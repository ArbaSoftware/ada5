package nl.arba.ada.client.adaclient.treeview;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import nl.arba.ada.client.api.addon.base.Folder;

public class FolderTreeItem extends AdaClientTreeItem{
    private boolean retrievedSubFolders = false;
    public FolderTreeItem(Folder folder) {
        super(folder, folder.getName());
    }

    @Override
    public TreeItemType getType() {
        return TreeItemType.FOLDER;
    }

    public void onSelection() {
        if (!retrievedSubFolders) {
            try {
                Folder[] subFolders = ((Folder) getObject()).getSubFolders();
                for (Folder subfolder: subFolders)
                    getChildren().add(new FolderTreeItem(subfolder));
                retrievedSubFolders = true;
            }
            catch (Exception err) {
                err.printStackTrace();
            }
        }
    }

    public Folder getFolder() {
        return (Folder) getObject();
    }
}
