package mc.arct.intstationapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import mc.arct.intstationapp.R;
import mc.arct.intstationapp.utils.IntentUtil;

/**
 * 起動画面
 */
public class StartUp extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // タイトルを非表示にする
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // s001_startup.xmlをViewに指定する
        setContentView(R.layout.s001_startup);
        // ロゴを貼ってあるビューを取得
        LinearLayout titleLogo = findViewById(R.id.title_logo);
        // アニメーションの設定(画面左から移動してくる)
        TranslateAnimation ta = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, -2.5f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        // animation時間 msec
        ta.setDuration(1000);
        // 繰り返し回数
        ta.setRepeatCount(0);
        // animationが終わったらそのまま表示する
        ta.setFillAfter(true);
        // アニメーションの開始
        titleLogo.startAnimation(ta);
        // 1.5秒遅延させてHandlerを実行する
        Handler hdl = new Handler();
        hdl.postDelayed(new Runnable() {
            public void run() {
                // スプラッシュ完了後に実行するActivityを指定する
                Intent intent = IntentUtil.prepareForInput(StartUp.this);
                startActivity(intent);
                // StartUpを終了させる
                StartUp.this.finish();
            }
        },1500);
    }
}
