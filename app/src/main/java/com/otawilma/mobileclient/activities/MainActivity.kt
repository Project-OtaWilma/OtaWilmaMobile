package com.otawilma.mobileclient.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import com.otawilma.mobileclient.OtawilmaNetworking
import com.otawilma.mobileclient.R
import com.otawilma.mobileclient.storage.EncryptedPreferenceStorage
import com.otawilma.mobileclient.tokenGlobal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), OtawilmaNetworking, NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawer : DrawerLayout
    private lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val navHostFragment=supportFragmentManager.findFragmentById(R.id.navHostFragmentMain)
        navController= navHostFragment!!.findNavController()
        val navView = findViewById<NavigationView>(R.id.navViewMain)
        val toolbar = findViewById<Toolbar>(R.id.toolbarMain)
        setSupportActionBar(toolbar)

        drawer = findViewById<DrawerLayout>(R.id.drawerLayoutMain)
        val toggle = ActionBarDrawerToggle(this,drawer,toolbar,0,0)

        drawer.addDrawerListener(toggle)
        toggle.syncState()

        // I debugged this for an hour ty very much
        navView.bringToFront()

        navView.setNavigationItemSelectedListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_login_settings,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.action_preferences ->{
                startActivity(Intent(this, PreferencesActivity::class.java))
            }
            R.id.action_info ->{
                // TODO info screen
            }
        }
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menuMainLogout ->{
                val encryptedPreferenceStorage = EncryptedPreferenceStorage(applicationContext)
                tokenGlobal = null
                CoroutineScope(Dispatchers.IO).launch {
                    repeatUntilSuccess(applicationContext, waitUntilToken(applicationContext)) { token ->
                        logout(token)
                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        encryptedPreferenceStorage.otaWilmaToken = null
                        encryptedPreferenceStorage.passWord = null
                        encryptedPreferenceStorage.userName = null
                        Toast.makeText(applicationContext,"Logout successful",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            R.id.menuMainHome -> navController.navigate(R.id.fragmentHomePage)
            R.id.menuMainSchedule -> navController.navigate(R.id.fragmentSchedule)
            R.id.menuMainMessages -> navController.navigate(R.id.fragmentMessages)
        }

        drawer.closeDrawer(GravityCompat.START)
        return true
    }

}