package mc.arct.intstationapp.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 駅乗り換え情報VO
 *
 * 乗り換え情報を保持する。
 */

public class StationTransferVO implements Serializable {

    // 出発駅名
    private String stationFrom;
    // 到着駅名
    private String stationTo;
    // 所要時間("分"のみの整数で格納)
    private int time;
    // 料金
    private int cost;
    // 乗り換え回数
    private int transfer;
    // 乗り換え途中駅リスト
    private ArrayList<String> transferList;
    // 検索URL
    private String searchURL;

    // コンストラクタ
    public StationTransferVO(String stationFrom, String stationTo, String searchURL) {

        this.stationFrom = stationFrom;
        this.stationTo = stationTo;
        this.time = 0;
        this.cost = 0;
        this.transfer = 0;
        this.transferList = new ArrayList<>();
        this.searchURL = searchURL;
    }

    public String getStationFrom() {
        return stationFrom;
    }

    public String etStationTo() {
        return stationTo;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getTransfer() {
        return transfer;
    }

    public void setTransfer(int transfer) {
        this.transfer = transfer;
    }

    public ArrayList<String> getTransferList() {
        return transferList;
    }

    public void setTransferList(ArrayList<String> transferList) {
        this.transferList = transferList;
    }

    public String getSearchURL() {
        return searchURL;
    }

    public void setSearchURL(String searchURL) {
        this.searchURL = searchURL;
    }

    @Override
    public String toString() {
        return "StationTransferVO{" +
                "stationFrom='" + stationFrom + '\'' +
                ", stationTo='" + stationTo + '\'' +
                ", time=" + time +
                ", cost=" + cost +
                ", transfer=" + transfer +
                ", transferList=" + transferList +
                ", searchURL='" + searchURL + '\'' +
                '}';
    }
}
