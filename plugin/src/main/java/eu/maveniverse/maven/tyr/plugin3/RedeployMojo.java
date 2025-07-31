/*
 * Copyright (c) 2023-2024 Maveniverse Org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */
package eu.maveniverse.maven.tyr.plugin3;

import eu.maveniverse.maven.tyr.shared.Session;
import eu.maveniverse.maven.tyr.shared.impl.J8Utils;
import eu.maveniverse.maven.tyr.shared.store.ArtifactStore;
import java.io.IOException;
import java.util.Optional;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Redeploys {@code from} store onto {@code to} store, eventually dropping {@code from} store.
 */
@Mojo(name = "redeploy", threadSafe = true, requiresProject = false, aggregator = true)
public class RedeployMojo extends NjordMojoSupport {
    /**
     * The name of the source store.
     */
    @Parameter(required = true, property = "from")
    private String from;

    /**
     * The name of the target store.
     */
    @Parameter(required = true, property = "to")
    private String to;

    /**
     * Whether source store should be dropped after successful operation.
     */
    @Parameter(required = true, property = "drop", defaultValue = "true")
    private boolean drop;

    @Override
    protected void doWithSession(Session ns) throws IOException {
        Optional<ArtifactStore> fromOptional = ns.artifactStoreManager().selectArtifactStore(from);
        Optional<ArtifactStore> toOptional = ns.artifactStoreManager().selectArtifactStore(to);
        if (!fromOptional.isPresent()) {
            logger.warn("ArtifactStore with given name not found: {}", from);
            return;
        }
        if (!toOptional.isPresent()) {
            logger.warn("ArtifactStore with given name not found: {}", to);
            return;
        }
        ns.artifactStoreMerger().redeploy(fromOptional.orElseThrow(J8Utils.OET), toOptional.orElseThrow(J8Utils.OET));
        if (drop) {
            logger.info("Dropping {}", from);
            ns.artifactStoreManager().dropArtifactStore(from);
        }
    }
}
