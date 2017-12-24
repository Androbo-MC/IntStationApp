package mc.arct.intstationapp.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.util.ArrayList;

import mc.arct.intstationapp.Area;
import mc.arct.intstationapp.Input;
import mc.arct.intstationapp.Result;
import mc.arct.intstationapp.Route;
import mc.arct.intstationapp.Suggested;
import mc.arct.intstationapp.models.StationDetailVO;
import mc.arct.intstationapp.models.StationTransferVO;
import mc.arct.intstationapp.models.StationVO;

import static java.lang.Character.LINE_SEPARATOR;

/**
 * 画面遷移準備共通クラス
 */

public class IntentUtil {

    /**
     * 入力画面への遷移準備
     *
     * @param context コンテキスト(実行中ActivityのthisでOK)
     * @return Intent 画面遷移に必要な情報を保持したIntent
     */
    public static Intent prepareForInput(Context context) {

        // メイン画面には何も送らないでそのまま返却
        return new Intent(context, Input.class);
    }

    /**
     * 検索結果画面への遷移準備
     *
     * @param activity 実行中のActivityのthis
     * @param stationList 入力されていた駅情報のリスト
     * @return Intent 画面遷移に必要な情報を保持したIntent
     */
    public static Intent prepareForResult(Activity activity, ArrayList<StationDetailVO> stationList) {

        // 入力されていた駅情報のリストを次の画面に送る準備
        Intent intent = new Intent(activity, Result.class)
                .putExtra("result", stationList);
        return intent;
    }

    /**
     * 周辺情報画面への遷移準備
     *
     * @param activity 実行中Activityのthis
     * @param stationList 入力されていた駅情報のリスト
     * @param resultVO 検索結果として選ばれた候補の駅
     * @return Intent 画面遷移に必要な情報を保持したIntent
     */
    public static Intent prepareForArea(Activity activity, ArrayList<StationDetailVO> stationList, StationDetailVO resultVO) {

        // 入力されていた駅情報のリストと候補駅を次の画面に送る準備
        Intent intent = new Intent(activity, Area.class)
                .putExtra("stationList", stationList)
                .putExtra("resultStation", resultVO);
        return intent;
    }

    /**
     * 候補駅画面への遷移準備
     *
     * @param activity 実行中のActivityのthis
     * @param stationList 入力されていた駅情報のリスト
     * @param centerLat 計算された中間地点の緯度
     * @param centerLng 計算された中間地点の経度
     * @return Intent 画面遷移に必要な情報を保持したIntent
     */
    public static Intent prepareForSuggested(Activity activity, ArrayList<StationDetailVO> stationList,
                                             double centerLat, double centerLng) {
        // 入力されていた駅情報のリストと中間地点座標を次の画面に送る準備
        Intent intent = new Intent(activity, Suggested.class)
                .putExtra("stationList", stationList)
                .putExtra("centerLat", centerLat)
                .putExtra("centerLng", centerLng);
        return intent;
    }

    /**
     * ルート画面への遷移準備
     *
     * @param activity 実行中Activityのthis
     * @param stationList 入力されていた駅情報のリスト
     * @param resultStation 検索結果として選ばれた候補の駅
     * @param centerLat 計算された中間地点の緯度
     * @param centerLng 計算された中間地点の経度
     * @param resultInfoLists Jorudan検索結果の経路を格納したVOのリストの配列
     * @return Intent 画面遷移に必要な情報を保持したIntent
     */
    public static Intent prepareForRoute(Activity activity, ArrayList<StationDetailVO> stationList,
                                         StationDetailVO resultStation, double centerLat, double centerLng,
                                         ArrayList<StationTransferVO>[] resultInfoLists) {

        // 入力されていた駅情報のリストと候補駅とJorudan情報を次の画面に送る準備
        Intent intent = new Intent(activity, Route.class)
                .putExtra("stationList", stationList)
                .putExtra("resultStation", resultStation)
                .putExtra("centerLat", centerLat)
                .putExtra("centerLng", centerLng)
                .putExtra("resultInfoLists", resultInfoLists);
        // 端末によってリストの配列がintentで正しく送れなかったので、要素を1個ずつ送る仕様に変更
        for (int i = 0; i < resultInfoLists.length; i++) {
            intent.putExtra("resultInfoList" + i, resultInfoLists[i]);
        }
        intent.putExtra("count", resultInfoLists.length);

        return intent;
    }

