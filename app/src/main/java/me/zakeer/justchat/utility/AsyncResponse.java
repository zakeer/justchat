package me.zakeer.justchat.utility;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import me.zakeer.justchat.items.FriendDetailItem;
import me.zakeer.justchat.items.FriendItem;
import me.zakeer.justchat.items.GcmDetailItem;
import me.zakeer.justchat.items.GroupDetailItem;
import me.zakeer.justchat.items.GroupListItem;
import me.zakeer.justchat.items.StickerItem;
import me.zakeer.justchat.items.UserDetailItem;
import me.zakeer.justchat.services.GcmIntentService;

public class AsyncResponse {
	
	private static final String TAG = "AsyncResponse";
	private JSONObject jsonObject;
	private JSONArray jsonArray;
	private ArrayList<Map<String, String>> arrayList;
	
	private String message = "";
	private boolean success = false;
	private String response;
	
	public AsyncResponse(String response) {
		this.response = response;
	}
	
	public boolean ifSuccess() {
		
		try {
			if(response!=null) {
				jsonObject = new JSONObject(response);
				success = jsonObject.getBoolean(Constant.SUCCESS);
			}
            return success;
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data => " + e.toString());            
            return success;
        }
	}
	
	public String getMessage() {
		
		try {
			if(response!=null) {
				jsonObject = new JSONObject(response);
				message = jsonObject.getString(Constant.MESSAGE);
			}
			else {
	            message = "Some error occured. Please try again.";
			}
            return message;
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data => " + e.toString());      
            message = "Some error occured. Please try again.";
            return message;
        }
	}
	
	public int getCount() {
		return jsonArray.length();
	}
	
