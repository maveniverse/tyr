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
import eu.maveniverse.maven.tyr.shared.publisher.ArtifactStorePublisher;
import eu.maveniverse.maven.tyr.shared.publisher.ArtifactStoreValidator;
import eu.maveniverse.maven.tyr.shared.store.ArtifactStore;
import java.io.IOException;
import java.util.Optional;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Validate given store against given publisher.
 */
@Mojo(name = "validate", threadSafe = true, requiresProject = false, aggregator = true)
public class ValidateMojo extends PublisherSupportMojo {
    /**
     * Show detailed validation report.
     */
    @Parameter(required = true, property = "details", defaultValue = "false")
    private boolean details;

    /**
     * If showing details, show full validation report (even valid checks too).
     */
    @Parameter(required = true, property = "full", defaultValue = "false")
    private boolean full;

    @Override
    protected void doWithSession(Session ns) throws IOException, MojoFailureException {
        ArtifactStorePublisher p = getArtifactStorePublisher(ns);
        try (ArtifactStore from = getArtifactStore(ns)) {
            Optional<ArtifactStoreValidator.ValidationResult> vro = p.validate(from);
            if (vro.isPresent()) {
                logger.info("Validated {} against {}", from, p.name());
                ArtifactStoreValidator.ValidationResult vr = vro.orElseThrow(J8Utils.OET);
                if (details) {
                    logger.info("Validation results for {}", from.name());
                    dumpValidationResult("", vr, full);
                }
                if (!vr.isValid()) {
                    logger.error("ArtifactStore {} failed validation with {} errors", from, vr.errorCount());
                    throw new MojoFailureException("ArtifactStore " + from.name() + " failed validation");
                } else {
                    int warnings = vr.warningCount();
                    if (warnings > 0) {
                        logger.warn(
                                "ArtifactStore {} passed {} validation with {} warnings",
                                from,
                                vr.checkCount(),
                                warnings);
                    } else {
                        logger.info("ArtifactStore {} passed {} validation", from, vr.checkCount());
                    }
                }
            } else {
                logger.info("No applicable validator for {} set in publisher {}; validation skipped", from, p.name());
            }
        }
    }

    private void dumpValidationResult(String prefix, ArtifactStoreValidator.ValidationResult vr, boolean full) {
        if (full) {
            logger.info("{} {}", prefix, vr.name());
            if (!vr.error().isEmpty()) {
                for (String msg : vr.error()) {
                    logger.error("{}    {}", prefix, msg);
                }
            }
            if (!vr.warning().isEmpty()) {
                for (String msg : vr.warning()) {
                    logger.warn("{}    {}", prefix, msg);
                }
            }
            if (!vr.info().isEmpty()) {
                for (String msg : vr.info()) {
                    logger.info("{}    {}", prefix, msg);
                }
            }
        } else {
            if (!vr.isValid()) {
                logger.error("{} {}", prefix, vr.name());
                if (!vr.error().isEmpty()) {
                    for (String msg : vr.error()) {
                        logger.error("{}    {}", prefix, msg);
                    }
                }
            }
        }
        for (ArtifactStoreValidator.ValidationResult child : vr.children()) {
            dumpValidationResult(prefix + "  ", child, full);
        }
    }
}
