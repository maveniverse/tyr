/*
 * Copyright (c) 2023-2024 Maveniverse Org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */
package eu.maveniverse.maven.tyr.plugin3;

import eu.maveniverse.maven.tyr.shared.Session;
import eu.maveniverse.maven.tyr.shared.publisher.ArtifactStorePublisher;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Lists available publishers.
 */
@Mojo(name = "list-publishers", threadSafe = true, requiresProject = false, aggregator = true)
public class ListPublishersMojo extends NjordMojoSupport {
    @Override
    protected void doWithSession(Session ns) {
        logger.info("Listing available publishers:");
        for (ArtifactStorePublisher publisher : ns.availablePublishers()) {
            printPublisher(publisher);
        }
    }
}
