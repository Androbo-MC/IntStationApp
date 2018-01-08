package mc.arct.intstationapp.network;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class MyNetworkManager {

    public static AlertDialog.Builder checkConnection(Activity activity) {

        // ネットワークの接続状態を確認
        ConnectivityManager connMgr = (ConnectivityManager)
                activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // ネットワークが問題なければnullを返却
            return null;
        } else {
            // 電波なかったらこのダイアログを出す
            return new AlertDialog.Builder(activity)
                    .setTitle("ネットワーク接続不可")
                    .setMessage("この機能は電波のある場所で使用してください。")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setCancelable(false);
        }
    }
}
