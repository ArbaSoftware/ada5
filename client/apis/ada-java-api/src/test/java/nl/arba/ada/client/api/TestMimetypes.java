package nl.arba.ada.client.api;

import nl.arba.ada.client.api.addon.AddOn;
import nl.arba.ada.client.api.exceptions.AddOnNotCreatedException;
import nl.arba.ada.client.api.exceptions.ApiException;
import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class TestMimetypes {
    private static Domain domain;
    private static String TEST_URL = "http://192.168.2.74:9601/ada";
    private final static String TEST_USER_2_EMAIL= "dev@arjanbas.nl";
    private final static String TEST_USER_2_PASSWORD = "hemertje";

    @BeforeClass
    public static void before() throws IOException {
        domain = Domain.create(TEST_URL);
        domain.login(TEST_USER_2_EMAIL, TEST_USER_2_PASSWORD);
    }

    @Test
    public void doGet() throws ApiException {
        domain.getMimetypes();
    }

    @Test
    public void doAdd() throws ApiException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); FileInputStream fis = new FileInputStream(new File("/home/arjan/Downloads/file-bmp-o.256x256.png"))) {
            IOUtils.copy(fis, bos);
            domain.addMimetype("bmp", "image/bmp", "bmp.png", bos.toByteArray());
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }
}
