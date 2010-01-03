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
import java.util.ArrayList;
import java.util.List;

public class Channel {
	
	private String name;
	private String topic;
	private List<String> users;
	private List<Message> messages;
	private Server server;
	
	public Channel(Server s) {
		users = Collections.synchronizedList(new ArrayList<String>());
		messages = Collections.synchronizedList(new ArrayList<Message>());
		server = s;
	}
	
	public void setName(String n) {
		name = n;
	}
	
	public String getName() {
		return name;
	}
	
	public void setTopic(String t) {
		topic = t;
	}
	
	public void addUser(String user) {
		users.add(user);
	}
	
	public List<String> getUsers() {
		return users;
	}
	
	public void removeUser(String user) {
		users.remove(user);
	}
	
	public void addMessage(Message msg) {
		messages.add(msg);
	}
	
	public List<Message> getMessages() {
		return messages;
	}
}