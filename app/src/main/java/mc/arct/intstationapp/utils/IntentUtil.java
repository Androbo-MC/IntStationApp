package mc.arct.intstationapp.utils;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import mc.arct.intstationapp.Area;
import mc.arct.intstationapp.Input;
import mc.arct.intstationapp.Result;
import mc.arct.intstationapp.models.StationVO;

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
     * @param context コンテキスト(実行中のActivityのthisでOK)
     * @param stationList 入力されていた駅情報のリスト
     * @return Intent 画面遷移に必要な情報を保持したIntent
     */
    public static Intent prepareForSearchResult(Context context, ArrayList<StationVO> stationList) {

        // 入力されていた駅情報のリストを次の画面に送る準備
        Intent intent = new Intent(context, Result.class)
                .putExtra("result", stationList);
        return intent;
    }

    /**
     * 周辺情報画面への遷移準備
     *
     * @param context コンテキスト(実行中ActivityのthisでOK)
     * @param stationList 入力されていた駅情報のリスト
     * @param resultVO 検索結果として選ばれた候補の駅
     * @return Intent 画面遷移に必要な情報を保持したIntent
     */
    public static Intent prepareForAreaInformation(Context context, ArrayList<StationVO> stationList, StationVO resultVO) {

        // 入力されていた駅情報のリストと候補駅を次の画面に送る準備
        Intent intent = new Intent(context, Area.class)
                .putExtra("stationList", stationList)
                .putExtra("resultStation", resultVO);
        return intent;
    }

}
