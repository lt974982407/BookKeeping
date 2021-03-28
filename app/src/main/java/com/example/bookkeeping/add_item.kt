package com.example.bookkeeping

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import java.math.BigDecimal
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class add_item : AppCompatActivity() {

    var keylist = ArrayList<keyborad>()

    val texts = arrayOf<String>("1","2","3","删除","4","5","6"," ","7","8","9"," ","清除","0",".","确认")
    val num = arrayOf<Int>(1,2,3,10,4,5,6,14,7,8,9,14,11,0,12,13)
    var type = 0//选择的支出类型

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)
        init()//初始化
        val grid : GridView = findViewById(R.id.gridView)//多行选择器实例化（键盘）

        var flag = true//判断是否已经输入过小数点
        var index = -1
        var reslist = arrayListOf<Int>()
        var input : TextView = findViewById(R.id.inputValue)
        var res = "￥"
        var measure = 0.1//小数单位
        var ress = 0.00//输入的结果
        var username = intent.getStringExtra("username")//获取用户名

        //键盘的响应函数
        grid.adapter = MyAdapter(this,keylist!!)
        grid.setOnItemClickListener { parent, view, position, id ->
            //点击数字0-9
            if (num[position] >= 0 && num[position] <= 9) {
                if (flag) {//没有输入小数点
                    reslist.add(num[position])
                    index++
                    res = res + num[position].toString()
                    input.setText(res)
                } else {//输入过小数点
                    reslist.add(num[position])
                    index++
                    res = res + num[position].toString()
                    input.setText(res)
                }
            }
            //点击删除
            if (num[position] == 10) {
               if(index >= 0) {//显示是否为空
                   if(reslist[index] == 20)//是否删除小数点
                       flag = true
                    reslist.removeAt(index)
                    index--
                    res = res.substring(0, index + 2)
                    input.setText(res)
                }
            }
            //点击清除
            if (num[position] == 11) {
                reslist.clear()
                res = "￥"
                ress = 0.0
                input.setText("")
                index = -1
                flag = true
            }
            //点击小数点
            if (num[position] == 12) {
                if (flag) {
                    if (index == -1){
                        index++
                        reslist.add(0)
                        res = res + "0"
                    }
                    flag = false
                    reslist.add(20)
                    index++
                    res = res + "."
                    input.setText(res)
                }
            }
            //点击确认
            if (num[position] == 13){
                var flag1 = true
                for (i in 0 .. index) {
                    if (flag1) {
                        if (reslist[i] != 20) {
                            ress = ress * 10 + (reslist[i] )
                        } else {
                            flag1 = false
                        }
                    } else {
                        ress = ress + measure * (reslist[i] )
                        measure = measure * 0.1
                    }
                }
                //全部清零
                reslist.clear()
                input.setText("")
                index = -1
                flag = true
                res = "￥"
                measure = 0.1
                var resss = BigDecimal(ress).setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()//保留小数点后两位
                val calendar = Calendar.getInstance().time.time//获取记录时间
                resss = -resss//支出为负
                var edit_des : EditText = findViewById(R.id.description_out)
                var des = edit_des.text.toString()//获取备注
                //在数据库中插入数据
                val bookKeeping = BookKeepingData(this,"example.db",1)
                val db = bookKeeping.writableDatabase
                db.execSQL("insert into Detail (username,time,amount,type,description) values(?,?,?,?,?)", arrayOf(username,calendar.toString(),resss.toString(),type.toString(),des))
                edit_des.setText("")
                ress = 0.0
            }

        }

        //多行选择器点击响应函数，更改类型
        var reg : MultiLineRadioGroup = findViewById(R.id.outcome_select)
        reg.setOnCheckedChangeListener(object: MultiLineRadioGroup.OnCheckedChangeListener{
            override fun onCheckedChanged(group: MultiLineRadioGroup?, checkedId: Int) {
                var add_hint : ImageView = findViewById(R.id.add_hint)
                 when (checkedId) {
                    R.id.outcome1 -> {
                        add_hint.setImageResource(R.drawable.classify_traffic)
                        type = 0
                    }
                    R.id.outcome2 -> {
                        add_hint.setImageResource(R.drawable.classify_eat)
                        type = 1
                    }
                    R.id.outcome3 -> {
                        add_hint.setImageResource(R.drawable.classify_cloth)
                        type = 2
                    }
                    R.id.outcome4 -> {
                        add_hint.setImageResource(R.drawable.classify_edu)
                        type = 3
                    }
                    R.id.outcome5 -> {
                        add_hint.setImageResource(R.drawable.classify_game)
                        type = 4
                    }
                    R.id.outcome6 -> {
                        add_hint.setImageResource(R.drawable.classify_fruit)
                        type = 5
                    }
                    R.id.outcome7 -> {
                        add_hint.setImageResource(R.drawable.classify_doctor)
                        type = 6
                    }
                    R.id.outcome8-> {
                        add_hint.setImageResource(R.drawable.classify_other)
                        type = 7
                    }
                    else -> "14"
                }
            }
        })

        var select_inout : RadioGroup = findViewById(R.id.select_inout)
        select_inout.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.select_in->{
                    //跳转到收入界面
                    val intent = Intent(this,add_item_in::class.java)
                    intent.putExtra("username",username)
                    startActivity(intent)
                    this.finish()
                }
                R.id.select_out->{
                }
            }
        }
    }

    //初始化
    private fun init(){
        for (i in 0..15){
            keylist?.add(keyborad(texts[i]))
        }
    }

    //设置焦点不在键盘上，键盘消失
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }
}
