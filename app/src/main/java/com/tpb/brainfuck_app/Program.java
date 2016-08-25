package com.tpb.brainfuck_app;

/**
 * Created by theo on 23/08/16.
 */
public class Program {
    int id;
    String name;
    String desc;
    String prog;

    String outputSuffix = "";
    int defaultSize = 10000; //Size of the memory array
    int defaultMax = (int)1E6; //Maximum value in a cell
    int defaultMin = 0; //Minimum value in a cell
    boolean overflowBehaviour = false; //Error on overflow, or wrap
    boolean underflowBehaviour = false;
    int sizeOverflowBehaviour = 0; //On memory overflow, error, wrap, expand
    int sizeUnderflowBehaviour = 0;



}
