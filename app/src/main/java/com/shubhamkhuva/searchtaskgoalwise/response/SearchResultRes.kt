package com.shubhamkhuva.searchtaskgoalwise.response

import com.google.gson.annotations.SerializedName

class SearchResultRes{

    @SerializedName("status")
    var status: String? = null

    @SerializedName("data")
    var datas: ArrayList<SearchResultPara>? = null
}
