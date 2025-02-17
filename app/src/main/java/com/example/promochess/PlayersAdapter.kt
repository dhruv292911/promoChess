package com.example.promochess

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlayersAdapter(
    private val players: List<Player>,
    private val onPlayerClick: (Player) -> Unit
) : RecyclerView.Adapter<PlayersAdapter.PlayerViewHolder>() {

    inner class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val playerName: TextView = itemView.findViewById(R.id.playerName)

        fun bind(player: Player) {
            playerName.text = player.username
            itemView.setOnClickListener { onPlayerClick(player) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_player, parent, false)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(players[position])
    }

    override fun getItemCount(): Int = players.size
}