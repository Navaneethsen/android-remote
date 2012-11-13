package com.axcoto.shinjuku.sushi;
import org.acra.*;
import org.acra.annotation.*;

import android.app.Application;

@ReportsCrashes(formKey = "dEZHZ0VQYWdTUV9MQnVkOUIySW9QbXc6MQ") 
public class MyApp extends Application {
  @Override
  public void onCreate() {
    // The following line triggers the initialization of ACRA
    ACRA.init(this);
    super.onCreate();
  }
}	