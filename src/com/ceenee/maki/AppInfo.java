package com.ceenee.maki;

import com.ceenee.q.hd.R;
public class AppInfo {
	//VERSIONING USE http://semver.org/
	public static final String CODENAME       = "HAJIME";
	public static final int VERSION_MAJOR     = 1;
	public static final int VERSION_MINOR     = 0;
	public static final int VERSION_PATCH     = 19;
	public static final String VERSION        = R.string.title_activity_main + " v1.0.19-rc2"; //an alias as a quickway to get version number instead of concat string
	
	public final static int PHASE_EMULATOR    = 1;
	public final static int PHASE_DEVELOPMENT = 2;
	public final static int PHASE_PRODUCTION  = 3;

//	public final static int ENVIRONMENT       = PHASE_EMULATOR;
	public final static int ENVIRONMENT       = PHASE_DEVELOPMENT;
//	 public final static int ENVIRONMENT      = PHASE_PRODUCTION;
	
}
