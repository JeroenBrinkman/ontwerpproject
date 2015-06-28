#!/bin/bash

#Kill the heartbeat
pkill -9 -f server/xmlrpc-heartbeat.py
#Kill the server
python server/xmlrpc-sign_off.py
#Kill the remover
pkill -9 -f nohupRemover.sh
