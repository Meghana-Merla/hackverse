package com.example.hackverse14

import Hackathon
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hackverse14.databinding.ItemHackathonBinding

class HackathonAdapter : RecyclerView.Adapter<HackathonAdapter.HackathonViewHolder>() {
    private val hackathonList = mutableListOf<Hackathon>()

    fun submitList(list: List<Hackathon>) {
        hackathonList.clear()
        hackathonList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HackathonViewHolder {
        val binding = ItemHackathonBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HackathonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HackathonViewHolder, position: Int) {
        holder.bind(hackathonList[position])
    }

    override fun getItemCount(): Int = hackathonList.size

    inner class HackathonViewHolder(private val binding: ItemHackathonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(hackathon: Hackathon) {
            binding.textViewTitle.text = hackathon.title
            binding.textViewDescription.text = hackathon.description
            binding.textViewDate.text = hackathon.date
            binding.textViewDeadline.text = hackathon.deadline
            binding.textViewPrizeMoney.text = hackathon.prizeMoney
            binding.textViewLocation.text = hackathon.location
            binding.textViewTime.text = hackathon.time
        }
    }
}

