package org.dogoodthings.ectr.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.dscsag.plm.spi.interfaces.gui.PluginFunctionService;

/**
 * Activator to register provided services
 */
public class Activator implements BundleActivator
{
  @Override
  public void start(BundleContext context) throws Exception
  {
    context.registerService(PluginFunctionService.class, new PluginFunctionManager(), null);
  }

  @Override
  public void stop(BundleContext context) throws Exception
  {
    // empty
  }
}