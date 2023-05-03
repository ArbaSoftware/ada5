package nl.arba.ada.client.api.util;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Helper class to handle stream issues
 */
public class StreamUtils {
    /**
     * Convert a stream to as string
     * @param stream The inputstream to read
     * @return The content of the stream as a string
     * @throws IOException An IOException can occur
     */
    public static String streamToString(InputStream stream) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            IOUtils.copy(stream, bos);
            return new String(bos.toByteArray());
        }
    }
}
