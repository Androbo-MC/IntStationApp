package mc.arct.intstationapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import mc.arct.intstationapp.R;
import mc.arct.intstationapp.models.StationDetailVO;
import mc.arct.intstationapp.utils.CalculateUtil;
import mc.arct.intstationapp.utils.IntentUtil;

/**
 * 周辺情報画面
 */
public class Area extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerDragListener {

    private ArrayList<StationDetailVO> stationList;
    private StationDetailVO resultStation;
    private LatLng centerLatLng;
    private ArrayList<Double> latList = new ArrayList<>();
    private ArrayList<Double> lngList = new ArrayList<>();
    private ArrayList<LatLng> latLngList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.s005_area);

        // 遷移前画面から駅情報リストと候補駅を受け取る
        Intent intent = getIntent();
        stationList =
                (ArrayList<StationDetailVO>) intent.getSerializableExtra("stationList");
        resultStation = (StationDetailVO) intent.getSerializableExtra("resultStation");

        // 緯度経度を計算用のリストに格納
        for (StationDetailVO vo : stationList) {
            latList.add(Double.parseDouble(vo.getLat()));
            lngList.add(Double.parseDouble(vo.getLng()));
        }
        // マップ使用準備
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    // マップ使用準備が完了したら呼ばれる
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // マップオブジェクトを受け取る
        GoogleMap map = googleMap;

        for (int i = 0; i < stationList.size(); i++) {
            // 取得した座標の数だけピンをセットする
              latLngList.add(new LatLng(latList.get(i), lngList.get(i)));
              map.addMarker(new MarkerOptions().position(latLngList.get(i))
                      .title(stationList.get(i).getName() + "駅"));
        }
        // 中間地点座標の取得
        centerLatLng = CalculateUtil.calcCenterLatLng(latList, lngList);
        // 候補駅の座標を取得
        LatLng resultStationLatLng = new LatLng(Double.parseDouble(resultStation.getLat()),
                Double.parseDouble(resultStation.getLng()));
        // 最大距離の取得
        double[] maxDistance = CalculateUtil.calcMaxDistance(latList, lngList);
        double maxDistanceLat = maxDistance[0];
        double maxDistanceLng = maxDistance[1];
        // 最大距離に応じてズーム具合を調整する
        int zoomLevel = 0;
        Log.d("lat", String.valueOf(maxDistanceLat));
        Log.d("lng", String.valueOf(maxDistanceLng));
        // 2～21で大きいほどズーム
        if (maxDistanceLat <= 0.03 && maxDistanceLng <= 0.03) {
            zoomLevel = 14;
        } else if (maxDistanceLat <= 0.06 && maxDistanceLng <= 0.06) {
            zoomLevel = 13;
        } else if (maxDistanceLat <= 0.1 && maxDistanceLng <= 0.1) {
            zoomLevel = 12;
        } else if (maxDistanceLat <= 0.2 && maxDistanceLng <= 0.2) {
            zoomLevel = 11;
        } else if (maxDistanceLat <= 0.4 && maxDistanceLng <= 0.4) {
            zoomLevel = 10;
        } else if (maxDistanceLat <= 0.8 && maxDistanceLng <= 0.8) {
            zoomLevel = 9;
        } else {
            zoomLevel = 8;
        }
        // 候補駅に色違いのピンをセットして情報ウィンドウも設定する
        Marker stationMarker = map.addMarker(new MarkerOptions().position(resultStationLatLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title(resultStation.getName() + "駅！"));
        // 情報ウィンドウを表示
        stationMarker.showInfoWindow();
        // 中間地点に色違いのピンをセットして情報ウィンドウも設定する
        map.addMarker(new MarkerOptions().position(centerLatLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                .title("中間地点！"));
        // フォーカスを当てるマーカーを設定
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng, zoomLevel));
        // 情報ウィンドウのリスナーをセット
        map.setOnInfoWindowClickListener(this);
        // ドラッグのリスナーをセット
        map.setOnMarkerDragListener(this);
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        // ドラッグが終わったら中間地点の座標をずらす
        centerLatLng = marker.getPosition();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        // 情報ウインドウがタップされた時の処理
    }

    // todo:共有ボタンが押された時
    // ジャンルボタンが押された時
    public void callGenre(View v) {
        // 各ジャンルを配列に格納
        final String[] items = {"レストラン", "居酒屋", "カフェ", "コンビニ", "カラオケ"};
        // デフォルトでチェックされているアイテム
        int defaultItem = 0;
        final ArrayList<Integer> checkedItems = new ArrayList<>();
        // 最初にデフォルトリストに追加しておく
        checkedItems.add(defaultItem);
        new AlertDialog.Builder(this)
                .setTitle("ジャンル選択")
                // ラジオボタンの設定
                .setSingleChoiceItems(items, defaultItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // クリックされたら、今ある番号をクリアして新しい番号を格納
                        checkedItems.clear();
                        checkedItems.add(which);
                    }
                })
                // OKボタンの設定
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (checkedItems.isEmpty()) {
                            // ジャンルが決定されたら、外部連携のURL情報を取得
                            Intent intent = IntentUtil.prepareForExternalInfo(
                                    resultStation, checkedItems.get(0));
                            // ブラウザを起動する
                            startActivity(intent);
                        }
                    }
                })
                // キャンセルボタンの設定
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!checkedItems.isEmpty()) {
                        }
                    }
                })
                .show();
    }
}
