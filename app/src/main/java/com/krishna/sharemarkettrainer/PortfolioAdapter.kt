package com.krishna.sharemarkettrainer

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.pnpninja.nsetools.NSETools
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.RoundingMode
import java.text.DecimalFormat
import android.os.AsyncTask
import com.github.pnpninja.nsetools.domain.StockQuote


class PortfolioAdapter(context: Context, stockTradeDataList: List<StockTradeDataItem>, cookie: String) : RecyclerView.Adapter<PortfolioAdapter.ViewHolder?>() {

    private val context : Context
    private val stockTradeDataList : List<StockTradeDataItem>
    var cookie : String
    val nseApi = NSERetrofitHelper.getInstance().create(NSEStockRestClient::class.java)

    init {
        this.context = context
        this.stockTradeDataList = stockTradeDataList
        this.cookie = cookie
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        var stock_name_portfolio: TextView
        var cost_portfolio: TextView
        var current_price_portfolio: TextView
        var percent_change_portfolio: TextView

        init {
            stock_name_portfolio = itemView.findViewById(R.id.stock_name_portfolio)
            cost_portfolio = itemView.findViewById(R.id.cost_portfolio)
            current_price_portfolio = itemView.findViewById(R.id.current_price_portfolio)
            percent_change_portfolio = itemView.findViewById(R.id.percent_change_portfolio)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.portfolio_item_layout,parent, false)
        return PortfolioAdapter.ViewHolder(view)
    }

    fun getCookieFromRequest(){
        var cookieResponse = nseApi.getCookieRequest()

        cookieResponse.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                if (!response.headers().get("Set-Cookie")?.isEmpty()!!) {
                    cookie = response.headers().get("Set-Cookie").toString()
                    Log.i("Got the cookie from the request",cookie!!)
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                Log.i("request",call.toString())
                Log.e("An exception occured in getCookie",t.toString())
            }
        })
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.cost_portfolio.text = "Avg. Cost\n Rs."+stockTradeDataList?.get(position)?.purchasedAt.toString()
        holder.stock_name_portfolio.text = stockTradeDataList?.get(position)?.stockSymbol.toString()

        val stockDataResponse = nseApi.getStockStatus(stockTradeDataList?.get(position)?.stockSymbol!!,cookie)

        stockDataResponse.enqueue(object: Callback<NSEStockStatus> {
            override fun onResponse(
                call: Call<NSEStockStatus>,
                response: Response<NSEStockStatus>
            ) {
                if (response.code() != 200){
                    getCookieFromRequest()
                }
                    var responseBody = response.body()
                    Log.i("Response Code",response.code().toString())
                    Log.i("Response Url",response.raw().request().url().toString())
                    Log.i("Response",responseBody.toString())
                    try {
                        val df = DecimalFormat("#.##")
                        df.roundingMode = RoundingMode.DOWN
                        var percentChange = 100*(responseBody?.priceInfo?.lastPrice?.minus(
                            stockTradeDataList?.get(position)?.purchasedAt
                        ))?.div(stockTradeDataList?.get(position)?.purchasedAt)!!

                        val roundoff = df.format(percentChange)
                        holder.current_price_portfolio.text =
                            "Cur Price\n Rs. " + responseBody?.priceInfo?.lastPrice?.toString()
                        holder.percent_change_portfolio.text = "% Change\n " + roundoff.toString()+"%"
                    }
                    catch (e: Exception){
                        holder.current_price_portfolio.text =
                            "Cur Price\n Rs. " + stockTradeDataList?.get(position)?.purchasedAt.toString()
                        holder.percent_change_portfolio.text = "% Change\n 0%"
                    }
            }
            override fun onFailure(call: Call<NSEStockStatus>, t: Throwable) {
                Log.d("TAG","Error Response = "+t.toString());
            }
        })
    }

    override fun getItemCount(): Int {
        return stockTradeDataList?.size!!
    }

}
