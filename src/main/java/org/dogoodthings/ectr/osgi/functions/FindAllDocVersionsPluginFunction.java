package org.dogoodthings.ectr.osgi.functions;

import java.util.ArrayList;
import java.util.List;

import org.dogoodthings.ectr.osgi.ServiceLocator;

import com.dscsag.plm.spi.interfaces.ECTRService;
import com.dscsag.plm.spi.interfaces.gui.PluginFunction;
import com.dscsag.plm.spi.interfaces.gui.PluginRequest;
import com.dscsag.plm.spi.interfaces.gui.PluginResponse;
import com.dscsag.plm.spi.interfaces.gui.PluginResponseFactory;
import com.dscsag.plm.spi.interfaces.gui.UIService;
import com.dscsag.plm.spi.interfaces.gui.objectlist.ObjectList;
import com.dscsag.plm.spi.interfaces.objects.PlmObjectKey;
import com.dscsag.plm.spi.interfaces.rfc.RfcCall;
import com.dscsag.plm.spi.interfaces.rfc.RfcExecutor;
import com.dscsag.plm.spi.interfaces.rfc.RfcResult;
import com.dscsag.plm.spi.interfaces.rfc.RfcStructure;
import com.dscsag.plm.spi.interfaces.rfc.RfcTable;
import com.dscsag.plm.spi.rfc.builder.RfcCallBuilder;
import com.dscsag.plm.spi.rfc.builder.RfcStructureBuilder;

/**
 * function which reads all versions from sap and logs them in debug log
 */
public class FindAllDocVersionsPluginFunction implements PluginFunction
{
  private ECTRService ectrService;
  private UIService uiService;

  public FindAllDocVersionsPluginFunction()
  {
    this.ectrService = ServiceLocator.getService(ECTRService.class);
    this.uiService = ServiceLocator.getService(UIService.class);
  }

  @Override
  public PluginResponse actionPerformed(PluginRequest request)
  {
    PluginResponse pr = null;

    if(!request.getObjects().isEmpty())
    {
      PlmObjectKey object = request.getObjects().get(0);// get first document
      if ("DRAW".equals(object.getType()))
      {
        final List<PlmObjectKey> allVersions = getAllVersions(object.getKey());
        final ObjectList allVersionsOlist = uiService.getObjectListService().getOrCreate("all versions");
        allVersionsOlist.set(allVersions);
        pr = PluginResponseFactory.infoResponse("all versions are in object list now");
      }
      else
        pr = PluginResponseFactory.warningResponse("only documents supported - please select one document");
    }
    else
      pr = PluginResponseFactory.warningResponse("please select one document");
    return pr;
  }

  private List<PlmObjectKey> getAllVersions(String key)
  {
    String type = key.substring(0, 3);
    String number = key.substring(3, 28);
    String version = key.substring(28, 30);
    String part = key.substring(30);
    
    RfcCallBuilder rfcCallBuilder = new RfcCallBuilder("/DSCSAG/DOC_VERSION_GET_ALL3");
    RfcStructureBuilder rfcStructureBuilder = new RfcStructureBuilder("DOCUMENTTYPE", "DOCUMENTNUMBER", "DOCUMENTVERSION", "DOCUMENTPART");
    rfcStructureBuilder.setValue("DOCUMENTTYPE", type);
    rfcStructureBuilder.setValue("DOCUMENTNUMBER", number);
    rfcStructureBuilder.setValue("DOCUMENTVERSION", version);
    rfcStructureBuilder.setValue("DOCUMENTPART", part);

    rfcCallBuilder.setInputStructure("DOCUMENTKEY", rfcStructureBuilder.toRfcStructure());

    RfcCall rfcCall = rfcCallBuilder.toRfcCall();

    RfcExecutor rfcExecutor = ectrService.getRfcExecutor();
    RfcResult result = rfcExecutor.execute(rfcCall);

    RfcTable table = result.getTable("OUT_DOCUMENT");

    List<PlmObjectKey> allVersions = new ArrayList<>();
    for (int i = 0; i < table.getRowCount(); i++)
    {
      RfcStructure line = table.getRow(i);
      String documentType = line.getFieldValue("DOKAR");
      String documentNumber = line.getFieldValue("DOKNR");
      String documentVersion = line.getFieldValue("DOKVR");
      String documentPart = line.getFieldValue("DOKTL");
      String sapKey=String.format("%-3s%-25s%-2s%-3s", documentType,documentNumber,documentVersion,documentPart);
      allVersions.add(new PlmObjectKey("DRAW",sapKey));
      ectrService.getPlmLogger().debug("DOC: '"+sapKey+"'");
    }
    return allVersions;
  }
}