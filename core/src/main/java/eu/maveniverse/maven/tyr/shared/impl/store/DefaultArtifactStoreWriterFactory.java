/*
 * Copyright (c) 2023-2024 Maveniverse Org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */
package eu.maveniverse.maven.tyr.shared.impl.store;

import eu.maveniverse.maven.tyr.shared.SessionConfig;
import eu.maveniverse.maven.tyr.shared.store.ArtifactStoreWriter;
import eu.maveniverse.maven.tyr.shared.store.ArtifactStoreWriterFactory;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Named
public class DefaultArtifactStoreWriterFactory implements ArtifactStoreWriterFactory {
    @Override
    public ArtifactStoreWriter create(SessionConfig sessionConfig) {
        return new DefaultArtifactStoreWriter(sessionConfig);
    }
}
