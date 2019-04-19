package org.apache.zeppelin.utils;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

/**
 *
 * Utility class to read the certificate information from the request header.
 *
 * If request is null (or) do not have header value it returns
 * null, else return X509Certificate. In exceptional cases it returns null.
 *
 */
public class CertUtil {

    /**
     *
     * @param request
     * @return
     */
    public static X509Certificate getCertificate(HttpServletRequest request, String SSL_CLIENT_CERT_HEADER) {
        if (request == null) {
            return null;
        }

        /* Read the certificate information from the header 'SSL_CLIENT_CERT_HEADER' */
        String certificateInfo = request.getHeader(SSL_CLIENT_CERT_HEADER);

        if (certificateInfo == null || certificateInfo.isEmpty()) {
            return null;
        }

        try (InputStream is = new ByteArrayInputStream(Base64.getDecoder().decode(certificateInfo))) {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) cf.generateCertificate(is);
            return cert;
        } catch (Exception e) {
            return null;
        }
    }
}