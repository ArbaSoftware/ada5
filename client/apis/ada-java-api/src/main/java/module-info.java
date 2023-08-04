module nl.ada.java.api {
    requires org.apache.httpcomponents.client5.httpclient5;
    requires org.apache.httpcomponents.core5.httpcore5.h2;
    requires org.apache.httpcomponents.core5.httpcore5;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires org.apache.commons.io;

    opens nl.arba.ada.client.api to com.fasterxml.jackson.databind;

    exports nl.arba.ada.client.api;
    exports nl.arba.ada.client.api.addon;
    exports nl.arba.ada.client.api.addon.base;
    exports nl.arba.ada.client.api.exceptions;
    exports nl.arba.ada.client.api.search;
    exports nl.arba.ada.client.api.security;
    exports nl.arba.ada.client.api.util;
}