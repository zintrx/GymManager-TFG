package com.gymmanager.android.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gymmanager.android.R
import com.gymmanager.android.model.Publicacion

class PostAdapter(private val posts: List<Publicacion>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAvatar: TextView = view.findViewById(R.id.tvAvatar)
        val tvAuthor: TextView = view.findViewById(R.id.tvPostAuthor)
        val tvDate: TextView = view.findViewById(R.id.tvPostDate)
        val tvContent: TextView = view.findViewById(R.id.tvPostContent)
        val tvLikes: TextView = view.findViewById(R.id.tvLikes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.tvAuthor.text = post.autor
        holder.tvContent.text = post.contenido
        holder.tvLikes.text = "${post.likes} Likes"
        holder.tvAvatar.text = post.autor.take(1).uppercase()
        
        // Simple date display
        holder.tvDate.text = post.fecha?.split("T")?.get(0) ?: "Recién"
    }

    override fun getItemCount() = posts.size
}
