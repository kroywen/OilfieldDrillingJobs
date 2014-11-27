package com.wds.oilfieldDrillingJobs.storage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.wds.oilfieldDrillingJobs.util.Utilities;

public class UuidsStorage {
	
	@SuppressWarnings("unchecked")
	public static List<String> getUuids(Context context, String userUuid) {
		String filename = "cleared_uuids_" + userUuid;
		try {
			FileInputStream fis = context.openFileInput(filename);
			ObjectInputStream ois = new ObjectInputStream(fis);
			List<String> uuids = (List<String>) ois.readObject();
			ois.close();
			return uuids;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void addUuids(Context context, String userUuid, List<String> uuids) {
		List<String> prevUuids = getUuids(context, userUuid);
		List<String> newUuids = new ArrayList<String>();
		if (!Utilities.isEmpty(prevUuids)) {
			newUuids.addAll(prevUuids);
		}
		if (!Utilities.isEmpty(uuids)) { 
			if (!Utilities.isEmpty(newUuids)) { 
				for (String  uuid : uuids) {
					boolean found = false;
					for (String oldUuid : newUuids) {
						if (uuid.equals(oldUuid)) {
							found = true;
							break;
						}
					}
					if (!found) {
						newUuids.add(uuid);
					}
				}
			} else {
				newUuids.addAll(uuids);
			}
		}
		saveUuids(context, userUuid, newUuids);
	}
	
	public static void saveUuids(Context context, String userUuid, List<String> uuids) {
		try {
			
			String filename = "cleared_uuids_" + userUuid;
			FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(uuids);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
