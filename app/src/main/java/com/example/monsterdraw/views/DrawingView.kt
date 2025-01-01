package com.example.monsterdraw.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var paint: Paint = Paint()
    private var bonesPaint: Paint = Paint()
    private var path = android.graphics.Path()
    private var bonesPath = android.graphics.Path()
    private var drawState = DrawingState.MONSTER



    private var boneStartX = 0f
    private var boneStartY = 0f
    private var boneEndX = 0f
    private var boneEndY = 0f
    private var touchdownX = 0f
    private var touchdownY = 0f
    private var difX = 0f
    private var difY = 0f
    private var pointIndexSelected = 0

    private var bonePointOffset = 30


    init {
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f
        bonesPaint.color = Color.CYAN
        bonesPaint.style = Paint.Style.STROKE
        bonesPaint.strokeWidth = 10f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path, paint)
//        canvas.drawPath(_translate(bonesPath), bonesPaint)
        canvas.drawPath(bonesPath, bonesPaint)
        canvas.drawOval(boneStartX - bonePointOffset, boneStartY - bonePointOffset, boneStartX + bonePointOffset, boneStartY + bonePointOffset, bonesPaint)
        canvas.drawOval(boneEndX - bonePointOffset, boneEndY - bonePointOffset, boneEndX + bonePointOffset, boneEndY + bonePointOffset, bonesPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                _pathMoveTo(x, y)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                _pathLineTo(x, y)
            }
            MotionEvent.ACTION_UP -> {
                // Do nothing
                difX = 0f
                difY = 0f
            }
            else -> return false
        }
        invalidate()
        return true
    }

    fun _pathMoveTo(x: Float, y: Float) {
        when(drawState) {
            DrawingState.BONES -> {
                boneStartX = x
                boneStartY = y
            }
            DrawingState.MONSTER -> {
                path.moveTo(x, y)
            }
            DrawingState.MOVE -> {
                //todo save touched point for ref later

                    touchdownX = x
                    touchdownY = y


            }
        }
    }
    fun _pathLineTo(x: Float, y: Float) {
        when(drawState) {
            DrawingState.BONES -> {
                bonesPath.reset()
                bonesPath.moveTo(boneStartX, boneStartY)
                bonesPath.lineTo(x, y)
                boneEndX = x
                boneEndY = y
            }
            DrawingState.MONSTER -> {
                path.lineTo(x, y)
            }
            DrawingState.MOVE -> {
                bonesPath.reset()
                bonesPath.moveTo(boneStartX, boneStartY)
                val newPoint = translatePointInCircle(boneStartX, boneStartY, boneEndX, boneEndY, x - touchdownX, y - touchdownY)
                bonesPath.lineTo(newPoint.first, newPoint.second)

            }
        }
    }

    fun setBonesState() {
        drawState = DrawingState.BONES
    }
    fun setMoveState() {
        drawState = DrawingState.MOVE
    }
    fun setMonsterState() {
        drawState = DrawingState.MONSTER
    }

    fun clear() {
        drawState = DrawingState.MONSTER
        path.reset()
        bonesPath.reset()
        invalidate()
    }

    fun _translate(path: android.graphics.Path): android.graphics.Path {
        var isFirst = true
        val p = android.graphics.Path()
        val points = mutableListOf<Point>()
        val pIterator = android.graphics.PathMeasure(path, false)
        val length = pIterator.length
        val step = 1f
        var distance = 0f
        while (distance < length) {
            val point = FloatArray(2)
            pIterator.getPosTan(distance, point, null)
            points.add(Point(point[0].toInt(), point[1].toInt()))
            distance += step
        }
        for (point in points) {
            if (isFirst) {
                p.moveTo(point.x + difX, point.y + difY)
                isFirst = false
            } else {
                p.lineTo(point.x + difX, point.y + difY)
            }
        }
        return p

    }
    fun _translateLastPoint(path: android.graphics.Path): android.graphics.Path {
        var isFirst = true
        val p = android.graphics.Path()
        val points = mutableListOf<Point>()
        val pIterator = android.graphics.PathMeasure(path, false)
        val length = pIterator.length
        val step = 1f
        var distance = 0f
        while (distance < length) {
            val point = FloatArray(2)
            pIterator.getPosTan(distance, point, null)
            points.add(Point(point[0].toInt(), point[1].toInt()))
            distance += step
        }
        for (point in points) {
            if (isFirst) {
//                p.moveTo(point.x + difX, point.y + difY)
                p.moveTo(point.x*1f, point.y*1f)
                isFirst = false
            } else {
                p.lineTo(point.x + difX, point.y + difY)
            }
        }
        return p

    }
    fun translatePointInCircle(ax: Float, ay: Float, bx: Float, by: Float, diffX: Float, diffY: Float): Pair<Float, Float> {
        // Calculate the radius
        val radius = Math.hypot((bx - ax).toDouble(), (by - ay).toDouble()).toFloat()

        // Calculate the new position of B
        val newBx = bx + diffX
        val newBy = by + diffY

        // Calculate the angle of the new position relative to A
        val angle = Math.atan2((newBy - ay).toDouble(), (newBx - ax).toDouble())

        // Calculate the constrained position of B
        val constrainedBx = ax + radius * Math.cos(angle).toFloat()
        val constrainedBy = ay + radius * Math.sin(angle).toFloat()

        return Pair(constrainedBx, constrainedBy)
    }

    fun translatePoint(x: Float, y: Float, path: android.graphics.Path) {
        val newX = x + difX
        val newY = y + difY
        path.lineTo(newX, newY)
    }


}

enum class DrawingState {
    MOVE, BONES, MONSTER
}

class FloatPoint(val x: Float, val y: Float)