package com.taskadapter.config;

import com.taskadapter.PluginManager;
import com.taskadapter.util.MyIOUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigStorage {
    private static final String FILE_EXTENSION = "ta_conf";

    private PluginManager pluginManager;
    private StorageListener listener;

    public ConfigStorage(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    public void setListener(StorageListener l) {
        this.listener = l;
    }

    private static String buildFileName(String name) {
        // this will NOT work for reserved names like "con" (under Windows), ...
        // XXX see
        // http://eng-przemelek.blogspot.com/2009/07/how-to-create-valid-file-name.html
        // ?
        // this solution is temporary until we get rid of the Project Explorer
        // completely
        // see https://www.hostedredmine.com/issues/9491
        // validFileName = URLEncoder.encode( name , "UTF-8");
        // validFileName = validFileName.replace('+', '_');

        String saferName = name.replaceAll("[+:\\\\/*?|<>]", "_");
      		/* see http://www.hostedredmine.com/issues/38681
      	 	   TA should not fail to create configs with names equal to standard Windows devices like "con"
      	 	   config name is used as a file name, so it's impossible to create a config with name "con" on Windows.

      			instead of trying to keep all "forbidden names" we can just add "-config" to the file name.
      		 */
   		return saferName + "-config";
    }

    public List<TAFile> getAllConfigs() {
        File root = new File(getRootFolderName());
        String[] fileNames = root.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(FILE_EXTENSION);
            }
        });
        List<TAFile> files = new ArrayList<TAFile>();
        if (fileNames != null) {
            for (String name : fileNames) {
                File file = new File(root, name);
                try {
                    String fileBody = MyIOUtils.loadFile(file.getAbsolutePath());
                    ConfigFileParser parser = new ConfigFileParser(pluginManager);
                    TAFile taFile = parser.parse(fileBody);
                    files.add(taFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        return files;
    }

    public void saveConfig(TAFile taFile) {
        String fileContents = new ConfigFileParser(pluginManager).convertToJSonString(taFile);
        try {
            File rootDir = new File(getRootFolderName());
            rootDir.mkdirs();
            String fullFileName = getAbsoluteFilePath(taFile);
            MyIOUtils.writeToFile(fullFileName, fileContents);
            if (this.listener != null) {
                this.listener.notifySomethingChanged();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void delete(TAFile config) {
        File file = new File(getAbsoluteFilePath(config));
        file.delete();
        if (this.listener != null) {
            this.listener.notifySomethingChanged();
        }
    }

    private String getAbsoluteFilePath(TAFile file) {
        String fileName = buildFileName(file.getName());
        return getRootFolderName() + "/" + fileName + "." + FILE_EXTENSION;
    }

    public static String getRootFolderName() {
        String userHome = System.getProperty("user.home");
        return userHome + "/taskadapter";
    }

    public void cloneConfig(TAFile file) {
        TAFile cfg = new TAFile("Copy of " + file.getName(), file.getConnector1(), file.getConnector2());
        this.saveConfig(cfg);

        if (this.listener != null) {
            this.listener.notifySomethingChanged();
        }
    }
}