package org.cluster.membership;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.TimeZone;
import java.util.UUID;

import org.cluster.membership.model.Node;
import org.cluster.membership.structures.DList;
import org.springframework.boot.ApplicationArguments;

public class Config {
	
	private static HashMap<String, String> map = Parsing.readAppConfig();
			
	/**The time interval for making requests to other nodes in the cluster*/
	public static final long ITERATION_INTERVAL_MS = Long.parseLong(map.get("iteration-interval-ms"));
		
	/**The time out for connection to other nodes*/
	public static final long CONNECTION_TIME_OUT_MS = Long.parseLong(map.get("connection-timeout-ms"));
		
	/**The expiration time for keeping a node alive after spread the message that is dead*/
	public static final long FAILING_NODE_EXPIRATION_TIME_MS = Long.parseLong(map.get("failing-node-expiration-time-ms"));//one day
				
	/**The max  number of iterations to select a random node*/
	public static final int MAX_EXPECTED_NODE_LOG_2_SIZE = Integer.parseInt(map.get("max-expected-node-log-2"));
	
	
	/**The max length of the set for storing rumors messages, used for recovery other nodes later*/
	public static final int MAX_RUMORS_LOG_SIZE = Integer.parseInt(map.get("max-rumor-log-size"));
	
	/**The max number of bytes allowed to transfer between client and server*/
	public static final int MAX_OBJECT_SIZE = Integer.parseInt(map.get("max-object-size"));
	
	public static final Node THIS_PEER = Parsing.readThisPeer();
	
	public static final DList SEEDS = new DList();
	
	public static boolean isValid() {
		return (ITERATION_INTERVAL_MS > 500 && ITERATION_INTERVAL_MS < 1000*60) &&
				(CONNECTION_TIME_OUT_MS > 100 && CONNECTION_TIME_OUT_MS < 1000*60) &&
				(FAILING_NODE_EXPIRATION_TIME_MS > 1000*60*60 && FAILING_NODE_EXPIRATION_TIME_MS < 1000*60*60*24*3) &&
				(MAX_RUMORS_LOG_SIZE < 10*1000*1000) && THIS_PEER != null;
	}
	
	public static void read(ApplicationArguments args) throws Exception {		
		Parsing.setSeedNodes(args);
	}
	
	private static class Parsing {
		
		private static final String id = "id";
		private static final String address = "address";
		private static final String port = "port";
		private static final String timeZone = "time-zone";		

		private static Properties prop(String file) throws Exception {
			File path = new File(ClusterNodeEntry.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			String parent = path.getParent();
			String peerConf = parent + File.separator + file;
			
			Properties p = new Properties();
			p.load(new FileInputStream(peerConf));
			
			return p;
		}
		
		private static Node readThisPeer() {
			try {
				Properties p = prop("config" + File.separator  + "peer.properties");
				
				String cId = UUID.randomUUID().toString();
				String cAddress = p.getProperty(address).trim();
				Integer cPort = Integer.parseInt(p.getProperty(port).trim());
				String cTimeZone = p.getProperty(timeZone).trim();
				
				return new Node(cId, cAddress, cPort, TimeZone.getTimeZone(cTimeZone));

			} catch(Exception e) {
				e.printStackTrace();
				return null;
			}			

		}
		
		private static HashMap<String, String> readAppConfig() {
			try {
				Properties p = prop("config" + File.separator  + "app.properties");
				
				HashMap<String, String> map = new HashMap<String, String>();
				
				for(Object key : p.keySet()) map.put(key.toString().trim(), p.getProperty(key.toString()).trim());
				
				return map;
			} catch(Exception e) {
				e.printStackTrace();
				return null;
			}
			
		}
		
		private static int containsOptions(ApplicationArguments args, String... options) {		
			int count = 0;
			
			for(String o : options) if(args.containsOption(o)) count++;
			
			if(count == options.length) return 1;
			return (count == 0 ? 0 : -1);
			
		}
		
		private static void setSeedNodes(ApplicationArguments args) throws Exception {
			int count = 1;
			do {
				int contains = containsOptions(args, id + "." + count, 
						address + "." + count,
						port + "." + count,
						timeZone + "." + count
						);
				
				if(contains == 0) break;
				if(contains == -1) throw new Exception("Error reading config for node " + count + 
						" some attributes are missing or bad configured");
				
				try {				
					
					String cId = args.getOptionValues(id + "." + count).get(0);
					String cAddress = args.getOptionValues(address + "." + count).get(0);
					int cPort = Integer.parseInt(args.getOptionValues(port + "." + count).get(0));
					String cTimeZone = args.getOptionValues(timeZone + "." + count).get(0);
					
					Node n = new Node(cId, cAddress, cPort, TimeZone.getTimeZone(cTimeZone));
					Config.SEEDS.add(n);

				} catch(Exception e) {
					throw new Exception("The node " + count + " is not configured properly.");
				}
				count++;
			} while(true);
			
			
			
		}
		
		
	}
	
}
