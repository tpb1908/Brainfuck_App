package com.tpb.brainfuck_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by theo on 21/08/16.
 */
public class Storage extends SQLiteOpenHelper {
    private Storage instance;

    private static final String DATABASE_NAME = "PROGRAMS";
    private static final int VERSION = 1;
    private static final String KEY_ID = "ID";
    private static final String TABLE_PROGRAMS = "PROGRAMS";
    private static final String KEY_NAME = "NAME";
    private static final String KEY_DESC = "DESC";
    private static final String KEY_PROG = "PROG";
    private static final String KEY_MEM_SIZE = "MEM_SIZE";
    private static final String KEY_MIN_VALUE = "MIN_VALUE";
    private static final String KEY_MAX_VALUE = "MAX_VALUE";
    private static final String KEY_VALUE_OVERFLOW_BEHAVIOUR = "VALUE_OVERFLOW_BEHAVIOUR";
    private static final String KEY_VALUE_UNDERFLOW_BEHAVIOUR = "VALUE_UNDERFLOW_BEHAVIOUR";
    private static final String KEY_POINTER_OVERFLOW_BEHAVIOUR = "POINTER_OVERFLOW_BEHAVIOUR";
    private static final String KEY_POINTER_UNDERFLOW_BEHAVIOUR = "POINTER_UNDERFLOW_BEHAVIOUR";
    private static final String KEY_OUTPUT_SUFFIX = "OUTPUT_SUFFIX";
    private static final String[] PROGRAM_COLUMNS = { KEY_ID, KEY_NAME, KEY_DESC, KEY_PROG, KEY_MEM_SIZE, KEY_MIN_VALUE, KEY_MAX_VALUE, KEY_VALUE_OVERFLOW_BEHAVIOUR, KEY_VALUE_UNDERFLOW_BEHAVIOUR, KEY_POINTER_OVERFLOW_BEHAVIOUR, KEY_POINTER_UNDERFLOW_BEHAVIOUR, KEY_OUTPUT_SUFFIX };


    public Storage instance(Context context) {
        if(instance == null) {
            instance = new Storage(context);
        }
        return instance;
    }

    private Storage(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_PROGRAMS +
                "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_NAME + " VARCHAR, " +
                KEY_DESC + " VARCHAR, " +
                KEY_PROG + " VARCHAR, " +
                KEY_MEM_SIZE + " INTEGER, " +
                KEY_MAX_VALUE + " INTEGER, " +
                KEY_MIN_VALUE + " INTEGER, " +
                KEY_VALUE_OVERFLOW_BEHAVIOUR + " INTEGER, " +
                KEY_VALUE_UNDERFLOW_BEHAVIOUR + " INTEGER, " +
                KEY_POINTER_OVERFLOW_BEHAVIOUR + " INTEGER, " +
                KEY_POINTER_UNDERFLOW_BEHAVIOUR + " INTEGER, " +
                KEY_OUTPUT_SUFFIX + " VARCHAR )";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void add(Program program) {
        final SQLiteDatabase db = this.getWritableDatabase();
        final ContentValues values = new ContentValues();
        values.put(KEY_NAME, program.name);
        values.put(KEY_DESC, program.desc);
        values.put(KEY_PROG, program.prog);
        values.put(KEY_MEM_SIZE, program.memSize);
        values.put(KEY_MAX_VALUE, program.maxValue);
        values.put(KEY_MIN_VALUE, program.minValue);
        values.put(KEY_VALUE_OVERFLOW_BEHAVIOUR, program.valueOverflowBehaviour);
        values.put(KEY_VALUE_UNDERFLOW_BEHAVIOUR, program.valueUnderflowBehaviour);
        values.put(KEY_POINTER_OVERFLOW_BEHAVIOUR, program.pointerOverflowBehaviour);
        values.put(KEY_POINTER_UNDERFLOW_BEHAVIOUR, program.pointerUnderflowBehaviour);
        values.put(KEY_OUTPUT_SUFFIX, program.outputSuffix);
        db.insert(TABLE_PROGRAMS, null, values);
    }

    public void update(Program program) {
        final SQLiteDatabase db = this.getWritableDatabase();
        final ContentValues values = new ContentValues();
        values.put(KEY_NAME, program.name);
        values.put(KEY_DESC, program.desc);
        values.put(KEY_PROG, program.prog);
        values.put(KEY_MEM_SIZE, program.memSize);
        values.put(KEY_MAX_VALUE, program.maxValue);
        values.put(KEY_MIN_VALUE, program.minValue);
        values.put(KEY_VALUE_OVERFLOW_BEHAVIOUR, program.valueOverflowBehaviour);
        values.put(KEY_VALUE_UNDERFLOW_BEHAVIOUR, program.valueUnderflowBehaviour);
        values.put(KEY_POINTER_OVERFLOW_BEHAVIOUR, program.pointerOverflowBehaviour);
        values.put(KEY_POINTER_UNDERFLOW_BEHAVIOUR, program.pointerUnderflowBehaviour);
        values.put(KEY_OUTPUT_SUFFIX, program.outputSuffix);
        db.update(TABLE_PROGRAMS,
                values,
                KEY_ID + " = " + program.id,
                null);
    }

    public void remove(Program program) {
        final SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABLE_PROGRAMS,
                KEY_ID + " = " + program.id,
                null);
    }

    public Program get(int id) {
        final Program prog = new Program();
        prog.id = id;
        final SQLiteDatabase db = this.getReadableDatabase();
        final Cursor cursor = db.query(TABLE_PROGRAMS,
                PROGRAM_COLUMNS,
                KEY_ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                null);
        if(cursor != null) {
            cursor.moveToFirst();
            prog.name = cursor.getString(1);
            prog.desc = cursor.getString(2);
            prog.prog = cursor.getString(3);
            prog.memSize = cursor.getInt(4);
            prog.maxValue = cursor.getInt(5);
            prog.minValue = cursor.getInt(6);
            prog.valueOverflowBehaviour = cursor.getInt(7);
            prog.valueUnderflowBehaviour = cursor.getInt(8);
            prog.pointerOverflowBehaviour = cursor.getInt(9);
            prog.pointerUnderflowBehaviour = cursor.getInt(10);
            prog.outputSuffix = cursor.getString(11);
            cursor.close();
        }

        return prog;
    }

    public ArrayList<Program> getAll() {
        return null;
    }


}
