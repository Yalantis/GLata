package com.yalantis.glatademo.advanced

import android.opengl.GLSurfaceView
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.yalantis.glata.core.FpsStabilizer
import com.yalantis.glata.core.texture.ITexture
import com.yalantis.glata.primitive.Color
import com.yalantis.glata.texture.Texture
import com.yalantis.glata.util.initSurface
import com.yalantis.glatademo.R
import kotlinx.android.synthetic.main.activity_advanced.*
import kotlinx.android.synthetic.main.item_card.view.*

class AdvancedAnimationActivity : AppCompatActivity() {

    private lateinit var cards: Array<CardModel>
    private var scenes = Array<CardScene?>(4) { null }
    private lateinit var mainScene: AdvancedBackgroundScene

    private var fpsStabilizer: FpsStabilizer? = null

    private lateinit var textures: Array<ITexture>

    private var fullScrollPath = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advanced)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
        initTextures()
        initSurface()
        initCardModels()
        initCards()
        updateMeterCounter(0f)
    }

    private fun initTextures() {
        textures = arrayOf(
                Texture("adv_0", R.drawable.adv_0),
                Texture("adv_1", R.drawable.adv_1),
                Texture("adv_2", R.drawable.adv_2),
                Texture("adv_3", R.drawable.adv_3)
        )
    }

    private fun initSurface() {
        mainScene = AdvancedBackgroundScene(textures)
        glsvBackground.initSurface(this, mainScene, Color(0.5f, 0.5f, 0.5f, 1f))
        fpsStabilizer = FpsStabilizer(glsvBackground)
    }

    private fun initCard(index: Int, card: View, texture: ITexture) {
        val glsv = card.findViewById<GLSurfaceView>(R.id.glsvCard)
        val scene = CardScene(texture)
        glsv.initSurface(this, scene)
        scenes[index] = scene

        with (cards[index]) {
            card.tvDepth.text = getString(R.string.adv_depth, depthMeters)
            card.tvLevel.text = level
            card.tvLocation.text = locationName
            card.tvName.text = name
            card.tvDuration.text = divingTime
        }
    }

    private fun initCards() {
        initCard(0, card0, Texture("adv_0", R.drawable.adv_0))
        initCard(1, card1, Texture("adv_1", R.drawable.adv_1))
        initCard(2, card2, Texture("adv_2", R.drawable.adv_2))
        initCard(3, card3, Texture("adv_3", R.drawable.adv_3))

        setScrollPercent(0f)

        svCards.initItems()
        svCards.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            findFullScrollPath()
            val scrollPercent = scrollX.toFloat() / fullScrollPath
            setScrollPercent(scrollPercent)
            updateMeterCounter(scrollPercent)
        }
        mainScene.onSwipeListener = object : AdvancedBackgroundScene.OnSwipeListener {
            override fun onSwipe(newProgress: Float) {
                findFullScrollPath()
                svCards.scrollX = (newProgress * fullScrollPath).toInt()
                updateMeterCounter(newProgress)
            }

            override fun onEndSwipe() {
                svCards.snap()
            }
        }
    }

    private fun updateMeterCounter(progress: Float) {
        var meters = 10f + progress * 80f
        if (meters > 80f) {
            meters = (meters - 80f) * 300f
        }
        tvDepthAmount.text = meters.toInt().toString()
    }

    private fun findFullScrollPath() {
        if (fullScrollPath == 0) {
            fullScrollPath = card0.measuredWidth * (ITEMS_COUNT - 1)
        }
    }

    private fun setScrollPercent(scrollPercent: Float) {
        mainScene.updateScrollAnimation(scrollPercent)
        for (scene in scenes) {
            scene?.setScrollPercent(scrollPercent)
        }
    }

    private fun initCardModels() {
        cards = arrayOf(
            CardModel("Museo Subacuático de Arte (MUSA)",
                    "Mexico",
                    10,
                    "40-120 min",
                    "Middle"),
            CardModel("Orda Cave",
                    "Russia",
                    38,
                    "50-120 min",
                    "Hard"),
            CardModel("The Green Lake",
                    "Tragoess, Austria",
                    66,
                    "60-80 min",
                    "Beginner"),
            CardModel("Rl'yeh",
                    "Ocean Floor",
                    2349,
                    "One way",
                    "Extreme")
        )
    }

    override fun onPause() {
        fpsStabilizer?.onPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        fpsStabilizer?.onResume()
    }

    companion object {
        const val ITEMS_COUNT = 4
    }
}