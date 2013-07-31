package com.ceenee.q;

/**
 * MyApp main class for ARCA crash report
 */
import org.acra.*;
import org.acra.annotation.*;

import com.ceenee.q.R;

import android.app.Application;

@ReportsCrashes(formKey = "", // will not be used
formUri = "http://qceeneer.herokuapp.com/api/submit_acra_report",
formUriBasicAuthLogin = "ceenee", // optional
formUriBasicAuthPassword = "ceenee", // optional
mode = ReportingInteractionMode.TOAST,
resToastText = R.string.crash_toast_text
) 
public class MyApp extends Application {
	
  @Override
  public void onCreate() {
	  super.onCreate();
	  ACRA.init(this);
  }
}	