#!/usr/bin/env python

import sys
import serial

if __name__ == '__main__':
	ser = serial.Serial('/dev/ttyACM0', '9600');
	ser.open()
	
	try:
		while 1:
			resp = ser.readline()
			sys.stdout.write(resp)
	except KeyboardInterrupt:
		ser.close();
