package mc.arct.intstationapp.activities;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import mc.arct.intstationapp.R;
import mc.arct.intstationapp.adapters.MyAdapterForAutoComplete;
import mc.arct.intstationapp.models.StationDetailVO;
import mc.arct.intstationapp.storage.MyPreferenceManager;
import mc.arct.intstationapp.storage.StationDAO;
import mc.arct.intstationapp.utils.IntentUtil;

/**
 * 入力画面
 */

public class Input extends AppCompatActivity {

    //入力欄を格納するリスト
    private ArrayList<AutoCompleteTextView> inputBoxList = new ArrayList<>();
    // 入力ボックス左右の線を格納するリスト
    private ArrayList<TextView> strokeList = new ArrayList<>();
    // 上記左右の線の反対側を隠すカバーを格納するリスト
    private ArrayList<TextView> strokeCoverList = new ArrayList<>();
    // 設定画面実行フラグ
    private boolean isSettings = false;
    // 全アクティビティで使えるアプリケーションクラス
    // プリファレンス管理クラス
    private MyPreferenceManager mpm;
    // 言語設定を保持する変数
    private String lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.s002_input);

        // プリファレンス管理クラスのインスタンスを取得
        mpm = new MyPreferenceManager(getApplicationContext());

        // XMLとの紐付け：1～10の入力ボックス
        for (int i = 1; i <= 10; i++) {
            inputBoxList.add((AutoCompleteTextView)findViewById(getResources().getIdentifier(
                    "inputBox" + String.valueOf(i), "id", getPackageName())));
        }
        // 1～9の左右線とカバー
        for (int i =1; i <= 9; i++) {
            strokeList.add((TextView)findViewById(getResources().getIdentifier(
                    "stroke" + String.valueOf(i), "id", getPackageName())));
            strokeCoverList.add((TextView)findViewById(getResources().getIdentifier(
                    "stroke" + String.valueOf(i) + "_cover", "id", getPackageName())));
        }
        for (AutoCompleteTextView textView : inputBoxList) {
            // 自分で定義したアダプターをビューに設定する
            MyAdapterForAutoComplete myAdapter = new MyAdapterForAutoComplete(getApplicationContext());
            textView.setAdapter(myAdapter);
            // 何文字目から予測変換を出すかを設定
            textView.setThreshold(1);
            // 改行ボタンでキーボードを閉じる設定(これをやらないとキーボードから次のテキストに進めない)
            textView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    return false;
                }
            });
        }
    }

    // 検索ボタンが押された時
    public void search(View v) {
        // 駅情報格納用VOのリスト
        ArrayList<StationDetailVO> stationList = new ArrayList<>();

        for (AutoCompleteTextView textView : inputBoxList) {

            // テキストが空だったら何もしない
            if (textView.getText().toString().isEmpty()) {
                // continueは今の処理は終了するがfor文自体は継続
                continue;
            }
            // 入力されたら駅名を取得
            String station = textView.getText().toString();
            // DB接続のためDAOを生成
            StationDAO dao = new StationDAO(getApplicationContext());
            // 駅情報を取得する
            StationDetailVO vo = dao.selectStationByName(station);
            // レコードが取得できなかった時は中断
            if (vo == null) {
                Toast.makeText(getApplicationContext(), station + getString(R.string.main_toast1), Toast.LENGTH_SHORT).show();
                return;
            }
            // 駅情報を格納したVOをリストに格納
            stationList.add(vo);
        }
        // 駅名が入力されていれば画面遷移へ
        if (!stationList.isEmpty()) {
            // プリファレンスからアニメの設定値を取得
            boolean animeFlag = mpm.getAnimeFlag();
            // アニメが有効なら検索中画面、無効なら直接検索画面へ遷移
            if (animeFlag) {
                // 画面遷移処理で駅情報のリストを次の画面に送る
                Intent intent = IntentUtil.prepareForSearching(Input.this, stationList);
                startActivity(intent);
            } else {
                // 画面遷移処理で、駅情報のリストを次の画面に送る
                Intent intent = IntentUtil.prepareForResult(Input.this, stationList);
                startActivity(intent);
            }
        } else {
            // 駅が入力されていなければ遷移せずに処理終了
            Toast.makeText(getApplicationContext(), getString(R.string.main_toast2), Toast.LENGTH_SHORT).show();
        }
    }

    // クリアボタンが押された時呼ばれる
    public void clearAll(View v) {
        // 入力されたテキストを全て削除
        for (AutoCompleteTextView textView : inputBoxList) {

            textView.setText("");
        }
    }

    // 設定ボタンが押された時呼ばれる


    /**
     * 引数に与えたXY座標の位置にターゲットを移動させる
     * ※基準の0は最初に動かす前のView位置
     *
     * @param target 移動対象のView
     * @param fromX 移動前X座標
     * @param toX 移動後X座標
     * @param fromY 移動前Y座標
     * @param toY 移動後Y座標
     */
    private void moveTarget(View target, float fromX, float toX, float fromY, float toY) {

        // translationXプロパティをOfからtoXに変化させる
        PropertyValuesHolder holderX = PropertyValuesHolder.ofFloat("translationX", fromX, toX);
        // translationYプロパティをOfからtoYに変化させる
        PropertyValuesHolder holderY = PropertyValuesHolder.ofFloat("translationY", fromY, toY);
        // targetに対してholderX、holderYを同時に実行させる
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(
                target, holderX, holderY);
        // animation時間 msec
        objectAnimator.setDuration(300);
        objectAnimator.start();
    }

    // ボックス追加ボタン(踏切)が押された時
    public void addInputBox(View v) {
        for (AutoCompleteTextView view : inputBoxList) {
            // 1～10の入力ボックスを確認して、GONEを見つけたらVISIBLEにする
            if (view.getVisibility() == View.GONE) {
                view.setVisibility(View.VISIBLE);
                // ひとつVISIBLEにしたら(表示されるビューが一つ増える)、すぐにfor文のループ終了
                break;
            }
        }
        for (TextView view : strokeList) {
            // 1～9の左右の線を確認して、GONEを見つけたらVISIBLEにする
            if (view.getVisibility() == View.GONE) {
                view.setVisibility(View.VISIBLE);
                // ひとつVISIBLEにしたら(表示されるビューが一つ増える)、すぐにfor文のループ終了
                break;
            }
        }
        for (TextView view : strokeCoverList) {
            // 1～9の左右線のカバーを確認して、GONEを見つけたらVISIBLEにする
            if (view.getVisibility() == View.GONE) {
                view.setVisibility(View.VISIBLE);
                // ひとつVISIBLEにしたら(表示されるビューが一つ増える)、すぐにfor文のループ終了
                break;
            }
        }
    }

}
