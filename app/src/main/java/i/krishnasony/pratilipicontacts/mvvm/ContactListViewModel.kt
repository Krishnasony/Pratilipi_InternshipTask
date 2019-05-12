package i.krishnasony.pratilipicontacts.mvvm

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.arch.paging.PositionalDataSource
import android.content.ContentResolver
import android.provider.ContactsContract
import i.krishnasony.pratilipicontacts.db.ContactModel

class ContactListViewModel(private val contentResolver: ContentResolver) : ViewModel() {
    //ToDo: use ViewModel
    lateinit var contactsList: LiveData<PagedList<ContactModel>>

    fun loadContacts() {
        val config = PagedList.Config.Builder()
            .setPageSize(20)
            .setEnablePlaceholders(false)
            .build()
        contactsList = LivePagedListBuilder<Int, ContactModel>(
            ContactsDataSourceFactory(contentResolver), config).build()
    }
}

class ContactsDataSourceFactory(private val contentResolver: ContentResolver) :
    DataSource.Factory<Int, ContactModel>() {

    override fun create(): DataSource<Int, ContactModel> {
        return ContactsDataSource(contentResolver)
    }
}

class ContactsDataSource(private val contentResolver: ContentResolver) :
    PositionalDataSource<ContactModel>() {

    companion object {
        private val PROJECTION = arrayOf(
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
            ContactsContract.CommonDataKinds.Email.DATA

        )
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<ContactModel>) {
        callback.onResult(getContacts(params.requestedLoadSize, params.requestedStartPosition) as List<ContactModel>, 0)
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<ContactModel>) {
        callback.onResult(getContacts(params.loadSize, params.startPosition) as List<ContactModel>)
    }

    private fun getContacts(limit: Int, offset: Int): MutableList<ContactModel> {
        val cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
            PROJECTION,
            null,
            null,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY +
                    " ASC LIMIT " + limit + " OFFSET " + offset)

        cursor.moveToFirst()
        val contacts: MutableList<ContactModel> = mutableListOf()
        while (!cursor.isAfterLast) {
            val name = cursor.getLong(cursor.getColumnIndex(PROJECTION[0]))
            val phone = cursor.getString(cursor.getColumnIndex(PROJECTION[1]))
            val photouri = cursor.getString(cursor.getColumnIndex(PROJECTION[2]))
            val email = cursor.getString(cursor.getColumnIndex(PROJECTION[3]))

            contacts.add(ContactModel(name.toString(),phone.toString(),photouri.toString(),email.toString()))
            cursor.moveToNext()
        }
        cursor.close()

        return contacts
    }

}