/*
 * Copyright (c) 2023-2024 Maveniverse Org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */
package eu.maveniverse.maven.tyr.shared.impl.publisher.basic;

import eu.maveniverse.maven.tyr.shared.impl.publisher.ValidatorSupport;
import eu.maveniverse.maven.tyr.shared.publisher.spi.ValidationContext;
import eu.maveniverse.maven.tyr.shared.store.ArtifactStore;
import java.io.IOException;
import org.eclipse.aether.artifact.Artifact;

/**
 * Verifies any found and support archive for proper paths.
 */
public class ArchiveValidator extends ValidatorSupport {
    public ArchiveValidator(String name) {
        super(name);
    }

    @Override
    public void validate(ArtifactStore artifactStore, Artifact artifact, ValidationContext collector)
            throws IOException {
        if (JAR.equals(artifact.getExtension())) {
            // TODO
        }
    }
}
