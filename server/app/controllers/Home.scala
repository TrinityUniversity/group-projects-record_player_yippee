package controllers

import javax.inject._

import models._
import play.api.mvc._
import play.api.libs.json._

class Home @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
  
  def test() = Action { implicit request => 
    Ok(views.html.index())
  }

  def addSong() = Action(parse.multipartFormData) { implicit request =>
    request.body.file("file").map{ fileTemp =>
      val name = request.body.dataParts.get("name").flatMap(_.headOption).getOrElse("Unknown").replaceAll(" ","").toLowerCase()
      val filename = fileTemp.filename
      val file = new java.io.File(s"./server/public/uploads/$name.mp3")
      fileTemp.ref.moveTo(file,false)
      Ok(Json.toJson(true))
    }.getOrElse(Ok(Json.toJson(false)))
  }

  def getSong(song :String) = Action { implicit request =>
    val file = new java.io.File(s"./server/public/uploads/$song.mp3")
    if(file.exists()){
      Ok.sendFile(file,inline=true)
    }else{
      NotFound(s"$song cannot be found")
    }
  }
}
