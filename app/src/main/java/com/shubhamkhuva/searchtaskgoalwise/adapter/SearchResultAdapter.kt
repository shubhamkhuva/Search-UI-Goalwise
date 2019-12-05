package com.shubhamkhuva.searchtaskgoalwise.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.shubhamkhuva.searchtaskgoalwise.R
import com.shubhamkhuva.searchtaskgoalwise.activities.SearchActivity
import com.shubhamkhuva.searchtaskgoalwise.adapter.SearchResultAdapter.MyViewHolder
import com.shubhamkhuva.searchtaskgoalwise.helper.AdapterToActivity
import kotlinx.android.synthetic.main.card_layout.view.*

class SearchResultAdapter(private val mContext: Context, private val CardList: List<SearchResultDataAd>, val listener:AdapterToActivity) : RecyclerView.Adapter<MyViewHolder>() {

    inner class MyViewHolder(view: View) : ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val cardlist = CardList[position]
        holder.itemView.fundname.text = cardlist.fundname

        holder.itemView.sip_amount.text =  mContext.resources.getString(R.string.currency) +" "+ cardlist.minsipamount
        holder.itemView.sip_multiple.text = mContext.resources.getString(R.string.currency) +" "+ cardlist.minsipmultiple
        holder.itemView.sip_dates.text = cardlist.sipupdate
        holder.itemView.addButtonShow.setOnClickListener {
            holder.itemView.addButtonShow.visibility = View.GONE
            holder.itemView.addbuttonui.visibility = View.VISIBLE
            val params: ViewGroup.LayoutParams = holder.itemView.card_single.layoutParams
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT
            holder.itemView.card_single.layoutParams = params
            holder.itemView.newsipamount.requestFocus()

        }
        if(cardlist.addActive.equals(0)){
            holder.itemView.addButtonShow.visibility = View.VISIBLE
            holder.itemView.addbuttonui.visibility = View.GONE
            holder.itemView.newsipamount.setText("")
            val pixels =  180 * mContext.getResources().getDisplayMetrics().density
            val params: ViewGroup.LayoutParams = holder.itemView.card_single.layoutParams
            params.height = pixels.toInt()
            holder.itemView.card_single.layoutParams = params
        }
        holder.itemView.addfund.setOnClickListener {
            var oldvalueMinSipString = holder.itemView.sip_amount.text.toString()
            oldvalueMinSipString = oldvalueMinSipString.replace("₹ ","")
            val oldvalueMinSipInt = oldvalueMinSipString.toInt()
            if(holder.itemView.newsipamount.text.toString().toInt()>=oldvalueMinSipInt){
                cardlist.addActive = 1
                holder.itemView.errorText.visibility = View.INVISIBLE
                listener.openSuccessDialog(holder.itemView.fundname.text.toString(),position)


            }else{
                holder.itemView.errorText.text = mContext.resources.getString(R.string.amount_error1)
                holder.itemView.errorText.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return CardList.size
    }

}