package com.taskadapter.connector.redmine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Utilities for certificates. Could be used to retrieve/store remote server's
 * certificate.
 */
public final class CertUtil {
	private static final TrustManager TRUST_ALL = new X509TrustManager() {
		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}

		@Override
		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			// Accepted!
		}

		@Override
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
			// Accepted!
		}
	};

	/**
	 * Returns a certificate used by the server. This method returns array of
	 * all server certificates without any attempts to validate them. This could
	 * be especially useful when a new user-driver trust should be established.
	 * 
	 * @param serverUrl
	 *            https url of the remote server.
	 * @return list of ssl server certificates.
	 * @throws IOException
	 *             if server could not be reached.
	 */
	public static Certificate[] getServerCertificates(URL serverUrl) throws IOException {
		if (!"https".equals(serverUrl.getProtocol())) {
			throw new IllegalArgumentException("Could not get certificates from non-https connection");
		}

		final SSLContext ctx;
		try {
			ctx = SSLContext.getInstance("SSL");
			ctx.init(null, new TrustManager[] { TRUST_ALL }, null);
		} catch (NoSuchAlgorithmException e) {
			throw new Error("No SSL protocols supported.");
		} catch (KeyManagementException e) {
			throw new Error("Could not manage default keys for SSL", e);
		}

		final URLConnection conn = serverUrl.openConnection();
		if (!(conn instanceof HttpsURLConnection)) {
			throw new IllegalArgumentException("Unexpected https url with non-https connection");
		}

		final HttpsURLConnection hurlConn = (HttpsURLConnection) conn;
		hurlConn.setSSLSocketFactory(ctx.getSocketFactory());
		hurlConn.setRequestMethod("OPTIONS");
		hurlConn.setDoOutput(false);
		hurlConn.setDoInput(true);
		hurlConn.connect();
		try {
			return hurlConn.getServerCertificates();
		} finally {
			hurlConn.disconnect();
		}
	}

	/** Returns a primary server certificate. */
	public static Certificate getServerCertificate(URL serverUrl) throws IOException {
		return getServerCertificates(serverUrl)[0];
	}

	/**
	 * Converts certificate into the keystore.
	 * 
	 * @throws KeyStoreException
	 *             if something is wrong with cert or key store.
	 * @throws CertificateException
	 *             if certificate is non-storable.
	 * @throws NoSuchAlgorithmException
	 *             if there is no valid algorithm to store the cert.
	 */
	public static byte[] toKeyStore(Certificate cert)
			throws KeyStoreException, NoSuchAlgorithmException, CertificateException {
		final KeyStore ks = getKeyStore();

		try {
			ks.load(null);
		} catch (IOException e1) {
			throw new Error("Could not load an empty keystore", e1);
		}
		ks.setCertificateEntry("trustedCert", cert);
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			try {
				ks.store(baos, "123456".toCharArray());
			} finally {
				baos.close();
			}
			return baos.toByteArray();
		} catch (IOException e) {
			throw new Error("Could not store cert in byte array stream", e);
		}
	}

	/** Returns a default key store. */
	private static KeyStore getKeyStore() throws Error {
		final KeyStore ks;
		try {
			ks = KeyStore.getInstance(KeyStore.getDefaultType());
		} catch (KeyStoreException e) {
			throw new Error("Default key store type is not available");
		}
		return ks;
	}
	
	
	/**
	 * Parses a key store (which could store a trusted cert).
	 * @param bytes bytes to parse.
	 * @return key store parsed from the bytes.
	 * @throws IOException 
	 * @throws CertificateException 
	 * @throws NoSuchAlgorithmException 
	 */
	public static KeyStore parseKeyStore(byte[] bytes) throws NoSuchAlgorithmException, CertificateException, IOException {
		final InputStream is = new ByteArrayInputStream(bytes);
		final KeyStore ks = getKeyStore();
		ks.load(is, "123456".toCharArray());
		return ks;
	}
}
