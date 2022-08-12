package com.example.ismosoundboard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class SoundDialog extends DialogFragment {

    private int soundName;
    private int soundFile;

    public SoundDialog(int selectedSound, int soundFile) {
        this.soundName = selectedSound;
        this.soundFile = soundFile;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(soundName);

        SharedPreferences prefs = getActivity().getSharedPreferences("favorites", Context.MODE_PRIVATE);
        final Set<String>[] favorites = new Set[]{prefs.getStringSet("favorites", new HashSet<>())};
        boolean inFavorites = favorites[0].contains(getResources().getResourceEntryName(soundFile));


        builder.setItems(new CharSequence[]{
                getResources().getString(R.string.set_as_ringtone),
                getResources().getString(R.string.set_as_alarm),
                getResources().getString(R.string.set_as_notification),
                inFavorites ? getResources().getString(R.string.remove_from_favorites) : getResources().getString(R.string.add_to_favorites)
        }, (dialog, pos) -> {
            switch (pos) {
                case 0:
                    setRingtone(RingtoneManager.TYPE_RINGTONE);
                    break;
                case 1:
                    setRingtone(RingtoneManager.TYPE_ALARM);
                    break;
                case 2:
                    setRingtone(RingtoneManager.TYPE_NOTIFICATION);
                    break;
                case 3:
                    if (!inFavorites)
                        favorites[0].add(getResources().getResourceEntryName(soundFile));
                    else {
                        HashSet<String> temp = new HashSet<>();
                        favorites[0].forEach(s -> {
                            if (!s.equals(getResources().getResourceEntryName(soundFile)))
                                temp.add(s);
                        });
                        favorites[0] = (Set<String>) temp;
                    }
                    favorites[0].forEach((s) -> {
                        Log.d("Sound Dialog", s);
                    });
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.clear();
                    editor.putStringSet("favorites", favorites[0]);
                    editor.apply();
                    Toast.makeText(getContext(), inFavorites ? R.string.removed_from_favorites : R.string.added_to_favorites, Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        return builder.create();
    }

    private void setRingtone(int ringtoneType) {
        File folder = Environment.getExternalStorageDirectory();
        TypedValue value = new TypedValue();
        getResources().getValue(soundFile, value, true);
        String[] s = value.string.toString().split("/");
        String fileName = s[s.length - 1];
//            Uri sound = Uri.parse(String.format(
//                    "android.resource://%s/raw/%s",
//                    getContext().getPackageName(),
//                    fileName
//            ));
        File outFolder = new File(Environment.getExternalStorageDirectory(), "Ringtones");
        if (!outFolder.exists())
            outFolder.mkdirs();
        File outFile = new File(outFolder, fileName);
//            File outFile = new File(String.format("/media/audio/ringtones/%s", fileName));

        Log.d("Path:",outFile.getPath());
        if (!outFile.exists()){
            InputStream in = null;
            try {
//                    in = new FileInputStream(sound.getPath());
                in = getResources().openRawResource(soundFile);
                outFile.createNewFile();
                FileOutputStream out = new FileOutputStream(outFile);
                int b;
                while ((b = in.read()) != -1) {
                    out.write(b);
                }
                out.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (!Settings.System.canWrite(getContext())){
            Toast.makeText(getContext(), "Please allow the app to change system settings to set the ringtone and try again.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:"+getContext().getPackageName()));
            startActivity(intent);
            return;
        }

        RingtoneManager.setActualDefaultRingtoneUri(getContext(), ringtoneType, Uri.parse(outFile.getAbsolutePath()));

        int stringId = -1;
        switch (ringtoneType) {
            case RingtoneManager.TYPE_RINGTONE:
                stringId = R.string.set_ringtone;
                break;
            case RingtoneManager.TYPE_ALARM:
                stringId = R.string.set_alarm;
                break;
            case RingtoneManager.TYPE_NOTIFICATION:
                stringId = R.string.set_notification;
                break;
        }
        Toast.makeText(getContext(), stringId, Toast.LENGTH_SHORT).show();
    }
}
