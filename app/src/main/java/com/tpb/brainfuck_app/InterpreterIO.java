package com.tpb.brainfuck_app;
/**
 * Created by theo on 25/08/16.
 */
public interface InterpreterIO {

    void output(String out);

    void getInput();

    void error(int pos, String error);

    void breakpoint();

}
