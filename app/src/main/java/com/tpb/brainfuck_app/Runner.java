package com.tpb.brainfuck_app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

    private TextView mOutput;
    private EditText mInput;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.runner);
        mOutput = (TextView) findViewById(R.id.output);
        mInput = (EditText) findViewById(R.id.input);
        program = new Program();
        program.prog = "-,+[ -[ >>++++[>++++++++<-] <+<-[ >+>+>-[>>>] <[[>+<-]>>+>] <<<<<- ] ]>>>[-]+ >--[-[<->[-]]]<[ ++++++++++++<[ >-[>+>>] >[+[<+>-]>+>>] <<<<<- ] >>[<+>-] >[ -[ -<<[-]>> ]<<[<<->>-]>> ]<<[<<+>>-] ] <[-] <.[-] <-,+ ]";
        program.outputSuffix = "\n";
        inter = new Interpreter(this, program);
        thread = new Thread(inter);
        thread.start();

    }

    @Override
    public void output(final char out) {
        Log.i(TAG, "output: " + out);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mOutput.append(out + program.outputSuffix);
            }
        });

    }

    @Override
    public void input() {
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
        private int stepCounter; //Rudimentary way to detect infinite loops
        private int[] mem;
        private int pos;
        private int pointer;
        private ArrayList<Loop> loops;
        private boolean waitingForInput = false;

        public Interpreter(InterpreterIO io, Program program) {
            this.io = io;
            this.program = program;
        }

        @Override
        public void run() {
            stepCounter = 0;
            mem = new int[program.defaultSize];
            pos = 0;
            pointer = 0;
            loops = new ArrayList<>();
            findLoopPositions();
            while(pos < program.prog.length()) {
                if(waitingForInput) {
                    try {
                        Thread.sleep(100);
                    } catch(InterruptedException e) {}
                } else {
                    step();
                }
            }
        }

        private void step() {
            Log.i(TAG, "step: " + pos + " at " + program.prog.charAt(pos));
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
                    io.input();
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
            }
            pos++;
        }

        public void setInput(char in) {
            mem[pointer] = in;
            waitingForInput = false;
        }


        public int closingPos(int left) {
            for(Loop l : loops) {
                if(l.left == left) return l.right;
            }
            return -1;
        }

        public int openingPos(int right) {
            for(Loop l : loops) {
                if(l.right == right) return l.left;
            }
            return -1;
        }

        public void findLoopPositions() {
            final Stack<Integer> openings = new Stack<>();
            for(int i = 0; i < program.prog.length(); i++) {
                if(program.prog.charAt(i) == '[') {
                    openings.push(i);
                } else if(program.prog.charAt(i) == ']') {
                    loops.add(new Loop(openings.pop(), i));
                }
            }
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
