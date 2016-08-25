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
    int defaultSize = 10000; //Size of the memory array
    int defaultMax = (int)1E6; //Maximum value in a cell
    int defaultMin = 0; //Minimum value in a cell
    boolean overflowBehaviour = false; //Error on overflow, or wrap
    boolean underflowBehaviour = false;
    int sizeOverflowBehaviour = 0; //On memory overflow, error, wrap, expand
    int sizeUnderflowBehaviour = 0;

    public Program() {}



    protected Program(Parcel in) {
        id = in.readInt();
        name = in.readString();
        desc = in.readString();
        prog = in.readString();
        outputSuffix = in.readString();
        defaultSize = in.readInt();
        defaultMax = in.readInt();
        defaultMin = in.readInt();
        overflowBehaviour = in.readByte() != 0x00;
        underflowBehaviour = in.readByte() != 0x00;
        sizeOverflowBehaviour = in.readInt();
        sizeUnderflowBehaviour = in.readInt();
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
        dest.writeInt(defaultSize);
        dest.writeInt(defaultMax);
        dest.writeInt(defaultMin);
        dest.writeByte((byte) (overflowBehaviour ? 0x01 : 0x00));
        dest.writeByte((byte) (underflowBehaviour ? 0x01 : 0x00));
        dest.writeInt(sizeOverflowBehaviour);
        dest.writeInt(sizeUnderflowBehaviour);
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

}
