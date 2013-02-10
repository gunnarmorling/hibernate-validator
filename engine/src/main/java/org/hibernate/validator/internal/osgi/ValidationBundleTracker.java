/*
* JBoss, Home of Professional Open Source
* Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual contributors
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.hibernate.validator.internal.osgi;

import java.util.Arrays;
import java.util.List;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.BundleTracker;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.osgi.HibernateValidationProviderResolver;
import org.hibernate.validator.resourceloading.AggregateResourceBundleLocator;

/**
 * @author Gunnar Morling
 */
public class ValidationBundleTracker extends BundleTracker {

	private static final String VALIDATION_HEADER_NAME = "Hibernate-Validator-Resource-Bundle";

	private static final String ACTIVATOR_HEADER_NAME = "Bundle-Activator";

	private ServiceRegistration factoryRegistration;

	public ValidationBundleTracker(BundleContext context) {
		super( context, Bundle.ACTIVE, null );
	}

	@Override
	public Object addingBundle(Bundle bundle, BundleEvent event) {
		String validationHeader = (String) bundle.getHeaders().get( VALIDATION_HEADER_NAME );
		String activatorHeader = (String) bundle.getHeaders().get( ACTIVATOR_HEADER_NAME );

		if ( validationHeader != null && activatorHeader != null ) {
			try {
				registerFactory(
						bundle.getBundleContext(),
						bundle.loadClass( activatorHeader ).getClassLoader(),
						Arrays.asList( validationHeader.split( "," ) )
				);
			}
			catch ( Exception e ) {
				throw new RuntimeException( e );
			}
		}

		return bundle;
	}

	@Override
	public void removedBundle(Bundle bundle, BundleEvent event, Object object) {
		unregisterFactory();
	}

	private synchronized void registerFactory(BundleContext context, ClassLoader classLoader, List<String> bundleNames) {
		ValidatorFactory validatorFactory = Validation
				.byProvider( HibernateValidator.class )
				.providerResolver( new HibernateValidationProviderResolver() )
				.configure()
				.messageInterpolator(
						new ResourceBundleMessageInterpolator(
								new AggregateResourceBundleLocator( bundleNames )
						)
				)
				.userClassLoader( classLoader )
				.buildValidatorFactory();

		factoryRegistration = context.registerService(
				ValidatorFactory.class.getName(), validatorFactory, null
		);
	}

	private synchronized void unregisterFactory() {
		if ( factoryRegistration != null ) {
			factoryRegistration.unregister();
			factoryRegistration = null;
		}
	}
}
