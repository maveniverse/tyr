/*
 * Copyright (c) 2023-2024 Maveniverse Org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */
package eu.maveniverse.maven.tyr.shared.source;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;
import org.eclipse.aether.artifact.Artifact;

public final class Artifacts {
    private Artifacts() {}

    public static GAKey getGAKey(String groupId, String artifactId) {
        return new GAKeyImpl(groupId, artifactId);
    }

    public static GAKey getGAKey(Artifact artifact) {
        return new GAKeyImpl(artifact.getGroupId(), artifact.getArtifactId());
    }

    public static GACEKey getGACEKey(String groupId, String artifactId, String classifier, String extension) {
        return new GACEKeyImpl(
                groupId, artifactId, classifier == null || classifier.isBlank() ? null : classifier, extension, null);
    }

    public static GACEKey getGACEKey(Artifact artifact) {
        return new GACEKeyImpl(
                artifact.getGroupId(),
                artifact.getArtifactId(),
                artifact.getClassifier().isBlank() ? null : artifact.getClassifier(),
                artifact.getExtension(),
                null);
    }

    public static GACEVKey getGACEVKey(
            String groupId, String artifactId, String classifier, String extension, String version) {
        return new GACEKeyImpl(
                groupId,
                artifactId,
                classifier == null || classifier.isBlank() ? null : classifier,
                extension,
                version);
    }

    public static GACEVKey getGACEVKey(Artifact artifact) {
        return new GACEKeyImpl(
                artifact.getGroupId(),
                artifact.getArtifactId(),
                artifact.getClassifier().isBlank() ? null : artifact.getClassifier(),
                artifact.getExtension(),
                artifact.getVersion());
    }

    public interface GAKey {
        String getGroupId();

        String getArtifactId();
    }

    public interface GACEKey extends GAKey {
        Optional<String> getClassifier();

        String getExtension();
    }

    public interface GACEVKey extends GACEKey {
        Optional<String> getVersion();
    }

    private static final class GAKeyImpl implements GAKey {
        private final String groupId;
        private final String artifactId;
        private final int hashCode;

        private GAKeyImpl(String groupId, String artifactId) {
            this.groupId = requireNonNull(groupId);
            this.artifactId = requireNonNull(artifactId);
            this.hashCode = Objects.hash(groupId, artifactId);
        }

        @Override
        public String getGroupId() {
            return groupId;
        }

        @Override
        public String getArtifactId() {
            return artifactId;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            GAKeyImpl key = (GAKeyImpl) o;
            return Objects.equals(groupId, key.groupId) && Objects.equals(artifactId, key.artifactId);
        }

        @Override
        public int hashCode() {
            return hashCode;
        }
    }

    private static final class GACEKeyImpl implements GACEVKey {
        private final String groupId;
        private final String artifactId;
        private final String classifier;
        private final String extension;
        private final String version;
        private final int hashCode;

        private GACEKeyImpl(String groupId, String artifactId, String classifier, String extension, String version) {
            this.groupId = requireNonNull(groupId);
            this.artifactId = requireNonNull(artifactId);
            this.classifier = classifier;
            this.extension = requireNonNull(extension);
            this.version = version;
            this.hashCode = Objects.hash(groupId, artifactId, classifier, extension, version);
        }

        @Override
        public String getGroupId() {
            return groupId;
        }

        @Override
        public String getArtifactId() {
            return artifactId;
        }

        @Override
        public Optional<String> getClassifier() {
            return Optional.ofNullable(classifier);
        }

        @Override
        public String getExtension() {
            return extension;
        }

        @Override
        public Optional<String> getVersion() {
            return Optional.ofNullable(version);
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            GACEKeyImpl gaceKey = (GACEKeyImpl) o;
            return Objects.equals(groupId, gaceKey.groupId)
                    && Objects.equals(artifactId, gaceKey.artifactId)
                    && Objects.equals(classifier, gaceKey.classifier)
                    && Objects.equals(extension, gaceKey.extension);
        }

        @Override
        public int hashCode() {
            return hashCode;
        }
    }
}
