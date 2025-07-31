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
import java.util.Optional;
import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.collection.DependencyManagement;
import org.eclipse.aether.collection.DependencyManager;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.util.artifact.JavaScopes;

public class TyrDependencyManager implements DependencyManager {
    private final DependencyManager delegate;
    private final ArtifactInfoManager artifactInfoManager;

    public TyrDependencyManager(DependencyManager delegate, ArtifactInfoManager artifactInfoManager) {
        this.delegate = requireNonNull(delegate);
        this.artifactInfoManager = requireNonNull(artifactInfoManager);
    }

    @Override
    public DependencyManagement manageDependency(Dependency dependency) {
        DependencyManagement management = delegate.manageDependency(dependency);
        if (!JavaScopes.SYSTEM.equals(dependency.getScope())) {
            Optional<Dependency> depMgt =
                    artifactInfoManager.getArtifactManagement(Artifacts.getGACEKey(dependency.getArtifact()));
            if (depMgt.isPresent()) {
                Dependency dep = depMgt.orElseThrow();
                if (!dep.getArtifact().getVersion().isBlank()) {
                    if (management == null) {
                        management = new DependencyManagement();
                    }
                    management.setVersion(dep.getArtifact().getVersion());
                }
                if (dep.getScope() != null) {
                    if (management == null) {
                        management = new DependencyManagement();
                    }
                    management.setScope(dep.getScope());
                }
                if (dep.getOptional() != null) {
                    if (management == null) {
                        management = new DependencyManagement();
                    }
                    management.setOptional(dep.getOptional());
                }
                if (!dep.getExclusions().isEmpty()) {
                    if (management == null) {
                        management = new DependencyManagement();
                    }
                    management.setExclusions(dep.getExclusions());
                }
            }
        }
        return management;
    }

    @Override
    public DependencyManager deriveChildManager(DependencyCollectionContext context) {
        DependencyManager childManager = delegate.deriveChildManager(context);
        return childManager != delegate ? new TyrDependencyManager(childManager, artifactInfoManager) : this;
    }
}
