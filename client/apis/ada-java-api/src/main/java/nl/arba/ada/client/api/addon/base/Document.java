package nl.arba.ada.client.api.addon.base;

import nl.arba.ada.client.api.*;
import nl.arba.ada.client.api.exceptions.InvalidPropertyTypeException;
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

    private Document(String title, File source, String mimetype, boolean minorversion) {
        content = Content.create(source, mimetype, minorversion);
        super.setStringProperty(DOCUMENT_TITLE, title);
    }

    /**
     * Create an instance of a document
     * @param title The title of the document
     * @param source The source file that holds the content of the document
     * @param mimetype The mimetype of the content of the document
     * @param minorversion <code>True</code> - the content of the document will be added as a minor version, <code>false</code> - the content of the document will be added as a major version
     * @return The instance of the document
     */
    public static Document create(String title, File source, String mimetype, boolean minorversion) {
        return new Document(title, source, mimetype, minorversion);
    }

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
}
