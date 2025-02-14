package com.bizup

import android.graphics.Paint
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        val button = findViewById<Button>(R.id.home);
        button.paintFlags = button.paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }
}