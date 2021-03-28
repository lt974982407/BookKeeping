package com.example.bookkeeping

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class add_item_in : AppCompatActivity() {
    var keylist = ArrayList<keyborad>()

    val texts = arrayOf<String>("1","2","3","删除","4","5","6"," ","7","8","9"," ","清除","0",".","确认")
    val num = arrayOf<Int>(1,2,3,10,4,5,6,14,7,8,9,14,11,0,12,13)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item_in)
        init()//初始化
        val grid : GridView = findViewById(R.id.gridView2)//键盘实例

        var flag = true//判断是否已经输入过小数点
        var index = -1
        var reslist = arrayListOf<Int>()
        var input : TextView = findViewById(R.id.inputValue2)
        var res = "￥"
        var measure = 0.1//小数单位
        var ress = 0.00//输入的结果
        var username = intent.getStringExtra("username")//获取用户名
        var type = 9//选择的收入类型

        //键盘的响应函数
        grid.adapter = MyAdapter(this,keylist!!)
        grid.setOnItemClickListener { parent, view, position, id ->
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
                var turn  = 0
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
                var edit_des : EditText = findViewById(R.id.description_in)
                var des = edit_des.text.toString()//获取备注
                //在数据库中插入数据
                val bookKeeping = BookKeepingData(this,"example.db",1)
                val db = bookKeeping.writableDatabase
                db.execSQL("insert into Detail (username,time,amount,type,description) values(?,?,?,?,?)", arrayOf(username,calendar.toString(),ress.toString(),type.toString(),des))
                edit_des.setText("")
                ress = 0.0
            }

        }

        //多行选择器点击响应函数，更改类型
        var reg : MultiLineRadioGroup = findViewById(R.id.income_select)
        reg.setOnCheckedChangeListener(object: MultiLineRadioGroup.OnCheckedChangeListener{
            override fun onCheckedChanged(group: MultiLineRadioGroup?, checkedId: Int) {
                var add_hint : ImageView = findViewById(R.id.add_hint2)
                when (checkedId) {
                    R.id.income1 -> {
                        add_hint.setImageResource(R.drawable.classify_income_wage)
                        type = 8
                    }
                    R.id.income2 -> {
                        add_hint.setImageResource(R.drawable.classify_income_jiangjin)
                        type = 9
                    }
                    R.id.income3 -> {
                        add_hint.setImageResource(R.drawable.classify_income_baoxiao)
                        type = 10
                    }
                    R.id.income4 -> {
                        add_hint.setImageResource(R.drawable.classify_income_jianzhi)
                        type = 11
                    }
                    R.id.income5 -> {
                        add_hint.setImageResource(R.drawable.classify_income_redpacket)
                        type = 12
                    }
                    R.id.income6 -> {
                        add_hint.setImageResource(R.drawable.classify_income_stock)
                        type = 13
                    }
                    R.id.income7 -> {
                        add_hint.setImageResource(R.drawable.classify_income_gift)
                        type = 14
                    }
                    R.id.income8-> {
                        add_hint.setImageResource(R.drawable.classify_other)
                        type = 15
                    }
                    else -> "14"
                }
            }
        })

        var select_inout : RadioGroup = findViewById(R.id.select_inout2)
        select_inout.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.select_in2->{
                }
                R.id.select_out2->{
                    //跳转到支出界面
                    val intent = Intent(this,add_item::class.java)
                    intent.putExtra("username",username)
                    startActivity(intent)
                    this.finish()
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