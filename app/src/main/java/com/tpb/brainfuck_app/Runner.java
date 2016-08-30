package com.tpb.brainfuck_app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.runner);
        mOutput = (TextView) findViewById(R.id.output);
        mInput = (EditText) findViewById(R.id.input);
        mPlayPauseButton = (ImageButton) findViewById(R.id.play_pause_button);
        program = getIntent().getParcelableExtra("prog");
        if(program.name != null && program.name.length() > 0) {
            setTitle(program.name);
        } else {
            setTitle("Runner");
        }
        startProgram();
    }

    private void startProgram() {
        program.outputSuffix = "\n";
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
                Log.i(TAG, "afterTextChanged: Scrolling to bottom");
            }
        });
    }

    public void dump(View v) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mOutput.append(inter.getDebugDump());
            }
        });
    }

    public void togglePause(View v) {
        if(paused) {
            inter.unPause();
            mOutput.append("\nUnpaused\n");
            mPlayPauseButton.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_pause_black));
        } else {
            inter.pause();
            mOutput.append("\nPaused\n");
            mPlayPauseButton.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_play_arrow_black));
        }
        paused = !paused;
    }

    @Override
    public void breakpoint() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                paused = true;
                mPlayPauseButton.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_play_arrow_black));
                mOutput.append("Hit breakpoint");
            }
        });

    }

    public void step(View v) {
        inter.step();
    }

    public void restart(View v) {
        thread.interrupt();
        mOutput.setText("");
        startProgram();
    }

    @Override
    public void output(final char out) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mOutput.append(out + program.outputSuffix);
            }
        });
    }

    @Override
    public void getInput() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mOutput.append("Input: ");
            }
        });
    }

    public void input(View v) {
        final String input = mInput.getText().toString();
        if(input.length() > 0) {
            mInput.setError(null);
            final char in = input.charAt(0);
            mOutput.append(in + "\n");
            inter.setInput(in);
        } else {
            mInput.setError("Input a character");
        }
    }

    private class Interpreter implements Runnable {
        private InterpreterIO io;
        private Program program;
        private int[] mem;
        private int pos;
        private int pointer;
        private ArrayList<Loop> loops;
        private boolean waitingForInput = false;
        private boolean paused = false;

        public Interpreter(InterpreterIO io, Program program) {
            this.io = io;
            this.program = program;
        }

        @Override
        public void run() {
            mem = new int[program.defaultSize];
            pos = 0;
            pointer = 0;
            loops = new ArrayList<>();
            findLoopPositions();
            while(pos < program.prog.length()) {
                if(paused || waitingForInput) {
                    try {
                        Thread.sleep(100);
                    } catch(InterruptedException e) {}
                } else {
                    step();
                }
            }
        }

        public void step() {
            switch(program.prog.charAt(pos)) {
                case '>':
                    pointer++;
                    break;
                case '<':
                    pointer--;
                    break;
                case '+':
                    mem[pointer]++;
                    break;
                case '-':
                    mem[pointer]--;
                    break;
                case '.':
                    io.output((char)mem[pointer]);
                    break;
                case ',':
                    waitingForInput = true;
                    io.getInput();
                    break;
                case '[':
                    if(mem[pointer] == 0) {
                        //Jump to matching ]
                        pos = closingPos(pos);
                    }
                    break;
                case ']':
                    if(mem[pointer] != 0) {
                        pos = openingPos(pos);
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

        public void setInput(char in) {
            if(waitingForInput) {
                mem[pointer] = in;
                waitingForInput = false;
            }
        }

        public void pause() {
            paused = true;
        }

        public void unPause() {
            paused = false;
        }


        private int closingPos(int left) {
            for(Loop l : loops) {
                if(l.left == left) return l.right;
            }
            return -1;
        }

        private int openingPos(int right) {
            for(Loop l : loops) {
                if(l.right == right) return l.left;
            }
            return -1;
        }

        private void findLoopPositions() {
            final Stack<Integer> openings = new Stack<>();
            for(int i = 0; i < program.prog.length(); i++) {
                if(program.prog.charAt(i) == '[') {
                    openings.push(i);
                } else if(program.prog.charAt(i) == ']') {
                    loops.add(new Loop(openings.pop(), i));
                }
            }
        }

        public String getDebugDump() {
            final StringBuilder builder = new StringBuilder();
            builder.append("\n\nDebug dump:\n\n");
            builder.append("Pointer position: ");
            builder.append(pointer);
            builder.append("\n");
            int lastUsedPos = 0;
            for(int i = 0; i < mem.length; i++) {
                if(mem[i] != 0 || i == mem.length-1) {
                    if(lastUsedPos < i-1) {
                        builder.append("Indexes from ");
                        builder.append(lastUsedPos);
                        builder.append(" to ");
                        builder.append(i-1);
                        builder.append(" are empty.\n");
                    } else {
                        builder.append("Index ");
                        builder.append(i);
                        builder.append(": ");
                        builder.append(mem[i]);
                        builder.append("\n");
                    }
                    lastUsedPos = i+1;
                }
            }
            return builder.toString();
        }

        private class Loop {
            int left;
            int right;

            Loop(int l, int r) {
                left = l;
                right = r;
            }
        }
    }

}
