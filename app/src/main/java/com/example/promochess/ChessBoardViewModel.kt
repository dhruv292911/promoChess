package com.example.promochess

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel



class ChessBoardViewModel : ViewModel() {

    //boolean to represent which player has the turn currently
    //white player or black player
    var white_turn = true
    private val _moveUpdated = MutableLiveData<Boolean>()
    val moveUpdated: LiveData<Boolean>
        get() = _moveUpdated




    // Initialize the chessboard
    val chessBoard = Array(8) { row ->
        Array<ChessPiece?>(8) { column ->
            when (row) {
                0 -> {
                    when (column) {
                        0, 7 -> ChessPiece("black", "rook", Pair(row, column))
                        1, 6 -> ChessPiece("black", "knight", Pair(row, column))
                        2, 5 -> ChessPiece("black", "bishop", Pair(row, column))
                        3 -> ChessPiece("black", "queen", Pair(row, column))
                        4 -> ChessPiece("black", "king", Pair(row, column))
                        else -> null
                    }
                }
                1 -> ChessPiece("black", "pawn", Pair(row, column))
                6 -> ChessPiece("white", "pawn", Pair(row, column))
                7 -> {
                    when (column) {
                        0, 7 -> ChessPiece("white", "rook", Pair(row, column))
                        1, 6 -> ChessPiece("white", "knight", Pair(row, column))
                        2, 5 -> ChessPiece("white", "bishop", Pair(row, column))
                        3 -> ChessPiece("white", "queen", Pair(row, column))
                        4 -> ChessPiece("white", "king", Pair(row, column))
                        else -> null
                    }
                }
                else -> null
            }
        }
    }

    // Array to store remaining white pieces
    val remaining_white_pieces = mutableListOf<ChessPiece>()

    // Array to store remaining black pieces
    val remaining_black_pieces = mutableListOf<ChessPiece>()

    init {
        // Populate remaining_white_pieces and remaining_black_pieces
        for (row in chessBoard.indices) {
            for (col in chessBoard[row].indices) {
                val piece = chessBoard[row][col]
                if (piece != null) {
                    if (piece.color == "white") {
                        remaining_white_pieces.add(piece)
                    } else {
                        remaining_black_pieces.add(piece)
                    }
                }
            }
        }
    }


    fun movePiece(sourcePosition: Pair<Int, Int>, targetPosition: Pair<Int, Int>) {
        // Add logic to handle the move here


        //if you got to here you clicked on the right color piece and current_piece is not null
        val current_piece = chessBoard[sourcePosition.first][sourcePosition.second]

//        val curpiece_stringbuilder = StringBuilder()
//        curpiece_stringbuilder.append("${current_piece!!.color}_${current_piece.type}")
//        println(curpiece_stringbuilder.toString())

        // if current_piece is a pawn
        if(current_piece!!.type == "pawn"){
            movePawn(sourcePosition, targetPosition)
        }

        // if current_piece is a knight
        else if(current_piece.type == "knight"){
            moveKnight(sourcePosition, targetPosition)
        }

        //if current piece is a rook
        else if(current_piece.type == "rook"){
            moveRook(sourcePosition, targetPosition)
        }

        //if current piece is a bishop
        else if(current_piece.type == "bishop"){
            moveBishop(sourcePosition, targetPosition)
        }

        //if current piece is a queen
        else if(current_piece.type == "queen"){
            moveQueen(sourcePosition, targetPosition)
        }

        //current piece is a king
        else{

        }
    }

    //Updates the Game State when a Piece is being moved to an empty square. Gets reference to the piece at source position, sets sourcepos to null, target pos to chess piece, update the piece position to targetpos,
    //and updates the respective remaining_white_pieces/remaining_black_pieces(array)

    fun moving_piece_empty_square(sourcePosition: Pair<Int, Int>, targetPosition: Pair<Int, Int>){
        // Get a reference to the piece at the source position
        val cur_piece = chessBoard[sourcePosition.first][sourcePosition.second]

        // Set the current location in the ChessBoard to null
        chessBoard[sourcePosition.first][sourcePosition.second] = null

        // Set the target location in the ChessBoard to the current piece
        chessBoard[targetPosition.first][targetPosition.second] = cur_piece

        // Update the piece position to the new target location
        cur_piece!!.position = targetPosition

        // Update piece position in remaining_black_pieces or remaining_white_pieces
        val remainingPieces = if (cur_piece.color == "white") remaining_white_pieces else remaining_black_pieces
        val index = remainingPieces.indexOfFirst { it.position == sourcePosition }
        if (index != -1) {
            remainingPieces[index].position = targetPosition
        }


        //Updating the player's turn and updating live data so the new state can be displayed in the chess board
        if(white_turn){
            white_turn = false

        }else{
            white_turn = true

        }
        _moveUpdated.value = true
    }

