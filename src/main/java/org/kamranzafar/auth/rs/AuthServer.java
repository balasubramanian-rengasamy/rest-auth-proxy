/**
 * Copyright 2012 Kamran Zafar 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 * 
 */
package org.kamranzafar.auth.rs;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;

/**
 * @author Kamran Zafar
 * 
 */
public class AuthServer {
	private static final URI BASE_URI = getBaseURI();

	private static int getPort(int defaultPort) {
		String port = System.getProperty("auth.server.port");

		if (port == null) {
			port = Configuration.getConfig().getProperty("server.port");
		}

		try {
			Integer.parseInt(port);
		} catch (NumberFormatException e) {

		}
		return defaultPort;
	}

	private static URI getBaseURI() {
		String addr = "127.0.0.1";
		String bindAddress = Configuration.getConfig().getProperty("server.bind");

		if (bindAddress == null) {
			try {
				InetAddress ia = InetAddress.getLocalHost();
				addr = ia.getHostAddress();
			} catch (UnknownHostException e) {
			}
		} else {
			addr = bindAddress;
		}

		return UriBuilder.fromUri("http://" + addr + "/").port(getPort(9998)).build();
	}

	protected static HttpServer startServer() throws IOException {
		System.out.println("Starting rest-auth-proxy server...");
		ResourceConfig rc = new PackagesResourceConfig("org.kamranzafar.auth.rs", "org.kamranzafar.auth.rs.ldap");
		rc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

		return GrizzlyServerFactory.createHttpServer(BASE_URI, rc);
	}

	public static void main(String[] args) throws IOException {
		HttpServer httpServer = startServer();
		System.out.println("rest-auth-proxy server has started");
		System.in.read();
		httpServer.stop();
	}
}
