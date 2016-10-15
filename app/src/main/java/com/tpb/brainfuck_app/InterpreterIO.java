package com.tpb.brainfuck_app;
/**
 * Created by theo on 25/08/16.
 */
public interface InterpreterIO {

    void output(char out);

    void getInput();

    void error(int pos, String error);

    void breakpoint();

}