    //Updates the Game State when a Piece is being captured. Gets reference to the Source Piece from source position at ChessBoard. Sets Current Location to null and target position to Source Piece(thus deleting captured Piece)
    //updates the source piece position to targetPosition. Then updates the remaining pieces vectors, deletes from the captured piece color and updates the pos of the source piece color
    fun capturing_piece_opposite_color(sourcePosition: Pair<Int, Int>, targetPosition: Pair<Int, Int>){
        // Get a reference to the piece at the source position
        val cur_piece = chessBoard[sourcePosition.first][sourcePosition.second]

        // Set the current location in the ChessBoard to null
        chessBoard[sourcePosition.first][sourcePosition.second] = null

        // Set the target location in the ChessBoard to the current piece
        chessBoard[targetPosition.first][targetPosition.second] = cur_piece

        // Update the piece position to the new target location
        cur_piece?.position = targetPosition

        // Update piece position in remaining_black_pieces or remaining_white_pieces
        if (cur_piece != null) {
            val remainingPieces = if (cur_piece.color == "white") remaining_white_pieces else remaining_black_pieces
            val index = remainingPieces.indexOfFirst { it.position == sourcePosition }
            if (index != -1) {
                remainingPieces[index].position = targetPosition
            }
        }

        // Remove the captured piece from remaining_pieces of the opposite color
        val remainingOppositePieces = if (cur_piece?.color == "white") remaining_black_pieces else remaining_white_pieces
        remainingOppositePieces.removeIf { it.position == targetPosition }


        //Updating the player's turn and updating live data so the new state can be displayed in the chess board
        if(white_turn){
            white_turn = false

        }else{
            white_turn = true

        }
        _moveUpdated.value = true
    }



    //Move Functions

