package org.cluster.membership.protocol;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.UUID;
import java.util.logging.Logger;

import org.cluster.membership.protocol.model.Node;
import org.cluster.membership.protocol.structures.DList;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.system.ApplicationHome;

public class Config {
	private static final Logger logger = Logger.getLogger(Config.class.getName());
	
	private static final HashMap<String, String> map = Parsing.readAppConfig();
			
	/**The time interval for making requests to other nodes in the cluster*/
	public static final long ITERATION_INTERVAL_MS = Long.parseLong(map.get("iteration.interval.ms"));
	
	/**The factor to multiply by iteration.interval.ms * (iterations=max.expected.node.log.2 || log2(cluster size)), and consider to send an update request*/
	public static final long READ_IDDLE_ITERATIONS_FACTOR = Integer.parseInt(map.get("read.iddle.iteration.factor"));

	/**The time out for connection to other nodes*/
	public static final long CONNECTION_TIME_OUT_MS = Long.parseLong(map.get("connection.timeout.ms"));
		
	/**The time to wait for a node sends a keep alive signal, to avoid removing from cluster*/
	public static final long FAILING_NODE_EXPIRATION_TIME_MS = Long.parseLong(map.get("failing.node.expiration.time.ms"));//one day
				
	/**The max  number of iterations to select a random node*/
	public static final int MAX_EXPECTED_NODE_LOG_2_SIZE = Integer.parseInt(map.get("max.expected.node.log.2"));
	
	
	/**The max length of the set for storing rumors messages, used for recovery other nodes later*/
	public static final int MAX_RUMORS_LOG_SIZE = Integer.parseInt(map.get("max.rumor.log.size"));
	
	/**The max number of bytes allowed to transfer between client and server*/
	public static final int MAX_OBJECT_SIZE = Integer.parseInt(map.get("max.object.size"));
	
	
	public static final Node THIS_PEER = Parsing.readThisPeer();
	
	public static final DList SEEDS = new DList();
	
	public static final String[] MODE = new String[] {"RELEASE"};
	
	public static boolean isValid() {
		return (ITERATION_INTERVAL_MS > 500 && ITERATION_INTERVAL_MS < 1000*60) &&
				(CONNECTION_TIME_OUT_MS > 100 && CONNECTION_TIME_OUT_MS < 1000*60) &&
				(FAILING_NODE_EXPIRATION_TIME_MS > 1000*60*60 && FAILING_NODE_EXPIRATION_TIME_MS < 1000*60*60*24*3) &&
				(MAX_RUMORS_LOG_SIZE < 10*1000*1000) && THIS_PEER != null;
	}
	
	public static void read(ApplicationArguments args) throws Exception {		
		Parsing.setSeedNodes(args);
		
		String appProperties = "using properties: \n";
		for(Map.Entry<String, String> entry : map.entrySet()) 
			appProperties += entry.getKey() + "=" + entry.getValue() + "\n";
		
		logger.info(appProperties);				
	}
	
	private static class Parsing {
		
		private static final String id = "id";
		private static final String address = "address";
		private static final String protocolPort = "protocol.port";
		private static final String servicePort = "server.port";
		private static final String timeZone = "time.zone";		
		
		private static final String homePath = getHomePath();
		
		private static final String configFolder = "config";
		private static final String appConfigFile = "app.properties";
		
		private static String getHomePath() {
			ApplicationHome home = new ApplicationHome(ClusterNodeEntry.class); 
			String path = home.getDir().getAbsolutePath();
			String configFolderPath = path + File.separator + configFolder;
			File configFolder = new File(configFolderPath);
			if(!configFolder.exists()) return path + File.separator + "target";
			return path;
		}

		private static Properties prop(String file) throws Exception {			
			String peerConf = homePath + File.separator + file;
			Properties p = new Properties();
			p.load(new FileInputStream(peerConf));			
			return p;
		}
		
		private static Node readThisPeer() {
			try {
				/*Properties p = prop(configFolder + File.separator  + peerFile);
				boolean noId = false;
				String cId = p.getProperty(id).trim();
				if(cId.isEmpty()) {
					noId = true;
					cId = UUID.randomUUID().toString();
					p.setProperty(id, cId);
				}

				String cAddress = p.getProperty(address).trim();
				Integer cProtocolPort = Integer.parseInt(p.getProperty(protocolPort).trim());
				Integer cServicePort = Integer.parseInt(p.getProperty(servicePort).trim());
				String cTimeZone = p.getProperty(timeZone).trim();*/
				
				
				Properties p = prop(configFolder + File.separator  + appConfigFile);
				boolean noId = false;
				String cId = map.get(id).trim();
				if(cId.isEmpty()) {
					noId = true;
					cId = UUID.randomUUID().toString();
					p.setProperty(id, cId);
				}

				String cAddress = map.get(address).trim();
				Integer cProtocolPort = Integer.parseInt(map.get(protocolPort).trim());
				Integer cServicePort = Integer.parseInt(map.get(servicePort).trim());
				String cTimeZone = map.get(timeZone).trim();
				
				
				Node node = new Node(cId, cAddress, cProtocolPort, cServicePort, TimeZone.getTimeZone(cTimeZone));
				
				if(noId) {
					OutputStream outFile = new FileOutputStream(homePath + File.separator + configFolder + 						
						File.separator + appConfigFile);
					p.store(outFile, (noId ? "#The node id has been generated by the program" : ""));				

				}
				
				return node;

				
				
			} catch(Exception e) {
				e.printStackTrace();
				return null;
			}			

		}
				
		private static HashMap<String, String> readAppConfig() {
			try {
				Properties p = prop(configFolder + File.separator  + "app.properties");
				
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
						protocolPort + "." + count,
						servicePort + "." + count,
						timeZone + "." + count
						);
				
				if(contains == 0) break;
				if(contains == -1) throw new Exception("Error reading config for node " + count + 
						", some attributes are missing or bad configured");
				
				try {				
					
					String cId = args.getOptionValues(id + "." + count).get(0);
					String cAddress = args.getOptionValues(address + "." + count).get(0);
					int cProtocolPort = Integer.parseInt(args.getOptionValues(protocolPort + "." + count).get(0));
					int cServicePort = Integer.parseInt(args.getOptionValues(servicePort + "." + count).get(0));
					String cTimeZone = args.getOptionValues(timeZone + "." + count).get(0);
					
					Node n = new Node(cId, cAddress, cProtocolPort, cServicePort, TimeZone.getTimeZone(cTimeZone));
					Config.SEEDS.add(n);

				} catch(Exception e) {
					throw new Exception("The node " + count + " is not configured properly.");
				}
				count++;
			} while(true);
			
			if(args.containsOption("mode")) {
				String mode = args.getOptionValues("mode").get(0);
				if(!mode.equals("DEBUG") && !mode.equals("RELEASE")) throw new Exception("Invalid mode argument");				
				Config.MODE[0] = mode;
			}
			
		}
		
		
	}
	
}
