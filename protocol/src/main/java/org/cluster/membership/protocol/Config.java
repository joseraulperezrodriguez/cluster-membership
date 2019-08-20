package org.cluster.membership.protocol;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.TimeZone;
import java.util.UUID;
import java.util.logging.Logger;

import org.cluster.membership.common.model.Node;
import org.cluster.membership.common.model.util.EnvUtils;
import org.cluster.membership.common.model.util.Literals;
import org.cluster.membership.protocol.structures.DList;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

@Component
public class Config {
	
	private static final Logger logger = Logger.getLogger(Config.class.getName());
	
	/**The time interval for making requests to other nodes in the cluster*/
	private long iterationIntervalMs;
	
	/**The factor to multiply by iteration.interval.ms * (iterations=max.expected.node.log.2 || log2(cluster size)), and consider to send an update request*/
	private int readIddleIterationsFactor;

	/**The time out for connection to other nodes*/
	private long connectionTimeOutMs;
		
	/**The number of cycles the cluster has to wait for a node sends a keep alive signal, to avoid removing from cluster*/
	private int cyclesForWaitKeepAlive;//one day
				
	/**The max  number of iterations to select a random node*/
	private int maxExpectedNodeLog2Size;
	
	
	/**The max length of the set for storing rumors messages, used for recovery other nodes later*/
	private int maxRumorsLogSize;
	
	/**The max number of bytes allowed to transfer between client and server*/
	private int maxObjectSize;
	
	
	private Node thisPeer;
	
	private DList seeds;
	
	private String mode;
	
	public Config() throws Exception {
		try {
			Properties properties = ClusterNodeEntry.properties;
			ApplicationArguments args = ClusterNodeEntry.appArguments;

			String appHome = Parsing.getHome(args);
			
			iterationIntervalMs = Long.parseLong(properties.getProperty(Literals.ITERATION_INTERVAL_MS));
			readIddleIterationsFactor = Integer.parseInt(properties.getProperty(Literals.READ_IDDLE_ITERATIONS_FACTOR));
			connectionTimeOutMs = Long.parseLong(properties.getProperty(Literals.CONNECTION_TIME_OUT_MS));
			cyclesForWaitKeepAlive = Integer.parseInt(properties.getProperty(Literals.CYCLES_FOR_WAIT_KEEP_ALIVE));//one day
			maxExpectedNodeLog2Size = Integer.parseInt(properties.getProperty(Literals.MAX_EXPECTED_NODE_LOG_2_SIZE));
			maxRumorsLogSize = Integer.parseInt(properties.getProperty(Literals.MAX_RUMORS_LOG_SIZE));
			maxObjectSize = Integer.parseInt(properties.getProperty(Literals.MAX_OBJECT_SIZE));
			thisPeer = Parsing.readThisPeer(properties, appHome);
			
			seeds = Parsing.readSeedNodes(args);
			
			mode = Parsing.getMode(args);
			
			
		} catch (Exception e) {
			throw e;
		}

	}
	
	public Config(Node thisPeer) {
		this.iterationIntervalMs = 3000l;
		this.connectionTimeOutMs = 1000l;
		this.readIddleIterationsFactor = 3;
		this.cyclesForWaitKeepAlive = 3;
		this.maxExpectedNodeLog2Size = 32;
		this.maxRumorsLogSize = 1000000;
		this.maxObjectSize = 2147483647;
		
		this.thisPeer = thisPeer;
		this.seeds = new DList();
		this.mode = Literals.APP_DEBUG_MODE;
	}
	
	public long getIterationIntervalMs() { return iterationIntervalMs; }
	public int getReadIddleIterationsFactor() { return readIddleIterationsFactor; }
	public long getConnectionTimeOutMs() { return connectionTimeOutMs; }
	public int getCyclesForWaitKeepAlive() { return cyclesForWaitKeepAlive; }
	public int getMaxExpectedNodeLog2Size() { return maxExpectedNodeLog2Size; }
	public int getMaxRumorsLogSize() { return maxRumorsLogSize; }
	public int getMaxObjectSize() { return maxObjectSize; }
	public Node getThisPeer() { return thisPeer; }
	public DList getSeeds() { return seeds; }
	public String getMode() { return mode; }

	public static boolean isValid(Properties properties, ApplicationArguments args) throws Exception {
		long ITERATION_INTERVAL_MS = Long.parseLong(properties.getProperty(Literals.ITERATION_INTERVAL_MS));
		long CONNECTION_TIME_OUT_MS = Long.parseLong(properties.getProperty(Literals.CONNECTION_TIME_OUT_MS));
		long CYCLES_FOR_WAIT_KEEP_ALIVE = Long.parseLong(properties.getProperty(Literals.CYCLES_FOR_WAIT_KEEP_ALIVE));
		long MAX_RUMORS_LOG_SIZE = Long.parseLong(properties.getProperty(Literals.MAX_RUMORS_LOG_SIZE));
		
		String appHome = Parsing.getHome(args);
		
		Node node = Parsing.readThisPeer(properties, appHome);
		
		return (ITERATION_INTERVAL_MS >= 500 && ITERATION_INTERVAL_MS <= 1000*60) &&
				(CONNECTION_TIME_OUT_MS >= 100 && CONNECTION_TIME_OUT_MS <= 1000*60) &&
				(CYCLES_FOR_WAIT_KEEP_ALIVE >= 1) &&
				(MAX_RUMORS_LOG_SIZE <= 10*1000*1000) && node != null;
	}
	
