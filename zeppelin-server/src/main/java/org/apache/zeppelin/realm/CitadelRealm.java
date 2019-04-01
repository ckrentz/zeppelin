package org.apache.zeppelin.realm;

import org.apache.zeppelin.utils.CertUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.StringUtils;

import java.security.Principal;
import java.security.cert.X509Certificate;

public class CitadelRealm  extends AuthorizingRealm {
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;

        try {
            //Verify authentication

            String username = (String) token.getPrincipal();

            X509Certificate cert = getSSLCertificate();

            Principal certPrincipal = cert.getSubjectDN();

            // Check principal against Citadel certificate

            if(username.equals(certPrincipal)) {
                // User is authorized
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
