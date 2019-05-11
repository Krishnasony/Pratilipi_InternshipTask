package i.krishnasony.pratilipicontacts

import android.Manifest
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import i.krishnasony.pratilipicontacts.databinding.ActivityContactListBinding
import android.provider.ContactsContract
import android.widget.Toast
import i.krishnasony.pratilipicontacts.db.ContactModel
import i.krishnasony.pratilipicontacts.ui.conatctlist.ContactListAdapter
import android.Manifest.permission
import android.Manifest.permission.INTERNET
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.support.annotation.NonNull






class ContactListActivity : AppCompatActivity() {

    private lateinit var dataBinding:ActivityContactListBinding
    private var name: String? = null
    private var phonenumber: String? = null
    private var contact:ContactModel?=null
    val INTERNETCODE = 101
    val CONTACTCODE = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this,R.layout.activity_contact_list)
        init()
        getInternetPermissions()
        enableRuntimePermissionForContacts()
        getContactList()

    }

    private fun getContactList() {
        val users:MutableList<ContactModel>? = mutableListOf()
        val cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)
        Toast.makeText(this,"Message"+cursor,Toast.LENGTH_LONG).show()
        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            users!!.add(ContactModel(name.toString(),phonenumber.toString()))
            Toast.makeText(this,"Name: "+name+"  Phone:  "+phonenumber,Toast.LENGTH_LONG).show()

        }
        cursor.close()
        dataBinding.contactRecyclerView.layoutManager = LinearLayoutManager(this)
        dataBinding.contactRecyclerView.adapter = ContactListAdapter(users!!)

    }
    private fun getInternetPermissions() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.INTERNET
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.INTERNET), INTERNETCODE)
        }

    }
    fun enableRuntimePermissionForContacts() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_CONTACTS
            )
        ) {

            Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show()
        } else {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                CONTACTCODE
            )

        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {

            INTERNETCODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permisson Granted", Toast.LENGTH_SHORT).show()
            }

            CONTACTCODE -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permisson Granted", Toast.LENGTH_SHORT).show()
            }

            else -> {
            }
        }


    }

    private fun init() {

        setToolbar()
    }

    private fun setToolbar() {
        dataBinding.toolbar.title= "Pratilipi Contact"
        dataBinding.toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.black))
        setSupportActionBar(dataBinding.toolbar)
    }
}
