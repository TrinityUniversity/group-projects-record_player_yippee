package models

import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext
import models.Tables._
import scala.concurrent.Future

class ProfileModel(db: Database)(implicit ec: ExecutionContext){
    def getUserData(userId:Int) : Future[UserDeliveryData] = {
        db.run(Users.filter(userRow => userRow.id === userId).result).flatMap{user =>
            db.run(Records.filter(recordRow => recordRow.creatorId === userId).result).flatMap{records => 
                db.run(Collections.filter(collectionRow => collectionRow.userId === userId && collectionRow.name === "Liked").result).flatMap{collections =>
                    if(collections.length == 0) Future.successful(UserDeliveryData(user(0).username,user(0).dateJoined.toString,records.length,0))
                    else {
                        db.run(CollectionItems.filter(collectionItemRow => collectionItemRow.collectionId === collections(0).collectionId).result).map{collectionItems =>
                            UserDeliveryData(user(0).username,user(0).dateJoined.toString,records.length,collectionItems.length)
                        }
                    }
                }
            }
        }
    }

    def loadCollections(userId:Int) : Future[Seq[CollectionDeliveryData]] = {
        db.run(Collections.filter(collectionRow => collectionRow.userId === userId).result).flatMap{collections =>
            Future.sequence(collections.map{collection =>
                db.run(CollectionItems.filter(collectionItemRow => collectionItemRow.collectionId === collection.collectionId).result).map{collectionItems => 
                    CollectionDeliveryData(collection.collectionId,collection.name,collectionItems.length)
                }
            })
        }
    }

    def getCollection(userId:Int,collectionId:Int):Future[Seq[RecordDeliveryData]] = {
        db.run(Collections.filter(collectionRow => collectionRow.collectionId === collectionId).result).flatMap{collection =>
            db.run((for{
                collectionItem <- CollectionItems if collectionItem.collectionId === collection(0).collectionId
                record <- Records if record.recordId === collectionItem.recordId
            } yield (record)).result).flatMap{records =>
                Future.sequence(records.map{record => 
                    db.run(Users.filter(userRow => userRow.id === record.creatorId).result).flatMap{user=>
                        db.run(Collections.filter(collectionRow=>collectionRow.userId === user(0).id && collectionRow.name==="Liked").result).flatMap{collection =>
                            if(collection.length == 0){
                                Future.successful(RecordDeliveryData(record.recordId,record.name,record.length,record.fileLocation,user(0).username,record.artist,false))
                            }else{
                                db.run(CollectionItems.filter(collectionItemRow => collectionItemRow.collectionId === collection(0).collectionId && collectionItemRow.recordId === record.recordId).result).map{collectionItem =>
                                    RecordDeliveryData(record.recordId,record.name,record.length,record.fileLocation,user(0).username,record.artist,collectionItem.length>0)
                                }
                            }
                        }
                    }
                })
            }
        }
    }

    def removeRecord(record:RecordDeliveryData, collectionId:Int):Future[Boolean] = {
        db.run(Collections.filter(collectionRow => collectionRow.collectionId === collectionId).result).flatMap{collection=>
            db.run(CollectionItems.filter(collectionItemRow => collectionItemRow.collectionId === collection(0).collectionId && collectionItemRow.recordId === record.id).delete).map{deleteCount=>
                deleteCount>0
            }
        }
    }
}