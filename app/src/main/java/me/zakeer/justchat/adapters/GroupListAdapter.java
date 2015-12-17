package me.zakeer.justchat.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import me.zakeer.justchat.R;
import me.zakeer.justchat.imagecache.ImageLoader;
import me.zakeer.justchat.items.GroupListItem;
import me.zakeer.justchat.sessions.Sessions;
import me.zakeer.justchat.utility.Constant;

public class GroupListAdapter extends BaseAdapter {
	
	private static final String TAG = "GroupListAdapter";
	private LayoutInflater inflater;
	private ViewHolder holder;
	private List<GroupListItem> list;
	private Context context;
	private ImageLoader imageLoader;
		
	private String response = "";
	
	public GroupListAdapter(Context context, List<GroupListItem> list) {
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
		return null;
	}
	
	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	public void refresh(List<GroupListItem> list) {
		this.list = list;
		notifyDataSetChanged();
	}
	
	public List<GroupListItem> getList() {
		return list;
	}
	
	View hView;
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
        
    	hView = convertView;
    	
    	final GroupListItem item = list.get(position);
    	
     	if (convertView == null) {
     		
            holder = new ViewHolder();
            
            hView = inflater.inflate(R.layout.friendlistitem, null);
        	holder.nameTextView			= (TextView) hView.findViewById(R.id.friend_name_text);
            holder.statusTextView			= (TextView) hView.findViewById(R.id.friend_count_text);
            holder.userImageView 			= (ImageView) hView.findViewById(R.id.friend_image);
	        holder.onlineImageView 			= (ImageView) hView.findViewById(R.id.friend_onlineimage);
            
     		hView.setTag(holder);
        }
     	else {
     		holder = (ViewHolder) hView.getTag();
     	}
     	
	    try {
	    	
	    	if(item.getAdminId().equals(Sessions.getUserId(context)))
	    	{
	    		holder.onlineImageView.setVisibility(View.VISIBLE);
	    		holder.onlineImageView.setImageResource(R.drawable.online);
	    		holder.nameTextView.setTextColor(context.getResources().getColor(R.color.green_android));
	    	}
	    	else
	    	{
	    		holder.nameTextView.setTextColor(context.getResources().getColor(R.color.holo_blue_dark));
	    		holder.onlineImageView.setVisibility(View.GONE);
	    	}
	    	
			hView.setBackgroundResource(R.drawable.selector_list);
	    	if(item.isNew())
    			hView.setBackgroundResource(R.drawable.selector_list_gray);
	    		
	    		holder.nameTextView.setText(""+item.getName());
	    		holder.statusTextView.setText("");
	    		if(item.getStatus()!=null) {
	    			if(item.getStatus().equals("") || item.getStatus().equals("null"))
	    			{}
	    			else
	    			{
	    				holder.statusTextView.setText(""+item.getStatus());
	    			}
	    		}
		   		String path = "http://www.gbggoa.org/testproject/four/images/pic.jpg";
		   		path = Constant.URL + Constant.FOLDER + Constant.FOLDER_IMAGES + item.getImage();
		   		imageLoader.displayImage(path, holder.userImageView);
		   				   		
		   		
		   		
		   		
    	} catch (Exception e) {
          	e.printStackTrace();
    	}
        
      	return hView;
	}	
	
	class ViewHolder
	{	
		TextView nameTextView, statusTextView;
        ImageView userImageView, onlineImageView;
	}
	
}   