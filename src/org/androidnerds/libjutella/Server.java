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
package org.androidnerds.libjutella;

import java.util.Collections;
import java.util.Hashtable;

/**
 * The Server class is responsible for holding the list of channels and other information
 * about the actual server the user is connected to. 
 *
 * @author mike novak, matheiu agopian
 * @version 1
 * @since 1
 */
public class Server {
	
	private String nickname;
	private String url;
	private String name;
	private Hashtable<String, Channel> channels;
	private List<Message> messages;
	private List<ServerListener> listeners;
	
	public Server() {
		channels = Collections.synchronizedMap(new Hashtable<String, Channel>());
		messages = Collections.synchronizedList(new ArrayList<Message>());
		listeners = Collections.synchronizedList(new ArrayList<ServerListener>());
	}
	
	/**
	 * returns the name associated with the server.
	 *
	 * @return name of the server
	 * @since 1
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * returns the caller an entire collection of the channels active on the server.
	 * This is not a recommended method if you are trying to modify the underlying 
	 * dataset.
	 *
	 * @return channels actively associated with the server instance
	 * @since 1
	 */
	public Hashtable<String, Channel> getChannels() {
		return channels;
	}
	
	/**
	 * returns the Channel object associated with the given key.
	 * The key is actually the name stored in the Channel object,
	 * this method provides quick lookup if the caller only has the
	 * name and needs to have the actual object associated with the channel.
	 *
	 * @param name the channel name is the key for looking up channels
	 * @return channel associated with the supplied key
	 * @since 1
	 */
	public Channel getChannel(String name) {
		return channels.get(name);
	}
	
	/**
	 * adds the channel object to the server, it should be understood that
	 * the connection with the channel as already been accepted by the server.
	 * The library itself actually makes this call to associate the two objects.
	 * The application level code can find out the result of joining a channel by
	 * implementing the ServerListener onJoinChannel method. This gives the user
	 * back the newly created Channel object as a reference.
	 *
	 * @param c the channel object to insert in the server
	 * @since 1
	 * @see ServerListener
	 */
	public void addChannel(Channel c) {
		channels.put(c.name, c);
	}
	
	/**
	 * This method is called when the part message has been successfully called
	 * to the remote server. To receive a notice when the channel has been parted
	 * implement the ServerListener onPartChannel method. This will give the user 
	 * back the name of the channel as a string
	 * 
	 * @param c the channel to remove from the server
	 * @since 1
	 * @see ServerListener
	 */
	public void removeChannel(Channel c) {
		channels.remove(c.name);
	}
	
	/**
	 * adds the provided ServiceListener to the list of objects contained
	 * in the server class. These listeners will be notified when new
	 * server actions have been processed.
	 *
	 * @param sl the ServerListener to register
	 * @since 1
	 */
	public void setServerListener(ServerListener sl) {
		listeners.add(sl);
	}
	
	/**
	 * This method receives a parsed message and will be responsible for 
	 * appropriately placing the message and notifying the application level
	 * code
	 *
	 * @param message the parsed message object to operate on
	 * @since 1
	 */
	protected void receiveMessage(Message message) {
		
		switch(message.command) {
		case SERV_CONNECTED:
			for (ServerListener sl : listeners) {
				sl.onClientConnected();
			}
			
			break;
		case SERV_TOPIC:
			Channel c = channels.get(message.getParams()[message.getParams().length - 1]);
			c.addMessage(message);
			
			for (ServerListener sl : listeners) {
				sl.onNewMessage(message, c);
			}
			break;
		case SERV_TOPIC_SET:
			Channel c = channels.get(message.getParams()[message.getParams().length - 1]);
			String timestamp = message.getParams()[3];
			SimpleDateFormat formatter = new SimpleDataFormat("MM dd, yyyy HH:mm:ss");
			Date date = new Date(Long.parseLong(timestamp) * 1000);
			String text = message.getParams()[2] + " - " + formatter.format(date);
			
			message.setText(text);
			c.addMessage(message);
			
			for (ServerListener sl : listeners) {
				sl.onNewMessage(message, c);
			}
			break;
		}
	}
	
	/**
	 * The server listener is responsible for notifying the application level code
	 * of some change to the underlying state. Implement the ServerListener methods
	 * to receive updates from the server.
	 *
	 * @author mike novak, matheiu agopian
	 * @since 1
	 */
	public interface ServerListener {
		
		/**
		 * The listener receives a simple notification that the server is now 
		 * connected.
		 *
		 * @since 1
		 */
		public void onClientConnected();
		
		/**
		 * The onNewMessage listener receives a finished Message object containing
		 * all the information surrounding the actual server response. For more
		 * information on exactly what the Message contains see Message.
		 *
		 * @param msg the message created based on the server response
		 * @since 1
		 * @see Message
		 */
		public void onNewMessage(Message msg, Channel chan);
		
		/**
		 * The onJoinChannel method receives a new Channel object that is created
		 * based on a join command sent to the server. For more information on
		 * what the Channel object contains see Channel.
		 *
		 * @param chan the newly created Channel object
		 * @since 1
		 * @see Channel
		 */
		public void onJoinChannel(Channel chan);
		
		/**
		 * This method simply notifies the application level code that the 
		 * channel associated with the channel name string has been closed
		 * and is no longer associated with the server object.
		 * 
		 * @param chan the name of the channel that has been parted
		 * @since 1
		 */
		public void onCloseChannel(String chan);
	}
}
