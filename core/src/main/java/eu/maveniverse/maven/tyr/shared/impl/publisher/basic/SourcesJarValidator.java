/*
 * Copyright (c) 2023-2024 Maveniverse Org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */
package eu.maveniverse.maven.tyr.shared.impl.publisher.basic;

import eu.maveniverse.maven.tyr.shared.impl.J8Utils;
import eu.maveniverse.maven.tyr.shared.impl.publisher.ValidatorSupport;
import eu.maveniverse.maven.tyr.shared.publisher.spi.ValidationContext;
import eu.maveniverse.maven.tyr.shared.store.ArtifactStore;
import java.io.IOException;
import org.eclipse.aether.artifact.Artifact;

/**
 * Verifies presence of sources JAR for every main JAR artifact.
 */
public class SourcesJarValidator extends ValidatorSupport {
    public SourcesJarValidator(String name) {
        super(name);
    }

    @Override
    public void validate(ArtifactStore artifactStore, Artifact artifact, ValidationContext collector)
            throws IOException {
        if (mainJar(artifact)) {
            if (artifactStore.artifactPresent(sourcesJar(artifact))) {
                collector.addInfo("PRESENT");
            } else {
                if (jarContainsJavaClasses(
                        artifactStore.artifactContent(artifact).orElseThrow(J8Utils.OET))) {
                    collector.addError("MISSING");
                }
            }
        }
    }
}
