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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PrivateChat {
	
	private List<Message> messages;
	private String user;
	
	public PrivateChat(String who) {
		user = who;
		messages = Collections.synchronizedList(new ArrayList<Message>());
	}
	
	protected void setUser(String who) {
		user = who;
	}
	
	public String getUser() {
		return user;
	}
	
	protected void addMessage(Message msg) {
		messages.add(msg);
	}
	
	public List<Message> getMessages() {
		return messages;
	}
}