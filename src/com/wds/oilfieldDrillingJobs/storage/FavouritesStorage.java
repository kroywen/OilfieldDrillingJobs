package com.wds.oilfieldDrillingJobs.storage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;

import com.wds.oilfieldDrillingJobs.model.Job;
import com.wds.oilfieldDrillingJobs.util.Utilities;

public class FavouritesStorage {
	
	@SuppressWarnings("unchecked")
	public static List<Job> getFavourites(Context context, String userUuid) {
		String filename = "favourites_" + userUuid;
		try {
			FileInputStream fis = context.openFileInput(filename);
			ObjectInputStream ois = new ObjectInputStream(fis);
			List<Job> favourites = (List<Job>) ois.readObject();
			ois.close();
			return favourites;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean isFavourite(Context context, String userUuid, Job job) {
		List<Job> favourites = getFavourites(context, userUuid);
		if (Utilities.isEmpty(favourites)) {
			return false;
		} else {
			for (Job favourite : favourites) {
				if (favourite.equals(job)) {
					return true;
				}
			}
			return false;
		}
	}
	
	public static void addFavourite(Context context, String userUuid, Job job) {
		List<Job> prevFavourites = getFavourites(context, userUuid);
		List<Job> newFavourites = new ArrayList<Job>();
		if (!Utilities.isEmpty(prevFavourites)) {
			newFavourites.addAll(prevFavourites);
		}
		if (job != null) {
			boolean found = false;
			for (Job favourite : newFavourites) {
				if (favourite.equals(job)) {
					found = true;
					break;
				}
			}
			if (!found) {
				newFavourites.add(job);
			}
		}
		saveFavourites(context, userUuid, newFavourites);
	}
	
	public static void removeFavourite(Context context, String userUuid, Job job) {
		List<Job> favourites = getFavourites(context, userUuid);
		if (!Utilities.isEmpty(favourites) && job != null) {
			Iterator<Job> i = favourites.iterator();
			while (i.hasNext()) {
				Job favourite = i.next();
				if (favourite.equals(job)) {
					i.remove();
					break;
				}
			}
		}
		saveFavourites(context, userUuid, favourites);
	}
	
	public static void saveFavourites(Context context, String userUuid, List<Job> favourites) {
		try {
			
			String filename = "favourites_" + userUuid;
			FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(favourites);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
