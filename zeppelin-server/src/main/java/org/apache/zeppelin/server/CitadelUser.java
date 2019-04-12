package org.apache.zeppelin.server;

import bdp.citadel.core.model.User;
import bdp.citadel.core.model.UserAttribute;

import javax.annotation.Nonnull;
import java.security.Principal;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import static bdp.citadel.core.model.AttributeType.AUTH;
import static bdp.citadel.core.model.AttributeType.NAME;

public class CitadelUser extends User implements Principal {
    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_NAME = "-";

    public CitadelUser() {
    }

    public CitadelUser(@Nonnull final User user) {
        this(user.getAttributes());
    }

    public CitadelUser(@Nonnull final Collection<UserAttribute> attributes) {
        super(attributes);
    }

    @Override
    @Nonnull
    public String getName() {
        return getAttributes(NAME).stream()
                .findFirst()
                .map(UserAttribute::getValue)
                .orElse(DEFAULT_NAME);
    }

    public Set<String> getAuths() {
        Set<String> auths = new TreeSet<>();
        getAttributes(AUTH).stream()
                .map(UserAttribute::getValue)
                .forEach(auths::add);
        getAttributes().stream()
                .map(UserAttribute::toString)
                .forEach(auths::add);
        return auths;
    }

    public Set<String> getAuths(Set<String> systemAuths) {
        Set<String> auths = getAuths();
        auths.retainAll(systemAuths);
        return auths;
    }
}
