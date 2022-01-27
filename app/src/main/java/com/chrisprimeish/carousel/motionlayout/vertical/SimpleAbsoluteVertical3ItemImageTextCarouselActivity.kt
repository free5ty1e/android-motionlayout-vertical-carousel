package com.chrisprimeish.carousel.motionlayout.vertical

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.helper.widget.Carousel
import androidx.constraintlayout.motion.widget.MotionLayout
import com.google.android.material.card.MaterialCardView
import com.saurabharora.motionlayout.playground.R

class SimpleAbsoluteVertical3ItemImageTextCarouselActivity : AppCompatActivity() {

    var motionLayout: MotionLayout? = null

    var colors = intArrayOf(
        Color.BLUE,
        Color.RED,
        Color.GREEN,
        Color.MAGENTA,
        Color.CYAN,
        Color.YELLOW,
        Color.parseColor("#DDEDED"),
        Color.parseColor("#00ABAB"),
        Color.BLACK,
        Color.parseColor("#9932CC"),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_vertical_absolute_carousel)

        motionLayout = findViewById(R.id.carousel_motion_layout)

        setupCarousel()
    }

    ////////////////////////////////////////////////////////////////
    // Setup the Carousel adapter
    ////////////////////////////////////////////////////////////////
    private fun setupCarousel() {
        val carousel = findViewById<Carousel>(R.id.carousel)


        carousel.setAdapter(object : Carousel.Adapter {
            override fun count(): Int {
                return 10       //Demonstrating the ability to utilize the carousel to show more items than are visible on the screen
            }

            @SuppressLint("ClickableViewAccessibility") //See https://stackoverflow.com/a/50345118/17339114 - Needed to allow passing through of cardView onTouch to the carousel itself to allow scrolling from a button anchor
            override fun populate(view: View, index: Int) {
                val cardView = view as MaterialCardView
                val textView = cardView.findViewById<AppCompatTextView>(R.id.carousel_item_text)
                val imageView = cardView.findViewById<AppCompatImageView>(R.id.carousel_item_image)
                imageView.setColorFilter(colors[index])
                textView.text = "Item $index"
                
                cardView.setOnTouchListener { _, event ->
                    motionLayout!!.onTouchEvent(event)
                    false
                }
                cardView.setOnClickListener {
                    Log.d("Carousel", "cardView.onClickListener(textView.text = ${textView.text}, currentIndexWas = ${carousel.currentIndex}, tappedIndex = $index, carousel.count = ${carousel.count}")

                    //if currentIndex is at the opposite end of the carousel from the tapped index, need to jump instead of transition to avoid
                    // scrolling up through the indices
                    // (tapping bottom item when currentItem = 0 and tapped bottom item = 9)
                    // (or tapping top item when currentItem = 9 and tapped top item = 0)
                    val lastIndex = carousel.count - 1
                    if (carousel.currentIndex == 0 && index == lastIndex) {
                        Log.d("Carousel", "cardView.onClickListener(textView.text = ${textView.text}): Tapped bottom item when front was first and bottom is last!")
                        //Tapped bottom item when front was first and bottom is last

                        //TODO: Figure out how to smoothly transition without screwing up the current item for the next scrolls / taps, for now just jump
//                        motionLayout?.transitionToState(R.id.previous)
                        carousel.jumpToIndex(index)

                    } else if (carousel.currentIndex == lastIndex && index == 0) {
                        Log.d("Carousel", "cardView.onClickListener(textView.text = ${textView.text}): Tapped top item when front was last and bottom is first!")
                        //Tapped top item when front was last and top is first

                        //TODO: Figure out how to smoothly transition without screwing up the current item for the next scrolls / taps, for now just jump
//                        motionLayout?.transitionToState(R.id.next)
                        carousel.jumpToIndex(index)

                    } else if (carousel.currentIndex != index) {
                        Log.d("Carousel", "cardView.onClickListener(textView.text = ${textView.text}): Tapped index not the same as current index, transitioning!")
                        //Only if not the same as current index
                        carousel.transitionToIndex(index, 300)
                    }
                }
            }

            override fun onNewItem(index: Int) {
            }
        })
    }
}