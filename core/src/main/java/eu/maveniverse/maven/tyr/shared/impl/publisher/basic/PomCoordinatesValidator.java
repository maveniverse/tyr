/*
 * Copyright (c) 2023-2024 Maveniverse Org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */
package eu.maveniverse.maven.tyr.shared.impl.publisher.basic;

import static java.util.Objects.requireNonNull;

import eu.maveniverse.maven.tyr.shared.Session;
import eu.maveniverse.maven.tyr.shared.impl.J8Utils;
import eu.maveniverse.maven.tyr.shared.impl.publisher.ValidatorSupport;
import eu.maveniverse.maven.tyr.shared.publisher.spi.ValidationContext;
import eu.maveniverse.maven.tyr.shared.store.ArtifactStore;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import org.apache.maven.model.Model;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;

/**
 * Verifies any found POM that its coordinates matches layout.
 */
public class PomCoordinatesValidator extends ValidatorSupport {
    private final Session session;

    public PomCoordinatesValidator(String name, Session session) {
        super(name);
        this.session = requireNonNull(session);
    }

    @Override
    public void validate(ArtifactStore artifactStore, Artifact artifact, ValidationContext collector)
            throws IOException {
        if (mainPom(artifact)) {
            ArrayList<RemoteRepository> remoteRepositories =
                    new ArrayList<>(session.config().allRemoteRepositories());
            remoteRepositories.add(0, artifactStore.storeRemoteRepository());
            Optional<Model> mo = session.readEffectiveModel(artifact, remoteRepositories);
            if (mo.isPresent()) {
                Model m = mo.orElseThrow(J8Utils.OET);
                if (Objects.equals(artifact.getGroupId(), m.getGroupId())
                        && Objects.equals(artifact.getArtifactId(), m.getArtifactId())
                        && Objects.equals(artifact.getBaseVersion(), m.getVersion())) {
                    collector.addInfo("VALID");
                } else {
                    collector.addError(String.format(
                            "MISMATCH: %s:%s:%s != %s:%s:%s",
                            artifact.getGroupId(),
                            artifact.getArtifactId(),
                            artifact.getBaseVersion(),
                            m.getGroupId(),
                            m.getArtifactId(),
                            m.getVersion()));
                }
            } else {
                collector.addWarning("Could not get effective model");
            }
        }
    }
}
