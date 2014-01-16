package com.taskadapter.auth.cred;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;
import com.taskadapter.auth.AuthException;

/**
 * File-based implementation of credentials manager.
 * 
 */
public final class FSCredentialStore implements CredentialsStore {
    private static final String LEGACY_FILE = "password.txt";
    private static final String CREDENTIALS_FILE = "credits.xxx";
    private static final String CURRENT_VERSION_PREFIX = "v==1";
    private static final String IO_CHARSET = "UTF-8";
    private static final String LINE_SEPARATOR = System
            .getProperty("line.separator");

    /**
     * Logger.
     */
    private final Logger LOGGER = LoggerFactory
            .getLogger(FSCredentialStore.class);

    /** Authentication store root. */
    private final File storeRoot;

    /**
     * Creates a new file-system credentials manager.
     * 
     * @param storeRoot
     *            credentials storage root.
     */
    public FSCredentialStore(File storeRoot) {
        this.storeRoot = storeRoot;

    }

    @Override
    public Credentials loadCredentials(String user) throws AuthException {
        final File userDir = getUserFolder(user);
        final CredentialsV1 v1 = loadV1(userDir);
        if (v1 != null) {
            return v1;
        }
        return loadV0(userDir);
    }

    private Credentials loadV0(File userDir) throws AuthException {
        final File credFile = new File(userDir, LEGACY_FILE);
        try {
            return new CredentialsV0(Files.readFirstLine(credFile,
                    Charset.forName(IO_CHARSET)));
        } catch (IOException e) {
            LOGGER.info("Broken cred v0 ", e);
            throw new AuthException("No credentials found");
        }
    }

    private CredentialsV1 loadV1(File userDir) throws AuthException {
        final String CRED_FILE = CREDENTIALS_FILE;
        final File credFile = new File(userDir, CRED_FILE);
        final List<String> creds;
        try {
            creds = Files.readLines(credFile, Charset.forName(IO_CHARSET));
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            LOGGER.info("Broken cred v1 ", e);
            throw new AuthException("Mailformed credentials found");
        }
        if (creds.size() < 2 || !CURRENT_VERSION_PREFIX.equals(creds.get(0))) {
            throw new AuthException("Mailformed credentials found");
        }
        return new CredentialsV1(creds.get(1), creds.subList(2, creds.size()));
    }

    @Override
    public void saveCredentials(String user, CredentialsV1 credentials)
            throws AuthException {
        final File baseDir = getUserFolder(user);
        baseDir.mkdirs();

        final StringBuilder writer = new StringBuilder();
        writer.append(CURRENT_VERSION_PREFIX).append(LINE_SEPARATOR)
                .append(credentials.primaryCredentials);
        for (String secondary : credentials.secondaryCredentials) {
            writer.append(LINE_SEPARATOR).append(secondary);
        }

        try {
            Files.write(writer, new File(baseDir, CREDENTIALS_FILE),
                    Charset.forName(IO_CHARSET));
        } catch (IOException e) {
            LOGGER.info("Failed to save credentials ", e);
            throw new AuthException("Failed to store credentials");
        }

        new File(baseDir, LEGACY_FILE).delete();
    }

    @Override
    public List<String> listUsers() {
        final String[] userFiles = storeRoot.list();
        if (userFiles == null)
            return Collections.emptyList();
        final List<String> result = new ArrayList<String>(userFiles.length);
        for (String userName : userFiles) {
            if (doesUserExists(userName)) {
                result.add(userName);
            }
        }
        return result;
    }

    private boolean doesUserExists(String userName) {
        final File userHome = getUserFolder(userName);
        return new File(userHome, CREDENTIALS_FILE).exists()
                || new File(userHome, LEGACY_FILE).exists();
    }

    @Override
    public void removeUser(String user) {
        final File userHome = getUserFolder(user);
        new File(userHome, CREDENTIALS_FILE).delete();
        new File(userHome, LEGACY_FILE).delete();
        try {
            deleteRecursively(userHome);
        } catch (IOException e) {
            LOGGER.error("Failed to delete user " + user);
        }
    }

    /** Deletes file recursively. */
    private static void deleteRecursively(File file) throws IOException {
        final File[] subs = file.listFiles();
        if (subs != null) {
            for (File c : subs) {
                deleteRecursively(c);
            }
        }
        if (!file.delete()) {
            throw new FileNotFoundException("Failed to delete file: " + file);
        }
    }
    
    /** Returns a credentials storage directory for the given user. */
    private File getUserFolder(String user) {
        return new File(storeRoot, user);
    }

}
