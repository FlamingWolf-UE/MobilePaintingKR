package com.example.paintingapplab3.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.graphics.scale
import androidx.recyclerview.widget.RecyclerView
import com.example.paintingapplab3.createNewPictureActivity
import com.example.paintingapplab3.databinding.NoteItemBinding
import com.example.paintingapplab3.db.model.PictureEntity
import com.example.paintingapplab3.viewModel.HomePageViewModel

class PicturesRecyclerViewAdapter(var viewModel : HomePageViewModel) : RecyclerView.Adapter<PicturesRecyclerViewAdapter.NoteHolder>() {
    private var pictures : List<PictureEntity> = mutableListOf()

    class NoteHolder(var binding: NoteItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        val binding = NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {

        holder.binding.textViewTitle.text = pictures[position].name
        holder.binding.textViewSubtitle.text = pictures[position].description
        val options = BitmapFactory.Options()
        options.inSampleSize = 16

        holder.binding.imageViewRound.setImageBitmap(BitmapFactory.decodeByteArray(pictures[position].data, 0, pictures[position].data.size, options))

        holder.binding.deleteBtn.setOnClickListener {
            viewModel.deleteHero(getPictureAt(position))
        }


        holder.binding.editBtn.setOnClickListener{
            val intent = Intent(holder.itemView.context, createNewPictureActivity::class.java)

            intent.putExtra("id",getPictureAt(position).id)
            intent.putExtra("title",getPictureAt(position).name)
            intent.putExtra("description",getPictureAt(position).description)
            holder.itemView.context.startActivity(intent)
        }

    }



    @SuppressLint("NotifyDataSetChanged")
    public fun setHeroes(pictures: List<PictureEntity>)
    {
        this.pictures = pictures
        notifyDataSetChanged()
    }

    fun getPictureAt(position: Int) : PictureEntity
    {
        return pictures[position]
    }


    override fun getItemCount(): Int {
        return pictures.size
    }

}