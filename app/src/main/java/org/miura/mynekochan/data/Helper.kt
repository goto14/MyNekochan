package org.miura.mynekochan.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Helper(
    context: Context,
    databaseName: String,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, databaseName, factory, version) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("create table if not exists ImageData (id text primary key, image blob, flag integer, deleted integer)")
        db?.execSQL("create table if not exists Target (image blob)")
        db?.execSQL("create table if not exists Preference (disposed int)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}