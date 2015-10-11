package com.taskadapter.connector.redmine;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import org.junit.Ignore;
import org.junit.Test;

/** Tests for certificate fetch. */
public class CertFetchTest {
	@Test
	@Ignore
	public void testGoogleCerts() throws MalformedURLException, IOException, KeyStoreException,
			NoSuchAlgorithmException, CertificateException {
		final Certificate cert = CertUtil.getServerCertificate(new URL("https://plan.io"));
		System.out.println(cert);
		final byte[] bytes = CertUtil.toKeyStore(cert);
		final KeyStore ks = CertUtil.parseKeyStore(bytes);
		System.out.println(ks.getCertificate("trustedCert"));
	}
}
