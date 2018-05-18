package com.bemetson.cloudberryhslstop;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AddIconDialogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_icon_dialog);
    }

    public void kill(View view) {
        Intent intent = getIntent();
        setResult(RESULT_CANCELED, intent);
        this.finish();
    }

    public void selectedIcon(View view) {
        Intent intent = getIntent();
        switch (view.getId()) {
            case R.id.icon_0:
                intent.putExtra("icon_value", 0);
                setResult(RESULT_OK, intent);
                break;
            case R.id.icon_1:
                intent.putExtra("icon_value", 1);
                setResult(RESULT_OK, intent);
                break;

            case R.id.icon_2:
                intent.putExtra("icon_value", 2);
                setResult(RESULT_OK, intent);
                break;

            case R.id.icon_3:
                intent.putExtra("icon_value", 3);
                setResult(RESULT_OK, intent);
                break;

            case R.id.icon_4:
                intent.putExtra("icon_value", 4);
                setResult(RESULT_OK, intent);
                break;

            case R.id.icon_5:
                intent.putExtra("icon_value", 5);
                setResult(RESULT_OK, intent);
                break;

            case R.id.icon_6:
                intent.putExtra("icon_value", 6);
                setResult(RESULT_OK, intent);
                break;

            case R.id.icon_7:
                intent.putExtra("icon_value", 7);
                setResult(RESULT_OK, intent);
                break;
        }
        this.finish();

    }
}
