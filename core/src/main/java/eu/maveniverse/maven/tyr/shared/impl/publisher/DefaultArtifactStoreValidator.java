/*
 * Copyright (c) 2023-2024 Maveniverse Org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */
package eu.maveniverse.maven.tyr.shared.impl.publisher;

import static java.util.Objects.requireNonNull;

import eu.maveniverse.maven.tyr.shared.Session;
import eu.maveniverse.maven.tyr.shared.impl.J8Utils;
import eu.maveniverse.maven.tyr.shared.publisher.ArtifactStoreValidator;
import eu.maveniverse.maven.tyr.shared.publisher.spi.BulkValidator;
import eu.maveniverse.maven.tyr.shared.publisher.spi.BulkValidatorFactory;
import eu.maveniverse.maven.tyr.shared.publisher.spi.ValidationContext;
import eu.maveniverse.maven.tyr.shared.publisher.spi.Validator;
import eu.maveniverse.maven.tyr.shared.publisher.spi.ValidatorFactory;
import eu.maveniverse.maven.tyr.shared.store.ArtifactStore;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.util.artifact.ArtifactIdUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultArtifactStoreValidator implements ArtifactStoreValidator {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final Session session;
    private final String name;
    private final String description;
    private final Collection<BulkValidatorFactory> bulkValidatorFactories;
    private final Collection<ValidatorFactory> validatorFactories;

    public DefaultArtifactStoreValidator(
            Session session,
            String name,
            String description,
            Collection<BulkValidatorFactory> bulkValidatorFactories,
            Collection<ValidatorFactory> validatorFactories) {
        this.session = requireNonNull(session);
        this.name = requireNonNull(name);
        this.description = requireNonNull(description);
        this.bulkValidatorFactories = requireNonNull(bulkValidatorFactories);
        this.validatorFactories = requireNonNull(validatorFactories);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public ValidationResult validate(ArtifactStore artifactStore) throws IOException {
        VR vr = new VR(description());
        for (BulkValidatorFactory bulkValidatorFactory : bulkValidatorFactories) {
            try (BulkValidator bulkValidator = bulkValidatorFactory.create(session)) {
                VR child = vr.child(bulkValidator.name());
                bulkValidator.validate(artifactStore, child);
                vr.dropIfEmpty(child);
            }
        }
        ArrayList<Validator> validators = new ArrayList<>();
        ArrayList<IOException> closeErrors = new ArrayList<>();
        try {
            for (ValidatorFactory validatorFactory : validatorFactories) {
                validators.add(validatorFactory.create(session));
            }
            for (Artifact artifact : artifactStore.artifacts()) {
                VR vvr = vr.child(ArtifactIdUtils.toId(artifact));
                for (Validator validator : validators) {
                    VR child = vvr.child(validator.name());
                    validator.validate(artifactStore, artifact, child);
                    vvr.dropIfEmpty(child);
                }
                vr.dropIfEmpty(vvr);
            }
        } finally {
            for (Validator validator : validators) {
                try {
                    validator.close();
                } catch (IOException e) {
                    closeErrors.add(e);
                }
            }
        }
        if (!closeErrors.isEmpty()) {
            IOException close = new IOException("Could not close validators");
            closeErrors.forEach(close::addSuppressed);
            throw close;
        }
        return vr;
    }

    private static final class VR implements ValidationResult, ValidationContext {
        private final String name;
        private final ArrayList<String> info = new ArrayList<>();
        private final ArrayList<String> warnings = new ArrayList<>();
        private final ArrayList<String> errors = new ArrayList<>();
        private final LinkedHashMap<String, VR> children = new LinkedHashMap<>();

        private VR(String name) {
            this.name = name;
        }

        @Override
        public String name() {
            return name;
        }

        private void dropIfEmpty(VR child) {
            if (child.info.isEmpty()
                    && child.warnings.isEmpty()
                    && child.errors.isEmpty()
                    && child.children.isEmpty()) {
                children.remove(child.name());
            }
        }

        @Override
        public Collection<String> info() {
            return J8Utils.copyOf(info);
        }

        @Override
        public Collection<String> warning() {
            return J8Utils.copyOf(warnings);
        }

        @Override
        public Collection<String> error() {
            return J8Utils.copyOf(errors);
        }

        @Override
        public Collection<ValidationResult> children() {
            return J8Utils.copyOf(children.values());
        }

        @Override
        public VR addInfo(String msg) {
            info.add(msg);
            return this;
        }

        @Override
        public VR addWarning(String msg) {
            warnings.add(msg);
            return this;
        }

        @Override
        public VR addError(String msg) {
            errors.add(msg);
            return this;
        }

        @Override
        public VR child(String name) {
            requireNonNull(name);
            VR child = new VR(name);
            children.put(name, child);
            return child;
        }
    }
}
