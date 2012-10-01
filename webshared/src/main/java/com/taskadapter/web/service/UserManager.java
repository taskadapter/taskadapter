package com.taskadapter.web.service;

import com.google.common.io.Files;
import com.taskadapter.FileManager;
import com.taskadapter.config.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;

public class UserManager {

    private final Logger logger = LoggerFactory.getLogger(UserManager.class);

    public static final String ADMIN_LOGIN_NAME = "admin";
    private static final String PASSWORD_FILE_NAME = "password.txt";
    private File dataRootFolder;

    public UserManager(File dataRootFolder) {
        this.dataRootFolder = dataRootFolder;
        // TODO race condition here: what if two sessions will be started at the same time?
        // completely unlikely, but still not nice!
        createFirstAdminUserIfNeeded();
    }

    private void createFirstAdminUserIfNeeded() {
        try {
            getUser(ADMIN_LOGIN_NAME);
        } catch (UserNotFoundException e) {
            saveUser(ADMIN_LOGIN_NAME, "admin");
        }
    }

    public Collection<User> getUsers() {
        String[] fileNames = dataRootFolder.list();
        Collection<User> users = new ArrayList<User>(fileNames.length);
        for (String fileName : fileNames) {
            try {
                users.add(getUser(fileName));
            } catch (UserNotFoundException e) {
                logger.error("User not found: " + fileName + ". This indicates incorrect folders structure in your data directory.");
            }
        }
        return users;
    }

    public void deleteUser(String loginName) throws IOException {
        File userFolder = new FileManager(dataRootFolder).getUserFolder(loginName);
        FileManager.deleteRecursively(userFolder);
    }

    public void saveUser(String loginName, String newPassword) {
        // TODO encrypt password
        File userFolder = new FileManager(dataRootFolder).getUserFolder(loginName);
        userFolder.mkdirs();
        File passwordFile = new File(userFolder, PASSWORD_FILE_NAME);
        try {
            Files.write(newPassword, passwordFile, Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException("can't read file " + passwordFile.getAbsolutePath() + ": " + e.getMessage());
        }
    }

    public User getUser(String loginName) throws UserNotFoundException {
        File userFolder = new FileManager(dataRootFolder).getUserFolder(loginName);
        if (!userFolder.exists()) {
            throw new UserNotFoundException();
        }
        File passwordFile = new File(userFolder, PASSWORD_FILE_NAME);
        if (!passwordFile.exists()) {
            throw new UserNotFoundException();
        }
        String passwordString;
        try {
            passwordString = Files.readFirstLine(passwordFile, Charset.forName("UTF-8"));
            return new User(loginName, passwordString);
        } catch (IOException e) {
            logger.error("Can't read " + passwordFile.getAbsolutePath() +" file for user " + loginName + ". The reason is:" + e.getMessage(), e);
            throw new UserNotFoundException();
        }
    }

    public boolean isAdmin(String userLoginName) {
        return ADMIN_LOGIN_NAME.equals(userLoginName);
    }
}
