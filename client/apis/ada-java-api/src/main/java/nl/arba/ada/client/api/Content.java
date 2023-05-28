package nl.arba.ada.client.api;

import org.apache.hc.client5.http.utils.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * This class represents a piece of content
 */
public class Content {
    private String mimetype;
    private File source;
    private boolean minorversion;

    private Content(File source, String mimetype, boolean minorversion) {
        this.source = source;
        this.mimetype = mimetype;
        this.minorversion = minorversion;
    }

    /**
     * Create an instance of the class
     * @param source The file that holds the content that is represented by the instance
     * @param mimetype The mimetype of the content
     * @param minorversion <code>True</code> - Add the content as a minor version, <code>false</code> - add the content as a major version
     * @return The instance that represents the content
     */
    public static Content create(File source, String mimetype, boolean minorversion) {
        return new Content(source, mimetype, minorversion);
    }

    /**
     * Get the json representation of the piece of content
     * @return The json representation of the piece of content
     * @throws IOException Throwed when a json problem occurs
     */
    public String toJson() throws IOException {
        try (FileInputStream fis = new FileInputStream(source);ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024*1024];
            int readed = 0;
            while (readed >= 0) {
                readed = fis.read(buffer);
                if (readed > 0)
                    bos.write(buffer, 0, readed);
            }
            return "{\"content\":\"" + Base64.encodeBase64String(bos.toByteArray()) + "\"," +
                "\"mimetype\": \"" + mimetype + "\"," +
                "\"uploadfile\": \"" + source.getName() + "\"," +
                "\"minorversion\":" + minorversion +
                "}";
        }
    }
}
