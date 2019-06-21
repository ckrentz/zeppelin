package org.apache.zeppelin.utils;

import java.util.Collection;

public interface AuthorizationStrategy {
    String getUsername();

    String getDisplayName();

    Collection<String> getAuthorizations();

    Collection<String> getRoles();

    Collection<String> getGroups();
}
