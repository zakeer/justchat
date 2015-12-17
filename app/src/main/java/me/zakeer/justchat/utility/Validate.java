package me.zakeer.justchat.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Build;
import android.telephony.PhoneNumberUtils;

public class Validate {
	
	private static final String TAG = "Validate";
	
	public Validate() {
		// Empty Constructor
	}
	
	public boolean emailValidator(String email) 
	{
	    Pattern pattern;
	    Matcher matcher;
	    final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	    pattern = Pattern.compile(EMAIL_PATTERN);
	    matcher = pattern.matcher(email);
	    return matcher.matches();
	}
	
	public boolean isValidEmail(String target) {
	    if (target.equals("")) {
	        return false;
	    } else {
	    	
	    	if(Build.VERSION.SDK_INT>=8)
	    	{
		    	if(android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches())
		    	{
		    		String topLevelDomain = target.substring(target.lastIndexOf(".") + 1);
		    		if(android.util.Patterns.TOP_LEVEL_DOMAIN.matcher(topLevelDomain).matches())
		    		{
		    			int targetLength = 8;
		    			if(isAtleastValidLength(target, targetLength))
		    				return true;
		    		}
		    	}
	    	}
	    	else
	    	{
	    		return isEmailValid(target);
	    	}
	    	return false;
	    }
	}
	
	/**
	 * method is used for checking valid email id format.
	 * 
	 * @param email
	 * @return boolean true for valid false for invalid
	 */
	private static boolean isEmailValid(String email) {
	    boolean isValid = false;

	    String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
	    CharSequence inputStr = email;

	    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(inputStr);
	    if (matcher.matches()) {
	        isValid = true;
	    }
	    return isValid;
	}
	
	public boolean isValidPhone(String target) {
	    if (target.equals("")) {
	        return false;
	    } else {
	    	if(PhoneNumberUtils.isGlobalPhoneNumber(target)) {
	    		if(target.length() > 8 && target.length() <= 10) {
	    			return true;
	    		}
	    		else {
	    			return false;
	    		}
	    	}
	    	else {
	    		return false;
	    	}	    		
	    }
	}
	
	public boolean isAtleastValidLength(String target, int targetLength) {
	    if (target.equals("")) {
	        return false;
	    } else {
	    	if(target.length()>=targetLength)
	    		return true;
	    	else
	    		return false;
	    }
	}
	
	public boolean isNotEmpty(String target) {
	    return isAtleastValidLength(target, 1);
	}
	
	public boolean isValueBetween(String val, int start, int end) {
		String tVal = val;
		
		if(tVal.contains("."))
		{
			val = tVal.split("\\.")[0];
		}
		
		int value = Integer.parseInt(val);
		if(value>=start && value<=end)
			return true;
		return false;
	}
	
	public static String convertEmail(String email) {
		
		String nmail = email.replace("@","_");		
		return nmail;
	}
}
