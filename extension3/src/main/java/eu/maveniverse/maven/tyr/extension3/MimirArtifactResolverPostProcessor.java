/*
 * Copyright (c) 2023-2024 Maveniverse Org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */
package eu.maveniverse.maven.tyr.extension3;

import static java.util.Objects.requireNonNull;

import eu.maveniverse.maven.shared.core.component.ComponentSupport;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.spi.connector.checksum.ChecksumAlgorithmFactorySelector;
import org.eclipse.aether.spi.resolution.ArtifactResolverPostProcessor;

@Singleton
@Named
public class MimirArtifactResolverPostProcessor extends ComponentSupport implements ArtifactResolverPostProcessor {
    private final ChecksumAlgorithmFactorySelector checksumAlgorithmFactorySelector;

    @Inject
    public MimirArtifactResolverPostProcessor(ChecksumAlgorithmFactorySelector checksumAlgorithmFactorySelector) {
        this.checksumAlgorithmFactorySelector =
                requireNonNull(checksumAlgorithmFactorySelector, "checksumAlgorithmFactorySelector");
    }

    @Override
    public void postProcess(RepositorySystemSession session, List<ArtifactResult> artifactResults) {
        for (ArtifactResult artifactResult : artifactResults) {
            if (artifactResult.getRequest().getRequestContext().startsWith("project")) {
                logger.info(
                        "[{}] {} @ {} // {}",
                        artifactResult.getRequest().getRequestContext(),
                        artifactResult.getArtifact(),
                        artifactResult.getRepository() != null
                                ? artifactResult.getRepository().getId()
                                : "NA",
                        artifactResult.getLocalArtifactResult() != null
                                ? artifactResult.getLocalArtifactResult()
                                : "NA");
            }
        }
    }
}
