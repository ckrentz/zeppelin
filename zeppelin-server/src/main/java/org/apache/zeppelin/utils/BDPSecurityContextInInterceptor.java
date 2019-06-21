package org.apache.zeppelin.utils;

import java.util.HashMap;
import java.util.Map;

public class BDPSecurityContextInInterceptor extends HeaderSecurityContextInInterceptor {

    private static final String HEADER_BDP_ISSUER_DN = "X-BDP-IssuerDn";
    private static final String HEADER_BDP_USER = "X-BDP-User";
    private static final String HEADER_BDP_USER_ATTRIBUTES = "X-BDP-UserAttribute";
    private static final String HEADER_BDP_USER_DN = "X-BDP-UserDn";
    private static final String HEADER_BDP_USER_NAME = "X-BDP-UserName";
    private static final String HEADER_BDP_USER_SIGNATURE = "X-BDP-UserSignature";

    private static final String PREFIX_SEPERATOR = ":";

    private static final String PREFIX_AUT = "AUTH";
    private static final String PREFIX_GROUP = "GROUP";
    private static final String PREFIX_ROLE = "ROLE";

    public BDPSecurityContextInInterceptor()
    {
        super(createParameters());
    }

    private static Map<String, String> createParameters()
    {
        Map<String, String> params = new HashMap<>();

        params.put(HeaderSecurityContextInInterceptor.ATTR_HEADER_USERNAME, HEADER_BDP_USER_NAME);
        params.put(HeaderSecurityContextInInterceptor.ATTR_HEADER_ROLES, HEADER_BDP_USER_ATTRIBUTES);
        params.put(HeaderSecurityContextInInterceptor.ATTR_HEADER_ROLES_SEPERATOR, PREFIX_SEPERATOR);
        params.put(HeaderSecurityContextInInterceptor.ATTR_HEADER_ROLES_PREFIXES,
                String.join(",", PREFIX_AUT, PREFIX_GROUP, PREFIX_ROLE));

        return params;
    }

}