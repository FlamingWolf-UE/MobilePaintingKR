package com.example.paintingapplab3

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.annotation.Nullable
import androidx.lifecycle.ViewModelProvider
import com.example.paintingapplab3.model.FloodFillModel
import com.example.paintingapplab3.viewModel.MainActivityViewModel
import kotlin.math.abs

enum class PaintViewMode {
    DRAW, ERASE, PAN_AND_ZOOM, IMAGE_CAPTURING, FLOODFILL
}

class PaintView(context: Context, @Nullable attrs : AttributeSet) : View(context, attrs) {

    private var prevChoosedMode : PaintViewMode = PaintViewMode.DRAW
    private var mode : PaintViewMode = PaintViewMode.DRAW
    private var btmView : Bitmap?
    private var paint : Paint = Paint()
    private var path : Path = Path()
    private var brushSize : Float = 12f
    private var eraserSize : Float = 12f
    private var mX : Float = 0f
    private var mY : Float = 0f
    private lateinit var canvas : Canvas
    private val _differenceSpace : Int = 4
    private var selectedColor : Int? = null
    private var eraserColor : Int = Color.WHITE
    private var scaleGestureDetector: ScaleGestureDetector
    private var gestureDetector: GestureDetector
    private var imageScaleGestureDetector : ScaleGestureDetector
    private var canvasWidth = 2048*16/9
    private var canvasHeight = 2048

    private var canvasScaleFactor = 1.0f

    private var translateX = 0.0f
    private var translateY = 0.0f

    private var imageScaleFactor : Float = 1f
    private var imageX: Float = 0f
    private var imageY: Float = 0f

    private var imageDrawable: BitmapDrawable? = null
    private var imageMovingMode : Boolean = false

    private var moving = false
    private var refX : Float? = null
    private var refY : Float?= null



