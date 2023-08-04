package nl.arba.ada.client.adaclient.utils;

public class IdName {
    private String id;
    private String name;

    public IdName(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String toString() {
        return name;
    }
}
