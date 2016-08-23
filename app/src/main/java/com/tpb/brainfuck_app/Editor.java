package com.tpb.brainfuck_app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

/**
 * Created by theo on 21/08/16.
 */
public class Editor extends AppCompatActivity {
    private boolean isKeyboardLocked = false;
    private EditText mEditor;
    private ImageButton mKeyboardLock;
    private ImageButton mRun;
    private ImageButton mQuickRun;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor);
        mEditor = (EditText) findViewById(R.id.editor);
        mKeyboardLock = (ImageButton) findViewById(R.id.lock_keyboard_button);
        mRun = (ImageButton) findViewById(R.id.run_button);
        mQuickRun = (ImageButton) findViewById(R.id.quick_run_button);


        mEditor.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.onTouchEvent(motionEvent);
                if(isKeyboardLocked) {
                    final InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                return true;
            }
        });

        mKeyboardLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isKeyboardLocked = ! isKeyboardLocked;
                if(isKeyboardLocked) {
                    mKeyboardLock.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_lock_outline_black));
                    final InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                } else {
                    mKeyboardLock.setImageDrawable(getApplicationContext().getDrawable(R.drawable.ic_lock_open_black));
                }
            }
        });
    }
}
