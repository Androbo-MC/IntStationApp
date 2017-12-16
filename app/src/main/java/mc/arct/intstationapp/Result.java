package mc.arct.intstationapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;

import mc.arct.intstationapp.models.StationDetailVO;
import mc.arct.intstationapp.models.StationDistanceVO;
import mc.arct.intstationapp.storage.StationDAO;
import mc.arct.intstationapp.utils.CalculateUtil;

/**
 * 検索結果
 */

public class Result extends AppCompatActivity{

    private ArrayList<StationDetailVO> stationList;
    private StationDetailVO resultStation;
    // ここに後でLatLngを入れる
    private TextView searchResult;
    // タスクを実行するタイマー
    private Timer timer;
    Handler handler;
    // 点滅状態を保持する
    private boolean isBlink = false;
    // 全アクティビティで使えるアプリケーションクラス（今回は無し）
    // 文字スクロール制御フラグ
    private boolean is_scroll = false;
    // ピンチアウト(2本指での拡大)に対応させるための定義
    ConstraintLayout layout;
    private float scale = 1f;
    private ScaleGestureDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.s004_result);

        // ここでID取得したレビュー(今回はレイアウト)が、ピンチアウトの対象になる
        layout = findViewById(R.id.result_train);
        // ここに後でdetectorを入れる

        // アプリケーションクラスのインスタンスを取得（今回は無し）

        // 遷移前画面から入力駅情報リストを受け取る
        Intent intent = getIntent();
        stationList =
                (ArrayList<StationDetailVO>) intent.getSerializableExtra("result");
        // 計算用の緯度と経度のリスト
        ArrayList<Double> latList = new ArrayList<>();
        ArrayList<Double> lngList = new ArrayList<>();
        // 緯度経度を駅情報リストから計算用のリストに格納
        for (StationDetailVO vo : stationList) {

            latList.add(Double.parseDouble(vo.getLat()));
            lngList.add(Double.parseDouble(vo.getLng()));
        }
        // 中間地点座標の取得

        // 中間地点から近い座標にある駅を調べる

        // DB接続のためDAOを生成
        StationDAO dao = new StationDAO(getApplicationContext());
        // 一番先頭にあるVOの駅情報を取得して返却

        // 駅名を表示
        searchResult = findViewById(R.id.staNameTextBox);
        // Touchモード時にViewがフォーカスを取得可能か設定(文字スクロールに必要)
        searchResult.setFocusableInTouchMode(true);
        searchResult.setText(resultStation.getName());
        // 結果の駅名をタップすると、表示しきれないときは文字列スクロールの切り替え
        searchResult.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (is_scroll) {
                            // 文字列を表示し切れないときには後ろを省略(元々はこれ)
                            searchResult.setEllipsize(TextUtils.TruncateAt.END);
                            is_scroll = false;
                        } else {
                            // 文字列を表示し切れないときにはスクロールする
                            searchResult.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                            is_scroll = true;
                        }
                    }
                }
        );
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // アクティビティが落ちる時にタイマーも終了させる
        timer.cancel();
    }

    // 共有ボタンが押された時

}
