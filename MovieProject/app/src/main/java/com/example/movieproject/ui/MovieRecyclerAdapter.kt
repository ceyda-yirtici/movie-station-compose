package com.example.movieproject.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.movieproject.R
import com.example.movieproject.model.MovieDetail
import com.example.movieproject.utils.BundleKeys
import java.util.Locale


class MovieRecyclerAdapter : RecyclerView.Adapter<MovieRecyclerAdapter.MovieViewHolder>() {

    private var movieList: MutableList<MovieDetail> = mutableListOf()
    companion object { var genreMapper : HashMap<Int, String> = HashMap()
        fun sendGenreList(it: HashMap<Int, String>) {
            genreMapper = it
        }
    }
    private var likedMovieIds: List<Int> = emptyList()
    private val handler = Handler(Looper.getMainLooper())
    private var onBottomReachedListener: OnBottomReachedListener? = null
    private lateinit var listener: OnClickListener
    private var viewMovieType: Int = 1

    interface OnClickListener {
        fun onMovieClick(position: Int,  movieView: View, movieList: MutableList<MovieDetail> )
        fun onHeartButtonClick(
            adapterPosition: Int,
            movieView: View,
            movieList: MutableList<MovieDetail>,
            heartButton: ImageButton
        )
    }
    fun setOnClickListener(listener: OnClickListener){
        this.listener = listener
    }
    interface OnBottomReachedListener {
        fun onBottomReached(position: Int)
    }

    fun setOnBottomReachedListener(onBottomReachedListener: OnBottomReachedListener) {
        this.onBottomReachedListener = onBottomReachedListener
    }
    fun updateFavList(item: MutableList<MovieDetail>) {
        handler.post {
            item.forEach { movieDetail ->
                movieDetail.heart_tag = "filled"
            }
            movieList = item
            notifyDataSetChanged()
        }
    }
    fun updateViewType(viewType: Int){
        viewMovieType = viewType
    }

    fun updateMovieList(item: MutableList<MovieDetail>) {
        handler.post {
            item.let {
                movieList = updateLikedStatusInMovieList(item)
            }
            notifyDataSetChanged()
        }
    }

    fun setLikedMovieIds(movies: List<Int>) {
        likedMovieIds = movies
        notifyDataSetChanged()
    }
    private fun updateLikedStatusInMovieList(item: MutableList<MovieDetail>): MutableList<MovieDetail> {
        for (movie in item) {
            movie.heart_tag = if (likedMovieIds.contains(movie.id)) "filled" else "outline"
        }
        return item
    }

