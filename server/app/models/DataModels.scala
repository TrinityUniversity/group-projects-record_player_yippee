package models

case class UserData(username:String,password:String)

case class UserCreationData(username:String,email:String,password:String,passwordVal:String)

case class CollectionData(id:Int,name:String)

case class RecordDeliveryData(id:Int,name:String,length:Option[Double],fileLocation:String,creatorName:String,artist:String,collections:Seq[CollectionData])

case class PartialRecordDeliveryData(id:Int,name:String,fileLocation:String,artist:String)

case class PathData(path:String)

case class CollectionDeliveryData(id:Int,name:String,numOfItems:Int)

case class RecordCollectionData(record:RecordDeliveryData,collectionId:Int)

case class CollectionRecordData(collectionId:Int,recordId:Int)

case class CollectionEntryData(name:String)

case class CollectionIdData(id:Int)

//variable names need to have an _ between words and they must be all lowercase because they are used when converted to JSON
case class UserDeliveryData(username:String,date_joined:String,songs_added:Int,songs_liked:Int)