package org.dogoodthings.ectr.osgi;

import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public class ServiceLocator
{
  public static  <T> T getService(Class<T> clazz)
  {
    BundleContext context = FrameworkUtil.getBundle(ServiceLocator.class).getBundleContext();
    ServiceReference<T> serviceRef = context.getServiceReference(clazz);
    if (serviceRef != null)
      return context.getService(serviceRef);
    throw new RuntimeException("Unable to find implementation for service " + clazz.getName());
  }
}