	public ArrayList<FriendItem> getFriendList() {
		
		ArrayList<FriendItem> arrayList = new ArrayList<FriendItem>();
		
        try {
        	jsonObject = new JSONObject(response);
            Log.d("JSON Response : ", jsonObject.toString());
            
            jsonArray = jsonObject.getJSONArray(Constant.TAG_DETAILS);
            Log.d(TAG, "JSON Array : " + jsonArray);
            
            // looping through All Products
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);
                
                String id			= c.getString(Constant.ID);
                String name			= c.getString(Constant.NAME);
                String lname		= c.getString(Constant.LNAME);
                String image		= c.getString(Constant.IMAGE);
                String isOnline		= c.getString(Constant.ONLINE);
                String status		= c.getString(Constant.STATUS);                
                String phone		= c.getString(Constant.PHONE);
                String phoneCode	= c.getString(Constant.PHONE_CODE);
                String qbId			= c.getString(Constant.QB_ID);
                
                FriendItem item = new FriendItem();
                item.setId(id);
                item.setName(name);
                item.setLname(lname);
                item.setImage(image);
                item.setIsOnline(isOnline);
                item.setStatus(status);
                item.setNew(false);
                
                item.setPhone(phone);
                item.setPhoneCode(phoneCode);
                
                item.setQbId(qbId);
                
                arrayList.add(item);
            }            
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data : " + e.toString());
        }  
        return arrayList;
	}
	

	public ArrayList<FriendItem> getFriendAllList() {
		
		ArrayList<FriendItem> arrayList = new ArrayList<FriendItem>();
		
        try {
        	jsonObject = new JSONObject(response);
            Log.d("JSON Response : ", jsonObject.toString());
            
            jsonArray = jsonObject.getJSONArray(Constant.TAG_DETAILS);
            Log.d(TAG, "JSON Array : " + jsonArray);
            
            // looping through All Products
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);
                
                String id			= c.getString(Constant.ID);
                String name			= c.getString(Constant.NAME);
                String lname		= c.getString(Constant.LNAME);
                String image		= c.getString(Constant.IMAGE);
                String status		= c.getString(Constant.STATUS);
                String type			= c.getString(Constant.TYPE);
                String adminId		= c.getString(Constant.ADMIN_ID);
                
                FriendItem item = new FriendItem();
                item.setId(id);
                item.setName(name);
                item.setLname(lname);
                item.setImage(image);
                item.setStatus(status);
                item.setType(type);
                item.setAdminId(adminId);
                
                arrayList.add(item);
            }            
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data : " + e.toString());
        }  
        return arrayList;
	}
	
	public ArrayList<FriendItem> getFriendAllListAfterRequest() {
		
		ArrayList<FriendItem> arrayList = new ArrayList<FriendItem>();
		
        try {
        	jsonObject = new JSONObject(response);
            Log.d("JSON Response : ", jsonObject.toString());
            
            jsonArray = jsonObject.getJSONArray(Constant.TAG_DETAILS);
            Log.d(TAG, "JSON Array : " + jsonArray);
            
            // looping through All Products
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);
                
                String type			= c.getString(Constant.TYPE);
                String adminId			= c.getString(Constant.ADMIN_ID);
                String friendRequestId		= c.getString(Constant.FRIEND_REQUEST_ID);
                
                FriendItem item = new FriendItem();
                item.setType(type);
                item.setAdminId(adminId);
                item.setFriendRequestId(friendRequestId);
                
                arrayList.add(item);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data : " + e.toString());
        }  
        return arrayList;
	}
	
	public ArrayList<FriendDetailItem> getFriendDetail1() { // OLD : dummy of getFriendDetail except userName
		
		ArrayList<FriendDetailItem> arrayList = new ArrayList<FriendDetailItem>();
		
        try {
        	jsonObject = new JSONObject(response);
            Log.d("JSON Response : ", jsonObject.toString());
            
            jsonArray = jsonObject.getJSONArray(Constant.TAG_DETAILS);
            Log.d(TAG, "JSON Array : " + jsonArray);
            
            // looping through All Products
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);
                
                FriendDetailItem item = new FriendDetailItem();
                
                // Storing each json item in variable
                String id 			= c.getString(Constant.ID);
                String userId		= c.getString(Constant.USER_ID);
                String type			= c.getString(Constant.TYPE);
                String message		= c.getString(Constant.MESSAGE);
                String time			= c.getString(Constant.TIME);
                String image		= c.getString(Constant.IMAGE);
                String who			= c.getString(Constant.WHO);
                
                item.setId(id);
                item.setType(type);
                item.setTime(time);
                item.setMessage(message);
                item.setImageType(0);
                
                item.setUserId(userId);
                item.setUserImage(image);
                
                item.setWho(who);
                
                arrayList.add(item);
            }
            
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data : " + e.toString());
        }
        return arrayList;
	}
	
	public ArrayList<FriendDetailItem> getFriendDetail() {
		
		ArrayList<FriendDetailItem> arrayList = new ArrayList<FriendDetailItem>();
		
        try {
        	jsonObject = new JSONObject(response);
            Log.d("JSON Response : ", jsonObject.toString());
            
            jsonArray = jsonObject.getJSONArray(Constant.TAG_DETAILS);
            Log.d(TAG, "JSON Array : " + jsonArray);
            
            // looping through All Products
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);
                
                FriendDetailItem item = new FriendDetailItem();
                
                // Storing each json item in variable
                String id 			= c.getString(Constant.ID);
                String userId		= c.getString(Constant.USER_ID);
                String type			= c.getString(Constant.TYPE);
                String message		= c.getString(Constant.MESSAGE);
                String time			= c.getString(Constant.TIME);
                String image		= c.getString(Constant.IMAGE);
                String who			= c.getString(Constant.WHO);
                String userName		= c.getString(Constant.NAME);
                
                String status			= c.getString(Constant.STATUS);
                String deliveryTime		= c.getString(Constant.DELIVERY_TIME);
                
                item.setId(id);
                item.setType(type);
                item.setTime(time);
                item.setMessage(message);
                item.setImageType(0);
                
                item.setUserId(userId);
                item.setUserImage(image);
                item.setUserName(userName);
                
                item.setWho(who);
                item.setChecked(false);
                
                item.setStatus(status);
                item.setDeliveryTime(deliveryTime);
                
                arrayList.add(item);
            }
            
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data : " + e.toString());
        }
        return arrayList;
	}
	
	public ArrayList<UserDetailItem> getUserDetail() {
		
		ArrayList<UserDetailItem> arrayList = new ArrayList<UserDetailItem>();
		
        try {
        	jsonObject = new JSONObject(response);
            Log.d("JSON Response : ", jsonObject.toString());
            
            jsonArray = jsonObject.getJSONArray(Constant.TAG_DETAILS);
            Log.d(TAG, "JSON Array : " + jsonArray);
            
            // looping through All Products
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);
                
                UserDetailItem item = new UserDetailItem();
                
                // Storing each json item in variable
                String id 			= c.getString(Constant.ID);
                String name			= c.getString(Constant.NAME);
                String lname		= c.getString(Constant.LNAME);
                String phone		= c.getString(Constant.PHONE);
                String phoneCode	= c.getString(Constant.PHONE_CODE);
                String email		= c.getString(Constant.EMAIL);
                String status		= c.getString(Constant.STATUS);
                String online		= c.getString(Constant.ONLINE);
                String image		= c.getString(Constant.IMAGE);
                
                item.setId(id);
                item.setName(name);
                item.setLname(lname);
                item.setPhone(phone);
                item.setPhoneCode(phoneCode);
                item.setEmail(email);
                item.setOnline(online);
                item.setStatus(status);
                item.setImage(image);
                
                arrayList.add(item);                
            }            
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data : " + e.toString());
        }  
        return arrayList;
	}
	
	public ArrayList<GcmDetailItem> getGcmDetail() {
		
		ArrayList<GcmDetailItem> arrayList = new ArrayList<GcmDetailItem>();
		
        try {
        	jsonObject = new JSONObject(response);
            Log.d("JSON Response : ", jsonObject.toString());
            
            jsonArray = jsonObject.getJSONArray(Constant.TAG_DETAILS);
            Log.d(TAG, "JSON Array : " + jsonArray);
            
            // looping through All Products
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);
                
                GcmDetailItem item = new GcmDetailItem();
                
                // Storing each json item in variable
                String id 			= c.getString(Constant.ID);
                String who 			= c.getString(Constant.WHO);
                String type 		= c.getString(Constant.TYPE);
                
                item.setId(id);
                item.setWho(who);
                item.setType(type);
                
                if(who.equals(GcmIntentService.MESSAGETYPE_REQUEST))
                {
                	String name = c.getString(Constant.NAME);
                	String userId = c.getString(Constant.USER_ID);
                	String friendId = c.getString(Constant.FRIEND_ID);
                	
                	item.setName(name);
                	item.setUserId(userId);
                	item.setFriendId(friendId);
                }
                
                arrayList.add(item);
            }
            
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data : " + e.toString());
        }
        return arrayList;
	}
	
	public ArrayList<GroupListItem> getGroupList() {
		
		ArrayList<GroupListItem> arrayList = new ArrayList<GroupListItem>();
		
        try {
        	jsonObject = new JSONObject(response);
            Log.d("JSON Response : ", jsonObject.toString());
            
            jsonArray = jsonObject.getJSONArray(Constant.TAG_DETAILS);
            Log.d(TAG, "JSON Array : " + jsonArray);
            
            // looping through All Products
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);
                
                String id			= c.getString(Constant.ID);
                String name			= c.getString(Constant.NAME);
                String image		= c.getString(Constant.IMAGE);
                String status		= c.getString(Constant.STATUS);
                String adminId		= c.getString(Constant.ADMIN_ID);
                
                GroupListItem item = new GroupListItem();
                item.setId(id);
                item.setName(name);
                item.setImage(image);
                item.setStatus(status);
                item.setAdminId(adminId);
                item.setNew(false);
                
                arrayList.add(item);
            }            
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data : " + e.toString());
        }  
        return arrayList;
	}
	
	public ArrayList<GroupDetailItem> getGroupDetail() {
		
		ArrayList<GroupDetailItem> arrayList = new ArrayList<GroupDetailItem>();
		
        try {
        	jsonObject = new JSONObject(response);
            Log.d("JSON Response : ", jsonObject.toString());
            
            jsonArray = jsonObject.getJSONArray(Constant.TAG_DETAILS);
            Log.d(TAG, "JSON Array : " + jsonArray);
            
            // looping through All Products
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);
                
                GroupDetailItem item = new GroupDetailItem();
                
                // Storing each json item in variable
                String id 			= c.getString(Constant.ID);
                String userId		= c.getString(Constant.USER_ID);
                String groupId		= c.getString(Constant.FRIEND_ID);
                String type			= c.getString(Constant.TYPE);
                String message		= c.getString(Constant.MESSAGE);
                String time			= c.getString(Constant.TIME);
                String image		= c.getString(Constant.IMAGE);
                String who			= c.getString(Constant.WHO);
                String userName		= c.getString(Constant.NAME);
                
                item.setId(id);
                item.setType(type);
                item.setTime(time);
                item.setMessage(message);
                item.setImageType(0);
                
                item.setUserId(userId);
                item.setUserImage(image);
                item.setUserName(userName);
                
                item.setWho(who);
                
                item.setGroupId(groupId);
                
                arrayList.add(item);                
            }
            
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data : " + e.toString());
        }
        return arrayList;
	}
	

	public ArrayList<FriendItem> getGroupFriendList() {
		
		ArrayList<FriendItem> arrayList = new ArrayList<FriendItem>();
		
        try {
        	jsonObject = new JSONObject(response);
            Log.d("JSON Response : ", jsonObject.toString());
            
            jsonArray = jsonObject.getJSONArray(Constant.TAG_DETAILS);
            Log.d(TAG, "JSON Array : " + jsonArray);
            
            // looping through All Products
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);
                
                String id			= c.getString(Constant.ID);
                String name			= c.getString(Constant.NAME);
                String lname		= c.getString(Constant.LNAME);
                String image		= c.getString(Constant.IMAGE);
                String status		= c.getString(Constant.STATUS);
                
                FriendItem item = new FriendItem();
                item.setId(id);
                item.setName(name);
                item.setLname(lname);
                item.setImage(image);
                item.setStatus(status);
                
                arrayList.add(item);
            }   
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data : " + e.toString());
        }  
        return arrayList;
	}
	
	public ArrayList<GroupListItem> getGroupIdList() {
		
		ArrayList<GroupListItem> arrayList = new ArrayList<GroupListItem>();
		
        try {
        	jsonObject = new JSONObject(response);
            Log.d("JSON Response : ", jsonObject.toString());
            
            jsonArray = jsonObject.getJSONArray(Constant.TAG_DETAILS);
            Log.d(TAG, "JSON Array : " + jsonArray);
            
            // looping through All Products
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);
                
                String id			= c.getString(Constant.ID);
                
                GroupListItem item = new GroupListItem();
                item.setId(id);
                
                arrayList.add(item);
            }            
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data : " + e.toString());
        }  
        return arrayList;
	}
	
	public ArrayList<FriendItem> getGroupFriendListMember() {
		
		ArrayList<FriendItem> arrayList = new ArrayList<FriendItem>();
		
        try {
        	jsonObject = new JSONObject(response);
            Log.d("JSON Response : ", jsonObject.toString());
            
            jsonArray = jsonObject.getJSONArray(Constant.TAG_DETAILS);
            Log.d(TAG, "JSON Array : " + jsonArray);
            
            // looping through All Products
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);
                
                String id			= c.getString(Constant.ID);
                String name			= c.getString(Constant.NAME);
                String lname		= c.getString(Constant.LNAME);
                String image		= c.getString(Constant.IMAGE);
                String status		= c.getString(Constant.STATUS);
                String type			= c.getString(Constant.TYPE);
                
                FriendItem item = new FriendItem();
                item.setId(id);
                item.setName(name);
                item.setLname(lname);
                item.setImage(image);
                item.setStatus(status);
                item.setType(type);
                
                arrayList.add(item);
            }   
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data : " + e.toString());
        }  
        return arrayList;
	}
	
	public ArrayList<FriendItem> getGroupFriendAndMemberList() {
		
		ArrayList<FriendItem> arrayList = new ArrayList<FriendItem>();
		
        try {
        	jsonObject = new JSONObject(response);
            Log.d("JSON Response : ", jsonObject.toString());
            
            jsonArray = jsonObject.getJSONArray(Constant.TAG_DETAILS);
            Log.d(TAG, "JSON Array : " + jsonArray);
            
            // looping through All Products
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);
                
                String id			= c.getString(Constant.ID);
                String name			= c.getString(Constant.NAME);
                String lname		= c.getString(Constant.LNAME);
                String image		= c.getString(Constant.IMAGE);
                String status		= c.getString(Constant.STATUS);
                boolean isChecked	= c.getBoolean(Constant.CHECK_STATE);
                
                FriendItem item = new FriendItem();
                item.setId(id);
                item.setName(name);
                item.setLname(lname);
                item.setImage(image);
                item.setStatus(status);
                item.setChecked(isChecked);
                
                arrayList.add(item);
            }   
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data : " + e.toString());
        }  
        return arrayList;
	}
	
	public ArrayList<StickerItem> getStickerList() {
		
		ArrayList<StickerItem> arrayList = new ArrayList<StickerItem>();
		
        try {
        	jsonObject = new JSONObject(response);
            Log.d("JSON Response : ", jsonObject.toString());
            
            jsonArray = jsonObject.getJSONArray(Constant.TAG_DETAILS);
            Log.d(TAG, "JSON Array : " + jsonArray);
            
            // looping through All Products
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);
                
                String id			= c.getString(Constant.ID);
                String image		= c.getString(Constant.IMAGE);
                String extension	= c.getString(Constant.EXT);
                String category		= c.getString(Constant.CATEGORY);
                
                StickerItem item = new StickerItem();
                item.setId(id);
                item.setImage(image);
                item.setExtension(extension);
                item.setCategory(category);                
                
                arrayList.add(item);
            }   
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data : " + e.toString());
        }  
        return arrayList;
	}
	
	public ArrayList<FriendItem> getLastSeen() {
		
		ArrayList<FriendItem> arrayList = new ArrayList<FriendItem>();
		
        try {
        	jsonObject = new JSONObject(response);
            //Log.d("JSON Response : ", jsonObject.toString());
            
            jsonArray = jsonObject.getJSONArray(Constant.TAG_DETAILS);
            //Log.d(TAG, "JSON Array : " + jsonArray);
            
            // looping through All Products
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);
                
                String lastSeen	= c.getString(Constant.LAST_SEEN);
                
                FriendItem item = new FriendItem();
                item.setLastSeen(lastSeen);
                
                arrayList.add(item);
            }            
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data : " + e.toString());
        }  
        return arrayList;
	}
	
}
