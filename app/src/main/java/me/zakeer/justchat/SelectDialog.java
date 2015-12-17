package me.zakeer.justchat;

import java.util.List;

import me.zakeer.justchat.adapters.StickerGridAdapter;
import me.zakeer.justchat.database.DbSticker;
import me.zakeer.justchat.items.StickerItem;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

public class SelectDialog extends Dialog implements OnItemClickListener {
	
	private static final String TAG = "SelectDialog";
	private Context context;
	
	private GridView gridView;
	private Button cancelButton;
	private TextView titleTextView;
	
	//private SelectAdapter adapter;
	private StickerGridAdapter adapter;
	//private ArrayList<Map<String, String>> arrayList;
	private List<StickerItem> list;
	OnSelectDialogResult mDialogResult; // the callback
	public static final int RESULT_OK = 1;
	public static final int RESULT_CANCEL = 0;
	
	private String title = "Select";
	
	View v = null;
	
	public SelectDialog(Context context) {
		super(context);
		this.context = context;
		
		//arrayList = new ArrayList<Map<String,String>>();
	}
	
	public SelectDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.e(TAG, "onCreate");
		
		setContentView(R.layout.griddisplay);
		
		gridView 			= (GridView) findViewById(R.id.gridview);
		//cancelButton		= (Button) findViewById(R.id.simple_list_cancel_button);
		//titleTextView		= (TextView) findViewById(R.id.simple_list_header_title);
		
		//titleTextView.setText(title);
		
		DbSticker dbSticker = new DbSticker(context);
		list	= dbSticker.getAllDetails();
		
		adapter = new StickerGridAdapter(context, list);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(this);
		
		/*
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if( mDialogResult != null ) {
		            mDialogResult.finish("", RESULT_CANCEL);
		        }
				dismiss();
			}
		});
		*/
	}	
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if( mDialogResult != null ) {
            mDialogResult.finish("", RESULT_CANCEL);
        }
	}
	
	
	
	@Override
	public void setTitle(CharSequence title) {
		super.setTitle(title);
	}
	
	/*
	public void refreshArrayList(ArrayList<Map<String, String>> arrayList) {
		this.arrayList = arrayList;
		adapter.refresh(arrayList);		
	}
	*/
	
    public void setOnItemClickListener(OnSelectDialogResult dialogResult) {
        mDialogResult = dialogResult;
    }
    
    public interface OnSelectDialogResult {
       //void finish(String result, int requestCode);
       void finish(String result, int resultCode);
    }
	
	public void setTitle(String title) {
		//super.setTitle(title);
		this.title = title;
	}
	
	/*
	public Dialog show(Context context) {
        Dialog d = new Dialog(context);
        v = LayoutInflaler.from(context, R.layout.resource, null);
        d.setContentView(v);
        return d.show();
    }
	 */
	
    public void update() {
        //v.invalidate();
    }
	
	/*
	public void setRequestCode(int requestCode) {
		REQUEST_CODE = requestCode;
	}
	*/
    
	
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		
		String imageName = list.get(position).getImage()+list.get(position).getExtension();
		Log.e(TAG, "imageName : "+imageName);		
		
		if( mDialogResult != null ) {
            mDialogResult.finish(imageName, RESULT_OK);
        }
		dismiss();
	}		
}
