package me.zakeer.justchat.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import me.zakeer.justchat.R;
import me.zakeer.justchat.imagecache.ImageLoader;
import me.zakeer.justchat.items.FriendItem;
import me.zakeer.justchat.utility.Constant;

public class GroupFriendListDetailAdapter extends BaseAdapter implements OnCheckedChangeListener {
	
	private static final String TAG = "FriendAdapter";
	
	public static final String FRIENDS_IN_GROUP = "1";
	public static final String ALL_FRIENDS_GROUP = "2";
	
	private LayoutInflater inflater;
	private ViewHolder holder;
	private List<FriendItem> list;
	private Context context;
	private ImageLoader imageLoader;
	
	boolean isNew = false;
	
	public GroupFriendListDetailAdapter(Context context, List<FriendItem> list) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.list = list;
		imageLoader = new ImageLoader(context);
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
	
	public void refresh(List<FriendItem> list) {
		this.list = list;
		notifyDataSetChanged();
	}
	
	public List<FriendItem> getList() {
		return list;
	}
	
	View hView;
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
        
    	hView = convertView;
    	
    	final FriendItem item = list.get(position);
    	
     	if (convertView == null) {
     		
            holder = new ViewHolder();
            
            hView = inflater.inflate(R.layout.friendlistitem, null);
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
	    		hView.setBackgroundResource(R.drawable.selector_list);
	    		holder.nameTextView.setText(""+item.getName());
	    		holder.statusTextView.setText(""+item.getStatus());
	    		
		   		String path = "http://www.gbggoa.org/testproject/four/images/pic.jpg";
		   		path = Constant.URL + Constant.FOLDER_IMAGES + item.getImage();
		   		imageLoader.displayImage(path, holder.userImageView);
		   		
		   		holder.checkBox.setVisibility(View.GONE);
	   			holder.checkBox.setClickable(false);
	   			
		   		/*
		   		if(item.getType().equals(ALL_FRIENDS_GROUP)) {
		   			holder.checkBox.setVisibility(View.VISIBLE);
		   			
		   		}
		   		else
		   		{
		   			holder.checkBox.setVisibility(View.GONE);
		   			holder.checkBox.setClickable(false);
		   		}*/
		   			
		   		holder.checkBox.setTag(position);
		   		holder.checkBox.setOnCheckedChangeListener(this);

		   		holder.checkBox.setChecked(item.isChecked());
		   				   		
    	} catch (Exception e) {
          	e.printStackTrace();
    	}
        
      	return hView;
	}	
	
	class ViewHolder
	{	
		TextView nameTextView, statusTextView;
        ImageView userImageView;
        CheckBox checkBox;
	}
	
	protected void showToast(String message) {
		Toast.makeText(context, ""+message, Toast.LENGTH_SHORT).show();
	}
    
    protected void showToastLong(String message) {
		Toast.makeText(context, ""+message, Toast.LENGTH_LONG).show();
	}
    
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		
		int position = (Integer) buttonView.getTag();
		if(isChecked)
			list.get(position).setChecked(true);
		else
			list.get(position).setChecked(false);
		notifyDataSetChanged();
		
	}
	
}   