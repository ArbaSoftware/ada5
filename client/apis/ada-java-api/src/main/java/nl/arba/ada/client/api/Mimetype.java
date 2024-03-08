package nl.arba.ada.client.api;

import org.apache.hc.client5.http.utils.Base64;

public class Mimetype {
    private String mimetype;
    private String extension;
    private byte[] icon;
    private String iconfilename;


    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getMimetype() {
        return mimetype;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }

    public void setIcon(String base64) {
        this.icon = Base64.decodeBase64(base64);
    }

    public byte[] getIcon() {
        return this.icon;
    }

    public boolean hasIcon() {
        return this.icon != null;
    }

    public void setIconfilename(String name) {
        this.iconfilename = name;
    }

    public String getIconfilename() {
        return this.iconfilename;
    }
}
