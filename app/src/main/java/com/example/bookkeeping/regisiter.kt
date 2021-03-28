package com.example.bookkeeping

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast

class regisiter : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regisiter)

        //设置圆角
        val eduser_reg : EditText = findViewById(R.id.edit_username_reg)
        val drawable1 : Drawable = resources.getDrawable(R.drawable.login1)
        drawable1.setBounds(0,0,90,90)
        eduser_reg.setCompoundDrawables(drawable1,null,null,null)

        //设置圆角
        val edpw_reg1 : EditText= findViewById(R.id.edit_password_reg)
        val edpw_reg2 : EditText = findViewById(R.id.edit_password_reg2)
        val drawable2 :Drawable = resources.getDrawable(R.drawable.password)
        drawable2.setBounds(0,0,90,90)
        edpw_reg1.setCompoundDrawables(drawable2, null, null, null)
        edpw_reg2.setCompoundDrawables(drawable2, null, null, null)

        val bookKeeping = BookKeepingData(this,"example.db",1)//创建数据库实例

        //注册按钮响应
        val button1 : ImageButton = findViewById(R.id.button_reg)
        button1.setOnClickListener(){
            //获取用户输入
            val pw1 = edpw_reg1.text.toString()
            val pw2 = edpw_reg2.text.toString()
            val edtuser = eduser_reg.text.toString()
            //密码为空
            if (pw1.length == 0 || pw2.length == 0){
                eduser_reg.setText("")
                edpw_reg1.setText("")
                edpw_reg2.setText("")
                AlertDialog.Builder(this).apply {
                    setTitle("提醒：")
                    setMessage("密码不能为空！")
                    setCancelable(false)
                    setPositiveButton("确定"){dialog, which ->
                    }
                    show()
                }
            }
            //密码不一致
            else if(pw1 != pw2){
                eduser_reg.setText("")
                edpw_reg1.setText("")
                edpw_reg2.setText("")
                AlertDialog.Builder(this).apply {
                    setTitle("提醒：")
                    setMessage("两次密码请保持一致")
                    setCancelable(false)
                    setPositiveButton("确定"){dialog, which ->
                    }
                    show()
                }
            }
            else{
                //在数据库中查询用户名
                val db = bookKeeping.writableDatabase
                val cursor = db.rawQuery("select * from User where username = ?", arrayOf(edtuser))
                //用户名已经存在
                if (cursor.count != 0){
                    eduser_reg.setText("")
                    edpw_reg1.setText("")
                    edpw_reg2.setText("")
                    AlertDialog.Builder(this).apply {
                        setTitle("提醒：")
                        setMessage("用户名已存在！")
                        setCancelable(false)
                        setPositiveButton("确定"){dialog, which ->
                        }
                        show()
                    }
                }
                else{
                    //注册成功，把数据写入数据库
                    db.execSQL("insert into User (username, password) values (?,?)", arrayOf(edtuser,pw1))
                    Toast.makeText(this,"注册成功！",1).show()
                    eduser_reg.setText("")
                    edpw_reg1.setText("")
                    edpw_reg2.setText("")
                }

            }
        }

        //返回按钮响应，回到登录界面
        val button2 : ImageButton = findViewById(R.id.button_return)
        button2.setOnClickListener(){
            val intent1 = Intent(this,login::class.java)
            startActivity(intent1)
            this.finish()
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