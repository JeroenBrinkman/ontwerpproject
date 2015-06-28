#!/bin/bash

#Start the server
nohup python server/xmlrpc-init.py &
#Start the nohup cleaner
./nohupRemover.sh &
