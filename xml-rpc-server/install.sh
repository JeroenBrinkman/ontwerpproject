#!/bin/bash


#########################################
#		  HELP			#
#########################################

#Help option, explains what install.sh does and how it should be used.
if [ "$1" == "-h" ]
then
  echo

  echo "This is the help page of install.sh"
  echo "The script installs the XML-RPS server server.py and its necessary files."
  echo

  echo "The script needs two arguments to run:"
  echo "  1. ssh login credentials to remote host in the form of \"username@remotehost\""
  echo "  2. destination path on server in the form of \"path/to/destination\""
  echo

  echo "Then, it will locate all files needed to install."

  echo
else

#########################################
#	     Installation		#
#########################################

  #Login credentials for server.
  destinationHost="$1"
  #Path on server in which the files must be installed.
  destinationPath="$2"
  #Path of map "server"
  pathServer="server"
  #Path of script "start.sh"
  pathStart="start.sh"
  #Path of script "stop.sh"
  pathStop="stop.sh"
  #Path of script "nohupRemover.sh"
  pathRemover="nohupRemover.sh"

  #Copy nohupRemover.sh to server.
  scp $pathRemover $destinationHost:$destinationPath
  #Copy start.sh to server.
  scp $pathStart $destinationHost:$destinationPath
  #Copy stop.sh to server.
  scp $pathStop $destinationHost:$destinationPath
  #Copy directory recursively to server.
  scp -r $pathServer $destinationHost:$destinationPath
fi
