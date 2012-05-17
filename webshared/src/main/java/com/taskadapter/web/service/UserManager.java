package com.taskadapter.web.service;

import com.google.common.io.Files;
import com.taskadapter.FileManager;
import com.taskadapter.config.User;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;

public class UserManager {

    private static final String PASSWORD_FILE_NAME = "password.txt";

    public UserManager() {
        // TODO race condition here: what if two sessions will be started at the same time?
        // completely unlikely, but still not nice!
        createFirstAdminUserIfNeeded();
    }

    private void createFirstAdminUserIfNeeded() {
        saveUser("admin", "admin");
    }

    public Collection<User> getUsers() {
        String dataRootFolderName = FileManager.getDataRootFolderName();
        File dataRootFolder = new File(dataRootFolderName);
        String[] fileNames = dataRootFolder.list();
        Collection<User> users = new ArrayList<User>(fileNames.length);
        for (String fileName : fileNames) {
            try {
                users.add(getUser(fileName));
            } catch (UserNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return users;
    }

    public void deleteUser(String loginName) {
        File userFolder = FileManager.getUserFolder(loginName);
        userFolder.delete();
    }

    public void saveUser(String loginName, String newPassword) {
        // TODO encrypt password
        File userFolder = FileManager.getUserFolder(loginName);
        userFolder.mkdirs();
        File passwordFile = new File(userFolder, PASSWORD_FILE_NAME);
        try {
            Files.write(newPassword, passwordFile, Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException("can't read password.txt file: " + e);
        }
    }

    public User getUser(String loginName) throws UserNotFoundException {
        File userFolder = FileManager.getUserFolder(loginName);
        if (!userFolder.exists()) {
            throw new UserNotFoundException();
        }
        File passwordFile = new File(userFolder, PASSWORD_FILE_NAME);
        if (!userFolder.exists()) {
            throw new UserNotFoundException();
        }
        String passwordString;
        try {
            passwordString = Files.readFirstLine(passwordFile, Charset.forName("UTF-8"));
            return new User(loginName, passwordString);
        } catch (IOException e) {
            throw new RuntimeException("can't read password.txt file: " + e);
        }
    }
}