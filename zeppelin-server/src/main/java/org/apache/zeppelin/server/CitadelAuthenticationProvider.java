package org.apache.zeppelin.server;

import bdp.citadel.core.model.User;
import bdp.citadel.core.model.UserSerializer;
import bdp.citadel.signature.Verifier;
import bdp.citadel.signature.VerifierFactory;
import bdp.citadel.signature.model.Signature;

import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

/**
 * This class is intended to provide implementation to be used for Citadel authentication. Since, Jersey filters do not
 * intercept websocket requests this process was encapsulated to a provider for the ability to extract user information
 * from the headers of the request for convenience.
 */
public class CitadelAuthenticationProvider {
    private static final UserSerializer USER_SERIALIZER = new UserSerializer();

    private final VerifierFactory verifierFactory;

    @Inject
    public CitadelAuthenticationProvider(VerifierFactory verifierFactory) {
        this.verifierFactory = verifierFactory;
    }

    public User getUser(String serializedUser, String serializedSignature) {
        return getUser(ofNullable(serializedUser), ofNullable(serializedSignature));
    }

    public User getUser(Optional<String> serializedUser, Optional<String> serializedSignature) {
        verifySignature(serializedUser, serializedSignature);

        return new CitadelUser(serializedUser.map((serialized) -> {
            try {
                return USER_SERIALIZER.getDeserializedUser(serialized);
            } catch (IllegalArgumentException e) {
                throw new ForbiddenException("Invalid serialized user", e);
            }
        }).orElseGet(() -> new User(emptyList())));
    }

    private void verifySignature(Optional<String> serializedUser, Optional<String> serializedSignature)
            throws ForbiddenException {
        if (serializedUser.isPresent()) {
            if (!serializedSignature.isPresent()) {
                throw new ForbiddenException("Signature verification failed");
            }

            try {
                Signature signature = Signature.deserialize(serializedSignature.get());
                Verifier verifier = verifierFactory.getVerifier(signature);
                if (!verifier.verify(serializedUser.get(), StandardCharsets.UTF_8, signature)) {
                    throw new ForbiddenException("Signature verification failed");
                }
            } catch (SignatureException | IllegalArgumentException e) {
                throw new ForbiddenException("Signature verification failed", e);
            }
        }
    }
}

