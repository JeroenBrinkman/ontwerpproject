#!/bin/bash

#Forever clear nohup every hour until this program is killed.
while [ 1 -eq 1 ]
do
	> nohup.out	#Clear nohup
	sleep 3600	#Clear nohup every hour
done
