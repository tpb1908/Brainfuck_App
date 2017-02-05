package com.tpb.brainfuck_app;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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
    private Program program;
    private Storage storage;
    private boolean editedWithoutSaving = false;



    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor);
        storage = Storage.instance(this);
        mEditor = (EditText) findViewById(R.id.editor);
        mKeyboardLock = (ImageButton) findViewById(R.id.lock_keyboard_button);
        final ImageButton mRun = (ImageButton) findViewById(R.id.run_button);
        final ImageButton mQuickRun = (ImageButton) findViewById(R.id.quick_run_button);
        final ImageButton mSaveButton = (ImageButton) findViewById(R.id.save_button);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        program = new Program();
        setTitle(R.string.title_editor);
        final Bundle b = getIntent().getExtras();
        if(b != null) {
            program = b.getParcelable("prog");
            if(program != null) {
                mEditor.setText(program.prog);
                setTitle(String.format(getResources().getString(R.string.title_editing), program.name));
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

        mEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                editedWithoutSaving = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mKeyboardLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isKeyboardLocked = !isKeyboardLocked;
                if(isKeyboardLocked) {
                    mKeyboardLock.setImageResource(R.drawable.ic_lock_outline_white);
                    final InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                } else {
                    mKeyboardLock.setImageResource(R.drawable.ic_lock_open_white);
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
                if(!program.prog.equals(mEditor.getText().toString())) {
                    program.prog = mEditor.getText().toString();
                    editedWithoutSaving = true;
                }
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
            backPress();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        backPress();
    }

    private void backPress() {
        if(editedWithoutSaving) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.title_exit_dialog);
            builder.setMessage(R.string.message_save_exit_dialog);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    showDialog(SettingsDialog.SettingsLaunchType.CLOSE);
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    finish();
                }
            });
            builder.setNeutralButton(R.string.cancel, null);
            builder.create().show();
        } else {
            finish();
        }
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
            program.prog = mEditor.getText().toString();
            if(program.id == 0) {
                storage.add(program);
            } else {
                storage.update(program);
            }
            editedWithoutSaving = false;
            if(lt == SettingsDialog.SettingsLaunchType.CLOSE) finish();
        }
    }

    public void editButtonPress(View v) {
        final int start = Math.max(mEditor.getSelectionStart(), 0);
        final int end = Math.max(mEditor.getSelectionEnd(), 0);
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
            case R.id.input_button:
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
            case R.id.enter_button:
                mEditor.dispatchKeyEvent(new KeyEvent(0,0, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER, 0));
                mEditor.dispatchKeyEvent(new KeyEvent(0, 0, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER, 0));
                break;
        }
    }
}
