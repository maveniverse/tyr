/*
 * Copyright (c) 2023-2024 Maveniverse Org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */
package eu.maveniverse.maven.tyr.shared.publisher;

import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;

/**
 * Maven Central publisher factory is an artifact store publisher factory that creates publishers for Maven Central.
 */
public interface MavenCentralPublisherFactory extends ArtifactStorePublisherFactory {
    RemoteRepository CENTRAL = new RemoteRepository.Builder(
                    "central", "default", "https://repo.maven.apache.org/maven2/")
            .setSnapshotPolicy(new RepositoryPolicy(false, null, null))
            .build();
}
