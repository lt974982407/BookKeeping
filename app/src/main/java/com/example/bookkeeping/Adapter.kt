package com.example.bookkeeping

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

//键盘类
class keyborad (val text:String){

}

//键盘（geidView的适配器）
class MyAdapter (var context: Context, var data : ArrayList<keyborad>) :BaseAdapter(){

    //内置类
    inner class MyHolder(){
        lateinit var text : TextView
    }

    //重写getview函数
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view:View ?= null
        var myHolder:MyHolder ?= null

        if(convertView == null){
            myHolder = MyHolder()
            view = LayoutInflater.from(context).inflate(R.layout.keyboard_item,null)//绑定
            myHolder.text = view.findViewById(R.id.number)
            view.tag = myHolder
        }
        else{
            view = convertView
            myHolder = view.tag as MyHolder
        }
        myHolder.text.setText(data.get(position).text)//设置显示数据
        return view!!
    }

    //以下三个函数必须重写，实际并未用到
    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Any {
        return data.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

}

//用户记录的信息类
class item_detail (val imageId : Int, val amount : Double, val detail : String, val detail_time : Long)

//RecyclerView的适配器
class itemDetailAdapter(var list : ArrayList<item_detail>):
        RecyclerView.Adapter<itemDetailAdapter.ViewHolder>(){

    private var detail_list = ArrayList<item_detail>()

    init {
        detail_list = list
    }
    //内置类
    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        //创造每个单位的实例
        val detail_type : ImageView = view.findViewById(R.id.detail_type)
        val detail_amount : TextView = view.findViewById(R.id.detail_amount)
        val detail_description : TextView = view.findViewById(R.id.detail_description)
        val time : TextView = view.findViewById(R.id.detail_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): itemDetailAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.detail_item,parent,false)//绑定

        val viewHolder = ViewHolder(view)

        //短按响应函数
        viewHolder.itemView.setOnClickListener {
        }

        //长按响应函数
        viewHolder.itemView.setOnLongClickListener{
            val position = viewHolder.adapterPosition//获取下标
            AlertDialog.Builder(parent.context).apply {
                setTitle("提醒：")
                setMessage("您确定要删除这条数据吗?")
                setCancelable(false)
                //确定删除
                setPositiveButton("确定"){dialog, which ->
                    //在数据库中删除
                    val bookKeeping = BookKeepingData(parent.context,"example.db",1)
                    var db = bookKeeping.writableDatabase
                    db.execSQL("delete from Detail where time = ?", arrayOf(detail_list[position].detail_time.toString()))
                    //静态界面删除
                    detail_list.remove(detail_list[position])
                    notifyItemRemoved(position)
                    notifyItemChanged(position)
                }
                setNegativeButton("取消"){dialog, which ->
                }
                show()
            }

            false
        }
        return viewHolder
    }

    //数据绑定
    override fun onBindViewHolder(holder: itemDetailAdapter.ViewHolder, position: Int) {
        val item = detail_list[position]
        holder.detail_type.setImageResource(item.imageId)
        holder.detail_description.text = item.detail
        if(item.amount >= 0){
            holder.detail_amount.setTextColor(Color.RED)
            holder.detail_amount.setText("￥" + item.amount.toString())
        }
        else{
            holder.detail_amount.setTextColor(Color.GREEN)
            holder.detail_amount.setText("￥" + item.amount.toString().substring(1))
        }
        var time = Date(item.detail_time)
        val cal = Calendar.getInstance()
        cal.time = time
        var year = cal[Calendar.YEAR]
        var month = cal[Calendar.MONTH]
        var day = cal[Calendar.DAY_OF_MONTH]
        val mDate = "${year}/${month + 1}/${day}"
        holder.time.text = mDate
    }

    //必须重写，并未用到
    override fun getItemCount(): Int {
        return detail_list.size
    }
}

