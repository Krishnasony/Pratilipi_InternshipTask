package i.krishnasony.pratilipicontacts

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import i.krishnasony.pratilipicontacts.databinding.ActivityContactListBinding
import i.krishnasony.pratilipicontacts.db.ContactModel
import i.krishnasony.pratilipicontacts.ui.conatctlist.ContactListAdapter


class ContactListActivity : AppCompatActivity() {

    private lateinit var dataBinding:ActivityContactListBinding
    private var name: String? = null
    private var phonenumber: String? = null
    private var photo:String?=null
    private var email:String?=null
    val INTERNETCODE = 101
    val CONTACTCODE = 102

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this,R.layout.activity_contact_list)
        init()
        getInternetPermissions()
        enableRuntimePermissionForContacts()
        setRefreshSwipeListener()
        getContactList()

    }

    private fun getContactList() {
        val users:MutableList<ContactModel>? = mutableListOf()
        val cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)
        while (cursor.moveToNext()) {
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            photo = openDisplayPhoto(cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))).toString()
            val id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID))
                val cur1 = contentResolver.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                    arrayOf(id), null
                )
            if (cur1.moveToNext()) {
                 email = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
            }


            users!!.add(ContactModel(name.toString(),phonenumber.toString(),photo.toString(),email.toString()))

        }
        cursor.close()
        dataBinding.contactRecyclerView.layoutManager = LinearLayoutManager(this)
        dataBinding.contactRecyclerView.adapter = ContactListAdapter(users!!.sortedBy { it.name })

    }
    fun openDisplayPhoto(contactId: Long): Uri? {
        val contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId)
        val displayPhotoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO)
       return displayPhotoUri

    }
    private fun setRefreshSwipeListener(){
        dataBinding.swipeRefreshContacts.setOnRefreshListener {
            getContactList()
            dataBinding.swipeRefreshContacts.isRefreshing = false
        }
    }

    override fun onResume() {
        super.onResume()
        setRefreshSwipeListener()
    }

//    fun getNameEmailDetails(): ArrayList<String> {
//        val names = ArrayList<String>()
//        val cr = contentResolver
//        val cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
//        if (cur!!.count > 0) {
//            while (cur.moveToNext()) {
//                val id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID))
//                val cur1 = cr.query(
//                    ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
//                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
//                    arrayOf(id), null
//                )
//                while (cur1!!.moveToNext()) {
//                    //to get the contact names
//                    val name = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
//                    Log.e("Name :", name)
//                    val email = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
//                    Log.e("Email", email)
//                    if (email != null) {
//                        names.add(name)
//                    }
//                }
//                cur1.close()
//            }
//        }
//        return names
//    }
    private fun getInternetPermissions() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.INTERNET
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.INTERNET), INTERNETCODE)
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun statusBarColor(contactListActivity: ContactListActivity) {
        val window = contactListActivity.window
        window.statusBarColor = ContextCompat.getColor(this,R.color.status_color_pratilipi)

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

    @RequiresApi(Build.VERSION_CODES.M)
    private fun init() {
        statusBarColor(this)
        setToolbar()
    }

    private fun setToolbar() {
        dataBinding.toolbar.title= "Pratilipi Contact"
        dataBinding.toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.black))
        setSupportActionBar(dataBinding.toolbar)
    }
}
