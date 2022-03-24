package com.pocketimps.test.bandlab.wavetrimmer.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Size
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.applyCanvas
import com.pocketimps.extlib.alsoIfTrue
import com.pocketimps.extlib.make
import com.pocketimps.test.bandlab.wavetrimmer.R
import com.pocketimps.test.bandlab.wavetrimmer.app.AppKoin
import com.pocketimps.testbandlab.wavetrimmer.core.ViewPort
import com.pocketimps.testbandlab.wavetrimmer.core.wave.WaveData
import com.pocketimps.testbandlab.wavetrimmer.core.wave.WavePoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


class WaveView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : View(context, attrs, defStyle),
      AppKoin {
  private enum class Handle {
    Start, End
  }

  private var selectionStart = 0
  private var selectionEnd = Int.MAX_VALUE
  private var visibleSelectionStart = 0
  private var visibleSelectionEnd = Int.MAX_VALUE

  private val _stateFlow = MutableStateFlow(0..0)
  val stateFlow: Flow<IntRange> = _stateFlow

  private val data = ArrayList<WavePoint>()
  private var xPointStep = 0.0F

  private val simplePaint = Paint()

  private val dimPaint = Paint().apply {
    color = context.getColor(R.color.waveDimBackground)
  }

  private val handlePaint = Paint().apply {
    color = context.getColor(R.color.waveHandle)
  }

  private val handleDraggedPaint = Paint().apply {
    color = context.getColor(R.color.waveHandleDragged)
    strokeWidth = 3.0F
  }

  private var bitmap: Bitmap? = null
  private var lastSize = Size(0, 0)

  private val handleTouchDistance = context.resources.getDimensionPixelSize(R.dimen.wave_handle_touch_distance)
  private var draggingHandle: Handle? = null
  private var draggingHandlePivot = 0

  private val snapStepDistance = context.resources.getDimensionPixelSize(R.dimen.wave_snap_step_distance)
  private val snapStepDelay = context.resources.getInteger(R.integer.wave_snap_step_ms).toLong()


  private fun hasData() = (data.size > 1)
  private fun isMeasured() = (lastSize.width > 0)

  private fun fixSelections() {
    if (!isMeasured())
      return

    if (selectionEnd == Int.MAX_VALUE)
      selectionEnd = lastSize.width

    if (visibleSelectionEnd == Int.MAX_VALUE)
      visibleSelectionEnd = lastSize.width
  }

  private fun notifyObservers() {
    if (!hasData() || !isMeasured())
      return

    val startIndex = selectionStart.pointToIndex()
    val endIndex = selectionEnd.pointToIndex()
    _stateFlow.tryEmit(startIndex..endIndex)
  }

  // X-coordinate of given point index on the canvas
  private fun Int.indexToPoint(): Int {
    when {
      this == 0 ->
        return 0

      this == data.size - 1 ->
        return lastSize.width
    }

    return (this * xPointStep).roundToInt()
  }

  // Closest point index of given x-coordinate on the canvas
  private fun Int.pointToIndex() = (this / xPointStep).toInt()

  private fun getHandleAtPoint(x: Float) = when {
    ((x - selectionStart).absoluteValue < handleTouchDistance) -> Handle.Start
    ((x - selectionEnd).absoluteValue < handleTouchDistance) -> Handle.End
    else -> null
  }

  private fun getSnapPoint(x: Int) =
    ((x / xPointStep).roundToInt() * xPointStep).toInt()

  private fun Canvas.drawData() {
    if (!hasData())
      return

    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
      color = context.getColor(R.color.waveBody)
    }

    val viewPort = ViewPort(lastSize.width, lastSize.height)

    // Scale points to viewport and associate to corresponding x-coordinates
    val scaled = data.mapIndexed { index, p ->
      val x = index.indexToPoint().toFloat()
      val scaled = p.scaleToViewPort(viewPort)
      (x to scaled)
    }

    // Construct path to draw: collect upper points, then lower ones
    val path = Path()
    scaled.forEachIndexed { index, (x, p) ->
      if (index == 0)
        path.moveTo(x, p.high)
      else
        path.lineTo(x, p.high)
    }

    scaled.reversed().forEach { (x, p) ->
      path.lineTo(x, p.low)
    }

    path.close()
    drawPath(path, paint)

    // Zero-level line
    val helperLinePaint = Paint().apply {
      color = context.getColor(R.color.waveHelper)
      style = Paint.Style.STROKE
      pathEffect = DashPathEffect(floatArrayOf(context.resources.getDimension(R.dimen.wave_helper_dash_gap),
                                               context.resources.getDimension(R.dimen.wave_helper_dash_line)), 0.0F)
    }

    val verticalCenter = lastSize.height / 2.0F
    drawLine(0.0F, verticalCenter, lastSize.width.toFloat(), verticalCenter, helperLinePaint)

    // Range-level lines
    drawLine(0.0F, 0.0F, lastSize.width.toFloat(), 0.0F, helperLinePaint)
    drawLine(0.0F, lastSize.height - 1.0F, lastSize.width.toFloat(), lastSize.height - 1.0F, helperLinePaint)
  }

  private fun Canvas.drawDim(start: Int, end: Int) {
    if (start < end)
      drawRect(Rect(start, 0, end, lastSize.height), dimPaint)
  }

  private fun Canvas.drawHandle(handle: Handle) {
    val paint = if (draggingHandle == handle)
      handleDraggedPaint
    else
      handlePaint

    val x = (if (handle == Handle.Start)
      visibleSelectionStart
    else
      visibleSelectionEnd - 1).toFloat()

    drawLine(x, 0.0F, x, lastSize.height.toFloat(), paint)
  }

  private fun ensureDataBitmap(): Bitmap {
    val innerWidth = width - paddingStart - paddingEnd
    val innerHeight = height - paddingTop - paddingBottom

    if (innerWidth == lastSize.width && innerHeight == lastSize.height)
      return bitmap!!

    lastSize = Size(innerWidth, innerHeight)
    xPointStep = innerWidth.toFloat() / (data.size - 1)
    fixSelections()
    notifyObservers()

    bitmap?.recycle()
    if (innerWidth < 1 || innerHeight < 1)
      return Bitmap.createBitmap(0, 0, Bitmap.Config.ARGB_8888).also {
        bitmap = it
      }

    return Bitmap.createBitmap(innerWidth, innerHeight, Bitmap.Config.ARGB_8888).also {
      it.applyCanvas {
        drawData()
      }

      bitmap = it
    }
  }

  private fun snapHandles() {
    var stepAgain = false

    fun Int.getNextSnapPoint(target: Int) =
      if (this == target)
        target
      else {
        stepAgain = true
        if ((this - target).absoluteValue > snapStepDistance) {
          if (this > target)
            this - snapStepDistance
          else
            this + snapStepDistance
        } else
          target
      }

    visibleSelectionStart = visibleSelectionStart.getNextSnapPoint(selectionStart)
    visibleSelectionEnd = visibleSelectionEnd.getNextSnapPoint(selectionEnd)

    if (stepAgain)
      postDelayed(::invalidate, snapStepDelay)
  }

  override fun onDraw(canvas: Canvas) {
    if (!isAttachedToWindow)
      return

    canvas.translate(paddingStart.toFloat(), paddingTop.toFloat())

    // Wave data layer
    ensureDataBitmap().make {
      canvas.drawBitmap(it, 0.0F, 0.0F, simplePaint)
    }

    // Dim areas before selection start and after selection end
    canvas.drawDim(0, visibleSelectionStart)
    canvas.drawDim(visibleSelectionEnd, lastSize.width)

    // Handles
    if (data.isNotEmpty()) {
      canvas.drawHandle(Handle.Start)
      canvas.drawHandle(Handle.End)
    }

    // If necessary, make a step to snap handle to nearest point
    draggingHandle ?: snapHandles()
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    if (!hasData())
      return false

    when (event.actionMasked) {
      MotionEvent.ACTION_DOWN -> {
        if (draggingHandle != null)
          return true

        return getHandleAtPoint(event.x).let { handle ->
          (handle != null).alsoIfTrue {
            draggingHandle = handle
            draggingHandlePivot = event.x.toInt() - if (handle == Handle.Start)
              selectionStart
            else
              selectionEnd

            invalidate()
          }
        }
      }

      MotionEvent.ACTION_UP,
      MotionEvent.ACTION_CANCEL ->
        return (draggingHandle != null).alsoIfTrue {
          draggingHandle = null
          draggingHandlePivot = 0
          invalidate()
        }

      MotionEvent.ACTION_MOVE ->
        return (draggingHandle != null).alsoIfTrue {
          val oldStart = selectionStart
          val oldEnd = selectionEnd

          val newX = (event.x.toInt() - draggingHandlePivot).coerceIn(0, lastSize.width - 1)
          val snap = getSnapPoint(newX)

          if (draggingHandle == Handle.Start) {
            visibleSelectionStart = newX
            if (snap != selectionStart)
              selectionStart = snap
          } else {
            visibleSelectionEnd = newX
            if (snap != selectionEnd)
              selectionEnd = snap
          }

          // Swap handles, if start handle slides after the end one
          if (visibleSelectionStart > visibleSelectionEnd) {
            var tmp = visibleSelectionStart
            visibleSelectionStart = visibleSelectionEnd
            visibleSelectionEnd = tmp

            tmp = selectionStart
            selectionStart = selectionEnd
            selectionEnd = tmp

            draggingHandle = if (draggingHandle == Handle.Start)
              Handle.End
            else
              Handle.Start
          }

          // Notify observers, if selection changed
          if (selectionStart != oldStart || selectionEnd != oldEnd)
            notifyObservers()

          invalidate()
        }
    }

    return false
  }

  private fun dropDataBitmap() {
    bitmap?.recycle()
    bitmap = null
    lastSize = Size(0, 0)
  }

  fun setData(newData: WaveData) {
    dropDataBitmap()

    data.clear()
    data.addAll(newData)

    resetSelection()
  }

  fun resetSelection() {
    selectionStart = 0
    selectionEnd = Int.MAX_VALUE
    visibleSelectionStart = selectionStart
    visibleSelectionEnd = selectionEnd
    fixSelections()
    notifyObservers()

    invalidate()
  }

  override fun onDetachedFromWindow() {
    super.onDetachedFromWindow()
    dropDataBitmap()
  }
}