    fun movePawn(sourcePosition: Pair<Int, Int>, targetPosition: Pair<Int, Int>){
        //Source Position comes from the chessboard position and piece is checked for not null in Main Activity
        val current_piece = chessBoard[sourcePosition.first][sourcePosition.second]

        //if cur_piece is white pawn
        if(current_piece!!.color == "white"){

            val jump_twice_position = Pair(current_piece.position.first -2, current_piece.position.second)

            val jump_once_position = Pair(current_piece.position.first - 1, current_piece.position.second)

            val capture_right_position = Pair(current_piece.position.first - 1, current_piece.position.second + 1)

            val capture_left_position = Pair(current_piece.position.first - 1, current_piece.position.second -1)

            //if white pawn is in starting row can jump once, jump twice, capture diagonal left, diagonal right (Row 6)
            //Cannot capture Black King
            if(sourcePosition.first == 6){

                // if you want to jump twice and the path is empty
                if(targetPosition == jump_twice_position && chessBoard[jump_twice_position.first][jump_twice_position.second] == null && chessBoard[jump_once_position.first][jump_once_position.second] == null){
                    //Pawn Jump Valid update game state
                    moving_piece_empty_square(sourcePosition,targetPosition)
                }
                // if you want to jump once and the path is empty
                else if(targetPosition == jump_once_position && chessBoard[jump_once_position.first][jump_once_position.second] == null){
                    //Pawn Jump Valid update game state
                    moving_piece_empty_square(sourcePosition,targetPosition)
                }
                // if you want to capture diagonally to the right and there is black piece there that is not black king
                else if(targetPosition == capture_right_position && chessBoard[targetPosition.first][targetPosition.second] != null){
                    if(chessBoard[targetPosition.first][targetPosition.second]!!.color == "black" && chessBoard[targetPosition.first][targetPosition.second]!!.type != "king"){
                        capturing_piece_opposite_color(sourcePosition, targetPosition)
                    }

                }
                //if you want to capture diagonally to the left and there is a black piece there that is not black king
                else if(targetPosition == capture_left_position && chessBoard[targetPosition.first][targetPosition.second] != null){
                    if(chessBoard[targetPosition.first][targetPosition.second]!!.color == "black" && chessBoard[targetPosition.first][targetPosition.second]!!.type != "king"){
                        capturing_piece_opposite_color(sourcePosition, targetPosition)
                    }
                }
            }

            // can jump once, capture diagonal left, capture diagonal right, all leads to promotion as it is in row before promotion (Row 1)
            //Cannot Capture Black King
            else if(sourcePosition.first == 1){

            }
            //can only jump once, capture diagonal left, capture diagonal right (Rows 5, 4, 3, 2)
            //Cannot Capture Black King
            else{
                // if you want to jump once and the path is empty
                if(targetPosition == jump_once_position && chessBoard[jump_once_position.first][jump_once_position.second] == null){
                    //Pawn Jump Valid update game state
                    moving_piece_empty_square(sourcePosition,targetPosition)
                }
                // if you want to capture diagonally to the right and there is black piece there that is not black king
                else if(targetPosition == capture_right_position && chessBoard[targetPosition.first][targetPosition.second] != null){
                    if(chessBoard[targetPosition.first][targetPosition.second]!!.color == "black" && chessBoard[targetPosition.first][targetPosition.second]!!.type != "king"){
                        capturing_piece_opposite_color(sourcePosition, targetPosition)
                    }

                }
                //if you want to capture diagonally to the left and there is a black piece there that is not black king
                else if(targetPosition == capture_left_position && chessBoard[targetPosition.first][targetPosition.second] != null){
                    if(chessBoard[targetPosition.first][targetPosition.second]!!.color == "black" && chessBoard[targetPosition.first][targetPosition.second]!!.type != "king"){
                        capturing_piece_opposite_color(sourcePosition, targetPosition)
                    }
                }
            }
        }
        //if current_piece is a black pawn
        else{

            val jump_twice_position = Pair(current_piece.position.first + 2, current_piece.position.second)

            val jump_once_position = Pair(current_piece.position.first + 1, current_piece.position.second)

            val capture_right_position = Pair(current_piece.position.first + 1, current_piece.position.second - 1)

            val capture_left_position = Pair(current_piece.position.first + 1, current_piece.position.second + 1)

            //if black pawn is in starting row can jump once, jump twice, capture diagonal left, diagonal right (Row 1)
            //cannot capture White king
            if(sourcePosition.first == 1){
                // if you want to jump twice and the path is empty
                if(targetPosition == jump_twice_position && chessBoard[jump_twice_position.first][jump_twice_position.second] == null && chessBoard[jump_once_position.first][jump_once_position.second] == null){
                    //Pawn Jump Valid update game state
                    moving_piece_empty_square(sourcePosition,targetPosition)
                }
                // if you want to jump once and the path is empty
                else if(targetPosition == jump_once_position && chessBoard[jump_once_position.first][jump_once_position.second] == null){
                    //Pawn Jump Valid update game state
                    moving_piece_empty_square(sourcePosition,targetPosition)
                }
                // if you want to capture diagonally to the right and there is White piece there that is not White king
                else if(targetPosition == capture_right_position && chessBoard[targetPosition.first][targetPosition.second] != null){
                    if(chessBoard[targetPosition.first][targetPosition.second]!!.color == "white" && chessBoard[targetPosition.first][targetPosition.second]!!.type != "king"){
                        capturing_piece_opposite_color(sourcePosition, targetPosition)
                    }

                }
                //if you want to capture diagonally to the left and there is a White piece there that is not White king
                else if(targetPosition == capture_left_position && chessBoard[targetPosition.first][targetPosition.second] != null){
                    if(chessBoard[targetPosition.first][targetPosition.second]!!.color == "white" && chessBoard[targetPosition.first][targetPosition.second]!!.type != "king"){
                        capturing_piece_opposite_color(sourcePosition, targetPosition)
                    }
                }
            }

            // can jump once, capture diagonal left, capture diagonal right, all leads to promotion as it is in row before promotion (Row 6)
            //Cannot Capture White King
            else if(sourcePosition.first == 6){


            }

            //can only jump once, capture diagonal left, capture diagonal right (Rows 5, 4, 3, 2)
            //Cannot Capture White King
            else{
                // if you want to jump once and the path is empty
                if(targetPosition == jump_once_position && chessBoard[jump_once_position.first][jump_once_position.second] == null){
                    //Pawn Jump Valid update game state
                    moving_piece_empty_square(sourcePosition,targetPosition)
                }
                // if you want to capture diagonally to the right and there is White piece there that is not White king
                else if(targetPosition == capture_right_position && chessBoard[targetPosition.first][targetPosition.second] != null){
                    if(chessBoard[targetPosition.first][targetPosition.second]!!.color == "white" && chessBoard[targetPosition.first][targetPosition.second]!!.type != "king"){
                        capturing_piece_opposite_color(sourcePosition, targetPosition)
                    }

                }
                //if you want to capture diagonally to the left and there is a White piece there that is not White king
                else if(targetPosition == capture_left_position && chessBoard[targetPosition.first][targetPosition.second] != null){
                    if(chessBoard[targetPosition.first][targetPosition.second]!!.color == "white" && chessBoard[targetPosition.first][targetPosition.second]!!.type != "king"){
                        capturing_piece_opposite_color(sourcePosition, targetPosition)
                    }
                }
            }
        }
    }

