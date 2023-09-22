package com.begumsaraunal.firebasebookapplication.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.begumsaraunal.firebasebookapplication.databinding.RecyclerRowBinding
import com.begumsaraunal.firebasebookapplication.model.Book
import com.bumptech.glide.Glide


fun interface ItemClickListener {
    fun onItemClick(position: Int)
}

class RecyclerAdapter(
    private val books: ArrayList<Book>,
    private val itemClickListener: ItemClickListener
) :
    RecyclerView.Adapter<RecyclerAdapter.BookHolder>() {
    class BookHolder(
        val binding:RecyclerRowBinding
        ) :
        RecyclerView.ViewHolder(binding.root)
    {
    }
    //recycler xml ile kod kısmını birbirine bağlar
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookHolder {
        //use inflater for connect xml and code
       // val inflater = LayoutInflater.from(parent.context)
      //  val view = inflater.inflate(R.layout.recycler_row, parent, false)

        val binding =  RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BookHolder(binding)
    }

    // how many book information's will be showing in page
    override fun getItemCount(): Int {
        return books.size
    }


    override fun onBindViewHolder(holder: BookHolder, position: Int)
    {
        with(holder)
        {
            with(books[position])
            {
                binding.recyclerRowUsername.text = username
                binding.recyclerRowBookName.text = bookName
                binding.recyclerRowBookAuthor.text = bookAuthor
                binding.recyclerRowBookContext.text = bookContent

                Glide.with(holder.itemView.context).load(bookPhotoUrl)
                    .into(binding.recyclerRowImageView)

                holder.itemView.setOnClickListener {
                    itemClickListener.onItemClick(position) // Tıklama olayını işle
            }
        }

        }



        }
    }
