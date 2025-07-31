/*
 * Copyright (c) 2023-2024 Maveniverse Org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */
package eu.maveniverse.maven.tyr.shared.source;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Optional;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.version.Version;
import org.eclipse.aether.version.VersionConstraint;
import org.eclipse.aether.version.VersionRange;
import org.eclipse.aether.version.VersionScheme;

public interface ArtifactInfoManager extends ArtifactInfoSource, VersionScheme {
    /**
     * The possible modes of Tyr.
     */
    enum Mode {
        ADVISORY,
        STRICT
    }

    /**
     * Returns the manager mode.
     */
    Mode getMode(MavenProject project);

    /**
     * VersionScheme method. Throws {@link IllegalArgumentException} instead.
     */
    @Override
    Version parseVersion(String version);

    /**
     * VersionScheme method. Throws {@link IllegalArgumentException} instead.
     */
    @Override
    VersionRange parseVersionRange(String range);

    /**
     * VersionScheme method. Throws {@link IllegalArgumentException} instead.
     */
    @Override
    VersionConstraint parseVersionConstraint(String constraint);

    /**
     * Returns the version of the GA, if the GA is part of the current reactor.
     */
    Optional<Version> getReactorProjectVersion(Artifacts.GAKey ga);

    /**
     * Returns {@code true} if the GA and given V (dependency version constraint) is contained in the
     * current reactor.
     */
    default boolean isReactorProject(Artifacts.GAKey ga, String depVersion) {
        requireNonNull(ga);
        requireNonNull(depVersion);
        Optional<Version> v = getReactorProjectVersion(ga);
        if (v.isPresent()) {
            return parseVersionConstraint(depVersion).containsVersion(v.orElseThrow());
        }
        return false;
    }

    /**
     * Returns {@code true} if given artifact (dependency) is managed/enlisted by Tyr.
     */
    default boolean artifactIsEnlisted(Artifact artifact) {
        return artifactIsEnlisted(artifact, artifact.getVersion());
    }

    /**
     * Returns {@code true} if given artifact (dependency) is managed/enlisted by Tyr.
     */
    default boolean artifactIsEnlisted(Artifact artifact, String depVersion) {
        Optional<Collection<Version>> versions = getVersions(Artifacts.getGAKey(artifact));
        if (versions.isPresent()) {
            VersionConstraint artifactConstraint = parseVersionConstraint(depVersion);
            for (Version version : versions.orElseThrow()) {
                if (artifactConstraint.containsVersion(version)) {
                    return true;
                }
            }
        }
        return false;
    }
}
