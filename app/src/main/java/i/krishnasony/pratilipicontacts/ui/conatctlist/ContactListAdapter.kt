package i.krishnasony.pratilipicontacts.ui.conatctlist

import android.os.Build
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import i.krishnasony.pratilipicontacts.R
import i.krishnasony.pratilipicontacts.Utils
import i.krishnasony.pratilipicontacts.db.ContactModel
import i.krishnasony.pratilipicontacts.ui.contactdeatail.ContactDetailActivity
import kotlinx.android.synthetic.main.layout_contact_list_item.view.*


class ContactListAdapter(var contactList:List<ContactModel>): RecyclerView.Adapter<ContactListAdapter.ContactListViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ContactListViewHolder {
        return ContactListViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.layout_contact_list_item,p0,false))
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    override fun onBindViewHolder(p0: ContactListViewHolder, p1: Int) {
        p0.bind(contactList[p1])
    }


    inner  class ContactListViewHolder(var view : View) : RecyclerView.ViewHolder(view) {

//        private var generator = ColorGenerator.MATERIAL
        fun bind(contact:ContactModel) {
            view.tv_name.text = contact.name
            view.tv_phone_number.text = contact.phone
            setIcon(contact.name!!)
            view.itemview.setOnClickListener {
                            onClicks(contact)
            }
        }

        private fun onClicks(contact: ContactModel) {
            ContactDetailActivity.start(view.context,contact)
        }

        private fun setIcon(name: String) {
            val splitNameArr = name?.split(" ")
            if (splitNameArr.size>1) {
                view.name_icon.text = splitNameArr!![0].substring(0, 1) + splitNameArr[1].substring(0, 1)
            }else{
                view.name_icon.text = splitNameArr!![0].substring(0, 1)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.name_icon.backgroundTintList =  Utils.getMaterialColor(view.context)
            }
        }
    }
}