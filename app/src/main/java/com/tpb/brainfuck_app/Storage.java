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
    private static final String KEY_DEFAULT_SIZE = "DEFAULT_SIZE";
    private static final String KEY_DEFAULT_MIN = "DEFAULT_MIN";
    private static final String KEY_DEFAULT_MAX = "DEFAULT_MAX";
    private static final String KEY_OVERFLOW_BEHAVIOUR = "OVERFLOW_BEHAVIOUR";
    private static final String KEY_UNDERFLOW_BEHAVIOUR = "UNDERFLOW_BEHAVIOUR";
    private static final String KEY_SIZE_OVERFLOW_BEHAVIOUR = "SIZE_OVERFLOW_BEHAVIOUR";
    private static final String KEY_SIZE_UNDERFLOW_BEHAVIOUR = "SIZE_UNDERFLOW_BEHAVIOUR";
    private static final String[] PROGRAM_COLUMNS = { KEY_ID, KEY_NAME, KEY_DESC, KEY_PROG, KEY_DEFAULT_SIZE, KEY_DEFAULT_MIN, KEY_DEFAULT_MAX, KEY_OVERFLOW_BEHAVIOUR, KEY_UNDERFLOW_BEHAVIOUR, KEY_SIZE_OVERFLOW_BEHAVIOUR, KEY_SIZE_UNDERFLOW_BEHAVIOUR };


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
                KEY_DEFAULT_SIZE + " INTEGER, " +
                KEY_DEFAULT_MAX + " INTEGER, " +
                KEY_DEFAULT_MIN + " INTEGER, " +
                KEY_OVERFLOW_BEHAVIOUR + " BOOLEAN, " +
                KEY_UNDERFLOW_BEHAVIOUR + " BOOLEAN, " +
                KEY_SIZE_OVERFLOW_BEHAVIOUR + " INTEGER, " +
                KEY_SIZE_UNDERFLOW_BEHAVIOUR + " INTEGER )";
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
        values.put(KEY_DEFAULT_SIZE, program.defaultSize);
        values.put(KEY_DEFAULT_MAX, program.defaultMax);
        values.put(KEY_DEFAULT_MIN, program.defaultMin);
        values.put(KEY_OVERFLOW_BEHAVIOUR, program.overflowBehaviour);
        values.put(KEY_UNDERFLOW_BEHAVIOUR, program.underflowBehaviour);
        values.put(KEY_SIZE_OVERFLOW_BEHAVIOUR, program.sizeOverflowBehaviour);
        values.put(KEY_SIZE_UNDERFLOW_BEHAVIOUR, program.sizeUnderflowBehaviour);
        db.insert(TABLE_PROGRAMS, null, values);
    }

    public void update(Program program) {
        final SQLiteDatabase db = this.getWritableDatabase();
        final ContentValues values = new ContentValues();
        values.put(KEY_NAME, program.name);
        values.put(KEY_DESC, program.desc);
        values.put(KEY_PROG, program.prog);
        values.put(KEY_DEFAULT_SIZE, program.defaultSize);
        values.put(KEY_DEFAULT_MAX, program.defaultMax);
        values.put(KEY_DEFAULT_MIN, program.defaultMin);
        values.put(KEY_OVERFLOW_BEHAVIOUR, program.overflowBehaviour);
        values.put(KEY_UNDERFLOW_BEHAVIOUR, program.underflowBehaviour);
        values.put(KEY_SIZE_OVERFLOW_BEHAVIOUR, program.sizeOverflowBehaviour);
        values.put(KEY_SIZE_UNDERFLOW_BEHAVIOUR, program.sizeUnderflowBehaviour);
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
            prog.defaultSize = cursor.getInt(4);
            prog.defaultMax = cursor.getInt(5);
            prog.defaultMin = cursor.getInt(6);
            prog.overflowBehaviour = cursor.getInt(7) > 0;
            prog.underflowBehaviour = cursor.getInt(8) > 0;
            prog.sizeOverflowBehaviour = cursor.getInt(9);
            prog.sizeUnderflowBehaviour = cursor.getInt(10);
            cursor.close();
        }

        return prog;
    }

    public ArrayList<Program> getAll() {
        return null;
    }


}
