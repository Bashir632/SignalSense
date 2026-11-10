package com.example.signalsense.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.R
import com.example.databinding.FragmentCollectionBinding
import com.example.signalsense.activities.ExportActivity
import com.example.signalsense.activities.HistoryActivity
import com.example.signalsense.activities.MainActivity

class CollectionFragment : Fragment() {

    private var _binding: FragmentCollectionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCollectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCreateSession.setOnClickListener {
            val sessionName = binding.etSessionName.text?.toString()?.trim()
            if (sessionName.isNullOrBlank()) {
                binding.tilSessionName.error = "Please enter a valid session name"
                return@setOnClickListener
            }
            binding.tilSessionName.error = null

            val interval = binding.etInterval.text?.toString()?.toIntOrNull() ?: 5
            Toast.makeText(
                context,
                "Initialized session '$sessionName' with ${interval}s sampling window",
                Toast.LENGTH_LONG
            ).show()

            // Navigate back to Dashboard to begin monitoring
            (activity as? MainActivity)?.switchTab(R.id.navigation_dashboard)
        }

        binding.btnGoHistory.setOnClickListener {
            startActivity(Intent(context, HistoryActivity::class.java))
        }

        binding.btnGoExport.setOnClickListener {
            startActivity(Intent(context, ExportActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
