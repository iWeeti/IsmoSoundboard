package xyz.weetisoft.ismosoundboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ismosoundboard.R;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class SoundsFragment extends Fragment {
    ArrayList<Sound> sounds;

    public SoundsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_activity, container, false);


        sounds = new ArrayList<>();
        Field[] fields = R.raw.class.getFields();

        for (Field field : fields) {
            sounds.add(new Sound(getContext(), field.getName()));
        }

        RecyclerView recyclerView = rootView.findViewById(R.id.list);
        SoundAdapter soundAdapter = new SoundAdapter(sounds, getParentFragmentManager());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(soundAdapter);

        return rootView;
    }
}
