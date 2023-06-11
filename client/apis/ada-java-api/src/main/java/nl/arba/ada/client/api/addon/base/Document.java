package nl.arba.ada.client.api.addon.base;

import nl.arba.ada.client.api.*;
import nl.arba.ada.client.api.exceptions.InvalidPropertyTypeException;
import nl.arba.ada.client.api.exceptions.ObjectNotCreatedException;
import nl.arba.ada.client.api.exceptions.ObjectNotFoundException;
import nl.arba.ada.client.api.exceptions.PropertyNotFoundException;

import java.io.File;
import java.util.*;

/**
 * Document class of the base addon
 */
public class Document extends AdaObject implements ContentHolding {
    private Content content;

    /**
     * The name of the document title property of this class
     */
    public static final String DOCUMENT_TITLE = "DocumentTitle";

    /**
     * Get the title of the document
     * @return The title of the document
     * @throws InvalidPropertyTypeException Throwed when the document title property has a other type then string
     * @throws PropertyNotFoundException Throwed when the document title property is not found
     * @see PropertyType
     */
    public String getTitle() throws InvalidPropertyTypeException, PropertyNotFoundException {
        return getStringProperty(DOCUMENT_TITLE);
    }

    public void setTitle(String title) {
        super.setStringProperty(DOCUMENT_TITLE, title);
    }

    /**
     * Get the classid of the document, default is Document
     * @return The classid of the document
     */
    public String getClassId() {
        return (super.getClassId() == null ? "Document" : super.getClassId());
    }

    /**
     * Get the content of the document
     * @return The content of the document
     * @see Content
     */
    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public boolean checkout() {
        boolean result = getStore().getDomain().checkout(getStore(), getId());
        try {
            this.refresh(getStore().getDomain().getObject(getStore(), getId()));
        }
        catch (ObjectNotFoundException onfe) {

        }
        return result;
    }

    public boolean checkin(Content content) {
        boolean result = getStore().getDomain().checkin(getStore().getId(), getId(), content);
        try {
            refresh(getStore().getDomain().getObject(getStore(), getId()));
        }
        catch (ObjectNotFoundException onfe) {}
        return result;
    }

    public void refresh(AdaObject source) {
        this.setId(source.getId());
        this.setStore(source.getStore());
        this.setProperties(source.getProperties().toArray(new PropertyValue[0]));
        HashMap<String, Object> content = new HashMap<>();
        content.put("majorversion", source.getMajorVersion());
        content.put("minorversion", source.getMinorVersion());
        content.put("mimetype", source.getMimetype());
        content.put("checkedout", source.isCheckedOut());
        if (source.isCheckedOut()) {
            content.put("checkedoutuser", source.getCheckOutUser().getEmail());
            content.put("checkedoutidentityproviderid", source.getCheckOutUser().getIdentityProvider().getId());
        }
        this.setContent(content);
    }
}
