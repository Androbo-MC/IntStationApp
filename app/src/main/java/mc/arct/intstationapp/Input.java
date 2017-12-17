package mc.arct.intstationapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

import mc.arct.intstationapp.models.StationDetailVO;
import mc.arct.intstationapp.storage.StationDAO;
import mc.arct.intstationapp.utils.IntentUtil;

/**
 * 入力画面
 */

public class Input extends AppCompatActivity {


    //入力欄を格納するリスト
    private ArrayList<AutoCompleteTextView> inputBoxList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.s002_input);

        getIntent();

        // 入力欄をリストに格納
        inputBoxList.add((AutoCompleteTextView) findViewById(R.id.inputBox1));
        inputBoxList.add((AutoCompleteTextView) findViewById(R.id.inputBox2));
        inputBoxList.add((AutoCompleteTextView) findViewById(R.id.inputBox3));
        inputBoxList.add((AutoCompleteTextView) findViewById(R.id.inputBox4));
        inputBoxList.add((AutoCompleteTextView) findViewById(R.id.inputBox5));
    }

    /**
     * 検索ボタン
     * @param view
     */
    public void searchButtonOnClick(View view) {

        ArrayList<StationDetailVO> lstStation = setLstStation();

        screenTransition(lstStation);

        Intent intent = new Intent(this,Result.class);
        startActivity(intent);
        Input.this.finish();

    }

    private ArrayList<StationDetailVO> setLstStation () {

        ArrayList<StationDetailVO>  lstStation =new ArrayList<>();

        for (AutoCompleteTextView txtView : inputBoxList) {

            if (txtView.getText().toString().isEmpty()) {
                continue;
            }
            String station = txtView.getText().toString();
            StationDAO dao = new StationDAO(getApplicationContext());
            StationDetailVO vo = dao.selectStationByName(station);

            if (vo == null){
                // todo:dialogmsg
                return null;
            }

            lstStation.add(vo);
        }
        return  lstStation;

    }

    private void screenTransition(ArrayList<StationDetailVO> lstStation){

        if(!lstStation.isEmpty() || lstStation != null){
            Intent intent = IntentUtil.prepareForResult(this,lstStation);
            startActivity(intent);
        }
        else{
            // todo:dialogmsg
        }

    }

    /**
     * クリアボタン
     * @param view
     */
    public void clearButtonOnClick(View view){

        // 全ての入力欄に空文字を設定する。
        for (AutoCompleteTextView inputBox : inputBoxList){
            // 空文字セットする
            inputBox.setText("");
        }

    }

}
