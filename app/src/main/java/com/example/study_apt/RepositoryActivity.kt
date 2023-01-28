package com.example.study_apt

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.apt.annotations.Builder
import com.example.apt.annotations.Optional
import com.example.apt.annotations.Required
import com.example.study_apt.databinding.ActivityRepositoryBinding

/**
 * @Description:
 * @author zouji
 * @date 2023/1/28
 */
@Builder
class RepositoryActivity : AppCompatActivity() {
    @Required
    lateinit var name: String

    @Required
    lateinit var owner: String

    @Optional(stringValue = "")
    lateinit var url: String

    @Optional
    var createAt: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRepositoryBinding.inflate(layoutInflater)
        setContentView(binding. root)
        binding.nameView.text = name
        binding.ownerView.text = owner
        binding.urlView.text = url
        binding.createAtView.text = createAt.toString()
    }
}