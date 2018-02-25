package mc.arct.intstationapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import mc.arct.intstationapp.R;
import mc.arct.intstationapp.adapters.MyAdapterForListView;
import mc.arct.intstationapp.models.StationDetailVO;
import mc.arct.intstationapp.models.StationDistanceVO;
import mc.arct.intstationapp.network.MyNetworkManager;
import mc.arct.intstationapp.storage.StationDAO;
import mc.arct.intstationapp.utils.CalculateUtil;
import mc.arct.intstationapp.utils.IntentUtil;

/**
 * 候補駅
 */

public class Suggested extends AppCompatActivity{

    private ArrayList<StationDetailVO> stationList = new ArrayList<>();
    private ArrayList<StationDistanceVO> stationDistanceList = new ArrayList<>();
    public static int FVP = 0;
    public static int y = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.s011_suggested_station);

        // 前画面から駅情報リストを受け取る
        Intent intent = getIntent();
        stationList = (ArrayList<StationDetailVO>) intent.getSerializableExtra("stationList");
        double centerLat = intent.getDoubleExtra("centerLat", 0);
        double centerLng = intent.getDoubleExtra("centerLng", 0);
        LatLng centerLatLng = new LatLng(centerLat, centerLng);

        // ここではソートされた駅情報をリストごと取得
        stationDistanceList
                = CalculateUtil.calcNearStationsList(centerLatLng, getApplicationContext());
        // 1000件以上もいらないので上位10件以外削除
        stationDistanceList.subList(10, stationDistanceList.size()).clear();

        // AdapterでListをListViewと紐付ける
        ListView listView = findViewById(R.id.list_view);
        MyAdapterForListView myAdapter = new MyAdapterForListView(this, stationDistanceList);
        listView.setAdapter(myAdapter);
        // 記憶してあったリストの位置を取得
        listView.setSelectionFromTop(FVP, y);
        // リストビュー内のボタンが押された時の処理
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 画面遷移処理で、入力されていた駅情報のリストと候補駅を次の画面に送る
                switch (view.getId()) {
                    case R.id.button12:
                        // LINE連携処理を呼び出す
                        callLINE(stationDistanceList.get(position).getName());
                        break;
                    case R.id.button13:
                        // MAPへ画面遷移させる
                        callMapInfo(stationDistanceList.get(position).getName());
                        break;
                }
            }
        });
    }

    // 共有ボタンが押された時
    public void callLINE(String stationName) {
        // DB接続のためDAOを生成
        StationDAO dao = new StationDAO(getApplicationContext());
        // 駅情報を取得する
        StationDetailVO resultStation = dao.selectStationByName(stationName);
        // LINE共有機能を呼び出す
        Object obj = IntentUtil.prepareForLINE(this, stationList, resultStation);
        // Intentが返却されていたら、LINE連携へ遷移する
        if (obj instanceof Intent) {
            Intent intent = (Intent)obj;
            startActivity(intent);
            // AlertDialog.Builderが返却されていたら、遷移せずダイアログを表示
        } else if (obj instanceof AlertDialog.Builder) {
            AlertDialog.Builder dialog = (AlertDialog.Builder)obj;
            dialog.show();
        }
    }

    // 周辺情報ボタンが押された時
    public void callMapInfo(String stationName) {
        // ネットワークの接続状態を確認
        AlertDialog.Builder dialog = MyNetworkManager.checkConnection(this);
        if (dialog == null) {
            // DB接続のためDAOを生成
            StationDAO dao = new StationDAO(getApplicationContext());
            // 駅情報を取得する
            StationDetailVO resultStation = dao.selectStationByName(stationName);
            // 周辺情報(MAP)の画面に遷移
            Intent intent = IntentUtil.prepareForArea(
                    Suggested.this, stationList, resultStation);
            startActivity(intent);
        } else {
            // 電波がなかったらダイアログを出す
            dialog.show();
        }
    }

}
