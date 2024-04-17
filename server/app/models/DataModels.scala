package models

case class UserData(username:String,password:String)

case class UserCreationData(username:String,email:String,password:String,passwordVal:String)

case class RecordDeliveryData(id:Int,name:String,length:Option[Double],fileLocation:String,creatorName:String)

case class PathData(path:String)

case class RecordPartialData(id:Int,name:String)

case class UserDeliveryData(username:String,dateJoined:String,songsAdded:Int,songsLiked:Int)