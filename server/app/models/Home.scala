package models

import slick.jdbc.PostgresProfile.api._
import scala.concurrent.ExecutionContext
import models.Tables._
import scala.concurrent.Future

class HomeModel(db: Database)(implicit ec: ExecutionContext){
    def fetchSongs() : Future[Seq[RecordDeliveryData]] = {
        db.run(Records.result).map{result => 
            result.map{recordRow =>
                RecordDeliveryData(recordRow.recordId,recordRow.name,recordRow.length,recordRow.fileLocation,recordRow.creatorId)
            }
        }
    }

    def fetchSong(id:Int) : Future[RecordDeliveryData] = {
        db.run(Records.filter(recordRow => recordRow.recordId === id).result).map{result => 
            RecordDeliveryData(result(0).recordId,result(0).name,result(0).length,result(0).fileLocation,result(0).creatorId)
        }
    }

    def addSong(name :String,length:Option[Double],fileLocation:String,creatorId:Int) : Future[Boolean] = {
        db.run(Records += RecordsRow(-1,name,length,fileLocation,creatorId)).map(addCount => addCount > 0)
    }
}