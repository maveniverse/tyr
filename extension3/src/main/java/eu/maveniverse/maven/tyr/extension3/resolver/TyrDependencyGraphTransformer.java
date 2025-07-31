/*
 * Copyright (c) 2023-2024 Maveniverse Org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */
package eu.maveniverse.maven.tyr.extension3.resolver;

import eu.maveniverse.maven.tyr.shared.source.ArtifactInfoManager;
import eu.maveniverse.maven.tyr.shared.source.Artifacts;
import java.util.*;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.DependencyGraphTransformationContext;
import org.eclipse.aether.collection.DependencyGraphTransformer;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.graph.DependencyVisitor;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.eclipse.aether.version.Version;

public class TyrDependencyGraphTransformer implements DependencyGraphTransformer {
    private final ArtifactInfoManager artifactInfoManager;
    private final MavenProject project;
    private final boolean strictVersions;
    private final boolean blockDependencies;

    public TyrDependencyGraphTransformer(
            ArtifactInfoManager artifactInfoManager,
            MavenProject project,
            boolean strictVersion,
            boolean blockDependencies) {
        this.artifactInfoManager = artifactInfoManager;
        this.project = project;
        this.strictVersions = strictVersion;
        this.blockDependencies = blockDependencies;
    }

    @Override
    public DependencyNode transformGraph(DependencyNode node, DependencyGraphTransformationContext context)
            throws RepositoryException {
        final List<List<DependencyNode>> blocked = new ArrayList<>();
        node.accept(new DependencyVisitor() {
            final Set<DependencyNode> visited = new HashSet<>();
            final ArrayDeque<DependencyNode> trail = new ArrayDeque<>();

            @Override
            public boolean visitLeave(DependencyNode node) {
                trail.pop();
                return true;
            }

            @Override
            public boolean visitEnter(DependencyNode node) {
                trail.push(node);
                if (!visited.add(node)) {
                    return false; // dependency cycle? do not recurse into children then.
                }
                process(node);
                return true;
            }

            private void process(DependencyNode node) {
                Dependency dependency = node.getDependency();

                if (dependency == null || JavaScopes.SYSTEM.equals(dependency.getScope())) {
                    return;
                }

                Artifact artifact = node.getArtifact();
                Artifacts.GAKey ga = Artifacts.getGAKey(artifact);

                if (artifactInfoManager.isReactorProject(ga, artifact.getVersion())) {
                    return;
                }
                if (artifactInfoManager.artifactIsEnlisted(artifact)) {
                    return;
                }

                Optional<Version> version = artifactInfoManager.getReactorProjectVersion(ga);
                if (version.isEmpty()) {
                    Optional<Collection<Version>> versions = artifactInfoManager.getVersions(ga);
                    if (versions.isPresent() && versions.orElseThrow().size() == 1) {
                        version = versions.orElseThrow().iterator().next().toString();
                    }
                }

                if (version != null && (strictVersions || artifact.getVersion().isEmpty())) {
                    node.setArtifact(artifact.setVersion(version.toString()));
                } else {
                    blocked.add(new ArrayList<>(trail));
                }
            }
        });

        if (blockDependencies && !blocked.isEmpty()) {
            StringBuilder message = new StringBuilder("Artifacts are not part of the project build target platform:");

            final Set<RemoteRepository> repositories = new LinkedHashSet<>();
            for (int blockedIdx = 0; blockedIdx < blocked.size(); blockedIdx++) {
                List<DependencyNode> trail = blocked.get(blockedIdx);

                message.append("\n").append(blockedIdx).append(". ");
                message.append(trail.get(trail.size() - 1).getArtifact());
                if (trail.size() > 2) {
                    message.append(", through dependency path");
                    message.append("\n   ").append(project);
                    for (int trailIdx = 1; trailIdx < trail.size(); trailIdx++) {
                        message.append("\n   ");
                        if (trailIdx == trail.size() - 1) {
                            message.append(" <blocked> ");
                        }
                        DependencyNode trailNode = trail.get(trailIdx);
                        message.append(trailNode.getDependency());
                        repositories.addAll(trailNode.getRepositories());
                    }
                }
                message.append("\n\n");
            }

            message.append("Remote repositories:\n");
            for (RemoteRepository repository : repositories) {
                message.append('\t').append(repository.toString()).append('\n');
            }
            throw new RepositoryException(message.toString());
        }

        return node;
    }
}
