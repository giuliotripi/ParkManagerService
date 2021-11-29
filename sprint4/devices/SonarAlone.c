#include <iostream>
#include <wiringPi.h>
#include <fstream>
#include <cmath>

#define TRIG 0
#define ECHO 2

using namespace std;
/*
g++  SonarAlone.c -l wiringPi -o  SonarAlone
 */
void setup() {
	wiringPiSetup();
	pinMode(TRIG, OUTPUT);
	pinMode(ECHO, INPUT);
	//TRIG pin must start LOW
	digitalWrite(TRIG, LOW);
	delay(30);
}

int getCM() {
	//Send trig pulse
	digitalWrite(TRIG, HIGH);
	delayMicroseconds(20);
	digitalWrite(TRIG, LOW);

	//Wait for echo start
	while(digitalRead(ECHO) == LOW);

	//Wait for echo end
	long startTime = micros();
	while(digitalRead(ECHO) == HIGH);
	long travelTime = micros() - startTime;

	//Get distance in cm
	int distance = travelTime / 58;

	return distance;
}

int main(void) {
	int cm ;
	setup();
	//while(1) {
 		cm = getCM();
		cout <<  cm   << endl;
		delay(30);
	//}
 	return 0;
}