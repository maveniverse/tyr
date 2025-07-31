/*
 * Copyright (c) 2023-2024 Maveniverse Org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */
package eu.maveniverse.maven.tyr.shared.source;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.version.Version;

public interface ArtifactInfoSource {
    /**
     * Returns the "enlisted versions" for given {@link Artifacts.GAKey}.
     */
    Optional<Collection<Version>> getVersions(Artifacts.GAKey ga);

    /**
     * Returns "data" map for given {@link Artifacts.GAKey}.
     */
    Optional<Map<String, String>> getData(Artifacts.GAKey ga);

    /**
     * Returns the dependency management for given {@link Artifacts.GACEKey}.
     */
    Optional<Dependency> getArtifactManagement(Artifacts.GACEKey key);

    /**
     * Returns "data" map for given {@link Artifacts.GACEKey} and name.
     */
    Optional<Map<String, String>> getArtifactData(Artifacts.GACEKey gace, String name);
}
