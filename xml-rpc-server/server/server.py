from SimpleXMLRPCServer import SimpleXMLRPCServer
from SimpleXMLRPCServer import SimpleXMLRPCRequestHandler
import subprocess
import time
import socket
import constants
import linux_metrics as lm
import threading
import xmlrpclib

# Restrict to a particular path.
class RequestHandler(SimpleXMLRPCRequestHandler):
    rpc_paths = ('/RPC2',)

# Create server
server = SimpleXMLRPCServer(([(s.connect(('8.8.8.8', 80)), s.getsockname()[0], s.close()) for s in [socket.socket(socket.AF_INET, socket.SOCK_DGRAM)]][0][1], constants.PORT_NR_INT),
                            requestHandler=RequestHandler)
server.register_introspection_functions()

#Polling time
pollingTime = 300

# Register an instance; all the methods of the instance are
# published as XML-RPC methods (in this case, just 'div').
class MyFuncs:
    lastTime = int(time.time())

    #Method returning cpu usage. This method uses
    #the library from linux_metrics to retrieve cpu data.
    def cpu(self):    
	lastTime = int(time.time())
	return 100 - lm.cpu_stat.cpu_percents(sample_duration=0.1)['idle']

    #Method returning memory usage. This method uses
    #standard commandline methods for retrieving memory usage.
    def mem(self):
	lastTime = int(time.time())
	return subprocess.check_output(
		"free | grep Mem | awk '{print $3/$2 * 100.0}'",	#Commandline method for retrieving memory usage
		shell=True)[:-1]

    #Method returning hard disk usage. This method uses
    #standard commandline methods for retrieving hard disk usage.
    def hdd(self):
	lastTime = int(time.time())
	return subprocess.check_output(
		"df -lh | awk '{if ($6 == \"/\") { print $5 }}' | head -1 | cut -d'%' -f1", #Commandline method for retrieving HDD usage
		shell=True)[:-1]

    #Method returning local machine time. This method uses
    #the time library for retrieving system time.
    def time(self):
	lastTime = int(time.time())
	return time.time()

    #Method returning the output of dnsjedi-wstats for workers or
    #dnsjedi-cmstats for managers. It checks the component type and
    #based on that returns the output of the corresponding script
    #which needs to be invoked.
    def getData(self):
	lastTime = int(time.time())
	if constants.COMPONENT_TYPE == constants.COMP_TYPE_WORKER:
		childProcess = subprocess.Popen("sudo /usr/local/bin/dnsjedi-wstats", stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE, shell=True)
		childProcess.wait()
		if childProcess.returncode == 0 :
			output, errors = childProcess.communicate()
			return output
		else:
			return ""
	elif constants.COMPONENT_TYPE == constants.COMP_TYPE_MANAGER:
		childProcess = subprocess.Popen("sudo /usr/local/bin/dnsjedi-cmstats", shell=True)
		childProcess.wait()
		if childProcess.returncode == 0 :
			output, errors = childProcess.communicate()
			return output
		else:
			return ""
	else:
		return ""
    def setPollingTime(self, x):    #Set Polling Time
	lastTime = int(time.time())
	pollingTime = 5*x
	return True
	
#Initialise all available functions.
functionClass = MyFuncs()

#Register to central xml-rpc server which polls this server.
server.register_instance(functionClass)

#Set last polling time.
functionClass.lastTime = int(time.time())

#Ensure the central xml-rpc server can call each method as long as this xml-rpc server lives.
server.serve_forever()
