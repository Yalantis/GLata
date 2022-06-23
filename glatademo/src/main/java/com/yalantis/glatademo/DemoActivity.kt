package com.yalantis.glatademo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yalantis.glata.core.FpsStabilizer
import com.yalantis.glata.util.initSurface
import com.yalantis.glatademo.scene.TestScene
import kotlinx.android.synthetic.main.activity_demo.*

class DemoActivity : AppCompatActivity() {

    private var fpsStabilizer: FpsStabilizer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)
        surfaceView.initSurface(this, TestScene())
        fpsStabilizer = FpsStabilizer(surfaceView)
    }

    override fun onPause() {
        fpsStabilizer?.onPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        fpsStabilizer?.onResume()
    }
}
