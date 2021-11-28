%====================================================================================
% parkingsystem description   
%====================================================================================
context(ctxservice, "localhost",  "TCP", "8050").
context(ctxbasicrobot, "127.0.0.1",  "TCP", "8020").
 qactor( basicrobot, ctxbasicrobot, "external").
  qactor( parkingservice, ctxservice, "it.unibo.parkingservice.Parkingservice").
  qactor( sensorservice, ctxservice, "it.unibo.sensorservice.Sensorservice").
  qactor( managerservice, ctxservice, "it.unibo.managerservice.Managerservice").
  qactor( trolley, ctxservice, "it.unibo.trolley.Trolley").
  qactor( dtfree, ctxservice, "it.unibo.dtfree.Dtfree").
  qactor( thermometer, ctxservice, "it.unibo.thermometer.Thermometer").
  qactor( sonar, ctxservice, "it.unibo.sonar.Sonar").
  qactor( fan, ctxservice, "it.unibo.fan.Fan").
  qactor( weight, ctxservice, "it.unibo.weight.Weight").
msglogging.
