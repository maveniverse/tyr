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
import java.util.List;
import java.util.Optional;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * List all existing stores.
 */
@Mojo(name = "list", threadSafe = true, requiresProject = false, aggregator = true)
public class ListMojo extends NjordMojoSupport {
    @Override
    protected void doWithSession(Session ns) throws IOException {
        logger.info("List of existing ArtifactStore:");
        List<String> storeNames = ns.artifactStoreManager().listArtifactStoreNames();
        for (String storeName : storeNames) {
            Optional<ArtifactStore> aso = ns.artifactStoreManager().selectArtifactStore(storeName);
            if (aso.isPresent()) {
                try (ArtifactStore store = aso.orElseThrow(J8Utils.OET)) {
                    logger.info("- " + store);
                }
            }
        }
        logger.info("Total of {} ArtifactStore.", storeNames.size());
    }
}
