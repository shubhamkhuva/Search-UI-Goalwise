package com.shubhamkhuva.searchtaskgoalwise.response

import com.google.gson.annotations.SerializedName

class SearchResultPara {
    @SerializedName("fundname")
    var fundname: String = ""

    @SerializedName("minsipamount")
    var minsipamount: Int=0

    @SerializedName("minsipmultiple")
    var minsipmultiple: Int=0

    @SerializedName("sipdates")
    lateinit var sipdates: Array<Int>

}