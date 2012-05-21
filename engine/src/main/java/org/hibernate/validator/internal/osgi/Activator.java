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
package org.hibernate.validator.internal.osgi;

import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.validation.ValidationProviderResolver;
import javax.validation.ValidatorFactory;
import javax.validation.spi.ValidationProvider;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.spi.osgi.UserClassLoaderProvider;


/**
 * Registers an instance of {@link HibernateValidator} as OSGi service.
 *
 * @author Gunnar Morling
 */
public class Activator implements BundleActivator {

	private ValidationBundleTracker bundleTracker;

	private ServiceTracker messageInterpolatorTracker;
	private ServiceTracker validationProviderResolverTracker;
	private ServiceTracker userClassLoaderProviderTracker;

	private ServiceRegistration factoryRegistration;

	public void start(BundleContext context) throws Exception {

		messageInterpolatorTracker = new ServiceTracker(
				context,
				MessageInterpolator.class.getName(),
				new MessageInterpolatorTrackerCustomizer( context )
		);
		messageInterpolatorTracker.open();

		validationProviderResolverTracker = new ServiceTracker(
				context,
				ValidationProviderResolver.class.getName(),
				new ValidationProviderResolverTrackerCustomizer( context )
		);
		validationProviderResolverTracker.open();

		userClassLoaderProviderTracker = new ServiceTracker(
				context,
				UserClassLoaderProvider.class.getName(),
				new UserClassLoaderProviderTrackerCustomizer( context )
		);
		userClassLoaderProviderTracker.open();

		bundleTracker = new ValidationBundleTracker( context );
		bundleTracker.open();

		context.registerService(
				ValidationProvider.class.getName(),
				new HibernateValidator(),
				null
		);
	}

	private class MessageInterpolatorTrackerCustomizer implements ServiceTrackerCustomizer {

		private final BundleContext context;

		public MessageInterpolatorTrackerCustomizer(BundleContext context) {
			this.context = context;
		}

		@Override
		public Object addingService(ServiceReference reference) {

			MessageInterpolator messageIntepolator = (MessageInterpolator) context.getService( reference );

			ValidationProviderResolver validationProviderResolver = (ValidationProviderResolver) validationProviderResolverTracker
					.getService();
			UserClassLoaderProvider userClassLoaderProvider = (UserClassLoaderProvider) userClassLoaderProviderTracker.getService();

			if ( validationProviderResolver != null && userClassLoaderProvider != null ) {
				registerFactory( context, validationProviderResolver, userClassLoaderProvider, messageIntepolator );
			}

			return validationProviderResolver;
		}

		@Override
		public void modifiedService(ServiceReference reference, Object service) {
			// TODO Auto-generated method stub
		}

		@Override
		public void removedService(ServiceReference reference, Object service) {
			unregisterFactory();
		}
	}

	private class ValidationProviderResolverTrackerCustomizer implements ServiceTrackerCustomizer {

		private final BundleContext context;

		public ValidationProviderResolverTrackerCustomizer(BundleContext context) {
			this.context = context;
		}

		@Override
		public Object addingService(ServiceReference reference) {

			ValidationProviderResolver validationProviderResolver = (ValidationProviderResolver) context.getService(
					reference
			);

			MessageInterpolator messageInterpolator = (MessageInterpolator) messageInterpolatorTracker.getService();
			UserClassLoaderProvider userClassLoaderProvider = (UserClassLoaderProvider) userClassLoaderProviderTracker.getService();

			if ( messageInterpolator != null && userClassLoaderProvider != null ) {
				registerFactory( context, validationProviderResolver, userClassLoaderProvider, messageInterpolator );
			}

			return validationProviderResolver;
		}

		@Override
		public void modifiedService(ServiceReference reference, Object service) {
			// TODO Auto-generated method stub
		}

		@Override
		public void removedService(ServiceReference reference, Object service) {
			unregisterFactory();
		}
	}

	private class UserClassLoaderProviderTrackerCustomizer implements ServiceTrackerCustomizer {

		private final BundleContext context;

		public UserClassLoaderProviderTrackerCustomizer(BundleContext context) {
			this.context = context;
		}

		@Override
		public Object addingService(ServiceReference reference) {

			UserClassLoaderProvider userClassLoaderProvider = (UserClassLoaderProvider) context.getService( reference );

			MessageInterpolator messageInterpolator = (MessageInterpolator) messageInterpolatorTracker.getService();
			ValidationProviderResolver validationProviderResolver = (ValidationProviderResolver) validationProviderResolverTracker
					.getService();
			if ( messageInterpolator != null && validationProviderResolver != null ) {
				registerFactory( context, validationProviderResolver, userClassLoaderProvider, messageInterpolator );
			}

			return userClassLoaderProvider;
		}

		@Override
		public void modifiedService(ServiceReference reference, Object service) {
			// TODO Auto-generated method stub
		}

		@Override
		public void removedService(ServiceReference reference, Object service) {
			unregisterFactory();
		}
	}

	public void stop(BundleContext context) throws Exception {
		bundleTracker.close();
	}

	private synchronized void registerFactory(BundleContext context, ValidationProviderResolver validationProviderResolver, UserClassLoaderProvider userClassLoaderProvider, MessageInterpolator messageIntepolator) {

		ValidatorFactory validatorFactory = Validation
				.byProvider( HibernateValidator.class )
				.providerResolver( validationProviderResolver )
				.configure().userClassLoader( userClassLoaderProvider.getUserClassLoader() )
				.messageInterpolator( messageIntepolator )
				.buildValidatorFactory();

		factoryRegistration = context.registerService(
				ValidatorFactory.class.getName(), validatorFactory, null
		);
	}

	private synchronized void unregisterFactory() {
		factoryRegistration.unregister();
		factoryRegistration = null;
	}
}