    fun moveKnight(sourcePosition: Pair<Int, Int>, targetPosition: Pair<Int, Int>){
        val current_piece = chessBoard[sourcePosition.first][sourcePosition.second]

        //Calculating the coordinates of the 8 possible knight jumps
        val up_left_pos = Pair(sourcePosition.first - 2, sourcePosition.second - 1)
        val up_right_pos = Pair(sourcePosition.first - 2, sourcePosition.second + 1)
        val down_right_pos = Pair(sourcePosition.first + 2, sourcePosition.second - 1)
        val down_left_pos = Pair(sourcePosition.first + 2, sourcePosition.second + 1)
        val left_down_pos = Pair(sourcePosition.first + 1, sourcePosition.second - 2)
        val left_up_pos = Pair(sourcePosition.first -1, sourcePosition.second - 2)
        val right_down_pos = Pair(sourcePosition.first + 1, sourcePosition.second + 2)
        val right_up_pos = Pair(sourcePosition.first -1, sourcePosition.second + 2)

        val possibleMoves = listOf(
            up_left_pos, up_right_pos, down_right_pos, down_left_pos,
            left_down_pos, left_up_pos, right_down_pos, right_up_pos
        )

        //Knight is moving to an empty square
        if(chessBoard[targetPosition.first][targetPosition.second] == null) {
            // Check if the targetPosition matches any of the possible knight moves
            // If it does update the Game State.
            if (targetPosition in possibleMoves) {
                moving_piece_empty_square(sourcePosition, targetPosition)
            }
        }

        //Knight is trying to move to an occupied square: if the colors of the pieces are different then this is a capture attempt.
        //Make sure you are not capturing the Enemy King
        else if(chessBoard[targetPosition.first][targetPosition.second] != null){
            if(targetPosition in possibleMoves && current_piece!!.color != chessBoard[targetPosition.first][targetPosition.second]!!.color && chessBoard[targetPosition.first][targetPosition.second]!!.type != "king"){
                capturing_piece_opposite_color(sourcePosition, targetPosition)
            }
        }
    }

