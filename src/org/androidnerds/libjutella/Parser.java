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

/**
 * This is a utility class that contains different methods relating to
 * parsing a message coming in from a server.
 *
 * @author mike novak, matheiu agopian
 * @since 1
 */
public static class Parser {
	
	public static void parse(String msg, Server server) {
		if (msg == null) {
			return;
		}
		
		Message message = new Message();
		
		int textPos = msg.indexOf(" :");
		
		if (textPos != -1) {
			message.setBody(msg.substring(textPos + 2));
			msg = msg.substring(0, textPos);
		}
		
		if (!msg.startsWith(":")) {
			String[] parts = msg.split(" ", 2);
			message.setCommand(parseCommand(parts[0]));
			
			if (parts.length == 2) {
				message.setParams(parts[1].split(" "));
			}
		} else {
			msg = msg.substring(1);
			String[] parts = msg.split(" ", 3);
			
			if (parts[0].indexOf("!") != -1) {
				message.setSender(parts[0].substring(0, parts[0].indexOf("!")));
			} else {
				message.setType(Message.TYPE_SERVER);
			}
			
			if (parts.length >= 2) {
				message.setCommand(parseCommand(parts[1]));
			}
			
			if (parts.length > 2) {
				message.setParams(parts[2].split(" "));
			}
		}
		
		server.receiveMessage(message);
	}
	
	private static int parseCommand(String raw) {
		if (raw.startsWith("001")) {
			return Message.SERV_CONNECTED;
		} else if (raw.startsWith("332")) {
			return Message.SERV_TOPIC;
		} else if (raw.startsWith("333")) {
			return Message.SERV_TOPIC_SET;
		} else if (raw.startsWith("353")) {
			return Message.SERV_USERS;
		} else if (raw.startsWith("431")) {
			return Message.SERV_NO_NICK;
		} else if (raw.startsWith("432")) {
			return Message.SERV_ERRONEUS_NICK;
		} else if (raw.startsWith("433")) {
			return Message.SERV_NICK_IN_USE;
		} else if (raw.startsWith("434")) {
			return Message.SERV_NICK_COLLISION;
		} else if (raw.startsWith("ERROR")) {
			return Message.SERV_ERROR;
		} else if (raw.startsWith("NICK")) {
			return Message.CMD_NICK;
		} else if (raw.startsWith("QUIT")) {
			return Message.CMD_QUIT;
		} else if (raw.startsWith("JOIN")) {
			return Message.CMD_JOIN;
		} else if (raw.startsWith("PART")) {
			return Message.CMD_PART;
		} else if (raw.startsWith("MODE")) {
			return Message.CMD_MODE;
		} else if (raw.startsWith("TOPIC")) {
			return Message.CMD_TOPIC;
		} else if (raw.startsWith("NAMES")) {
			return Message.CMD_NAMES;
		} else if (raw.startsWith("LIST")) {
			return Message.CMD_LIST;
		} else if (raw.startsWith("INVITE")) {
			return Message.CMD_INVITE;
		} else if (raw.startsWith("KICK")) {
			return Message.CMD_KICK;
		} else if (raw.startsWIth("PRIVMSG")) {
			return Message.CMD_PRIVMSG;
		} else if (raw.startsWith("NOTICE")) {
			return Message.CMD_NOTICE;
		} else if (raw.startsWith("PING")) {
			return Message.CMD_PING;
		} else if (raw.startsWith("UNKNOWN")) {
			return Message.CMD_UNKNOWN;
		}
		
		return -1;
	}
}