package com.ceenee.remote;

import com.ceenee.maki.Finder;
import com.ceenee.maki.MyLog;

/**
 * 
 * Sync song book command. This handle songbook syncing signal to send to the board.
 * @see com.ceenee.remote.Remote
 * @see com.ceenee.remote.Command
 * 
 * @author kureikain
 *
 */
public class CommandSync extends Command {
	String commandTemplate = "<iCeeNee ip=\"[IP]\" mode=\"[MODE]\"/>";
	
	@Override
	public boolean execute() {
		String command = compileCommand();
		MyLog.i("REMOTE: COMMANDSYNC", "Command to run" + command);
		return false;
	}
	
	public String compileCommand() {
		String command = this.commandTemplate;
		command.replace("[IP]", Finder.getInstance().getPhoneAddress());
		command.replace("[MODE]", params[0]);
		return command;
	}

}
