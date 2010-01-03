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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.androidnerds.libjutella.net.Connection.ConnectionListener;

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
	private String password;
	private String url;
	private String name;
	private int port;
	private Map<String, Channel> channels;
	private Map<String, PrivateChat> privateChats;
	private List<Message> messages;
	private List<ServerListener> listeners;
	private ConnectionListener connection;
	
	public Server(String name, String url, String nick, String pass, int prt, ConnectionListener conn) {
		channels = Collections.synchronizedMap(new HashMap<String, Channel>());
		privateChats = Collections.synchronizedMap(new HashMap<String, PrivateChat>());
		messages = Collections.synchronizedList(new ArrayList<Message>());
		listeners = Collections.synchronizedList(new ArrayList<ServerListener>());
		
		connection = conn;
		nickname = nick;
		password = pass;
		port = prt;
		this.name = name;
		this.url = url;
	}
	
	public Server(String url, String nick, String pass, int prt, ConnectionListener conn) {
		this(url, url, nick, pass, prt, conn);
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
	
	public String getUrl() {
		return url;
	}
	
	public int getPort() {
		return port;
	}
	
	/**
	 * returns the caller an entire collection of the channels active on the server.
	 * This is not a recommended method if you are trying to modify the underlying 
	 * dataset.
	 *
	 * @return channels actively associated with the server instance
	 * @since 1
	 */
	public Map<String, Channel> getChannels() {
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
		channels.put(c.getName(), c);
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
		channels.remove(c.getName());
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
		Channel c;
		
		switch(message.getCommand()) {
		case Message.SERV_CONNECTED:
			for (ServerListener sl : listeners) {
				sl.onClientConnected();
			}
			
			break;
		case Message.SERV_TOPIC:
			c = channels.get(message.getParams()[message.getParams().length - 1]);
			message.setType(Message.TYPE_CHANNEL);
			c.addMessage(message);
			
			for (ServerListener sl : listeners) {
				sl.onNewChannelMessage(message, c);
			}
			
			break;
		case Message.SERV_TOPIC_SET:
			c = channels.get(message.getParams()[message.getParams().length - 1]);
			String timestamp = message.getParams()[3];
			SimpleDateFormat formatter = new SimpleDateFormat("MM dd, yyyy HH:mm:ss");
			Date date = new Date(Long.parseLong(timestamp) * 1000);
			String text = message.getParams()[2] + " - " + formatter.format(date);
			
			message.setText(text);
			message.setType(Message.TYPE_CHANNEL);
			c.addMessage(message);
			
			for (ServerListener sl : listeners) {
				sl.onNewChannelMessage(message, c);
			}
			
			break;
		case Message.SERV_USERS:
			c = channels.get(message.getParams()[message.getParams().length - 1]);
			String users[] = message.getText().split(" ");
			
			for (String user : users) {
				c.addUser(user);
			}

			break;
		case Message.SERV_NO_NICK:
		case Message.SERV_ERRONEUS_NICK:
		case Message.SERV_NICK_IN_USE:
		case Message.SERV_NICK_COLLISION:
			message.setType(Message.TYPE_SERVER);
			messages.add(message);
			
			for (ServerListener sl : listeners) {
				sl.onNickError(message);
			}
			
			break;
		case Message.SERV_ERROR:
			message.setType(Message.TYPE_SERVER);
			messages.add(message);
			
			for (ServerListener sl : listeners) {
				sl.onServerError(message);
			}
			
			break;
		case Message.CMD_NICK:
			if (message.getSender().equals(nickname)) {
				nickname = message.getText();
			}
			
			synchronized (channels) {
				for (Channel channel : channels.values()) {
					channel.removeUser(message.getSender());
					channel.addUser(message.getText());
					
					for (ServerListener sl : listeners) {
						sl.onUpdateUser(channel, message.getSender(), message.getText());
					}
				}
			}
			
			break;
		case Message.CMD_JOIN:
			if (message.getSender().equals(nickname)) {
				Channel channel = new Channel(this);
				channel.setName(message.getText());
				
				for (ServerListener sl : listeners) {
					sl.onJoinChannel(channel);
				}
			} else {
				Channel channel = channels.get(message.getText());
				channel.addUser(message.getSender());
				
				for (ServerListener sl : listeners) {
					sl.onUserEnteredChannel(message.getSender(), channel);
				}
			}
			break;
		case Message.CMD_QUIT:
			synchronized (channels) {
				for (Channel channel : channels.values()) {
					if (channel.getUsers().contains(message.getSender())) {
						channel.removeUser(message.getSender());
						
						for (ServerListener sl : listeners) {
							sl.onUserQuit(message);
						}
					}
				}
			}
			
			break;
		case Message.CMD_PART:
			Channel chan = channels.get(message.getParams()[0]);
			
			if (message.getSender().equals(nickname)) {
				for (ServerListener sl : listeners) {
					sl.onLeaveChannel(chan.getName());
				}
			} else {
				synchronized (channels) {
					for (Channel channel : channels.values()) {
						if (channel.getUsers().contains(message.getSender())) {
							channel.removeUser(message.getSender());
							
							for (ServerListener sl : listeners) {
								sl.onUserLeftChannel(message.getSender(), channel);
							}
						}
					}
				}
			}
			
			break;
		case Message.CMD_PRIVMSG:
			String dest = message.getParams()[0];
			
			if (dest.toLowerCase().equals(nickname.toLowerCase())) {
				if (!privateChats.containsKey(message.getSender().toLowerCase())) {
					PrivateChat chat = new PrivateChat(message.getSender());
					privateChats.put(message.getSender().toLowerCase(), chat);
				}
				
				PrivateChat chat = privateChats.get(message.getSender().toLowerCase());
				chat.addMessage(message);
				
				for (ServerListener sl : listeners) {
					sl.onNewPrivateMessage(message, chat);
				}
			} else {
				Channel channel = channels.get(dest);
				channel.addMessage(message);
				
				for (ServerListener sl : listeners) {
					sl.onNewChannelMessage(message, channel);
				}
			}
			
			break;
		case Message.CMD_NOTICE:
			messages.add(message);
			
			for (ServerListener sl : listeners) {
				sl.onNewNotice(message, this);
			}
			
			break;
		case Message.CMD_PING:
			Message msg = new Message();
			msg.setType(Message.TYPE_SERVER);
			msg.setCommand(Message.CMD_PONG);
			msg.setText(message.getText());
			connection.onSendMessage(this, msg);
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
		 * The onNewChannelMessage listener receives a finished Message object containing
		 * all the information surrounding the actual server response. For more
		 * information on exactly what the Message contains see Message.
		 *
		 * @param msg the message created based on the server response
		 * @since 1
		 * @see Message
		 */
		public void onNewChannelMessage(Message msg, Channel chan);
		
		/**
		 * This method let's the application level code know that a new private
		 * message has been received.
		 *
		 * @param msg the actual message object.
		 * @param chat the private chat instance receiving the message
		 * @since 1
		 */
		public void onNewPrivateMessage(Message msg, PrivateChat chat);
		
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
		public void onLeaveChannel(String chan);
		
		/**
		 * This method notifies the application level code that there is
		 * an error with the provided nickname and user action is required.
		 *
		 * @param message the last message provided by the server containing
		 * the error message
		 * @since 1
		 */
		public void onNickError(Message message);
		
		/**
		 * This method notifies the application level code that there has been
		 * an error message received by the server.
		 *
		 * @param message the message object containing the error from the server
		 * @since 1
		 */
		public void onServerError(Message message);
		
		/**
		 * This method allows users to act upon a user updating their nick.
		 *
		 * @param chan the channel the user is in
		 * @param oldnick the old nickname of the user
		 * @param newnick the new nickname of the user
		 * @since 1
		 */
		public void onUpdateUser(Channel chan, String oldnick, String newnick);
		
		/**
		 * This method notifies the application level code that the following
		 * user has entered the channel.
		 *
		 * @param user the user's nickname
		 * @param chan the channel the user entered
		 * @since 1
		 */
		public void onUserEnteredChannel(String user, Channel chan);
		
		/**
		 * This method notifies the application level code that the following
		 * user has quit the server. The user that quit is actually the sender
		 * of the message.
		 *
		 * @param message the message associated with the quit
		 * @since 1
		 */
		public void onUserQuit(Message message);
		
		/**
		 * This method notifies the application level code that the following
		 * user has left the channel.
		 *
		 * @param user the user's nickname
		 * @param chan the channel the user left
		 * @since 1
		 */
		public void onUserLeftChannel(String user, Channel chan);
		
		/**
		 * Notifies the application level code that a notice has been
		 * received by the client.
		 *
		 * @param msg the message received
		 * @param serv the server that has received the message
		 * @since 1
		 */
		public void onNewNotice(Message msg, Server serv);
	}
}
