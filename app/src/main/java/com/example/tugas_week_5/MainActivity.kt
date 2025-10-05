package com.example.tugas_week_5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.tugas_week_5.api.CatApiService
import com.example.tugas_week_5.model.ImageData
import retrofit2.*
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var apiResponseView: TextView
    private lateinit var catImageView: ImageView
    private lateinit var nextButton: Button

    private val retrofit by lazy {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        Retrofit.Builder()
            .baseUrl("https://api.thecatapi.com/v1/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    private val catApiService by lazy {
        retrofit.create(CatApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        apiResponseView = findViewById(R.id.api_response)
        catImageView = findViewById(R.id.cat_image)
        nextButton = findViewById(R.id.btn_next)

        // ✅ Load pertama kali
        getCatImageResponse()

        // ✅ Klik tombol untuk kucing baru
        nextButton.setOnClickListener {
            getCatImageResponse()
        }
    }

    private fun getCatImageResponse() {
        apiResponseView.text = "Loading new cat..."
        val call = catApiService.searchImages(1, "full")

        call.enqueue(object : Callback<List<ImageData>> {
            override fun onFailure(call: Call<List<ImageData>>, t: Throwable) {
                Log.e(MAIN_ACTIVITY, "Failed to get response", t)
                apiResponseView.text = "Error: ${t.message}"
            }

            override fun onResponse(call: Call<List<ImageData>>, response: Response<List<ImageData>>) {
                if (response.isSuccessful) {
                    val imageList = response.body()
                    val firstImage = imageList?.firstOrNull()

                    val firstImageUrl = firstImage?.imageUrl ?: "No URL found"
                    val breedName = firstImage?.breeds?.firstOrNull()?.name ?: "Unknown Breed"

                    apiResponseView.text = "Breed: $breedName"

                    Glide.with(this@MainActivity)
                        .load(firstImageUrl)
                        .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                        .error(android.R.drawable.stat_notify_error)
                        .into(catImageView)

                } else {
                    Log.e(MAIN_ACTIVITY, "Error: ${response.errorBody()?.string()}")
                    apiResponseView.text = "Response Error"
                }
            }
        })
    }

    companion object {
        const val MAIN_ACTIVITY = "MAIN_ACTIVITY"
    }
}