    /**
     * 外部連携(ブラウザ)への遷移準備
     *
     * @param resultStation 検索結果として選ばれた候補の駅
     * @param genre 選択されたジャンル
     * @return Intent 画面遷移(ブラウザ起動)に必要な情報を保持したIntent
     */
    public static Intent prepareForExternalInfo(StationDetailVO resultStation, int genre) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        // ジャンル別で接続先を分ける
        switch (genre){
            // レストラン→ぐるナビ
            case 0:
                intent.setData(Uri.parse("https://r.gnavi.co.jp/eki/"
                        + resultStation.getGnaviId() + "/rs/"));
                break;
            // 居酒屋→ぐるナビ
            case 1:
                intent.setData(Uri.parse("https://r.gnavi.co.jp/eki/"
                        + resultStation.getGnaviId() + "/izakaya/rs/"));
                break;
            // カフェ→ぐるナビ
            case 2:
                intent.setData(Uri.parse("https://r.gnavi.co.jp/eki/"
                        + resultStation.getGnaviId() + "/cafe/rs/"));
                break;
            // コンビニ→グーグルマップ
            case 3:
                intent.setData(Uri.parse("https://www.google.co.jp/maps/search/コンビニ/@"
                        + resultStation.getLat() + "," + resultStation.getLng() + "," + "15z")); // 15zはズーム具合
                break;
            // カラオケ→グーグルマップ
            case 4:
                intent.setData(Uri.parse("https://www.google.co.jp/maps/search/カラオケ/@"
                        + resultStation.getLat() + "," + resultStation.getLng() + "," + "15z"));
                break;
        }
        return intent;
    }

    /**
     * 共有(LINE)への遷移準備
     *
     * @param activity 実行中のActivityのthis
     * @param stationName 検索結果として選ばれた候補駅の駅名
     * @return Intent 画面遷移(LINE起動)に必要な情報を保持したIntent
     */
    public static Object prepareForLINE(Activity activity, String stationName) {

        // LINEのアプリID
        String LINE_APP_ID = "jp.never.line.android";
        // LINEで送る用の改行コード
        String LIN_SEPARATOR = "%0D%0A";
        // アプリケーションクラスのインスタンスを取得

        try {
            // パッケージ情報の取得
            PackageManager pm = activity.getPackageManager();
            // LINEがインストールされているかの確認
            ApplicationInfo appInfo = pm.getApplicationInfo(LINE_APP_ID, PackageManager.GET_META_DATA);
            // インストールされてたら、LINEへ
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("line://msg/text/" + "中間地点は…" + LIN_SEPARATOR
                    + stationName + "駅" + LIN_SEPARATOR
                    + "だよ！" + LIN_SEPARATOR
                    + "by 中間地点アプリ"
            ));
            return intent;

        } catch(PackageManager.NameNotFoundException e) {
            //インストールされてなかったら、インストールを要求する
            AlertDialog.Builder dialog = new AlertDialog.Builder(activity)
                    .setTitle("LINEが見つかりません")
                    .setMessage("LINEをインストールしてやり直して下さい")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setCancelable(false);
            return dialog;
        }
    }

    /**
     * 周辺情報画面への遷移準備
     *
     * @param activity 実行中Activityのthis
     * @param stationList 入力されていた駅情報のリスト
     * @param resultVO 検索結果として選ばれた候補の駅
     * @return Intent 画面遷移に必要な情報を保持したIntent
     */
    public static Intent prepareForMapsActivity(Activity activity, ArrayList<StationDetailVO> stationList, StationDetailVO resultVO) {

        // 入力されていた駅情報のリストと候補駅を次の画面に送る準備
        Intent intent = new Intent(activity, Result.class)
                .putExtra("stationList", stationList)
                .putExtra("resultStation", resultVO);
        return intent;
    }

}
