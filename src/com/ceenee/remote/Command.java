package com.ceenee.remote;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Base class for command. A command can be primitive or complex command.
 * Simple commands are those command that handles by the physical remote itself.
 * Complex commands are extends command those don't exist on physical remote. However, firmware are able
 * to handle these command such as: sync.
 * 
 * Currently, we have only sync command. However, we may have more later.
 * @see com.ceenee.remote.Remote
 * @see com.ceenee.remote.CommandSync 
 * @author kureikain
 *
 */

abstract class Command {
//	protected Hashtable<String, String> params = new Hashtable<String, String>();
	protected String[] params; 
	
	public class NotSupportCommandException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public NotSupportCommandException(String s) {
			super(s);
		}
	}
	
	public void setParam(String[] _params) {
		params = _params;
	}
	
	
	public static Command getCommand(String command) throws Exception {
		Command c;
		if (command.equalsIgnoreCase("Sync")) {
			c = new CommandSync();
		} else {
			throw new Exception("We are not support this yet");
		}
		return c;
	}
	
	/**
	 * Execute command for only one time.
	 *  
	 * @return
	 */
	abstract public boolean execute();
	
	/**
	 * 
	 * Execute the command. However, accept one param as the number of repeating running this command.
	 * @param how many time we want to repeat this command.
	 * @return
	 * 
	 */
	public boolean execute(int time) {
		for (int count=0; count<time; count++) {
			execute(1);
		}
		return true;
	}
}
