package com.example.promochess

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class MoveHistoryAdapter(private val moveSnapshots: List<Bitmap>) :
    RecyclerView.Adapter<MoveHistoryAdapter.MoveViewHolder>() {

    class MoveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val moveImage: ImageView = itemView.findViewById(R.id.moveSnapshotImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoveViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_move_snapshot, parent, false)
        return MoveViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoveViewHolder, position: Int) {
        holder.moveImage.setImageBitmap(moveSnapshots[position])
    }

    override fun getItemCount(): Int = moveSnapshots.size
}