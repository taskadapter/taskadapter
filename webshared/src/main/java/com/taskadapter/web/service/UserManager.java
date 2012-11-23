package com.taskadapter.web.service;

import com.taskadapter.FileManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class UserManager {

    public static final String ADMIN_LOGIN_NAME = "admin";
    private final FileManager fileManager;

    public UserManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public Collection<String> getUsers() {
        String[] possibleUserNames = fileManager.listUsers();
        Collection<String> users = new ArrayList<String>(possibleUserNames.length);
        for (String fileName : possibleUserNames) {
            if (doesUserExists(fileName)) {
                users.add(fileName);
            }
        }
        return users;
    }

    public boolean doesUserExists(String loginName) {
        return fileManager.getUserFolder(loginName).exists();
    }

    public void createUser(String loginName) {
        fileManager.getUserFolder(loginName).mkdirs();
    }
    
    public void deleteUser(String loginName) throws IOException {
        fileManager.deleteUserFolder(loginName);
    }

    public boolean isAdmin(String userLoginName) {
        return ADMIN_LOGIN_NAME.equals(userLoginName);
    }
}