    inner class MovieViewHolder(itemView: View, listener: OnClickListener, viewMovieType: Int) : ViewHolder(itemView) {

        private var  heartButton: ImageButton

        init {

            if (viewMovieType == 1)
                heartButton = itemView.findViewById(R.id.heart_in_detail)
            else
                heartButton = itemView.findViewById(R.id.heart_in_grid)
            if(viewMovieType == 3) heartButton.visibility = View.GONE




            itemView.setOnClickListener {
                listener.onMovieClick(adapterPosition,itemView, movieList)
            }

            heartButton.setOnClickListener {
                val position = adapterPosition
                listener.onHeartButtonClick(position, itemView, movieList, heartButton)
            }

        }
        @SuppressLint("SetTextI18n")
        fun bind(detail: MovieDetail) {
            val heartResource: Int
            if(viewMovieType == 1) {
                val movie: TextView = itemView.findViewById(R.id.movie)
                val photo: ImageView = itemView.findViewById(R.id.photo)
                val movie_desc: TextView = itemView.findViewById(R.id.movie_description)
                heartResource= R.drawable.heart_shape_outlined
                val date: TextView = itemView.findViewById(R.id.release_date)
                val vote: TextView = itemView.findViewById(R.id.vote_text)

                Glide.with(photo).load(BundleKeys.baseImageUrl + detail.poster_path)
                    .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(30)))
                    .placeholder(R.drawable.baseline_photo_24) // drawable as a placeholder
                    .error(R.drawable.baseline_photo_24) //  drawable if an error occurs
                    .into(photo)
                movie.text = detail.title
                movie_desc.text = detail.overview
                if (detail.release_date.isNotEmpty())
                    date.text = detail.release_date.subSequence(0, 4)
                else date.text = "invalid"
                val formattedVote = String.format(Locale.US, "%.1f", detail.vote)
                vote.text = formattedVote
            }
            else {
                heartResource= R.drawable.heart_shape_grey
                val movie: TextView = itemView.findViewById(R.id.title_grid)
                val photo: ImageView = itemView.findViewById(R.id.photoGrid)

                Glide.with(photo).load(BundleKeys.baseImageUrl + detail.poster_path)
                    .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(30)))
                    .placeholder(R.drawable.baseline_photo_24) // drawable as a placeholder
                    .error(R.drawable.baseline_photo_24) //  drawable if an error occurs
                    .into(photo)
                movie.text = detail.title
            }

            if (detail.heart_tag == "filled") {
                // Movie is liked, update the UI accordingly
                heartButton.setImageResource(R.drawable.heart_shape_red)
            } else {
                // Movie is not liked, update the UI accordingly
                heartButton.setImageResource(heartResource)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if(viewMovieType == 1) {
            val itemView = inflater.inflate(R.layout.item_movie, parent, false)
            MovieViewHolder(itemView, listener, viewMovieType)
        } else{
            val itemView = inflater.inflate(R.layout.item_movie_grid, parent, false)
            MovieViewHolder(itemView, listener, viewMovieType)
        }
    }

    override fun getItemCount() = movieList.size

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        try {
            holder.bind(movieList[position])

            if (movieList.isNotEmpty() && position == movieList.size - 1) {
                onBottomReachedListener?.onBottomReached(position)
            }
            if (viewMovieType == 1) {
                val genreNamesOfTheMovies = arrayListOf<String>()

                if(movieList[position].genres.isNullOrEmpty())
                    for (genreId in movieList[position].genre_ids) {

                        val genreName = genreMapper[genreId]
                        if (genreName != null) {
                            genreNamesOfTheMovies.add(genreName)
                        }
                    }
                else movieList[position].genres?.let { genreNamesOfTheMovies.addAll(it.map { it.genre_name }) }
                decideAddingGenreView(holder, genreNamesOfTheMovies)
            }
        }
        catch (e: NullPointerException) {
            // Handle the exception
            Log.e("NullPointerException", "Caught: ${e.message}")
        }


    }
    private fun convertPixelsToDp(px: Int, context: Context): Int {
        return (px / context.resources.displayMetrics.density).toInt()
    }
    private fun decideAddingGenreView(holder: MovieViewHolder, genre_names: ArrayList<String>){

        val genreContainer: LinearLayout = holder.itemView.findViewById(R.id.genreContainer)
        // Clear any previous genres before adding new ones
        genreContainer.removeAllViews()

        // Get the width of the views
        val photoImageView: ImageView = holder.itemView.findViewById(R.id.photo)
        val photoWidth = photoImageView.layoutParams.width
        val screenWidthInDp = convertPixelsToDp(Resources.getSystem().displayMetrics.widthPixels, holder.itemView.context)
        val availableWidthInDp = screenWidthInDp - convertPixelsToDp(photoWidth, holder.itemView.context)

        // Add each genre to the LinearLayout as separate rounded corner boxes
        var totalWidthInDp = 0
        for (genreName in genre_names) {
            Log.d("GenreAdapter", "Genre Name: $genreName")
            val genreView = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.item_genre, genreContainer, false)
            val genreTextView = genreView.findViewById<TextView>(R.id.genreTextView)
            genreTextView.text = genreName

            // Measure the genreView width and add it to the totalWidth
            genreView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            totalWidthInDp += convertPixelsToDp(genreView.measuredWidth, holder.itemView.context)
            totalWidthInDp += convertPixelsToDp(photoImageView.marginEnd +
                    photoImageView.marginBottom +
                    genreContainer.marginEnd , holder.itemView.context)
            // Check if the totalWidth exceeds the availableWidth
            if (totalWidthInDp > availableWidthInDp) {
                break
            }
            // Add the genre item to the actual genreContainer
            genreContainer.addView(genreView)
        }
    }


}