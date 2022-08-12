package xyz.weetisoft.ismosoundboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ismosoundboard.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FavoritesFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    ArrayList<Sound> sounds;
    Set<String> favorites;

    private View rootView;
    public FavoritesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_activity, container, false);
        SharedPreferences prefs = getActivity().getSharedPreferences("favorites", Context.MODE_PRIVATE);
        favorites = prefs.getStringSet("favorites", new HashSet<>());
        favorites.forEach((s) -> {
            Log.d("favs", s);});

        sounds = new ArrayList<>();
        Field[] fields = R.raw.class.getFields();

        for (Field field : fields) {
            if (favorites.contains(field.getName()))
                sounds.add(new Sound(getContext(), field.getName()));
        }
        RecyclerView recyclerView = rootView.findViewById(R.id.list);
        SoundAdapter soundAdapter = new SoundAdapter(sounds, getParentFragmentManager());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(soundAdapter);

        return rootView;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String s) {
        if (s != "favorites") return;
        favorites = prefs.getStringSet("favorites", new HashSet<>());


        sounds = new ArrayList<>();
        Field[] fields = R.raw.class.getFields();

        for (Field field : fields) {
            if (favorites.contains(field.getName()))
                sounds.add(new Sound(getContext(), field.getName()));
        }

        RecyclerView recyclerView = rootView.findViewById(R.id.list);
        SoundAdapter soundAdapter = new SoundAdapter(sounds, getParentFragmentManager());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(soundAdapter);
    }
}
