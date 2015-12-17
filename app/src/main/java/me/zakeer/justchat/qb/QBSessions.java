package me.zakeer.justchat.qb;

import android.content.Context;

import me.zakeer.justchat.sessions.Sessions;
import me.zakeer.justchat.utility.Validate;
import com.quickblox.core.QBCallback;
import com.quickblox.core.result.Result;
import com.quickblox.module.auth.QBAuth;

public class QBSessions {

	Context context;
	
	public QBSessions(Context context) {
		this.context = context;	
	}
	
	public void CheckUserAlive()
	{
		String password = Validate.convertEmail(Sessions.getEmail(context));
		if(DataHolder.getInstance().getCurrentQbUser() == null)
		{
			
			DataHolder.getInstance().setCurrentQbUser(Integer.parseInt(Sessions.getQbId(context)), password);
						
						
		}
		
		QBAuth.createSessionByEmail(Sessions.getEmail(context), password, new QBCallback() {
			
			@Override
			public void onComplete(Result arg0, Object arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onComplete(Result arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	
}
