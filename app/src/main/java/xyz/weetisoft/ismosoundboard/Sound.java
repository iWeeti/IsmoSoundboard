package xyz.weetisoft.ismosoundboard;

import android.content.Context;
import android.content.res.Resources;
import android.media.MediaPlayer;

import com.example.ismosoundboard.R;

public class Sound {
    public String filename;
    public String listName;
    public int resId;
    public int soundResId;

    private MediaPlayer mediaPlayer;
    private Context ctx;
    private Thread thread;
    private MediaPlayer.OnCompletionListener onCompletionListener;

    public Sound(Context ctx, String filename) {
        this.ctx = ctx;
        this.filename = filename;
        Resources res = ctx.getResources();
        resId = res.getIdentifier(filename, "string", res.getResourcePackageName(R.string.ah));
        soundResId = res.getIdentifier(filename, "raw", ctx.getPackageName());
        listName = res.getString(resId);
    }

    public void play(MediaPlayer.OnCompletionListener onCompletionListener) {
        this.onCompletionListener = onCompletionListener;
        thread = new Thread(() -> {
            mediaPlayer = MediaPlayer.create(ctx, soundResId);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener((player) -> {
                onCompletionListener.onCompletion(player);
            });
        });
        thread.start();
    }

    public void stop() {
        try {
            thread.join();
            mediaPlayer.stop();
            onCompletionListener.onCompletion(mediaPlayer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isPlaying() {
        if (mediaPlayer == null) return false;
        return mediaPlayer.isPlaying();
    }

    public String toString(){
        return listName;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
}
