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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.androidnerds.libjutella.Message;
import org.androidnerds.libjutella.Parser;
import org.androidnerds.libjutella.Server;

/**
 * The connection class implements runnable and is responsible for managing the
 * connection between the server and the client.
 * 
 * @author mike novak
 * @since 1
 */
public class Connection implements Runnable {
	
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
	private Server server;
	private volatile boolean kill = false;
	
	public Connection(Server s) {
		server = s;
	}
	
	public void disconnect() {
		sendMessage("QUIT :client quit.");
		requestKill();
	}
	
	/**
	 * Takes the raw message and sends it to the remote server
	 *
	 * @param msg the raw message to send to the server
	 * @since 1
	 */
	protected void sendMessage(String msg) {
		try {
			writer.write(msg + "\r\n");
			writer.flush();
		} catch (Exception e) {
			
		}
	}
	
	/**
	 * this method is a check that the connection loop uses to see if the connection
	 * should stop monitoring, close and return from the thread.
	 *
	 * @return kill whether the thread should stay alive or return
	 * @since 1
	 */
	private synchronized boolean shouldKill() {
		return kill;
	}
	
	/**
	 * This method is called when the thread should stop running. This method
	 * is only accessible by the library, application level code cannot kill
	 * a server connection via the thread. To close a server connection see the
	 * server class for the close connection method
	 * 
	 * @since 1
	 * @see Server
	 */
	protected synchronized void requestKill() {
		kill = true;
	}
	
	/**
	 * The run method loops on the bufferedreader from the socket to listen for
	 * new messages incoming from the server. Once the message is received the
	 * server object is sent the raw message. 
	 *
	 * @since 1
	 * @see Message
	 */
	public void run() {
		try {
			socket = new Socket(server.getUrl(), server.getPort()); 
		} catch (Exception e) {
			
		}
		
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			sendMessage("PASS " + server.getPassword());
			sendMessage("NICK " + server.getNickname());
			sendMessage("USER " + server.getNickname() + " * * :Mike Novak");
		} catch (Exception e) {
			
		}
		
		try {
			while (!shouldKill()) {
				String message = reader.readLine();
				
				if (message == null) {
					break;
				}
				
				System.out.println("Raw Message: " + message);
				Parser.parse(message, server);
			}
		} catch (Exception e) {
			
		}
	}
	
	/**
	 * The ConnectionListener facilitates the sending and receiving of
	 * messages from the library and the thread.
	 * 
	 * @author mike novak
	 * @since 1
	 */
	public interface ConnectionListener {
		
		/**
		 * This method is called from the Server class and the ConnectionManager
		 * listens to actually send it to the proper thread.
		 *
		 * @param serv the server to send the message to
		 * @param msg the message object to send
		 * @since 1
		 */
		public void onSendMessage(Server serv, Message msg);
	}
}