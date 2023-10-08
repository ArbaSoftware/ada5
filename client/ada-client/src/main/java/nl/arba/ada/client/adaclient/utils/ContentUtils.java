package nl.arba.ada.client.adaclient.utils;
import java.io.File;
import java.util.Properties;

public class ContentUtils {
    private static Properties mimetypeProperties;

    public static String getMimetype(String extension) {
        if (getProperties().containsKey(extension.toLowerCase()))
            return getProperties().getProperty(extension.toLowerCase());
        else
            return "application/octet-stream";
    }

    public static String getMimetype(File source) {
        return getMimetype(source.getName().contains(".") ? source.getName().substring(source.getName().lastIndexOf('.')+1): "");
    }

    private static Properties getProperties() {
        if (mimetypeProperties == null) {
            mimetypeProperties = new Properties();
            try {
                mimetypeProperties.load(ContentUtils.class.getResourceAsStream("/mimetype.properties"));
            }
            catch (Exception err) {}
        }
        return mimetypeProperties;
    }
}
