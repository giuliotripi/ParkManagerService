%====================================================================================
% parkingsystem description   
%====================================================================================
context(ctxservice, "localhost",  "TCP", "8050").
 qactor( parkingservice, ctxservice, "it.unibo.parkingservice.Parkingservice").
  qactor( sensorservice, ctxservice, "it.unibo.sensorservice.Sensorservice").
  qactor( client, ctxservice, "it.unibo.client.Client").
  qactor( manager, ctxservice, "it.unibo.manager.Manager").
  qactor( trolley, ctxservice, "it.unibo.trolley.Trolley").
  qactor( dtfree, ctxservice, "it.unibo.dtfree.Dtfree").
  qactor( thermometer, ctxservice, "it.unibo.thermometer.Thermometer").
  qactor( thermometerevent, ctxservice, "it.unibo.thermometerevent.Thermometerevent").
  qactor( sonar, ctxservice, "it.unibo.sonar.Sonar").
  qactor( fan, ctxservice, "it.unibo.fan.Fan").
  qactor( weight, ctxservice, "it.unibo.weight.Weight").
msglogging.
