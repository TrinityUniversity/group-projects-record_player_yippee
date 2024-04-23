package models

case class UserData(username:String,password:String)

case class UserCreationData(username:String,email:String,password:String,passwordVal:String)

case class RecordDeliveryData(id:Int,name:String,length:Option[Double],fileLocation:String,creatorName:String,artist:String,liked:Boolean)

case class PartialRecordDeliveryData(id:Int,name:String,fileLocation:String,artist:String)

case class PathData(path:String)

case class CollectionDeliveryData(id:Int,name:String,numOfItems:Int)

case class RecordCollectionData(record:PartialRecordDeliveryData,collectionId:Int)

//variable names need to have an _ between words and they must be all lowercase because they are used when converted to JSON
case class UserDeliveryData(username:String,date_joined:String,songs_added:Int,songs_liked:Int)