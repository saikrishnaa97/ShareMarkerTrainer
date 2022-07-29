package com.krishna.sharemarkettrainer

class StockTradeData{
    var stockTradeDataList: List<StockTradeDataItem>? = null

    constructor()

    constructor(
    stockTradeDataList: List<StockTradeDataItem>
) {
    this.stockTradeDataList = stockTradeDataList
}

}
class StockTradeDataItem {
    var exchange: String = ""
    var numOfShares: Int = 0
    var purchasedAt: Int = 0
    var soldAt: Int = 0
    var status: String = ""
    var stockSymbol: String = ""
    var uid: String = ""
    var currentPrice = 0.0
    var SCRIP: Int = 0

    constructor()

    constructor(
        exchange: String,
        numOfShares: Int,
        purchasedAt: Int,
        soldAt: Int,
        status: String,
        stockSymbol: String,
        uid: String,
        SCRIP: Int
    ) {
        this.exchange = exchange
        this.numOfShares = numOfShares
        this.purchasedAt = purchasedAt
        this.soldAt = soldAt
        this.status = exchange
        this.stockSymbol = stockSymbol
        this.uid = uid
        this.SCRIP = SCRIP
    }

    override fun toString(): String {
        return "StockTradeDataItem(exchange='$exchange', numOfShares=$numOfShares, purchasedAt=$purchasedAt, soldAt=$soldAt, status='$status', stockSymbol='$stockSymbol', uid='$uid', currentPrice=$currentPrice, SCRIP=$SCRIP)"
    }
}