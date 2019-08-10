package com.tavish.musicplayerbeat.Activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tavish.musicplayerbeat.Adapters.SongAdapter
import com.tavish.musicplayerbeat.Helpers.MediaHelpers.MusicCursor
import com.tavish.musicplayerbeat.Helpers.SharedPrefHelper
import com.tavish.musicplayerbeat.Models.BeatDto
import com.tavish.musicplayerbeat.R
import com.tavish.musicplayerbeat.Utils.Constants
import com.tavish.musicplayerbeat.Utils.Logger
import com.tavish.musicplayerbeat.Utils.MusicUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.ArrayList

open class SplashActivity : AppCompatActivity() {

    private var mSongList: MutableList<BeatDto>? = null
    private var mCompositeDisposable: CompositeDisposable? = null
  //  private var mAdapter: SongAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        mSongList = mutableListOf()
    //    mAdapter=SongAdapter(,)
        mCompositeDisposable= CompositeDisposable()

        var launchCount = SharedPrefHelper.getInstance().getInt(SharedPrefHelper.Key.LAUNCH_COUNT, 0)
        launchCount++

        SharedPrefHelper.getInstance().put(SharedPrefHelper.Key.LAUNCH_COUNT, launchCount)
        launchMainActivity()
    }



    private fun launchMainActivity() {
        if (!MusicUtils.isLollipop()) {
            if (checkAndRequestPermissions()) {
                buildLibrary()
            }
        } else {
            buildLibrary()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (Constants.REQUEST_PERMISSIONS === requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                buildLibrary()
            } else {
                val builder = AlertDialog.Builder(this)
                builder.setTitle(R.string.grant_permission)
                builder.setMessage(R.string.grant_permission_message)
                builder.setNegativeButton(R.string.no) { dialog, which ->
                    dialog.dismiss()
                    finish()
                }


                builder.setPositiveButton(R.string.open_settings) { dialog, which ->
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)

                }
                builder.create().show()
            }
        }
    }

    private fun checkAndRequestPermissions(): Boolean {
        val modifyAudioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

        val listPermissionsNeeded = ArrayList<String>()
        if (modifyAudioPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), Constants.REQUEST_PERMISSIONS)
            return false
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        mCompositeDisposable?.dispose()
    }

    private fun buildLibrary() {
        mCompositeDisposable?.add(Observable.fromCallable { MusicCursor.buildMusicLibrary() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object:DisposableObserver<Boolean>(){
                override fun onNext(t: Boolean) {
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                    this@SplashActivity.finish()
                }

                override fun onComplete() {
                }


                override fun onError(e: Throwable) {
                    Logger.exp("" + e.message)
                }


            })

        )
    }



}
