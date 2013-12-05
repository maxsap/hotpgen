package com.jswidler.hotpgen.app;

import com.jswidler.hotpgen.util.HmacOneTimePassword;
import com.jswidler.hotpgen.util.SimpleCrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.Properties;

/**
 * @author <a href="mailto:jswidler@gmail.com">Jesse Swidler</a>
 */
public class AppProperties extends Properties {

    private static final String FILE_NAME = "hotp.properties";
    private static final String KEY = "key";
    private static final String ALGORITHM = "algorithm";
    private static final String PERIOD = "period";
    private static final String DIGITS = "digits";

    /**
     * This secret is used so the key is not stored in plaintext in the property file.
     */
    private static final String SECRET = "3pkZ1TS6AIRqO7jN";

    public AppProperties() {
        initializeDefaults();
        loadPropertyFile();
    }

    private void initializeDefaults() {
        setProperty(ALGORITHM, HmacOneTimePassword.DEFAULT_ALGORITHM);
        setProperty(PERIOD, Integer.toString(HmacOneTimePassword.DEFAULT_PERIOD));
        setProperty(DIGITS, Integer.toString(HmacOneTimePassword.DEFAULT_DIGITS));
    }

    private void loadPropertyFile() {
        try {
            InputStream in = new FileInputStream(FILE_NAME);
            load(in);
            in.close();
        } catch (FileNotFoundException ignored) {
            //Continue if the file is not found.
        } catch (IOException e) {
            System.err.println("Unable to load " + FILE_NAME + ": " + e);
        }
    }

    public void savePropertyFile() {
        try {
            File outputFile = new File(FILE_NAME);
            OutputStream outputStream = new FileOutputStream(outputFile);
            store(outputStream, "hotpgen settings");
        } catch (IOException e) {
            System.err.println("Error when trying to save properties: " + e);
        }
    }

    public String getKey() throws GeneralSecurityException {
        String encryptedKey = getProperty(KEY);
        if (encryptedKey == null || encryptedKey.isEmpty()) {
            return "";
        }
        return SimpleCrypt.decrypt(SECRET, getProperty(KEY));
    }

    public void setKey(String key) throws GeneralSecurityException {
        setProperty(KEY, SimpleCrypt.encrypt(SECRET, key));
    }

    public String getAlgorithm() {
        return getProperty(ALGORITHM);
    }

    public int getDigits() {
        return Integer.parseInt(getProperty(DIGITS));
    }

    public int getPeriod() {
        return Integer.parseInt(getProperty(PERIOD));
    }
}
