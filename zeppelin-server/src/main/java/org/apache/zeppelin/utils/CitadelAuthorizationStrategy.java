package org.apache.zeppelin.utils;

import bdp.citadel.core.model.UserAttribute;
import bdp.citadel.springsecurity.CitadelUser;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.stream.Collectors;

import static bdp.citadel.core.model.AttributeType.*;

public class CitadelAuthorizationStrategy implements AuthorizationStrategy {
    @Override
    public String getUsername() {
        return getUser().getUsername();
    }

    @Override
    public String getDisplayName() {
        return getUsername();
    }

    @Override
    public Collection<String> getAuthorizations() {
        return getUser().getAttributes(AUTH).stream().map(UserAttribute::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<String> getRoles() {
        return getUser().getAttributes(ROLE).stream().map(UserAttribute::getValue)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<String> getGroups() {
        return getUser().getAttributes(GROUP).stream().map(UserAttribute::getValue)
                .collect(Collectors.toList());
    }

    private CitadelUser getUser() {
        return (CitadelUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
