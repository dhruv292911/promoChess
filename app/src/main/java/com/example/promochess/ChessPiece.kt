package com.example.promochess

class ChessPiece(

    //string "white" or "black"
    val color: String,

    //"pawn", "king", "queen" "bishop", "knight", "rook"
    val type: String,


    //"current position on the board", "row # and column #"
    var position: Pair<Int, Int>,


    // Boolean indicating if the piece has castling rights
    //By default it will be false for all pieces except kings and rooks on initialization of the game.
    //Once a king or rook looses castlingRight i.e. it moves from their starting position it will be set to false.
    var castlingRight: Boolean = false


){
    // Constructor to initialize the ChessPiece
    constructor(color: String, type: String, row: Int, column: Int) : this(color, type, Pair(row, column))
}