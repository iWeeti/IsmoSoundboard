package xyz.weetisoft.ismosoundboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ismosoundboard.R;

import java.util.List;

public class SoundAdapter extends RecyclerView.Adapter<SoundAdapter.ViewHolder> {

    private List<Sound> localDataset;
    private FragmentManager fragmentManager;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageButton playButton;
        private final TextView textView;

        public ViewHolder(View view) {
            super(view);

            textView = (TextView) view.findViewById(R.id.textView);
            playButton = (ImageButton) view.findViewById(R.id.playButton);
        }

        public ImageButton getPlayButton() {
            return playButton;
        }

        public TextView getTextView() {
            return textView;
        }
    }

    public SoundAdapter(List<Sound> localDataset, FragmentManager fragmentManager) {
        this.localDataset = localDataset;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.sound_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.getTextView().setText(localDataset.get(position).toString());
        holder.itemView.setOnLongClickListener(longClickListener(position));
        holder.getPlayButton().setOnLongClickListener(longClickListener(position));
        holder.getPlayButton().setOnClickListener((view) -> {
            Sound sound = localDataset.get(position);
            if (sound.isPlaying()) {
                sound.stop();
                return;
            }
            holder.getPlayButton().setImageResource(R.drawable.stop_button);
            sound.play((mediaPlayer) -> {
                holder.getPlayButton().setImageResource(R.drawable.play_button);
            });
        });
    }

    @NonNull
    private View.OnLongClickListener longClickListener(int position) {
        return (view) -> {
            Sound sound = localDataset.get(position);
            DialogFragment fragment = new SoundDialog(sound.resId, sound.soundResId);
            fragment.show(fragmentManager, "sound");

            return false;
        };
    }

    @Override
    public int getItemCount() {
        return localDataset.size();
    }

}
