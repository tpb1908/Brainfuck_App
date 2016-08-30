package com.tpb.brainfuck_app;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by theo on 23/08/16.
 */
public class Program implements Parcelable {
    int id;
    String name;
    String desc;
    String prog;

    String outputSuffix = "";
    int memSize = 10000; //Size of the memory array
    int maxValue = (int)1E6; //Maximum value in a cell
    int minValue = 0; //Minimum value in a cell
    int valueOverflowBehaviour = 0; //Error on value overflow, wrap, or cap
    int valueUnderflowBehaviour = 0;
    int pointerOverflowBehaviour = 0; //On memory overflow, error, wrap, expand
    int pointerUnderflowBehaviour = 0;

    public Program() {}



    protected Program(Parcel in) {
        id = in.readInt();
        name = in.readString();
        desc = in.readString();
        prog = in.readString();
        outputSuffix = in.readString();
        memSize = in.readInt();
        maxValue = in.readInt();
        minValue = in.readInt();
        valueOverflowBehaviour = in.readInt();
        valueUnderflowBehaviour = in.readInt();
        pointerOverflowBehaviour = in.readInt();
        pointerUnderflowBehaviour = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(desc);
        dest.writeString(prog);
        dest.writeString(outputSuffix);
        dest.writeInt(memSize);
        dest.writeInt(maxValue);
        dest.writeInt(minValue);
        dest.writeInt(valueOverflowBehaviour);
        dest.writeInt(valueUnderflowBehaviour);
        dest.writeInt(pointerOverflowBehaviour);
        dest.writeInt(pointerUnderflowBehaviour);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Program> CREATOR = new Parcelable.Creator<Program>() {
        @Override
        public Program createFromParcel(Parcel in) {
            return new Program(in);
        }

        @Override
        public Program[] newArray(int size) {
            return new Program[size];
        }
    };

    @Override
    public String toString() {
        return "Program{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                ", prog='" + prog + '\'' +
                ", outputSuffix='" + outputSuffix + '\'' +
                ", memSize=" + memSize +
                ", maxValue=" + maxValue +
                ", minValue=" + minValue +
                ", valueOverflowBehaviour=" + valueOverflowBehaviour +
                ", valueUnderflowBehaviour=" + valueUnderflowBehaviour +
                ", pointerOverflowBehaviour=" + pointerOverflowBehaviour +
                ", pointerUnderflowBehaviour=" + pointerUnderflowBehaviour +
                '}';
    }
}
