package models

import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext
import models.Tables._
import scala.concurrent.Future

class HomeModel(db: Database)(implicit ec: ExecutionContext){
        def fetchSongs() : Future[Seq[RecordDeliveryData]] = {
        db.run((for {
            record <- Records
            user <- Users if record.creatorId === user.id
        } yield (record,user.username)).result).map{result => 
            result.map{recordRow =>
                RecordDeliveryData(recordRow._1.recordId,recordRow._1.name,recordRow._1.length,recordRow._1.fileLocation,recordRow._2,recordRow._1.artist)
            }
        }
    }

    def fetchSong(id:Int) : Future[RecordDeliveryData] = {
        db.run(Records.filter(recordRow => recordRow.recordId === id).result).flatMap{result => 
            db.run(Users.filter(userRow => userRow.id === result(0).creatorId).result).map{ user =>
                RecordDeliveryData(result(0).recordId,result(0).name,result(0).length,result(0).fileLocation,user(0).username,result(0).artist)
            }
        }
    }

    def addSong(name :String,artist:String,length:Option[Double],fileLocation:String,creatorId:Int) : Future[Boolean] = {
        db.run(Records += RecordsRow(-1,name,length,fileLocation,creatorId,artist)).map(addCount => addCount > 0)
    }
}