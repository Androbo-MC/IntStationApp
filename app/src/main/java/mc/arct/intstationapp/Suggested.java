package mc.arct.intstationapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import mc.arct.intstationapp.models.StationDetailVO;
import mc.arct.intstationapp.models.StationDistanceVO;
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
        setContentView(R.layout.s011_suggested_lst);

        // アプリケーションクラスのインスタンスを取得(今回は無し)

        // 全画面から駅情報リストを受け取る
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
    }

    private void btnLINEOnClick (String stationName){
        Object object = IntentUtil.prepareForLINE(this,stationName);
        if (object instanceof Intent){
            Intent intent = (Intent)object;
            startActivity(intent);
        }
        else{
            // todo:dialog
        }

    }

    private void btnAreaOnClick (String stationName){
        StationDAO dao = new StationDAO(getApplicationContext());
        StationDetailVO vo = dao.selectStationByName(stationName);
        Intent intent = IntentUtil.prepareForMapsActivity(Suggested.this, stationList, vo);
        startActivity(intent);
    }
}
