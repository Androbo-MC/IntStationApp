package mc.arct.intstationapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;

public class Input extends AppCompatActivity {

    //カウンター
    private int counter = 0;
    //入力欄を格納するリスト
    private ArrayList<AutoCompleteTextView> inputBoxList = new ArrayList<>();
    //入力値を格納するリスト
    private ArrayList<String> inputStationNameList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.S002_Input);

        // 入力欄をリストに格納
        inputBoxList.add((AutoCompleteTextView) findViewById(R.id.inputBox1));
        inputBoxList.add((AutoCompleteTextView) findViewById(R.id.inputBox2));
        inputBoxList.add((AutoCompleteTextView) findViewById(R.id.inputBox3));
        inputBoxList.add((AutoCompleteTextView) findViewById(R.id.inputBox4));
        inputBoxList.add((AutoCompleteTextView) findViewById(R.id.inputBox5));
    }

    public void searchButtonOnClick(View view) {
        // 入力値をリストに格納
        for (AutoCompleteTextView text : inputBoxList) {
            // 空の入力欄は含まない
            if (!text.getText().toString().isEmpty()) {
                inputStationNameList.add(text.getText().toString());
            }
        }

        // 位置情報取得メソッドに入力値を渡す。
        for (String text : inputStationNameList){
            if (null != text && !text.isEmpty()){
                counter++;
                getPlaceInfo(text);
            }
        }

    }

    private void getPlaceInfo(String text){

        // TODO : DBから値を取得。

    }

    public void clearButtonOnClick(View view){

        // 全ての入力欄に空文字を設定する。
        for (AutoCompleteTextView inputBox : inputBoxList){
            inputBox.setText("");
        }

    }

    public void settingButtonOnClick(View view){
        // TODO:設定画面を表示する処理を記述する。
    }
}
