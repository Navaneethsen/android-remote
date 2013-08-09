package com.ceenee.remote;

import com.ceenee.maki.Finder;
import com.ceenee.maki.MyLog;

/**
 * 
 * Sync song book command. This handle songbook syncing signal to send to the board.
 * 
 * 
 * 
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
		MyLog.i("REMOTE: COMMANDSYNC", "Command to run " + command);
		
//		String rawData = command;
//		String type = "application/x-www-form-urlencoded";
//		String encodedData;
//		try {
//			encodedData = URLEncoder.encode( rawData, "UTF-8");
//			URL u = new URL("http://192.168.0.55:5230");
//			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
//			conn.setDoOutput(true);
//			conn.setRequestMethod( "POST" );
//			conn.setRequestProperty( "Content-Type", type );
//			conn.setRequestProperty( "Content-Length", String.valueOf(encodedData.length()));
//			OutputStream os = conn.getOutputStream();
//			os.write( encodedData.getBytes() );
//			
//		} catch (UnsupportedEncodingException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//			MyLog.e("SUNC COMMAND", e.getMessage());
//		}

		return false;
	}
	
	public String compileCommand() {
		String command = this.commandTemplate;
		command = command.replace("[IP]", Finder.getInstance().getPhoneAddress());
		command = command.replace("[MODE]", "i"); //params[0]);
		return command;
	}
	
	

//	public static Charset charset = Charset.forName("UTF-8");
//	public static CharsetEncoder encoder = charset.newEncoder();
//	public static CharsetDecoder decoder = charset.newDecoder();
//
//	public static ByteBuffer str_to_bb(String msg){
//	  try{
//	    return encoder.encode(CharBuffer.wrap(msg));
//	  } catch(Exception e){
//		  e.printStackTrace();
//	  }
//	  return null;
//	}
//	
//	
//	public static String bb_to_str(byte buffer){
//	  String data = "";
//	  try{
//	    int old_position = buffer.position();
//	    data = decoder.decode(buffer).toString();
//	    // reset buffer's position to its original so it is not altered:
//	    buffer.position(old_position);  
//	  }catch (Exception e){
//	    e.printStackTrace();
//	    return "";
//	  }
//	  return data;
//	}
	

}
