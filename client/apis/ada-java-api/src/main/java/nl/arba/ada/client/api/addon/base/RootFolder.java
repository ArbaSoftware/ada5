package nl.arba.ada.client.api.addon.base;
import nl.arba.ada.client.api.AdaObject;
import nl.arba.ada.client.api.PropertyType;
import nl.arba.ada.client.api.Store;
import nl.arba.ada.client.api.exceptions.NoSearchResultsException;
import nl.arba.ada.client.api.exceptions.ObjectNotCreatedException;
import nl.arba.ada.client.api.search.PropertyFilter;
import nl.arba.ada.client.api.search.Search;

/**
 * Helper class to access the root of a store
 */
public class RootFolder {
    private Store store;

    private RootFolder(Store store) {
        this.store = store;
    }

    /**
     * Create an instance of the root based on the given store
     * @param store The store that the root folder should represent
     * @return An instance of the root based on the given store
     * @see Store
     */
    public static RootFolder create(Store store) {
        return new RootFolder(store);
    }

    /**
     * Add a rootfolder to the given store
     * @param store The store to add the rootfolder to
     * @param name The name of the new rootfolder
     * @return The created rootfolder
     * @throws ObjectNotCreatedException Throwed when the creation of the rootfolder fails
     */
    public static Folder addSubFolder(Store store, String name) throws ObjectNotCreatedException {
        return Folder.create(store, name, null);
    }

    /**
     * Get the subfolders of the roots (the root folders)
     * @return An array of the rootfolders
     * @throws NoSearchResultsException Throwed when the retrieval of the root folders fails
     * @see Folder
     */
    public Folder[] getSubFolders() throws NoSearchResultsException{
        try {
            AdaObject[] results = store.getDomain().search(store, Search.create(store.getAdaClass("Folder")).addFilter(PropertyFilter.createNullFilter(Folder.PARENT_FOLDER, PropertyType.OBJECT)));
            Folder[] subfolders = new Folder[results.length];
            for (int index = 0; index < results.length; index++) {
                subfolders[index] = Folder.create(results[index]);
            }
            return subfolders;
        }
        catch (Exception err) {
            throw new NoSearchResultsException();
        }
    }

}
