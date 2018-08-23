#Cluster Membership Protocol Tester
This is the tester program for the implemented membership protocol

###Cases file
The case file is a json object that contains the configuration for the cluster to test and the instructions to launch a test process  

###Configuration
It should contains the same properties as the peer.properties file for skip some one for use the default value

###Process
The process object is an orderer array containing objects for the instructions to run the jobs, the possible objects are:

- node  
	- id: the node id  
	- address: the network address to reach the node  
	- node-port: the protocol port for this node 
	- service-port: the service port for this node 
	- time-zone: the time zone for this node
	
- wait
	- a long value in milliseconds
	
- check
	- nodes: an ordered list of ids of the nodes we expect to be in the cluster at this moment
	- dead-nodes: a list of ids of the nodes we expect to be in "dead" state at this moment, wait for the expiration time to be deleted
	- failing-nodes: a list of ids of the nodes we expect to be in "failing mode at this moment"
	
- pause
	- node-id: the id of a node we want to pause
	- time: the time in milliseconds we want to pause the node
	
- unsubscribe
	- the node we want to unsubscribe
 

 
 