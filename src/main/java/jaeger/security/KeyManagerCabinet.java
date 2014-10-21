/*
 *  Copyright 2014 University of Washington Licensed under the
 *	Educational Community License, Version 2.0 (the "License"); you may
 *	not use this file except in compliance with the License. You may
 *	obtain a copy of the License at
 *
 *  http://www.osedu.org/licenses/ECL-2.0
 *
 *	Unless required by applicable law or agreed to in writing,
 *	software distributed under the License is distributed on an "AS IS"
 *	BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *	or implied. See the License for the specific language governing
 *	permissions and limitations under the License.
 */
package jaeger.security;

import jaeger.properties.SecurityProperties;
import org.apache.commons.lang.StringUtils;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/**
 * @author James Renfro
 */
public class KeyManagerCabinet {

    private final KeyStore keystore;
	private final KeyManager[] keyManagers;
	private final TrustManager[] trustManagers;
	
	private KeyManagerCabinet(KeyStore keystore, KeyManager[] keyManagers, TrustManager[] trustManagers) {
		this.keystore = keystore;
        this.keyManagers = keyManagers;
		this.trustManagers = trustManagers;
	}

    public KeyStore getKeystore() {
        return keystore;
    }

    public KeyManager[] getKeyManagers() {
		return keyManagers;
	}

	public TrustManager[] getTrustManagers() {
		return trustManagers;
	}

	/*
	 * Fluent builder class, as per Joshua Bloch's Effective Java
	 */
	public final static class Builder {
		
		private final SecurityProperties securitySettings;
		private String keystoreType;
		
		public Builder(SecurityProperties securitySettings) {
			this.securitySettings = securitySettings;
			this.keystoreType = "JKS";
		}
		
		public Builder keystoreType(String keystoreType) {
			this.keystoreType = keystoreType;
			return this;
		}
		
		public KeyManagerCabinet build() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException {
			if (keystoreType == null)
	        	keystoreType = "JKS";

            if (StringUtils.isEmpty(securitySettings.getKeystoreFile()))
                return new KeyManagerCabinet(null, null, null);

	        KeyStore ks = KeyStore.getInstance(keystoreType);

            FileInputStream fis = new FileInputStream(securitySettings.getKeystoreFile());
            char[] password = securitySettings.getKeystorePassword() != null ? securitySettings.getKeystorePassword().toCharArray() : "changeit".toCharArray();

            try {
                ks.load(fis, password);
            } finally {
                if (fis != null)
                    fis.close();
            }

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, password);

			
			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
			tmf.init((KeyStore)null);
			
			return new KeyManagerCabinet(ks, kmf.getKeyManagers(), tmf.getTrustManagers());
		}
		
	}

}
