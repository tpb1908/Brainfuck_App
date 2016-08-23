package com.tpb.brainfuck_app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by theo on 21/08/16.
 */
public class Editor extends AppCompatActivity {
    private boolean isKeyboardLocked;
    private EditText mEditor;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor);
        mEditor = (EditText) findViewById(R.id.editor);


        mEditor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.onTouchEvent(motionEvent);
                final InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                return true;
            }
        });
    }
}
