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
    var castlingRight: Boolean = false,

    //I will set this property when a pawn double jumps to signal that it can possibly be captured
    //by special move en passant
    var enpassantMoveFlag: Int = -1 // Default value is -1





){
    // Constructor to initialize the ChessPiece
    constructor(color: String, type: String, row: Int, column: Int) : this(color, type, Pair(row, column))

    // Copy function to create a deep copy of the ChessPiece
    fun copy(): ChessPiece {
        return ChessPiece(color, type, position, castlingRight, enpassantMoveFlag)
    }
}