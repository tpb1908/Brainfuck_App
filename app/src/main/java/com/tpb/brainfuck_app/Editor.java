package com.tpb.brainfuck_app;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

/**
 * Created by theo on 21/08/16.
 */
public class Editor extends AppCompatActivity implements SettingsDialog.SettingsDialogListener {
    private static final String TAG = "Editor";
    private boolean isKeyboardLocked = false;
    private EditText mEditor;
    private ImageButton mKeyboardLock;
    private ImageButton mRun;
    private ImageButton mQuickRun;
    private ImageButton mSaveButton;
    private Program program;
    private Storage storage;
    private boolean saved = true;


    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor);
        storage = Storage.instance(this);
        mEditor = (EditText) findViewById(R.id.editor);
        mKeyboardLock = (ImageButton) findViewById(R.id.lock_keyboard_button);
        mRun = (ImageButton) findViewById(R.id.run_button);
        mQuickRun = (ImageButton) findViewById(R.id.quick_run_button);
        mSaveButton = (ImageButton) findViewById(R.id.save_button);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        program = new Program();
        setTitle("Editor");
        final Bundle b = getIntent().getExtras();
        if(b != null) {
            program = b.getParcelable("prog");
            if(program != null) {
                mEditor.setText(program.prog);
                setTitle("Editing " + program.name);
            }
        }

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
                    mKeyboardLock.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_lock_outline_white));
                    final InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                } else {
                    mKeyboardLock.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_lock_open_white));
                }
            }
        });

        mRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                program.prog = mEditor.getText().toString();
                showDialog(SettingsDialog.SettingsLaunchType.RUN);
            }
        });

        mQuickRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                program.prog = mEditor.getText().toString();
                final Intent i = new Intent(Editor.this, Runner.class);
                i.putExtra("prog", program);
                startActivity(i);
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                program.prog = mEditor.getText().toString();
                showDialog(SettingsDialog.SettingsLaunchType.SAVE);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            Log.i(TAG, "onOptionsItemSelected: Program " + program.prog);
            Log.i(TAG, "onOptionsItemSelected: Text " + mEditor.getText().toString());
            if(!program.prog.equals(mEditor.getText().toString()) || !saved) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Confirm");
                builder.setMessage("Save before closing?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showDialog(SettingsDialog.SettingsLaunchType.CLOSE);
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
                    }
                });
                builder.setNeutralButton("CANCEL", null);
                builder.create().show();
            } else {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialog(SettingsDialog.SettingsLaunchType lt) {
        final DialogFragment dialog = new SettingsDialog();
        final Bundle bundle = new Bundle();
        bundle.putParcelable("prog", program);
        bundle.putSerializable("launchType", lt);
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), "");
    }

    @Override
    public void onNegativeClick(DialogFragment dialog, SettingsDialog.SettingsLaunchType lt) {

    }

    @Override
    public void onPositiveClick(DialogFragment dialog, SettingsDialog.SettingsLaunchType lt,  Program program) {
        this.program = program;
        if(lt == SettingsDialog.SettingsLaunchType.RUN) { //About to run
            final Intent i = new Intent(Editor.this, Runner.class);
            i.putExtra("prog", program);
            startActivity(i);
        } else if(lt == SettingsDialog.SettingsLaunchType.SAVE || lt == SettingsDialog.SettingsLaunchType.CLOSE) {
            if(program.id == 0) {
                storage.add(program);
            } else {
                storage.update(program);
            }
            saved = true;
            if(lt == SettingsDialog.SettingsLaunchType.CLOSE) finish();
        }
    }

    public void editButtonPress(View v) {
        final int start = Math.max(mEditor.getSelectionStart(), 0);
        final int end = Math.max(mEditor.getSelectionEnd(), 0);
        saved = false;
        switch(v.getId()) {
            //TODO- Find out how to do <> with key events

            case R.id.decrement_button:
                mEditor.getText().replace(Math.min(start, end), Math.max(start, end), "<");
                break;
            case R.id.increment_button:
                mEditor.getText().replace(Math.min(start, end), Math.max(start, end), ">");
                break;
            case R.id.plus_button:
                mEditor.dispatchKeyEvent(new KeyEvent(0,0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_PLUS, 0));
                mEditor.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_PLUS, 0));
                break;
            case R.id.minus_button:
                mEditor.dispatchKeyEvent(new KeyEvent(0,0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MINUS, 0));
                mEditor.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MINUS, 0));
                break;
            case R.id.output_button:
                mEditor.dispatchKeyEvent(new KeyEvent(0,0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_PERIOD, 0));
                mEditor.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_PERIOD, 0));
                break;
            case R.id.input:
                mEditor.dispatchKeyEvent(new KeyEvent(0,0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_COMMA, 0));
                mEditor.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_COMMA, 0));
                break;
            case R.id.start_loop_button:
                mEditor.dispatchKeyEvent(new KeyEvent(0,0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_LEFT_BRACKET, 0));
                mEditor.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_LEFT_BRACKET, 0));
                break;
            case R.id.end_loop_button:
                mEditor.dispatchKeyEvent(new KeyEvent(0,0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_RIGHT_BRACKET, 0));
                mEditor.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_RIGHT_BRACKET, 0));
                break;
            case R.id.space_button:
                mEditor.dispatchKeyEvent(new KeyEvent(0,0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_SPACE, 0));
                mEditor.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_SPACE, 0));
                break;
            case R.id.breakpoint_button:
                mEditor.dispatchKeyEvent(new KeyEvent(0,0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACKSLASH, 0));
                mEditor.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACKSLASH, 0));
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