    fun moveRook(sourcePosition: Pair<Int, Int>, targetPosition: Pair<Int, Int>){

        val current_piece = chessBoard[sourcePosition.first][sourcePosition.second]

        val possible_moves_list = mutableListOf<Pair<Int, Int>>()

        val cur_row = sourcePosition.first
        val cur_col = sourcePosition.second

        //North Direction (Row # decreases column stays the same)
        var nextsquare_northrow = cur_row - 1

        //Make Sure while loop condition changes at the end
        while (nextsquare_northrow >= 0){
            //if the next square is empty add to possible move  //(Move to Blank Square)
            if(chessBoard[nextsquare_northrow][cur_col] == null){
                possible_moves_list.add(Pair(nextsquare_northrow, cur_col))
            }

            //if next square is not null
            else{
                // if next square is occupied by the same color piece stop expansion
                if(chessBoard[nextsquare_northrow][cur_col]!!.color == current_piece!!.color){
                    break
                }
                //next square is occupied by a different color piece: Potential Capture and Stop Expansion
                else{
                    //if the next square is an enemy piece that is not the king add to possible list of moves. Cannot capture enemy King
                    if(chessBoard[nextsquare_northrow][cur_col]!!.type != "king"){
                        possible_moves_list.add(Pair(nextsquare_northrow, cur_col))
                    }
                    break
                }
            }
            nextsquare_northrow -= 1
        }

        //South Direction
        var nextsquare_southrow = cur_row + 1

        while(nextsquare_southrow <= 7){
            //Next square is blank space
            if(chessBoard[nextsquare_southrow][cur_col] == null){
                possible_moves_list.add(Pair(nextsquare_southrow, cur_col))
            }
            //Next square is not a blank space
            else{
                // if next square is occupied by the same color piece stop expansion
                if(chessBoard[nextsquare_southrow][cur_col]!!.color == current_piece!!.color){
                    break
                }
                //next square is occupied by a different color piece: Potential Capture and Stop Expansion
                else{
                    //if the next square is an enemy piece that is not the king add to possible list of moves. Cannot capture enemy King
                    if(chessBoard[nextsquare_southrow][cur_col]!!.type != "king"){
                        possible_moves_list.add(Pair(nextsquare_southrow, cur_col))
                    }
                    break
                }
            }
            nextsquare_southrow += 1
        }

        //West Direction
        var nextsquare_westcol = cur_col - 1

        while(nextsquare_westcol >= 0){
            //Next square is a blank space
            if(chessBoard[cur_row][nextsquare_westcol] == null){
                possible_moves_list.add(Pair(cur_row, nextsquare_westcol))
            }
            //Next square is not a blank space
            else{
                // if next square is occupied by the same color piece stop expansion
                if(chessBoard[cur_row][nextsquare_westcol]!!.color == current_piece!!.color){
                    break
                }
                // next square is occupied by a different color piece: Potential Capture and Stop Expansion
                else{
                    //if next square is an enemey piece that is not king add to possible list of moves. Cannot capture enemy King
                    if(chessBoard[cur_row][nextsquare_westcol]!!.type != "king"){
                        possible_moves_list.add(Pair(cur_row, nextsquare_westcol))
                    }
                    break
                }
            }
            nextsquare_westcol -= 1
        }

        //East Direction
        var nextsquare_eastcol = cur_col + 1

        while(nextsquare_eastcol <= 7){
            //Next square is a blank space
            if(chessBoard[cur_row][nextsquare_eastcol] == null){
                possible_moves_list.add(Pair(cur_row, nextsquare_eastcol))
            }
            //Next square is not a blank space
            else{
                // if next square is occupied by the same color piece stop expansion
                if(chessBoard[cur_row][nextsquare_eastcol]!!.color == current_piece!!.color){
                    break
                }
                // next square is occupied by a different color piece: Potential Capture and Stop Expansion
                else{
                    //if next square is an enemey piece that is not king add to possible list of moves. Cannot capture enemy King
                    if(chessBoard[cur_row][nextsquare_eastcol]!!.type != "king"){
                        possible_moves_list.add(Pair(cur_row, nextsquare_eastcol))
                    }
                    break
                }
            }
            nextsquare_eastcol += 1
        }

        //All the possible squares for the rook to move are compiled and the paths are checked
        //Rook is either moving to a blank square or moving to a square with an enemy piece that is not the king

        //If the target position is part of the possible_moves_list
        if(possible_moves_list.contains(targetPosition)){
            //Moving to a blank square
            if(chessBoard[targetPosition.first][targetPosition.second] == null){
                moving_piece_empty_square(sourcePosition, targetPosition)
            }
            //Capturing an enemy piece that is not the king
            else{
                capturing_piece_opposite_color(sourcePosition, targetPosition)
            }
        }
    }

