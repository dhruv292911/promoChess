package com.example.promochess

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.promochess.databinding.FragmentHomeBinding //this is important this is how we use view binding

//Import all the firebase libraries
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

import android.content.Intent
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: ChessBoardViewModel by viewModels()
    private var sourcePosition: Pair<Int, Int>? = null

    private var soure_valid_move: Pair<Int, Int>? = null
    private var target_valid_move: Pair<Int, Int>? = null

    var previousSource: Pair<Int, Int>? = null
    var previousTarget: Pair<Int, Int>? = null


    //we will use prev click to save what square we clicked on previously
    var prev_click: Pair<Int, Int>? = null


    //Setting Up the snapshots for the moveHistory Recycler View
    private lateinit var moveHistoryAdapter: MoveHistoryAdapter
    private val moveSnapshots = mutableListOf<Bitmap>()


    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!  // Ensures binding is non-null when accessed



    //AUTH REQUEST CODE for Firebase Auth
    private val AUTH_REQUEST_CODE = 1234
    //Firebase Login
    private val signInLauncher =
        registerForActivityResult(FirebaseAuthUIActivityResultContract()) { result ->
            val response = result.idpResponse
            if (result.resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(requireContext(), "Welcome, ${user?.email}!", Toast.LENGTH_SHORT).show()

                // Navigate to OnlinePlayFragment
                findNavController().navigate(R.id.action_homeFragment_to_onlinePlayFragment)

            } else {
                // Sign-in failed
                Toast.makeText(requireContext(), "Login failed: ${response?.error?.message}", Toast.LENGTH_SHORT).show()
            }
        }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Here you can set up your UI logic, listeners, etc.
        //Setting Up my recyclerview moveHistory Recycler View
        val recyclerView = binding.moveHistoryRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        moveHistoryAdapter = MoveHistoryAdapter(moveSnapshots)
        recyclerView.adapter = moveHistoryAdapter

        val chessboardLayout = binding.chessboard

        // Calculate the size of each square
        val squareSize =
            resources.displayMetrics.widthPixels / 8 // Assuming the screen width is divided into 8 equal parts
        //val squareSize = 51
        //log.d()
        Log.d("squareSize", "$squareSize")


        val startingPosition = arrayOf(
            arrayOf(
                "black_rook",
                "black_knight",
                "black_bishop",
                "black_queen",
                "black_king",
                "black_bishop",
                "black_knight",
                "black_rook"
            ),
            arrayOf(
                "black_pawn",
                "black_pawn",
                "black_pawn",
                "black_pawn",
                "black_pawn",
                "black_pawn",
                "black_pawn",
                "black_pawn"
            ),
            arrayOf("", "", "", "", "", "", "", ""),
            arrayOf("", "", "", "", "", "", "", ""),
            arrayOf("", "", "", "", "", "", "", ""),
            arrayOf("", "", "", "", "", "", "", ""),
            arrayOf(
                "white_pawn",
                "white_pawn",
                "white_pawn",
                "white_pawn",
                "white_pawn",
                "white_pawn",
                "white_pawn",
                "white_pawn"
            ),
            arrayOf(
                "white_rook",
                "white_knight",
                "white_bishop",
                "white_queen",
                "white_king",
                "white_bishop",
                "white_knight",
                "white_rook"
            )
        )

        // Loop to generate 64 ImageViews
        for (row in 0 until 8) {
            for (column in 0 until 8) {
                val square = ImageView(requireContext())
                square.layoutParams = GridLayout.LayoutParams().apply {
                    width = squareSize
                    height = squareSize
                }

                // Set background color alternatively
                if ((row + column) % 2 == 0) {
                    square.setBackgroundColor(resources.getColor(R.color.white))
                } else {
                    square.setBackgroundColor(resources.getColor(R.color.light_brown))
                }

                // Add the appropriate chess piece image
                val piece = startingPosition[row][column]
                if (piece.isNotEmpty()) {
                    val resourceId = when (piece) {
                        "black_pawn" -> R.drawable.black_pawn
                        "black_rook" -> R.drawable.black_rook
                        "black_knight" -> R.drawable.black_knight
                        "black_bishop" -> R.drawable.black_bishop
                        "black_queen" -> R.drawable.black_queen
                        "black_king" -> R.drawable.black_king
                        "white_pawn" -> R.drawable.white_pawn
                        "white_rook" -> R.drawable.white_rook
                        "white_knight" -> R.drawable.white_knight
                        "white_bishop" -> R.drawable.white_bishop
                        "white_queen" -> R.drawable.white_queen
                        "white_king" -> R.drawable.white_king
                        else -> throw IllegalArgumentException("Invalid piece name: $piece")
                    }
                    square.setImageResource(resourceId)
                }

                // Add the ImageView to the GridLayout
                chessboardLayout.addView(square)

                // Add OnClickListener to each square
                square.setOnClickListener {
                    // Handle click event
                    // You can use the `row` and `column` variables here to determine which square was clicked
                    // and perform the necessary actions
                    // For example:
                    // Log.d("Chess", "Clicked on square ($row, $column)")
                    val position = Pair(row, column)
                    //Log.d("Clicked Square", "Position: $position")

                    //square.setBackgroundResource(R.drawable.border)
                    //Call Handle Click to Handle the Move
                    //1st Click will Check if the right Color piece is clicked
                    //White turn -> White piece Clicked, Black turn --> Black piece Clicked

                    if(prev_click != null){
                        val prev_click_index = prev_click!!.first * 8 + prev_click!!.second
                        val prev_row = prev_click!!.first
                        val prev_col = prev_click!!.second

                        val prevSquare = chessboardLayout.getChildAt(prev_click_index) as? ImageView
                        prevSquare?.setBackgroundResource(0)

                        if ((prev_row + prev_col) % 2 == 0) {
                            prevSquare?.setBackgroundColor(resources.getColor(R.color.white))
                        } else {
                            prevSquare?.setBackgroundColor(resources.getColor(R.color.light_brown))
                        }
                    }

                    square.setBackgroundColor(resources.getColor(R.color.yellow))

                    handleSquareClick(row, column)

                    //set the current square as the new prev square
                    prev_click = Pair(row, column)
                }
            }
        }

        //viewModel.printChessBoard()

        //viewModel.printRemainingWhitePieces()

        //viewModel.printRemainingBlackPieces()


        // Observe the moveUpdated LiveData
        viewModel.moveUpdated.observe(viewLifecycleOwner) { moveUpdated ->
            if (moveUpdated) {
                Log.d("LiveData Received", "moveUpdated Received the LiveData")
                soure_valid_move?.let { source ->
                    target_valid_move?.let { target ->
                        // Update the chessboard display
                        // Get the source and target positions
                        //Log.d("Updating Display", "Starting Update")
                        val sourceSquareIndex =
                            soure_valid_move!!.first * 8 + soure_valid_move!!.second
                        val targetSquareIndex =
                            target_valid_move!!.first * 8 + target_valid_move!!.second
                        // Get the ImageView at the source position and retrieve its image resource
                        val sourceSquare =
                            chessboardLayout.getChildAt(sourceSquareIndex) as? ImageView
                        val sourceImageResource =
                            sourceSquare?.drawable // Get the drawable from the source position

                        // Clear the image resource from the source position
                        sourceSquare?.setImageResource(0)

                        // Get the ImageView at the target position and clear its image resource
                        val targetSquare =
                            chessboardLayout.getChildAt(targetSquareIndex) as? ImageView
                        targetSquare?.setImageResource(0)

                        // Set the image resource of the target position to the source image
                        targetSquare?.setImageDrawable(sourceImageResource)
                        //Log.d("Updating Display", "Finished Update")

                        //Increasing move_counter
                        viewModel.move_counter += 1
                        //viewModel.printChessBoard()
                        //viewModel.printRemainingWhitePieces()
                        //viewModel.printRemainingBlackPieces()


                        // Reset background color of previous source and target squares (if any)
//                        previousSourceSquare?.setBackgroundColor(Color.TRANSPARENT)
//                        previousTargetSquare?.setBackgroundColor(Color.TRANSPARENT)
                        if(previousSource != null){

                            val previoussource_index = previousSource!!.first * 8 + previousSource!!.second

                            val previousSquare = chessboardLayout.getChildAt(previoussource_index) as? ImageView

                            if((previousSource!!.first + previousSource!!.second) % 2 == 0){
                                previousSquare!!.setBackgroundColor(resources.getColor(R.color.white))
                            }else{
                                previousSquare!!.setBackgroundColor(resources.getColor(R.color.light_brown))
                            }
                        }
                        if(previousTarget != null){
                            val previoustarget_index = previousTarget!!.first * 8 + previousTarget!!.second

                            val previousTargetSquare = chessboardLayout.getChildAt(previoustarget_index) as? ImageView

                            if((previousTarget!!.first + previousTarget!!.second) % 2 == 0){
                                previousTargetSquare!!.setBackgroundColor(resources.getColor(R.color.white))
                            }else{
                                previousTargetSquare!!.setBackgroundColor(resources.getColor(R.color.light_brown))
                            }
                        }

                        sourceSquare?.setBackgroundColor(Color.LTGRAY) // Set background color
                        targetSquare?.setBackgroundColor(Color.LTGRAY) // Set background color

                        previousSource = soure_valid_move
                        previousTarget = target_valid_move
//                        // Update previous source and target squares
//                        previousSourceSquare = sourceSquare
//                        previousTargetSquare = targetSquare

                        //Call this function to update the recyclerview
                        onMoveMade()
                    }
                }
            }
        }
        // Observe white castling LiveData
        viewModel.whiteCastling.observe(viewLifecycleOwner) { castling ->
            if (castling) {
                soure_valid_move?.let { source ->
                    target_valid_move?.let { target ->
                        //Below handles the case for the King based on source and target
                        val sourceSquareIndex =
                            soure_valid_move!!.first * 8 + soure_valid_move!!.second
                        val targetSquareIndex =
                            target_valid_move!!.first * 8 + target_valid_move!!.second

                        // Get the ImageView at the source position and retrieve its image resource
                        val sourceSquare =
                            chessboardLayout.getChildAt(sourceSquareIndex) as? ImageView
                        val sourceImageResource =
                            sourceSquare?.drawable // Get the drawable from the source position

                        // Clear the image resource from the source position
                        sourceSquare?.setImageResource(0)

                        // Get the ImageView at the target position and clear its image resource
                        val targetSquare =
                            chessboardLayout.getChildAt(targetSquareIndex) as? ImageView
                        targetSquare?.setImageResource(0)

                        // Set the image resource of the target position to the source image
                        targetSquare?.setImageDrawable(sourceImageResource)
                        //Log.d("Updating Display", "Finished Update")

                        sourceSquare?.setBackgroundColor(Color.LTGRAY) // Set background color
                        targetSquare?.setBackgroundColor(Color.LTGRAY) // Set background color


                        //Now we need to do similar for the rook
                        //King side Castling Rook Source(7,7) Rook Target (7,5)
                        if (target_valid_move == Pair(7, 6)) {
                            val sourceSquareIndexRook = 7 * 8 + 7
                            val targetSquareIndexRook = 7 * 8 + 5

                            // Get the ImageView at the source position and retrieve its image resource
                            val src_sqrook =
                                chessboardLayout.getChildAt(sourceSquareIndexRook) as? ImageView
                            val src_ImgResource = src_sqrook?.drawable

                            // Clear the image resource from the source position
                            src_sqrook?.setImageResource(0)

                            // Get the ImageView at the target position and clear its image resource
                            val target_sqrook =
                                chessboardLayout.getChildAt(targetSquareIndexRook) as? ImageView
                            target_sqrook?.setImageResource(0)

                            // Set the image resource of the target position to the source image
                            target_sqrook?.setImageDrawable(src_ImgResource)

                        }
                        //Queen side Castling Rook Source (7,0) Rook Target(7, 3)
                        else {
                            val sourceSquareIndexRook = 7 * 8 + 0
                            val targetSquareIndexRook = 7 * 8 + 3

                            // Get the ImageView at the source position and retrieve its image resource
                            val src_sqrook =
                                chessboardLayout.getChildAt(sourceSquareIndexRook) as? ImageView
                            val src_ImgResource = src_sqrook?.drawable

                            // Clear the image resource from the source position
                            src_sqrook?.setImageResource(0)

                            // Get the ImageView at the target position and clear its image resource
                            val target_sqrook =
                                chessboardLayout.getChildAt(targetSquareIndexRook) as? ImageView
                            target_sqrook?.setImageResource(0)

                            // Set the image resource of the target position to the source image
                            target_sqrook?.setImageDrawable(src_ImgResource)
                        }
                    }

                    //Increasing move_counter
                    viewModel.move_counter += 1
                    //viewModel.printChessBoard()
                    //viewModel.printRemainingWhitePieces()

                    if(previousSource != null){

                        val previoussource_index = previousSource!!.first * 8 + previousSource!!.second

                        val previousSquare = chessboardLayout.getChildAt(previoussource_index) as? ImageView

                        if((previousSource!!.first + previousSource!!.second) % 2 == 0){
                            previousSquare!!.setBackgroundColor(resources.getColor(R.color.white))
                        }else{
                            previousSquare!!.setBackgroundColor(resources.getColor(R.color.light_brown))
                        }
                    }
                    if(previousTarget != null){
                        val previoustarget_index = previousTarget!!.first * 8 + previousTarget!!.second

                        val previousTargetSquare = chessboardLayout.getChildAt(previoustarget_index) as? ImageView

                        if((previousTarget!!.first + previousTarget!!.second) % 2 == 0){
                            previousTargetSquare!!.setBackgroundColor(resources.getColor(R.color.white))
                        }else{
                            previousTargetSquare!!.setBackgroundColor(resources.getColor(R.color.light_brown))
                        }
                    }


                    previousSource = soure_valid_move
                    previousTarget = target_valid_move
//                        // Update previous source and target squares
//                        previousSourceSquare = sourceSquare
//                        previousTargetSquare = targetSquare

                }
            }
        }
        // Observe black castling LiveData
        viewModel.blackCastling.observe(viewLifecycleOwner) { castling ->
            if (castling) {
                soure_valid_move?.let { source ->
                    target_valid_move?.let { target ->
                        //Below handles the case for the King based on source and target
                        val sourceSquareIndex =
                            soure_valid_move!!.first * 8 + soure_valid_move!!.second
                        val targetSquareIndex =
                            target_valid_move!!.first * 8 + target_valid_move!!.second

                        // Get the ImageView at the source position and retrieve its image resource
                        val sourceSquare =
                            chessboardLayout.getChildAt(sourceSquareIndex) as? ImageView
                        val sourceImageResource =
                            sourceSquare?.drawable // Get the drawable from the source position

                        // Clear the image resource from the source position
                        sourceSquare?.setImageResource(0)

                        // Get the ImageView at the target position and clear its image resource
                        val targetSquare =
                            chessboardLayout.getChildAt(targetSquareIndex) as? ImageView
                        targetSquare?.setImageResource(0)

                        // Set the image resource of the target position to the source image
                        targetSquare?.setImageDrawable(sourceImageResource)
                        //Log.d("Updating Display", "Finished Update")

                        sourceSquare?.setBackgroundColor(Color.LTGRAY) // Set background color
                        targetSquare?.setBackgroundColor(Color.LTGRAY) // Set background color

                        //Now we need to do similar for the rook
                        //King side Castling Rook Source(0,7) Rook Target (0,5)
                        if (target_valid_move == Pair(0, 6)) {
                            val sourceSquareIndexRook = 0 * 8 + 7
                            val targetSquareIndexRook = 0 * 8 + 5

                            // Get the ImageView at the source position and retrieve its image resource
                            val src_sqrook =
                                chessboardLayout.getChildAt(sourceSquareIndexRook) as? ImageView
                            val src_ImgResource = src_sqrook?.drawable

                            // Clear the image resource from the source position
                            src_sqrook?.setImageResource(0)

                            // Get the ImageView at the target position and clear its image resource
                            val target_sqrook =
                                chessboardLayout.getChildAt(targetSquareIndexRook) as? ImageView
                            target_sqrook?.setImageResource(0)

                            // Set the image resource of the target position to the source image
                            target_sqrook?.setImageDrawable(src_ImgResource)

                        }
                        //Queen side Castling Rook Source (0,0) Rook Target(0, 3)
                        else {
                            val sourceSquareIndexRook = 0 * 8 + 0
                            val targetSquareIndexRook = 0 * 8 + 3

                            // Get the ImageView at the source position and retrieve its image resource
                            val src_sqrook =
                                chessboardLayout.getChildAt(sourceSquareIndexRook) as? ImageView
                            val src_ImgResource = src_sqrook?.drawable

                            // Clear the image resource from the source position
                            src_sqrook?.setImageResource(0)

                            // Get the ImageView at the target position and clear its image resource
                            val target_sqrook =
                                chessboardLayout.getChildAt(targetSquareIndexRook) as? ImageView
                            target_sqrook?.setImageResource(0)

                            // Set the image resource of the target position to the source image
                            target_sqrook?.setImageDrawable(src_ImgResource)
                        }
                    }

                    //Increasing move_counter
                    viewModel.move_counter += 1
                    //viewModel.printChessBoard()
                    //viewModel.printRemainingBlackPieces()

                    if(previousSource != null){

                        val previoussource_index = previousSource!!.first * 8 + previousSource!!.second

                        val previousSquare = chessboardLayout.getChildAt(previoussource_index) as? ImageView

                        if((previousSource!!.first + previousSource!!.second) % 2 == 0){
                            previousSquare!!.setBackgroundColor(resources.getColor(R.color.white))
                        }else{
                            previousSquare!!.setBackgroundColor(resources.getColor(R.color.light_brown))
                        }
                    }
                    if(previousTarget != null){
                        val previoustarget_index = previousTarget!!.first * 8 + previousTarget!!.second

                        val previousTargetSquare = chessboardLayout.getChildAt(previoustarget_index) as? ImageView

                        if((previousTarget!!.first + previousTarget!!.second) % 2 == 0){
                            previousTargetSquare!!.setBackgroundColor(resources.getColor(R.color.white))
                        }else{
                            previousTargetSquare!!.setBackgroundColor(resources.getColor(R.color.light_brown))
                        }
                    }


                    previousSource = soure_valid_move
                    previousTarget = target_valid_move
//                        // Update previous source and target squares
//                        previousSourceSquare = sourceSquare
//                        previousTargetSquare = targetSquare
                }
            }
        }


        viewModel.isPawnPromotion.observe(viewLifecycleOwner) { isPawnPromotion ->
            if (isPawnPromotion) {
                soure_valid_move?.let { source ->
                    target_valid_move?.let { target ->
                        // Update the chessboard display
                        // Get the source and target positions
                        val sourceSquareIndex = source.first * 8 + source.second
                        val targetSquareIndex = target.first * 8 + target.second

                        // Get the ImageView at the source position and clear its image resource
                        val sourceSquare =
                            chessboardLayout.getChildAt(sourceSquareIndex) as? ImageView
                        sourceSquare?.setImageResource(0)

                        // Get the ImageView at the target position and clear its image resource
                        val targetSquare =
                            chessboardLayout.getChildAt(targetSquareIndex) as? ImageView
                        targetSquare?.setImageResource(0)

                        sourceSquare?.setBackgroundColor(Color.LTGRAY) // Set background color
                        targetSquare?.setBackgroundColor(Color.LTGRAY) // Set background color

                        // Display a white piece
                        if (target.first == 0) {
                            val new_resourceID = when (target.second) {
                                0, 7 -> R.drawable.white_rook
                                1, 6 -> R.drawable.white_knight
                                2, 5 -> R.drawable.white_bishop
                                else -> R.drawable.white_queen
                            }
                            // Set the image resource of the target position to the promoting piece
                            targetSquare?.setImageResource(new_resourceID)
                        }
                        // Display a black piece
                        else {
                            val new_resourceID = when (target.second) {
                                0, 7 -> R.drawable.black_rook
                                1, 6 -> R.drawable.black_knight
                                2, 5 -> R.drawable.black_bishop
                                else -> R.drawable.black_queen
                            }
                            // Set the image resource of the target position to the promoting piece
                            targetSquare?.setImageResource(new_resourceID)
                        }
                    }

                    //Increasing move_counter
                    viewModel.move_counter += 1

                    if(previousSource != null){

                        val previoussource_index = previousSource!!.first * 8 + previousSource!!.second

                        val previousSquare = chessboardLayout.getChildAt(previoussource_index) as? ImageView

                        if((previousSource!!.first + previousSource!!.second) % 2 == 0){
                            previousSquare!!.setBackgroundColor(resources.getColor(R.color.white))
                        }else{
                            previousSquare!!.setBackgroundColor(resources.getColor(R.color.light_brown))
                        }
                    }
                    if(previousTarget != null){
                        val previoustarget_index = previousTarget!!.first * 8 + previousTarget!!.second

                        val previousTargetSquare = chessboardLayout.getChildAt(previoustarget_index) as? ImageView

                        if((previousTarget!!.first + previousTarget!!.second) % 2 == 0){
                            previousTargetSquare!!.setBackgroundColor(resources.getColor(R.color.white))
                        }else{
                            previousTargetSquare!!.setBackgroundColor(resources.getColor(R.color.light_brown))
                        }
                    }


                    previousSource = soure_valid_move
                    previousTarget = target_valid_move
//                        // Update previous source and target squares
//                        previousSourceSquare = sourceSquare
//                        previousTargetSquare = targetSquare
                }
            }
        }

        viewModel.enPassantFlag.observe(viewLifecycleOwner) { isEnPassant ->
            if (isEnPassant) {
                soure_valid_move?.let { source ->
                    target_valid_move?.let { target ->

                        // Get the source and target positions. Also calculate the capture position
                        val sourceSquareIndex = source.first * 8 + source.second
                        val targetSquareIndex = target.first * 8 + target.second

                        //Coordinate of the Capture Position(same row as source_square as same column as target_square)
                        val capturePosition = Pair(source.first, target.second)
                        val captureSquareIndex = capturePosition.first * 8 + capturePosition.second

                        val sourceSquare =
                            chessboardLayout.getChildAt(sourceSquareIndex) as? ImageView

                        //Save the Image of the moving pawn as the sourceImageResource
                        val sourceImageResource =
                            sourceSquare?.drawable // Get the drawable from the source position

                        // Clear Source Image
                        sourceSquare?.setImageResource(0)

                        // Get the ImageView at the source position and clear its image resource
                        val captureSquare =
                            chessboardLayout.getChildAt(captureSquareIndex) as? ImageView
                        captureSquare?.setImageResource(0)


                        // Get the ImageView at the target position and clear its image resource
                        val targetSquare =
                            chessboardLayout.getChildAt(targetSquareIndex) as? ImageView
                        targetSquare?.setImageResource(0)

                        // Set the image resource of the target position to the source image
                        targetSquare?.setImageDrawable(sourceImageResource)

                        sourceSquare?.setBackgroundColor(Color.LTGRAY) // Set background color
                        targetSquare?.setBackgroundColor(Color.LTGRAY) // Set background color

                        //Increasing move_counter
                        viewModel.move_counter += 1
                        //viewModel.printChessBoard()
                        //viewModel.printRemainingWhitePieces()
                        //viewModel.printRemainingBlackPieces()


                        if(previousSource != null){

                            val previoussource_index = previousSource!!.first * 8 + previousSource!!.second

                            val previousSquare = chessboardLayout.getChildAt(previoussource_index) as? ImageView

                            if((previousSource!!.first + previousSource!!.second) % 2 == 0){
                                previousSquare!!.setBackgroundColor(resources.getColor(R.color.white))
                            }else{
                                previousSquare!!.setBackgroundColor(resources.getColor(R.color.light_brown))
                            }
                        }
                        if(previousTarget != null){
                            val previoustarget_index = previousTarget!!.first * 8 + previousTarget!!.second

                            val previousTargetSquare = chessboardLayout.getChildAt(previoustarget_index) as? ImageView

                            if((previousTarget!!.first + previousTarget!!.second) % 2 == 0){
                                previousTargetSquare!!.setBackgroundColor(resources.getColor(R.color.white))
                            }else{
                                previousTargetSquare!!.setBackgroundColor(resources.getColor(R.color.light_brown))
                            }
                        }


                        previousSource = soure_valid_move
                        previousTarget = target_valid_move
//                        // Update previous source and target squares
//                        previousSourceSquare = sourceSquare
//                        previousTargetSquare = targetSquare

                    }
                }
            }
        }

        viewModel.checkmate.observe(viewLifecycleOwner) { isCheckmate ->
            if (isCheckmate) {
                // Perform actions when checkmate occurs
                // For example, show a dialog, end the game, etc.
                // You can replace the toast with your desired action
                binding.gameOverText.text = "CheckMate, Game Over"
                binding.gameOverText.setTextColor(Color.RED)
            }
        }

        viewModel.stalemate.observe(viewLifecycleOwner) { isStalemate ->
            if (isStalemate) {
                // Perform actions when Stalemate occurs
                // For example, show a dialog, end the game, etc.
                // You can replace the toast with your desired action
                binding.gameOverText.text = "StaleMate, Game Over"
                binding.gameOverText.setTextColor(Color.RED)
            }
        }


        binding.newGameButton.setOnClickListener {

            for (row in 0 until 8) {
                for (column in 0 until 8) {

                    val cur_square_index = row * 8 + column

                    val cur_square = chessboardLayout.getChildAt(cur_square_index) as? ImageView

                    cur_square?.setImageResource(0)

                    val piece = startingPosition[row][column]
                    if (piece.isNotEmpty()) {
                        val resourceId = when (piece) {
                            "black_pawn" -> R.drawable.black_pawn
                            "black_rook" -> R.drawable.black_rook
                            "black_knight" -> R.drawable.black_knight
                            "black_bishop" -> R.drawable.black_bishop
                            "black_queen" -> R.drawable.black_queen
                            "black_king" -> R.drawable.black_king
                            "white_pawn" -> R.drawable.white_pawn
                            "white_rook" -> R.drawable.white_rook
                            "white_knight" -> R.drawable.white_knight
                            "white_bishop" -> R.drawable.white_bishop
                            "white_queen" -> R.drawable.white_queen
                            "white_king" -> R.drawable.white_king
                            else -> throw IllegalArgumentException("Invalid piece name: $piece")
                        }
                        cur_square?.setImageResource(resourceId)
                    }

                    if ((row + column) % 2 == 0) {
                        cur_square!!.setBackgroundColor(resources.getColor(R.color.white))
                    } else {
                        cur_square!!.setBackgroundColor(resources.getColor(R.color.light_brown))
                    }
                }
            }

            viewModel.resetGame()

            binding.gameOverText.text = ""

            //Reset the recyclerview
            // Clear the snapshots list
            moveSnapshots.clear()
            // Notify the adapter that all items have been removed
            moveHistoryAdapter.notifyDataSetChanged()
        }

        binding.onlinePlayButton.setOnClickListener {
            launchFirebaseLogin()

//            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        val user = FirebaseAuth.getInstance().currentUser
//                        Toast.makeText(requireContext(), "Welcome, ${user?.email}!", Toast.LENGTH_SHORT).show()
//                    } else {
//                        Toast.makeText(requireContext(), "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
//                    }
//                }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Avoid memory leaks
    }

    private fun launchFirebaseLogin() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        // Create and launch sign-in intent
        // XXX Write me. Set authentication providers and start sign-in for user
        signInLauncher.launch(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build()
            )
            // setIsSmartLockEnabled(false) solves some problems

            //DISABLE EMAIL ENUMERATION, (To show password option when trying to login with existing user) in Firebase Console
            //***//https://github.com/firebase/firebaseui-web/issues/1040***
    }




    private fun handleSquareClick(row: Int, column: Int) {
        if (sourcePosition == null) {
            // First click, record the source position

            //First Check if click is clicking on actual piece
            if(viewModel.chessBoard[row][column] != null){
                val current_clicked_piece = viewModel.chessBoard[row][column]

                //if White Player's turn and clicking on white piece then set source position
                if(viewModel.white_turn && current_clicked_piece!!.color=="white"){
                    sourcePosition = Pair(row, column)
                }
                //if Black Player's turn and clicking on black piece then set source position
                else if(!viewModel.white_turn && current_clicked_piece!!.color=="black"){
                    sourcePosition = Pair(row, column)
                }
                //Else you clicked the wrong color piece so keep source position = null
                else{
                    val message = if (viewModel.white_turn) "White to Move" else "Black to Move"
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    sourcePosition = null
                }
            }
            //else you clicked on a empty square so source position remains null, nothing happens
        }
        else {
            // Second click, move the piece from sourcePosition to (row, column)
            soure_valid_move = sourcePosition
            target_valid_move = Pair(row,column)
            viewModel.movePiece(sourcePosition!!, Pair(row, column))
            // Reset the source position after the move

//            soure_valid_move = sourcePosition
//            target_valid_move = Pair(row,column)

            sourcePosition = null
        }
    }

    fun captureBoardSnapshot(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    fun onMoveMade() {
        val snapshot = captureBoardSnapshot(binding.chessboard)
        moveSnapshots.add(snapshot)
        moveHistoryAdapter.notifyItemInserted(moveSnapshots.size - 1)
    }
}