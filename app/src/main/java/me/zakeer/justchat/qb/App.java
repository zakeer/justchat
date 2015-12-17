package me.zakeer.justchat.qb;

import android.app.Application;
import android.content.Intent;
import com.quickblox.module.videochat.core.service.QBVideoChatService;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew Dmitrenko
 * Date: 8/20/13
 * Time: 12:03 PM
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this, QBVideoChatService.class));
    }
}
