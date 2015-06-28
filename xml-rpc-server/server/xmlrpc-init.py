#!
from threading import Thread
import constants
import xmlrpclib
import socket
import time
import os

#Connect to server using server ip and port.
server_ip = 'http://' + constants.MONITOR_SYSTEM_IP + ':' + constants.PORT_NR
s = xmlrpclib.ServerProxy(server_ip)

#Check whether the server is online
while (not s.Controller.online()):
	print 'Server offline'
	time.sleep(constants.SLEEP_TIME)

#Tell server to add component with this IP.
while (not s.Controller.add(constants.COMPONENT_TYPE, [(s.connect(('8.8.8.8', 80)), s.getsockname()[0], s.close()) for s in [socket.socket(socket.AF_INET, socket.SOCK_DGRAM)]][0][1], constants.PORT_NR_INT)):
	print 'Not added'
	time.sleep(constants.SLEEP_TIME)

#Initialise server
import server.py
