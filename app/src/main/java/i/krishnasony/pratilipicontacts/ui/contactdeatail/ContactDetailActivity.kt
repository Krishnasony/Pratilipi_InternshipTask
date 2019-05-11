package i.krishnasony.pratilipicontacts.ui.contactdeatail

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import i.krishnasony.pratilipicontacts.R
import i.krishnasony.pratilipicontacts.databinding.ActivityContactDetailBinding
import i.krishnasony.pratilipicontacts.db.ContactModel

class ContactDetailActivity : AppCompatActivity() {
    private lateinit var dataBinding: ActivityContactDetailBinding
    private var generator = ColorGenerator.MATERIAL

    companion object {
        private const val CONTACT_KEY = "contact_key"
        fun start(context: Context, contact : ContactModel){
            val intent = Intent(context,ContactDetailActivity::class.java)
            intent.putExtra(CONTACT_KEY,contact)
            context.startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       dataBinding = DataBindingUtil.setContentView(this,R.layout.activity_contact_detail)
        handleIntentData()
        dataBinding.backButton.setOnClickListener {
            onBackPressed()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun handleIntentData() {
        intent?.let {
            setDataToDisplay(intent.getSerializableExtra(CONTACT_KEY) as ContactModel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setDataToDisplay(contact: ContactModel) {
        dataBinding.mobile.text =   contact.phone
        dataBinding.userName.text = contact.name
       if (contact.email!=null){
           dataBinding.email.text = contact.email
       }else{
           dataBinding.email.text = getString(R.string.email_not_available)

       }
        contact.photo?.let {
            val uri = Uri.parse(contact.photo)
            dataBinding.view.setBackgroundColor(generator.randomColor)
            dataBinding.circleImageView.setImageURI(uri)
            if (dataBinding.circleImageView.drawable!=null){
                dataBinding.circleImageView.setImageURI(uri)
            }else{
                dataBinding.circleImageView.setImageDrawable(getDrawable(R.drawable.user))

            }
        } ?: run{
            setAvatar(contact.name!!)
        }
    }
    private fun setAvatar(name:String) {
        val splitNameArr = name?.split(" ")
        var username:String?=null
        if (splitNameArr.size>1){
            username = splitNameArr!![0].substring(0, 1) + splitNameArr[1].substring(0, 1)
        }else{
            username = splitNameArr!![0].substring(0, 1)
        }
        val textDrawable: TextDrawable = TextDrawable.builder().beginConfig().width(40).height(40).bold().endConfig()
            .buildRound(username?.toUpperCase(), generator.randomColor)
        dataBinding.circleImageView.setImageDrawable(textDrawable)
    }

}
