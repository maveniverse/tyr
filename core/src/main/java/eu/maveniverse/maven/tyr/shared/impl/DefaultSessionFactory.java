/*
 * Copyright (c) 2023-2024 Maveniverse Org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */
package eu.maveniverse.maven.tyr.shared.impl;

import static java.util.Objects.requireNonNull;

import eu.maveniverse.maven.mima.extensions.mmr.internal.MavenModelReaderImpl;
import eu.maveniverse.maven.tyr.shared.SessionConfig;
import eu.maveniverse.maven.tyr.shared.SessionFactory;
import eu.maveniverse.maven.tyr.shared.publisher.ArtifactPublisherRedirectorFactory;
import eu.maveniverse.maven.tyr.shared.publisher.ArtifactStorePublisherFactory;
import eu.maveniverse.maven.tyr.shared.store.ArtifactStoreComparatorFactory;
import eu.maveniverse.maven.tyr.shared.store.ArtifactStoreMergerFactory;
import eu.maveniverse.maven.tyr.shared.store.ArtifactStoreWriterFactory;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Named
public class DefaultSessionFactory implements SessionFactory {
    private final InternalArtifactStoreManagerFactory internalArtifactStoreManagerFactory;
    private final ArtifactStoreWriterFactory artifactStoreWriterFactory;
    private final ArtifactStoreMergerFactory artifactStoreMergerFactory;
    private final ArtifactPublisherRedirectorFactory artifactPublisherRedirectorFactory;
    private final Map<String, ArtifactStorePublisherFactory> artifactStorePublisherFactories;
    private final Map<String, ArtifactStoreComparatorFactory> artifactStoreComparatorFactories;
    private final MavenModelReaderImpl mavenModelReader;

    @Inject
    public DefaultSessionFactory(
            InternalArtifactStoreManagerFactory internalArtifactStoreManagerFactory,
            ArtifactStoreWriterFactory artifactStoreWriterFactory,
            ArtifactStoreMergerFactory artifactStoreMergerFactory,
            ArtifactPublisherRedirectorFactory artifactPublisherRedirectorFactory,
            Map<String, ArtifactStorePublisherFactory> artifactStorePublisherFactories,
            Map<String, ArtifactStoreComparatorFactory> artifactStoreComparatorFactories,
            MavenModelReaderImpl mavenModelReader) {
        this.internalArtifactStoreManagerFactory = requireNonNull(internalArtifactStoreManagerFactory);
        this.artifactStoreWriterFactory = requireNonNull(artifactStoreWriterFactory);
        this.artifactStoreMergerFactory = requireNonNull(artifactStoreMergerFactory);
        this.artifactPublisherRedirectorFactory = requireNonNull(artifactPublisherRedirectorFactory);
        this.artifactStorePublisherFactories = requireNonNull(artifactStorePublisherFactories);
        this.artifactStoreComparatorFactories = requireNonNull(artifactStoreComparatorFactories);
        this.mavenModelReader = requireNonNull(mavenModelReader);
    }

    @Override
    public DefaultSession create(SessionConfig sessionConfig) {
        return new DefaultSession(
                sessionConfig,
                internalArtifactStoreManagerFactory,
                artifactStoreWriterFactory,
                artifactStoreMergerFactory,
                artifactPublisherRedirectorFactory,
                artifactStorePublisherFactories,
                artifactStoreComparatorFactories,
                mavenModelReader);
    }
}