    fun moveBishop(sourcePosition: Pair<Int, Int>, targetPosition: Pair<Int, Int>){
        val current_piece = chessBoard[sourcePosition.first][sourcePosition.second]

        val possible_moves_list = mutableListOf<Pair<Int, Int>>()

        val cur_row = sourcePosition.first
        val cur_col = sourcePosition.second


        //NE Direction: Row # decrease, column # increases
        var row_NE = cur_row - 1
        var col_NE = cur_col + 1

        while(row_NE >= 0 && col_NE <= 7){
            //Next square is a blank space
            if(chessBoard[row_NE][col_NE] == null){
                possible_moves_list.add(Pair(row_NE, col_NE))
            }
            //Next square is not a blank space
            else{
                // if next square is occupied by the same color piece stop expansion
                if(chessBoard[row_NE][col_NE]!!.color == current_piece!!.color){
                    break
                }
                // next square is occupied by a different color piece: Potential Capture and Stop Expansion
                else{
                    //if next square is an enemey piece that is not king add to possible list of moves. Cannot capture enemy King
                    if(chessBoard[row_NE][col_NE]!!.type != "king"){
                        possible_moves_list.add(Pair(row_NE, col_NE))
                    }
                    break
                }
            }
            row_NE -= 1
            col_NE += 1
        }


        //SE Direction: Row # increases, column # increases
        var row_SE = cur_row + 1
        var col_SE = cur_col + 1

        while(row_SE <= 7 && col_SE <= 7){
            //Next square is a blank space
            if(chessBoard[row_SE][col_SE] == null){
                possible_moves_list.add(Pair(row_SE, col_SE))
            }
            //Next square is not a blank space
            else{
                // if next square is occupied by the same color piece stop expansion
                if(chessBoard[row_SE][col_SE]!!.color == current_piece!!.color){
                    break
                }
                // next square is occupied by a different color piece: Potential Capture and Stop Expansion
                else{
                    //if next square is an enemey piece that is not king add to possible list of moves. Cannot capture enemy King
                    if(chessBoard[row_SE][col_SE]!!.type != "king"){
                        possible_moves_list.add(Pair(row_SE, col_SE))
                    }
                    break
                }
            }
            row_SE += 1
            col_SE += 1
        }

        //NW Direction: Row # decreases, column # decreases
        var row_NW = cur_row - 1
        var col_NW = cur_col - 1

        while(row_NW >= 0 && col_NW >= 0){
            //Next square is a blank space
            if(chessBoard[row_NW][col_NW] == null){
                possible_moves_list.add(Pair(row_NW, col_NW))
            }
            //Next square is not a blank space
            else{
                // if next square is occupied by the same color piece stop expansion
                if(chessBoard[row_NW][col_NW]!!.color == current_piece!!.color){
                    break
                }
                // next square is occupied by a different color piece: Potential Capture and Stop Expansion
                else{
                    //if next square is an enemey piece that is not king add to possible list of moves. Cannot capture enemy King
                    if(chessBoard[row_NW][col_NW]!!.type != "king"){
                        possible_moves_list.add(Pair(row_NW, col_NW))
                    }
                    break
                }
            }
            row_NW -= 1
            col_NW -= 1
        }


        //SW Direction: Row # increases, column # decreases
        var row_SW = cur_row + 1
        var col_SW = cur_col - 1

        while(row_SW <= 7 && col_SW >=0){
            //Next square is a blank space
            if(chessBoard[row_SW][col_SW] == null){
                possible_moves_list.add(Pair(row_SW, col_SW))
            }
            //Next square is not a blank space
            else{
                // if next square is occupied by the same color piece stop expansion
                if(chessBoard[row_SW][col_SW]!!.color == current_piece!!.color){
                    break
                }
                // next square is occupied by a different color piece: Potential Capture and Stop Expansion
                else{
                    //if next square is an enemey piece that is not king add to possible list of moves. Cannot capture enemy King
                    if(chessBoard[row_SW][col_SW]!!.type != "king"){
                        possible_moves_list.add(Pair(row_SW, col_SW))
                    }
                    break
                }
            }

            row_SW += 1
            col_SW -= 1
        }


        //All the possible squares for the Bishop to move are compiled and the paths are checked
        //Bishop is either moving to a blank square or moving to a square with an enemy piece that is not the king

        //If the target position is part of the possible_moves_list
        if(possible_moves_list.contains(targetPosition)){
            //Moving to a blank square
            if(chessBoard[targetPosition.first][targetPosition.second] == null){
                moving_piece_empty_square(sourcePosition, targetPosition)
            }
            //Capturing an enemy piece that is not the king
            else{
                capturing_piece_opposite_color(sourcePosition, targetPosition)
            }
        }

    }

