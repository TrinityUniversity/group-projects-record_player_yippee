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
  // implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
  
  val homeModel = new HomeModel(db)

  implicit val recordDeliverDataWrites : Writes[RecordDeliveryData] = Json.writes[RecordDeliveryData]

  def withSessionUserId(f:String => Future[Result])(implicit request: Request[AnyContent]) = {
        request.session.get("userId").map(f).getOrElse(Ok(Json.toJson(Seq.empty[String])))
    }

  def home() = Action { implicit request => 
    Ok(views.html.home())
  }

  def addSong() = Action(parse.multipartFormData) { implicit request =>
    withSessionUserId( cre
      request.body.file("file").map{ fileTemp =>
      val name = request.body.dataParts.get("name").flatMap(_.headOption).getOrElse("Unknown").replaceAll(" ","").toLowerCase()
      val path = s"./server/public/uploads/$name.mp3"
      val file = new java.io.File(path)
      homeModel.addSong(name,None,path,creatorId)
      fileTemp.ref.moveTo(file,false)
      Ok(Json.toJson(true))
    }.getOrElse(Ok(Json.toJson(false)))
    )
  }

  def getSong(path :String) = Action { implicit request =>
    val file = new java.io.File(path)
    if(file.exists()){
      Ok.sendFile(file,inline=true)
    }else{
      NotFound(s"$path is an invalid path")
    }
  }

  def getSongs() = Action.async { implicit request => 
    homeModel.fetchSongs().map{ result =>
       Ok(Json.toJson(result))
    }
  }
}
