package com.example.study_apt

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.study_apt.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.goToRepository.setOnClickListener {
//            startActivity(Intent(this, RepositoryActivity::class.java)
//                .putExtra(RepositoryActivityBuilder.REQUIRED_NAME, "Kotlin")
//                  .putExtra(RepositoryActivityBuilder.REQUIRED_OWNER, "JetBrains")
//                .putExtra(RepositoryActivityBuilder.OPTIONAL_CREATE_AT, 10086L)
//                .putExtra(RepositoryActivityBuilder.OPTIONAL_URL, "https://www.jetbrains.com.cn/"))

            // 调用build后生成的java start方法
//            RepositoryActivityBuilder.start(this, "Kotlin", "JetBrains", 10086L, "https://www.jetbrains.com.cn/")
            // 调用build后生成的kt扩展方法
            startRepositoryActivity("Kotlin", "JetBrains", url = "https://www.jetbrains.com.cn/")
        }

        binding.goToUserActivity.setOnClickListener {
            // 可以调用build后生成的start方法
            UserActivityBuilder.start(this, 28, "jillian", "Kotlin", "developer", "Shanghai")
        }
    }

}