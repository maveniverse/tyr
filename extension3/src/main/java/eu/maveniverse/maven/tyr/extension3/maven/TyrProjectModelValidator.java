/*
 * Copyright (c) 2023-2024 Maveniverse Org.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */
package eu.maveniverse.maven.tyr.extension3.maven;

import com.google.inject.AbstractModule;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.building.ModelProblemCollector;
import org.apache.maven.model.interpolation.ModelVersionProcessor;
import org.apache.maven.model.validation.DefaultModelValidator;
import org.apache.maven.model.validation.ModelValidator;

public class TyrProjectModelValidator extends DefaultModelValidator implements ModelValidator {

    @Named
    public static class Module extends AbstractModule {
        @Override
        protected void configure() {
            bind(ModelValidator.class).to(TyrProjectModelValidator.class);
            bind(DefaultModelValidator.class).to(TyrProjectModelValidator.class);
        }
    }

    @Inject
    public TyrProjectModelValidator(ModelVersionProcessor versionProcessor) {
        super(versionProcessor);
    }

    @Override
    protected void validateDependencyVersion(ModelProblemCollector problems, Dependency d, String prefix) {
        // allow empty versions; they will be "caught" later by Tyr anyway
    }
}
