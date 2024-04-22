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
}