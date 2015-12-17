package me.zakeer.justchat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import me.zakeer.justchat.items.CountryCodeData;
import me.zakeer.justchat.utility.Constant;
import me.zakeer.justchat.utility.Countries;
import me.zakeer.justchat.utility.ReadFiles;


public class CountryCodesActivity extends SherlockActivity implements OnItemClickListener {
	
	private static final String TAG = "CountryCodesActivity";
	
	private Context context = CountryCodesActivity.this;
	
	private static final String NAME = "name";
	private static final String PHONE_CODE = "phone-code";
	private static final String COUNTRY_CODE = "country-code";
	private static final String ALPHA_2 = "alpha-2";
	
	private CountryCodeAdapter adapter;
	
	private ListView codeListView;
	private EditText countryCodeEditText;
	
	private ArrayList<CountryCodeData> allCountriesList;// ,selectedCountriesList;
	
	private JSONArray responseArray = null;
	
	private int position = 0;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.country_codes);
		
		getSupportActionBar().setTitle(getResources().getString(R.string.country_code_header));
		
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIcon(Constant.ACTIONBAR_ICON);
		
		codeListView 		= (ListView) findViewById(R.id.code_listview);
		countryCodeEditText = (EditText) findViewById(R.id.country_code_edittext);
		
		responseArray = new JSONArray();
		
		try {
			String response = ReadFiles.readRawFileAsString(context, R.raw.countrycodes);
			Log.e(TAG, response);
			responseArray = new JSONArray(response);
			Log.i(TAG, ""+responseArray);
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		allCountriesList = new ArrayList<CountryCodeData>();
		for (int i = 0; i < responseArray.length(); i++) {
			try
			{
				CountryCodeData data = new CountryCodeData();
				data.setItem1(responseArray.getJSONObject(i).getString(NAME));
				data.setItem2(responseArray.getJSONObject(i).getString(PHONE_CODE));
				data.setItem3(responseArray.getJSONObject(i).getString(ALPHA_2));
				allCountriesList.add(data);
			}
			catch(Exception e)
			{
				Log.i(TAG, "Exception : "+e.getMessage());
			}
		}
		
		String countryCode = Countries.getCurrentCountry(context);		
		for (int i = 0; i < allCountriesList.size(); i++) {			
			if(countryCode.equalsIgnoreCase(allCountriesList.get(i).getItem3())) {
				Log.e(TAG, ""+position+ " - "+allCountriesList.get(i).getItem3());
				position = i;
				break;
			}
		}
				
		//adapter = new CountryCodeAdapter(context, selectedCountriesList);
		adapter = new CountryCodeAdapter(context, allCountriesList);
		codeListView.setAdapter(adapter);
		codeListView.setOnItemClickListener(this);
		codeListView.setSmoothScrollbarEnabled(true);
		
		codeListView.post(new Runnable() {
            @Override
            public void run() {
            	codeListView.setSelection(position);
            }
        });
		
		countryCodeEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String text = s.toString().trim();
				search(text);
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				
			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
			switch (item.getItemId()) {
				
			case android.R.id.home:
	        	finish();
	        return true;
	        
			default:
				return super.onOptionsItemSelected(item);
			}
	}
	
	
	public void onBackClick(View view) {
		finish();
	}
	
	private void search(String text) {
		ArrayList<CountryCodeData> selectedCountriesList = new ArrayList<CountryCodeData>();
		
		for (CountryCodeData country : allCountriesList) {
			if (country.getItem1().toLowerCase(Locale.ENGLISH)
					.contains(text.toLowerCase(Locale.ENGLISH))) {
				selectedCountriesList.add(country);
			}
		}	
		adapter.refresh(selectedCountriesList);
	}
	
	private class CountryCodeAdapter extends BaseAdapter
	{	
		private LayoutInflater inflater;
	    private ViewHolder holder;
	    private ArrayList<CountryCodeData> list; 
	    	    
	    public CountryCodeAdapter(Context context, ArrayList<CountryCodeData> list) {
            inflater = LayoutInflater.from(context);   
            this.list = list;
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
		
		public void refresh(ArrayList<CountryCodeData> list) {
			this.list = list;
			notifyDataSetChanged();
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
        	
            View hView = convertView;
            if (convertView == null) {
                hView = inflater.inflate(R.layout.country_codes_item, null);
                holder = new ViewHolder();
                holder.country 	= (TextView) hView.findViewById(R.id.country_text);
                holder.code 	= (TextView) hView.findViewById(R.id.country_code_text);      
                hView.setTag(holder);
                
            }
            
            holder = (ViewHolder) hView.getTag();
            
            if (position % 2 == 0) {
                hView.setBackgroundResource(R.drawable.list_selector_white);
            } else {
                hView.setBackgroundResource(R.drawable.list_selector_gray);
            }
            
            try {
            	
            	holder.country.setText(""+list.get(position).getItem1());
            	holder.code.setText(""+list.get(position).getItem2());
            	
            } catch (Exception e) {
            	e.printStackTrace();
            }
            
            return hView;
        }		
	}   
	
    class ViewHolder
    {
        TextView country, code;
    }
    
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		TextView text = (TextView) view.findViewById(R.id.country_code_text);
		String code = text.getText().toString();
		Intent data = new Intent();
		data.putExtra(Constant.CODES, code);
		setResult(RESULT_OK, data);
		finish();
	}	
}
