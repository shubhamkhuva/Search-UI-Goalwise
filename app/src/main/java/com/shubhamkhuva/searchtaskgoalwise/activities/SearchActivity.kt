package com.shubhamkhuva.searchtaskgoalwise.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import com.shubhamkhuva.searchtaskgoalwise.R
import com.shubhamkhuva.searchtaskgoalwise.adapter.SearchResultAdapter
import com.shubhamkhuva.searchtaskgoalwise.adapter.SearchResultDataAd
import com.shubhamkhuva.searchtaskgoalwise.apiInterface.ApiInterface
import com.shubhamkhuva.searchtaskgoalwise.helper.AdapterToActivity
import com.shubhamkhuva.searchtaskgoalwise.helper.ConnectionDetector
import com.shubhamkhuva.searchtaskgoalwise.helper.DatabaseHelper
import com.shubhamkhuva.searchtaskgoalwise.response.SearchResultPara
import com.shubhamkhuva.searchtaskgoalwise.response.SearchResultRes
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.dialog_success.*
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import java.util.ArrayList

class SearchActivity : AppCompatActivity(),AdapterToActivity {
    private var adapter: SearchResultAdapter? = null
    private var cardList: MutableList<SearchResultDataAd>? = null
    var isInternetPresent: Boolean? = false
    var cd: ConnectionDetector? = null
    private var mydb: DatabaseHelper? = null
    var newSearch:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        supportActionBar!!.hide()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
        mydb = DatabaseHelper(this)
        cd = ConnectionDetector(applicationContext)
        cardList = ArrayList()
        adapter = SearchResultAdapter(applicationContext, cardList as ArrayList<SearchResultDataAd>,this)
        val mLayoutManager = GridLayoutManager(applicationContext, 1)
        searchresult_recycle!!.layoutManager = mLayoutManager
        searchresult_recycle!!.itemAnimator = DefaultItemAnimator()
        searchresult_recycle!!.adapter = adapter
        adapter!!.notifyDataSetChanged()

        editSearch_click.setOnClickListener {
            clearResult()
            hideKeyboard(applicationContext,this.viewMain)
            callWebService(editSearch.text.toString())
        }

        updateRecentSearch()

        editSearch.onChange {
            if(it.length>=3){
                callWebService(it)
            }
        }
    }

    private fun updateRecentSearch() {
        try {
            val list: ArrayList<String> = ArrayList()
            val res: Cursor = mydb!!.allData
            if (res.count > 0) {
                while (res.moveToNext()) {
                    if (res.getString(0) != "") {
                        if (!list.contains(res.getString(0))) {
                            list.add(res.getString(0))
                        }
                    }
                }
                val adapter: ArrayAdapter<String> =
                    ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, list)
                editSearch.setAdapter(adapter)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    private fun noInternet() {
        clearResult()
        noresult.setText(applicationContext.resources.getString(R.string.nointernet_msg))
        noresult.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }

    private fun callWebService(searchString: String) {
        isInternetPresent = cd!!.isConnectingToInternet
        if (isInternetPresent == true) {
            newSearch = true
            val apiService = ApiInterface.create()

            val jsonParams: MutableMap<String, String> = HashMap()
            jsonParams.put("keyword", searchString)
            val body:RequestBody =
            RequestBody.create(okhttp3.MediaType.parse("application/json"), (JSONObject(jsonParams)).toString())

            val call = apiService.getResult(body)
            Log.d("REQUEST", call.toString() + "")
            call.enqueue(object : Callback<SearchResultRes> {
                override fun onResponse(call: Call<SearchResultRes>, response: retrofit2.Response<SearchResultRes>?) {
                    if (response != null) {
                        clearResult()
                        newSearch = false
                        if(response.body()!!.status.toString().equals("success")) {
                            val list: List<SearchResultPara> = response.body()!!.datas!!
                            if(list.size>0) {
                                for (item: SearchResultPara in list.iterator()) {

                                    val fundname = item.fundname
                                    val minsipamount = item.minsipamount
                                    val minsipmultiple = item.minsipmultiple
                                    val sipdates = item.sipdates
                                    var sipupdatestring = sipdates.contentToString()
                                    sipupdatestring = sipupdatestring.replace("[", "")
                                    sipupdatestring = sipupdatestring.replace("]", "")
                                    val a = SearchResultDataAd(fundname, minsipamount, minsipmultiple, sipupdatestring,0)
                                    cardList!!.add(a)
                                }
                                adapter!!.notifyDataSetChanged()
                            }
                            else{
                                clearResult()
                                noresult.setText(applicationContext.resources.getString(R.string.noresult_msg))
                                noresult.visibility = View.VISIBLE
                            }
                            progressBar.visibility = View.GONE
                            Handler().postDelayed({
                                if(editSearch.text.length>=3) {
                                    if(!newSearch) {
                                        mydb!!.insertData(editSearch.text.toString())
                                        updateRecentSearch()
                                    }
                                }
                            }, 4000)
                        }
                    }
                }
                override fun onFailure(call: Call<SearchResultRes>, t: Throwable) {

                }
            })
        }else{
            noInternet()
        }
    }

    private fun clearResult() {
        try {
            progressBar.visibility = View.VISIBLE
            noresult.visibility = View.GONE
            cardList = ArrayList()
            adapter = SearchResultAdapter(applicationContext, cardList as ArrayList<SearchResultDataAd>, this)
            searchresult_recycle!!.adapter = adapter
            adapter!!.notifyDataSetChanged()
        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun hideKeyboard(context: Context, view:View) {
        try {
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        catch(e:Exception){
            e.printStackTrace()
        }
    }

    fun AutoCompleteTextView.onChange(cb: (String) -> Unit) {
        this.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) { cb(s.toString()) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
    public override fun openSuccessDialog(fundname: String?, position: Int) {
        try {
            hideKeyboard(applicationContext, this.viewMain)
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_success, null)
            val mBuilder = AlertDialog.Builder(this).setView(mDialogView)
            val mAlertDialog = mBuilder.show()
            mAlertDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            mAlertDialog.setCanceledOnTouchOutside(false)
            mAlertDialog.message.setText(fundname + this.resources.getString(R.string.success_msg))
            mAlertDialog.gotit.setOnClickListener {
                mAlertDialog.dismiss()
                this.cardList?.get(position)?.addActive = 0
                adapter!!.notifyDataSetChanged()
            }
        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }
}
