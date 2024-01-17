package com.nabil.submission1_appstory.Component

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton

class SignUpButton : AppCompatButton {

    private var txtColor: Int = 0

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        setTextColor(txtColor)
        background = null
        textSize = 14f
        gravity = Gravity.CENTER
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val input = s.toString().trim()
                if (input.length < 8) {
                    error = "Input minimal 8 karakter"

                } else {
                    error = null
                }

            }

            override fun afterTextChanged(s: Editable) {
                // Do nothing.
            }
        })
    }
}