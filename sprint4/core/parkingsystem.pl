%====================================================================================
% parkingsystem description   
%====================================================================================
context(ctxservice, "localhost",  "TCP", "8050").
context(ctxsensor, "sensor.local",  "TCP", "8054").
context(ctxbasicrobot, "127.0.0.1",  "TCP", "8020").
 qactor( basicrobot, ctxbasicrobot, "external").
  qactor( thermometer, ctxsensor, "external").
  qactor( sonar, ctxsensor, "external").
  qactor( fan, ctxsensor, "external").
  qactor( weight, ctxsensor, "external").
  qactor( parkingservice, ctxservice, "it.unibo.parkingservice.Parkingservice").
  qactor( sensorservice, ctxservice, "it.unibo.sensorservice.Sensorservice").
  qactor( managerservice, ctxservice, "it.unibo.managerservice.Managerservice").
  qactor( trolley, ctxservice, "it.unibo.trolley.Trolley").
  qactor( dtfree, ctxservice, "it.unibo.dtfree.Dtfree").
msglogging.
