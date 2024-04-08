package com.example.promochess

class ChessPiece(

    //string "white" or "black"
    val color: String,

    //"pawn", "king", "queen" "bishop", "knight", "rook"
    val type: String,


    //"current position on the board", "row # and column #"
    var position: Pair<Int, Int>


){
    // Constructor to initialize the ChessPiece
    constructor(color: String, type: String, row: Int, column: Int) : this(color, type, Pair(row, column))
}