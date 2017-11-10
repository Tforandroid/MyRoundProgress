package application.forsister.tj.myroundprogress;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RoundProgressImage viewById = (RoundProgressImage) findViewById(R.id.item_home_iv1);
        viewById.setCurrentProgress(80);//进度
        viewById.startAnimation();
    }
}
