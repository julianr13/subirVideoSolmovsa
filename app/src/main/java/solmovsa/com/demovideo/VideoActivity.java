package solmovsa.com.demovideo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Intent intMain = getIntent();

        Uri vidFile = Uri.parse(intMain.getStringExtra("path"));
        VideoView videoView = (VideoView) findViewById(R.id.videoView);
        videoView.setVideoURI(vidFile);
        videoView.setMediaController(new MediaController(this));
        videoView.start();
    }


}
