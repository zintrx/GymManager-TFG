package com.gymmanager.android.activity

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.gymmanager.android.R
import java.util.UUID

class QrAccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_access)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val ivQrCode = findViewById<ImageView>(R.id.ivQrCode)

        btnBack.setOnClickListener {
            finish()
        }

        val prefs = getSharedPreferences("gymmanager_prefs", MODE_PRIVATE)
        val userId = prefs.getLong("userId", -1)
        val username = prefs.getString("username", "user")

        // Generate a dynamic string for the QR code
        val accessString = "GYM_${userId}_${username}_${System.currentTimeMillis()}"
        
        // Generate QR Code bitmap
        val bitmap = generateQRCode(accessString)
        if (bitmap != null) {
            ivQrCode.setImageBitmap(bitmap)
        }
    }

    private fun generateQRCode(text: String): Bitmap? {
        val size = 512 // pixels
        return try {
            val bitMatrix = MultiFormatWriter().encode(
                text,
                BarcodeFormat.QR_CODE,
                size,
                size
            )
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
