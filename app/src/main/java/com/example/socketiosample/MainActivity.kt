package com.example.socketiosample

import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.RotationType
import com.yandex.runtime.image.ImageProvider
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.net.URISyntaxException


class MainActivity : AppCompatActivity() {

    private val TAG_SOCKET = "TAG_SOCKET"
    private lateinit var mSocket: Socket
    private var marker : PlacemarkMapObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.setApiKey("63163360-3814-4a22-b5ce-13eb939c1661")
        MapKitFactory.initialize(this)

        setContentView(R.layout.activity_main)
        mapview.map.move(
            CameraPosition(Point(51.1188, 71.4157), 11.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0f),
            null
        )

        button.setOnClickListener {
            val jsonCoor = JSONObject()
            jsonCoor.put("lat", 51.1188)
            jsonCoor.put("long", 71.4157)
            jsonCoor.put("created_at", "2019-12-10 13:34:00")

            val jsonObj = JSONObject()
            jsonObj.put("user_id", 2850)
            jsonObj.put("coordinates", jsonCoor)

            Log.e(TAG_SOCKET, "emit gps update user")
            mSocket.emit("gps user data")
        }

        try {
            mSocket = IO.socket("https://testgps.unipark.kz")
        } catch (e: URISyntaxException) {
        }

        mSocket.on("gps updated location") {
            Log.e(TAG_SOCKET, "gps updated location")
            it.forEach {
                val data = it as JSONObject
                runOnUiThread {
                    if (marker == null) {
                        marker = mapview.map.mapObjects.addPlacemark(
                            Point(
                                data.getDouble("lat"),
                                data.getDouble("long")
                            ),
                            ImageProvider.fromResource(this, R.drawable.red_location),
                            IconStyle().setAnchor(PointF(0.5f, 1f))
                                .setRotationType(RotationType.ROTATE)
                                .setZIndex(0f)
                                .setScale(0.1f)
                        )
                    } else {
                        marker!!.geometry = Point(
                            data.getDouble("lat"),
                            data.getDouble("long")
                        )
                    }
                }
                Log.e(TAG_SOCKET, data.toString())
            }
        }
        mSocket.on("auth success") {
            mSocket.emit("gps subscribe", getUserJSON())
            Log.e(TAG_SOCKET, "auth success")
            it.forEach {
                val data1 = it as JSONObject
                Log.e(TAG_SOCKET, data1.toString())
            }
        }
        mSocket.on("auth error") {
            Log.e(TAG_SOCKET, "auth error")
            it.forEach {
                val data1 = it as JSONObject
                Log.e(TAG_SOCKET, data1.toString())
            }
        }
        mSocket.on("gps user data") {
            Log.e(TAG_SOCKET, "gps user data")
            it.forEach {
                val data1 = it as JSONObject
                Log.e(TAG_SOCKET, data1.toString())
            }
        }
        mSocket.on("gps driver data") {
            Log.e(TAG_SOCKET, "gps driver data")
            it.forEach {
                val data1 = it as JSONObject
                Log.e(TAG_SOCKET, data1.toString())
            }
        }
        mSocket.on("gps user location") {
            Log.e(TAG_SOCKET, "gps user location")
            it.forEach {
                val data1 = it as JSONObject
                Log.e(TAG_SOCKET, data1.toString())
            }
        }
        mSocket.on("gps driver location") {
            Log.e(TAG_SOCKET, "gps driver location")
            it.forEach {
                val data1 = it as JSONObject
                Log.e(TAG_SOCKET, data1.toString())
            }
        }
        mSocket.connect()
        Log.e(TAG_SOCKET, "emit auth")
        mSocket.emit("auth", getUserLogin())
        Log.e(TAG_SOCKET, "emit gps subscribe")
    }

    private fun getUserLogin(): JSONObject {
        val jsonObj = JSONObject()
        jsonObj.put("phoneNumber", "+77779936999")
        jsonObj.put("password", "1111")
        return jsonObj
    }

    private fun getUserJSON(): JSONObject {
        val jsonObj = JSONObject()
        jsonObj.put("type", "user")
        jsonObj.put("id", 2850)
        return jsonObj
    }

    override fun onStop() {
        super.onStop()
        mapview.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onStart() {
        super.onStart()
        mapview.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onDestroy() {
        mSocket.disconnect()
        mSocket.off()
        super.onDestroy()
    }
}
