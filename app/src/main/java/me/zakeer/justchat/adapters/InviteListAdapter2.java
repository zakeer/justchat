package me.zakeer.justchat.adapters;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import me.zakeer.justchat.R;
import me.zakeer.justchat.database.FriendData;
import me.zakeer.justchat.database.SqliteHandle;
import me.zakeer.justchat.imagecache.ImageLoader;
import me.zakeer.justchat.utility.Constant;

public class InviteListAdapter2 extends BaseAdapter{
	
	private static final String TAG = "GroupFriendListAdapter";
		
	private LayoutInflater inflater;
	private ViewHolder holder;
	private List<FriendData> list;
	private Context context;
	boolean isNew = false;
	SqliteHandle sqhandle;
	private ImageLoader imageLoader;
		
	int capacity=0;
	
	public InviteListAdapter2(Context context, List<FriendData> list) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.list = list;
		imageLoader = new ImageLoader(context);
		
		
	}   
	
	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	@Override
	public int getCount() {
		return list.size();
	}
	
	@Override
	public Object getItem(int position) {
		return list.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return list.indexOf(getItem(position));
	}
	
	public void refresh(List<FriendData> list) {
		this.list = list;
		notifyDataSetChanged();
	}
	
	public List<FriendData> getList() {
		return list;
	}
	
	View hView;
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
        
    	hView = convertView;
    	
    	final FriendData item = list.get(position);
    	
     	if (convertView == null) {
     		
            holder = new ViewHolder();
            
            hView = inflater.inflate(R.layout.invite_item, null);
        	holder.nameTextView			= (TextView) hView.findViewById(R.id.friend_name_text);
        	holder.statusTextView		= (TextView) hView.findViewById(R.id.friend_count_text);
            holder.userImageView 		= (ImageView) hView.findViewById(R.id.friend_image);
	        holder.checkBox 			= (CheckBox) hView.findViewById(R.id.chekbox);
            
     		hView.setTag(holder);
        }
     	else {
     		holder = (ViewHolder) hView.getTag();
     	}
     	
	    try {
	    		holder.nameTextView.setText(""+item.getFname());	    		
		   		holder.checkBox.setVisibility(View.VISIBLE);		   	
		   		holder.statusTextView.setText(""+item.getPhone());
		   		
		   		String f_id=list.get(position).getFriend_id();
		   		Log.e("ITEM"+f_id, item.getStatus());
		   		
		   				   		
		   		String path = "http://www.gbggoa.org/testproject/four/images/pic.jpg";
		   		path = Constant.URL  + Constant.FOLDER_IMAGES + item.getPic();
		   		imageLoader.displayImage(path, holder.userImageView);
		   		
		   		
		   		if(item.getStatus().equals("0"))
		   		{
		   			holder.checkBox.setChecked(false);
		   		}
		   		else
		   		{
		   			holder.checkBox.setChecked(true);
		   		}
		   		holder.checkBox.setTag(position);
		   		holder.checkBox.setOnClickListener(new OnClickListener() {
		   			
					@Override
					public void onClick(View v) {
						
						holder.checkBox 			= (CheckBox) v.findViewById(R.id.chekbox);	
						
						int position = (Integer) v.getTag();
						String f_id=list.get(position).getFriend_id();
						
						sqhandle=new SqliteHandle(context);						
						int total=sqhandle.getCheckedCount();						
						
						if(holder.checkBox.isChecked())
						{
						
							if(total==capacity)
							{
								holder.checkBox.setChecked(false);
								new AlertDialog.Builder(context)
								.setTitle("Promo Code")
								.setMessage("Promo Code Expired or Already Used")
								.setPositiveButton(android.R.string.yes,
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog,
													int which) {
												
											}
										}).setIcon(R.drawable.top_logo).show();
							}
							else
							{								
								sqhandle.changeCheck("1", f_id);
							}
						}
						else
						{
							sqhandle.changeCheck("0", f_id);
						}
						sqhandle.close();
						
					}
				});
		   			
				
    	} catch (Exception e) {
          	e.printStackTrace();
    	}
        
      	return hView;
	}	
	
	class ViewHolder
	{	
		TextView nameTextView,statusTextView;
        ImageView userImageView;
        CheckBox checkBox;
	}
	
	protected void showToast(String message) {
		Toast.makeText(context, ""+message, Toast.LENGTH_SHORT).show();
	}
    
    protected void showToastLong(String message) {
		Toast.makeText(context, ""+message, Toast.LENGTH_LONG).show();
	}
	
}   