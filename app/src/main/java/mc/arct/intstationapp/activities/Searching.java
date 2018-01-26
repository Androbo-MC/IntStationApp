package mc.arct.intstationapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import java.util.ArrayList;
import mc.arct.intstationapp.R;
import mc.arct.intstationapp.models.StationDetailVO;
import mc.arct.intstationapp.utils.IntentUtil;

/**
 * 検索中画面クラス
 */

public class Searching extends AppCompatActivity {

    // 入力情報リスト
    private ArrayList<StationDetailVO> stationList;
    // 遅延処理用
    Handler handler;
    Runnable r;
    // 全アクティビティで使えるアプリケーションクラス　※当面は無し
    // 停止させる時に使用するため宣言しておく
    private int streamId1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.s003_searching);
        // アプリケーションクラスのインスタンスを取得　※当面は無し
        // 遷移前画面から入力駅情報リストを受け取る
        Intent intent = getIntent();
        stationList =
                (ArrayList<StationDetailVO>) intent.getSerializableExtra("result");
        // キャラクターを貼っているビューを取得
        ImageView searchingTrain = findViewById(R.id.searching_train);
        // 拡大アニメーションの設定(キャラクターの拡大)
        // pivotを両方0.5fにすると拡大の起点がViewの中心からになる
        ScaleAnimation scale = new ScaleAnimation(
                0.0f,5.0f, 0.0f, 5.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        // 移動アニメーションの設定(左から右へ移動)
        TranslateAnimation ta = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, -0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        // AnimationSetを使いアニメーションを合成する(複数同時に使う時はこれにまとめる)
        AnimationSet animeSet = new AnimationSet(false);
        animeSet.addAnimation(scale);
        animeSet.addAnimation(ta);
        // 3秒かけてアニメーションする
        animeSet.setDuration(3000);
        // animationが終わったそのまま表示にする(元位置に戻らない)
        animeSet.setFillAfter(true);
        //アニメーションの開始
        searchingTrain.startAnimation(animeSet);
        // 3秒遅延させて(アニメ終了時に)Handlerを実行
        handler = new Handler();
        r = new Runnable(){
            public void run() {
                // 画面遷移処理で、駅情報のリストを次の画面に送る
                Intent intent = IntentUtil.prepareForResult(Searching.this, stationList);
                startActivity(intent);
                // Searching終了
                Searching.this.finish();
            }
        };
        handler.postDelayed(r, 3000);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // アクティビティが落ちる時に遅延処理も中断させる
        // (これやんないとバックボタン押して戻っても検索結果画面に遷移しちゃう)
        handler.removeCallbacks(r);
    }
}
