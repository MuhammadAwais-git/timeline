package com.translate.language.timelinemoduleonmap.Utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import com.translate.language.timelinemoduleonmap.R
import org.osmdroid.api.IMapController
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.MapBoxTileSource
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.tileprovider.tilesource.bing.BingMapTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


class Osmhelper {
    companion object {


        private const val bingMapApiKey =
            "AiHpeKq3PsuJTilRM7Cwna2MJ9ER69nz4fTXi5U3J1l2FrIxhXwGyhFi-aPbLv8w"
        //old
//        const val bindMapApiKey = "AoexEyqVex1qAdw1WdPm-gAot8bO_-Tf6B-5ZfH5jWGaOc7Q_0GSgy6ZilW2HPWn"


        fun zoomWithAnimate(mController: IMapController, point: GeoPoint, zoom: Int) {
            mController.animateTo(point, zoom.toDouble(), 3000, 0.75F)
        }


        fun bingMapStyle(mMapView: MapView) {
            BingMapTileSource.setBingKey(bingMapApiKey)
            val bing = BingMapTileSource(null)
            bing.style = BingMapTileSource.IMAGERYSET_AERIALWITHLABELS
//            bing.style = BingMapTileSource.IMAGERYSET_AERIAL
            bing.initMetaData()
            mMapView.setTileSource(bing)
        }

        fun setStdTileProvider(context: Context, mMapView: MapView) {
            if (mMapView.tileProvider !is MapTileProviderBasic) {
                val bitmapProvider = MapTileProviderBasic(context)
                mMapView.tileProvider = bitmapProvider
            }
        }

        fun setMarkerIconAsPhoto(context: Context, marker: Marker, thumbnail: Bitmap) {
            var thumbnail = thumbnail
            val borderSize = 2
            thumbnail = Bitmap.createScaledBitmap(thumbnail, 60, 60, true)
            val withBorder = Bitmap.createBitmap(
                thumbnail.width + borderSize * 2,
                thumbnail.height + borderSize * 2,
                thumbnail.config
            )
            val canvas = Canvas(withBorder)
            canvas.drawColor(Color.TRANSPARENT)
            canvas.drawBitmap(thumbnail, borderSize.toFloat(), borderSize.toFloat(), null)
            val icon = BitmapDrawable(context.resources, withBorder)
            marker.icon = icon
        }


        fun setMarkerIconAsOnMap(context: Context, marker: Marker, thumbnail: Bitmap) {
            var thumbnail = thumbnail
            val borderSize = 2
            thumbnail = Bitmap.createScaledBitmap(thumbnail, 100, 100, true)
            val withBorder = Bitmap.createBitmap(
                thumbnail.width + borderSize * 2,
                thumbnail.height + borderSize * 2,
                thumbnail.config
            )
            val canvas = Canvas(withBorder)
            canvas.drawColor(Color.TRANSPARENT)
            canvas.drawBitmap(thumbnail, borderSize.toFloat(), borderSize.toFloat(), null)
            val icon = BitmapDrawable(context.resources, withBorder)
            marker.icon = icon
        }


        fun addMarkerOnMapWithDrawable(
            context: Context,
            mOriginPoint: GeoPoint,
            snip: String,
            mapView: MapView
        ) {
            try {
                val startMarker = Marker(mapView)
                startMarker.position = mOriginPoint
                startMarker.title = snip
                val icon =
                    BitmapFactory.decodeResource(context.resources, R.drawable.location_off)
                startMarker.icon = null
                setMarkerIconAsPhoto(context, startMarker, icon!!)
                mapView.overlays.add(startMarker)


            } catch (e: Exception) {
            }
        }

        fun addMarkerOnMapWithCommon(
            context: Context,
            mOriginPoint: GeoPoint,
            mapView: MapView
        ) {
            try {
                val startMarker = Marker(mapView)
                startMarker.position = mOriginPoint
                val icon =
                    BitmapFactory.decodeResource(context.resources, R.drawable.location_off)
                startMarker.icon = null
                setMarkerIconAsPhoto(context, startMarker, icon!!)
                mapView.overlays.add(startMarker)


            } catch (e: Exception) {
            }
        }

        fun addMarkerOnMapWithDrawableSatellite(
            context: Context,
            mOriginPoint: GeoPoint,
            snip: String,
            mapView: MapView
        ) {
            try {
                val startMarker = Marker(mapView)
                startMarker.position = mOriginPoint
                startMarker.title = snip
                val icon =
                    BitmapFactory.decodeResource(context.resources, R.drawable.location_off)
                startMarker.icon = null
                setMarkerIconAsPhoto(context, startMarker, icon!!)
                mapView.overlays.add(startMarker)
            } catch (e: Exception) {
            }
        }

        fun addMarkerOnMapBitmapWithName(
            context: Context,
            mOriginPoint: GeoPoint,
            titleName: String,
            bitmap: Bitmap,
            mapView: MapView
        ) {
            try {
                val startMarker = Marker(mapView)
                startMarker.position = mOriginPoint
                startMarker.title = titleName
                startMarker.icon = null
                setMarkerIconAsOnMap(context, startMarker, bitmap)
                mapView.overlays.add(startMarker)
            } catch (e: Exception) {
            }
        }

        fun setMapBoxMapStyleLayer(context: Context): OnlineTileSourceBase {
            var mMabBoxStyle: OnlineTileSourceBase? = null

            mMabBoxStyle = MapBoxTileSource("MapBoxSatelliteLabelled", 1, 19, 256, ".png")
            (mMabBoxStyle as MapBoxTileSource).retrieveAccessToken(context)
            (mMabBoxStyle as MapBoxTileSource).retrieveMapBoxMapId(context)
            return TileSourceFactory.addTileSource(mMabBoxStyle) as OnlineTileSourceBase
        }

        fun hav(x: Double): Double {
            val sinHalf = Math.sin(x * 0.5)
            return sinHalf * sinHalf
        }

        fun arcHav(x: Double): Double {
            return 2.0 * Math.asin(Math.sqrt(x))
        }

        fun havDistance(lat1: Double, lat2: Double, dLng: Double): Double {
            return hav(lat1 - lat2) + hav(dLng) * Math.cos(lat1) * Math.cos(lat2)
        }

        private fun distanceRadians(
            lat1: Double,
            lng1: Double,
            lat2: Double,
            lng2: Double
        ): Double {

            return arcHav(havDistance(lat1, lat2, lng1 - lng2))
        }

        fun computeAngleBetween(from: GeoPoint, to: GeoPoint): Double {
            return distanceRadians(
                Math.toRadians(from.latitude),
                Math.toRadians(from.longitude),
                Math.toRadians(to.latitude),
                Math.toRadians(to.longitude)
            )
        }

        fun computeDistanceBetween(from: GeoPoint, to: GeoPoint): Double {
            return computeAngleBetween(from, to) * 6371009.0
        }
    }
}