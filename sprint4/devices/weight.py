import sys
import RPi.GPIO as GPIO

#from RPi import GPIO #solo debug


GPIOnumber = 11

GPIO.setup(GPIOnumber, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)

print(GPIO.input(GPIOnumber))
