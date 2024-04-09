package models

case class UserData(username:String,password:String)

case class UserCreationData(username:String,email:String,password:String,passwordVal:String)

case class RecordDeliveryData(id:Int,name:String,length:Option[Double],fileLocation:String,creatorId:Int)