    fun moveQueen(sourcePosition: Pair<Int, Int>, targetPosition: Pair<Int, Int>){
        val current_piece = chessBoard[sourcePosition.first][sourcePosition.second]
        val possible_moves_list = mutableListOf<Pair<Int, Int>>()
        val cur_row = sourcePosition.first
        val cur_col = sourcePosition.second


        //***Queen is a combination of Rook and Bishop ***//
        //***Same Code as Rook***
        //North Direction (Row # decreases column stays the same)
        var nextsquare_northrow = cur_row - 1

        //Make Sure while loop condition changes at the end
        while (nextsquare_northrow >= 0){
            //if the next square is empty add to possible move  //(Move to Blank Square)
            if(chessBoard[nextsquare_northrow][cur_col] == null){
                possible_moves_list.add(Pair(nextsquare_northrow, cur_col))
            }

            //if next square is not null
            else{
                // if next square is occupied by the same color piece stop expansion
                if(chessBoard[nextsquare_northrow][cur_col]!!.color == current_piece!!.color){
                    break
                }
                //next square is occupied by a different color piece: Potential Capture and Stop Expansion
                else{
                    //if the next square is an enemy piece that is not the king add to possible list of moves. Cannot capture enemy King
                    if(chessBoard[nextsquare_northrow][cur_col]!!.type != "king"){
                        possible_moves_list.add(Pair(nextsquare_northrow, cur_col))
                    }
                    break
                }
            }
            nextsquare_northrow -= 1
        }

        //South Direction
        var nextsquare_southrow = cur_row + 1

        while(nextsquare_southrow <= 7){
            //Next square is blank space
            if(chessBoard[nextsquare_southrow][cur_col] == null){
                possible_moves_list.add(Pair(nextsquare_southrow, cur_col))
            }
            //Next square is not a blank space
            else{
                // if next square is occupied by the same color piece stop expansion
                if(chessBoard[nextsquare_southrow][cur_col]!!.color == current_piece!!.color){
                    break
                }
                //next square is occupied by a different color piece: Potential Capture and Stop Expansion
                else{
                    //if the next square is an enemy piece that is not the king add to possible list of moves. Cannot capture enemy King
                    if(chessBoard[nextsquare_southrow][cur_col]!!.type != "king"){
                        possible_moves_list.add(Pair(nextsquare_southrow, cur_col))
                    }
                    break
                }
            }
            nextsquare_southrow += 1
        }

        //West Direction
        var nextsquare_westcol = cur_col - 1

        while(nextsquare_westcol >= 0){
            //Next square is a blank space
            if(chessBoard[cur_row][nextsquare_westcol] == null){
                possible_moves_list.add(Pair(cur_row, nextsquare_westcol))
            }
            //Next square is not a blank space
            else{
                // if next square is occupied by the same color piece stop expansion
                if(chessBoard[cur_row][nextsquare_westcol]!!.color == current_piece!!.color){
                    break
                }
                // next square is occupied by a different color piece: Potential Capture and Stop Expansion
                else{
                    //if next square is an enemey piece that is not king add to possible list of moves. Cannot capture enemy King
                    if(chessBoard[cur_row][nextsquare_westcol]!!.type != "king"){
                        possible_moves_list.add(Pair(cur_row, nextsquare_westcol))
                    }
                    break
                }
            }
            nextsquare_westcol -= 1
        }

        //East Direction
        var nextsquare_eastcol = cur_col + 1

        while(nextsquare_eastcol <= 7){
            //Next square is a blank space
            if(chessBoard[cur_row][nextsquare_eastcol] == null){
                possible_moves_list.add(Pair(cur_row, nextsquare_eastcol))
            }
            //Next square is not a blank space
            else{
                // if next square is occupied by the same color piece stop expansion
                if(chessBoard[cur_row][nextsquare_eastcol]!!.color == current_piece!!.color){
                    break
                }
                // next square is occupied by a different color piece: Potential Capture and Stop Expansion
                else{
                    //if next square is an enemey piece that is not king add to possible list of moves. Cannot capture enemy King
                    if(chessBoard[cur_row][nextsquare_eastcol]!!.type != "king"){
                        possible_moves_list.add(Pair(cur_row, nextsquare_eastcol))
                    }
                    break
                }
            }
            nextsquare_eastcol += 1
        }


        //***Queen is a combination of Rook and Bishop ***//
        //***Same Code as Bishop***
        //NE Direction: Row # decrease, column # increases
        var row_NE = cur_row - 1
        var col_NE = cur_col + 1

        while(row_NE >= 0 && col_NE <= 7){
            //Next square is a blank space
            if(chessBoard[row_NE][col_NE] == null){
                possible_moves_list.add(Pair(row_NE, col_NE))
            }
            //Next square is not a blank space
            else{
                // if next square is occupied by the same color piece stop expansion
                if(chessBoard[row_NE][col_NE]!!.color == current_piece!!.color){
                    break
                }
                // next square is occupied by a different color piece: Potential Capture and Stop Expansion
                else{
                    //if next square is an enemey piece that is not king add to possible list of moves. Cannot capture enemy King
                    if(chessBoard[row_NE][col_NE]!!.type != "king"){
                        possible_moves_list.add(Pair(row_NE, col_NE))
                    }
                    break
                }
            }
            row_NE -= 1
            col_NE += 1
        }


        //SE Direction: Row # increases, column # increases
        var row_SE = cur_row + 1
        var col_SE = cur_col + 1

        while(row_SE <= 7 && col_SE <= 7){
            //Next square is a blank space
            if(chessBoard[row_SE][col_SE] == null){
                possible_moves_list.add(Pair(row_SE, col_SE))
            }
            //Next square is not a blank space
            else{
                // if next square is occupied by the same color piece stop expansion
                if(chessBoard[row_SE][col_SE]!!.color == current_piece!!.color){
                    break
                }
                // next square is occupied by a different color piece: Potential Capture and Stop Expansion
                else{
                    //if next square is an enemey piece that is not king add to possible list of moves. Cannot capture enemy King
                    if(chessBoard[row_SE][col_SE]!!.type != "king"){
                        possible_moves_list.add(Pair(row_SE, col_SE))
                    }
                    break
                }
            }
            row_SE += 1
            col_SE += 1
        }

        //NW Direction: Row # decreases, column # decreases
        var row_NW = cur_row - 1
        var col_NW = cur_col - 1

        while(row_NW >= 0 && col_NW >= 0){
            //Next square is a blank space
            if(chessBoard[row_NW][col_NW] == null){
                possible_moves_list.add(Pair(row_NW, col_NW))
            }
            //Next square is not a blank space
            else{
                // if next square is occupied by the same color piece stop expansion
                if(chessBoard[row_NW][col_NW]!!.color == current_piece!!.color){
                    break
                }
                // next square is occupied by a different color piece: Potential Capture and Stop Expansion
                else{
                    //if next square is an enemey piece that is not king add to possible list of moves. Cannot capture enemy King
                    if(chessBoard[row_NW][col_NW]!!.type != "king"){
                        possible_moves_list.add(Pair(row_NW, col_NW))
                    }
                    break
                }
            }
            row_NW -= 1
            col_NW -= 1
        }


        //SW Direction: Row # increases, column # decreases
        var row_SW = cur_row + 1
        var col_SW = cur_col - 1

        while(row_SW <= 7 && col_SW >=0){
            //Next square is a blank space
            if(chessBoard[row_SW][col_SW] == null){
                possible_moves_list.add(Pair(row_SW, col_SW))
            }
            //Next square is not a blank space
            else{
                // if next square is occupied by the same color piece stop expansion
                if(chessBoard[row_SW][col_SW]!!.color == current_piece!!.color){
                    break
                }
                // next square is occupied by a different color piece: Potential Capture and Stop Expansion
                else{
                    //if next square is an enemey piece that is not king add to possible list of moves. Cannot capture enemy King
                    if(chessBoard[row_SW][col_SW]!!.type != "king"){
                        possible_moves_list.add(Pair(row_SW, col_SW))
                    }
                    break
                }
            }

            row_SW += 1
            col_SW -= 1
        }



        //All the possible squares for the Queen to move are compiled and the paths are checked
        //Queen is either moving to a blank square or moving to a square with an enemy piece that is not the king

        //If the target position is part of the possible_moves_list
        if(possible_moves_list.contains(targetPosition)){
            //Moving to a blank square
            if(chessBoard[targetPosition.first][targetPosition.second] == null){
                moving_piece_empty_square(sourcePosition, targetPosition)
            }
            //Capturing an enemy piece that is not the king
            else{
                capturing_piece_opposite_color(sourcePosition, targetPosition)
            }
        }
    }

    //Print function to see where all the pieces are currently in the Chess Board.
    fun printChessBoard() {
        for (row in chessBoard.indices) {
            val rowStringBuilder = StringBuilder()
            for (col in chessBoard[row].indices) {
                val piece = chessBoard[row][col]
                if (piece != null) {
                    rowStringBuilder.append("${piece.color}_${piece.type} : ")
                } else {
                    rowStringBuilder.append("Empty : ")
                }
            }
            println("Row $row: ${rowStringBuilder.toString()}")
        }
    }

    //Print RemainingWhitePieces type and coordinates
    fun printRemainingWhitePieces() {
        println("Remaining White Pieces:")
        for (piece in remaining_white_pieces) {
            println("${piece.type}_${piece.color} : ${piece.position}")
        }
    }

    //Print RemainingBlackPieces type and coordinates
    fun printRemainingBlackPieces() {
        println("Remaining Black Pieces:")
        for (piece in remaining_black_pieces) {
            println("${piece.type}_${piece.color} : ${piece.position}")
        }
    }

}