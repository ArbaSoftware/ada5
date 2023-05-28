package nl.arba.ada.client.api.addon.base;

import nl.arba.ada.client.api.*;
import nl.arba.ada.client.api.exceptions.InvalidPropertyTypeException;
import nl.arba.ada.client.api.exceptions.PropertyNotFoundException;

import java.io.File;
import java.util.*;

public class Document extends AdaObject implements ContentHolding {
    private Content content;

    public static final String DOCUMENT_TITLE = "DocumentTitle";

    private Document(String title, File source, String mimetype, boolean minorversion) {
        content = Content.create(source, mimetype, minorversion);
        super.setStringProperty(DOCUMENT_TITLE, title);
    }

    public static Document create(String title, File source, String mimetype, boolean minorversion) {
        return new Document(title, source, mimetype, minorversion);
    }

    public String getTitle() throws InvalidPropertyTypeException, PropertyNotFoundException {
        return getStringProperty(DOCUMENT_TITLE);
    }

    public String getClassId() {
        return (super.getClassId() == null ? "Document" : super.getClassId());
    }

    public Content getContent() {
        return content;
    }

    public static void main(String[] args) {
        try {
            Document target = Document.create("eerste", new File("/home/arjanbas/test.txt"), "text/text", false);
            PropertyValue documentTitle = new PropertyValue();
            documentTitle.setName(DOCUMENT_TITLE);
            documentTitle.setType(PropertyType.STRING);
            documentTitle.setValue("Eerste document");
            target.setProperties(new PropertyValue[] {
                    documentTitle
            });
            target.setClassid("Document");
            System.out.println(target.createAddRequest());
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }

}
