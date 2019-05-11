package i.krishnasony.pratilipicontacts.db

import java.io.Serializable

data class ContactModel(
    var name:String?,
    var phone:String?,
    var photo:String?,
    var email:String?):Serializable