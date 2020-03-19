package org.dogoodthings.ectr.osgi;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.dogoodthings.ectr.osgi.functions.FindAllDocVersionsPluginFunction;

import com.dscsag.plm.spi.interfaces.gui.PluginFunction;
import com.dscsag.plm.spi.interfaces.gui.PluginFunctionService;

/**
 * function manager which maps the function code to "real" plugin function
 *
 */
class PluginFunctionManager implements PluginFunctionService
{
  private final Map<String, Supplier<PluginFunction>> map;

  protected PluginFunctionManager()
  {
    map = new HashMap<>();
    map.put("fnc.org.dogoodthings.find.all.doc.versions", () -> new FindAllDocVersionsPluginFunction());
  }

  @Override
  public PluginFunction getPluginFunction(String functionName)
  {
    Supplier<PluginFunction> supplier = map.get(functionName);
    if (supplier != null)
      return supplier.get();
    return null;
  }
}