package com.example.promochess

import android.util.Log

class VirtualChessGame {
    private lateinit var chessBoard: Array<Array<ChessPiece?>>
    private lateinit var remainingWhitePieces: MutableList<ChessPiece>
    private lateinit var remainingBlackPieces: MutableList<ChessPiece>
    private var whiteTurn: Boolean = true
    private var moveCounter: Int = 0

    fun initializeGame(
        initialChessBoard: Array<Array<ChessPiece?>>,
        initialRemainingWhitePieces: MutableList<ChessPiece>,
        initialRemainingBlackPieces: MutableList<ChessPiece>,
        initialWhiteTurn: Boolean,
        initialMoveCounter: Int
    ) {
        chessBoard = initialChessBoard.map { it.clone() }.toTypedArray()
        remainingWhitePieces = initialRemainingWhitePieces.toList() as MutableList<ChessPiece>
        remainingBlackPieces = initialRemainingBlackPieces.toList() as MutableList<ChessPiece>
        whiteTurn = initialWhiteTurn
        moveCounter = initialMoveCounter
    }

    fun playMove(sourcePosition: Pair<Int, Int>, targetPosition: Pair<Int, Int>): Boolean {
        // Implement move logic here
        // You can access the game state properties directly
        // Update the game state based on the move
//        Log.d("Virtual Source Position", "Row #: ${sourcePosition.first} Column#: ${sourcePosition.second}")
//        Log.d("Virtual Target Position", "Row #: ${targetPosition.first} Column#: ${targetPosition.second}")



//        // Return true if the move is valid, otherwise false
        val current_piece = chessBoard[sourcePosition.first][sourcePosition.second]
//
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
            moveKing(sourcePosition, targetPosition)
        }

        //printChessBoard()
        //printRemainingWhitePieces()
        //printRemainingBlackPieces()

        // Find the position of the current player's king
        val currentPlayerKingPosition = if (whiteTurn) {
            remainingWhitePieces.find { it.type == "king" }?.position
        } else {
            remainingBlackPieces.find { it.type == "king" }?.position
        }

        val squares_attacked_enemy = all_squares_attacked_by_enemy()

        if(squares_attacked_enemy.contains(currentPlayerKingPosition)){
            return false
        }

