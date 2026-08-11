package com.warburton.wfreunion

import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.warburton.wfreunion.api.ApiClient
import com.warburton.wfreunion.api.Photo
import com.warburton.wfreunion.databinding.FragmentPhotoGalleryBinding
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class PhotoGalleryFragment : Fragment() {

    private var _binding: FragmentPhotoGalleryBinding? = null
    private val binding get() = _binding!!

    private val photos = mutableListOf<Photo>()
    private lateinit var adapter: PhotoAdapter
    private lateinit var prefs: SharedPreferences

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { uploadPhoto(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = requireActivity().getSharedPreferences("wf_prefs", AppCompatActivity.MODE_PRIVATE)

        adapter = PhotoAdapter(photos)
        binding.rvPhotos.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvPhotos.adapter = adapter

        binding.btnUploadPhoto.setOnClickListener { pickImage.launch("image/*") }

        loadPhotos()
    }

    private fun loadPhotos() {
        val token = prefs.getString("wf_token", null) ?: return
        lifecycleScope.launch {
            try {
                val response = ApiClient.service.getPhotos("Bearer $token")
                if (response.isSuccessful) {
                    val list = response.body()?.photos ?: emptyList()
                    photos.clear()
                    photos.addAll(list)
                    adapter.notifyDataSetChanged()
                    binding.tvEmptyState.visibility = if (photos.isEmpty()) View.VISIBLE else View.GONE
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), getString(R.string.toast_load_fail), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadPhoto(uri: Uri) {
        val token = prefs.getString("wf_token", null) ?: return
        val contentResolver = requireContext().contentResolver
        val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
        val bytes = contentResolver.openInputStream(uri)?.readBytes() ?: return
        val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("photoFile", "upload.jpg", requestBody)

        lifecycleScope.launch {
            try {
                val response = ApiClient.service.uploadPhoto("Bearer $token", part)
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), getString(R.string.toast_upload_success), Toast.LENGTH_SHORT).show()
                    loadPhotos()
                } else {
                    Toast.makeText(requireContext(), getString(R.string.toast_upload_fail), Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), getString(R.string.error_network), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class PhotoAdapter(private val photos: List<Photo>) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.iv_photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        Glide.with(holder.imageView.context)
            .load(photos[position].url)
            .centerCrop()
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = photos.size
}
