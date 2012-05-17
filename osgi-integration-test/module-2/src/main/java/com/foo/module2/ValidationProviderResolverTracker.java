/*
* JBoss, Home of Professional Open Source
* Copyright 2012, Red Hat, Inc. and/or its affiliates, and individual contributors
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
package com.foo.module2;

import java.util.Arrays;
import javax.validation.Validation;
import javax.validation.ValidationProviderResolver;
import javax.validation.ValidatorFactory;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.AggregateResourceBundleLocator;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;

/**
 * @author Gunnar Morling
 */
public class ValidationProviderResolverTracker extends ServiceTracker {

	private ServiceRegistration factoryRegistration;

	public ValidationProviderResolverTracker(BundleContext bundleContext) {
		super( bundleContext, ValidationProviderResolver.class.getName(), null );
	}

	@Override
	public synchronized Object addingService(ServiceReference reference) {

		ValidationProviderResolver providerResolver = (ValidationProviderResolver) context.getService( reference );

		ValidatorFactory validatorFactory = Validation
				.byProvider( HibernateValidator.class )
				.providerResolver( providerResolver )
				.configure().userClassLoader( ValidationProviderResolverTracker.class.getClassLoader() )
				.messageInterpolator(
						new ResourceBundleMessageInterpolator(
								(ResourceBundleLocator) new AggregateResourceBundleLocator(
										Arrays.asList(
												"ValidationMessages",
												"Module1ValidationMessages"
										)
								)
						)
				)
				.buildValidatorFactory();

		factoryRegistration = context.registerService(
				ValidatorFactory.class.getName(), validatorFactory, null
		);

		return providerResolver;
	}

	@Override
	public synchronized void removedService(ServiceReference reference, Object service) {

		factoryRegistration.unregister();
		factoryRegistration = null;

		super.removedService( reference, service );
	}
}
