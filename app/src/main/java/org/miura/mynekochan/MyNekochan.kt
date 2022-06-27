package org.miura.mynekochan

import android.app.Application
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.miura.mynekochan.data.Helper

class MyNekochan : Application() {
    companion object {
        lateinit var instance: MyNekochan
    }

    private lateinit var helper: SQLiteOpenHelper
    private var mRDb: SQLiteDatabase? = null
    val rDb: SQLiteDatabase
        get() {
            if (mRDb?.isOpen != true) {
                mRDb = helper.readableDatabase
            }
            return mRDb!!
        }

    private var mWDb: SQLiteDatabase? = null
    val wDb: SQLiteDatabase
        get() {
            if (mWDb?.isOpen != true) {
                mWDb = helper.writableDatabase
            }
            return mWDb!!
        }

    override fun onCreate() {
        super.onCreate()
        instance = this
        helper = Helper(applicationContext, "neko", null, 1)
    }
}