package work.pengzhe.com.waterripplesplash;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {

    SplashView splashView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        FrameLayout frameLayout = new FrameLayout(this);
        ContentView contentView = new ContentView(this);
        frameLayout.addView(contentView);
        splashView = new SplashView(this);
        frameLayout.addView(splashView);
        setContentView(frameLayout);

        startLoadData();
    }

    Handler handler = new Handler();

    private void startLoadData() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                splashView.disppear();
            }
        }, 2000);
    }
}
