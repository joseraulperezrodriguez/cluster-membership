package org.cluster.membership.protocol.core;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Global {
			
	public static void shutdown(ConfigurableApplicationContext application, int seconds) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(seconds * 1000);
					SpringApplication.exit(application, () -> 0);
				} catch(Exception e) {
					SpringApplication.exit(application, () -> 0);
				}
			}
		}).start();
	}

}
