package mc.arct.intstationapp.network;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.os.AsyncTask;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import mc.arct.intstationapp.models.StationDetailVO;
import mc.arct.intstationapp.models.StationTransferVO;
import mc.arct.intstationapp.utils.ConvertUtil;

/**
 * Jorudanから情報を取得する非同期処理クラス
 */

public class JorudanInfoTask extends AsyncTask< Void, Integer, String[]> {
    // doInBackgroundメソッドの引数の型, onProgressUpdateメソッドの引数の型, onPostExecuteメソッドの戻り値の型

    // 結果格納用リストをまとめた配列
    private ArrayList<StationTransferVO>[] resultInfoLists;
    // 出発駅用配列
    private StationDetailVO[] inputStationArray;
    // 到着駅
    private String destStation;
    // プログレスバー
    private Activity activity;
    private ProgressDialog dialog;
    // コールバック用
    private CallBackTask callbacktask;

    /**
     * コンストラクタ(onPostExecuteで更新したいViewなどを渡す)
     *
     * @param activity プログレスバーに渡すアクティビティ
     * @param inputStationList 入力された駅名のリスト
     * @param destStationName 到着候補の駅名(ジョルダンでの駅名)
     */
    public JorudanInfoTask(Activity activity, ArrayList<StationDetailVO> inputStationList,
                           String destStationName) {
        super();
        this.resultInfoLists = new ArrayList[inputStationList.size()];
        this.inputStationArray =
                inputStationList.toArray(new StationDetailVO[inputStationList.size()]);
        this.destStation = destStationName;
        this.activity = activity;
    }

    //
    @Override
    protected void onPreExecute() {
        //
        dialog = new ProgressDialog(this.activity);
        dialog.setTitle("");
        dialog.setMessage("");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        dialog.setMax(inputStationArray.length);
        dialog.setProgress(0);
        dialog.show();
    }

    //
    @Override
    protected String[] doInBackground(Void... prams) {

        //
        String[] resultArray = new String[inputStationArray.length];

        try {
            for (int i = 0; i < inputStationArray.length; i++) {
                //
                Request request = new Request.Builder()
                        .url("http://www.jorudan.co.jp/norikae/cgi/nori.cgi?Sok=決+定&eki1="
                                + inputStationArray[i].getJorudanName() + "&eki2=" + destStation)
                        .get()
                        .build();

                OkHttpClient client = new OkHttpClient();

                Response response = client.newCall(request).execute();
                //
                resultArray[i] = response.body().string();
                //
                publishProgress(i + 1);
            }
        } catch (IOException e) {

            e.printStackTrace();
        }
        return resultArray;
    }

    //
    @Override
    protected void onPostExecute(String[] resultArray) {
        super.onPostExecute(resultArray);
        //
        for (int i = 0; i < resultArray.length; i++) {
            //
            resultInfoLists[i] = new ArrayList<>();
            //
            Document doc = Jsoup.parse(resultArray[i]);
            //
            //
            if (doc.getElementById("search_msg").text().equals("検索できない駅の指定です。（近距離です。）")) {
                //
                StationTransferVO vo =
                        new StationTransferVO(inputStationArray[i].getJorudanName(), destStation);
                resultInfoLists[i].add(vo);
            //
            } else if (doc.getElementById("search_msg").text().equals("出発地 到着地 に同じ目的地は設定できません。")) {
                //
                StationTransferVO vo =
                        new StationTransferVO(inputStationArray[i].getJorudanName(), destStation);
                resultInfoLists[i].add(vo);
            //
            } else {
                //
                Element tBody = doc.getElementById("Bk_list_tbody");
                for (int j = 0; j < tBody.getElementsByTag("tr").size(); j++) {
                    //
                    StationTransferVO vo =
                            new StationTransferVO(inputStationArray[i].getJorudanName(), destStation);
                    //
                    String timeStr = tBody.child(j).child(2).text();
                    String costStr = tBody.child(j).child(4).text();
                    String transferStr = tBody.child(j).child(3).text();
                    //
                    vo.setTime(ConvertUtil.timeToMinutes(timeStr));
                    vo.setCost(ConvertUtil.removeYenAndComma(costStr));
                    vo.setTransfer(ConvertUtil.removeNorikaeAndKai(transferStr));
                    resultInfoLists[i].add(vo);
                }
                //
                Elements routes = doc.getElementsByClass("route");
                //
                for (int j = 0; j < routes.size(); j++) {
                    //
                    Elements stations = routes.get(j).getElementsByClass("nm");
                    //
                    ArrayList<String> transferList = new ArrayList<>();
                    //
                    for (Element station : stations) {
                        transferList.add(station.text());
                    }
                    //
                    resultInfoLists[i].get(j).setTransferList(transferList);
                }
            }
        }
        //
        dialog.dismiss();
        //
        callbacktask.CallBack();
    }

    public ArrayList<StationTransferVO>[] getResultInfoLists() {

        return this.resultInfoLists;
    }

    public void setOnCallBack(CallBackTask _cbj) {
        callbacktask = _cbj;
    }

    /**
     * コールバック用のstaticなclass
     */
    public static class CallBackTask {
        //
        public void CallBack(String[] result) {
        }
        //
        public void CallBack() {
        }
    }
}