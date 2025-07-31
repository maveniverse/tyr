/*
 * Copyright (c) 2023-2024 Maveniverse Org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */
package eu.maveniverse.maven.tyr.extension3.resolver;

import static java.util.Objects.requireNonNull;

import eu.maveniverse.maven.tyr.shared.source.ArtifactInfoManager;
import eu.maveniverse.maven.tyr.shared.source.Artifacts;
import java.util.Iterator;
import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.collection.VersionFilter;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.eclipse.aether.version.Version;

public class TyrVersionFilter implements VersionFilter {
    private final ArtifactInfoManager artifactInfoManager;

    public TyrVersionFilter(ArtifactInfoManager artifactInfoManager) {
        this.artifactInfoManager = requireNonNull(artifactInfoManager);
    }

    @Override
    public void filterVersions(VersionFilterContext context) throws RepositoryException {
        Dependency dependency = context.getDependency();
        if (JavaScopes.SYSTEM.equals(dependency.getScope())) {
            return;
        }
        Artifact artifact = dependency.getArtifact();
        Artifacts.GAKey ga = Artifacts.getGAKey(artifact);
        Iterator<Version> versions = context.iterator();
        while (versions.hasNext()) {
            Version version = versions.next();
            if (!artifactInfoManager.isReactorProject(ga, version.toString())
                    && !artifactInfoManager.artifactIsEnlisted(artifact, version.toString())) {
                versions.remove();
            }
        }
    }

    @Override
    public VersionFilter deriveChildFilter(DependencyCollectionContext context) {
        return this;
    }
}
