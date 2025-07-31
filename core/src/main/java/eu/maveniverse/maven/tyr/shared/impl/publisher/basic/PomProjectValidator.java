/*
 * Copyright (c) 2023-2024 Maveniverse Org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */
package eu.maveniverse.maven.tyr.shared.impl.publisher.basic;

import static java.util.Objects.requireNonNull;

import eu.maveniverse.maven.tyr.shared.Session;
import eu.maveniverse.maven.tyr.shared.impl.J8Utils;
import eu.maveniverse.maven.tyr.shared.impl.publisher.ValidatorSupport;
import eu.maveniverse.maven.tyr.shared.publisher.spi.ValidationContext;
import eu.maveniverse.maven.tyr.shared.store.ArtifactStore;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import org.apache.maven.model.Developer;
import org.apache.maven.model.License;
import org.apache.maven.model.Model;
import org.apache.maven.model.Scm;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;

/**
 *  Verifies that any found POM name, description, project URL, SCM and license is filled in.
 */
public class PomProjectValidator extends ValidatorSupport {
    private final Session session;

    public PomProjectValidator(String name, Session session) {
        super(name);
        this.session = requireNonNull(session);
    }

    @Override
    public void validate(ArtifactStore artifactStore, Artifact artifact, ValidationContext collector)
            throws IOException {
        if (mainPom(artifact)) {
            ArrayList<RemoteRepository> remoteRepositories =
                    new ArrayList<>(session.config().allRemoteRepositories());
            remoteRepositories.add(0, artifactStore.storeRemoteRepository());
            Optional<Model> mo = session.readEffectiveModel(artifact, remoteRepositories);
            if (mo.isPresent()) {
                Model m = mo.orElseThrow(J8Utils.OET);
                if (!nullOrBlank(m.getName())) {
                    collector.addInfo("VALID project/name");
                } else {
                    collector.addError("MISSING project/name");
                }
                if (!nullOrBlank(m.getDescription())) {
                    collector.addInfo("VALID project/description");
                } else {
                    collector.addError("MISSING project/description");
                }
                if (!nullOrBlank(m.getUrl())) {
                    collector.addInfo("VALID project/url");
                } else {
                    collector.addError("MISSING project/url");
                }
                if (m.getLicenses().isEmpty()) {
                    collector.addError("MISSING project/licenses");
                } else {
                    boolean ok = true;
                    for (License license : m.getLicenses()) {
                        if (ok && (nullOrBlank(license.getName()) || nullOrBlank(license.getUrl()))) {
                            ok = false;
                            collector.addError("MISSING project/licenses/license (name, url)");
                        }
                    }
                    if (ok) {
                        collector.addInfo("VALID project/licenses");
                    }
                }
                if (m.getDevelopers().isEmpty()) {
                    collector.addError("MISSING project/developers");
                } else {
                    boolean ok = true;
                    for (Developer developer : m.getDevelopers()) {
                        if (ok
                                && (nullOrBlank(developer.getId())
                                        && nullOrBlank(developer.getName())
                                        && nullOrBlank(developer.getEmail()))) {
                            ok = false;
                            collector.addError("MISSING project/developers/developer (id, name or email)");
                        }
                        if (ok && nullOrBlank(developer.getId())
                                || nullOrBlank(developer.getName())
                                || nullOrBlank(developer.getEmail())) {
                            collector.addWarning("INCOMPLETE project/developers/developer (id, name or email)");
                        }
                    }
                    if (ok) {
                        collector.addInfo("VALID project/developers");
                    }
                }
                if (m.getScm() == null) {
                    collector.addError("MISSING project/scm");
                } else {
                    Scm scm = m.getScm();
                    if (nullOrBlank(scm.getUrl())
                            || nullOrBlank(scm.getConnection())
                            || nullOrBlank(scm.getDeveloperConnection())) {
                        collector.addError("MISSING project/scm (url, connection, developerConnection)");
                    }
                }
            } else {
                collector.addWarning("Could not get effective model");
            }
        }
    }

    private boolean nullOrBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}
