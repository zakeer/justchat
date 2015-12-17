package me.zakeer.justchat.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import me.zakeer.justchat.R;
import me.zakeer.justchat.database.DbSticker;
import me.zakeer.justchat.imagecache.ImageLoader;
import me.zakeer.justchat.items.StickerItem;
import me.zakeer.justchat.utility.FileUtility;

public class StickerGridAdapter extends BaseAdapter {
	
	private static final String TAG = "StickerGridAdapter";
	
	private LayoutInflater inflater;
	private ViewHolder holder;
	private List<StickerItem> list;
	private Context context;
	private ImageLoader imageLoader;
	
	public StickerGridAdapter(Context context, List<StickerItem> list) {
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
	
	public void refresh(List<StickerItem> list) {
		this.list = list;
		notifyDataSetChanged();
	}
	
	public List<StickerItem> getList() {
		return list;
	}
	
	View hView;
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
        
    	hView = convertView;
    	
    	final StickerItem item = list.get(position);
    	
     	if (convertView == null) {
     		
            holder = new ViewHolder();
            
            hView = inflater.inflate(R.layout.griditem, parent, false);     
            holder.image = (ImageView) hView.findViewById(R.id.griditemimage);
            
     		hView.setTag(holder);
        }
     	else {
     		holder = (ViewHolder) hView.getTag();
     	}
     	
     	try {
     		DbSticker dbSticker = new DbSticker(context);
     		if (dbSticker.isImagePresent(Integer.parseInt(item.getId()))) {
            	Log.i(TAG, "Image has been already downloaded. No need to download again.");
            	String imageName = item.getImage()+item.getExtension();
            	Log.i(TAG, "imageName : "+imageName);
            	FileUtility fileUtility = new FileUtility(context);
            	if(imageName!=null) {
	            	if(fileUtility.isStickerPresent(imageName)) {// check if image is present in the folder.
	            		Log.i(TAG, "sticker present : "+imageName);
	            		Bitmap sticker = fileUtility.getStickerImage(imageName);
	            		if(sticker!=null)
	            			holder.image.setImageBitmap(sticker);
	            		else
	            			Log.e(TAG, "sticker null ");
	            	}
	            	else
	            	{
	            		Log.e(TAG, "sticker NOT present : "+imageName);
	            	}
            	}
     		}
     		
        } catch (Exception e) {
        	e.printStackTrace();
        }   
      	return hView;
	}	
	
	class ViewHolder
    {
        ImageView image;
    }
	
	protected void showToast(String message) {
		Toast.makeText(context, ""+message, Toast.LENGTH_SHORT).show();
	}
    
    protected void showToastLong(String message) {
		Toast.makeText(context, ""+message, Toast.LENGTH_LONG).show();
	}   
}   