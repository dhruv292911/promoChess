package com.example.promochess
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.promochess.databinding.FragmentOnlinePlayBinding


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OnlinePlayFragment: Fragment() {

    private var _binding: FragmentOnlinePlayBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var playersAdapter: PlayersAdapter
    private val onlinePlayers = mutableListOf<Player>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnlinePlayBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("online_players")

        setupRecyclerView()
        loadOnlinePlayers()
    }
    private fun setupRecyclerView() {
        playersAdapter = PlayersAdapter(onlinePlayers) { selectedPlayer ->
            //We need to implement startGamewithPlayer
            //startGameWithPlayer(selectedPlayer)
        }
    }
    private fun loadOnlinePlayers() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                onlinePlayers.clear()
                for (child in snapshot.children) {
                    val player = child.getValue(Player::class.java)
                    player?.let { onlinePlayers.add(it) }
                }
                playersAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Failed to load players", error.toException())
            }
        })
    }
}