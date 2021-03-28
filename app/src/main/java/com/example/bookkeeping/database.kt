package com.example.bookkeeping

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

//数据库类
class BookKeepingData (val context : Context, name : String, version: Int) :
        SQLiteOpenHelper(context, name,null, version){
    private val createuser = "create table User ("+
            "username text primary key,"+
            "password text)"
    private val createdetail = "create table Detail ("+
            "username text,"+
            "time integer,"+
            "amount real,"+
            "type integer,"+
            "description text,"+
            "primary key (username,time) )"


    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(createuser)//创建User表
        db?.execSQL(createdetail)//创建Detail表
        Toast.makeText(context,"Create successfully!",Toast.LENGTH_SHORT).show()
    }

    //更新数据库的函数，本程序并未用到
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }
}