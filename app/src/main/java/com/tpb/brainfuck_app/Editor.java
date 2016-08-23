package com.tpb.brainfuck_app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
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
        setTitle("Editor");
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

    public void editButtonPress(View v) {
        final int start = Math.max(mEditor.getSelectionStart(), 0);
        final int end = Math.max(mEditor.getSelectionEnd(), 0);
        switch(v.getId()) {
            //TODO- Replace the text replacement with key events?
            case R.id.decrement_button:
                mEditor.getText().replace(Math.min(start, end), Math.max(start, end), "<");
                break;
            case R.id.increment_button:
                mEditor.getText().replace(Math.min(start, end), Math.max(start, end), ">");
                break;
            case R.id.plus_button:
                mEditor.getText().replace(Math.min(start, end), Math.max(start, end), "+");
                break;
            case R.id.minus_button:
                mEditor.getText().replace(Math.min(start, end), Math.max(start, end), "-");
                break;
            case R.id.output_button:
                mEditor.getText().replace(Math.min(start, end), Math.max(start, end), ".");
                break;
            case R.id.input_button:
                mEditor.getText().replace(Math.min(start, end), Math.max(start, end), ",");
                break;
            case R.id.start_loop_button:
                mEditor.getText().replace(Math.min(start, end), Math.max(start, end), "[");
                break;
            case R.id.end_loop_button:
                mEditor.getText().replace(Math.min(start, end), Math.max(start, end), "]");
                break;
            case R.id.space_button:
                mEditor.getText().replace(Math.min(start, end), Math.max(start, end), " ");
                break;
            case R.id.backspace_button:
                mEditor.dispatchKeyEvent(new KeyEvent(0,0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL, 0));
                mEditor.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL, 0));
                break;
            case R.id.backward_button:
                mEditor.dispatchKeyEvent(new KeyEvent(0,0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT, 0));
                mEditor.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_LEFT, 0));
                break;
            case R.id.forward_button:
                mEditor.dispatchKeyEvent(new KeyEvent(0,0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT, 0));
                mEditor.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_RIGHT, 0));
                break;
            case R.id.up_button:
                mEditor.dispatchKeyEvent(new KeyEvent(0,0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP, 0));
                mEditor.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_UP, 0));
                break;
            case R.id.down_button:
                mEditor.dispatchKeyEvent(new KeyEvent(0,0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN, 0));
                mEditor.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_DOWN, 0));
                break;
            case R.id.enter_button:
                mEditor.dispatchKeyEvent(new KeyEvent(0,0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER, 0));
                mEditor.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER, 0));
                break;
        }
    }
}
