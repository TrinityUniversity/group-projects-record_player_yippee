package controllers

import javax.inject._

import models._
import play.api.mvc._
import play.api.libs.json._
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.ExecutionContext
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._
import scala.concurrent.Future

class Home @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, cc: ControllerComponents)(implicit ec: ExecutionContext) 
    extends AbstractController(cc) with HasDatabaseConfigProvider[JdbcProfile] {
  
  val homeModel = new HomeModel(db)

  implicit val collectionDataWrites : Writes[CollectionData] = Json.writes[CollectionData]

  implicit val collectionDataReads : Reads[CollectionData] = Json.reads[CollectionData]

  implicit val recordDeliveryDataWrites : Writes[RecordDeliveryData] = Json.writes[RecordDeliveryData]

  implicit val recordDeliveryDataReads : Reads[RecordDeliveryData] = Json.reads[RecordDeliveryData]

  implicit val pathDataReads : Reads[PathData] = Json.reads[PathData]

  def withSessionUserId(f:String => Future[Result])(implicit request: Request[Any]) : Future[Result] = {
        request.session.get("userId").map(f).getOrElse(Future.successful(Ok(Json.toJson(Seq.empty[String]))))
  }

  def withJsonBody[A](f: A => Future[Result])(implicit request:Request[AnyContent], reads: Reads[A]) :Future[Result] = {
        request.body.asJson.map{ body =>
            Json.fromJson[A](body) match {
                case JsSuccess(a,path) => f(a)
                case e @ JsError(_) => Future.successful(Redirect(routes.Login.login()))
            }
        }.getOrElse(Future.successful(Redirect(routes.Login.login())))
  }

  def home() = Action { implicit request =>
    request.session.get("userId").map{ _ =>
      Ok(views.html.home())
    }.getOrElse(Redirect(routes.Login.login()))
  }

  def addSong() = Action(parse.multipartFormData).async { implicit request =>
    withSessionUserId{ crId => 
      request.body.file("file").map{ fileTemp =>
        val name = request.body.dataParts.get("name").flatMap(_.headOption).getOrElse("Unknown")
        val artist = request.body.dataParts.get("artist").flatMap(_.headOption).getOrElse("Unknown")
        val fileName = name.replaceAll(" ","").toLowerCase()
        val path = s"./server/public/uploads/${fileName}_${artist}_${crId}.mp3"
        val file = new java.io.File(path)
          homeModel.addSong(name,artist,None,path,crId.toInt).map{added =>
            if(added){
              fileTemp.ref.moveTo(file,false)
              Ok(Json.toJson(true))
            }else{
              Ok(Json.toJson(false))
            }
          }
      }.getOrElse(Future.successful(Ok(Json.toJson(false))))
    }
  }

  def getSong() = Action.async { implicit request =>
    withJsonBody[PathData]{pd =>
      val file = new java.io.File(pd.path)
      if(file.exists()){
        Future.successful(Ok.sendFile(file,inline=true))
      }else{
        Future.successful(NotFound(s"${pd.path} is an invalid path"))
      }
    }
    
  }

  def getSongs() = Action.async { implicit request => 
    withSessionUserId{ud =>
      homeModel.fetchSongs(ud.toInt).map{ result =>
        Ok(Json.toJson(result))
      }
    }
  }

  def likeSong() = Action.async{ implicit request =>
    withSessionUserId{ud =>
      withJsonBody[RecordDeliveryData]{record=>
        if(record.collections.filter(coll => coll.name == "Liked").length > 0){
          homeModel.dislikeSong(ud.toInt,record.id).map{result =>
            Ok(Json.toJson(result))  
          }
        }else{
          homeModel.likeSong(ud.toInt,record.id).map{result =>
            Ok(Json.toJson(result))
        }
      }
    }
    }
  }
}
