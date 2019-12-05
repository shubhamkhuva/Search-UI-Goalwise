package com.shubhamkhuva.searchtaskgoalwise.helper

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table $TABLE_NAME ($COL_1 TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertData(search: String?) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_1, search)
        val result = db.insert(TABLE_NAME, null, contentValues)
    }

    val allData: Cursor
        get() {
            val db = this.writableDatabase
            return db.rawQuery("select * from $TABLE_NAME", null)
        }

    companion object {
        const val DATABASE_NAME = "searched.db"
        const val TABLE_NAME = "searched_master"
        const val COL_1 = "search"
    }
}