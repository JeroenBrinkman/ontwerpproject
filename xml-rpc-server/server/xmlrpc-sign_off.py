import constants
import xmlrpclib
import socket
import subprocess

#Connect to server using server ip and port.
server_ip = 'http://' + constants.MONITOR_SYSTEM_IP + ':' + constants.PORT_NR
s = xmlrpclib.ServerProxy(server_ip)

#Sign off from monitoring system.
s.Controller.remove([(s.connect(('8.8.8.8', 80)), s.getsockname()[0], s.close()) for s in [socket.socket(socket.AF_INET, socket.SOCK_DGRAM)]][0][1], constants.PORT_NR_INT)

#Kill own xml-rpc server
subprocess.call("pkill -9 -f xmlrpc-init.py",	shell=True)
