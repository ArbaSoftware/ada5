module nl.arba.ada.client.adaclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.httpcomponents.client5.httpclient5;
    requires org.apache.httpcomponents.core5.httpcore5.h2;
    requires org.apache.httpcomponents.core5.httpcore5;
    requires com.fasterxml.jackson.databind;
    requires nl.ada.java.api;

    opens nl.arba.ada.client.adaclient to javafx.fxml;
    opens nl.arba.ada.client.adaclient.dialogs to javafx.fxml;
    exports nl.arba.ada.client.adaclient;
}