package org.apache.zeppelin.utils;

import org.apache.shiro.authc.AuthenticationToken;

import java.security.cert.X509Certificate;

public interface X509CertificateAuthenticationToken extends AuthenticationToken {

    public abstract X509Certificate getCertificate();

}
