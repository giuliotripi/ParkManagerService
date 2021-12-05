from w1thermsensor import W1ThermSensor
import w1thermsensor
sensor = W1ThermSensor()

try:
	temp = sensor.get_temperature()
	print(temp)
except w1thermsensor.errors.SensorNotReadyError:
	print(21.2121)
except w1thermsensor.errors.ResetValueError:
	print(21.2122)
except w1thermsensor.errors.NoSensorFoundError:
	print(21.2123)
