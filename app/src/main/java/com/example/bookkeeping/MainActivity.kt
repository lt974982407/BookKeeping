package com.example.bookkeeping

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.sql.Timestamp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.contracts.Returns


class MainActivity : AppCompatActivity() {

    var selece_id = 1//存储用户选择，默认为周

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var username = intent.getStringExtra("username")//获取用户名
        var tt = getWeekStartTime(0)//获取本周开始时间

        val cal1 = Calendar.getInstance().time.time//获取现在的时间戳
        val cal2 = getWeekStartTime1().time.time//获取本周开始的时间戳

        setText(username,cal2,cal1)//显示统计结果

        //显示开始的时间
        val date_hint : TextView = findViewById(R.id.date_hint)
        date_hint.setText("从"+tt+"开始")

        //单选按钮响应事件
        val radio : RadioGroup = findViewById(R.id.select_time)
        radio.setOnCheckedChangeListener{group, checkedId ->
            when(checkedId){
                R.id.select_week ->{
                    selece_id = 1
                    date_hint.setText("从"+tt+"开始")
                    val calendar1 = Calendar.getInstance().time.time
                    val calendar2 = getWeekStartTime1().time.time
                    setText(username,calendar2,calendar1)//更改统计结果为周
                }
                R.id.select_month ->{
                    selece_id = 2
                    //获取开始、结束时间
                    val calendar1 = Calendar.getInstance()
                    val calendar2 = Calendar.getInstance()
                    calendar2.set(Calendar.DAY_OF_MONTH,1)
                    var year = calendar2[Calendar.YEAR]
                    var month = calendar2[Calendar.MONTH]
                    var day = calendar2[Calendar.DAY_OF_MONTH]
                    val mDate = "${year}/${month + 1}/${day}"
                    calendar2.set(year,month,day,0,0,0)
                    date_hint.setText("从"+mDate+"开始")
                    val start = calendar2.time.time
                    val end = calendar1.time.time
                    setText(username,start, end)//更改统计结果为月
                }
                R.id.select_year -> {
                    selece_id = 3
                    //获取开始、结束时间
                    val calendar1 = Calendar.getInstance()
                    val calendar2 = Calendar.getInstance()
                    calendar2.set(Calendar.DAY_OF_MONTH,1)
                    calendar2.set(Calendar.MONTH,0)
                    var year = calendar2[Calendar.YEAR]
                    var month = calendar2[Calendar.MONTH]
                    var day = calendar2[Calendar.DAY_OF_MONTH]
                    val mDate = "${year}/${month + 1}/${day}"
                    calendar2.set(year,month,day,0,0,0)
                    date_hint.setText("从"+mDate+"开始")
                    val start = calendar2.time.time
                    val end = calendar1.time.time
                    setText(username,start, end)//更改统计结果为周年
                }
            }
        }