        return true // Placeholder return value
    }


    fun all_squares_attacked_by_enemy(): List<Pair<Int, Int>>{

        var squares_attacked = mutableListOf<Pair<Int, Int>>()

        val enemyPieces = if (whiteTurn) remainingBlackPieces else remainingWhitePieces

        for(enemyPiece in enemyPieces){
           var attacked_squares_cur_piece = mutableListOf<Pair<Int, Int>>()
           var cur_piece_position = enemyPiece.position

            //if enemey piece is a knight
            if(enemyPiece.type == "knight"){
                val up_left_pos = Pair(cur_piece_position.first - 2, cur_piece_position.second - 1)
                val up_right_pos = Pair(cur_piece_position.first - 2, cur_piece_position.second + 1)
                val down_right_pos = Pair(cur_piece_position.first + 2, cur_piece_position.second - 1)
                val down_left_pos = Pair(cur_piece_position.first + 2, cur_piece_position.second + 1)
                val left_down_pos = Pair(cur_piece_position.first + 1, cur_piece_position.second - 2)
                val left_up_pos = Pair(cur_piece_position.first -1, cur_piece_position.second - 2)
                val right_down_pos = Pair(cur_piece_position.first + 1, cur_piece_position.second + 2)
                val right_up_pos = Pair(cur_piece_position.first -1, cur_piece_position.second + 2)

                val possiblesquares_attackedKnight = listOf(up_left_pos, up_right_pos, down_right_pos, down_left_pos, left_down_pos,
                    left_up_pos, right_down_pos, right_up_pos )

                attacked_squares_cur_piece.addAll(possiblesquares_attackedKnight)

            }
            //if enemy piece is a king
            else if(enemyPiece.type == "king"){
                val cur_row = cur_piece_position.first
                val cur_col = cur_piece_position.second


                //Eight Possible Moves N, S, E, W, NE, NW, SE, SW
                val N = Pair(cur_row - 1, cur_col)
                val S = Pair(cur_row + 1, cur_col)
                val E = Pair(cur_row, cur_col + 1)
                val W = Pair(cur_row, cur_col - 1)


                val NE = Pair(cur_row -1, cur_col + 1)
                val SE = Pair(cur_row + 1, cur_col + 1)
                val NW = Pair(cur_row -1, cur_col - 1)
                val SW = Pair(cur_row + 1, cur_col - 1)


                val possiblesquares_attackedKing = listOf(
                    N, S, E, W, NE, SE, NW, SW
                )
                attacked_squares_cur_piece.addAll(possiblesquares_attackedKing)
            }
            // if enemy piece is a pawn
            else if(enemyPiece.type == "pawn"){
                val capture_right_position = if(enemyPiece.color == "white") Pair(cur_piece_position.first - 1, cur_piece_position.second + 1) else Pair(cur_piece_position.first + 1, cur_piece_position.second - 1)
                val capture_left_position = if(enemyPiece.color == "black") Pair(cur_piece_position.first - 1, cur_piece_position.second -1) else Pair(cur_piece_position.first + 1, cur_piece_position.second + 1)

                val possiblesquares_attackedPawn = listOf(capture_left_position, capture_right_position)

                attacked_squares_cur_piece.addAll(possiblesquares_attackedPawn)
            }
            // if enemy piece is a bishop
            else if(enemyPiece.type == "bishop"){
                val possiblesquares_attackedBishop = mutableListOf<Pair<Int, Int>>()

                val cur_row = cur_piece_position.first
                val cur_col = cur_piece_position.second

                //NE Direction: Row # decrease, column # increases
                var row_NE = cur_row - 1
                var col_NE = cur_col + 1

                while(row_NE >= 0 && col_NE <= 7){
                    //Next square is a blank space
                    if(chessBoard[row_NE][col_NE] == null){
                        possiblesquares_attackedBishop.add(Pair(row_NE, col_NE))
                    }
                    //Next square is not a blank space
                    else{
                        possiblesquares_attackedBishop.add(Pair(row_NE, col_NE))
                        break
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
                        possiblesquares_attackedBishop.add(Pair(row_SE, col_SE))
                    }
                    //Next square is not a blank space
                    else{
                        possiblesquares_attackedBishop.add(Pair(row_SE, col_SE))
                        break
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
                        possiblesquares_attackedBishop.add(Pair(row_NW, col_NW))
                    }
                    //Next square is not a blank space
                    else{
                        possiblesquares_attackedBishop.add(Pair(row_NW, col_NW))
                        break
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
                        possiblesquares_attackedBishop.add(Pair(row_SW, col_SW))
                    }
                    //Next square is not a blank space
                    else{
                        possiblesquares_attackedBishop.add(Pair(row_SW, col_SW))
                        break
                    }
                    row_SW += 1
                    col_SW -= 1
                }
                attacked_squares_cur_piece.addAll(possiblesquares_attackedBishop)
            }
            // if enemy piece is a rook
            else if(enemyPiece.type == "rook"){
                val cur_row = cur_piece_position.first
                val cur_col = cur_piece_position.second

                val possiblesquares_attackedRook = mutableListOf<Pair<Int, Int>>()

                //North Direction (Row # decreases column stays the same)
                var nextsquare_northrow = cur_row - 1

                //Make Sure while loop condition changes at the end
                while (nextsquare_northrow >= 0){
                    //if the next square is empty add to possible move  //(Move to Blank Square)
                    if(chessBoard[nextsquare_northrow][cur_col] == null){
                        possiblesquares_attackedRook.add(Pair(nextsquare_northrow, cur_col))
                    }

                    //if next square is not null I am either attacking enemy piece or defending my own piece. Either way that square should be added and break
                    else{
                        possiblesquares_attackedRook.add(Pair(nextsquare_northrow, cur_col))
                        break
                    }
                    nextsquare_northrow -= 1
                }

                //South Direction
                var nextsquare_southrow = cur_row + 1

                while(nextsquare_southrow <= 7){
                    //Next square is blank space
                    if(chessBoard[nextsquare_southrow][cur_col] == null){
                        possiblesquares_attackedRook.add(Pair(nextsquare_southrow, cur_col))
                    }
                    //if next square is not null I am either attacking enemy piece or defending my own piece. Either way that square should be added and break
                    else{
                        possiblesquares_attackedRook.add(Pair(nextsquare_southrow, cur_col))
                        break

                    }
                    nextsquare_southrow += 1
                }

                //West Direction
                var nextsquare_westcol = cur_col - 1

                while(nextsquare_westcol >= 0){
                    //Next square is a blank space
                    if(chessBoard[cur_row][nextsquare_westcol] == null){
                        possiblesquares_attackedRook.add(Pair(cur_row, nextsquare_westcol))
                    }
                    //if next square is not null I am either attacking enemy piece or defending my own piece. Either way that square should be added and break
                    else{
                        possiblesquares_attackedRook.add(Pair(cur_row, nextsquare_westcol))
                        break
                    }
                    nextsquare_westcol -= 1
                }

                //East Direction
                var nextsquare_eastcol = cur_col + 1

                while(nextsquare_eastcol <= 7){
                    //Next square is a blank space
                    if(chessBoard[cur_row][nextsquare_eastcol] == null){
                        possiblesquares_attackedRook.add(Pair(cur_row, nextsquare_eastcol))
                    }
                    //Next square is not a blank space
                    else{
                        possiblesquares_attackedRook.add(Pair(cur_row, nextsquare_eastcol))
                        break
                    }
                    nextsquare_eastcol += 1
                }

                attacked_squares_cur_piece.addAll(possiblesquares_attackedRook)
            }
            //enemy piece is a queen
            else{
                val possiblesquares_attackedQueen = mutableListOf<Pair<Int, Int>>()
                val cur_row = cur_piece_position.first
                val cur_col = cur_piece_position.second


                //Queen is a combination of the Bishop and Rook
                //Rook 4 Paths
                //North Direction (Row # decreases column stays the same)
                var nextsquare_northrow = cur_row - 1

                //Make Sure while loop condition changes at the end
                while (nextsquare_northrow >= 0){
                    //if the next square is empty add to possible move  //(Move to Blank Square)
                    if(chessBoard[nextsquare_northrow][cur_col] == null){
                        possiblesquares_attackedQueen.add(Pair(nextsquare_northrow, cur_col))
                    }

                    //if next square is not null I am either attacking enemy piece or defending my own piece. Either way that square should be added and break
                    else{
                        possiblesquares_attackedQueen.add(Pair(nextsquare_northrow, cur_col))
                        break
                    }
                    nextsquare_northrow -= 1
                }

                //South Direction
                var nextsquare_southrow = cur_row + 1

                while(nextsquare_southrow <= 7){
                    //Next square is blank space
                    if(chessBoard[nextsquare_southrow][cur_col] == null){
                        possiblesquares_attackedQueen.add(Pair(nextsquare_southrow, cur_col))
                    }
                    //if next square is not null I am either attacking enemy piece or defending my own piece. Either way that square should be added and break
                    else{
                        possiblesquares_attackedQueen.add(Pair(nextsquare_southrow, cur_col))
                        break

                    }
                    nextsquare_southrow += 1
                }

                //West Direction
                var nextsquare_westcol = cur_col - 1

                while(nextsquare_westcol >= 0){
                    //Next square is a blank space
                    if(chessBoard[cur_row][nextsquare_westcol] == null){
                        possiblesquares_attackedQueen.add(Pair(cur_row, nextsquare_westcol))
                    }
                    //if next square is not null I am either attacking enemy piece or defending my own piece. Either way that square should be added and break
                    else{
                        possiblesquares_attackedQueen.add(Pair(cur_row, nextsquare_westcol))
                        break
                    }
                    nextsquare_westcol -= 1
                }

                //East Direction
                var nextsquare_eastcol = cur_col + 1

                while(nextsquare_eastcol <= 7){
                    //Next square is a blank space
                    if(chessBoard[cur_row][nextsquare_eastcol] == null){
                        possiblesquares_attackedQueen.add(Pair(cur_row, nextsquare_eastcol))
                    }
                    //Next square is not a blank space
                    else{
                        possiblesquares_attackedQueen.add(Pair(cur_row, nextsquare_eastcol))
                        break
                    }
                    nextsquare_eastcol += 1
                }

                //Queen is a combination of the Bishop and Rook
                //Bishop 4 Paths
                //NE Direction: Row # decrease, column # increases
                var row_NE = cur_row - 1
                var col_NE = cur_col + 1

                while(row_NE >= 0 && col_NE <= 7){
                    //Next square is a blank space
                    if(chessBoard[row_NE][col_NE] == null){
                        possiblesquares_attackedQueen.add(Pair(row_NE, col_NE))
                    }
                    //Next square is not a blank space
                    else{
                        possiblesquares_attackedQueen.add(Pair(row_NE, col_NE))
                        break
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
                        possiblesquares_attackedQueen.add(Pair(row_SE, col_SE))
                    }
                    //Next square is not a blank space
                    else{
                        possiblesquares_attackedQueen.add(Pair(row_SE, col_SE))
                        break
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
                        possiblesquares_attackedQueen.add(Pair(row_NW, col_NW))
                    }
                    //Next square is not a blank space
                    else{
                        possiblesquares_attackedQueen.add(Pair(row_NW, col_NW))
                        break
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
                        possiblesquares_attackedQueen.add(Pair(row_SW, col_SW))
                    }
                    //Next square is not a blank space
                    else{
                        possiblesquares_attackedQueen.add(Pair(row_SW, col_SW))
                        break
                    }
                    row_SW += 1
                    col_SW -= 1

                }
                attacked_squares_cur_piece.addAll(possiblesquares_attackedQueen)
            }
            // Add the calculated attacked squares for the current piece
            squares_attacked.addAll(attacked_squares_cur_piece.filter {
                it.first in 0..7 && it.second in 0..7 && !squares_attacked.contains(it)
            })
        }

        return squares_attacked
    }



    // Other methods for game state manipulation and validation
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
        val remainingPieces = if (cur_piece.color == "white") remainingWhitePieces else remainingBlackPieces
        val index = remainingPieces.indexOfFirst { it.position == sourcePosition }
        //Log.d("Moving Piece Empty Square Index", "$index")
        if (index != -1) {
            remainingPieces[index].position = targetPosition
        }
        //Log.d("Moving Piece Empty Square Index", "$index")

        //Taking away castling rights for king and rook if they still have it
        if(cur_piece.type == "king" || cur_piece.type == "rook"){
            if(cur_piece.castlingRight){
                cur_piece.castlingRight = false
            }
        }

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
            val remainingPieces = if (cur_piece.color == "white") remainingWhitePieces else remainingBlackPieces
            val index = remainingPieces.indexOfFirst { it.position == sourcePosition }
            //Log.d("Capturing Piece Opposite Color Index", "$index")
            if (index != -1) {
                remainingPieces[index].position = targetPosition
            }
            //Log.d("Capturing Piece Opposite Color Index", "$index")
        }

        // Remove the captured piece from remaining_pieces of the opposite color
        val remainingOppositePieces = if (cur_piece?.color == "white") remainingBlackPieces else remainingWhitePieces
        remainingOppositePieces.removeIf { it.position == targetPosition }


        //Taking away castling rights for king and rook if they still have it
        if(cur_piece!!.type == "king" || cur_piece.type == "rook"){
            if(cur_piece.castlingRight){
                cur_piece.castlingRight = false
            }
        }
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
                    current_piece.enpassantMoveFlag = moveCounter
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
                if(targetPosition == jump_once_position && chessBoard[jump_once_position.first][jump_once_position.second] == null){
                    specialMove_PromotionBlank(sourcePosition, targetPosition)
                }
                else if(targetPosition == capture_right_position && chessBoard[targetPosition.first][targetPosition.second] != null){
                    if(chessBoard[targetPosition.first][targetPosition.second]!!.color == "black" && chessBoard[targetPosition.first][targetPosition.second]!!.type != "king"){
                        specialMove_PromotionCapture(sourcePosition, targetPosition)
                    }
                }
                else if(targetPosition == capture_left_position && chessBoard[targetPosition.first][targetPosition.second] != null){
                    if(chessBoard[targetPosition.first][targetPosition.second]!!.color == "black" && chessBoard[targetPosition.first][targetPosition.second]!!.type != "king"){
                        specialMove_PromotionCapture(sourcePosition, targetPosition)
                    }
                }
            }

            //Check case for special move en passant. White pawn will be in row 3 and neighboring black pawn double jumps in previous turn to row 3
            else if(sourcePosition.first == 3){
                //Can't get right and left_neighbor both on the edge of the board
                //Column 0 and 7 only have 1 neighbor each

                val left_neighbor_pos = Pair(sourcePosition.first, sourcePosition.second - 1)
                val right_neighbor_pos = Pair(sourcePosition.first, sourcePosition.second + 1)


                //if white pawn is in row 3 and targetposition/capture right position match and targetPos is empty then white pawn can move to target after en-passant
                if(targetPosition == capture_right_position && chessBoard[right_neighbor_pos.first][right_neighbor_pos.second]!= null && chessBoard[targetPosition.first][targetPosition.second] == null){
                    //check if right_neighbor is a black_pawn that just double jumped on previous move
                    if(chessBoard[right_neighbor_pos.first][right_neighbor_pos.second]!!.color == "black" && chessBoard[right_neighbor_pos.first][right_neighbor_pos.second]!!.type == "pawn" && chessBoard[right_neighbor_pos.first][right_neighbor_pos.second]!!.enpassantMoveFlag == moveCounter - 1){
                        specialMove_enpassant(sourcePosition, targetPosition, right_neighbor_pos)
                    }
                }
                else if(targetPosition == capture_left_position && chessBoard[left_neighbor_pos.first][left_neighbor_pos.second] != null && chessBoard[targetPosition.first][targetPosition.second] == null){
                    if(chessBoard[left_neighbor_pos.first][left_neighbor_pos.second]!!.color == "black" && chessBoard[left_neighbor_pos.first][left_neighbor_pos.second]!!.type == "pawn" && chessBoard[left_neighbor_pos.first][left_neighbor_pos.second]!!.enpassantMoveFlag == moveCounter - 1){
                        specialMove_enpassant(sourcePosition, targetPosition, left_neighbor_pos)
                    }
                }
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
                    current_piece.enpassantMoveFlag = moveCounter
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
                if(targetPosition == jump_once_position && chessBoard[jump_once_position.first][jump_once_position.second] == null){
                    specialMove_PromotionBlank(sourcePosition, targetPosition)
                }
                else if(targetPosition == capture_right_position && chessBoard[targetPosition.first][targetPosition.second] != null){
                    if(chessBoard[targetPosition.first][targetPosition.second]!!.color == "white" && chessBoard[targetPosition.first][targetPosition.second]!!.type != "king"){
                        specialMove_PromotionCapture(sourcePosition, targetPosition)
                    }
                }
                else if(targetPosition == capture_left_position && chessBoard[targetPosition.first][targetPosition.second] != null){
                    if(chessBoard[targetPosition.first][targetPosition.second]!!.color == "white" && chessBoard[targetPosition.first][targetPosition.second]!!.type != "king"){
                        specialMove_PromotionCapture(sourcePosition, targetPosition)
                    }
                }
            }
            //Check case for special move en passant. Black pawn will be in row 4 and neighboring white pawn double jumps in previous turn to row 4 to avoid battle
            //Special Move En-Passant
            else if(sourcePosition.first == 4)   {

                val left_neighbor_pos = Pair(sourcePosition.first, sourcePosition.second + 1)
                val right_neighbor_pos = Pair(sourcePosition.first, sourcePosition.second -1)



                //if black pawn is in row 4 and targetposition/capture right position match and targetPos is empty then black pawn can move to target after en-passant
                if(targetPosition == capture_right_position && chessBoard[right_neighbor_pos.first][right_neighbor_pos.second]!= null && chessBoard[targetPosition.first][targetPosition.second] == null){
                    //check if right_neighbor is a white_pawn that just double jumped on previous move
                    if(chessBoard[right_neighbor_pos.first][right_neighbor_pos.second]!!.color == "white" && chessBoard[right_neighbor_pos.first][right_neighbor_pos.second]!!.type == "pawn" && chessBoard[right_neighbor_pos.first][right_neighbor_pos.second]!!.enpassantMoveFlag == moveCounter - 1){
                        specialMove_enpassant(sourcePosition, targetPosition, right_neighbor_pos)
                    }
                }
                else if(targetPosition == capture_left_position && chessBoard[left_neighbor_pos.first][left_neighbor_pos.second] != null && chessBoard[targetPosition.first][targetPosition.second] == null){
                    if(chessBoard[left_neighbor_pos.first][left_neighbor_pos.second]!!.color == "white" && chessBoard[left_neighbor_pos.first][left_neighbor_pos.second]!!.type == "pawn" && chessBoard[left_neighbor_pos.first][left_neighbor_pos.second]!!.enpassantMoveFlag == moveCounter - 1){
                        specialMove_enpassant(sourcePosition, targetPosition, left_neighbor_pos)
                    }
                }
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

    fun moveKing(sourcePosition: Pair<Int, Int>, targetPosition: Pair<Int, Int>){

        val current_piece = chessBoard[sourcePosition.first][sourcePosition.second]
        val possible_moves_list = mutableListOf<Pair<Int, Int>>()
        val cur_row = sourcePosition.first
        val cur_col = sourcePosition.second


        //Eight Possible Moves N, S, E, W, NE, NW, SE, SW
        val N = Pair(cur_row - 1, cur_col)
        val S = Pair(cur_row + 1, cur_col)
        val E = Pair(cur_row, cur_col + 1)
        val W = Pair(cur_row, cur_col - 1)


        val NE = Pair(cur_row -1, cur_col + 1)
        val SE = Pair(cur_row + 1, cur_col + 1)
        val NW = Pair(cur_row -1, cur_col - 1)
        val SW = Pair(cur_row + 1, cur_col - 1)


        val possibleMoves = listOf(
            N, S, E, W, NE, SE, NW, SW
        )


        //King is moving to an empty square
        if(chessBoard[targetPosition.first][targetPosition.second] == null) {
            // Check if the targetPosition matches any of the possible King moves
            // If it does update the Game State.
            if (targetPosition in possibleMoves) {
                moving_piece_empty_square(sourcePosition, targetPosition)
            }
        }

        //King is trying to move to an occupied square: if the colors of the pieces are different then this is a capture attempt.
        //Make sure you are not capturing the Enemy King
        else if(chessBoard[targetPosition.first][targetPosition.second] != null){
            if(targetPosition in possibleMoves && current_piece!!.color != chessBoard[targetPosition.first][targetPosition.second]!!.color && chessBoard[targetPosition.first][targetPosition.second]!!.type != "king"){
                capturing_piece_opposite_color(sourcePosition, targetPosition)
            }
        }

        //Check 4 Special Coordinates/Cases for Special Move: Castling
        //(White Kingside Castling, White Queenside Castling, Black KingSide Castling, Black QueenSide Castling)
        //Target Pos will not be in the list of normal moves Above for King Castling

        //White KingSide Castling Pass in SourcePos (7,4) Target Pos (7,6)
        if (sourcePosition.first == 7 && sourcePosition.second == 4 && targetPosition.first == 7 && targetPosition.second == 6 && current_piece!!.color == "white"){
            //Checking if the path is empty for the player to castle and there is a white rook in the corner
            if(chessBoard[7][5] == null && chessBoard[7][6] == null && chessBoard[7][7] != null){
                if(chessBoard[7][7]!!.color == "white" && chessBoard[7][7]!!.type == "rook"){
                    if(current_piece.castlingRight && chessBoard[7][7]!!.castlingRight){
                        special_Move_Castling(sourcePosition, targetPosition)
                    }
                }
            }
        }
        //White QueenSide Castling  Pass in SourcePos (7,4) TargetPos (7,2)
        if (sourcePosition.first == 7 && sourcePosition.second == 4 && targetPosition.first == 7 && targetPosition.second == 2 && current_piece!!.color == "white"){
            //Checking if the path is empty for the player to castle and there is a white rook in the corner
            if(chessBoard[7][3] == null && chessBoard[7][2] == null && chessBoard[7][1] == null && chessBoard[7][0] != null){
                if(chessBoard[7][0]!!.color == "white" && chessBoard[7][0]!!.type == "rook"){
                    if(current_piece.castlingRight && chessBoard[7][7]!!.castlingRight){
                        special_Move_Castling(sourcePosition, targetPosition)
                    }
                }
            }
        }

        //Black KingSide Castling Pass in Source Pos (0,4) Target Pos (0,6)
        if(sourcePosition.first == 0 && sourcePosition.second == 4 && targetPosition.first == 0 && targetPosition.second == 6 && current_piece!!.color == "black"){
            //Check if the path is empty for the player to castle and there is a black rook in the corner
            if(chessBoard[0][5] == null && chessBoard[0][6] == null && chessBoard[0][7] != null){
                if(chessBoard[0][7]!!.color == "black" && chessBoard[0][7]!!.type == "rook"){
                    if(current_piece.castlingRight && chessBoard[0][7]!!.castlingRight){
                        special_Move_Castling(sourcePosition, targetPosition)
                    }
                }
            }
        }


        //Black QueenSide Castling Pass in Source Pos(0, 4) Target Post (0, 2)
        if (sourcePosition.first == 0 && sourcePosition.second == 4 && targetPosition.first == 0 && targetPosition.second == 2 && current_piece!!.color == "black"){
            //Checking if the path is empty for the player to castle and there is a white rook in the corner
            if(chessBoard[0][3] == null && chessBoard[0][2] == null && chessBoard[0][1] == null && chessBoard[0][0] != null){
                if(chessBoard[0][0]!!.color == "black" && chessBoard[0][0]!!.type == "rook"){
                    if(current_piece.castlingRight && chessBoard[0][0]!!.castlingRight){
                        special_Move_Castling(sourcePosition, targetPosition)
                    }
                }
            }
        }
    }

    fun special_Move_Castling(sourcePosition: Pair<Int, Int>, targetPosition: Pair<Int, Int>){

        //Black King Side Castling
        if(sourcePosition == Pair(0,4) && targetPosition == Pair(0,6)){
            val cur_black_king_pos = Pair(0, 4)
            val cur_black_rook_pos = Pair(0, 7)

            val updated_black_king_pos = Pair(0,6)
            val updated_black_rook_pos = Pair(0,5)


            // Move the black king to the target position in the ChessBoard and set previous position in the ChessBoard to Null
            chessBoard[updated_black_king_pos.first][updated_black_king_pos.second] = chessBoard[cur_black_king_pos.first][cur_black_king_pos.second]
            chessBoard[cur_black_king_pos.first][cur_black_king_pos.second] = null

            // Update the position of the black king and castling right property of the black king
            chessBoard[updated_black_king_pos.first][updated_black_king_pos.second]!!.position = updated_black_king_pos
            chessBoard[updated_black_king_pos.first][updated_black_king_pos.second]!!.castlingRight = false

            // Move the black rook to the target position
            chessBoard[updated_black_rook_pos.first][updated_black_rook_pos.second] = chessBoard[cur_black_rook_pos.first][cur_black_rook_pos.second]
            chessBoard[cur_black_rook_pos.first][cur_black_rook_pos.second] = null

            // Update the position of the black rook and the castling right property of the black rook
            chessBoard[updated_black_rook_pos.first][updated_black_rook_pos.second]!!.position = updated_black_rook_pos
            chessBoard[updated_black_rook_pos.first][updated_black_rook_pos.second]!!.castlingRight = false

            // Find and update the position of the king in the list of remaining pieces
            val blackKingIndex = remainingBlackPieces.indexOfFirst { it.position == cur_black_king_pos }
            //Log.d("Black King Index in remaining_black_pieces", "$blackKingIndex")
            if (blackKingIndex != -1) {
                remainingBlackPieces[blackKingIndex].position = updated_black_king_pos
            }

            // Find and update the position of the rook in the list of remaining pieces
            val blackRookIndex = remainingBlackPieces.indexOfFirst { it.position == cur_black_rook_pos }
            if (blackRookIndex != -1) {
                remainingBlackPieces[blackRookIndex].position = updated_black_rook_pos
            }
        }

        //Black Queen Side Castling
        else if(sourcePosition == Pair(0,4) && targetPosition == Pair(0,2)){
            val cur_black_king_pos = Pair(0, 4)
            val cur_black_rook_pos = Pair(0, 0)

            val updated_black_king_pos = Pair(0,2)
            val updated_black_rook_pos = Pair(0,3)

            // Move the black king to the target position
            chessBoard[updated_black_king_pos.first][updated_black_king_pos.second] = chessBoard[cur_black_king_pos.first][cur_black_king_pos.second]
            chessBoard[cur_black_king_pos.first][cur_black_king_pos.second] = null

            // Update the position of the black king and castling right property of the black king
            chessBoard[updated_black_king_pos.first][updated_black_king_pos.second]!!.position = updated_black_king_pos
            chessBoard[updated_black_king_pos.first][updated_black_king_pos.second]!!.castlingRight = false

            // Move the black rook to the target position
            chessBoard[updated_black_rook_pos.first][updated_black_rook_pos.second] = chessBoard[cur_black_rook_pos.first][cur_black_rook_pos.second]
            chessBoard[cur_black_rook_pos.first][cur_black_rook_pos.second] = null

            // Update the position of the black rook and the castling right property of the black rook
            chessBoard[updated_black_rook_pos.first][updated_black_rook_pos.second]!!.position = updated_black_rook_pos
            chessBoard[updated_black_rook_pos.first][updated_black_rook_pos.second]!!.castlingRight = false


            // Find and update the position of the king in the list of remaining pieces
            val blackKingIndex = remainingBlackPieces.indexOfFirst { it.position == cur_black_king_pos }
            if (blackKingIndex != -1) {
                remainingBlackPieces[blackKingIndex].position = updated_black_king_pos
            }

            // Find and update the position of the rook in the list of remaining pieces
            val blackRookIndex = remainingBlackPieces.indexOfFirst { it.position == cur_black_rook_pos }
            if (blackRookIndex != -1) {
                remainingBlackPieces[blackRookIndex].position = updated_black_rook_pos
            }
        }

        //White King Side Castling
        else if(sourcePosition == Pair(7,4) && targetPosition == Pair(7,6)){
            val cur_white_king_pos = Pair(7,4)
            val cur_white_rook_pos = Pair(7,7)

            val updated_white_king_pos = Pair(7, 6)
            val updated_white_rook_pos = Pair(7, 5)

            // Move the white king to the target position
            chessBoard[updated_white_king_pos.first][updated_white_king_pos.second] = chessBoard[cur_white_king_pos.first][cur_white_king_pos.second]
            chessBoard[cur_white_king_pos.first][cur_white_king_pos.second] = null

            // Update the position of the white king and the castling right of the white king
            chessBoard[updated_white_king_pos.first][updated_white_king_pos.second]!!.position = updated_white_king_pos
            chessBoard[updated_white_king_pos.first][updated_white_king_pos.second]!!.castlingRight = false

            // Move the white rook to the target position
            chessBoard[updated_white_rook_pos.first][updated_white_rook_pos.second] = chessBoard[cur_white_rook_pos.first][cur_white_rook_pos.second]
            chessBoard[cur_white_rook_pos.first][cur_white_rook_pos.second] = null

            // Update the position of the white rook and the castling right of the white rook
            chessBoard[updated_white_rook_pos.first][updated_white_rook_pos.second]!!.position = updated_white_rook_pos
            chessBoard[updated_white_rook_pos.first][updated_white_rook_pos.second]!!.castlingRight = false

            //printRemainingWhitePieces()
            // Find and update the position of the king in the list of remaining pieces
            val whiteKingIndex = remainingWhitePieces.indexOfFirst { it.position == cur_white_king_pos }
            //Log.d("White King Index in remaining_white_pieces", "$whiteKingIndex")
            if (whiteKingIndex != -1) {
                remainingWhitePieces[whiteKingIndex].position = updated_white_king_pos
            }
            //printRemainingWhitePieces()

            // Find and update the position of the rook in the list of remaining pieces
            val whiteRookIndex = remainingWhitePieces.indexOfFirst { it.position == cur_white_rook_pos }
            if (whiteRookIndex != -1) {
                remainingWhitePieces[whiteRookIndex].position = updated_white_rook_pos
            }
        }
        //White Queen Side Castling Pair(7,4) && targetPost == (7,2)
        else{
            val cur_white_king_pos = Pair(7, 4)
            val cur_white_rook_pos = Pair(7, 0)

            val updated_white_king_pos = Pair(7, 2)
            val updated_white_rook_pos = Pair(7, 3)


            // Move the white king to the target position
            chessBoard[updated_white_king_pos.first][updated_white_king_pos.second] = chessBoard[cur_white_king_pos.first][cur_white_king_pos.second]
            chessBoard[cur_white_king_pos.first][cur_white_king_pos.second] = null

            // Update the position of the white king and the castlingRight of the white king
            chessBoard[updated_white_king_pos.first][updated_white_king_pos.second]!!.position = updated_white_king_pos
            chessBoard[updated_white_king_pos.first][updated_white_king_pos.second]!!.castlingRight = false

            // Move the white rook to the target position
            chessBoard[updated_white_rook_pos.first][updated_white_rook_pos.second] = chessBoard[cur_white_rook_pos.first][cur_white_rook_pos.second]
            chessBoard[cur_white_rook_pos.first][cur_white_rook_pos.second] = null

            // Update the position of the white rook and the castling right of the white rook
            chessBoard[updated_white_rook_pos.first][updated_white_rook_pos.second]!!.position = updated_white_rook_pos
            chessBoard[updated_white_rook_pos.first][updated_white_rook_pos.second]!!.castlingRight = false


            // Find and update the position of the king in the list of remaining pieces
            val whiteKingIndex = remainingWhitePieces.indexOfFirst { it.position == cur_white_king_pos }
            if (whiteKingIndex != -1) {
                remainingWhitePieces[whiteKingIndex].position = updated_white_king_pos
            }

            // Find and update the position of the rook in the list of remaining pieces
            val whiteRookIndex = remainingWhitePieces.indexOfFirst { it.position == cur_white_rook_pos }
            if (whiteRookIndex != -1) {
                remainingWhitePieces[whiteRookIndex].position = updated_white_rook_pos
            }
        }
    }

    fun specialMove_PromotionBlank(sourcePosition: Pair<Int, Int>, targetPosition: Pair<Int, Int>){
        // Retrieve the current piece at the source position
        val curPiece = chessBoard[sourcePosition.first][sourcePosition.second]

        // Check the color of the current piece
        when (curPiece!!.color) {
            "white" -> {
                // Remove the piece from the chessboard at the source position
                chessBoard[sourcePosition.first][sourcePosition.second] = null

                // Find and remove the piece from remaining_white_pieces list
                val index = remainingWhitePieces.indexOfFirst { it.position == curPiece.position }
                if (index != -1) {
                    remainingWhitePieces.removeAt(index)
                }

                // Create a new piece based on the column of the target position
                val newPiece = when (targetPosition.second) {
                    0, 7 -> ChessPiece("white", "rook", targetPosition.first, targetPosition.second)
                    1, 6 -> ChessPiece("white", "knight", targetPosition.first, targetPosition.second)
                    2, 5 -> ChessPiece("white", "bishop", targetPosition.first, targetPosition.second)
                    else -> ChessPiece("white", "queen", targetPosition.first, targetPosition.second)
                }

                // Set the position of the new piece
                newPiece.position = targetPosition

                // Update the chessboard at the target position with the new piece
                chessBoard[targetPosition.first][targetPosition.second] = newPiece

                // Add the new piece to remaining_white_pieces
                remainingWhitePieces.add(newPiece)
            }
            "black" -> {
                // Remove the piece from the chessboard at the source position
                chessBoard[sourcePosition.first][sourcePosition.second] = null

                // Find and remove the piece from remaining_black_pieces list
                val index = remainingBlackPieces.indexOfFirst { it.position == curPiece.position }
                if (index != -1) {
                    remainingBlackPieces.removeAt(index)
                }

                // Create a new piece based on the column of the target position
                val newPiece = when (targetPosition.second) {
                    0, 7 -> ChessPiece("black", "rook", targetPosition.first, targetPosition.second)
                    1, 6 -> ChessPiece("black", "knight", targetPosition.first, targetPosition.second)
                    2, 5 -> ChessPiece("black", "bishop", targetPosition.first, targetPosition.second)
                    else -> ChessPiece("black", "queen", targetPosition.first, targetPosition.second)
                }

                // Set the position of the new piece
                newPiece.position = targetPosition

                // Update the chessboard at the target position with the new piece
                chessBoard[targetPosition.first][targetPosition.second] = newPiece

                // Add the new piece to remaining_black_pieces
                remainingBlackPieces.add(newPiece)
            }
        }

    }
    fun specialMove_PromotionCapture(sourcePosition: Pair<Int, Int>, targetPosition: Pair<Int, Int>){
        // Retrieve the current piece at the source position
        val curPiece = chessBoard[sourcePosition.first][sourcePosition.second]

        // Retrieve the captured piece at the target position
        val capturedPiece = chessBoard[targetPosition.first][targetPosition.second]

        // Check the color of the current piece
        when (curPiece!!.color) {
            "white" -> {
                // Remove the current piece from the chessboard at the source position
                chessBoard[sourcePosition.first][sourcePosition.second] = null

                // Remove the captured piece from the chessboard at the target position
                chessBoard[targetPosition.first][targetPosition.second] = null

                // Find and remove the current piece from remaining_white_pieces list
                val indexCur =
                    remainingWhitePieces.indexOfFirst { it.position == curPiece.position }
                if (indexCur != -1) {
                    remainingWhitePieces.removeAt(indexCur)
                }

                // Find and remove the captured piece from remaining_black_pieces list
                val indexCaptured =
                    remainingBlackPieces.indexOfFirst { it.position == capturedPiece!!.position }
                if (indexCaptured != -1) {
                    remainingBlackPieces.removeAt(indexCaptured)
                }

                // Create a new piece based on the column of the target position
                val newPiece = when (targetPosition.second) {
                    0, 7 -> ChessPiece("white", "rook", targetPosition.first, targetPosition.second)
                    1, 6 -> ChessPiece(
                        "white",
                        "knight",
                        targetPosition.first,
                        targetPosition.second
                    )

                    2, 5 -> ChessPiece(
                        "white",
                        "bishop",
                        targetPosition.first,
                        targetPosition.second
                    )

                    else -> ChessPiece(
                        "white",
                        "queen",
                        targetPosition.first,
                        targetPosition.second
                    )
                }

                // Set the position of the new piece
                newPiece.position = targetPosition

                // Update the chessboard at the target position with the new piece
                chessBoard[targetPosition.first][targetPosition.second] = newPiece

                // Add the new piece to remaining_white_pieces
                remainingWhitePieces.add(newPiece)
            }

            "black" -> {
                // Remove the current piece from the chessboard at the source position
                chessBoard[sourcePosition.first][sourcePosition.second] = null

                // Remove the captured piece from the chessboard at the target position
                chessBoard[targetPosition.first][targetPosition.second] = null

                // Find and remove the current piece from remaining_black_pieces list
                val indexCur =
                    remainingBlackPieces.indexOfFirst { it.position == curPiece.position }
                if (indexCur != -1) {
                    remainingBlackPieces.removeAt(indexCur)
                }

                // Find and remove the captured piece from remaining_white_pieces list
                val indexCaptured =
                    remainingWhitePieces.indexOfFirst { it.position == capturedPiece!!.position }
                if (indexCaptured != -1) {
                    remainingWhitePieces.removeAt(indexCaptured)
                }

                // Create a new piece based on the column of the target position
                val newPiece = when (targetPosition.second) {
                    0, 7 -> ChessPiece("black", "rook", targetPosition.first, targetPosition.second)
                    1, 6 -> ChessPiece(
                        "black",
                        "knight",
                        targetPosition.first,
                        targetPosition.second
                    )

                    2, 5 -> ChessPiece(
                        "black",
                        "bishop",
                        targetPosition.first,
                        targetPosition.second
                    )

                    else -> ChessPiece(
                        "black",
                        "queen",
                        targetPosition.first,
                        targetPosition.second
                    )
                }

                // Set the position of the new piece
                newPiece.position = targetPosition

                // Update the chessboard at the target position with the new piece
                chessBoard[targetPosition.first][targetPosition.second] = newPiece

                // Add the new piece to remaining_black_pieces
                remainingBlackPieces.add(newPiece)
            }
        }

    }


    fun specialMove_enpassant(sourcePosition: Pair<Int, Int>, targetPosition: Pair<Int, Int>, capturePosition: Pair<Int, Int>){
        val cur_piece = chessBoard[sourcePosition.first][sourcePosition.second]
        val captured_piece = chessBoard[capturePosition.first][capturePosition.second]

//        Log.d("Source Piece Position", "Row:${sourcePosition.first} Column: ${sourcePosition.second}")
//        Log.d("Target Piece Position", "Row ${targetPosition.first} Column: ${targetPosition.second}")
//        Log.d("Captured Piece Position", "Row: ${capturePosition.first} Column: ${capturePosition.second}")

        // Set the captured piece's location in the chessboard to null
        chessBoard[capturePosition.first][capturePosition.second] = null


        // Remove the captured piece from remaining_white_pieces or remaining_black_pieces
        if (cur_piece?.color == "white") {
            val index = remainingBlackPieces.indexOfFirst { it.position == captured_piece?.position }
            if (index != -1) {
                remainingBlackPieces.removeAt(index)
            }
        } else {
            val index = remainingWhitePieces.indexOfFirst { it.position == captured_piece?.position }
            if (index != -1) {
                remainingWhitePieces.removeAt(index)
            }
        }

        // Set the current location in the ChessBoard to null
        chessBoard[sourcePosition.first][sourcePosition.second] = null

        // Set the target location in the ChessBoard to the current piece
        chessBoard[targetPosition.first][targetPosition.second] = cur_piece

        // Update the piece position to the new target location
        cur_piece?.position = targetPosition

        // Find the curPiece in remaining_white_pieces or remaining_black_pieces and update its position
        if (cur_piece?.color == "white") {
            val index = remainingWhitePieces.indexOfFirst { it.position == sourcePosition }
            if (index != -1) {
                remainingWhitePieces[index].position = targetPosition
            }
        } else {
            val index = remainingBlackPieces.indexOfFirst { it.position == sourcePosition }
            if (index != -1) {
                remainingBlackPieces[index].position = targetPosition
            }
        }

    }
    //Print function to see where all the pieces are currently in the Chess Board.
    fun printChessBoard() {
        //println("Move Counter: $moveCounter")

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
            println("Virtual ChessBoard")
            println("Row $row: ${rowStringBuilder.toString()}")
        }
    }

    //Print RemainingWhitePieces type and coordinates
    fun printRemainingWhitePieces() {
        //println("Move Counter: $moveCounter")
        println("Remaining Virtual White Pieces:")
        for (piece in remainingWhitePieces) {
            println("${piece.type}_${piece.color} : ${piece.position}")
        }
    }

    //Print RemainingBlackPieces type and coordinates
    fun printRemainingBlackPieces() {
        //println("Move Counter: $moveCounter")
        println("Remaining Virtual Black Pieces:")
        for (piece in remainingBlackPieces) {
            println("${piece.type}_${piece.color} : ${piece.position}")
        }
    }
}