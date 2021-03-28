package com.example.bookkeeping

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class detail : AppCompatActivity() {
    private val itemList = ArrayList<item_detail>()
    private val image = arrayOf<Int>(R.drawable.classify_traffic,R.drawable.classify_eat,R.drawable.classify_cloth,R.drawable.classify_edu,R.drawable.classify_game,R.drawable.classify_fruit,R.drawable.classify_doctor,R.drawable.classify_other
            ,R.drawable.classify_income_wage,R.drawable.classify_income_jiangjin,R.drawable.classify_income_baoxiao,R.drawable.classify_income_jianzhi,R.drawable.classify_income_redpacket,R.drawable.classify_income_stock,R.drawable.classify_income_gift,R.drawable.classify_other)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        var username = intent.getStringExtra("username")
        init(username)//初始化
        //创建RecyclerView实例
        val layoutManager = LinearLayoutManager(this)
        val recyle : RecyclerView = findViewById(R.id.detail_show)
        recyle.layoutManager = layoutManager
        val adapter = itemDetailAdapter(itemList)
        recyle.adapter = adapter
    }

    fun init(user: String?){
        //查询数据
        val bookKeeping = BookKeepingData(this,"example.db",1)
        val db = bookKeeping.writableDatabase
        val cursor = db.rawQuery("select * from Detail where username = ?", arrayOf(user))
        //读取数据并装入数组
        if(cursor.moveToFirst()){
            do{
                val time = cursor.getLong(cursor.getColumnIndex("time"))
                val amount = cursor.getDouble(cursor.getColumnIndex("amount"))
                val type = cursor.getInt(cursor.getColumnIndex("type"))
                val des = cursor.getString(cursor.getColumnIndex("description"))
                itemList.add(item_detail(image[type],amount,des,time))
            }while(cursor.moveToNext())
        }
        cursor.close()
    }
}