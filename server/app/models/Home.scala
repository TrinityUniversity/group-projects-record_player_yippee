package models

import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext
import models.Tables._
import scala.concurrent.Future

class HomeModel(db: Database)(implicit ec: ExecutionContext){
        def fetchSongs(id:Int) : Future[Seq[RecordDeliveryData]] = {
        db.run((for {
            record <- Records
            user <- Users if record.creatorId === user.id
        } yield (record,user)).result).flatMap{result => 
            Future.sequence(result.map{recordRow =>
                db.run(Collections.filter(collectionRow=>collectionRow.userId === id && collectionRow.name==="Liked").result).flatMap{collection =>
                    if(collection.length == 0){
                        Future.successful(RecordDeliveryData(recordRow._1.recordId,recordRow._1.name,recordRow._1.length,recordRow._1.fileLocation,recordRow._2.username,recordRow._1.artist,false))
                    }else{
                        db.run(CollectionItems.filter(collectionItemRow => collectionItemRow.collectionId === collection(0).collectionId && collectionItemRow.recordId === recordRow._1.recordId).result).map{collectionItem =>
                            RecordDeliveryData(recordRow._1.recordId,recordRow._1.name,recordRow._1.length,recordRow._1.fileLocation,recordRow._2.username,recordRow._1.artist,collectionItem.length>0)
                        }
                    }
                }
            })
        }
    }

    def fetchSong(id:Int) : Future[RecordDeliveryData] = {
        db.run(Records.filter(recordRow => recordRow.recordId === id).result).flatMap{result =>
            db.run(Users.filter(userRow => userRow.id === result(0).creatorId).result).flatMap{ user => 
                db.run(Collections.filter(collectionRow=>collectionRow.userId === user(0).id && collectionRow.name==="Liked").result).flatMap{collection =>
                    if(collection.length == 0){
                        Future.successful(RecordDeliveryData(result(0).recordId,result(0).name,result(0).length,result(0).fileLocation,user(0).username,result(0).artist,false))
                    }else{
                        db.run(CollectionItems.filter(collectionItemRow => collectionItemRow.collectionId === collection(0).collectionId && collectionItemRow.recordId === result(0).recordId).result).map{collectionItem =>
                            RecordDeliveryData(result(0).recordId,result(0).name,result(0).length,result(0).fileLocation,user(0).username,result(0).artist,collectionItem.length>0)
                        }
                    }
                }
            }
        }
    }

    def addSong(name :String,artist:String,length:Option[Double],fileLocation:String,creatorId:Int) : Future[Boolean] = {
        db.run(Records.filter(recordRow => recordRow.fileLocation === fileLocation).result).flatMap{record=>
            if(record.length>0)Future.successful(false)
            else db.run(Records += RecordsRow(-1,name,length,fileLocation,creatorId,artist)).map(addCount => addCount > 0)
        }
    }

    def likeSong(userId:Int,recordId:Int) : Future[Boolean] = {
        db.run(Collections.filter(collectionRow => collectionRow.userId === userId && collectionRow.name === "Liked").result).flatMap{collection =>
            if(collection.length == 0){
                db.run(Collections += CollectionsRow(-1,"Liked",userId))
            }
            db.run(Collections.filter(collectionRow => collectionRow.userId === userId && collectionRow.name === "Liked").result).flatMap{collection =>
                db.run(CollectionItems += CollectionItemsRow(-1,collection(0).collectionId,recordId)).map(addCount => addCount>0)
            }
        }
    }

    def dislikeSong(userId:Int,recordId:Int):Future[Boolean] = {
        db.run(Collections.filter(collectionRow => collectionRow.userId === userId && collectionRow.name === "Liked").result).flatMap{collection =>
            db.run(CollectionItems.filter(collectionItemRow => collectionItemRow.collectionId === collection(0).collectionId && collectionItemRow.recordId === recordId).delete).map(deleteCount => deleteCount>0)
        }
    }

}