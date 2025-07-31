/*
 * Copyright (c) 2023-2024 Maveniverse Org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */
package eu.maveniverse.maven.tyr.shared.source;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.version.Version;

import java.util.Collection;

public interface ArtifactInfoManager {
    /**
     * Returns {@code true} if given artifact is contained in existing sources.
     */
    boolean contains(Artifact artifact);

    boolean containsVersion(Artifact artifact, String version);

    Collection<Version> versions(Artifacts.GAKey ga);

    Collection<Version> versions(Artifact artifact);
}
