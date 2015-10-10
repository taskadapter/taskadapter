package com.taskadapter.connector.redmine;

import com.taskadapter.connector.definition.WebServerInfo;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.TransportConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.http.impl.conn.PoolingClientConnectionManager;

public final class RedmineManagerFactory {
    public static RedmineManager createRedmineManager(WebServerInfo serverInfo) {
        final PoolingClientConnectionManager connectionManager;
        try {
            connectionManager = createConnectionManager();
        } catch (Exception e) {
            throw new RuntimeException("cannot create a connection manager for insecure SSL connections", e);
        }
        final TransportConfiguration ignoredSslConfig = com.taskadapter.redmineapi.RedmineManagerFactory.createShortTermConfig(connectionManager);

        if (serverInfo.isUseAPIKeyInsteadOfLoginPassword()) {
            return com.taskadapter.redmineapi.RedmineManagerFactory.createWithApiKey(serverInfo.getHost(), serverInfo.getApiKey(), ignoredSslConfig);
        } else {
            return com.taskadapter.redmineapi.RedmineManagerFactory.createWithUserAuth(serverInfo.getHost(),
                    serverInfo.getUserName(), serverInfo.getPassword(), ignoredSslConfig);
        }
    }
    
    /** Returns a key store with the extended certificates. */
    private static KeyStore getExtensionKeystore() {
    	final InputStream extStore = 
    		RedmineManagerFactory.class.getClassLoader().getResourceAsStream("/com/taskadapter/connector/redmine/extratrust");
    	if (extStore == null) {
    		return null;
    	}
    	
    	try {
    		final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
    		ks.load(extStore, "123456".toCharArray());
    		return ks;
    	} catch (Exception e) {
    		e.printStackTrace();
    		return null;
		} finally {
    		try {
				extStore.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }

	private static PoolingClientConnectionManager createConnectionManager() throws KeyStoreException,
			NoSuchAlgorithmException, CertificateException, KeyManagementException, UnrecoverableKeyException {
		final Collection<KeyStore> extraStores = new ArrayList<KeyStore>();
		final KeyStore builtInExtension = getExtensionKeystore();
		if (builtInExtension != null) {
			extraStores.add(builtInExtension);
		}
		
		if (!extraStores.isEmpty()) {
			return com.taskadapter.redmineapi.RedmineManagerFactory.createConnectionManagerWithExtraTrust(extraStores);
		}
		return com.taskadapter.redmineapi.RedmineManagerFactory.createDefaultConnectionManager();
	}
}
