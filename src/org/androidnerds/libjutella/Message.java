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

import java.util.Date;

/**
 * This class defines a message from the server. Each message from the server
 * gets a message object created for it.
 *
 * @author mike novak, matheiu agopian
 * @since 1
 */
public class Message {
	
	private int type;
	private int command;
	private String sender;
	private String[] parameters;
	private String body;
	private long timestamp;
	
	public Message() {
		timestamp = new Date().getTime();
	}
	
	/**
	 * set the type of the message
	 *
	 * @since 1
	 */
	protected void setType(int t) {
		type = t;
	}
	
	/**
	 * get the type of the message, the types are defined in the Message class
	 *
	 * @since 1
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * set the command of the message, command types are defined in the Message class
	 * 
	 * @since 1
	 */
	protected void setCommand(int c) {
		command = c;
	}
	
	/**
	 * gets the command of the message, command types are defined in the Message class
	 *
	 * @since 1
	 */
	public int getCommand() {
		return command;
	}
	
	/**
	 * sets the body of the message
	 *
	 * @since 1
	 */
	protected void setBody(String b) {
		body = b;
	}
	
	/**
	 * gets the body of the message
	 *
	 * @since 1
	 */
	public String getBody() {
		return body;
	}
	
	/**
	 * sets the parameters of the message
	 *
	 * @since 1
	 */
	protected void setParams(String[] params) {
		parameters = params;
	}
	
	/**
	 * gets the parameters of the message.
	 * 
	 * @since 1 
	 */
	public String[] getParams() {
		return parameters;
	}
	
	/**
	 * returns the raw timestamp of the message
	 *
	 * @since 1
	 */
	public long rawTimestamp() {
		return timestamp;
	}
	
	/**
	 * return the short version of the timestamp
	 *
	 * @since 1
	 */
	public String shortTimestamp() {
		return "";
	}
	
	/**
	 * return the long version of the timestamp
	 *
	 * @since 1
	 */
	public String longTimestamp() {
		return "";
	}
	
	/* Below contains the definitions for types and commands */
	
	//the following items refer to message types.
	public static final int TYPE_NOTICE = 1;
	public static final int TYPE_SERVER = 2;
	public static final int TYPE_CHANNEL = 3;
	public static final int TYPE_PRIVATE = 4;
	
	//the following items refer to message commands.
	public static final int SERV_CONNECTED = 1;
	public static final int SERV_TOPIC = 2;
	public static final int SERV_TOPIC_SET = 3;
	public static final int SERV_USERS = 4;
	public static final int SERV_NO_NICK = 5;
	public static final int SERV_ERRONEUS_NICK = 6;
	public static final int SERV_NICK_IN_USE = 7;
	public static final int SERV_NICK_COLLISION = 8;
	public static final int SERV_ERROR = 9;
	
	public static final int CMD_NICK = 1;
	public static final int CMD_QUIT = 2;
	public static final int CMD_JOIN = 3;
	public static final int CMD_PART = 4;
	public static final int CMD_MODE = 5;
	public static final int CMD_TOPIC = 6;
	public static final int CMD_NAMES = 7;
	public static final int CMD_LIST = 8;
	public static final int CMD_INVITE = 9;
	public static final int CMD_KICK = 10;
	public static final int CMD_PRIVMSG = 11;
	public static final int CMD_NOTICE = 12;
	public static final int CMD_PING = 13;
	public static final int CMD_UNKNOWN = 14;
	
}