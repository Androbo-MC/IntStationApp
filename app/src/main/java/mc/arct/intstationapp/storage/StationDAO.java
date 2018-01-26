package mc.arct.intstationapp.storage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import mc.arct.intstationapp.models.StationDetailVO;
import mc.arct.intstationapp.models.StationVO;

/**
 * 駅情報DAO
 *
 * データベースの駅情報テーブルにアクセスするときに使用する。
 */

public class StationDAO {

    private MyOpenHelper helper;
    private SQLiteDatabase db;

    // コンストラクタ(初期化時に呼ばれる)
    public StationDAO (Context context) {

        // DBの準備
        this.helper = new MyOpenHelper(context);
        this.db = helper.getWritableDatabase();
    }

    // 文字列と部分一致する駅名と仮名を取得するメソッド
    public ArrayList<StationVO> selectNamesByStr(String str) {

        // 結果返却用リスト
        ArrayList<StationVO> stationList = new ArrayList<>();
        // DBから部分一致で駅名かカナに当てはまる駅情報を取得
        Cursor cursor = db.rawQuery("SELECT name, kana FROM station " +
                        "WHERE name LIKE '%' || ? || '%' OR kana LIKE '%' || ? || '%'",
                new String[]{str, str});
        // 取得した数だけ繰り返す
        while (cursor.moveToNext()) {
            // 取得した全駅名と仮名をVOに格納
            stationList.add(new StationVO(cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("kana"))));
        }
        // 使用済カーソルはクローズする
        cursor.close();
        // 結果値を返却
        return stationList;
    }

    // 文字列と部分一致するローマ字名を取得するメソッド
    public ArrayList<StationVO> selectRomajiByStr(String str) {

        // 結果返却用リスト
        ArrayList<StationVO> stationList = new ArrayList<>();
        // DBから部分一致で駅名カナに当てはまる駅情報を取得
        Cursor cursor = db.rawQuery("SELECT kana, romaji FROM station " +
                "WHERE romaji LIKE '%' || ? || '%'",
                new String[]{str});
        // 取得した数だけ繰り返す
        while (cursor.moveToNext()) {
            // 取得した全駅名と仮名をVOに格納
            stationList.add(new StationDetailVO(cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("kana")),
                    cursor.getString(cursor.getColumnIndex("romaji"))));
        }
        // 使用済カーソルはクローズする
        cursor.close();
        // 結果値VOを返却
        return stationList;
    }

    // 駅名から駅情報を取得するメソッド
    public StationDetailVO selectStationByName(String name) {

        // 結果返却用VO
        StationDetailVO vo = null;
        // DBから駅情報を取得
        Cursor cursor = db.rawQuery("SELECT kana, pref_cd, lat, lng, gnavi_id, jorudan_name, romaji FROM station WHERE name = ?",
                new String[]{name});

        if (cursor.moveToNext()) {
            // カーソルから各項目を取得
            String kana = cursor.getString(cursor.getColumnIndex("kana"));
            String prefCd = cursor.getString(cursor.getColumnIndex("pref_cd"));
            String lat = cursor.getString(cursor.getColumnIndex("lat"));
            String lng = cursor.getString(cursor.getColumnIndex("lng"));
            String gnaviId = cursor.getString(cursor.getColumnIndex("gnavi_id"));
            String jorudanName = cursor.getString(cursor.getColumnIndex("jorudan_name"));
            String romaji = cursor.getString(cursor.getColumnIndex("romaji"));
            // DBから取得した値を格納したVOを生成
            vo = new StationDetailVO(name, kana, prefCd, lat, lng, gnaviId, jorudanName, romaji);
        }
        // 使用済カーソルはクローズする
        cursor.close();
        // 結果値を格納したリストを返却
        return vo;
    }
}
