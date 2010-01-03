/* Copyright (C) 2009, 2010 Android Nerds Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.androidnerds.libjutella.net;

import java.util.Collections;
import java.util.Hashtable;

import org.androidnerds.libjutella.Server;

/**
 * This class contains references to all the current connections for the client.
 * It is also responsible for creating and destroying each thread and its 
 * objects.
 *
 * @author mike novak, matheiu agopian
 * @since 1
 */
public class ConnectionManager {
	
	private Hashtable<Server, Connection> connections;
	
	public ConnectionManager() {
		connections = new Collections.synchronizedMap(new Hashtable<Server, Connection>());
	}
	
	/**
	 * Sets up the new connection with a server and adds the connection object
	 * to the map for the manager.
	 *
	 * @param s the server to create a connection for
	 * @since 1
	 */
	public void createNewConnection(Server s) {
		Connection c = new Connection(s);
		connections.put(s, c);
	}
	
	/**
	 * When closeConnection is called the library handles closing the link with
	 * the server and terminating the thread.
	 *
	 * @param s the serve to disconnect from
	 * @since 1
	 */
	public void closeConnection(Server s) {
		Connection c = sconnections.get(s);
		c.disconnect();
	}
}