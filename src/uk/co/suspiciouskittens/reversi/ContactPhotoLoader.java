package uk.co.suspiciouskittens.reversi;

import java.io.IOException;
import java.io.InputStream;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract.Contacts;

public class ContactPhotoLoader {

	Bitmap photo;

	public Bitmap load(String id, ContentResolver cr) {
		Uri person = ContentUris.withAppendedId(Contacts.CONTENT_URI,
				Long.parseLong(id));
		InputStream photoStream = Contacts.openContactPhotoInputStream(cr,
				person);
		if (photoStream != null) {
			photo = BitmapFactory.decodeStream(photoStream);
			try {
				photoStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			return null;
		}
		return photo;

	}
}
