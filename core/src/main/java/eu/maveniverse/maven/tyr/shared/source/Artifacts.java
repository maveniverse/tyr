/*
 * Copyright (c) 2023-2024 Maveniverse Org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */
package eu.maveniverse.maven.tyr.shared.source;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.eclipse.aether.version.Version;

public interface Artifacts {
    interface GAKey {
        String getGroupId();
        String getArtifactId();
    }

    interface GACEKey extends GAKey {
        Optional<String> getClassifier();
        String getExtension();
    }

    final class GAKeyImpl implements GAKey {
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

    final class GACEKeyImpl implements GACEKey {
        private final String groupId;
        private final String artifactId;
        private final String classifier;
        private final String extension;
        private final int hashCode;

        private GACEKeyImpl(String groupId, String artifactId, String classifier, String extension) {
            this.groupId = requireNonNull(groupId);
            this.artifactId = requireNonNull(artifactId);
            this.classifier = classifier;
            this.extension = requireNonNull(extension);
            this.hashCode = Objects.hash(groupId, artifactId, classifier, extension);
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

    class Info {
        private final Version version;
        private final String classifier;
        private final String extension;
        private final Map<String, String> checksums;

        private Info(String classifier, String extension, Version version, Map<String, String> checksums) {
            this.classifier = classifier; // optional
            this.extension = requireNonNull(extension);
            this.version = requireNonNull(version);
            this.checksums = requireNonNull(checksums);
        }

        public Optional<String> getClassifier() {
            return Optional.ofNullable(classifier);
        }

        public String getExtension() {
            return extension;
        }

        public Version getVersion() {
            return version;
        }

        public Map<String, String> getChecksums() {
            return checksums;
        }
    }
}
