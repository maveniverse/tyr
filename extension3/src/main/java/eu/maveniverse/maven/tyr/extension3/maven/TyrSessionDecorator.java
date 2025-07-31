/*
 * Copyright (c) 2023-2024 Maveniverse Org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */
package eu.maveniverse.maven.tyr.extension3.maven;

import static java.util.Objects.requireNonNull;

import eu.maveniverse.maven.tyr.extension3.resolver.TyrDependencyGraphTransformer;
import eu.maveniverse.maven.tyr.extension3.resolver.TyrDependencyManager;
import eu.maveniverse.maven.tyr.extension3.resolver.TyrVersionFilter;
import eu.maveniverse.maven.tyr.shared.source.ArtifactInfoManager;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.maven.SessionScoped;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.RepositorySessionDecorator;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.util.graph.transformer.ChainedDependencyGraphTransformer;
import org.eclipse.aether.util.graph.version.ChainedVersionFilter;

@Named
@SessionScoped
public class TyrSessionDecorator implements RepositorySessionDecorator {

    private final ArtifactInfoManager artifactInfoManager;

    @Inject
    public TyrSessionDecorator(ArtifactInfoManager artifactInfoManager) {
        this.artifactInfoManager = requireNonNull(artifactInfoManager);
    }

    @Override
    public RepositorySystemSession decorate(final MavenProject project, final RepositorySystemSession session) {

        final TakariTargetPlatform targetPlatform = targetPlatformProvider.getTargetPlatform(project);
        if (targetPlatform == null) {
            return null;
        }

        final boolean strict = artifactInfoManager.getMode(project) == ArtifactInfoManager.Mode.STRICT;
        final DefaultRepositorySystemSession filtered = new DefaultRepositorySystemSession(session);
        filtered.setDependencyGraphTransformer(ChainedDependencyGraphTransformer.newInstance(
                new TyrDependencyGraphTransformer(artifactInfoManager, project, strict, strict),
                filtered.getDependencyGraphTransformer()));

        if (strict) {
            filtered.setVersionFilter(ChainedVersionFilter.newInstance(
                    filtered.getVersionFilter(), new TyrVersionFilter(artifactInfoManager)));

            filtered.setDependencyManager(
                    new TyrDependencyManager(filtered.getDependencyManager(), artifactInfoManager));
        }

        return filtered;
    }
}
