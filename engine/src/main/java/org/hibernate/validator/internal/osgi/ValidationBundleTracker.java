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
import javax.validation.MessageInterpolator;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.util.tracker.BundleTracker;

import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.AggregateResourceBundleLocator;
import org.hibernate.validator.spi.osgi.UserClassLoaderProvider;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;

/**
 * @author Gunnar Morling
 */
public class ValidationBundleTracker extends BundleTracker {

	private final static String VALIDATION_HEADER_NAME = "Hibernate-Validator-Resource-Bundle";

	private final static String ACTIVATOR_HEADER_NAME = "Bundle-Activator";

	public ValidationBundleTracker(BundleContext context) {

		super( context, Bundle.ACTIVE, null );
	}

	@Override
	public Object addingBundle(Bundle bundle, BundleEvent event) {

		String validationHeader = (String) bundle.getHeaders().get( VALIDATION_HEADER_NAME );
		String activatorHeader = (String) bundle.getHeaders().get( ACTIVATOR_HEADER_NAME );

		if ( validationHeader != null && activatorHeader != null ) {

			try {
				final ClassLoader bundleClassLoader = bundle.loadClass( activatorHeader ).getClassLoader();

				bundle.getBundleContext()
						.registerService(
								UserClassLoaderProvider.class.getName(),
								new UserClassLoaderProvider() {

									@Override
									public ClassLoader getUserClassLoader() {
										return bundleClassLoader;
									}
								},
								null
						);

				String[] bundleNames = validationHeader.split( "," );

				bundle.getBundleContext()
						.registerService(
								MessageInterpolator.class.getName(),
								new ResourceBundleMessageInterpolator(
										(ResourceBundleLocator) new AggregateResourceBundleLocator(
												Arrays.asList( bundleNames )
										)
								),
								null
						);


			}
			catch ( Exception e ) {
				throw new RuntimeException( e );
			}
		}
		return bundle;
	}
}