        //日期选择按钮响应事件
        val button1: ImageButton = findViewById(R.id.date_picker)
        button1.setOnClickListener {
            //获取用户选择的时间
            val ca = Calendar.getInstance()
            var mYear = ca[Calendar.YEAR]
            var mMonth = ca[Calendar.MONTH]
            var mDay = ca[Calendar.DAY_OF_MONTH]
            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    mYear = year
                    mMonth = month
                    mDay = dayOfMonth
                    val mDate = "${year}/${month + 1}/${dayOfMonth}"
                    date_hint.setText("从"+mDate+"开始")
                    val calendar1 = Calendar.getInstance()
                    calendar1.set(mYear,mMonth,mDay,0,0,0)
                    val calendar2 = Calendar.getInstance()
                    calendar2.set(mYear,mMonth,mDay,23,59,59)
                    //根据用户选择的单位分情况统计
                    when(selece_id){
                        1 -> {
                            calendar2.add(Calendar.DAY_OF_MONTH,7)//下一周的今天
                            val start = calendar1.time.time
                            val end = calendar2.time.time
                            setText(username,start, end)
                        }
                        2 -> {
                            calendar2.add(Calendar.MONTH,1)//下一月的今天
                            val start = calendar1.time.time
                            val end = calendar2.time.time
                            setText(username,start, end)
                        }
                        3->{
                            calendar1.add(Calendar.YEAR,1)//下一年的今天
                            val start = calendar1.time.time
                            val end = calendar2.time.time
                            setText(username,start, end)
                        }
                    }
                },
                mYear, mMonth, mDay
            )
            datePickerDialog.show()
        }

        //添加按钮响应事件
        val button2 : ImageButton = findViewById(R.id.add_button)
        button2.setOnClickListener {
            val intent = Intent(this, add_item::class.java)
            intent.putExtra("username",username)
            startActivity(intent)
        }
    }


    //创建菜单
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    //点击菜单
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            //转到详情界面
            R.id.detail_itemm ->{
                var username = intent.getStringExtra("username")
                val intent = Intent(this,detail::class.java)
                intent.putExtra("username",username)
                startActivity(intent)
            }
            //退出登录，转到登录界面
            R.id.exit_item ->{
                val intent2 = Intent(this,login::class.java)
                startActivity(intent2)
                this.finish()
            }
        }
        return true
    }

    //重写onRestart函数，使得从其他Activity回到本Activity时，统计的数据得到更新
    override fun onRestart() {
        super.onRestart()
        val date_hint : TextView = findViewById(R.id.date_hint)
        var username = intent.getStringExtra("username")

        //分情况统计，逻辑和多项选择器相同
        when(selece_id){
            1 ->{
                var tt = getWeekStartTime(0)
                date_hint.setText("从"+tt+"开始")
                val calendar1 = Calendar.getInstance().time.time
                val calendar2 = getWeekStartTime1().time.time
                setText(username,calendar2,calendar1)
            }

            2-> {
                val calendar1 = Calendar.getInstance()
                val calendar2 = Calendar.getInstance()
                calendar2.set(Calendar.DAY_OF_MONTH,1)
                var year = calendar2[Calendar.YEAR]
                var month = calendar2[Calendar.MONTH]
                var day = calendar2[Calendar.DAY_OF_MONTH]
                val mDate = "${year}/${month + 1}/${day}"
                calendar2.set(year,month,day,0,0,0)
                date_hint.setText("从"+mDate+"开始")
                val start = calendar2.time.time
                val end = calendar1.time.time
                setText(username,start, end)
            }

            3->{
                val calendar1 = Calendar.getInstance()
                val calendar2 = Calendar.getInstance()
                calendar2.set(Calendar.DAY_OF_MONTH,1)
                calendar2.set(Calendar.MONTH,0)
                var year = calendar2[Calendar.YEAR]
                var month = calendar2[Calendar.MONTH]
                var day = calendar2[Calendar.DAY_OF_MONTH]
                val mDate = "${year}/${month + 1}/${day}"
                calendar2.set(year,month,day,0,0,0)
                date_hint.setText("从"+mDate+"开始")
                val start = calendar2.time.time
                val end = calendar1.time.time
                setText(username,start, end)
            }
        }
    }

    //获取本周开始事件的函数，返回字符串
    fun getWeekStartTime(value: Int): String {
        val calendar = Calendar.getInstance()
        val current = calendar[Calendar.DAY_OF_WEEK] //获取当天周内天数
        calendar.add(Calendar.DAY_OF_WEEK, 1 - current) //当天-基准，获取周开始日期
        var year = calendar[Calendar.YEAR]
        var month = calendar[Calendar.MONTH]
        var day = calendar[Calendar.DAY_OF_MONTH]
        val mDate = "${year}/${month + 1}/${day}"//按照“yyyy/dd/mm”的形式返回
        return mDate
    }

    //获取本周开始时间的函数，返回Calendar实例
    fun getWeekStartTime1 (): Calendar{
        val calendar = Calendar.getInstance()
        val current = calendar[Calendar.DAY_OF_WEEK] //获取当天周内天数
        calendar.add(Calendar.DAY_OF_MONTH,  1 - current) //当天-基准，获取周开始日
        calendar.set(calendar[Calendar.YEAR],calendar[Calendar.MONTH],calendar[Calendar.DAY_OF_MONTH],0,0,0)
        return calendar
    }

    //获取数组中前三大的值的下表
    fun getMax (list : Array<Double>) :  ArrayList<Int>{
        var res = 0
        var ans = arrayListOf<Int>()

        //获取最大的下标
        for(i in 0..list.size-1){
            if (list [res] < list[i]){
                res = i
            }
        }
        ans.add(res)
        var res1 = 0
        if(res === 0)
            res1++
        //第二大的下标
        for(i in 0..list.size-1){
            if(i != res){
                if (list [res1] < list[i]){
                    res1 = i
                }
            }
        }
        ans.add(res1)
        var res2 = 0
        while(res2 == res || res2 == res1){
            res2++
        }
        //第三大的下标
        for(i in 0..list.size-1){
            if(i != res && i != res1){
                if (list [res2] < list[i]){
                    res2 = i
                }
            }
        }
        ans.add(res2)
        return ans
    }

    //获取数组前三小的值的下标
    fun getMin (list :  Array<Double>) :  ArrayList<Int>{
        var res = 0
        var ans = arrayListOf<Int>()
        //第一小的下标
        for(i in 0..list.size-1){
            if (list [res] > list[i]){
                res = i
            }
        }
        ans.add(res)
        var res1 = 0
        if(res === 0)
            res1++
        //第二小的下标
        for(i in 0..list.size-1){
            if(i != res){
                if (list [res1] > list[i]){
                    res1 = i
                }
            }
        }
        ans.add(res1)
        var res2 = 0
        while(res2 == res || res2 == res1){
            res2++
        }
        //第三小的下标
        for(i in 0..list.size-1){
            if(i != res && i != res1){
                if (list [res2] > list[i]){
                    res2 = i
                }
            }
        }
        ans.add(res2)
        return ans
    }

    //统计的函数
    fun setText (username : String?, start : Long, end: Long){

        //实例化数据库
        val bookKeeping = BookKeepingData(this,"example.db",1)
        var db = bookKeeping.writableDatabase

        var amount_total = 0.0//总共的金额
        var amount_in = 0.0//收入金额
        var amount_out = 0.0//支出金额
        var out = arrayOf<Double>(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0)//各类别支出
        var inm = arrayOf<Double>(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0)//各类别收入

        var start1 = start.toString()
        var end1 = end.toString()

        val cursor = db.rawQuery("select * from Detail where username = ? and time between ? and ?", arrayOf(username,start1,end1))//查询规定时间内的记录
        //读取数据
        if (cursor.moveToFirst()){
            do{
                val amount_tmp = cursor.getDouble(cursor.getColumnIndex("amount"))
                val type = cursor.getInt(cursor.getColumnIndex("type"))
                amount_total = amount_total + amount_tmp
                if( amount_tmp > 0){
                    amount_in += amount_tmp
                    inm[type - 8] = amount_tmp + inm[type - 8]
                }
                if( amount_tmp < 0){
                    amount_out += amount_tmp
                    out[type] = amount_tmp + out[type]
                }
            }while(cursor.moveToNext())
        }
        cursor.close()

        //显示总金额
        val ss = SpannableString("结余\n￥"+amount_total.toString()+"\n")
        ss.setSpan(AbsoluteSizeSpan(30), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(AbsoluteSizeSpan(70), 3, ss.length , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        var show_total : TextView = findViewById(R.id.show_total)
        show_total.setText(ss)

        var maxnum = getMax(inm)
        var minnum = getMin(out)

        //显示收入、支出金额
        var inout_in : TextView = findViewById(R.id.inout_in)
        var inout_out: TextView = findViewById(R.id.inout_out)
        inout_in.text= "收入    ￥"+amount_in.toString()
        inout_out.text="支出    ￥"+amount_out.toString()

        var intype = arrayOf<String>("工资","奖金","报销","兼职","红包","股票","礼物","其他")
        var outtype = arrayOf<String>("出行","饮食","衣服","教育","游戏","水果","医疗","其他")

        //显示收入、支出前三的金额
        var in_first : TextView = findViewById(R.id.in_first)
        var in_second : TextView = findViewById(R.id.in_second)
        var in_third : TextView = findViewById(R.id.in_third)
        var out_first : TextView = findViewById(R.id.out_first)
        var out_second : TextView = findViewById(R.id.out_second)
        var out_third : TextView = findViewById(R.id.out_third)

        in_first.text = intype[maxnum[0]] +"    ￥"+inm[maxnum[0]]
        in_second.text = intype[maxnum[1]] +"    ￥"+inm[maxnum[1]]
        in_third.text = intype[maxnum[2]] +"    ￥"+inm[maxnum[2]]
        out_first.text = outtype[minnum[0]] +"    ￥"+out[minnum[0]]
        out_second.text = outtype[minnum[1]] +"    ￥"+out[minnum[1]]
        out_third.text = outtype[minnum[2]] +"    ￥"+out[minnum[2]]

    }

}



