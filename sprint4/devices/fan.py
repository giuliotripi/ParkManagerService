import sys
import RPi.GPIO as GPIO

#from RPi import GPIO #solo debug

if len(sys.argv) != 2 or (sys.argv[1] != "on" and sys.argv[1] != "off"):
	print("Usage: fan.py on|off")
	sys.exit(1)

GPIOnumber = 16

GPIO.setmode(GPIO.BCM)

newState = 1 if sys.argv[1] == "on" else 0

GPIO.setup(GPIOnumber, GPIO.OUT)

GPIO.output(GPIOnumber, newState)
