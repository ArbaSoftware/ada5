package nl.arba.ada.client.api.addon.base;

import nl.arba.ada.client.api.AdaObject;
import nl.arba.ada.client.api.PropertyType;
import nl.arba.ada.client.api.PropertyValue;
import nl.arba.ada.client.api.Store;
import nl.arba.ada.client.api.exceptions.NoSearchResultsException;
import nl.arba.ada.client.api.exceptions.ObjectNotCreatedException;
import nl.arba.ada.client.api.search.PropertyFilter;
import nl.arba.ada.client.api.search.Search;
import nl.arba.ada.client.api.security.GrantedRight;

import java.util.Optional;

/**
 * Class that represents a folder object
 */
public class Folder extends AdaObject {
    /**
     * Constant for the property name
     */
    public static final String NAME = "Name";
    /**
     * Constant for the property parent folder
     */
    public static final String PARENT_FOLDER = "ParentFolder";

    /**
     * Constructor the create an instance of a folder based on an object
     * @param source The source object
     * @see AdaObject
     */
    private Folder(AdaObject source) {
        super.setId(source.getId());
        super.setProperties(source.getProperties().toArray(new PropertyValue[0]));
        super.setClassid(source.getClassId());
        super.setRights(source.getRights().toArray(new GrantedRight[0]));
        super.setStore(source.getStore());
    }

    /**
     * Get the name of the folder, which is the value of the name property
     * @return The name of the folder
     */
    public String getName() {
        Optional <PropertyValue> propertyValue = super.getProperties().stream().filter(p -> p.getName().equals(NAME)).findFirst();
        return propertyValue.isPresent() ? (String) propertyValue.get().getValue() : null;
    }

    /**
     * Create an instance of a folder based on an object
     * @param source The source object
     * @return The instance of the folder
     * @see AdaObject
     */
    public static Folder create(AdaObject source) {
        return new Folder(source);
    }

    /**
     * Add a folder to a store
     * @param store The store to add the folder to
     * @param name The name of the folder
     * @param parent The parent folder
     * @return The new created folder
     * @throws ObjectNotCreatedException Throwed when the creation of the folder failed
     */
    public static Folder create(Store store, String name, Folder parent) throws ObjectNotCreatedException {
        AdaObject newFolder = new AdaObject();
        newFolder.setClassid("Folder");
        PropertyValue nameProperty = new PropertyValue();
        nameProperty.setName(NAME);
        nameProperty.setType(PropertyType.STRING);
        nameProperty.setValue(name);

        if (parent == null)
            newFolder.setProperties(new PropertyValue[] { nameProperty});
        else {
            PropertyValue parentProperty = new PropertyValue();
            parentProperty.setName(PARENT_FOLDER);
            parentProperty.setType(PropertyType.OBJECT);
            parentProperty.setValue(parent.getId());
            newFolder.setProperties(new PropertyValue[] {
                    nameProperty,
                    parentProperty
            });
        }
        AdaObject result = store.createObject(newFolder);

        Folder resultFolder = new Folder(result);
        return resultFolder;
    }

    /**
     * Create a subfolder of this folder
     * @param name The name of the subfolder
     * @return The new created subfolder
     * @throws ObjectNotCreatedException Throwed when the creation of the subfolder failed
     */
    public Folder createSubFolder(String name) throws ObjectNotCreatedException {
        return Folder.create(getStore(), name, this);
    }

    /**
     * Get the subfolders of this folder
     * @return An array of the subfolders of this folder
     * @throws NoSearchResultsException Throwed when the subfolders fails to retrieve
     */
    public Folder[] getSubFolders() throws NoSearchResultsException{
        try {
            AdaObject[] searchresults = getStore().getDomain().search(getStore(), Search
                    .create(getStore().getAdaClass("Folder"))
                    .addProperty("Name")
                    .addFilter(PropertyFilter.createEqualFilter(PARENT_FOLDER, PropertyType.OBJECT, getId()))
            );
            Folder[] result = new Folder[searchresults.length];
            for (int index = 0; index < result.length; index++) {
                result[index] = Folder.create(searchresults[index]);
            }
            return result;
        }
        catch (Exception err) {
            throw new NoSearchResultsException();
        }
    }
}