	public static Properties read(ApplicationArguments args) throws Exception {
		String appHome = Parsing.getHome(args);

		Properties properties = Parsing.prop(appHome);
		
		String appProperties = "using properties: \n";
		for(Object key : properties.keySet()) 
			appProperties += key + "=" + properties.getProperty(key.toString()) + "\n";
		
		logger.info(appProperties);			
		
		return properties;
	}
	
	public static class Parsing {
		
		
		private static final String configFolder = "config";
		private static final String appConfigFile = Literals.APP_PROP_FILE;
		
		//private static String appHome;
		
		private static Properties prop(String appHome) throws Exception {			
			String peerConf = appHome + File.separator + Parsing.configFolder + File.separator  + Parsing.appConfigFile;
			Properties p = new Properties();
			p.load(new FileInputStream(peerConf));			
			return p;
		}
		
		public static String getHome(ApplicationArguments args) {
			String appHome = "";
			if(args.containsOption(Literals.APP_HOME)) appHome = args.getOptionValues(Literals.APP_HOME).get(0);			
			else appHome = EnvUtils.getHomePath(ClusterNodeEntry.class, Parsing.configFolder);
			return appHome;
		}
		
		public static Node readThisPeer(Properties properties, String appHome) throws Exception {
			try {				
				//Properties p = prop(configFolder + File.separator  + appConfigFile);
				boolean noId = false;
				String cId = properties.getProperty(Literals.NODE_ID).trim();
				if(cId.isEmpty()) {
					noId = true;
					cId = UUID.randomUUID().toString();
					properties.setProperty(Literals.NODE_ID, cId);
				}

				String cAddress = properties.getProperty(Literals.NODE_ADDRESS).trim();
				Integer cProtocolPort = Integer.parseInt(properties.getProperty(Literals.NODE_PROTOCOL_PORT).trim());
				Integer cServicePort = Integer.parseInt(properties.getProperty(Literals.NODE_SERVER_PORT).trim());
				String cTimeZone = properties.getProperty(Literals.NODE_TIME_ZONE).trim();
				
				
				Node node = new Node(cId, cAddress, cProtocolPort, cServicePort, TimeZone.getTimeZone(cTimeZone));
				
				if(noId) {
					OutputStream outFile = new FileOutputStream(appHome + File.separator + configFolder + 						
						File.separator + appConfigFile);
					properties.store(outFile, (noId ? "#The node id has been generated by the program" : ""));				

				}				
				return node;
			} catch(Exception e) {
				throw e;
			}			
		}
						
		private static int containsOptions(ApplicationArguments args, String... options) {
			int count = 0;
			
			for(String o : options) if(args.containsOption(o)) count++;
			
			if(count == options.length) return 1;
			return (count == 0 ? 0 : -1);
			
		}
		
		private static String getMode(ApplicationArguments args) throws Exception {
			if(args.containsOption(Literals.APP_MODE)) {
				String mode = args.getOptionValues(Literals.APP_MODE).get(0);
				if(!mode.equals(Literals.APP_DEBUG_MODE) && !mode.equals(Literals.APP_RELEASE_MODE)) throw new Exception("Invalid mode argument");				
				return mode;
			} else return Literals.APP_RELEASE_MODE;
		}
		
		private static DList readSeedNodes(ApplicationArguments args) throws Exception {
			DList seeds = new DList(); 
			int count = 1;
			do {
				int contains = containsOptions(args, Literals.NODE_ID + "." + count, 
						Literals.NODE_ADDRESS + "." + count,
						Literals.NODE_PROTOCOL_PORT + "." + count,
						Literals.NODE_SERVER_PORT + "." + count,
						Literals.NODE_TIME_ZONE + "." + count
						);
				
				if(contains == 0) break;
				if(contains == -1) throw new Exception("Error reading config for node " + count + 
						", some attributes are missing or wrongly configured");
				
				try {				
					
					String cId = args.getOptionValues(Literals.NODE_ID + "." + count).get(0);
					String cAddress = args.getOptionValues(Literals.NODE_ADDRESS + "." + count).get(0);
					int cProtocolPort = Integer.parseInt(args.getOptionValues(Literals.NODE_PROTOCOL_PORT + "." + count).get(0));
					int cServicePort = Integer.parseInt(args.getOptionValues(Literals.NODE_SERVER_PORT + "." + count).get(0));
					String cTimeZone = args.getOptionValues(Literals.NODE_TIME_ZONE + "." + count).get(0);
					
					Node n = new Node(cId, cAddress, cProtocolPort, cServicePort, TimeZone.getTimeZone(cTimeZone));
					seeds.add(n);

				} catch(Exception e) {
					throw new Exception("The node " + count + " is not configured properly.");
				}
				count++;
			} while(true);
						
			return seeds;
		}
	}
}
