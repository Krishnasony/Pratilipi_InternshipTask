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
import android.view.View
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

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this,R.layout.activity_contact_list)
        init()
        setRefreshSwipeListener()
        getContactList()

    }

    private fun getContactList() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // No explanation needed, we can request the permission.
            dataBinding.progressbar.visibility = View.VISIBLE
            dataBinding.contactRecyclerView.visibility = View.GONE
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_CONTACTS,Manifest.permission.CALL_PHONE),
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS
                )


        } else {
            dataBinding.progressbar.visibility = View.VISIBLE
            dataBinding.contactRecyclerView.visibility = View.GONE
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
            dataBinding.swipeRefreshContacts.isRefreshing = false
            dataBinding.contactRecyclerView.visibility = View.VISIBLE
            dataBinding.progressbar.visibility = View.GONE
            dataBinding.contactRecyclerView.layoutManager = LinearLayoutManager(this)
            dataBinding.contactRecyclerView.adapter = ContactListAdapter(users!!.sortedBy { it.name })
        }

    }
    fun openDisplayPhoto(contactId: Long): Uri? {
        val contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId)
        var displayPhotoUri:Uri?=null
        if (contactUri!=null){
            displayPhotoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.DISPLAY_PHOTO)
        }
       return displayPhotoUri

    }
    private fun setRefreshSwipeListener(){
        dataBinding.swipeRefreshContacts.setOnRefreshListener {
            dataBinding.swipeRefreshContacts.isRefreshing = true
            getContactList()
        }
    }

    override fun onResume() {
        super.onResume()
        setRefreshSwipeListener()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun statusBarColor(contactListActivity: ContactListActivity) {
        val window = contactListActivity.window
        window.statusBarColor = ContextCompat.getColor(this,R.color.status_color_pratilipi)

    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)||(grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay!
                    getContactList()
                } else {
                    // permission denied, boo!
                    Toast.makeText(this,"Permission Denied please try Again",Toast.LENGTH_LONG).show()
                    dataBinding.swipeRefreshContacts.isRefreshing = false
                    dataBinding.contactRecyclerView.visibility = View.VISIBLE
                    dataBinding.progressbar.visibility = View.GONE

                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
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

    companion object {
        private const val MY_PERMISSIONS_REQUEST_READ_CONTACTS =103
        private const val MAKE_PHONE_CALL =104

    }
}
