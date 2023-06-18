package com.inayatulmaula.linear

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.inayatulmaula.myapplicationlinear.R
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class MainActivity : AppCompatActivity() {
    private lateinit var editText : EditText
    private lateinit var button :Button
    private lateinit var textView : TextView
    lateinit var interpreter : Interpreter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.inputX)
        button = findViewById(R.id.predictbtn)
        textView = findViewById(R.id.outputY)

        try {
            interpreter = Interpreter(loadModelFile(), null)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        button.setOnClickListener(View.OnClickListener {
            val f = doInference(editText.getText().toString())
            textView.setText("Result - $f")
        })
    }

    @Throws(IOException::class)
    private fun loadModelFile(): MappedByteBuffer {
        val assetFileDescriptor = this.assets.openFd("linear.tflite")
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val len = assetFileDescriptor.length
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, len)
    }

    fun doInference(`val`: String): Float {
        val input = FloatArray(1)
        input[0] = `val`.toFloat()
        val output = Array(1) {
            FloatArray(
                1
            )
        }
        interpreter!!.run(input, output)
        return output[0][0]
    }
}