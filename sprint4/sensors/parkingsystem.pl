%====================================================================================
% parkingsystem description
%====================================================================================
context(ctxsensor, "127.0.0.1",  "TCP", "8054").
context(ctxservice, "service.local",  "TCP", "8050").
 qactor( thermometer, ctxsensor, "it.unibo.thermometer.Thermometer").
  qactor( sonar, ctxsensor, "it.unibo.sonar.Sonar").
  qactor( fan, ctxsensor, "it.unibo.fan.Fan").
  qactor( weight, ctxsensor, "it.unibo.weight.Weight").
msglogging.
