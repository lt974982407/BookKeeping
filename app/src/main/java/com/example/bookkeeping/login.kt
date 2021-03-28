package com.example.bookkeeping

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*

class login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //设置用户名输入框为圆角
        val eduser : EditText = findViewById(R.id.edit_username)
        val drawable1 : Drawable = resources.getDrawable(R.drawable.login1)
        drawable1.setBounds(0,0,90,90)
        eduser.setCompoundDrawables(drawable1,null,null,null)

        //设置密码输入矿为圆角
        val edpw : EditText= findViewById(R.id.edit_password)
        val drawable2 :Drawable = resources.getDrawable(R.drawable.password)
        drawable2.setBounds(0,0,90,90)
        edpw.setCompoundDrawables(drawable2, null, null, null)

        val bookKeeping = BookKeepingData(this,"example.db",1)//创建数据库实例

        //登录按钮响应
        val button1 : ImageButton = findViewById(R.id.button_login)
        button1.setOnClickListener(){
            //获取输入的账号和密码
            val edtuser = eduser.text.toString()
            val edtpw = edpw.text.toString()
            //检查用户名或密码是否为空
            if(edtuser.length == 0 || edtpw.length == 0){
                AlertDialog.Builder(this).apply {
                    setTitle("提醒：")
                    setMessage("用户名或密码不得为空！")
                    setCancelable(false)
                    setPositiveButton("确定"){dialog, which ->
                    }
                    show()
                }
            }
            else {
                val db = bookKeeping.writableDatabase
                //db.execSQL("delete from Detail where username =?", arrayOf(edtuser))
                //从数据库中查询密码
                val cursor = db.rawQuery("select * from User where username = ?", arrayOf(edtuser))
                //用户名不存在
                if(cursor.count == 0){
                    eduser.setText("")
                    edpw.setText("")
                    AlertDialog.Builder(this).apply {
                        setTitle("提醒：")
                        setMessage("用户名不存在！")
                        setCancelable(false)
                        setPositiveButton("确定"){dialog, which ->
                        }
                        show()
                    }
                }
                else{
                    cursor.moveToFirst()
                    val pw = cursor.getString(cursor.getColumnIndex("password"))
                    //密码错误
                    if(pw != edtpw){
                        eduser.setText("")
                        edpw.setText("")
                        AlertDialog.Builder(this).apply {
                            setTitle("提醒：")
                            setMessage("密码错误！")
                            setCancelable(false)
                            setPositiveButton("确定"){dialog, which ->
                            }
                            show()
                        }
                    }
                   //成功登录
                   else {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("username", edtuser)//把用户名传给下一个ACTIVITY
                        startActivity(intent)
                    }
                }
            }
        }

        //注册按钮响应
        val button2 : ImageView = findViewById(R.id.button_regisiter)
        button2.setOnClickListener(){
            val intent1 = Intent(this,regisiter::class.java)
            startActivity(intent1)
        }

    }

    //重写dispatchTouchEvent函数，使得焦点不在键盘上时键盘自动消失
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

}