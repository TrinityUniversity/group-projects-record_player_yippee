package controllers

import javax.inject._

import models._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.Files.TemporaryFile

@Singleton
class Application @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  def test() = Action { implicit request => 
    Ok(views.html.index())
  }
  case class FileData(name:String,file:String)

  implicit val fileDataReads : Reads[FileData] = Json.reads[FileData]

  def addSong() = Action(parse.multipartFormData) { implicit request =>
    request.body.file("file").map{ fileTemp =>
      val filename = fileTemp.filename
      val contentType = fileTemp.contentType
      val file = new java.io.File(s"public/uploads/$filename")
      fileTemp.ref.moveTo(file)
      Ok(Json.toJson(true))
    }.getOrElse(Ok(Json.toJson(false)))
  }
}
