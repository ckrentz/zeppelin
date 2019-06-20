package org.apache.zeppelin.realm;

import bdp.citadel.core.model.AttributeType;
import bdp.citadel.core.model.UserAttribute;
import bdp.citadel.springsecurity.CitadelUser;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.StringUtils;
import org.apache.zeppelin.utils.CertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.cert.X509Certificate;
import java.util.HashSet;
import java.util.Set;

public class CitadelRealm extends AuthorizingRealm {

    Logger logger = LoggerFactory.getLogger(CitadelRealm.class);

    @Override
    protected void assertCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) throws AuthenticationException {

        if (!isUserInCitadel(info)) {
            throw new AuthenticationException("User was not registered in Citadel: " + token.getPrincipal());
        }
    }

    private boolean isUserInCitadel(AuthenticationInfo info) {
        //Look up the user and confirm they exist in Citadel
        CitadelUser user = getUser();
        logger.info("User from Citadel is: " + user.getUsername());

        if(user.getUsername() == info.getPrincipals().getPrimaryPrincipal())
        {
            //User was found in Citadel so we can move on

            if (!isUserZeppelinUser(user)) {
                throw new AuthenticationException("User has not been granted access to Zeppelin: " + info.getPrincipals().getPrimaryPrincipal());
            } else {
                assignRoleToUser(user);
            }
            return true;
        }

        return false;
    }

    private boolean isUserZeppelinUser(CitadelUser user) {
        //Find user role and confirm they are a Zeppelin User
        Set<UserAttribute> roles = user.getAttributes(AttributeType.ROLE);

        logger.info("Roles for User:");

        for(UserAttribute attribute : roles) {

            logger.info(attribute.getValue());
        }

        return false;
    }

    private CitadelUser getUser()  throws AuthenticationException {
        logger.info("Attempting to get user from Citadel...");
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            //	No user is logged in.

            logger.warn("Authentication problem: Citadel authentication was null");
            return null;
        }
        final Object principal = authentication.getPrincipal();
        if (principal instanceof CitadelUser) {
            return (CitadelUser) principal;
        }
        // Unexpected principal type.
        return null;
    }

    private void assignRoleToUser(CitadelUser user) {

    }

    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authcToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authcToken;

        logger.info("Authenticating with Citadel Realm");

        try {
            String username = (String) token.getPrincipal();

            logger.info("Username we got was: " + token.getUsername());

            //Actually cert is being handled by nginx now so we don't need to load it
            //Verify authentication
            /*logger.info("Getting SSL cert");

            X509Certificate cert = getSSLCertificate();

            logger.info("Retrieving user DN");

            Principal certPrincipal = null;

            if(cert != null) {
                certPrincipal = cert.getSubjectDN();
            } else {
                logger.info("Cert was null!");
            }

            logger.info("Cert principal: " + certPrincipal);

            // Check principal against Citadel certificate

            logger.info("Getting principal from token");

            if(username.equals(certPrincipal)) {
                // User is authorized

                logger.info("Principals match");
            }
            */

        } catch (Exception e) {
            String msg = StringUtils.clean(e.getMessage());
            if (msg == null) {
                msg = StringUtils.clean(e.getLocalizedMessage());
            }
            if (msg == null) {
                msg = "Invalid login or password.";
            }
            logger.info(e.toString());
            //throw new AuthenticationException(msg, e);
        }

        PrincipalCollection principals = new SimplePrincipalCollection(token.getPrincipal(), this.getName());

        try {

        } catch (Exception e) {
            throw new AuthenticationException("Unable to obtain authenticated account properties.", e);
        }

        return new SimpleAuthenticationInfo(principals, token.getCredentials());
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        logger.info("Authorizing with Citadel Realm");
        PrincipalCollection principals = new SimplePrincipalCollection();
        try {

        } catch (Throwable t) {
            System.out.println(t.getMessage());
        }
        SimpleAuthorizationInfo authn = new SimpleAuthorizationInfo();
        return authn;
    }

    //Probably not needed anymore
    private X509Certificate getSSLCertificate() {
        //Replace null with HTTP request
        X509Certificate cert = CertUtil.getCertificate(null, "X-BDP-UserCert");

        return cert;
    }
}
