package nl.arba.ada.client.api.util;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
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

    /**
     * Reads a stream to a new stream that is not readed yet
     * @param stream The source stream
     * @return A stream that is not readed yet
     * @throws IOException When reading of the source stream fails
     */
    public static InputStream readStream(InputStream stream) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            IOUtils.copy(stream, bos);
            return new ByteArrayInputStream(bos.toByteArray());
        }
    }
}
