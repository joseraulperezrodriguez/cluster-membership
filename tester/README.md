# Cluster Membership Protocol Tester
This is the tester program for the implemented membership protocol

## Cases file
The case file is a json object that contains the configuration for the cluster to test and the instructions to launch a test process  

### Configuration
It should contains the same properties in [the app config file](../common/src/main/resources/config/app.properties), if some properties are not used, are replaced by the default value, the one included in the config file.

### Process
<p>The process object is an orderer array containing objects for the instructions to run the jobs, the possible objects are:</p>

```bash
{
	//an instruction to create a node, with the properties specified bellow
	node: {  
		id: //the node id  
		address: //the network address to reach the node  
	}
	
	//an instruction to wait some second
	wait: 3
		
	//an instruction to pause a server  
	pause: {
		node.id: //the id of a node we want to pause
		time: //the time in milliseconds we want to pause the node		 	
	}
 	
 	// un instruction to unsubscribe a node
	unsubscribe: // the id of the node to unsubscribe
 	
	shutdown: 	//the id of the node to shutdown	 	
	
}
```

## Launch the tester program 

The tester receives a single argument: a path to a protocol .jar file

Then, the tester code creates a folder structure with test cases and configuration files:

```bash
/member-protocol-tester-0.0.1/
.
├── instances
│	├── template
│	│   └── membership-protocol-0.0.1.jar
│	│   └── app.properties
│	├── A
│	│   └── membership-protocol-0.0.1.jar
│	│   └── app.properties
│	│   └── A.log
│	│   
│	├── B
│	│   └── membership-protocol-0.0.1.jar
│	│   └── app.properties
│	│   └── B.log
├── cases
│   └── case-1.json
│   └── case-2.json
│   └── .
│   └── .
│   └── .
│   └── case-n.json

```
