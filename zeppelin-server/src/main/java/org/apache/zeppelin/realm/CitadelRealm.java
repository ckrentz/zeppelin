package org.apache.zeppelin.realm;

import org.apache.zeppelin.rest.CredentialRestApi;
import org.apache.zeppelin.utils.CertUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.security.cert.X509Certificate;

public class CitadelRealm  extends AuthorizingRealm {

    Logger logger = LoggerFactory.getLogger(CitadelRealm.class);

    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;

        logger.info("Authenticating with Citadel Realm");

        try {
            //Verify authentication
            logger.info("Getting SSL cert");

            X509Certificate cert = getSSLCertificate();

            logger.info("Retrieving user DN");

            Principal certPrincipal = cert.getSubjectDN();

            logger.info("Cert principal: " + certPrincipal);

            // Check principal against Citadel certificate

            logger.info("Getting principal from token");

            String username = (String) token.getPrincipal();

            logger.info("comparing username with principal");

            if(username.equals(certPrincipal)) {
                // User is authorized

                logger.info("Principals match");
            }

        } catch (Exception e) {
            String msg = StringUtils.clean(e.getMessage());
            if (msg == null) {
                msg = StringUtils.clean(e.getLocalizedMessage());
            }
            if (msg == null) {
                msg = "Invalid login or password.";
            }
            throw new AuthenticationException(msg, e);
        }

        PrincipalCollection principals = new SimplePrincipalCollection(token.getPrincipal(), this.getName());

        try {

        } catch (Exception e) {
            throw new AuthenticationException("Unable to obtain authenticated account properties.", e);
        }

        return new SimpleAuthenticationInfo(principals, null);
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        logger.info("Authorizing with Citadel Realm");
        PrincipalCollection principals = new SimplePrincipalCollection();
        try {

        } catch(Throwable t) {
            System.out.println(t.getMessage());
        }
        SimpleAuthorizationInfo authn = new SimpleAuthorizationInfo();
        return authn;
    }

    private X509Certificate getSSLCertificate() {
        X509Certificate cert = CertUtil.getCertificate(null, "X-BDP-UserCert");

        return cert;
    }
}
