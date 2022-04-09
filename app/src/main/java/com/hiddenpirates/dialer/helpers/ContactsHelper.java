package com.hiddenpirates.dialer.helpers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class ContactsHelper {

    public static String getContactNameByPhoneNumber(String phone_number, Context context){

        Uri uri=Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(phone_number));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        String contactName = "";

        Cursor cursor = context.getContentResolver().query(uri,projection,null,null,null);

        if (cursor != null) {
            if(cursor.moveToFirst()) {
                contactName = cursor.getString(0);
            }
            cursor.close();
        }

        if (contactName.equals("")){
            contactName = Constant.HP_UNKNOWN_CALLER_NAME;
        }

        return contactName;
    }
}