    init {
        btmView = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888)
        btmView!!.eraseColor(Color.WHITE)
        paint.color = Color.BLACK
        paint.isDither = true
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeWidth = brushSize
        scaleGestureDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                canvasScaleFactor *= detector.scaleFactor
                canvasScaleFactor = canvasScaleFactor.coerceIn(0.2f, 10.0f)
                return true
            }
        })
        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                translateX -= distanceX
                translateY -= distanceY
                invalidate()
                return true
            }
        })

        imageScaleGestureDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener(){
            private var lastImageScaleFactor = 1.0f
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                if (imageMovingMode && imageDrawable != null) {
                    val scaleFactor = detector.scaleFactor.coerceIn(0.2f, 10.0f)
                    imageScaleFactor = lastImageScaleFactor * scaleFactor
                    imageDrawable!!.setBounds(
                        0,
                        0,
                        (imageDrawable!!.intrinsicWidth * imageScaleFactor).toInt(),
                        (imageDrawable!!.intrinsicHeight * imageScaleFactor).toInt()
                    )
                    lastImageScaleFactor = imageScaleFactor
                    invalidate()
                }
                return true
            }
        })
    }



    fun getBtmView() : Bitmap
    {
        return btmView!!
    }

    fun setBtmView(bitmap : Bitmap)
    {
        btmView!!.recycle()
        btmView = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        invalidate()
    }



    private fun imageCapturingReset()
    {
        imageX = 0f
        imageY = 0f
        imageScaleFactor = 1f
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setBackgroundColor(Color.rgb(51,51,51))
        canvas = Canvas(btmView!!)

    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.save()
        canvas?.scale(canvasScaleFactor, canvasScaleFactor)
        canvas?.translate(translateX/canvasScaleFactor  , translateY/canvasScaleFactor)

        canvas?.drawBitmap(btmView!!, 0f, 0f, null)
        if (imageDrawable != null) {
            canvas?.save()
             canvas?.translate(imageX, imageY)
            imageDrawable?.draw(canvas!!)
            canvas?.restore()
        }
        canvas?.restore()

    }

    fun setSizeBrush(s : Float)
    {
        brushSize = s
        paint.strokeWidth = brushSize*resources.displayMetrics.density
    }

    fun setBrushColor(color : Int)
    {
        selectedColor = color
        paint.color = selectedColor!!
    }


    fun captureDrawableImage()
    {
        if (imageDrawable != null) {
            canvas.save()
            canvas.translate(imageX,imageY)
            canvas.scale(imageScaleFactor,imageScaleFactor)
            canvas.drawBitmap(imageDrawable!!.bitmap,0f,0f,null)
            canvas.restore()
            imageMovingMode = false
            imageDrawable!!.bitmap.recycle()
            imageDrawable = null
            invalidate()
            imageCapturingReset()
            setPreviousMode()
        }
    }


    fun setSizeEraser(s : Float)
    {
        eraserSize = s
        paint.strokeWidth = eraserSize*resources.displayMetrics.density
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean
    {
        var x = event?.x
        var y = event?.y
        // учесть смещение при перемещении холста
        if (event != null) {
            x = (x!! - translateX) / canvasScaleFactor
            y = (y!! - translateY) / canvasScaleFactor
        }


        when(mode)
        {
            PaintViewMode.DRAW ->
            {

                when(event?.action) {
                    MotionEvent.ACTION_DOWN -> touchStart(x!!, y!!)
                    MotionEvent.ACTION_MOVE -> touchMove(x!!, y!!)
                    MotionEvent.ACTION_UP -> touchUp()
                }
            }
            PaintViewMode.ERASE ->
            {
                paint.color = eraserColor
                when(event?.action) {
                    MotionEvent.ACTION_DOWN -> touchStart(x!!, y!!)
                    MotionEvent.ACTION_MOVE -> touchMove(x!!, y!!)
                    MotionEvent.ACTION_UP -> touchUp()
                }
                paint.color = selectedColor!!
            }
            PaintViewMode.FLOODFILL ->
            {
                when(event?.action)
                {
                    MotionEvent.ACTION_DOWN ->
                    {
                        if (x!! >= 0 && y!! >=0 && y < btmView!!.height && x < btmView!!.width) {
                            val targetColor = btmView!!.getPixel(x.toInt(), y.toInt())
                            val floodfill  = FloodFillModel()
                            floodfill.useImage(btmView!!)
                            floodfill.floodFill( x.toInt(), y.toInt(), targetColor, paint.color)
                            invalidate()
                        }
                    }
                }
            }
            PaintViewMode.PAN_AND_ZOOM ->
            {
                scaleGestureDetector.onTouchEvent(event!!)
                gestureDetector.onTouchEvent(event)
            }
            PaintViewMode.IMAGE_CAPTURING ->
            {
                if (imageMovingMode && imageDrawable!=null)
                {

                    if (moving) imageScaleGestureDetector.onTouchEvent(event!!)
                    else
                    {
                        scaleGestureDetector.onTouchEvent(event!!)
                        gestureDetector.onTouchEvent(event)
                    }

                    when(event.action)
                    {
                        MotionEvent.ACTION_DOWN -> {
                            // проверяем, касается ли палец изображения
                            if (x!! >= imageX && x <= imageDrawable!!.bounds.right + imageX &&
                                y!! >= imageY && y <= imageDrawable!!.bounds.bottom + imageY
                            ) {

                                moving = true
                                refX = x
                                refY = y
                            }
                        }
                        MotionEvent.ACTION_MOVE -> {
                            // перемещаем изображение, если палец касается изображения
                            if (moving) {
                                imageX += x!! - refX!!
                                imageY += y!! - refY!!
                                invalidate()
                                refX = x
                                refY = y
                            }
                        }
                        MotionEvent.ACTION_UP ->
                        {
                            moving = false
                        }
                    }
                }
            }

        }
        return true
    }

    private fun touchUp() {
        path.reset()
    }

    fun cancelCapturing()
    {
        if (imageDrawable != null) {
            imageDrawable!!.bitmap.recycle()
            imageDrawable = null
            imageScaleFactor = 1f
            invalidate()
            imageCapturingReset()
            setPreviousMode()
        }
    }

    private fun touchMove(x: Float, y: Float) {
        val dx = abs(x -mX)
        val dy = abs(y -mY)
        if (dx >= _differenceSpace || dy >= _differenceSpace)
        {
            path.lineTo((x + mX)/2, (y + mY)/2)
            mX = x
            mY = y
            canvas.drawPath(path, paint)
            invalidate()
        }
    }

    private fun touchStart(x: Float, y: Float) {
        path.moveTo(x,y)
        mX = x
        mY = y
    }

    fun setImage(bitmap: Bitmap?) {
        imageDrawable = BitmapDrawable(resources, bitmap)
        imageDrawable?.setBounds(0, 0, bitmap!!.width, bitmap.height)
        imageMovingMode = true
    }

    fun setMode(mode : PaintViewMode) {
        this.prevChoosedMode = this.mode
        this.mode = mode

    }
    fun setPreviousMode()
    {
        mode = prevChoosedMode
    }

}
