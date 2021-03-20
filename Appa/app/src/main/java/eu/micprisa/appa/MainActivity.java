package eu.micprisa.appa;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

//sources: https://www.youtube.com/watch?v=AD5qt7xoUU8&t=126s
//https://www.youtube.com/watch?v=QtCHlQ2sLx8
public class MainActivity extends AppCompatActivity {

    private TextView result;
    private EditText  num1;
    private EditText num2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    //here will be sent data to App b via button
    public void sendDataToOtherApp (View view){

        num1 = findViewById(R.id.mainNum1);
        num2 = findViewById(R.id.mainNum2);
        if (num1.getText().toString().equals("") || num2.getText().toString().equals("")){
            Toast.makeText(MainActivity.this, "Please insert numbers", Toast.LENGTH_SHORT).show();
        }else {
            Intent i = new Intent();
            //i.setAction(Intent.ACTION_SEND);
            //i.putExtra(Intent.EXTRA_TEXT, "")
            //i.setType("text/plain");
            i.setClassName("eu.micprisa.appb","eu.micprisa.appb.MainActivity");
            i.putExtra("num1", Integer.parseInt(num1.getText().toString()));
            i.putExtra("num2", Integer.parseInt(num2.getText().toString()));
            startActivityForResult(i, 123);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        result = findViewById(R.id.mainResult);
        if (requestCode == 123) {
            if (resultCode == RESULT_OK) {
                int result = data.getIntExtra("result",0);
                this.result.setText("" + result);
            }
        }
        if(resultCode == RESULT_CANCELED){
            result.setText("Data not processed!");
        }
    }

}