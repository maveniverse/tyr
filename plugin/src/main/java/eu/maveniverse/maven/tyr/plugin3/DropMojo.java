/*
 * Copyright (c) 2023-2024 Maveniverse Org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */
package eu.maveniverse.maven.tyr.plugin3;

import eu.maveniverse.maven.tyr.shared.Session;
import java.io.IOException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Drops given store.
 */
@Mojo(name = "drop", threadSafe = true, requiresProject = false, aggregator = true)
public class DropMojo extends NjordMojoSupport {
    /**
     * The name of the store to drop. As operation is destructive, this parameter is mandatory.
     */
    @Parameter(required = true, property = "store")
    private String store;

    @Override
    protected void doWithSession(Session ns) throws IOException {
        if (ns.artifactStoreManager().dropArtifactStore(store)) {
            logger.info("Dropped ArtifactStore {}", store);
        } else {
            logger.warn("ArtifactStore with given name not found");
        }
    }
}
