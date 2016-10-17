package com.tpb.brainfuck_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.SparseIntArray;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Stack;

/**
 * Created by theo on 21/08/16.
 */
public class Runner extends AppCompatActivity implements InterpreterIO {
    private static final String TAG =  "Runner";
    private Program program;
    private Thread thread;
    private Interpreter inter;
    private boolean paused = false;
    private boolean shouldReadBreakpoints = true;

    private TextView mOutput;
    private EditText mInput;
    private ImageButton mPlayPauseButton;
    private TextView mPlayPauseLabel;
    private TextView mBreakpointLabel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.runner);
        mOutput = (TextView) findViewById(R.id.output);
        mInput = (EditText) findViewById(R.id.input_edittext);
        mPlayPauseButton = (ImageButton) findViewById(R.id.play_pause_button);
        mPlayPauseLabel = (TextView) findViewById(R.id.play_pause_label);
        mBreakpointLabel = (TextView) findViewById(R.id.breakpoint_label);
        program = getIntent().getParcelableExtra("prog");
        if(program.name != null && program.name.length() > 0) {
            setTitle(String.format(getResources().getString(R.string.title_running), program.name));
        } else {
            setTitle(R.string.title_runner);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        startProgram();
    }

    private void startProgram() {
        inter = new Interpreter(this, program);
        thread = new Thread(inter);
        thread.start();
        mOutput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                final ScrollView sv = (ScrollView) findViewById(R.id.output_scrollview);
                sv.post(new Runnable() {
                    @Override
                    public void run() {
                        sv.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        });
        mPlayPauseButton.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_pause_white));
    }

    private void setPaused() {
        mPlayPauseButton.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_play_arrow_white));
        mPlayPauseLabel.setText(R.string.label_play_button);
        paused = true;
    }

    private void setPlaying() {
        mPlayPauseButton.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_pause_white));
        mPlayPauseLabel.setText(R.string.label_pause_button);
        paused = false;
    }

    public void restart(View v) {
        thread.interrupt();
        mOutput.setText("");
        startProgram();
    }

    public void togglePause(View v) {
        if(paused) {
            inter.unPause();
            final SpannableString sp = new SpannableString("\nUnpaused\n");
            sp.setSpan(new ForegroundColorSpan(Color.GREEN), 1, sp.length()-1, 0);
            mOutput.append(sp);
            setPlaying();
        } else {
            inter.pause();
            final SpannableString sp = new SpannableString("\nPaused\n");
            sp.setSpan(new ForegroundColorSpan(Color.YELLOW), 1, sp.length()-1, 0);
            mOutput.append(sp);
            setPaused();
        }
    }

    public void input(View v) {
        final String input = mInput.getText().toString();
        if(input.length() > 0) {
            mInput.setError(null);
            final char in = input.charAt(0);
            mOutput.append(in + "\n");
            inter.setInput(in);
            mInput.setText(null);
        } else {
            mInput.setError("Input a character");
        }
    }

    public void toggleBreak(View v) {
        if(shouldReadBreakpoints) {
            shouldReadBreakpoints = false;
            mBreakpointLabel.setText(R.string.label_breakpoint_ignore_button);
        } else {
            shouldReadBreakpoints = true;
            mBreakpointLabel.setText(R.string.label_breakpoint_break_button);
        }
    }

    public void step(View v) {
        if(inter.pos < inter.program.prog.length()) inter.step();
    }

    public void dump(View v) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mOutput.append(inter.getDebugDump());
            }
        });
    }

    @Override
    public void output(final String out) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mOutput.append(out + program.outputSuffix);
            }
        });
    }

    @Override
    public void breakpoint() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setPaused();
                final SpannableString sp = new SpannableString("Hit breakpoint");
                sp.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, sp.length(), 0);
                mOutput.append(sp);
            }
        });

    }

    @Override
    public void error(final int pos, final String error) {
        thread.interrupt();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final String e = "Error at position " + pos + "\nMessage: " + error;
                final SpannableString er = new SpannableString(e);
                er.setSpan(new ForegroundColorSpan(Color.RED),0, e.length(), 0);
                mOutput.append(er);
            }
        });
    }

    @Override
    public void getInput() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final SpannableString in = new SpannableString("Input: ");
                in.setSpan(new ForegroundColorSpan(Color.GREEN), 0, in.length(), 0);
                mOutput.append(in);
                InputMethodManager inputMethodManager =  (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(mInput.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
                mInput.requestFocus();
            }
        });
    }

    @Override
    public void finish() {
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).
                hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        thread.interrupt();
        super.finish();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            backPress();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        backPress();
    }

    public void backPress() {
        if(thread.isAlive()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.title_exit_dialog);
            builder.setMessage(R.string.message_exit_running_program_dialog);
            builder.setPositiveButton(android.R.string.ok,  new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.create().show();
        } else {
            finish();
        }
    }

    private class Interpreter implements Runnable {
        private InterpreterIO io;
        private Program program;
        private int[] mem;
        private int pos;
        private int pointer;
        private boolean waitingForInput = false;
        private boolean paused = false;
        private SparseIntArray loopPositions;

        Interpreter(InterpreterIO io, Program program) {
            this.io = io;
            this.program = program;
        }

        @Override
        public void run() {
            mem = new int[program.memSize];
            pos = 0;
            pointer = 0;
            final int sCount = program.prog.length() - program.prog.replace("[", "").length();
            final int eCount = program.prog.length() - program.prog.replace("]", "").length();
            if(sCount > eCount) {
                error(-1, "Mismatched brackets, more [ than ]");
            } else if(eCount > sCount) {
                error(-1, "Mismatched brackets, more ] than [");
            } else {
                findLoopPositions();
                while(!Thread.currentThread().isInterrupted() && pos < program.prog.length()) {
                    if(paused || waitingForInput) {
                        try {
                            Thread.sleep(100);
                        } catch(InterruptedException e) {}
                    } else {
                        step();
                    }
                }
            }
            io.output("\nProgram complete");
        }

        private void step() {
            switch(program.prog.charAt(pos)) {
                case '>':
                    pointer++;
                    if(pointer >= mem.length) {
                        if(program.pointerOverflowBehaviour == 0) {
                            pause();
                            error(pos, "Pointer overflow");
                        } else if(program.pointerOverflowBehaviour == 1){
                            pointer = 0;
                        } else if(program.pointerOverflowBehaviour == 2) {
                            final int[] newMem = new int[(int)(mem.length * 1.5)];
                            System.arraycopy(mem, 0, newMem, 0, mem.length);
                            mem = newMem;
                        }
                    }
                    break;
                case '<':
                    pointer--;
                    if(pointer < 0) {
                        if(program.pointerUnderflowBehaviour == 0) {
                            pause();
                            error(pos, "Pointer underflow");
                        } else if(program.pointerUnderflowBehaviour == 1){
                            pointer = mem.length - 1;
                        }
                    }
                    break;
                case '+':
                    mem[pointer]++;
                    if(mem[pointer] > program.maxValue) {
                        if(program.valueOverflowBehaviour == 0) {
                            pause();
                            error(pos, "Value overflow");
                        } else if(program.valueOverflowBehaviour == 1) {
                            mem[pointer] = program.minValue;
                        } else if(program.valueOverflowBehaviour == 2) {
                            mem[pointer] = program.maxValue;
                        }
                    }
                    break;
                case '-':
                    mem[pointer]--;
                    if(mem[pointer] < program.minValue) {
                        if(program.valueUnderflowBehaviour == 0) {
                            pause();
                            error(pos, "Value underflow");
                        } else if(program.valueUnderflowBehaviour == 1) {
                            mem[pointer] = program.maxValue;
                        } else if(program.valueUnderflowBehaviour == 2) {
                            mem[pointer] = program.minValue;
                        }
                    }
                    break;
                case '.':
                    io.output( Character.toString((char) mem[pointer]));
                    break;
                case ',':
                    waitingForInput = true;
                    io.getInput();
                    break;
                case '[':
                    if(mem[pointer] == 0) {
                        //Jump to matching ]
                        pos = loopPositions.get(pos);
                    }
                    break;
                case ']':
                    if(mem[pointer] != 0) {
                        pos = loopPositions.get(pos);
                    }
                    break;
                case '\\':
                    if(shouldReadBreakpoints) {
                        paused = true;
                        io.breakpoint();
                    }
                    break;
            }
            pos++;
        }

        void setInput(char in) {
            if(waitingForInput) {
                mem[pointer] = in;
                waitingForInput = false;
            }
        }

        void pause() {
            paused = true;
        }

        void unPause() {
            paused = false;
        }

        private void findLoopPositions() {
            loopPositions = new SparseIntArray();
            final Stack<Integer> openings = new Stack<>();
            for(int i = 0; i < program.prog.length(); i++) {
                if(program.prog.charAt(i) == '[') {
                    openings.push(i);
                } else if(program.prog.charAt(i) == ']') {
                    loopPositions.put(openings.peek(), i);
                    loopPositions.put(i, openings.pop());
                }
            }
        }

        SpannableString getDebugDump() {
            final StringBuilder builder = new StringBuilder();
            builder.append("\n\nDebug dump:\n\n");
            builder.append(program.debugDump());
            builder.append("\n\nInterpreter:\n-Pointer position: ");
            builder.append(pointer);
            builder.append("\nMemory:\n");
            if(program.pointerOverflowBehaviour == 2) {
                builder.append("\n-Actual memory size: ");
                builder.append(mem.length);
                builder.append("\n");
            }
            int lastUsedPos = 0;
            for(int i = 0; i < mem.length; i++) {
                if(mem[i] != 0 || i == mem.length-1) {
                    if(lastUsedPos < i-1) {
                        builder.append("-Indexes from ");
                        builder.append(lastUsedPos);
                        builder.append(" to ");
                        builder.append(i-1);
                        builder.append(" are empty.\n");
                    } else {
                        builder.append("-Index ");
                        builder.append(i);
                        builder.append(": ");
                        builder.append(mem[i]);
                        builder.append("\n");
                    }
                    lastUsedPos = i+1;
                }
            }
            final SpannableString sp = new SpannableString(builder.toString());
            sp.setSpan(new ForegroundColorSpan(Color.GREEN), 0, sp.length(), 0);
            return sp;
        }


    }

}
